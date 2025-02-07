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
exports.TrivialTree = void 0;
var TrivialTreeBox_1 = require("./TrivialTreeBox");
var TrivialCore_1 = require("./TrivialCore");
var teamapps_client_core_1 = require("teamapps-client-core");
var TrivialTree = /** @class */ (function () {
    function TrivialTree(options) {
        var _this = this;
        this.onSelectedEntryChanged = new teamapps_client_core_1.TeamAppsEvent();
        this.onNodeExpansionStateChanged = new teamapps_client_core_1.TeamAppsEvent();
        var defaultIdFunction = function (entry) { return entry ? entry.id : null; };
        this.config = __assign({ idFunction: defaultIdFunction, childrenProperty: "children", lazyChildrenFlag: "hasLazyChildren", lazyChildrenQueryFunction: function () { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                return [2 /*return*/, []];
            }); }); }, expandedProperty: 'expanded', spinnerTemplate: TrivialCore_1.DEFAULT_TEMPLATES.defaultSpinnerTemplate, noEntriesTemplate: TrivialCore_1.DEFAULT_TEMPLATES.defaultNoEntriesTemplate, entries: null, selectedEntryId: null, matchingOptions: {
                matchingMode: 'contains',
                ignoreCase: true
            }, directSelectionViaArrowKeys: false }, options);
        this.entries = this.config.entries;
        this.$componentWrapper = (0, teamapps_client_core_1.parseHtml)('<div class="tr-tree" tabindex="0"></div>');
        this.$componentWrapper.addEventListener("keydown", function (e) {
            if (e.key == "ArrowUp" || e.key == "ArrowDown") {
                var direction = e.key == "ArrowUp" ? -1 : 1;
                if (_this.entries != null) {
                    if (_this.config.directSelectionViaArrowKeys) {
                        _this.treeBox.selectNextEntry(direction, false, false, function () { return true; }, true, e);
                    }
                    else {
                        _this.treeBox.selectNextEntry(direction, false);
                    }
                    return false; // some browsers move the caret to the beginning on up key
                }
            }
            else if (e.key == "ArrowLeft" || e.key == "ArrowRight") {
                _this.treeBox.setSelectedNodeExpanded(e.key == "ArrowRight");
            }
            else if (e.key == "Enter") {
                _this.fireChangeEvents(_this.treeBox.getSelectedEntry());
            }
        });
        this.treeBox = new TrivialTreeBox_1.TrivialTreeBox(this.config);
        this.$componentWrapper.append(this.treeBox.getMainDomElement());
        this.treeBox.onNodeExpansionStateChanged.addListener(function (node) {
            _this.onNodeExpansionStateChanged.fire(node);
        });
        this.treeBox.onSelectedEntryChanged.addListener(function () {
            var selectedTreeBoxEntry = _this.treeBox.getSelectedEntry();
            if (selectedTreeBoxEntry) {
                _this.setSelectedEntry(selectedTreeBoxEntry);
            }
        });
        this.setSelectedEntry((this.config.selectedEntryId !== undefined && this.config.selectedEntryId !== null) ? this.findEntryById(this.config.selectedEntryId) : null);
    }
    TrivialTree.prototype.updateEntries = function (newEntries) {
        this.entries = newEntries;
        this.treeBox.setEntries(newEntries);
    };
    TrivialTree.prototype.findEntries = function (filterFunction) {
        var _this = this;
        var findEntriesInSubTree = function (node, listOfFoundEntries) {
            if (filterFunction.call(_this, node)) {
                listOfFoundEntries.push(node);
            }
            if (node[_this.config.childrenProperty]) {
                for (var i = 0; i < node[_this.config.childrenProperty].length; i++) {
                    var child = node[_this.config.childrenProperty][i];
                    findEntriesInSubTree(child, listOfFoundEntries);
                }
            }
        };
        var matchingEntries = [];
        for (var i = 0; i < this.entries.length; i++) {
            var rootEntry = this.entries[i];
            findEntriesInSubTree(rootEntry, matchingEntries);
        }
        return matchingEntries;
    };
    TrivialTree.prototype.findEntryById = function (id) {
        var _this = this;
        return this.findEntries(function (entry) {
            return _this.config.idFunction(entry) === id;
        })[0];
    };
    TrivialTree.prototype.setSelectedEntry = function (entry) {
        this.config.selectedEntryId = entry ? this.config.idFunction(entry) : null;
        this.fireChangeEvents(entry);
    };
    TrivialTree.prototype.fireChangeEvents = function (entry) {
        this.onSelectedEntryChanged.fire((0, TrivialCore_1.unProxyEntry)(entry));
    };
    TrivialTree.prototype.getSelectedEntry = function () {
        (0, TrivialCore_1.unProxyEntry)(this.treeBox.getSelectedEntry());
    };
    ;
    TrivialTree.prototype.updateChildren = function (parentNodeId, children) {
        this.treeBox.updateChildren(parentNodeId, children);
    };
    ;
    TrivialTree.prototype.updateNode = function (node) {
        this.treeBox.updateNode(node);
    };
    ;
    TrivialTree.prototype.removeNode = function (nodeId) {
        this.treeBox.removeNode(nodeId);
    };
    ;
    TrivialTree.prototype.addNode = function (parentNodeId, node) {
        this.treeBox.addNode(parentNodeId, node);
    };
    ;
    TrivialTree.prototype.addOrUpdateNode = function (parentNodeId, node, recursiveUpdate) {
        if (recursiveUpdate === void 0) { recursiveUpdate = false; }
        this.treeBox.addOrUpdateNode(parentNodeId, node, recursiveUpdate);
    };
    TrivialTree.prototype.getTreeBox = function () {
        return this.treeBox;
    };
    TrivialTree.prototype.setShowExpanders = function (showExpanders) {
        this.treeBox.setShowExpanders(showExpanders);
    };
    TrivialTree.prototype.setAnimationDuration = function (animationDuration) {
        this.treeBox.setAnimationDuration(animationDuration);
    };
    TrivialTree.prototype.setSelectedEntryById = function (nodeId, reveal) {
        this.treeBox.setSelectedEntryById(nodeId, reveal);
    };
    TrivialTree.prototype.setExpandOnSelection = function (expandOnSelection) {
        this.treeBox.setExpandOnSelection(expandOnSelection);
    };
    TrivialTree.prototype.setEnforceSingleExpandedPath = function (enforceSingleExpandedPath) {
        this.treeBox.setEnforceSingleExpandedPath(enforceSingleExpandedPath);
    };
    TrivialTree.prototype.setIndentationWidth = function (indentationWidth) {
        this.treeBox.setIndentationWidth(indentationWidth);
    };
    TrivialTree.prototype.destroy = function () {
        this.$componentWrapper.remove();
    };
    ;
    TrivialTree.prototype.getMainDomElement = function () {
        return this.$componentWrapper;
    };
    return TrivialTree;
}());
exports.TrivialTree = TrivialTree;
