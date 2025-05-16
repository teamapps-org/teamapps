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
import {Connection, ConnectionListener} from "./Connection";
import {ReconnectingCompressingWebSocketConnection} from "./ReconnectingWebSocketConnection";
import {
	ClientInfo,
	CMD,
	CMD_RES,
	EVT,
	INIT,
	INIT_NOK,
	INIT_OK, IReliableServerMessage,
	PING,
	QUERY,
	QUERY_RES,
	REINIT,
	REINIT_NOK,
	REINIT_OK,
	ReliableClientMessage, ReliableServerMessage,
	REQN,
	SESSION_CLOSED,
	SessionClosingReason
} from "../protocol/protocol";


enum ProtocolStatus {
	INITIALIZING,
	REINITIALIZING,
	ERROR,
	ESTABLISHED
}

export class ConnectionImpl implements Connection {

	private sentEventsMinBufferSize = 500;
	private minRequestedCommands = 3;
	private maxRequestedCommands = 20;

	private protocolStatus: ProtocolStatus;

	private connection: ReconnectingCompressingWebSocketConnection;

	private reliableMessagesQueue: ReliableClientMessage[] = [];
	private sentReliableMessagesBuffer: ReliableClientMessage[] = [];

	private clientMessageSequenceNumberCounter = 1;
	private eventResultHandlerByMessageId: Map<number, (result: any) => any> = new Map();

	private maxRequestedCommandId = 0;
	private lastReceivedCommandSequenceNumber: number;

	private currentCommandExecutionPromise: Promise<any> = Promise.resolve();

