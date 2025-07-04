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

import {type EditingMode, type HighlightDirection, isModifierKey, type TrivialComponent} from "./TrivialCore";
import {TrivialCalendarBox} from "./TrivialCalendarBox";
import {DateSuggestionEngine, getYearMonthDayOrderForLocale} from "../DateSuggestionEngine";
import {TimeSuggestionEngine} from "../TimeSuggestionEngine";
import {TrivialTreeBox} from "./TrivialTreeBox";
import {DateTime} from "luxon";
import {
	createClockIconRenderer,
	createDateIconRenderer,
	createDateRenderer,
	createTimeRenderer
} from "../datetime-rendering";
import {LocalDateTime} from "../LocalDateTime";
import {type Disposable, positionDropdownWithAutoUpdate} from "./ComboBoxPopper";
import {parseHtml, ProjectorEvent} from "projector-client-object-api";
import {selectElementContents} from "../util";

const Modes = {
	MODE_CALENDAR: 0,
	MODE_DATE_LIST: 1,
	MODE_TIME_LIST: 2
};
type Mode = typeof Modes[keyof typeof Modes];

export interface TrivialDateTimeFieldConfig {
	locale?: string,
	timeZone?: string,
	dateFormat?: Intl.DateTimeFormatOptions,
	timeFormat?: Intl.DateTimeFormatOptions,
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

	public readonly onFocus = new ProjectorEvent<void>();
	public readonly onBlur = new ProjectorEvent<void>();
	public readonly onChange = new ProjectorEvent<DateTime>();

	private config: TrivialDateTimeFieldConfig;

	private dateIconRenderer: (localDateTime: DateTime) => string;
	private timeIconRenderer: (localDateTime: DateTime) => string;
	private dateRenderer: (localDateTime: DateTime) => string;
	private timeRenderer: (localDateTime: DateTime) => string;

	private dateListBox: TrivialTreeBox<DateTime>;
	private timeListBox: TrivialTreeBox<DateTime>;
	private calendarBox: TrivialCalendarBox;
	private _isDropDownOpen = false;

	private value: DateTime = null;

	private blurCausedByClickInsideComponent = false;
	private focused: boolean;
	private autoCompleteTimeoutId = -1;
	private doNoAutoCompleteBecauseBackspaceWasPressed = false;
	private calendarBoxInitialized = false;
	private editingMode: string;

	private dropDownMode = Modes.MODE_CALENDAR;

	private $dateTimeField: HTMLElement;
	private $dropDown: HTMLElement;

	private $dateIconWrapper: HTMLElement;
	private $dateEditor: HTMLDivElement;
	private $timeIconWrapper: HTMLElement;
	private $timeEditor: HTMLDivElement;
	private $dropDownTargetElement: HTMLElement;

	private $dateListBoxWrapper: HTMLElement;
	private $timeListBoxWrapper: HTMLElement;
	private $calendarBoxWrapper: HTMLElement;

	private $activeEditor: HTMLDivElement;

	private dateSuggestionEngine: DateSuggestionEngine;
	private timeSuggestionEngine: TimeSuggestionEngine;

