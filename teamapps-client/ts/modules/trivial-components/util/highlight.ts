/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import {MatchingOptions, trivialMatch} from "../TrivialCore";

export type HighlightOptions = MatchingOptions & {
	highlightClassName?: string
};

const isIE11 = !((<any>window).ActiveXObject) && "ActiveXObject" in window;
function normalizeForIE11 (node:Node) {
	if (!node) { return; }
	if (node.nodeType == 3) {
		while (node.nextSibling && node.nextSibling.nodeType == 3) {
			node.nodeValue += node.nextSibling.nodeValue;
			node.parentNode.removeChild(node.nextSibling);
		}
	} else {
		normalizeForIE11(node.firstChild);
	}
	normalizeForIE11(node.nextSibling);
}

export function highlightMatches(node: Element|Element[]|NodeListOf<Element>|JQuery, searchString:string, options: HighlightOptions) {
	let defaultOptions: HighlightOptions = {
		highlightClassName: 'tr-highlighted-text',
		matchingMode: 'contains',
		ignoreCase: true,
		maxLevenshteinDistance: 3
	};
	options = {...defaultOptions, ...options};

	$(node).find('*').each(function () {
		const $this = $(this);

		$this.find('.' + options.highlightClassName).contents().unwrap();
		if (isIE11) {
			normalizeForIE11(this);
		} else {
			this.normalize();
		}

		if (searchString && searchString !== '') {
			$this.contents().filter(function () {
				return this.nodeType == 3 && trivialMatch(this.nodeValue, searchString, options).length > 0;
			}).replaceWith(function () {
				const oldNodeValue = (this.nodeValue || "");
				let newNodeValue = "";
				const matches = trivialMatch(this.nodeValue, searchString, options);
				let oldMatchEnd = 0;
				for (let i = 0; i < matches.length; i++) {
					const match = matches[i];
					newNodeValue += this.nodeValue.substring(oldMatchEnd, match.start);
					newNodeValue += "<span class=\"" + options.highlightClassName + "\">" + oldNodeValue.substr(match.start, match.length) + "</span>";
					oldMatchEnd = match.start + match.length;
				}
				newNodeValue += oldNodeValue.substring(oldMatchEnd, oldNodeValue.length);
				return newNodeValue as any; // type definition does not support string but should...
			});
		}
	});
}
