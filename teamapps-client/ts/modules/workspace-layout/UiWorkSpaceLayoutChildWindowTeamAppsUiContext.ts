/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import * as log from "loglevel";
import {UiWorkSpaceLayout, UiWorkspaceLayoutSubWindowProtocol_INIT_OK} from "./UiWorkSpaceLayout";
import {UiRootPanel} from "../UiRootPanel";
import {UiComponentConfig} from "../../generated/UiComponentConfig";
import {TeamAppsUiContext, TeamAppsUiContextInternalApi} from "../TeamAppsUiContext";
import {logException} from "../Common";
import {UiConfigurationConfig} from "../../generated/UiConfigurationConfig";
import {UiEvent} from "../../generated/UiEvent";
import {EventRegistrator} from "../../generated/EventRegistrator";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {TemplateRegistry} from "../TemplateRegistry";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiCommand} from "../../generated/UiCommand";
import {UiComponent} from "../UiComponent";

export class UiWorkSpaceLayoutChildWindowTeamAppsUiContext implements TeamAppsUiContext, TeamAppsUiContextInternalApi {

	private static logger: log.Logger = log.getLogger("UiWorkSpaceLayoutChildWindowTeamAppsUiContext");

	public readonly onStaticMethodCommandInvocation: TeamAppsEvent<UiCommand> = new TeamAppsEvent(this);
	private _sessionId: string;
	public readonly isHighDensityScreen: boolean;
	public config: UiConfigurationConfig;
	public readonly templateRegistry: TemplateRegistry = new TemplateRegistry(this);

	private workSpaceLayout: UiWorkSpaceLayout;
	private parentWindowMessagePort: MessagePort;
	private _executingCommand: boolean = false;
	private components: { [identifier: string]: UiComponent<UiComponentConfig> } = {};

	constructor() {
		this.isHighDensityScreen = ((window.matchMedia && (window.matchMedia('only screen and (min-resolution: 124dpi), only screen and (min-resolution: 1.3dppx), only screen and (min-resolution: 48.8dpcm)').matches || window.matchMedia('only screen and (-webkit-min-device-pixel-ratio: 1.3), only screen and (-o-min-device-pixel-ratio: 2.6/2), only screen and (min--moz-device-pixel-ratio: 1.3), only screen and (min-device-pixel-ratio: 1.3)').matches)) || (window.devicePixelRatio && window.devicePixelRatio > 1.3));
		if (this.isHighDensityScreen) {
			document.body.classList.add('high-density-screen');
		}
		window.onmessage = (e: MessageEvent) => {
			if (e.origin !== location.origin || e.ports.length === 0) {
				return;
			}
			UiWorkSpaceLayoutChildWindowTeamAppsUiContext.logger.debug(e.data);
			this.parentWindowMessagePort = e.ports[0];
			setTimeout(() => this.parentWindowMessagePort.postMessage({_type: 'INIT'}), 500); // TODO remove timeout!
			this.parentWindowMessagePort.onmessage = (e) => {
				let messageObject = JSON.parse(e.data);
				UiWorkSpaceLayoutChildWindowTeamAppsUiContext.logger.debug(messageObject);
				if (UiWorkSpaceLayoutChildWindowTeamAppsUiContext.isINIT_OK(messageObject)) {
					this._sessionId = messageObject.sessionId;
					this.config = messageObject.uiConfiguration;
					let rootPanel = new UiRootPanel({
						_type: "UiRootPanel",
						id: "ROOT"
					}, this);
					UiRootPanel.setConfig(messageObject.uiConfiguration, this);
					UiRootPanel.registerTemplates(messageObject.registeredTemplates, this);
					// TODO set background image!
					document.body.appendChild(rootPanel.getMainElement());
					this.workSpaceLayout = new UiWorkSpaceLayout({
						_type: "UiWorkSpaceLayout",
						id: messageObject.workspaceLayoutId,
						views: null,
						initialLayout: null,
						toolbar: null,
						childWindowPageTitle: messageObject.childWindowPageTitle,
					}, this, messageObject.windowId, this.parentWindowMessagePort);
					this.registerClientObject(this.workSpaceLayout, messageObject.workspaceLayoutId, "UiWorkSpaceLayout");
					rootPanel.setContent(this.workSpaceLayout);
				} else if (messageObject._type === "COMMAND") {
					const commandMethodName = messageObject.methodName as string;
					if (messageObject.componentId) {
						let component = this.components[messageObject.componentId];
						if (!component) {
							throw new Error("The component " + messageObject.componentId + " does not exist, so cannot call " + commandMethodName + "() on it.");
						} else if (typeof (component as any)[commandMethodName] !== "function") {
							throw new Error(`The ${(<any>component.constructor).name || 'component'} ${messageObject.componentId} does not have a method ${commandMethodName}()!`);
						}
						const result = (component as any)[messageObject.methodName].apply(component, messageObject.arguments);
						if (result instanceof Promise) {
							result.catch(reason => logException(reason, e.data));
						}
					} else {
						// static method invocation
						const dotIndex = commandMethodName.indexOf(".");
						const className = commandMethodName.substring(0, dotIndex);
						const methodName = commandMethodName.substring(dotIndex + 1);
						const componentClass = TeamAppsUiComponentRegistry.getComponentClassForName(className);
						(componentClass as any)[methodName](messageObject.arguments);
					}
				} else if (messageObject._type === "WORKSPACELAYOUT_METHOD_INVOCATION") {
					let {methodName, args, correlationId} = messageObject;
					Promise.resolve((this.workSpaceLayout as any)[methodName](args))
						.then(value => this.parentWindowMessagePort.postMessage({
							_type: "WORKSPACELAYOUT_METHOD_INVOCATION_RESPONSE",
							correlationId: correlationId,
							successful: true,
							returnValue: value
						}))
						.catch(reason => this.parentWindowMessagePort.postMessage({
							_type: "WORKSPACELAYOUT_METHOD_INVOCATION_RESPONSE",
							correlationId: correlationId,
							successful: false,
							returnValue: reason
						}));
				}
			};
		};
	}

