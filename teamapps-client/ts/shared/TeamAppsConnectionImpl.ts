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
import {TeamAppsConnection, TeamAppsConnectionListener} from "../shared/TeamAppsConnection";
import {ReconnectingCompressingWebSocketConnection} from "./ReconnectingWebSocketConnection";
import {UiClientInfoConfig} from "../generated/UiClientInfoConfig";
import {INITConfig} from "../generated/INITConfig";
import {INIT_NOK_Reason, INIT_NOKConfig} from "../generated/INIT_NOKConfig";
import {REINIT_NOK_Reason, REINIT_NOKConfig} from "../generated/REINIT_NOKConfig";
import {REINITConfig} from "../generated/REINITConfig";
import {CMD_REQUESTConfig} from "../generated/CMD_REQUESTConfig";
import {INIT_OKConfig} from "../generated/INIT_OKConfig";
import {REINIT_OKConfig} from "../generated/REINIT_OKConfig";
import {MULTI_CMDConfig} from "../generated/MULTI_CMDConfig";
import {EVENTConfig} from "../generated/EVENTConfig";
import {SERVER_ERROR_Reason, SERVER_ERRORConfig} from "../generated/SERVER_ERRORConfig";
import {CMD} from "./CMD";
import {logException} from "../modules/Common";
import {AbstractClientPayloadMessageConfig} from "../generated/AbstractClientPayloadMessageConfig";
import {UiEvent} from "../generated/UiEvent";
import {CMD_RESULTConfig} from "../generated/CMD_RESULTConfig";


enum TeamAppsProtocolStatus {
	INITIALIZING,
	REINITIALIZING,
	ERROR,
	ESTABLISHED
}

export class TeamAppsConnectionImpl implements TeamAppsConnection {

	private static readonly SENT_EVENTS_MIN_BUFFER_SIZE = 500;
	private static readonly SENT_EVENTS_MAX_BUFFER_SIZE = 1000;
	private static readonly KEEPALIVE_INTERVAL = 25000;
	private static readonly MIN_REQUESTED_COMMANDS = 5;
	private static readonly MAX_REQUESTED_COMMANDS = 20;

	private protocolStatus: TeamAppsProtocolStatus;

	private connection: ReconnectingCompressingWebSocketConnection;

	private payloadMessagesQueue: AbstractClientPayloadMessageConfig[] = [];
	private sentEventsBuffer: AbstractClientPayloadMessageConfig[] = [];

	private eventIdCounter = 1;

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
					maxRequestedCommandId: TeamAppsConnectionImpl.MAX_REQUESTED_COMMANDS
				} as INITConfig)
			},
			onMessage: (message) => {
				if (TeamAppsConnectionImpl.isMULTI_CMD(message)) {
					let cmds = message.cmds as CMD[];
					const resultPromises = commandHandler.executeCommands(cmds.map(cmd => cmd.c));
					resultPromises.forEach((promise, i) => {
						promise.then(result => {
							if (cmds[i].r) {
								this.sendResult(cmds[i].id, result);
							}
						}).catch(reason => logException(reason))
					});
					this.lastReceivedCommandId = cmds[cmds.length - 1].id;
					this.ensureEnoughCommandsRequested();
				} else if (TeamAppsConnectionImpl.isINIT_OK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ESTABLISHED;
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
					this.log("Connection refused. Reason: " + INIT_NOK_Reason[message.reason]);
					commandHandler.onConnectionErrorOrBroken(message.reason);
					this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
				} else if (TeamAppsConnectionImpl.isREINIT_NOK(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Reconnect refused. Reason: " + REINIT_NOK_Reason[message.reason]);
					commandHandler.onConnectionErrorOrBroken(message.reason);
					this.connection.stopReconnecting(); // give the server the chance to send more commands, but if it disconnected, do not attempt to reconnect.
				} else if (TeamAppsConnectionImpl.isSERVER_ERROR(message)) {
					this.protocolStatus = TeamAppsProtocolStatus.ERROR;
					this.log("Error reported by server: " + SERVER_ERROR_Reason[message.reason] + ": " + message.message);
					commandHandler.onConnectionErrorOrBroken(message.reason, message.message);
					// do NOT close the connection here. The server might handle this gracefully.
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
					maxRequestedCommandId: this.lastReceivedCommandId + TeamAppsConnectionImpl.MAX_REQUESTED_COMMANDS
				} as REINITConfig)
			}
		});
		self.setInterval(() => {
			if (this.isConnected()) {
				this.connection.send({
					_type: "KEEPALIVE",
					sessionId: sessionId
				})
			}
		}, TeamAppsConnectionImpl.KEEPALIVE_INTERVAL)
	}

	private ensureEnoughCommandsRequested() {
		if (this.maxRequestedCommandId - this.lastReceivedCommandId <= TeamAppsConnectionImpl.MIN_REQUESTED_COMMANDS) {
			try {
				let maxRequestedCommandId = this.lastReceivedCommandId + TeamAppsConnectionImpl.MAX_REQUESTED_COMMANDS;
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

	private static isMULTI_CMD(message: any): message is MULTI_CMDConfig {
		return message._type === 'MULTI_CMD';
	}

	private static isSERVER_ERROR(message: any): message is SERVER_ERRORConfig {
		return message._type === 'SERVER_ERROR';
	}

	private isConnected() {
		return this.connection.isConnected() && this.protocolStatus === TeamAppsProtocolStatus.ESTABLISHED;
	}

	public sendEvent(event: UiEvent) {
		let protocolEvent: EVENTConfig = {
			_type: "EVENT",
			sessionId: this.sessionId,
			id: this.eventIdCounter++,
			uiEvent: event
		};
		this.sendClientPayloadMessage(protocolEvent);
	}

	private sendResult(cmdId: number, result: any) {
		let cmdResult: CMD_RESULTConfig = {
			_type: "CMD_RESULT",
			sessionId: this.sessionId,
			id: this.eventIdCounter++,
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
		if (this.sentEventsBuffer.length > TeamAppsConnectionImpl.SENT_EVENTS_MAX_BUFFER_SIZE) {
			this.sentEventsBuffer.splice(0, TeamAppsConnectionImpl.SENT_EVENTS_MAX_BUFFER_SIZE - TeamAppsConnectionImpl.SENT_EVENTS_MIN_BUFFER_SIZE);
		}
		this.connection.send(payloadMessage);
	}

	private log(...message: string[]) {
		console.log("TeamAppsConnection: " + message);
	}
}
