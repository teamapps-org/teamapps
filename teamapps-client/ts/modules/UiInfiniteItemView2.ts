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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {addDelegatedEventListener, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {
	UiInfiniteItemView2_ContextMenuRequestedEvent, UiInfiniteItemView2_DisplayedRangeChangedEvent,
	UiInfiniteItemView2_ItemClickedEvent,
	UiInfiniteItemView2CommandHandler,
	UiInfiniteItemView2Config,
	UiInfiniteItemView2EventSource
} from "../generated/UiInfiniteItemView2Config";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {UiItemJustification} from "../generated/UiItemJustification";
import {UiIdentifiableClientRecordConfig} from "../generated/UiIdentifiableClientRecordConfig";
import {ContextMenu} from "./micro-components/ContextMenu";
import {UiComponent} from "./UiComponent";
import {debouncedMethod, DebounceMode} from "./util/debounce";
import {UiHorizontalElementAlignment} from "../generated/UiHorizontalElementAlignment";
import {UiVerticalElementAlignment} from "../generated/UiVerticalElementAlignment";

type RenderedItem = {
	item: UiIdentifiableClientRecordConfig,
	$element: HTMLElement,
	$wrapper: HTMLElement,
	x: number,
	y: number,
	width: number,
	height: number
};

export var cssJustifyContent = {
	[UiHorizontalElementAlignment.LEFT]: "flex-start",
	[UiHorizontalElementAlignment.RIGHT]: "flex-end",
	[UiHorizontalElementAlignment.CENTER]: "center",
	[UiHorizontalElementAlignment.STRETCH]: "stretch",
};
export var cssAlignItems = {
	[UiVerticalElementAlignment.TOP]: "flex-start",
	[UiVerticalElementAlignment.CENTER]: "center",
	[UiVerticalElementAlignment.BOTTOM]: "flex-end",
	[UiVerticalElementAlignment.STRETCH]: "stretch"
};

export class UiInfiniteItemView2 extends AbstractUiComponent<UiInfiniteItemView2Config> implements UiInfiniteItemView2CommandHandler, UiInfiniteItemView2EventSource {

	public readonly onDisplayedRangeChanged: TeamAppsEvent<UiInfiniteItemView2_DisplayedRangeChangedEvent> = new TeamAppsEvent();
	public readonly onItemClicked: TeamAppsEvent<UiInfiniteItemView2_ItemClickedEvent> = new TeamAppsEvent();
	public readonly onContextMenuRequested: TeamAppsEvent<UiInfiniteItemView2_ContextMenuRequestedEvent> = new TeamAppsEvent();

	private $mainDomElement: HTMLElement;
	private $grid: HTMLElement;
	private $styles: HTMLStyleElement;
	private itemTemplateRenderer: Renderer;
	private contextMenu: ContextMenu;

	private renderedRange: [number, number] = [0, 0];
	private renderedIds: number[] = []; // in order
	private renderedItems: Map<number, RenderedItem> = new Map<number, RenderedItem>();
	private totalNumberOfRecords: number = null;

	constructor(config: UiInfiniteItemView2Config, context: TeamAppsUiContext) {
		super(config, context);
		this.$mainDomElement = parseHtml(`<div class="UiInfiniteItemView2 grid-${this._config.id}">
                <div class="grid"></div>
                <style></style>
            </div>`);
		this.$grid = this.$mainDomElement.querySelector<HTMLElement>(":scope .grid");
		this.$styles = this.$mainDomElement.querySelector<HTMLStyleElement>(":scope style");
		this.updateStyles();
		this.setItemTemplate(config.itemTemplate);
		this.setItemPositionAnimationTime(config.itemPositionAnimationTime);
		this.contextMenu = new ContextMenu();

		this.$mainDomElement.addEventListener("scroll", ev => {
			this.requestDataIfNeededDebounced();
		});
		this.$mainDomElement.addEventListener("wheel", ev => {
			if (Math.abs(ev.deltaY) < 5) {
				this.requestDataIfNeededDebounced();
			}
		});

		addDelegatedEventListener(this.getMainElement(), ".item-wrapper", ["click"], (element, ev) => {
			let recordId = parseInt(element.getAttribute("data-id"));
			this.onItemClicked.fire({
				recordId: recordId,
				isDoubleClick: false
			});
		});
		addDelegatedEventListener(this.getMainElement(), ".item-wrapper", "contextmenu", (element, ev) => {
			let recordId = parseInt(element.getAttribute("data-id"));
			if (!isNaN(recordId) && this._config.contextMenuEnabled) {
				this.contextMenu.open(ev, requestId => this.onContextMenuRequested.fire({
					recordId: recordId,
					requestId
				}));
			}
		});
		addDelegatedEventListener(this.getMainElement(), ".item-wrapper", ["dblclick"], (element, ev) => {
			let recordId = parseInt(element.getAttribute("data-id"));
			this.onItemClicked.fire({
				recordId: recordId,
				isDoubleClick: true
			});
		});
	}

	setItemPositionAnimationTime(animationMillis: number): void {
		this.$mainDomElement.style.setProperty("--item-position-animation-time", animationMillis + "ms");
	}

	@debouncedMethod(150, DebounceMode.BOTH)
	private requestDataIfNeededDebounced() {
		this.requestDataIfNeeded();
	}

	private requestDataIfNeeded() {
		let visibleItemRange = this.getVisibleItemRange();
		let visibleItemRangeLength = visibleItemRange[1] - visibleItemRange[0];
		if (this._config.visible && visibleItemRangeLength === 0) {
			visibleItemRangeLength = this.getItemsPerRow(); // even if this component has zero height, it should at least query one row in order to be able to auto-size!
		}
		const newRenderedRange: [number, number] = [
			Math.max(0, visibleItemRange[0] - visibleItemRangeLength),
			visibleItemRange[1] + visibleItemRangeLength
		];
		if (newRenderedRange[0] != this.renderedRange[0] || newRenderedRange[1] != this.renderedRange[1]) {
			this.renderedRange = newRenderedRange;
			let eventObject = {
				startIndex: newRenderedRange[0],
				length: newRenderedRange[1] - newRenderedRange[0] // send the uncut value, so the server will send new records if some are added user scrolled to the end
			};
			this.logger.debug("onRenderedItemRangeChanged ", eventObject);
			this.onDisplayedRangeChanged.fire(eventObject);
		}
	}

	private getVisibleItemRange() {
		const itemsPerRow = this.getItemsPerRow();
		const viewportTop = this.$mainDomElement.scrollTop;
		const visibleStartRowIndex = Math.floor(viewportTop / this._config.itemHeight);
		const numberOfVisibleRows = Math.ceil(this.getHeight() / this._config.itemHeight); // exclusive
		let startIndex = Math.max(0, visibleStartRowIndex * itemsPerRow);
		let endIndex = startIndex + numberOfVisibleRows * itemsPerRow;
		return [startIndex, endIndex];
	}

	private getItemsPerRow() {
		let itemsPerRow: number;
		if (this._config.itemWidth <= 0) {
			itemsPerRow = 1;
		} else if (this._config.itemWidth < 1) {
			itemsPerRow = Math.floor(1 / this._config.itemWidth);
		} else {
			itemsPerRow = Math.floor(this.$mainDomElement.clientWidth / this._config.itemWidth);
		}
		return itemsPerRow;
	}

	private updateItemPositions() {
		const itemsPerRow = this.getItemsPerRow();
		const availableWidth = this.$mainDomElement.clientWidth;
		const itemWidth = this._config.itemWidth <= 0 ? availableWidth : this._config.itemWidth < 1 ? availableWidth * this._config.itemWidth : this._config.itemWidth;
		const itemHeight = this._config.itemHeight;
		for (let i = 0; i < this.renderedIds.length; i++) {
			const absoluteIndex = i + this.renderedRange[0];
			const item = this.renderedItems.get(this.renderedIds[i]);
			const positionX = itemWidth * (absoluteIndex % itemsPerRow);
			const positionY = itemHeight * Math.floor(absoluteIndex / itemsPerRow);
			this.updateItemPosition(item, positionX, positionY, itemWidth, itemHeight, absoluteIndex);
		}
	}

	private createRenderedItem(item: UiIdentifiableClientRecordConfig): RenderedItem {
		let $wrapper = document.createElement("div");
		let $element = parseHtml(this.itemTemplateRenderer.render(item.values));
		$wrapper.classList.add("item-wrapper");
		$wrapper.setAttribute("data-id", "" + item.id);
		$wrapper.appendChild($element);
		return {
			item: item,
			$element: $element,
			$wrapper: $wrapper,
			x: -1,
			y: -1,
			width: -1,
			height: -1
		};
	}

	private updateItemPosition(renderedItem: RenderedItem, positionX: number, positionY: number, itemWidth: number, itemHeight: number, absoluteIndex: number) {
		if (positionX !== renderedItem.x
			|| positionY !== renderedItem.y
			|| itemWidth !== renderedItem.width
			|| itemWidth !== renderedItem.height
		) {
			renderedItem.$wrapper.style.left = positionX + "px";
			renderedItem.$wrapper.style.top = positionY + "px";
			renderedItem.$wrapper.style.width = itemWidth + "px";
			renderedItem.$wrapper.style.height = itemHeight + "px";
			renderedItem.x = positionX;
			renderedItem.y = positionY;
			renderedItem.width = itemWidth;
			renderedItem.height = itemHeight;
		}
	}

	private rerenderAllItems() {
		if (this.totalNumberOfRecords != null) { // data was ever received
			let items = [...this.renderedItems.values()].map(renderdItem => renderdItem.item);
			this.renderedItems.forEach(renderedItem => renderedItem.$wrapper.remove());
			this.renderedItems.clear();
			this.setData(this.renderedRange[0], items.map(item => item.id), items, this.totalNumberOfRecords);
		}
	}

	setData(startIndex: number, recordIds: number[], newRecords: UiIdentifiableClientRecordConfig[], totalNumberOfRecords: number): void {
		this.logger.debug("got data ", startIndex, recordIds.length, newRecords.length, totalNumberOfRecords);
		this.totalNumberOfRecords = totalNumberOfRecords;
		this.updateGridHeight();
		let recordIdsAsSet: Set<number> = new Set(recordIds);
		const existingItems = [...this.renderedItems.values()];
		for (let existingItem of existingItems) {
			if (!recordIdsAsSet.has(existingItem.item.id)) {
				existingItem.$wrapper.remove();
				this.renderedItems.delete(existingItem.item.id);
			}
		}
		newRecords.forEach(newRecord => {
			let renderedItem = this.createRenderedItem(newRecord);
			this.renderedItems.set(newRecord.id, renderedItem);
			this.$grid.prepend(renderedItem.$wrapper);
		});
		this.renderedIds = recordIds;
		this.updateItemPositions()
	}

	private updateGridHeight() {
		this.$grid.style.height = this._config.itemHeight * Math.ceil(this.totalNumberOfRecords / this.getItemsPerRow()) + "px";
	}

	@executeWhenFirstDisplayed(true)
	public onResize(): void {
		this.logger.debug("onResize ", this.getWidth(), this.getHeight());
		this.updateGridHeight();
		this.updateItemPositions();
		this.requestDataIfNeededDebounced();
	}

	doGetMainElement(): HTMLElement {
		return this.$mainDomElement;
	}

	private updateStyles() {
		this.$styles.textContent = `
            .grid-${this._config.id} .item-wrapper {
                 align-items: ${cssAlignItems[this._config.itemContentVerticalAlignment]};
                 justify-content: ${cssJustifyContent[this._config.itemContentHorizontalAlignment]};
            }
            .grid-${this._config.id} .item-wrapper > * {
                 flex: ${this._config.itemContentHorizontalAlignment == UiHorizontalElementAlignment.STRETCH ? "1 1 auto" : "0 1 auto"};
            }`;
	}

	setItemTemplate(itemTemplate: UiTemplateConfig): void {
		this.itemTemplateRenderer = this._context.templateRegistry.createTemplateRenderer(itemTemplate);
		this.rerenderAllItems();
	}

	setItemWidth(itemWidth: number): void {
		this._config.itemWidth = itemWidth;
		this.updateStyles();
		this.requestDataIfNeeded();
		this.updateItemPositions();
	}

	setItemHeight(itemHeight: number): void {
		this._config.itemHeight = itemHeight;
		this.updateStyles();
		this.requestDataIfNeeded();
		this.updateItemPositions();
	}

	setHorizontalSpacing(horizontalSpacing: number): void {
		this._config.horizontalSpacing = horizontalSpacing;
		this.updateStyles();
		this.requestDataIfNeeded();
		this.updateItemPositions();
	}

	setVerticalSpacing(verticalSpacing: number): void {
		this.updateStyles();
		this._config.verticalSpacing = verticalSpacing;
		this.requestDataIfNeeded();
		this.updateItemPositions();
	}

	setItemContentHorizontalAlignment(itemContentHorizontalAlignment: UiHorizontalElementAlignment): void {
		this._config.itemContentHorizontalAlignment = itemContentHorizontalAlignment;
		this.updateStyles();
		this.requestDataIfNeeded();
		this.updateItemPositions();
	}

	setItemContentVerticalAlignment(itemContentVerticalAlignment: UiVerticalElementAlignment): void {
		this._config.itemContentVerticalAlignment = itemContentVerticalAlignment;
		this.updateStyles();
		this.requestDataIfNeeded();
		this.updateItemPositions();
	}

	setRowHorizontalAlignment(rowHorizontalAlignment: UiItemJustification): void {
		this._config.rowHorizontalAlignment = rowHorizontalAlignment;
		this.updateStyles();
		this.requestDataIfNeeded();
		this.updateItemPositions();
	}

	setContextMenuContent(requestId: number, component: UiComponent): void {
		this.contextMenu.setContent(component, requestId);
	}

	closeContextMenu(requestId: number): void {
		this.contextMenu.close(requestId);
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiInfiniteItemView2", UiInfiniteItemView2);
