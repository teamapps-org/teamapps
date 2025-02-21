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
	sendEvent(name: string, eventObject: any): void;

	sendQuery(name: string, ...params: any[]): Promise<any>;
}

export function isServerObjectChannel(o: any): o is ServerObjectChannel {
	return typeof (o) === "object" && typeof (o.sendEvent) === "function" && typeof (o.sendQuery) === "function";
}

export const noOpServerObjectChannel = {
	sendEvent(name: string, params: any[]) {
	},
	sendQuery(name: string, params: any[]): Promise<any> {
		return Promise.resolve();
	}
}

export interface ComponentLibrary {
	/**
	 * Initializes this component library.
	 * @param serverObjectChannel the serverObjectChannel for sending global/static events and queries.
	 */
	init?: (serverObjectChannel: ServerObjectChannel) => void;

	/**
	 * ComponentLibrary implementations may choose between providing an exported class/constructor for each client object type
	 * or this catch-all method for instantiating client objects.
	 *
	 * @param typeName the name of the client object type
	 * @param config the configuration for the client object to instantiate
	 * @param serverObjectChannel the ServerObjectChannel to use for sending events/queries from this client object
	 */
	createClientObject?(typeName: string, config: any, serverObjectChannel: ServerObjectChannel): Promise<ClientObject>
}

/**
 * An object controlled by the server, using commands. ClientObjects may send events and issue queries to the server throug the provided
 * ServerObjectChannel.
 *
 * ClientObjects are instantiated using one of two approaches:
 * * Either by calling the ComponentLibrary's createClientObject method
 * * or by calling the constructor of an exported class with the name of the client object type, with two parameters:
 *   a configuration object and a ServerObjectChannel.
 *
 * Additionally, if provided, the init method is going to be called with the same parameters.
 */
export interface ClientObject {

	/**
	 * Additional init method, called right after instantiating. This method is guaranteed to be called, if implemented.
	 */
	init?(config: any, serverObjectChannel?: ServerObjectChannel): any;

	/**
	 * ClientObject implementations may choose between implementing either an individual method for each command name (named by the command)
	 * or this method as a catch-all for all commands.
	 *
	 * @param name name of the command
	 * @param params parameters of the command
	 */
	executeCommand?(name: string, params: any[]): Promise<any>;

	/**
	 * Invoked to destroy the object.
	 */
	destroy?(): void;
}
