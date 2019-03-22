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
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiTemplateTestContainerConfig} from "../generated/UiTemplateTestContainerConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";


export class UiTemplateTestContainer extends UiComponent<UiTemplateTestContainerConfig> {

	private $panel: JQuery;

	constructor(config: UiTemplateTestContainerConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$panel = $(`<div class="UiTemplateTestContainer" id="' + config.id + '">
	<div class="description">${config.description}</div>
	<div class="template-wrapper" style="min-width: ${config.minContainerWidth ? config.minContainerWidth + "px" : "none"}; max-width: ${config.maxContainerWidth ? config.maxContainerWidth + "px" : "none"}; min-height: ${config.minContainerHeight ? config.minContainerHeight + "px" : "none"}; max-height: ${config.maxContainerHeight ? config.maxContainerHeight + "px" : "none"};">
		${context.templateRegistry.createTemplateRenderer(config.template).render(config.data)}
	</div>
</div>`);
	}


	public getMainDomElement(): JQuery {
		return this.$panel;
	}

	public destroy(): void {
	}

	protected onAttachedToDom(): void {
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiTemplateTestContainer", UiTemplateTestContainer);
