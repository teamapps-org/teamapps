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
import {UiTableColumnConfig} from "./UiTableColumnConfig";
import {UiTableClientRecordConfig} from "./UiTableClientRecordConfig";
import {UiSelectionFrameConfig} from "./UiSelectionFrameConfig";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiTableDataRequestConfig} from "./UiTableDataRequestConfig";
import {UiSortDirection} from "./UiSortDirection";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiTableConfig extends UiComponentConfig {
	_type?: string;
	columns: UiTableColumnConfig[];
	displayAsList?: boolean;
	forceFitWidth?: boolean;
	rowHeight?: number;
	stripedRows?: boolean;
	stripedRowColorEven?: string;
	stripedRowColorOdd?: string;
	hideHeaders?: boolean;
	allowMultiRowSelection?: boolean;
	selectionColor?: string;
	rowBorderWidth?: number;
	rowBorderColor?: string;
	showRowCheckBoxes?: boolean;
	showNumbering?: boolean;
	textSelectionEnabled?: boolean;
	tableData?: UiTableClientRecordConfig[];
	totalNumberOfRecords?: number;
	sortField?: string;
	sortDirection?: UiSortDirection;
	editable?: boolean;
	treeMode?: boolean;
	indentedColumnName?: string;
	indentation?: number;
	selectionFrame?: UiSelectionFrameConfig;
	contextMenuEnabled?: boolean;
	showHeaderRow?: boolean;
	headerRowBorderWidth?: number;
	headerRowBorderColor?: string;
	headerRowHeight?: number;
	headerRowBackgroundColor?: string;
	headerRowFields?: {[name: string]: unknown};
	showFooterRow?: boolean;
	footerRowBorderWidth?: number;
	footerRowBorderColor?: string;
	footerRowHeight?: number;
	footerRowBackgroundColor?: string;
	footerRowFields?: {[name: string]: unknown}
}

export interface UiTableCommandHandler extends UiComponentCommandHandler {
	clearTable(): any;
	addData(startRowIndex: number, data: UiTableClientRecordConfig[], totalNumberOfRecords: number, sortField: string, sortDirection: UiSortDirection, clearTableCache: boolean): any;
	removeData(ids: number[]): any;
	insertRows(index: number, data: UiTableClientRecordConfig[]): any;
	deleteRows(ids: number[]): any;
	updateRecord(record: UiTableClientRecordConfig): any;
	setCellValue(recordId: number, columnPropertyName: string, value: any): any;
	setChildrenData(parentRecordId: number, data: UiTableClientRecordConfig[]): any;
	markTableField(recordId: number, columnPropertyName: string, mark: boolean): any;
	clearAllFieldMarkings(): any;
	setRecordBold(recordId: number, bold: boolean): any;
	selectRows(recordIds: number[], scrollToFirstRecord: boolean): any;
	editCellIfAvailable(recordId: number, propertyName: string): any;
	cancelEditingCell(recordId: number, propertyName: string): any;
	focusCell(recordId: number, columnPropertyName: string): any;
	setSingleCellMessages(recordId: number, columnPropertyName: string, messages: UiFieldMessageConfig[]): any;
	clearAllCellMessages(): any;
	setColumnMessages(columnPropertyName: string, messages: UiFieldMessageConfig[]): any;
	addColumns(column: UiTableColumnConfig[], index: number): any;
	removeColumns(columnName: string[]): any;
	setColumnVisibility(columnPropertyName: string, visible: boolean): any;
	setContextMenuContent(requestId: number, component: unknown): any;
	closeContextMenu(requestId: number): any;
}

export interface UiTableEventSource {
	onCellClicked: TeamAppsEvent<UiTable_CellClickedEvent>;
	onCellEditingStarted: TeamAppsEvent<UiTable_CellEditingStartedEvent>;
	onCellEditingStopped: TeamAppsEvent<UiTable_CellEditingStoppedEvent>;
	onCellValueChanged: TeamAppsEvent<UiTable_CellValueChangedEvent>;
	onRowSelected: TeamAppsEvent<UiTable_RowSelectedEvent>;
	onMultipleRowsSelected: TeamAppsEvent<UiTable_MultipleRowsSelectedEvent>;
	onSortingChanged: TeamAppsEvent<UiTable_SortingChangedEvent>;
	onDisplayedRangeChanged: TeamAppsEvent<UiTable_DisplayedRangeChangedEvent>;
	onRequestNestedData: TeamAppsEvent<UiTable_RequestNestedDataEvent>;
	onContextMenuRequested: TeamAppsEvent<UiTable_ContextMenuRequestedEvent>;
	onFieldOrderChange: TeamAppsEvent<UiTable_FieldOrderChangeEvent>;
	onColumnSizeChange: TeamAppsEvent<UiTable_ColumnSizeChangeEvent>;
}

export interface UiTable_CellClickedEvent extends UiEvent {
	recordId: number;
	columnPropertyName: string
}

export interface UiTable_CellEditingStartedEvent extends UiEvent {
	recordId: number;
	columnPropertyName: string;
	currentValue: any
}

export interface UiTable_CellEditingStoppedEvent extends UiEvent {
	recordId: number;
	columnPropertyName: string
}

export interface UiTable_CellValueChangedEvent extends UiEvent {
	recordId: number;
	columnPropertyName: string;
	value: any
}

export interface UiTable_RowSelectedEvent extends UiEvent {
	recordId: number;
	isRightMouseButton: boolean;
	isDoubleClick: boolean
}

export interface UiTable_MultipleRowsSelectedEvent extends UiEvent {
	recordIds: number[]
}

export interface UiTable_SortingChangedEvent extends UiEvent {
	sortField: string;
	sortDirection: UiSortDirection
}

export interface UiTable_DisplayedRangeChangedEvent extends UiEvent {
	startIndex: number;
	length: number;
	displayedRecordIds: number[];
	dataRequest: UiTableDataRequestConfig
}

export interface UiTable_RequestNestedDataEvent extends UiEvent {
	recordId: number
}

export interface UiTable_ContextMenuRequestedEvent extends UiEvent {
	requestId: number;
	recordId: number
}

export interface UiTable_FieldOrderChangeEvent extends UiEvent {
	columnPropertyName: string;
	position: number
}

export interface UiTable_ColumnSizeChangeEvent extends UiEvent {
	columnPropertyName: string;
	size: number
}

