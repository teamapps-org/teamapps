/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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


import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {
	UiTable_CellClickedEvent,
	UiTable_CellEditingStartedEvent,
	UiTable_CellEditingStoppedEvent,
	UiTable_CellValueChangedEvent,
	UiTable_ColumnSizeChangeEvent,
	UiTable_ContextMenuRequestedEvent,
	UiTable_DisplayedRangeChangedEvent,
	UiTable_FieldOrderChangeEvent, UiTable_RowsSelectedEvent,
	UiTable_SortingChangedEvent,
	UiTableCommandHandler,
	UiTableConfig,
	UiTableEventSource
} from "../../generated/UiTableConfig";
import {UiField, ValueChangeEventData} from "../formfield/UiField";
import {DEFAULT_TEMPLATES} from "../trivial-components/TrivialCore";
import {UiTableColumnConfig} from "../../generated/UiTableColumnConfig";
import {UiCompositeFieldTableCellEditor} from "./UiCompositeFieldTableCellEditor";
import {debouncedMethod, DebounceMode} from "../util/debounce";
import {AbstractUiComponent} from "../AbstractUiComponent";
import {UiDropDown} from "../micro-components/UiDropDown";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";
import {arraysEqual, closestAncestor, css, fadeIn, fadeOut, manipulateWithoutTransitions, parseHtml, Renderer} from "../Common";
import {UiSortDirection} from "../../generated/UiSortDirection";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiGenericTableCellEditor} from "./UiGenericTableCellEditor";
import {FixedSizeTableCellEditor} from "./FixedSizeTableCellEditor";
import {UiHierarchicalClientRecordConfig} from "../../generated/UiHierarchicalClientRecordConfig";
import {TableDataProvider} from "./TableDataProvider";
import {UiButton, UiCompositeField, UiFileField, UiMultiLineTextField, UiRichTextEditor} from "..";
import {UiFieldMessageConfig} from "../../generated/UiFieldMessageConfig";
import {FieldMessagesPopper, getHighestSeverity} from "../micro-components/FieldMessagesPopper";
import {nonRecursive} from "../util/nonRecursive";
import {throttledMethod} from "../util/throttle";
import {UiFieldMessageSeverity} from "../../generated/UiFieldMessageSeverity";
import {UiTableClientRecordConfig} from "../../generated/UiTableClientRecordConfig";
import {UiTableRowSelectionModel} from "./UiTableRowSelectionModel";
import {ContextMenu} from "../micro-components/ContextMenu";
import {UiComponent} from "../UiComponent";
import EventData = Slick.EventData;
import {UiTextAlignment} from "../../generated/UiTextAlignment";
import {UiRefreshableTableConfigUpdateConfig} from "../../generated/UiRefreshableTableConfigUpdateConfig";

interface Column extends Slick.Column<any> {
	id: string;
	field: string;
	uiField?: UiField;
	name: string;
	width: number;
	minWidth?: number;
	maxWidth?: number;
	formatter: (row: number, cell: number, value: any, columnDef: Slick.Column<UiTableClientRecordConfig>, dataContext: UiTableClientRecordConfig) => string;
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
	messages?: UiFieldMessageConfig[],
	uiConfig?: UiTableColumnConfig,
	visible: boolean
}

const backgroundColorCssClassesByMessageSeverity = {
	[UiFieldMessageSeverity.INFO]: "bg-info",
	[UiFieldMessageSeverity.SUCCESS]: "bg-success",
	[UiFieldMessageSeverity.WARNING]: "bg-warning",
	[UiFieldMessageSeverity.ERROR]: "bg-danger",
};

type FieldsByName = { [fieldName: string]: UiField };

export class UiTable extends AbstractUiComponent<UiTableConfig> implements UiTableCommandHandler, UiTableEventSource {

