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
import {UiWorkSpaceLayoutItemConfig} from "../../generated/UiWorkSpaceLayoutItemConfig";
import {ItemTreeItem} from "./ItemTree";
import {View} from "./View";
import {SplitPaneItem} from "./SplitPaneItem";
import {UiWorkSpaceLayoutSplitItemConfig} from "../../generated/UiWorkSpaceLayoutSplitItemConfig";
import {TabPanelItem} from "./TabPanelItem";
import {UiWorkSpaceLayoutViewGroupItemConfig} from "../../generated/UiWorkSpaceLayoutViewGroupItemConfig";
import {LocalViewContainer} from "./LocalViewContainer";
import * as log from "loglevel";
import {UiWorkSpaceLayoutViewConfig} from "../../generated/UiWorkSpaceLayoutViewConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {isSplitPanelDescriptor, isTabPanelDescriptor} from "./UiWorkSpaceLayout";
import {UiViewGroupPanelState} from "../../generated/UiViewGroupPanelState";
import {UiComponent} from "../UiComponent";

export class LayoutDescriptorApplyer {

	private static logger: log.Logger = log.getLogger("LocalViewContainer");

	private descriptorItemById: { [itemId: string]: UiWorkSpaceLayoutItemConfig } = {};
	private descriptorViewNames: string[] = [];

	private clientItemsById: { [itemId: string]: ItemTreeItem } = {};
	private clientViewsByName: { [viewName: string]: View } = {};

	private clientItemStash: { [itemId: string]: ItemTreeItem } = {};

	constructor(
		private $rootItemContainer: HTMLElement,
		private viewGroupFactory: (config: UiWorkSpaceLayoutViewGroupItemConfig, parent: SplitPaneItem) => TabPanelItem,
		private setViewGroupPanelStateFunction: (viewGroupItem: TabPanelItem, panelState: UiViewGroupPanelState) => void,
		private context: TeamAppsUiContext
	) {
	}

	public apply(
		currentRootItem: ItemTreeItem,
		newLayoutDescriptor: UiWorkSpaceLayoutItemConfig,
		newViewConfigs: UiWorkSpaceLayoutViewConfig[]
	): ItemTreeItem {
		this.descriptorItemById = {};
		this.descriptorViewNames = [];
		this.clientItemsById = {};
		this.clientViewsByName = {};
		this.clientItemStash = {};

		this.buildDescriptorDictionaries(newLayoutDescriptor);
		if (currentRootItem != null) {
			this.buildClientItemDictionaries(currentRootItem);
			this.cleanupUnknownClientItems(currentRootItem, newLayoutDescriptor, null);
		}
		return this.addNewStructure(newLayoutDescriptor, null, false, newViewConfigs);
	}

	private buildDescriptorDictionaries(descriptorItem: UiWorkSpaceLayoutItemConfig) {
		this.descriptorItemById[descriptorItem.id] = descriptorItem;
		if (isTabPanelDescriptor(descriptorItem)) {
			this.descriptorViewNames.push(...descriptorItem.viewNames);
		} else if (isSplitPanelDescriptor(descriptorItem)) {
			if (descriptorItem.firstChild != null) {
				this.buildDescriptorDictionaries(descriptorItem.firstChild);
			}
			if (descriptorItem.lastChild != null) {
				this.buildDescriptorDictionaries(descriptorItem.lastChild);
			}
		}
	}

	private buildClientItemDictionaries(item: ItemTreeItem) {
		this.clientItemsById[item.id] = item;
		if (item instanceof TabPanelItem) {
			item.tabs.forEach(tab => this.clientViewsByName[tab.viewName] = tab);
		} else if (item instanceof SplitPaneItem) {
			this.buildClientItemDictionaries(item.firstChild);
			this.buildClientItemDictionaries(item.lastChild);
		}
	}

	public cleanupUnknownClientItems(clientSideItem: ItemTreeItem, descriptorItem: UiWorkSpaceLayoutItemConfig, parent: SplitPaneItem | null) {
		// descriptorItem may be null for recursive executions of this method!
		if (descriptorItem != null && descriptorItem.id === clientSideItem.id) {
			if (clientSideItem instanceof SplitPaneItem) {
				this.cleanupUnknownClientItems(clientSideItem.firstChild, (descriptorItem as UiWorkSpaceLayoutSplitItemConfig).firstChild, clientSideItem);
				this.cleanupUnknownClientItems(clientSideItem.lastChild, (descriptorItem as UiWorkSpaceLayoutSplitItemConfig).lastChild, clientSideItem);
			} else if (clientSideItem instanceof TabPanelItem) {
				this.stashUnknownViews(clientSideItem, (descriptorItem as UiWorkSpaceLayoutViewGroupItemConfig).viewNames);
			}
		} else {
			let correspondingDescriptorItem = this.descriptorItemById[clientSideItem.id];
			if (correspondingDescriptorItem != null) {
				this.clientItemStash[clientSideItem.id] = clientSideItem;
				clientSideItem.component.getMainDomElement().remove();
				this.cleanupUnknownClientItems(clientSideItem, correspondingDescriptorItem, null);
			} else {
				// not referenced in the descriptor! however, descendants might well be referenced in the descriptor!
				if (clientSideItem instanceof TabPanelItem) {
					this.stashUnknownViews(clientSideItem, []);
				} else if (clientSideItem instanceof SplitPaneItem) {
					this.cleanupUnknownClientItems(clientSideItem.firstChild, null, clientSideItem);
					this.cleanupUnknownClientItems(clientSideItem.lastChild, null, clientSideItem);
				}
			}

			// remove the clientSideItem!
			if (parent != null) {
				if (clientSideItem === parent.firstChild) {
					parent.firstChild = null;
				} else {
					parent.lastChild = null;
				}
			} else {
				// this is the root item!
				clientSideItem.component.getMainDomElement().remove();
			}
		}
	}
	
