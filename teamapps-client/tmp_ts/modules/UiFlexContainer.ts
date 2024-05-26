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

import {AbstractComponent} from "teamapps-client-core";
import {TeamAppsUiContext} from "teamapps-client-core";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";
import {DtoFlexContainerCommandHandler, DtoFlexContainer} from "../generated/DtoFlexContainer";
import {UiCssFlexDirection} from "../generated/UiCssFlexDirection";
import {UiCssAlignItems} from "../generated/UiCssAlignItems";
import {UiCssJustifyContent} from "../generated/UiCssJustifyContent";
import {UiComponent} from "./UiComponent";

export class UiFlexContainer extends AbstractLegacyComponent<DtoFlexContainer> implements DtoFlexContainerCommandHandler {

	private $main: HTMLDivElement;
	private components: UiComponent[] = [];

	constructor(config: DtoFlexContainer, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this.$main = parseHtml(`<div class="UiFlexContainer"></div>`);
		this.$main.style.flexDirection = config.flexDirection;
		this.$main.style.alignItems = config.alignItems;
		this.$main.style.justifyContent = config.justifyContent;

		config.components.forEach(c => this.addComponent(c as UiComponent));
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addComponent(component: UiComponent): void {
		this.components.push(component);
		this.$main.appendChild(component.getMainElement());
	}

	removeComponent(component: UiComponent): void {
		try {
			this.$main.removeChild(component.getMainElement());
		} catch (e) {
			// ignore if this is actually not a child...
		}
		this.components = this.components.filter(c => c !== component);
	}

}


