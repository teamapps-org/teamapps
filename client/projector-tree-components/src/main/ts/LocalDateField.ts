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
import {DateSuggestionEngine} from "./DateSuggestionEngine";
import {LocalDateTime} from "./LocalDateTime";
import {createDateRenderer} from "./datetime-rendering";
import {CalendarBoxDropdown} from "./trivial-components/dropdown/CalendarBoxDropdown";
import {TrivialCalendarBox} from "./trivial-components/TrivialCalendarBox";
import {
	DateFieldDropDownMode,
	DtoLocalDate,
	DtoLocalDateField,
	DtoLocalDateField_TextInputEvent,
	DtoLocalDateFieldCommandHandler,
	DtoLocalDateFieldEventSource
} from "./generated";
import {TrivialComboBox} from "./trivial-components/TrivialComboBox";
import {TreeBoxDropdown} from "./trivial-components/dropdown/TreeBoxDropdown";
import {TrivialTreeBox} from "./trivial-components/TrivialTreeBox";
import {
	AbstractField,
	DebounceMode,
	deepEquals,
	DtoDateTimeFormatDescriptor, executeWhenFirstDisplayed,
	FieldEditingMode,
	ProjectorEvent
} from "projector-client-object-api";
import {DtoTextInputHandlingField_SpecialKeyPressedEvent, SpecialKey} from "projector-client-core-components";

export class LocalDateField extends AbstractField<DtoLocalDateField, DtoLocalDate> implements DtoLocalDateFieldEventSource, DtoLocalDateFieldCommandHandler {

	public readonly onTextInput: ProjectorEvent<DtoLocalDateField_TextInputEvent> = ProjectorEvent.createDebounced(250, DebounceMode.BOTH);
	public readonly onSpecialKeyPressed: ProjectorEvent<DtoTextInputHandlingField_SpecialKeyPressedEvent> = ProjectorEvent.createDebounced(250, DebounceMode.BOTH);

	protected trivialComboBox: TrivialComboBox<LocalDateTime>;
	protected dateSuggestionEngine: DateSuggestionEngine;
	protected dateRenderer: (time: LocalDateTime) => string;

	private calendarBoxDropdown: CalendarBoxDropdown;

