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

import {AbstractComponent} from "teamapps-client-core";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {Constants, generateUUID, parseHtml} from "./Common";
import {TeamAppsUiContext} from "teamapps-client-core";
import {executeWhenFirstDisplayed} from "./util/executeWhenFirstDisplayed";
import {
	DtoInfiniteItemView_ContextMenuRequestedEvent,
	DtoInfiniteItemView_DisplayedRangeChangedEvent,
	DtoInfiniteItemView_ItemClickedEvent,
	DtoInfiniteItemViewCommandHandler,
	DtoInfiniteItemView,
	DtoInfiniteItemViewEventSource
} from "../generated/DtoInfiniteItemView";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {DtoTemplate} from "../generated/DtoTemplate";
import {itemCssStringsAlignItems, itemCssStringsJustification} from "./UiItemView";
import {UiItemJustification} from "../generated/UiItemJustification";
import {DtoIdentifiableClientRecord} from "../generated/DtoIdentifiableClientRecord";
import {UiVerticalItemAlignment} from "../generated/UiVerticalItemAlignment";
import {ContextMenu} from "./micro-components/ContextMenu";
import {UiComponent} from "./UiComponent";
import {loadSensitiveThrottling, throttle} from "./util/throttle";
import {
	createDtoInfiniteItemViewDataRequest,
	DtoInfiniteItemViewDataRequest
} from "../generated/DtoInfiniteItemViewDataRequest";
import {DtoTableClientRecord} from "../generated/DtoTableClientRecord";

const ROW_LOOKAHAED = 10;

export interface TableDataProviderItem extends DtoTableClientRecord {
	children: TableDataProviderItem[];
	depth: number;
	parentId: number;
	expanded: boolean;
}

class UiInfiniteItemViewDataProvider implements Slick.DataProvider<DtoIdentifiableClientRecord> {

	private availableWidth: number;
	private timerId: number = null;

	private totalNumberOfRecords = 0;

	constructor(private data: DtoIdentifiableClientRecord[], private itemWidthIncludingMargin: number, private dataRequestCallback: (from: number, length: number) => void) {
	}

	/**
	 * This method is called by SlickGrid.
	 */
	public getLength(): number {
		return Math.ceil(this.totalNumberOfRecords / this.getItemsPerRow());
	}

	public setTotalNumberOfRecords(totalNumberOfRecords: number) {
		if (this.data.length > totalNumberOfRecords) {
			this.data.length = totalNumberOfRecords;
		}
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	/**
	 * This method is called by SlickGrid.
	 */
	public getItem(index: number): any[] {
		let itemsPerRow = this.getItemsPerRow();
		let startIndex = Math.floor(index * itemsPerRow);
		return this.data.slice(startIndex, startIndex + itemsPerRow);
	}

	/**
	 * This method is called by SlickGrid.
	 */
	public getItemMetadata(index: number): any {
		return {};
	}

	public getItemsPerRow(): number {
		return Math.floor(this.availableWidth / this.itemWidthIncludingMargin);
	}

	public setItemWidthIncludingMargin(itemWidthIncludingMargin: number) {
		this.itemWidthIncludingMargin = itemWidthIncludingMargin;
	}

	public setAvailableWidth(availableWidth: number) {
		this.availableWidth = availableWidth;
	}

	public ensureData(firstVisibleRowIndex: number, lastVisibleRowIndex: number) {
		if (firstVisibleRowIndex > lastVisibleRowIndex) {
			return;
		}

		let itemsPerRow = this.getItemsPerRow();
		let from = Math.max(firstVisibleRowIndex - ROW_LOOKAHAED, 0) * itemsPerRow;
		let toInclusive = (lastVisibleRowIndex + ROW_LOOKAHAED) * itemsPerRow;
		toInclusive = Math.min(toInclusive, this.totalNumberOfRecords - 1);

		while (this.data[from] !== undefined && from < toInclusive) {
			from++;
		}
		while (this.data[toInclusive] !== undefined && from < toInclusive) {
			toInclusive--;
		}

		if (firstVisibleRowIndex * itemsPerRow > toInclusive || (lastVisibleRowIndex + 1) * itemsPerRow < from) { // not really necessary to load anything
			return;
		}

		if (from == toInclusive && this.data[toInclusive] !== undefined) {
			return;
		}

		if (this.timerId != null) {
			clearTimeout(this.timerId);
		}

		this.timerId = window.setTimeout(() => {
			for (var i = from; i <= toInclusive; i++) {
				this.data[i] = null; // null indicates a 'requested but not available yet'
			}

			let length = toInclusive - from;
			this.dataRequestCallback(from, length);
		}, 100);
	}

	public clear() {
		this.data.length = 0;
	}

	setData(startIndex: number, data: any[]) {
		this.data.length = Math.max(this.data.length, startIndex + data.length);
		this.data.splice.apply(this.data, ([startIndex, data.length] as any[]).concat(data));
	}

	removeData(ids: number[]) {
		let idsAsMap: { [id: string]: number } = ids.reduce((previousValue: { [id: string]: number }, currentValue) => {
			previousValue[currentValue] = currentValue;
			return previousValue;
		}, {});
		for (let i = 0; i < this.data.length; i++) {
			const currentRecord = this.data[i];
			if (currentRecord != null && idsAsMap[currentRecord.id] != null) {
				this.data[i] = undefined;
			}
		}
	}
}

export class UiInfiniteItemView extends AbstractLegacyComponent<DtoInfiniteItemView> implements DtoInfiniteItemViewCommandHandler, DtoInfiniteItemViewEventSource {


