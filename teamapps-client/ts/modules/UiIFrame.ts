/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiIFrameCommandHandler, UiIFrameConfig} from "../generated/UiIFrameConfig";
import {parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";

export class UiIFrame extends AbstractUiComponent<UiIFrameConfig> implements UiIFrameCommandHandler {

	private $iframe: HTMLIFrameElement;

	constructor(config: UiIFrameConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$iframe = parseHtml(`<iframe class="UiIFrame" src="${config.url}"></iframe>`);
		// this.$iframe.addEventListener("load", e => {
		// 	console.log(`load: ${this.$iframe.src}`);
		// });
		// this.$iframe.addEventListener("unload", e => {
		// 	console.log(`unload: ${this.$iframe.src}`);
		// });
	}

	public doGetMainElement(): HTMLElement {
		return this.$iframe;
	}


	setUrl(url: string): void {
		this.$iframe.src = url;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiIFrame", UiIFrame);
