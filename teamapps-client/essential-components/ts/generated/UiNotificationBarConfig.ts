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
import {UiNotificationBarItemConfig} from "./UiNotificationBarItemConfig";
import {UiExitAnimation} from "./UiExitAnimation";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiNotificationBarConfig extends UiComponentConfig {
	_type?: string;
	initialItems?: UiNotificationBarItemConfig[]
}

export interface UiNotificationBarCommandHandler extends UiComponentCommandHandler {
	addItem(item: UiNotificationBarItemConfig): any;
	removeItem(id: string, exitAnimation: UiExitAnimation): any;
}

export interface UiNotificationBarEventSource {
	onItemClicked: TeamAppsEvent<UiNotificationBar_ItemClickedEvent>;
	onItemClosed: TeamAppsEvent<UiNotificationBar_ItemClosedEvent>;
}

export interface UiNotificationBar_ItemClickedEvent extends UiEvent {
	id: string
}

export interface UiNotificationBar_ItemClosedEvent extends UiEvent {
	id: string;
	wasTimeout: boolean
}

