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
	ClosedSessionHandlingType,
	ComponentLibrary,
	generateUUID,
	ServerObjectChannel,
	Showable
} from "projector-client-object-api";
import {
	ClientInfo,
	SessionClosingReason,
	Connection,
	ConnectionImpl,
	ConnectionListener
} from "projector-client-communication";
import {CoreLibrary} from "./CoreLibrary";
import {createGenericErrorMessageShowable} from "./genericErrorMessages";


class FilteredServerObjectChannel implements ServerObjectChannel {
	private activeEventNames: Set<string> = new Set<string>();

	constructor(private serverObjectChannel: ServerObjectChannel) {
	}

	toggleEvent(name: string, enabled: boolean): void {
		console.debug(`Toggling event ${name}: ${enabled}`)
		if (enabled) {
			this.activeEventNames.add(name);
		} else {
			this.activeEventNames.delete(name);
		}
	};

	sendEvent(name: string, params: any[]) {
		if (this.activeEventNames.has(name)) {
			console.debug(`sendEvent(${name}) will be forwarded to server.`);
			this.serverObjectChannel.sendEvent(name, params);
		} else {
			console.debug(`Event swallowed (not enabled): ${name}.`);
		}
	}

	sendQuery(name: string, params: any[]): Promise<any> {
		return this.serverObjectChannel.sendQuery(name, params);
	}
}

class ModuleWrapper {
	constructor(public module: ComponentLibrary,
				private filteredServerObjectChannel: FilteredServerObjectChannel) {
		if (typeof module.init === "function") {
			module.init(filteredServerObjectChannel);
		}
	}

	toggleEvent(name: string, enabled: boolean) {
		this.filteredServerObjectChannel.toggleEvent(name, enabled);
	}
}

class ClientObjectWrapper {
	constructor(public clientObjectPromise: Promise<ClientObject>,
				private filteredServerObjectChannel: FilteredServerObjectChannel) {
	}

	toggleEvent(name: string, enabled: boolean) {
		this.filteredServerObjectChannel.toggleEvent(name, enabled);
	}
}

export class DefaultUiContext implements ConnectionListener {

	public readonly sessionId: string;

	private connection: Connection;

	private libraryModulesById: Map<string, Promise<ModuleWrapper>> = new Map();
	private clientObjectWrappersById: Map<string, ClientObjectWrapper> = new Map();
	private idByClientObject: Map<ClientObject, string> = new Map();

