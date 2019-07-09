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
import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiEvent} from "../generated/UiEvent";
import {TemplateRegistry} from "./TemplateRegistry";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiCommand} from "../generated/UiCommand";
import {UiComponent} from "./UiComponent";

export const typescriptDeclarationFixConstant = 1;

export interface IconPathProvider {
	getIconPath(iconName: string, iconSize: number, ignoreRetina?: boolean): string;
}

export interface TeamAppsUiContext extends IconPathProvider {
	readonly sessionId: string;
	readonly isHighDensityScreen: boolean;
	readonly executingCommand: boolean;
	readonly config: UiConfigurationConfig;
	readonly templateRegistry: TemplateRegistry;

	getComponentById(id: string): UiComponent<UiComponentConfig>;
}

// TeamAppsUiContext implementations should implement this too. See usages.
export interface TeamAppsUiContextInternalApi extends TeamAppsUiContext{
	readonly onStaticMethodCommandInvocation: TeamAppsEvent<UiCommand>;
	registerComponent(component: UiComponent<UiComponentConfig>, id: string, teamappsType: string): void;
	createAndRegisterComponent(config: UiComponentConfig): UiComponent<UiComponentConfig>;
	destroyComponent(componentId: string): void;
	refreshComponent(config: UiComponentConfig): void;
	fireEvent(eventObject: UiEvent): void;
}
