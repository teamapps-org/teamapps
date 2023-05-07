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
import {DtoSplitDirection, DtoSplitSizePolicy} from "../../generated";
import {SplitPane} from "../SplitPane";
import {Component, generateUUID, TeamAppsUiContext} from "teamapps-client-core";
import {ItemTreeItem} from "./ItemTree";

export class SplitPaneItem implements ItemTreeItem<SplitPane> {
	id: string;
	parent: SplitPaneItem;
	component: SplitPane;
	private _firstChild: ItemTreeItem<Component>;
	private _lastChild: ItemTreeItem<Component>;

	constructor(id: string, parent: SplitPaneItem, splitDirection: DtoSplitDirection, sizePolicy: DtoSplitSizePolicy, firstChildRelativeSize: number) {
		this.id = id;
		this.parent = parent;
		this.component = new SplitPane({
			id: generateUUID(),
			splitDirection: splitDirection,
			sizePolicy: sizePolicy,
			referenceChildSize: firstChildRelativeSize,
			resizable: true,
			fillIfSingleChild: true,
			collapseEmptyChildren: true
		});
	}

	public get itemIds() {
		const firstChildItemIds = this._firstChild != null ? this._firstChild.itemIds : [];
		const lastChildItemIds = this._lastChild != null ? this._lastChild.itemIds : [];
		return [this.id, ...firstChildItemIds, ...lastChildItemIds];
	}

	public get viewNames() {
		const firstChildViewNames = this._firstChild ? this._firstChild.viewNames : [];
		const lastChildViewNames = this._lastChild ? this._lastChild.viewNames : [];
		return [...firstChildViewNames, ...lastChildViewNames];
	}

	public set firstChild(firstChild: ItemTreeItem<Component>) {
		this._firstChild = firstChild;
		this._firstChild && (this._firstChild.parent = this);
		this.component.setFirstChild(firstChild ? firstChild.component : null);
	}

	public set lastChild(lastChild: ItemTreeItem<Component>) {
		this._lastChild = lastChild;
		this._lastChild && (this._lastChild.parent = this);
		this.component.setLastChild(lastChild ? lastChild.component : null);
	}

	public get firstChild(): ItemTreeItem<Component> {
		return this._firstChild;
	}

	public get lastChild(): ItemTreeItem<Component> {
		return this._lastChild;
	}

	get splitDirection(): DtoSplitDirection {
		return this.component.splitDirection;
	}

	get sizePolicy(): DtoSplitSizePolicy {
		return this.component.sizePolicy;
	}

	get referenceChildSize(): number {
		return this.component.referenceChildSize;
	}
}
