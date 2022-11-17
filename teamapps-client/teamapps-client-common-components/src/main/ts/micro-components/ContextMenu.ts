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


import {UiComponent} from "../component/UiComponent";
import {doOnceOnClickOutsideElement, parseHtml} from "../Common";

export class ContextMenu {

	private width: number = 250;
	private height: number = -1;
	private viewPortPadding: number = 10;
	private content: UiComponent;

	private $main: HTMLElement;
	private $spinner: HTMLElement;

	private currentRequestId = 0;

	constructor() {
		this.$main = parseHtml(`<div class="ContextMenu empty">
	<div class="teamapps-spinner"></div>
</div>`);
		this.$spinner = this.$main.querySelector(":scope .teamapps-spinner")

		this.updateSize();
	}

	public setContent(content: UiComponent, requestId?: number) {
		if (requestId != null && requestId !== this.currentRequestId) {
			return;
		}
		
		if (this.content != null) {
			this.content.getMainElement().remove();
		}
		this.content = content;
		if (content == null) {
			this.$main.classList.add("empty");
		} else {
			this.$main.classList.remove("empty");
			this.$main.appendChild(content.getMainElement());
		}
	}

	public open(e: PointerEvent | MouseEvent, contextMenuRequestCallback: (requestId: number) => any) {
		e.preventDefault();
		this.setContent(null);

		this.$main.style.opacity = "0";
		document.body.appendChild(this.$main);
		this.$main.offsetHeight; // force css layout
		this.$main.style.opacity = "1";

		let rect = this.$main.getBoundingClientRect();
		let {pageX, pageY} = e;
		pageX = Math.min(pageX, window.innerWidth - rect.width - this.viewPortPadding);
		pageX = Math.max(pageX, this.viewPortPadding);
		pageY = Math.min(pageY, window.innerHeight - rect.height - this.viewPortPadding);
		pageY = Math.max(pageY, this.viewPortPadding);

		this.$main.style.left = pageX + "px";
		this.$main.style.top = pageY + "px";

		doOnceOnClickOutsideElement(this.$main, e => this.close(), false);

		contextMenuRequestCallback(++this.currentRequestId);
	}

	public close(requestId?: number) {
		if (requestId != null && requestId !== this.currentRequestId) {
			return;
		}
		this.setContent(null);
		this.$main.remove();
	}

	public setSize(width: number, height: number) {
		this.width = width;
		this.height = height;
		this.updateSize();
	}

	private updateSize() {
		this.$main.style.width = this.width + "px";
		if (this.height >= 0) {
			this.$main.style.height = this.height + "px";
		} else {
			this.$main.style.removeProperty("height");
		}
	}
}
