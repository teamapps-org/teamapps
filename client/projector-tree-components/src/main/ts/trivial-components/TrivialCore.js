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
var __spreadArray = (this && this.__spreadArray) || function (to, from, pack) {
    if (pack || arguments.length === 2) for (var i = 0, l = from.length, ar; i < l; i++) {
        if (ar || !(i in from)) {
            if (!ar) ar = Array.prototype.slice.call(from, 0, i);
            ar[i] = from[i];
        }
    }
    return to.concat(ar || Array.prototype.slice.call(from));
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateUUID = exports.setTimeoutOrDoImmediately = exports.trivialMatch = exports.objectEquals = exports.escapeSpecialRegexCharacter = exports.defaultTreeQueryFunctionFactory = exports.defaultEntryMatchingFunctionFactory = exports.unProxyEntry = exports.createProxy = exports.extractValue = exports.defaultListQueryFunctionFactory = exports.wrapWithDefaultTagWrapper = exports.DEFAULT_TEMPLATES = exports.DEFAULT_RENDERING_FUNCTIONS = exports.isSpecialKey = exports.isModifierKey = void 0;
var fastest_levenshtein_1 = require("fastest-levenshtein");
var modifierKeys = ["Alt", "AltGraph", "CapsLock", "Control", "Fn", "Shift", "Hyper", "Meta", "NumLock", "ScrollLock", "Super", "Symbol", "SymbolLock"];
var specialKeys = __spreadArray(__spreadArray([], modifierKeys, true), ["Tab", "Enter", "Delete", "Escape", "PageUp", "PageDown", "Home"], false);
function isModifierKey(e) {
    return modifierKeys.indexOf(e.key) != -1;
}
exports.isModifierKey = isModifierKey;
function isSpecialKey(e) {
    return specialKeys.indexOf(e.key) != -1;
}
exports.isSpecialKey = isSpecialKey;
exports.DEFAULT_RENDERING_FUNCTIONS = {
    currencySingleLineShort: function (entry) {
        entry = entry || {};
        return "<div class=\"tr-template-currency-single-line-short\">\n          <div class=\"content-wrapper tr-editor-area\"> \n            <div>".concat(entry.symbol != null ? "<span class=\"currency-symbol\">".concat(entry.symbol || '', "</span>") : '', " ").concat(entry.code != null ? "<span class=\"currency-code\">".concat(entry.code || '', "</span>") : '', "</div> \n          </div>\n        </div>");
    },
    currency2Line: function (entry) {
        entry = entry || {};
        return "<div class=\"tr-template-currency-2-lines\">\n          <div class=\"content-wrapper tr-editor-area\"> \n            <div class=\"main-line\">\n              <span class=\"currency-code\">".concat(entry.code || '', "</span>\n              <span class=\"currency-name\">").concat(entry.name || '', "</span>\n            </div> \n            <div class=\"additional-info\">\n              <span class=\"currency-symbol\">").concat(entry.symbol || '', "</span>&nbsp;\n              ").concat(entry.exchangeRate != null ? "<div class=\"exchange\"> = <span class=\"exchange-rate\">".concat(entry.exchangeRate || '', "</span> <span class=\"exchange-rate-base\">").concat(entry.exchangeRateBase || '', "</span></div>") : '', "\n            </div>\n          </div>\n        </div>");
    }
};
exports.DEFAULT_TEMPLATES = {
    defaultSpinnerTemplate: "<div class=\"tr-default-spinner\"><div class=\"spinner\"></div><div>Fetching data...</div></div>",
    defaultNoEntriesTemplate: "<div class=\"tr-default-no-data-display\"><div>No matching entries...</div></div>"
};
function wrapWithDefaultTagWrapper(entryHtml, deleteButton) {
    if (deleteButton === void 0) { deleteButton = true; }
    return ("<div class=\"tr-tagcombobox-default-wrapper-template\">\n        <div class=\"tr-tagcombobox-tag-content\">\n            ".concat(entryHtml, "\n        </div>\n        ").concat(deleteButton ? '<div class="tr-remove-button"></div>' : '', "\n    </div>"));
}
exports.wrapWithDefaultTagWrapper = wrapWithDefaultTagWrapper;
function defaultListQueryFunctionFactory(entries, properties, matchingOptions) {
    function filterElements(queryString) {
        var visibleEntries = [];
        for (var i = 0; i < entries.length; i++) {
            var entry = entries[i];
            for (var j = 0; j < properties.length; j++) {
                var propertyValue = extractValue(entry, properties[j]);
                if (!queryString || trivialMatch(propertyValue, queryString, matchingOptions).length > 0) {
                    visibleEntries.push(entry);
                    break;
                }
            }
        }
        return visibleEntries;
    }
    return function (queryString) {
        return filterElements(queryString);
    };
}
exports.defaultListQueryFunctionFactory = defaultListQueryFunctionFactory;
function extractValue(entry, property) {
    if (entry == null) {
        return null;
    }
    else if (typeof property === 'string') {
        return entry[property];
    }
    else if (typeof property === 'function') {
        return property(entry);
    }
}
exports.extractValue = extractValue;
function createProxy(delegate) {
    var proxyConstructor = function () {
        this._trProxyDelegate = delegate;
    };
    proxyConstructor.prototype = delegate;
    var proxyConstructorTypescriptHack = proxyConstructor;
    return new proxyConstructorTypescriptHack();
}
exports.createProxy = createProxy;
function unProxyEntry(entry) {
    return entry != null ? (entry._trProxyDelegate || entry) : null;
}
exports.unProxyEntry = unProxyEntry;
function defaultEntryMatchingFunctionFactory(searchedPropertyNames, matchingOptions) {
    return function (entry, queryString, depth) {
        return searchedPropertyNames
            .some(function (propertyName) {
            var value = entry[propertyName];
            return value != null && trivialMatch(value.toString(), queryString, matchingOptions).length > 0;
        });
    };
}
exports.defaultEntryMatchingFunctionFactory = defaultEntryMatchingFunctionFactory;
function defaultTreeQueryFunctionFactory(topLevelEntries, entryMatchingFunction, childrenPropertyName, expandedPropertyName) {
    function findMatchingEntriesAndTheirAncestors(entry, queryString, nodeDepth) {
        var entryProxy = createProxy(entry);
        entryProxy[childrenPropertyName] = [];
        entryProxy[expandedPropertyName] = false;
        if (entry[childrenPropertyName]) {
            for (var i = 0; i < entry[childrenPropertyName].length; i++) {
                var child = entry[childrenPropertyName][i];
                var childProxy = findMatchingEntriesAndTheirAncestors(child, queryString, nodeDepth + 1);
                if (childProxy) {
                    entryProxy[childrenPropertyName].push(childProxy);
                    entryProxy[expandedPropertyName] = true;
                }
            }
        }
        var hasMatchingChildren = entryProxy[childrenPropertyName].length > 0;
        var matchesItself = entryMatchingFunction(entry, queryString, nodeDepth);
        if (matchesItself && !hasMatchingChildren) {
            // still make it expandable!
            entryProxy[childrenPropertyName] = entry[childrenPropertyName];
        }
        return matchesItself || hasMatchingChildren ? entryProxy : null;
    }
    return function (queryString) {
        var theTopLevelEntries = typeof topLevelEntries === 'function' ? topLevelEntries() : topLevelEntries;
        if (!queryString) {
            return theTopLevelEntries;
        }
        else {
            var matchingEntries = [];
            for (var i = 0; i < theTopLevelEntries.length; i++) {
                var topLevelEntry = theTopLevelEntries[i];
                var entryProxy = findMatchingEntriesAndTheirAncestors(topLevelEntry, queryString, 0);
                if (entryProxy) {
                    matchingEntries.push(entryProxy);
                }
            }
            return matchingEntries;
        }
    };
}
exports.defaultTreeQueryFunctionFactory = defaultTreeQueryFunctionFactory;
var escapeSpecialRegexCharacter = function (s) {
    return s.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
};
exports.escapeSpecialRegexCharacter = escapeSpecialRegexCharacter;
// see http://stackoverflow.com/a/27014537/524913
function objectEquals(x, y) {
    'use strict';
    if (x === null || x === undefined || y === null || y === undefined) {
        return x === y;
    }
    if (x.constructor !== y.constructor) {
        return false;
    }
    if (x instanceof Function) {
        return x === y;
    }
    if (x instanceof RegExp) {
        return x === y;
    }
    if (x === y || x.valueOf() === y.valueOf()) {
        return true;
    }
    if (Array.isArray(x) && x.length !== y.length) {
        return false;
    }
    if (x instanceof Date) {
        return false;
    }
    if (!(x instanceof Object)) {
        return false;
    }
    if (!(y instanceof Object)) {
        return false;
    }
    var p = Object.keys(x);
    return Object.keys(y).every(function (i) {
        return p.indexOf(i) !== -1;
    }) &&
        p.every(function (i) {
            return objectEquals(x[i], y[i]);
        });
}
exports.objectEquals = objectEquals;
/**
 * @param text
 * @param searchString
 * @param options matchingMode: 'prefix', 'prefix-word', 'contain', 'prefix-levenshtein', 'levenshtein';
 *        ignoreCase: boolean
 *        maxLevenshteinDistance: integer (number) - only for levenshtein
 * @returns array of matchers {start, length, distance}
 */
function trivialMatch(text, searchString, options) {
    if (text == null) {
        return [];
    }
    if (typeof text !== "string" /* non-typescript or "any" usage */) {
        text = "" + text;
    }
    if (!searchString) {
        return [{
                start: 0,
                length: text.length
            }];
    }
    options = __assign({ matchingMode: 'contains', ignoreCase: true, maxLevenshteinDistance: 3 }, options);
    if (options.ignoreCase) {
        text = text.toLowerCase();
        searchString = searchString.toLowerCase();
    }
    function findRegexMatches(regex) {
        var matches = [];
        var match;
        while (match = regex.exec(text)) {
            matches.push({
                start: match.index,
                length: match[0].length
            });
        }
        return matches;
    }
    function findLevenshteinMatches(text, searchString) {
        var distance = (0, fastest_levenshtein_1.distance)(text, searchString);
        if (distance <= options.maxLevenshteinDistance) {
            return [{
                    start: 0,
                    length: searchString.length,
                    distance: distance
                }];
        }
        else {
            return [];
        }
    }
    if (options.matchingMode == 'contains') {
        searchString = searchString.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&"); // escape all regex special chars
        return findRegexMatches(new RegExp(searchString, "g"));
    }
    else if (options.matchingMode == 'prefix') {
        searchString = searchString.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&"); // escape all regex special chars
        return findRegexMatches(new RegExp('^' + searchString, "g"));
    }
    else if (options.matchingMode == 'prefix-word') {
        // ATTENTION: IF YOU CHANGE THIS, MAKE SURE TO EXECUTE THE UNIT TESTS!!
        searchString = searchString.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&"); // escape all regex special chars
        if (searchString.charAt(0).match(/^\w/)) {
            return findRegexMatches(new RegExp('\\b' + searchString, "g"));
        }
        else {
            // search string starts with a non-word character, so \b will possibly not match!
            // After all, we cannot really decide, what is meant to be a word boundary in this context
            // (e.g.: "12€" with searchString "€"), so we fall back to "contains" mode.
            return findRegexMatches(new RegExp(searchString, "g"));
        }
    }
    else if (options.matchingMode == 'prefix-levenshtein') {
        return findLevenshteinMatches(text.substr(0, Math.min(searchString.length, text.length)), searchString);
    }
    else if (options.matchingMode == 'levenshtein') {
        return findLevenshteinMatches(text, searchString);
    }
    else {
        throw "unknown matchingMode: " + options.matchingMode;
    }
}
exports.trivialMatch = trivialMatch;
function setTimeoutOrDoImmediately(f, delay) {
    if (delay != null && delay > 0) {
        return window.setTimeout(f(), delay);
    }
    else {
        return void f();
    }
}
exports.setTimeoutOrDoImmediately = setTimeoutOrDoImmediately;
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}
exports.generateUUID = generateUUID;
