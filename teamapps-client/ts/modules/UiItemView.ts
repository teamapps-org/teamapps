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

import {UiItemViewItemGroupConfig} from "../generated/UiItemViewItemGroupConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {DEFAULT_TEMPLATES, trivialMatch, TrivialTreeBox} from "trivial-components";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {generateUUID, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiItemView_ItemClickedEvent, UiItemViewCommandHandler, UiItemViewConfig, UiItemViewEventSource} from "../generated/UiItemViewConfig";
import {UiItemViewFloatStyle} from "../generated/UiItemViewFloatStyle";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {isGridTemplate} from "./TemplateRegistry";
import {UiItemJustification} from "../generated/UiItemJustification";
import {UiItemViewItemBackgroundMode} from "../generated/UiItemViewItemBackgroundMode";
import * as log from "loglevel";
import {UiIdentifiableClientRecordConfig} from "../generated/UiIdentifiableClientRecordConfig";
import {UiVerticalItemAlignment} from "../generated/UiVerticalItemAlignment";

export var itemCssStringsJustification = {
	[UiItemJustification.LEFT]: "flex-start",
	[UiItemJustification.RIGHT]: "flex-end",
	[UiItemJustification.CENTER]: "center",
	[UiItemJustification.SPACE_AROUND]: "space-around",
	[UiItemJustification.SPACE_BETWEEN]: "space-between"
};
export var itemCssStringsAlignItems = {
	[UiVerticalItemAlignment.TOP]: "flex-start",
	[UiVerticalItemAlignment.CENTER]: "center",
	[UiVerticalItemAlignment.BOTTOM]: "flex-end",
	[UiVerticalItemAlignment.STRETCH]: "stretch"
};

export class UiItemView extends AbstractUiComponent<UiItemViewConfig> implements UiItemViewCommandHandler, UiItemViewEventSource {

	public readonly onItemClicked: TeamAppsEvent<UiItemView_ItemClickedEvent> = new TeamAppsEvent<UiItemView_ItemClickedEvent>(this);

	private $itemView: HTMLElement;
	private groupHeaderTemplateRenderer: Renderer;
	private groupsByGroupId: { [index: string]: ItemGroup } = {};
	private filterString: string = "";

	constructor(config: UiItemViewConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$itemView = parseHtml('<div class="UiItemView"></div>');
		this.$itemView.style.padding = config.verticalPadding + "px " + config.horizontalPadding + "px";
		if (config.groupHeaderTemplate) {
			this.groupHeaderTemplateRenderer = context.templateRegistry.createTemplateRenderer(config.groupHeaderTemplate, null);
		}
		this.$itemView.classList.add("background-mode-" + UiItemViewItemBackgroundMode[config.itemBackgroundMode].toLowerCase());

		config.itemGroups.forEach(group => {
			this.addItemGroup(group);
		});

		this.setFilter(config.filter);
	}

	public getMainDomElement(): HTMLElement {
		return this.$itemView;
	}

	public addItemGroup(itemGroupConfig: UiItemViewItemGroupConfig): ItemGroup {
		if (this.groupsByGroupId[itemGroupConfig.id]) {
			this.removeItemGroup(itemGroupConfig.id);
		}
		const itemGroup = this.createItemGroup(itemGroupConfig);
		this.groupsByGroupId[itemGroupConfig.id] = itemGroup;
		this.$itemView.appendChild(itemGroup.getMainDomElement());
		return itemGroup;
	}

	private createItemGroup(itemGroupConfig: UiItemViewItemGroupConfig) {
		const itemGroup = new ItemGroup(this, this._context, itemGroupConfig, this.groupHeaderTemplateRenderer);
		itemGroup.onItemClicked.addListener(item => this.onItemClicked.fire({
			groupId: itemGroupConfig.id,
			itemId: item.id
		}));
		itemGroup.getMainDomElement().style.paddingBottom = this._config.groupSpacing + "px";
		itemGroup.setFilter(this.filterString);
		return itemGroup;
	}

	public refreshItemGroup(itemGroupConfig: UiItemViewItemGroupConfig): void {
		let oldGroup = this.groupsByGroupId[itemGroupConfig.id];
		if (!oldGroup) {
			this.logger.error(`Could not refresh non-existing group "${itemGroupConfig.id}"!`);
			return;
		}
		const newGroup = this.createItemGroup(itemGroupConfig);
		this.groupsByGroupId[itemGroupConfig.id] = newGroup;
		oldGroup.getMainDomElement().parentElement.insertBefore(newGroup.getMainDomElement(), oldGroup.getMainDomElement());
		oldGroup.getMainDomElement().remove();
	}

	public removeItemGroup(groupId: string): void {
		const itemGroup = this.groupsByGroupId[groupId];
		itemGroup.getMainDomElement().remove();
		delete this.groupsByGroupId[groupId];
	}

	public addItem(groupId: string, item: UiIdentifiableClientRecordConfig): void {
		const itemGroup = this.groupsByGroupId[groupId];
		if (!itemGroup) {
			this.logger.error(`Cannot find group ${groupId} in UiItemView ` + this._config.id);
			return;
		}
		itemGroup.addItem(item);
	}

	public removeItem(groupId: string, itemId: number): void {
		const itemGroup = this.groupsByGroupId[groupId];
		if (!itemGroup) {
			this.logger.error(`Cannot find group ${groupId} in UiItemView ` + this._config.id);
			return;
		}
		itemGroup.removeItem(itemId);
	}

