/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {Constants, deepEquals, insertAfter, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {
	UiInfiniteItemView2_ContextMenuRequestedEvent,
	UiInfiniteItemView2_DisplayedRangeChangedEvent,
	UiInfiniteItemView2_ItemClickedEvent,
	UiInfiniteItemView2CommandHandler,
	UiInfiniteItemView2Config,
	UiInfiniteItemView2EventSource
} from "../generated/UiInfiniteItemView2Config";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {UiItemJustification} from "../generated/UiItemJustification";
import {UiIdentifiableClientRecordConfig} from "../generated/UiIdentifiableClientRecordConfig";
import {UiVerticalItemAlignment} from "../generated/UiVerticalItemAlignment";
import {ContextMenu} from "./micro-components/ContextMenu";
import {UiComponent} from "./UiComponent";
import {throttledMethod} from "./util/throttle";
import {createUiInfiniteItemViewDataRequestConfig, UiInfiniteItemViewDataRequestConfig} from "../generated/UiInfiniteItemViewDataRequestConfig";
import {debounce, debouncedMethod, DebounceMode} from "./util/debounce";
import {UiHorizontalElementAlignment} from "../generated/UiHorizontalElementAlignment";
import {UiVerticalElementAlignment} from "../generated/UiVerticalElementAlignment";

type RenderedItem = {
	item: UiIdentifiableClientRecordConfig,
	$element: HTMLElement,
	$wrapper: HTMLElement,
	position: [number, number]
};

class UiInfiniteItemView2DataProvider {

	data: UiIdentifiableClientRecordConfig[] = [];
	private totalNumberOfRecords = 0;

	constructor(private dataRequestCallback: (fromIndex: number, toIndex: number) => void) {
	}

	public setTotalNumberOfRecords(totalNumberOfRecords: number) {
		if (this.data.length > totalNumberOfRecords) {
			this.data.length = totalNumberOfRecords;
		}
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public getTotalNumberOfRecords() {
		return this.totalNumberOfRecords;
	}

	public getItems(startIndex: number, endIndex: number, requestMissing: boolean) {
		if (requestMissing) {
			console.log(`Get items: ${startIndex} - ${endIndex}`)
		}
		if (requestMissing) {
			this.ensureData(startIndex, endIndex);
		}
		return this.data.slice(startIndex, endIndex);
	}

	public ensureData(startIndex: number, endIndex: number) {
		if (endIndex <= startIndex) {
			return;
		}
		endIndex = Math.min(endIndex, this.totalNumberOfRecords);
		while (startIndex < endIndex && this.data[startIndex] !== undefined) {
			startIndex++;
		}
		while (startIndex < endIndex && this.data[endIndex - 1] !== undefined) {
			endIndex--;
		}
		if (startIndex == endIndex) {
			return;
		}
		for (var i = startIndex; i < endIndex; i++) {
			if (this.data[i] === undefined) {
				this.data[i] = null; // null indicates a 'requested but not available yet'
			}
		}
		this.requestRange(startIndex, endIndex);
	}

	private requestRange = (() => {
		let rangesToBeRequested: [number, number][] = [];

		let mergeAndRequestDebounced = debounce(() => {
			console.log(`rangesToBeRequested: ${JSON.stringify(rangesToBeRequested)}`);
			rangesToBeRequested.sort((a, b) => a[0] - b[0]);
			for (let i = 0; i < rangesToBeRequested.length - 1;) {
				let range1 = rangesToBeRequested[i];
				let range2 = rangesToBeRequested[i + 1];
				if (range1[1] >= range2[0]) {
					range1[1] = range2[0];
					rangesToBeRequested.splice(i + 1, 1);
				} else {
					i++
				}
			}
			for (let range of rangesToBeRequested) {
				this.dataRequestCallback(range[0], range[1]);
			}
			rangesToBeRequested = [];
		}, 200, DebounceMode.BOTH);

		return (startIndex: number, endIndex: number) => {
			rangesToBeRequested.push([startIndex, endIndex]);
			mergeAndRequestDebounced();
		}
	})();

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
export var itemCssStringsJustification = {
	[UiHorizontalElementAlignment.LEFT]: "flex-start",
	[UiHorizontalElementAlignment.RIGHT]: "flex-end",
	[UiHorizontalElementAlignment.CENTER]: "center",
	[UiHorizontalElementAlignment.STRETCH]: "stretch",
};
export var itemCssStringsAlignItems = {
	[UiVerticalElementAlignment.TOP]: "flex-start",
	[UiVerticalElementAlignment.CENTER]: "center",
	[UiVerticalElementAlignment.BOTTOM]: "flex-end",
	[UiVerticalElementAlignment.STRETCH]: "stretch"
};

export class UiInfiniteItemView2 extends AbstractUiComponent<UiInfiniteItemView2Config> implements UiInfiniteItemView2CommandHandler, UiInfiniteItemView2EventSource {


	public readonly onDisplayedRangeChanged: TeamAppsEvent<UiInfiniteItemView2_DisplayedRangeChangedEvent> = new TeamAppsEvent(this);
	public readonly onItemClicked: TeamAppsEvent<UiInfiniteItemView2_ItemClickedEvent> = new TeamAppsEvent(this);
	public readonly onContextMenuRequested: TeamAppsEvent<UiInfiniteItemView2_ContextMenuRequestedEvent> = new TeamAppsEvent(this);

	private $mainDomElement: HTMLElement;
	private $grid: HTMLElement;
	private $styles: HTMLStyleElement;
	private dataProvider: UiInfiniteItemView2DataProvider;
	private itemTemplateRenderer: Renderer;
	private contextMenu: ContextMenu;

	constructor(config: UiInfiniteItemView2Config, context: TeamAppsUiContext) {
		super(config, context);
		this.dataProvider = new UiInfiniteItemView2DataProvider((fromIndex, endIndex) => {
			let eventObject = this.createDisplayRangeChangedEvent(createUiInfiniteItemViewDataRequestConfig(fromIndex, endIndex - fromIndex));
			console.log(`Requesting range: ${eventObject.dataRequest?.startIndex} - ${eventObject.dataRequest?.startIndex + eventObject.dataRequest?.length}; Displayed range: ${eventObject.startIndex} - ${eventObject.startIndex + eventObject.length};`); // Displayed IDs: ${eventObject.displayedRecordIds.join(", ")}`)
			this.onDisplayedRangeChanged.fire(eventObject);
		});
		this.$mainDomElement = parseHtml(`<div id="${config.id}" class="UiInfiniteItemView2 grid-${this._config.id}">
                <div class="grid"></div>
                <style></style>
            </div>`);
		this.$grid = this.$mainDomElement.querySelector<HTMLElement>(":scope .grid");
		this.$styles = this.$mainDomElement.querySelector<HTMLStyleElement>(":scope style");
		this.updateStyles();
		this.setItemTemplate(config.itemTemplate);
		this.dataProvider.setTotalNumberOfRecords(config.totalNumberOfRecords || 0);
		this.contextMenu = new ContextMenu();

		this.$mainDomElement.addEventListener("scroll", ev => {
			this.handleScroll();
		});

		let me = this;
		$(this.getMainElement())
			.on("click contextmenu", ".item-wrapper", function (e: JQueryMouseEventObject) {
				let recordId = parseInt((<Element>this).getAttribute("data-id"));
				me.onItemClicked.fire({
					recordId: recordId,
					isRightMouseButton: e.button === 2,
					isDoubleClick: false
				});
				if (e.button == 2 && !isNaN(recordId) && me._config.contextMenuEnabled) {
					me.contextMenu.open(e as unknown as MouseEvent, requestId => me.onContextMenuRequested.fire({recordId: recordId, requestId}));
				}
			})
			.on("dblclick", ".item-wrapper", function (e: JQueryMouseEventObject) {
				let recordId = parseInt((<Element>this).getAttribute("data-id"));
				me.onItemClicked.fire({
					recordId: recordId,
					isRightMouseButton: e.button === 2,
					isDoubleClick: true
				});
			});
	}

	private renderedItems: RenderedItem[] = [];
	private numberOfPreRenderedLines = 3;

	@debouncedMethod(150, DebounceMode.LATER)
	private handleScroll() {
		this.updateRenderedItems();
		this.onDisplayedRangeChanged.fire(this.createDisplayRangeChangedEvent());
	}

	private getItemsPerRow() {
		let itemsPerRow: number;
		if (this._config.itemWidth <= 0) {
			itemsPerRow = 1;
		} else if (this._config.itemWidth < 1) {
			itemsPerRow = Math.floor(1 / this._config.itemWidth);
		} else {
			itemsPerRow = Math.floor(this.getAvailableWidth() / this._config.itemWidth);
		}
		return itemsPerRow;
	}

	private getVisibleItemRange() {
		const itemsPerRow = this.getItemsPerRow();
		const viewportTop = this.$mainDomElement.scrollTop;
		const viewportBottom = viewportTop + this.getHeight();
		const visibleStartRowIndex = Math.floor(viewportTop / this._config.itemHeight);
		const visibleEndRowIndex = Math.ceil(viewportBottom / this._config.itemHeight); // exclusive
		let startIndex = Math.max(0, visibleStartRowIndex * itemsPerRow);
		let endIndex = Math.min(visibleEndRowIndex * itemsPerRow, this.dataProvider.getTotalNumberOfRecords());
		return [startIndex, endIndex];
	}

	private updateRenderedItems() {
		let itemsPerRow = this.getItemsPerRow();
		let [startIndex, endIndex] = this.getVisibleItemRange();
		if (endIndex - startIndex > 1000) {
			throw new Error(`the number of items to be drawn is unrealistically high: ${endIndex - startIndex}`);
		}
		startIndex = Math.max(0, startIndex - (this.numberOfPreRenderedLines * itemsPerRow));
		endIndex = Math.min(endIndex + (this.numberOfPreRenderedLines * itemsPerRow), this.dataProvider.getTotalNumberOfRecords());
		for (let i = 0; i < Math.min(this.renderedItems.length, this.dataProvider.getTotalNumberOfRecords()); i++) {
			if ((i < startIndex || i >= endIndex) && this.renderedItems[i] != null) {
				this.renderedItems[i].$wrapper.remove();
				this.renderedItems[i] = null;
			}
		}
		const availableWidth = this.getAvailableWidth();
		const itemWidth = this._config.itemWidth <= 0 ? availableWidth : this._config.itemWidth < 1 ? availableWidth * this._config.itemWidth : this._config.itemWidth;
		let items = this.dataProvider.getItems(startIndex, endIndex, true);
		let $lastSeenItemElementWrapper: HTMLElement = null;
		for (let i = startIndex; i < Math.min(endIndex, startIndex + items.length); i++) {
			const item = items[i - startIndex];
			const existingRenderedItem = this.renderedItems[i];
			const positionX = this._config.itemWidth * (i % this.getItemsPerRow());
			const positionY = this._config.itemHeight * Math.floor(i / this.getItemsPerRow());
			if (item == null) { // item not yet loaded
				if (existingRenderedItem != null) {
					existingRenderedItem.$wrapper.remove();
					this.renderedItems[i] = null;
				}
			} else if (existingRenderedItem == null || !deepEquals(existingRenderedItem.item.values, item.values)) {
				let $wrapper = existingRenderedItem != null ? existingRenderedItem.$wrapper : document.createElement("div");
				$wrapper.innerHTML = '';
				let $element = parseHtml(this.itemTemplateRenderer.render(item.values));
				$wrapper.classList.add("item-wrapper");
				$wrapper.setAttribute("data-id", "" + item.id);
				$wrapper.appendChild($element);
				this.renderedItems[i] = {
					item: item,
					$element: $element,
					$wrapper: $wrapper,
					position: [positionX, positionY]
				};
				this.updateItemPosition(this.renderedItems[i], positionX, positionY, itemWidth, this._config.itemHeight);
				if ($lastSeenItemElementWrapper == null) {
					this.$grid.prepend(this.renderedItems[i].$wrapper);
				} else {
					insertAfter(this.renderedItems[i].$wrapper, $lastSeenItemElementWrapper);
				}
			} else if (!deepEquals([positionX, positionY], existingRenderedItem.position)) {
				this.updateItemPosition(existingRenderedItem, positionX, positionY, itemWidth, this._config.itemHeight);
			}
			$lastSeenItemElementWrapper = this.renderedItems[i]?.$wrapper ?? $lastSeenItemElementWrapper;
		}
	}

	private clearRenderedItems() {
		this.renderedItems.forEach(item => item.$wrapper.remove());
		this.renderedItems = [];
	}

	private updateItemPosition(renderedItem: RenderedItem, positionX: number, positionY: number, itemWidth: number, itemHeight: number) {
		renderedItem.$wrapper.style.left = positionX + "px";
		renderedItem.$wrapper.style.top = positionY + "px";
		renderedItem.$wrapper.style.width = itemWidth + "px";
		renderedItem.$wrapper.style.height = itemHeight + "px";
		renderedItem.$wrapper;
		renderedItem.position = [positionX, positionY];
	}

	@throttledMethod(500) // CAUTION: debounce/throttle scrolling without data requests only!!! (otherwise the tableDataProvider will mark rows as requested but the actual request will not get to the server)
	private throttledFireDisplayedRangeChanged() {
		this.onDisplayedRangeChanged.fire(this.createDisplayRangeChangedEvent());
	}

	private createDisplayRangeChangedEvent(dataRequest?: UiInfiniteItemViewDataRequestConfig) {
		let visibleItemRange = this.getVisibleItemRange();
		return {
			startIndex: visibleItemRange[0],
			length: visibleItemRange[1] - visibleItemRange[0],
			displayedRecordIds: this.dataProvider.getItems(visibleItemRange[0], visibleItemRange[1] - visibleItemRange[0], false).filter(item => item != null).map(item => item.id),
			dataRequest: dataRequest
		};
	}

	@executeWhenFirstDisplayed()
	public addData(startIndex: number,
	               data: any[],
	               totalNumberOfRecords: number,
	               clearTableCache: boolean) {
		console.log(`addData: ${startIndex} - ${startIndex + data.length}`);
		if (clearTableCache) {
			this.dataProvider.clear();
		}
		this.dataProvider.setTotalNumberOfRecords(totalNumberOfRecords);
		this.dataProvider.setData(startIndex, data);
		this.$grid.style.height = this._config.itemHeight * Math.ceil(totalNumberOfRecords / this.getItemsPerRow()) + "px";
		this.updateRenderedItems();
	}

	@executeWhenFirstDisplayed()
	removeData(ids: number[]): void {
		let deleteIndexes = ids.map(id => this.dataProvider.data.findIndex(item => item != null && item.id == id));
		deleteIndexes.sort((a, b) => a - b);
		let deleteRanges: [number, number][] = [];
		let currentDeleteRange: [number, number] = null;
		for (let i = 0; i < deleteIndexes.length; i++) {
			if (currentDeleteRange == null || deleteIndexes[i] > currentDeleteRange[1]) {
				currentDeleteRange = [deleteIndexes[i], deleteIndexes[i] + 1];
				deleteRanges.push(currentDeleteRange)
			} else {
				currentDeleteRange[1] = deleteIndexes[i] + 1;
			}
		}
		console.log(`removeData: `, JSON.stringify(deleteRanges));
		this.dataProvider.removeData(ids);
		this.updateRenderedItems();
	}

	@executeWhenFirstDisplayed(true)
	public onResize(): void {
		console.log("onResize ", this.getWidth(), this.getHeight());
		this.updateRenderedItems();
	}

	private getAvailableWidth() {
		return this.getWidth() - Constants.SCROLLBAR_WIDTH;
	}

	doGetMainElement(): HTMLElement {
		return this.$mainDomElement;
	}

	private updateStyles() {
		this.$styles.textContent = `
            .grid-${this._config.id} .item-wrapper {
                 align-items: ${itemCssStringsAlignItems[this._config.itemContentVerticalAlignment]};
                 justify-content: ${itemCssStringsJustification[this._config.itemContentHorizontalAlignment]};
            }`;
	}

	setItemTemplate(itemTemplate: UiTemplateConfig): void {
		this.itemTemplateRenderer = this._context.templateRegistry.createTemplateRenderer(itemTemplate);
		this.clearRenderedItems();
		this.updateRenderedItems();
	}

	setItemWidth(itemWidth: number): void {
		this._config.itemWidth = itemWidth;
		this.updateStyles();
		this.updateRenderedItems();
	}

	setItemHeight(itemHeight: number): void {
		this._config.itemHeight = itemHeight;
		this.updateStyles();
		this.updateRenderedItems();
	}

	setHorizontalSpacing(horizontalSpacing: number): void {
		this._config.horizontalSpacing = horizontalSpacing;
		this.updateStyles();
		this.updateRenderedItems();
	}

	setVerticalSpacing(verticalSpacing: number): void {
		this.updateStyles();
		this._config.verticalSpacing = verticalSpacing;
		this.updateRenderedItems();
	}

	setItemContentHorizontalAlignment(itemContentHorizontalAlignment: UiHorizontalElementAlignment): void {
		this._config.itemContentHorizontalAlignment = itemContentHorizontalAlignment;
		this.updateStyles();
		this.updateRenderedItems();
	}

	setItemContentVerticalAlignment(itemContentVerticalAlignment: UiVerticalElementAlignment): void {
		this._config.itemContentVerticalAlignment = itemContentVerticalAlignment;
		this.updateStyles();
		this.updateRenderedItems();
	}

	setRowHorizontalAlignment(rowHorizontalAlignment: UiItemJustification): void {
		this._config.rowHorizontalAlignment = rowHorizontalAlignment;
		this.updateStyles();
		this.updateRenderedItems();
	}

	setContextMenuContent(requestId: number, component: UiComponent): void {
		this.contextMenu.setContent(component, requestId);
	}

	closeContextMenu(requestId: number): void {
		this.contextMenu.close(requestId);
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiInfiniteItemView2", UiInfiniteItemView2);
