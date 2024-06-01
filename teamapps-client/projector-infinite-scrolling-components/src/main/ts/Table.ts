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
///<reference types="slickgrid"/>
///<reference types="slickgrid/slick.checkboxselectcolumn"/>
///<reference types="slickgrid/slick.rowselectionmodel"/>


import {
	AbstractLegacyComponent,
	Component,
	debouncedMethod,
	DebounceMode,
	executeWhenFirstDisplayed,
	nonRecursive,
	parseHtml,
	TeamAppsEvent
} from "teamapps-client-core";

import {GenericTableCellEditor} from "./GenericTableCellEditor";
import {TableDataProvider} from "./TableDataProvider";
import {
	AbstractField,
	applyCss,
	arraysEqual,
	ContextMenu,
	DropDown,
	DtoFieldMessage,
	DtoFieldMessageSeverity,
	TextAlignment,
	fadeIn,
	fadeOut,
	getHighestSeverity,
	manipulateWithoutTransitions
} from "teamapps-client-core-components";
import {
	DtoAbstractInfiniteListComponent_DisplayedRangeChangedEvent,
	DtoSortDirection,
	DtoTable,
	DtoTable_CellClickedEvent,
	DtoTable_CellEditingStartedEvent,
	DtoTable_CellEditingStoppedEvent,
	DtoTable_CellValueChangedEvent,
	DtoTable_ColumnSizeChangeEvent,
	DtoTable_ContextMenuRequestedEvent,
	DtoTable_FieldOrderChangeEvent,
	DtoTable_RowsSelectedEvent,
	DtoTable_SortingChangedEvent,
	DtoTableClientRecord,
	DtoTableColumn,
	DtoTableCommandHandler,
	DtoTableDisplayStyle,
	DtoTableEventSource
} from "./generated";
import {TableRowSelectionModel} from "./TableRowSelectionModel";
import {FieldMessagesPopper} from "./FieldMessagesPopper";
import EventData = Slick.EventData;

interface Column extends Slick.Column<any> {
	id: string;
	field: string;
	uiField?: AbstractField;
	name: string;
	width: number;
	minWidth?: number;
	maxWidth?: number;
	formatter: (row: number, cell: number, value: any, columnDef: Slick.Column<DtoTableClientRecord>, dataContext: DtoTableClientRecord) => string;
	asyncEditorLoading?: boolean;
	autoEdit?: boolean;
	focusable: boolean;
	sortable: boolean;
	resizable: boolean;
	headerCssClass?: string;
	cssClass?: string,
	cannotTriggerInsert?: boolean,
	unselectable?: boolean,
	hiddenIfOnlyEmptyCellsVisible: boolean,
	messages?: DtoFieldMessage[],
	uiConfig?: DtoTableColumn,
	visible: boolean
}

const backgroundColorCssClassesByMessageSeverity = {
	[DtoFieldMessageSeverity.INFO]: "bg-info",
	[DtoFieldMessageSeverity.SUCCESS]: "bg-success",
	[DtoFieldMessageSeverity.WARNING]: "bg-warning",
	[DtoFieldMessageSeverity.ERROR]: "bg-danger",
};

type ElementsByName = { [fieldName: string]: HTMLElement };
type FieldsByName = { [fieldName: string]: AbstractField };

export class Table extends AbstractLegacyComponent<DtoTable> implements DtoTableCommandHandler, DtoTableEventSource {

	public readonly onCellEditingStarted: TeamAppsEvent<DtoTable_CellEditingStartedEvent> = new TeamAppsEvent();
	public readonly onCellEditingStopped: TeamAppsEvent<DtoTable_CellEditingStoppedEvent> = new TeamAppsEvent();
	public readonly onCellValueChanged: TeamAppsEvent<DtoTable_CellValueChangedEvent> = new TeamAppsEvent();
	public readonly onCellClicked: TeamAppsEvent<DtoTable_CellClickedEvent> = new TeamAppsEvent();
	public readonly onSortingChanged: TeamAppsEvent<DtoTable_SortingChangedEvent> = new TeamAppsEvent();
	public readonly onRowsSelected: TeamAppsEvent<DtoTable_RowsSelectedEvent> = new TeamAppsEvent();
	public readonly onFieldOrderChange: TeamAppsEvent<DtoTable_FieldOrderChangeEvent> = new TeamAppsEvent();
	public readonly onColumnSizeChange: TeamAppsEvent<DtoTable_ColumnSizeChangeEvent> = new TeamAppsEvent();
	public readonly onDisplayedRangeChanged: TeamAppsEvent<DtoAbstractInfiniteListComponent_DisplayedRangeChangedEvent> = new TeamAppsEvent();
	public readonly onContextMenuRequested: TeamAppsEvent<DtoTable_ContextMenuRequestedEvent> = new TeamAppsEvent();

	private $component: HTMLElement;
	private _grid: Slick.Grid<any>;
	private allColumns: Column[];
	private dataProvider: TableDataProvider;
	private _$loadingIndicator: HTMLElement;
	private loadingIndicatorFadeInTimer: number;

	private _sortField: string;
	private _sortDirection: DtoSortDirection;

