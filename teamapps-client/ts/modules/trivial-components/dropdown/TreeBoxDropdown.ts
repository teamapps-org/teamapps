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
import {DropDownComponent, SelectionDirection} from "./DropDownComponent";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {QueryFunction} from "../TrivialCore";
import {TrivialTreeBox} from "../TrivialTreeBox";

type TreeBoxDropdownConfig<E> = {
	queryFunction: QueryFunction<E>;
	/**
	 * Performance setting. Defines the maximum number of entries until which text highlighting is performed.
	 * Set to `0` to disable text highlighting.
	 *
	 * @default `100`
	 */
	textHighlightingEntryLimit?: number;
	preselectionMatcher: (query: string, entry: E) => boolean;
};

export class TreeBoxDropdown<E> implements DropDownComponent<E> {

	public readonly onValueChanged: TeamAppsEvent<{ value: E; finalSelection: boolean }> = new TeamAppsEvent();

	private treeBox: TrivialTreeBox<E>;
	private config: TreeBoxDropdownConfig<E>;

	constructor(config: TreeBoxDropdownConfig<E>, treeBox: TrivialTreeBox<E>) {
		this.treeBox = treeBox;
		this.config = {
			textHighlightingEntryLimit: 100,
			...config
		};
		this.treeBox.onSelectedEntryChanged.addListener(entry => this.onValueChanged.fire({value: entry, finalSelection: true}));
	}

	getMainDomElement(): HTMLElement {
		return this.treeBox.getMainDomElement();
	}

	getValue(): E {
		return this.treeBox.getSelectedEntry();
	}

	handleKeyboardInput(event: KeyboardEvent): boolean {
		if (["ArrowUp", "ArrowDown"].indexOf(event.code) !== -1) {
			let selectedEntry = this.treeBox.selectNextEntry(event.code == "ArrowUp" ? -1 : 1, true);
			if (selectedEntry != null) {
				this.onValueChanged.fire({value: this.treeBox.getSelectedEntry(), finalSelection: false});
				return true;
			} else {
				return false;
			}
		} else if (["ArrowRight", "ArrowLeft"].indexOf(event.code) !== -1) {
			return this.treeBox.setSelectedNodeExpanded(event.code == "ArrowRight");
		}
	}

	async handleQuery(query: string, selectionDirection: SelectionDirection, currentComboBoxValue: E): Promise<boolean> {
		let results = await this.config.queryFunction(query) ?? [];
		this.treeBox.setEntries(results);
		this.treeBox.setSelectedEntryById(null); // make sure we don't remember the last selected value and go down/up from it
		this.getMainDomElement().scrollIntoView({block: "start"}); // make sure we scroll up
		this.treeBox.highlightTextMatches(results.length <= this.config.textHighlightingEntryLimit ? query : null);
		if (selectionDirection === 0) {
			this.treeBox.setSelectedEntryById(null)
		} else {
			let selectedEntry = this.treeBox.selectNextEntry(1, false, true, (entry) => this.config.preselectionMatcher(query, entry));
			if (selectedEntry == null) {
				this.treeBox.selectNextEntry(1, false);
			}
		}
		return results.length > 0;
	}

	setValue(value: E): void {
		// do nothing
	}

	destroy(): void {
		this.treeBox.destroy();
	}

	getComponent(): TrivialTreeBox<E> {
		return this.treeBox;
	}

}
