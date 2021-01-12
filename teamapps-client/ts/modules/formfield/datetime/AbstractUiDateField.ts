/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {ResultCallback} from "../../trivial-components/TrivialCore";
import {TrivialComboBox} from "../../trivial-components/TrivialComboBox";
import {DateSuggestionEngine} from "./DateSuggestionEngine";
import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {UiField} from "../UiField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {
	UiTextInputHandlingField_SpecialKeyPressedEvent,
	UiTextInputHandlingField_TextInputEvent
} from "../../../generated/UiTextInputHandlingFieldConfig";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiSpecialKey} from "../../../generated/UiSpecialKey";
import {
	AbstractUiDateFieldCommandHandler,
	AbstractUiDateFieldConfig,
	AbstractUiDateFieldEventSource
} from "../../../generated/AbstractUiDateFieldConfig";
import {parseHtml} from "../../Common";
import {UiDateTimeFormatDescriptorConfig} from "../../../generated/UiDateTimeFormatDescriptorConfig";
import {LocalDateTime} from "../../util/LocalDateTime";
import {createDateRenderer} from "./datetime-rendering";

export abstract class AbstractUiDateField<C extends AbstractUiDateFieldConfig, V> extends UiField<C, V> implements AbstractUiDateFieldEventSource, AbstractUiDateFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, {throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, {throttlingMode: "debounce", delay: 250});

	private $originalInput: HTMLElement;
	protected trivialComboBox: TrivialComboBox<LocalDateTime>;
	protected dateSuggestionEngine: DateSuggestionEngine;
	protected dateRenderer: (time: LocalDateTime) => string;

	protected initialize(config: AbstractUiDateFieldConfig, context: TeamAppsUiContext) {
		this.$originalInput = parseHtml('<input type="text" autocomplete="off">');

		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();

		this.trivialComboBox = new TrivialComboBox<LocalDateTime>(this.$originalInput, {
			queryFunction: (searchString: string, resultCallback: ResultCallback<LocalDateTime>) => {
				this.onTextInput.fire({enteredString: searchString});
				resultCallback(this.dateSuggestionEngine.generateSuggestions(searchString, LocalDateTime.local()));
			},
			showTrigger: config.showDropDownButton,
			textHighlightingEntryLimit: -1, // no highlighting!
			autoCompleteFunction: (editorText, entry) => {
				let entryAsString = this.localDateTimeToString(entry);
				if (editorText && entryAsString.toLowerCase().indexOf(editorText.toLowerCase()) === 0) {
					return entryAsString;
				} else {
					return null;
				}
			},
			entryToEditorTextFunction: entry => {
				return this.localDateTimeToString(entry);
			},
			entryRenderingFunction: (localDateTime) => this.dateRenderer(localDateTime),
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			showClearButton: config.showClearButton
		});
		this.trivialComboBox.getMainDomElement().classList.add("AbstractUiDateField");
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

	protected localDateTimeToString(entry: LocalDateTime): string {
		return entry.setLocale(this._config.locale).toLocaleString(this._config.dateFormat);
	}

	protected createDateRenderer(): (time: LocalDateTime) => string {
		let dateRenderer = createDateRenderer(this._config.locale, this._config.dateFormat);
		return entry => dateRenderer(entry?.toUTC());
	}

	private updateDateSuggestionEngine() {
		this.dateSuggestionEngine = new DateSuggestionEngine({
			locale: this._config.locale,
			favorPastDates: this._config.favorPastDates
		});
	}

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

	destroy(): void {
		super.destroy();
		this.trivialComboBox.destroy();
		this.$originalInput.remove();
	}

	getDefaultValue(): V {
		return null;
	}

	setLocaleAndDateFormat(locale: string, dateFormat: UiDateTimeFormatDescriptorConfig): void {
		this._config.locale = locale;
		this._config.dateFormat = dateFormat;
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.trivialComboBox.setSelectedEntry(this.trivialComboBox.getSelectedEntry(), true);
		this.trivialComboBox.updateEntries([]);
	}

	setFavorPastDates(favorPastDates: boolean): void {
		this._config.favorPastDates = favorPastDates;
		this.updateDateSuggestionEngine();
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		this.trivialComboBox.setShowTrigger(showDropDownButton);
	}

	setShowClearButton(showClearButton: boolean): void {
		this.trivialComboBox.setShowClearButton(showClearButton);
	}

}
