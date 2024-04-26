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
exports.LocalTimeField = void 0;
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
var LocalDateTime_1 = require("./LocalDateTime");
var datetime_rendering_1 = require("./datetime-rendering");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var AbstractTimeField_1 = require("./AbstractTimeField");
var LocalTimeField = /** @class */ (function (_super) {
    __extends(LocalTimeField, _super);
    function LocalTimeField() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    LocalTimeField.prototype.initialize = function (config) {
        _super.prototype.initialize.call(this, config);
        this.getMainInnerDomElement().classList.add("UiLocalTimeField");
    };
    LocalTimeField.prototype.isValidData = function (v) {
        return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number");
    };
    LocalTimeField.prototype.displayCommittedValue = function () {
        var uiValue = this.getCommittedValue();
        if (uiValue) {
            this.trivialComboBox.setValue(LocalTimeField.localTimeToLocalDateTime(uiValue));
        }
        else {
            this.trivialComboBox.setValue(null);
        }
    };
    LocalTimeField.prototype.getTransientValue = function () {
        var selectedEntry = this.trivialComboBox.getValue();
        if (selectedEntry) {
            return [selectedEntry.hour, selectedEntry.minute, 0, 0];
        }
        else {
            return null;
        }
    };
    LocalTimeField.prototype.getReadOnlyHtml = function (value, availableWidth) {
        if (value != null) {
            return this.timeRenderer(LocalTimeField.localTimeToLocalDateTime(value));
        }
        else {
            return "";
        }
    };
    LocalTimeField.prototype.valuesChanged = function (v1, v2) {
        return !(0, teamapps_client_core_components_1.arraysEqual)(v1, v2);
    };
    LocalTimeField.localTimeToLocalDateTime = function (uiValue) {
        return LocalDateTime_1.LocalDateTime.fromObject({ hour: uiValue[0], minute: uiValue[1], second: uiValue[2], millisecond: uiValue[3] });
    };
    LocalTimeField.prototype.localDateTimeToString = function (entry) {
        return entry.setLocale(this.config.locale).toLocaleString(this.config.timeFormat);
    };
    LocalTimeField.prototype.createTimeRenderer = function () {
        var timeRenderer = (0, datetime_rendering_1.createTimeRenderer)(this.config.locale, this.config.timeFormat);
        return function (entry) { return timeRenderer(entry === null || entry === void 0 ? void 0 : entry.toUTC()); };
    };
    return LocalTimeField;
}(AbstractTimeField_1.AbstractTimeField));
exports.LocalTimeField = LocalTimeField;
