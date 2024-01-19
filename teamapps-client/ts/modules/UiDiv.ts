/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {UiDivConfig} from "../generated/UiDivConfig";
import {parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";
import {UiDivCommandHandler} from "../generated/UiDivConfig";

export class UiDiv extends AbstractUiComponent<UiDivConfig> implements UiDivCommandHandler {

	private $main: HTMLDivElement;

	constructor(config: UiDivConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiDiv"></div>`);
		this.setContent(config.content);
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	setContent(content: unknown): void {
		this.$main.innerHTML = "";
		if (content != null) {
			this.$main.append((content as UiComponent).getMainElement());
		}
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiDiv", UiDiv);