	private expiredMessageWindow: Showable;
	private errorMessageWindow: Showable;
	private terminatedMessageWindow: Showable;
	private closedSessionHandling = ClosedSessionHandlingType.MESSAGE_WINDOW;

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
		this.libraryModulesById.set(null, Promise.resolve(new ModuleWrapper(CoreLibrary, this.createfilteredServerObjectChannel(null, null))));
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
		} else if (this.closedSessionHandling == ClosedSessionHandlingType.REFRESH_PAGE) {
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
			// static event
			const moduleWrapper = await this.libraryModulesById.get(libraryUuid ?? null);
			moduleWrapper.toggleEvent(eventName, enabled);
		}
	}

	public async createClientObject(libraryId: string, typeName: string, objectId: string, config: any, enabledEventNames: string[]) {
		console.log("Creating ClientObject: ", libraryId, typeName, objectId, config, enabledEventNames);

		let filteredServerObjectChannel = this.createfilteredServerObjectChannel(libraryId, objectId);
		let clientObjectPromise = this.instantiateClientObject(libraryId, typeName, config, filteredServerObjectChannel);
		let clientObjectWrapper = new ClientObjectWrapper(clientObjectPromise, filteredServerObjectChannel);

		this.clientObjectWrappersById.set(objectId, clientObjectWrapper);
		clientObjectWrapper.clientObjectPromise
			.then(clientObject => this.idByClientObject.set(clientObject, objectId))

		console.debug(`Listening on clientObject ${objectId} to events: ${enabledEventNames}`);
		enabledEventNames?.forEach(name => clientObjectWrapper.toggleEvent(name, true));
	}

	private async instantiateClientObject(libraryId: string, typeName: string, config: any, serverObjectChannel: ServerObjectChannel): Promise<ClientObject> {
		const moduleWrapper = await this.libraryModulesById.get(libraryId ?? null);
		const enhancedConfig = await this.replaceComponentReferencesWithInstances(config);

		let instance: ClientObject;
		if (typeof moduleWrapper.module["createClientObject"] === "function") {
			instance = await moduleWrapper.module.createClientObject(typeName, enhancedConfig, serverObjectChannel);
		} else {
			// fallback behavior: try to get hold of the constructor and invoke it with the config
			const clazz = moduleWrapper.module[typeName];
			if (!clazz) {
				throw `Unknown client object type ${typeName} in library ${libraryId}`;
			}
			instance = new clazz(enhancedConfig, serverObjectChannel);
		}
		if (typeof instance.init === "function") {
			instance.init(enhancedConfig, serverObjectChannel);
		}
		return instance;
	}

	async destroyClientObject(oid: string) {
		console.log("Destroy client object", oid);
		let clientObjectWrapper = this.clientObjectWrappersById.get(oid);
		if (clientObjectWrapper == null) {
			console.error("Could not find component to destroy: " + oid);
			return;
		}
		const clientObject: ClientObject = await clientObjectWrapper.clientObjectPromise;

		this.clientObjectWrappersById.delete(oid);
		this.idByClientObject.delete(clientObject);

		if (typeof clientObject.destroy === 'function') {
			clientObject.destroy();
		}
	}

	public async getClientObjectById(id: string): Promise<ClientObject> {
		const clientObjectWrapper = this.clientObjectWrappersById.get(id);
		if (clientObjectWrapper == null) {
			console.error(`Cannot find component with id ${id}`);
			return null;
		} else {
			return await clientObjectWrapper.clientObjectPromise;
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
			const clientObjectPromise: any = this.getClientObjectById(clientObjectId);
			if (!clientObjectPromise) {
				throw new Error("The object " + clientObjectId + " does not exist, so cannot call " + commandName + "() on it.");
			}
			const clientObject = await clientObjectPromise;
			if (typeof clientObject[commandName] !== "function") {
				throw new Error(`The ${(<any>clientObject.constructor).name || 'object'} ${clientObjectId} does not have a method ${commandName}()!`);
			}
			return await clientObject[commandName].apply(clientObject, params);
		} else if (libraryUuid == null) {
			// this is a core library command
			return await (CoreLibrary[commandName] as Function).apply(null, [...params, this]);
		} else {
			console.debug(`Trying to call global function ${libraryUuid}.${commandName}(${params.join(", ")})`);
			const moduleWrapper = await this.libraryModulesById.get(libraryUuid ?? null);
			const method = moduleWrapper.module[commandName];
			if (method == null) {
				throw `Cannot find exported function ${method} from library ${libraryUuid}`;
			} else {
				return await (method.apply(moduleWrapper.module, params));
			}
		}
	}

	registerLibrary(libraryId: string, mainJsUrl: string, mainCssUrl: string) {
		console.log(`Register library`, libraryId, mainJsUrl, mainCssUrl);
		const module: Promise<ComponentLibrary> = import(mainJsUrl);
		this.libraryModulesById.set(libraryId, module.then(module => {
			return new ModuleWrapper(module, this.createfilteredServerObjectChannel(libraryId, null));
		}));
		if (mainCssUrl != null) {
			const link = document.createElement('link');
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = mainCssUrl;
			document.getElementsByTagName('head')[0].appendChild(link);
		}
	}

	private createfilteredServerObjectChannel(libraryId: string, objectId: string) {
		return new FilteredServerObjectChannel({
			sendEvent: async (name: string, ...params: any[]) => {
				params = await this.replaceComponentInstancesWithReferences(params);
				this.connection.sendEvent(libraryId, objectId, name, params);
			},
			sendQuery: async (name: string, ...params: any[]): Promise<any> => {
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
