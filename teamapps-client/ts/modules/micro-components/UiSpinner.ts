/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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

import {parseHtml} from "../Common";

export class UiSpinner {
	private $mainDomElement: HTMLElement;

	constructor(options?: { fixedSize?: number | string }) {
		options = options || {};
		this.$mainDomElement = parseHtml(`<div class="UiSpinner"><div class="teamapps-spinner"></div></div>`);
		let fixedSizeCssValue: string = options.fixedSize == null ? "100%" : typeof options.fixedSize === "number" ? options.fixedSize + "px" :  options.fixedSize;
		const $spinner = this.$mainDomElement.querySelector<HTMLElement>(":scope .teamapps-spinner");
		$spinner.style.width = fixedSizeCssValue;
		$spinner.style.height = fixedSizeCssValue;
	}

	public getMainDomElement() {
		return this.$mainDomElement;
	}
}
