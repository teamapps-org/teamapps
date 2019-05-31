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
import * as $ from "jquery";
import {defaultTreeQueryFunctionFactory, keyCodes, ResultCallback, TrivialComboBox, trivialMatch, TrivialTreeBox} from "trivial-components";

import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiTextMatchingMode} from "../../generated/UiTextMatchingMode";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiComboBox_LazyChildDataRequestedEvent, UiComboBoxCommandHandler, UiComboBoxConfig, UiComboBoxEventSource} from "../../generated/UiComboBoxConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../generated/UiTextInputHandlingFieldConfig";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {UiComboBoxTreeRecordConfig} from "../../generated/UiComboBoxTreeRecordConfig";
import {UiTemplateConfig} from "../../generated/UiTemplateConfig";
import {buildObjectTree, NodeWithChildren, Renderer} from "../Common";
import {EventFactory} from "../../generated/EventFactory";

export function isFreeTextEntry(o: UiComboBoxTreeRecordConfig): boolean {
	return o != null && o.id < 0;
}

export class UiComboBox extends UiField<UiComboBoxConfig, UiComboBoxTreeRecordConfig> implements UiComboBoxEventSource, UiComboBoxCommandHandler {
	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent(this, 250);
	public readonly onLazyChildDataRequested: TeamAppsEvent<UiComboBox_LazyChildDataRequestedEvent> = new TeamAppsEvent(this);

	private $originalInput: JQuery;
	private trivialComboBox: TrivialComboBox<NodeWithChildren<UiComboBoxTreeRecordConfig>>;
	private templateRenderers: { [name: string]: Renderer };
	private lastResultCallback: (result: NodeWithChildren<UiComboBoxTreeRecordConfig>[]) => void;

	private freeTextIdEntryCounter = -1;

