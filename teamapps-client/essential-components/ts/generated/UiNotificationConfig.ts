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
import {UiSpacingConfig} from "./UiSpacingConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiNotificationConfig extends UiComponentConfig {
	_type?: string;
	backgroundColor?: string;
	padding?: UiSpacingConfig;
	dismissible?: boolean;
	displayTimeInMillis?: number;
	progressBarVisible?: boolean;
	content?: unknown;
	contentHeight?: number
}

export interface UiNotificationCommandHandler extends UiComponentCommandHandler {
	close(): any;
	update(config: UiNotificationConfig): any;
}

export interface UiNotificationEventSource {
	onOpened: TeamAppsEvent<UiNotification_OpenedEvent>;
	onClosed: TeamAppsEvent<UiNotification_ClosedEvent>;
}

export interface UiNotification_OpenedEvent extends UiEvent {
}

export interface UiNotification_ClosedEvent extends UiEvent {
	byUser: boolean
}