	constructor(url: string, private sessionId: string, clientInfo: ClientInfo, private connectionListener: ConnectionListener) {
		if (sessionId == null) {
			throw "sessionId may not be null!!";
		}
		this.connection = new ReconnectingCompressingWebSocketConnection(url, {
			onConnected: () => {
				this.protocolStatus = ProtocolStatus.INITIALIZING;
				this.connection.send({
					_type: "INIT",
					sessionId,
					clientInfo,
					maxRequestedCommandId: this.maxRequestedCommands
				} as INIT)
			},
			onMessage: async (messages) => {
				for (var i = 0; i < messages.length; i++) {
					var message = messages[i];

					switch (message._type) {
						case "INIT_OK":
							this.protocolStatus = ProtocolStatus.ESTABLISHED;
							this.sentEventsMinBufferSize = message.sentEventsBufferSize;
							this.initKeepAlive(message.keepaliveInterval ?? 25_000);
							this.minRequestedCommands = message.minRequestedCommands;
							this.maxRequestedCommands = message.maxRequestedCommands;
							this.log("Connection accepted.");
							this.flushPayloadMessages();
							connectionListener.onConnectionInitialized();
							break;
						case "INIT_NOK":
							this.protocolStatus = ProtocolStatus.ERROR;
							this.log("Connection refused. Reason: " + SessionClosingReason[message.reason]);
							connectionListener.onConnectionErrorOrBroken(message.reason);
							this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
							break;
						case "REINIT_OK":
							this.protocolStatus = ProtocolStatus.ESTABLISHED;
							this.log("Reconnect accepted.");

							let lastReceivedEventIndex: number;
							for (let i = 0; i < this.sentReliableMessagesBuffer.length; i++) {
								if (this.sentReliableMessagesBuffer[i].sn === message.lastReceivedEventId) {
									lastReceivedEventIndex = i;
								}
							}
							let eventsToBeResent = this.sentReliableMessagesBuffer.slice(lastReceivedEventIndex + 1, this.sentReliableMessagesBuffer.length);
							this.reliableMessagesQueue.splice(0, 0, ...eventsToBeResent);
							this.sentReliableMessagesBuffer = [];

							this.flushPayloadMessages();
							break;
						case "REINIT_NOK":
							this.protocolStatus = ProtocolStatus.ERROR;
							this.log("Reconnect refused. Reason: " + SessionClosingReason[message.reason]);
							connectionListener.onConnectionErrorOrBroken(message.reason);
							this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
							break;
						case "PING":
							this.log("Got PING from server.");
							this.connection.send({_type: "KEEPALIVE"});
							break;
						case "SESSION_CLOSED":
							this.protocolStatus = ProtocolStatus.ERROR;
							this.log("Error reported by server: " + SessionClosingReason[message.reason] + ": " + message.message);
							connectionListener.onConnectionErrorOrBroken(message.reason, message.message);
							this.connection.stopReconnecting();
							break;
						case "REGISTER_LIB":
							this.handleReliableServerMessage(message, async (message) => {
								await this.connectionListener.registerLibrary(message.lid, message.jsUrl, message.cssUrl);
							});
							break;
						case "CREATE_OBJ":
							this.handleReliableServerMessage(message, async (message) => {
								await this.connectionListener.createClientObject(message.lid, message.typeName, message.oid, message.config, message.evtNames);
							});
							break;
						case "DESTROY_OBJ":
							this.handleReliableServerMessage(message, async (message) => {
								await connectionListener.destroyClientObject(message.oid);
							});
							break;
						case "TOGGLE_EVT":
							this.handleReliableServerMessage(message, async (message) => {
								await connectionListener.toggleEvent(message.lid, message.oid, message.evtName, message.enabled);
							});
							break;
						case "ADD_EVT_HANDLER":
							this.handleReliableServerMessage(message, async (message) => {
								await connectionListener.addEventHandler(message.lid, message.oid, message.evtName, message.registrationId, message.invokableId, message.functionName, message.evtObjAsFirstParam, message.params);
							});
							break;
						case "REMOVE_EVT_HANDLER":
							this.handleReliableServerMessage(message, async (message) => {
								await connectionListener.removeEventHandler(message.lid, message.oid, message.evtName, message.registrationId);
							});
							break;
						case "CMD":
							this.handleReliableServerMessage(message, async (message) => {
								let result = await this.connectionListener.executeCommand(message.lid, message.oid, message.name, message.params);
								if (message.r) {
									this.sendCommandResult(message.sn, result);
								}
							});
							break;
						case "QUERY_RES":
							this.handleReliableServerMessage(message, async (message) => {
								let handler = this.eventResultHandlerByMessageId.get(message.evtId);
								this.eventResultHandlerByMessageId.delete(message.evtId);
								await handler(message.result);
							});
							break;
						default:
							this.log(`ERROR: unknown message type`, message)
					}
				}
			},
			onConnectionLost: () => {
				// do nothing...
			},
			onReconnected: () => {
				this.protocolStatus = ProtocolStatus.REINITIALIZING;
				console.log("Sending REINIT.")
				this.connection.send({
					_type: "REINIT",
					sessionId,
					lastReceivedCommandId: this.lastReceivedCommandSequenceNumber || -1,
					maxRequestedCommandId: this.lastReceivedCommandSequenceNumber + this.maxRequestedCommands
				} as REINIT)
			}
		});
	}

	private initKeepAlive(keepaliveInterval: number) {
		self.setInterval(() => {
			if (this.isConnected()) {
				this.connection.send({_type: "KEEPALIVE"});
			}
		}, keepaliveInterval)
	}

	private ensureEnoughCommandsRequested() {
		if (this.maxRequestedCommandId - this.lastReceivedCommandSequenceNumber <= this.minRequestedCommands) {
			try {
				let maxRequestedCommandId = this.lastReceivedCommandSequenceNumber + this.maxRequestedCommands;
				this.connection.send({
					_type: "REQN",
					lastReceivedCommandId: this.lastReceivedCommandSequenceNumber,
					maxRequestedCommandId: maxRequestedCommandId
				} as REQN);
				this.maxRequestedCommandId = maxRequestedCommandId;
			} catch (e) {
				this.log(`Could not send REQN: ${e}.`);
			}
		}
	}

