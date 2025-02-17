/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import {UiPanel} from "./UiPanel";
import {UiPanelHeaderFieldConfig} from "../generated/UiPanelHeaderFieldConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiWindow_ClosedEvent, UiWindowCommandHandler, UiWindowConfig, UiWindowEventSource} from "../generated/UiWindowConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {keyCodes} from "./trivial-components/TrivialCore";
import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiToolButton} from "./micro-components/UiToolButton";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiPanel_WindowButtonClickedEvent} from "../generated/UiPanelConfig";
import {UiWindowButtonType} from "../generated/UiWindowButtonType";
import {animateCSS, Constants, css, parseHtml} from "./Common";
import {UiComponent} from "./UiComponent";
import {UiExitAnimation} from "../generated/UiExitAnimation";
import {UiEntranceAnimation} from "../generated/UiEntranceAnimation";
import {viewport} from "@popperjs/core";

export class UiWindow extends AbstractUiComponent<UiWindowConfig> implements UiWindowCommandHandler, UiWindowEventSource {

	public readonly onWindowButtonClicked: TeamAppsEvent<UiPanel_WindowButtonClickedEvent> = new TeamAppsEvent();
	public readonly onClosed: TeamAppsEvent<UiWindow_ClosedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $panelWrapper: HTMLElement;
	private $resizers: HTMLElement[];
	private panel: UiPanel;

	private escapeKeyListener: (e: KeyboardEvent) => void;
	private clickOutsideListener: (e: MouseEvent) => void;
	private windowResizeListener: (e: Event) => void;

