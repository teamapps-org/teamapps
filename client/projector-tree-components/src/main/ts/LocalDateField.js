"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        if (typeof b !== "function" && b !== null)
            throw new TypeError("Class extends value " + String(b) + " is not a constructor or null");
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.LocalDateField = void 0;
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
var DateSuggestionEngine_1 = require("./DateSuggestionEngine");
var teamapps_client_core_1 = require("teamapps-client-core");
var LocalDateTime_1 = require("./LocalDateTime");
var datetime_rendering_1 = require("./datetime-rendering");
var CalendarBoxDropdown_1 = require("./trivial-components/dropdown/CalendarBoxDropdown");
var TrivialCalendarBox_1 = require("./trivial-components/TrivialCalendarBox");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var generated_1 = require("./generated");
var TrivialComboBox_1 = require("./trivial-components/TrivialComboBox");
var TreeBoxDropdown_1 = require("./trivial-components/dropdown/TreeBoxDropdown");
var TrivialTreeBox_1 = require("./trivial-components/TrivialTreeBox");
var LocalDateField = /** @class */ (function (_super) {
    __extends(LocalDateField, _super);
    function LocalDateField() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.onTextInput = new teamapps_client_core_1.TeamAppsEvent({
            throttlingMode: "debounce",
            delay: 250
        });
        _this.onSpecialKeyPressed = new teamapps_client_core_1.TeamAppsEvent({
            throttlingMode: "debounce",
            delay: 250
        });
        return _this;
    }
    LocalDateField.prototype.initialize = function (config) {
        var _this = this;
        this.updateDateSuggestionEngine();
        this.dateRenderer = this.createDateRenderer();
        var treeBoxDropdown = new TreeBoxDropdown_1.TreeBoxDropdown({
            queryFunction: function (searchString) {
                return _this.dateSuggestionEngine.generateSuggestions(searchString, _this.getDefaultDate(), {
                    shuffledFormatSuggestionsEnabled: _this.config.shuffledFormatSuggestionsEnabled
                });
            },
            textHighlightingEntryLimit: -1,
            preselectionMatcher: function (query, entry) { return _this.localDateTimeToString(entry).toLowerCase().indexOf(query.toLowerCase()) >= 0; }
        }, new TrivialTreeBox_1.TrivialTreeBox({
            entryRenderingFunction: function (localDateTime) { return _this.dateRenderer(localDateTime); },
            selectOnHover: true
        }));
        this.calendarBoxDropdown = new CalendarBoxDropdown_1.CalendarBoxDropdown(new TrivialCalendarBox_1.TrivialCalendarBox({
            locale: config.locale,
            // firstDayOfWeek?: config., TODO
            highlightKeyboardNavigationState: false
        }), function (query) { return _this.dateSuggestionEngine.generateSuggestions(query, _this.getDefaultDate(), {
            shuffledFormatSuggestionsEnabled: _this.config.shuffledFormatSuggestionsEnabled
        })[0]; }, this.getDefaultDate());
        this.trivialComboBox = new TrivialComboBox_1.TrivialComboBox({
            showTrigger: config.showDropDownButton,
            entryToEditorTextFunction: function (entry) {
                return _this.localDateTimeToString(entry);
            },
            selectedEntryRenderingFunction: function (localDateTime) { return _this.dateRenderer(localDateTime); },
            textToEntryFunction: function (freeText) {
                var suggestions = _this.dateSuggestionEngine.generateSuggestions(freeText, _this.getDefaultDate(), {
                    suggestAdjacentWeekForEmptyInput: false,
                    shuffledFormatSuggestionsEnabled: _this.config.shuffledFormatSuggestionsEnabled
                });
                return suggestions.length > 0 ? suggestions[0] : null;
            },
            editingMode: config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY ? 'readonly' : config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable',
            showClearButton: config.showClearButton,
            placeholderText: config.placeholderText
        }, this.config.dropDownMode === generated_1.DtoLocalDateFieldDropDownMode.CALENDAR ? this.calendarBoxDropdown : treeBoxDropdown);
        [this.trivialComboBox.onBeforeQuery, this.trivialComboBox.onBeforeDropdownOpens].forEach(function (event) { return event.addListener(function (queryString) {
            if (_this.config.dropDownMode == generated_1.DtoLocalDateFieldDropDownMode.CALENDAR
                || _this.config.dropDownMode == generated_1.DtoLocalDateFieldDropDownMode.CALENDAR_SUGGESTION_LIST && !queryString) {
                _this.trivialComboBox.setDropDownComponent(_this.calendarBoxDropdown);
            }
            else {
                _this.trivialComboBox.setDropDownComponent(treeBoxDropdown);
            }
        }); });
        this.trivialComboBox.getMainDomElement().classList.add("DtoAbstractDateField");
        this.trivialComboBox.onSelectedEntryChanged.addListener(function () { return _this.commit(); });
        this.trivialComboBox.getEditor().addEventListener("keydown", function (e) {
            if (e.key === "Escape") {
                _this.onSpecialKeyPressed.fire({
                    key: teamapps_client_core_components_1.DtoSpecialKey.ESCAPE
                });
            }
            else if (e.key === "Enter") {
                _this.onSpecialKeyPressed.fire({
                    key: teamapps_client_core_components_1.DtoSpecialKey.ENTER
                });
            }
        });
        this.trivialComboBox.getEditor().addEventListener("input", function (e) { return _this.onTextInput.fire({ enteredString: e.target.value }); });
        this.trivialComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
        this.trivialComboBox.getMainDomElement().querySelector(":scope .tr-editor").classList.add("field-background");
        this.trivialComboBox.getMainDomElement().querySelector(":scope .tr-trigger").classList.add("field-border");
        this.getMainInnerDomElement().classList.add("UiLocalDateField");
    };
    LocalDateField.prototype.getDefaultDate = function () {
        var _a;
        return (_a = LocalDateField.UiLocalDateToLocalDateTime(this.config.defaultSuggestionDate)) !== null && _a !== void 0 ? _a : LocalDateTime_1.LocalDateTime.local();
    };
    LocalDateField.prototype.localDateTimeToString = function (entry) {
        return entry.setLocale(this.config.locale).toLocaleString(this.config.dateFormat);
    };
    LocalDateField.prototype.createDateRenderer = function () {
        var dateRenderer = (0, datetime_rendering_1.createDateRenderer)(this.config.locale, this.config.dateFormat);
        return function (entry) { return dateRenderer(entry === null || entry === void 0 ? void 0 : entry.toUTC()); };
    };
    LocalDateField.prototype.updateDateSuggestionEngine = function () {
        this.dateSuggestionEngine = new DateSuggestionEngine_1.DateSuggestionEngine({
            locale: this.config.locale,
            favorPastDates: this.config.favorPastDates
        });
    };
    LocalDateField.prototype.getMainInnerDomElement = function () {
        return this.trivialComboBox.getMainDomElement();
    };
    LocalDateField.prototype.initFocusHandling = function () {
        var _this = this;
        this.trivialComboBox.onFocus.addListener(function () { return _this.onFocus.fire({}); });
        this.trivialComboBox.onBlur.addListener(function () { return _this.onBlur.fire({}); });
    };
    LocalDateField.prototype.focus = function () {
        this.trivialComboBox.focus();
    };
    LocalDateField.prototype.onEditingModeChanged = function (editingMode) {
        var _a;
        (_a = this.getMainElement().classList).remove.apply(_a, Object.values(teamapps_client_core_components_1.AbstractField.editingModeCssClasses));
        this.getMainElement().classList.add(teamapps_client_core_components_1.AbstractField.editingModeCssClasses[editingMode]);
        if (editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY) {
            this.trivialComboBox.setEditingMode("readonly");
        }
        else if (editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED) {
            this.trivialComboBox.setEditingMode("disabled");
        }
        else {
            this.trivialComboBox.setEditingMode("editable");
        }
    };
    LocalDateField.prototype.isValidData = function (v) {
        return v == null || v._type === "UiLocalDate";
    };
    LocalDateField.prototype.displayCommittedValue = function () {
        var uiValue = this.getCommittedValue();
        if (uiValue) {
            this.trivialComboBox.setValue(LocalDateField.UiLocalDateToLocalDateTime(uiValue));
        }
        else {
            this.trivialComboBox.setValue(null);
        }
    };
    LocalDateField.prototype.getTransientValue = function () {
        var selectedEntry = this.trivialComboBox.getValue();
        if (selectedEntry) {
            return (0, function (year: number, month: number, day: number): DtoLocalDate {
                return {
                    year, month, day
                };
            })(selectedEntry.year, selectedEntry.month, selectedEntry.day);
        }
        else {
            return null;
        }
    };
    LocalDateField.prototype.getReadOnlyHtml = function (value, availableWidth) {
        if (value != null) {
            return this.dateRenderer(LocalDateField.UiLocalDateToLocalDateTime(value));
        }
        else {
            return "";
        }
    };
    LocalDateField.prototype.valuesChanged = function (v1, v2) {
        return !(0, teamapps_client_core_1.deepEquals)(v1, v2);
    };
    LocalDateField.UiLocalDateToLocalDateTime = function (uiValue) {
        return uiValue != null ? LocalDateTime_1.LocalDateTime.fromObject({ year: uiValue.year, month: uiValue.month, day: uiValue.day }) : null;
    };
    LocalDateField.prototype.destroy = function () {
        _super.prototype.destroy.call(this);
        this.trivialComboBox.destroy();
    };
    LocalDateField.prototype.update = function (config) {
        this.setShowDropDownButton(config.showDropDownButton);
        this.setShowClearButton(config.showClearButton);
        this.setFavorPastDates(config.favorPastDates);
        this.setLocaleAndDateFormat(config.locale, config.dateFormat);
        this.trivialComboBox.setPlaceholderText(config.placeholderText);
        this.calendarBoxDropdown.defaultDate = LocalDateField.UiLocalDateToLocalDateTime(config.defaultSuggestionDate);
        this.config = config;
    };
    LocalDateField.prototype.setLocaleAndDateFormat = function (locale, dateFormat) {
        this.config.locale = locale;
        this.config.dateFormat = dateFormat;
        this.updateDateSuggestionEngine();
        this.dateRenderer = this.createDateRenderer();
        this.trivialComboBox.setValue(this.trivialComboBox.getValue());
    };
    LocalDateField.prototype.setFavorPastDates = function (favorPastDates) {
        this.config.favorPastDates = favorPastDates;
        this.updateDateSuggestionEngine();
    };
    LocalDateField.prototype.setShowDropDownButton = function (showDropDownButton) {
        this.config.showDropDownButton;
        this.trivialComboBox.setShowTrigger(showDropDownButton);
    };
    LocalDateField.prototype.setShowClearButton = function (showClearButton) {
        this.config.showClearButton;
        this.trivialComboBox.setShowClearButton(showClearButton);
    };
    return LocalDateField;
}(teamapps_client_core_components_1.AbstractField));
exports.LocalDateField = LocalDateField;
