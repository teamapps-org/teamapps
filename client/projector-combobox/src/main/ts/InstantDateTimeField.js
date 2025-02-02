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
exports.InstantDateTimeField = void 0;
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
var InstantDateTimeField = /** @class */ (function (_super) {
    __extends(InstantDateTimeField, _super);
    function InstantDateTimeField() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    InstantDateTimeField.prototype.initialize = function (config) {
        _super.prototype.initialize.call(this, config);
        this.getMainInnerDomElement().classList.add("UiDateTimeField");
    };
    InstantDateTimeField.prototype.getTimeZone = function () {
        return this.config.timeZoneId;
    };
    InstantDateTimeField.prototype.isValidData = function (v) {
        return v == null || typeof v === "number";
    };
    InstantDateTimeField.prototype.displayCommittedValue = function () {
        var uiValue = this.getCommittedValue();
        if (uiValue) {
            this.trivialDateTimeField.setValue(this.toDateTime(uiValue));
        }
        else {
            this.trivialDateTimeField.setValue(null);
        }
    };
    InstantDateTimeField.prototype.toDateTime = function (uiValue) {
        return luxon_1.DateTime.fromMillis(uiValue).setZone(this.config.timeZoneId);
    };
    InstantDateTimeField.prototype.getTransientValue = function () {
        var value = this.trivialDateTimeField.getValue();
        if (value) {
            return value.valueOf();
        }
        else {
            return null;
        }
    };
    InstantDateTimeField.prototype.getReadOnlyHtml = function (value, availableWidth) {
        if (value != null) {
            return "<div class=\"static-readonly-UiDateTimeField\">"
                + this.dateRenderer(this.toDateTime(value))
                + this.timeRenderer(this.toDateTime(value))
                + '</div>';
        }
        else {
            return "";
        }
    };
    InstantDateTimeField.prototype.getDefaultValue = function () {
        return null;
    };
    InstantDateTimeField.prototype.valuesChanged = function (v1, v2) {
        return v1 !== v2;
    };
    InstantDateTimeField.prototype.setTimeZoneId = function (timeZoneId) {
        this.config.timeZoneId = timeZoneId;
        this.displayCommittedValue();
    };
    return InstantDateTimeField;
}(AbstractDateTimeField_1.AbstractDateTimeField));
exports.InstantDateTimeField = InstantDateTimeField;
