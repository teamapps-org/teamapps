/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {TrivialComboBox} from "../../trivial-components/TrivialComboBox";
import {TimeSuggestionEngine} from "./TimeSuggestionEngine";
import {UiField} from "./../UiField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {
	UiTextInputHandlingField_SpecialKeyPressedEvent,
	UiTextInputHandlingField_TextInputEvent
} from "../../../generated/UiTextInputHandlingFieldConfig";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiSpecialKey} from "../../../generated/UiSpecialKey";
import {
	AbstractUiTimeFieldCommandHandler,
	AbstractUiTimeFieldConfig,
	AbstractUiTimeFieldEventSource
} from "../../../generated/AbstractUiTimeFieldConfig";
import {parseHtml} from "../../Common";
import {UiDateTimeFormatDescriptorConfig} from "../../../generated/UiDateTimeFormatDescriptorConfig";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {TreeBoxDropdown} from "../../trivial-components/dropdown/TreeBoxDropdown";
import {TrivialTreeBox} from "../../trivial-components/TrivialTreeBox";


export abstract class AbstractUiTimeField<C extends AbstractUiTimeFieldConfig, V> extends UiField<C, V> implements AbstractUiTimeFieldEventSource, AbstractUiTimeFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, {throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, {throttlingMode: "debounce", delay: 250});

	protected trivialComboBox: TrivialComboBox<LocalDateTime>;
	protected timeRenderer: (time: LocalDateTime) => string;

	protected initialize(config: AbstractUiTimeFieldConfig, context: TeamAppsUiContext) {
		let timeSuggestionEngine = new TimeSuggestionEngine();
		this.timeRenderer = this.createTimeRenderer();

		this.trivialComboBox = new TrivialComboBox<LocalDateTime>({
			showTrigger: config.showDropDownButton,
			autoCompleteFunction: (editorText, entry) => {
				let entryAsString = this.localDateTimeToString(entry);
				if (editorText && entryAsString.toLowerCase().indexOf(editorText.toLowerCase()) === 0) {
					return entryAsString;
				} else {
					return null;
				}
			},
			entryToEditorTextFunction: entry => this.localDateTimeToString(entry),
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			selectedEntryRenderingFunction: localDateTime => this.timeRenderer(localDateTime),
		}, new TreeBoxDropdown(new TrivialTreeBox<LocalDateTime>({
			entryRenderingFunction: localDateTime => this.timeRenderer(localDateTime),
		}), {
			queryFunction: (searchString: string) => {
				this.onTextInput.fire({enteredString: searchString});
				return timeSuggestionEngine.generateSuggestions(searchString);
			},
			textHighlightingEntryLimit: -1, // no highlighting!
			preselectionMatcher: (query, entry) => this.localDateTimeToString(entry).toLowerCase().indexOf(query.toLowerCase()) >= 0
		}));
		this.trivialComboBox.getMainDomElement().classList.add("AbstractUiTimeField");
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

		this.trivialComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
		this.trivialComboBox.onFocus.addListener(() => this.getMainElement().classList.add("focus"));
		this.trivialComboBox.onBlur.addListener(() => this.getMainElement().classList.remove("focus"));
	}

	protected abstract localDateTimeToString(entry: LocalDateTime): string;

	protected abstract createTimeRenderer(): (time: LocalDateTime) => string;

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement() as HTMLElement;
	}

	public getFocusableElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor");
	}

	focus(): void {
		this.trivialComboBox.focus();
	}

	public hasFocus(): boolean {
		return this.getMainInnerDomElement().matches('.focus');
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainInnerDomElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainInnerDomElement().classList.add(UiField.editingModeCssClasses[editingMode]);
		if (editingMode === UiFieldEditingMode.READONLY) {
			this.trivialComboBox.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
			this.trivialComboBox.setEditingMode("disabled");
		} else {
			this.trivialComboBox.setEditingMode("editable");
		}
	}

	destroy(): void {
		super.destroy();
		this.trivialComboBox.destroy();
	}

	getDefaultValue(): V {
		return null;
	}

	setLocaleAndTimeFormat(locale: string, timeFormat: UiDateTimeFormatDescriptorConfig): void {
		this._config.locale = locale;
		this._config.timeFormat = timeFormat;
		this.timeRenderer = this.createTimeRenderer();
		this.trivialComboBox.setSelectedEntry(this.trivialComboBox.getSelectedEntry(), true);
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		this.trivialComboBox.setShowTrigger(showDropDownButton);
	}

	setShowClearButton(showClearButton: boolean): void {
		this.trivialComboBox.setShowClearButton(showClearButton);
	}

}