	private doNotFireEventBecauseSelectionIsCausedByApiCall: boolean = false; // slickgrid fires events even if we set the selection via api...

	private dropDown: DropDown;
	private $selectionFrame: HTMLElement;

	private headerRowFieldWrappers: ElementsByName = {};
	private headerFields: FieldsByName = {};
	private footerRowFieldWrappers: ElementsByName = {};
	private footerFields: FieldsByName = {};

	private $editorFieldTempContainer: HTMLElement;

	private contextMenu: ContextMenu;

	constructor(config: DtoTable, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		console.log("new Table");
		this.$component = parseHtml(`<div class="Table"">
    <div class="slick-table"></div>
    <div class="editor-field-temp-container hidden"></div>
</div>`);
		const $table = this.$component.querySelector<HTMLElement>(":scope .slick-table");
		if (config.stripedRowsEnabled) {
			$table.classList.add("striped-rows");
		}
		this.$editorFieldTempContainer = this.$component.querySelector<HTMLElement>(":scope .editor-field-temp-container");

		this.dataProvider = new TableDataProvider();

		this.createSlickGrid(config, $table);

		this.contextMenu = new ContextMenu();

		this.dropDown = new DropDown();

		this.displayedDeferredExecutor.invokeWhenReady(() => {
			if (this._grid != null) {
				this._grid.scrollRowToTop(0); // the scroll position gets lost when the table gets detached, so it is necessary to inform it that it should display the top of the table
			}
		});
	}

