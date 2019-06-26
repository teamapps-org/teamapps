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
import {AbstractServerMessageConfig} from "../../generated/AbstractServerMessageConfig";
import {AbstractClientMessageConfig} from "../../generated/AbstractClientMessageConfig";
import * as log from "loglevel";
import * as stringify from "json-stable-stringify";
import {Inflate, Deflate} from "pako";

export interface ReconnectingCompressingWebSocketConnectionListener {
	onConnected: () => void;
	onMessage: (messageObject: AbstractServerMessageConfig) => void;
	onConnectionLost: () => void;
	onReconnected: () => void;
}

export class ReconnectingCompressingWebSocketConnection {

	private static logger: log.Logger = log.getLogger("ReconnectingCompressingWebSocketConnection");

	private url: string;
	private connection: WebSocket;
	private initialConnection = true;
	private inflator: ZlibStreamingInflator;
	private deflator: ZlibStreamingDeflator;
	private closed: any;

	constructor(url: string, private listener: ReconnectingCompressingWebSocketConnectionListener) {
		this.url = url;
		this.reconnect();
	}

	public isConnected() {
		return this.connection != null && this.connection.readyState === WebSocket.OPEN;
	};

	public send(object: AbstractClientMessageConfig) {
		let jsonString = stringify(object, {
			cmp: (a, b) => {
				let aIsUnderscore = a.key[0] === '_';
				let bIsUnderscore = b.key[0] === '_';
				return (aIsUnderscore && !bIsUnderscore) ? -1 : (!aIsUnderscore && bIsUnderscore) ? 1 : 0;
			}
		});
		const compressed = this.deflator.deflateData(jsonString);
		this.connection.send(compressed);
	};

	private reconnect() {
		if (this.closed) {
			return;
		}

		ReconnectingCompressingWebSocketConnection.log(`Connecting to ${this.url}`);

		this.connection = new WebSocket(this.url);
		this.connection.binaryType = 'arraybuffer';

		this.connection.onopen = () => {
			this.inflator = new ZlibStreamingInflator();
			this.deflator = new ZlibStreamingDeflator();

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
			let jsonString: string;
			try {
				let data: ArrayBuffer = e.data;

				/*
				 * It might make sense in the future to split up large messages into multiple frames.
				 * However, it seems that browsers don't have any problems with receiving (very) large frames, neither do Servlet containers have sending them.
				 * Servlet containers only has a limitation receiving frames (see MAX_BINARY_CLIENT_MESSAGE_SIZE).
				 */
				// data = this.splitMessageConcatenator.pushData(data);

				jsonString = this.inflator.inflateData(data);
			} catch (err) {
				ReconnectingCompressingWebSocketConnection.log("Error while inflating message: " + jsonString + "\n" + err);
				return;
			}
			if (jsonString) {
				try {
					this.listener.onMessage(JSON.parse(jsonString));
				} catch (err) {
					ReconnectingCompressingWebSocketConnection.log("Error while parsing message JSON: " + jsonString + "\n" + err);
				}
			}
		};
	}

	public stopReconnecting() {
		this.closed = true;
	}

	public close() {
		this.stopReconnecting();
		this.connection.close();
	}

	private static log(message: string) {
		console.log("Connection: " + message);
	}
}

class SplitMessageConcatenator {
	private dataBuffer = new Uint8Array(10 * 1024 * 1024); // 10 MB
	private dataBufferFillSize = 0;

	public pushData(data: any): Uint8Array | null {
		let uintArray = new Uint8Array(data);
		let lastMessageFlag = uintArray[0];
		this.dataBuffer.set(uintArray.slice(1), this.dataBufferFillSize);
		this.dataBufferFillSize += uintArray.length - 1;

		let completeMessageData: Uint8Array;
		if (lastMessageFlag) {
			completeMessageData = this.dataBuffer.slice(0, this.dataBufferFillSize);
			this.dataBufferFillSize = 0;
		} else {
			console.warn("Receiving large (splitted) message!");
		}
		return completeMessageData;
	}
}

class ZlibStreamingInflator {
	private inflate: Inflate;
	private currentInflatedData: string;

	constructor() {
		this.inflate = new Inflate({to: 'string'});
		this.inflate.onData = (data: string) => this.currentInflatedData += data;
	}

	public inflateData(data: ArrayBuffer | Uint8Array): string {
		this.currentInflatedData = "";
		this.inflate.push(data, 2); // will update currentInflatedData synchronously!
		return this.currentInflatedData;
	}
}

class ZlibStreamingDeflator {
	private deflate: Deflate;
	private currentInflatedData: Uint8Array = new Uint8Array(20 * 1024 * 1024); // 20 MB
	private currentInflatedDataFillSize = 0;

	constructor() {
		this.deflate = new Deflate();
		this.deflate.onData = (data: Uint8Array) => {
			this.currentInflatedData.set(data, this.currentInflatedDataFillSize);
			this.currentInflatedDataFillSize += data.length;
		}
	}

	public deflateData(data: string): Uint8Array {
		this.currentInflatedDataFillSize = 0;
		this.deflate.push(data, 2); // will update currentInflatedData synchronously!
		return this.currentInflatedData.slice(0, this.currentInflatedDataFillSize)
	}
}
