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

import Moment = moment.Moment;
import moment from "moment-timezone";
import {EditingMode, HighlightDirection, keyCodes, selectElementContents, TrivialComponent} from "./TrivialCore";
import {TrivialEvent} from "./TrivialEvent";
import {TrivialCalendarBox} from "./TrivialCalendarBox";
import {TrivialDateSuggestionEngine} from "./TrivialDateSuggestionEngine";
import {TrivialTimeSuggestionEngine} from "./TrivialTimeSuggestionEngine";
import {place} from "place-to";
import {createPopper, Instance as Popper} from '@popperjs/core';
import {TrivialTreeBox} from "./TrivialTreeBox";
import KeyDownEvent = JQuery.KeyDownEvent;

enum Mode {
	MODE_CALENDAR,
	MODE_DATE_LIST,
	MODE_TIME_LIST
}

type DateComboBoxEntry = {
	moment: Moment,
	day: number,
	weekDay: string,
	month: number,
	year: number,
	displayString: string
}

type TimeComboBoxEntry = {
	hour: number,
	minute: number,
	hourString: string,
	minuteString: string,
	displayString: string,
	hourAngle: number,
	minuteAngle: number,
	isNight: boolean
};

export interface TrivialDateTimeFieldConfig {
	dateFormat?: string,
	timeFormat?: string,
	autoComplete?: boolean,
	autoCompleteDelay?: number,
	showTrigger?: boolean,
	editingMode?: EditingMode,
	favorPastDates?: boolean
}

export interface LocalDate {
	year: number,
	month: number,
	day: number
}

export interface LocalTime {
	hour: number,
	minute: number,
	second?: number
}

export interface LocalDateTime extends LocalDate, LocalTime {
}

export class TrivialDateTimeField implements TrivialComponent {

	private config: TrivialDateTimeFieldConfig;

	private dateIconRenderer = (entry: DateComboBoxEntry) => {
		return `<svg viewBox="0 0 540 540" width="22" height="22" class="calendar-icon">
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
            <text class="calendar-symbol-date" x="270" y="415" text-anchor="middle">${entry && entry.weekDay || ''}</text>
        </g>
    </svg>`;
	};
	private dateRenderer = (entry: DateComboBoxEntry) => {
		return `<div class="tr-template-icon-single-line">
            ${this.dateIconRenderer(entry)}
            <div class="content-wrapper tr-editor-area">${entry && entry.displayString || ''}</div>
        </div>`;
	};
	private timeIconRenderer = (entry: TimeComboBoxEntry) => {
		return `<svg class="clock-icon night-${entry && entry.isNight}" viewBox="0 0 110 110" width="22" height="22">
            <circle class="clockcircle" cx="55" cy="55" r="45"></circle>
            <g class="hands">
                <line class="hourhand" x1="55" y1="55" x2="55" y2="35" ${(entry && entry.hourAngle) ? `transform="rotate(${entry.hourAngle},55,55)"` : ''}></line>
                <line class="minutehand" x1="55" y1="55" x2="55" y2="22" ${(entry && entry.minuteAngle) ? `transform="rotate(${entry.minuteAngle},55,55)"` : ''}></line>
            </g>
        </svg>`;
	};
	private timeRenderer = (entry: TimeComboBoxEntry) => `<div class="tr-template-icon-single-line">
        ${this.timeIconRenderer(entry)}
        <div class="content-wrapper tr-editor-area">${entry && entry.displayString || ''}</div>
    </div>`;

	public readonly onChange = new TrivialEvent<LocalDateTime>(this);

	private dateListBox: TrivialTreeBox<DateComboBoxEntry>;
	private timeListBox: TrivialTreeBox<TimeComboBoxEntry>;
	private calendarBox: TrivialCalendarBox;
	private _isDropDownOpen = false;

	private dateValue: DateComboBoxEntry = null; // moment object representing the current value
	private timeValue: TimeComboBoxEntry = null; // moment object representing the current value

	private blurCausedByClickInsideComponent = false;
	private focusGoesToOtherEditor = false;
	private autoCompleteTimeoutId = -1;
	private doNoAutoCompleteBecauseBackspaceWasPressed = false;
	private calendarBoxInitialized = false;
	private editingMode: string;

	private dropDownMode = Mode.MODE_CALENDAR;

	private $originalInput: JQuery;
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

	private dateSuggestionEngine: TrivialDateSuggestionEngine;
	private timeSuggestionEngine: TrivialTimeSuggestionEngine;