	public readonly onDisplayedRangeChanged: TeamAppsEvent<DtoInfiniteItemView_DisplayedRangeChangedEvent> = new TeamAppsEvent();
	public readonly onItemClicked: TeamAppsEvent<DtoInfiniteItemView_ItemClickedEvent> = new TeamAppsEvent();
	public readonly onContextMenuRequested: TeamAppsEvent<DtoInfiniteItemView_ContextMenuRequestedEvent> = new TeamAppsEvent();

	private $mainDomElement: HTMLElement;
	private $grid: HTMLElement;
	private grid: Slick.Grid<any>;
	private dataProvider: UiInfiniteItemViewDataProvider;
	private itemTemplateRenderer: Renderer;
	private uuid: string;
	private horizontalItemMargin: number;
	private itemWidth: number;
	private itemJustification: UiItemJustification;
	private verticalItemAlignment: UiVerticalItemAlignment;
	private contextMenu: ContextMenu;

	constructor(config: DtoInfiniteItemView, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this.uuid = generateUUID();
		this.$mainDomElement = parseHtml(`<div class="UiInfiniteItemView grid-${this.uuid}">
                <div class="slickgrid"></div>
            </div>`);
		this.$grid = this.$mainDomElement.querySelector<HTMLElement>(":scope .slickgrid");
		this.setItemTemplate(config.itemTemplate);
		this.itemWidth = config.itemWidth;
		this.horizontalItemMargin = config.horizontalItemMargin;
		this.itemJustification = config.itemJustification;
		this.verticalItemAlignment = config.verticalItemAlignment;
		this.dataProvider = new UiInfiniteItemViewDataProvider(config.data || [], 10 /*cannot know item width until component width is known*/, (fromIndex, length) => {
			this.onDisplayedRangeChanged.fire(this.createDisplayRangeChangedEvent(createDtoInfiniteItemViewDataRequest(fromIndex, length)));
		});
		if (config.totalNumberOfRecords) {
			this.dataProvider.setTotalNumberOfRecords(config.totalNumberOfRecords);
		}
		this.createGrid();

		this.contextMenu = new ContextMenu();

		let me = this;
		$(this.getMainElement())
			.on("click", ".item-wrapper", function (e: JQueryMouseEventObject) {
				let recordId = parseInt((<Element>this).getAttribute("data-id"));
				me.onItemClicked.fire({
					recordId: recordId,
					isDoubleClick: false
				});
			})
			.on("contextmenu", ".item-wrapper", function (e: JQueryMouseEventObject) {
				let recordId = parseInt((<Element>this).getAttribute("data-id"));
				if (!isNaN(recordId) && me._config.contextMenuEnabled) {
					me.contextMenu.open(e as unknown as MouseEvent, requestId => me.onContextMenuRequested.fire({
						recordId: recordId,
						requestId
					}));
				}
			})
			.on("dblclick", ".item-wrapper", function (e: JQueryMouseEventObject) {
				let recordId = parseInt((<Element>this).getAttribute("data-id"));
				me.onItemClicked.fire({
					recordId: recordId,
					isDoubleClick: true
				});
			});

		this.setHorizontalItemMargin(config.horizontalItemMargin);
	}

