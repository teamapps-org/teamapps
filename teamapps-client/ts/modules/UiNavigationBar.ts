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
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiNavigationBarButtonConfig} from "../generated/UiNavigationBarButtonConfig";
import {UiComponent} from "./UiComponent";
import {ClickOutsideHandle, doOnceOnClickOutsideElement, Renderer} from "./Common";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {
	UiNavigationBar_ButtonClickedEvent,
	UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent,
	UiNavigationBarCommandHandler,
	UiNavigationBarConfig,
	UiNavigationBarEventSource
} from "../generated/UiNavigationBarConfig";
import {EventFactory} from "../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {UiColorConfig} from "../generated/UiColorConfig";

interface Button {
	data: any;
	$button: JQuery;
}

export class UiNavigationBar extends UiComponent<UiNavigationBarConfig> implements UiNavigationBarCommandHandler, UiNavigationBarEventSource {

	public readonly onButtonClicked: TeamAppsEvent<UiNavigationBar_ButtonClickedEvent> = new TeamAppsEvent(this);
	public readonly onFanoutClosedDueToClickOutsideFanout: TeamAppsEvent<UiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent> = new TeamAppsEvent(this);

	private $bar: JQuery;
	private $buttonsWrapper: JQuery;
	private buttons: { [id: string]: Button } = {};
	private buttonTemplateRenderer: Renderer;
	private $fanOutContainerWrapper: JQuery;
	private $fanOutContainer: JQuery;
	private fanOutComponents: UiComponent[] = [];
	private currentFanOutComponent: UiComponent<UiComponentConfig>;
	private fanoutClickOutsideHandle: ClickOutsideHandle;

	constructor(config: UiNavigationBarConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.buttonTemplateRenderer = context.templateRegistry.createTemplateRenderer(config.buttonTemplate, null);

		this.$bar = $(`<div class="UiNavigationBar">
                <div class="fan-out-container-wrapper teamapps-blurredBackgroundImage">
                    <div class="fan-out-container"/>
                </div>
                <div class="buttons-wrapper"/>
            </div>`);
		this.$buttonsWrapper = this.$bar.find(">.buttons-wrapper");
		this.$fanOutContainerWrapper = this.$bar.find(">.fan-out-container-wrapper");
		this.$fanOutContainer = this.$fanOutContainerWrapper.find(">.fan-out-container");
		this.setBackgroundColor(config.backgroundColor);
		this.setBorderColor(config.borderColor);
		if (config.fanOutComponents) {
			config.fanOutComponents.forEach(c => this.addFanOutComponent(c));
		}

		config.buttons.forEach(button => this.addButton(button));

		this.reLayout();
	}

	public setBackgroundColor(color: UiColorConfig) {
		this.$buttonsWrapper.css("background-color", createUiColorCssString(color));
	}

	public setBorderColor(color: UiColorConfig) {
		this.$bar.css("border-color", createUiColorCssString(color));
	}

	public getMainDomElement(): JQuery {
		return this.$bar;
	}

	protected onAttachedToDom() {
		// do nothing.
	}

	setButtons(buttons: UiNavigationBarButtonConfig[]): void {
		this.$buttonsWrapper[0].innerHTML = '';
		this.buttons = {};
		buttons.forEach(button => this.addButton(button));
	}

	private addButton(button: UiNavigationBarButtonConfig) {
		let $button = $(`<div class="nav-button-wrapper"><div class="nav-button-inner-wrapper"></div></div>`);
		let $innerWrapper = $button.find(".nav-button-inner-wrapper");
		$innerWrapper.append(this.buttonTemplateRenderer.render(button.data));
		$button.click(() => {
			this.onButtonClicked.fire(EventFactory.createUiNavigationBar_ButtonClickedEvent(this.getId(), button.id, this.currentFanOutComponent && this.currentFanOutComponent.getId()));
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
			button.$button.toggleClass("hidden", !visible);
		}
	}

	public addFanOutComponent(fanOutComponent: UiComponent) {
		if (this.fanOutComponents.indexOf(fanOutComponent) === -1) {
			fanOutComponent.getMainDomElement().addClass("pseudo-hidden").appendTo(this.$fanOutContainer);
			this.fanOutComponents.push(fanOutComponent);
		}
	};

	public removeFanOutComponent(fanOutComponent: UiComponent) {
		fanOutComponent.getMainDomElement().detach();
		this.fanOutComponents = this.fanOutComponents.filter(c => c !== fanOutComponent);
	};

	public showFanOutComponent(fanOutComponent: UiComponent) {
		this.addFanOutComponent(fanOutComponent);
		const showFanout = () => {
			this.currentFanOutComponent = fanOutComponent;
			this.fanOutComponents.forEach(c => c.getMainDomElement().addClass("pseudo-hidden"));
			this.currentFanOutComponent.getMainDomElement().removeClass("pseudo-hidden");
			this.currentFanOutComponent.attachedToDom = true;
			this.$fanOutContainerWrapper.addClass("open");
			this.$fanOutContainerWrapper.css("bottom", this.$bar.outerHeight(true) + "px");
			this.$fanOutContainerWrapper.slideDown(200);
			this.onResize();
			this.fanoutClickOutsideHandle = doOnceOnClickOutsideElement(this.getMainDomElement().add(this.$fanOutContainerWrapper), e => {
				this.hideFanOutComponent();
				this.onFanoutClosedDueToClickOutsideFanout.fire(EventFactory.createUiNavigationBar_FanoutClosedDueToClickOutsideFanoutEvent(this.getId()));
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
		this.$fanOutContainerWrapper.removeClass("open");
		this.$fanOutContainerWrapper.slideUp(200);
		this.currentFanOutComponent = null;
		this.fanoutClickOutsideHandle.cancel();
	}

	public onResize(): void {
		if (this.$fanOutContainerWrapper.is(".open")) {
			let $clippingParent = this.findNearestParentWithHiddenVerticalOverflow(this.$fanOutContainerWrapper);
			let maxFanOutHeight = this.$bar.offset().top - $clippingParent.offset().top;
			if (maxFanOutHeight <= 0) {
				this.logger.warn("Fanout height is 0 due to clipping parent component...");
			}
			this.$fanOutContainerWrapper.css("max-height", maxFanOutHeight + "px")[0].offsetHeight;
			this.currentFanOutComponent && this.currentFanOutComponent.reLayout();
		}
	}

	private findNearestParentWithHiddenVerticalOverflow(child: JQuery): JQuery {
		let el = child[0];
		while (el != null) {
			el = el.parentElement;
			if ($(el).css("overflow-y") !== "visible") {
				break;
			}
		}
		return $(el);
	}

	public destroy(): void {
		// do nothing
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiNavigationBar", UiNavigationBar);