	@executeWhenFirstDisplayed()
	private createSlickGrid(config: DtoTable, $table: HTMLElement) {
		console.log("createGrid");
		this.allColumns = this._createColumns();

		if (config.rowCheckBoxesEnabled) {
			var checkboxSelector = new Slick.CheckboxSelectColumn({});
			this.allColumns.unshift(checkboxSelector.getColumnDefinition() as Column);
		}
		console.log("createGrid 2");
		if (config.numberingColumnEnabled) {
			const RowNumberFormatter: Slick.Formatter<DtoTableClientRecord> = (row: number, cell: number, value: any, columnDef: Slick.Column<DtoTableClientRecord>, dataContext: DtoTableClientRecord) => {
				return "" + (row + 1);
			};
			this.allColumns.unshift({
				id: "__rowNumber",
				name: "#",
				field: "",
				formatter: RowNumberFormatter,
				headerCssClass: "text-right",
				width: 35,
				maxWidth: 75,
				cannotTriggerInsert: true,
				resizable: true,
				unselectable: true,
				sortable: false,
				focusable: false,
				hiddenIfOnlyEmptyCellsVisible: false,
				visible: true
			});
		}
		console.log("createGrid 3");
		const options: Slick.GridOptions<any> & { createFooterRow: boolean, showFooterRow: boolean, footerRowHeight: number } = {
			explicitInitialization: true,
			enableCellNavigation: true,
			enableColumnReorder: false,
			forceFitColumns: config.forceFitWidth,
			fullWidthRows: true,
			rowHeight: config.rowHeight + config.rowBorderWidth,
			multiColumnSort: false,
			multiSelect: config.multiRowSelectionEnabled,
			enableTextSelectionOnCells: config.textSelectionEnabled, // see also CSS style user-select: none
			editable: config.editable,
			// editCommandHandler: (item: TableDataProviderItem, column: Column, editCommand: any) => {
			// 	column.uiField.commit();
			// 	editCommand.execute();
			// },
			showHeaderRow: config.headerFieldsRowEnabled,
			headerRowHeight: config.headerFieldsRowHeight,
			createFooterRow: true,
			showFooterRow: config.footerFieldsRowEnabled,
			footerRowHeight: config.footerFieldsRowHeight,
		};
		console.log("createGrid 4");
		this._grid = new Slick.Grid($table, this.dataProvider, this.getVisibleColumns(), options);
		console.log("createGrid 5");

		if (config.headerFields) {
			this.initializeOuterFieldWrappers(true);
			this.headerFields = config.headerFields as FieldsByName;
		}
		if (config.footerFields) {
			this.initializeOuterFieldWrappers(false);
			this.footerFields = config.footerFields as FieldsByName;
		}

		if (config.columnHeadersVisible) {
			applyCss($table.querySelector<HTMLElement>(":scope .slick-header-columns"), {
				height: 0,
				border: "none"
			});
			//this._grid.resizeCanvas();
		}

		$table.classList.add(config.displayStyle == DtoTableDisplayStyle.LIST ? 'list-mode' : 'table-mode');

		this._grid.setSelectionModel(new TableRowSelectionModel());

		if (config.rowCheckBoxesEnabled) {
			this._grid.registerPlugin(checkboxSelector);
		}

		this._$loadingIndicator = parseHtml("<span>Loading...</span>");
		this._$loadingIndicator.classList.add("hidden");
		$table.appendChild(this._$loadingIndicator);
		this.dataProvider.onDataLoading.subscribe(() => {
			clearTimeout(this.loadingIndicatorFadeInTimer);
			this.loadingIndicatorFadeInTimer = window.setTimeout(() => {
				fadeIn(this._$loadingIndicator);
			}, 2000);
		});
		if (config.sortField) {
			this._sortField = config.sortField;
			this._sortDirection = config.sortDirection;
			this._grid.setSortColumn(config.sortField, config.sortDirection === DtoSortDirection.ASC);
		}
		this._grid.onSort.subscribe((e, args: Slick.OnSortEventArgs<Slick.SlickData>) => {
			this._sortField = args.sortCol.id;
			this._sortDirection = args.sortAsc ? DtoSortDirection.ASC : DtoSortDirection.DESC;
			this.onSortingChanged.fire({
				sortField: this._sortField,
				sortDirection: args.sortAsc ? DtoSortDirection.ASC : DtoSortDirection.DESC
			});
		});
		this._grid.onSelectedRowsChanged.subscribe((eventData, args) => {
			if (args.rows.some((row) => !this.dataProvider.getItem(row))) {
				return; // TODO one of the selected rows has no id. How to handle this? What if the user wants to select a huge range?
			}
			if (!this.doNotFireEventBecauseSelectionIsCausedByApiCall && !this.dataProvider.agreesWithSelectedRows(args.rows)) {
				this.onRowsSelected.fire({
					recordIds: args.rows.map((selectionIndex) => {
						return this.dataProvider.getItem(selectionIndex).id;
					})
				});
			}
			this.dataProvider.setSelectedRows(args.rows);
			this.updateSelectionFramePosition(true);
		});
		this._grid.onCellChange.subscribe((eventData: EventData, args: Slick.OnCellChangeEventArgs<DtoTableClientRecord>) => {

			// The problem with this approach is we do not get the intermediate committed change events!
			// let columnPropertyName = this.getVisibleColumns()[args.cell].id;
			// this.onCellValueChanged.fire({recordId: args.item.id, columnPropertyName: columnPropertyName, value: args.item.values[columnPropertyName]});

			this.updateSelectionFramePosition(true);
		});
		this._grid.onClick.subscribe((e: MouseEvent, args: Slick.OnClickEventArgs<DtoTableClientRecord>) => {
			setTimeout(/* make sure the table updated its activeCell property! */ () => {
				const column = this._grid.getColumns()[args.cell];
				let fieldName = column.id;
				let uiField: AbstractField = (column as Column).uiField;
				let item = this.dataProvider.getItem(args.row);
				if (item) { // may be undefined if this is a new row!
					if (uiField != null) {
						this.onCellClicked.fire({columnPropertyName: fieldName, recordId: item.id})
					}
				}
			});
		});
		this._grid.onBeforeEditCell.subscribe((e, data: Slick.OnBeforeEditCellEventArgs<any>) => {
			let id = data.item && data.item.id;
			let fieldName = this._grid.getColumns()[data.cell].field;
			if (id != null && fieldName != null) {
				this.onCellEditingStarted.fire({
					recordId: id,
					columnPropertyName: fieldName,
					currentValue: data.item.values[fieldName]
				});
			}
		});
		this._grid.onBeforeCellEditorDestroy.subscribe((e, data: Slick.OnBeforeCellEditorDestroyEventArgs<any>) => {
			const dataItem = this._grid.getDataItem(this._grid.getActiveCell().row);
			if (dataItem != null) { // might be null if table data was replaced during editing...
				let id = dataItem.id;
				let fieldName = this._grid.getColumns()[this._grid.getActiveCell().cell].field;
				if (id != null && fieldName != null) {
					this.onCellEditingStopped.fire({
						recordId: id,
						columnPropertyName: fieldName
					});
				}
			}
		});
		this._grid.getCanvasNode().addEventListener("dblclick", (e) => {
			let cell = this._grid.getCellFromEvent(<any>e);
			if (cell != null) {
				// TODO double-click event?
			}
		});

		this._grid.getCanvasNode().addEventListener("contextmenu", (e) => {
			let cell = this._grid.getCellFromEvent(<any>e);
			if (cell != null && this.dataProvider.getItem(cell.row) != null) {
				let recordId = this.dataProvider.getItem(cell.row).id;
				this._grid.setSelectedRows([cell.row]);
				this.onRowsSelected.fire({
					recordIds: [recordId]
				});
				if (!isNaN(recordId) && this.config.contextMenuEnabled) {
					this.contextMenu.open(e as unknown as MouseEvent, requestId => this.onContextMenuRequested.fire({
						recordId: recordId,
						requestId
					}));
				}
			}
		});

		if (config.selectionFrame) {
			this.$selectionFrame = parseHtml(`<div class="UiTable-selection-frame">`);
			applyCss(this.$selectionFrame, {
				border: `${this.config.selectionFrame.borderWidth}px solid ${(this.config.selectionFrame.color)}`,
				boxShadow: `0 0 ${this.config.selectionFrame.shadowWidth}px 0 rgba(0, 0, 0, .5), 0 0 ${this.config.selectionFrame.glowingWidth}px 0 ${(this.config.selectionFrame.color)}`,
				transition: `top ${this.config.selectionFrame.animationDuration}ms, left ${this.config.selectionFrame.animationDuration}ms, right ${this.config.selectionFrame.animationDuration}ms, width ${this.config.selectionFrame.animationDuration}ms, height ${this.config.selectionFrame.animationDuration}ms`
			});
			this.$component.appendChild(this.$selectionFrame);
		}
		this._grid.onViewportChanged.subscribe(() => {
			//this.onDisplayedRangeChanged.fire()
		});
		this._grid.onScroll.subscribe((eventData) => {
			this.updateSelectionFramePosition();
			this.throttledFireDisplayedRangeChanged();
		});
		this._grid.onViewportChanged.subscribe((eventData) => {
			this.toggleColumnsThatAreHiddenWhenTheyContainNoVisibleNonEmptyCells();
		});
		this._grid.onColumnsResized.subscribe((eventData) => {
			this.rerenderAllRows();
			this.updateSelectionFramePosition();
		});
		this._grid.onHeaderMouseEnter.subscribe((e, args) => {
			const fieldName = args.column.id;

			const columnMessages = this.getColumnById(fieldName).messages;
			const cellHasMessages = columnMessages.length > 0;
			if (cellHasMessages) {
				this.fieldMessagePopper.setReferenceElement((e as any).currentTarget);
				this.fieldMessagePopper.setMessages([...columnMessages]);
				this.fieldMessagePopper.setVisible(true);
			} else {
				this.fieldMessagePopper.setVisible(false);
			}
		});
		this._grid.onMouseEnter.subscribe((e, args) => {
			const cell = this._grid.getCellFromEvent(e as any);
			if (cell == null) {
				return;
			}
			const item = this.dataProvider.getItem(cell.row);
			if (item == null) {
				return;
			}
			const columnId = (this._grid.getColumns()[cell.cell] as Column).id;

			if (!this.isSpecialColumn(this.getColumnById(columnId))) {
				const cellMessages = (item.messages && item.messages[columnId] || []);
				const columnMessages = this.getColumnById(columnId).messages;
				const cellHasMessages = cellMessages.length > 0 || columnMessages.length > 0;
				const isEditorCell = this._grid.getCellEditor() != null && this._grid.getActiveCell().row === cell.row && this._grid.getActiveCell().cell === cell.cell;
				if (!isEditorCell && cellHasMessages) {
					this.fieldMessagePopper.setReferenceElement(this._grid.getCellNode(cell.row, cell.cell));
					this.fieldMessagePopper.setMessages([...cellMessages, ...columnMessages]);
					this.fieldMessagePopper.setVisible(true);
				} else {
					this.fieldMessagePopper.setVisible(false);
				}
			}
		});
		this._grid.onMouseLeave.subscribe((e, args) => {
			this.fieldMessagePopper.setVisible(false);
		});
		this._grid.init();
	}

