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
import {AbstractUiDateFieldConfig} from "../../../generated/AbstractUiDateFieldConfig";
import {arraysEqual} from "../../Common";
import {UiDateTimeFormatDescriptorConfig} from "../../../generated/UiDateTimeFormatDescriptorConfig";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {createDateRenderer} from "./datetime-rendering";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {
	UiLocalDateFieldCommandHandler,
	UiLocalDateFieldConfig,
	UiLocalDateFieldEventSource
} from "../../../generated/UiLocalDateFieldConfig";
import {TreeBoxDropdown} from "../../trivial-components/dropdown/TreeBoxDropdown";
import {TrivialTreeBox} from "../../trivial-components/TrivialTreeBox";

type LocalDate = [number, number, number];

export class UiLocalDateField extends UiField<UiLocalDateFieldConfig, LocalDate> implements UiLocalDateFieldEventSource, UiLocalDateFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, {throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, {throttlingMode: "debounce", delay: 250});

	protected trivialComboBox: TrivialComboBox<LocalDateTime>;
	protected dateSuggestionEngine: DateSuggestionEngine;
	protected dateRenderer: (time: LocalDateTime) => string;

	protected initialize(config: AbstractUiDateFieldConfig, context: TeamAppsUiContext) {
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();

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
			entryToEditorTextFunction: entry => {
				return this.localDateTimeToString(entry);
			},
			selectedEntryRenderingFunction: (localDateTime) => this.dateRenderer(localDateTime),
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			showClearButton: config.showClearButton
		}, new TreeBoxDropdown(new TrivialTreeBox<LocalDateTime>({
			entryRenderingFunction: (localDateTime) => this.dateRenderer(localDateTime),
		}), {
			queryFunction: (searchString: string) => {
				this.onTextInput.fire({enteredString: searchString});
				return this.dateSuggestionEngine.generateSuggestions(searchString, LocalDateTime.local());
			},
			textHighlightingEntryLimit: -1, // no highlighting!
		}));
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

		this.getMainInnerDomElement().classList.add("UiLocalDateField");
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
		return this.trivialComboBox.getMainDomElement();
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

	isValidData(v: LocalDate): boolean {
		return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number" && typeof v[2] === "number");
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialComboBox.setSelectedEntry(UiLocalDateField.localDateToLocalDateTime(uiValue), true);
		} else {
			this.trivialComboBox.setSelectedEntry(null, true);
		}
	}

	public getTransientValue(): LocalDate {
		let selectedEntry = this.trivialComboBox.getSelectedEntry();
		if (selectedEntry) {
			return [selectedEntry.year, selectedEntry.month, selectedEntry.day];
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: LocalDate, availableWidth: number): string {
		if (value != null) {
			return this.dateRenderer(UiLocalDateField.localDateToLocalDateTime(value));
		} else {
			return "";
		}
	}

	public valuesChanged(v1: LocalDate, v2: LocalDate): boolean {
		return !arraysEqual(v1, v2);
	}

	private static localDateToLocalDateTime(uiValue: [number, number, number]) {
		return LocalDateTime.fromObject({
			year: uiValue[0],
			month: uiValue[1],
			day: uiValue[2]
		});
	}

	destroy(): void {
		super.destroy();
		this.trivialComboBox.destroy();
	}

	setLocaleAndDateFormat(locale: string, dateFormat: UiDateTimeFormatDescriptorConfig): void {
		this._config.locale = locale;
		this._config.dateFormat = dateFormat;
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.trivialComboBox.setSelectedEntry(this.trivialComboBox.getSelectedEntry(), true);
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

TeamAppsUiComponentRegistry.registerFieldClass("UiLocalDateField", UiLocalDateField);

