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
exports.TrivialTagComboBox = void 0;
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
var ComboBoxPopper_1 = require("./ComboBoxPopper");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var TrivialTagComboBox = /** @class */ (function () {
    function TrivialTagComboBox(options, dropDownComponent) {
        var _this = this;
        this.onValueChanged = new teamapps_client_core_1.TeamAppsEvent();
        this.onFocus = new teamapps_client_core_1.TeamAppsEvent();
        this.onBlur = new teamapps_client_core_1.TeamAppsEvent();
        this.selectedEntries = [];
        this.blurCausedByClickInsideComponent = false;
        this.autoCompleteTimeoutId = -1;
        this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
        this._isDropDownOpen = false;
        this.config = __assign({ spinnerTemplate: TrivialCore_1.DEFAULT_TEMPLATES.defaultSpinnerTemplate, autoComplete: true, autoCompleteDelay: 0, freeTextEntryFactory: function (freeText) {
                return {
                    displayValue: freeText,
                    id: (0, TrivialCore_1.generateUUID)(),
                    _isFreeTextEntry: true
                };
            }, freeTextSeparators: [',', ';'], tagCompleteDecider: function (mergedEntry) {
                return true;
            }, entryMerger: function (partialEntry, newEntry) {
                return newEntry;
            }, removePartialTagOnBlur: true, selectionAcceptor: function (e) { return !e._isFreeTextEntry; }, showTrigger: true, editingMode: "editable", showDropDownOnResultsOnly: false, twoStepDeletion: false, placeholderText: "" }, options);
        this.dropDownComponent = dropDownComponent;
        this.$tagComboBox = (0, teamapps_client_core_1.parseHtml)("<div class=\"tr-tagcombobox tr-input-wrapper\">\n\t\t\t<div class=\"tr-tagcombobox-main-area\">\n\t\t\t\t<div class=\"placeholder-text\"></div>\n\t\t\t\t<div class=\"tr-tagcombobox-tagarea\"></div>\n\t\t\t</div>\n\t\t\t <div class=\"tr-remove-button ".concat(this.config.showClearButton ? '' : 'hidden', "\"></div>\n            <div class=\"tr-trigger ").concat(this.config.showTrigger ? '' : 'hidden', "\"><span class=\"tr-trigger-icon\"></span></div>\n        </div>"));
        this.$tagArea = this.$tagComboBox.querySelector(':scope .tr-tagcombobox-tagarea');
        this.$placeholderText = this.$tagComboBox.querySelector(':scope .placeholder-text');
        this.setPlaceholderText(this.config.placeholderText);
        this.$clearButton = this.$tagComboBox.querySelector(':scope .tr-remove-button');
        this.$clearButton.addEventListener("mousedown", function (e) {
            _this.$editor.value = "";
            _this.setSelectedEntries([], true);
        });
        this.$trigger = this.$tagComboBox.querySelector(':scope .tr-trigger');
        this.$trigger.addEventListener("mousedown", function () {
            _this.focus();
            if (_this._isDropDownOpen) {
                _this.closeDropDown();
            }
            else if (_this.editingMode === "editable") {
                (0, teamapps_client_core_components_1.selectElementContents)(_this.$editor);
                _this.openDropDown();
                _this.query();
            }
        });
        this.$dropDown = (0, teamapps_client_core_1.parseHtml)('<div class="tr-dropdown"></div>');
        this.$dropDown.style.minWidth = this.config.dropDownMinWidth != null ? (this.config.dropDownMinWidth + "px") : null;
        this.$dropDown.style.maxHeight = this.config.dropDownMaxHeight != null ? (this.config.dropDownMaxHeight + "px") : null;
        this.$dropDown.addEventListener("scroll", function (e) {
            e.stopPropagation();
            e.preventDefault();
        });
        this.setEditingMode(this.config.editingMode);
        this.$editor = (0, teamapps_client_core_1.parseHtml)("<span contenteditable=\"true\" class=\"tagbox-editor\" autocomplete=\"no\"></span>");
        this.$tagArea.append(this.$editor);
        this.$editor.classList.add("tr-tagcombobox-editor", "tr-editor");
        this.$editor.addEventListener("focus", function () {
            _this.setFocused(true);
            _this.$tagComboBox.offsetHeight;
            _this.$editor.scrollIntoView({
                behavior: "smooth",
                block: "nearest",
                inline: "nearest"
            });
        });
        this.$editor.addEventListener("blur", function (e) {
            if (_this.blurCausedByClickInsideComponent) {
                _this.$editor.focus();
                // Don't just unset the blurCausedByClickInsideComponent here! See the comment with hashtag #regainFocus.
            }
            else {
                _this.setFocused(false);
                _this.setTagToBeRemoved(null);
                _this.closeDropDown();
                if (_this.$editor.textContent.trim().length > 0) {
                    _this.addTag(_this.config.freeTextEntryFactory(_this.$editor.textContent), true, e);
                }
                if (_this.config.removePartialTagOnBlur && _this.currentPartialTag != null) {
                    _this.cancelPartialTag();
                }
                _this.$editor.textContent = "";
            }
        });
        this.$editor.addEventListener("keydown", function (e) {
            if ((0, TrivialCore_1.isModifierKey)(e)) {
                return;
            }
            else if (e.key == "Tab" || e.key == "Enter") {
                _this.setTagToBeRemoved(null);
                var highlightedEntry = _this.dropDownComponent.getValue();
                if (_this._isDropDownOpen && highlightedEntry != null) {
                    _this.addTag(highlightedEntry, true, e);
                    e.preventDefault(); // do not tab away from the tag box nor insert a newline character
                }
                else if (_this.$editor.textContent.trim().length > 0) {
                    _this.addTag(_this.config.freeTextEntryFactory(_this.$editor.textContent), true, e);
                    e.preventDefault(); // do not tab away from the tag box nor insert a newline character
                }
                else if (_this.currentPartialTag) {
                    if (e.shiftKey) { // we do not want the editor to get the focus right back, so we need to position the $editor intelligently...
                        _this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertAfter)(_this.$editor, _this.currentPartialTag.$tagWrapper); });
                    }
                    else {
                        _this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertBefore)(_this.$editor, _this.currentPartialTag.$tagWrapper); });
                    }
                    _this.currentPartialTag.$tagWrapper.remove();
                    _this.currentPartialTag = null;
                }
                _this.closeDropDown();
                if (e.key == "Enter") {
                    e.preventDefault(); // under any circumstances, prevent the new line to be added to the editor!
                }
            }
            else if (e.key == "ArrowLeft" || e.key == "ArrowRight") {
                _this.setTagToBeRemoved(null);
                if (_this._isDropDownOpen && _this.dropDownComponent.handleKeyboardInput(e)) {
                    e.preventDefault();
                    return;
                }
                else if (e.key == "ArrowLeft" && _this.$editor.textContent.length === 0 && window.getSelection().anchorOffset === 0) {
                    if (_this.$editor.previousElementSibling != null) {
                        _this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertBefore)(_this.$editor, _this.$editor.previousElementSibling); });
                        _this.focus();
                    }
                }
                else if (e.key == "ArrowRight" && _this.$editor.textContent.length === 0 && window.getSelection().anchorOffset === 0) {
                    if (_this.$editor.nextElementSibling != null) {
                        _this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertAfter)(_this.$editor, _this.$editor.nextElementSibling); });
                        _this.focus();
                    }
                }
            }
            else if (e.key == "Backspace" || e.key == "Delete") {
                if (_this.$editor.textContent == "") {
                    if (_this.currentPartialTag != null) {
                        _this.cancelPartialTag();
                        _this.focus();
                    }
                    else {
                        var tagToBeRemoved = _this.selectedEntries[(0, teamapps_client_core_1.elementIndex)(_this.$editor) + (e.key == "Backspace" ? -1 : 0)];
                        if (tagToBeRemoved != null) {
                            if (!_this.config.twoStepDeletion || _this.tagToBeRemoved === tagToBeRemoved) {
                                _this.removeTag(tagToBeRemoved, true, e);
                                _this.closeDropDown();
                                _this.setTagToBeRemoved(null);
                            }
                            else {
                                _this.setTagToBeRemoved(tagToBeRemoved);
                            }
                        }
                    }
                }
                else {
                    _this.doNoAutoCompleteBecauseBackspaceWasPressed = true; // we want query results, but no autocomplete
                    setTimeout(function () { return _this.query(0); }); // asynchronously to make sure the editor has been updated
                }
            }
            else if (e.key == "ArrowUp" || e.key == "ArrowDown") {
                _this.setTagToBeRemoved(null);
                var direction = e.key == "ArrowUp" ? -1 : 1;
                if (!_this._isDropDownOpen) {
                    _this.query(direction);
                    _this.openDropDown(); // directly open the dropdown (the user definitely wants to see it)
                }
                else {
                    if (_this.dropDownComponent.handleKeyboardInput(e)) {
                        if (_this.dropDownComponent.getValue() != null) {
                            _this.$editor.textContent = _this.config.entryToEditorTextFunction(_this.dropDownComponent.getValue());
                            (0, teamapps_client_core_components_1.selectElementContents)(_this.$editor);
                        }
                        else {
                            _this.$editor.textContent = "";
                        }
                    }
                }
                e.preventDefault(); // some browsers move the caret to the beginning on up key
            }
            else if (e.key == "Escape") {
                _this.setTagToBeRemoved(null);
                if (_this.$editor.textContent.length > 0) {
                    _this.$editor.textContent = "";
                }
                else if (_this.currentPartialTag != null) {
                    _this.cancelPartialTag();
                    _this.focus();
                }
                _this.closeDropDown();
            }
            else {
                _this.setTagToBeRemoved(null);
                if (!_this.config.showDropDownOnResultsOnly) {
                    _this.openDropDown();
                }
                // We need the new editor value (after the keydown event). Therefore setTimeout().
                setTimeout(function () { return _this.query(_this.config.preselectFirstQueryResult && _this.$editor.textContent ? 1 : 0); });
            }
        });
        this.$editor.addEventListener("keyup", function (e) {
            function splitStringBySeparatorChars(s, separatorChars) {
                return s.split(new RegExp("[" + (0, TrivialCore_1.escapeSpecialRegexCharacter)(separatorChars.join()) + "]"));
            }
            if (_this.$editor.querySelector(':scope *') != null) {
                _this.$editor.innerHTML = _this.$editor.textContent; // removes possible <div> or <br> or whatever the browser likes to put inside...
            }
            var editorValueBeforeCursor = _this.getNonSelectedEditorValue();
            if (editorValueBeforeCursor.length > 0) {
                var tagValuesEnteredByUser = splitStringBySeparatorChars(editorValueBeforeCursor, _this.config.freeTextSeparators);
                for (var i = 0; i < tagValuesEnteredByUser.length - 1; i++) {
                    var value = tagValuesEnteredByUser[i].trim();
                    if (value.length > 0) {
                        _this.addTag(_this.config.freeTextEntryFactory(value), true, e);
                    }
                    _this.$editor.textContent = tagValuesEnteredByUser[tagValuesEnteredByUser.length - 1];
                    (0, teamapps_client_core_components_1.selectElementContents)(_this.$editor, _this.$editor.textContent.length, _this.$editor.textContent.length);
                    _this.closeDropDown();
                }
            }
        });
        this.$editor.addEventListener("mousedown", function () {
            if (_this.editingMode === "editable") {
                if (!_this.config.showDropDownOnResultsOnly) {
                    _this.openDropDown();
                }
                _this.query();
            }
        });
        [this.$tagComboBox, this.$dropDown].forEach(function (element) {
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
        this.$dropDown.append(this.dropDownComponent.getMainDomElement());
        this.dropDownComponent.onValueChanged.addListener(function (eventData) {
            if (eventData.finalSelection) {
                _this.setTagToBeRemoved(null);
                _this.addTag(eventData.value, true);
                _this.closeDropDown();
            }
        });
        this.$tagArea.addEventListener("mousedown", function (e) {
            if (_this.currentPartialTag == null) {
                var $nearestTag_1 = _this.findNearestTag(e);
                if ($nearestTag_1) {
                    var tagBoundingRect = $nearestTag_1.getBoundingClientRect();
                    var isRightSide = e.clientX > (tagBoundingRect.left + tagBoundingRect.right) / 2;
                    if (isRightSide) {
                        _this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertAfter)(_this.$editor, $nearestTag_1); });
                    }
                    else {
                        _this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertBefore)(_this.$editor, $nearestTag_1); });
                    }
                }
            }
            _this.$editor.focus();
        });
        this.$tagArea.addEventListener("click", function (e) {
            if (_this.editingMode === "editable") {
                if (!_this.config.showDropDownOnResultsOnly) {
                    _this.openDropDown();
                }
                _this.query();
            }
        });
        this.$editor.addEventListener("keyup", function () { return _this.updatePlaceholderTextVisibility(); });
    }
    TrivialTagComboBox.prototype.setFocused = function (focused) {
        if (focused != this.focused) {
            if (focused) {
                this.onFocus.fire();
            }
            else {
                this.onBlur.fire();
            }
            this.$tagComboBox.classList.toggle('focus', focused);
            this.focused = focused;
        }
    };
    TrivialTagComboBox.prototype.setTagToBeRemoved = function (tagToBeRemoved) {
        if (this.tagToBeRemoved != null) {
            this.tagToBeRemoved.$tagWrapper.classList.remove("marked-for-removal");
        }
        this.tagToBeRemoved = tagToBeRemoved;
        if (tagToBeRemoved != null) {
            tagToBeRemoved.$tagWrapper.classList.add("marked-for-removal");
        }
    };
    TrivialTagComboBox.prototype.cancelPartialTag = function () {
        var _this = this;
        this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertBefore)(_this.$editor, _this.currentPartialTag.$tagWrapper); });
        this.currentPartialTag.$tagWrapper.remove();
        this.currentPartialTag = null;
    };
    TrivialTagComboBox.prototype.findNearestTag = function (mouseEvent) {
        var $nearestTag = null;
        var smallestDistanceX = 1000000;
        for (var i = 0; i < this.selectedEntries.length; i++) {
            var selectedEntry = this.selectedEntries[i];
            var $tag = selectedEntry.$tagWrapper;
            var tagBoundingRect = $tag.getBoundingClientRect();
            var sameRow = mouseEvent.clientY >= tagBoundingRect.top && mouseEvent.clientY < tagBoundingRect.bottom;
            var sameCol = mouseEvent.clientX >= tagBoundingRect.left && mouseEvent.clientX < tagBoundingRect.right;
            var distanceX = sameCol ? 0 : Math.min(Math.abs(mouseEvent.clientX - tagBoundingRect.left), Math.abs(mouseEvent.clientX - tagBoundingRect.right));
            if (sameRow && distanceX < smallestDistanceX) {
                $nearestTag = $tag;
                smallestDistanceX = distanceX;
                if (distanceX === 0) {
                    break;
                }
            }
        }
        return $nearestTag;
    };
    TrivialTagComboBox.prototype.removeTag = function (tagToBeRemoved, fireChangeEvent, originalEvent) {
        if (fireChangeEvent === void 0) { fireChangeEvent = false; }
        var index = this.selectedEntries.indexOf(tagToBeRemoved);
        if (index > -1) {
            this.selectedEntries.splice(index, 1);
        }
        tagToBeRemoved.$tagWrapper.remove();
        if (fireChangeEvent) {
            this.fireChangeEvents(this.getSelectedEntries(), originalEvent);
        }
        this.updatePlaceholderTextVisibility();
    };
    TrivialTagComboBox.prototype.query = function (highlightDirection) {
        var _a;
        if (highlightDirection === void 0) { highlightDirection = 0; }
        return __awaiter(this, void 0, void 0, function () {
            var gotResultsForQuery;
            return __generator(this, function (_b) {
                switch (_b.label) {
                    case 0: return [4 /*yield*/, this.dropDownComponent.handleQuery(this.getNonSelectedEditorValue(), highlightDirection, (_a = this.getSelectedEntries()[0]) !== null && _a !== void 0 ? _a : null)];
                    case 1:
                        gotResultsForQuery = _b.sent();
                        this.blurCausedByClickInsideComponent = false; // we won't get any mouseout or mouseup events for entries if they get removed. so do this here proactively
                        this.autoCompleteIfPossible(this.config.autoCompleteDelay);
                        if (this.config.showDropDownOnResultsOnly && gotResultsForQuery && document.activeElement == this.$editor) {
                            this.openDropDown();
                        }
                        return [2 /*return*/];
                }
            });
        });
    };
    TrivialTagComboBox.prototype.fireChangeEvents = function (entries, originalEvent) {
        this.onValueChanged.fire(entries.map(TrivialCore_1.unProxyEntry));
    };
    TrivialTagComboBox.prototype.addTag = function (entry, fireEvent, originalEvent, forceAcceptance) {
        var _this = this;
        if (fireEvent === void 0) { fireEvent = false; }
        if (entry == null) {
            return; // do nothing
        }
        if (!forceAcceptance && !this.config.selectionAcceptor(entry)) {
            return;
        }
        var wasPartial = !!this.currentPartialTag;
        var editorIndex = wasPartial ? (0, teamapps_client_core_1.elementIndex)(this.currentPartialTag.$tagWrapper) : (0, teamapps_client_core_1.elementIndex)(this.$editor);
        if (wasPartial) {
            this.doPreservingFocus(function () { return _this.$tagArea.append(_this.$editor); }); // make sure the event handlers don't get detached when removing the partial tag
            this.currentPartialTag.$tagWrapper.remove();
            entry = this.config.entryMerger(this.currentPartialTag.entry, entry);
        }
        var $entry = (0, teamapps_client_core_1.parseHtml)(this.config.selectedEntryRenderingFunction(entry));
        var $tagWrapper = (0, teamapps_client_core_1.parseHtml)('<div class="tr-tagcombobox-tag"></div>');
        $tagWrapper.append($entry);
        var tag = new Tag(entry, $tagWrapper, $entry);
        if (this.config.tagCompleteDecider(entry)) {
            this.selectedEntries.splice(editorIndex, 0, tag);
            this.currentPartialTag = null;
        }
        else {
            this.currentPartialTag = tag;
        }
        this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertAtIndex)(_this.$tagArea, $tagWrapper, editorIndex); });
        var removeButton = $entry.querySelector('.tr-remove-button');
        if (removeButton != null) {
            removeButton.addEventListener("click", function (e) {
                _this.removeTag(tag, true, e);
                return false;
            });
        }
        if (this.config.tagCompleteDecider(entry)) {
            this.doPreservingFocus(function () { return (0, teamapps_client_core_1.insertAtIndex)(_this.$tagArea, _this.$editor, editorIndex + 1); });
        }
        else {
            this.doPreservingFocus(function () { return $entry.querySelector('.tr-editor').append(_this.$editor); });
        }
        this.$editor.textContent = "";
        if (document.activeElement == this.$editor) {
            (0, teamapps_client_core_components_1.selectElementContents)(this.$editor, 0, 0); // make sure Chrome displays a cursor after changing the content (bug...)
        }
        if (this.config.tagCompleteDecider(entry) && fireEvent) {
            this.fireChangeEvents(this.getSelectedEntries(), originalEvent);
        }
        this.updatePlaceholderTextVisibility();
    };
    TrivialTagComboBox.prototype.repositionDropDown = function () {
        this.popper.update();
    };
    TrivialTagComboBox.prototype.openDropDown = function () {
        var _this = this;
        var _a;
        if (this.isDropDownNeeded()) {
            if (this.getMainDomElement().parentElement !== this.parentElement || this.dropdownAutoUpdateDisposable == null) {
                (_a = this.dropdownAutoUpdateDisposable) === null || _a === void 0 ? void 0 : _a.call(this);
                this.dropdownAutoUpdateDisposable = this.dropdownAutoUpdateDisposable = (0, ComboBoxPopper_1.positionDropdownWithAutoUpdate)(this.$tagComboBox, this.$dropDown, {
                    referenceOutOfViewPortHandler: function () { return _this.closeDropDown(); }
                });
                this.parentElement = this.getMainDomElement().parentElement;
            }
            this.$tagComboBox.classList.add("open");
            this.repositionDropDown();
            this._isDropDownOpen = true;
        }
    };
    TrivialTagComboBox.prototype.closeDropDown = function () {
        this.$tagComboBox.classList.remove("open");
        this._isDropDownOpen = false;
    };
    TrivialTagComboBox.prototype.getNonSelectedEditorValue = function () {
        var editorText = this.$editor.textContent.replace(String.fromCharCode(160), " ");
        var selection = window.getSelection();
        if (selection.anchorOffset != selection.focusOffset) {
            return editorText.substring(0, Math.min(window.getSelection().baseOffset, window.getSelection().focusOffset));
        }
        else {
            return editorText;
        }
    };
    TrivialTagComboBox.prototype.autoCompleteIfPossible = function (delay) {
        var _this = this;
        if (this.config.autoComplete) {
            clearTimeout(this.autoCompleteTimeoutId);
            var dropDownValue_1 = this.dropDownComponent.getValue();
            if (dropDownValue_1 && !this.doNoAutoCompleteBecauseBackspaceWasPressed) {
                this.autoCompleteTimeoutId = (0, TrivialCore_1.setTimeoutOrDoImmediately)(function () {
                    var currentEditorValue = _this.getNonSelectedEditorValue();
                    var entryAsString = _this.config.entryToEditorTextFunction(dropDownValue_1);
                    if (entryAsString.toLowerCase().indexOf(("" + currentEditorValue).toLowerCase()) === 0) {
                        _this.$editor.textContent = currentEditorValue + entryAsString.substr(currentEditorValue.length);
                        if (document.activeElement == _this.$editor) {
                            (0, teamapps_client_core_components_1.selectElementContents)(_this.$editor, currentEditorValue.length, entryAsString.length);
                        }
                    }
                    _this.repositionDropDown(); // the auto-complete might cause a line-break, so the dropdown would cover the editor...
                }, delay || 0);
            }
            this.doNoAutoCompleteBecauseBackspaceWasPressed = false;
        }
    };
    TrivialTagComboBox.prototype.isDropDownNeeded = function () {
        return this.editingMode == 'editable';
    };
    TrivialTagComboBox.prototype.doPreservingFocus = function (f) {
        var hadFocus = document.activeElement == this.$editor;
        var oldValueOfBlurCausedByClickInsideComponent = this.blurCausedByClickInsideComponent;
        this.blurCausedByClickInsideComponent = true; // prevent triggering the onBlur event etc...
        try {
            return f.call(this);
        }
        finally {
            this.blurCausedByClickInsideComponent = oldValueOfBlurCausedByClickInsideComponent;
            var hasFocus = document.activeElement == this.$editor;
            if (hadFocus && !hasFocus) {
                this.$editor.focus();
            }
        }
    };
    TrivialTagComboBox.prototype.setEditingMode = function (newEditingMode) {
        this.editingMode = newEditingMode;
        this.$tagComboBox.classList.remove("editable", "readonly", "disabled");
        this.$tagComboBox.classList.add(this.editingMode);
        if (this.isDropDownNeeded()) {
            this.$tagComboBox.append(this.$dropDown);
        }
    };
    TrivialTagComboBox.prototype.setSelectedEntries = function (entries, forceAcceptance) {
        var _this = this;
        this.selectedEntries
            .slice() // copy the array as it gets changed during the forEach loop
            .forEach(function (e) { return _this.removeTag(e, false); });
        if (entries) {
            for (var i = 0; i < entries.length; i++) {
                this.addTag(entries[i], false, null, forceAcceptance);
            }
        }
        if (this.$clearButton) {
            this.$clearButton.classList.toggle("hidden", !this.config.showClearButton || this.selectedEntries.length > 0);
        }
    };
    TrivialTagComboBox.prototype.getSelectedEntries = function () {
        return this.selectedEntries.map(function (tag) { return tag.entry; });
    };
    ;
    TrivialTagComboBox.prototype.getDropDownComponent = function () {
        return this.dropDownComponent;
    };
    TrivialTagComboBox.prototype.getCurrentPartialTag = function () {
        return this.currentPartialTag;
    };
    TrivialTagComboBox.prototype.focus = function () {
        this.$editor.focus();
        (0, teamapps_client_core_components_1.selectElementContents)(this.$editor, 0, this.$editor.textContent.length); // we need to do this, else the cursor does not appear in Chrome when navigating using left and right keys...
    };
    TrivialTagComboBox.prototype.getEditor = function () {
        return this.$editor;
    };
    TrivialTagComboBox.prototype.getDropDown = function () {
        return this.$dropDown;
    };
    ;
    TrivialTagComboBox.prototype.setShowClearButton = function (showClearButton) {
        this.config.showClearButton = showClearButton;
        this.$clearButton.classList.toggle('hidden', !this.config.showClearButton || this.selectedEntries.length > 0);
    };
    TrivialTagComboBox.prototype.setShowTrigger = function (showTrigger) {
        this.config.showTrigger = showTrigger;
        this.$trigger.classList.toggle('hidden', !showTrigger);
    };
    TrivialTagComboBox.prototype.isDropDownOpen = function () {
        return this._isDropDownOpen;
    };
    TrivialTagComboBox.prototype.destroy = function () {
        this.$tagComboBox.remove();
        this.$dropDown.remove();
    };
    ;
    TrivialTagComboBox.prototype.getMainDomElement = function () {
        return this.$tagComboBox;
    };
    TrivialTagComboBox.prototype.updatePlaceholderTextVisibility = function () {
        this.$placeholderText.classList.toggle("hidden", this.selectedEntries.length > 0 || !!this.$editor.textContent);
    };
    TrivialTagComboBox.prototype.setPlaceholderText = function (placeholderText) {
        this.$placeholderText.innerText = placeholderText !== null && placeholderText !== void 0 ? placeholderText : "";
    };
    TrivialTagComboBox.prototype.setShowDropDownOnResultsOnly = function (showDropDownAfterResultsArrive) {
        this.config.showDropDownOnResultsOnly = showDropDownAfterResultsArrive;
    };
    TrivialTagComboBox.prototype.setPreselectFirstQueryResult = function (preselectFirstQueryResult) {
        this.config.preselectFirstQueryResult = preselectFirstQueryResult;
    };
    TrivialTagComboBox.prototype.setAutoComplete = function (autoComplete) {
        this.config.autoComplete = autoComplete;
    };
    TrivialTagComboBox.prototype.setDropdownMinWidth = function (dropDownMinWidth) {
        this.$dropDown.style.minWidth = dropDownMinWidth != null ? (dropDownMinWidth + "px") : null;
    };
    TrivialTagComboBox.prototype.setDropdownMaxHeight = function (dropDownMaxHeight) {
        this.$dropDown.style.maxHeight = dropDownMaxHeight != null ? (dropDownMaxHeight + "px") : null;
    };
    TrivialTagComboBox.prototype.setTwoStepDeletion = function (twoStepDeletionEnabled) {
        this.config.twoStepDeletion = twoStepDeletionEnabled;
    };
    return TrivialTagComboBox;
}());
exports.TrivialTagComboBox = TrivialTagComboBox;
var Tag = /** @class */ (function () {
    function Tag(entry, $tagWrapper, $entry) {
        this.entry = entry;
        this.$tagWrapper = $tagWrapper;
        this.$entry = $entry;
    }
    return Tag;
}());
