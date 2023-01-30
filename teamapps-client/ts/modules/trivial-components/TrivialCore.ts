/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import * as Levenshtein from "levenshtein";
import KeyboardEventBase = JQuery.KeyboardEventBase;

export interface TrivialComponent {
	getMainDomElement(): HTMLElement;

	destroy(): void;
}

export type EditingMode = 'editable' | 'disabled' | 'readonly';

export type MatchingOptions = {
	matchingMode?: 'contains' | 'prefix' | 'prefix-word' | 'prefix-levenshtein' | 'levenshtein',
	ignoreCase?: boolean,
	maxLevenshteinDistance?: number
};

export type Match = {
	start: number,
	length: number,
	distance?: number
};

export type HighlightDirection = number | null | undefined;
export type NavigationDirection = "up" | "left" | "down" | "right";
export type QueryFunction<E> = (queryString: string) => E[] | Promise<E[]>;

/**
 * Used to render an entry.
 *
 * @param entry the entry to render
 * @return HTML string
 */
export type RenderingFunction<E> = (entry: E) => string;

export const keyCodes = {
	backspace: 8,
	tab: 9,
	enter: 13,
	shift: 16,
	ctrl: 17,
	alt: 18,
	pause: 19,
	caps_lock: 20,
	escape: 27,
	space: 32,
	page_up: 33,
	page_down: 34,
	end: 35,
	home: 36,
	left_arrow: 37,
	up_arrow: 38,
	right_arrow: 39,
	down_arrow: 40,
	insert: 45,
	"delete": 46,
	left_window_key: 91,
	right_window_key: 92,
	select_key: 93,
	num_lock: 144,
	scroll_lock: 145,
	specialKeys: [8, 9, 13, 16, 17, 18, 19, 20, 27, 33, 34, 35, 36, 37, 38, 39, 40, 45, 46, 91, 92, 93, 144, 145],
	numberKeys: [48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105],
	isSpecialKey: function (keyCode: number) {
		return this.specialKeys.indexOf(keyCode) != -1;
	},
	isDigitKey: function (keyCode: number) {
		return this.numberKeys.indexOf(keyCode) != -1;
	},
	isModifierKey(e: KeyboardEventBase | KeyboardEvent) {
		return [keyCodes.shift, keyCodes.caps_lock, keyCodes.alt, keyCodes.ctrl, keyCodes.left_window_key, keyCodes.right_window_key]
			.indexOf(e.which) != -1;
	}
};

export interface DefaultEntryStructure {
	imageUrl?: string,
	displayValue?: string,
	additionalInfo?: string,
	statusColor?: string
}

export interface DefaultCurrencyEntryStructure {
	symbol?: string,
	code?: string,
	name?: string,
	exchangeRate?: string | number,
	exchangeRateBase?: string
}

export const DEFAULT_RENDERING_FUNCTIONS = {
	currencySingleLineShort: (entry: DefaultCurrencyEntryStructure) => {
		entry = entry || {};
		return `<div class="tr-template-currency-single-line-short">
          <div class="content-wrapper tr-editor-area"> 
            <div>${entry.symbol != null ? `<span class="currency-symbol">${entry.symbol || ''}</span>` : ''} ${entry.code != null ? `<span class="currency-code">${entry.code || ''}</span>` : ''}</div> 
          </div>
        </div>`;
	},
	currency2Line: (entry: DefaultCurrencyEntryStructure) => {
		entry = entry || {};
		return `<div class="tr-template-currency-2-lines">
          <div class="content-wrapper tr-editor-area"> 
            <div class="main-line">
              <span class="currency-code">${entry.code || ''}</span>
              <span class="currency-name">${entry.name || ''}</span>
            </div> 
            <div class="additional-info">
              <span class="currency-symbol">${entry.symbol || ''}</span>&nbsp;
              ${entry.exchangeRate != null ? `<div class="exchange"> = <span class="exchange-rate">${entry.exchangeRate || ''}</span> <span class="exchange-rate-base">${entry.exchangeRateBase || ''}</span></div>` : ''}
            </div>
          </div>
        </div>`;
	}
};

export const DEFAULT_TEMPLATES = {
	defaultSpinnerTemplate: `<div class="tr-default-spinner"><div class="spinner"></div><div>Fetching data...</div></div>`,
	defaultNoEntriesTemplate: `<div class="tr-default-no-data-display"><div>No matching entries...</div></div>`
};

export function wrapWithDefaultTagWrapper(entryHtml: string, deleteButton = true) {
	return (`<div class="tr-tagcombobox-default-wrapper-template">
        <div class="tr-tagcombobox-tag-content">
            ${entryHtml}
        </div>
        ${deleteButton ? '<div class="tr-remove-button"></div>' : ''}
    </div>`);
}

