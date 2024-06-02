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
import {TrivialComboBox} from "./trivial-components/TrivialComboBox";
import {TrivialTreeBox} from "./trivial-components/TrivialTreeBox";

import {
	DtoComboBox,
	DtoComboBox_TextInputEvent,
	DtoComboBoxCommandHandler,
	DtoComboBoxEventSource, DtoComboBoxServerObjectChannel,
	DtoComboBoxTreeRecord
} from "./generated";
import {TreeBoxDropdown} from "./trivial-components/dropdown/TreeBoxDropdown";
import {
	AbstractField,
	DebounceMode,
	FieldEditingMode,
	TeamAppsEvent, Template
} from "projector-client-object-api";
import {buildObjectTree, NodeWithChildren} from "./util";

export function isFreeTextEntry(o: DtoComboBoxTreeRecord): boolean {
	return o != null && o.id < 0;
}

export class ComboBox extends AbstractField<DtoComboBox, DtoComboBoxTreeRecord> implements DtoComboBoxEventSource, DtoComboBoxCommandHandler {
	public readonly onTextInput: TeamAppsEvent<DtoComboBox_TextInputEvent> = TeamAppsEvent.createDebounced(250, DebounceMode.BOTH);

	private trivialComboBox: TrivialComboBox<NodeWithChildren<DtoComboBoxTreeRecord>>;
	private freeTextIdEntryCounter = -1;

	private trivialTreeBox: TrivialTreeBox<NodeWithChildren<DtoComboBoxTreeRecord>>;

	private treeBoxDropdown: TreeBoxDropdown<DtoComboBoxTreeRecord & { __children?: NodeWithChildren<DtoComboBoxTreeRecord>[] }>;

	constructor(config: DtoComboBox, private soc: DtoComboBoxServerObjectChannel) {
		super(config, soc);
	}

	protected initialize(config: DtoComboBox) {

		this.trivialTreeBox = new TrivialTreeBox<NodeWithChildren<DtoComboBoxTreeRecord>>({
			childrenProperty: "__children",
			expandedProperty: "expanded",
			showExpanders: config.expandersVisible,
			entryRenderingFunction: entry => this.renderRecord(entry, true),
			idFunction: entry => entry && entry.id,
			lazyChildrenQueryFunction: async (node: NodeWithChildren<DtoComboBoxTreeRecord>) => {
				let children = await this.soc.sendQuery("lazyChildren", node.id);
				return buildObjectTree(children, "id", "parentId");
			},
			lazyChildrenFlag: entry => entry.lazyChildren,
			selectableDecider: entry => entry.selectable,
			selectOnHover: true,
			animationDuration: this.config.expandAnimationEnabled ? 120 : 0
		});
		this.treeBoxDropdown = new TreeBoxDropdown({
			queryFunction: (queryString: string) => {
				return this.soc.sendQuery("retrieveDropdownEntries", queryString)
					.then(entries => buildObjectTree(entries, "id", "parentId"));
			},
			textHighlightingEntryLimit: config.textHighlightingEntryLimit,
			preselectionMatcher: (query, entry) => entry.asString.toLowerCase().indexOf(query.toLowerCase()) >= 0
		}, this.trivialTreeBox);
		this.trivialComboBox = new TrivialComboBox<NodeWithChildren<DtoComboBoxTreeRecord>>({
			selectedEntryRenderingFunction: entry => {
				if (entry == null) {
					return "";
				} else if (isFreeTextEntry(entry)) {
					return `<div class="free-text-entry">${entry.asString}</div>`;
				} else {
					return this.renderRecord(entry, false);
				}
			},
			autoComplete: !!config.autoCompletionEnabled,
			showTrigger: config.dropDownButtonVisible,
			editingMode: config.editingMode === FieldEditingMode.READONLY ? 'readonly' : config.editingMode === FieldEditingMode.DISABLED ? 'disabled' : 'editable',

			showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
			showClearButton: config.clearButtonEnabled,
			entryToEditorTextFunction: e => e.asString,
			textToEntryFunction: this.createTextToEntryFunction(config.freeTextEnabled),
			preselectFirstQueryResult: config.firstEntryAutoHighlight,
			placeholderText: config.placeholderText,
			dropDownMaxHeight: config.dropDownMaxHeight,
			dropDownMinWidth: config.dropDownMinWidth
		}, this.treeBoxDropdown);
		this.trivialComboBox.getMainDomElement().classList.add("ComboBox");
		this.trivialComboBox.onSelectedEntryChanged.addListener(() => this.commit());
		this.trivialComboBox.getEditor().addEventListener("input", e => this.onTextInput.fire({enteredString: (e.target as HTMLInputElement).value}));

		this.trivialComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
	}

	private createTextToEntryFunction(freeTextEnabled: boolean) {
		return (freeText) => {
			if (freeTextEnabled) {
				return {id: this.freeTextIdEntryCounter--, values: {}, asString: freeText};
			} else {
				return null;
			}
		};
	}

	protected initFocusHandling() {
		this.trivialComboBox.onFocus.addListener(() => this.onFocus.fire({}));
		this.trivialComboBox.onBlur.addListener(() => this.onBlur.fire({}));
	}

