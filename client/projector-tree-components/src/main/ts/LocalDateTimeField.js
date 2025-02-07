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
exports.LocalDateTimeField = void 0;
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
var luxon_1 = require("luxon");
var AbstractDateTimeField_1 = require("./AbstractDateTimeField");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var LocalDateTimeField = /** @class */ (function (_super) {
    __extends(LocalDateTimeField, _super);
    function LocalDateTimeField() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    LocalDateTimeField.prototype.initialize = function (config) {
        _super.prototype.initialize.call(this, config);
        this.getMainInnerDomElement().classList.add("UiDateTimeField");
    };
    LocalDateTimeField.prototype.getTimeZone = function () {
        return "UTC";
    };
    LocalDateTimeField.prototype.isValidData = function (v) {
        return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number" && typeof v[2] === "number" && typeof v[3] === "number" && typeof v[4] === "number");
    };
    LocalDateTimeField.prototype.displayCommittedValue = function () {
        var uiValue = this.getCommittedValue();
        if (uiValue) {
            this.trivialDateTimeField.setValue(LocalDateTimeField.toDateTime(uiValue));
        }
        else {
            this.trivialDateTimeField.setValue(null);
        }
    };
    LocalDateTimeField.toDateTime = function (uiValue) {
        return luxon_1.DateTime.fromObject({
            year: uiValue[0],
            month: uiValue[1],
            day: uiValue[2],
            hour: uiValue[3],
            minute: uiValue[4],
            second: uiValue[5] || 0,
            millisecond: uiValue[6] || 0,
            zone: "UTC"
        });
    };
    LocalDateTimeField.prototype.getTransientValue = function () {
        var value = this.trivialDateTimeField.getValue();
        if (value) {
            return [value.year || 0, value.month || 0, value.day || 0, value.hour || 0, value.minute || 0, 0, 0];
        }
        else {
            return null;
        }
    };
    LocalDateTimeField.prototype.getReadOnlyHtml = function (value, availableWidth) {
        if (value != null) {
            return "<div class=\"static-readonly-UiDateTimeField\">"
                + this.dateRenderer(LocalDateTimeField.toDateTime(value))
                + this.timeRenderer(LocalDateTimeField.toDateTime(value))
                + '</div>';
        }
        else {
            return "";
        }
    };
    LocalDateTimeField.prototype.getDefaultValue = function () {
        return null;
    };
    LocalDateTimeField.prototype.valuesChanged = function (v1, v2) {
        return !(0, teamapps_client_core_components_1.arraysEqual)(v1, v2);
    };
    return LocalDateTimeField;
}(AbstractDateTimeField_1.AbstractDateTimeField));
exports.LocalDateTimeField = LocalDateTimeField;