	@debouncedMethod(150, DebounceMode.BOTH)
	private throttledFireDisplayedRangeChanged() {
		this.onDisplayedRangeChanged.fireIfChanged(this.createDisplayRangeChangedEvent());
	}

	private createDisplayRangeChangedEvent(): DtoAbstractInfiniteListComponent_DisplayedRangeChangedEvent {
		const viewPort = this._grid.getViewport();
		const displayedStartIndex = viewPort.top;
		const displayedLength = viewPort.bottom - viewPort.top;

		const requestedStartIndex = Math.max(0, displayedStartIndex - displayedLength);
		const requestedEndIndex = displayedStartIndex + 2 * displayedLength;
		return {
			startIndex: requestedStartIndex,
			length: requestedEndIndex - requestedStartIndex
		};
	}

	@nonRecursive
	private toggleColumnsThatAreHiddenWhenTheyContainNoVisibleNonEmptyCells() {
		let range = this._grid.getViewport();
		let columns: Column[] = this.getVisibleColumns();
		let usedColumns: { [key: string]: true } = {};
		columns.filter(c => !c.hiddenIfOnlyEmptyCellsVisible).forEach(c => usedColumns[c.id] = true);
		const lastRowIndex = Math.min(range.bottom + 1 /*bottom seems to be inclusive*/, this.dataProvider.getLength());
		for (let i = range.top; i < lastRowIndex; i++) {
			let item = this.dataProvider.getItem(i);
			if (item == null) {
				return; // an item is not loaded yet. This makes updating the columns unnecessary (until the item is loaded)!
			}
			columns.forEach(c => {
				if (item.values[c.id] != null) {
					usedColumns[c.id] = true;
				}
			})
		}
		let columnsToDisplay = columns.filter(c => usedColumns[c.id] == true);
		if (!arraysEqual(columnsToDisplay.map(c => c.id), this._grid.getColumns().map(c => c.id))) {
			this.setSlickGridColumns(columnsToDisplay);
		}
	}

	private initializeOuterFieldWrappers(header: boolean) {
		const fieldsByColumnId: { [columnName: string]: AbstractField } = header ? this.headerFields : this.footerFields;
		const wrapperElementsMap = header ? this.headerRowFieldWrappers : this.footerRowFieldWrappers;
		const renderedEvent: Slick.Event<Slick.OnHeaderRowCellRenderedEventArgs<any>> = header ? this._grid.onHeaderRowCellRendered : (this._grid as any).onFooterRowCellRendered;
		renderedEvent.subscribe((e, args) => {
			args.node.innerHTML = '';
			const columnName = args.column.id;

			let wrapperElement = parseHtml(`<div class="outer-field-wrapper ${header ? 'header' : 'footer'}-field-wrapper"></div>`);
			wrapperElementsMap[columnName] = wrapperElement;
			args.node.appendChild(wrapperElement);

			let field = fieldsByColumnId[columnName];
			if (field) {
				this.setOuterRowField(columnName, header, field);
			}
		});
	}