	private static isINIT_OK(message: any): message is INIT_OK {
		return message._type === 'INIT_OK';
	}

	private static isINIT_NOK(message: any): message is INIT_NOK {
		return message._type === 'INIT_NOK';
	}

	private static isREINIT_OK(message: any): message is REINIT_OK {
		return message._type === 'REINIT_OK';
	}

	private static isREINIT_NOK(message: any): message is REINIT_NOK {
		return message._type === 'REINIT_NOK';
	}

	private static isPING(message: any): message is PING & { tsCompilerTooIntelligentWorkaround: number } {
		return message._type === 'PING';
	}

	private static isCMDS(message: any): message is CMD {
		return message._type === 'CMD';
	}

	private static isQUERY_RES(message: any): message is QUERY_RES {
		return message._type === 'QUERY_RES';
	}

	private static isSESSION_CLOSED(message: any): message is SESSION_CLOSED {
		return message._type === 'SESSION_CLOSED';
	}

	private isConnected() {
		return this.connection.isConnected() && this.protocolStatus === ProtocolStatus.ESTABLISHED;
	}

	public sendEvent(libraryId: string | null, objectId: string | null, name: string, eventObject: any): void {
		let sequenceNumber = this.clientMessageSequenceNumberCounter++;
		let evt: EVT = {
			_type: "EVT",
			sn: sequenceNumber,
			lid: libraryId,
			oid: objectId,
			name,
			evtObj: eventObject
		};
		this.sendClientPayloadMessage(evt);
	}

	public sendQuery(libraryId: string | null, objectId: string | null, name: string, params: any[]): Promise<any> {
		let sequenceNumber = this.clientMessageSequenceNumberCounter++;
		let query: QUERY = {
			_type: "QUERY",
			sn: sequenceNumber,
			lid: libraryId,
			oid: objectId,
			name,
			params
		};
		this.sendClientPayloadMessage(query);
		return new Promise<any>(resolve => this.eventResultHandlerByMessageId.set(sequenceNumber, resolve));
	}

	private sendCommandResult(cmdSn: number, result: any) {
		let cmdResult: CMD_RES = {
			_type: "CMD_RES",
			sn: this.clientMessageSequenceNumberCounter++,
			cmdSn: cmdSn,
			result: result
		};
		this.sendClientPayloadMessage(cmdResult);
	}

	private sendClientPayloadMessage(payloadObject: ReliableClientMessage) {
		this.reliableMessagesQueue.push(payloadObject);
		if (this.isConnected()) {
			this.flushPayloadMessages();
		}
	}

	private flushPayloadMessages() {
		let i: number;
		for (i = 0; i < this.reliableMessagesQueue.length; i++) {
			try {
				this.sendPayloadMessage(this.reliableMessagesQueue[i]);
			} catch (e) {
				console.error(e);
				break;
			}
		}
		this.reliableMessagesQueue.splice(0, i);
	}

	private sendPayloadMessage(message: ReliableClientMessage) {
		this.sentReliableMessagesBuffer.push(message);
		let sentEventsMaxBufferSize = this.sentEventsMinBufferSize * 2;
		if (this.sentReliableMessagesBuffer.length > sentEventsMaxBufferSize) {
			this.sentReliableMessagesBuffer.splice(0, sentEventsMaxBufferSize - this.sentEventsMinBufferSize);
		}
		this.connection.send(message);
	}

	private log(...message: string[]) {
		console.log("TeamAppsConnection: " + message);
	}
	private handleReliableServerMessage<M extends IReliableServerMessage>(message: M, action: (message: M) => Promise<any>) {
		this.lastReceivedCommandSequenceNumber = message.sn;
		this.currentCommandExecutionPromise = this.currentCommandExecutionPromise.finally(async () => {
			try {
				await action(message);
			} catch (e) {
				console.error(e);
			} finally {
				this.ensureEnoughCommandsRequested();
			}
		});
	}
}
