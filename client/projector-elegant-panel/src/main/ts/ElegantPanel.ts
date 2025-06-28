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

import {AbstractComponent, type Component, createSpacingCssString, parseHtml} from "projector-client-object-api";
import {type DtoElegantPanel, type DtoElegantPanelCommandHandler} from "./generated";

export class ElegantPanel extends AbstractComponent<DtoElegantPanel> implements DtoElegantPanelCommandHandler {

	private $element: HTMLElement;
	private $contentContainer: HTMLElement;
	private contentComponent: Component | null = null;

	constructor(config: DtoElegantPanel) {
		super(config);
		this.$element = parseHtml(`<div class="ElegantPanel">
                <div class="flex-container">
                    <div class="background-image-div teamapps-blurredBackgroundImage">
                        <div class="background-color-div">
                            <div class="content-container scroll-container ${config.horizontalContentAlignment}" style="${createSpacingCssString("padding", config.padding)}; max-width: ${config.maxContentWidth ? config.maxContentWidth + "px" : "100%"};"></div>
                        </div>
                    </div>
                </div>
            </div>`);

		let $backgroundColorDiv = this.$element.querySelector<HTMLElement>(':scope .background-color-div')!;
		this.$contentContainer = this.$element.querySelector<HTMLElement>(':scope .content-container')!;

		if (config.bodyBackgroundColor) {
			$backgroundColorDiv.style.backgroundColor = config.bodyBackgroundColor;
		}
		if (config.content) {
			this.setContent(config.content as Component);
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$element;
	}

	public setContent(content: Component) {
		this.$contentContainer.innerHTML = '';
		this.contentComponent = content;
		if (content) {
			this.$contentContainer.appendChild(this.contentComponent.getMainElement());
		}
	}

}


