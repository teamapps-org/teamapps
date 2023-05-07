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

import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {DtoComponent} from "../generated/DtoComponent";
import {DtoNavigationBarButton} from "../generated/DtoNavigationBarButton";
import {AbstractComponent} from "teamapps-client-core";
import {ClickOutsideHandle, doOnceOnClickOutsideElement, outerHeightIncludingMargins, parseHtml} from "./Common";
import {TeamAppsUiContext} from "teamapps-client-core";
import {
	UiNavigationBar_ButtonClickedEvent,
	UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent,
	UiNavigationBarCommandHandler,
	DtoNavigationBar,
	UiNavigationBarEventSource
} from "../generated/DtoNavigationBar";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";
import {UiMultiProgressDisplay} from "./UiDefaultMultiProgressDisplay";

interface Button {
	data: any;
	$button: HTMLElement;
}

export class UiNavigationBar extends AbstractComponent<DtoNavigationBar> implements UiNavigationBarCommandHandler, UiNavigationBarEventSource {

	public readonly onButtonClicked: TeamAppsEvent<UiNavigationBar_ButtonClickedEvent> = new TeamAppsEvent();
	public readonly onFanoutClosedDueToClickOutsideFanout: TeamAppsEvent<UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent> = new TeamAppsEvent();

	private $bar: HTMLElement;
	private $buttonsContainer: HTMLElement;
	private buttons: { [id: string]: Button } = {};
	private buttonTemplateRenderer: Template;
	private $fanOutContainerWrapper: HTMLElement;
	private $fanOutContainer: HTMLElement;
	private fanOutComponents: UiComponent[] = [];
	private currentFanOutComponent: UiComponent<DtoComponent>;
	private fanoutClickOutsideHandle: ClickOutsideHandle;
	private multiProgressDisplay: UiMultiProgressDisplay;
	private $multiProgressDisplayContainer: HTMLElement;

	constructor(config: DtoNavigationBar) {
		super(config);

		this.buttonTemplateRenderer = context.templateRegistry.createTemplateRenderer(config.buttonTemplate, null);

		this.$bar = parseHtml(`<div class="UiNavigationBar">
                <div class="fan-out-container-wrapper teamapps-blurredBackgroundImage">
                    <div class="fan-out-container"></div>
                </div>
                <div class="buttons-container">
                	<div class="progress-container"></div>
				</div>
            </div>`);
		this.$buttonsContainer = this.$bar.querySelector<HTMLElement>(":scope >.buttons-container");
		this.$multiProgressDisplayContainer = this.$buttonsContainer.querySelector<HTMLElement>(":scope > .progress-container");
		this.$fanOutContainerWrapper = this.$bar.querySelector<HTMLElement>(":scope >.fan-out-container-wrapper");
		this.$fanOutContainer = this.$fanOutContainerWrapper.querySelector<HTMLElement>(":scope >.fan-out-container");

		this.setBackgroundColor(config.backgroundColor);
		this.setBorderColor(config.borderColor);
		if (config.fanOutComponents) {
			config.fanOutComponents.forEach(c => this.addFanOutComponent(c as UiComponent));
		}

		this.setMultiProgressDisplay(config.multiProgressDisplay as UiMultiProgressDisplay);

		config.buttons.forEach(button => this.addButton(button));
	}

	public setBackgroundColor(color: string) {
		this.$buttonsContainer.style.backgroundColor = color;
	}

	public setBorderColor(color: string) {
		this.$bar.style.borderColor = color;
	}

	public doGetMainElement(): HTMLElement {
		return this.$bar;
	}

	setButtons(buttons: DtoNavigationBarButton[]): void {
		this.$buttonsContainer.innerHTML = '';
		this.buttons = {};
		buttons.forEach(button => this.addButton(button));
	}

	private addButton(button: DtoNavigationBarButton) {
		let $button = parseHtml(`<div class="nav-button-wrapper"><div class="nav-button-inner-wrapper"></div></div>`);
		let $innerWrapper = $button.querySelector<HTMLElement>(":scope .nav-button-inner-wrapper");
		$innerWrapper.appendChild(parseHtml(this.buttonTemplateRenderer.render(button.data)));
		$button.addEventListener("click", () => {
			this.onButtonClicked.fire({
				buttonId: button.id,
				visibleFanOutComponentId: this.currentFanOutComponent && (this.currentFanOutComponent as AbstractComponent).getId()
			});
		});
		this.buttons[button.id] = {
			data: button.data,
			$button: $button
		};
		this.setButtonVisible(button.id, button.visible);
		this.$buttonsContainer.append($button);
	}

	public setButtonVisible(buttonId: string, visible: boolean) {
		let button = this.buttons[buttonId];
		if (button) {
			button.$button.classList.toggle("hidden", !visible);
		}
	}

	public addFanOutComponent(fanOutComponent: UiComponent) {
		if (this.fanOutComponents.indexOf(fanOutComponent) === -1) {
			fanOutComponent.getMainElement().classList.add("pseudo-hidden");
			this.$fanOutContainer.appendChild(fanOutComponent.getMainElement());
			this.fanOutComponents.push(fanOutComponent);
		}
	};

	public removeFanOutComponent(fanOutComponent: UiComponent) {
		fanOutComponent.getMainElement().remove();
		this.fanOutComponents = this.fanOutComponents.filter(c => c !== fanOutComponent);
	};

	public showFanOutComponent(fanOutComponent: UiComponent) {
		this.addFanOutComponent(fanOutComponent);
		const showFanout = () => {
			this.currentFanOutComponent = fanOutComponent;
			this.fanOutComponents.forEach(c => c.getMainElement().classList.add("pseudo-hidden"));
			this.currentFanOutComponent.getMainElement().classList.remove("pseudo-hidden");
			this.$fanOutContainerWrapper.classList.add("open");
			this.$fanOutContainerWrapper.style.bottom = outerHeightIncludingMargins(this.$bar) + "px";
			slideDown(this.$fanOutContainerWrapper);
			this.onResize();
			this.fanoutClickOutsideHandle = doOnceOnClickOutsideElement([this.getMainElement(), this.$fanOutContainerWrapper], e => {
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
		slideUp(this.$fanOutContainerWrapper);
		this.currentFanOutComponent = null;
		this.fanoutClickOutsideHandle.cancel();
	}

	public onResize(): void {
		if (this.$fanOutContainerWrapper.classList.contains("open")) {
			let $clippingParent = this.findNearestParentWithHiddenVerticalOverflow(this.$fanOutContainerWrapper);
			
			let maxFanOutHeight = this.$bar.offsetTop - $clippingParent.offsetTop;
			if (maxFanOutHeight <= 0) {
				console.warn("Fanout height is 0 due to clipping parent component...");
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

	public setMultiProgressDisplay(multiProgressDisplay: UiMultiProgressDisplay): void {
		this.multiProgressDisplay && this.multiProgressDisplay.getMainElement().remove();
		this.multiProgressDisplay = multiProgressDisplay;
		multiProgressDisplay && this.$multiProgressDisplayContainer.appendChild(multiProgressDisplay.getMainElement());
	}

}


