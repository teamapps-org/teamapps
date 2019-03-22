/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import * as $ from "jquery";
import {ProgressIndicator} from "./ProgressIndicator";
import {generateUUID} from "../Common";

export class ProgressBar implements ProgressIndicator {
	private $mainDomElement: JQuery;
	private $progressBar: JQuery;
	private $errorMessageBar: JQuery;

	constructor(initialProgress: number, {
		height = 18,
		transitionTime = 700
	}) {
		let uuid = generateUUID();
		this.$mainDomElement = $(`<div id="c-${uuid}" class="ProgressBar progress">
                 <div class="progress-bar active" role="progressbar" style="width: 0"></div>
                 <div class="error-message-bar hidden"></div>
                 <style>
                   #c-${uuid} .progress-bar,
                   #c-${uuid} .error-message-bar{        
                     transition: width ${transitionTime}ms linear;
                     height: ${height}px;
                   } 
                 </style>
                </div>`);
		this.$progressBar = this.$mainDomElement.find('.progress-bar').first();
		this.$errorMessageBar = this.$mainDomElement.find('.error-message-bar');

		this.setProgress(initialProgress)
	}

	/**
	 * @param progress Between 0 and 1
	 * @param caption
	 */
	public setProgress(progress: number) {
		let percentString = Math.ceil(progress * 100) + "%";
		this.$progressBar.width(percentString).text(percentString);
	}

	setErrorMessage(message: string | null): void {
		this.$progressBar.toggleClass('hidden', !!message);
		this.$errorMessageBar.toggleClass('hidden', !message);
		this.$errorMessageBar.text(message);
	}

	public getMainDomElement() {
		return this.$mainDomElement;
	}
}
