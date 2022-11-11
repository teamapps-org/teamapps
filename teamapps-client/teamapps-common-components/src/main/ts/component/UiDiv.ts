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
import {UiDivCommandHandler, UiDivConfig} from "../generated/UiDivConfig";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";
import {AbstractUiWebComponent} from "./AbstractUiWebComponent";

const template = document.createElement("template");
template.innerHTML = `
<style>
	:host {
		display: block;
	}
</style>
<slot></slot>
`;

export class UiDiv extends AbstractUiWebComponent<UiDivConfig> implements UiDivCommandHandler {
	private $slot: HTMLSlotElement;

	constructor() {
		super();
		this.shadowRoot?.appendChild(template.content.cloneNode(true));
		this.$slot = this.shadowRoot.querySelector("slot");
	}

	public setConfig(config: UiDivConfig) {
		super.setConfig(config);
		if (config.innerHtml != null) {
			this.setInnerHtml(config.innerHtml);
		}
		if (config.content != null) {
			this.setContent(config.content);
		}
	}

	setContent(content: unknown): any {
		if (content == null) {
			this.innerHTML = '';
		} else {
			this.appendChild((content as UiComponent).getMainElement())
		}
	}

	setInnerHtml(innerHtml: string): any {
		if (innerHtml == null) {
			this.innerHTML = '';
		} else {
			this.innerHTML = innerHtml;
		}
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiDiv", UiDiv);
window.customElements.define("ui-div", UiDiv);