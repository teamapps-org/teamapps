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
import {Component} from "teamapps-client-core";
import {SplitPaneItem} from "./SplitPaneItem";
import {View} from "./View";
import {TabPanelItem} from "./TabPanelItem";


export interface ItemTreeItem<C extends Component = Component> {
	id: string;
	parent: SplitPaneItem;
	component: C;
	itemIds: string[];
	viewNames: string[];
}

export class ItemTree {
	private _rootItem: ItemTreeItem<Component>;
	private _viewsByName: { [viewName: string]: View } = {};

	set rootItem(item: ItemTreeItem<Component>) {
		this._rootItem = item;
		this.updateIndex();
	}

	get rootItem() {
		return this._rootItem;
	}

	get views() {
		return Object.keys(this._viewsByName).map(viewName => this._viewsByName[viewName]);
	}

	get viewNames() {
		return Object.keys(this._viewsByName);
	}

	get viewCount() {
		return Object.keys(this._viewsByName).length;
	}

	get viewsByName() {
		return this._viewsByName;
	}

	public updateIndex() {
		this._viewsByName = {};
		this.doForEachItemAndView(this._rootItem, null, view => this._viewsByName[view.viewName] = view);
	}

	getViewByName(viewName: string): View {
		let view = this._viewsByName[viewName];
		if (view == null) {
			console.warn(`Cannot find view with name ${viewName}.`);
		}
		return view;
	}

	private doForEachItemAndView(item: ItemTreeItem, itemFun: (item: ItemTreeItem) => void, viewFun: (view: View) => void) {
		itemFun && itemFun(item);
		if (item instanceof SplitPaneItem) {
			item.firstChild && this.doForEachItemAndView(item.firstChild, itemFun, viewFun);
			item.lastChild && this.doForEachItemAndView(item.lastChild, itemFun, viewFun);
		} else if (item instanceof TabPanelItem) {
			viewFun && item.tabs.forEach(viewFun);
		}
	}

	getAllTabPanelItems(): TabPanelItem[] {
		let tabPanels: TabPanelItem[] = [];
		this.doForEachItemAndView(this._rootItem, item => (item instanceof TabPanelItem) && tabPanels.push(item), null);
		return tabPanels;
	}

	getTabPanelForView(viewName: string) {
		return this.getAllTabPanelItems()
			.filter(tabPanel => {
				return tabPanel.tabs.filter(tab => tab.viewName === viewName).length > 0
			})[0];
	}

	getTabPanelById(itemId: string) {
		return this.getAllTabPanelItems()
			.filter(tabPanel => tabPanel.id === itemId)[0];
	}
}

