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

export interface ServerObjectChannel {
	sendEvent(name: string, params: any[]): void;
	sendQuery(name: string, params: any[]): Promise<any>;
}

export const noOpServerChannel = {
	sendEvent(name: string, params: any[]) {
	},
	sendQuery(name: string, params: any[]): Promise<any> {
		return Promise.resolve();
	}
}

export interface ClientObjectFactory {
	createClientObject(typeName: string, config: any, serverChannel: ServerObjectChannel): Promise<ClientObject>;
}

export interface ClientObject {
	init?(): any; // additional init method, called right after instantiating
	invoke(name: string, params: any[]): Promise<any>;
	destroy(): void;
}
