"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.CalendarBoxDropdown = void 0;
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
var teamapps_client_core_1 = require("teamapps-client-core");
var LocalDateTime_1 = require("../../LocalDateTime");
var CalendarBoxDropdown = /** @class */ (function () {
    function CalendarBoxDropdown(calendarBox, queryFunction, defaultDate) {
        var _this = this;
        this.calendarBox = calendarBox;
        this.queryFunction = queryFunction;
        this.defaultDate = defaultDate;
        this.onValueChanged = new teamapps_client_core_1.TeamAppsEvent();
        this.calendarBox.onChange.addListener(function (event) {
            _this.onValueChanged.fire({ value: event.value, finalSelection: event.timeUnitEdited == "day" });
        });
    }
    CalendarBoxDropdown.prototype.getMainDomElement = function () {
        return this.calendarBox.getMainDomElement();
    };
    CalendarBoxDropdown.prototype.setValue = function (value) {
        var _a;
        this.calendarBox.setSelectedDate((_a = value !== null && value !== void 0 ? value : this.defaultDate) !== null && _a !== void 0 ? _a : LocalDateTime_1.LocalDateTime.local());
    };
    CalendarBoxDropdown.prototype.getValue = function () {
        return this.calendarBox.getSelectedDate();
    };
    CalendarBoxDropdown.prototype.handleKeyboardInput = function (event) {
        if (["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"].indexOf(event.code) !== -1) {
            this.calendarBox.navigateTimeUnit("day", event.code.substr(5).toLowerCase());
            this.onValueChanged.fire({ value: this.calendarBox.getSelectedDate(), finalSelection: false });
            return true;
        }
    };
    CalendarBoxDropdown.prototype.handleQuery = function (query, selectionDirection, currentComboBoxValue) {
        return __awaiter(this, void 0, void 0, function () {
            var suggestedDate;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        if (!(!query && currentComboBoxValue != null)) return [3 /*break*/, 1];
                        this.calendarBox.setSelectedDate(currentComboBoxValue);
                        return [3 /*break*/, 3];
                    case 1: return [4 /*yield*/, this.queryFunction(query)];
                    case 2:
                        suggestedDate = _a.sent();
                        if (suggestedDate != null) {
                            this.calendarBox.setSelectedDate(suggestedDate);
                            return [2 /*return*/, true];
                        }
                        else {
                            return [2 /*return*/, false];
                        }
                        _a.label = 3;
                    case 3: return [2 /*return*/];
                }
            });
        });
    };
    CalendarBoxDropdown.prototype.destroy = function () {
        this.calendarBox.destroy();
    };
    CalendarBoxDropdown.prototype.getComponent = function () {
        return this.calendarBox;
    };
    return CalendarBoxDropdown;
}());
exports.CalendarBoxDropdown = CalendarBoxDropdown;
