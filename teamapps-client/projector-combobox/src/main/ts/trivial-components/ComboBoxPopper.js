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
exports.positionDropdown = exports.positionDropdownWithAutoUpdate = void 0;
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
var dom_1 = require("@floating-ui/dom");
var DEFAULT_OPTIONS = {
    padding: 5,
    strategy: 'fixed',
};
function positionDropdownWithAutoUpdate(reference, dropdown, options) {
    if (options === void 0) { options = {}; }
    options = __assign(__assign({}, DEFAULT_OPTIONS), options);
    return (0, dom_1.autoUpdate)(reference, dropdown, function () { return positionDropdown(reference, dropdown, options); });
}
exports.positionDropdownWithAutoUpdate = positionDropdownWithAutoUpdate;
function positionDropdown(reference, dropdown, options) {
    if (options === void 0) { options = {}; }
    options = __assign(__assign({}, DEFAULT_OPTIONS), options);
    (0, dom_1.computePosition)(reference, dropdown, {
        placement: 'bottom-start',
        strategy: options.strategy,
        middleware: [
            (0, dom_1.hide)(),
            (0, dom_1.flip)(),
            (0, dom_1.size)({
                apply: function (_a) {
                    var rects = _a.rects, elements = _a.elements, availableHeight = _a.availableHeight, placement = _a.placement;
                    // const isFlipped = placement.indexOf('bottom') === -1;
                    Object.assign(elements.floating.style, {
                        width: "".concat(rects.reference.width, "px"),
                        maxHeight: "".concat(Math.ceil(availableHeight - options.padding), "px")
                    });
                }
            }),
            (0, dom_1.shift)({ padding: 5 }),
            {
                name: 'detectOverflow',
                fn: function (state) {
                    var _a;
                    return __awaiter(this, void 0, void 0, function () {
                        var sideObject;
                        return __generator(this, function (_b) {
                            switch (_b.label) {
                                case 0: return [4 /*yield*/, (0, dom_1.detectOverflow)(state, { elementContext: "reference" })];
                                case 1:
                                    sideObject = _b.sent();
                                    if (sideObject.left > state.rects.reference.width || sideObject.right > state.rects.reference.width
                                        || sideObject.top > state.rects.reference.height || sideObject.bottom > state.rects.reference.height) {
                                        (_a = options.referenceOutOfViewPortHandler) === null || _a === void 0 ? void 0 : _a.call(options);
                                    }
                                    return [2 /*return*/, {}];
                            }
                        });
                    });
                },
            }
        ],
    }).then(function (values) {
        console.log(values.x, values.y);
        Object.assign(dropdown.style, {
            left: "".concat((values.x), "px"),
            top: "".concat((values.y), "px"),
            visibility: values.middlewareData.hide.referenceHidden ? 'hidden' : null,
            pointerEvents: values.middlewareData.hide.referenceHidden ? 'none' : null
        });
    });
}
exports.positionDropdown = positionDropdown;
