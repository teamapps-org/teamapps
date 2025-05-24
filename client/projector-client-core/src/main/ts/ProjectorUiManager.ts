/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
"use strict";

import {
	ClientObject,
	ClosedSessionHandlingType, ClosedSessionHandlingTypes,
	ComponentLibrary,
	generateUUID,
	Invokable,
	ServerObjectChannel,
	Showable
} from "projector-client-object-api";
import {
	ClientInfo,
	Connection,
	ConnectionImpl,
	ConnectionListener,
	SessionClosingReason
} from "projector-client-communication";
import {CoreLibrary} from "./CoreLibrary";
import {createGenericErrorMessageShowable} from "./genericErrorMessages";
import {ServerObjectChannelImpl} from "./ServerObjectChannelImpl";

abstract class AbstractClientObjectWrapper<WRAPPED extends ComponentLibrary | ClientObject> {

	public objectPromise: Promise<WRAPPED>;

	constructor(protected id: string,
	            promise: Promise<WRAPPED>,
	            private serverObjectChannel: ServerObjectChannelImpl) {
		this.objectPromise = promise.then(object => {
			if (typeof object['init'] === "function") {
				object.init(serverObjectChannel);
			}
			return object;
		});
	}

	async executeCommand?(name: string, params: any[]): Promise<any> {
		let object = await this.objectPromise;
		if (typeof object['executeCommand'] === "function") {
			return object.executeCommand(name, params);
		} else if (typeof object[name] === "function") {
			return object[name].apply(object, params);
		} else {
			throw new Error(`Cannot invoke command ${name} on ${(<any>object.constructor).name || 'object'} with id ${this.id} !`);
		}
	}

	toggleEvent(eventName: string, enabled: boolean) {
		this.serverObjectChannel.toggleEvent(eventName, enabled);
	}

	addEventHandler(registrationId: string, eventName: string, handler: (any) => any): void {
		this.serverObjectChannel.addEventHandler(eventName, registrationId, handler);
	}

	removeEventHandler(registrationId: string): void {
		this.serverObjectChannel.removeEventHandler(registrationId);
	}
}

class ModuleWrapper extends AbstractClientObjectWrapper<ComponentLibrary> {

	async instantiateClientObject(typeName: string, enhancedConfig: any, serverObjectChannel: ServerObjectChannel): Promise<ClientObject> {
		let module = await this.objectPromise;

		if (typeof module["createClientObject"] === "function") {
			return module.createClientObject(typeName, enhancedConfig, serverObjectChannel);
		} else {
			// fallback behavior: try to get hold of the constructor and invoke it with the config
			const clazz = module[typeName];
			if (!clazz) {
				throw `Unknown client object type ${typeName} in library ${this.id}`;
			}
			return new clazz(enhancedConfig, serverObjectChannel);
		}

	}
}

class ClientObjectWrapper extends AbstractClientObjectWrapper<ClientObject> {
	async destroy() {
		let clientObject = await this.objectPromise;
		if (typeof clientObject.destroy === 'function') {
			clientObject.destroy();
		}
	}
}

export class ProjectorUiManager implements ConnectionListener {

	public readonly sessionId: string;

	private connection: Connection;

	private libraryModulesById: Map<string, ModuleWrapper> = new Map();
	private clientObjectWrappersById: Map<string, ClientObjectWrapper> = new Map();
	private idByClientObject: Map<ClientObject, string> = new Map();

	private expiredMessageWindow: Showable;
	private errorMessageWindow: Showable;
	private terminatedMessageWindow: Showable;
	private closedSessionHandling:ClosedSessionHandlingType = ClosedSessionHandlingTypes.MESSAGE_WINDOW;