	protected initialize(config: DtoLocalDateField) {
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();

		let treeBoxDropdown = new TreeBoxDropdown({
			queryFunction: (searchString: string) => {
				return this.dateSuggestionEngine.generateSuggestions(searchString, this.getDefaultDate(), {
					shuffledFormatSuggestionsEnabled: this.config.shuffledFormatSuggestionsEnabled
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
			shuffledFormatSuggestionsEnabled: this.config.shuffledFormatSuggestionsEnabled
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
					shuffledFormatSuggestionsEnabled: this.config.shuffledFormatSuggestionsEnabled
				});
				return suggestions.length > 0 ? suggestions[0] : null;
			},
			editingMode: config.editingMode === FieldEditingMode.READONLY ? 'readonly' : config.editingMode === FieldEditingMode.DISABLED ? 'disabled' : 'editable',
			showClearButton: config.showClearButton,
			placeholderText: config.placeholderText
		}, this.config.dropDownMode === DateFieldDropDownMode.CALENDAR ? this.calendarBoxDropdown : treeBoxDropdown);
		
		[this.trivialComboBox.onBeforeQuery, this.trivialComboBox.onBeforeDropdownOpens].forEach(event => event.addListener(queryString => {
			if (this.config.dropDownMode == DateFieldDropDownMode.CALENDAR
				|| this.config.dropDownMode == DateFieldDropDownMode.CALENDAR_SUGGESTION_LIST && !queryString) {
				this.trivialComboBox.setDropDownComponent(this.calendarBoxDropdown);
			} else {
				this.trivialComboBox.setDropDownComponent(treeBoxDropdown);
			}
		}));
		this.trivialComboBox.getMainDomElement().classList.add("DtoAbstractDateField", "default-min-field-width");
		this.trivialComboBox.onSelectedEntryChanged.addListener(() => this.commit());
		this.trivialComboBox.getEditor().addEventListener("keydown", (e: KeyboardEvent) => {
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
		this.trivialComboBox.getEditor().addEventListener("input", e => this.onTextInput.fire({enteredString: (e.target as HTMLInputElement).value}));

		this.trivialComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");

		this.getMainInnerDomElement().classList.add("UiLocalDateField");
	}

	private getDefaultDate() {
		return LocalDateField.UiLocalDateToLocalDateTime(this.config.defaultSuggestionDate) ?? LocalDateTime.local();
	}

	protected localDateTimeToString(entry: LocalDateTime): string {
		return entry.setLocale(this.config.locale).toLocaleString(this.config.dateFormat);
	}

	protected createDateRenderer(): (time: LocalDateTime) => string {
		let dateRenderer = createDateRenderer(this.config.locale, this.config.dateFormat, this.config.calendarIconEnabled, "static-readonly-UiDateField");
		return entry => dateRenderer(entry?.toUTC());
	}

	private updateDateSuggestionEngine() {
		this.dateSuggestionEngine = new DateSuggestionEngine({
			locale: this.config.locale,
			favorPastDates: this.config.favorPastDates
		});
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement() as HTMLElement;
	}

	protected initFocusHandling() {
		this.trivialComboBox.onFocus.addListener(() => this.onFocus.fire({}));
		this.trivialComboBox.onBlur.addListener(() => this.onBlur.fire({}));
	}

	@executeWhenFirstDisplayed()
	focus(): void {
		this.trivialComboBox.focus();
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

	isValidData(v: DtoLocalDate): boolean {
		return v == null || v._type === "UiLocalDate";
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialComboBox.setValue(LocalDateField.UiLocalDateToLocalDateTime(uiValue));
		} else {
			this.trivialComboBox.setValue(null);
		}
	}

	public getTransientValue(): DtoLocalDate {
		let selectedEntry = this.trivialComboBox.getValue();
		if (selectedEntry) {
			return {
				year: selectedEntry.year, month: selectedEntry.month, day: selectedEntry.day
			};
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: DtoLocalDate, availableWidth: number): string {
		if (value != null) {
			return this.dateRenderer(LocalDateField.UiLocalDateToLocalDateTime(value));
		} else {
			return "";
		}
	}

	public valuesChanged(v1: DtoLocalDate, v2: DtoLocalDate): boolean {
		return !deepEquals(v1, v2);
	}

	private static UiLocalDateToLocalDateTime(uiValue: DtoLocalDate) {
		return uiValue != null ? LocalDateTime.fromObject({year: uiValue.year, month: uiValue.month, day: uiValue.day}) : null;
	}

	destroy(): void {
		super.destroy();
		this.trivialComboBox.destroy();
	}

	update(config: DtoLocalDateField): any {
		this.setShowDropDownButton(config.showDropDownButton);
		this.setShowClearButton(config.showClearButton);
		this.setFavorPastDates(config.favorPastDates);
		this.setLocaleAndDateFormat(config.locale, config.dateFormat);
		this.trivialComboBox.setPlaceholderText(config.placeholderText);
		this.calendarBoxDropdown.defaultDate = LocalDateField.UiLocalDateToLocalDateTime(config.defaultSuggestionDate);
		this.setCalendarIconEnabled(config.calendarIconEnabled);
		this.config = config;
	}

	setLocaleAndDateFormat(locale: string, dateFormat: DtoDateTimeFormatDescriptor): void {
		this.config.locale = locale;
		this.config.dateFormat = dateFormat;
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.trivialComboBox.setValue(this.trivialComboBox.getValue());
	}

	setFavorPastDates(favorPastDates: boolean): void {
		this.config.favorPastDates = favorPastDates;
		this.updateDateSuggestionEngine();
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		this.config.showDropDownButton;
		this.trivialComboBox.setShowTrigger(showDropDownButton);
	}

	setShowClearButton(showClearButton: boolean): void {
		this.config.showClearButton;
		this.trivialComboBox.setShowClearButton(showClearButton);
	}

	setCalendarIconEnabled(calendarIconEnabled: boolean) {
		this.config.calendarIconEnabled = calendarIconEnabled;
		this.dateRenderer = this.createDateRenderer();
		this.trivialComboBox.setValue(this.trivialComboBox.getValue());
	}
}



