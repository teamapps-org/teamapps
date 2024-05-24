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

import {parseHtml, TeamAppsEvent, Template} from "teamapps-client-core";
import {isFreeTextEntry} from "./ComboBox";
import {
	AbstractField,
	buildObjectTree,
	DtoFieldEditingMode, SpecialKey,
	DtoTextInputHandlingField_SpecialKeyPressedEvent,
	DtoTextInputHandlingField_TextInputEvent,
	NodeWithChildren
} from "teamapps-client-core-components";
import {
	DtoComboBoxTreeRecord,
	DtoTagComboBox,
	DtoTagComboBoxCommandHandler,
	DtoTagComboBoxEventSource,
	DtoTagComboBoxWrappingMode
} from "./generated";
import {TrivialTagComboBox} from "./trivial-components/TrivialTagComboBox";
import {wrapWithDefaultTagWrapper} from "./trivial-components/TrivialCore";
import {TreeBoxDropdown} from "./trivial-components/dropdown/TreeBoxDropdown";
import {TrivialTreeBox} from "./trivial-components/TrivialTreeBox";

export class TagComboBox extends AbstractField<DtoTagComboBox, DtoComboBoxTreeRecord[]> implements DtoTagComboBoxEventSource, DtoTagComboBoxCommandHandler {

	public readonly onTextInput: TeamAppsEvent<DtoTextInputHandlingField_TextInputEvent> = new TeamAppsEvent({
		throttlingMode: "debounce",
		delay: 250
	});
	public readonly onSpecialKeyPressed: TeamAppsEvent<DtoTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent({
		throttlingMode: "debounce",
		delay: 250
	});

	private $originalInput: HTMLElement;
	private trivialTagComboBox: TrivialTagComboBox<NodeWithChildren<DtoComboBoxTreeRecord>>;
	private resultCallbacksQueue: ((result: NodeWithChildren<DtoComboBoxTreeRecord>[]) => void)[] = [];

	private freeTextIdEntryCounter = -1;

	private trivialTreeBox: TrivialTreeBox<NodeWithChildren<DtoComboBoxTreeRecord>>;

	private treeBoxDropdown: TreeBoxDropdown<DtoComboBoxTreeRecord & { __children?: NodeWithChildren<DtoComboBoxTreeRecord>[] }>;

