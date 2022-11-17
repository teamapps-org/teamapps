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
import {TeamAppsConnection, TeamAppsConnectionListener} from "./TeamAppsConnection";
import {ReconnectingCompressingWebSocketConnection} from "./ReconnectingWebSocketConnection";
import {logException} from "../Common";
import {
	AbstractClientPayloadMessage,
	CMD, CMD_REQUEST, CMD_RESULT, EVENT,
	INIT, INIT_NOK, INIT_OK, MULTI_CMD, QUERY, QUERY_RESULT,
	REINIT, REINIT_NOK, REINIT_OK, SESSION_CLOSED,
	UiClientInfo, UiEvent,
	UiQuery,
	UiSessionClosingReason
} from "teamapps-client-communication";


enum TeamAppsProtocolStatus {
	INITIALIZING,
	REINITIALIZING,
	ERROR,
	ESTABLISHED
}

export class TeamAppsConnectionImpl implements TeamAppsConnection {

	private sentEventsMinBufferSize = 500;
	private minRequestedCommands = 3;
	private maxRequestedCommands = 20;

	private protocolStatus: TeamAppsProtocolStatus;

	private connection: ReconnectingCompressingWebSocketConnection;

	private payloadMessagesQueue: AbstractClientPayloadMessage[] = [];
	private sentEventsBuffer: AbstractClientPayloadMessage[] = [];

	private clientMessageIdCounter = 1;
	private queryResultHandlerByMessageId: Map<number, (result: any) => any> = new Map();

	private maxRequestedCommandId = 0;
	private lastReceivedCommandId: number;

