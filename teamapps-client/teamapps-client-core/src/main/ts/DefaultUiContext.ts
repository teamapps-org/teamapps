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

import {generateUUID} from "./util/string-util";
import {TeamAppsUiContextInternalApi} from "./TeamAppsUiContext";
import {DtoConfiguration, DtoGenericErrorMessageOption} from "./generated";
import {
	createDtoClientInfo,
	DtoSessionClosingReason,
	TeamAppsConnection,
	TeamAppsConnectionImpl,
	TeamAppsConnectionListener
} from "teamapps-client-communication";
import {ClientObject, ClientObjectFactory, ServerObjectChannel} from "./ClientObject";
import {Showable} from "./util/Showable";
import {Globals} from "./Globals";
import {createUiLocation} from "./util/locationUtil";

class ModuleWrapper {
	activeEventNames: Set<string> = new Set<string>();

	constructor(public module: any,
				serverChannel: ServerObjectChannel) {
		if (module.setServerChannel) {
			module.setServerChannel({
				sendEvent: (name: string, params: any[]) => {
					if (this.activeEventNames.has(name)) {
						serverChannel.sendEvent(name, params);
					}
				},
				sendQuery(name: string, params: any[]): Promise<any> {
					return serverChannel.sendQuery(name, params);
				}
			})
		}
	}

	toggleEvent(name: string, enabled: boolean) {
		if (enabled) {
			if (!this.module.setServerChannel) {
				console.error(`Library does not define setServerChannel() function, so cannot listen to static event ${name}`);
			}
			this.activeEventNames.add(name);
		} else {
			this.activeEventNames.delete(name);
		}
	}
}

interface ClientObjectWrapper {
	clientObject: ClientObject;
	toggleEvent(name: string, enabled: boolean): void;
}

export class DefaultUiContext implements TeamAppsUiContextInternalApi {

	public readonly sessionId: string;
	public isHighDensityScreen: boolean;
	public config: DtoConfiguration = {
		_type: "UiConfiguration",
		locale: "en",
		themeClassName: null,
		optimizedForTouch: false
	};

	private connection: TeamAppsConnection;

	private libraryModulesById: Map<string, Promise<ModuleWrapper>> = new Map();
	private clientObjectWrappersById: Map<string, Promise<ClientObjectWrapper>> = new Map();

	private expiredMessageWindow: Showable;
	private errorMessageWindow: Showable;
	private terminatedMessageWindow: Showable;

	constructor(webSocketUrl: string, clientParameters: { [key: string]: string } = {}) {
		this.sessionId = generateUUID();

		const clientInfo = createDtoClientInfo({
			viewPortWidth: window.innerWidth,
			viewPortHeight: window.innerHeight,
			screenWidth: window.screen.width,
			screenHeight: window.screen.height,
			highDensityScreen: ((window.matchMedia && (window.matchMedia('only screen and (min-resolution: 124dpi), only screen and (min-resolution: 1.3dppx), only screen and (min-resolution: 48.8dpcm)').matches || window.matchMedia('only screen and (-webkit-min-device-pixel-ratio: 1.3), only screen and (-o-min-device-pixel-ratio: 2.6/2), only screen and (min--moz-device-pixel-ratio: 1.3), only screen and (min-device-pixel-ratio: 1.3)').matches)) || (window.devicePixelRatio && window.devicePixelRatio > 1.3)),
			timezoneOffsetMinutes: new Date().getTimezoneOffset(),
			timezoneIana: Intl.DateTimeFormat().resolvedOptions().timeZone,
			clientTokens: Globals.getClientTokens(),
			location: createUiLocation(),
			clientParameters: clientParameters,
			teamAppsVersion: '__TEAMAPPS_VERSION__'
		});

		const connectionListener: TeamAppsConnectionListener = {
			onConnectionInitialized: () => {
			},
			onConnectionErrorOrBroken: (reason, message) => {
				console.error(`Connection broken. ${message != null ? 'Message: ' + message : ""}`);
				sessionStorage.clear();
				if (reason == DtoSessionClosingReason.WRONG_TEAMAPPS_VERSION) {
					// NOTE that there is a special handling for wrong teamapps client versions on the server side, which sends the client a goToUrl() command for a page with a cache-prevention GET parameter.
					// This is only in case the server-side logic does not work.
					document.body.innerHTML = `<div class="centered-body-text">
						<h3>Caching problem!</h3>
						<p>Your browser uses an old client version to connect to our server. Please <a onclick="location.reload()">refresh this page</a>. If this does not help, please clear your browser's cache.</p>
					<div>`;
				} else if (reason == DtoSessionClosingReason.SESSION_NOT_FOUND || reason == DtoSessionClosingReason.SESSION_TIMEOUT) {
					if (this.expiredMessageWindow != null) {
						this.expiredMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Session Expired", "Your session has expired.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.",
							false, [DtoGenericErrorMessageOption.OK, DtoGenericErrorMessageOption.RELOAD]).show(500);
					}
				} else if (reason == DtoSessionClosingReason.TERMINATED_BY_APPLICATION) {
					if (this.terminatedMessageWindow != null) {
						this.terminatedMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Session Terminated", "Your session has been terminated.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.",
							true, [DtoGenericErrorMessageOption.OK, DtoGenericErrorMessageOption.RELOAD]).show(500);
					}
				} else {
					if (this.errorMessageWindow != null) {
						this.errorMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Error", "A server-side error has occurred.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.",
							true, [DtoGenericErrorMessageOption.OK, DtoGenericErrorMessageOption.RELOAD]).show(500);
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
			const componentWrapper = await this.clientObjectWrappersById.get(clientObjectId);
			componentWrapper.toggleEvent(eventName, enabled);
		} else {
			// static event
			const moduleWrapper = await this.libraryModulesById.get(libraryUuid);
			moduleWrapper.toggleEvent(eventName, enabled);
		}
	}

