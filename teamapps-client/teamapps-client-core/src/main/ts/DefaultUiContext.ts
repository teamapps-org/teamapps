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

import {generateUUID, Showable, ClientObject, ClientObjectFactory, ComponentLibrary, ServerChannel} from "projector-client-object-api";
import {
	ClientInfo,
	SessionClosingReason,
	TeamAppsConnection,
	TeamAppsConnectionImpl,
	TeamAppsConnectionListener
} from "teamapps-client-communication";
import {Globals} from "./Globals";

class FilteredServerChannel implements ServerChannel {
	private activeEventNames: Set<string> = new Set<string>();

	constructor(private serverChannel: ServerChannel) {
	}

	toggleEvent(name: string, enabled: boolean): void {
		if (enabled) {
			this.activeEventNames.add(name);
		} else {
			this.activeEventNames.delete(name);
		}
	};

	sendEvent(name: string, params: any[]) {
		if (this.activeEventNames.has(name)) {
			this.serverChannel.sendEvent(name, params);
		}
	}

	sendQuery(name: string, params: any[]): Promise<any> {
		return this.serverChannel.sendQuery(name, params);
	}
}

class ModuleWrapper {
	constructor(public module: ComponentLibrary,
				private filteredServerChannel: FilteredServerChannel) {
		if (typeof module.init === "function") {
			module.init(filteredServerChannel);
		}
	}

	toggleEvent(name: string, enabled: boolean) {
		this.filteredServerChannel.toggleEvent(name, enabled);
	}
}

class ClientObjectWrapper {
	constructor(public clientObjectPromise: Promise<ClientObject>,
				private filteredServerChannel: FilteredServerChannel) {
	}

	toggleEvent(name: string, enabled: boolean) {
		this.filteredServerChannel.toggleEvent(name, enabled);
	}
}

export class DefaultUiContext {

	public readonly sessionId: string;

	private connection: TeamAppsConnection;

	private libraryModulesById: Map<string, Promise<ModuleWrapper>> = new Map();
	private clientObjectWrappersById: Map<string, ClientObjectWrapper> = new Map();
	private idByClientObject: Map<ClientObject, string> = new Map();

