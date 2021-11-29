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
import {UiIdentifiableClientRecordConfig} from "./UiIdentifiableClientRecordConfig";
import {UiHorizontalElementAlignment} from "./UiHorizontalElementAlignment";
import {UiVerticalElementAlignment} from "./UiVerticalElementAlignment";
import {UiItemJustification} from "./UiItemJustification";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiInfiniteItemView2Config extends UiComponentConfig {
	_type?: string;
	itemTemplate: UiTemplateConfig;
	itemWidth?: number;
	itemHeight?: number;
	horizontalSpacing?: number;
	verticalSpacing?: number;
	itemPositionAnimationTime?: number;
	itemContentHorizontalAlignment?: UiHorizontalElementAlignment;
	itemContentVerticalAlignment?: UiVerticalElementAlignment;
	rowHorizontalAlignment?: UiItemJustification;
	contextMenuEnabled?: boolean
}

export interface UiInfiniteItemView2CommandHandler extends UiComponentCommandHandler {
	setData(startIndex: number, recordIds: number[], newRecords: UiIdentifiableClientRecordConfig[], totalNumberOfRecords: number): any;
	setItemTemplate(itemTemplate: UiTemplateConfig): any;
	setItemWidth(itemWidth: number): any;
	setItemHeight(itemHeight: number): any;
	setHorizontalSpacing(horizontalSpacing: number): any;
	setVerticalSpacing(verticalSpacing: number): any;
	setItemContentHorizontalAlignment(itemContentHorizontalAlignment: UiHorizontalElementAlignment): any;
	setItemContentVerticalAlignment(itemContentVerticalAlignment: UiVerticalElementAlignment): any;
	setRowHorizontalAlignment(rowHorizontalAlignment: UiItemJustification): any;
	setItemPositionAnimationTime(animationMillis: number): any;
	setContextMenuContent(requestId: number, component: unknown): any;
	closeContextMenu(requestId: number): any;
}

export interface UiInfiniteItemView2EventSource {
	onRenderedItemRangeChanged: TeamAppsEvent<UiInfiniteItemView2_RenderedItemRangeChangedEvent>;
	onItemClicked: TeamAppsEvent<UiInfiniteItemView2_ItemClickedEvent>;
	onContextMenuRequested: TeamAppsEvent<UiInfiniteItemView2_ContextMenuRequestedEvent>;
}

export interface UiInfiniteItemView2_RenderedItemRangeChangedEvent extends UiEvent {
	startIndex: number;
	endIndex: number
}

export interface UiInfiniteItemView2_ItemClickedEvent extends UiEvent {
	recordId: number;
	isRightMouseButton: boolean;
	isDoubleClick: boolean
}

export interface UiInfiniteItemView2_ContextMenuRequestedEvent extends UiEvent {
	requestId: number;
	recordId: number
}

