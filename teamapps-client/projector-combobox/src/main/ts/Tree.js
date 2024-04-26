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
var __runInitializers = (this && this.__runInitializers) || function (thisArg, initializers, value) {
    var useValue = arguments.length > 2;
    for (var i = 0; i < initializers.length; i++) {
        value = useValue ? initializers[i].call(thisArg, value) : initializers[i].call(thisArg);
    }
    return useValue ? value : void 0;
};
var __esDecorate = (this && this.__esDecorate) || function (ctor, descriptorIn, decorators, contextIn, initializers, extraInitializers) {
    function accept(f) { if (f !== void 0 && typeof f !== "function") throw new TypeError("Function expected"); return f; }
    var kind = contextIn.kind, key = kind === "getter" ? "get" : kind === "setter" ? "set" : "value";
    var target = !descriptorIn && ctor ? contextIn["static"] ? ctor : ctor.prototype : null;
    var descriptor = descriptorIn || (target ? Object.getOwnPropertyDescriptor(target, contextIn.name) : {});
    var _, done = false;
    for (var i = decorators.length - 1; i >= 0; i--) {
        var context = {};
        for (var p in contextIn) context[p] = p === "access" ? {} : contextIn[p];
        for (var p in contextIn.access) context.access[p] = contextIn.access[p];
        context.addInitializer = function (f) { if (done) throw new TypeError("Cannot add initializers after decoration has completed"); extraInitializers.push(accept(f || null)); };
        var result = (0, decorators[i])(kind === "accessor" ? { get: descriptor.get, set: descriptor.set } : descriptor[key], context);
        if (kind === "accessor") {
            if (result === void 0) continue;
            if (result === null || typeof result !== "object") throw new TypeError("Object expected");
            if (_ = accept(result.get)) descriptor.get = _;
            if (_ = accept(result.set)) descriptor.set = _;
            if (_ = accept(result.init)) initializers.unshift(_);
        }
        else if (_ = accept(result)) {
            if (kind === "field") initializers.unshift(_);
            else descriptor[key] = _;
        }
    }
    if (target) Object.defineProperty(target, contextIn.name, descriptor);
    done = true;
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
var _this = this;
Object.defineProperty(exports, "__esModule", { value: true });
exports.Tree = void 0;
var teamapps_client_core_1 = require("teamapps-client-core");
var TrivialTree_1 = require("./trivial-components/TrivialTree");
var teamapps_client_core_components_1 = require("teamapps-client-core-components");
var Tree = exports.Tree = function () {
    var _a;
    var _instanceExtraInitializers = [];
    var _replaceNodes_decorators;
    return _a = /** @class */ (function (_super) {
            __extends(Tree, _super);
            function Tree(config) {
                var _this = _super.call(this, config) || this;
                _this.onNodeSelected = (__runInitializers(_this, _instanceExtraInitializers), new teamapps_client_core_1.TeamAppsEvent());
                _this.$panel = (0, teamapps_client_core_1.parseHtml)('<div class="UiTree">');
                _this.nodes = config.initialData;
                _this.trivialTree = new TrivialTree_1.TrivialTree({
                    entries: (0, teamapps_client_core_components_1.buildObjectTree)(config.initialData, "id", "parentId"),
                    selectedEntryId: config.selectedNodeId,
                    childrenProperty: "__children",
                    expandedProperty: "expanded",
                    entryRenderingFunction: function (entry) { return _this.renderRecord(entry); },
                    lazyChildrenFlag: 'lazyChildren',
                    lazyChildrenQueryFunction: function (node) { return __awaiter(_this, void 0, void 0, function () { return __generator(this, function (_a) {
                        switch (_a.label) {
                            case 0: return [4 /*yield*/, this.config.lazyLoadChildren({ parentNodeId: node && node.id })];
                            case 1: return [2 /*return*/, _a.sent()];
                        }
                    }); }); },
                    spinnerTemplate: "<div class=\"UiSpinner\" style=\"height: 20px; width: 20px; margin: 4px auto 4px auto;\"></div>",
                    showExpanders: config.expandersVisible,
                    expandOnSelection: config.expandOnSelection,
                    enforceSingleExpandedPath: config.enforceSingleExpandedPath,
                    idFunction: function (entry) { return entry && entry.id; },
                    indentation: config.indentation,
                    animationDuration: config.expandAnimationEnabled ? 120 : 0,
                    directSelectionViaArrowKeys: true
                });
                _this.trivialTree.onSelectedEntryChanged.addListener(function (entry) {
                    _this.onNodeSelected.fire({
                        nodeId: entry.id
                    });
                });
                _this.$panel.appendChild(_this.trivialTree.getMainDomElement());
                if (config.selectedNodeId != null) {
                    _this.trivialTree.getTreeBox().revealSelectedEntry(false);
                }
                return _this;
            }
            Tree.prototype.renderRecord = function (record) {
                var template = record.displayTemplate;
                if (template != null) {
                    return template.render(record.values);
                }
                else {
                    return "<div class=\"string-template\">".concat(record.asString, "</div>");
                }
            };
            Tree.prototype.doGetMainElement = function () {
                return this.$panel;
            };
            Tree.prototype.setExpandersVisible = function (expandersVisible) {
                this.trivialTree.setShowExpanders(expandersVisible);
            };
            Tree.prototype.setExpandAnimationEnabled = function (expandAnimationEnabled) {
                this.trivialTree.setAnimationDuration(expandAnimationEnabled ? 120 : 0);
            };
            Tree.prototype.setExpandOnSelection = function (expandOnSelection) {
                this.trivialTree.setExpandOnSelection(expandOnSelection);
            };
            Tree.prototype.setEnforceSingleExpandedPath = function (enforceSingleExpandedPath) {
                this.trivialTree.setEnforceSingleExpandedPath(enforceSingleExpandedPath);
            };
            Tree.prototype.setIndentation = function (indentation) {
                this.trivialTree.setIndentationWidth(indentation);
            };
            Tree.prototype.setSelectedNodeId = function (selectedNodeId, reveal) {
                this.trivialTree.setSelectedEntryById(selectedNodeId, reveal);
            };
            Tree.prototype.replaceNodes = function (nodes) {
                this.nodes = nodes;
                this.trivialTree.updateEntries((0, teamapps_client_core_components_1.buildObjectTree)(nodes, "id", "parentId"));
            };
            Tree.prototype.bulkUpdate = function (nodesToBeRemoved, nodesToBeAdded) {
                var _a;
                var _this = this;
                this.nodes = this.nodes.filter(function (node) { return nodesToBeRemoved.indexOf(node.id) === -1; });
                (_a = this.nodes).push.apply(_a, nodesToBeAdded);
                nodesToBeRemoved.forEach(function (nodeId) { return _this.trivialTree.removeNode(nodeId); });
                nodesToBeAdded.forEach(function (node) { return _this.trivialTree.addOrUpdateNode(node.parentId, node, false); });
            };
            return Tree;
        }(teamapps_client_core_1.AbstractComponent)),
        (function () {
            _replaceNodes_decorators = [(0, teamapps_client_core_1.loadSensitiveThrottling)(100, 10, 3000)];
            __esDecorate(_a, null, _replaceNodes_decorators, { kind: "method", name: "replaceNodes", static: false, private: false, access: { has: function (obj) { return "replaceNodes" in obj; }, get: function (obj) { return obj.replaceNodes; } } }, null, _instanceExtraInitializers);
        })(),
        _a;
}();
