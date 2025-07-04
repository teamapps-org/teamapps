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
	AbstractComponent,
	type ClickOutsideHandle,
	type Component, doOnceOnClickOutsideElement,
	parseHtml,
	type ServerObjectChannel, slideDown, slideUp,
	ProjectorEvent,
	type Template
} from "projector-client-object-api";
import {
	type DtoNavigationBar,
	type DtoNavigationBar_ButtonClickedEvent, type DtoNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent, type DtoNavigationBarButton,
	type DtoNavigationBarCommandHandler,
	type DtoNavigationBarEventSource
} from "./generated";
import {MultiProgressDisplay} from "projector-progress-display";
import {outerHeightIncludingMargins} from "projector-client-object-api";

interface Button {
	data: any;
	$button: HTMLElement;
}

export class NavigationBar extends AbstractComponent<DtoNavigationBar> implements DtoNavigationBarCommandHandler, DtoNavigationBarEventSource {

	public readonly onButtonClicked: ProjectorEvent<DtoNavigationBar_ButtonClickedEvent> = new ProjectorEvent();
	public readonly onFanoutClosedDueToClickOutsideFanout: ProjectorEvent<DtoNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent> = new ProjectorEvent();

	private $bar: HTMLElement;
	private $buttonsContainer: HTMLElement;
	private buttons: { [id: string]: Button } = {};
	private $fanOutContainerWrapper: HTMLElement;
	private $fanOutContainer: HTMLElement;
	private fanOutComponents: Component[] = [];
	private currentFanOutComponent: Component | null = null;
	private fanoutClickOutsideHandle: ClickOutsideHandle | null = null;
	private multiProgressDisplay: MultiProgressDisplay | null = null;
	private $multiProgressDisplayContainer: HTMLElement;

	constructor(config: DtoNavigationBar, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this.$bar = parseHtml(`<div class="NavigationBar">
                <div class="fan-out-container-wrapper teamapps-blurredBackgroundImage">
                    <div class="fan-out-container"></div>
                </div>
                <div class="buttons-container">
                	<div class="progress-container"></div>
				</div>
            </div>`);
		this.$buttonsContainer = this.$bar.querySelector<HTMLElement>(":scope >.buttons-container")!;
		this.$multiProgressDisplayContainer = this.$buttonsContainer.querySelector<HTMLElement>(":scope > .progress-container")!;
		this.$fanOutContainerWrapper = this.$bar.querySelector<HTMLElement>(":scope >.fan-out-container-wrapper")!;
		this.$fanOutContainer = this.$fanOutContainerWrapper.querySelector<HTMLElement>(":scope >.fan-out-container")!;

		this.setBackgroundColor(config.backgroundColor);
		this.setBorderColor(config.borderColor);
		if (config.fanOutComponents) {
			config.fanOutComponents.forEach(c => this.addFanOutComponent(c as Component));
		}

		this.setMultiProgressDisplay(config.multiProgressDisplay as MultiProgressDisplay);

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
		let $innerWrapper = $button.querySelector<HTMLElement>(":scope .nav-button-inner-wrapper")!;
		$innerWrapper.appendChild(parseHtml((button.template as Template).render(button.data)));
		$button.addEventListener("click", () => {
			this.onButtonClicked.fire({
				buttonId: button.id,
				visibleFanOutComponentId: this.currentFanOutComponent
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

	public addFanOutComponent(fanOutComponent: Component) {
		if (this.fanOutComponents.indexOf(fanOutComponent) === -1) {
			fanOutComponent.getMainElement().classList.add("pseudo-hidden");
			this.$fanOutContainer.appendChild(fanOutComponent.getMainElement());
			this.fanOutComponents.push(fanOutComponent);
		}
	};

	public removeFanOutComponent(fanOutComponent: Component) {
		fanOutComponent.getMainElement().remove();
		this.fanOutComponents = this.fanOutComponents.filter(c => c !== fanOutComponent);
	};

	public showFanOutComponent(fanOutComponent: Component) {
		this.addFanOutComponent(fanOutComponent);
		const showFanout = () => {
			this.currentFanOutComponent = fanOutComponent;
			this.fanOutComponents.forEach(c => c.getMainElement().classList.add("pseudo-hidden"));
			this.currentFanOutComponent.getMainElement().classList.remove("pseudo-hidden");
			this.$fanOutContainerWrapper.classList.add("open");
			this.$fanOutContainerWrapper.style.bottom = outerHeightIncludingMargins(this.$bar) + "px";
			slideDown(this.$fanOutContainerWrapper);
			this.onResize();
			this.fanoutClickOutsideHandle = doOnceOnClickOutsideElement([this.getMainElement(), this.$fanOutContainerWrapper],() => {
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
		this.fanoutClickOutsideHandle?.cancel();
	}

	public onResize(): void {
		if (this.$fanOutContainerWrapper.classList.contains("open")) {
			let $clippingParent = this.findNearestParentWithHiddenVerticalOverflow(this.$fanOutContainerWrapper)!;
			
			let maxFanOutHeight = this.$bar.offsetTop - $clippingParent.offsetTop;
			if (maxFanOutHeight <= 0) {
				console.warn("Fanout height is 0 due to clipping parent component...");
			}
			this.$fanOutContainerWrapper.style.maxHeight = maxFanOutHeight + "px";
			this.$fanOutContainerWrapper.offsetHeight; // reflow
		}
	}

	private findNearestParentWithHiddenVerticalOverflow(child: HTMLElement): HTMLElement | null {
		let el: HTMLElement | null = child;
		while (el != null) {
			el = el.parentElement;
			if (el != null && getComputedStyle(el).overflowY !== "visible") {
				break;
			}
		}
		return el;
	}

	public setMultiProgressDisplay(multiProgressDisplay: MultiProgressDisplay): void {
		this.multiProgressDisplay && this.multiProgressDisplay.getMainElement().remove();
		this.multiProgressDisplay = multiProgressDisplay;
		multiProgressDisplay && this.$multiProgressDisplayContainer.appendChild(multiProgressDisplay.getMainElement());
	}

}


