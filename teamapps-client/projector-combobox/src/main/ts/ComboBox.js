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
exports.ComboBox = exports.isFreeTextEntry = void 0;
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
var TrivialComboBox_1 = require("./trivial-components/TrivialComboBox");
var TrivialTreeBox_1 = require("./trivial-components/TrivialTreeBox");
var TreeBoxDropdown_1 = require("./trivial-components/dropdown/TreeBoxDropdown");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var teamapps_client_core_1 = require("teamapps-client-core");
function isFreeTextEntry(o) {
    return o != null && o.id < 0;
}
exports.isFreeTextEntry = isFreeTextEntry;
var ComboBox = /** @class */ (function (_super) {
    __extends(ComboBox, _super);
    function ComboBox() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.onTextInput = new teamapps_client_core_1.TeamAppsEvent({ throttlingMode: "debounce", delay: 250 });
        _this.onSpecialKeyPressed = new teamapps_client_core_1.TeamAppsEvent({ throttlingMode: "debounce", delay: 250 });
        _this.freeTextIdEntryCounter = -1;
        return _this;
    }
    ComboBox.prototype.initialize = function (config) {
        var _this = this;
        this.trivialTreeBox = new TrivialTreeBox_1.TrivialTreeBox({
            childrenProperty: "__children",
            expandedProperty: "expanded",
            showExpanders: config.expandersVisible,
            entryRenderingFunction: function (entry) { return _this.renderRecord(entry, true); },
            idFunction: function (entry) { return entry && entry.id; },
            lazyChildrenQueryFunction: function (node) { return __awaiter(_this, void 0, void 0, function () { var _a; return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0:
                        _a = teamapps_client_core_components_1.buildObjectTree;
                        return [4 /*yield*/, config.lazyChildren({ parentId: node.id })];
                    case 1: return [2 /*return*/, _a.apply(void 0, [_b.sent(), "id", "parentId"])];
                }
            }); }); },
            lazyChildrenFlag: function (entry) { return entry.lazyChildren; },
            selectableDecider: function (entry) { return entry.selectable; },
            selectOnHover: true,
            animationDuration: this.config.expandAnimationEnabled ? 120 : 0
        });
        this.treeBoxDropdown = new TreeBoxDropdown_1.TreeBoxDropdown({
            queryFunction: function (queryString) {
                return config.retrieveDropdownEntries({ queryString: queryString })
                    .then(function (entries) { return (0, teamapps_client_core_components_1.buildObjectTree)(entries, "id", "parentId"); });
            },
            textHighlightingEntryLimit: config.textHighlightingEntryLimit,
            preselectionMatcher: function (query, entry) { return entry.asString.toLowerCase().indexOf(query.toLowerCase()) >= 0; }
        }, this.trivialTreeBox);
        this.trivialComboBox = new TrivialComboBox_1.TrivialComboBox({
            selectedEntryRenderingFunction: function (entry) {
                if (entry == null) {
                    return "";
                }
                else if (isFreeTextEntry(entry)) {
                    return "<div class=\"free-text-entry\">".concat(entry.asString, "</div>");
                }
                else {
                    return _this.renderRecord(entry, false);
                }
            },
            autoComplete: !!config.autoCompletionEnabled,
            showTrigger: config.dropDownButtonVisible,
            editingMode: config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY ? 'readonly' : config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable',
            showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
            showClearButton: config.clearButtonEnabled,
            entryToEditorTextFunction: function (e) { return e.asString; },
            textToEntryFunction: this.createTextToEntryFunction(config.freeTextEnabled),
            preselectFirstQueryResult: config.firstEntryAutoHighlight,
            placeholderText: config.placeholderText,
            dropDownMaxHeight: config.dropDownMaxHeight,
            dropDownMinWidth: config.dropDownMinWidth
        }, this.treeBoxDropdown);
        this.trivialComboBox.getMainDomElement().classList.add("ComboBox");
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
    };
    ComboBox.prototype.createTextToEntryFunction = function (freeTextEnabled) {
        var _this = this;
        return function (freeText) {
            if (freeTextEnabled) {
                return { id: _this.freeTextIdEntryCounter--, values: {}, asString: freeText };
            }
            else {
                return null;
            }
        };
    };
    ComboBox.prototype.initFocusHandling = function () {
        var _this = this;
        this.trivialComboBox.onFocus.addListener(function () { return _this.onFocus.fire({}); });
        this.trivialComboBox.onBlur.addListener(function () { return _this.onBlur.fire({}); });
    };
    ComboBox.prototype.renderRecord = function (record, dropdown) {
        var template = (dropdown ? record.dropDownTemplate : record.displayTemplate);
        if (template != null) {
            return template.render(record.values);
        }
        else {
            return "<div class=\"string-template\">".concat(record.asString, "</div>");
        }
    };
    ComboBox.prototype.isValidData = function (v) {
        return v == null || typeof v === "object" && v.id != null;
    };
    ComboBox.prototype.getMainInnerDomElement = function () {
        return this.trivialComboBox.getMainDomElement();
    };
    ComboBox.prototype.displayCommittedValue = function () {
        var uiValue = this.getCommittedValue();
        this.trivialComboBox.setValue(uiValue);
    };
    ComboBox.prototype.getTransientValue = function () {
        return this.trivialComboBox.getValue();
    };
    ComboBox.prototype.convertValueForSendingToServer = function (value) {
        if (value == null) {
            return null;
        }
        return isFreeTextEntry(value) ? value.asString : value.id;
    };
    ComboBox.prototype.focus = function () {
        if (this.isEditable()) {
            this.trivialComboBox.focus();
        }
    };
    ComboBox.prototype.onEditingModeChanged = function (editingMode) {
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
    ComboBox.prototype.replaceFreeTextEntry = function (freeText, record) {
        var selectedEntry = this.trivialComboBox.getValue();
        if (isFreeTextEntry(selectedEntry) && selectedEntry.asString === freeText) {
            this.setCommittedValue(record);
        }
    };
    ComboBox.prototype.destroy = function () {
        _super.prototype.destroy.call(this);
        this.trivialComboBox.destroy();
    };
    ComboBox.prototype.getReadOnlyHtml = function (value, availableWidth) {
        if (value != null) {
            return "<div class=\"static-readonly-ComboBox\">".concat(this.renderRecord(value, false), "</div>");
        }
        else {
            return "";
        }
    };
    ComboBox.prototype.getDefaultValue = function () {
        return null;
    };
    ComboBox.prototype.valuesChanged = function (v1, v2) {
        var nullAndNonNull = ((v1 == null) !== (v2 == null));
        var nonNullAndValuesDifferent = (v1 != null && v2 != null && (v1.id !== v2.id
            || v1.id !== v2.id));
        return nullAndNonNull || nonNullAndValuesDifferent;
    };
    ComboBox.prototype.setDropDownButtonVisible = function (dropDownButtonVisible) {
        this.config.dropDownButtonVisible = dropDownButtonVisible;
        this.trivialComboBox.setShowTrigger(dropDownButtonVisible);
    };
    ComboBox.prototype.setShowDropDownAfterResultsArrive = function (showDropDownAfterResultsArrive) {
        this.config.showDropDownAfterResultsArrive = showDropDownAfterResultsArrive;
        this.trivialComboBox.setShowDropDownOnResultsOnly(showDropDownAfterResultsArrive);
    };
    ComboBox.prototype.setFirstEntryAutoHighlight = function (firstEntryAutoSelectEnabled) {
        this.config.firstEntryAutoHighlight = firstEntryAutoSelectEnabled;
        this.trivialComboBox.setPreselectFirstQueryResult(firstEntryAutoSelectEnabled);
    };
    ComboBox.prototype.setTextHighlightingEntryLimit = function (textHighlightingEntryLimit) {
        this.config.textHighlightingEntryLimit = textHighlightingEntryLimit;
        this.treeBoxDropdown.setTextHighlightingEntryLimit(textHighlightingEntryLimit);
    };
    ComboBox.prototype.setAutoCompletionEnabled = function (autoCompletionEnabled) {
        this.config.autoCompletionEnabled = autoCompletionEnabled;
        this.trivialComboBox.setAutoComplete(autoCompletionEnabled);
    };
    ComboBox.prototype.setFreeTextEnabled = function (freeTextEnabled) {
        this.config.freeTextEnabled = freeTextEnabled;
        this.trivialComboBox.setTextToEntryFunction(this.createTextToEntryFunction(freeTextEnabled));
    };
    ComboBox.prototype.setClearButtonEnabled = function (clearButtonEnabled) {
        this.config.clearButtonEnabled = clearButtonEnabled;
        this.trivialComboBox.setShowClearButton(clearButtonEnabled);
    };
    ComboBox.prototype.setExpandersVisible = function (expandersVisible) {
        this.config.expandersVisible = expandersVisible;
        this.trivialTreeBox.setShowExpanders(expandersVisible);
    };
    ComboBox.prototype.setExpandAnimationEnabled = function (expandAnimationEnabled) {
        this.config.expandAnimationEnabled = expandAnimationEnabled;
        this.trivialTreeBox.setAnimationDuration(expandAnimationEnabled ? 120 : 0);
    };
    ComboBox.prototype.setPlaceholderText = function (placeholderText) {
        this.trivialComboBox.setPlaceholderText(placeholderText);
    };
    ComboBox.prototype.setDropDownMinWidth = function (dropDownMinWidth) {
        this.trivialComboBox.setDropdownMinWidth(dropDownMinWidth);
    };
    ComboBox.prototype.setDropDownMaxHeight = function (dropDownMaxHeight) {
        this.trivialComboBox.setDropdownMaxHeight(dropDownMaxHeight);
    };
    ComboBox.prototype.setIndentation = function (indentation) {
        this.trivialTreeBox.setIndentationWidth(indentation);
    };
    return ComboBox;
}(teamapps_client_core_components_1.AbstractField));
exports.ComboBox = ComboBox;
