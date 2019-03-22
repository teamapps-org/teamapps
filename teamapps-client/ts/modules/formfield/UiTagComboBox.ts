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
import {defaultTreeQueryFunctionFactory, keyCodes, ResultCallback, trivialMatch, TrivialTagComboBox, TrivialTreeBox, wrapWithDefaultTagWrapper} from "trivial-components";

import {UiTagComboBox_WrappingMode, UiTagComboBoxConfig, UiTagComboBoxCommandHandler, UiTagComboBoxEventSource} from "../../generated/UiTagComboBoxConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComboBox_LazyChildDataRequestedEvent} from "../../generated/UiComboBoxConfig";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../generated/UiTextInputHandlingFieldConfig";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {UiComboBoxTreeRecordConfig} from "../../generated/UiComboBoxTreeRecordConfig";
import {UiTemplateConfig} from "../../generated/UiTemplateConfig";
import {isFreeTextEntry, UiComboBox} from "./UiComboBox";
import {buildObjectTree, NodeWithChildren, Renderer} from "../Common";
import {EventFactory} from "../../generated/EventFactory";

export class UiTagComboBox extends UiField<UiTagComboBoxConfig, UiComboBoxTreeRecordConfig[]> implements UiTagComboBoxEventSource, UiTagComboBoxCommandHandler {
	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent(this, 250);
	public readonly onLazyChildDataRequested: TeamAppsEvent<UiComboBox_LazyChildDataRequestedEvent> = new TeamAppsEvent(this);

	private $originalInput: JQuery;
	private trivialTagComboBox: TrivialTagComboBox<NodeWithChildren<UiComboBoxTreeRecordConfig>>;
	private templateRenderers: { [name: string]: Renderer };
	private lastResultCallback: Function;

	private freeTextIdEntryCounter = -1;

