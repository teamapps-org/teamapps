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

import {createUiLocation, generateUUID, logException} from "./Common";
import {TeamAppsUiContextInternalApi} from "./TeamAppsUiContext";
import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiEvent} from "../generated/UiEvent";
import {UiRootPanel} from "./UiRootPanel";
import {ComponentEventSubscriptionManager} from "./util/ComponentEventSubscriptionManager";
import {UiCommand} from '../generated/UiCommand';
import {CommandExecutor} from "../generated/CommandExecutor";
import {TeamAppsConnection, TeamAppsConnectionListener} from "./communication/TeamAppsConnection";
import * as log from "loglevel";
import * as jstz from "jstz";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TemplateRegistry} from "./TemplateRegistry";
import {createUiClientInfoConfig} from "../generated/UiClientInfoConfig";
import {UiGenericErrorMessageOption} from "../generated/UiGenericErrorMessageOption";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {bind} from "./util/Bind";
import {isRefreshableComponentProxyHandle, RefreshableComponentProxyHandle} from "./util/RefreshableComponentProxyHandle";
import {TeamAppsConnectionImpl} from "./communication/TeamAppsConnectionImpl";
import {UiComponent} from "./UiComponent";
import {UiSessionClosingReason} from "../generated/UiSessionClosingReason";
import {UiClientObject} from "./UiClientObject";
import {UiClientObjectConfig} from "../generated/UiClientObjectConfig";
import {UiWindow} from "./UiWindow";
import {QueryFunctionAdder} from "../generated/QueryFunctionAdder";
import {UiQuery} from "../generated/UiQuery";
import {componentEventDescriptors, staticComponentEventDescriptors} from "../generated/ComponentEventDescriptors";

declare var __TEAMAPPS_VERSION__: string;

function isComponent(o: UiClientObject<UiClientObjectConfig>): o is UiComponent {
	return o != null && (o as any).getMainElement;
}

export class DefaultTeamAppsUiContext implements TeamAppsUiContextInternalApi {

	private static logger = log.getLogger("DefaultTeamAppsUiContext");

	public readonly onStaticMethodCommandInvocation: TeamAppsEvent<UiCommand> = new TeamAppsEvent();
	public readonly sessionId: string;
	public isHighDensityScreen: boolean;
	public config: UiConfigurationConfig = {
		_type: "UiConfigurationConfig",
		locale: "en",
		themeClassName: null,
		optimizedForTouch: false
	};
	public readonly templateRegistry: TemplateRegistry = new TemplateRegistry(this);

	private components: { [identifier: string]: UiClientObject | RefreshableComponentProxyHandle<UiComponent> } = {};
	private _executingCommand: boolean = false;
	private connection: TeamAppsConnection;

	private commandExecutor = new CommandExecutor();

	private expiredMessageWindow: UiWindow;
	private errorMessageWindow: UiWindow;
	private terminatedMessageWindow: UiWindow;

	private componentEventSubscriptionManager: ComponentEventSubscriptionManager;