	public readonly onCellEditingStarted: TeamAppsEvent<UiTable_CellEditingStartedEvent> = new TeamAppsEvent();
	public readonly onCellEditingStopped: TeamAppsEvent<UiTable_CellEditingStoppedEvent> = new TeamAppsEvent();
	public readonly onCellValueChanged: TeamAppsEvent<UiTable_CellValueChangedEvent> = new TeamAppsEvent();
	public readonly onCellClicked: TeamAppsEvent<UiTable_CellClickedEvent> = new TeamAppsEvent();
	public readonly onSortingChanged: TeamAppsEvent<UiTable_SortingChangedEvent> = new TeamAppsEvent();
	public readonly onRowsSelected: TeamAppsEvent<UiTable_RowsSelectedEvent> = new TeamAppsEvent();
	public readonly onFieldOrderChange: TeamAppsEvent<UiTable_FieldOrderChangeEvent> = new TeamAppsEvent();
	public readonly onColumnSizeChange: TeamAppsEvent<UiTable_ColumnSizeChangeEvent> = new TeamAppsEvent();
	public readonly onDisplayedRangeChanged: TeamAppsEvent<UiTable_DisplayedRangeChangedEvent> = new TeamAppsEvent();
	public readonly onContextMenuRequested: TeamAppsEvent<UiTable_ContextMenuRequestedEvent> = new TeamAppsEvent();

	private $component: HTMLElement;
	private _grid: Slick.Grid<any>;
	private allColumns: Column[];
	private dataProvider: TableDataProvider;
	private _$loadingIndicator: HTMLElement;
	private loadingIndicatorFadeInTimer: number;

	private _sortField: string;
	private _sortDirection: UiSortDirection;

	private doNotFireEventBecauseSelectionIsCausedByApiCall: boolean = false; // slickgrid fires events even if we set the selection via api...

	private dropDown: UiDropDown;
	private $selectionFrame: HTMLElement;
	private headerRowFields: FieldsByName = {};
	private footerRowFields: FieldsByName = {};

	private $editorFieldTempContainer: HTMLElement;

	private contextMenu: ContextMenu;

	private rowSelectionCausedByApiCall: boolean;

	constructor(config: UiTableConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$component = parseHtml(`<div class="UiTable"">
    <div class="slick-table"></div>
    <div class="editor-field-temp-container hidden"></div>
</div>`);
		const $table = this.$component.querySelector<HTMLElement>(":scope .slick-table");
		if (config.stripedRows) {
			$table.classList.add("striped-rows");
		}
		this.$editorFieldTempContainer = this.$component.querySelector<HTMLElement>(":scope .editor-field-temp-container");

		this.dataProvider = new TableDataProvider();

		this.createSlickGrid(config, $table);

		this.contextMenu = new ContextMenu();

		this.dropDown = new UiDropDown();

		this.displayedDeferredExecutor.invokeWhenReady(() => {
			if (this._grid != null) {
				this._grid.scrollRowToTop(0); // the scroll position gets lost when the table gets detached, so it is necessary to inform it that it should display the top of the table
			}
		});
	}

	private isRowExpanded(item: UiHierarchicalClientRecordConfig): boolean {
		return item.expanded;
	}

