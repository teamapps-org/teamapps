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
exports.CurrencyField = void 0;
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
var TrivialCore_1 = require("./trivial-components/TrivialCore");
var TrivialUnitBox_1 = require("./trivial-components/TrivialUnitBox");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var generated_1 = require("./generated");
var CurrencyField = /** @class */ (function (_super) {
    __extends(CurrencyField, _super);
    function CurrencyField() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.onTextInput = new teamapps_client_core_1.TeamAppsEvent({ throttlingMode: "debounce", delay: 250 });
        _this.onSpecialKeyPressed = new teamapps_client_core_1.TeamAppsEvent({ throttlingMode: "debounce", delay: 250 });
        return _this;
    }
    CurrencyField.prototype.initialize = function (config) {
        var _this = this;
        var initialPrecision = config.fixedPrecision >= 0 ? config.fixedPrecision : 2;
        this.trivialUnitBox = new TrivialUnitBox_1.TrivialUnitBox({
            numberFormatFunction: function (entry) { return _this.getNumberFormat(entry); },
            idFunction: function (entry) { return entry.code; },
            unitDisplayPosition: config.showCurrencyBeforeAmount ? 'left' : 'right',
            entryRenderingFunction: function (entry) {
                entry = entry || {};
                return "<div class=\"tr-template-currency-single-line-long\">\n\t\t\t\t  <div class=\"content-wrapper tr-editor-area\"> \n\t\t\t\t\t<div class=\"symbol-and-code\">".concat(entry.code != null ? "<span class=\"currency-code\">".concat(entry.code || '', "</span>") : '', " ").concat(entry.symbol != null ? "<span class=\"currency-symbol\">".concat(entry.symbol || '', "</span>") : '', "</div>\n\t\t\t\t\t<div class=\"currency-name\">").concat(entry.name || '', "</div>\n\t\t\t\t  </div>\n\t\t\t\t</div>");
            },
            selectedEntryRenderingFunction: function (entry) {
                if (entry == null) {
                    return "<div class=\"tr-template-currency-single-line-short\">-</div>";
                }
                else if (_this.config.showCurrencySymbol && entry.symbol) {
                    return "<div class=\"tr-template-currency-single-line-short\">".concat(entry.symbol, " (").concat(entry.code, ")</div>");
                }
                else {
                    return "<div class=\"tr-template-currency-single-line-short\">".concat(entry.code, "</div>");
                }
            },
            queryOnNonNumberCharacters: config.alphabeticKeysQueryEnabled,
            editingMode: this.convertToTrivialComponentsEditingMode(config.editingMode),
            queryFunction: function (queryString) { return _this.queryFunction(queryString); }
        });
        this.trivialUnitBox.getMainDomElement().classList.add("DtoCurrencyField");
        this.trivialUnitBox.onChange.addListener(function (value) {
            _this.commit();
        });
        this.trivialUnitBox.getEditor().addEventListener('keyup', function (e) {
            if (e.key !== "Enter"
                && e.key !== "Tab"
                && !(0, TrivialCore_1.isModifierKey)(e)) {
                _this.onTextInput.fire({
                    enteredString: _this.trivialUnitBox.getEditor().value
                });
            }
            else if (e.key === "Escape") {
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
        this.setCurrencyUnits(config.currencyUnits);
        this.trivialUnitBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
        this.trivialUnitBox.getMainDomElement().querySelector(":scope .tr-editor").classList.add("field-background");
        this.trivialUnitBox.getMainDomElement().querySelector(":scope .tr-unitbox-selected-entry-and-trigger-wrapper").classList.add("field-border");
        this.trivialUnitBox.onFocus.addListener(function () { return _this.getMainElement().classList.add("focus"); });
        this.trivialUnitBox.onBlur.addListener(function () { return _this.getMainElement().classList.remove("focus"); });
    };
    CurrencyField.prototype.getNumberFormat = function (entry) {
        if (entry == null) {
            var fractionDigits = this.config.fixedPrecision >= 0 ? this.config.fixedPrecision : 2;
            return new Intl.NumberFormat(this.config.locale, {
                useGrouping: true,
                minimumFractionDigits: fractionDigits,
                maximumFractionDigits: fractionDigits
            });
        }
        else {
            var fractionDigits = this.config.fixedPrecision >= 0 ? this.config.fixedPrecision : entry.fractionDigits >= 0 ? entry.fractionDigits : 4;
            return new Intl.NumberFormat(this.config.locale, {
                minimumFractionDigits: fractionDigits,
                maximumFractionDigits: fractionDigits,
                useGrouping: true
            });
        }
    };
    CurrencyField.prototype.isValidData = function (v) {
        return v == null || typeof v === "object";
    };
    CurrencyField.prototype.convertToTrivialComponentsEditingMode = function (editingMode) {
        return editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.READONLY ? 'readonly' : editingMode === teamapps_client_core_components_1.DtoFieldEditingMode.DISABLED ? 'disabled' : 'editable';
    };
    CurrencyField.prototype.getMainInnerDomElement = function () {
        return this.trivialUnitBox.getMainDomElement();
    };
    CurrencyField.prototype.displayCommittedValue = function () {
        var DtoValue = this.getCommittedValue();
        if (DtoValue) {
            this.trivialUnitBox.setSelectedEntry(DtoValue === null || DtoValue === void 0 ? void 0 : DtoValue.currencyUnit);
            this.trivialUnitBox.setAmount(teamapps_client_core_components_1.BigDecimal.of(DtoValue === null || DtoValue === void 0 ? void 0 : DtoValue.amount));
        }
        else {
            this.trivialUnitBox.setAmount(null);
            this.trivialUnitBox.setSelectedEntry(null);
        }
    };
    CurrencyField.prototype.focus = function () {
        this.trivialUnitBox.focus();
        (0, teamapps_client_core_components_1.selectElementContents)(this.trivialUnitBox.getMainDomElement().querySelector(":scope .tr-editor"));
    };
    CurrencyField.prototype.destroy = function () {
        _super.prototype.destroy.call(this);
        this.trivialUnitBox.destroy();
    };
    CurrencyField.prototype.getTransientValue = function () {
        var _a;
        var amount = this.trivialUnitBox.getAmount();
        return (0, generated_1.createDtoCurrencyValue)(this.trivialUnitBox.getSelectedEntry() && this.trivialUnitBox.getSelectedEntry(), (_a = this.trivialUnitBox.getAmount()) === null || _a === void 0 ? void 0 : _a.value);
    };
    CurrencyField.prototype.onEditingModeChanged = function (editingMode) {
        var _a;
        (_a = this.getMainElement().classList).remove.apply(_a, Object.values(teamapps_client_core_components_1.AbstractField.editingModeCssClasses));
        this.getMainElement().classList.add(teamapps_client_core_components_1.AbstractField.editingModeCssClasses[editingMode]);
        this.trivialUnitBox.setEditingMode(this.convertToTrivialComponentsEditingMode(editingMode));
    };
    CurrencyField.prototype.getReadOnlyHtml = function (value, availableWidth) {
        var content;
        if (value != null) {
            var currency = value.currencyUnit;
            var displayedCurrency = void 0;
            if (currency != null) {
                displayedCurrency = this.config.showCurrencySymbol ? "".concat(currency === null || currency === void 0 ? void 0 : currency.symbol, " (").concat(currency === null || currency === void 0 ? void 0 : currency.code, ")") : currency === null || currency === void 0 ? void 0 : currency.code;
            }
            else {
                displayedCurrency = null;
            }
            var amount = teamapps_client_core_components_1.BigDecimal.of(value.amount);
            var formattedAmount = amount === null || amount === void 0 ? void 0 : amount.format(this.getNumberFormat(currency));
            if (this.config.showCurrencyBeforeAmount) {
                content = [displayedCurrency, formattedAmount].filter(function (x) { return x != null; }).join(' ');
            }
            else {
                content = [formattedAmount, displayedCurrency].filter(function (x) { return x != null; }).join(' ');
            }
        }
        else {
            content = "";
        }
        return "<div class=\"static-readonly-DtoCurrencyField\">".concat(content, "</div>");
    };
    CurrencyField.prototype.getDefaultValue = function () {
        return (0, generated_1.createDtoCurrencyValue)(null, null);
    };
    CurrencyField.prototype.setCurrencyUnits = function (currencyUnits) {
        this.config.currencyUnits = currencyUnits;
        this.queryFunction = (0, TrivialCore_1.defaultListQueryFunctionFactory)(currencyUnits, ["code", "name", "symbol"], { matchingMode: "contains", ignoreCase: true });
    };
    CurrencyField.prototype.setShowCurrencyBeforeAmount = function (showCurrencyBeforeAmount) {
        this.trivialUnitBox.setUnitDisplayPosition(showCurrencyBeforeAmount ? 'left' : "right");
    };
    CurrencyField.prototype.setShowCurrencySymbol = function (showCurrencySymbol) {
        this.config.showCurrencySymbol = showCurrencySymbol;
        this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry());
    };
    CurrencyField.prototype.valuesChanged = function (v1, v2) {
        return !(0, teamapps_client_core_1.deepEquals)(v1, v2);
    };
    CurrencyField.prototype.setLocale = function (locale) {
        this.config.locale = locale;
        this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry()); // update format
    };
    CurrencyField.prototype.setFixedPrecision = function (fixedPrecision) {
        this.config.fixedPrecision = fixedPrecision;
        this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry()); // update format
    };
    CurrencyField.prototype.setAlphabeticKeysQueryEnabled = function (alphabeticKeysQueryEnabled) {
        this.config.alphabeticKeysQueryEnabled = alphabeticKeysQueryEnabled;
        this.trivialUnitBox.setQueryOnNonNumberCharacters(alphabeticKeysQueryEnabled);
    };
    return CurrencyField;
}(teamapps_client_core_components_1.AbstractField));
exports.CurrencyField = CurrencyField;