	public setFilter(filter: string) {
		this.filterString = filter;
		this.filter();
	}

	private filter() {
		Object.keys(this.groupsByGroupId).forEach(groupId => {
			const group = this.groupsByGroupId[groupId];
			group.setFilter(this.filterString);
		});
	}

	public destroy(): void {
		Object.keys(this.groupsByGroupId).forEach(groupId => {
			const group = this.groupsByGroupId[groupId];
			group.destroy()
		});
	}
}

class ItemGroup {
	private $itemGroup: HTMLElement;
	private items: UiIdentifiableClientRecordConfig[];
	private trivialTreeBox: TrivialTreeBox<UiIdentifiableClientRecordConfig>;
	private itemRenderer: Renderer;
	private filterString: string;

	private static logger = log.getLogger("UiItemView-ItemGroup");

	public readonly onItemClicked: TeamAppsEvent<UiIdentifiableClientRecordConfig> = new TeamAppsEvent<UiIdentifiableClientRecordConfig>(this);

	constructor(private itemView: UiItemView, private context: TeamAppsUiContext, private config: UiItemViewItemGroupConfig, groupHeaderTemplateRenderer: Renderer) {
		this.items = config.items;

		const groupHtmlId = `item-group-${generateUUID()}`;
		this.$itemGroup = parseHtml('<div class="item-group" id="' + groupHtmlId + '">');

		let buttonWidthCssValue;
		if (config.buttonWidth < 0) {
			buttonWidthCssValue = 'auto';
		} else if (config.buttonWidth === 0) {
			buttonWidthCssValue = '100%'
		} else if (config.buttonWidth < 1) {
			buttonWidthCssValue = (config.buttonWidth * 100) + '%';
		} else {
			buttonWidthCssValue = config.buttonWidth + 'px';
		}

		this.$itemGroup.append(parseHtml(`<style>
            #${groupHtmlId} .tr-tree-entryTree {
               justify-content: ${itemCssStringsJustification[config.itemJustification]};
            }
            #${groupHtmlId} .tr-tree-entryTree .tr-tree-entry-outer-wrapper {
               margin: ${config.verticalItemMargin}px ${config.horizontalItemMargin}px;
               width: ${buttonWidthCssValue};
            }
            </style>`));
		const $itemGroupHeader = parseHtml('<div class="item-group-header">');
		this.$itemGroup.appendChild($itemGroupHeader);

		if (!config.headerVisible) {
			$itemGroupHeader.classList.add("hidden");
		}
		if (groupHeaderTemplateRenderer && config.headerData) {
			$itemGroupHeader.append(parseHtml(groupHeaderTemplateRenderer.render(config.headerData.values)));
		}

		const $itemContainer = parseHtml('<div class="item-container">');
		this.$itemGroup.appendChild($itemContainer);

		$itemContainer.classList.add(UiItemViewFloatStyle[config.floatStyle]);

		$itemContainer.style.padding = config.verticalPadding + "px " + config.horizontalPadding + "px";

		this.itemRenderer = context.templateRegistry.createTemplateRenderer(config.itemTemplate);
		this.trivialTreeBox = new TrivialTreeBox<UiIdentifiableClientRecordConfig>($itemContainer, {
			entryRenderingFunction: (entry) => this.itemRenderer.render(entry.values),
			spinnerTemplate: DEFAULT_TEMPLATES.defaultSpinnerTemplate,
			entries: config.items,
			idFunction: entry => entry && entry.id
		});
		this.trivialTreeBox.onSelectedEntryChanged.addListener(() => {
			this.onItemClicked.fire(this.trivialTreeBox.getSelectedEntry());
		});
	}

	setFilter(filterString: string) {
		this.filterString = filterString;
		this.filter();
	}

	private filter() {
		const matchingElements = this.filterItems(this.filterString);
		this.trivialTreeBox.updateEntries(matchingElements);
		if (matchingElements.length < 100) {
			this.trivialTreeBox.highlightTextMatches(this.filterString);
		}
		this.$itemGroup.classList.toggle("hidden", matchingElements.length === 0);
	}

	private filterItems(queryString: string) {
		if (!queryString) {
			return this.items;
		}

		let relevantFieldNames: string[];
		if (isGridTemplate(this.itemRenderer.template)) {
			relevantFieldNames = this.itemRenderer.template.elements
				.filter(e => e._type === "UiTextElement")
				.map(e => e.dataKey);
		} else {
			ItemGroup.logger.error("Unknown type of template!");
		}

		return this.items.filter((item) => {
			for (let i = 0; i < relevantFieldNames.length; i++) {
				const relevantFieldName = relevantFieldNames[i];
				if (trivialMatch(item.values[relevantFieldName], queryString, {
					matchingMode: 'contains',
					ignoreCase: true,
					maxLevenshteinDistance: 2
				}).length > 0) {
					return true;
				}
			}
			return false;
		});
	}

	getMainDomElement() {
		return this.$itemGroup;
	}

	addItem(item: UiIdentifiableClientRecordConfig) {
		this.items.push(item);
		this.filter();
	}

	removeItem(itemId: number) {
		this.items = this.items.filter((e) => e.id !== itemId);
		this.filter();
	}

	destroy() {
		this.trivialTreeBox.destroy();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiItemView", UiItemView);
