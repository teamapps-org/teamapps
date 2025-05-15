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

import {ProgressIndicator} from "./ProgressIndicator";
import {generateUUID, parseHtml} from "../Common";

export class ProgressBar implements ProgressIndicator {
	private $mainDomElement: HTMLElement;
	private $progressBar: HTMLElement;
	private $errorMessageBar: HTMLElement;

	constructor(initialProgress: number, {
		height = 18,
		transitionTime = 700
	}) {
		let uuid = generateUUID();
		this.$mainDomElement = parseHtml(`<div id="c-${uuid}" class="ProgressBar progress">
	<div class="progress-bar active" role="progressbar" style="width: 0%"></div>
	<div class="error-message-bar hidden"></div>
	<style>
	  #c-${uuid} {
	    height: ${height}px;
	  }
	  
	  #c-${uuid} .progress-bar,
	  #c-${uuid} .error-message-bar{        
	    transition: width ${transitionTime}ms linear;
	    height: ${height}px;
	  } 
	</style>
</div>`);
		this.$progressBar = this.$mainDomElement.querySelector<HTMLElement>(':scope .progress-bar');
		this.$errorMessageBar = this.$mainDomElement.querySelector<HTMLElement>(':scope .error-message-bar');

		this.setProgress(initialProgress)
	}

	/**
	 * @param progress Between 0 and 1
	 */
	public setProgress(progress: number) {
		this.$progressBar.style.width = Math.ceil(progress * 100) + "%";
	}

	public setErrorMessage(message: string | null): void {
		this.$progressBar.classList.toggle('hidden', !!message);
		this.$errorMessageBar.classList.toggle('hidden', !message);
		this.$errorMessageBar.innerText = message;
	}

	public getMainDomElement() {
		return this.$mainDomElement;
	}
}