	constructor(webSocketUrl: string, clientParameters: { [key: string]: string } = {}) {
		this.sessionId = generateUUID();

		const clientInfo: ClientInfo = {
			viewPortWidth: window.innerWidth,
			viewPortHeight: window.innerHeight,
			screenWidth: window.screen.width,
			screenHeight: window.screen.height,
			highDensityScreen: ((window.matchMedia && (window.matchMedia('only screen and (min-resolution: 124dpi), only screen and (min-resolution: 1.3dppx), only screen and (min-resolution: 48.8dpcm)').matches || window.matchMedia('only screen and (-webkit-min-device-pixel-ratio: 1.3), only screen and (-o-min-device-pixel-ratio: 2.6/2), only screen and (min--moz-device-pixel-ratio: 1.3), only screen and (min-device-pixel-ratio: 1.3)').matches)) || (window.devicePixelRatio && window.devicePixelRatio > 1.3)),
			timezoneOffsetMinutes: new Date().getTimezoneOffset(),
			timezoneIana: Intl.DateTimeFormat().resolvedOptions().timeZone,
			clientTokens: CoreLibrary.getClientTokens(),
			location: location.href,
			clientParameters: clientParameters,
			teamAppsVersion: '__TEAMAPPS_VERSION__'
		};

		this.connection = new ConnectionImpl(webSocketUrl, this.sessionId, clientInfo, this);

		window.addEventListener('unload', () => {
			if (!navigator.sendBeacon) return;
			var status = navigator.sendBeacon("/leave", this.sessionId);
			console.log(`Beacon returned: ${status}`);
		});

		console.debug(`Registering global library.`);
		this.registerLibraryInternal(null, Promise.resolve(CoreLibrary), null);
	}

	public onConnectionInitialized() {
		console.log('Connection initialized')
	}

	public onConnectionErrorOrBroken(reason, message) {
		console.error(`Connection broken. ${message != null ? 'Message: ' + message : ""}`);
		sessionStorage.clear();
		if (reason == SessionClosingReason.WRONG_TEAMAPPS_VERSION) {
			// NOTE that there is a special handling for wrong teamapps client versions on the server side, which sends the client a goToUrl() command for a page with a cache-prevention GET parameter.
			// This is only in case the server-side logic does not work.
			document.body.innerHTML = `<div class="centered-body-text">
						<h3>Caching problem!</h3>
						<p>Your browser uses an old client version to connect to our server. Please <a onclick="location.reload()">refresh this page</a>. If this does not help, please clear your browser's cache.</p>
					<div>`;
		} else if (this.closedSessionHandling == ClosedSessionHandlingTypes.REFRESH_PAGE) {
			location.reload();
		} else {
			if (reason == SessionClosingReason.SESSION_NOT_FOUND || reason == SessionClosingReason.SESSION_TIMEOUT) {
				if (this.expiredMessageWindow != null) {
					this.expiredMessageWindow.show(500);
				} else {
					createGenericErrorMessageShowable("Session Expired", "Your session has expired.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.", false).show(500);
				}
			} else if (reason == SessionClosingReason.TERMINATED_BY_APPLICATION) {
				if (this.terminatedMessageWindow != null) {
					this.terminatedMessageWindow.show(500);
				} else {
					createGenericErrorMessageShowable("Session Terminated", "Your session has been terminated.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.", true).show(500);
				}
			} else {
				if (this.errorMessageWindow != null) {
					this.errorMessageWindow.show(500);
				} else {
					createGenericErrorMessageShowable("Error", "A server-side error has occurred.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.", true).show(500);
				}
			}
		}
	}

	async toggleEvent(libraryUuid: string | null, clientObjectId: string | null, eventName: string, enabled: boolean) {
		console.log("Toggle event", libraryUuid, clientObjectId, eventName, enabled);
		if (clientObjectId != null) {
			const componentWrapper = this.clientObjectWrappersById.get(clientObjectId);
			componentWrapper.toggleEvent(eventName, enabled);
		} else {
			const moduleWrapper = await this.libraryModulesById.get(libraryUuid ?? null);
			moduleWrapper.toggleEvent(eventName, enabled);
		}
	}

