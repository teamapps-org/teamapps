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
Object.defineProperty(exports, "__esModule", { value: true });
exports.ItemView = void 0;
var teamapps_client_core_1 = require("teamapps-client-core");
var TrivialTreeBox_1 = require("./trivial-components/TrivialTreeBox");
var TrivialCore_1 = require("./trivial-components/TrivialCore");
var ItemView = /** @class */ (function (_super) {
    __extends(ItemView, _super);
    function ItemView(config) {
        var _this = _super.call(this, config) || this;
        _this.onItemClicked = new teamapps_client_core_1.TeamAppsEvent();
        _this.groupsByGroupId = {};
        _this.filterString = "";
        _this.$itemView = (0, teamapps_client_core_1.parseHtml)('<div class="UiItemView"></div>');
        _this.$itemView.style.padding = config.verticalPadding + "px " + config.horizontalPadding + "px";
        _this.$itemView.classList.add("background-mode-" + config.itemBackgroundMode);
        config.itemGroups.forEach(function (group) {
            _this.addItemGroup(group);
        });
        _this.setFilter(config.filter);
        return _this;
    }
    ItemView.prototype.doGetMainElement = function () {
        return this.$itemView;
    };
    ItemView.prototype.addItemGroup = function (itemGroupConfig) {
        if (this.groupsByGroupId[itemGroupConfig.id]) {
            this.removeItemGroup(itemGroupConfig.id);
        }
        var itemGroup = this.createItemGroup(itemGroupConfig);
        this.groupsByGroupId[itemGroupConfig.id] = itemGroup;
        this.$itemView.appendChild(itemGroup.getMainDomElement());
        return itemGroup;
    };
    ItemView.prototype.createItemGroup = function (itemGroupConfig) {
        var _this = this;
        var itemGroup = new ItemGroup(this, itemGroupConfig, this.config.groupHeaderTemplate);
        itemGroup.onItemClicked.addListener(function (item) { return _this.onItemClicked.fire({
            groupId: itemGroupConfig.id,
            itemId: item.id
        }); });
        itemGroup.getMainDomElement().style.paddingBottom = this.config.groupSpacing + "px";
        itemGroup.setFilter(this.filterString);
        return itemGroup;
    };
    ItemView.prototype.refreshItemGroup = function (itemGroupConfig) {
        var oldGroup = this.groupsByGroupId[itemGroupConfig.id];
        if (!oldGroup) {
            console.error("Could not refresh non-existing group \"".concat(itemGroupConfig.id, "\"!"));
            return;
        }
        var newGroup = this.createItemGroup(itemGroupConfig);
        this.groupsByGroupId[itemGroupConfig.id] = newGroup;
        oldGroup.getMainDomElement().parentElement.insertBefore(newGroup.getMainDomElement(), oldGroup.getMainDomElement());
        oldGroup.getMainDomElement().remove();
    };
    ItemView.prototype.removeItemGroup = function (groupId) {
        var itemGroup = this.groupsByGroupId[groupId];
        itemGroup.getMainDomElement().remove();
        delete this.groupsByGroupId[groupId];
    };
    ItemView.prototype.addItem = function (groupId, item) {
        var itemGroup = this.groupsByGroupId[groupId];
        if (!itemGroup) {
            console.error("Cannot find group ".concat(groupId, " in UiItemView ") + this.config.id);
            return;
        }
        itemGroup.addItem(item);
    };
    ItemView.prototype.removeItem = function (groupId, itemId) {
        var itemGroup = this.groupsByGroupId[groupId];
        if (!itemGroup) {
            console.error("Cannot find group ".concat(groupId, " in UiItemView ") + this.config.id);
            return;
        }
        itemGroup.removeItem(itemId);
    };
    ItemView.prototype.setFilter = function (filter) {
        this.filterString = filter;
        this.filter();
    };
    ItemView.prototype.filter = function () {
        var _this = this;
        Object.keys(this.groupsByGroupId).forEach(function (groupId) {
            var group = _this.groupsByGroupId[groupId];
            group.setFilter(_this.filterString);
        });
    };
    ItemView.prototype.setGroupHeaderTemplate = function (groupHeaderTemplate) {
        this.config.groupHeaderTemplate = groupHeaderTemplate;
    };
    ItemView.prototype.setHorizontalPadding = function (horizontalPadding) {
        throw new Error("Method not implemented.");
    };
    ItemView.prototype.setVerticalPadding = function (verticalPadding) {
        throw new Error("Method not implemented.");
    };
    ItemView.prototype.setGroupSpacing = function (groupSpacing) {
        throw new Error("Method not implemented.");
    };
    ItemView.prototype.setItemBackgroundMode = function (itemBackgroundMode) {
        throw new Error("Method not implemented.");
    };
    ItemView.prototype.destroy = function () {
        var _this = this;
        _super.prototype.destroy.call(this);
        Object.keys(this.groupsByGroupId).forEach(function (groupId) {
            var group = _this.groupsByGroupId[groupId];
            group.destroy();
        });
    };
    return ItemView;
}(teamapps_client_core_1.AbstractComponent));
exports.ItemView = ItemView;
var ItemGroup = /** @class */ (function () {
    function ItemGroup(itemView, config, groupHeaderTemplateRenderer) {
        var _this = this;
        this.itemView = itemView;
        this.config = config;
        this.onItemClicked = new teamapps_client_core_1.TeamAppsEvent();
        this.items = config.items;
        var groupHtmlId = "item-group-".concat((0, teamapps_client_core_1.generateUUID)());
        this.$itemGroup = (0, teamapps_client_core_1.parseHtml)('<div class="item-group" id="' + groupHtmlId + '">');
        var buttonWidthCssValue;
        if (config.buttonWidth < 0) {
            buttonWidthCssValue = 'auto';
        }
        else if (config.buttonWidth === 0) {
            buttonWidthCssValue = '100%';
        }
        else if (config.buttonWidth < 1) {
            buttonWidthCssValue = (config.buttonWidth * 100) + '%';
        }
        else {
            buttonWidthCssValue = config.buttonWidth + 'px';
        }
        this.$itemGroup.append((0, teamapps_client_core_1.parseHtml)("<style>\n            #".concat(groupHtmlId, " .tr-tree-entryTree {\n               justify-content: ").concat(config.itemJustification, ";\n            }\n            #").concat(groupHtmlId, " .tr-tree-entryTree .tr-tree-entry-outer-wrapper {\n               margin: ").concat(config.verticalItemMargin, "px ").concat(config.horizontalItemMargin, "px;\n               width: ").concat(buttonWidthCssValue, ";\n            }\n            </style>")));
        var $itemGroupHeader = (0, teamapps_client_core_1.parseHtml)('<div class="item-group-header">');
        this.$itemGroup.appendChild($itemGroupHeader);
        if (!config.headerVisible) {
            $itemGroupHeader.classList.add("hidden");
        }
        if (groupHeaderTemplateRenderer && config.headerData) {
            $itemGroupHeader.append((0, teamapps_client_core_1.parseHtml)(groupHeaderTemplateRenderer.render(config.headerData.values)));
        }
        var $itemContainer = (0, teamapps_client_core_1.parseHtml)('<div class="item-container">');
        this.$itemGroup.appendChild($itemContainer);
        $itemContainer.classList.add(config.floatStyle);
        $itemContainer.style.padding = config.verticalPadding + "px " + config.horizontalPadding + "px";
        this.itemTemplate = config.itemTemplate;
        this.trivialTreeBox = new TrivialTreeBox_1.TrivialTreeBox({
            entryRenderingFunction: function (entry) { return _this.itemTemplate.render(entry.values); },
            spinnerTemplate: TrivialCore_1.DEFAULT_TEMPLATES.defaultSpinnerTemplate,
            entries: config.items,
            idFunction: function (entry) { return entry && entry.id; }
        });
        $itemContainer.append(this.trivialTreeBox.getMainDomElement());
        this.trivialTreeBox.onSelectedEntryChanged.addListener(function () {
            _this.onItemClicked.fire(_this.trivialTreeBox.getSelectedEntry());
        });
    }
    ItemGroup.prototype.setFilter = function (filterString) {
        this.filterString = filterString;
        this.filter();
    };
    ItemGroup.prototype.filter = function () {
        var matchingElements = this.filterItems(this.filterString);
        this.trivialTreeBox.setEntries(matchingElements);
        if (matchingElements.length < 100) {
            this.trivialTreeBox.highlightTextMatches(this.filterString);
        }
        this.$itemGroup.classList.toggle("hidden", matchingElements.length === 0);
    };
    ItemGroup.prototype.filterItems = function (queryString) {
        if (!queryString) {
            return this.items;
        }
        return this.items.filter(function (item) {
            return item.asString.indexOf(queryString) > -1;
        });
    };
    ItemGroup.prototype.getMainDomElement = function () {
        return this.$itemGroup;
    };
    ItemGroup.prototype.addItem = function (item) {
        this.items.push(item);
        this.filter();
    };
    ItemGroup.prototype.removeItem = function (itemId) {
        this.items = this.items.filter(function (e) { return e.id !== itemId; });
        this.filter();
    };
    ItemGroup.prototype.destroy = function () {
        this.trivialTreeBox.destroy();
    };
    return ItemGroup;
}());
