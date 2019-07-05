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

import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiNavigationBarButtonConfig} from "../generated/UiNavigationBarButtonConfig";
import {UiComponent} from "./UiComponent";
import {ClickOutsideHandle, doOnceOnClickOutsideElement, outerHeightIncludingMargins, parseHtml, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {
	UiNavigationBar_ButtonClickedEvent,
	UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent,
	UiNavigationBarCommandHandler,
	UiNavigationBarConfig,
	UiNavigationBarEventSource
} from "../generated/UiNavigationBarConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {UiColorConfig} from "../generated/UiColorConfig";

interface Button {
	data: any;
	$button: HTMLElement;
}

export class UiNavigationBar extends UiComponent<UiNavigationBarConfig> implements UiNavigationBarCommandHandler, UiNavigationBarEventSource {

	public readonly onButtonClicked: TeamAppsEvent<UiNavigationBar_ButtonClickedEvent> = new TeamAppsEvent(this);
	public readonly onFanoutClosedDueToClickOutsideFanout: TeamAppsEvent<UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent> = new TeamAppsEvent(this);

	private $bar: HTMLElement;
	private $buttonsWrapper: HTMLElement;
	private buttons: { [id: string]: Button } = {};
	private buttonTemplateRenderer: Renderer;
	private $fanOutContainerWrapper: HTMLElement;
	private $fanOutContainer: HTMLElement;
	private fanOutComponents: UiComponent[] = [];
	private currentFanOutComponent: UiComponent<UiComponentConfig>;
	private fanoutClickOutsideHandle: ClickOutsideHandle;

	constructor(config: UiNavigationBarConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.buttonTemplateRenderer = context.templateRegistry.createTemplateRenderer(config.buttonTemplate, null);

		this.$bar = parseHtml(`<div class="UiNavigationBar">
                <div class="fan-out-container-wrapper teamapps-blurredBackgroundImage">
                    <div class="fan-out-container"></div>
                </div>
                <div class="buttons-wrapper"></div>
            </div>`);
		this.$buttonsWrapper = this.$bar.querySelector<HTMLElement>(":scope >.buttons-wrapper");
		this.$fanOutContainerWrapper = this.$bar.querySelector<HTMLElement>(":scope >.fan-out-container-wrapper");
		this.$fanOutContainer = this.$fanOutContainerWrapper.querySelector<HTMLElement>(":scope >.fan-out-container");
		this.setBackgroundColor(config.backgroundColor);
		this.setBorderColor(config.borderColor);
		if (config.fanOutComponents) {
			config.fanOutComponents.forEach(c => this.addFanOutComponent(c as UiComponent));
		}

		config.buttons.forEach(button => this.addButton(button));
	}

	public setBackgroundColor(color: UiColorConfig) {
		this.$buttonsWrapper.style.backgroundColor = createUiColorCssString(color);
	}

	public setBorderColor(color: UiColorConfig) {
		this.$bar.style.borderColor = createUiColorCssString(color);
	}

	public getMainDomElement(): HTMLElement {
		return this.$bar;
	}

	setButtons(buttons: UiNavigationBarButtonConfig[]): void {
		this.$buttonsWrapper.innerHTML = '';
		this.buttons = {};
		buttons.forEach(button => this.addButton(button));
	}

	private addButton(button: UiNavigationBarButtonConfig) {
		let $button = parseHtml(`<div class="nav-button-wrapper"><div class="nav-button-inner-wrapper"></div></div>`);
		let $innerWrapper = $button.querySelector<HTMLElement>(":scope .nav-button-inner-wrapper");
		$innerWrapper.append(this.buttonTemplateRenderer.render(button.data));
		$button.addEventListener("click", () => {
			this.onButtonClicked.fire({
				buttonId: button.id,
				visibleFanOutComponentId: this.currentFanOutComponent && this.currentFanOutComponent.getId()
			});
		});
		this.buttons[button.id] = {
			data: button.data,
			$button: $button
		};
		this.setButtonVisible(button.id, button.visible);
		this.$buttonsWrapper.append($button);
	}

	public setButtonVisible(buttonId: string, visible: boolean) {
		let button = this.buttons[buttonId];
		if (button) {
			button.$button.classList.toggle("hidden", !visible);
		}
	}

	public addFanOutComponent(fanOutComponent: UiComponent) {
		if (this.fanOutComponents.indexOf(fanOutComponent) === -1) {
			fanOutComponent.getMainDomElement().classList.add("pseudo-hidden");
			this.$fanOutContainer.appendChild(fanOutComponent.getMainDomElement());
			this.fanOutComponents.push(fanOutComponent);
		}
	};

	public removeFanOutComponent(fanOutComponent: UiComponent) {
		fanOutComponent.getMainDomElement().remove();
		this.fanOutComponents = this.fanOutComponents.filter(c => c !== fanOutComponent);
	};

	public showFanOutComponent(fanOutComponent: UiComponent) {
		this.addFanOutComponent(fanOutComponent);
		const showFanout = () => {
			this.currentFanOutComponent = fanOutComponent;
			this.fanOutComponents.forEach(c => c.getMainDomElement().classList.add("pseudo-hidden"));
			this.currentFanOutComponent.getMainDomElement().classList.remove("pseudo-hidden");
			this.$fanOutContainerWrapper.classList.add("open");
			this.$fanOutContainerWrapper.style.bottom = outerHeightIncludingMargins(this.$bar) + "px";
			$(this.$fanOutContainerWrapper).slideDown(200);
			this.onResize();
			this.fanoutClickOutsideHandle = doOnceOnClickOutsideElement([this.getMainDomElement(), this.$fanOutContainerWrapper], e => {
				this.hideFanOutComponent();
				this.onFanoutClosedDueToClickOutsideFanout.fire({});
			});
		};
		if (this.currentFanOutComponent) {
			this.hideFanOutComponent();
			setTimeout(showFanout, 210);
		} else {
			showFanout();
		}
	}

	public hideFanOutComponent() {
		this.$fanOutContainerWrapper.classList.remove("open");
		$(this.$fanOutContainerWrapper).slideUp(200);
		this.currentFanOutComponent = null;
		this.fanoutClickOutsideHandle.cancel();
	}

	public onResize(): void {
		if (this.$fanOutContainerWrapper.classList.contains("open")) {
			let $clippingParent = this.findNearestParentWithHiddenVerticalOverflow(this.$fanOutContainerWrapper);
			
			let maxFanOutHeight = this.$bar.offsetTop - $clippingParent.offsetTop;
			if (maxFanOutHeight <= 0) {
				this.logger.warn("Fanout height is 0 due to clipping parent component...");
			}
			this.$fanOutContainerWrapper.style.maxHeight = maxFanOutHeight + "px";
			this.$fanOutContainerWrapper.offsetHeight; // reflow
		}
	}

	private findNearestParentWithHiddenVerticalOverflow(child: HTMLElement): HTMLElement {
		let el = child;
		while (el != null) {
			el = el.parentElement;
			if (getComputedStyle(el).overflowY !== "visible") {
				break;
			}
		}
		return el;
	}

	public destroy(): void {
		// do nothing
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiNavigationBar", UiNavigationBar);
