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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiHtmlViewCommandHandler, UiHtmlViewConfig} from "../generated/UiHtmlViewConfig";
import {parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";

export class UiHtmlView extends AbstractUiComponent<UiHtmlViewConfig> implements UiHtmlViewCommandHandler {

	private $main: HTMLIFrameElement;

	constructor(config: UiHtmlViewConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiHtmlView">${config.html}</div>`);
		for (let selector in config.componentsByContainerElementSelector) {
			this.addComponent(selector, config.componentsByContainerElementSelector[selector]);
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addComponent(containerElementSelector: string, component: unknown): void {
		let containerElement = this.$main.querySelector(containerElementSelector);
		if (containerElement != null) {
			containerElement.appendChild((component as UiComponent).getMainElement());
		} else {
			this.logger.error(`Could not add child component since selector does not match any element: ${containerElementSelector}`);
		}
	}

	removeComponent(component: unknown): void {
		(component as UiComponent).getMainElement().remove();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiHtmlView", UiHtmlView);
