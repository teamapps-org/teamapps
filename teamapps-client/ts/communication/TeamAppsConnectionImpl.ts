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
import {UiClientInfoConfig} from "../generated/UiClientInfoConfig";
import {INITConfig} from "../generated/INITConfig";
import {INIT_NOKConfig} from "../generated/INIT_NOKConfig";
import {REINIT_NOKConfig} from "../generated/REINIT_NOKConfig";
import {REINITConfig} from "../generated/REINITConfig";
import {CMD_REQUESTConfig} from "../generated/CMD_REQUESTConfig";
import {INIT_OKConfig} from "../generated/INIT_OKConfig";
import {REINIT_OKConfig} from "../generated/REINIT_OKConfig";
import {MULTI_CMDConfig} from "../generated/MULTI_CMDConfig";
import {EVENTConfig} from "../generated/EVENTConfig";
import {SESSION_CLOSEDConfig} from "../generated/SESSION_CLOSEDConfig";
import {CMD} from "./CMD";
import {logException} from "../Common";
import {AbstractClientPayloadMessageConfig} from "../generated/AbstractClientPayloadMessageConfig";
import {UiEvent} from "../generated/UiEvent";
import {CMD_RESULTConfig} from "../generated/CMD_RESULTConfig";
import {UiSessionClosingReason} from "../generated/UiSessionClosingReason";
import {UiQuery} from "../generated/UiQuery";
import {QUERYConfig} from "../generated/QUERYConfig";
import {QUERY_RESULTConfig} from "../generated/QUERY_RESULTConfig";


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

	private payloadMessagesQueue: AbstractClientPayloadMessageConfig[] = [];
	private sentEventsBuffer: AbstractClientPayloadMessageConfig[] = [];

	private clientMessageIdCounter = 1;
	private queryResultHandlerByMessageId: Map<number, (result: any) => any> = new Map();

	private maxRequestedCommandId = 0;
	private lastReceivedCommandId: number;

	constructor(url: string, private sessionId: string, clientInfo: UiClientInfoConfig, commandHandler: TeamAppsConnectionListener) {
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
				} as INITConfig)
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
				} as REINITConfig)
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
				} as CMD_REQUESTConfig);
				this.maxRequestedCommandId = maxRequestedCommandId;
			} catch (e) {
				this.log(`Could not send CMD_REQUEST: ${e}.`);
			}
		}
	}

	private static isINIT_OK(message: any): message is INIT_OKConfig & { tscompilerbugworkaround: number } {
		return message._type === 'INIT_OK';
	}

	private static isINIT_NOK(message: any): message is INIT_NOKConfig {
		return message._type === 'INIT_NOK';
	}

	private static isREINIT_OK(message: any): message is REINIT_OKConfig {
		return message._type === 'REINIT_OK';
	}

	private static isREINIT_NOK(message: any): message is REINIT_NOKConfig {
		return message._type === 'REINIT_NOK';
	}

	private static isPING(message: any): message is REINIT_NOKConfig {
		return message._type === 'PING';
	}

	private static isMULTI_CMD(message: any): message is MULTI_CMDConfig {
		return message._type === 'MULTI_CMD';
	}

	private static isQUERY_RESULT(message: any): message is QUERY_RESULTConfig {
		return message._type === 'QUERY_RESULT';
	}

	private static isSESSION_CLOSED(message: any): message is SESSION_CLOSEDConfig {
		return message._type === 'SESSION_CLOSED';
	}

	private isConnected() {
		return this.connection.isConnected() && this.protocolStatus === TeamAppsProtocolStatus.ESTABLISHED;
	}

	public sendEvent(event: UiEvent) {
		let protocolEvent: EVENTConfig = {
			_type: "EVENT",
			sessionId: this.sessionId,
			id: this.clientMessageIdCounter++,
			uiEvent: event
		};
		this.sendClientPayloadMessage(protocolEvent);
	}

	sendQuery(query: UiQuery): Promise<any> {
		let clientMessageId = this.clientMessageIdCounter++;
		let protocolQuery: QUERYConfig = {
			_type: "QUERY",
			sessionId: this.sessionId,
			id: clientMessageId,
			uiQuery: query
		};
		this.sendClientPayloadMessage(protocolQuery);
		return new Promise<any>(resolve => this.queryResultHandlerByMessageId.set(clientMessageId, resolve))
	}

	private sendResult(cmdId: number, result: any) {
		let cmdResult: CMD_RESULTConfig = {
			_type: "CMD_RESULT",
			sessionId: this.sessionId,
			id: this.clientMessageIdCounter++,
			cmdId: cmdId,
			result: result
		};
		this.sendClientPayloadMessage(cmdResult);
	}

	private sendClientPayloadMessage(cmdResult: AbstractClientPayloadMessageConfig) {
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

	private sendPayloadMessage(payloadMessage: AbstractClientPayloadMessageConfig) {
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