	protected initialize(config: DtoTagComboBox) {
		this.$originalInput = parseHtml(`<input type="text" autocomplete="no">`);

		this.trivialTreeBox = new TrivialTreeBox<NodeWithChildren<DtoComboBoxTreeRecord>>({
			childrenProperty: "__children",
			expandedProperty: "expanded",
			showExpanders: config.expandersVisible,
			entryRenderingFunction: entry => this.renderRecord(entry, true),
			idFunction: entry => entry && entry.id,
			lazyChildrenQueryFunction: async (node: NodeWithChildren<DtoComboBoxTreeRecord>) => buildObjectTree(await config.lazyChildren({parentId: node.id}), "id", "parentId"),
			lazyChildrenFlag: entry => entry.lazyChildren,
			selectableDecider: entry => entry.selectable,
			selectOnHover: true,
			animationDuration: this.config.expandAnimationEnabled ? 120 : 0
		});

		this.treeBoxDropdown = new TreeBoxDropdown({
			queryFunction: (queryString: string) => {
				this.onTextInput.fire({enteredString: queryString}); // TODO this is definitely the wrong place for this!!
				return config.retrieveDropdownEntries({queryString})
					.then(entries => buildObjectTree(entries, "id", "parentId"));
			},
			textHighlightingEntryLimit: config.textHighlightingEntryLimit,
			preselectionMatcher: (query, entry) => entry.asString.toLowerCase().indexOf(query.toLowerCase()) >= 0
		}, this.trivialTreeBox);
		this.trivialTagComboBox = new TrivialTagComboBox<NodeWithChildren<DtoComboBoxTreeRecord>>({
			selectedEntryRenderingFunction: (entry) => {
				if (isFreeTextEntry(entry)) {
					return wrapWithDefaultTagWrapper(`<div class="free-text-entry">${entry.asString}</div>`, config.deleteButtonsEnabled);
				} else {
					return wrapWithDefaultTagWrapper(this.renderRecord(entry, false), config.deleteButtonsEnabled);
				}
			},
			showClearButton: config.clearButtonEnabled,
			autoComplete: !!config.autoCompletionEnabled,
			showTrigger: config.dropDownButtonVisible,
			editingMode: config.editingMode === DtoFieldEditingMode.READONLY ? 'readonly' : config.editingMode === DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			spinnerTemplate: `<div class="teamapps-spinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
			entryToEditorTextFunction: e => e.asString,
			freeTextEntryFactory: (freeText) => {
				return {id: this.freeTextIdEntryCounter--, values: {}, asString: freeText};
			},
			selectionAcceptor: entry => {
				const violatesDistinctSetting = config.distinct && this.trivialTagComboBox.getSelectedEntries().map(e => e.id).indexOf(entry.id) !== -1;
				const violatesMaxEntriesSetting = !!config.maxEntries && this.trivialTagComboBox.getSelectedEntries().length >= config.maxEntries;
				const violatesFreeTextSetting = !config.freeTextEnabled && isFreeTextEntry(entry);
				return !violatesDistinctSetting && !violatesMaxEntriesSetting && !violatesFreeTextSetting;
			},
			twoStepDeletion: this.config.twoStepDeletionEnabled,
			preselectFirstQueryResult: config.firstEntryAutoHighlight,
			placeholderText: config.placeholderText,
			dropDownMaxHeight: config.dropDownMaxHeight,
			dropDownMinWidth: config.dropDownMinWidth
		}, this.treeBoxDropdown);
		this.trivialTagComboBox.getMainDomElement().classList.add("UiTagComboBox");
		this.setWrappingMode(config.wrappingMode);
		this.trivialTagComboBox.onValueChanged.addListener(() => this.commit());
		this.trivialTagComboBox.getEditor().addEventListener("keydown", (e: KeyboardEvent) => {
			if (e.key === "Escape") {
				this.onSpecialKeyPressed.fire({
					key: SpecialKey.ESCAPE
				});
			} else if (e.key === "Enter") {
				this.onSpecialKeyPressed.fire({
					key: SpecialKey.ENTER
				});
			}
		});

		this.trivialTagComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialTagComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialTagComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
	}

	protected initFocusHandling() {
		this.trivialTagComboBox.onFocus.addListener(() => this.onFocus.fire({}));
		this.trivialTagComboBox.onBlur.addListener(() => this.onBlur.fire({}));
	}

	private renderRecord(record: NodeWithChildren<DtoComboBoxTreeRecord>, dropdown: boolean): string {
		const template = (dropdown ? record.dropDownTemplate : record.displayTemplate) as Template;
		if (template != null) {
			return template.render(record.values);
		} else {
			return `<div class="string-template">${record.asString}</div>`;
		}
	}

	isValidData(v: any[]): boolean {
		return v == null || Array.isArray(v);
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialTagComboBox.getMainDomElement() as HTMLElement;
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		this.trivialTagComboBox.setSelectedEntries(uiValue);
	}

	public getTransientValue(): any[] {
		return this.trivialTagComboBox.getSelectedEntries();
	}

	protected convertValueForSendingToServer(values: DtoComboBoxTreeRecord[]): any {
		return values.map(value => isFreeTextEntry(value) ? value.asString : value.id);
	}

	focus(): void {
		this.trivialTagComboBox.focus(); // TODO
	}

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.values(AbstractField.editingModeCssClasses));
		this.getMainElement().classList.add(AbstractField.editingModeCssClasses[editingMode]);
		if (editingMode === DtoFieldEditingMode.READONLY) {
			this.trivialTagComboBox.setEditingMode("readonly");
		} else if (editingMode === DtoFieldEditingMode.DISABLED) {
			this.trivialTagComboBox.setEditingMode("disabled");
		} else {
			this.trivialTagComboBox.setEditingMode("editable");
		}
	}

	replaceFreeTextEntry(freeText: string, record: DtoComboBoxTreeRecord): void {
		const selectedEntries = this.trivialTagComboBox.getSelectedEntries();
		var changed = false;
		for (let i = 0; i < selectedEntries.length; i++) {
			const entry = selectedEntries[i];
			if (isFreeTextEntry(entry) && entry.asString === freeText) {
				selectedEntries[i] = record;
				changed = true;
			}
		}
		if (changed) {
			this.setCommittedValue(selectedEntries);
		}
	}

	setDropDownButtonVisible(dropDownButtonVisible: boolean) {
		this.config.dropDownButtonVisible = dropDownButtonVisible;
		this.trivialTagComboBox.setShowTrigger(dropDownButtonVisible);
	}

	setShowDropDownAfterResultsArrive(showDropDownAfterResultsArrive: boolean) {
		this.config.showDropDownAfterResultsArrive = showDropDownAfterResultsArrive;
		this.trivialTagComboBox.setShowDropDownOnResultsOnly(showDropDownAfterResultsArrive);
	}

	setFirstEntryAutoHighlight(firstEntryAutoSelectEnabled: boolean) {
		this.config.firstEntryAutoHighlight = firstEntryAutoSelectEnabled;
		this.trivialTagComboBox.setPreselectFirstQueryResult(firstEntryAutoSelectEnabled);
	}

	setTextHighlightingEntryLimit(textHighlightingEntryLimit: number) {
		this.config.textHighlightingEntryLimit = textHighlightingEntryLimit;
		this.treeBoxDropdown.setTextHighlightingEntryLimit(textHighlightingEntryLimit);
	}

	setAutoCompletionEnabled(autoCompletionEnabled: boolean) {
		this.config.autoCompletionEnabled = autoCompletionEnabled;
		this.trivialTagComboBox.setAutoComplete(autoCompletionEnabled);
	}

	setFreeTextEnabled(freeTextEnabled: boolean) {
		this.config.freeTextEnabled = freeTextEnabled;
	}

	setClearButtonEnabled(clearButtonEnabled: boolean) {
		this.config.clearButtonEnabled = clearButtonEnabled;
		this.trivialTagComboBox.setShowClearButton(clearButtonEnabled);
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
		this.trivialTagComboBox.setPlaceholderText(placeholderText);
	}

	setDropDownMinWidth(dropDownMinWidth: number) {
		this.trivialTagComboBox.setDropdownMinWidth(dropDownMinWidth);
	}

	setDropDownMaxHeight(dropDownMaxHeight: number) {
		this.trivialTagComboBox.setDropdownMaxHeight(dropDownMaxHeight);
	}

	setIndentation(indentation: number) {
		this.trivialTreeBox.setIndentationWidth(indentation);
	}

	setMaxEntries(maxEntries: number) {
		this.config.maxEntries = maxEntries;
	}

	setWrappingMode(wrappingMode: DtoTagComboBoxWrappingMode) {
		this.config.wrappingMode = wrappingMode;
		this.trivialTagComboBox.getMainDomElement().classList.toggle("wrapping-mode-single-line", wrappingMode === DtoTagComboBoxWrappingMode.SINGLE_LINE);
		this.trivialTagComboBox.getMainDomElement().classList.toggle("wrapping-mode-single-tag-per-line", wrappingMode === DtoTagComboBoxWrappingMode.SINGLE_TAG_PER_LINE);
	}

	setDistinct(distinct: boolean) {
		this.config.distinct = distinct;
	}

	setTwoStepDeletionEnabled(twoStepDeletionEnabled: boolean) {
		this.trivialTagComboBox.setTwoStepDeletion(twoStepDeletionEnabled);
	}

	setDeleteButtonsEnabled(deleteButtonsEnabled: boolean) {
		this.config.deleteButtonsEnabled = deleteButtonsEnabled;
		this.trivialTagComboBox.setSelectedEntries(this.trivialTagComboBox.getSelectedEntries());
	}

	destroy(): void {
		super.destroy();
		this.trivialTagComboBox.destroy();
		this.$originalInput.remove();
	}


	public getReadOnlyHtml(records: DtoComboBoxTreeRecord[], availableWidth: number): string {
		let content: string;
		if (records != null) {
			content = records.map((record) => {
				return wrapWithDefaultTagWrapper(this.renderRecord(record, false));
			}).join("");
		} else {
			content = "";
		}
		return `<div class="static-readonly-UiTagComboBox">${content}</div>`
	}

	getDefaultValue(): any[] {
		return [];
	}

	public valuesChanged(v1: DtoComboBoxTreeRecord[], v2: DtoComboBoxTreeRecord[]): boolean {
		if (v1 == null && v2 == null) {
			return false;
		}
		let nullAndNonNull = ((v1 == null) !== (v2 == null));
		let differentArrayLengths = v1 != null && v2 != null && v1.length !== v2.length;
		if (nullAndNonNull || differentArrayLengths) {
			return true;
		} else {
			for (let i = 0; i < v1.length; i++) {
				if (v1[i].id !== v2[i].id) {
					return true;
				}
			}
			return false;
		}
	}
}