	constructor(config: UiWindowConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiWindow">
	<div class="panel-wrapper">
		<div class="resizer corner t l"></div>
		<div class="resizer corner t r"></div>
		<div class="resizer corner b l"></div>
		<div class="resizer corner b r"></div>
		<div class="resizer t"></div>
		<div class="resizer b"></div>
		<div class="resizer l"></div>
		<div class="resizer r"></div>
	</div>
</div>`);
		this.$panelWrapper = this.$main.querySelector<HTMLElement>(":scope >.panel-wrapper");
		this.$resizers = Array.from(this.$panelWrapper.querySelectorAll(":scope > .resizer"));

		this.panel = new UiPanel(config, context);
		this.$panelWrapper.appendChild(this.panel.getMainElement());

		if (config.closeable) {
			this.panel.addWindowButton(UiWindowButtonType.CLOSE);
		}
		this.panel.getWindowButton(UiWindowButtonType.CLOSE).onClicked.addListener(() => this.close(500, true));
		this.panel.onWindowButtonClicked.addListener(eventObject => {
			return this.onWindowButtonClicked.fire({
				windowButton: eventObject.windowButton
			});
		});

		this.setSize(config.width, config.height);
		this.setModal(config.modal);
		this.setModalBackgroundDimmingColor(config.modalBackgroundDimmingColor);
		this.setCloseable(config.closeable);
		this.setCloseOnEscape(config.closeOnEscape);
		this.setCloseOnClickOutside(config.closeOnClickOutside);

		const $panelHeading = this.panel.getMainElement().querySelector(":scope > .panel-heading")
		$panelHeading.addEventListener("mousedown", (e: MouseEvent) => this.handleTitlebarMousedown(e))

		for (let resizer of this.$resizers) {                   
			resizer.addEventListener("mousedown", (e: MouseEvent) => this.handleResizerMousedown(resizer, e))
		}

		this.windowResizeListener = () => this.assureInViewPort();
		window.addEventListener("resize", this.windowResizeListener);

		this.setResizable(this._config.resizable);
	}



	public show(animationDuration: number) {
		document.body.appendChild(this.getMainElement());

		this.$main.classList.remove("hidden");
		this.$main.classList.add("open");
		animateCSS(this.$panelWrapper, Constants.ENTRANCE_ANIMATION_CSS_CLASSES[UiEntranceAnimation.ZOOM_IN], animationDuration);

		this.escapeKeyListener = (e) => {
			if (this._config.closeOnEscape && e.keyCode === keyCodes.escape) {
				this.close(animationDuration, true);
			}
		};
		document.body.addEventListener("keydown", this.escapeKeyListener, {capture: true});

		this.clickOutsideListener = (e) => {
			if (this._config.closeOnClickOutside && e.target === this.$main) {
				this.close(animationDuration, true);
			}
		};
		this.$main.addEventListener("click", this.clickOutsideListener)
	}

	private removeEventListeners() {
		if (this.escapeKeyListener) {
			document.body.removeEventListener("keydown", this.escapeKeyListener, {capture: true});
		}
		if (this.clickOutsideListener) {
			this.$main.removeEventListener("click", this.clickOutsideListener);
		}
		window.removeEventListener("resize", this.windowResizeListener);
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public close(animationDuration: number, fireEvent: boolean = false) {
		this.setMaximized(false);
		this.$main.classList.remove("open");
		animateCSS(this.$panelWrapper, Constants.EXIT_ANIMATION_CSS_CLASSES[UiExitAnimation.ZOOM_OUT], animationDuration, () => {
			this.$main.classList.add("hidden");
			this.getMainElement().remove();
		});
		this.removeEventListeners();
		if (fireEvent) {
			this.onClosed.fire({});
		}
	}

	public setContent(content: UiComponent) {
		this.panel.setContent(content);
	}

	public setLeftHeaderField(field: UiPanelHeaderFieldConfig) {
		this.panel.setLeftHeaderField(field);
	}

	public setRightHeaderField(field: UiPanelHeaderFieldConfig) {
		this.panel.setRightHeaderField(field);
	}

	public setToolbar(toolbar: UiToolbar): void {
		this.panel.setToolbar(toolbar);
	}

	public setMaximized(maximized: boolean): void {
		this.panel.setMaximized(maximized);
	}

	public setToolButtons(toolButtons: UiToolButton[]): void {
		this.panel.setToolButtons(toolButtons);
	}

	public setIcon(icon: string) {
		this.panel.setIcon(icon);
	}

	public setTitle(title: string) {
		this.panel.setTitle(title);
	}

	public setBadge(badge: string) {
		this.panel.setBadge(badge);
	}

	public setCloseOnClickOutside(closeOnClickOutside: boolean): void {
		this._config.closeOnClickOutside = closeOnClickOutside;
	}

	public setCloseOnEscape(closeOnEscape: boolean): void {
		this._config.closeOnEscape = closeOnEscape;
	}

	public setCloseable(closeable: boolean): void {
		this._config.closeable = closeable;
		if (closeable) {
			this.panel.addWindowButton(UiWindowButtonType.CLOSE);
		} else {
			this.panel.removeWindowButton(UiWindowButtonType.CLOSE);
		}
	}

	public setModalBackgroundDimmingColor(modalBackgroundDimmingColor: string): void {
		this._config.modalBackgroundDimmingColor = modalBackgroundDimmingColor;
		this.$main.style.backgroundColor = modalBackgroundDimmingColor;
	}

	public setModal(modal: boolean) {
		this._config.modal = modal;
		this.$main.classList.toggle('modal', modal);
	}

	public setSize(width: number, height: number): void {
		css(this.$panelWrapper, {
			width: width ? width + "px" : "100%",
			height: height === 0 ? "100%" : height < 0 ? "auto" : height + "px"
		});
	}

	public setStretchContent(stretch: boolean): void {
		this.panel.setStretchContent(stretch);
	}

	public destroy(): void {
		super.destroy();
		this.removeEventListeners();
	}


	setWindowButtons(windowButtons: UiWindowButtonType[]): void {
	}

	private handleTitlebarMousedown(e: MouseEvent) {
		if (!this._config.movable) {
			return;
		}
		const initialRect = getBoundingPageRect(this.$panelWrapper);
		const initialMouseX = e.pageX
		const initialMouseY = e.pageY

		const mousemove = (e: MouseEvent) => {
			const totalDeltaX = e.pageX - initialMouseX;
			const totalDeltaY = e.pageY - initialMouseY;

			const viewPortRect = getViewportRect();

			const minX = this._config.keepInViewport ? viewPortRect.left : Number.NEGATIVE_INFINITY;
			const maxX = this._config.keepInViewport ? viewPortRect.right - initialRect.width : Number.POSITIVE_INFINITY;
			const minY = this._config.keepInViewport ? viewPortRect.top : Number.NEGATIVE_INFINITY;
			const maxY = this._config.keepInViewport ? viewPortRect.bottom - initialRect.height : Number.POSITIVE_INFINITY;

			this.$panelWrapper.style.left = Math.min(maxX, Math.max(minX, initialRect.x + totalDeltaX)) + "px"
			this.$panelWrapper.style.top = Math.min(maxY, Math.max(minY, initialRect.y + totalDeltaY)) + "px"
		}

		const mouseup = () => {
			window.removeEventListener("mousemove", mousemove)
			window.removeEventListener("mouseup", mouseup)
		}

		window.addEventListener("mousemove", mousemove)
		window.addEventListener("mouseup", mouseup)
	}

	private handleResizerMousedown(resizer: HTMLElement, e: MouseEvent) {
		if (!this._config.resizable) {
			return;
		}
		const initialRect = getBoundingPageRect(this.$panelWrapper);
		const initialMouseX = e.pageX
		const initialMouseY = e.pageY

		const mousemove = (e: MouseEvent) => {
			const totalDeltaX = e.pageX - initialMouseX;
			const totalDeltaY = e.pageY - initialMouseY;

			const viewPortRect = getViewportRect();

			if (resizer.classList.contains("t")) {
				const minYDueToViewPort = this._config.keepInViewport ? viewPortRect.top : Number.NEGATIVE_INFINITY;
				const maxYDueToMinHeight = initialRect.bottom - this._config.minHeight;

				const newTop = Math.max(minYDueToViewPort, Math.min(maxYDueToMinHeight, initialRect.y + totalDeltaY));
				const newHeight = Math.max(this._config.minHeight, initialRect.bottom - newTop);

				this.$panelWrapper.style.top = newTop + "px"
				this.$panelWrapper.style.height = newHeight + "px"
			}
			if (resizer.classList.contains("b")) {
				const maxHeight = this._config.keepInViewport ? viewPortRect.bottom - initialRect.top : Number.POSITIVE_INFINITY;
				const newHeight = Math.min(maxHeight, Math.max(this._config.minHeight, initialRect.height + totalDeltaY));
				this.$panelWrapper.style.height = newHeight + "px";
			}
			if (resizer.classList.contains("l")) {
				const minXDueToViewPort = this._config.keepInViewport ? viewPortRect.left : Number.NEGATIVE_INFINITY;
				const maxXDueToMinWidth = initialRect.right - this._config.minWidth;

				const newLeft = Math.max(minXDueToViewPort, Math.min(maxXDueToMinWidth, initialRect.x + totalDeltaX));
				const newWidth = Math.max(this._config.minWidth, initialRect.right - newLeft);

				this.$panelWrapper.style.left = newLeft + "px"
				this.$panelWrapper.style.width = newWidth + "px"
			}
			if (resizer.classList.contains("r")) {
				const maxWidth = this._config.keepInViewport ? viewPortRect.right - initialRect.left : Number.POSITIVE_INFINITY;
				const newWidth = Math.min(maxWidth, Math.max(this._config.minWidth, initialRect.width + totalDeltaX));
				this.$panelWrapper.style.width = newWidth + "px";
			}
		}

		const mouseup = () => {
			window.removeEventListener("mousemove", mousemove)
			window.removeEventListener("mouseup", mouseup)
		}

		window.addEventListener("mousemove", mousemove)
		window.addEventListener("mouseup", mouseup)
	}

	private assureInViewPort() {
		const windowRect = getBoundingPageRect(this.$panelWrapper);
		const viewPortRect = getViewportRect();

		if (windowRect.right > viewPortRect.right) {
			this.$panelWrapper.style.left = viewPortRect.right - windowRect.width + "px";
		}
		if (windowRect.bottom > viewPortRect.bottom) {
			this.$panelWrapper.style.top = viewPortRect.bottom - windowRect.height + "px";
		}
	}

	public setResizable(resizable: boolean) {
		this._config.resizable = resizable;
		this.$panelWrapper.classList.toggle("resizable", resizable);
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiWindow", UiWindow);


function getBoundingPageRect(element: HTMLElement) {
	const rect = element.getBoundingClientRect();
	const scrollLeft = window.scrollX || document.documentElement.scrollLeft;
	const scrollTop = window.scrollY || document.documentElement.scrollTop;

	return {
		left: rect.left + scrollLeft,
		top: rect.top + scrollTop,
		x: rect.left + scrollLeft,
		y: rect.top + scrollTop,
		right: rect.right + scrollLeft,
		bottom: rect.bottom + scrollTop,
		width: rect.width,
		height: rect.height
	};
}

function getViewportRect() {
	const scrollLeft = window.scrollX || document.documentElement.scrollLeft;
	const scrollTop = window.scrollY || document.documentElement.scrollTop;
	return {
		left: scrollLeft,
		top: scrollTop,
		x: scrollLeft,
		y: scrollTop,
		right: scrollLeft + window.innerWidth,
		bottom: scrollTop + window.innerHeight,
		width: window.innerWidth,
		height: window.innerHeight
	};
}