	protected initialize(config: UiTagComboBoxConfig, context: TeamAppsUiContext) {
		this.$originalInput = $('<input type="text" autocomplete="off">');

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

		this.trivialTagComboBox = new TrivialTagComboBox<NodeWithChildren<UiComboBoxTreeRecordConfig>>(this.$originalInput, {
			childrenProperty: "__children",
			expandedProperty: "expanded",
			showExpanders: config.showExpanders,
			queryFunction: queryFunction,
			entryRenderingFunction: (entry) => this.renderRecord(entry, true),
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
			lazyChildrenQueryFunction: (node: NodeWithChildren<UiComboBoxTreeRecordConfig>) => {
				this.onLazyChildDataRequested.fire(EventFactory.createUiComboBox_LazyChildDataRequestedEvent(this.getId(), node.id));
			},
			lazyChildrenFlag: entry => entry.lazyChildren,
			spinnerTemplate: `<div class="UiSpinner" style="height: 20px; width: 20px; margin: 4px auto 4px auto;"></div>`,
			textHighlightingEntryLimit: config.textHighlightingEntryLimit,
			showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
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
			idFunction: entry => entry && entry.id,
			selectionAcceptor: entry => {
				const violatesDistinctSetting = config.distinct && this.trivialTagComboBox.getSelectedEntries().map(e => e.id).indexOf(entry.id) !== -1;
				const violatesMaxEntriesSetting = !!config.maxEntries && this.trivialTagComboBox.getSelectedEntries().length >= config.maxEntries;
				const violatesFreeTextSetting = !config.allowAnyText && isFreeTextEntry(entry);
				return !violatesDistinctSetting && !violatesMaxEntriesSetting && !violatesFreeTextSetting;
			}
		});
		$(this.trivialTagComboBox.getMainDomElement())
			.addClass("UiTagComboBox")
			.toggleClass("wrapping-mode-single-line", config.wrappingMode === UiTagComboBox_WrappingMode.SINGLE_LINE)
			.toggleClass("wrapping-mode-single-tag-per-line", config.wrappingMode === UiTagComboBox_WrappingMode.SINGLE_TAG_PER_LINE);
		this.trivialTagComboBox.onSelectedEntryChanged.addListener(() => this.commit());
		$(this.trivialTagComboBox.getEditor()).on("keydown", (e) => {
			if (e.keyCode === keyCodes.escape) {
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ESCAPE));
			} else if (e.keyCode === keyCodes.enter) {
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ENTER));
			}
		});

		$(this.trivialTagComboBox.getMainDomElement()).addClass("field-border field-border-glow field-background");
		$(this.trivialTagComboBox.getMainDomElement()).find(".tr-editor").addClass("field-background");
		$(this.trivialTagComboBox.getMainDomElement()).find(".tr-trigger").addClass("field-border");
		this.trivialTagComboBox.onFocus.addListener(() => this.getMainDomElement().addClass("focus"));
		this.trivialTagComboBox.onBlur.addListener(() => this.getMainDomElement().removeClass("focus"));
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

	isValidData(v: any[]): boolean {
		return v == null || Array.isArray(v);
	}

	setDropDownData(data: UiComboBoxTreeRecordConfig[]): void {
		const objectTree = buildObjectTree(data, "id", "parentId");
		if (this.lastResultCallback != null) {
			this.lastResultCallback(objectTree);
		} else {
			this.trivialTagComboBox.updateEntries(objectTree);
		}
		if (this._config.showDropDownAfterResultsArrive && data.length > 0 && this.hasFocus()) {
			this.trivialTagComboBox.openDropDown();
		}
	}

	setChildNodes(parentId: number, recordList: UiComboBoxTreeRecordConfig[]): void {
		const objectTree = buildObjectTree(recordList, "id", "parentId");
		(this.trivialTagComboBox.getDropDownComponent() as TrivialTreeBox<NodeWithChildren<UiComboBoxTreeRecordConfig>>).updateChildren(parentId as any, objectTree);
	}

	public getMainInnerDomElement(): JQuery {
		return $(this.trivialTagComboBox.getMainDomElement());
	}

	public getFocusableElement(): JQuery {
		return $(this.trivialTagComboBox.getMainDomElement());
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		this.trivialTagComboBox.setSelectedEntries(uiValue);
	}

	public getTransientValue(): any[] {
		return this.trivialTagComboBox.getSelectedEntries();
	}

	protected convertValueForSendingToServer(values: UiComboBoxTreeRecordConfig[]): any {
		return values.map(value => isFreeTextEntry(value) ? value.asString : value.id);
	}

	focus(): void {
		this.trivialTagComboBox.focus(); // TODO
	}

	public hasFocus(): boolean {
		return this.getMainInnerDomElement().is('.focus');
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainDomElement()
			.removeClass(Object.values(UiField.editingModeCssClasses).join(" "))
			.addClass(UiField.editingModeCssClasses[editingMode]);
		if (editingMode === UiFieldEditingMode.READONLY) {
			this.trivialTagComboBox.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
			this.trivialTagComboBox.setEditingMode("disabled");
		} else {
			this.trivialTagComboBox.setEditingMode("editable");
		}
	}

	registerTemplate(id: string, template: UiTemplateConfig): void {
		this.templateRenderers[id] = this._context.templateRegistry.createTemplateRenderer(template);
	}

	replaceFreeTextEntry(freeText: string, record: UiComboBoxTreeRecordConfig): void {
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

	doDestroy(): void {
		this.trivialTagComboBox.destroy();
		this.$originalInput.detach();
	}


	public getReadOnlyHtml(records: UiComboBoxTreeRecordConfig[], availableWidth: number): string {
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

	public valuesChanged(v1: UiComboBoxTreeRecordConfig[], v2: UiComboBoxTreeRecordConfig[]): boolean {
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

TeamAppsUiComponentRegistry.registerFieldClass("UiTagComboBox", UiTagComboBox);