	private static isINIT_OK(messageObject: any): messageObject is UiWorkspaceLayoutSubWindowProtocol_INIT_OK {
		return messageObject._type === "INIT_OK"
	}

	public get sessionId() {
		return this._sessionId;
	}

	public get executingCommand() {
		return this._executingCommand;
	}

	fireEvent(eventObject: UiEvent): void {
		this.parentWindowMessagePort.postMessage({
			_type: "EVENT",
			eventObject
		})
	}

	registerClientObject(component: UiComponent<UiComponentConfig>, id: string, teamappsType: string): void {
		this.components[id] = component;
		EventRegistrator.registerForEvents(component, teamappsType, (eventObject: UiEvent) => this.fireEvent(eventObject), {id: id});

		if (id !== this.workSpaceLayout.getId()) {
			this.parentWindowMessagePort.postMessage({
				_type: "REGISTER_COMPONENT",
				componentId: id,
				teamappsType: teamappsType
			});
		}
	}

	destroyClientObject(id: string): void {
		let c = this.components[id];
		if (c == null) {
			UiWorkSpaceLayoutChildWindowTeamAppsUiContext.logger.error("Could not find component to destroy: " + id)
		}
		c.getMainElement().remove();
		c.destroy();
		delete this.components[id];
	}

	public createClientObject(config: UiComponentConfig) {
		let component: UiComponent<UiComponentConfig>;
		if ((TeamAppsUiComponentRegistry.getComponentClassForName(config._type))) {
			component = new (TeamAppsUiComponentRegistry.getComponentClassForName(config._type))(config, this);
			this.registerClientObject(component, config.id, config._type);
			return component;
		} else {
			UiWorkSpaceLayoutChildWindowTeamAppsUiContext.logger.error("Unknown component type: " + config._type);
			return;
		}
	}

	public getClientObjectById(id: string): UiComponent<UiComponentConfig> {
		return this.components[id];
	}

	refreshComponent(config: UiComponentConfig): void {
		console.error("TODO refreshComponent"); // TODO #componentRef
	}

}
