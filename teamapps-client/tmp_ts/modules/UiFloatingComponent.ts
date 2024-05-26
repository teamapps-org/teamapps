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
import {DtoFloatingComponent_ExpandedOrCollapsedEvent, DtoFloatingComponentCommandHandler, DtoFloatingComponent, DtoFloatingComponentEventSource} from "../generated/DtoFloatingComponent";
import {UiComponent} from "./UiComponent";
import ResizeObserver from 'resize-observer-polyfill';
import {parseHtml, prependChild, removeClassesByFunction} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiFloatingComponentPosition} from "../generated/UiFloatingComponentPosition";
import {TeamAppsEvent} from "./util/TeamAppsEvent";

export class UiFloatingComponent extends AbstractLegacyComponent<DtoFloatingComponent> implements DtoFloatingComponentCommandHandler, DtoFloatingComponentEventSource {

	public readonly onExpandedOrCollapsed: TeamAppsEvent<DtoFloatingComponent_ExpandedOrCollapsedEvent> = new TeamAppsEvent();

	private containerComponent: UiComponent;
	private contentComponent: UiComponent;
	private $main: HTMLElement;

	private $expanderHandle: HTMLElement;

	constructor(config: DtoFloatingComponent, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this.containerComponent = config.containerComponent as UiComponent;

		this.$main = parseHtml(`<div class="UiFloatingComponent"></div>`);

		this.setContentComponent(config.contentComponent);
		this.$expanderHandle = parseHtml(`<div class="expander-handle"></div>`);
		this.$expanderHandle.addEventListener("click", evt => {
			this.setExpanded(!this.config.expanded);
			this.onExpandedOrCollapsed.fire({expanded: this.config.expanded});
		});
		this.$main.appendChild(this.$expanderHandle);

		prependChild(this.containerComponent.getMainElement(), this.getMainElement());

		this.setBackgroundColor(config.backgroundColor);
		this.setExpanderHandleColor(config.expanderHandleColor);

		const resizeObserver = new ResizeObserver(entries => {
			for (let entry of entries) {
				this.updateFloatingPosition();
			}
		});
		resizeObserver.observe(this.containerComponent.getMainElement());
		resizeObserver.observe(this.contentComponent.getMainElement());

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
		removeClassesByFunction(this.$expanderHandle.classList, className => className.startsWith("position-"));
		this.$expanderHandle.classList.add("position-" + this.config.position);

		let containerWidth = this.containerComponent.getMainElement().offsetWidth;
		let containerHeight = this.containerComponent.getMainElement().offsetHeight;

		// width
		if (this.config.width === -1) {
			this.getMainElement().style.width = null;
			this.getMainElement().style.maxWidth = (containerWidth - 2 * this.config.marginX) + "px";
		} else if (this.config.width === 0) {
			this.getMainElement().style.width = (containerWidth - 2 * this.config.marginX) + "px";
			this.getMainElement().style.maxWidth = null;
		} else {
			this.getMainElement().style.width = this.config.width + "px";
			this.getMainElement().style.maxWidth = null;
		}

		let contentWidth = this.contentComponent.getMainElement().offsetWidth;
		let contentHeight = this.contentComponent.getMainElement().offsetHeight;

		// position X
		if (this.config.expanded) {
			if (this.config.position === UiFloatingComponentPosition.TOP_LEFT || this.config.position === UiFloatingComponentPosition.BOTTOM_LEFT) {
				this.getMainElement().style.left = this.config.marginX + "px";
				this.getMainElement().style.right = null;
			} else if (this.config.position === UiFloatingComponentPosition.TOP_RIGHT || this.config.position === UiFloatingComponentPosition.BOTTOM_RIGHT) {
				this.getMainElement().style.left = null;
				this.getMainElement().style.right = this.config.marginX + "px";
			}
		} else {
			if (this.config.position === UiFloatingComponentPosition.TOP_LEFT || this.config.position === UiFloatingComponentPosition.BOTTOM_LEFT) {
				this.getMainElement().style.left = `-${contentWidth}px`;
				this.getMainElement().style.right = null;
			} else if (this.config.position === UiFloatingComponentPosition.TOP_RIGHT || this.config.position === UiFloatingComponentPosition.BOTTOM_RIGHT) {
				this.getMainElement().style.left = null;
				this.getMainElement().style.right = `-${contentWidth}px`;
			}
		}

		// position Y
		if (this.config.position === UiFloatingComponentPosition.TOP_LEFT || this.config.position === UiFloatingComponentPosition.TOP_RIGHT) {
			this.getMainElement().style.top = this.config.marginY + "px";
			this.getMainElement().style.bottom = null;
		} else if (this.config.position === UiFloatingComponentPosition.BOTTOM_LEFT || this.config.position === UiFloatingComponentPosition.BOTTOM_RIGHT) {
			this.getMainElement().style.top = null;
			this.getMainElement().style.bottom = this.config.marginY + "px";
		}
		// height
		if (this.config.height === -1) {
			this.getMainElement().style.height = null;
			this.getMainElement().style.maxHeight = (containerHeight - 2 * this.config.marginY) + "px";
		} else if (this.config.height === 0) {
			this.getMainElement().style.height = (containerHeight - 2 * this.config.marginY) + "px";
			this.getMainElement().style.maxHeight = null;
		} else {
			this.getMainElement().style.height = this.config.height + "px";
			this.getMainElement().style.maxHeight = (containerHeight - 2 * this.config.marginY) + "px";
		}
	}


	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public setContentComponent(contentComponent: unknown) {
		this.$main.innerHTML = '';
		this.contentComponent = contentComponent as UiComponent;
		if (contentComponent != null) {
			this.$main.appendChild(this.contentComponent.getMainElement());
		}
	}

	setExpanded(expanded: boolean): void {
		this.config.expanded = expanded;
		this.$main.classList.toggle("expanded", expanded);
		this.updateFloatingPosition();
	}

	setPosition(position: UiFloatingComponentPosition): void {
		this.config.position = position;
		this.updateFloatingPosition();
	}

	setDimensions(width: number, height: number): void {
		this.config.width = width;
		this.config.height = height;
		this.updateFloatingPosition();
	}

	setMargins(marginX: number, marginY: number): void {
		this.config.marginX = marginX;
		this.config.marginY = marginY;
		this.updateFloatingPosition();
	}

	setBackgroundColor(backgroundColor: string) {
		this.getMainElement().style.backgroundColor = backgroundColor;
	}

	setExpanderHandleColor(expanderHandleColor: string) {
		this.$expanderHandle.style.backgroundColor = expanderHandleColor;
	}
}