	private setOuterRowField(columnName: string, header: boolean, field: AbstractField) {
		const fieldsByColumnId: { [columnName: string]: AbstractField } = header ? this.headerFields : this.footerFields;
		fieldsByColumnId[columnName] = field;
		let wrapperElementsMap = header ? this.headerRowFieldWrappers : this.footerRowFieldWrappers;
		let wrapper = wrapperElementsMap[columnName];
		if (wrapper == null) {
			console.error(`Could not set ${header ? 'header' : 'footer'} field for column ${columnName}`);
			return;
		}
		wrapper.innerHTML = '';
		wrapper.appendChild(field.getMainElement());
	}

	public setHeaderFields(headerFields: { [p: string]: unknown }): any {
		Object.entries(this.headerRowFieldWrappers).forEach(e => e[1].innerHTML = '');
		this.headerFields = {}
		Object.entries(this.headerFields).forEach(e => this.setHeaderRowField(e[0], e[1]));
	}

	public setHeaderRowField(columnName: string, field: AbstractField) {
		this.setOuterRowField(columnName, true, field);
	}

	public setFooterFields(footerFields: { [p: string]: unknown }): any {
		Object.entries(this.footerRowFieldWrappers).forEach(e => e[1].innerHTML = '');
		this.footerFields = {}
		Object.entries(this.footerFields).forEach(e => this.setFooterRowField(e[0], e[1]));
	}

	public setFooterRowField(columnName: string, field: AbstractField) {
		this.setOuterRowField(columnName, false, field);
	}

	private getCurrentlyDisplayedRecordIds() {
		const viewPort = this._grid.getViewport();
		const currentlyDisplayedRecordIds = [];
		for (let i = viewPort.top; i <= viewPort.bottom; i++) {
			const item = this.dataProvider.getItem(i);
			if (item != null) {
				currentlyDisplayedRecordIds.push(item.id);
			}
		}
		return currentlyDisplayedRecordIds;
	}

	public doGetMainElement(): HTMLElement {
		return this.$component;
	}

	private _createColumns(): Column[] {
		const columns: Column[] = [];
		for (let i = 0; i < this.config.columns.length; i++) {
			const slickColumnConfig = this.createSlickColumnConfig(this.config.columns[i]);
			columns.push(slickColumnConfig);
		}
		return columns;
	}

	private createSlickColumnConfig(columnConfig: DtoTableColumn): Column {
		const uiField = columnConfig.field as AbstractField;
		this.prepareEditorField(columnConfig.propertyName, uiField);

		let editorFactory;
		// TODO
		// if (uiField instanceof UiRichTextEditor || uiField instanceof UiMultiLineTextField) {
		// 	editorFactory = FixedSizeTableCellEditor.bind(null, uiField, () => this.$editorFieldTempContainer.appendChild(uiField.getMainElement()));
		// } else {
		editorFactory = GenericTableCellEditor.bind(null, uiField, () => this.$editorFieldTempContainer.appendChild(uiField.getMainElement()));
		// }

		const slickColumnConfig: Column = {
			id: columnConfig.propertyName,
			field: columnConfig.propertyName,
			uiField: uiField,
			name: `<div class="column-header-icon img img-16 ${columnConfig.icon == null ? "hidden" : ""}" style="background-image: url('${columnConfig.icon}')"></div>
<div class="column-header-title">${columnConfig.title}</div>`,
			width: columnConfig.defaultWidth || ((columnConfig.minWidth + columnConfig.maxWidth) / 2) || undefined,
			minWidth: columnConfig.minWidth || 30,
			maxWidth: columnConfig.maxWidth || undefined,
			formatter: this.createCellFormatter(uiField),
			editor: editorFactory,
			asyncEditorLoading: false,
			autoEdit: true,
			focusable: this.config.displayStyle == DtoTableDisplayStyle.LIST || uiField.isEditable(),
			sortable: columnConfig.sortable,
			resizable: columnConfig.resizeable,
			hiddenIfOnlyEmptyCellsVisible: columnConfig.hiddenIfOnlyEmptyCellsVisible,
			messages: columnConfig.messages,
			uiConfig: columnConfig,
			visible: columnConfig.visible
		};

		slickColumnConfig.headerCssClass = this.getColumnCssClass(slickColumnConfig);
		slickColumnConfig.cssClass = this.getColumnCssClass(slickColumnConfig);

		return slickColumnConfig;
	}


	private getColumnCssClass(column: Column) {
		let columnCssClasses: string[] = [];
		const highestSeverity = getHighestSeverity(column.messages, null);
		if (highestSeverity != null) {
			columnCssClasses.push(backgroundColorCssClassesByMessageSeverity[highestSeverity]);
		}
		columnCssClasses.push("align-" + TextAlignment[column.uiConfig.headerAlignment].toLocaleLowerCase());
		return columnCssClasses.join(" ");
	}

