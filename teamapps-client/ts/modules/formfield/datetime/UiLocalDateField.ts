/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {deepEquals} from "../../Common";
import {UiDateTimeFormatDescriptorConfig} from "../../../generated/UiDateTimeFormatDescriptorConfig";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {createDateRenderer} from "./datetime-rendering";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {
	UiLocalDateField_DropDownMode,
	UiLocalDateFieldCommandHandler,
	UiLocalDateFieldConfig,
	UiLocalDateFieldEventSource
} from "../../../generated/UiLocalDateFieldConfig";
import {TreeBoxDropdown} from "../../trivial-components/dropdown/TreeBoxDropdown";
import {TrivialTreeBox} from "../../trivial-components/TrivialTreeBox";
import {CalendarBoxDropdown} from "../../trivial-components/dropdown/CalendarBoxDropdown";
import {TrivialCalendarBox} from "../../trivial-components/TrivialCalendarBox";
import {createUiLocalDateConfig, UiLocalDateConfig} from "../../../generated/UiLocalDateConfig";

export class UiLocalDateField extends UiField<UiLocalDateFieldConfig, UiLocalDateConfig> implements UiLocalDateFieldEventSource, UiLocalDateFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>({
		throttlingMode: "debounce",
		delay: 250
	});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>({
		throttlingMode: "debounce",
		delay: 250
	});

	protected trivialComboBox: TrivialComboBox<LocalDateTime>;
	protected dateSuggestionEngine: DateSuggestionEngine;
	protected dateRenderer: (time: LocalDateTime) => string;

	private calendarBoxDropdown: CalendarBoxDropdown;

	protected initialize(config: UiLocalDateFieldConfig, context: TeamAppsUiContext) {
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();

		let treeBoxDropdown = new TreeBoxDropdown({
			queryFunction: (searchString: string) => {
				return this.dateSuggestionEngine.generateSuggestions(searchString, this.getDefaultDate(), {
					shuffledFormatSuggestionsEnabled: this._config.shuffledFormatSuggestionsEnabled
				});
			},
			textHighlightingEntryLimit: -1, // no highlighting!
			preselectionMatcher: (query, entry) => this.localDateTimeToString(entry).toLowerCase().indexOf(query.toLowerCase()) >= 0
		}, new TrivialTreeBox<LocalDateTime>({
			entryRenderingFunction: (localDateTime) => this.dateRenderer(localDateTime),
			selectOnHover: true
		}));

		this.calendarBoxDropdown = new CalendarBoxDropdown(new TrivialCalendarBox({
			locale: config.locale,
			// firstDayOfWeek?: config., TODO
			highlightKeyboardNavigationState: false
		}), query => this.dateSuggestionEngine.generateSuggestions(query, this.getDefaultDate(), {
			shuffledFormatSuggestionsEnabled: this._config.shuffledFormatSuggestionsEnabled
		})[0], this.getDefaultDate());

		this.trivialComboBox = new TrivialComboBox<LocalDateTime>({
			showTrigger: config.showDropDownButton,
			entryToEditorTextFunction: entry => {
				return this.localDateTimeToString(entry);
			},
			selectedEntryRenderingFunction: (localDateTime) => this.dateRenderer(localDateTime),
			textToEntryFunction: freeText => {
				let suggestions = this.dateSuggestionEngine.generateSuggestions(freeText, this.getDefaultDate(), {
					suggestAdjacentWeekForEmptyInput: false,
					shuffledFormatSuggestionsEnabled: this._config.shuffledFormatSuggestionsEnabled
				});
				return suggestions.length > 0 ? suggestions[0] : null;
			},
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			showClearButton: config.showClearButton,
			placeholderText: config.placeholderText
		}, this._config.dropDownMode === UiLocalDateField_DropDownMode.CALENDAR ? this.calendarBoxDropdown : treeBoxDropdown);
		
		[this.trivialComboBox.onBeforeQuery, this.trivialComboBox.onBeforeDropdownOpens].forEach(event => event.addListener(queryString => {
			if (this._config.dropDownMode == UiLocalDateField_DropDownMode.CALENDAR
				|| this._config.dropDownMode == UiLocalDateField_DropDownMode.CALENDAR_SUGGESTION_LIST && !queryString) {
				this.trivialComboBox.setDropDownComponent(this.calendarBoxDropdown);
			} else {
				this.trivialComboBox.setDropDownComponent(treeBoxDropdown);
			}
		}));
		this.trivialComboBox.getMainDomElement().classList.add("AbstractUiDateField", "default-min-field-width");
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

		this.getMainInnerDomElement().classList.add("UiLocalDateField");
	}

	private getDefaultDate() {
		return UiLocalDateField.UiLocalDateToLocalDateTime(this._config.defaultSuggestionDate) ?? LocalDateTime.local();
	}

	protected localDateTimeToString(entry: LocalDateTime): string {
		return entry.setLocale(this._config.locale).toLocaleString(this._config.dateFormat);
	}

	protected createDateRenderer(): (time: LocalDateTime) => string {
		let dateRenderer = createDateRenderer(this._config.locale, this._config.dateFormat, this._config.calendarIconEnabled, "static-readonly-UiDateField");
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

	protected initFocusHandling() {
		this.trivialComboBox.onFocus.addListener(() => this.onFocusGained.fire({}));
		this.trivialComboBox.onBlur.addListener(() => this.onBlur.fire({}));
	}

	focus(): void {
		this.trivialComboBox.focus();
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

	isValidData(v: UiLocalDateConfig): boolean {
		return v == null || v._type === "UiLocalDate";
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialComboBox.setValue(UiLocalDateField.UiLocalDateToLocalDateTime(uiValue));
		} else {
			this.trivialComboBox.setValue(null);
		}
	}

	public getTransientValue(): UiLocalDateConfig {
		let selectedEntry = this.trivialComboBox.getValue();
		if (selectedEntry) {
			return createUiLocalDateConfig(selectedEntry.year, selectedEntry.month, selectedEntry.day);
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: UiLocalDateConfig, availableWidth: number): string {
		if (value != null) {
			return this.dateRenderer(UiLocalDateField.UiLocalDateToLocalDateTime(value));
		} else {
			return "";
		}
	}

	public valuesChanged(v1: UiLocalDateConfig, v2: UiLocalDateConfig): boolean {
		return !deepEquals(v1, v2);
	}

	private static UiLocalDateToLocalDateTime(uiValue: UiLocalDateConfig) {
		return uiValue != null ? LocalDateTime.fromObject({year: uiValue.year, month: uiValue.month, day: uiValue.day}) : null;
	}

	destroy(): void {
		super.destroy();
		this.trivialComboBox.destroy();
	}

	update(config: UiLocalDateFieldConfig): any {
		this.setShowDropDownButton(config.showDropDownButton);
		this.setShowClearButton(config.showClearButton);
		this.setFavorPastDates(config.favorPastDates);
		this.setLocaleAndDateFormat(config.locale, config.dateFormat);
		this.trivialComboBox.setPlaceholderText(config.placeholderText);
		this.calendarBoxDropdown.defaultDate = UiLocalDateField.UiLocalDateToLocalDateTime(config.defaultSuggestionDate);
		this.setCalendarIconEnabled(config.calendarIconEnabled);
		this._config = config;
	}

	setLocaleAndDateFormat(locale: string, dateFormat: UiDateTimeFormatDescriptorConfig): void {
		this._config.locale = locale;
		this._config.dateFormat = dateFormat;
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.trivialComboBox.setValue(this.trivialComboBox.getValue());
	}

	setFavorPastDates(favorPastDates: boolean): void {
		this._config.favorPastDates = favorPastDates;
		this.updateDateSuggestionEngine();
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		this._config.showDropDownButton;
		this.trivialComboBox.setShowTrigger(showDropDownButton);
	}

	setShowClearButton(showClearButton: boolean): void {
		this._config.showClearButton;
		this.trivialComboBox.setShowClearButton(showClearButton);
	}

	setCalendarIconEnabled(calendarIconEnabled: boolean) {
		this._config.calendarIconEnabled = calendarIconEnabled;
		this.dateRenderer = this.createDateRenderer();
		this.trivialComboBox.setValue(this.trivialComboBox.getValue());
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiLocalDateField", UiLocalDateField);

