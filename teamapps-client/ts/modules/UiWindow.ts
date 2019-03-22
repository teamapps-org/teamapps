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
import {UiPanel} from "./UiPanel";
import {UiPanelHeaderFieldConfig} from "../generated/UiPanelHeaderFieldConfig";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiWindowCommandHandler, UiWindowConfig, UiWindowEventSource} from "../generated/UiWindowConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {keyCodes} from "trivial-components";
import {UiColorConfig} from "../generated/UiColorConfig";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiToolButton} from "./micro-components/UiToolButton";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiPanel_WindowButtonClickedEvent} from "../generated/UiPanelConfig";
import {EventFactory} from "../generated/EventFactory";
import {UiWindowButtonType} from "../generated/UiWindowButtonType";

export interface UiWindowListener {
	onWindowClosed: (window: UiWindow, animationDuration: number) => void;
}

export class UiWindow extends UiComponent<UiWindowConfig> implements UiWindowCommandHandler, UiWindowEventSource {

	public readonly onWindowButtonClicked: TeamAppsEvent<UiPanel_WindowButtonClickedEvent> = new TeamAppsEvent(this);

	private $main: JQuery;
	private $panelWrapper: JQuery;
	private listener: UiWindowListener;
	private panel: UiPanel;

	private escapeKeyListener: (e: KeyboardEvent) => void;
	private clickOutsideListener: (e: MouseEvent) => void;
	private closeable: boolean;
	private closeOnEscape: boolean;
	private closeOnClickOutside: boolean;
	private modal: boolean;
	private modalBackgroundDimmingColor: UiColorConfig;

	constructor(config: UiWindowConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = $(`<div class="UiWindow offscreen">
	<div class="panel-wrapper">
	</div>
</div>`);
		this.$panelWrapper = this.$main.find(">.panel-wrapper");

		this.panel = new UiPanel(config, context);
		this.panel.getMainDomElement().appendTo(this.$panelWrapper);

		if (config.closeable) {
			this.panel.addWindowButton(UiWindowButtonType.CLOSE);
		}
		this.panel.getWindowButton(UiWindowButtonType.CLOSE).onClicked.addListener(() => this.close(500));
		this.panel.onWindowButtonClicked.addListener(eventObject => this.onWindowButtonClicked.fire(EventFactory.createUiPanel_WindowButtonClickedEvent(this.getId(), eventObject.windowButton)));

		this.setSize(config.width, config.height);
		this.setModal(config.modal);
		this.setModalBackgroundDimmingColor(config.modalBackgroundDimmingColor);
		this.setCloseable(config.closeable);
		this.setCloseOnEscape(config.closeOnEscape);
		this.setCloseOnClickOutside(config.closeOnClickOutside);
	}

	protected onAttachedToDom() {
		if (this.panel) this.panel.attachedToDom = true;
	}

	public show(animationDuration: number) {
		this.$main.css({
			transition: `opacity ${animationDuration}ms`
		});
		this.$panelWrapper.css({
			transition: `box-shadow ${animationDuration}ms, transform ${animationDuration}ms`
		});
		this.$main.removeClass("offscreen");

		this.escapeKeyListener = (e) => {
			if (this.closeOnEscape && e.keyCode === keyCodes.escape) {
				this.close(500);
			}
		};
		document.body.addEventListener("keydown", this.escapeKeyListener, {capture: true});

		this.clickOutsideListener = (e) => {
			if (this.closeOnClickOutside && e.target === this.$main[0]) {
				this.close(500);
			}
		};
		this.$main[0].addEventListener("click", this.clickOutsideListener)
	}

	public hide(animationDuration: number) {
		this.$main.css({
			transition: `opacity ${animationDuration}ms`
		});
		this.$panelWrapper.css({
			transition: `box-shadow ${animationDuration}ms, transform ${animationDuration}ms`
		});
		this.$main.addClass("offscreen");
		this.$main.css({
			"pointer-events": "none"
		});
		this.removeCloseEventListeners();
	}

	private removeCloseEventListeners() {
		if (this.escapeKeyListener) {
			document.body.removeEventListener("keydown", this.escapeKeyListener, {capture: true})
		}
		if (this.clickOutsideListener) {
			this.$main[0].removeEventListener("click", this.clickOutsideListener)
		}
	}

	public getMainDomElement(): JQuery {
		return this.$main;
	}

	public setListener(listener: UiWindowListener) {
		this.listener = listener;
	}

	public close(animationDuration: number) {
		this.listener.onWindowClosed(this, animationDuration);
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

	public setCloseOnClickOutside(closeOnClickOutside: boolean): void {
		this.closeOnClickOutside = closeOnClickOutside;
	}

	public setCloseOnEscape(closeOnEscape: boolean): void {
		this.closeOnEscape = closeOnEscape;
	}

	public setCloseable(closeable: boolean): void {
		this.closeable = closeable;
		if (closeable) {
			this.panel.addWindowButton(UiWindowButtonType.CLOSE);
		} else {
			this.panel.removeWindowButton(UiWindowButtonType.CLOSE);
		}
	}

	public setModalBackgroundDimmingColor(modalBackgroundDimmingColor: UiColorConfig): void {
		this.modalBackgroundDimmingColor = modalBackgroundDimmingColor;
		this.$main.css("background-color", createUiColorCssString(modalBackgroundDimmingColor));
	}

	public setModal(modal: boolean) {
		this.modal = modal;
		this.$main.toggleClass('modal', this.modal);
	}

	public isModal() {
		return this.modal;
	}

	public setSize(width: number, height: number): void {
		this.$panelWrapper.css({
			width: width ? width + "px" : "100%",
			height: height ? height + "px" : "100%"
		});
	}

	public setStretchContent(stretch: boolean): void {
		this.panel.setStretchContent(stretch);
	}

	public onResize(): void {
		this.panel.reLayout();
	}

	public destroy(): void {
		this.removeCloseEventListeners();
	}



	setWindowButtons(windowButtons: UiWindowButtonType[]): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiWindow", UiWindow);
