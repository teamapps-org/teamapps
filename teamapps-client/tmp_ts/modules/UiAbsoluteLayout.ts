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
import {DtoAbsoluteLayoutCommandHandler, DtoAbsoluteLayout} from "../generated/DtoAbsoluteLayout";
import {DtoAbsolutePositionedComponent} from "../generated/DtoAbsolutePositionedComponent";
import {UiAnimationEasing} from "../generated/UiAnimationEasing";
import {parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {bind} from "./util/Bind";
import {UiComponent} from "./UiComponent";

const animationEasingCssValues = {
	[UiAnimationEasing.EASE]: "ease",
	[UiAnimationEasing.LINEAR]: "linear",
	[UiAnimationEasing.EASE_IN]: "ease-in",
	[UiAnimationEasing.EASE_OUT]: "ease-out",
	[UiAnimationEasing.EASE_IN_OUT]: "ease-in-out",
	[UiAnimationEasing.STEP_START]: "step-start",
	[UiAnimationEasing.STEP_END]: "step-end"
};

export class UiAbsoluteLayout extends AbstractLegacyComponent<DtoAbsoluteLayout> implements DtoAbsoluteLayoutCommandHandler {
	private $main: HTMLElement;
	private $style: HTMLStyleElement;
	private components: DtoAbsolutePositionedComponent[];

	constructor(config: DtoAbsoluteLayout, serverChannel: ServerChannel) {
		super(config, serverChannel);
		this.$main = parseHtml(`<div class="UiAbsoluteLayout" data-teamapps-id="${this.getId()}">
	<style></style>
</div>`);
		this.$style = this.$main.querySelector(":scope > style");
		this.update(config.components, 0, UiAnimationEasing.EASE);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	private transitionEndEventListener = (e: Event) => {
		if (event.currentTarget !== event.target) {
			return;
		}
		this.onResize();
	};

	update(components: DtoAbsolutePositionedComponent[], animationDuration: number, easing: UiAnimationEasing): void {
		Array.from(this.$main.querySelectorAll(":scope > :not(style)")).forEach(c => c.remove());

		this.components = components;
		components
			.map(c => c.component)
			.forEach((c: UiComponent) => {
				const mainDomElementElement = c.getMainElement();
				mainDomElementElement.removeEventListener("transitionend", this.transitionEndEventListener);
				this.$main.appendChild(mainDomElementElement);
			});

		this.$main.offsetWidth; // trigger reflow

		let styles = `[data-teamapps-id=${this.getId()}] > * {
	transition: top ${animationDuration}ms ${animationEasingCssValues[easing]}, right ${animationDuration}ms ${animationEasingCssValues[easing]}, bottom ${animationDuration}ms ${animationEasingCssValues[easing]}, left ${animationDuration}ms ${animationEasingCssValues[easing]}, width ${animationDuration}ms ${animationEasingCssValues[easing]}, height ${animationDuration}ms ${animationEasingCssValues[easing]};
}`;
		components.forEach(c => {
			const component = c.component as AbstractComponent;
			component.getMainElement().addEventListener('transitionend', this.transitionEndEventListener);
			component.getMainElement().setAttribute("data-absolute-positioning-id", component.getId());
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


