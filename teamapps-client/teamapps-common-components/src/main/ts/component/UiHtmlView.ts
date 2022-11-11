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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiHtmlViewCommandHandler, UiHtmlViewConfig} from "../generated/UiHtmlViewConfig";
import {parseHtml} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";

export class UiHtmlView extends AbstractUiComponent<UiHtmlViewConfig> implements UiHtmlViewCommandHandler {

	private $main: HTMLDivElement;

	constructor(config: UiHtmlViewConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiHtmlView">${config.html}</div>`);
		for (let selector in config.componentsByContainerElementSelector) {
			let components = config.componentsByContainerElementSelector[selector] as UiComponent[];
			for (let c of components) {
				this.addComponent(selector, c, false);
			}
		}
		for (let selector in config.contentHtmlByContainerElementSelector) {
			this.setContentHtml(selector, config.contentHtmlByContainerElementSelector[selector]);
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addComponent(containerElementSelector: string, component: unknown, clearContainer: boolean): void {
		let containerElement = this.$main.querySelector(`:scope ${containerElementSelector}`);
		if (containerElement != null) {
			if (clearContainer) {
				containerElement.innerHTML = '';
			}
			containerElement.appendChild((component as UiComponent).getMainElement());
		} else {
			console.error(`Could not add child component since selector does not match any element: ${containerElementSelector}`);
		}
	}

	removeComponent(component: unknown): void {
		(component as UiComponent).getMainElement().remove();
	}

	setContentHtml(containerElementSelector: string, html: string): void {
		let containerElement = this.$main.querySelector(`:scope ${containerElementSelector}`);
		if (containerElement != null) {
			containerElement.innerHTML = '';
			if (html != null) {
				let childNodes = parseHtml(`<div>${html}</div>`).childNodes;
				childNodes.forEach(cn => containerElement.appendChild(cn))
			}
		} else {
			console.error(`Could not set content HTML since selector does not match any element: ${containerElementSelector}`);
		}
	}


}

TeamAppsUiComponentRegistry.registerComponentClass("UiHtmlView", UiHtmlView);
