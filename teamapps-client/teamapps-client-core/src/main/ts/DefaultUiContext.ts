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

import {capitalizeFirstLetter, generateUUID} from "./util/string-util";
import {TeamAppsUiContext, TeamAppsUiContextInternalApi} from "./TeamAppsUiContext";
import {
	DtoClientObject as DtoClientObjectConfig,
	DtoComponent as DtoComponentConfig,
	DtoConfiguration,
	DtoGenericErrorMessageOption
} from "./generated";
import {
	createDtoClientInfo,
	DtoCommand,
	DtoEvent,
	DtoQuery,
	DtoSessionClosingReason,
	TeamAppsConnection,
	TeamAppsConnectionImpl,
	TeamAppsConnectionListener
} from "teamapps-client-communication";
import * as jstz from "jstz";
import {bind} from "./util/bind";
import {isRefreshableComponentProxyHandle, RefreshableComponentProxyHandle} from "./proxy/RefreshableComponentProxyHandle";
import {Component} from "./component/Component";
import {ClientObject} from "./ClientObject";
import {Showable} from "./util/Showable";
import {Globals} from "./Globals";
import {logException} from "./util/exception-util";
import {createUiLocation} from "./util/locationUtil";
import {AbstractWebComponent} from "./component/AbstractWebComponent";
import {AbstractComponent} from "./component/AbstractComponent";

type ClientObjectClass<T extends ClientObject = ClientObject> = { new(config: DtoClientObjectConfig, context: TeamAppsUiContext): T };


function isClassicComponent(o: ClientObject): o is AbstractComponent {
	return o != null && (o as any).getMainElement && (o as any).ELEMENT_NODE !== 1;
}

function isWebComponent(o: ClientObject): o is AbstractWebComponent {
	return o != null && (o as any).getMainElement && (o as any).ELEMENT_NODE === 1;
}

class ClientObjectClassWrapper {
	clazz: ClientObjectClass;
	eventListeners: { [qualifiedName: string]: (any) => void } = {};

	constructor(clazz: ClientObjectClass) {
		this.clazz = clazz;
	}

	toggleEventListener(qualifiedEventName: string, listener?: (x: DtoEvent) => void) {
		const eventName = capitalizeFirstLetter(qualifiedEventName.substring(qualifiedEventName.indexOf('.') + 1));
		const event = this.clazz["on" + eventName];
		const oldListener = this.eventListeners[qualifiedEventName];
		if (oldListener) {
			console.debug("Removing old listener", qualifiedEventName, this.eventListeners[qualifiedEventName])
			event?.removeListener(oldListener);
			delete this.eventListeners[qualifiedEventName];
		}
		if (listener != null) {
			console.debug("Adding listener", qualifiedEventName, listener)
			event?.addListener(listener);
			this.eventListeners[qualifiedEventName] = listener;
		}
	}
}

class ClientObjectWrapper {
	clientObject: ClientObject | RefreshableComponentProxyHandle;
	eventListeners: { [qualifiedName: string]: (any) => void } = {};
	queryListeners: { [qualifiedName: string]: (any) => void } = {};

	constructor(component: ClientObject | RefreshableComponentProxyHandle) {
		this.clientObject = component;
	}

	getUnwrappedComponent() {
		return isRefreshableComponentProxyHandle(this.clientObject) ? this.clientObject.proxy : this.clientObject
	}

	toggleEventListener(qualifiedEventName: string, listener?: (x: DtoEvent) => void) {
		const eventName = capitalizeFirstLetter(qualifiedEventName.substring(qualifiedEventName.indexOf('.') + 1));
		const event = this.getUnwrappedComponent()["on" + eventName];
		const oldListener = this.eventListeners[qualifiedEventName];
		if (oldListener) {
			console.debug("Removing old listener", qualifiedEventName, this.eventListeners[qualifiedEventName])
			event?.removeListener(oldListener);
			delete this.eventListeners[qualifiedEventName];
		}
		if (listener != null) {
			console.debug("Adding listener", qualifiedEventName, listener)
			event?.addListener(listener);
			this.eventListeners[qualifiedEventName] = listener;
		}
	}
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

