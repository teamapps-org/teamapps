"use strict";
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
exports.TrivialUnitBox = void 0;
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
var TrivialCore_1 = require("./TrivialCore");
var TrivialTreeBox_1 = require("./TrivialTreeBox");
var teamapps_client_core_1 = require("teamapps-client-core");
var TreeBoxDropdown_1 = require("./dropdown/TreeBoxDropdown");
var ComboBoxPopper_1 = require("./ComboBoxPopper");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var TrivialUnitBox = /** @class */ (function () {
    function TrivialUnitBox(options) {
        var _this = this;
        this.onChange = new teamapps_client_core_1.TeamAppsEvent();
        this.onSelectedEntryChanged = new teamapps_client_core_1.TeamAppsEvent();
        this.onFocus = new teamapps_client_core_1.TeamAppsEvent();
        this.onBlur = new teamapps_client_core_1.TeamAppsEvent();
        this.clickInsideEditorWasWhileNotHavingFocus = false;
        this.blurCausedByClickInsideComponent = false;
        this.dropDownOpen = false;
        this.config = __assign({ numberFormatFunction: function (entry) { return new Intl.NumberFormat("en-US", {
                useGrouping: true,
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }); }, unitDisplayPosition: 'right', allowNullAmount: true, entryRenderingFunction: TrivialCore_1.DEFAULT_RENDERING_FUNCTIONS.currency2Line, selectedEntryRenderingFunction: TrivialCore_1.DEFAULT_RENDERING_FUNCTIONS.currencySingleLineShort, noEntriesTemplate: TrivialCore_1.DEFAULT_TEMPLATES.defaultNoEntriesTemplate, entries: null, queryFunction: null, queryOnNonNumberCharacters: true, openDropdownOnEditorClick: false, showTrigger: true, matchingOptions: {
                matchingMode: 'prefix-word',
                ignoreCase: true
            }, editingMode: 'editable' }, options);
        if (!this.config.queryFunction) {
            this.config.queryFunction = (0, TrivialCore_1.defaultListQueryFunctionFactory)(this.config.entries || [], ["code", "name", "symbol"], this.config.matchingOptions);
            this.usingDefaultQueryFunction = true;
        }
        this.$unitBox = (0, teamapps_client_core_1.parseHtml)("<div class=\"tr-unitbox tr-input-wrapper\">\n\t\t\t<input type=\"text\" autocomplete=\"false\"></input>\n\t\t\t<div class=\"tr-unitbox-selected-entry-and-trigger-wrapper\">\n\t\t\t\t<div class=\"tr-unitbox-selected-entry-wrapper\"></div>\n\t\t\t\t<div class=\"tr-trigger\"><span class=\"tr-trigger-icon\"></div>\n\t\t\t</div>\n\t\t</div>");
        this.$editor = this.$unitBox.querySelector(':scope input');
        this.$selectedEntryAndTriggerWrapper = this.$unitBox.querySelector(':scope .tr-unitbox-selected-entry-and-trigger-wrapper');
        this.$selectedEntryWrapper = this.$selectedEntryAndTriggerWrapper.querySelector(':scope .tr-unitbox-selected-entry-wrapper');
        this.$trigger = this.$selectedEntryAndTriggerWrapper.querySelector(':scope .tr-trigger');
        if (!this.config.showTrigger) {
            this.$trigger.classList.add('hidden');
        }
        this.$selectedEntryAndTriggerWrapper.addEventListener('mousedown', function () {
            if (_this.dropDownOpen) {
                _this.closeDropDown();
            }
            else if (_this.editingMode === "editable") {
                _this.openDropDown();
                _this.$editor.focus();
                _this.$editor.selectionStart = _this.$editor.selectionEnd = _this.config.unitDisplayPosition == "left" ? 0 : Number.MAX_SAFE_INTEGER;
                _this.query();
            }
        });
        this.$dropDown = (0, teamapps_client_core_1.parseHtml)('<div class="tr-dropdown"></div>');
        this.$dropDown.addEventListener("scroll", function (e) {
            e.stopPropagation();
            e.preventDefault();
        });
        this.setEditingMode(this.config.editingMode);
        this.$editor.classList.add("tr-unitbox-editor", "tr-editor");
        this.$editor.addEventListener("focus", function (e) {
            if (_this.editingMode !== "editable") {
                _this.$editor.blur(); // must not get focus!
                return false;
            }
            if (_this.blurCausedByClickInsideComponent) {
                // do nothing!
            }
            else {
                _this.onFocus.fire();
                _this.$unitBox.classList.add('focus');
                _this.cleanupEditorValue();
            }
        });
        this.$editor.addEventListener("blur", function () {
            if (_this.blurCausedByClickInsideComponent) {
                _this.$editor.focus();
            }
            else {
                _this.onBlur.fire();
                _this.$unitBox.classList.remove('focus');
                _this.formatEditorValue();
                _this.closeDropDown();
            }
        });
        this.$editor.addEventListener("keydown", function (e) {
            var usedByDropdownComponent = _this.dropDownComponent.handleKeyboardInput(e);
            if ((0, TrivialCore_1.isModifierKey)(e)) {
                return;
            }
            else if (e.key === "Tab" || e.key === "Enter") {
                e.key === "Enter" && e.preventDefault(); // do not submit form
                var highlightedEntry = _this.dropDownComponent.getValue();
                if (_this.dropDownOpen && highlightedEntry) {
                    _this.setSelectedEntry(highlightedEntry, false, e);
                }
                _this.closeDropDown();
                _this.fireChangeEvents();
                return;
            }
            else if (e.key === "ArrowUp" || e.key === "ArrowDown") {
                var direction = e.key === "ArrowUp" ? -1 : 1;
                if (!_this.dropDownOpen) {
                    _this.openDropDown();
                    _this.query(direction);
                }
                e.preventDefault(); // some browsers move the caret to the beginning on up key
            }
            else if (e.key === "Escape") {
                _this.closeDropDown();
                _this.cleanupEditorValue();
            }
        });
        this.$editor.addEventListener("keyup", function (e) {
            if ((0, TrivialCore_1.isSpecialKey)(e)) {
                return; // ignore
            }
            var hasDoubleDecimalSeparator = new RegExp("(?:\\" + _this.getDecimalSeparator() + ".*)" + "\\" + _this.getDecimalSeparator(), "g").test(_this.$editor.value);
            if (hasDoubleDecimalSeparator) {
                _this.cleanupEditorValue();
            }
            if (_this.config.queryOnNonNumberCharacters) {
                if (_this.getQueryString().length > 0) {
                    _this.openDropDown();
                    _this.query(1);
                }
                else {
                    _this.closeDropDown();
                }
            }
        });
        this.$editor.addEventListener("mousedown", function () {
            _this.clickInsideEditorWasWhileNotHavingFocus = (document.activeElement != _this.$editor);
            if (_this.editingMode === "editable") {
                if (_this.config.openDropdownOnEditorClick) {
                    _this.openDropDown();
                    _this.query();
                }
            }
        });
        this.$editor.addEventListener("click", function (ev) {
            if (_this.clickInsideEditorWasWhileNotHavingFocus) {
                _this.$editor.select();
            }
        });
        this.$editor.addEventListener("change", function () {
            _this.fireChangeEvents();
        });
        [this.$unitBox, this.$dropDown].forEach(function (el) {
            el.addEventListener("mousedown", function () {
                if (document.activeElement == _this.$editor) {
                    _this.blurCausedByClickInsideComponent = true;
                }
            });
            el.addEventListener("mouseup", function () {
                if (_this.blurCausedByClickInsideComponent) {
                    _this.$editor.focus();
                    _this.blurCausedByClickInsideComponent = false;
                }
            });
            el.addEventListener("mouseout", function () {
                if (_this.blurCausedByClickInsideComponent) {
                    _this.$editor.focus();
                    _this.blurCausedByClickInsideComponent = false;
                }
            });
        });
        var listBox = new TrivialTreeBox_1.TrivialTreeBox(__assign({ selectOnHover: true }, this.config));
        this.dropDownComponent = new TreeBoxDropdown_1.TreeBoxDropdown({
            queryFunction: this.config.queryFunction,
            preselectionMatcher: function (query) { return true; },
            textHighlightingEntryLimit: 100
        }, listBox);
        this.$dropDown.append(this.dropDownComponent.getMainDomElement());
        this.dropDownComponent.onValueChanged.addListener(function (_a) {
            var value = _a.value, finalSelection = _a.finalSelection;
            if (value && finalSelection) {
                _this.setSelectedEntry(value, true);
                _this.closeDropDown();
            }
        });
        this.setUnitDisplayPosition(this.config.unitDisplayPosition);
        if (this.config.amount != null) {
            this.$editor.value = this.config.amount.toString();
        }
        this.formatEditorValue();
        this.setSelectedEntry(this.config.selectedEntry || null, false, null);
    }
    TrivialUnitBox.prototype.getNumberFormat = function () {
        return this.config.numberFormatFunction(this.selectedEntry);
    };
    TrivialUnitBox.prototype.getDecimalSeparator = function () {
        return teamapps_client_core_components_1.NumberParser.getDecimalSeparatorForLocale(this.getNumberFormat().resolvedOptions().locale);
    };
    TrivialUnitBox.prototype.getThousandsSeparator = function () {
        return teamapps_client_core_components_1.NumberParser.getGroupSeparatorForLocale(this.getNumberFormat().resolvedOptions().locale);
    };
    TrivialUnitBox.prototype.getNumerals = function () {
        return teamapps_client_core_components_1.NumberParser.getNumeralsForLocale(this.getNumberFormat().resolvedOptions().locale);
    };
    TrivialUnitBox.prototype.getMinFractionDigits = function () {
        return this.getNumberFormat().resolvedOptions().minimumFractionDigits;
    };
    TrivialUnitBox.prototype.getMaxFractionDigits = function () {
        return this.getNumberFormat().resolvedOptions().maximumFractionDigits;
    };
    TrivialUnitBox.prototype.getQueryString = function () {
        return (this.$editor.value || "").toString().replace(new RegExp("[".concat(this.getDecimalSeparator()).concat(this.getThousandsSeparator()).concat(this.getNumerals().join(""), "\\s]"), "g"), '').trim();
    };
    TrivialUnitBox.prototype.getEditorValueLocalNumberPart = function (fillupDecimals) {
        if (fillupDecimals === void 0) { fillupDecimals = false; }
        var rawNumber = (this.$editor.value || "").replace(new RegExp("[^".concat(this.getDecimalSeparator()).concat(this.getNumerals().join(""), "]"), "g"), '').trim();
        var decimalSeparatorIndex = rawNumber.indexOf(this.getDecimalSeparator());
        var integerPart;
        var fractionalPart;
        if (decimalSeparatorIndex !== -1) {
            integerPart = rawNumber.substring(0, decimalSeparatorIndex);
            fractionalPart = rawNumber.substring(decimalSeparatorIndex + 1, rawNumber.length).replace(/\D/g, '');
        }
        else {
            integerPart = rawNumber;
            fractionalPart = "";
        }
        if (integerPart.length == 0 && fractionalPart.length == 0) {
            return "";
        }
        else {
            if (fillupDecimals && fractionalPart.length < this.getMinFractionDigits()) {
                fractionalPart = fractionalPart + this.getNumerals()[0].repeat(this.getMinFractionDigits() - fractionalPart.length);
            }
            if (fractionalPart.length > this.getMaxFractionDigits()) {
                fractionalPart = fractionalPart.substr(0, this.getMaxFractionDigits());
            }
            return integerPart + (fractionalPart.length > 0 ? this.getDecimalSeparator() + fractionalPart : "");
        }
    };
    TrivialUnitBox.prototype.localNumberStringToBigDecimal = function (localizedNumber) {
        if (localizedNumber == null || localizedNumber.length == 0) {
            return null;
        }
        var numerals = this.getNumerals();
        return teamapps_client_core_components_1.BigDecimal.of(localizedNumber
            .replace(this.getThousandsSeparator(), '')
            .replace(this.getDecimalSeparator(), '.')
            .split('').map(function (c) { return c == "." ? "." : "" + numerals.indexOf(c); })
            .join(""));
    };
    TrivialUnitBox.prototype.query = function (highlightDirection) {
        return __awaiter(this, void 0, void 0, function () {
            var gotResultsForQuery;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.dropDownComponent.handleQuery(this.getQueryString(), highlightDirection, this.getSelectedEntry())];
                    case 1:
                        gotResultsForQuery = _a.sent();
                        if (gotResultsForQuery && document.activeElement == this.$editor) {
                            this.openDropDown();
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    TrivialUnitBox.prototype.fireSelectedEntryChangedEvent = function () {
        this.onSelectedEntryChanged.fire(this.selectedEntry);
    };
    TrivialUnitBox.prototype.fireChangeEvents = function () {
        this.onChange.fireIfChanged({
            unitEntry: (0, TrivialCore_1.unProxyEntry)(this.selectedEntry),
            amount: this.getAmount()
        });
    };
    TrivialUnitBox.prototype.setSelectedEntry = function (entry, fireEvent, originalEvent) {
        this.selectedEntry = entry;
        this.onChange.resetChangeValue();
        var $selectedEntry = (0, teamapps_client_core_1.parseHtml)(this.config.selectedEntryRenderingFunction(entry));
        $selectedEntry.classList.add("tr-combobox-entry");
        this.$selectedEntryWrapper.innerHTML = "";
        this.$selectedEntryWrapper.append($selectedEntry);
        this.cleanupEditorValue();
        if (document.activeElement !== this.$editor) {
            this.formatEditorValue();
        }
        if (fireEvent) {
            this.fireSelectedEntryChangedEvent();
            this.fireChangeEvents();
        }
    };
    TrivialUnitBox.prototype.formatEditorValue = function () {
        var amount = this.orFallback(this.localNumberStringToBigDecimal(this.getEditorValueLocalNumberPart()));
        this.$editor.value = amount != null ? amount.format(this.getNumberFormat(), this.$editor == document.activeElement) : "";
    };
    TrivialUnitBox.prototype.orFallback = function (amount) {
        if (amount == null && !this.config.allowNullAmount) {
            return teamapps_client_core_components_1.BigDecimal.of("0");
        }
        return amount;
    };
    TrivialUnitBox.prototype.cleanupEditorValue = function () {
        if (this.$editor.value) {
            this.$editor.value = this.getEditorValueLocalNumberPart(true);
        }
    };
    TrivialUnitBox.prototype.openDropDown = function () {
        var _this = this;
        var _a;
        if (this.getMainDomElement().parentElement !== this.parentElement || this.dropdownAutoUpdateDisposable == null) {
            (_a = this.dropdownAutoUpdateDisposable) === null || _a === void 0 ? void 0 : _a.call(this);
            this.dropdownAutoUpdateDisposable = this.dropdownAutoUpdateDisposable = (0, ComboBoxPopper_1.positionDropdownWithAutoUpdate)(this.$unitBox, this.$dropDown, {
                referenceOutOfViewPortHandler: function () { return _this.closeDropDown(); }
            });
            this.parentElement = this.getMainDomElement().parentElement;
        }
        if (!this.dropDownOpen) {
            this.$unitBox.classList.add("open");
            this.popper.update();
            this.dropDownOpen = true;
        }
    };
    TrivialUnitBox.prototype.closeDropDown = function () {
        this.$unitBox.classList.remove("open");
        this.dropDownOpen = false;
    };
    TrivialUnitBox.prototype.getAmount = function () {
        var editorValueNumberPart = this.getEditorValueLocalNumberPart();
        if (editorValueNumberPart.length === 0 && this.config.allowNullAmount) {
            return null;
        }
        else if (editorValueNumberPart.length === 0) {
            return teamapps_client_core_components_1.BigDecimal.of("0");
        }
        else {
            return this.localNumberStringToBigDecimal(this.getEditorValueLocalNumberPart());
        }
    };
    TrivialUnitBox.prototype.isDropDownNeeded = function () {
        return this.editingMode == 'editable' && (this.config.entries && this.config.entries.length > 0 || !this.usingDefaultQueryFunction || this.config.showTrigger);
    };
    TrivialUnitBox.prototype.setEditingMode = function (newEditingMode) {
        this.editingMode = newEditingMode;
        this.$unitBox.classList.remove("editable", "readonly", "disabled");
        this.$unitBox.classList.add(this.editingMode);
        this.$editor.readOnly = newEditingMode !== "editable";
        if (this.isDropDownNeeded()) {
            this.$unitBox.append(this.$dropDown);
        }
    };
    TrivialUnitBox.prototype.getSelectedEntry = function () {
        if (this.selectedEntry == null) {
            return null;
        }
        else {
            return (0, TrivialCore_1.unProxyEntry)(this.selectedEntry);
        }
    };
    TrivialUnitBox.prototype.setAmount = function (amount) {
        amount = this.orFallback(amount);
        this.$editor.value = amount != null ? amount.format(this.getNumberFormat()) : "";
        this.onChange.resetChangeValue();
    };
    TrivialUnitBox.prototype.focus = function () {
        this.$editor.select();
    };
    TrivialUnitBox.prototype.getEditor = function () {
        return this.$editor;
    };
    TrivialUnitBox.prototype.setUnitDisplayPosition = function (unitDisplayPosition) {
        this.config.unitDisplayPosition = unitDisplayPosition;
        this.$unitBox.classList.toggle('unit-display-left', unitDisplayPosition === 'left');
        this.$unitBox.classList.toggle('unit-display-right', unitDisplayPosition === 'right');
    };
    TrivialUnitBox.prototype.setQueryOnNonNumberCharacters = function (queryOnNonNumberCharacters) {
        this.config.queryOnNonNumberCharacters = queryOnNonNumberCharacters;
    };
    TrivialUnitBox.prototype.isDropDownOpen = function () {
        return this.dropDownOpen;
    };
    TrivialUnitBox.prototype.destroy = function () {
        this.$unitBox.remove();
        this.$dropDown.remove();
    };
    TrivialUnitBox.prototype.getMainDomElement = function () {
        return this.$unitBox;
    };
    return TrivialUnitBox;
}());
exports.TrivialUnitBox = TrivialUnitBox;
