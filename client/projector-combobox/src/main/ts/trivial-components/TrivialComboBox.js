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
exports.TrivialComboBox = void 0;
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
var teamapps_client_core_1 = require("teamapps-client-core");
var teamapps_client_core_2 = require("teamapps-client-core");
var ComboBoxPopper_1 = require("./ComboBoxPopper");
var TrivialComboBox = /** @class */ (function () {
    function TrivialComboBox(options, dropDownComponent) {
        var _this = this;
        this.onSelectedEntryChanged = new teamapps_client_core_2.TeamAppsEvent();
        this.onFocus = new teamapps_client_core_2.TeamAppsEvent();
        this.onBlur = new teamapps_client_core_2.TeamAppsEvent();
        this.onBeforeQuery = new teamapps_client_core_2.TeamAppsEvent();
        this.onBeforeDropdownOpens = new teamapps_client_core_2.TeamAppsEvent();
        this.selectedEntry = null;
        this.blurCausedByClickInsideComponent = false;
        this.autoCompleteTimeoutId = -1;
        this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
        this.dropDownOpen = false;
        this.isEditorVisible = false;
        this.config = __assign({ autoComplete: true, autoCompleteDelay: 0, textToEntryFunction: function (freeText) { return null; }, showClearButton: false, showTrigger: true, editingMode: "editable", showDropDownOnResultsOnly: false, preselectFirstQueryResult: true, placeholderText: "" }, options);
        this.$comboBox = (0, teamapps_client_core_1.parseHtml)("<div class=\"tr-combobox tr-input-wrapper editor-hidden\">\n\t\t\t<div class=\"tr-combobox-main-area\">\n\t\t\t\t<input type=\"text\" class=\"tr-combobox-editor tr-editor\" autocomplete=\"no\"></input>\n\t\t\t\t<div class=\"tr-combobox-selected-entry-wrapper\"></div>\t\t\t\n\t\t\t</div>\n            <div class=\"tr-remove-button ".concat(this.config.showClearButton ? '' : 'hidden', "\"></div>\n            <div class=\"tr-trigger ").concat(this.config.showTrigger ? '' : 'hidden', "\"><span class=\"tr-trigger-icon\"></span></div>\n        </div>"));
        this.$selectedEntryWrapper = this.$comboBox.querySelector(':scope .tr-combobox-selected-entry-wrapper');
        this.$clearButton = this.$comboBox.querySelector(':scope .tr-remove-button');
        this.$clearButton.addEventListener("mousedown", function (e) {
            _this.$editor.value = "";
            _this.setValue(null, true, e);
        });
        this.$trigger = this.$comboBox.querySelector(':scope .tr-trigger');
        this.$dropDown = (0, teamapps_client_core_1.parseHtml)('<div class="tr-dropdown"></div>');
        this.setDropdownMinWidth(this.config.dropDownMinWidth);
        this.setDropdownMaxHeight(this.config.dropDownMaxHeight);
        this.$dropDown.addEventListener("scroll", function (e) {
            e.stopPropagation();
            e.preventDefault();
        });
        this.setEditingMode(this.config.editingMode);
        this.$editor = this.$comboBox.querySelector(':scope .tr-editor');
        this.$editor.addEventListener("focus", function () {
            _this.setFocused(true);
            if (!_this.blurCausedByClickInsideComponent) {
                _this.showEditor();
            }
        });
        this.$editor.addEventListener("blur", function (e) {
            if (_this.blurCausedByClickInsideComponent) {
                _this.$editor.focus();
                // Don't just unset the blurCausedByClickInsideComponent here! See the comment with hashtag #regainFocus.
            }
            else {
                _this.setFocused(false);
                if (_this.isEditorVisible) {
                    var freeTextEntry = _this.getFreeTextEntry();
                    if (freeTextEntry != null) {
                        _this.setValue(freeTextEntry, true, e);
                    }
                }
                _this.hideEditor();
                _this.closeDropDown();
            }
        });
        this.$editor.addEventListener("keydown", function (e) {
            if ((0, TrivialCore_1.isModifierKey)(e)) {
                return;
            }
            else if (e.key === "Tab" || e.key === "Enter") {
                if (_this.isEditorVisible) {
                    e.key == "Enter" && e.preventDefault(); // do not submit form
                    var highlightedEntry = _this.dropDownComponent.getValue();
                    if (_this.dropDownOpen && highlightedEntry) {
                        _this.setValue(highlightedEntry, true, e);
                    }
                    else {
                        var freeTextEntry = _this.getFreeTextEntry();
                        if (freeTextEntry != null) {
                            _this.setValue(freeTextEntry, true, e);
                        }
                    }
                    _this.closeDropDown();
                    _this.hideEditor();
                }
                return;
            }
            else if (e.key === "ArrowLeft" || e.key === "ArrowRight") {
                if (_this.dropDownOpen && _this.dropDownComponent.handleKeyboardInput(e)) {
                    _this.setAndSelectEditorValue(_this.dropDownComponent.getValue());
                    e.preventDefault();
                    return;
                }
                else {
                    _this.showEditor();
                    return; // let the user navigate freely left and right...
                }
            }
            else if (e.key === "Backspace" || e.key === "Delete") {
                _this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
                setTimeout(function () { return _this.query(_this.getEditorValueLeftOfSelection(), 0); }); // asynchronously to make sure the editor has been updated
            }
            else if (e.key === "ArrowUp" || e.key === "ArrowDown") {
                if (!_this.isEditorVisible) {
                    _this.$editor.select();
                    _this.showEditor();
                }
                var direction = e.key === "ArrowUp" ? -1 : 1;
                if (!_this.dropDownOpen) {
                    _this.query(_this.getEditorValueLeftOfSelection(), direction);
                    _this.openDropDown(); // directly open the dropdown (the user definitely wants to see it)
                }
                else {
                    if (_this.dropDownComponent.handleKeyboardInput(e)) {
                        _this.setAndSelectEditorValue(_this.dropDownComponent.getValue());
                    }
                }
                e.preventDefault(); // some browsers move the caret to the beginning on up key
            }
            else if (e.key === "Escape") {
                if (!(!_this.isEntrySelected() && _this.$editor.value.length > 0 && _this.dropDownOpen)) {
                    _this.hideEditor();
                    _this.$editor.value = "";
                }
                _this.closeDropDown();
            }
            else {
                if (!_this.isEditorVisible) {
                    _this.showEditor();
                    _this.$editor.select();
                }
                if (!_this.config.showDropDownOnResultsOnly) {
                    _this.openDropDown();
                }
                // We need the new editor value (after the keydown event). Therefore setTimeout().
                setTimeout(function () { return _this.query(_this.getEditorValueLeftOfSelection(), _this.config.preselectFirstQueryResult && _this.$editor.value ? 1 : 0); });
            }
        });
        [this.$comboBox, this.$dropDown].forEach(function (element) {
            element.addEventListener("mousedown", function () {
                _this.blurCausedByClickInsideComponent = true;
                // #regainFocus
                // Why don't we just do a "setTimeout(() => this.blurCausedByClickInsideComponent = false);" and check for
                // the flag in the blur event handler instead of this overly complex handling of "mouseup" and "mousedown" events?
                // That's because in Firefox, doing $editor.focus() has no effect as long as the mouse is pressed. The
                // document.activeElement will be document.body until the mouse is released.
                // So when the user presses somewhere inside the tag combobox (except the $editor), the focus will be lost (blur event)
                // and re-focusing will have no effect. We HAVE TO wait for the mouseup or mouseout event in order to re-focus.
            }, true);
            element.addEventListener("mouseup", function () {
                if (_this.blurCausedByClickInsideComponent) {
                    _this.$editor.focus();
                    _this.blurCausedByClickInsideComponent = false;
                }
            });
            element.addEventListener("mouseout", function () {
                if (_this.blurCausedByClickInsideComponent) {
                    _this.$editor.focus();
                    _this.blurCausedByClickInsideComponent = false;
                }
            });
        });
        this.setDropDownComponent(dropDownComponent);
        this.$editor.addEventListener("mousedown", function () {
            if (_this.editingMode === "editable") {
                if (!_this.config.showDropDownOnResultsOnly) {
                    _this.openDropDown();
                }
                _this.query(_this.getEditorValueLeftOfSelection());
            }
        });
        this.$trigger.addEventListener("mousedown", function () {
            if (_this.dropDownOpen) {
                _this.closeDropDown();
                _this.showEditor();
            }
            else if (_this.editingMode === "editable") {
                _this.showEditor();
                _this.$editor.select();
                _this.dropDownComponent.setValue(_this.getValue());
                _this.query("", 0);
                _this.openDropDown();
            }
        });
        this.$selectedEntryWrapper.addEventListener("click", function () {
            if (_this.editingMode === "editable") {
                _this.showEditor();
                _this.$editor.select();
                if (!_this.config.showDropDownOnResultsOnly) {
                    _this.openDropDown();
                }
                _this.dropDownComponent.setValue(_this.getValue());
                _this.query("", 0);
            }
        });
    }
    TrivialComboBox.prototype.setDropdownMinWidth = function (dropDownMinWidth) {
        this.$dropDown.style.minWidth = dropDownMinWidth != null ? (dropDownMinWidth + "px") : null;
    };
    TrivialComboBox.prototype.setDropdownMaxHeight = function (dropDownMaxHeight) {
        this.$dropDown.style.maxHeight = dropDownMaxHeight != null ? (dropDownMaxHeight + "px") : null;
    };
    TrivialComboBox.prototype.setFocused = function (focused) {
        if (focused != this.focused) {
            if (focused) {
                this.onFocus.fire();
            }
            else {
                this.onBlur.fire();
            }
            this.$comboBox.classList.toggle('focus', focused);
            this.focused = focused;
        }
    };
    TrivialComboBox.prototype.getFreeTextEntry = function () {
        var editorValue = this.getEditorValueLeftOfSelection();
        return editorValue ? this.config.textToEntryFunction(editorValue) : null;
    };
    TrivialComboBox.prototype.setAndSelectEditorValue = function (value) {
        if (value != null) {
            this.$editor.value = this.config.entryToEditorTextFunction(value);
            this.$editor.select();
        }
        else {
            this.$editor.value = "";
        }
    };
    TrivialComboBox.prototype.handleDropDownValueChange = function (eventData) {
        if (eventData.finalSelection) {
            this.setValue(eventData.value, true);
            this.closeDropDown();
            this.hideEditor();
        }
    };
    TrivialComboBox.prototype.query = function (nonSelectedEditorValue, highlightDirection) {
        if (highlightDirection === void 0) { highlightDirection = 0; }
        return __awaiter(this, void 0, void 0, function () {
            var gotResultsForQuery;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        this.onBeforeQuery.fire(nonSelectedEditorValue);
                        return [4 /*yield*/, this.dropDownComponent.handleQuery(nonSelectedEditorValue, highlightDirection, this.getValue())];
                    case 1:
                        gotResultsForQuery = _a.sent();
                        if (highlightDirection !== 0) {
                            this.autoCompleteIfPossible(this.config.autoCompleteDelay);
                        }
                        if (this.config.showDropDownOnResultsOnly && gotResultsForQuery && document.activeElement == this.$editor) {
                            this.openDropDown();
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    TrivialComboBox.prototype.fireChangeEvents = function (entry, originalEvent) {
        this.onSelectedEntryChanged.fire((0, TrivialCore_1.unProxyEntry)(entry));
    };
    TrivialComboBox.prototype.setValue = function (entry, fireEventIfChanged, originalEvent) {
        var _a;
        var changing = !(0, TrivialCore_1.objectEquals)(entry, this.selectedEntry);
        this.selectedEntry = entry;
        this.$selectedEntryWrapper.innerHTML = '';
        var $selectedEntry = (0, teamapps_client_core_1.parseHtml)(this.config.selectedEntryRenderingFunction(entry));
        if ($selectedEntry != null) {
            $selectedEntry.classList.add("tr-combobox-entry");
            this.$selectedEntryWrapper.append($selectedEntry);
        }
        else {
            this.$selectedEntryWrapper.append((0, teamapps_client_core_1.parseHtml)("<div class=\"placeholder-text\">".concat((_a = this.config.placeholderText) !== null && _a !== void 0 ? _a : "", "</div>")));
        }
        if (entry != null) {
            this.$editor.value = this.config.entryToEditorTextFunction(entry);
        }
        else {
            this.$editor.value = '';
        }
        if (changing && fireEventIfChanged) {
            this.fireChangeEvents(entry, originalEvent);
        }
        if (this.$clearButton) {
            this.$clearButton.classList.toggle("hidden", !this.config.showClearButton || entry == null);
        }
        if (this.isEditorVisible) {
            this.showEditor(); // reposition editor
        }
    };
    TrivialComboBox.prototype.isEntrySelected = function () {
        return this.selectedEntry != null;
    };
    TrivialComboBox.prototype.showEditor = function () {
        this.$comboBox.classList.remove("editor-hidden");
        this.isEditorVisible = true;
    };
    TrivialComboBox.prototype.hideEditor = function () {
        this.$comboBox.classList.add("editor-hidden");
        this.isEditorVisible = false;
    };
    TrivialComboBox.prototype.openDropDown = function () {
        var _this = this;
        var _a;
        if (this.isDropDownNeeded()) {
            if (this.getMainDomElement().parentElement !== this.parentElement || this.dropdownAutoUpdateDisposable == null) {
                (_a = this.dropdownAutoUpdateDisposable) === null || _a === void 0 ? void 0 : _a.call(this);
                this.dropdownAutoUpdateDisposable = this.dropdownAutoUpdateDisposable = (0, ComboBoxPopper_1.positionDropdownWithAutoUpdate)(this.$comboBox, this.$dropDown, {
                    referenceOutOfViewPortHandler: function () { return _this.closeDropDown(); }
                });
                this.parentElement = this.getMainDomElement().parentElement;
            }
            if (!this.dropDownOpen) {
                this.onBeforeDropdownOpens.fire(this.getEditorValueLeftOfSelection());
                this.$comboBox.classList.add("open");
                this.dropDownOpen = true;
            }
        }
    };
    TrivialComboBox.prototype.closeDropDown = function () {
        this.$comboBox.classList.remove("open");
        this.dropDownOpen = false;
    };
    TrivialComboBox.prototype.getEditorValueLeftOfSelection = function () {
        return this.$editor.value.substring(0, Math.min(this.$editor.selectionStart, this.$editor.selectionEnd));
    };
    TrivialComboBox.prototype.autoCompleteIfPossible = function (delay) {
        var _this = this;
        if (this.config.autoComplete) {
            clearTimeout(this.autoCompleteTimeoutId);
            var dropDownValue_1 = this.dropDownComponent.getValue();
            if (dropDownValue_1 && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
                this.autoCompleteTimeoutId = (0, TrivialCore_1.setTimeoutOrDoImmediately)(function () {
                    var currentEditorValue = _this.getEditorValueLeftOfSelection();
                    var entryAsString = _this.config.entryToEditorTextFunction(dropDownValue_1);
                    if (entryAsString.toLowerCase().indexOf(("" + currentEditorValue).toLowerCase()) === 0) {
                        _this.$editor.value = currentEditorValue + entryAsString.substr(currentEditorValue.length);
                        if (document.activeElement == _this.$editor) {
                            _this.$editor.setSelectionRange(currentEditorValue.length, entryAsString.length);
                        }
                    }
                }, delay);
            }
            this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
        }
    };
    TrivialComboBox.prototype.isDropDownNeeded = function () {
        return this.editingMode == 'editable';
    };
    TrivialComboBox.prototype.setEditingMode = function (newEditingMode) {
        this.editingMode = newEditingMode;
        this.$comboBox.classList.remove("editable", "readonly", "disabled");
        this.$comboBox.classList.add(this.editingMode);
        if (this.isDropDownNeeded()) {
            this.$comboBox.append(this.$dropDown);
        }
    };
    TrivialComboBox.prototype.getValue = function () {
        return (0, TrivialCore_1.unProxyEntry)(this.selectedEntry);
    };
    TrivialComboBox.prototype.getDropDownComponent = function () {
        return this.dropDownComponent;
    };
    TrivialComboBox.prototype.setDropDownComponent = function (dropDownComponent) {
        if (this.dropDownComponent != null) {
            this.dropDownComponent.onValueChanged.removeListener(this.handleDropDownValueChange);
            this.$dropDown.innerHTML = '';
        }
        this.dropDownComponent = dropDownComponent;
        this.$dropDown.append(dropDownComponent.getMainDomElement());
        dropDownComponent.onValueChanged.addListener(this.handleDropDownValueChange.bind(this));
        dropDownComponent.setValue(this.getValue());
    };
    TrivialComboBox.prototype.focus = function () {
        this.showEditor();
        this.$editor.select();
    };
    ;
    TrivialComboBox.prototype.getEditor = function () {
        return this.$editor;
    };
    TrivialComboBox.prototype.setShowClearButton = function (showClearButton) {
        this.config.showClearButton = showClearButton;
        this.$clearButton.classList.toggle('hidden', !this.config.showClearButton || this.selectedEntry == null);
    };
    TrivialComboBox.prototype.setShowTrigger = function (showTrigger) {
        this.config.showTrigger = showTrigger;
        this.$trigger.classList.toggle('hidden', !showTrigger);
    };
    TrivialComboBox.prototype.setShowDropDownOnResultsOnly = function (showDropDownAfterResultsArrive) {
        this.config.showDropDownOnResultsOnly = showDropDownAfterResultsArrive;
    };
    TrivialComboBox.prototype.setPreselectFirstQueryResult = function (preselectFirstQueryResult) {
        this.config.preselectFirstQueryResult = preselectFirstQueryResult;
    };
    TrivialComboBox.prototype.setAutoComplete = function (autoComplete) {
        this.config.autoComplete = autoComplete;
    };
    TrivialComboBox.prototype.setTextToEntryFunction = function (textToEntryFunction) {
        this.config.textToEntryFunction = textToEntryFunction;
    };
    TrivialComboBox.prototype.isDropDownOpen = function () {
        return this.dropDownOpen;
    };
    TrivialComboBox.prototype.destroy = function () {
        this.$comboBox.remove();
        this.$dropDown.remove();
    };
    ;
    TrivialComboBox.prototype.getMainDomElement = function () {
        return this.$comboBox;
    };
    TrivialComboBox.prototype.setPlaceholderText = function (placeholderText) {
        this.config.placeholderText = placeholderText;
        var $placeholderText = this.$selectedEntryWrapper.querySelector(":scope .placeholder-text");
        if ($placeholderText != null) {
            $placeholderText.innerText = placeholderText !== null && placeholderText !== void 0 ? placeholderText : "";
        }
    };
    return TrivialComboBox;
}());
exports.TrivialComboBox = TrivialComboBox;