	async addEventHandler(libraryUuid: string, clientObjectId: string, eventName: string, registrationId: string, invokableId: string, functionName: string, evtObjAsFirstParam: boolean, params: any[]) {
		console.log("Registering client-side event handler", libraryUuid, clientObjectId, eventName, functionName, params);
		let invokableWrapper = this.clientObjectWrappersById.get(invokableId);
		let clientObjectWrapper = clientObjectId != null ? this.clientObjectWrappersById.get(clientObjectId)
			: this.libraryModulesById.get(libraryUuid ?? null);
		if (clientObjectId != null) {
			clientObjectWrapper.addEventHandler(registrationId, eventName, (eventObject) => {
				let paramsArray = evtObjAsFirstParam ? [eventObject, ...params] : params;
				return invokableWrapper.executeCommand("invoke", [functionName, paramsArray]);
			});
		}
	}

	removeEventHandler(libraryUuid: string, clientObjectId: string, eventName: string, registrationId: string) {
		console.log("Removing client-side event handler", registrationId);
		if (clientObjectId != null) {
			let wrapper = this.clientObjectWrappersById.get(clientObjectId);
			wrapper.removeEventHandler(registrationId);
		} else {
			const moduleWrapper = this.libraryModulesById.get(libraryUuid ?? null);
			moduleWrapper.removeEventHandler(registrationId);
		}
	}

	public async createClientObject(libraryId: string, typeName: string, objectId: string, config: any, enabledEventNames: string[]) {
		console.log("Creating ClientObject: ", libraryId, typeName, objectId, config, enabledEventNames);

		const moduleWrapper = await this.libraryModulesById.get(libraryId ?? null);
		const enhancedConfig = await this.replaceComponentReferencesWithInstances(config);
		let serverObjectChannel = this.createServerObjectChannel(libraryId, objectId);
		let clientObjectPromise = moduleWrapper.instantiateClientObject(typeName, enhancedConfig, serverObjectChannel);
		let clientObjectWrapper = new ClientObjectWrapper(objectId, clientObjectPromise, serverObjectChannel);
		this.clientObjectWrappersById.set(objectId, clientObjectWrapper);
		clientObjectWrapper.objectPromise
			.then(clientObject => this.idByClientObject.set(clientObject, objectId))

		console.debug(`Listening on clientObject ${objectId} to events: ${enabledEventNames}`);
		enabledEventNames?.forEach(name => clientObjectWrapper.toggleEvent(name, true));
	}

	async destroyClientObject(oid: string) {
		console.log("Destroy client object", oid);
		let clientObjectWrapper = this.clientObjectWrappersById.get(oid);
		if (clientObjectWrapper == null) {
			console.error("Could not find component to destroy: " + oid);
			return;
		}
		const clientObject: ClientObject = await clientObjectWrapper.objectPromise;
		clientObjectWrapper.destroy();
		this.clientObjectWrappersById.delete(oid);
		this.idByClientObject.delete(clientObject);
	}

	public async getClientObjectById(id: string): Promise<ClientObject> {
		const clientObjectWrapper = this.clientObjectWrappersById.get(id);
		if (clientObjectWrapper == null) {
			console.error(`Cannot find component with id ${id}`);
			return null;
		} else {
			return await clientObjectWrapper.objectPromise;
		}
	}

	private async replaceComponentReferencesWithInstances(serverMessageObject: any) {
		return await this.replaceRecursively(serverMessageObject, async (o, recur) => {
			if (o != null && o._ref) {
				const componentById = await this.getClientObjectById(o._ref);
				if (componentById != null) {
					return componentById;
				} else {
					throw new Error("Could not find component with id " + o.id);
				}
			} else {
				return await recur(o);
			}
		});
	}

	private async replaceComponentInstancesWithReferences(clientMessageObject: any) {
		return await this.replaceRecursively(clientMessageObject, async (o, recur) => {
			let clientId: string = o != null && this.idByClientObject.get(o);
			if (clientId) {
				return {"_ref": clientId};
			} else {
				return await recur(o);
			}
		});
	}