	constructor(options: TrivialDateTimeFieldConfig = {}) {
		options = options || {};
		this.config = {
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
			favorPastDates: false,
			...options
		};

		this.updateRenderers();

		this.$dateTimeField = parseHtml(`<div class="tr-datetimefield tr-input-wrapper">
            <div class="tr-editor-wrapper">
                <div class="tr-date-icon-wrapper"></div>
                <div class="tr-date-editor" contenteditable="true"></div>
                <div class="tr-time-icon-wrapper"></div>
                <div class="tr-time-editor" contenteditable="true"></div>
            </div>
            <div class="tr-trigger"><span class="tr-trigger-icon"></span></div>
        </div>`);

		let $editorWrapper = this.$dateTimeField.querySelector(':scope .tr-editor-wrapper');
		this.$dateIconWrapper = $editorWrapper.querySelector(':scope .tr-date-icon-wrapper');
		this.$dateEditor = $editorWrapper.querySelector(':scope .tr-date-editor');
		this.$timeIconWrapper = $editorWrapper.querySelector(':scope .tr-time-icon-wrapper');
		this.$timeEditor = $editorWrapper.querySelector(':scope .tr-time-editor');

		this.$dateIconWrapper.addEventListener("click", () => {
			this.$activeEditor = this.$dateEditor;
			this.setDropDownMode(Modes.MODE_CALENDAR);
			selectElementContents(this.$dateEditor, 0, this.$dateEditor.innerText.length);
			this.openDropDown();
		});
		this.$timeIconWrapper.addEventListener("click", () => {
			this.$activeEditor = this.$timeEditor;
			this.setDropDownMode(Modes.MODE_TIME_LIST);
			selectElementContents(this.$timeEditor, 0, this.$timeEditor.innerText.length);
			this.queryTime(1);
			this.openDropDown();
		});

		this.$dateEditor.addEventListener("focus", () => {
			this.$activeEditor = this.$dateEditor;
			this.setDropDownMode(Modes.MODE_CALENDAR);
			// if (!this.blurCausedByClickInsideComponent) {
			selectElementContents(this.$dateEditor, 0, this.$dateEditor.innerText.length);
			this.queryDate(0);
			this.openDropDown();
			// }
		});
		this.$timeEditor.addEventListener("focus", () => {
			this.$activeEditor = this.$timeEditor;
			this.setDropDownMode(Modes.MODE_TIME_LIST);
			// if (!this.blurCausedByClickInsideComponent) {
			selectElementContents(this.$timeEditor, 0, this.$timeEditor.innerText.length);
			this.queryTime(0);
			this.openDropDown();
			// }
		});


		const $trigger = this.$dateTimeField.querySelector(':scope .tr-trigger');
		$trigger.classList.toggle("hidden", !this.config.showTrigger);
		$trigger.addEventListener("mousedown", () => {
			if (this._isDropDownOpen) {
				this.closeDropDown();
			} else {
				this.setDropDownMode(Modes.MODE_CALENDAR);
				this.calendarBox.setSelectedDate(new LocalDateTime(this.value ?? DateTime.local()));
				this.$activeEditor = this.$dateEditor;
				selectElementContents(this.$dateEditor, 0, this.$dateEditor.innerText.length);
				this.openDropDown();
			}
		});

		this.$dropDownTargetElement = document.body;
		this.$dropDown = parseHtml(`<div class="tr-dropdown" style="display: none">
            <div class="date-listbox"></div>
            <div class="time-listbox"></div>
            <div class="calendarbox"></div>
        </div>`);
		this.$dropDown.addEventListener("scroll", () => {
			return false;
		});


		this.setEditingMode(this.config.editingMode);

		this.$dateListBoxWrapper = this.$dropDown.querySelector(':scope .date-listbox');
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
		this.$timeListBoxWrapper = this.$dropDown.querySelector(':scope .time-listbox');
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
		this.$calendarBoxWrapper = this.$dropDown.querySelector(':scope .calendarbox');

		[this.$dateEditor, this.$timeEditor].forEach(el => {
			el.addEventListener("focus", () => this.setFocused(true));
			el.addEventListener("blur",
				() => {
					if (this.blurCausedByClickInsideComponent) {
						this.blurCausedByClickInsideComponent = false;
					} else {
						this.updateDisplay();
						this.closeDropDown();
						this.setFocused(false);
					}
				}
			)
			el.addEventListener("keydown",
				(e: KeyboardEvent) => {
					if (isModifierKey(e)) {
						return;
					} else if (e.key == "Tab") {
						if (!e.shiftKey && document.activeElement == this.$dateEditor
							|| e.shiftKey && document.activeElement == this.$timeEditor) {
							this.blurCausedByClickInsideComponent = true;
						}
						this.selectHighlightedListBoxEntry();
						return;
					} else if (e.key == "ArrowLeft" || e.key == "ArrowRight") {
						if (this.getActiveEditor() === this.$timeEditor && e.key == "ArrowLeft" && window.getSelection().focusOffset === 0) {
							e.preventDefault();
							selectElementContents(this.$dateEditor, 0, this.$dateEditor.innerText.length);
						} else if (this.getActiveEditor() === this.$dateEditor && e.key == "ArrowRight" && window.getSelection().anchorOffset === window.getSelection().focusOffset && window.getSelection().focusOffset === this.$dateEditor.innerText.length) {
							e.preventDefault();
							selectElementContents(this.$timeEditor, 0, this.$timeEditor.innerText.length);
						}
						return; // let the user navigate freely left and right...
					}

					if (e.key == "Backspace" || e.key == "Delete") {
						this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
					}

					if (e.key == "ArrowUp" || e.key == "ArrowDown") {
						selectElementContents(this.getActiveEditor(), 0, this.$dateEditor.innerText.length);
						const direction = e.key == "ArrowUp" ? -1 : 1;
						if (this._isDropDownOpen) {
							if (this.dropDownMode !== Modes.MODE_CALENDAR) {
								(this.getActiveBox() as TrivialTreeBox<any>).selectNextEntry(direction);
								this.autoCompleteIfPossible(this.config.autoCompleteDelay);
							} else if (this.calendarBox != null) {
								this.getActiveBox().navigate(direction === 1 ? 'down' : 'up');
								this.autoCompleteIfPossible(this.config.autoCompleteDelay);
							}
						} else {
							this.setDropDownMode(e.currentTarget === this.$dateEditor ? Modes.MODE_DATE_LIST : Modes.MODE_TIME_LIST);
							this.query(direction);
							this.openDropDown();
						}
						return false; // some browsers move the caret to the beginning on up key
					} else if (e.key == "Enter") {
						if (this._isDropDownOpen) {
							e.preventDefault(); // do not submit form
							this.selectHighlightedListBoxEntry();
							selectElementContents(this.getActiveEditor(), 0, this.getActiveEditor().innerText.length);
							this.closeDropDown();
						}
					} else if (e.key == "Escape") {
						e.preventDefault(); // prevent ie from doing its text field magic...
						if (this._isDropDownOpen) {
							this.updateDisplay();
							selectElementContents(this.getActiveEditor(), 0, this.getActiveEditor().innerText.length);
						}
						this.closeDropDown();
					}
				}
			);
		});

		[this.$dateEditor, this.$timeEditor].forEach(editor => {
			editor.addEventListener("input", e => {
				this.setDropDownMode(e.currentTarget === this.$dateEditor[0] ? Modes.MODE_DATE_LIST : Modes.MODE_TIME_LIST);
				this.openDropDown();
				this.query(1);
			})
		});

		this.setValue(null);

		[this.$dateTimeField, this.$dropDown].forEach(el => {
			el.addEventListener("mousedown", (e) => {
				this.blurCausedByClickInsideComponent = true;
				setTimeout(() => this.blurCausedByClickInsideComponent = false);
			});
		})
		this.$activeEditor = this.$dateEditor;

		this.dateSuggestionEngine = new DateSuggestionEngine({
			locale: this.config.locale,
			preferredYearMonthDayOrder: getYearMonthDayOrderForLocale(this.config.locale),
			favorPastDates: this.config.favorPastDates
		});
		this.timeSuggestionEngine = new TimeSuggestionEngine();
	}