export function defaultListQueryFunctionFactory<E>(entries: E[], properties: (string | ((entry: E) => any))[], matchingOptions: MatchingOptions): QueryFunction<E> {
	function filterElements(queryString: string): E[] {
		const visibleEntries: any[] = [];
		for (let i = 0; i < entries.length; i++) {
			const entry = entries[i];
			for (let j = 0; j < properties.length; j++) {
				let propertyValue = extractValue(entry, properties[j]);
				if (!queryString || trivialMatch(propertyValue, queryString, matchingOptions).length > 0) {
					visibleEntries.push(entry);
					break;
				}
			}
		}
		return visibleEntries;
	}

	return function (queryString: string) {
		return filterElements(queryString);
	}
}

export type PropertyReadAccess<E> = (string | ((entry: E) => any));

export function extractValue<E>(entry: E, property: PropertyReadAccess<E>): any {
	if (entry == null) {
		return null;
	} else if (typeof property === 'string') {
		return (entry as any)[property];
	} else if (typeof property === 'function') {
		return property(entry);
	}
}

export function createProxy(delegate: any): any {
	const proxyConstructor = function () {
		this._trProxyDelegate = delegate;
	};
	proxyConstructor.prototype = delegate;
	let proxyConstructorTypescriptHack = proxyConstructor as any;
	return new proxyConstructorTypescriptHack();
}

export function unProxyEntry(entry: any): any {
	return entry != null ? (entry._trProxyDelegate || entry) : null;
}

export function defaultEntryMatchingFunctionFactory(searchedPropertyNames: string[], matchingOptions: MatchingOptions) {
	return function (entry: any, queryString: string, depth: number) {
		return searchedPropertyNames
			.some((propertyName: string) => {
				const value = entry[propertyName];
				return value != null && trivialMatch(value.toString(), queryString, matchingOptions).length > 0
			});
	};
}

export function defaultTreeQueryFunctionFactory(topLevelEntries: any[] | (() => any[]), entryMatchingFunction: (entry: any, queryString: string, nodeDepth: number) => boolean, childrenPropertyName: string, expandedPropertyName: string): QueryFunction<any> {

	function findMatchingEntriesAndTheirAncestors(entry: any, queryString: string, nodeDepth: number) {
		const entryProxy = createProxy(entry);
		entryProxy[childrenPropertyName] = [];
		entryProxy[expandedPropertyName] = false;
		if (entry[childrenPropertyName]) {
			for (let i = 0; i < entry[childrenPropertyName].length; i++) {
				const child = entry[childrenPropertyName][i];
				const childProxy = findMatchingEntriesAndTheirAncestors(child, queryString, nodeDepth + 1);
				if (childProxy) {
					entryProxy[childrenPropertyName].push(childProxy);
					entryProxy[expandedPropertyName] = true;
				}
			}
		}
		let hasMatchingChildren = entryProxy[childrenPropertyName].length > 0;
		const matchesItself = entryMatchingFunction(entry, queryString, nodeDepth);
		if (matchesItself && !hasMatchingChildren) {
			// still make it expandable!
			entryProxy[childrenPropertyName] = entry[childrenPropertyName];
		}
		return matchesItself || hasMatchingChildren ? entryProxy : null;
	}

	return function (queryString: string) {
		let theTopLevelEntries = typeof topLevelEntries === 'function' ? topLevelEntries() : topLevelEntries;
		if (!queryString) {
			return theTopLevelEntries;
		} else {
			const matchingEntries: any[] = [];
			for (let i = 0; i < theTopLevelEntries.length; i++) {
				const topLevelEntry = theTopLevelEntries[i];
				const entryProxy = findMatchingEntriesAndTheirAncestors(topLevelEntry, queryString, 0);
				if (entryProxy) {
					matchingEntries.push(entryProxy);
				}
			}
			return matchingEntries;
		}
	}
}

export function selectElementContents(domElement: Node, start: number, end: number) {
	if (domElement == null || !document.body.contains(domElement)) {
		return;
	}
	domElement = domElement.firstChild || domElement;
	end = end || start;
	const range = document.createRange();
	//range.selectNodeContents(el);
	range.setStart(domElement, start);
	range.setEnd(domElement, end);
	const sel = window.getSelection();
	try {
		sel.removeAllRanges();
	} catch (e) {
		// ignore (ie 11 problem, can be ignored even in ie 11)
	}
	sel.addRange(range);
}

export const escapeSpecialRegexCharacter = function (s: string) {
	return s.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
};

