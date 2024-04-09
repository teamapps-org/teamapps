/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {UiWindowCommandHandler, UiWindowConfig, UiWindowEventSource, UiWindow_ClosedEvent} from "../generated/UiWindowConfig";
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

export class UiWindow extends AbstractUiComponent<UiWindowConfig> implements UiWindowCommandHandler, UiWindowEventSource {

	public readonly onWindowButtonClicked: TeamAppsEvent<UiPanel_WindowButtonClickedEvent> = new TeamAppsEvent();
	public readonly onClosed: TeamAppsEvent<UiWindow_ClosedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $panelWrapper: HTMLElement;
	private panel: UiPanel;

	private escapeKeyListener: (e: KeyboardEvent) => void;
	private clickOutsideListener: (e: MouseEvent) => void;

	constructor(config: UiWindowConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiWindow">
	<div class="panel-wrapper"></div>
</div>`);
		this.$panelWrapper = this.$main.querySelector<HTMLElement>(":scope >.panel-wrapper");

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

	private removeBodyClickAndEscapeListeners() {
		if (this.escapeKeyListener) {
			document.body.removeEventListener("keydown", this.escapeKeyListener, {capture: true})
		}
		if (this.clickOutsideListener) {
			this.$main.removeEventListener("click", this.clickOutsideListener)
		}
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
		this.removeBodyClickAndEscapeListeners();
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
		this.removeBodyClickAndEscapeListeners();
	}



	setWindowButtons(windowButtons: UiWindowButtonType[]): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiWindow", UiWindow);