	constructor(originalInput: Element, options: TrivialDateTimeFieldConfig = {}) {
		options = options || {};
		this.config = $.extend(<TrivialDateTimeFieldConfig> {
			dateFormat: "MM/DD/YYYY",
			timeFormat: "HH:mm",
			autoComplete: true,
			autoCompleteDelay: 0,
			showTrigger: true,
			editingMode: "editable", // one of 'editable', 'disabled' and 'readonly'
			favorPastDates: false
		}, options);

		this.$originalInput = $(originalInput).addClass("tr-original-input");
		this.$dateTimeField = $(`<div class="tr-datetimefield tr-input-wrapper">
            <div class="tr-editor-wrapper">
                <div class="tr-date-icon-wrapper"></div>
                <div class="tr-date-editor" contenteditable="true"></div>
                <div class="tr-time-icon-wrapper"></div>
                <div class="tr-time-editor" contenteditable="true"></div>
            </div>
            <div class="tr-trigger"><span class="tr-trigger-icon"></span></div>
        </div>`)
			.insertAfter(this.$originalInput);

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
			this.query(1);
			this.openDropDown();
		});

		this.$dateEditor.focus(() => {
			this.$activeEditor = this.$dateEditor;
			if (!this.blurCausedByClickInsideComponent || this.focusGoesToOtherEditor) {
				selectElementContents(this.$dateEditor[0], 0, this.$dateEditor.text().length);
			}
		});
		this.$timeEditor.focus(() => {
			this.$activeEditor = this.$timeEditor;
			if (!this.blurCausedByClickInsideComponent || this.focusGoesToOtherEditor) {
				selectElementContents(this.$timeEditor[0], 0, this.$timeEditor.text().length);
			}
		});


		const $trigger = this.$dateTimeField.find('.tr-trigger');
		$trigger.toggle(this.config.showTrigger);
		$trigger.mousedown(() => {
			if (this._isDropDownOpen) {
				this.closeDropDown();
			} else {
				setTimeout(() => { // TODO remove this when Chrome bug is fixed. Chrome scrolls to the top of the page if we do this synchronously. Maybe this has something to do with https://code.google.com/p/chromium/issues/detail?id=342307 .
					this.setDropDownMode(Mode.MODE_CALENDAR);
					this.calendarBox.setSelectedDate(this.dateValue ? this.dateValue.moment : moment());
					this.$activeEditor = this.$dateEditor;
					selectElementContents(this.$dateEditor[0], 0, this.$dateEditor.text().length);
					this.openDropDown();
				});
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
		this.dateListBox = new TrivialTreeBox<DateComboBoxEntry>(this.$dateListBoxWrapper, {
			entryRenderingFunction: this.dateRenderer,
			scrollContainer: this.$dropDown
		});
		this.dateListBox.onSelectedEntryChanged.addListener((selectedEntry: DateComboBoxEntry) => {
			if (selectedEntry) {
				this.setDate(selectedEntry, selectedEntry.displayString != (this.dateValue && this.dateValue.displayString));
				this.dateListBox.setSelectedEntryById(null);
				this.closeDropDown();
			}
		});
		this.$timeListBoxWrapper = this.$dropDown.find('.time-listbox');
		this.timeListBox = new TrivialTreeBox<TimeComboBoxEntry>(this.$timeListBoxWrapper, {
			entryRenderingFunction: this.timeRenderer,
			scrollContainer: this.$dropDown
		});
		this.timeListBox.onSelectedEntryChanged.addListener((selectedEntry: TimeComboBoxEntry) => {
			if (selectedEntry) {
				this.setTime(selectedEntry, selectedEntry.displayString != (this.timeValue && this.timeValue.displayString));
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

		if (this.$originalInput.val()) {
			this.setValue(moment(this.$originalInput.val()));
		} else {
			this.setValue(null);
		}

		if (this.$originalInput.attr("tabindex")) {
			this.$dateEditor.add(this.$timeEditor).attr("tabindex", this.$originalInput.attr("tabindex"));
		}
		if (this.$originalInput.attr("autofocus")) {
			this.$dateEditor.focus();
		}

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

		this.dateSuggestionEngine = new TrivialDateSuggestionEngine({
			preferredDateFormat: this.config.dateFormat,
			favorPastDates: this.config.favorPastDates
		});
		this.timeSuggestionEngine = new TrivialTimeSuggestionEngine();


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

	private isDropDownNeeded() {
		return this.editingMode == 'editable';
	}

	private setDropDownMode(mode: Mode) {
		this.dropDownMode = mode;
		if (!this.calendarBoxInitialized && mode === Mode.MODE_CALENDAR) {
			this.calendarBox = new TrivialCalendarBox(this.$calendarBoxWrapper, {
				firstDayOfWeek: 1,
				mode: 'date' // 'date', 'time', 'datetime'
			});
			this.calendarBox.setKeyboardNavigationState('month');
			this.calendarBox.onChange.addListener(({value, timeUnitEdited}) => {
				this.setDate(TrivialDateTimeField.createDateComboBoxEntry(value, this.config.dateFormat));
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


	private query(highlightDirection: HighlightDirection) {
		const queryString = this.getNonSelectedEditorValue();
		let entries: DateComboBoxEntry[] | TimeComboBoxEntry[];
		if (this.getActiveEditor() === this.$dateEditor) {
			entries = this.dateSuggestionEngine.generateSuggestions(queryString, moment())
				.map(s => TrivialDateTimeField.createDateComboBoxEntry(s.moment, this.config.dateFormat));
		} else {
			entries = this.timeSuggestionEngine.generateSuggestions(queryString)
				.map(s => TrivialDateTimeField.createTimeComboBoxEntry(s.hour, s.minute, this.config.timeFormat));
		}
		if (!entries || entries.length === 0) {
			this.closeDropDown();
		} else {
			this.updateEntries(entries, highlightDirection);
		}
	}

	public getValue(): LocalDateTime {
		if (this.dateValue == null) {
			return null;
		} else if (this.timeValue == null) {
			return {
				year: this.dateValue.year,
				month: this.dateValue.month,
				day: this.dateValue.day,
				hour: null,
				minute: null
			};
		} else {
			return {
				year: this.dateValue.year,
				month: this.dateValue.month,
				day: this.dateValue.day,
				hour: this.timeValue.hour,
				minute: this.timeValue.minute
			}
		}
	};

	private fireChangeEvents() {
		this.$originalInput.trigger("change");
		this.onChange.fire(this.getValue());
	}

	private setDate(newDateValue: DateComboBoxEntry, fireEvent: boolean = false) {
		this.dateValue = newDateValue;
		this.updateOriginalInputValue();
		this.updateDisplay();
		if (fireEvent) {
			this.fireChangeEvents();
		}
	}

	private setTime(newTimeValue: TimeComboBoxEntry, fireEvent: boolean = false) {
		this.timeValue = newTimeValue;
		this.updateOriginalInputValue();
		this.updateDisplay();
		if (fireEvent) {
			this.fireChangeEvents();
		}
	}

	private updateOriginalInputValue() {
		if (this.dateValue == null) {
			this.$originalInput.val('');
		} else if (this.timeValue == null) {
			this.$originalInput.val(moment(this.getValue()).format(this.config.dateFormat));
		} else {
			this.$originalInput.val(moment(this.getValue()).format(this.config.dateFormat + ' ' + this.config.timeFormat));
		}
	}

	private updateDisplay() {
		if (this.dateValue) {
			this.$dateEditor.text(moment([this.dateValue.year, this.dateValue.month - 1, this.dateValue.day]).format(this.config.dateFormat));
			this.$dateIconWrapper.empty().append(this.dateIconRenderer(this.dateValue));
		} else {
			this.$dateEditor.text("");
			this.$dateIconWrapper.empty().append(this.dateIconRenderer(null));
		}
		if (this.timeValue) {
			this.$timeEditor.text(moment([1970, 0, 1, this.timeValue.hour, this.timeValue.minute]).format(this.config.timeFormat));
			this.$timeIconWrapper.empty().append(this.timeIconRenderer(this.timeValue));
		} else {
			this.$timeEditor.text("");
			this.$timeIconWrapper.empty().append(this.timeIconRenderer(null));
		}
	}

	public setValue(mom: Moment) {
		this.setDate(mom && TrivialDateTimeField.createDateComboBoxEntry(mom, this.config.dateFormat));
		this.setTime(mom && TrivialDateTimeField.createTimeComboBoxEntry(mom.hour(), mom.minute(), this.config.timeFormat));
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

	private updateEntries(newEntries: any[], highlightDirection: HighlightDirection) {
		const listBox = this.getActiveBox();

		highlightDirection = highlightDirection === undefined ? 1 : highlightDirection;
		listBox.updateEntries(newEntries);

		listBox.highlightTextMatches(this.getNonSelectedEditorValue());

		listBox.highlightNextEntry(highlightDirection);

		this.autoCompleteIfPossible(this.config.autoCompleteDelay);

		if (this._isDropDownOpen) {
			this.openDropDown(); // only for repositioning!
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

	private static createTimeComboBoxEntry(hour: number, minute: number, timeFormat: string): TimeComboBoxEntry {
		return {
			hour: hour,
			minute: minute,
			hourString: TrivialDateTimeField.pad(hour, 2),
			minuteString: TrivialDateTimeField.pad(minute, 2),
			displayString: moment().hour(hour).minute(minute).format(timeFormat),
			hourAngle: ((hour % 12) + minute / 60) * 30,
			minuteAngle: minute * 6,
			isNight: hour < 6 || hour >= 20
		};
	}

	private static pad(num: number, size: number) {
		let s = num + "";
		while (s.length < size) s = "0" + s;
		return s;
	}

	private static createDateComboBoxEntry(m: Moment, dateFormat: string): DateComboBoxEntry {
		return {
			moment: m,
			day: m.date(),
			weekDay: m.format('dd'),
			month: m.month() + 1,
			year: m.year(),
			displayString: m.format(dateFormat)
		};
	}

	public focus() {
		selectElementContents(this.getActiveEditor()[0], 0, this.getActiveEditor().text().length);
	}

	public isDropDownOpen(): boolean {
		return this._isDropDownOpen;
	}

	public destroy() {
		this.$originalInput.removeClass('tr-original-input').insertBefore(this.$dateTimeField);
		this.$dateTimeField.remove();
		this.$dropDown.remove();
	}

	getMainDomElement(): Element {
		return this.$dateTimeField[0];
	}
}
