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
import {wrapWithDefaultTagWrapper} from "../trivial-components/TrivialCore";
import {TrivialTagComboBox} from "../trivial-components/TrivialTagComboBox";

import {
	UiTagComboBox_WrappingMode,
	UiTagComboBoxCommandHandler,
	DtoTagComboBox,
	UiTagComboBoxEventSource
} from "../../generated/DtoTagComboBox";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "teamapps-client-core";

import {TeamAppsEvent} from "teamapps-client-core";
import {
	UiTextInputHandlingField_SpecialKeyPressedEvent,
	UiTextInputHandlingField_TextInputEvent
} from "../../generated/DtoTextInputHandlingField";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {DtoComboBoxTreeRecord} from "../../generated/DtoComboBoxTreeRecord";
import {DtoTemplate} from "../../generated/DtoTemplate";
import {isFreeTextEntry} from "./UiComboBox";
import {buildObjectTree, getAutoCompleteOffValue, NodeWithChildren, parseHtml} from "../Common";
import {TreeBoxDropdown} from "../trivial-components/dropdown/TreeBoxDropdown";
import {TrivialTreeBox} from "../trivial-components/TrivialTreeBox";

export class UiTagComboBox extends UiField<DtoTagComboBox, DtoComboBoxTreeRecord[]> implements UiTagComboBoxEventSource, UiTagComboBoxCommandHandler {
	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent({throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent({throttlingMode: "debounce", delay: 250});

	private $originalInput: HTMLElement;
	private trivialTagComboBox: TrivialTagComboBox<NodeWithChildren<DtoComboBoxTreeRecord>>;
	private templateRenderers: { [name: string]: Renderer };
	private resultCallbacksQueue: ((result: NodeWithChildren<DtoComboBoxTreeRecord>[]) => void)[] = [];

	private freeTextIdEntryCounter = -1;

	protected initialize(config: DtoTagComboBox) {
		this.$originalInput = parseHtml(`<input type="text" autocomplete="${getAutoCompleteOffValue()}">`);

		this.templateRenderers = config.templates != null ? context.templateRegistry.createTemplateRenderers(config.templates) : {};

		this.trivialTagComboBox = new TrivialTagComboBox<NodeWithChildren<DtoComboBoxTreeRecord>>({
			selectedEntryRenderingFunction: (entry) => {
				if (isFreeTextEntry(entry)) {
					return wrapWithDefaultTagWrapper(`<div class="free-text-entry">${entry.asString}</div>`, config.showClearButton);
				} else {
					return wrapWithDefaultTagWrapper(this.renderRecord(entry, false), config.showClearButton);
				}
			},
			autoComplete: !!config.autoComplete,
			showTrigger: config.showDropDownButton,
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			spinnerTemplate: `<div class="teamapps-spinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			textHighlightingEntryLimit: config.textHighlightingEntryLimit,
			showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
			entryToEditorTextFunction: e => e.asString,
			freeTextEntryFactory: (freeText) => {
				return {id: this.freeTextIdEntryCounter--, values: {}, asString: freeText};
			},
			selectionAcceptor: entry => {
				const violatesDistinctSetting = config.distinct && this.trivialTagComboBox.getSelectedEntries().map(e => e.id).indexOf(entry.id) !== -1;
				const violatesMaxEntriesSetting = !!config.maxEntries && this.trivialTagComboBox.getSelectedEntries().length >= config.maxEntries;
				const violatesFreeTextSetting = !config.allowAnyText && isFreeTextEntry(entry);
				return !violatesDistinctSetting && !violatesMaxEntriesSetting && !violatesFreeTextSetting;
			},
			twoStepDeletion: this.config.twoStepDeletion,
			preselectFirstQueryResult: config.highlightFirstResultEntry,
			placeholderText: config.placeholderText,
			dropDownMaxHeight: config.dropDownMaxHeight,
			dropDownMinWidth: config.dropDownMinWidth
		}, new TreeBoxDropdown({
			queryFunction: (queryString: string) => {
				this.onTextInput.fire({enteredString: queryString}); // TODO this is definitely the wrong place for this!!
				return config.retrieveDropdownEntries({queryString})
					.then(entries => buildObjectTree(entries, "id", "parentId"));
			},
			textHighlightingEntryLimit: config.textHighlightingEntryLimit,
			preselectionMatcher: (query, entry) => entry.asString.toLowerCase().indexOf(query.toLowerCase()) >= 0
		}, new TrivialTreeBox<NodeWithChildren<DtoComboBoxTreeRecord>>({
			childrenProperty: "__children",
			expandedProperty: "expanded",
			showExpanders: config.showExpanders,
			entryRenderingFunction: entry => this.renderRecord(entry, true),
			idFunction: entry => entry && entry.id,
			lazyChildrenQueryFunction: async (node: NodeWithChildren<DtoComboBoxTreeRecord>) => buildObjectTree(await config.lazyChildren({parentId: node.id}), "id", "parentId"),
			lazyChildrenFlag: entry => entry.lazyChildren,
			selectableDecider: entry => entry.selectable,
			selectOnHover: true,
			animationDuration: this.config.animate ? 120 : 0
		})));
		this.trivialTagComboBox.getMainDomElement().classList.add("UiTagComboBox");
		this.trivialTagComboBox.getMainDomElement().classList.toggle("wrapping-mode-single-line", config.wrappingMode === UiTagComboBox_WrappingMode.SINGLE_LINE);
		this.trivialTagComboBox.getMainDomElement().classList.toggle("wrapping-mode-single-tag-per-line", config.wrappingMode === UiTagComboBox_WrappingMode.SINGLE_TAG_PER_LINE);
		this.trivialTagComboBox.onValueChanged.addListener(() => this.commit());
		this.trivialTagComboBox.getEditor().addEventListener("keydown", (e: KeyboardEvent) => {
			if (e.key === "Escape") {
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ESCAPE
				});
			} else if (e.key === "Enter") {
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ENTER
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
		const templateId = dropdown ? record.dropDownTemplateId : record.displayTemplateId;
		if (templateId != null && this.templateRenderers[templateId] != null) {
			const renderer = this.templateRenderers[templateId];
			return renderer.render(record.values);
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

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainElement().classList.add(UiField.editingModeCssClasses[editingMode]);
		if (editingMode === UiFieldEditingMode.READONLY) {
			this.trivialTagComboBox.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
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