	constructor(webSocketUrl: string, clientParameters: { [key: string]: string | number } = {}) {
		this.componentEventSubscriptionManager = new ComponentEventSubscriptionManager();
		this.componentEventSubscriptionManager.registerComponentTypes(componentEventDescriptors, staticComponentEventDescriptors, this.sendEvent);

		this.sessionId = generateUUID();

		let clientInfo = createUiClientInfoConfig({
			viewPortWidth: window.innerWidth,
			viewPortHeight: window.innerHeight,
			screenWidth: window.screen.width,
			screenHeight: window.screen.height,
			highDensityScreen: this.isHighDensityScreen,
			timezoneOffsetMinutes: new Date().getTimezoneOffset(),
			timezoneIana: jstz.determine().name(),
			clientTokens: UiRootPanel.getClientTokens(),
			location: createUiLocation(),
			clientParameters: clientParameters,
			teamAppsVersion: __TEAMAPPS_VERSION__
		});

		let connectionListener: TeamAppsConnectionListener = {
			onConnectionInitialized: () => {
			},
			onConnectionErrorOrBroken: (reason, message) => {
				DefaultTeamAppsUiContext.logger.error(`Connection broken. ${message != null ? 'Message: ' + message : ""}`);
				sessionStorage.clear();
				if (reason == UiSessionClosingReason.WRONG_TEAMAPPS_VERSION) {
					// NOTE that there is a special handling for wrong teamapps client versions on the server side, which sends the client a goToUrl() command for a page with a cache-prevention GET parameter.
					// This is only in case the server-side logic does not work.
					document.body.innerHTML = `<div class="centered-body-text">
						<h3>Caching problem!</h3>
						<p>Your browser uses an old client version to connect to our server. Please <a onclick="location.reload()">refresh this page</a>. If this does not help, please clear your browser's cache.</p>
					<div>`;
				} else if (reason == UiSessionClosingReason.SESSION_NOT_FOUND || reason == UiSessionClosingReason.SESSION_TIMEOUT || reason == UiSessionClosingReason.HTTP_SESSION_CLOSED) {
					if (this.expiredMessageWindow != null) {
						this.expiredMessageWindow.show(500);
					} else {
						UiRootPanel.createGenericErrorMessageWindow("Session Expired", "<p>Your session has expired.</p><p>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.</p>",
							false, [UiGenericErrorMessageOption.OK, UiGenericErrorMessageOption.RELOAD], this).show(500);
					}
				} else if (reason == UiSessionClosingReason.TERMINATED_BY_APPLICATION) {
					if (this.terminatedMessageWindow != null) {
						this.terminatedMessageWindow.show(500);
					} else {
						UiRootPanel.createGenericErrorMessageWindow("Session Terminated", "<p>Your session has been terminated.</p><p>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.</p>",
							true, [UiGenericErrorMessageOption.OK, UiGenericErrorMessageOption.RELOAD], this).show(500);
					}
				} else {
					if (this.errorMessageWindow != null) {
						this.errorMessageWindow.show(500);
					} else {
						UiRootPanel.createGenericErrorMessageWindow("Error", "<p>A server-side error has occurred.</p><p>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.</p>",
							true, [UiGenericErrorMessageOption.OK, UiGenericErrorMessageOption.RELOAD], this).show(500);
					}
				}
			},
			executeCommand: (uiCommand: UiCommand) => this.executeCommand(uiCommand)
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

	public get executingCommand() {
		return this._executingCommand;
	}

	@bind
	public sendEvent(eventObject: UiEvent) {
		this.connection.sendEvent(eventObject);
	}

	@bind
	public async sendQuery(query: UiQuery): Promise<any> {
		let result = await this.connection.sendQuery(query);
		let resultWrapper = [result];
		this.replaceComponentReferencesWithInstances(resultWrapper)
		return resultWrapper[0];
	}

	public registerClientObject(clientObject: UiClientObject, id: string, teamappsType: string): void {
		DefaultTeamAppsUiContext.logger.debug("registering ClientObject: ", id);
		if (isComponent(clientObject)) {
			let existingProxy = this.components[id];
			if (existingProxy != null) {
				(existingProxy as RefreshableComponentProxyHandle).component = clientObject;
			} else {
				this.components[id] = new RefreshableComponentProxyHandle(clientObject);
			}
		} else {
			this.components[id] = clientObject;
		}
		this.componentEventSubscriptionManager.registerEventListener(clientObject, teamappsType, this.sendEvent, {componentId: id});
	}

	public createClientObject(config: UiClientObjectConfig): UiClientObject {
		let componentClass = TeamAppsUiComponentRegistry.getComponentClassForName(config._type);
		if (componentClass) {
			QueryFunctionAdder.addQueryFunctionsToConfig(config, (componentId, queryTypeId, queryObject) => {
				return this.sendQuery({...queryObject, _type: queryTypeId, componentId: componentId});
			});
			return new componentClass(config, this);
		} else {
			DefaultTeamAppsUiContext.logger.error("Unknown component type: " + config._type);
			return null;
		}
	}

	refreshComponent(config: UiComponentConfig): void {
		let clientObject = this.createClientObject(config) as UiComponent;
		if (this.components[config.id] != null) {
			(this.components[config.id] as RefreshableComponentProxyHandle).component = clientObject;
		} else {
			this.registerClientObject(clientObject, config.id, config._type);
		}
	}

	destroyClientObject(id: string): void {
		let o = this.components[id];
		if (o != null) {
			if (isRefreshableComponentProxyHandle(o)) {
				o = o.component;
			}
			if (isComponent(o)) {
				o.getMainElement().remove();
			}
			o.destroy();
			delete this.components[id];
			this.componentEventSubscriptionManager.unregisterEventListener(o);
		} else {
			DefaultTeamAppsUiContext.logger.warn("Could not find component to destroy: " + id);
		}
	}

	public getClientObjectById(id: string): UiClientObject {
		const clientObject = this.components[id];
		if (clientObject == null) {
			DefaultTeamAppsUiContext.logger.error(`Cannot find component with id ${id}`);
			return null;
		} else {
			return isRefreshableComponentProxyHandle(clientObject) ? clientObject.proxy : clientObject;
		}
	}

	private replaceComponentReferencesWithInstances(o: any) {
		let replaceOrRecur = (value: any) => {
			if (value != null && value._type && typeof (value._type) === "string" && value._type.indexOf("UiClientObjectReference") !== -1) {
				const componentById = this.getClientObjectById(value.id);
				if (componentById != null) {
					return componentById;
				} else {
					throw new Error("Could not find component with id " + value.id);
				}
			} else {
				this.replaceComponentReferencesWithInstances(value);
				return value;
			}
		};

		if (o == null || typeof o === "string" || typeof o === "boolean" || typeof o === "function" || typeof o === "number" || typeof o === "symbol") {
			return;
		} else if (Array.isArray(o)) {
			for (let i = 0; i < o.length; i++) {
				let value = o[i];
				const replacingValue = replaceOrRecur(value);
				if (replacingValue !== value) {
					o[i] = replacingValue;
				}
			}
		} else if (typeof o === "object") {
			Object.keys(o).forEach(key => {
				let value = o[key];
				const replacingValue = replaceOrRecur(value);
				if (replacingValue !== value) {
					o[key] = replacingValue;
				}
			})
		}
	}

	private async executeCommand(command: UiCommand): Promise<any> {
		try {
			// console.warn(command);
			this.replaceComponentReferencesWithInstances(command);
			this._executingCommand = true;
			const commandMethodName = command._type.substring(command._type.lastIndexOf('.') + 1);
			if (command.componentId) {
				DefaultTeamAppsUiContext.logger.trace(`Trying to call ${command.componentId}.${commandMethodName}()`);
				let component: any = this.getClientObjectById(command.componentId);
				if (!component) {
					throw new Error("The component " + command.componentId + " does not exist, so cannot call " + commandMethodName + "() on it.");
				} else if (typeof component[commandMethodName] !== "function") {
					throw new Error(`The ${(<any>component.constructor).name || 'component'} ${command.componentId} does not have a method ${commandMethodName}()!`);
				}
				return await this.commandExecutor.executeCommand(component, command);
			} else {
				DefaultTeamAppsUiContext.logger.trace(`Trying to call static method ${command._type}()`);
				const result = await this.commandExecutor.executeStaticCommand(command, this);
				await this.onStaticMethodCommandInvocation.fire(command);
				return result;
			}
		} catch (e) {
			logException(e);
		} finally {
			this._executingCommand = false;
		}
	}

	public setSessionMessageWindows(expiredMessageWindow: UiWindow, errorMessageWindow: UiWindow, terminatedMessageWindow: UiWindow) {
		this.expiredMessageWindow = expiredMessageWindow;
		this.errorMessageWindow = errorMessageWindow;
		this.terminatedMessageWindow = terminatedMessageWindow;
	}
}
