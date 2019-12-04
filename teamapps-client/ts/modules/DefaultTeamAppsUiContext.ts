/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import {generateUUID, logException} from "./Common";
import {TeamAppsUiContextInternalApi} from "./TeamAppsUiContext";
import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {UiWeekDay} from "../generated/UiWeekDay";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiEvent} from "../generated/UiEvent";
import {UiRootPanel} from "./UiRootPanel";
import {EventRegistrator} from "../generated/EventRegistrator";
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
import {RefreshableComponentProxyHandle} from "./util/RefreshableComponentProxyHandle";
import {TeamAppsConnectionImpl} from "./communication/TeamAppsConnectionImpl";
import {UiComponent} from "./UiComponent";
import {UiSessionClosingReason} from "../generated/UiSessionClosingReason";

export class DefaultTeamAppsUiContext implements TeamAppsUiContextInternalApi {

	private static logger = log.getLogger("DefaultTeamAppsUiContext");

	public readonly onStaticMethodCommandInvocation: TeamAppsEvent<UiCommand> = new TeamAppsEvent(this);
	public readonly sessionId: string;
	public isHighDensityScreen: boolean;
	public config: UiConfigurationConfig = {
		_type: "UiConfigurationConfig",
		isoLanguage: "en",
		themeClassName: null,
		optimizedForTouch: false,
		timeZoneId: "Europe/Berlin",
		firstDayOfWeek: UiWeekDay.MONDAY,
		dateFormat: "yyyy-MM-dd",
		timeFormat: "HH:mm",
		decimalSeparator: ".",
		thousandsSeparator: ""
	};
	public readonly templateRegistry: TemplateRegistry = new TemplateRegistry(this);

	private components: { [identifier: string]: RefreshableComponentProxyHandle<UiComponent> } = {}; // only for debugging!!!
	private _executingCommand: boolean = false;
	private connection: TeamAppsConnection;

	constructor(webSocketUrl: string, clientParameters: {[key: string]: string|number} = {}) {
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
			clientUrl: location.href,
			clientParameters: clientParameters
		});

		let connectionListener: TeamAppsConnectionListener = {
			onConnectionInitialized: () => {
			},
			onConnectionErrorOrBroken: (reason, message) => {
				DefaultTeamAppsUiContext.logger.error("Connection broken.");
				sessionStorage.clear();
				if (reason == UiSessionClosingReason.SESSION_NOT_FOUND || reason == UiSessionClosingReason.SESSION_TIMEOUT || reason == UiSessionClosingReason.HTTP_SESSION_CLOSED) {
					UiRootPanel.showGenericErrorMessage("Session Expired", "<p>Your session has expired.</p><p>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.</p>",
						false, [UiGenericErrorMessageOption.OK, UiGenericErrorMessageOption.RELOAD], this);
				} else if (reason == UiSessionClosingReason.TERMINATED_BY_APPLICATION) {
					UiRootPanel.showGenericErrorMessage("Session Terminated", "<p>Your session has been terminated.</p><p>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.</p>",
						true, [UiGenericErrorMessageOption.OK, UiGenericErrorMessageOption.RELOAD], this);
				} else {
					UiRootPanel.showGenericErrorMessage("Error", "<p>A server-side error has occurred.</p><p>Please reload this page or click OK if you want to refresh later. The application will however remain unresponsive until you reload this page.</p>",
						true, [UiGenericErrorMessageOption.OK, UiGenericErrorMessageOption.RELOAD], this);
				}
			},
			executeCommand: (uiCommand: UiCommand) => this.executeCommand(uiCommand),
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
	public fireEvent(eventObject: UiEvent) {
		this.connection.sendEvent(eventObject);
	}

	public registerComponent(component: UiComponent<UiComponentConfig>, id: string, teamappsType: string): void {
		DefaultTeamAppsUiContext.logger.debug("registering component: ", id);
		if (this.components[id] == null) {
			this.components[id] = new RefreshableComponentProxyHandle(component);
		} else {
			this.components[id].component = component;
		}
		EventRegistrator.registerForEvents(component, teamappsType, this.fireEvent, {componentId: id});
	}

	public createAndRegisterComponent(config: UiComponentConfig) {
		if (TeamAppsUiComponentRegistry.getComponentClassForName(config._type)) {
		let component = new (TeamAppsUiComponentRegistry.getComponentClassForName(config._type))(config, this);
			this.registerComponent(component, config.id, config._type);
			return this.getComponentById(config.id); // return the proxied component!!!
		} else {
			DefaultTeamAppsUiContext.logger.error("Unknown component type: " + config._type);
			return;
		}
	}

	destroyComponent(componentId: string): void {
		let component = this.components[componentId].component;
		if (component != null) {
			component.getMainElement().remove();
			component.destroy();
			delete this.components[componentId];
		} else {
			DefaultTeamAppsUiContext.logger.warn("Could not find component to destroy: " + componentId);
		}
	}

	refreshComponent(config: UiComponentConfig): void {
		this.createAndRegisterComponent(config);
	}

	public getComponentById(id: string): UiComponent<UiComponentConfig> {
		const componentProxyHandle = this.components[id];
		if (componentProxyHandle == null) {
			DefaultTeamAppsUiContext.logger.error(`Cannot find component with id ${id}`);
			return null;
		} else {
			return componentProxyHandle.proxy;
		}
	}
	private replaceComponentReferencesWithInstances(o: any) {
		let replaceOrRecur = (key: number|string) => {
			const value = o[key];
			if (value != null && value._type && typeof (value._type) === "string" && value._type.indexOf("UiComponentReference") !== -1) {
				const componentById = this.getComponentById(value.id);
				if (componentById != null) {
					o[key] = componentById;
				} else {
					throw new Error("Could not find component with id " + value.id);
				}
			} else {
				this.replaceComponentReferencesWithInstances(value);
			}
		};

		if (o == null || typeof o === "string" || typeof o === "boolean" || typeof o === "function" || typeof o === "number" || typeof o === "symbol") {
			return o;
		} else if (Array.isArray(o)) {
			for (let i = 0; i < o.length; i++) {
				replaceOrRecur(i);
			}
		} else if (typeof o === "object") {
			Object.keys(o).forEach(key => {
				replaceOrRecur(key);
			})
		}
	}

	private commandExecutor = new CommandExecutor(reference => this.getComponentById(reference.id));

	private async executeCommand(command: UiCommand): Promise<any> {
		try {
			// console.warn(command);
			this.replaceComponentReferencesWithInstances(command);
			this._executingCommand = true;
			const commandMethodName = command._type.substring(command._type.lastIndexOf('.') + 1);
			if (command.componentId) {
				DefaultTeamAppsUiContext.logger.trace(`Trying to call ${command.componentId}.${commandMethodName}()`);
				let component: any = this.getComponentById(command.componentId);
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
}