	@executeWhenFirstDisplayed()
	private createSlickGrid(config: UiTableConfig, $table: HTMLElement) {
		this.allColumns = this._createColumns();

		if (config.showRowCheckBoxes) {
			var checkboxSelector = new Slick.CheckboxSelectColumn({});
			this.allColumns.unshift(checkboxSelector.getColumnDefinition() as Column);
		}
		if (config.showNumbering) {
			const RowNumberFormatter: Slick.Formatter<UiTableClientRecordConfig> = (row: number, cell: number, value: any, columnDef: Slick.Column<UiTableClientRecordConfig>, dataContext: UiTableClientRecordConfig) => {
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

		const options: Slick.GridOptions<any> & { createFooterRow: boolean, showFooterRow: boolean, footerRowHeight: number } = {
			explicitInitialization: true,
			enableCellNavigation: true,
			enableColumnReorder: false,
			forceFitColumns: config.forceFitWidth,
			fullWidthRows: true,
			rowHeight: config.rowHeight + config.rowBorderWidth,
			multiColumnSort: false,
			multiSelect: config.allowMultiRowSelection,
			enableTextSelectionOnCells: config.textSelectionEnabled, // see also CSS style user-select: none
			editable: config.editable,
			// editCommandHandler: (item: TableDataProviderItem, column: Column, editCommand: any) => {
			// 	column.uiField.commit();
			// 	editCommand.execute();
			// },
			showHeaderRow: config.showHeaderRow,
			headerRowHeight: config.headerRowHeight,
			createFooterRow: true,
			showFooterRow: config.showFooterRow,
			footerRowHeight: config.footerRowHeight,
		};

		this._grid = new Slick.Grid($table, this.dataProvider, this.getVisibleColumns(), options);

		config.columns.forEach(c => {
			if (c.headerRowField != null) {
				this.headerRowFields[c.name] = c.headerRowField as UiField;
			}
			if (c.footerRowField != null) {
				this.footerRowFields[c.name] = c.footerRowField as UiField;
			}
		});
		this.configureOuterFields(this.headerRowFields as FieldsByName, true);
		this.configureOuterFields(this.footerRowFields as FieldsByName, false);

		if (config.hideHeaders) {
			css($table.querySelector<HTMLElement>(":scope .slick-header-columns"), {
				height: 0,
				border: "none"
			});
			//this._grid.resizeCanvas();
		}

		$table.classList.add(config.displayAsList ? 'list-mode' : 'table-mode');

		this._grid.setSelectionModel(new UiTableRowSelectionModel());

		if (config.showRowCheckBoxes) {
			this._grid.registerPlugin(checkboxSelector);
		}

		this._$loadingIndicator = parseHtml(DEFAULT_TEMPLATES.defaultSpinnerTemplate);
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
			this._grid.setSortColumn(config.sortField, config.sortDirection === UiSortDirection.ASC);
		}
		this._grid.onSort.subscribe((e, args: Slick.OnSortEventArgs<Slick.SlickData>) => {
			this._sortField = args.sortCol.id;
			this._sortDirection = args.sortAsc ? UiSortDirection.ASC : UiSortDirection.DESC;
			this.onSortingChanged.fire({
				sortField: this._sortField,
				sortDirection: args.sortAsc ? UiSortDirection.ASC : UiSortDirection.DESC
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
		this._grid.onCellChange.subscribe((eventData: EventData, args: Slick.OnCellChangeEventArgs<UiTableClientRecordConfig>) => {

			// The problem with this approach is we do not get the intermediate committed change events!
			// let columnPropertyName = this.getVisibleColumns()[args.cell].id;
			// this.onCellValueChanged.fire({recordId: args.item.id, columnPropertyName: columnPropertyName, value: args.item.values[columnPropertyName]});

			this.updateSelectionFramePosition(true);
		});
		this._grid.onClick.subscribe((e: MouseEvent, args: Slick.OnClickEventArgs<UiTableClientRecordConfig>) => {
			setTimeout(/* make sure the table updated its activeCell property! */ () => {
				const column = this._grid.getColumns()[args.cell];
				let fieldName = column.id;
				let uiField: UiField = (column as Column).uiField;
				let item = this.dataProvider.getItem(args.row);
				if (item) { // may be undefined if this is a new row!
					let $buttonElement = $(e.target).closest(".UiButton");
					if (uiField && uiField instanceof UiButton && $buttonElement.length > 0) {
						this.logger.warn("TODO: handle button click, especially in case of a dropdown...");
						// uiField.onValueChanged.fire(null);
						// this.dropDown.setContentComponent(null);
						// this.dropDown.open($buttonElement, {
						// 	width: uiButton.minDropDownWidth,
						// 	minHeight: uiButtonConfig.minDropDownHeight
						// });
					} else if (uiField instanceof UiFileField) {
						let $templateWrapper = $((<any>e).target).closest(".custom-entry-template-wrapper");
						if ($templateWrapper.length > 0) {
							let index = $templateWrapper.parent().index($templateWrapper);
							this.logger.warn("TODO: handle file field click");
						}
					}
					if (uiField) {
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
		$(this._grid.getCanvasNode()).dblclick((e) => {
			let cell = this._grid.getCellFromEvent(<any>e);
			if (cell != null) {
				// TODO double-click event?
			}
		});

		$(this._grid.getCanvasNode()).on("contextmenu", (e) => {
			let cell = this._grid.getCellFromEvent(<any>e);
			if (cell != null && this.dataProvider.getItem(cell.row) != null) {
				let recordId = this.dataProvider.getItem(cell.row).id;
				this._grid.setSelectedRows([cell.row]);
				this.onRowsSelected.fire({
					recordIds: [recordId]
				});
				if (!isNaN(recordId) && this._config.contextMenuEnabled) {
					this.contextMenu.open(e as unknown as MouseEvent, requestId => this.onContextMenuRequested.fire({
						recordId: recordId,
						requestId
					}));
				}
			}
		});

		if (config.selectionFrame) {
			this.$selectionFrame = parseHtml(`<div class="UiTable-selection-frame">`);
			css(this.$selectionFrame, {
				border: `${this._config.selectionFrame.borderWidth}px solid ${(this._config.selectionFrame.color)}`,
				boxShadow: `0 0 ${this._config.selectionFrame.shadowWidth}px 0 rgba(0, 0, 0, .5), 0 0 ${this._config.selectionFrame.glowingWidth}px 0 ${(this._config.selectionFrame.color)}`,
				transition: `top ${this._config.selectionFrame.animationDuration}ms, left ${this._config.selectionFrame.animationDuration}ms, right ${this._config.selectionFrame.animationDuration}ms, width ${this._config.selectionFrame.animationDuration}ms, height ${this._config.selectionFrame.animationDuration}ms`
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

	private createDisplayRangeChangedEvent(): UiTable_DisplayedRangeChangedEvent {
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

	private configureOuterFields(fieldsByColumnId: { [columnName: string]: UiField }, header: boolean) {
		let renderedEvent: Slick.Event<Slick.OnHeaderRowCellRenderedEventArgs<any>> = header ? this._grid.onHeaderRowCellRendered : (this._grid as any).onFooterRowCellRendered;
		renderedEvent.subscribe((e, args) => {
			$(args.node)[0].innerHTML = '';
			const columnName = args.column.id;
			let field = fieldsByColumnId[columnName];
			if (field) {
				args.node.appendChild(field.getMainElement());
			}
		});
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
		for (let i = 0; i < this._config.columns.length; i++) {
			const slickColumnConfig = this.createSlickColumnConfig(this._config.columns[i]);
			columns.push(slickColumnConfig);
		}
		return columns;
	}

	private createSlickColumnConfig(columnConfig: UiTableColumnConfig): Column {
		const uiField = columnConfig.field as UiField;
		this.prepareEditorField(columnConfig.name, uiField);

		let editorFactory;
		if (uiField instanceof UiCompositeField) {
			editorFactory = UiCompositeFieldTableCellEditor.bind(null, uiField, () => this.$editorFieldTempContainer.appendChild(uiField.getMainElement()));
		} else if (uiField instanceof UiRichTextEditor || uiField instanceof UiMultiLineTextField) {
			editorFactory = FixedSizeTableCellEditor.bind(null, uiField, () => this.$editorFieldTempContainer.appendChild(uiField.getMainElement()));
		} else {
			editorFactory = UiGenericTableCellEditor.bind(null, uiField, () => this.$editorFieldTempContainer.appendChild(uiField.getMainElement()));
		}

		var displayTemplateRenderer = columnConfig.displayTemplate != null ? this._context.templateRegistry.createTemplateRenderer(columnConfig.displayTemplate) : null;

		const slickColumnConfig: Column = {
			id: columnConfig.name,
			field: columnConfig.name,
			uiField: uiField,
			name: `<div class="column-header-icon img img-16 ${columnConfig.icon == null ? "hidden" : ""}" style="background-image: url('${columnConfig.icon}')"></div>
<div class="column-header-title">${columnConfig.title}</div>`,
			width: columnConfig.defaultWidth || ((columnConfig.minWidth + columnConfig.maxWidth) / 2) || undefined,
			minWidth: columnConfig.minWidth || 30,
			maxWidth: columnConfig.maxWidth || undefined,
			formatter: this.createCellFormatter(uiField, displayTemplateRenderer),
			editor: editorFactory,
			asyncEditorLoading: false,
			autoEdit: true,
			focusable: this._config.displayAsList || uiField.isEditable(),
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
		columnCssClasses.push("align-" + UiTextAlignment[column.uiConfig.headerAlignment].toLocaleLowerCase());
		return columnCssClasses.join(" ");
	}

	private prepareEditorField(columnPropertyName: string, uiField: UiField) {
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
		uiField.onValueChanged.addListener((eventData: ValueChangeEventData) => this.handleFieldValueChanged(columnPropertyName, eventData.value));
		uiField.onVisibilityChanged.addListener(visible => this.setSlickGridColumns(this.getVisibleColumns()));

		if (uiField instanceof UiCompositeField) {
			uiField.onSubFieldValueChanged.addListener((eventData) => {
				this.handleFieldValueChanged(eventData.fieldName, eventData.value);
				this._grid.getDataItem(this._grid.getActiveCell().row)[eventData.fieldName] = eventData.value;
			});
		}
	}

	private createCellFormatter(field: UiField, displayTemplate: Renderer | null | undefined) {
		const createInnerCellFormatter = () => {
			if (field instanceof UiCompositeField) {
				this.logger.warn("TODO: create cell formatter for UiCompositeField!");
				return null;
				// return (row: number, cell: number, value: any, columnDef: Slick.Column<TableDataProviderItem>, dataContext: TableDataProviderItem) => {
				// return field.getReadOnlyHtml(field as UiCompositeFieldConfig, {_type: "UiRecordValue", value: dataContext}, this._context, columnDef.width + 1);
				// };
			} else if (displayTemplate != null) {
				return (row: number, cell: number, value: any, columnDef: Slick.Column<UiTableClientRecordConfig>, dataContext: UiTableClientRecordConfig) => {
					return displayTemplate.render(dataContext.displayTemplateValues[columnDef.id]);
				};
			} else if (field.getReadOnlyHtml != null) {
				return (row: number, cell: number, value: any, columnDef: Slick.Column<UiTableClientRecordConfig>, dataContext: UiTableClientRecordConfig) => {
					return field.getReadOnlyHtml(dataContext.values[columnDef.id], columnDef.width);
				};
			} else {
				return null;
			}
		};

		const innerCellFormatter = createInnerCellFormatter(); // may be undefined!
		return (row: number, cell: number, value: any, columnDef: Slick.Column<UiTableClientRecordConfig>, dataContext: UiTableClientRecordConfig) => {
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

	private getCellMessageCssClassName(severity: UiFieldMessageSeverity) {
		return "message-" + UiFieldMessageSeverity[severity].toLowerCase();
	}

	@executeWhenFirstDisplayed()
	public clearTable() {
		this.dataProvider.clear();
		this._grid.setData(this.dataProvider, true);
		this._grid.render();
		this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows([]));
	}

	@executeWhenFirstDisplayed()
	updateData(startIndex: number, recordIds: number[], newRecords: UiTableClientRecordConfig[], totalNumberOfRecords: number): any {
		let editorCoordinates: { row: number, recordId: any; fieldName: any };
		editorCoordinates = this._grid.getCellEditor() != null ? {
			row: this._grid.getActiveCell().row,
			recordId: this.getActiveCellRecordId(),
			fieldName: this.getActiveCellFieldName()
		} : null;
		let editorRowChanged = false;
		const countChanged = this.dataProvider.getLength() != totalNumberOfRecords;

		// TODO necessary: this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows([]));
		let changedRowNumbers = this.dataProvider.updateData(startIndex, recordIds, newRecords, totalNumberOfRecords);
		if (changedRowNumbers === true) {
			this._grid.invalidateAllRows();
			editorRowChanged = editorCoordinates != null;
		} else {
			this._grid.invalidateRows(changedRowNumbers);
			editorRowChanged = editorCoordinates != null && changedRowNumbers.indexOf(editorCoordinates.row) >= 0;
		}

		if (countChanged) {
			this._grid.updateRowCount(); // this will make the editor lose focus
		}
		this._grid.render(); // this will make the editor lose focus, if its row changed (and therefore was invalidated)
		this.toggleColumnsThatAreHiddenWhenTheyContainNoVisibleNonEmptyCells();
		this.doWithoutFiringSelectionEvent(() => this._grid.setSelectedRows(this.dataProvider.getSelectedRowsIndexes()));
		clearTimeout(this.loadingIndicatorFadeInTimer);

		fadeOut(this._$loadingIndicator);

		if (countChanged) {
			this._grid.resizeCanvas(); // this will make the editor lose focus
		}
		this.updateSelectionFramePosition();

		if (editorCoordinates != null && (editorRowChanged || countChanged)) {
			this.editCellIfVisible(editorCoordinates.recordId, editorCoordinates.row, editorCoordinates.fieldName);
		}
	}

	setSorting(sortField: string, sortDirection: UiSortDirection) {
		this._sortField = sortField;
		this._sortDirection = sortDirection;
		if (sortField) {
			this._grid.setSortColumn(sortField, sortDirection === UiSortDirection.ASC);
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

	// private cellMessages: {[recordId: number]: {[fieldName: string]: UiFieldMessageConfig[]}} = {};
	private fieldMessagePopper = new FieldMessagesPopper();

	setSingleCellMessages(recordId: number, fieldName: string, messages: UiFieldMessageConfig[]): void {
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

	setColumnMessages(fieldName: string, messages: UiFieldMessageConfig[]): void {
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

	editCellIfVisible(recordId: number, row: number, propertyName: string): void {
		let rowNumberFromRecordId = this.dataProvider.getRowIndexByRecordId(recordId);
		const rowNumber = rowNumberFromRecordId != -1 ? rowNumberFromRecordId : row;
		if (rowNumber >= this._grid.getViewport().top && rowNumber <= this._grid.getViewport().bottom) {
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
			} else {
				let compositeFieldColumnIndex = this.getCompositeFieldColumnForSubFieldName(columnPropertyName);
				if (compositeFieldColumnIndex != null) {
					this._grid.setActiveCell(rowIndex, compositeFieldColumnIndex);
					this._grid.editActiveCell(null);
					((<any>this._grid.getCellEditor()).uiField as UiCompositeField).focusField(columnPropertyName);
				}
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
		// TODO check if this has updated a boolean field and update composite fields accordingly (field visibilities)

		let currentlyEditingThisColumn = !!this._grid.getCellEditor() && this.getActiveCellFieldName() === fieldName;
		if (currentlyEditingThisColumn) {
			this.onCellValueChanged.fire({recordId: this.getActiveCellRecordId(), columnPropertyName: fieldName, value: value})
		}
	}

	private getCompositeFieldColumnForSubFieldName(columnPropertyName: string): number {
		if (this.allColumns.filter(c => c.id == columnPropertyName).length !== 0) {
			return null; // this is a normal field...
		}
		for (let col = 0; col < this.allColumns.length; col++) {
			let field = this.allColumns[col].uiField;
			if (field && field instanceof UiCompositeField) {
				if (field.getSubFields().filter(subField => subField.config.propertyName === columnPropertyName).length > 0) {
					return col;
				}
			}
		}
		return null;
	}

	private getActiveCellValue() {
		if (this._grid.getActiveCell()) {
			return this._grid.getDataItem(this._grid.getActiveCell().row)[this._grid.getColumns()[this._grid.getActiveCell().cell].field];
		}
	};

	private getActiveCellRecordId(): any {
		if (this._grid.getActiveCell()) {
			let dataItem: UiTableClientRecordConfig = this._grid.getDataItem(this._grid.getActiveCell().row);
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

	public updateRefreshableConfig(config: UiRefreshableTableConfigUpdateConfig) {
		let options = {
			...this._grid.getOptions(),
			forceFitColumns: config.forceFitWidth,
			rowHeight: config.rowHeight + config.rowBorderWidth,
			multiSelect: config.allowMultiRowSelection,
			enableTextSelectionOnCells: config.textSelectionEnabled,
			editable: config.editable,
			showHeaderRow: config.showHeaderRow,
			headerRowHeight: config.headerRowHeight,
			showFooterRow: config.showFooterRow,
			footerRowHeight: config.footerRowHeight,
		};
		this._grid.setOptions(options);
	}

	private updateSelectionFramePosition(animate: boolean = false) {
		let selectionFrame = this._config.selectionFrame;
		if (selectionFrame == null) {
			return;
		}
		let activeCellNode = this._grid.getActiveCellNode();
		if (activeCellNode == null) {
			manipulateWithoutTransitions(this.$selectionFrame, () => {
				css(this.$selectionFrame, {
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
					width: (selectionFrame.fullRow ? $($cell.parentElement).width() + 2 * selectionFrame.borderWidth + 1 : $($cell.parentElement).width() - parseInt(computedStyle.left) - parseInt(computedStyle.right) + 2 * selectionFrame.borderWidth - 1) + "px",
					height: ($cell.offsetHeight + 2 * selectionFrame.borderWidth - this._config.rowBorderWidth) + "px"
				};
				css(this.$selectionFrame, cssValues);
			}, animate);
		}
	}

	@executeWhenFirstDisplayed()
	addColumns(columnConfigs: UiTableColumnConfig[], index: number): void {
		const slickColumnConfigs = this._grid.getColumns();
		const newSlickColumnConfigs = columnConfigs.map(columnConfig => this.createSlickColumnConfig(columnConfig));
		slickColumnConfigs.splice(index, 0, ...newSlickColumnConfigs);
		this.allColumns = slickColumnConfigs as Column[];
		this.dataProvider.clear();
		this.setSlickGridColumns(this.getVisibleColumns());
	}

	private setSlickGridColumns(columns: Column[]) {
		Object.values(this.headerRowFields).forEach(f => f.getMainElement().remove()); // prevent slickgrid from doing this via jQuery's empty() (and thereby removing all events handlers)
		Object.values(this.footerRowFields).forEach(f => f.getMainElement().remove()); // prevent slickgrid from doing this via jQuery's empty() (and thereby removing all events handlers)
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

	setContextMenuContent(requestId: number, component: UiComponent): void {
		this.contextMenu.setContent(component, requestId);
	}

	closeContextMenu(requestId: number): void {
		this.contextMenu.close(requestId);
	}

	setHeaderRowField(columnName: string, field: unknown): any {
		if (field == null) {
			delete this.headerRowFields[columnName];
		} else {
			this.headerRowFields[columnName] = field as UiField;
		}
		this.configureOuterFields(this.headerRowFields as FieldsByName, true);
		if (this._grid != null) {
			this._grid.setColumns(this._grid.getColumns());
		}
	}

	setFooterRowField(columnName: string, field: unknown): any {
		if (field == null) {
			delete this.footerRowFields[columnName];
		} else {
			this.footerRowFields[columnName] = field as UiField;
		}
		this.configureOuterFields(this.footerRowFields as FieldsByName, false);
		if (this._grid != null) {
			this._grid.setColumns(this._grid.getColumns());
		}
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiTable", UiTable);
