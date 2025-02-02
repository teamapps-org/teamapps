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
exports.AbstractTimeField = void 0;
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
var TimeSuggestionEngine_1 = require("./TimeSuggestionEngine");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var teamapps_client_core_1 = require("teamapps-client-core");
var TrivialComboBox_1 = require("./trivial-components/TrivialComboBox");
var TreeBoxDropdown_1 = require("./trivial-components/dropdown/TreeBoxDropdown");
var TrivialTreeBox_1 = require("./trivial-components/TrivialTreeBox");
var AbstractTimeField = /** @class */ (function (_super) {
    __extends(AbstractTimeField, _super);
    function AbstractTimeField() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.onTextInput = new teamapps_client_core_1.TeamAppsEvent({ throttlingMode: "debounce", delay: 250 });
        _this.onSpecialKeyPressed = new teamapps_client_core_1.TeamAppsEvent({ throttlingMode: "debounce", delay: 250 });
        return _this;
    }
    AbstractTimeField.prototype.initialize = function (config) {
        var _this = this;
        var timeSuggestionEngine = new TimeSuggestionEngine_1.TimeSuggestionEngine();
        this.timeRenderer = this.createTimeRenderer();
        this.trivialComboBox = new TrivialComboBox_1.TrivialComboBox({
            showTrigger: config.showDropDownButton,
            entryToEditorTextFunction: function (entry) { return _this.localDateTimeToString(entry); },
            editingMode: config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY ? 'readonly' : config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable',
            selectedEntryRenderingFunction: function (localDateTime) { return _this.timeRenderer(localDateTime); },
        }, new TreeBoxDropdown_1.TreeBoxDropdown({
            queryFunction: function (searchString) { return timeSuggestionEngine.generateSuggestions(searchString); },
            textHighlightingEntryLimit: -1,
            preselectionMatcher: function (query, entry) { return _this.localDateTimeToString(entry).toLowerCase().indexOf(query.toLowerCase()) >= 0; }
        }, new TrivialTreeBox_1.TrivialTreeBox({
            entryRenderingFunction: function (localDateTime) { return _this.timeRenderer(localDateTime); },
        })));
        this.trivialComboBox.getEditor().addEventListener("input", function (e) { return _this.onTextInput.fire({ enteredString: e.target.value }); });
        this.trivialComboBox.getMainDomElement().classList.add("DtoAbstractTimeField");
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
        this.trivialComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
        this.trivialComboBox.getMainDomElement().querySelector(":scope .tr-editor").classList.add("field-background");
        this.trivialComboBox.getMainDomElement().querySelector(":scope .tr-trigger").classList.add("field-border");
    };
    AbstractTimeField.prototype.getMainInnerDomElement = function () {
        return this.trivialComboBox.getMainDomElement();
    };
    AbstractTimeField.prototype.initFocusHandling = function () {
        var _this = this;
        this.trivialComboBox.onFocus.addListener(function () { return _this.onFocus.fire({}); });
        this.trivialComboBox.onBlur.addListener(function () { return _this.onBlur.fire({}); });
    };
    AbstractTimeField.prototype.focus = function () {
        this.trivialComboBox.focus();
    };
    AbstractTimeField.prototype.onEditingModeChanged = function (editingMode) {
        var _a;
        (_a = this.getMainInnerDomElement().classList).remove.apply(_a, Object.values(teamapps_client_core_components_1.AbstractField.editingModeCssClasses));
        this.getMainInnerDomElement().classList.add(teamapps_client_core_components_1.AbstractField.editingModeCssClasses[editingMode]);
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
    AbstractTimeField.prototype.destroy = function () {
        _super.prototype.destroy.call(this);
        this.trivialComboBox.destroy();
    };
    AbstractTimeField.prototype.getDefaultValue = function () {
        return null;
    };
    AbstractTimeField.prototype.setLocaleAndTimeFormat = function (locale, timeFormat) {
        this.config.locale = locale;
        this.config.timeFormat = timeFormat;
        this.timeRenderer = this.createTimeRenderer();
        this.trivialComboBox.setValue(this.trivialComboBox.getValue());
    };
    AbstractTimeField.prototype.setShowDropDownButton = function (showDropDownButton) {
        this.trivialComboBox.setShowTrigger(showDropDownButton);
    };
    AbstractTimeField.prototype.setShowClearButton = function (showClearButton) {
        this.trivialComboBox.setShowClearButton(showClearButton);
    };
    return AbstractTimeField;
}(teamapps_client_core_components_1.AbstractField));
exports.AbstractTimeField = AbstractTimeField;
