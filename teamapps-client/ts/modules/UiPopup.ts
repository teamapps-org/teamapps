/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiPopupCommandHandler, UiPopupConfig} from "../generated/UiPopupConfig";
import {parseHtml} from "./Common";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";

export class UiPopup extends AbstractUiComponent<UiPopupConfig> implements UiPopupCommandHandler {

	private contentComponent: UiComponent;
	private $main: HTMLElement;
	private $componentWrapper: HTMLElement;

	constructor(config: UiPopupConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiPopup">
	<div class="component-wrapper"></div>
</div>`);

		this.$componentWrapper = this.$main.querySelector(":scope .component-wrapper");

		this.contentComponent = config.contentComponent as UiComponent;
		this.$componentWrapper.appendChild(this.contentComponent.getMainElement());

		this.setBackgroundColor(config.backgroundColor);
		this.setDimmingColor(config.dimmingColor);
		this.setDimensions(config.width, config.height);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	setBackgroundColor(backgroundColor: string): void {
		this._config.backgroundColor = backgroundColor;
		this.$componentWrapper.style.backgroundColor = backgroundColor;
	}

	setDimmingColor(dimmingColor: string): void {
		this._config.dimmingColor = dimmingColor;
		this.$main.style.backgroundColor = dimmingColor;
	}

	setDimensions(width: number, height: number): void {
		this._config.width = width;
		this._config.height = height;
		this.updatePosition();
	}

	setPosition(x: number, y: number) {
		this._config.x = x;
		this._config.y = y;
		this.updatePosition();
	}

	@executeWhenFirstDisplayed()
	private updatePosition() {
		let containerWidth = this.$main.parentElement && this.$main.parentElement.offsetWidth;
		let containerHeight = this.$main.parentElement && this.$main.parentElement.offsetHeight;
		// width
		if (this._config.width === -1) {
			this.$main.style.width = null;
			this.$main.style.maxWidth = containerWidth + "px";
		} else if (this._config.width === 0) {
			this.$main.style.width = containerWidth + "px";
			this.$main.style.maxWidth = null;
		} else {
			this.$main.style.width = this._config.width + "px";
			this.$main.style.maxWidth = containerWidth + "px";
		}
		// height
		if (this._config.height === -1) {
			this.$main.style.height = null;
			this.$main.style.maxHeight = containerHeight + "px";
		} else if (this._config.height === 0) {
			this.$main.style.height = containerHeight + "px";
			this.$main.style.maxHeight = null;
		} else {
			this.$main.style.height = this._config.height + "px";
			this.$main.style.maxHeight = containerHeight + "px";
		}
		
		this.$componentWrapper.style.left = this._config.x + "px";
		this.$componentWrapper.style.top = this._config.y + "px";
	}


	onResize(): void {
		this.updatePosition();
	}

	close(): void {
		this.getMainElement().remove();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiPopup", UiPopup);
