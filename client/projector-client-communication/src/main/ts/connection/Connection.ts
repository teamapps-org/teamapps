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

import {SessionClosingReason} from "../protocol/protocol";

export interface Connection {
	sendEvent(lid: string | null, oid: string | null, name: string, eventObject: any): void;
	sendQuery(lid: string | null, oid: string | null, name: string, params: any[]): Promise<any>;
}

export interface ConnectionListener {
	onConnectionInitialized(): void;
	onConnectionErrorOrBroken(reason: SessionClosingReason, message?: string): void;
	registerLibrary(libraryId: string, mainJsUrl: string, mainCssUrl: string): void;
	createClientObject(libraryId: string, typeName: string, objectId: string, config: any, enabledEventNames: string[]): Promise<any>;
	destroyClientObject(oid: string): Promise<any>;
	toggleEvent(libraryUuid: string | null, clientObjectId: string | null, eventName: string, enabled: boolean): Promise<any>;
	executeCommand(libraryUuid: string, clientObjectId: string, commandName: string, params: any[]): Promise<any>;
	addEventHandler(libraryUuid: string, clientObjectId: string, eventName: string, registrationId: string, invokableId: string, functionName: string, evtObjAsFirstParam: boolean, params: any[]);
	removeEventHandler(libraryUuid: string, clientObjectId: string, eventName: string, registrationId: string);
}
