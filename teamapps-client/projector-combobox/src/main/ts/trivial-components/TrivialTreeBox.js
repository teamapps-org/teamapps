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
exports.TrivialTreeBox = void 0;
var TrivialCore_1 = require("./TrivialCore");
var highlight_1 = require("./util/highlight");
var teamapps_client_core_1 = require("teamapps-client-core");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var EntryWrapper = /** @class */ (function () {
    function EntryWrapper(entry, parent, config) {
        var _this = this;
        this.parent = parent;
        this.config = config;
        this.entry = entry;
        this.depth = parent != null ? parent.depth + 1 : 0;
        var children = this.entry[config.childrenProperty];
        var hasLazyChildren = (typeof config.lazyChildrenFlag === 'string') ? !!this.entry[config.lazyChildrenFlag] : config.lazyChildrenFlag(entry);
        if (children == null && hasLazyChildren) {
            this.children = null;
        }
        else if (children == null) {
            this.children = [];
        }
        else {
            this.children = children.map(function (child) { return new EntryWrapper(child, _this, config); });
        }
        var id = config.idFunction(entry);
        this._id = id != null ? id : (0, TrivialCore_1.generateUUID)();
    }
    Object.defineProperty(EntryWrapper.prototype, "id", {
        get: function () {
            return this._id;
        },
        enumerable: false,
        configurable: true
    });
    EntryWrapper.prototype.lazyLoadChildren = function () {
        return __awaiter(this, void 0, void 0, function () {
            var entries;
            var _this = this;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0: return [4 /*yield*/, this.config.lazyChildrenQueryFunction(this.entry)];
                    case 1:
                        entries = _a.sent();
                        this.children = entries.map(function (child) { return new EntryWrapper(child, _this, _this.config); });
                        return [2 /*return*/, entries];
                }
            });
        });
    };
    EntryWrapper.prototype.render = function () {
        if (!this.$element) {
            this.$element = this.config.elementFactory(this);
        }
        return this.$element;
    };
    EntryWrapper.prototype.isLeaf = function () {
        return this.children != null && this.children.length === 0;
    };
    Object.defineProperty(EntryWrapper.prototype, "expanded", {
        get: function () {
            return this.entry[this.config.expandedProperty] || false;
        },
        set: function (expanded) {
            this.entry[this.config.expandedProperty] = expanded;
            if (this.$element) {
                this.$element.classList.toggle("expanded", expanded);
            }
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(EntryWrapper.prototype, "$treeEntryAndExpanderWrapper", {
        get: function () {
            var _a;
            return (_a = this.$element) === null || _a === void 0 ? void 0 : _a.querySelector(":scope > .tr-tree-entry-and-expander-wrapper");
        },
        enumerable: false,
        configurable: true
    });
    Object.defineProperty(EntryWrapper.prototype, "$childrenWrapper", {
        get: function () {
            var _a;
            return (_a = this.$element) === null || _a === void 0 ? void 0 : _a.querySelector(":scope > .tr-tree-entry-children-wrapper");
        },
        enumerable: false,
        configurable: true
    });
    return EntryWrapper;
}());
var TrivialTreeBox = /** @class */ (function () {
    function TrivialTreeBox(options) {
        var _this = this;
        this.onSelectedEntryChanged = new teamapps_client_core_1.TeamAppsEvent();
        this.onNodeExpansionStateChanged = new teamapps_client_core_1.TeamAppsEvent();
        this.config = __assign({ idFunction: function (entry) { return entry ? entry.id : null; }, childrenProperty: "children", lazyChildrenFlag: "hasLazyChildren", lazyChildrenQueryFunction: function () { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                return [2 /*return*/, []];
            }); }); }, expandedProperty: 'expanded', spinnerTemplate: TrivialCore_1.DEFAULT_TEMPLATES.defaultSpinnerTemplate, noEntriesTemplate: TrivialCore_1.DEFAULT_TEMPLATES.defaultNoEntriesTemplate, entries: null, selectedEntryId: null, matchingOptions: {
                matchingMode: 'contains',
                ignoreCase: true
            }, animationDuration: 70, showExpanders: false, expandOnSelection: false, enforceSingleExpandedPath: false, indentation: null, selectableDecider: function () { return true; }, selectOnHover: false }, options);
        this.$componentWrapper = (0, teamapps_client_core_1.parseHtml)('<div class="tr-treebox"></div>');
        this.setShowExpanders(this.config.showExpanders);
        this.setEntries(this.config.entries || []);
        this.setSelectedEntryById(this.config.selectedEntryId);
    }
    TrivialTreeBox.prototype.createEntryElement = function (entry) {
        var $outerEntryWrapper = (0, teamapps_client_core_1.parseHtml)("<div class=\"tr-tree-entry-outer-wrapper ".concat((entry.isLeaf() ? '' : 'has-children'), "\" data-id=\"").concat(entry.id, "\"></div>"));
        entry.$element = $outerEntryWrapper;
        var $entryAndExpanderWrapper = (0, teamapps_client_core_1.parseHtml)('<div class="tr-tree-entry-and-expander-wrapper"></div>');
        $outerEntryWrapper.append($entryAndExpanderWrapper);
        $entryAndExpanderWrapper.trivialEntryWrapper = entry;
        for (var k = 0; k < entry.depth; k++) {
            var indentationWidth = void 0;
            if (typeof this.config.indentation === 'number') {
                indentationWidth = this.config.indentation;
            }
            else if (Array.isArray(this.config.indentation)) {
                indentationWidth = this.config.indentation[Math.min(k, this.config.indentation.length - 1)];
            }
            else if (typeof this.config.indentation === 'function') {
                indentationWidth = this.config.indentation(k);
            }
            $entryAndExpanderWrapper.append("<div class=\"tr-indent-spacer\" ".concat(indentationWidth != null ? "style=\"width:".concat(indentationWidth, "px\"") : '', "></div>"));
        }
        var $treeExpander = (0, teamapps_client_core_1.parseHtml)('<div class="tr-tree-expander"></div>');
        $entryAndExpanderWrapper.append($treeExpander);
        var $entry = (0, teamapps_client_core_1.parseHtml)(this.config.entryRenderingFunction(entry.entry, entry.depth));
        $entry.classList.add("tr-tree-entry", "filterable-item");
        $entryAndExpanderWrapper.append($entry);
        if (entry.id === this.selectedEntryId) {
            $entryAndExpanderWrapper.classList.add("tr-selected-entry");
        }
        if (!entry.isLeaf()) {
            this.create$ChildrenWrapper(entry);
        }
        this.setNodeExpanded(entry, entry.expanded, false);
        return $outerEntryWrapper;
    };
    TrivialTreeBox.prototype.create$ChildrenWrapper = function (entry) {
        var $childrenWrapper = entry.$childrenWrapper;
        if ($childrenWrapper == null) {
            $childrenWrapper = (0, teamapps_client_core_1.parseHtml)('<div class="tr-tree-entry-children-wrapper"></div>');
            entry.$element.append($childrenWrapper);
            if (entry.children != null) {
                if (entry.expanded) {
                    for (var i = 0; i < entry.children.length; i++) {
                        $childrenWrapper.append(this.createEntryElement(entry.children[i]));
                    }
                }
            }
            else {
                $childrenWrapper.append(this.config.spinnerTemplate);
            }
        }
        return $childrenWrapper;
    };
    TrivialTreeBox.prototype.setNodeExpanded = function (node, expanded, animate) {
        var _this = this;
        var expansionStateChange = !!node.expanded != !!expanded;
        if (expanded && this.config.enforceSingleExpandedPath) {
            this.enforceSingleExpandedPath(node);
        }
        node.expanded = expanded;
        if (node.$element) {
            var nodeHasUnrenderedChildren = function (node) {
                return node.children && node.children.some(function (child) {
                    return !child.$element || !document.documentElement.contains(child.$element);
                });
            };
            if (expanded && node.children == null) {
                node.lazyLoadChildren()
                    .then(function (children) { return _this.setChildren(node, children); });
            }
            else if (expanded && nodeHasUnrenderedChildren(node)) {
                this.renderChildren(node);
            }
            if (expanded) {
                this.minimallyScrollTo(node.$element);
            }
            var $childrenWrapper = node.$childrenWrapper;
            if ($childrenWrapper != null) {
                (0, teamapps_client_core_components_1.toggleElementCollapsed)($childrenWrapper, !expanded, animate ? this.config.animationDuration : 0);
            }
        }
        if (expansionStateChange) {
            this.onNodeExpansionStateChanged.fire(node.entry);
        }
    };
    TrivialTreeBox.prototype.enforceSingleExpandedPath = function (node) {
        var currentlyExpandedNodes = this.findEntries(function (n) {
            return n.expanded;
        });
        var newExpandedPath = this.findPathToFirstMatchingNode(function (n) {
            return n === node;
        });
        for (var i = 0; i < currentlyExpandedNodes.length; i++) {
            var currentlyExpandedNode = currentlyExpandedNodes[i];
            if (newExpandedPath.indexOf(currentlyExpandedNode) === -1) {
                this.setNodeExpanded(currentlyExpandedNode, false, true);
            }
        }
    };
    TrivialTreeBox.prototype.setChildren = function (parentEntryWrapper, children) {
        var _this = this;
        parentEntryWrapper.children = children.map(function (child) { return _this.createEntryWrapper(child, parentEntryWrapper); });
        this.renderChildren(parentEntryWrapper);
    };
    TrivialTreeBox.prototype.renderChildren = function (node) {
        var $childrenWrapper = this.create$ChildrenWrapper(node);
        $childrenWrapper.innerHTML = ''; // remove the spinner!
        if (node.children && node.children.length > 0) {
            node.children.forEach(function (child) {
                $childrenWrapper.append(child.render());
            });
        }
        else {
            node.$element.classList.remove('has-children', 'expanded');
        }
    };
    TrivialTreeBox.prototype.setEntries = function (newEntries) {
        var _this = this;
        newEntries = newEntries || [];
        this.entries = newEntries.map(function (e) { return _this.createEntryWrapper(e, null); });
        this.$tree && this.$tree.remove();
        this.$tree = (0, teamapps_client_core_1.parseHtml)('<div class="tr-tree-entryTree"></div>');
        (0, teamapps_client_core_components_1.addDelegatedEventListener)(this.$tree, ".tr-tree-expander", "mousedown", function (element, ev) {
            ev.stopPropagation();
            ev.stopImmediatePropagation();
        }, true);
        (0, teamapps_client_core_components_1.addDelegatedEventListener)(this.$tree, ".tr-tree-expander", "click", function (element, ev) {
            var entryWrapper = (0, teamapps_client_core_components_1.closestAncestor)(element, ".tr-tree-entry-and-expander-wrapper").trivialEntryWrapper;
            _this.setNodeExpanded(entryWrapper, !entryWrapper.expanded, true);
        });
        (0, teamapps_client_core_components_1.addDelegatedEventListener)(this.$tree, ".tr-tree-entry-and-expander-wrapper", "mousedown", function (element, ev) {
            var entryWrapper = element.trivialEntryWrapper;
            if (_this.config.selectableDecider(entryWrapper.entry)) {
                _this.setSelectedEntry(entryWrapper, null, true);
                if (entryWrapper && _this.config.expandOnSelection) {
                    _this.setNodeExpanded(entryWrapper, true, true);
                }
            }
        });
        (0, teamapps_client_core_components_1.addDelegatedEventListener)(this.$tree, ".tr-tree-entry-and-expander-wrapper", "mouseenter", function (element, ev) {
            var entryWrapper = element.trivialEntryWrapper;
            if (_this.config.selectOnHover && _this.config.selectableDecider(entryWrapper.entry)) {
                _this.setSelectedEntry(entryWrapper, ev, false);
            }
        }, true);
        if (this.entries.length > 0) {
            this.entries.forEach(function (entry) { return _this.$tree.append(entry.render()); });
        }
        else {
            this.$tree.append((0, teamapps_client_core_1.parseHtml)(this.config.noEntriesTemplate));
        }
        this.$componentWrapper.append(this.$tree);
        var selectedEntry = this.findEntryById(this.selectedEntryId);
        if (selectedEntry) {
            // selected entry in filtered tree? then mark it as selected!
            this.markSelectedEntry(selectedEntry);
        }
    };
    TrivialTreeBox.prototype.createEntryWrapper = function (e, parent) {
        var _this = this;
        return new EntryWrapper(e, parent, __assign(__assign({}, this.config), { elementFactory: function (entry) { return _this.createEntryElement(entry); } }));
    };
    TrivialTreeBox.prototype.findEntries = function (filterFunction, visibleOnly) {
        var _this = this;
        if (visibleOnly === void 0) { visibleOnly = false; }
        var findEntriesInSubTree = function (node, listOfFoundEntries) {
            if (filterFunction.call(_this, node)) {
                listOfFoundEntries.push(node);
            }
            if (!visibleOnly || node.expanded) {
                if (node.children) {
                    for (var i = 0; i < node.children.length; i++) {
                        var child = node.children[i];
                        findEntriesInSubTree(child, listOfFoundEntries);
                    }
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
    TrivialTreeBox.prototype.findPathToFirstMatchingNode = function (predicateFunction) {
        var _this = this;
        var searchInSubTree = function (node, path) {
            if (predicateFunction.call(_this, node, path)) {
                path.push(node);
                return path;
            }
            if (node.children) {
                var newPath = path.slice();
                newPath.push(node);
                for (var i = 0; i < node.children.length; i++) {
                    var child = node.children[i];
                    var result = searchInSubTree(child, newPath);
                    if (result) {
                        return result;
                    }
                }
            }
        };
        for (var i = 0; i < this.entries.length; i++) {
            var rootEntry = this.entries[i];
            var path = searchInSubTree(rootEntry, []);
            if (path) {
                return path;
            }
        }
    };
    TrivialTreeBox.prototype.findEntryById = function (id) {
        if (id == null) {
            return null;
        }
        return this.findEntries(function (entry) {
            return entry.id === id;
        })[0];
    };
    TrivialTreeBox.prototype.setSelectedEntry = function (entry, originalEvent, fireEvents) {
        if (fireEvents === void 0) { fireEvents = false; }
        this.selectedEntryId = entry && entry.id;
        this.markSelectedEntry(entry);
        if (fireEvents) {
            this.fireChangeEvents(entry);
        }
    };
    TrivialTreeBox.prototype.setSelectedEntryById = function (nodeId, reveal) {
        if (reveal === void 0) { reveal = false; }
        var entry = this.findEntryById(nodeId);
        this.setSelectedEntry(entry, null, false);
        if (reveal) {
            this.revealSelectedEntry(this.config.animationDuration > 0);
        }
        if (entry && this.config.expandOnSelection) {
            this.setNodeExpanded(entry, true, true);
        }
    };
    TrivialTreeBox.prototype.minimallyScrollTo = function ($entryWrapper) {
        $entryWrapper.querySelector(":scope > .tr-tree-entry-and-expander-wrapper").scrollIntoView({
            // behavior: "smooth",
            block: "nearest"
        });
    };
    TrivialTreeBox.prototype.markSelectedEntry = function (entry) {
        this.$tree.querySelectorAll(":scope .tr-selected-entry").forEach(function (e) { return e.classList.remove("tr-selected-entry"); });
        if (entry && entry.$element) {
            var $entryWrapper = entry.$element.querySelector(':scope > .tr-tree-entry-and-expander-wrapper');
            $entryWrapper.classList.add("tr-selected-entry");
        }
    };
    TrivialTreeBox.prototype.fireChangeEvents = function (entry) {
        this.onSelectedEntryChanged.fire(entry.entry);
    };
    TrivialTreeBox.prototype.selectNextEntry = function (direction, rollover, selectableOnly, matcher, fireEvents, originalEvent) {
        if (rollover === void 0) { rollover = false; }
        if (selectableOnly === void 0) { selectableOnly = true; }
        if (matcher === void 0) { matcher = function () { return true; }; }
        if (fireEvents === void 0) { fireEvents = false; }
        var nextMatchingEntry = this.getNextVisibleEntry(this.getSelectedEntryWrapper(), direction, rollover, selectableOnly, matcher);
        if (nextMatchingEntry != null) {
            this.setSelectedEntry(nextMatchingEntry, originalEvent, fireEvents);
            this.minimallyScrollTo(nextMatchingEntry.$element);
        }
        return nextMatchingEntry === null || nextMatchingEntry === void 0 ? void 0 : nextMatchingEntry.entry;
    };
    TrivialTreeBox.prototype.getNextVisibleEntry = function (currentEntry, direction, rollover, selectableOnly, entryMatcher) {
        var _this = this;
        if (selectableOnly === void 0) { selectableOnly = true; }
        if (entryMatcher === void 0) { entryMatcher = function () { return true; }; }
        var newSelectedElementIndex;
        var visibleEntriesAsList = this.findEntries(function (entry) {
            if (!entry.$element) {
                return false;
            }
            else {
                return ((!selectableOnly || _this.config.selectableDecider(entry.entry)) && entryMatcher(entry.entry)) || entry === currentEntry;
            }
        }, true);
        if (visibleEntriesAsList == null || visibleEntriesAsList.length == 0) {
            return null;
        }
        else if (currentEntry == null && direction > 0) {
            newSelectedElementIndex = -1 + direction;
        }
        else if (currentEntry == null && direction < 0) {
            newSelectedElementIndex = visibleEntriesAsList.length + direction;
        }
        else {
            var currentSelectedElementIndex = visibleEntriesAsList.indexOf(currentEntry);
            if (rollover) {
                newSelectedElementIndex = (currentSelectedElementIndex + visibleEntriesAsList.length + direction) % visibleEntriesAsList.length;
            }
            else {
                newSelectedElementIndex = Math.max(0, Math.min(visibleEntriesAsList.length - 1, currentSelectedElementIndex + direction));
            }
        }
        return visibleEntriesAsList[newSelectedElementIndex];
    };
    TrivialTreeBox.prototype.highlightTextMatches = function (searchString) {
        this.$tree.remove();
        for (var i = 0; i < this.entries.length; i++) {
            var entry = this.entries[i];
            var $entryElement = entry.$element.querySelector(':scope .tr-tree-entry');
            (0, highlight_1.highlightMatches)($entryElement, searchString, this.config.matchingOptions);
        }
        this.$componentWrapper.append(this.$tree);
    };
    TrivialTreeBox.prototype.getSelectedEntry = function () {
        var selectedEntryWrapper = this.getSelectedEntryWrapper();
        return selectedEntryWrapper != null ? selectedEntryWrapper.entry : null;
    };
    TrivialTreeBox.prototype.getSelectedEntryWrapper = function () {
        return (this.selectedEntryId !== undefined && this.selectedEntryId !== null) ? this.findEntryById(this.selectedEntryId) : null;
    };
    TrivialTreeBox.prototype.revealSelectedEntry = function (animate) {
        if (animate === void 0) { animate = false; }
        var selectedEntry = this.getSelectedEntryWrapper();
        if (!selectedEntry) {
            return;
        }
        var currentEntry = selectedEntry;
        while (currentEntry = currentEntry.parent) {
            this.setNodeExpanded(currentEntry, true, animate);
        }
        this.minimallyScrollTo(selectedEntry.$element);
    };
    TrivialTreeBox.prototype.setSelectedNodeExpanded = function (expanded) {
        var selectedEntry = this.getSelectedEntryWrapper();
        if (!selectedEntry || selectedEntry.isLeaf()) {
            return false;
        }
        else {
            var wasExpanded = selectedEntry.expanded;
            this.setNodeExpanded(selectedEntry, expanded, true);
            return !wasExpanded != !expanded;
        }
    };
    TrivialTreeBox.prototype.updateChildren = function (parentNodeId, children) {
        var node = this.findEntryById(parentNodeId);
        if (node) {
            this.setChildren(node, children);
        }
        else {
            console.error("Could not set the children of unknown node with id " + parentNodeId);
        }
    };
    ;
    TrivialTreeBox.prototype.updateNode = function (node, recursive) {
        if (recursive === void 0) { recursive = false; }
        var oldNode = this.findEntryById(this.config.idFunction(node));
        var shouldBeExpanded = node[this.config.expandedProperty];
        var expandedStateChanged = oldNode.expanded !== shouldBeExpanded;
        var parentNode = oldNode.parent;
        var newEntryWrapper;
        if (recursive) {
            newEntryWrapper = this.createEntryWrapper(node, parentNode);
        }
        else {
            oldNode.entry = node;
            var $entry = (0, teamapps_client_core_1.parseHtml)(this.config.entryRenderingFunction(node, oldNode.depth));
            $entry.classList.add("tr-tree-entry", "filterable-item");
            var $oldEntry = oldNode.$element.querySelector(":scope .tr-tree-entry");
            (0, teamapps_client_core_1.insertBefore)($entry, $oldEntry);
            $oldEntry.remove();
            newEntryWrapper = oldNode;
        }
        if (parentNode) {
            parentNode.children[parentNode.children.indexOf(oldNode)] = newEntryWrapper;
        }
        else {
            this.entries[this.entries.indexOf(oldNode)] = newEntryWrapper;
        }
        if (newEntryWrapper != oldNode) {
            (0, teamapps_client_core_1.insertAfter)(newEntryWrapper.render(), oldNode.$element);
            oldNode.$element.remove();
        }
        if (expandedStateChanged) {
            this.setNodeExpanded(newEntryWrapper, shouldBeExpanded, true);
        }
    };
    ;
    TrivialTreeBox.prototype.removeNode = function (nodeId) {
        var childNode = this.findEntryById(nodeId);
        if (childNode) {
            var parentNode = childNode.parent;
            if (parentNode) {
                parentNode.children.splice(parentNode.children.indexOf(childNode), 1);
            }
            else {
                this.entries.splice(this.entries.indexOf(childNode), 1);
            }
            childNode.$element.remove();
        }
    };
    ;
    TrivialTreeBox.prototype.addNode = function (parentNodeId, node) {
        var parentNode = this.findEntryById(parentNodeId);
        if (parentNode.children == null) {
            parentNode.children = [];
        }
        var newEntryWrapper = this.createEntryWrapper(node, parentNode);
        var $childrenWrapper = this.create$ChildrenWrapper(parentNode);
        if (parentNode.children.length === 0) {
            $childrenWrapper.innerHTML = ''; // remove the spinner!
        }
        parentNode.children.push(newEntryWrapper);
        $childrenWrapper.append(newEntryWrapper.render());
        parentNode.$element.classList.add('has-children');
    };
    ;
    TrivialTreeBox.prototype.addOrUpdateNode = function (parentNodeId, node, recursiveUpdate) {
        if (recursiveUpdate === void 0) { recursiveUpdate = false; }
        var existingNode = this.findEntryById(this.config.idFunction(node));
        if (existingNode != null) {
            this.updateNode(node, recursiveUpdate);
        }
        else {
            this.addNode(parentNodeId, node);
        }
    };
    TrivialTreeBox.prototype.setShowExpanders = function (showExpanders) {
        this.config.showExpanders = showExpanders;
        this.$componentWrapper.classList.toggle("hide-expanders", !showExpanders);
    };
    TrivialTreeBox.prototype.setAnimationDuration = function (animationDuration) {
        this.config.animationDuration = animationDuration;
    };
    TrivialTreeBox.prototype.setExpandOnSelection = function (expandOnSelection) {
        this.config.expandOnSelection = expandOnSelection;
    };
    TrivialTreeBox.prototype.setEnforceSingleExpandedPath = function (enforceSingleExpandedPath) {
        this.config.enforceSingleExpandedPath = enforceSingleExpandedPath;
        var selectedEntry = this.findEntryById(this.selectedEntryId);
        if (selectedEntry != null) {
            this.enforceSingleExpandedPath(selectedEntry);
        }
    };
    TrivialTreeBox.prototype.setIndentationWidth = function (indentationWidth) {
        this.config.indentation = indentationWidth;
    };
    TrivialTreeBox.prototype.destroy = function () {
        this.$componentWrapper.remove();
    };
    ;
    TrivialTreeBox.prototype.getMainDomElement = function () {
        return this.$componentWrapper;
    };
    return TrivialTreeBox;
}());
exports.TrivialTreeBox = TrivialTreeBox;