	@executeWhenFirstDisplayed()
	private createGrid() {
		let cellFormatter = (row: number, cell: number, value: any, columnDef: Slick.Column<TableDataProviderItem>, dataContext: any[]) => {
			let html = '<div class="line-wrapper">';
			for (let record of dataContext) {
				if (record != null) { // null happens for unknown reasons...
					html += `<div class="item-wrapper" data-id="${record.id}" style="width: ${this.calculateItemWidthInPixels(false)}px;">${this.itemTemplateRenderer.render(record.values)}</div>`;
				}
			}
			html += "</div>";
			return html;
		};

		let columns: any[] = [{
			id: "main",
			field: "",
			name: null, // no label
			width: 10000,
			formatter: cellFormatter,
			resizable: true
		}];

		let options = {
			enableColumnReorder: false,
			forceFitColumns: true,
			fullWidthRows: true,
			rowHeight: this.config.rowHeight,
			enableTextSelectionOnCells: false,
			editable: false,
			enableAddRow: false
		};

		this.dataProvider.setAvailableWidth(this.getAvailableWidth());

		this.grid = new Slick.Grid(this.$grid, this.dataProvider, columns, options);

		this.grid.onViewportChanged.subscribe((e, args) => {
			this.dataProvider.ensureData(this.grid.getViewport().top, this.grid.getViewport().bottom);
		});
		this.grid.onScroll.subscribe(throttle((eventData) => {
			// CAUTION: debounce/throttle scrolling without data requests only!!! (otherwise the tableDataProvider will mark rows as requested but the actual request will not get to the server)
			this.onDisplayedRangeChanged.fire(this.createDisplayRangeChangedEvent());
		}, 500));

		this.updateAutoHeight();
	}

	private createDisplayRangeChangedEvent(dataRequest?: DtoInfiniteItemViewDataRequest) {
		const viewPort = this.grid.getViewport();
		return {
			startIndex: Math.max(0, (viewPort.top - ROW_LOOKAHAED) * this.dataProvider.getItemsPerRow()),
			length: (viewPort.bottom - viewPort.top + ROW_LOOKAHAED * 2) * this.dataProvider.getItemsPerRow(),
			displayedRecordIds: this.getCurrentlyDisplayedRecordIds(),
			dataRequest: dataRequest
		};
	}

	private getCurrentlyDisplayedRecordIds() {
		const viewPort = this.grid.getViewport();
		const currentlyDisplayedRecordIds: any[] = [];
		for (let i = Math.max(0, viewPort.top - ROW_LOOKAHAED); i <= viewPort.bottom + ROW_LOOKAHAED; i++) {
			const row = this.dataProvider.getItem(i);
			if (row != null) {
				row.forEach(item => item != null && currentlyDisplayedRecordIds.push(item.id));
			}
		}
		return currentlyDisplayedRecordIds;
	}

	private calculateItemWidthInPixels(includeMargins: boolean) {
		if (this.itemWidth < 0) {
			console.error("itemWidth < 0 not allowed! Displaying full-width!");
			this.itemWidth = 0;
		}

		let availableWidth = this.getAvailableWidth();
		if (this.itemWidth === 0) {
			return availableWidth;
		} else if (this.itemWidth < 1) {
			let widthIncludingMargins = availableWidth * this.itemWidth + this.config.horizontalItemMargin * 2;
			if (widthIncludingMargins > availableWidth) {
				return availableWidth - this.config.horizontalItemMargin * 2;
			} else {
				return availableWidth * this.itemWidth + (includeMargins ? this.config.horizontalItemMargin * 2 : 0);
			}
			return Math.min(widthIncludingMargins, availableWidth);
		} else if (this.itemWidth >= 1) {
			return Math.min(this.itemWidth + (includeMargins ? this.config.horizontalItemMargin * 2 : 0), availableWidth);
		}
	}

