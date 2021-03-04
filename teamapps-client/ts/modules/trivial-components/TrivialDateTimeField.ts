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
/*!
 Trivial Components (https://github.com/trivial-components/trivial-components)

 Copyright 2016 Yann Massard (https://github.com/yamass) and other contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import {EditingMode, HighlightDirection, keyCodes, selectElementContents, TrivialComponent} from "./TrivialCore";
import {TrivialEvent} from "./TrivialEvent";
import {TrivialCalendarBox} from "./TrivialCalendarBox";
import {DateSuggestionEngine, getYearMonthDayOrderFromLocale} from "../formfield/datetime/DateSuggestionEngine";
import {TimeSuggestionEngine} from "../formfield/datetime/TimeSuggestionEngine";
import {place} from "place-to";
import {createPopper, Instance as Popper} from '@popperjs/core';
import {TrivialTreeBox} from "./TrivialTreeBox";
import {DateTime} from "luxon";
import {
	createClockIconRenderer,
	createDateIconRenderer,
	createDateRenderer,
	createTimeRenderer
} from "../formfield/datetime/datetime-rendering";
import {LocalDateTime} from "../datetime/LocalDateTime";
import KeyDownEvent = JQuery.KeyDownEvent;
import DateTimeFormatOptions = Intl.DateTimeFormatOptions;

enum Mode {
	MODE_CALENDAR,
	MODE_DATE_LIST,
	MODE_TIME_LIST
}

export interface TrivialDateTimeFieldConfig {
	locale?: string,
	timeZone?: string,
	dateFormat?: DateTimeFormatOptions,
	timeFormat?: DateTimeFormatOptions,
	autoComplete?: boolean,
	autoCompleteDelay?: number,
	showTrigger?: boolean,
	editingMode?: EditingMode,
	favorPastDates?: boolean
}

export interface LocalTime {
	hour: number,
	minute: number,
	second?: number
}

export class TrivialDateTimeField implements TrivialComponent {

	private config: TrivialDateTimeFieldConfig;

	private dateIconRenderer: (localDateTime: DateTime) => string;
	private timeIconRenderer: (localDateTime: DateTime) => string;
	private dateRenderer: (localDateTime: DateTime) => string;
	private timeRenderer: (localDateTime: DateTime) => string;

	public readonly onChange = new TrivialEvent<DateTime>(this);

	private dateListBox: TrivialTreeBox<DateTime>;
	private timeListBox: TrivialTreeBox<DateTime>;
	private calendarBox: TrivialCalendarBox;
	private _isDropDownOpen = false;

	private value: DateTime = null;

	private blurCausedByClickInsideComponent = false;
	private focusGoesToOtherEditor = false;
	private autoCompleteTimeoutId = -1;
	private doNoAutoCompleteBecauseBackspaceWasPressed = false;
	private calendarBoxInitialized = false;
	private editingMode: string;

	private dropDownMode = Mode.MODE_CALENDAR;

	private $dateTimeField: JQuery;
	private $dropDown: JQuery;
	private popper: Popper;

	private $dateIconWrapper: JQuery;
	private $dateEditor: JQuery;
	private $timeIconWrapper: JQuery;
	private $timeEditor: JQuery;
	private $dropDownTargetElement: JQuery;

	private $dateListBoxWrapper: JQuery;
	private $timeListBoxWrapper: JQuery;
	private $calendarBoxWrapper: JQuery;

	private $activeEditor: JQuery;

	private dateSuggestionEngine: DateSuggestionEngine;
	private timeSuggestionEngine: TimeSuggestionEngine;

	constructor(options: TrivialDateTimeFieldConfig = {}) {
		options = options || {};
		this.config = $.extend(<TrivialDateTimeFieldConfig>{
			locale: "de-DE",
			timeZone: "UTC",
			dateFormat: {
				year: "numeric",
				month: "2-digit",
				day: "2-digit"
			},
			timeFormat: {
				hour: "2-digit",
				minute: "2-digit"
			},
			autoComplete: true,
			autoCompleteDelay: 0,
			showTrigger: true,
			editingMode: "editable", // one of 'editable', 'disabled' and 'readonly'
			favorPastDates: false
		}, options);

		this.updateRenderers();

		this.$dateTimeField = $(`<div class="tr-datetimefield tr-input-wrapper">
            <div class="tr-editor-wrapper">
                <div class="tr-date-icon-wrapper"></div>
                <div class="tr-date-editor" contenteditable="true"></div>
                <div class="tr-time-icon-wrapper"></div>
                <div class="tr-time-editor" contenteditable="true"></div>
            </div>
            <div class="tr-trigger"><span class="tr-trigger-icon"></span></div>
        </div>`);

		let $editorWrapper = this.$dateTimeField.find('.tr-editor-wrapper').appendTo(this.$dateTimeField);
		this.$dateIconWrapper = $editorWrapper.find('.tr-date-icon-wrapper');
		this.$dateEditor = $editorWrapper.find('.tr-date-editor');
		this.$timeIconWrapper = $editorWrapper.find('.tr-time-icon-wrapper');
		this.$timeEditor = $editorWrapper.find('.tr-time-editor');

		this.$dateIconWrapper.click(() => {
			this.$activeEditor = this.$dateEditor;
			this.setDropDownMode(Mode.MODE_CALENDAR);
			selectElementContents(this.$dateEditor[0], 0, this.$dateEditor.text().length);
			this.openDropDown();
		});
		this.$timeIconWrapper.click(() => {
			this.$activeEditor = this.$timeEditor;
			this.setDropDownMode(Mode.MODE_TIME_LIST);
			selectElementContents(this.$timeEditor[0], 0, this.$timeEditor.text().length);
			this.queryTime(1);
			this.openDropDown();
		});

		this.$dateEditor.focus(() => {
			this.$activeEditor = this.$dateEditor;
			this.setDropDownMode(Mode.MODE_CALENDAR);
			if (!this.blurCausedByClickInsideComponent || this.focusGoesToOtherEditor) {
				selectElementContents(this.$dateEditor[0], 0, this.$dateEditor.text().length);
				this.queryDate(0);
				this.openDropDown();
			}
		});
		this.$timeEditor.focus(() => {
			this.$activeEditor = this.$timeEditor;
			this.setDropDownMode(Mode.MODE_TIME_LIST);
			if (!this.blurCausedByClickInsideComponent || this.focusGoesToOtherEditor) {
				selectElementContents(this.$timeEditor[0], 0, this.$timeEditor.text().length);
				this.queryTime(0);
				this.openDropDown();
			}
		});


		const $trigger = this.$dateTimeField.find('.tr-trigger');
		$trigger.toggle(this.config.showTrigger);
		$trigger.mousedown(() => {
			if (this._isDropDownOpen) {
				this.closeDropDown();
			} else {
				this.setDropDownMode(Mode.MODE_CALENDAR);
				this.calendarBox.setSelectedDate(new LocalDateTime(this.value ?? DateTime.local()));
				this.$activeEditor = this.$dateEditor;
				selectElementContents(this.$dateEditor[0], 0, this.$dateEditor.text().length);
				this.openDropDown();
			}
		});

		this.$dropDownTargetElement = $("body");
		this.$dropDown = $(`<div class="tr-dropdown">
            <div class="date-listbox"></div>
            <div class="time-listbox"></div>
            <div class="calendarbox"></div>
        </div>`)
			.scroll(() => {
				return false;
			});

		this.setEditingMode(this.config.editingMode);

		this.$dateListBoxWrapper = this.$dropDown.find('.date-listbox');
		this.dateListBox = new TrivialTreeBox<DateTime>({
			entryRenderingFunction: this.dateRenderer
		});
		this.$dateListBoxWrapper.append(this.dateListBox.getMainDomElement());
		this.dateListBox.onSelectedEntryChanged.addListener((selectedEntry: DateTime) => {
			if (selectedEntry) {
				this.setDate(selectedEntry, true);
				this.dateListBox.setSelectedEntryById(null);
				this.closeDropDown();
			}
		});
		this.$timeListBoxWrapper = this.$dropDown.find('.time-listbox');
		this.timeListBox = new TrivialTreeBox<DateTime>({
			entryRenderingFunction: this.timeRenderer
		});
		this.$timeListBoxWrapper.append(this.timeListBox.getMainDomElement());
		this.timeListBox.onSelectedEntryChanged.addListener((selectedEntry: DateTime) => {
			if (selectedEntry) {
				this.setTime(selectedEntry, true);
				this.dateListBox.setSelectedEntryById(null);
				this.closeDropDown();
			}
		});
		this.$calendarBoxWrapper = this.$dropDown.find('.calendarbox');

		this.$dateEditor
			.add(this.$timeEditor)
			.focus(
				() => {
					this.$dateTimeField.addClass('focus');
				}
			)
			.blur(
				() => {
					if (!this.blurCausedByClickInsideComponent) {
						this.$dateTimeField.removeClass('focus');
						this.updateDisplay();
						this.closeDropDown();
					}
				}
			)
			.keydown(
				(e: KeyDownEvent) => {
					if (keyCodes.isModifierKey(e)) {
						return;
					} else if (e.which == keyCodes.tab) {
						this.selectHighlightedListBoxEntry();
						return;
					} else if (e.which == keyCodes.left_arrow || e.which == keyCodes.right_arrow) {
						if (this.getActiveEditor() === this.$timeEditor && e.which == keyCodes.left_arrow && window.getSelection().focusOffset === 0) {
							e.preventDefault();
							selectElementContents(this.$dateEditor[0], 0, this.$dateEditor.text().length);
						} else if (this.getActiveEditor() === this.$dateEditor && e.which == keyCodes.right_arrow && window.getSelection().anchorOffset === window.getSelection().focusOffset && window.getSelection().focusOffset === this.$dateEditor.text().length) {
							e.preventDefault();
							selectElementContents(this.$timeEditor[0], 0, this.$timeEditor.text().length);
						}
						return; // let the user navigate freely left and right...
					}

					if (e.which == keyCodes.backspace || e.which == keyCodes.delete) {
						this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
					}

					if (e.which == keyCodes.up_arrow || e.which == keyCodes.down_arrow) {
						this.getActiveEditor().select();
						const direction = e.which == keyCodes.up_arrow ? -1 : 1;
						if (this._isDropDownOpen) {
							if (this.dropDownMode !== Mode.MODE_CALENDAR) {
								this.getActiveBox().highlightNextEntry(direction);
								this.autoCompleteIfPossible(this.config.autoCompleteDelay);
							} else if (this.calendarBox != null) {
								this.getActiveBox().navigate(direction === 1 ? 'down' : 'up');
								this.autoCompleteIfPossible(this.config.autoCompleteDelay);
							}
						} else {
							this.setDropDownMode(e.currentTarget === this.$dateEditor[0] ? Mode.MODE_DATE_LIST : Mode.MODE_TIME_LIST);
							this.query(direction);
							this.openDropDown();
						}
						return false; // some browsers move the caret to the beginning on up key
					} else if (e.which == keyCodes.enter) {
						if (this._isDropDownOpen) {
							e.preventDefault(); // do not submit form
							this.selectHighlightedListBoxEntry();
							selectElementContents(this.getActiveEditor()[0], 0, this.getActiveEditor().text().length);
							this.closeDropDown();
						}
					} else if (e.which == keyCodes.escape) {
						e.preventDefault(); // prevent ie from doing its text field magic...
						if (this._isDropDownOpen) {
							this.updateDisplay();
							selectElementContents(this.getActiveEditor()[0], 0, this.getActiveEditor().text().length);
						}
						this.closeDropDown();
					} else {
						this.setDropDownMode(e.currentTarget === this.$dateEditor[0] ? Mode.MODE_DATE_LIST : Mode.MODE_TIME_LIST);
						this.openDropDown();
						setTimeout(() => { // We need the new editor value (after the keydown event). Therefore setTimeout().
							// if (this.$editor.val()) {
							// this.query(1);
							// } else {
							// 	this.query(0);
							// 	this.treeBox.setHighlightedEntryById(null);
							// }
							this.query(1);
						});
					}
				}
			);

		this.setValue(null);

		this.$dateTimeField.add(this.$dropDown).mousedown((e) => {
			if (this.$dateEditor.is(":focus") || this.$timeEditor.is(":focus")) {
				this.blurCausedByClickInsideComponent = true;
			}
			if (e.target === this.$dateEditor[0]
				|| e.target === this.$timeEditor[0]
				|| e.target === this.$dateIconWrapper[0]
				|| e.target === this.$timeIconWrapper[0]) {
				this.focusGoesToOtherEditor = true;
			}
		}).on('mouseup mouseout', () => {
			if (this.blurCausedByClickInsideComponent && !this.focusGoesToOtherEditor) {
				this.getActiveEditor().focus();
			}
			this.blurCausedByClickInsideComponent = false;
			this.focusGoesToOtherEditor = false;
		});
		this.$activeEditor = this.$dateEditor;

		this.dateSuggestionEngine = new DateSuggestionEngine({
			locale: this.config.locale,
			preferredYearMonthDayOrder: getYearMonthDayOrderFromLocale(this.config.locale),
			favorPastDates: this.config.favorPastDates
		});
		this.timeSuggestionEngine = new TimeSuggestionEngine();


		this.popper = createPopper(this.$dateTimeField[0], this.$dropDown[0], {
			placement: 'bottom',
			modifiers: [
				{
					name: "flip",
					options: {
						fallbackPlacements: ['top']
					}
				},
				{
					name: "preventOverflow"
				},
				{
					name: 'dropDownCornerSmoother',
					enabled: true,
					phase: 'write',
					fn: ({state}) => {
						this.$dateTimeField[0].classList.toggle("dropdown-flipped", state.placement === 'top');
						this.$dateTimeField[0].classList.toggle("flipped", state.placement === 'top');
					}
				}
			]
		})
	}

	private updateRenderers() {
		this.dateIconRenderer = createDateIconRenderer(this.config.locale);
		this.timeIconRenderer = createClockIconRenderer();
		this.dateRenderer = createDateRenderer(this.config.locale, this.config.dateFormat);
		this.timeRenderer = createTimeRenderer(this.config.locale, this.config.timeFormat);
	}

	private isDropDownNeeded() {
		return this.editingMode == 'editable';
	}

	private setDropDownMode(mode: Mode) {
		this.dropDownMode = mode;
		if (!this.calendarBoxInitialized && mode === Mode.MODE_CALENDAR) {
			this.calendarBox = new TrivialCalendarBox({
				firstDayOfWeek: 1
			});
			this.$calendarBoxWrapper.append(this.calendarBox.getMainDomElement());
			this.calendarBox.setKeyboardNavigationState('month');
			this.calendarBox.onChange.addListener(({value, timeUnitEdited}) => {
				this.setDate(value.toZoned(this.config.timeZone));
				if (timeUnitEdited === 'day') {
					this.closeDropDown();
					this.$activeEditor = this.$timeEditor;
					selectElementContents(this.$timeEditor[0], 0, this.$timeEditor.text().length);
					this.fireChangeEvents();
				}
			});
			this.calendarBoxInitialized = true;
		}
		this.$calendarBoxWrapper.toggle(mode === Mode.MODE_CALENDAR);
		this.$dateListBoxWrapper.toggle(mode === Mode.MODE_DATE_LIST);
		this.$timeListBoxWrapper.toggle(mode === Mode.MODE_TIME_LIST);
	}

	private getActiveBox(): any /*TODO Navigateable*/ {
		if (this.dropDownMode === Mode.MODE_CALENDAR) {
			return this.calendarBox;
		} else if (this.dropDownMode === Mode.MODE_DATE_LIST) {
			return this.dateListBox;
		} else {
			return this.timeListBox;
		}
	}

	private getActiveEditor() {
		return this.$activeEditor;
	}


	private selectHighlightedListBoxEntry() {
		if (this.dropDownMode === Mode.MODE_DATE_LIST || this.dropDownMode === Mode.MODE_TIME_LIST) {
			const highlightedEntry = this.getActiveBox().getHighlightedEntry();
			if (this._isDropDownOpen && highlightedEntry) {
				if (this.getActiveEditor() === this.$dateEditor) {
					this.setDate(highlightedEntry, true);
				} else {
					this.setTime(highlightedEntry, true);
				}
			}
		}
	}

	private query(direction: number) {
		if (this.$activeEditor == this.$dateEditor) {
			this.queryDate(direction);
		} else {
			this.queryTime(direction)
		}
	}

	private queryDate(highlightDirection: HighlightDirection = 1) {
		const queryString = this.getNonSelectedEditorValue();
		let entries: DateTime[] = this.dateSuggestionEngine
			.generateSuggestions(queryString, LocalDateTime.fromDateTime(DateTime.fromObject({zone: this.config.timeZone})))
			.map(s => s.toZoned(this.config.timeZone));
		if (!entries || entries.length === 0) {
			this.closeDropDown();
		} else {
			this.dateListBox.setEntries(entries);
			this.dateListBox.highlightTextMatches(this.getNonSelectedEditorValue());
			this.dateListBox.selectNextEntry(highlightDirection);
			this.autoCompleteIfPossible(this.config.autoCompleteDelay);
			if (this._isDropDownOpen) {
				this.openDropDown(); // only for repositioning!
			}
		}
	}

	private queryTime(highlightDirection: HighlightDirection) {
		const queryString = this.getNonSelectedEditorValue();
		let entries: DateTime[] = this.timeSuggestionEngine
			.generateSuggestions(queryString)
			.map(s => s.toZoned(this.config.timeZone));
		if (!entries || entries.length === 0) {
			this.closeDropDown();
		} else {
			this.timeListBox.setEntries(entries);
			this.timeListBox.highlightTextMatches(this.getNonSelectedEditorValue());
			this.timeListBox.selectNextEntry(highlightDirection);
			this.autoCompleteIfPossible(this.config.autoCompleteDelay);
			if (this._isDropDownOpen) {
				this.openDropDown(); // only for repositioning!
			}
		}
	}

	public getValue(): DateTime {
		return this.value;
	};

	private fireChangeEvents() {
		this.onChange.fire(this.getValue());
	}

	private setDate(newDateValue: DateTime, fireEvent: boolean = false) {
		let valuesObject = {
			year: newDateValue.year, month: newDateValue.month, day: newDateValue.day
		};
		this.setValue(this.value == null ? DateTime.fromObject(valuesObject) : this.value.set(valuesObject), fireEvent);
	}

	private setTime(newTimeValue: DateTime, fireEvent: boolean = false) {
		let valuesObject = {
			hour: newTimeValue.hour,
			minute: newTimeValue.minute,
			second: newTimeValue.second,
			millisecond: newTimeValue.millisecond
		};
		this.setValue(this.value == null ? DateTime.fromObject(valuesObject) : this.value.set(valuesObject), fireEvent);
	}

	public setValue(value: DateTime, fireEvent: boolean = false) {
		this.value = value;
		this.updateDisplay();
		if (fireEvent) {
			this.fireChangeEvents();
		}
	}

	private updateDisplay() {
		if (this.value) {
			this.$dateEditor.text(this.value.setLocale(this.config.locale).toLocaleString(this.config.dateFormat));
			this.$dateIconWrapper.empty().append(this.dateIconRenderer(this.value));
			this.$timeEditor.text(this.value.setLocale(this.config.locale).toLocaleString(this.config.timeFormat));
			this.$timeIconWrapper.empty().append(this.timeIconRenderer(this.value));
		} else {
			this.$dateEditor.text("");
			this.$dateIconWrapper.empty().append(this.dateIconRenderer(null));
			this.$timeEditor.text("");
			this.$timeIconWrapper.empty().append(this.timeIconRenderer(null));
		}
	}

	private repositionDropDown() {
		place(this.$dropDown[0], "top left")
			.to(this.$dateTimeField[0], "bottom left");
		this.popper.update();
		this.$dropDown.width(this.$dateTimeField.width());
	}

	public openDropDown() {
		if (this.$dropDown != null) {
			this.$dateTimeField.addClass("open");
			this.$dropDown.show();
			this.repositionDropDown();
			this._isDropDownOpen = true;
		}
	}

	public closeDropDown() {
		this.$dateTimeField.removeClass("open");
		this.$dropDown.hide();
		this._isDropDownOpen = false;
	}

	private getNonSelectedEditorValue() {
		const editorText = this.getActiveEditor().text().replace(String.fromCharCode(160), " ");
		const selection = window.getSelection();
		if (selection.anchorOffset != selection.focusOffset) {
			return editorText.substring(0, Math.min(selection.anchorOffset, selection.focusOffset));
		} else {
			return editorText;
		}
	}

	private autoCompleteIfPossible(delay: number) {
		if (this.config.autoComplete && (this.dropDownMode === Mode.MODE_DATE_LIST || this.dropDownMode === Mode.MODE_TIME_LIST)) {
			clearTimeout(this.autoCompleteTimeoutId);

			const listBox = this.getActiveBox();
			const highlightedEntry = listBox.getHighlightedEntry();
			if (highlightedEntry && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
				const autoCompletingEntryDisplayValue = highlightedEntry.displayString;
				if (autoCompletingEntryDisplayValue) {
					this.autoCompleteTimeoutId = window.setTimeout(() => {
						const oldEditorValue = this.getNonSelectedEditorValue();
						let newEditorValue: string;
						if (autoCompletingEntryDisplayValue.toLowerCase().indexOf(oldEditorValue.toLowerCase()) === 0) {
							newEditorValue = oldEditorValue + autoCompletingEntryDisplayValue.substr(oldEditorValue.length);
						} else {
							newEditorValue = this.getNonSelectedEditorValue();
						}
						this.getActiveEditor().text(newEditorValue);
						// $editor[0].offsetHeight;  // we need this to guarantee that the editor has been updated...
						if (this.getActiveEditor().is(":focus")) {
							selectElementContents(this.getActiveEditor()[0], oldEditorValue.length, newEditorValue.length);
						}
					}, delay || 0);
				}
			}
			this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
		}
	}

	public setEditingMode(newEditingMode: EditingMode) {
		this.editingMode = newEditingMode;
		this.$dateTimeField.removeClass("editable readonly disabled").addClass(this.editingMode);

		if (newEditingMode == "editable") {
			this.$dateEditor[0].setAttribute("contenteditable", "");
			this.$timeEditor[0].setAttribute("contenteditable", "");
		} else {
			this.$dateEditor[0].removeAttribute("contenteditable");
			this.$timeEditor[0].removeAttribute("contenteditable");
		}

		if (this.isDropDownNeeded()) {
			this.$dropDown.appendTo(this.$dropDownTargetElement);
		}
	}

	public setLocale(locale: string) {
		this.config.locale = locale;
		this.updateDisplay();
	}

	public setDateFormat(dateFormat: DateTimeFormatOptions) {
		this.config.dateFormat = dateFormat;
		this.updateDisplay();
	}

	public setTimeFormat(timeFormat: DateTimeFormatOptions) {
		this.config.timeFormat = timeFormat;
		this.updateDisplay();
	}

	setLocaleAndFormats(locale: string, dateFormat: DateTimeFormatOptions, timeFormat: DateTimeFormatOptions) {
		this.config.locale = locale;
		this.config.dateFormat = dateFormat;
		this.config.timeFormat = timeFormat;
		this.updateDisplay();
	}

	public focus() {
		selectElementContents(this.getActiveEditor()[0], 0, this.getActiveEditor().text().length);
	}

	public isDropDownOpen(): boolean {
		return this._isDropDownOpen;
	}

	public destroy() {
		this.$dateTimeField.remove();
		this.$dropDown.remove();
	}

	getMainDomElement(): HTMLElement {
		return this.$dateTimeField[0];
	}
}