	protected initialize(config: UiComboBoxConfig, context: TeamAppsUiContext) {
		this.$originalInput = $(`<input type="text" autocomplete="off">`);

		this.templateRenderers = config.templates != null ? context.templateRegistry.createTemplateRenderers(config.templates) : {};

		let localQueryFunction: Function;
		const objectTree = buildObjectTree(config.staticData, "id", "parentId");
		if (config.staticData != null && config.staticData.length > 0) {
			let trivialMatchingOptions = UiComboBox.createTrivialMatchingOptions(config);
			localQueryFunction = defaultTreeQueryFunctionFactory(objectTree, (entry: NodeWithChildren<UiComboBoxTreeRecordConfig>, queryString: string) => {
				if (config.staticDataMatchPropertyNames) {
					return config.staticDataMatchPropertyNames.some(fieldName => entry.values[fieldName] && trivialMatch(entry.values[fieldName], queryString, trivialMatchingOptions).length > 0);
				} else {
					return trivialMatch(entry.asString, queryString, trivialMatchingOptions).length > 0;
				}
			}, "__children", "expanded");
		}
		let queryFunction = (queryString: string, resultCallback: ResultCallback<NodeWithChildren<UiComboBoxTreeRecordConfig>>) => {
			this.lastResultCallback = resultCallback;
			this.onTextInput.fire(EventFactory.createUiTextInputHandlingField_TextInputEvent(this.getId(), queryString));
			if (localQueryFunction != null) {
				localQueryFunction(queryString, resultCallback);
			}
		};

		this.trivialComboBox = new TrivialComboBox<NodeWithChildren<UiComboBoxTreeRecordConfig>>(this.$originalInput, {
			allowFreeText: config.allowAnyText,
			childrenProperty: "__children",
			expandedProperty: "expanded",
			showExpanders: config.showExpanders,
			queryFunction: queryFunction,
			entryRenderingFunction: entry => this.renderRecord(entry, true),
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
			lazyChildrenQueryFunction: (node: NodeWithChildren<UiComboBoxTreeRecordConfig>) => {
				this.onLazyChildDataRequested.fire(EventFactory.createUiComboBox_LazyChildDataRequestedEvent(this.getId(), node.id));
			},
			lazyChildrenFlag: entry => entry.lazyChildren,
			spinnerTemplate: `<div class="UiSpinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			textHighlightingEntryLimit: config.textHighlightingEntryLimit,
			showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
			showClearButton: config.showClearButton,
			entryToEditorTextFunction: e => e.asString,
			autoCompleteFunction: (editorText, entry) => {
				const entryAsString = entry.asString;
				if (entryAsString.toLowerCase().indexOf(editorText.toLowerCase()) === 0) {
					return entryAsString;
				} else {
					return "";
				}
			},
			freeTextEntryFactory: (freeText) => {
				return {id: this.freeTextIdEntryCounter--, values: {}, asString: freeText};
			},
			idFunction: entry => entry && entry.id
		});
		$(this.trivialComboBox.getMainDomElement()).addClass("UiComboBox");
		this.trivialComboBox.onSelectedEntryChanged.addListener(() => this.commit());
		$(this.trivialComboBox.getEditor()).on("keydown", (e) => {
			if (e.keyCode === keyCodes.escape) {
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ESCAPE));
			} else if (e.keyCode === keyCodes.enter) {
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ENTER));
			}
		});

		$(this.trivialComboBox.getMainDomElement()).addClass("field-border field-border-glow field-background");
		$(this.trivialComboBox.getMainDomElement()).find(".tr-editor").addClass("field-background");
		$(this.trivialComboBox.getMainDomElement()).find(".tr-trigger").addClass("field-border");
		this.trivialComboBox.onFocus.addListener(() => this.getMainDomElement().addClass("focus"));
		this.trivialComboBox.onBlur.addListener(() => this.getMainDomElement().removeClass("focus"));
	}

	private renderRecord(record: NodeWithChildren<UiComboBoxTreeRecordConfig>, dropdown: boolean): string {
		const templateId = dropdown ? record.dropDownTemplateId : record.displayTemplateId;
		if (templateId != null && this.templateRenderers[templateId] != null) {
			const renderer = this.templateRenderers[templateId];
			return renderer.render(record.values);
		} else {
			return `<div class="string-template">${record.asString}</div>`;
		}
	}

	isValidData(v: any): boolean {
		return v == null || typeof v === "object" && (v as UiComboBoxTreeRecordConfig).id != null;
	}

	static createTrivialMatchingOptions(config: { textMatchingMode?: UiTextMatchingMode }) {
		let matchingModes: { [x: number]: 'contains' | 'prefix' | 'prefix-word' | 'prefix-levenshtein' | 'levenshtein' } = {
			[UiTextMatchingMode.PREFIX]: "prefix",
			[UiTextMatchingMode.PREFIX_WORD]: "prefix-word",
			[UiTextMatchingMode.CONTAINS]: "contains",
			[UiTextMatchingMode.SIMILARITY]: "prefix-levenshtein"
		};
		return {
			matchingMode: matchingModes[config.textMatchingMode],
			ignoreCase: true,
			maxLevenshteinDistance: 3
		};
	}

	setDropDownData(data: UiComboBoxTreeRecordConfig[]): void {
		const objectTree = buildObjectTree(data, "id", "parentId");
		if (this.lastResultCallback != null) {
			this.lastResultCallback(objectTree);
		} else {
			this.trivialComboBox.updateEntries(objectTree);
		}
		if (this._config.showDropDownAfterResultsArrive && data.length > 0 && this.hasFocus()) {
			this.trivialComboBox.openDropDown();
		}
	}

	setChildNodes(parentId: number, recordList: UiComboBoxTreeRecordConfig[]): void {
		const objectTree = buildObjectTree(recordList, "id", "parentId");
		(this.trivialComboBox.getDropDownComponent() as TrivialTreeBox<NodeWithChildren<UiComboBoxTreeRecordConfig>>).updateChildren(parentId as any /*fix trivial-components tsd*/, objectTree);
	}

	public getMainInnerDomElement(): JQuery {
		return $(this.trivialComboBox.getMainDomElement() as any);
	}

	public getFocusableElement(): JQuery {
		return $(this.trivialComboBox.getMainDomElement() as any);
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		this.trivialComboBox.setSelectedEntry(uiValue, true);
	}

	public getTransientValue(): any {
		return this.trivialComboBox.getSelectedEntry();
	}

	protected convertValueForSendingToServer(value: UiComboBoxTreeRecordConfig): any {
		if (value == null) {
			return null;
		}
		return isFreeTextEntry(value) ? value.asString : value.id;
	}

	focus(): void {
		this.trivialComboBox.focus();
	}

	public hasFocus(): boolean {
		return this.getMainInnerDomElement().is('.focus');
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainDomElement()
			.removeClass(Object.values(UiField.editingModeCssClasses).join(" "))
			.addClass(UiField.editingModeCssClasses[editingMode]);
		if (editingMode === UiFieldEditingMode.READONLY) {
			this.trivialComboBox.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
			this.trivialComboBox.setEditingMode("disabled");
		} else {
			this.trivialComboBox.setEditingMode("editable");
		}
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	replaceFreeTextEntry(freeText: string, record: UiComboBoxTreeRecordConfig): void {
		const selectedEntry = this.trivialComboBox.getSelectedEntry();
		if (isFreeTextEntry(selectedEntry) && selectedEntry.asString === freeText) {
			this.setCommittedValue(record);
		}
	}

	doDestroy(): void {
		this.trivialComboBox.destroy();
		this.$originalInput.detach();
	}

	public getReadOnlyHtml(value: UiComboBoxTreeRecordConfig, availableWidth: number): string {
		if (value != null) {
			return `<div class="static-readonly-UiComboBox">${this.renderRecord(value, false)}</div>`;
		} else {
			return "";
		}
	}

	getDefaultValue(): any {
		return null;
	}

	public valuesChanged(v1: UiComboBoxTreeRecordConfig, v2: UiComboBoxTreeRecordConfig): boolean {
		let nullAndNonNull = ((v1 == null) !== (v2 == null));
		let nonNullAndValuesDifferent = (v1 != null && v2 != null && (
			v1.id !== v2.id
			|| v1.id !== v2.id
		));
		return nullAndNonNull || nonNullAndValuesDifferent;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiComboBox", UiComboBox);
