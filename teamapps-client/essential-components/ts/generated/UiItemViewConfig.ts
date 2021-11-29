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
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiItemViewItemGroupConfig} from "./UiItemViewItemGroupConfig";
import {UiIdentifiableClientRecordConfig} from "./UiIdentifiableClientRecordConfig";
import {UiItemViewItemBackgroundMode} from "./UiItemViewItemBackgroundMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiItemViewConfig extends UiComponentConfig {
	_type?: string;
	groupHeaderTemplate?: UiTemplateConfig;
	itemGroups?: UiItemViewItemGroupConfig[];
	horizontalPadding?: number;
	verticalPadding?: number;
	groupSpacing?: number;
	itemBackgroundMode?: UiItemViewItemBackgroundMode;
	filter?: string
}

export interface UiItemViewCommandHandler extends UiComponentCommandHandler {
	setFilter(filter: string): any;
	addItemGroup(itemGroup: UiItemViewItemGroupConfig): any;
	refreshItemGroup(itemGroup: UiItemViewItemGroupConfig): any;
	removeItemGroup(groupId: string): any;
	addItem(groupId: string, item: UiIdentifiableClientRecordConfig): any;
	removeItem(groupId: string, itemId: number): any;
}

export interface UiItemViewEventSource {
	onItemClicked: TeamAppsEvent<UiItemView_ItemClickedEvent>;
}

export interface UiItemView_ItemClickedEvent extends UiEvent {
	groupId: string;
	itemId: number
}