	private prepareEditorField(columnPropertyName: string, uiField: AbstractField) {
		this.$editorFieldTempContainer.appendChild(uiField.getMainElement());
		uiField.getMainElement().addEventListener("keydown", (e) => {
			if (e.key === "ArrowLeft"
				|| e.key === "ArrowRight"
				|| e.key === "ArrowUp"
				|| e.key === "ArrowDown") {
				e.stopPropagation();
			}
		});
		// TODO do not rely on focusableElement as its focus and blur events are probably not what one wants!
		uiField.getMainElement().addEventListener("focusin", () => {
			uiField.getMainElement().style.zIndex = "1000";
		});
		uiField.getMainElement().addEventListener("focusout", () => uiField.getMainElement().style.zIndex = "0");
		uiField.onValueChanged.addListener((eventData) => this.handleFieldValueChanged(columnPropertyName, eventData.value));
		uiField.onVisibilityChanged.addListener(visible => this.setSlickGridColumns(this.getVisibleColumns()));
	}

	private createCellFormatter(field: AbstractField) {
		const createInnerCellFormatter = () => {
			if (field.getReadOnlyHtml) {
				return (row: number, cell: number, value: any, columnDef: Slick.Column<DtoTableClientRecord>, dataContext: DtoTableClientRecord) => {
					return field.getReadOnlyHtml(dataContext.values[columnDef.id], columnDef.width);
				};
			} else {
				return null;
			}
		};

		const innerCellFormatter = createInnerCellFormatter(); // may be undefined!
		return (row: number, cell: number, value: any, columnDef: Slick.Column<DtoTableClientRecord>, dataContext: DtoTableClientRecord) => {
			const innerHtml = innerCellFormatter ? innerCellFormatter(row, cell, value, columnDef, dataContext) : "###";
			const highestMessageSeverity = getHighestSeverity(dataContext.messages && dataContext.messages[columnDef.id], null);
			const fieldCssClasses: string[] = [];
			if (dataContext.bold) {
				fieldCssClasses.push("text-bold");
			}
			if (highestMessageSeverity != null) {
				fieldCssClasses.push(this.getCellMessageCssClassName(highestMessageSeverity));
			}
			let additionalHtml = "";
			if (dataContext.markings != null && dataContext.markings.some(propertyName => propertyName === columnDef.id)) {
				additionalHtml += `<div class="cell-marker"></div>`;
			}
			const cellClass = fieldCssClasses.join(" ");
			return `<div class="validation-class-wrapper ${cellClass}">
    <div class="anti-overflow-wrapper">
    	${innerHtml}
	</div>
	${additionalHtml}
</div>`;
		};
	}

	private getCellMessageCssClassName(severity: DtoFieldMessageSeverity) {
		return "message-" + DtoFieldMessageSeverity[severity].toLowerCase();
	}

