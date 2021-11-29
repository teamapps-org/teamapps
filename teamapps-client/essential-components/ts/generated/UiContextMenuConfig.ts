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
import {UiContextMenuEntryConfig} from "./UiContextMenuEntryConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiContextMenuConfig extends UiComponentConfig {
	_type?: string;
	entries: UiContextMenuEntryConfig[]
}

export interface UiContextMenuCommandHandler extends UiComponentCommandHandler {
	hide(): any;
}

export interface UiContextMenuEventSource {
	onContextMenuSelection: TeamAppsEvent<UiContextMenu_ContextMenuSelectionEvent>;
}

export interface UiContextMenu_ContextMenuSelectionEvent extends UiEvent {
	entryId: string
}

