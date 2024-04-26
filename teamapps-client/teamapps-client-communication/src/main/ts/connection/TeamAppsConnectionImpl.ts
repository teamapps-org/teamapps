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
import {
	ClientMessage, CMD,
	REQN,
	CMD_RES, EVT, QUERY_RES, INIT, INIT_NOK, INIT_OK, PING,
	REINIT,
	REINIT_NOK,
	REINIT_OK, ReliableClientMessage,
	SESSION_CLOSED, QUERY, ClientInfo, SessionClosingReason
} from "../protocol/protocol";


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

	private reliableMessagesQueue: ReliableClientMessage[] = [];
	private sentReliableMessagesBuffer: ReliableClientMessage[] = [];

	private clientMessageSequenceNumberCounter = 1;
	private eventResultHandlerByMessageId: Map<number, (result: any) => any> = new Map();

	private maxRequestedCommandId = 0;
	private lastReceivedCommandSequenceNumber: number;

	private currentCommandExecutionPromise: Promise<any> = Promise.resolve();

	constructor(url: string, private sessionId: string, clientInfo: ClientInfo, private commandHandler: TeamAppsConnectionListener) {
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
				if (TeamAppsConnectionImpl.isCMDS(message)) {
					let cmd = message as CMD;
					this.lastReceivedCommandSequenceNumber = cmd.sn;
					this.enchainCommandExecution(cmd);
				} else if (TeamAppsConnectionImpl.isQUERY_RES(message)) {
					this.eventResultHandlerByMessageId.get(message.evtId)(message.result);
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
					for (let i = 0; i < this.sentReliableMessagesBuffer.length; i++) {
						if (this.sentReliableMessagesBuffer[i].sn === message.lastReceivedEventId) {
							lastReceivedEventIndex = i;
						}
					}
					let eventsToBeResent = this.sentReliableMessagesBuffer.slice(lastReceivedEventIndex + 1, this.sentReliableMessagesBuffer.length);
					this.reliableMessagesQueue.splice(0, 0, ...eventsToBeResent);
					this.sentReliableMessagesBuffer = [];

					this.flushPayloadMessages();
				} else if (TeamAppsConnectionImpl.isINIT_NOK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Connection refused. Reason: " + SessionClosingReason[message.reason]);
					commandHandler.onConnectionErrorOrBroken(message.reason);
					this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
				} else if (TeamAppsConnectionImpl.isREINIT_NOK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Reconnect refused. Reason: " + SessionClosingReason[message.reason]);
					commandHandler.onConnectionErrorOrBroken(message.reason);
					this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
				} else if (TeamAppsConnectionImpl.isPING(message)) {
					this.log("Got PING from server.");
					this.connection.send({_type: "KEEPALIVE"});
				} else if (TeamAppsConnectionImpl.isSESSION_CLOSED(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Error reported by server: " + SessionClosingReason[message.reason] + ": " + message.message);
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
		return this.connection.isConnected() && this.protocolStatus === TeamAppsProtocolStatus.ESTABLISHED;
	}

	public sendEvent(libraryId: string | null, objectId: string | null, name: string, params: any[]): void {
		let sequenceNumber = this.clientMessageSequenceNumberCounter++;
		let evt: EVT = {
			_type: "EVT",
			sn: sequenceNumber,
			lid: libraryId,
			oid: objectId,
			name,
			params
		};
		this.sendClientPayloadMessage(evt);
	}

	public sendQuery(libraryId: string | null, objectId: string | null, name: string, params: any[]): Promise<any> {
		let sequenceNumber = this.clientMessageSequenceNumberCounter++;
		let evt: QUERY = {
			_type: "QUERY",
			sn: sequenceNumber,
			lid: libraryId,
			oid: objectId,
			name,
			params
		};
		this.sendClientPayloadMessage(evt);
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

	private enchainCommandExecution(cmd: CMD) {
		this.currentCommandExecutionPromise = this.currentCommandExecutionPromise.finally(async () => {
			try {
				let result = await this.commandHandler.executeCommand(cmd.lid, cmd.oid, cmd.name, cmd.params);
				if (cmd.r) {
					this.sendCommandResult(cmd.sn, result);
				}
			} catch (reason) {
				console.error(reason);
			} finally {
				this.ensureEnoughCommandsRequested();
			}
		});
	}
}
