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
	Component,
	enterFullScreen,
	exitFullScreen,
	generateUUID,
	isFullScreen,
	parseHtml,
	ProjectorEvent,
	Template
} from "projector-client-object-api";
import {DtoToolbarButton as DtoToolbarButton} from "../../../generated/DtoToolbarButton";
import {AbstractToolContainer} from "../AbstractToolContainer";
import {ToolAccordion} from "./ToolAccordion";

export class ToolAccordionButton {

	public readonly onClick: ProjectorEvent<void> = new ProjectorEvent();

	private config: DtoToolbarButton;
	private $buttonWrapper: HTMLElement;
	private $button: HTMLElement;
	private $dropDownCaret: HTMLElement;

	public optimizedWidth: number;
	public dropDownComponent: Component;
	public $dropDown: HTMLElement;
	public $dropDownSourceButtonIndicator: HTMLElement;

	private uuidClass: string = `DtoToolbarButton-${generateUUID()}`;
	private $styleTag: HTMLStyleElement;

	constructor(config: DtoToolbarButton) {
		this.config = config;
		this.$buttonWrapper = parseHtml(`<div class="toolbar-button-wrapper ${this.uuidClass}" data-buttonId="${config.buttonId}" tabindex="0">
	${(this.config.template as Template).render(config.recordData)}
	<div class="toolbar-button-caret ${config.hasDropDown ? '' : 'hidden'}">
	  <div class="caret"></div>
	</div>
	<style></style>
</div>`);
		if (this.config.debuggingId != null) {
			this.$buttonWrapper.setAttribute("data-teamapps-debugging-id", this.config.debuggingId);
		}

		this.$button = this.$buttonWrapper.firstElementChild as HTMLElement;
		this.optimizedWidth = AbstractToolContainer.optimizeButtonWidth(this.$buttonWrapper, this.$button, (config.template as any).maxHeight || ToolAccordion.DEFAULT_TOOLBAR_MAX_HEIGHT);
		this.$dropDownCaret = this.$buttonWrapper.querySelector<HTMLElement>(":scope .toolbar-button-caret");
		this.$buttonWrapper.classList.toggle("hidden", !config.visible);
		this.$styleTag = this.$buttonWrapper.querySelector(":scope style");
		this.updateStyles();

		this.$buttonWrapper.addEventListener('click', () => {
			if (this.config.togglesFullScreenOnComponent) {
				if (isFullScreen()) {
					exitFullScreen();
				} else {
					enterFullScreen((this.config.togglesFullScreenOnComponent as Component).getMainElement());
				}
			}
			if (this.config.startPlaybackComponent) {
				(this.config.startPlaybackComponent as any).play();
			}
			if (this.config.openNewTabWithUrl) {
				window.open(this.config.openNewTabWithUrl, '_blank');
			}

			this.onClick.fire();
		});
	}


	public get id() {
		return this.config.buttonId;
	}

	public get visible() {
		return this.config.visible;
	}

	public set visible(visible: boolean) {
		this.$buttonWrapper.classList.toggle("hidden", !visible)
		this.config.visible = visible;
	}

	public getMainDomElement() {
		return this.$buttonWrapper;
	}

	public set hasDropDown(hasDropDown: boolean) {
		this.config.hasDropDown = hasDropDown;
		this.$dropDownCaret.classList.toggle("hidden", !hasDropDown);
	}

	public get hasDropDown() {
		return this.config.hasDropDown;
	}

	setColors(backgroundColor: string, hoverBackgroundColor: string) {
		this.config.backgroundColor = backgroundColor;
		this.config.hoverBackgroundColor = hoverBackgroundColor;
		this.updateStyles();
	}

	private updateStyles() {
		this.$styleTag.innerHTML = '';
		this.$styleTag.innerText = `
		.${this.uuidClass} {
			background-color: ${(this.config.backgroundColor ?? '')} !important;           
        }
		.${this.uuidClass}:hover {
			background-color: ${(this.config.hoverBackgroundColor ?? '')} !important;            
        }`;
	}
}
