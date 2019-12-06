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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiVerticalLayoutCommandHandler, UiVerticalLayoutConfig} from "../generated/UiVerticalLayoutConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";
import {UiComponent} from "./UiComponent";


export class UiVerticalLayout extends AbstractUiComponent<UiVerticalLayoutConfig> implements UiVerticalLayoutCommandHandler {
	private $verticalLayout: HTMLElement;
	private children: UiComponent<UiComponentConfig>[] = [];

	constructor(config: UiVerticalLayoutConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$verticalLayout = parseHtml('<div id="' + config.id + '" class="UiVerticalLayout"></div>');

		if (config.components) {
			for (let i = 0; i < config.components.length; i++) {
				this.addComponent(config.components[i] as UiComponent);
			}
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$verticalLayout;
	}

	public addComponent(childComponent: UiComponent) {
		const $childWrapper = parseHtml('<div class="vertical-layout-child-wrapper" style="' + (this._config.fixedChildHeight ? 'height:' + this._config.fixedChildHeight + 'px' : '') + '">');
		this.$verticalLayout.appendChild($childWrapper);
		$childWrapper.appendChild(childComponent.getMainElement());
		this.children.push(childComponent);
	}

	public removeComponent(childComponent: UiComponent) {
		this.children = this.children.filter(c => c !== childComponent);
		let $childWrapper = childComponent.getMainElement().closest(".vertical-layout-child-wrapper");
		$childWrapper.remove();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiVerticalLayout", UiVerticalLayout);
