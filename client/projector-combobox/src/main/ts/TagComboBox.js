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
exports.TagComboBox = void 0;
var teamapps_client_core_1 = require("teamapps-client-core");
var ComboBox_1 = require("./ComboBox");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var generated_1 = require("./generated");
var TrivialTagComboBox_1 = require("./trivial-components/TrivialTagComboBox");
var TrivialCore_1 = require("./trivial-components/TrivialCore");
var TreeBoxDropdown_1 = require("./trivial-components/dropdown/TreeBoxDropdown");
var TrivialTreeBox_1 = require("./trivial-components/TrivialTreeBox");
var TagComboBox = /** @class */ (function (_super) {
    __extends(TagComboBox, _super);
    function TagComboBox() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.onTextInput = new teamapps_client_core_1.TeamAppsEvent({
            throttlingMode: "debounce",
            delay: 250
        });
        _this.onSpecialKeyPressed = new teamapps_client_core_1.TeamAppsEvent({
            throttlingMode: "debounce",
            delay: 250
        });
        _this.resultCallbacksQueue = [];
        _this.freeTextIdEntryCounter = -1;
        return _this;
    }
    TagComboBox.prototype.initialize = function (config) {
        var _this = this;
        this.$originalInput = (0, teamapps_client_core_1.parseHtml)("<input type=\"text\" autocomplete=\"no\">");
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
                _this.onTextInput.fire({ enteredString: queryString }); // TODO this is definitely the wrong place for this!!
                return config.retrieveDropdownEntries({ queryString: queryString })
                    .then(function (entries) { return (0, teamapps_client_core_components_1.buildObjectTree)(entries, "id", "parentId"); });
            },
            textHighlightingEntryLimit: config.textHighlightingEntryLimit,
            preselectionMatcher: function (query, entry) { return entry.asString.toLowerCase().indexOf(query.toLowerCase()) >= 0; }
        }, this.trivialTreeBox);
        this.trivialTagComboBox = new TrivialTagComboBox_1.TrivialTagComboBox({
            selectedEntryRenderingFunction: function (entry) {
                if ((0, ComboBox_1.isFreeTextEntry)(entry)) {
                    return (0, TrivialCore_1.wrapWithDefaultTagWrapper)("<div class=\"free-text-entry\">".concat(entry.asString, "</div>"), config.deleteButtonsEnabled);
                }
                else {
                    return (0, TrivialCore_1.wrapWithDefaultTagWrapper)(_this.renderRecord(entry, false), config.deleteButtonsEnabled);
                }
            },
            showClearButton: config.clearButtonEnabled,
            autoComplete: !!config.autoCompletionEnabled,
            showTrigger: config.dropDownButtonVisible,
            editingMode: config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY ? 'readonly' : config.editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable',
            spinnerTemplate: "<div class=\"teamapps-spinner\" style=\"height: 20px; width: 20px; margin: 4px auto 4px auto;\"></div>",
            showDropDownOnResultsOnly: config.showDropDownAfterResultsArrive,
            entryToEditorTextFunction: function (e) { return e.asString; },
            freeTextEntryFactory: function (freeText) {
                return { id: _this.freeTextIdEntryCounter--, values: {}, asString: freeText };
            },
            selectionAcceptor: function (entry) {
                var violatesDistinctSetting = config.distinct && _this.trivialTagComboBox.getSelectedEntries().map(function (e) { return e.id; }).indexOf(entry.id) !== -1;
                var violatesMaxEntriesSetting = !!config.maxEntries && _this.trivialTagComboBox.getSelectedEntries().length >= config.maxEntries;
                var violatesFreeTextSetting = !config.freeTextEnabled && (0, ComboBox_1.isFreeTextEntry)(entry);
                return !violatesDistinctSetting && !violatesMaxEntriesSetting && !violatesFreeTextSetting;
            },
            twoStepDeletion: this.config.twoStepDeletionEnabled,
            preselectFirstQueryResult: config.firstEntryAutoHighlight,
            placeholderText: config.placeholderText,
            dropDownMaxHeight: config.dropDownMaxHeight,
            dropDownMinWidth: config.dropDownMinWidth
        }, this.treeBoxDropdown);
        this.trivialTagComboBox.getMainDomElement().classList.add("UiTagComboBox");
        this.setWrappingMode(config.wrappingMode);
        this.trivialTagComboBox.onValueChanged.addListener(function () { return _this.commit(); });
        this.trivialTagComboBox.getEditor().addEventListener("keydown", function (e) {
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
        this.trivialTagComboBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
        this.trivialTagComboBox.getMainDomElement().querySelector(":scope .tr-editor").classList.add("field-background");
        this.trivialTagComboBox.getMainDomElement().querySelector(":scope .tr-trigger").classList.add("field-border");
    };
    TagComboBox.prototype.initFocusHandling = function () {
        var _this = this;
        this.trivialTagComboBox.onFocus.addListener(function () { return _this.onFocus.fire({}); });
        this.trivialTagComboBox.onBlur.addListener(function () { return _this.onBlur.fire({}); });
    };
    TagComboBox.prototype.renderRecord = function (record, dropdown) {
        var template = (dropdown ? record.dropDownTemplate : record.displayTemplate);
        if (template != null) {
            return template.render(record.values);
        }
        else {
            return "<div class=\"string-template\">".concat(record.asString, "</div>");
        }
    };
    TagComboBox.prototype.isValidData = function (v) {
        return v == null || Array.isArray(v);
    };
    TagComboBox.prototype.getMainInnerDomElement = function () {
        return this.trivialTagComboBox.getMainDomElement();
    };
    TagComboBox.prototype.displayCommittedValue = function () {
        var uiValue = this.getCommittedValue();
        this.trivialTagComboBox.setSelectedEntries(uiValue);
    };
    TagComboBox.prototype.getTransientValue = function () {
        return this.trivialTagComboBox.getSelectedEntries();
    };
    TagComboBox.prototype.convertValueForSendingToServer = function (values) {
        return values.map(function (value) { return (0, ComboBox_1.isFreeTextEntry)(value) ? value.asString : value.id; });
    };
    TagComboBox.prototype.focus = function () {
        this.trivialTagComboBox.focus(); // TODO
    };
    TagComboBox.prototype.onEditingModeChanged = function (editingMode) {
        var _a;
        (_a = this.getMainElement().classList).remove.apply(_a, Object.values(teamapps_client_core_components_1.AbstractField.editingModeCssClasses));
        this.getMainElement().classList.add(teamapps_client_core_components_1.AbstractField.editingModeCssClasses[editingMode]);
        if (editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY) {
            this.trivialTagComboBox.setEditingMode("readonly");
        }
        else if (editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED) {
            this.trivialTagComboBox.setEditingMode("disabled");
        }
        else {
            this.trivialTagComboBox.setEditingMode("editable");
        }
    };
    TagComboBox.prototype.replaceFreeTextEntry = function (freeText, record) {
        var selectedEntries = this.trivialTagComboBox.getSelectedEntries();
        var changed = false;
        for (var i = 0; i < selectedEntries.length; i++) {
            var entry = selectedEntries[i];
            if ((0, ComboBox_1.isFreeTextEntry)(entry) && entry.asString === freeText) {
                selectedEntries[i] = record;
                changed = true;
            }
        }
        if (changed) {
            this.setCommittedValue(selectedEntries);
        }
    };
    TagComboBox.prototype.setDropDownButtonVisible = function (dropDownButtonVisible) {
        this.config.dropDownButtonVisible = dropDownButtonVisible;
        this.trivialTagComboBox.setShowTrigger(dropDownButtonVisible);
    };
    TagComboBox.prototype.setShowDropDownAfterResultsArrive = function (showDropDownAfterResultsArrive) {
        this.config.showDropDownAfterResultsArrive = showDropDownAfterResultsArrive;
        this.trivialTagComboBox.setShowDropDownOnResultsOnly(showDropDownAfterResultsArrive);
    };
    TagComboBox.prototype.setFirstEntryAutoHighlight = function (firstEntryAutoSelectEnabled) {
        this.config.firstEntryAutoHighlight = firstEntryAutoSelectEnabled;
        this.trivialTagComboBox.setPreselectFirstQueryResult(firstEntryAutoSelectEnabled);
    };
    TagComboBox.prototype.setTextHighlightingEntryLimit = function (textHighlightingEntryLimit) {
        this.config.textHighlightingEntryLimit = textHighlightingEntryLimit;
        this.treeBoxDropdown.setTextHighlightingEntryLimit(textHighlightingEntryLimit);
    };
    TagComboBox.prototype.setAutoCompletionEnabled = function (autoCompletionEnabled) {
        this.config.autoCompletionEnabled = autoCompletionEnabled;
        this.trivialTagComboBox.setAutoComplete(autoCompletionEnabled);
    };
    TagComboBox.prototype.setFreeTextEnabled = function (freeTextEnabled) {
        this.config.freeTextEnabled = freeTextEnabled;
    };
    TagComboBox.prototype.setClearButtonEnabled = function (clearButtonEnabled) {
        this.config.clearButtonEnabled = clearButtonEnabled;
        this.trivialTagComboBox.setShowClearButton(clearButtonEnabled);
    };
    TagComboBox.prototype.setExpandersVisible = function (expandersVisible) {
        this.config.expandersVisible = expandersVisible;
        this.trivialTreeBox.setShowExpanders(expandersVisible);
    };
    TagComboBox.prototype.setExpandAnimationEnabled = function (expandAnimationEnabled) {
        this.config.expandAnimationEnabled = expandAnimationEnabled;
        this.trivialTreeBox.setAnimationDuration(expandAnimationEnabled ? 120 : 0);
    };
    TagComboBox.prototype.setPlaceholderText = function (placeholderText) {
        this.trivialTagComboBox.setPlaceholderText(placeholderText);
    };
    TagComboBox.prototype.setDropDownMinWidth = function (dropDownMinWidth) {
        this.trivialTagComboBox.setDropdownMinWidth(dropDownMinWidth);
    };
    TagComboBox.prototype.setDropDownMaxHeight = function (dropDownMaxHeight) {
        this.trivialTagComboBox.setDropdownMaxHeight(dropDownMaxHeight);
    };
    TagComboBox.prototype.setIndentation = function (indentation) {
        this.trivialTreeBox.setIndentationWidth(indentation);
    };
    TagComboBox.prototype.setMaxEntries = function (maxEntries) {
        this.config.maxEntries = maxEntries;
    };
    TagComboBox.prototype.setWrappingMode = function (wrappingMode) {
        this.config.wrappingMode = wrappingMode;
        this.trivialTagComboBox.getMainDomElement().classList.toggle("wrapping-mode-single-line", wrappingMode === generated_1.DtoTagComboBoxWrappingMode.SINGLE_LINE);
        this.trivialTagComboBox.getMainDomElement().classList.toggle("wrapping-mode-single-tag-per-line", wrappingMode === generated_1.DtoTagComboBoxWrappingMode.SINGLE_TAG_PER_LINE);
    };
    TagComboBox.prototype.setDistinct = function (distinct) {
        this.config.distinct = distinct;
    };
    TagComboBox.prototype.setTwoStepDeletionEnabled = function (twoStepDeletionEnabled) {
        this.trivialTagComboBox.setTwoStepDeletion(twoStepDeletionEnabled);
    };
    TagComboBox.prototype.setDeleteButtonsEnabled = function (deleteButtonsEnabled) {
        this.config.deleteButtonsEnabled = deleteButtonsEnabled;
        this.trivialTagComboBox.setSelectedEntries(this.trivialTagComboBox.getSelectedEntries());
    };
    TagComboBox.prototype.destroy = function () {
        _super.prototype.destroy.call(this);
        this.trivialTagComboBox.destroy();
        this.$originalInput.remove();
    };
    TagComboBox.prototype.getReadOnlyHtml = function (records, availableWidth) {
        var _this = this;
        var content;
        if (records != null) {
            content = records.map(function (record) {
                return (0, TrivialCore_1.wrapWithDefaultTagWrapper)(_this.renderRecord(record, false));
            }).join("");
        }
        else {
            content = "";
        }
        return "<div class=\"static-readonly-UiTagComboBox\">".concat(content, "</div>");
    };
    TagComboBox.prototype.getDefaultValue = function () {
        return [];
    };
    TagComboBox.prototype.valuesChanged = function (v1, v2) {
        if (v1 == null && v2 == null) {
            return false;
        }
        var nullAndNonNull = ((v1 == null) !== (v2 == null));
        var differentArrayLengths = v1 != null && v2 != null && v1.length !== v2.length;
        if (nullAndNonNull || differentArrayLengths) {
            return true;
        }
        else {
            for (var i = 0; i < v1.length; i++) {
                if (v1[i].id !== v2[i].id) {
                    return true;
                }
            }
            return false;
        }
    };
    return TagComboBox;
}(teamapps_client_core_components_1.AbstractField));
exports.TagComboBox = TagComboBox;