	@executeWhenFirstDisplayed()
	public addData(startIndex: number,
				   data: any[],
				   totalNumberOfRecords: number,
				   clearTableCache: boolean) {
		if (clearTableCache) {
			this.dataProvider.clear();
		}

		if (totalNumberOfRecords != this.dataProvider.getLength()) {
			this.dataProvider.setTotalNumberOfRecords(totalNumberOfRecords);
		}

		this.dataProvider.setData(startIndex, data);

		this.redrawGridContents();
	}

	@loadSensitiveThrottling(100, 7, 2000)
	private redrawGridContents() {
		this.grid.updateRowCount();
		this.updateAutoHeight();
		this.grid.invalidateAllRows();
		this.grid.render();
		this.grid.resizeCanvas();
	}

	private updateAutoHeight() {
		if (this.config.autoHeight) {
			let computedStyle = getComputedStyle(this.$mainDomElement);
			let newHeight = Math.min(parseFloat(computedStyle.maxHeight) || Number.MAX_SAFE_INTEGER, this.dataProvider.getLength() * this.config.rowHeight
				+ parseFloat(computedStyle.paddingTop) + parseFloat(computedStyle.paddingBottom)) + "px";
			if (newHeight != this.$grid.style.height) {
				this.$grid.style.height = newHeight;
			}
		}
	}

	@executeWhenFirstDisplayed()
	removeData(ids: number[]): void {
		this.dataProvider.removeData(ids);
		this.redrawGridContents();
		this.dataProvider.ensureData(this.grid.getViewport().top, this.grid.getViewport().bottom);
	}

	@executeWhenFirstDisplayed(true)
	public onResize(): void {
		this.dataProvider.setAvailableWidth(this.getAvailableWidth());
		this.dataProvider.setItemWidthIncludingMargin(this.calculateItemWidthInPixels(true));
		this.redrawGridContents();
		this.dataProvider.ensureData(this.grid.getViewport().top, this.grid.getViewport().bottom);
	}

	private getAvailableWidth() {
		return this.getWidth() - Constants.SCROLLBAR_WIDTH;
	}

	doGetMainElement(): HTMLElement {
		return this.$mainDomElement;
	}

	private updateStyles() {
		this.getMainElement().append(parseHtml(`<style>
            .grid-${this.uuid} .line-wrapper {
                 align-items: ${itemCssStringsAlignItems[this.verticalItemAlignment]};
                 justify-content: ${itemCssStringsJustification[this.itemJustification]};
            }
            .grid-${this.uuid} .item-wrapper {
               margin: 0 ${this.horizontalItemMargin}px;
            }
            </style>`));
	}

	@executeWhenFirstDisplayed(true)
	setHorizontalItemMargin(horizontalItemMargin: number): void {
		this.horizontalItemMargin = horizontalItemMargin;
		this.dataProvider.setItemWidthIncludingMargin(this.calculateItemWidthInPixels(true));
		if (this.grid) {
			this.redrawGridContents();
		}
		this.updateStyles();
	}

	setVerticalItemAlignment(verticalItemAlignment: UiVerticalItemAlignment): void {
		this.verticalItemAlignment = verticalItemAlignment;
		this.updateStyles();
	}

	setItemTemplate(itemTemplate: Template): void {
		this.itemTemplateRenderer = itemTemplate;
		if (this.grid) {
			this.redrawGridContents();
		}
	}

	@executeWhenFirstDisplayed(true)
	setItemWidth(itemWidth: number): void {
		this.itemWidth = itemWidth;
		this.dataProvider.setItemWidthIncludingMargin(this.calculateItemWidthInPixels(true));
		if (this.grid) {
			this.redrawGridContents();
		}
	}

	setItemJustification(itemJustification: UiItemJustification): void {
		this.itemJustification = itemJustification;
		this.updateStyles();
	}

	setContextMenuContent(requestId: number, component: UiComponent): void {
		this.contextMenu.setContent(component, requestId);
	}

	closeContextMenu(requestId: number): void {
		this.contextMenu.close(requestId);
	}

}