	@executeWhenFirstDisplayed()
	public clearTable() {
		this.dataProvider.clear();
		this._grid.setData(this.dataProvider, true);
		this._grid.render();
		this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows([]));
	}

	@executeWhenFirstDisplayed()
	updateData(startIndex: number, recordIds: number[], newRecords: DtoTableClientRecord[], totalNumberOfRecords: number): any {
		console.log("updateData");
		let editorCoordinates: { recordId: any; fieldName: any };
		editorCoordinates = this._grid.getCellEditor() != null ? {
			recordId: this.getActiveCellRecordId(),
			fieldName: this.getActiveCellFieldName()
		} : null;
		// TODO necessary: this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows([]));

		let changedRowNumbers = this.dataProvider.updateData(startIndex, recordIds, newRecords, totalNumberOfRecords);
		if (changedRowNumbers === true) {
			this._grid.invalidateAllRows();
		} else {
			this._grid.invalidateRows(changedRowNumbers);
		}

		this._grid.updateRowCount();
		this._grid.render();
		this.toggleColumnsThatAreHiddenWhenTheyContainNoVisibleNonEmptyCells();

		this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows(this.dataProvider.getSelectedRowsIndexes()));

		clearTimeout(this.loadingIndicatorFadeInTimer);
		fadeOut(this._$loadingIndicator);

		this._grid.resizeCanvas();
		this.updateSelectionFramePosition();

		if (editorCoordinates != null) {
			this.editCellIfAvailable(editorCoordinates.recordId, editorCoordinates.fieldName);
		}
	}

	setSorting(sortField: string, sortDirection: DtoSortDirection) {
		this._sortField = sortField;
		this._sortDirection = sortDirection;
		if (sortField) {
			this._grid.setSortColumn(sortField, sortDirection === DtoSortDirection.ASC);
		}
	}

	@executeWhenFirstDisplayed()
	public setCellValue(recordId: any, fieldName: string, data: any) {
		const node = this.dataProvider.getRecordById(recordId);
		if (node) {
			node.values[fieldName] = data;
		}
		this.rerenderRecordRow(recordId);
	}

	private rerenderRecordRow(recordId: any) {
		const rowIndex = this.dataProvider.getRowIndexByRecordId(recordId);
		if (rowIndex != null) {
			let editing = this._grid.getCellEditor();

			this._grid.invalidateRow(rowIndex);
			this._grid.render();

			if (editing && this.getActiveCellRecordId() && this.getActiveCellRecordId() === recordId) {
				this._grid.editActiveCell(null);
			}
		}
	}

	// private cellMessages: {[recordId: number]: {[fieldName: string]: DtoFieldMessage[]}} = {};
	private fieldMessagePopper = new FieldMessagesPopper();

	setSingleCellMessages(recordId: number, fieldName: string, messages: DtoFieldMessage[]): void {
		if (messages == null) {
			messages = [];
		}
		this.dataProvider.setCellMessages(recordId, fieldName, messages);
		this.rerenderRecordRow(recordId);
	}

	clearAllCellMessages() {
		this.dataProvider.clearAllCellMessages();
		this.rerenderAllRows();
	}

	private rerenderAllRows() {
		if (this._grid != null) {
			this._grid.invalidateAllRows();
			this._grid.updateRowCount();
			this._grid.render();
			this.toggleColumnsThatAreHiddenWhenTheyContainNoVisibleNonEmptyCells();
		}
	}

	setColumnMessages(fieldName: string, messages: DtoFieldMessage[]): void {
		const column = this.getColumnById(fieldName);
		column.messages = messages;
		const columnCssClass = this.getColumnCssClass(column);
		column.headerCssClass = column.cssClass = columnCssClass;
		this.setSlickGridColumns(this.getVisibleColumns());
	}

	private getVisibleColumns() {
		return this.allColumns.filter((column) => {
			return this.isSpecialColumn(column) || (column.visible && column.uiField.isVisible());
		});
	}

	private isSpecialColumn(column: Column) {
		return !column.uiField;
	}

	private getColumnById(id: string) {
		return this.allColumns.filter(column => column.id === id)[0];
	}

	@executeWhenFirstDisplayed()
	public markTableField(recordId: any, fieldName: string, mark: boolean) {
		this.dataProvider.setCellMarked(recordId, fieldName, mark);
		this.rerenderRecordRow(recordId);
	}

	@executeWhenFirstDisplayed()
	public clearAllFieldMarkings() {
		this.dataProvider.clearAllCellMarkings();
		for (let i = 0; i < this.dataProvider.getLength(); i++) {
			this._grid.invalidateRow(i);
		}
		this._grid.render();
	}

	@executeWhenFirstDisplayed()
	public setRecordBold(recordId: any, bold: boolean) {
		let rowIndex = this.dataProvider.getRowIndexByRecordId(recordId);
		if (rowIndex == null) {
			return;
		}
		const record = this.dataProvider.getItem(rowIndex);
		record.bold = bold;
		this._grid.invalidateRow(rowIndex);
		this._grid.render();
	}

	@executeWhenFirstDisplayed()
	public selectRecords(recordIds: number[], scrollToFirstIfAvailable: boolean) {
		const rowIndexes = recordIds.map(id => this.dataProvider.getRowIndexByRecordId(id));
		this.doWithoutFiringSelectionEvent(() => {
			if (rowIndexes.length > 0) {
				this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows(rowIndexes));
				if (scrollToFirstIfAvailable) {
					this._grid.scrollRowIntoView(rowIndexes[0], false);
				}
			} else {
				this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows([]));
			}
		});
	}

	@executeWhenFirstDisplayed()
	public selectRows(rowIndexes: number[], scrollToFirst: boolean) {
		this.doWithoutFiringSelectionEvent(() => {
			if (rowIndexes.length > 0) {
				this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows(rowIndexes));
				if (scrollToFirst) {
					this._grid.scrollRowIntoView(rowIndexes[0], false);
				}
			} else {
				this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows([]));
			}
		});
	}

	private doWithoutFiringSelectionEvent(action: () => any) {
		this.doNotFireEventBecauseSelectionIsCausedByApiCall = true;
		try {
			return action();
		} finally {
			this.doNotFireEventBecauseSelectionIsCausedByApiCall = false;
		}
	}

	editCellIfAvailable(recordId: number, propertyName: string): void {
		const rowNumber = this.dataProvider.getRowIndexByRecordId(recordId);
		if (rowNumber != null) {
			this._grid.setActiveCell(rowNumber, this._grid.getColumns().findIndex(c => c.id === propertyName));
			this._grid.editActiveCell(null);
		}
	}

	@executeWhenFirstDisplayed()
	public focusCell(recordId: any, columnPropertyName: string) {
		const rowIndex = this.dataProvider.getRowIndexByRecordId(recordId);
		if (rowIndex != null) {
			let columnIndex = this._grid.getColumnIndex(columnPropertyName);
			if (columnIndex != null) {
				this._grid.setActiveCell(rowIndex, columnIndex);
				this._grid.editActiveCell(null);
			}
		}
	}

	@executeWhenFirstDisplayed(true)
	@debouncedMethod(300)
	public onResize(): void {
		this._grid.resizeCanvas();
		this.rerenderAllRows();
		this._grid.getCellEditor() && (this._grid.getCellEditor() as any).onResize();
		this.updateSelectionFramePosition();
	}

	private handleFieldValueChanged(fieldName: string, value: any): void {
		let currentlyEditingThisColumn = !!this._grid.getCellEditor() && this.getActiveCellFieldName() === fieldName;
		if (currentlyEditingThisColumn) {
			this.onCellValueChanged.fire({recordId: this.getActiveCellRecordId(), columnPropertyName: fieldName, value: value})
		}
	}

	private getActiveCellValue() {
		if (this._grid.getActiveCell()) {
			return this._grid.getDataItem(this._grid.getActiveCell().row)[this._grid.getColumns()[this._grid.getActiveCell().cell].field];
		}
	};

	private getActiveCellRecordId(): any {
		if (this._grid.getActiveCell()) {
			let dataItem: DtoTableClientRecord = this._grid.getDataItem(this._grid.getActiveCell().row);
			return dataItem ? dataItem.id : null;
		}
	}

	private getActiveCellFieldName() {
		if (this._grid.getActiveCell()) {
			return this._grid.getColumns()[this._grid.getActiveCell().cell].id;
		}
	}

	public destroy(): void {
		super.destroy();
		this.fieldMessagePopper.destroy();
	}

	private updateSelectionFramePosition(animate: boolean = false) {
		let selectionFrame = this.config.selectionFrame;
		if (selectionFrame == null) {
			return;
		}
		let activeCellNode = this._grid.getActiveCellNode();
		if (activeCellNode == null) {
			manipulateWithoutTransitions(this.$selectionFrame, () => {
				applyCss(this.$selectionFrame, {
					top: -10000,
					left: -10000
				});
			}, false);
		} else {
			manipulateWithoutTransitions(this.$selectionFrame, () => {
				let $cell: HTMLElement = activeCellNode;
				let computedStyle = getComputedStyle($cell);
				let cellOffsetParentClientTop = $cell.offsetParent.getBoundingClientRect().top;
				let selectionFrameOffsetParentClientTop = this.$selectionFrame.offsetParent.getBoundingClientRect().top;
				let cssValues = {
					top: (cellOffsetParentClientTop - selectionFrameOffsetParentClientTop + $cell.offsetTop - selectionFrame.borderWidth) + "px",
					left: (selectionFrame.fullRow ? -selectionFrame.borderWidth : parseInt(computedStyle.left) - selectionFrame.borderWidth - this._grid.getViewport().leftPx) + "px",
					width: (selectionFrame.fullRow ? $cell.parentElement.offsetWidth + 2 * selectionFrame.borderWidth + 1 : $cell.parentElement.offsetWidth - parseInt(computedStyle.left) - parseInt(computedStyle.right) + 2 * selectionFrame.borderWidth - 1) + "px",
					height: ($cell.offsetHeight + 2 * selectionFrame.borderWidth - this.config.rowBorderWidth) + "px"
				};
				applyCss(this.$selectionFrame, cssValues);
			}, animate);
		}
	}

	@executeWhenFirstDisplayed()
	addColumns(columnConfigs: DtoTableColumn[], index: number): void {
		const slickColumnConfigs = this._grid.getColumns();
		const newSlickColumnConfigs = columnConfigs.map(columnConfig => this.createSlickColumnConfig(columnConfig));
		slickColumnConfigs.splice(index, 0, ...newSlickColumnConfigs);
		this.allColumns = slickColumnConfigs as Column[];
		this.dataProvider.clear();
		this.setSlickGridColumns(this.getVisibleColumns());
	}

	private setSlickGridColumns(columns: Column[]) {
		Object.values(this.headerFields).forEach(f => f.getMainElement().remove()); // prevent slickgrid from doing this via jQuery's empty() (and thereby removing all events handlers)
		Object.values(this.footerFields).forEach(f => f.getMainElement().remove()); // prevent slickgrid from doing this via jQuery's empty() (and thereby removing all events handlers)
		this._grid.setColumns(columns);
	}

	@executeWhenFirstDisplayed()
	removeColumns(columnNames: string[]): void {
		const slickColumnConfigs = this._grid.getColumns()
			.filter(c => columnNames.indexOf(c.id) === -1);
		this.allColumns = slickColumnConfigs as Column[];
		this.dataProvider.clear();
		this.setSlickGridColumns(this.getVisibleColumns());
	}

	@executeWhenFirstDisplayed()
	setColumnVisibility(propertyName: string, visible: boolean): void {
		const column = this.getColumnById(propertyName);
		if (column.visible !== visible) {
			column.visible = visible;
			this.setSlickGridColumns(this.getVisibleColumns());
		}
	}

	cancelEditingCell(recordId: number, propertyName: string): void {
		if (recordId === this.getActiveCellRecordId() && propertyName === this.getActiveCellFieldName()) {
			this._grid.getEditController().cancelCurrentEdit();
		}
	}

	setContextMenuContent(requestId: number, component: Component): void {
		this.contextMenu.setContent(component, requestId);
	}

	closeContextMenu(requestId: number): void {
		this.contextMenu.close(requestId);
	}

}


