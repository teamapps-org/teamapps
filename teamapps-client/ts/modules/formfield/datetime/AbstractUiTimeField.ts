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
import Mustache from "mustache";
import moment from "moment-timezone";

import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {TrivialComboBox} from "../../trivial-components/TrivialComboBox";
import {TrivialTimeSuggestionEngine} from "../../trivial-components/TrivialTimeSuggestionEngine";
import {UiField} from "./../UiField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../../generated/UiTextInputHandlingFieldConfig";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiSpecialKey} from "../../../generated/UiSpecialKey";
import {AbstractUiTimeFieldCommandHandler, AbstractUiTimeFieldConfig, AbstractUiTimeFieldEventSource} from "../../../generated/AbstractUiTimeFieldConfig";
import {convertJavaDateTimeFormatToMomentDateTimeFormat, parseHtml} from "../../Common";

export abstract class AbstractUiTimeField<C extends AbstractUiTimeFieldConfig, V> extends UiField<C, V> implements AbstractUiTimeFieldEventSource, AbstractUiTimeFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, 250);

	public static comboBoxTemplate = '<div class="tr-template-icon-single-line">' +
		'<svg class="clock-icon night-{{isNight}}" viewBox="0 0 110 110" width="22" height="22"> ' +
		'<circle class="clockcircle" cx="55" cy="55" r="45"></circle>' +
		'<g class="hands">' +
		' <line class="hourhand" x1="55" y1="55" x2="55" y2="35" transform="rotate({{hourAngle}},55,55)"></line> ' +
		' <line class="minutehand" x1="55" y1="55" x2="55" y2="22" transform="rotate({{minuteAngle}},55,55)"></line>' +
		'</g> ' +
		'</svg>' +
		'  <div class="content-wrapper tr-editor-area">{{displayString}}</div>' +
		'</div>';

	private $originalInput: HTMLElement;
	protected trivialComboBox: TrivialComboBox<any>;
	private timeFormat: string;

	protected initialize(config: AbstractUiTimeFieldConfig, context: TeamAppsUiContext) {
		this.$originalInput = parseHtml('<input type="text" autocomplete="off">');

		this.timeFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(config.timeFormat);

		let timeSuggestionEngine = new TrivialTimeSuggestionEngine();
		this.trivialComboBox = new TrivialComboBox<any>(this.$originalInput, {
			queryFunction: (searchString: string, resultCallback: Function) => {
				this.onTextInput.fire({
					enteredString: searchString
				});

				let comboBoxEntries = timeSuggestionEngine.generateSuggestions(searchString)
					.map(s => AbstractUiTimeField.createTimeComboBoxEntry(s.hour, s.minute, this.getTimeFormat()));
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
			entryToEditorTextFunction: entry => entry.displayString,
			entryRenderingFunction: (entry) => Mustache.render(AbstractUiTimeField.comboBoxTemplate, entry || {hourAngle: 0, minuteAngle: 0}),
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable'
		});
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

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement() as HTMLElement;
	}

	public getFocusableElement(): HTMLElement {
		return this.trivialComboBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor");
	}

	protected getTimeFormat() {
		return this.timeFormat || this._context.config.timeFormat;
	}


	focus(): void {
		this.trivialComboBox.focus();
	}

	public hasFocus(): boolean {
		return this.getMainInnerDomElement().matches('.focus');
	}

	private static intRange(fromInclusive: number, toInclusive: number) {
		const ints = [];
		for (let i = fromInclusive; i <= toInclusive; i++) {
			ints.push(i)
		}
		return ints;
	}

	private static pad(num: number, size: number) {
		let s = num + "";
		while (s.length < size) s = "0" + s;
		return s;
	}

	public static createTimeComboBoxEntry(h: number, m: number, timeFormat: string) {
		return {
			hour: h,
			minute: m,
			hourString: AbstractUiTimeField.pad(h, 2),
			minuteString: AbstractUiTimeField.pad(m, 2),
			displayString: moment().hour(h).minute(m).second(0).millisecond(0).format(timeFormat),
			hourAngle: ((h % 12) + m / 60) * 30,
			minuteAngle: m * 6,
			isNight: h < 6 || h >= 20
		};
	}

	public static createTimeComboBoxEntryFromMoment(mom: moment.Moment, timeFormat: string) {
		return {
			hour: mom.hour(),
			minute: mom.minute(),
			hourString: AbstractUiTimeField.pad(mom.hour(), 2),
			minuteString: AbstractUiTimeField.pad(mom.minute(), 2),
			displayString: mom.format(timeFormat),
			hourAngle: ((mom.hour() % 12) + mom.minute() / 60) * 30,
			minuteAngle: mom.minute() * 6,
			isNight: mom.hour() < 6 || mom.hour() >= 20
		};
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
		this.$originalInput.remove();
	}

	getDefaultValue(): V {
		return null;
	}

	setTimeFormat(timeFormat: string): void {
		this.timeFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(timeFormat);
		let selectedEntry = this.trivialComboBox.getSelectedEntry();
		selectedEntry.displayString = selectedEntry.moment.format(this.getTimeFormat());
		this.trivialComboBox.setSelectedEntry(selectedEntry, true);
		this.trivialComboBox.updateEntries([]);
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		this.trivialComboBox.setShowTrigger(showDropDownButton);
	}

	setShowClearButton(showClearButton: boolean): void {
		this.trivialComboBox.setShowClearButton(showClearButton);
	}
}