	private stashUnknownViews(viewGroup: TabPanelItem, descriptorViewNames: string[]) {
		let viewsNotFoundInDescriptorItem = viewGroup.tabs
			.filter(tab => descriptorViewNames.indexOf(tab.viewName) === -1);
		viewsNotFoundInDescriptorItem.forEach((tab: View) => {
			let viewIsReferencedAnywhereInRootDescriptor = this.descriptorViewNames.indexOf(tab.viewName) !== -1;
			if (viewIsReferencedAnywhereInRootDescriptor) {
				tab.component.getMainDomElement().remove();
				viewGroup.removeTab(tab);
			} else {
				// will not be used anymore in any way! just remove and destroy
				viewGroup.removeTab(tab);
			}
		});
	}

	private addNewStructure(descriptor: UiWorkSpaceLayoutItemConfig, parent: SplitPaneItem | null, firstChild: boolean, newViewConfigs: UiWorkSpaceLayoutViewConfig[]) {
		if (descriptor == null) {
			return null;
		}
		let clientSideItem = this.clientItemsById[descriptor.id];
		let itemMovedToStash = this.clientItemStash[descriptor.id] != null;
		let item: ItemTreeItem;
		if (clientSideItem != null) {
			item = clientSideItem;
			if (isSplitPanelDescriptor(descriptor)) {
				this.addNewStructure(descriptor.firstChild, clientSideItem as SplitPaneItem, true, newViewConfigs);
				this.addNewStructure(descriptor.lastChild, clientSideItem as SplitPaneItem, false, newViewConfigs);
			} else if (isTabPanelDescriptor(descriptor)) {
				this.addViews(clientSideItem as TabPanelItem, descriptor, newViewConfigs);
			}
		} else { // this is a descriptor for a new item
			item = this.createTreeItemFromLayoutDescriptor(descriptor, parent, newViewConfigs);
		}

		if (itemMovedToStash) {
			if (parent != null) {
				if (firstChild) {
					parent.firstChild = item;
				} else {
					parent.lastChild = item;
				}
				item.parent = parent;
			} else {
				this.$rootItemContainer.append(item.component.getMainDomElement());
			}
		}

		if (item instanceof SplitPaneItem && isSplitPanelDescriptor(descriptor)) {
			item.component.setSize(descriptor.referenceChildSize, descriptor.sizePolicy);
		} else if (item instanceof TabPanelItem && isTabPanelDescriptor(descriptor)) {
			this.setViewGroupPanelStateFunction(item, descriptor.panelState);
		}
		return item;
	}

	private createTreeItemFromLayoutDescriptor(descriptor: UiWorkSpaceLayoutItemConfig, parent: SplitPaneItem, newViewConfigs: UiWorkSpaceLayoutViewConfig[]) {
		if (isTabPanelDescriptor(descriptor)) {
			let tabPanelItem = this.viewGroupFactory(descriptor, parent);
			this.addViews(tabPanelItem, descriptor, newViewConfigs);
			return tabPanelItem;
		} else if (isSplitPanelDescriptor(descriptor)) {
			let splitPaneItem = new SplitPaneItem(descriptor.id, parent, descriptor.splitDirection, descriptor.sizePolicy, descriptor.referenceChildSize, this.context);
			splitPaneItem.firstChild = this.addNewStructure(descriptor.firstChild, splitPaneItem, true, newViewConfigs);
			splitPaneItem.lastChild = this.addNewStructure(descriptor.lastChild, splitPaneItem, false, newViewConfigs);
			return splitPaneItem;
		}
	}

	private addViews(tabPanelItem: TabPanelItem, viewGroupDescriptor: UiWorkSpaceLayoutViewGroupItemConfig, newViewConfigs: UiWorkSpaceLayoutViewConfig[]) {
		viewGroupDescriptor.viewNames.forEach((viewName, index) => {
			let selected = viewName === viewGroupDescriptor.selectedViewName || viewGroupDescriptor.selectedViewName == null && index === 0;
			let tabAlreadyInGroupItem = tabPanelItem.tabs.filter(tab => tab.viewName === viewName)[0];
			if (tabAlreadyInGroupItem) {
				tabPanelItem.moveTab(viewName, index);
				if (selected) {
					tabPanelItem.selectTab(viewName);
				}
			} else if (this.clientViewsByName[viewName]) {
				let view = this.clientViewsByName[viewName];
				tabPanelItem.addTab(view, selected, index);
			} else {
				let newViewConfig = newViewConfigs.filter(view => view.viewName === viewName)[0];
				if (newViewConfig != null) {
					let view = new View(newViewConfig.viewName, newViewConfig.tabIcon, newViewConfig.tabCaption, newViewConfig.tabCloseable, newViewConfig.lazyLoading, newViewConfig.visible, newViewConfig.component as UiComponent);
					tabPanelItem.addTab(view, selected);
				} else {
					LayoutDescriptorApplyer.logger.error("View item references non-existing view: " + viewName);
					return;
				}
			}
		});
	}
}
