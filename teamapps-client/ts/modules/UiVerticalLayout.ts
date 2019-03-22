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
import {UiComponent} from "./UiComponent";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiVerticalLayoutCommandHandler, UiVerticalLayoutConfig} from "../generated/UiVerticalLayoutConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";


export class UiVerticalLayout extends UiComponent<UiVerticalLayoutConfig> implements UiVerticalLayoutCommandHandler {
	private $verticalLayout: JQuery;
	private children: UiComponent<UiComponentConfig>[] = [];

	constructor(config: UiVerticalLayoutConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$verticalLayout = $('<div id="' + config.id + '" class="UiVerticalLayout"></div>');

		if (config.components) {
			for (let i = 0; i < config.components.length; i++) {
				this.addComponent(config.components[i]);
			}
		}
	}

	public getMainDomElement(): JQuery {
		return this.$verticalLayout;
	}

	protected onAttachedToDom() {
		this.children.forEach(c => c.attachedToDom = true);
	}

	public onResize(): void {
		this.children.forEach(c => c.reLayout());
	}

	public destroy(): void {
	}

	public addComponent(childComponent: UiComponent) {
		const $childWrapper = $('<div class="vertical-layout-child-wrapper" style="' + (this._config.fixedChildHeight ? 'height:' + this._config.fixedChildHeight + 'px' : '') + '">').appendTo(this.$verticalLayout);
		childComponent.getMainDomElement().appendTo($childWrapper);
		this.children.push(childComponent);
		childComponent.attachedToDom = this.attachedToDom;
	}

	public removeComponent(childComponent: UiComponent) {
		this.children = this.children.filter(c => c !== childComponent);
		let $childWrapper = childComponent.getMainDomElement().closest(".vertical-layout-child-wrapper");
		$childWrapper.detach();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiVerticalLayout", UiVerticalLayout);