// see http://stackoverflow.com/a/27014537/524913
export function objectEquals(x: any, y: any): boolean {
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
	const p = Object.keys(x);
	return Object.keys(y).every(function (i) {
			return p.indexOf(i) !== -1;
		}) &&
		p.every(function (i) {
			return objectEquals(x[i], y[i]);
		});
}

/**
 * @param text
 * @param searchString
 * @param options matchingMode: 'prefix', 'prefix-word', 'contain', 'prefix-levenshtein', 'levenshtein';
 *        ignoreCase: boolean
 *        maxLevenshteinDistance: integer (number) - only for levenshtein
 * @returns array of matchers {start, length, distance}
 */
export function trivialMatch(text: string, searchString: string, options?: MatchingOptions): Match[] {
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

	options = <MatchingOptions>$.extend({
		matchingMode: 'contains',
		ignoreCase: true,
		maxLevenshteinDistance: 3
	}, options || null);

	if (options.ignoreCase) {
		text = text.toLowerCase();
		searchString = searchString.toLowerCase();
	}

	function findRegexMatches(regex: RegExp) {
		const matches: Match[] = [];
		let match: RegExpExecArray;
		while (match = regex.exec(text)) {
			matches.push({
				start: match.index,
				length: match[0].length
			});
		}
		return matches;
	}

	function findLevenshteinMatches(text: string, searchString: string) {
		const levenshtein = new (Levenshtein as any)(text, searchString);
		//console.log('distance between "' + text + '" and "' + searchString + '" is ' + levenshtein.distance);
		if (levenshtein.distance <= options.maxLevenshteinDistance) {
			return [{
				start: 0,
				length: searchString.length,
				distance: levenshtein.distance
			}];
		} else {
			return [];
		}
	}

	if (options.matchingMode == 'contains') {
		searchString = searchString.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&"); // escape all regex special chars
		return findRegexMatches(new RegExp(searchString, "g"));
	} else if (options.matchingMode == 'prefix') {
		searchString = searchString.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&"); // escape all regex special chars
		return findRegexMatches(new RegExp('^' + searchString, "g"));
	} else if (options.matchingMode == 'prefix-word') {
		// ATTENTION: IF YOU CHANGE THIS, MAKE SURE TO EXECUTE THE UNIT TESTS!!
		searchString = searchString.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&"); // escape all regex special chars
		if (searchString.charAt(0).match(/^\w/)) {
			return findRegexMatches(new RegExp('\\b' + searchString, "g"));
		} else {
			// search string starts with a non-word character, so \b will possibly not match!
			// After all, we cannot really decide, what is meant to be a word boundary in this context
			// (e.g.: "12€" with searchString "€"), so we fall back to "contains" mode.
			return findRegexMatches(new RegExp(searchString, "g"));
		}
	} else if (options.matchingMode == 'prefix-levenshtein') {
		return findLevenshteinMatches(text.substr(0, Math.min(searchString.length, text.length)), searchString);
	} else if (options.matchingMode == 'levenshtein') {
		return findLevenshteinMatches(text, searchString);
	} else {
		throw "unknown matchingMode: " + options.matchingMode;
	}
}

export function minimallyScrollTo(element: Element | JQuery<Element>, target: Element | JQuery) {
	let $target = $(target);
	$(element).each(function () {
		const $this = $(this);

		const viewPortMinY = $this.scrollTop();
		const viewPortMaxY = viewPortMinY + $this.innerHeight();

		const targetMinY = $($target).offset().top - $(this).offset().top + $this.scrollTop();
		const targetMaxY = targetMinY + $target.height();

		if (targetMinY < viewPortMinY) {
			$this.scrollTop(targetMinY);
		} else if (targetMaxY > viewPortMaxY) {
			$this.scrollTop(Math.min(targetMinY, targetMaxY - $this.innerHeight()));
		}

		const viewPortMinX = $this.scrollLeft();
		const viewPortMaxX = viewPortMinX + $this.innerWidth();

		const targetMinX = $($target).offset().left - $(this).offset().left + $this.scrollLeft();
		const targetMaxX = targetMinX + $target.width();

		if (targetMinX < viewPortMinX) {
			$this.scrollLeft(targetMinX);
		} else if (targetMaxX > viewPortMaxX) {
			$this.scrollLeft(Math.min(targetMinX, targetMaxX - $this.innerWidth()));
		}
	});
}

export function setTimeoutOrDoImmediately(f: Function, delay?: number): number {
	if (delay != null && delay > 0) {
		return window.setTimeout(f(), delay);
	} else {
		return void f();
	}
}

export function generateUUID() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		const r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
		return v.toString(16);
	});
}
