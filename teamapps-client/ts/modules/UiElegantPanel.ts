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
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiElegantPanelCommandHandler, UiElegantPanelConfig} from "../generated/UiElegantPanelConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {createUiSpacingCssString} from "./util/CssFormatUtil";
import {UiHorizontalElementAlignment} from "../generated/UiHorizontalElementAlignment";

export class UiElegantPanel extends UiComponent<UiElegantPanelConfig> implements UiElegantPanelCommandHandler {

	private $element: JQuery;
	private $contentContainer: JQuery;
	private contentComponent: UiComponent<UiComponentConfig>;

	constructor(config: UiElegantPanelConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$element = $(`<div id="${config.id}" class="UiElegantPanel">
                <div class="flex-container">
                    <div class="background-image-div teamapps-blurredBackgroundImage">
                        <div class="background-color-div">
                            <div class="content-container scroll-container ${UiHorizontalElementAlignment[config.horizontalContentAlignment].toLowerCase()}" style="${createUiSpacingCssString("padding", config.padding)}; max-width: ${config.maxContentWidth ? config.maxContentWidth + "px" : "100%"};"></div>
                        </div>
                    </div>
                </div>
            </div>`);

		let $backgroundColorDiv = this.$element.find('.background-color-div');
		this.$contentContainer = this.$element.find('.content-container');

		if (config.bodyBackgroundColor) {
			$backgroundColorDiv.css("background-color", config.bodyBackgroundColor);
		}
		if (config.content) {
			this.setContent(config.content);
		}
	}

	public getMainDomElement(): JQuery {
		return this.$element;
	}

	public setContent(content: UiComponent) {
		this.$contentContainer[0].innerHTML = '';
		this.contentComponent = content;
		if (content) {
			this.contentComponent.getMainDomElement().appendTo(this.$contentContainer);
			this.contentComponent.attachedToDom = this.attachedToDom;
		}
	}

	protected onAttachedToDom() {
		if (this.contentComponent) this.contentComponent.attachedToDom = true;
		this.reLayout();
	}

	onResize(): void {
		if (!this.attachedToDom || this.getMainDomElement()[0].offsetWidth <= 0) return;
		this.contentComponent && this.contentComponent.reLayout();
	}

	public destroy(): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiElegantPanel", UiElegantPanel);