	public async createClientObject(libraryId: string, typeName: string, config: any, enabledEventNames: string[]) {
		console.debug("creating ClientObject: ", config._type, config.id, libraryId, config);

		const activeEventNames: Set<string> = new Set<string>();
		let serverChannel = {
			sendEvent: (name: string, params: any[]): void => {
				if (activeEventNames.has(name)) {
					this.connection.sendEvent(libraryId, null, name, params);
				}
			},
			sendQuery: async (name: string, params: any[]): Promise<any> => {
				const result = await this.connection.sendQuery(libraryId, null, name, params);
				return await this.replaceComponentReferencesWithInstances(result);
			}
		};

		const wrapperPromise = this.instantiateClientObject(libraryId, typeName, config, serverChannel)
			.then(clientObject => {
				return {
					clientObject,
					toggleEvent(name: string, enabled: boolean) {
						if (enabled) {
							activeEventNames.add(name);
						} else {
							activeEventNames.delete(name);
						}
					}
				} as ClientObjectWrapper;
			});

		this.clientObjectWrappersById.set(config.id, wrapperPromise);

		console.debug(`Listening on clientObject ${(config.id)} to events ${(enabledEventNames)}`);
		enabledEventNames?.forEach(qualifiedEventName => {
			this.toggleEvent(null, config.id, qualifiedEventName, true);
		});
	}

	private async instantiateClientObject(libraryId: string, typeName: string, config: any, serverChannel: ServerObjectChannel) {
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
		const o: any = (await this.clientObjectWrappersById.get(oid)).clientObject;
		if (o != null) {
			o.destroy();
			this.clientObjectWrappersById.delete(oid);
		} else {
			console.error("Could not find component to destroy: " + oid);
		}
	}

	public async getClientObjectById(id: string): Promise<ClientObject> {
		const promise = this.clientObjectWrappersById.get(id);
		if (promise == null) {
			console.error(`Cannot find component with id ${id}`);
			return null;
		} else {
			return (await promise).clientObject;
		}
	}

	private async replaceComponentReferencesWithInstances<T>(o: any) {
		const recur = async (o: any) => {
			if (o == null || typeof o === "string" || typeof o === "boolean" || typeof o === "function" || typeof o === "number" || typeof o === "symbol") {
				return o;
			} else if (Array.isArray(o)) {
				for (let i = 0; i < o.length; i++) {
					let value = o[i];
					const replacingValue = await this.replaceComponentReferencesWithInstances(value);
					if (replacingValue !== value) {
						o[i] = replacingValue;
					}
				}
			} else if (typeof o === "object") {
				for (const key of Object.keys(o)) {
					let value: any = o[key];
					const replacingValue = await this.replaceComponentReferencesWithInstances(value);
					if (replacingValue !== value) {
						o[key] = replacingValue;
					}
				}
			} else {
				return o;
			}
		}

		if (o != null && o._type && o._type === "_ref") {
			const componentById = await this.getClientObjectById(o.id);
			if (componentById != null) {
				return componentById;
			} else {
				throw new Error("Could not find component with id " + o.id);
			}
		} else {
			o = await recur(o);
			return o;
		}
	}

	private async executeCommand(libraryUuid: string | null, clientObjectId: string | null, commandName: string, params: any[]): Promise<any> {
		try {
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
		} catch (e) {
			console.error(e);
		}
	}

	registerComponentLibrary(libraryId: string, mainJsUrl: string, mainCssUrl: string) {
		const module = import(mainJsUrl);
		this.libraryModulesById.set(libraryId, module.then(module => new ModuleWrapper(module, {
			sendEvent: (name: string, params: any[]): void => {
				this.connection.sendEvent(libraryId, null, name, params);
			},
			sendQuery: async (name: string, params: any[]): Promise<any> => {
				const result = await this.connection.sendQuery(libraryId, null, name, params);
				return await this.replaceComponentReferencesWithInstances(result);
			}
		})));
		if (mainCssUrl != null) {
			const link = document.createElement('link');
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = mainCssUrl;
			document.getElementsByTagName('head')[0].appendChild(link);
		}
	}

	public setSessionMessageWindows(expiredMessageWindow: Showable, errorMessageWindow: Showable, terminatedMessageWindow: Showable) {
		this.expiredMessageWindow = expiredMessageWindow;
		this.errorMessageWindow = errorMessageWindow;
		this.terminatedMessageWindow = terminatedMessageWindow;
	}
}
