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
import {TrivialComboBox} from "../trivial-components/TrivialComboBox";
import {TrivialTreeBox} from "../trivial-components/TrivialTreeBox";

import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "teamapps-client-core";
import {UiComboBoxCommandHandler, DtoComboBox, UiComboBoxEventSource} from "../../generated/DtoComboBox";
import {TeamAppsEvent} from "teamapps-client-core";

import {
	UiTextInputHandlingField_SpecialKeyPressedEvent,
	UiTextInputHandlingField_TextInputEvent
} from "../../generated/DtoTextInputHandlingField";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {DtoComboBoxTreeRecord} from "../../generated/DtoComboBoxTreeRecord";
import {DtoTemplate} from "../../generated/DtoTemplate";
import {buildObjectTree, NodeWithChildren, Renderer} from "../Common";
import {TreeBoxDropdown} from "../trivial-components/dropdown/TreeBoxDropdown";

export function isFreeTextEntry(o: DtoComboBoxTreeRecord): boolean {
	return o != null && o.id < 0;
}

export class UiComboBox extends UiField<DtoComboBox, DtoComboBoxTreeRecord> implements UiComboBoxEventSource, UiComboBoxCommandHandler {
	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent({throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent({throttlingMode: "debounce", delay: 250});

	private trivialComboBox: TrivialComboBox<NodeWithChildren<DtoComboBoxTreeRecord>>;
	private templateRenderers: { [name: string]: Renderer };

	private freeTextIdEntryCounter = -1;

	protected initialize(config: DtoComboBox, context: TeamAppsUiContext) {
		this.templateRenderers = config.templates != null ? context.templateRegistry.createTemplateRenderers(config.templates) : {};

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
			autoComplete: !!config.autoComplete,
			showTrigger: config.showDropDownButton,
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',

			showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
			showClearButton: config.showClearButton,
			entryToEditorTextFunction: e => e.asString,
			textToEntryFunction: (freeText) => {
				if (config.allowAnyText) {
					return {id: this.freeTextIdEntryCounter--, values: {}, asString: freeText};
				} else {
					return null;
				}
			},
			preselectFirstQueryResult: config.highlightFirstResultEntry,
			placeholderText: config.placeholderText,
			dropDownMaxHeight: config.dropDownMaxHeight,
			dropDownMinWidth: config.dropDownMinWidth
		}, new TreeBoxDropdown({
			queryFunction: (queryString: string) => {
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
			animationDuration: this._config.animate ? 120 : 0
		})));
		this.trivialComboBox.getMainDomElement().classList.add("UiComboBox");
		this.trivialComboBox.onSelectedEntryChanged.addListener(() => this.commit());
		this.trivialComboBox.getEditor().addEventListener("keydown", (e: KeyboardEvent) => {
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
		this.trivialComboBox.getEditor().addEventListener("input", e => this.onTextInput.fire({enteredString: (e.target as HTMLInputElement).value}));

		this.trivialComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
	}

	protected initFocusHandling() {
		this.trivialComboBox.onFocus.addListener(() => this.onFocus.fire({}));
		this.trivialComboBox.onBlur.addListener(() => this.onBlur.fire({}));
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

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainElement().classList.add(UiField.editingModeCssClasses[editingMode]);
		if (editingMode === UiFieldEditingMode.READONLY) {
			this.trivialComboBox.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
			this.trivialComboBox.setEditingMode("disabled");
		} else {
			this.trivialComboBox.setEditingMode("editable");
		}
	}

	registerTemplate(id: string, template: DtoTemplate): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
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
			return `<div class="static-readonly-UiComboBox">${this.renderRecord(value, false)}</div>`;
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
}


