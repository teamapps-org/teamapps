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
Object.defineProperty(exports, "__esModule", { value: true });
exports.highlightMatches = void 0;
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
/*
 *
 *  Copyright 2016 Yann Massard (https://github.com/yamass) and other contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
var TrivialCore_1 = require("../TrivialCore");
function highlightMatches(node, searchString, options) {
    var defaultOptions = {
        highlightClassName: 'tr-highlighted-text',
        matchingMode: 'contains',
        ignoreCase: true
    };
    options = __assign(__assign({}, defaultOptions), options);
    // remove old highlighting
    node.querySelectorAll('.' + options.highlightClassName).forEach(function (el) {
        var parent = el.parentNode;
        while (el.firstChild) {
            parent.insertBefore(el.firstChild, el);
        }
        parent.removeChild(el);
    });
    node.querySelectorAll(':scope *').forEach(function (el) {
        el.normalize();
        if (searchString && searchString !== '') {
            Array.from(el.children)
                .filter(function (child) {
                return child.nodeType == 3 && (0, TrivialCore_1.trivialMatch)(el.nodeValue, searchString, options).length > 0;
            })
                .forEach(function (el) {
                var oldNodeValue = (el.nodeValue || "");
                var newNodeValue = "";
                var matches = (0, TrivialCore_1.trivialMatch)(oldNodeValue, searchString, options);
                var oldMatchEnd = 0;
                for (var i = 0; i < matches.length; i++) {
                    var match = matches[i];
                    newNodeValue += oldNodeValue.substring(oldMatchEnd, match.start);
                    newNodeValue += "<span class=\"" + options.highlightClassName + "\">" + oldNodeValue.substr(match.start, match.length) + "</span>";
                    oldMatchEnd = match.start + match.length;
                }
                newNodeValue += oldNodeValue.substring(oldMatchEnd, oldNodeValue.length);
                el.nodeValue = newNodeValue;
            });
        }
    });
}
exports.highlightMatches = highlightMatches;
