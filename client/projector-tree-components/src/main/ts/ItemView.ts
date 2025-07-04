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

import {
	type DtoItemView,
	type DtoItemView_ItemClickedEvent,
	type DtoItemViewCommandHandler,
	type DtoItemViewEventSource,
	type DtoItemViewItemGroup,
	type DtoItemViewServerObjectChannel,
	type ItemBackgroundMode
} from "./generated";
import {TrivialTreeBox} from "./trivial-components/TrivialTreeBox";
import {DEFAULT_TEMPLATES} from "./trivial-components/TrivialCore";
import {
	AbstractComponent,
	type DtoIdentifiableClientRecord,
	generateUUID,
	parseHtml,
	ProjectorEvent,
	type Template
} from "projector-client-object-api";

export class ItemView extends AbstractComponent<DtoItemView> implements DtoItemViewCommandHandler, DtoItemViewEventSource {

	public readonly onItemClicked: ProjectorEvent<DtoItemView_ItemClickedEvent> = new ProjectorEvent<DtoItemView_ItemClickedEvent>();

	private $itemView: HTMLElement;
	private groupsByGroupId: { [index: string]: ItemGroup } = {};
	private filterString: string = "";

	constructor(config: DtoItemView, soc: DtoItemViewServerObjectChannel) {
		super(config, soc);

		this.$itemView = parseHtml('<div class="ItemView"></div>');
		this.$itemView.style.padding = config.verticalPadding + "px " + config.horizontalPadding + "px";
		this.$itemView.classList.add("background-mode-" + config.itemBackgroundMode);

		config.itemGroups.forEach(group => {
			this.addItemGroup(group);
		});

		this.setFilter(config.filter);
	}

	public doGetMainElement(): HTMLElement {
		return this.$itemView;
	}

	public addItemGroup(itemGroupConfig: DtoItemViewItemGroup): ItemGroup {
		if (this.groupsByGroupId[itemGroupConfig.id]) {
			this.removeItemGroup(itemGroupConfig.id);
		}
		const itemGroup = this.createItemGroup(itemGroupConfig);
		this.groupsByGroupId[itemGroupConfig.id] = itemGroup;
		this.$itemView.appendChild(itemGroup.getMainDomElement());
		return itemGroup;
	}

	private createItemGroup(itemGroupConfig: DtoItemViewItemGroup) {
		const itemGroup = new ItemGroup(itemGroupConfig, this.config.groupHeaderTemplate as Template);
		itemGroup.onItemClicked.addListener(item => this.onItemClicked.fire({
			groupId: itemGroupConfig.id,
			itemId: item.id
		}));
		itemGroup.getMainDomElement().style.paddingBottom = this.config.groupSpacing + "px";
		itemGroup.setFilter(this.filterString);
		return itemGroup;
	}

	public refreshItemGroup(itemGroupConfig: DtoItemViewItemGroup): void {
		let oldGroup = this.groupsByGroupId[itemGroupConfig.id];
		if (!oldGroup) {
			console.error(`Could not refresh non-existing group "${itemGroupConfig.id}"!`);
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

	public addItem(groupId: string, item: DtoIdentifiableClientRecord): void {
		const itemGroup = this.groupsByGroupId[groupId];
		if (!itemGroup) {
			console.error(`Cannot find group ${groupId}`);
			return;
		}
		itemGroup.addItem(item);
	}

	public removeItem(groupId: string, itemId: number): void {
		const itemGroup = this.groupsByGroupId[groupId];
		if (!itemGroup) {
			console.error(`Cannot find group ${groupId}`);
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

	setGroupHeaderTemplate(groupHeaderTemplate: unknown) {
		this.config.groupHeaderTemplate = groupHeaderTemplate;
	}

	setHorizontalPadding(horizontalPadding: number) {
		throw new Error("Method not implemented.");
	}

	setVerticalPadding(verticalPadding: number) {
		throw new Error("Method not implemented.");
	}

	setGroupSpacing(groupSpacing: number) {
		throw new Error("Method not implemented.");
	}

	setItemBackgroundMode(itemBackgroundMode: ItemBackgroundMode) {
		throw new Error("Method not implemented.");
	}

	public destroy(): void {
		super.destroy();
		Object.keys(this.groupsByGroupId).forEach(groupId => {
			const group = this.groupsByGroupId[groupId];
			group.destroy()
		});
	}
}

class ItemGroup {
	private $itemGroup: HTMLElement;
	private items: DtoIdentifiableClientRecord[];
	private trivialTreeBox: TrivialTreeBox<DtoIdentifiableClientRecord>;
	private itemTemplate: Template;
	private filterString: string;

	public readonly onItemClicked: ProjectorEvent<DtoIdentifiableClientRecord> = new ProjectorEvent<DtoIdentifiableClientRecord>();

	constructor(config: DtoItemViewItemGroup, groupHeaderTemplateRenderer: Template) {
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
               justify-content: ${config.itemJustification};
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

		$itemContainer.classList.add(config.floatStyle);

		$itemContainer.style.padding = config.verticalPadding + "px " + config.horizontalPadding + "px";

		this.itemTemplate = config.itemTemplate as Template;
		this.trivialTreeBox = new TrivialTreeBox<DtoIdentifiableClientRecord>({
			entryRenderingFunction: (entry) => this.itemTemplate.render(entry.values),
			spinnerTemplate: DEFAULT_TEMPLATES.defaultSpinnerTemplate,
			entries: config.items,
			idFunction: entry => entry && entry.id
		});
		$itemContainer.append(this.trivialTreeBox.getMainDomElement());
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
		this.trivialTreeBox.setEntries(matchingElements);
		if (matchingElements.length < 100) {
			this.trivialTreeBox.highlightTextMatches(this.filterString);
		}
		this.$itemGroup.classList.toggle("hidden", matchingElements.length === 0);
	}

	private filterItems(queryString: string) {
		if (!queryString) {
			return this.items;
		}

		return this.items.filter((item) => {
			return item.asString.indexOf(queryString) > -1;
		});
	}

	getMainDomElement() {
		return this.$itemGroup;
	}

	addItem(item: DtoIdentifiableClientRecord) {
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


