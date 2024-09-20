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
	AbstractLegacyComponent, Component,
	parseHtml,
	prependChild,
	removeClassesByFunction,
	ServerObjectChannel,
	TeamAppsEvent
} from "projector-client-object-api";
import {
	DrawerPosition,
	DtoSideDrawer,
	DtoSideDrawer_ExpandedOrCollapsedEvent,
	DtoSideDrawerCommandHandler,
	DtoSideDrawerEventSource
} from "./generated";

export class SideDrawer extends AbstractLegacyComponent<DtoSideDrawer> implements DtoSideDrawerCommandHandler, DtoSideDrawerEventSource {

	public readonly onExpandedOrCollapsed: TeamAppsEvent<DtoSideDrawer_ExpandedOrCollapsedEvent> = new TeamAppsEvent();

	private containerComponent: Component;
	private contentComponent: Component;
	private $main: HTMLElement;

	private $expanderHandle: HTMLElement;

	constructor(config: DtoSideDrawer, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.containerComponent = config.containerComponent as Component;

		this.$main = parseHtml(`<div class="SideDrawer"></div>`);

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
			if (this.config.position === DrawerPosition.TOP_LEFT || this.config.position === DrawerPosition.BOTTOM_LEFT) {
				this.getMainElement().style.left = this.config.marginX + "px";
				this.getMainElement().style.right = null;
			} else if (this.config.position === DrawerPosition.TOP_RIGHT || this.config.position === DrawerPosition.BOTTOM_RIGHT) {
				this.getMainElement().style.left = null;
				this.getMainElement().style.right = this.config.marginX + "px";
			}
		} else {
			if (this.config.position === DrawerPosition.TOP_LEFT || this.config.position === DrawerPosition.BOTTOM_LEFT) {
				this.getMainElement().style.left = `-${contentWidth}px`;
				this.getMainElement().style.right = null;
			} else if (this.config.position === DrawerPosition.TOP_RIGHT || this.config.position === DrawerPosition.BOTTOM_RIGHT) {
				this.getMainElement().style.left = null;
				this.getMainElement().style.right = `-${contentWidth}px`;
			}
		}

		// position Y
		if (this.config.position === DrawerPosition.TOP_LEFT || this.config.position === DrawerPosition.TOP_RIGHT) {
			this.getMainElement().style.top = this.config.marginY + "px";
			this.getMainElement().style.bottom = null;
		} else if (this.config.position === DrawerPosition.BOTTOM_LEFT || this.config.position === DrawerPosition.BOTTOM_RIGHT) {
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
		this.contentComponent = contentComponent as Component;
		if (contentComponent != null) {
			this.$main.appendChild(this.contentComponent.getMainElement());
		}
	}

	setExpanded(expanded: boolean): void {
		this.config.expanded = expanded;
		this.$main.classList.toggle("expanded", expanded);
		this.updateFloatingPosition();
	}

	setPosition(position: DrawerPosition): void {
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