	constructor(url: string, private sessionId: string, clientInfo: UiClientInfo, commandHandler: TeamAppsConnectionListener) {
		if (sessionId == null) {
			throw "sessionId may not be null!!";
		}
		this.connection = new ReconnectingCompressingWebSocketConnection(url, {
			onConnected: () => {
				this.protocolStatus = TeamAppsProtocolStatus.INITIALIZING;
				this.connection.send({
					_type: "INIT",
					sessionId,
					clientInfo,
					maxRequestedCommandId: this.maxRequestedCommands
				} as INIT)
			},
			onMessage: async (message) => {
				if (TeamAppsConnectionImpl.isMULTI_CMD(message)) {
					let cmds = message.cmds as CMD[];
					for (let cmd of cmds) {
						this.lastReceivedCommandId = cmd.id;
						try {
							let result = await commandHandler.executeCommand(cmd.lid, cmd.cid, cmd.c);
							if (cmd.r) {
								this.sendResult(cmd.id, result);
							}
						} catch (reason) {
							logException(reason);
						} finally {
							this.ensureEnoughCommandsRequested();
						}
					}
					this.lastReceivedCommandId = cmds[cmds.length - 1].id;
				} else if (TeamAppsConnectionImpl.isQUERY_RESULT(message)) {
					this.queryResultHandlerByMessageId.get(message.queryId)(message.result);
				} else if (TeamAppsConnectionImpl.isINIT_OK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ESTABLISHED;
					this.sentEventsMinBufferSize = message.sentEventsBufferSize;
					this.initKeepAlive(message.keepaliveInterval ?? 25_000);
					this.minRequestedCommands = message.minRequestedCommands;
					this.maxRequestedCommands = message.maxRequestedCommands;
					this.log("Connection accepted.");
					this.flushPayloadMessages();
					commandHandler.onConnectionInitialized();
				} else if (TeamAppsConnectionImpl.isREINIT_OK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ESTABLISHED;
					this.log("Reconnect accepted.");

					let lastReceivedEventIndex: number;
					for (let i = 0; i < this.sentEventsBuffer.length; i++) {
						if (this.sentEventsBuffer[i].id === message.lastReceivedEventId) {
							lastReceivedEventIndex = i;
						}
					}
					let eventsToBeResent = this.sentEventsBuffer.slice(lastReceivedEventIndex + 1, this.sentEventsBuffer.length);
					this.payloadMessagesQueue.splice(0, 0, ...eventsToBeResent);
					this.sentEventsBuffer = [];

					this.flushPayloadMessages();
				} else if (TeamAppsConnectionImpl.isINIT_NOK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Connection refused. Reason: " + UiSessionClosingReason[message.reason]);
					commandHandler.onConnectionErrorOrBroken(message.reason);
					this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
				} else if (TeamAppsConnectionImpl.isREINIT_NOK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Reconnect refused. Reason: " + UiSessionClosingReason[message.reason]);
					commandHandler.onConnectionErrorOrBroken(message.reason);
					this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
				} else if (TeamAppsConnectionImpl.isPING(message)) {
					this.log("Got PING from server.");
					this.connection.send({_type: "KEEPALIVE", sessionId: this.sessionId});
				} else if (TeamAppsConnectionImpl.isSESSION_CLOSED(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Error reported by server: " + UiSessionClosingReason[message.reason] + ": " + message.message);
					commandHandler.onConnectionErrorOrBroken(message.reason, message.message);
					this.connection.stopReconnecting();
				} else {
					this.log(`ERROR: unknown message type: ${message._type}`)
				}
			},
			onConnectionLost: () => {
				// do nothing...
			},
			onReconnected: () => {
				this.protocolStatus = TeamAppsProtocolStatus.REINITIALIZING;
				this.connection.send({
					_type: "REINIT",
					sessionId,
					lastReceivedCommandId: this.lastReceivedCommandId || -1,
					maxRequestedCommandId: this.lastReceivedCommandId + this.maxRequestedCommands
				} as REINIT)
			}
		});
	}

	private initKeepAlive(keepaliveInterval: number) {
		self.setInterval(() => {
			if (this.isConnected()) {
				this.connection.send({_type: "KEEPALIVE", sessionId: this.sessionId});
			}
		}, keepaliveInterval)
	}

	private ensureEnoughCommandsRequested() {
		if (this.maxRequestedCommandId - this.lastReceivedCommandId <= this.minRequestedCommands) {
			try {
				let maxRequestedCommandId = this.lastReceivedCommandId + this.maxRequestedCommands;
				this.connection.send({
					_type: "CMD_REQUEST",
					sessionId: this.sessionId,
					lastReceivedCommandId: this.lastReceivedCommandId,
					maxRequestedCommandId: maxRequestedCommandId
				} as CMD_REQUEST);
				this.maxRequestedCommandId = maxRequestedCommandId;
			} catch (e) {
				this.log(`Could not send CMD_REQUEST: ${e}.`);
			}
		}
	}

	private static isINIT_OK(message: any): message is INIT_OK & { tscompilerbugworkaround: number } {
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

	private static isPING(message: any): message is REINIT_NOK {
		return message._type === 'PING';
	}

	private static isMULTI_CMD(message: any): message is MULTI_CMD {
		return message._type === 'MULTI_CMD';
	}

	private static isQUERY_RESULT(message: any): message is QUERY_RESULT {
		return message._type === 'QUERY_RESULT';
	}

	private static isSESSION_CLOSED(message: any): message is SESSION_CLOSED {
		return message._type === 'SESSION_CLOSED';
	}

	private isConnected() {
		return this.connection.isConnected() && this.protocolStatus === TeamAppsProtocolStatus.ESTABLISHED;
	}

	public sendEvent(event: UiEvent) {
		let protocolEvent: EVENT = {
			_type: "EVENT",
			sessionId: this.sessionId,
			id: this.clientMessageIdCounter++,
			uiEvent: event
		};
		this.sendClientPayloadMessage(protocolEvent);
	}

	sendQuery(query: UiQuery): Promise<any> {
		let clientMessageId = this.clientMessageIdCounter++;
		let protocolQuery: QUERY = {
			_type: "QUERY",
			sessionId: this.sessionId,
			id: clientMessageId,
			uiQuery: query
		};
		this.sendClientPayloadMessage(protocolQuery);
		return new Promise<any>(resolve => this.queryResultHandlerByMessageId.set(clientMessageId, resolve))
	}

	private sendResult(cmdId: number, result: any) {
		let cmdResult: CMD_RESULT = {
			_type: "CMD_RESULT",
			sessionId: this.sessionId,
			id: this.clientMessageIdCounter++,
			cmdId: cmdId,
			result: result
		};
		this.sendClientPayloadMessage(cmdResult);
	}

	private sendClientPayloadMessage(cmdResult: AbstractClientPayloadMessage) {
		this.payloadMessagesQueue.push(cmdResult);
		if (this.isConnected()) {
			this.flushPayloadMessages();
		}
	}

	private flushPayloadMessages() {
		let i: number;
		for (i = 0; i < this.payloadMessagesQueue.length; i++) {
			try {
				this.sendPayloadMessage(this.payloadMessagesQueue[i]);
			} catch (e) {
				logException(e);
				break;
			}
		}
		this.payloadMessagesQueue.splice(0, i);
	}

	private sendPayloadMessage(payloadMessage: AbstractClientPayloadMessage) {
		this.sentEventsBuffer.push(payloadMessage);
		let sentEventsMaxBufferSize = this.sentEventsMinBufferSize * 2;
		if (this.sentEventsBuffer.length > sentEventsMaxBufferSize) {
			this.sentEventsBuffer.splice(0, sentEventsMaxBufferSize - this.sentEventsMinBufferSize);
		}
		this.connection.send(payloadMessage);
	}

	private log(...message: string[]) {
		console.log("TeamAppsConnection: " + message);
	}
}
