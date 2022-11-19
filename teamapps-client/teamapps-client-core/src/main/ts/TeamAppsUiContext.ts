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
import {DtoClientObject as DtoClientObjectConfig, DtoComponent as DtoComponentConfig, DtoConfiguration} from "./generated";
import {DtoCommand, DtoEvent} from "teamapps-client-communication";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {ClientObject} from "./ClientObject";

export interface TeamAppsUiContext {
	readonly sessionId: string;
	readonly isHighDensityScreen: boolean;
	readonly config: DtoConfiguration;

	getClientObjectById(id: string): Promise<ClientObject>;
}

// TeamAppsUiContext implementations should implement this too. See usages.
export interface TeamAppsUiContextInternalApi extends TeamAppsUiContext {
	readonly onStaticMethodCommandInvocation: TeamAppsEvent<DtoCommand>;

	renderClientObject(libraryUuid: string, config: DtoClientObjectConfig): Promise<ClientObject>;

	refreshComponent(libraryUuid: string, config: DtoComponentConfig): void;

	destroyClientObject(componentId: string);

	sendEvent(eventObject: DtoEvent): void;

	toggleEventListener(libraryUuid: string | null, clientObjectId: string | null, qualifiedEventName: string, enabled: boolean): any;

	registerComponentLibrary(uuid: string, mainJsUrl: string): void;
}
