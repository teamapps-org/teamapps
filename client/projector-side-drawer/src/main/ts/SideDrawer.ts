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
	type Component,
	parseHtml,
	prependChild,
	ProjectorEvent,
	removeClassesByFunction,
	type ServerObjectChannel
} from "projector-client-object-api";
import {
	type DrawerPosition,
	DrawerPositions,
	type DtoSideDrawer,
	type DtoSideDrawer_ExpandedOrCollapsedEvent,
	type DtoSideDrawerCommandHandler,
	type DtoSideDrawerEventSource
} from "./generated";

export class SideDrawer extends AbstractComponent<DtoSideDrawer> implements DtoSideDrawerCommandHandler, DtoSideDrawerEventSource {

	public readonly onExpandedOrCollapsed: ProjectorEvent<DtoSideDrawer_ExpandedOrCollapsedEvent> = new ProjectorEvent();

	private containerComponent: Component;
	private contentComponent: Component | null = null;
	private $main: HTMLElement;

	private $expanderHandle: HTMLElement;

	constructor(config: DtoSideDrawer, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this.containerComponent = config.containerComponent as Component;

		this.$main = parseHtml(`<div class="SideDrawer"></div>`);

		this.setContentComponent(config.contentComponent);
		this.$expanderHandle = parseHtml(`<div class="expander-handle"></div>`);
		this.$expanderHandle.addEventListener("click", () => {
			this.setExpanded(!this.config.expanded);
			this.onExpandedOrCollapsed.fire({expanded: this.config.expanded});
		});
		this.$main.appendChild(this.$expanderHandle);

		prependChild(this.containerComponent.getMainElement(), this.getMainElement());

		this.setBackgroundColor(config.backgroundColor ?? null);
		this.setExpanderHandleColor(config.expanderHandleColor ?? null);

		const resizeObserver = new ResizeObserver(_ => {
			this.updateFloatingPosition();
		});
		resizeObserver.observe(this.containerComponent.getMainElement());

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
			this.getMainElement().style.width = '';
			this.getMainElement().style.maxWidth = (containerWidth - 2 * this.config.marginX) + "px";
		} else if (this.config.width === 0) {
			this.getMainElement().style.width = (containerWidth - 2 * this.config.marginX) + "px";
			this.getMainElement().style.maxWidth = '';
		} else {
			this.getMainElement().style.width = this.config.width + "px";
			this.getMainElement().style.maxWidth = '';
		}

		let contentWidth = this.contentComponent?.getMainElement()?.offsetWidth ?? 0;
		// let contentHeight = this.contentComponent?.getMainElement()?.offsetHeight ?? 0;

		// position X
		if (this.config.expanded) {
			if (this.config.position === DrawerPositions.TOP_LEFT || this.config.position === DrawerPositions.BOTTOM_LEFT) {
				this.getMainElement().style.left = this.config.marginX + "px";
				this.getMainElement().style.right = '';
			} else if (this.config.position === DrawerPositions.TOP_RIGHT || this.config.position === DrawerPositions.BOTTOM_RIGHT) {
				this.getMainElement().style.left = '';
				this.getMainElement().style.right = this.config.marginX + "px";
			}
		} else {
			if (this.config.position === DrawerPositions.TOP_LEFT || this.config.position === DrawerPositions.BOTTOM_LEFT) {
				this.getMainElement().style.left = `-${contentWidth}px`;
				this.getMainElement().style.right = '';
			} else if (this.config.position === DrawerPositions.TOP_RIGHT || this.config.position === DrawerPositions.BOTTOM_RIGHT) {
				this.getMainElement().style.left = '';
				this.getMainElement().style.right = `-${contentWidth}px`;
			}
		}

		// position Y
		if (this.config.position === DrawerPositions.TOP_LEFT || this.config.position === DrawerPositions.TOP_RIGHT) {
			this.getMainElement().style.top = this.config.marginY + "px";
			this.getMainElement().style.bottom = '';
		} else if (this.config.position === DrawerPositions.BOTTOM_LEFT || this.config.position === DrawerPositions.BOTTOM_RIGHT) {
			this.getMainElement().style.top = '';
			this.getMainElement().style.bottom = this.config.marginY + "px";
		}
		// height
		if (this.config.height === -1) {
			this.getMainElement().style.height = '';
			this.getMainElement().style.maxHeight = (containerHeight - 2 * this.config.marginY) + "px";
		} else if (this.config.height === 0) {
			this.getMainElement().style.height = (containerHeight - 2 * this.config.marginY) + "px";
			this.getMainElement().style.maxHeight = '';
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

	setBackgroundColor(backgroundColor: string | null) {
		this.getMainElement().style.backgroundColor = backgroundColor ?? '';
	}

	setExpanderHandleColor(expanderHandleColor: string | null) {
		this.$expanderHandle.style.backgroundColor = expanderHandleColor ?? '';
	}
}


