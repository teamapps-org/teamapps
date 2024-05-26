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

import {
	AbstractLegacyComponent,
	Component,
	DtoAlignItems,
	DtoFlexDirection, DtoJustifyContent,
	insertAtIndex,
	parseHtml,
	ServerObjectChannel
} from "projector-client-object-api";
import {DtoFlexContainer, DtoFlexContainerCommandHandler} from "../generated";

export class FlexContainer extends AbstractLegacyComponent<DtoFlexContainer> implements DtoFlexContainerCommandHandler {

	private $main: HTMLDivElement;

	constructor(config: DtoFlexContainer, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this.$main = parseHtml(`<div class="FlexContainer"></div>`);

		this.setAlignItems(config.alignItems);
		this.setFlexDirection(config.flexDirection);
		this.setJustifyContent(config.justifyContent);
		this.setComponents(config.components as Component[]);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	setComponents(components: Component[]): any {
		this.config.components = components;
		for (let i = 0; i < components.length; i++) {
			if (this.$main.children[i] !== components[i].getMainElement()) {
				if (this.$main.children.length > components.length) {
					this.$main.children[i].remove();
				}
				insertAtIndex(this.$main, components[i].getMainElement(), i);
			}
		}
		for (let i = components.length; i < this.$main.children.length; i++) {
			this.$main.children[i].remove();
		}
	}

	setAlignItems(alignItems: DtoAlignItems): any {
		this.config.alignItems = alignItems;
		this.$main.style.alignItems = alignItems;
	}

	setFlexDirection(flexDirection: DtoFlexDirection): any {
		this.config.flexDirection = flexDirection;
		this.$main.style.flexDirection = flexDirection;
	}

	setJustifyContent(justifyContent: DtoJustifyContent): any {
		this.config.justifyContent = justifyContent;
		this.$main.style.justifyContent = justifyContent;
	}


}