	private setFocused(focused: boolean) {
		if (focused != this.focused) {
			if (focused) {
				this.onFocus.fire();
			} else {
				this.onBlur.fire();
			}
			this.$dateTimeField.classList.toggle('focus', focused);
			this.focused = focused;
		}
	}

	private updateRenderers() {
		this.dateIconRenderer = createDateIconRenderer(this.config.locale);
		this.timeIconRenderer = createClockIconRenderer();
		this.dateRenderer = createDateRenderer(this.config.locale, this.config.dateFormat, true);
		this.timeRenderer = createTimeRenderer(this.config.locale, this.config.timeFormat, true);
	}

	private isDropDownNeeded() {
		return this.editingMode == 'editable';
	}

	private setDropDownMode(mode: Mode) {
		this.dropDownMode = mode;
		if (!this.calendarBoxInitialized && mode === Modes.MODE_CALENDAR) {
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
					selectElementContents(this.$timeEditor, 0, this.$timeEditor.innerText.length);
					this.fireChangeEvents();
				}
			});
			this.calendarBoxInitialized = true;
		}
		this.$calendarBoxWrapper.classList.toggle(".hidden", mode !== Modes.MODE_CALENDAR);
		this.$dateListBoxWrapper.classList.toggle(".hidden", mode !== Modes.MODE_DATE_LIST);
		this.$timeListBoxWrapper.classList.toggle(".hidden", mode !== Modes.MODE_TIME_LIST);
	}

	private getActiveBox(): any /*TODO Navigateable*/ {
		if (this.dropDownMode === Modes.MODE_CALENDAR) {
			return this.calendarBox;
		} else if (this.dropDownMode === Modes.MODE_DATE_LIST) {
			return this.dateListBox;
		} else {
			return this.timeListBox;
		}
	}

	private getActiveEditor() {
		return this.$activeEditor;
	}


	private selectHighlightedListBoxEntry() {
		if (this.dropDownMode === Modes.MODE_DATE_LIST || this.dropDownMode === Modes.MODE_TIME_LIST) {
			const highlightedEntry = (this.getActiveBox() as TrivialTreeBox<any>).getSelectedEntry();
			if (this._isDropDownOpen && highlightedEntry) {
				if (this.getActiveEditor() === this.$dateEditor) {
					this.setDate(highlightedEntry, true);
				} else {
					this.setTime(highlightedEntry, true);
				}
			}
		}
	}

	private query(direction: HighlightDirection) {
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
			this.$dateEditor.innerText = this.value.setLocale(this.config.locale).toLocaleString(this.config.dateFormat);
			this.$dateIconWrapper.innerText = '';
			this.$dateIconWrapper.append(this.dateIconRenderer(this.value));
			this.$timeEditor.innerText = this.value.setLocale(this.config.locale).toLocaleString(this.config.timeFormat);
			this.$timeIconWrapper.innerText = '';
			this.$timeIconWrapper.append(this.timeIconRenderer(this.value));
		} else {
			this.$dateEditor.innerText = "";
			this.$dateIconWrapper.innerText = "";
			this.$dateIconWrapper.append(this.dateIconRenderer(null));
			this.$timeEditor.innerText = "";
			this.$timeIconWrapper.innerText = "";
			this.$timeIconWrapper.append(this.timeIconRenderer(null));
		}
	}

	private parentElement: Element;
	private dropdownAutoUpdateDisposable: Disposable | null;

	public openDropDown() {
		if (this.$dropDown != null) {
			if (this.getMainDomElement().parentElement !== this.parentElement || this.dropdownAutoUpdateDisposable == null) {
				this.dropdownAutoUpdateDisposable?.();
				this.dropdownAutoUpdateDisposable = this.dropdownAutoUpdateDisposable = positionDropdownWithAutoUpdate(this.getMainDomElement(), this.$dropDown, {
					referenceOutOfViewPortHandler: () => this.closeDropDown()
				});
				this.parentElement = this.getMainDomElement().parentElement;
			}

			this.$dateTimeField.classList.add("open");
			this.$dropDown.classList.remove("hidden");
			this._isDropDownOpen = true;
		}
	}

	public closeDropDown() {
		this.$dateTimeField.classList.remove("open");
		this.$dropDown.classList.add("hidden");
		this._isDropDownOpen = false;
	}

	private getNonSelectedEditorValue() {
		const editorText = this.getActiveEditor().innerText;
		const selection = window.getSelection();
		return editorText.substring(0, Math.min(selection.anchorOffset, selection.focusOffset)).replace(String.fromCharCode(160), " ");
	}

	private autoCompleteIfPossible(delay: number) {
		if (this.config.autoComplete && (this.dropDownMode === Modes.MODE_DATE_LIST || this.dropDownMode === Modes.MODE_TIME_LIST)) {
			clearTimeout(this.autoCompleteTimeoutId);

			const listBox = this.getActiveBox() as TrivialTreeBox<any>;
			const highlightedEntry = listBox.getSelectedEntry();
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
						this.getActiveEditor().innerText = newEditorValue;
						// $editor.offsetHeight;  // we need this to guarantee that the editor has been updated...
						if (document.activeElement === this.getActiveEditor()) {
							selectElementContents(this.getActiveEditor(), oldEditorValue.length, newEditorValue.length);
						}
					}, delay || 0);
				}
			}
			this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
		}
	}

	public setEditingMode(newEditingMode: EditingMode) {
		this.editingMode = newEditingMode;
		this.$dateTimeField.classList.remove("editable", "readonly", "disabled");
		this.$dateTimeField.classList.add(this.editingMode);

		if (newEditingMode == "editable") {
			this.$dateEditor.setAttribute("contenteditable", "");
			this.$timeEditor.setAttribute("contenteditable", "");
		} else {
			this.$dateEditor.removeAttribute("contenteditable");
			this.$timeEditor.removeAttribute("contenteditable");
		}

		if (this.isDropDownNeeded()) {
			this.$dropDownTargetElement.append(this.$dropDown);
		}
	}

	public setLocale(locale: string) {
		this.config.locale = locale;
		this.updateDisplay();
	}

	public setDateFormat(dateFormat: Intl.DateTimeFormatOptions) {
		this.config.dateFormat = dateFormat;
		this.updateDisplay();
	}

	public setTimeFormat(timeFormat: Intl.DateTimeFormatOptions) {
		this.config.timeFormat = timeFormat;
		this.updateDisplay();
	}

	setLocaleAndFormats(locale: string, dateFormat: Intl.DateTimeFormatOptions, timeFormat: Intl.DateTimeFormatOptions) {
		this.config.locale = locale;
		this.config.dateFormat = dateFormat;
		this.config.timeFormat = timeFormat;
		this.updateDisplay();
	}

	public focus() {
		selectElementContents(this.getActiveEditor(), 0, this.getActiveEditor().innerText.length);
	}

	public isDropDownOpen(): boolean {
		return this._isDropDownOpen;
	}

	public destroy() {
		this.$dateTimeField.remove();
		this.$dropDown.remove();
	}

	getMainDomElement(): HTMLElement {
		return this.$dateTimeField;
	}
}
