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
exports.AbstractDateTimeField = void 0;
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
var TrivialDateTimeField_1 = require("./trivial-components/TrivialDateTimeField");
var DateSuggestionEngine_1 = require("./DateSuggestionEngine");
var datetime_rendering_1 = require("./datetime-rendering");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var teamapps_client_core_1 = require("teamapps-client-core");
var AbstractDateTimeField = /** @class */ (function (_super) {
    __extends(AbstractDateTimeField, _super);
    function AbstractDateTimeField() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.onTextInput = new teamapps_client_core_1.TeamAppsEvent();
        _this.onSpecialKeyPressed = new teamapps_client_core_1.TeamAppsEvent();
        return _this;
    }
    AbstractDateTimeField.prototype.initialize = function (config) {
        var _this = this;
        this.updateDateSuggestionEngine();
        this.dateRenderer = this.createDateRenderer();
        this.timeRenderer = this.createTimeRenderer();
        this.trivialDateTimeField = new TrivialDateTimeField_1.TrivialDateTimeField({
            timeZone: this.getTimeZone(),
            locale: config.locale,
            dateFormat: config.dateFormat,
            timeFormat: config.timeFormat,
            showTrigger: config.showDropDownButton,
            editingMode: config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY ? 'readonly' : config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable',
            favorPastDates: config.favorPastDates
        });
        this.trivialDateTimeField.getMainDomElement().classList.add("DtoAbstractDateTimeField");
        this.trivialDateTimeField.onChange.addListener(function () { return _this.commit(); });
        this.trivialDateTimeField.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
        this.trivialDateTimeField.getMainDomElement().querySelectorAll(":scope .tr-date-editor, :scope .tr-time-editor").forEach(function (element) { return element.classList.add("field-background"); });
        this.trivialDateTimeField.getMainDomElement().querySelector(":scope .tr-trigger").classList.add("field-border");
    };
    AbstractDateTimeField.prototype.createDateRenderer = function () {
        return (0, datetime_rendering_1.createDateRenderer)(this.config.locale, this.config.dateFormat);
    };
    AbstractDateTimeField.prototype.createTimeRenderer = function () {
        return (0, datetime_rendering_1.createTimeRenderer)(this.config.locale, this.config.timeFormat);
    };
    AbstractDateTimeField.prototype.dateTimeToDateString = function (dateTime) {
        return dateTime.setLocale(this.config.locale).toLocaleString(this.config.dateFormat);
    };
    AbstractDateTimeField.prototype.dateTimeToTimeString = function (dateTime) {
        return dateTime.setLocale(this.config.locale).toLocaleString(this.config.timeFormat);
    };
    AbstractDateTimeField.prototype.updateDateSuggestionEngine = function () {
        this.dateSuggestionEngine = new DateSuggestionEngine_1.DateSuggestionEngine({
            locale: this.config.locale,
            favorPastDates: this.config.favorPastDates
        });
    };
    AbstractDateTimeField.prototype.getMainInnerDomElement = function () {
        return this.trivialDateTimeField.getMainDomElement();
    };
    AbstractDateTimeField.prototype.initFocusHandling = function () {
        var _this = this;
        this.trivialDateTimeField.onFocus.addListener(function () { return _this.onFocus.fire({}); });
        this.trivialDateTimeField.onBlur.addListener(function () { return _this.onBlur.fire({}); });
    };
    AbstractDateTimeField.prototype.focus = function () {
        this.trivialDateTimeField.focus();
    };
    AbstractDateTimeField.prototype.onEditingModeChanged = function (editingMode) {
        var _a;
        (_a = this.getMainElement().classList).remove.apply(_a, Object.values(teamapps_client_core_components_1.AbstractField.editingModeCssClasses));
        this.getMainElement().classList.add(teamapps_client_core_components_1.AbstractField.editingModeCssClasses[editingMode]);
        if (editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY) {
            this.trivialDateTimeField.setEditingMode("readonly");
        }
        else if (editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED) {
            this.trivialDateTimeField.setEditingMode("disabled");
        }
        else {
            this.trivialDateTimeField.setEditingMode("editable");
        }
    };
    AbstractDateTimeField.prototype.destroy = function () {
        _super.prototype.destroy.call(this);
        this.trivialDateTimeField.destroy();
    };
    AbstractDateTimeField.prototype.getDefaultValue = function () {
        return null;
    };
    AbstractDateTimeField.prototype.setLocaleAndFormats = function (locale, dateFormat, timeFormat) {
        this.config.locale = locale;
        this.config.dateFormat = dateFormat;
        this.config.timeFormat = timeFormat;
        this.updateDateSuggestionEngine();
        this.dateRenderer = this.createDateRenderer();
        this.trivialDateTimeField.setLocaleAndFormats(locale, dateFormat, timeFormat);
    };
    AbstractDateTimeField.prototype.setFavorPastDates = function (favorPastDates) {
        // TODO
        console.warn("TODO: implement DtoAbstractDateTimeField.setFavorPastDates()");
    };
    AbstractDateTimeField.prototype.setShowDropDownButton = function (showDropDownButton) {
        // TODO
        console.warn("TODO: implement DtoAbstractDateTimeField.setShowDropDownButton()");
    };
    return AbstractDateTimeField;
}(teamapps_client_core_components_1.AbstractField));
exports.AbstractDateTimeField = AbstractDateTimeField;
