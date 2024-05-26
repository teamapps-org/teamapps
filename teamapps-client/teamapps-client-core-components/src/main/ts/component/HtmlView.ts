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
import {AbstractLegacyComponent, Component, parseHtml, ServerObjectChannel} from "projector-client-object-api";
import {DtoHtmlView, DtoHtmlViewCommandHandler} from "../generated";

export class HtmlView extends AbstractLegacyComponent<DtoHtmlView> implements DtoHtmlViewCommandHandler {

	private $main: HTMLDivElement;

	constructor(config: DtoHtmlView, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this.$main = parseHtml(`<div class="HtmlView">${config.html}</div>`);
		for (let selector in config.componentsByContainerElementSelector) {
			let components = config.componentsByContainerElementSelector[selector] as Component[];
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
			containerElement.appendChild((component as Component).getMainElement());
		} else {
			console.error(`Could not add child component since selector does not match any element: ${containerElementSelector}`);
		}
	}

	removeComponent(component: unknown): void {
		(component as Component).getMainElement().remove();
	}

	setContentHtml(containerElementSelector: string, html: string): void {
		let containerElement = this.$main.querySelector(`:scope ${containerElementSelector}`);
		if (containerElement != null) {
			containerElement.innerHTML = '';
			if (html != null) {
				let childNodes = parseHtml(`<div>${html}</div>`).childNodes;
				// Note that we need to copy childNodes, since childNodes is going to change on containerElement.appendChild(),
				// since this effectively removes one child from the childNodes NodeList.
				Array.from(childNodes).forEach(cn => containerElement.appendChild(cn))
			}
		} else {
			console.error(`Could not set content HTML since selector does not match any element: ${containerElementSelector}`);
		}
	}


}


