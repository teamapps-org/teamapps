/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiChatMessageConfig} from "./UiChatMessageConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiChatDisplayConfig extends UiComponentConfig {
	_type?: string;
	messages?: UiChatMessageConfig[];
	includesFirstMessage?: boolean
}

export interface UiChatDisplayCommandHandler extends UiComponentCommandHandler {
	addChatMessages(chatMessages: UiChatMessageConfig[], prepend: boolean, includesFirstMessage: boolean): any;
	replaceChatMessages(chatMessages: UiChatMessageConfig[], includesFirstMessage: boolean): any;
}

export interface UiChatDisplayEventSource {
	onPreviousMessagesRequested: TeamAppsEvent<UiChatDisplay_PreviousMessagesRequestedEvent>;
}

export interface UiChatDisplay_PreviousMessagesRequestedEvent extends UiEvent {
	earliestKnownMessageId: string
}