	private expiredMessageWindow: Showable;
	private errorMessageWindow: Showable;
	private terminatedMessageWindow: Showable;

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
			clientTokens: Globals.getClientTokens(),
			location: location.href,
			clientParameters: clientParameters,
			teamAppsVersion: '__TEAMAPPS_VERSION__'
		};

		const connectionListener: TeamAppsConnectionListener = {
			onConnectionInitialized: () => {
			},
			onConnectionErrorOrBroken: (reason, message) => {
				console.error(`Connection broken. ${message != null ? 'Message: ' + message : ""}`);
				sessionStorage.clear();
				if (reason == SessionClosingReason.WRONG_TEAMAPPS_VERSION) {
					// NOTE that there is a special handling for wrong teamapps client versions on the server side, which sends the client a goToUrl() command for a page with a cache-prevention GET parameter.
					// This is only in case the server-side logic does not work.
					document.body.innerHTML = `<div class="centered-body-text">
						<h3>Caching problem!</h3>
						<p>Your browser uses an old client version to connect to our server. Please <a onclick="location.reload()">refresh this page</a>. If this does not help, please clear your browser's cache.</p>
					<div>`;
				} else if (reason == SessionClosingReason.SESSION_NOT_FOUND || reason == SessionClosingReason.SESSION_TIMEOUT) {
					if (this.expiredMessageWindow != null) {
						this.expiredMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Session Expired", "Your session has expired.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.", false).show(500);
					}
				} else if (reason == SessionClosingReason.TERMINATED_BY_APPLICATION) {
					if (this.terminatedMessageWindow != null) {
						this.terminatedMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Session Terminated", "Your session has been terminated.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.", true).show(500);
					}
				} else {
					if (this.errorMessageWindow != null) {
						this.errorMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Error", "A server-side error has occurred.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.", true).show(500);
					}
				}
			},
			executeCommand: (libraryUuid: string, clientObjectId: string, commandName: string, params: any[]) => this.executeCommand(libraryUuid, clientObjectId, commandName, params)
		};

		this.connection = new TeamAppsConnectionImpl(webSocketUrl, this.sessionId, clientInfo, connectionListener);

		window.addEventListener('unload', () => {
			if (!navigator.sendBeacon) return;
			var status = navigator.sendBeacon("/leave", this.sessionId);
			console.log(`Beacon returned: ${status}`);
		})
	}

	async toggleEvent(libraryUuid: string | null, clientObjectId: string | null, eventName: string, enabled: boolean) {
		if (clientObjectId != null) {
			const componentWrapper = this.clientObjectWrappersById.get(clientObjectId);
			componentWrapper.toggleEvent(eventName, enabled);
		} else {
			// static event
			const moduleWrapper = await this.libraryModulesById.get(libraryUuid);
			moduleWrapper.toggleEvent(eventName, enabled);
		}
	}

	public async createClientObject(libraryId: string, typeName: string, objectId: string, config: any, enabledEventNames: string[]) {
		console.debug("creating ClientObject: ", libraryId, typeName, objectId, config, enabledEventNames);

		let filteredServerChannel = this.createFilteredServerChannel(libraryId, objectId);
		let clientObjectPromise = this.instantiateClientObject(libraryId, typeName, config, filteredServerChannel);
		let clientObjectWrapper = new ClientObjectWrapper(clientObjectPromise, filteredServerChannel);

		this.clientObjectWrappersById.set(objectId, clientObjectWrapper);
		clientObjectWrapper.clientObjectPromise
			.then(clientObject => this.idByClientObject.set(clientObject, objectId))

		console.debug(`Listening on clientObject ${(config.id)} to events ${(enabledEventNames)}`);
		enabledEventNames?.forEach(name => clientObjectWrapper.toggleEvent(name, true));
	}

	private async instantiateClientObject(libraryId: string, typeName: string, config: any, serverChannel: ServerChannel): Promise<ClientObject> {
		const moduleWrapper = await this.libraryModulesById.get(libraryId);
		const enhancedConfig = await this.replaceComponentReferencesWithInstances(config);

		if (this.isClientObjectFactory(moduleWrapper.module)) {
			return await moduleWrapper.module.createClientObject(typeName, enhancedConfig, serverChannel);
		} else {
			// fallback behavior: try to get hold of the constructor and invoke it with the config
			const clazz = moduleWrapper[typeName];
			if (!clazz) {
				throw `Unknown client object type ${typeName} in library ${libraryId}`;
			}
			return new clazz(enhancedConfig);
		}
	}

	private isClientObjectFactory(module: any): module is ClientObjectFactory {
		return typeof module["createClientObject"] === "function";
	}

	async destroyClientObject(oid: string) {
		let clientObjectWrapper = this.clientObjectWrappersById.get(oid);
		if (clientObjectWrapper == null) {
			console.error("Could not find component to destroy: " + oid);
			return;
		}
		const clientObject: ClientObject = await clientObjectWrapper.clientObjectPromise;

		this.clientObjectWrappersById.delete(oid);
		this.idByClientObject.delete(clientObject);

		clientObject.destroy();
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
			if (o != null && o._type && o._type === "_ref") {
				const componentById = await this.getClientObjectById(o.id);
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
			} else if (typeof o === "object") {
				for (const key of Object.keys(o)) {
					let value: any = o[key];
					const replacingValue = await this.replaceRecursively(value, replacer);
					if (replacingValue !== value) {
						o[key] = replacingValue;
					}
				}
			} else {
				return o;
			}
		}

		let replaced = await replacer(o, recur);
		if (replaced === o) {
			return await recur(o);
		} else {
			return replaced;
		}
	}

	private async executeCommand(libraryUuid: string | null, clientObjectId: string | null, commandName: string, params: any[]): Promise<any> {
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
		} else {
			console.debug(`Trying to call global function ${libraryUuid}.${commandName}(${params.join(", ")})`);
			const moduleWrapper = await this.libraryModulesById.get(libraryUuid);
			const method = moduleWrapper[commandName];
			if (method == null) {
				throw `Cannot find exported function ${method} from library ${libraryUuid}`;
			} else {
				return await (method.apply(moduleWrapper.module, params));
			}
		}
	}

	registerComponentLibrary(libraryId: string, mainJsUrl: string, mainCssUrl: string) {
		const module: Promise<ComponentLibrary> = import(mainJsUrl);
		this.libraryModulesById.set(libraryId, module.then(module => {
			return new ModuleWrapper(module, this.createFilteredServerChannel(libraryId, null));
		}));
		if (mainCssUrl != null) {
			const link = document.createElement('link');
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = mainCssUrl;
			document.getElementsByTagName('head')[0].appendChild(link);
		}
	}

	private createFilteredServerChannel(libraryId: string, objectId: string) {
		return new FilteredServerChannel({
			sendEvent: async (name: string, params: any[]) => {
				params = await this.replaceComponentInstancesWithReferences(params);
				this.connection.sendEvent(libraryId, objectId, name, params);
			},
			sendQuery: async (name: string, params: any[]): Promise<any> => {
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

}
