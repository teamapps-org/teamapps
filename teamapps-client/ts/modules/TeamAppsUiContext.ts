/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiEvent} from "../generated/UiEvent";
import {TemplateRegistry} from "./TemplateRegistry";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiCommand} from "../generated/UiCommand";
import {UiComponent} from "./UiComponent";
import {UiClientObject} from "./UiClientObject";
import {UiClientObjectConfig} from "../generated/UiClientObjectConfig";

export const typescriptDeclarationFixConstant = 1;

export interface TeamAppsUiContext {
	readonly sessionId: string;
	readonly isHighDensityScreen: boolean;
	readonly executingCommand: boolean;
	readonly config: UiConfigurationConfig;
	readonly templateRegistry: TemplateRegistry;

	getClientObjectById(id: string): UiClientObject;
}

// TeamAppsUiContext implementations should implement this too. See usages.
export interface TeamAppsUiContextInternalApi extends TeamAppsUiContext {
	readonly onStaticMethodCommandInvocation: TeamAppsEvent<UiCommand>;

	registerClientObject(component: UiClientObject, id: string, teamappsType: string): void;

	createClientObject(config: UiClientObjectConfig): UiClientObject;

	refreshComponent(config: UiComponentConfig): void;

	destroyClientObject(componentId: string): void;

	sendEvent(eventObject: UiEvent): void;
}
