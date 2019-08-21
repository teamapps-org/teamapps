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

import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiFloatingComponentCommandHandler, UiFloatingComponentConfig} from "../generated/UiFloatingComponentConfig";
import {UiComponent} from "./UiComponent";
import ResizeObserver from 'resize-observer-polyfill';
import {parseHtml, prependChild, removeClassesByFunction} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {UiFloatingComponentPosition} from "../generated/UiFloatingComponentPosition";

export class UiFloatingComponent extends AbstractUiComponent<UiFloatingComponentConfig> implements UiFloatingComponentCommandHandler {
	private containerComponent: UiComponent;
	private contentComponent: UiComponent;
	private $main: HTMLElement;

	private expanderLatch: HTMLElement;

	constructor(config: UiFloatingComponentConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.containerComponent = config.containerComponent as UiComponent;
		this.contentComponent = config.contentComponent as UiComponent;

		this.$main = parseHtml(`<div class="UiFloatingComponent"></div>`);

		this.$main.appendChild(this.contentComponent.getMainDomElement());
		this.expanderLatch = parseHtml(`<div class="expander-latch"></div>`);
		this.expanderLatch.addEventListener("click", evt => {
			this.setExpanded(!this._config.expanded);
		});
		this.$main.appendChild(this.expanderLatch);

		prependChild(this.containerComponent.getMainDomElement(), this.getMainDomElement());

		this.getMainDomElement().style.backgroundColor = createUiColorCssString(config.backgroundColor);

		const resizeObserver = new ResizeObserver(entries => {
			for (let entry of entries) {
				this.updateFloatingPosition();
			}
		});
		resizeObserver.observe(this.containerComponent.getMainDomElement());
		resizeObserver.observe(this.contentComponent.getMainDomElement());

		this.$main.addEventListener("click", ev => ev.stopPropagation());
		this.$main.addEventListener("pointerdown", ev => ev.stopPropagation());
		this.$main.addEventListener("mousedown", ev => ev.stopPropagation());
		this.$main.addEventListener("mouseclick", ev => ev.stopPropagation());
		this.$main.addEventListener("dblclick", ev => ev.stopPropagation());
		this.$main.addEventListener("scroll", ev => ev.stopPropagation());
		this.$main.addEventListener("wheel", ev => ev.stopPropagation());
		this.$main.addEventListener("keydown", ev => ev.stopPropagation());
		this.$main.addEventListener("keyup", ev => ev.stopPropagation());
		this.$main.addEventListener("keypress", ev => ev.stopPropagation());

		this.updateFloatingPosition();
	}

	private updateFloatingPosition() {
		removeClassesByFunction(this.expanderLatch.classList, className => className.startsWith("position-"));
		this.expanderLatch.classList.add("position-" + UiFloatingComponentPosition[this._config.position].toLowerCase().replace('_', '-'));

		let containerWidth = this.containerComponent.getMainDomElement().offsetWidth;
		let containerHeight = this.containerComponent.getMainDomElement().offsetHeight;

		// width
		if (this._config.width === -1) {
			this.getMainDomElement().style.width = null;
			this.getMainDomElement().style.maxWidth = (containerWidth - 2 * this._config.marginX) + "px";
		} else if (this._config.width === 0) {
			this.getMainDomElement().style.width = (containerWidth - 2 * this._config.marginX) + "px";
			this.getMainDomElement().style.maxWidth = null;
		} else {
			this.getMainDomElement().style.width = this._config.width + "px";
			this.getMainDomElement().style.maxWidth = null;
		}

		let contentWidth = this.contentComponent.getMainDomElement().offsetWidth;
		let contentHeight = this.contentComponent.getMainDomElement().offsetHeight;

		// position X
		if (this._config.expanded) {
			if (this._config.position === UiFloatingComponentPosition.TOP_LEFT || this._config.position === UiFloatingComponentPosition.BOTTOM_LEFT) {
				this.getMainDomElement().style.left = this._config.marginX + "px";
				this.getMainDomElement().style.right = null;
			} else if (this._config.position === UiFloatingComponentPosition.TOP_RIGHT || this._config.position === UiFloatingComponentPosition.BOTTOM_RIGHT) {
				this.getMainDomElement().style.left = null;
				this.getMainDomElement().style.right = this._config.marginX + "px";
			}
		} else {
			if (this._config.position === UiFloatingComponentPosition.TOP_LEFT || this._config.position === UiFloatingComponentPosition.BOTTOM_LEFT) {
				this.getMainDomElement().style.left = `-${contentWidth}px`;
				this.getMainDomElement().style.right = null;
			} else if (this._config.position === UiFloatingComponentPosition.TOP_RIGHT || this._config.position === UiFloatingComponentPosition.BOTTOM_RIGHT) {
				this.getMainDomElement().style.left = null;
				this.getMainDomElement().style.right = `-${contentWidth}px`;
			}
		}

		// position Y
		if (this._config.position === UiFloatingComponentPosition.TOP_LEFT || this._config.position === UiFloatingComponentPosition.TOP_RIGHT) {
			this.getMainDomElement().style.top = this._config.marginY + "px";
			this.getMainDomElement().style.bottom = null;
		} else if (this._config.position === UiFloatingComponentPosition.BOTTOM_LEFT || this._config.position === UiFloatingComponentPosition.BOTTOM_RIGHT) {
			this.getMainDomElement().style.top = null;
			this.getMainDomElement().style.bottom = this._config.marginY + "px";
		}
		// height
		if (this._config.height === -1) {
			this.getMainDomElement().style.height = null;
			this.getMainDomElement().style.maxHeight = (containerHeight - 2 * this._config.marginY) + "px";
		} else if (this._config.height === 0) {
			this.getMainDomElement().style.height = (containerHeight - 2 * this._config.marginY) + "px";
			this.getMainDomElement().style.maxHeight = null;
		} else {
			this.getMainDomElement().style.height = this._config.height + "px";
			this.getMainDomElement().style.maxHeight = (containerHeight - 2 * this._config.marginY) + "px";
		}
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	setExpanded(expanded: boolean): void {
		this._config.expanded = expanded;
		this.$main.classList.toggle("expanded", expanded);
		this.updateFloatingPosition();
	}

	setPosition(position: UiFloatingComponentPosition): void {
		this._config.position = position;
		this.updateFloatingPosition();
	}

	setDimensions(width: number, height: number): void {
		this._config.width = width;
		this._config.height = height;
		this.updateFloatingPosition();
	}

	setMargins(marginX: number, marginY: number): void {
		this._config.marginX = marginX;
		this._config.marginY = marginY;
		this.updateFloatingPosition();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiFloatingComponent", UiFloatingComponent);