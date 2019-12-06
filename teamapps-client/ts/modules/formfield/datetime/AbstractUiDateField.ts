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
import * as Mustache from "mustache";
import {ResultCallback, TrivialComboBox, TrivialDateSuggestionEngine} from "trivial-components";
import moment from "moment-timezone";
import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {UiField} from "../UiField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../../generated/UiTextInputHandlingFieldConfig";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiSpecialKey} from "../../../generated/UiSpecialKey";
import {AbstractUiDateFieldCommandHandler, AbstractUiDateFieldConfig, AbstractUiDateFieldEventSource} from "../../../generated/AbstractUiDateFieldConfig";
import {convertJavaDateTimeFormatToMomentDateTimeFormat, parseHtml} from "../../Common";
import Moment = moment.Moment;

interface DateSuggestion {
	moment: Moment;
	ymdOrder: string;
}

export interface DateComboBoxEntry {
	day: number,
	weekDay: string,
	month: number,
	year: number,
	displayString: string
}

export abstract class AbstractUiDateField<C extends AbstractUiDateFieldConfig, V> extends UiField<C, V> implements AbstractUiDateFieldEventSource, AbstractUiDateFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, 250);

	public static comboBoxTemplate = `<div class="tr-template-icon-single-line">
    <svg viewBox="0 0 540 540" width="22" height="22" class="calendar-icon">
        <defs>
            <linearGradient id="Gradient1" x1="0" x2="0" y1="0" y2="1">
                <stop class="calendar-symbol-ring-gradient-stop1" offset="0%"></stop>
                <stop class="calendar-symbol-ring-gradient-stop2" offset="50%"></stop>
                <stop class="calendar-symbol-ring-gradient-stop3" offset="100%"></stop>
            </linearGradient>
        </defs>        
        <g id="layer1">
            <rect class="calendar-symbol-page-background" x="90" y="90" width="360" height="400" ry="3.8"></rect>
            <rect class="calendar-symbol-color" x="90" y="90" width="360" height="100" ry="3.5"></rect>
            <rect class="calendar-symbol-page" x="90" y="90" width="360" height="395" ry="3.8"></rect>
            <rect class="calendar-symbol-ring" fill="url(#Gradient2)" x="140" y="30" width="40" height="120" ry="30.8"></rect>
            <rect class="calendar-symbol-ring" fill="url(#Gradient2)" x="250" y="30" width="40" height="120" ry="30.8"></rect>
            <rect class="calendar-symbol-ring" fill="url(#Gradient2)" x="360" y="30" width="40" height="120" ry="30.8"></rect>
            <text class="calendar-symbol-date" x="270" y="415" text-anchor="middle">{{weekDay}}</text>
        </g>
    </svg>
    <div class="content-wrapper tr-editor-area">{{displayString}}</div>
</div>`;

	private $originalInput: HTMLElement;
	protected trivialComboBox: TrivialComboBox<DateComboBoxEntry>;
	private dateSuggestionEngine: TrivialDateSuggestionEngine;

	private favorPastDates: boolean;
	private dateFormat: string;

	protected initialize(config: AbstractUiDateFieldConfig, context: TeamAppsUiContext) {
		this.$originalInput = parseHtml('<input type="text" autocomplete="off">');

		this.favorPastDates = config.favorPastDates;
		this.dateFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(config.dateFormat);

		this.updateDateSuggestionEngine();
		this.trivialComboBox = new TrivialComboBox<DateComboBoxEntry>(this.$originalInput, {
			queryFunction: (searchString: string, resultCallback: ResultCallback<DateComboBoxEntry>) => {
				this.onTextInput.fire({
					enteredString: searchString
				});
				let comboBoxEntries = this.dateSuggestionEngine.generateSuggestions(searchString, moment() as any)
					.map(s => AbstractUiDateField.createDateComboBoxEntryFromMoment(s.moment as any, this.getDateFormat()));
				resultCallback(comboBoxEntries);
			},
			showTrigger: config.showDropDownButton,
			textHighlightingEntryLimit: -1, // no highlighting!
			autoCompleteFunction: function (editorText, entry) {
				if (editorText && entry.displayString.toLowerCase().indexOf(editorText.toLowerCase()) === 0) {
					return entry.displayString;
				} else {
					return null;
				}
			},
			entryToEditorTextFunction: entry => {
				return entry.displayString;
			},
			entryRenderingFunction: (entry) => {
				return entry ? Mustache.render(AbstractUiDateField.comboBoxTemplate, entry) : "";
			},
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

	private updateDateSuggestionEngine() {
		this.dateSuggestionEngine = new TrivialDateSuggestionEngine({
			preferredDateFormat: this.getDateFormat(),
			favorPastDates: this.favorPastDates
		});
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement() as HTMLElement;
	}

	public getFocusableElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor");
	}

	protected getDateFormat() {
		return this.dateFormat || this._context.config.dateFormat;
	}

	public static createDateComboBoxEntryFromMoment(m: Moment, dateFormat: string): DateComboBoxEntry {
		return {
			day: m.date(),
			weekDay: m.format('dd'), // see UiRootPanel.setConfig()...
			month: m.month() + 1,
			year: m.year(),
			displayString: m.format(dateFormat)
		};
	}

	public static createDateComboBoxEntryFromLocalValues(year: number, month: number, day: number, dateFormat: string): DateComboBoxEntry {
		let m = moment({year: year, month: month - 1, day: day});
		return {
			day: day,
			weekDay: m.format('dd'), // see UiRootPanel.setConfig()...
			month: month,
			year: year,
			displayString: m.format(dateFormat)
		};
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

	setDateFormat(dateFormat: string): void {
		this.dateFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(dateFormat);
		this.updateDateSuggestionEngine();
		let selectedEntry = this.trivialComboBox.getSelectedEntry();
		selectedEntry.displayString = moment({year: selectedEntry.year, month: selectedEntry.month - 1, day: selectedEntry.day}).format(this.getDateFormat());
		this.trivialComboBox.setSelectedEntry(selectedEntry, true);
		this.trivialComboBox.updateEntries([]);
	}

	setFavorPastDates(favorPastDates: boolean): void {
		this.favorPastDates = favorPastDates;
		this.updateDateSuggestionEngine();
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		this.trivialComboBox.setShowTrigger(showDropDownButton);
	}

	setShowClearButton(showClearButton: boolean): void {
		this.trivialComboBox.setShowClearButton(showClearButton);
	}

}
