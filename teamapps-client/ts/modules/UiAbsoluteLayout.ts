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
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiAbsoluteLayoutCommandHandler, UiAbsoluteLayoutConfig} from "../generated/UiAbsoluteLayoutConfig";
import {UiAbsolutePositionedComponentConfig} from "../generated/UiAbsolutePositionedComponentConfig";
import {UiAnimationEasing} from "../generated/UiAnimationEasing";
import {parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {bind} from "./util/Bind";

const animationEasingCssValues = {
	[UiAnimationEasing.EASE]: "ease",
	[UiAnimationEasing.LINEAR]: "linear",
	[UiAnimationEasing.EASE_IN]: "ease-in",
	[UiAnimationEasing.EASE_OUT]: "ease-out",
	[UiAnimationEasing.EASE_IN_OUT]: "ease-in-out",
	[UiAnimationEasing.STEP_START]: "step-start",
	[UiAnimationEasing.STEP_END]: "step-end"
};

export class UiAbsoluteLayout extends UiComponent<UiAbsoluteLayoutConfig> implements UiAbsoluteLayoutCommandHandler {
	private $main: HTMLElement;
	private $style: HTMLStyleElement;
	private components: UiAbsolutePositionedComponentConfig[];

	constructor(config: UiAbsoluteLayoutConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiAbsoluteLayout" data-teamapps-id="${this.getId()}">
	<style></style>
</div>`);
		this.$style = this.$main.querySelector(":scope > style");
		this.update(config.components, 0, UiAnimationEasing.EASE);
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	private transitionEndEventListener = (e: Event) => {
		if (event.currentTarget !== event.target) {
			return;
		}
		this.onResize();
	};

	update(components: UiAbsolutePositionedComponentConfig[], animationDuration: number, easing: UiAnimationEasing): void {
		Array.from(this.$main.querySelectorAll(":scope > :not(style)")).forEach(c => c.remove());

		this.components = components;
		components
			.map(c => c.component)
			.forEach((c: UiComponent) => {
				const mainDomElementElement = c.getMainDomElement();
				mainDomElementElement.removeEventListener("transitionend", this.transitionEndEventListener);
				this.$main.appendChild(mainDomElementElement);
			});

		this.$main.offsetWidth; // trigger reflow

		let styles = `[data-teamapps-id=${this.getId()}] > * {
	transition: top ${animationDuration}ms ${animationEasingCssValues[easing]}, right ${animationDuration}ms ${animationEasingCssValues[easing]}, bottom ${animationDuration}ms ${animationEasingCssValues[easing]}, left ${animationDuration}ms ${animationEasingCssValues[easing]}, width ${animationDuration}ms ${animationEasingCssValues[easing]}, height ${animationDuration}ms ${animationEasingCssValues[easing]};
}`;
		components.forEach(c => {
			const component = c.component as UiComponent;
			component.getMainDomElement().addEventListener('transitionend', this.transitionEndEventListener);
			component.getMainDomElement().setAttribute("data-absolute-positioning-id", component.getId());
			styles += `[data-teamapps-id=${this.getId()}] > [data-absolute-positioning-id=${component.getId()}] {
	top: ${c.position.topCss != null ? c.position.topCss : "initial"};
	right: ${c.position.rightCss != null ? c.position.rightCss : "initial"};
	bottom: ${c.position.bottomCss != null ? c.position.bottomCss : "initial"};
	left: ${c.position.leftCss != null ? c.position.leftCss : "initial"};
	width: ${c.position.widthCss != null ? c.position.widthCss : "initial"};
	height: ${c.position.heightCss != null ? c.position.heightCss : "initial"};
	z-index: ${c.position.zIndex != null ? c.position.zIndex : "initial"};			
}`;
		});

		this.$style.innerText = styles;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiAbsoluteLayout", UiAbsoluteLayout);
