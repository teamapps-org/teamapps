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
import {UiInfiniteItemViewDataRequestConfig} from "./UiInfiniteItemViewDataRequestConfig";
import {UiItemJustification} from "./UiItemJustification";
import {UiVerticalItemAlignment} from "./UiVerticalItemAlignment";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiInfiniteItemViewConfig extends UiComponentConfig {
	_type?: string;
	itemTemplate: UiTemplateConfig;
	itemWidth?: number;
	rowHeight: number;
	data?: UiIdentifiableClientRecordConfig[];
	totalNumberOfRecords?: number;
	horizontalItemMargin?: number;
	autoHeight?: boolean;
	itemJustification?: UiItemJustification;
	verticalItemAlignment?: UiVerticalItemAlignment;
	contextMenuEnabled?: boolean
}

export interface UiInfiniteItemViewCommandHandler extends UiComponentCommandHandler {
	addData(startIndex: number, data: UiIdentifiableClientRecordConfig[], totalNumberOfRecords: number, clear: boolean): any;
	removeData(ids: number[]): any;
	setItemTemplate(itemTemplate: UiTemplateConfig): any;
	setItemWidth(itemWidth: number): any;
	setHorizontalItemMargin(horizontalItemMargin: number): any;
	setItemJustification(itemJustification: UiItemJustification): any;
	setVerticalItemAlignment(verticalItemAlignment: UiVerticalItemAlignment): any;
	setContextMenuContent(requestId: number, component: unknown): any;
	closeContextMenu(requestId: number): any;
}

export interface UiInfiniteItemViewEventSource {
	onDisplayedRangeChanged: TeamAppsEvent<UiInfiniteItemView_DisplayedRangeChangedEvent>;
	onItemClicked: TeamAppsEvent<UiInfiniteItemView_ItemClickedEvent>;
	onContextMenuRequested: TeamAppsEvent<UiInfiniteItemView_ContextMenuRequestedEvent>;
}

export interface UiInfiniteItemView_DisplayedRangeChangedEvent extends UiEvent {
	startIndex: number;
	length: number;
	displayedRecordIds: number[];
	dataRequest: UiInfiniteItemViewDataRequestConfig
}

export interface UiInfiniteItemView_ItemClickedEvent extends UiEvent {
	recordId: number;
	isRightMouseButton: boolean;
	isDoubleClick: boolean
}

export interface UiInfiniteItemView_ContextMenuRequestedEvent extends UiEvent {
	requestId: number;
	recordId: number
}

