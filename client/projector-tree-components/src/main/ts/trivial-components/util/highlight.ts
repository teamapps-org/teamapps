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
import {type MatchingOptions, trivialMatch} from "../TrivialCore";

export type HighlightOptions = MatchingOptions & {
	highlightClassName?: string
};

export function highlightMatches(node: HTMLElement, searchString: string, options: HighlightOptions) {
	let defaultOptions: HighlightOptions = {
		highlightClassName: 'tr-highlighted-text',
		matchingMode: 'contains',
		ignoreCase: true
	};
	options = {...defaultOptions, ...options};

	// remove old highlighting
	node.querySelectorAll('.' + options.highlightClassName).forEach(el => {
		const parent = el.parentNode;
		while (el.firstChild) {
			parent.insertBefore(el.firstChild, el);
		}
		parent.removeChild(el);
	});

	node.querySelectorAll(':scope *').forEach(function (el) {
		el.normalize();

		if (searchString && searchString !== '') {
			Array.from(el.children)
				.filter((child) => {
					return child.nodeType == 3 && trivialMatch(el.nodeValue, searchString, options).length > 0;
				})
				.forEach((el) => {
					const oldNodeValue = (el.nodeValue || "");
					let newNodeValue = "";
					const matches = trivialMatch(oldNodeValue, searchString, options);
					let oldMatchEnd = 0;
					for (let i = 0; i < matches.length; i++) {
						const match = matches[i];
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
