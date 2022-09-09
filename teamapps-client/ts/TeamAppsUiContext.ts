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
import {UiConfigurationConfig} from "./generated/UiConfigurationConfig";
import {UiComponentConfig} from "./generated/UiComponentConfig";
import {UiEventConfig as UiEvent} from "./generated/UiEventConfig";
import {TemplateRegistry} from "./TemplateRegistry";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiCommand} from "./generated/UiCommand";
import {UiClientObject} from "./UiClientObject";
import {UiClientObjectConfig} from "./generated/UiClientObjectConfig";

export const typescriptDeclarationFixConstant = 1;

export interface TeamAppsUiContext {
	readonly sessionId: string;
	readonly isHighDensityScreen: boolean;
	readonly executingCommand: boolean;
	readonly config: UiConfigurationConfig;
	readonly templateRegistry: TemplateRegistry;

	getClientObjectById(id: string): Promise<UiClientObject>;
}

// TeamAppsUiContext implementations should implement this too. See usages.
export interface TeamAppsUiContextInternalApi extends TeamAppsUiContext {
	readonly onStaticMethodCommandInvocation: TeamAppsEvent<UiCommand>;

	renderClientObject(libraryUuid: string, config: UiClientObjectConfig): Promise<UiClientObject>;

	refreshComponent(libraryUuid: string, config: UiComponentConfig): void;

	destroyClientObject(componentId: string);

	sendEvent(eventObject: UiEvent): void;

	toggleEventListener(libraryUuid: string | null, clientObjectId: string | null, qualifiedEventName: string, enabled: boolean): any;

	registerComponentLibrary(uuid: string, mainJsUrl: string): void;
}
