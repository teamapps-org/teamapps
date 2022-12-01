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
import stringify from "json-stable-stringify";
import {DtoAbstractClientMessage, DtoAbstractServerMessage} from "../protocol/protocol";

export interface ReconnectingCompressingWebSocketConnectionListener {
	onConnected: () => void;
	onMessage: (messageObject: DtoAbstractServerMessage) => void;
	onConnectionLost: () => void;
	onReconnected: () => void;
}

export class ReconnectingCompressingWebSocketConnection {

	private url: string;
	private connection: WebSocket;
	private initialConnection = true;
	private closed: any;

	constructor(url: string, private listener: ReconnectingCompressingWebSocketConnectionListener) {
		this.url = url;
		this.reconnect();
	}

	public isConnected() {
		return this.connection != null && this.connection.readyState === WebSocket.OPEN;
	};

	public send(object: DtoAbstractClientMessage) {
		let jsonString = stringify(object, {
			cmp: (a, b) => {
				let aIsUnderscore = a.key[0] === '_';
				let bIsUnderscore = b.key[0] === '_';
				return (aIsUnderscore && !bIsUnderscore) ? -1 : (!aIsUnderscore && bIsUnderscore) ? 1 : 0;
			}
		});
		this.connection.send(jsonString);
	};

	private reconnect() {
		if (this.closed) {
			return;
		}

		ReconnectingCompressingWebSocketConnection.log(`Connecting to ${this.url}`);

		this.connection = new WebSocket(this.url);
		this.connection.binaryType = 'arraybuffer';

		this.connection.onopen = () => {
			if (this.initialConnection) {
				ReconnectingCompressingWebSocketConnection.log("Connected.");
				this.listener.onConnected();
				this.initialConnection = false;
			} else {
				ReconnectingCompressingWebSocketConnection.log("Reconnected.");
				this.listener.onReconnected();
			}
		};
		this.connection.onclose = () => {
			console.warn('Websocket connection CLOSED!');
			this.listener.onConnectionLost();
			this.connection = null;
			setTimeout(() => this.reconnect(), 2000);
		};
		this.connection.onerror = (error) => {
			ReconnectingCompressingWebSocketConnection.log('WebSocket error: ' + error);
		};
		this.connection.onmessage = (e) => {
			let json: string = e.data as string;
			if (json) {
				try {
					this.listener.onMessage(JSON.parse(json));
				} catch (err) {
					ReconnectingCompressingWebSocketConnection.log("Error while parsing message JSON: " + json + "\n" + err);
				}
			}
		};
	}

	public stopReconnecting() {
		this.closed = true;
	}


	private static log(message: string) {
		console.log("Connection: " + message);
	}
}
