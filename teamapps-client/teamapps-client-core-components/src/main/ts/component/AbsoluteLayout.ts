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
import {AbstractLegacyComponent, Component, generateUUID, parseHtml, ServerChannel} from "projector-client-object-api";
import {DtoAbsoluteLayout, DtoAbsoluteLayoutCommandHandler, DtoAbsolutePositionedComponent, DtoAnimationEasing} from "../generated";

const animationEasingCssValues = {
	[DtoAnimationEasing.EASE]: "ease",
	[DtoAnimationEasing.LINEAR]: "linear",
	[DtoAnimationEasing.EASE_IN]: "ease-in",
	[DtoAnimationEasing.EASE_OUT]: "ease-out",
	[DtoAnimationEasing.EASE_IN_OUT]: "ease-in-out",
	[DtoAnimationEasing.STEP_START]: "step-start",
	[DtoAnimationEasing.STEP_END]: "step-end"
};

export class AbsoluteLayout extends AbstractLegacyComponent<DtoAbsoluteLayout> implements DtoAbsoluteLayoutCommandHandler {
	private $main: HTMLElement;

	constructor(config: DtoAbsoluteLayout, serverChannel: ServerChannel) {
		super(config, serverChannel);
		this.$main = parseHtml(`<div class="AbsoluteLayout">
</div>`);
		this.update(config.components, 0, DtoAnimationEasing.EASE);
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

	update(components: DtoAbsolutePositionedComponent[], animationDuration: number, easing: DtoAnimationEasing): void {
		Array.from(this.$main.querySelectorAll(":scope > :not(style)")).forEach(c => c.remove());

		components
			.map(c => c.component)
			.forEach((c: Component) => {
				const mainDomElementElement = c.getMainElement();
				mainDomElementElement.removeEventListener("transitionend", this.transitionEndEventListener);
				this.$main.appendChild(mainDomElementElement);

				let childId = mainDomElementElement.getAttribute("absolute-layout-child-id");
				if (!childId) {
					mainDomElementElement.setAttribute("absolute-layout-child-id", generateUUID());
				}
			});

		this.$main.offsetWidth; // trigger reflow

		this.setStyle("*", {"transition": `transition: top ${animationDuration}ms ${animationEasingCssValues[easing]}, right ${animationDuration}ms ${animationEasingCssValues[easing]}, bottom ${animationDuration}ms ${animationEasingCssValues[easing]}, left ${animationDuration}ms ${animationEasingCssValues[easing]}, width ${animationDuration}ms ${animationEasingCssValues[easing]}, height ${animationDuration}ms ${animationEasingCssValues[easing]};`})
		components.forEach(c => {
			const component = c.component as AbstractLegacyComponent;
			component.getMainElement().addEventListener('transitionend', this.transitionEndEventListener);
			// TODO when having many different (ever changing) children, this will generate a lot of CSS rules. Clean old ones up!
			this.setStyle(`> [absolute-layout-child-id=${component.getMainElement().getAttribute("absolute-layout-child-id")}]`, {
				top: `${c.position.topCss != null ? c.position.topCss : "initial"}`,
				right: `${c.position.rightCss != null ? c.position.rightCss : "initial"}`,
				bottom: `${c.position.bottomCss != null ? c.position.bottomCss : "initial"}`,
				left: `${c.position.leftCss != null ? c.position.leftCss : "initial"}`,
				width: `${c.position.widthCss != null ? c.position.widthCss : "initial"}`,
				height: `${c.position.heightCss != null ? c.position.heightCss : "initial"}`,
				"z-index": `${c.position.zIndex != null ? c.position.zIndex : "initial"}`
			});
		});
	}

}


