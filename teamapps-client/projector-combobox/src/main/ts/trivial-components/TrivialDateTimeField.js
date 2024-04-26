"use strict";
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
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.TrivialDateTimeField = void 0;
var TrivialCore_1 = require("./TrivialCore");
var TrivialCalendarBox_1 = require("./TrivialCalendarBox");
var DateSuggestionEngine_1 = require("../DateSuggestionEngine");
var TimeSuggestionEngine_1 = require("../TimeSuggestionEngine");
var core_1 = require("@popperjs/core");
var TrivialTreeBox_1 = require("./TrivialTreeBox");
var luxon_1 = require("luxon");
var datetime_rendering_1 = require("../datetime-rendering");
var LocalDateTime_1 = require("../LocalDateTime");
var teamapps_client_core_1 = require("teamapps-client-core");
var ComboBoxPopper_1 = require("./ComboBoxPopper");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var Mode;
(function (Mode) {
    Mode[Mode["MODE_CALENDAR"] = 0] = "MODE_CALENDAR";
    Mode[Mode["MODE_DATE_LIST"] = 1] = "MODE_DATE_LIST";
    Mode[Mode["MODE_TIME_LIST"] = 2] = "MODE_TIME_LIST";
})(Mode || (Mode = {}));
var TrivialDateTimeField = /** @class */ (function () {
    function TrivialDateTimeField(options) {
        if (options === void 0) { options = {}; }
        var _this = this;
        this.onFocus = new teamapps_client_core_1.TeamAppsEvent();
        this.onBlur = new teamapps_client_core_1.TeamAppsEvent();
        this.onChange = new teamapps_client_core_1.TeamAppsEvent();
        this._isDropDownOpen = false;
        this.value = null;
        this.blurCausedByClickInsideComponent = false;
        this.autoCompleteTimeoutId = -1;
        this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
        this.calendarBoxInitialized = false;
        this.dropDownMode = Mode.MODE_CALENDAR;
        options = options || {};
        this.config = __assign({ locale: "de-DE", timeZone: "UTC", dateFormat: {
                year: "numeric",
                month: "2-digit",
                day: "2-digit"
            }, timeFormat: {
                hour: "2-digit",
                minute: "2-digit"
            }, autoComplete: true, autoCompleteDelay: 0, showTrigger: true, editingMode: "editable", favorPastDates: false }, options);
        this.updateRenderers();
        this.$dateTimeField = (0, teamapps_client_core_1.parseHtml)("<div class=\"tr-datetimefield tr-input-wrapper\">\n            <div class=\"tr-editor-wrapper\">\n                <div class=\"tr-date-icon-wrapper\"></div>\n                <div class=\"tr-date-editor\" contenteditable=\"true\"></div>\n                <div class=\"tr-time-icon-wrapper\"></div>\n                <div class=\"tr-time-editor\" contenteditable=\"true\"></div>\n            </div>\n            <div class=\"tr-trigger\"><span class=\"tr-trigger-icon\"></span></div>\n        </div>");
        var $editorWrapper = this.$dateTimeField.querySelector(':scope .tr-editor-wrapper');
        this.$dateIconWrapper = $editorWrapper.querySelector(':scope .tr-date-icon-wrapper');
        this.$dateEditor = $editorWrapper.querySelector(':scope .tr-date-editor');
        this.$timeIconWrapper = $editorWrapper.querySelector(':scope .tr-time-icon-wrapper');
        this.$timeEditor = $editorWrapper.querySelector(':scope .tr-time-editor');
        this.$dateIconWrapper.addEventListener("click", function () {
            _this.$activeEditor = _this.$dateEditor;
            _this.setDropDownMode(Mode.MODE_CALENDAR);
            (0, teamapps_client_core_components_1.selectElementContents)(_this.$dateEditor, 0, _this.$dateEditor.innerText.length);
            _this.openDropDown();
        });
        this.$timeIconWrapper.addEventListener("click", function () {
            _this.$activeEditor = _this.$timeEditor;
            _this.setDropDownMode(Mode.MODE_TIME_LIST);
            (0, teamapps_client_core_components_1.selectElementContents)(_this.$timeEditor, 0, _this.$timeEditor.innerText.length);
            _this.queryTime(1);
            _this.openDropDown();
        });
        this.$dateEditor.addEventListener("focus", function () {
            _this.$activeEditor = _this.$dateEditor;
            _this.setDropDownMode(Mode.MODE_CALENDAR);
            if (!_this.blurCausedByClickInsideComponent) {
                (0, teamapps_client_core_components_1.selectElementContents)(_this.$dateEditor, 0, _this.$dateEditor.innerText.length);
                _this.queryDate(0);
                _this.openDropDown();
            }
        });
        this.$timeEditor.addEventListener("focus", function () {
            _this.$activeEditor = _this.$timeEditor;
            _this.setDropDownMode(Mode.MODE_TIME_LIST);
            if (!_this.blurCausedByClickInsideComponent) {
                (0, teamapps_client_core_components_1.selectElementContents)(_this.$timeEditor, 0, _this.$timeEditor.innerText.length);
                _this.queryTime(0);
                _this.openDropDown();
            }
        });
        var $trigger = this.$dateTimeField.querySelector(':scope .tr-trigger');
        $trigger.classList.toggle("hidden", !this.config.showTrigger);
        $trigger.addEventListener("mousedown", function () {
            var _a;
            if (_this._isDropDownOpen) {
                _this.closeDropDown();
            }
            else {
                _this.setDropDownMode(Mode.MODE_CALENDAR);
                _this.calendarBox.setSelectedDate(new LocalDateTime_1.LocalDateTime((_a = _this.value) !== null && _a !== void 0 ? _a : luxon_1.DateTime.local()));
                _this.$activeEditor = _this.$dateEditor;
                (0, teamapps_client_core_components_1.selectElementContents)(_this.$dateEditor, 0, _this.$dateEditor.innerText.length);
                _this.openDropDown();
            }
        });
        this.$dropDownTargetElement = document.body;
        this.$dropDown = (0, teamapps_client_core_1.parseHtml)("<div class=\"tr-dropdown\">\n            <div class=\"date-listbox\"></div>\n            <div class=\"time-listbox\"></div>\n            <div class=\"calendarbox\"></div>\n        </div>");
        this.$dropDown.addEventListener("scroll", function () {
            return false;
        });
        this.setEditingMode(this.config.editingMode);
        this.$dateListBoxWrapper = this.$dropDown.querySelector(':scope .date-listbox');
        this.dateListBox = new TrivialTreeBox_1.TrivialTreeBox({
            entryRenderingFunction: this.dateRenderer
        });
        this.$dateListBoxWrapper.append(this.dateListBox.getMainDomElement());
        this.dateListBox.onSelectedEntryChanged.addListener(function (selectedEntry) {
            if (selectedEntry) {
                _this.setDate(selectedEntry, true);
                _this.dateListBox.setSelectedEntryById(null);
                _this.closeDropDown();
            }
        });
        this.$timeListBoxWrapper = this.$dropDown.querySelector(':scope .time-listbox');
        this.timeListBox = new TrivialTreeBox_1.TrivialTreeBox({
            entryRenderingFunction: this.timeRenderer
        });
        this.$timeListBoxWrapper.append(this.timeListBox.getMainDomElement());
        this.timeListBox.onSelectedEntryChanged.addListener(function (selectedEntry) {
            if (selectedEntry) {
                _this.setTime(selectedEntry, true);
                _this.dateListBox.setSelectedEntryById(null);
                _this.closeDropDown();
            }
        });
        this.$calendarBoxWrapper = this.$dropDown.querySelector(':scope .calendarbox');
        [this.$dateEditor, this.$timeEditor].forEach(function (el) {
            el.addEventListener("focus", function () { return _this.setFocused(true); });
            el.addEventListener("blur", function () {
                if (_this.blurCausedByClickInsideComponent) {
                    _this.blurCausedByClickInsideComponent = false;
                }
                else {
                    _this.updateDisplay();
                    _this.closeDropDown();
                    _this.setFocused(false);
                }
            });
            el.addEventListener("keydown", function (e) {
                if ((0, TrivialCore_1.isModifierKey)(e)) {
                    return;
                }
                else if (e.key == "Tab") {
                    if (!e.shiftKey && document.activeElement == _this.$dateEditor
                        || e.shiftKey && document.activeElement == _this.$timeEditor) {
                        _this.blurCausedByClickInsideComponent = true;
                    }
                    _this.selectHighlightedListBoxEntry();
                    return;
                }
                else if (e.key == "ArrowLeft" || e.key == "ArrowRight") {
                    if (_this.getActiveEditor() === _this.$timeEditor && e.key == "ArrowLeft" && window.getSelection().focusOffset === 0) {
                        e.preventDefault();
                        (0, teamapps_client_core_components_1.selectElementContents)(_this.$dateEditor, 0, _this.$dateEditor.innerText.length);
                    }
                    else if (_this.getActiveEditor() === _this.$dateEditor && e.key == "ArrowRight" && window.getSelection().anchorOffset === window.getSelection().focusOffset && window.getSelection().focusOffset === _this.$dateEditor.innerText.length) {
                        e.preventDefault();
                        (0, teamapps_client_core_components_1.selectElementContents)(_this.$timeEditor, 0, _this.$timeEditor.innerText.length);
                    }
                    return; // let the user navigate freely left and right...
                }
                if (e.key == "Backspace" || e.key == "Delete") {
                    _this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
                }
                if (e.key == "ArrowUp" || e.key == "ArrowDown") {
                    (0, teamapps_client_core_components_1.selectElementContents)(_this.getActiveEditor(), 0, _this.$dateEditor.innerText.length);
                    var direction = e.key == "ArrowUp" ? -1 : 1;
                    if (_this._isDropDownOpen) {
                        if (_this.dropDownMode !== Mode.MODE_CALENDAR) {
                            _this.getActiveBox().selectNextEntry(direction);
                            _this.autoCompleteIfPossible(_this.config.autoCompleteDelay);
                        }
                        else if (_this.calendarBox != null) {
                            _this.getActiveBox().navigate(direction === 1 ? 'down' : 'up');
                            _this.autoCompleteIfPossible(_this.config.autoCompleteDelay);
                        }
                    }
                    else {
                        _this.setDropDownMode(e.currentTarget === _this.$dateEditor ? Mode.MODE_DATE_LIST : Mode.MODE_TIME_LIST);
                        _this.query(direction);
                        _this.openDropDown();
                    }
                    return false; // some browsers move the caret to the beginning on up key
                }
                else if (e.key == "Enter") {
                    if (_this._isDropDownOpen) {
                        e.preventDefault(); // do not submit form
                        _this.selectHighlightedListBoxEntry();
                        (0, teamapps_client_core_components_1.selectElementContents)(_this.getActiveEditor(), 0, _this.getActiveEditor().innerText.length);
                        _this.closeDropDown();
                    }
                }
                else if (e.key == "Escape") {
                    e.preventDefault(); // prevent ie from doing its text field magic...
                    if (_this._isDropDownOpen) {
                        _this.updateDisplay();
                        (0, teamapps_client_core_components_1.selectElementContents)(_this.getActiveEditor(), 0, _this.getActiveEditor().innerText.length);
                    }
                    _this.closeDropDown();
                }
                else {
                    _this.setDropDownMode(e.currentTarget === _this.$dateEditor ? Mode.MODE_DATE_LIST : Mode.MODE_TIME_LIST);
                    _this.openDropDown();
                    setTimeout(function () {
                        // if (this.$editor.val()) {
                        // this.query(1);
                        // } else {
                        // 	this.query(0);
                        // 	this.treeBox.setHighlightedEntryById(null);
                        // }
                        _this.query(1);
                    });
                }
            });
        });
        this.setValue(null);
        [this.$dateTimeField, this.$dropDown].forEach(function (el) {
            el.addEventListener("mousedown", function (e) {
                _this.blurCausedByClickInsideComponent = true;
                setTimeout(function () { return _this.blurCausedByClickInsideComponent = false; });
            });
        });
        this.$activeEditor = this.$dateEditor;
        this.dateSuggestionEngine = new DateSuggestionEngine_1.DateSuggestionEngine({
            locale: this.config.locale,
            preferredYearMonthDayOrder: (0, DateSuggestionEngine_1.getYearMonthDayOrderForLocale)(this.config.locale),
            favorPastDates: this.config.favorPastDates
        });
        this.timeSuggestionEngine = new TimeSuggestionEngine_1.TimeSuggestionEngine();
        this.popper = (0, core_1.createPopper)(this.$dateTimeField, this.$dropDown, {
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
                    fn: function (_a) {
                        var state = _a.state;
                        _this.$dateTimeField.classList.toggle("dropdown-flipped", state.placement === 'top');
                        _this.$dateTimeField.classList.toggle("flipped", state.placement === 'top');
                    }
                }
            ]
        });
    }
    TrivialDateTimeField.prototype.setFocused = function (focused) {
        if (focused != this.focused) {
            if (focused) {
                this.onFocus.fire();
            }
            else {
                this.onBlur.fire();
            }
            this.$dateTimeField.classList.toggle('focus', focused);
            this.focused = focused;
        }
    };
    TrivialDateTimeField.prototype.updateRenderers = function () {
        this.dateIconRenderer = (0, datetime_rendering_1.createDateIconRenderer)(this.config.locale);
        this.timeIconRenderer = (0, datetime_rendering_1.createClockIconRenderer)();
        this.dateRenderer = (0, datetime_rendering_1.createDateRenderer)(this.config.locale, this.config.dateFormat);
        this.timeRenderer = (0, datetime_rendering_1.createTimeRenderer)(this.config.locale, this.config.timeFormat);
    };
    TrivialDateTimeField.prototype.isDropDownNeeded = function () {
        return this.editingMode == 'editable';
    };
    TrivialDateTimeField.prototype.setDropDownMode = function (mode) {
        var _this = this;
        this.dropDownMode = mode;
        if (!this.calendarBoxInitialized && mode === Mode.MODE_CALENDAR) {
            this.calendarBox = new TrivialCalendarBox_1.TrivialCalendarBox({
                firstDayOfWeek: 1
            });
            this.$calendarBoxWrapper.append(this.calendarBox.getMainDomElement());
            this.calendarBox.setKeyboardNavigationState('month');
            this.calendarBox.onChange.addListener(function (_a) {
                var value = _a.value, timeUnitEdited = _a.timeUnitEdited;
                _this.setDate(value.toZoned(_this.config.timeZone));
                if (timeUnitEdited === 'day') {
                    _this.closeDropDown();
                    _this.$activeEditor = _this.$timeEditor;
                    (0, teamapps_client_core_components_1.selectElementContents)(_this.$timeEditor, 0, _this.$timeEditor.innerText.length);
                    _this.fireChangeEvents();
                }
            });
            this.calendarBoxInitialized = true;
        }
        this.$calendarBoxWrapper.classList.toggle(".hidden", mode !== Mode.MODE_CALENDAR);
        this.$dateListBoxWrapper.classList.toggle(".hidden", mode !== Mode.MODE_DATE_LIST);
        this.$timeListBoxWrapper.classList.toggle(".hidden", mode !== Mode.MODE_TIME_LIST);
    };
    TrivialDateTimeField.prototype.getActiveBox = function () {
        if (this.dropDownMode === Mode.MODE_CALENDAR) {
            return this.calendarBox;
        }
        else if (this.dropDownMode === Mode.MODE_DATE_LIST) {
            return this.dateListBox;
        }
        else {
            return this.timeListBox;
        }
    };
    TrivialDateTimeField.prototype.getActiveEditor = function () {
        return this.$activeEditor;
    };
    TrivialDateTimeField.prototype.selectHighlightedListBoxEntry = function () {
        if (this.dropDownMode === Mode.MODE_DATE_LIST || this.dropDownMode === Mode.MODE_TIME_LIST) {
            var highlightedEntry = this.getActiveBox().getSelectedEntry();
            if (this._isDropDownOpen && highlightedEntry) {
                if (this.getActiveEditor() === this.$dateEditor) {
                    this.setDate(highlightedEntry, true);
                }
                else {
                    this.setTime(highlightedEntry, true);
                }
            }
        }
    };
    TrivialDateTimeField.prototype.query = function (direction) {
        if (this.$activeEditor == this.$dateEditor) {
            this.queryDate(direction);
        }
        else {
            this.queryTime(direction);
        }
    };
    TrivialDateTimeField.prototype.queryDate = function (highlightDirection) {
        var _this = this;
        if (highlightDirection === void 0) { highlightDirection = 1; }
        var queryString = this.getNonSelectedEditorValue();
        var entries = this.dateSuggestionEngine
            .generateSuggestions(queryString, LocalDateTime_1.LocalDateTime.fromDateTime(luxon_1.DateTime.fromObject({ zone: this.config.timeZone })))
            .map(function (s) { return s.toZoned(_this.config.timeZone); });
        if (!entries || entries.length === 0) {
            this.closeDropDown();
        }
        else {
            this.dateListBox.setEntries(entries);
            this.dateListBox.highlightTextMatches(this.getNonSelectedEditorValue());
            this.dateListBox.selectNextEntry(highlightDirection);
            this.autoCompleteIfPossible(this.config.autoCompleteDelay);
            if (this._isDropDownOpen) {
                this.openDropDown(); // only for repositioning!
            }
        }
    };
    TrivialDateTimeField.prototype.queryTime = function (highlightDirection) {
        var _this = this;
        var queryString = this.getNonSelectedEditorValue();
        var entries = this.timeSuggestionEngine
            .generateSuggestions(queryString)
            .map(function (s) { return s.toZoned(_this.config.timeZone); });
        if (!entries || entries.length === 0) {
            this.closeDropDown();
        }
        else {
            this.timeListBox.setEntries(entries);
            this.timeListBox.highlightTextMatches(this.getNonSelectedEditorValue());
            this.timeListBox.selectNextEntry(highlightDirection);
            this.autoCompleteIfPossible(this.config.autoCompleteDelay);
            if (this._isDropDownOpen) {
                this.openDropDown(); // only for repositioning!
            }
        }
    };
    TrivialDateTimeField.prototype.getValue = function () {
        return this.value;
    };
    ;
    TrivialDateTimeField.prototype.fireChangeEvents = function () {
        this.onChange.fire(this.getValue());
    };
    TrivialDateTimeField.prototype.setDate = function (newDateValue, fireEvent) {
        if (fireEvent === void 0) { fireEvent = false; }
        var valuesObject = {
            year: newDateValue.year, month: newDateValue.month, day: newDateValue.day
        };
        this.setValue(this.value == null ? luxon_1.DateTime.fromObject(valuesObject) : this.value.set(valuesObject), fireEvent);
    };
    TrivialDateTimeField.prototype.setTime = function (newTimeValue, fireEvent) {
        if (fireEvent === void 0) { fireEvent = false; }
        var valuesObject = {
            hour: newTimeValue.hour,
            minute: newTimeValue.minute,
            second: newTimeValue.second,
            millisecond: newTimeValue.millisecond
        };
        this.setValue(this.value == null ? luxon_1.DateTime.fromObject(valuesObject) : this.value.set(valuesObject), fireEvent);
    };
    TrivialDateTimeField.prototype.setValue = function (value, fireEvent) {
        if (fireEvent === void 0) { fireEvent = false; }
        this.value = value;
        this.updateDisplay();
        if (fireEvent) {
            this.fireChangeEvents();
        }
    };
    TrivialDateTimeField.prototype.updateDisplay = function () {
        if (this.value) {
            this.$dateEditor.innerText = this.value.setLocale(this.config.locale).toLocaleString(this.config.dateFormat);
            this.$dateIconWrapper.innerText = '';
            this.$dateIconWrapper.append(this.dateIconRenderer(this.value));
            this.$timeEditor.innerText = this.value.setLocale(this.config.locale).toLocaleString(this.config.timeFormat);
            this.$timeIconWrapper.innerText = '';
            this.$timeIconWrapper.append(this.timeIconRenderer(this.value));
        }
        else {
            this.$dateEditor.innerText = "";
            this.$dateIconWrapper.innerText = "";
            this.$dateIconWrapper.append(this.dateIconRenderer(null));
            this.$timeEditor.innerText = "";
            this.$timeIconWrapper.innerText = "";
            this.$timeIconWrapper.append(this.timeIconRenderer(null));
        }
    };
    TrivialDateTimeField.prototype.openDropDown = function () {
        var _this = this;
        var _a;
        if (this.$dropDown != null) {
            if (this.getMainDomElement().parentElement !== this.parentElement || this.dropdownAutoUpdateDisposable == null) {
                (_a = this.dropdownAutoUpdateDisposable) === null || _a === void 0 ? void 0 : _a.call(this);
                this.dropdownAutoUpdateDisposable = this.dropdownAutoUpdateDisposable = (0, ComboBoxPopper_1.positionDropdownWithAutoUpdate)(this.getMainDomElement(), this.$dropDown, {
                    referenceOutOfViewPortHandler: function () { return _this.closeDropDown(); }
                });
                this.parentElement = this.getMainDomElement().parentElement;
            }
            this.$dateTimeField.classList.add("open");
            this.$dropDown.classList.remove("hidden");
            this._isDropDownOpen = true;
        }
    };
    TrivialDateTimeField.prototype.closeDropDown = function () {
        this.$dateTimeField.classList.remove("open");
        this.$dropDown.classList.add("hidden");
        this._isDropDownOpen = false;
    };
    TrivialDateTimeField.prototype.getNonSelectedEditorValue = function () {
        var editorText = this.getActiveEditor().innerText.replace(String.fromCharCode(160), " ");
        var selection = window.getSelection();
        if (selection.anchorOffset != selection.focusOffset) {
            return editorText.substring(0, Math.min(selection.anchorOffset, selection.focusOffset));
        }
        else {
            return editorText;
        }
    };
    TrivialDateTimeField.prototype.autoCompleteIfPossible = function (delay) {
        var _this = this;
        if (this.config.autoComplete && (this.dropDownMode === Mode.MODE_DATE_LIST || this.dropDownMode === Mode.MODE_TIME_LIST)) {
            clearTimeout(this.autoCompleteTimeoutId);
            var listBox = this.getActiveBox();
            var highlightedEntry = listBox.getSelectedEntry();
            if (highlightedEntry && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
                var autoCompletingEntryDisplayValue_1 = highlightedEntry.displayString;
                if (autoCompletingEntryDisplayValue_1) {
                    this.autoCompleteTimeoutId = window.setTimeout(function () {
                        var oldEditorValue = _this.getNonSelectedEditorValue();
                        var newEditorValue;
                        if (autoCompletingEntryDisplayValue_1.toLowerCase().indexOf(oldEditorValue.toLowerCase()) === 0) {
                            newEditorValue = oldEditorValue + autoCompletingEntryDisplayValue_1.substr(oldEditorValue.length);
                        }
                        else {
                            newEditorValue = _this.getNonSelectedEditorValue();
                        }
                        _this.getActiveEditor().innerText = newEditorValue;
                        // $editor.offsetHeight;  // we need this to guarantee that the editor has been updated...
                        if (document.activeElement === _this.getActiveEditor()) {
                            (0, teamapps_client_core_components_1.selectElementContents)(_this.getActiveEditor(), oldEditorValue.length, newEditorValue.length);
                        }
                    }, delay || 0);
                }
            }
            this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
        }
    };
    TrivialDateTimeField.prototype.setEditingMode = function (newEditingMode) {
        this.editingMode = newEditingMode;
        this.$dateTimeField.classList.remove("editable", "readonly", "disabled");
        this.$dateTimeField.classList.add(this.editingMode);
        if (newEditingMode == "editable") {
            this.$dateEditor.setAttribute("contenteditable", "");
            this.$timeEditor.setAttribute("contenteditable", "");
        }
        else {
            this.$dateEditor.removeAttribute("contenteditable");
            this.$timeEditor.removeAttribute("contenteditable");
        }
        if (this.isDropDownNeeded()) {
            this.$dropDownTargetElement.append(this.$dropDown);
        }
    };
    TrivialDateTimeField.prototype.setLocale = function (locale) {
        this.config.locale = locale;
        this.updateDisplay();
    };
    TrivialDateTimeField.prototype.setDateFormat = function (dateFormat) {
        this.config.dateFormat = dateFormat;
        this.updateDisplay();
    };
    TrivialDateTimeField.prototype.setTimeFormat = function (timeFormat) {
        this.config.timeFormat = timeFormat;
        this.updateDisplay();
    };
    TrivialDateTimeField.prototype.setLocaleAndFormats = function (locale, dateFormat, timeFormat) {
        this.config.locale = locale;
        this.config.dateFormat = dateFormat;
        this.config.timeFormat = timeFormat;
        this.updateDisplay();
    };
    TrivialDateTimeField.prototype.focus = function () {
        (0, teamapps_client_core_components_1.selectElementContents)(this.getActiveEditor(), 0, this.getActiveEditor().innerText.length);
    };
    TrivialDateTimeField.prototype.isDropDownOpen = function () {
        return this._isDropDownOpen;
    };
    TrivialDateTimeField.prototype.destroy = function () {
        this.$dateTimeField.remove();
        this.$dropDown.remove();
    };
    TrivialDateTimeField.prototype.getMainDomElement = function () {
        return this.$dateTimeField;
    };
    return TrivialDateTimeField;
}());
exports.TrivialDateTimeField = TrivialDateTimeField;
