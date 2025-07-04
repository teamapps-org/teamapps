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

import {distance as levenshteinDistance} from 'fastest-levenshtein';

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

export type HighlightDirection = -1 | 0 | 1 | null | undefined;
export type NavigationDirection = "up" | "left" | "down" | "right";
export type QueryFunction<E> = (queryString: string) => E[] | Promise<E[]>;

/**
 * Used to render an entry.
 *
 * @param entry the entry to render
 * @return HTML string
 */
export type RenderingFunction<E> = (entry: E) => string;

const modifierKeys = ["Alt", "AltGraph", "CapsLock", "Control", "Fn", "Shift", "Hyper", "Meta", "NumLock", "ScrollLock", "Super", "Symbol", "SymbolLock"];
const specialKeys = [...modifierKeys, "Tab", "Enter", "Delete", "Escape", "PageUp", "PageDown", "Home"];

export function isModifierKey(e: KeyboardEvent) {
	return modifierKeys.indexOf(e.key) != -1;
}

export function isSpecialKey(e: KeyboardEvent) {
	return specialKeys.indexOf(e.key) != -1;
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

	options = {
		matchingMode: 'contains',
		ignoreCase: true,
		maxLevenshteinDistance: 3,
		...options
	};

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
		const distance = levenshteinDistance(text, searchString);
		if (distance <= options.maxLevenshteinDistance) {
			return [{
				start: 0,
				length: searchString.length,
				distance
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
