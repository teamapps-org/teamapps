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
import {ClientObject} from "./ClientObject";
import {DtoConfiguration} from "./generated";

export interface TeamAppsUiContext {
	readonly sessionId: string;
	readonly config: DtoConfiguration;

	getClientObjectById(id: string): Promise<ClientObject>;
}

// TeamAppsUiContext implementations should implement this too. See usages.
export interface TeamAppsUiContextInternalApi extends TeamAppsUiContext {

	registerComponentLibrary(uuid: string, mainJsUrl: string, mainCssUrl: string): void;

	createClientObject(libraryUuid: string, typeName: string, objectId: string, config: any, enabledEventNames: string[]): void;

	destroyClientObject(oid: string);

	toggleEvent(libraryUuid: string | null, clientObjectId: string | null, eventName: string, enabled: boolean): any;

}