	private libraryModules: Map<string, Promise<any>> = new Map();
	private componentClasses: Map<string, Promise<ClientObjectClassWrapper>> = new Map();
	private components: Map<string, Promise<ClientObjectWrapper>> = new Map();
	private _executingCommand: boolean = false;
	private connection: TeamAppsConnection;

	private expiredMessageWindow: Showable;
	private errorMessageWindow: Showable;
	private terminatedMessageWindow: Showable;

	constructor(webSocketUrl: string, clientParameters: { [key: string]: string } = {}) {
		this.sessionId = generateUUID();

		let clientInfo = createDtoClientInfo({
			viewPortWidth: window.innerWidth,
			viewPortHeight: window.innerHeight,
			screenWidth: window.screen.width,
			screenHeight: window.screen.height,
			highDensityScreen: this.isHighDensityScreen,
			timezoneOffsetMinutes: new Date().getTimezoneOffset(),
			timezoneIana: jstz.determine().name(),
			clientTokens: Globals.getClientTokens(),
			location: createUiLocation(),
			clientParameters: clientParameters,
			teamAppsVersion: '__TEAMAPPS_VERSION__'
		});

		let connectionListener: TeamAppsConnectionListener = {
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
							false, [DtoGenericErrorMessageOption.OK, DtoGenericErrorMessageOption.RELOAD], this).show(500);
					}
				} else if (reason == DtoSessionClosingReason.TERMINATED_BY_APPLICATION) {
					if (this.terminatedMessageWindow != null) {
						this.terminatedMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Session Terminated", "Your session has been terminated.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.",
							true, [DtoGenericErrorMessageOption.OK, DtoGenericErrorMessageOption.RELOAD], this).show(500);
					}
				} else {
					if (this.errorMessageWindow != null) {
						this.errorMessageWindow.show(500);
					} else {
						Globals.createGenericErrorMessageShowable("Error", "A server-side error has occurred.<br/><br/>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.",
							true, [DtoGenericErrorMessageOption.OK, DtoGenericErrorMessageOption.RELOAD], this).show(500);
					}
				}
			},
			executeCommand: (libraryUuid: string, clientObjectId: string, uiCommand: DtoCommand) => this.executeCommand(libraryUuid, clientObjectId, uiCommand)
		};

		this.connection = new TeamAppsConnectionImpl(webSocketUrl, this.sessionId, clientInfo, connectionListener);

		this.isHighDensityScreen = ((window.matchMedia && (window.matchMedia('only screen and (min-resolution: 124dpi), only screen and (min-resolution: 1.3dppx), only screen and (min-resolution: 48.8dpcm)').matches || window.matchMedia('only screen and (-webkit-min-device-pixel-ratio: 1.3), only screen and (-o-min-device-pixel-ratio: 2.6/2), only screen and (min--moz-device-pixel-ratio: 1.3), only screen and (min-device-pixel-ratio: 1.3)').matches)) || (window.devicePixelRatio && window.devicePixelRatio > 1.3));
		if (this.isHighDensityScreen) {
			document.body.classList.add('high-density-screen');
		}

		window.addEventListener('unload', () => {
			if (!navigator.sendBeacon) return;
			var status = navigator.sendBeacon("/leave", this.sessionId);
			console.log(`Beacon returned: ${status}`);
		})
	}

	@bind
	public sendEvent(eventObject: DtoEvent) {
		this.connection.sendEvent(eventObject);
	}

	@bind
	public async sendQuery(query: DtoQuery): Promise<any> {
		let result = await this.connection.sendQuery(query);
		let resultWrapper = [result];
		resultWrapper = await this.replaceComponentReferencesWithInstances(resultWrapper)
		return resultWrapper[0];
	}

	private async registerClientObject(clientObjectPromise: Promise<ClientObject>, id: string, teamappsType: string, listeningEvents: string[]) {
		console.debug("registering ClientObject: ", id);

		const getClientComponentWrapper = async (clientObjectPromise: Promise<ClientObject>) => {
			let clientObject = await clientObjectPromise;
			if (isClassicComponent(clientObject)) {
				return new ClientObjectWrapper(new RefreshableComponentProxyHandle(clientObject));
			} else {
				return new ClientObjectWrapper(clientObject);
			}
		}

		let clientComponentWrapper = getClientComponentWrapper(clientObjectPromise);
		this.components.set(id, clientComponentWrapper);

		console.debug(`Listening on clientObject ${id} to events ${listeningEvents}`);
		listeningEvents?.forEach(qualifiedEventName => {
			this.toggleEventListener(null, id, qualifiedEventName, true);
		})
	}

	async toggleEventListener(libraryUuid: string | null, clientObjectId: string | null, qualifiedEventName: string, enabled: boolean) {
		if (clientObjectId != null) {
			let listener = enabled ? (eventObject) => {
				const enhancedEventObject = {...eventObject, componentId: clientObjectId, _type: qualifiedEventName};
				console.debug("Sending event to server: ", enhancedEventObject);
				this.sendEvent(enhancedEventObject);
			} : null;
			let componentWrapper = await this.components.get(clientObjectId);
			componentWrapper.toggleEventListener(qualifiedEventName, listener);
		} else {
			// static event
			let clazzWrapper: ClientObjectClassWrapper;
			let className = qualifiedEventName.substring(0, qualifiedEventName.indexOf('.'));
			let module = await this.libraryModules.get(libraryUuid);
			clazzWrapper = await this.getClientObjectClassWrapper(libraryUuid, className);

			let listener = enabled ? (eventObject) => {
				const enhancedEventObject = {...eventObject, _type: qualifiedEventName};
				console.debug("Sending static event to server: ", enhancedEventObject);
				this.sendEvent(enhancedEventObject);
			} : null;

			clazzWrapper.toggleEventListener(qualifiedEventName, listener);
		}
	}

	public async renderClientObject(libraryUuid: string, config: DtoClientObjectConfig) {
		console.debug("rendering ClientObject: ", config._type, config.id, libraryUuid, config);
		let promise = this.createClientObject(libraryUuid, config);
		await this.registerClientObject(promise, config.id, config._type, config.listeningEvents);
		return await promise;
	}

	public async createClientObject(libraryUuid: string, config: DtoClientObjectConfig): Promise<ClientObject> {
		let componentClass = await this.getClientObjectClassWrapper(libraryUuid, config._type);
		if (componentClass) {

			// ((config as any)._queries ?? []).forEach(queryName => {
			// 	(config as any)[queryName] = (queryObject: DtoQuery) => this.sendQuery({
			// 		...queryObject,
			// 		_type: config._type + "." + queryName,
			// 		componentId: config.id
			// 	});
			// });
			// delete (config as any)._queries;

			let isWebComponent = (await componentClass.clazz as any).ELEMENT_NODE === 1;
			if (isWebComponent) {
				let webComponent = document.createElement('ui-div');
				(webComponent as any).setConfig(config);
				return webComponent as unknown as Component;
			} else {
				return new (await componentClass).clazz(config, this);
			}
		} else {
			console.error("Unknown component type: " + config._type);
			return null;
		}
	}

	async refreshComponent(libraryUuid: string, config: DtoComponentConfig) {
		let clientObject = (await this.createClientObject(libraryUuid, config)) as Component;
		if (this.components[config.id] != null) {
			(this.components[config.id].clientObject as RefreshableComponentProxyHandle).component = clientObject;
		} else {
			await this.registerClientObject(Promise.resolve(clientObject), config.id, config._type, config.listeningEvents);
		}
	}

	async destroyClientObject(id: string) {
		let o: any = (await this.components.get(id)).clientObject;
		if (o != null) {
			if (isRefreshableComponentProxyHandle(o)) {
				o = o.component;
			}
			if (isClassicComponent(o)) {
				o.getMainElement().remove();
			}
			o.destroy();
			this.components.delete(id);
		} else {
			console.warn("Could not find component to destroy: " + id);
		}
	}

	public async getClientObjectById(id: string): Promise<ClientObject> {
		let promise = this.components.get(id);
		if (promise == null) {
			console.error(`Cannot find component with id ${id}`);
			return null;
		} else {
			const clientObject = (await promise).clientObject;
			return isRefreshableComponentProxyHandle(clientObject) ? clientObject.proxy : clientObject;
		}
	}

	private async replaceComponentReferencesWithInstances(o: any) {
		let replaceOrRecur = async (value: any) => {
			if (value != null && value._type && typeof (value._type) === "string" && value._type.indexOf("ClientObjectReference") !== -1) {
				const componentById = await this.getClientObjectById(value.id);
				if (componentById != null) {
					return componentById;
				} else {
					throw new Error("Could not find component with id " + value.id);
				}
			} else {
				value = await this.replaceComponentReferencesWithInstances(value);
				return value;
			}
		};

		if (o == null || typeof o === "string" || typeof o === "boolean" || typeof o === "function" || typeof o === "number" || typeof o === "symbol") {
			return o;
		} else if (Array.isArray(o)) {
			for (let i = 0; i < o.length; i++) {
				let value = o[i];
				const replacingValue = await replaceOrRecur(value);
				if (replacingValue !== value) {
					o[i] = replacingValue;
				}
			}
		} else if (typeof o === "object") {
			for (const key of Object.keys(o)) {
				let value = o[key];
				const replacingValue = await replaceOrRecur(value);
				if (replacingValue !== value) {
					o[key] = replacingValue;
				}
			}
		}
		return o;
	}

	private async executeCommand(libraryUuid: string | null, clientObjectId: string | null, command: DtoCommand): Promise<any> {
		try {
			command = await this.replaceComponentReferencesWithInstances(command);
			this._executingCommand = true;
			const qualifiedMethodName = command[0];
			const className = qualifiedMethodName.substring(0, qualifiedMethodName.indexOf('.'));
			const methodName = qualifiedMethodName.substring(qualifiedMethodName.lastIndexOf('.') + 1);
			const parameters = command[1];
			if (clientObjectId != null) {
				console.debug(`Trying to call ${clientObjectId}.${methodName}()`);
				const componentPromise: any = this.getClientObjectById(clientObjectId);
				if (!componentPromise) {
					throw new Error("The component " + clientObjectId + " does not exist, so cannot call " + methodName + "() on it.");
				}
				const component = await componentPromise;
				if (typeof component[methodName] !== "function") {
					throw new Error(`The ${(<any>component.constructor).name || 'component'} ${clientObjectId} does not have a method ${methodName}()!`);
				}
				return await component[methodName].apply(component, parameters);
			} else {
				console.debug(`Trying to call static method ${qualifiedMethodName}(${parameters.join(", ")})`);
				const classWrapper = await this.getClientObjectClassWrapper(libraryUuid, className);
				return await (classWrapper.clazz[methodName].apply(classWrapper.clazz, [...parameters, this]));
			}
		} catch (e) {
			logException(e);
		} finally {
			this._executingCommand = false;
		}
	}

	registerComponentLibrary(uuid: string, mainJsUrl: string, mainCssUrl: string) {
		let module = import(mainJsUrl);
		this.libraryModules.set(uuid, module);
		if (mainCssUrl != null) {
			const link = document.createElement('link');
			link.rel = 'stylesheet';
			link.type = 'text/css';
			link.href = mainCssUrl;
			document.getElementsByTagName('head')[0].appendChild(link);
		}
	}

	private async getClientObjectClassWrapper(libraryUuid: string, className: string): Promise<ClientObjectClassWrapper> {
		let key = `${libraryUuid}#${className}`;
		let clientObjectClassWrapperPromise = this.componentClasses.get(key);
		if (clientObjectClassWrapperPromise == null) {
			let promise = new Promise<ClientObjectClassWrapper>(async (resolve, reject) => {
				if (libraryUuid != null) {
					let module = await this.libraryModules.get(libraryUuid);
					let clazz = module[className];

					if (!clazz) {
						console.error("Unknown client object type in module: " + className);
						reject();
					} else {
						resolve(new ClientObjectClassWrapper(clazz))
					}
				} else {
					resolve(new ClientObjectClassWrapper(Globals as ClientObjectClass))
				}
			});
			this.componentClasses.set(key, promise);
			clientObjectClassWrapperPromise = promise;
		}
		return clientObjectClassWrapperPromise;
	}

	public setSessionMessageWindows(expiredMessageWindow: Showable, errorMessageWindow: Showable, terminatedMessageWindow: Showable) {
		this.expiredMessageWindow = expiredMessageWindow;
		this.errorMessageWindow = errorMessageWindow;
		this.terminatedMessageWindow = terminatedMessageWindow;
	}
}