	private renderRecord(record: NodeWithChildren<DtoComboBoxTreeRecord>, dropdown: boolean): string {
		const template = (dropdown ? record.dropDownTemplate : record.displayTemplate) as Template;
		if (template != null) {
			return template.render(record.values);
		} else {
			return `<div class="string-template">${record.asString}</div>`;
		}
	}

	isValidData(v: any): boolean {
		return v == null || typeof v === "object" && (v as DtoComboBoxTreeRecord).id != null;
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement() as HTMLElement;
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		this.trivialComboBox.setValue(uiValue);
	}

	public getTransientValue(): any {
		return this.trivialComboBox.getValue();
	}

	protected convertValueForSendingToServer(value: DtoComboBoxTreeRecord): any {
		if (value == null) {
			return null;
		}
		return isFreeTextEntry(value) ? value.asString : value.id;
	}

	focus(): void {
		if (this.isEditable()) {
			this.trivialComboBox.focus();
		}
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.keys(FieldEditingMode));
		this.getMainElement().classList.add(editingMode);
		if (editingMode === FieldEditingMode.READONLY) {
			this.trivialComboBox.setEditingMode("readonly");
		} else if (editingMode === FieldEditingMode.DISABLED) {
			this.trivialComboBox.setEditingMode("disabled");
		} else {
			this.trivialComboBox.setEditingMode("editable");
		}
	}

	replaceFreeTextEntry(freeText: string, record: DtoComboBoxTreeRecord): void {
		const selectedEntry = this.trivialComboBox.getValue();
		if (isFreeTextEntry(selectedEntry) && selectedEntry.asString === freeText) {
			this.setCommittedValue(record);
		}
	}

	destroy(): void {
		super.destroy();
		this.trivialComboBox.destroy();
	}

	public getReadOnlyHtml(value: DtoComboBoxTreeRecord, availableWidth: number): string {
		if (value != null) {
			return `<div class="static-readonly-ComboBox">${this.renderRecord(value, false)}</div>`;
		} else {
			return "";
		}
	}

	getDefaultValue(): any {
		return null;
	}

	public valuesChanged(v1: DtoComboBoxTreeRecord, v2: DtoComboBoxTreeRecord): boolean {
		let nullAndNonNull = ((v1 == null) !== (v2 == null));
		let nonNullAndValuesDifferent = (v1 != null && v2 != null && (
			v1.id !== v2.id
			|| v1.id !== v2.id
		));
		return nullAndNonNull || nonNullAndValuesDifferent;
	}

	setDropDownButtonVisible(dropDownButtonVisible: boolean) {
		this.config.dropDownButtonVisible = dropDownButtonVisible;
		this.trivialComboBox.setShowTrigger(dropDownButtonVisible);
	}
	setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive: boolean) {
		this.config.showDropDownAfterResultsArrive = showDropDownAfterResultsArrive;
		this.trivialComboBox.setShowDropDownOnResultsOnly(showDropDownAfterResultsArrive);
	}
	setFirstEntryAutoHighlight(firstEntryAutoSelectEnabled: boolean) {
		this.config.firstEntryAutoHighlight = firstEntryAutoSelectEnabled;
		this.trivialComboBox.setPreselectFirstQueryResult(firstEntryAutoSelectEnabled);
	}
	setTextHighlightingEntryLimit(textHighlightingEntryLimit: number) {
		this.config.textHighlightingEntryLimit = textHighlightingEntryLimit;
		this.treeBoxDropdown.setTextHighlightingEntryLimit(textHighlightingEntryLimit);
	}
	setAutoCompletionEnabled(autoCompletionEnabled: boolean) {
		this.config.autoCompletionEnabled = autoCompletionEnabled;
		this.trivialComboBox.setAutoComplete(autoCompletionEnabled);
	}
	setFreeTextEnabled(freeTextEnabled: boolean) {
		this.config.freeTextEnabled = freeTextEnabled;
		this.trivialComboBox.setTextToEntryFunction(this.createTextToEntryFunction(freeTextEnabled));
	}
	setClearButtonEnabled(clearButtonEnabled: boolean) {
		this.config.clearButtonEnabled = clearButtonEnabled;
		this.trivialComboBox.setShowClearButton(clearButtonEnabled);
	}
	setExpandersVisible(expandersVisible: boolean) {
		this.config.expandersVisible = expandersVisible;
		this.trivialTreeBox.setShowExpanders(expandersVisible);
	}
	setExpandAnimationEnabled(expandAnimationEnabled: boolean) {
		this.config.expandAnimationEnabled = expandAnimationEnabled;
		this.trivialTreeBox.setAnimationDuration(expandAnimationEnabled ? 120 : 0);
	}
	setPlaceholderText(placeholderText: string) {
		this.trivialComboBox.setPlaceholderText(placeholderText);
	}
	setDropDownMinWidth(dropDownMinWidth: number) {
		this.trivialComboBox.setDropdownMinWidth(dropDownMinWidth);
	}
	setDropDownMaxHeight(dropDownMaxHeight: number) {
		this.trivialComboBox.setDropdownMaxHeight(dropDownMaxHeight);
	}
	setIndentation(indentation: number) {
		this.trivialTreeBox.setIndentationWidth(indentation);
	}
}


