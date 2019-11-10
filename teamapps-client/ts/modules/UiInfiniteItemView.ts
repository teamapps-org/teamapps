/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {Constants, generateUUID, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {TableDataProviderItem} from "./table/TableDataProvider";
import {
	UiInfiniteItemView_DataRequestEvent,
	UiInfiniteItemView_ItemClickedEvent,
	UiInfiniteItemViewCommandHandler,
	UiInfiniteItemViewConfig,
	UiInfiniteItemViewEventSource
} from "../generated/UiInfiniteItemViewConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {itemCssStringsAlignItems, itemCssStringsJustification} from "./UiItemView";
import {UiItemJustification} from "../generated/UiItemJustification";
import {UiIdentifiableClientRecordConfig} from "../generated/UiIdentifiableClientRecordConfig";
import {UiVerticalItemAlignment} from "../generated/UiVerticalItemAlignment";

///<reference types="slickgrid"/>

class UiInfiniteItemViewDataProvider implements Slick.DataProvider<UiIdentifiableClientRecordConfig> {

	private availableWidth: number;
	private static ROW_LOOKAHAED = 20;
	private timerId: number = null;

	private totalNumberOfRecords = 0;

	constructor(private data: UiIdentifiableClientRecordConfig[], private itemWidthIncludingMargin: number, private dataRequestCallback: (from: number, length: number) => void) {
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

	private getItemsPerRow(): number {
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
		let from = Math.max(firstVisibleRowIndex - UiInfiniteItemViewDataProvider.ROW_LOOKAHAED, 0) * itemsPerRow;
		let to = (lastVisibleRowIndex + UiInfiniteItemViewDataProvider.ROW_LOOKAHAED) * itemsPerRow;
		if (this.data.length) {
			to = Math.min(to, this.data.length - 1);
		}

		while (this.data[from] !== undefined && from < to) {
			from++;
		}
		while (this.data[to] !== undefined && from < to) {
			to--;
		}

		if (firstVisibleRowIndex * itemsPerRow > to || (lastVisibleRowIndex + 1) * itemsPerRow < from) { // not really necessary to load anything
			return;
		}

		if (from == to && this.data[to] !== undefined) {
			return;
		}

		if (this.timerId != null) {
			clearTimeout(this.timerId);
		}

		this.timerId = window.setTimeout(() => {
			for (var i = from; i <= to; i++) {
				this.data[i] = null; // null indicates a 'requested but not available yet'
			}

			let length = to - from + 1;
			this.dataRequestCallback(from, length);
		}, 100);
	}

	public clear() {
		this.data.length = 0;
	}

	setData(startIndex: number, data: any[]) {
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

export class UiInfiniteItemView extends AbstractUiComponent<UiInfiniteItemViewConfig> implements UiInfiniteItemViewCommandHandler, UiInfiniteItemViewEventSource {

	public readonly onDataRequest: TeamAppsEvent<UiInfiniteItemView_DataRequestEvent> = new TeamAppsEvent<UiInfiniteItemView_DataRequestEvent>(this);
	public readonly onItemClicked: TeamAppsEvent<UiInfiniteItemView_ItemClickedEvent> = new TeamAppsEvent<UiInfiniteItemView_ItemClickedEvent>(this);

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

	constructor(config: UiInfiniteItemViewConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.uuid = generateUUID();
		this.$mainDomElement = parseHtml(`<div id="${config.id}" class="UiInfiniteItemView grid-${this.uuid}">
                <div class="slickgrid"></div>
            </div>`);
		this.$grid = this.$mainDomElement.querySelector<HTMLElement>(":scope .slickgrid");
		this.setItemTemplate(config.itemTemplate);
		this.itemWidth = config.itemWidth;
		this.horizontalItemMargin = config.horizontalItemMargin;
		this.itemJustification = config.itemJustification;
		this.verticalItemAlignment = config.verticalItemAlignment;
		this.dataProvider = new UiInfiniteItemViewDataProvider(config.data || [], 10 /*cannot know item width until component width is known*/, (fromIndex, length) => {
			this.onDataRequest.fire({
				startIndex: fromIndex,
				length: length
			});
		});
		if (config.totalNumberOfRecords) {
			this.dataProvider.setTotalNumberOfRecords(config.totalNumberOfRecords);
		}
		this.createGrid();

		let me = this;
		$(this.getMainElement())
			.on("click contextmenu", ".item-wrapper", function (e: JQueryMouseEventObject) {
				let recordId = parseInt((<Element>this).getAttribute("data-id"));
				me.onItemClicked.fire({
					recordId: recordId,
					isRightMouseButton: e.button === 2,
					isDoubleClick: false
				});
			})
			.on("dblclick", ".item-wrapper", function (e: JQueryMouseEventObject) {
				let recordId = parseInt((<Element>this).getAttribute("data-id"));
				me.onItemClicked.fire({
					recordId: recordId,
					isRightMouseButton: e.button === 2,
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
					html += `<div class="item-wrapper" data-id="${record.id}" style="width: ${this.calculateItemWidthInPixels()}px;">${this.itemTemplateRenderer.render(record.values)}</div>`;
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
			rowHeight: this._config.rowHeight,
			enableTextSelectionOnCells: false,
			editable: false,
			enableAddRow: false
		};

		this.dataProvider.setAvailableWidth(this.$mainDomElement.offsetWidth - Constants.SCROLLBAR_WIDTH);

		this.grid = new Slick.Grid(this.$grid, this.dataProvider, columns, options);

		this.grid.onViewportChanged.subscribe((e, args) => {
			this.dataProvider.ensureData(this.grid.getViewport().top, this.grid.getViewport().bottom);
		});

		this.updateAutoHeight();
	}

	private calculateItemWidthInPixels() {
		if (this.itemWidth < 0) {
			console.error("itemWidth < 0 not allowed! Displaying full-width!");
			this.itemWidth = 0;
		}

		if (this.itemWidth === 0) {
			return this.getWidth() - Constants.SCROLLBAR_WIDTH - 1 /*TODO remove -1*/;
		} else if (this.itemWidth < 1) {
			return Math.min(this.getWidth() * this.itemWidth + this._config.horizontalItemMargin - Constants.SCROLLBAR_WIDTH - 1 /*TODO remove -1*/, this.getWidth() - Constants.SCROLLBAR_WIDTH - 1 /*TODO remove -1*/);
		} else if (this.itemWidth >= 1) {
			return Math.min(this.itemWidth + this._config.horizontalItemMargin, this.getWidth() - Constants.SCROLLBAR_WIDTH - 1 /*TODO remove -1*/);
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

		this.dataProvider.setData(startIndex, data);

		this.grid.invalidateAllRows();

		if (totalNumberOfRecords != this.dataProvider.getLength()) {
			this.dataProvider.setTotalNumberOfRecords(totalNumberOfRecords);
			this.grid.updateRowCount();
			this.updateAutoHeight();
		}

		this.grid.render();

		// clearTimeout(this.loadingIndicatorFadeInTimer);
		// this._$loadingIndicator.fadeOut();

		this.grid.resizeCanvas();
	}

	private updateAutoHeight() {
		if (this._config.autoHeight) {
			let computedStyle = getComputedStyle(this.$mainDomElement);
			this.$grid.style.height = Math.min(parseFloat(computedStyle.maxHeight) || Number.MAX_SAFE_INTEGER, this.dataProvider.getLength() * this._config.rowHeight
				+ parseFloat(computedStyle.paddingTop) + parseFloat(computedStyle.paddingBottom)) + "px";
		}
	}

	@executeWhenFirstDisplayed()
	removeData(ids: number[]): void {
		this.dataProvider.removeData(ids);
		this.grid.invalidateAllRows();
		this.grid.resizeCanvas();
		this.dataProvider.ensureData(this.grid.getViewport().top, this.grid.getViewport().bottom);
	}

	@executeWhenFirstDisplayed(true)
	public onResize(): void {
		this.logger.debug(this.$mainDomElement.offsetWidth - Constants.SCROLLBAR_WIDTH);
		this.dataProvider.setAvailableWidth(this.$mainDomElement.offsetWidth - Constants.SCROLLBAR_WIDTH);
		this.dataProvider.setItemWidthIncludingMargin(this.calculateItemWidthInPixels());
		this.grid.invalidateAllRows();
		this.grid.resizeCanvas();
		this.dataProvider.ensureData(this.grid.getViewport().top, this.grid.getViewport().bottom);
	}


	destroy(): void {
		// nothing to do...
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
		this.dataProvider.setItemWidthIncludingMargin(this.calculateItemWidthInPixels());
		if (this.grid) {
			this.grid.invalidateAllRows();
			this.grid.updateRowCount();
			this.grid.render();
		}
		this.updateStyles();
	}

	setVerticalItemAlignment(verticalItemAlignment: UiVerticalItemAlignment): void {
		this.verticalItemAlignment = verticalItemAlignment;
		this.updateStyles();
	}

	setItemTemplate(itemTemplate: UiTemplateConfig): void {
		this.itemTemplateRenderer = this._context.templateRegistry.createTemplateRenderer(itemTemplate);
		if (this.grid) {
			this.grid.invalidateAllRows();
			this.grid.render();
		}
	}

	@executeWhenFirstDisplayed(true)
	setItemWidth(itemWidth: number): void {
		this.itemWidth = itemWidth;
		this.dataProvider.setItemWidthIncludingMargin(this.calculateItemWidthInPixels());
		if (this.grid) {
			this.grid.invalidateAllRows();
			this.grid.updateRowCount();
			this.grid.render();
		}
	}

	setItemJustification(itemJustification: UiItemJustification): void {
		this.itemJustification = itemJustification;
		this.updateStyles();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiInfiniteItemView", UiInfiniteItemView);