	private async replaceRecursively(o: any, replacer: (o: any, recur: (o: any) => Promise<any>) => Promise<any>) {
		const recur = async (o: any) => {
			if (o == null || typeof o === "string" || typeof o === "boolean" || typeof o === "function" || typeof o === "number" || typeof o === "symbol") {
				return o;
			} else if (Array.isArray(o)) {
				for (let i = 0; i < o.length; i++) {
					let value = o[i];
					const replacingValue = await this.replaceRecursively(value, replacer);
					if (replacingValue !== value) {
						o[i] = replacingValue;
					}
				}
				return o;
			} else if (typeof o === "object") {
				for (const key of Object.keys(o)) {
					let value: any = o[key];
					const replacingValue = await this.replaceRecursively(value, replacer);
					if (replacingValue !== value) {
						o[key] = replacingValue;
					}
				}
				return o;
			} else {
				return o;
			}
		}

		return await replacer(o, recur);
		// if (replaced === o) {
		// 	return await recur(o);
		// } else {
		// 	return replaced;
		// }
	}

	async executeCommand(libraryUuid: string | null, clientObjectId: string | null, commandName: string, params: any[]): Promise<any> {
		console.log("Execute command", libraryUuid, clientObjectId, commandName, params);
		params = await this.replaceComponentReferencesWithInstances(params);
		if (clientObjectId != null) {
			console.debug(`Trying to call ${libraryUuid}.${clientObjectId}.${commandName}(${params.join(", ")})`);
			const clientObjectWrapper = this.clientObjectWrappersById.get(clientObjectId);
			if (clientObjectWrapper == null) {
				console.error(`Cannot find component with id ${clientObjectId}`);
				return null;
			}
			return clientObjectWrapper.executeCommand(commandName, params);
		} else {
			console.debug(`Trying to call global function ${libraryUuid}.${commandName}(${params.join(", ")})`);
			const moduleWrapper = await this.libraryModulesById.get(libraryUuid ?? null);
			if (moduleWrapper == null) {
				console.error(`Cannot find module with id ${clientObjectId}`);
				return null;
			}
			return moduleWrapper.executeCommand(commandName, params);
		}
	}

	registerLibrary(libraryId: string, mainJsUrl: string, mainCssUrl: string) {
		console.log(`Registering library`, libraryId, mainJsUrl, mainCssUrl);
		const module: Promise<ComponentLibrary> = import(mainJsUrl);
		this.registerLibraryInternal(libraryId, module, mainCssUrl);
	}

	private registerLibraryInternal(libraryId: string, module: Promise<ComponentLibrary>, mainCssUrl: string) {
		let serverObjectChannel = this.createServerObjectChannel(libraryId, null);
		let moduleWrapper = new ModuleWrapper(libraryId, module, serverObjectChannel);
		this.libraryModulesById.set(libraryId, moduleWrapper);
		if (mainCssUrl != null) {
			const link = document.createElement('link');
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = mainCssUrl;
			document.getElementsByTagName('head')[0].appendChild(link);
		}
	}

	private createServerObjectChannel(libraryId: string, objectId: string): ServerObjectChannelImpl {
		return new ServerObjectChannelImpl({
			handleEvent: async (name: string, ...params: any[]) => {
				params = await this.replaceComponentInstancesWithReferences(params);
				this.connection.sendEvent(libraryId, objectId, name, params);
			},
			handleQuery: async (name: string, ...params: any[]): Promise<any> => {
				params = await this.replaceComponentInstancesWithReferences(params);
				const result = await this.connection.sendQuery(libraryId, objectId, name, params);
				return await this.replaceComponentReferencesWithInstances(result);
			}
		});
	}

	public setSessionMessageWindows(expiredMessageWindow: Showable, errorMessageWindow: Showable, terminatedMessageWindow: Showable) {
		this.expiredMessageWindow = expiredMessageWindow;
		this.errorMessageWindow = errorMessageWindow;
		this.terminatedMessageWindow = terminatedMessageWindow;
	}

	public setClosedSessionHandling(closedSessionHandling: ClosedSessionHandlingType) {
		this.closedSessionHandling = closedSessionHandling;
	}
}
