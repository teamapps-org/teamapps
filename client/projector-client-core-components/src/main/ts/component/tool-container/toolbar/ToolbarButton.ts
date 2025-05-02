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
import {DropDown} from "../../../micro-components/DropDown";
import {DtoToolbarButton as DtoToolbarButton} from "../../../generated/DtoToolbarButton";
import {
	Component,
	enterFullScreen,
	exitFullScreen,
	generateUUID,
	isFullScreen,
	parseHtml,
	prependChild,
	ProjectorEvent,
	Template
} from "projector-client-object-api";
import {Toolbar} from "./Toolbar";
import {AbstractToolContainer} from "../AbstractToolContainer";
import {DropDownButtonClickInfo} from "../../../generated";

export class ToolbarButton {

	public readonly onClick: ProjectorEvent<DropDownButtonClickInfo> = new ProjectorEvent();

	private $buttonWrapper: HTMLElement;
	private $button: HTMLElement;
	private $dropDownCaret: HTMLElement;
	public optimizedWidth: number;
	private visible: boolean;

	private hasDropDown: boolean;
	private dropDown: DropDown;
	private dropDownComponent: Component;

	private uuidClass: string = `DtoToolbarButton-${generateUUID()}`;
	private $styleTag: HTMLStyleElement;

	constructor(public config: DtoToolbarButton) {
		this.$buttonWrapper = parseHtml(`<div class="toolbar-button-wrapper ${this.uuidClass}" data-buttonId="${config.id}">
	<div class="toolbar-button-caret ${config.hasDropDown ? '' : 'hidden'}">
	  <div class="caret"></div>
	</div>
	<style></style>
</div>`);
		if (this.config.debuggingId != null) {
			this.$buttonWrapper.setAttribute("data-teamapps-debugging-id", this.config.debuggingId);
		}
		this.$button = parseHtml((config.template as Template).render(config.recordData));
		prependChild(this.$buttonWrapper, this.$button);
		this.$dropDownCaret = this.$buttonWrapper.querySelector<HTMLElement>(":scope .toolbar-button-caret");
		this.optimizedWidth = AbstractToolContainer.optimizeButtonWidth(this.$buttonWrapper, this.$button, (config.template as any).maxHeight);
		this.$styleTag = this.$buttonWrapper.querySelector(":scope style");
		this.updateStyles();
		this.setVisible(config.visible);
		this.setHasDropDown(config.hasDropDown);
		this.setDropDownComponent(config.dropDownComponent as Component);

		this.$buttonWrapper.addEventListener("mousedown", (e) => {
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

			let dropdownClickInfo: DropDownButtonClickInfo = null;
			if (this.hasDropDown) {
				if (this.dropDown == null) {
					this.dropDown = new DropDown(this.dropDownComponent?.getMainElement());
				}
				dropdownClickInfo = {
					opening: !this.dropDown.isOpen,
					contentSet: this.dropDownComponent != null
				};
				if (!this.dropDown.isOpen) {
					this.dropDown.setContentComponent(this.dropDownComponent?.getMainElement());
					this.dropDown.open({$reference: this.$buttonWrapper, width: config.dropDownPanelWidth});
				} else {
					this.dropDown.close();
				}
			}

			setTimeout(() => this.onClick.fire(dropdownClickInfo)); // make sure that any blur event is processed before this!
		});
	}

	public getMainDomElement() {
		return this.$buttonWrapper;
	}

	setDropDownComponent(component: Component) {
		this.dropDownComponent = component;
		if (this.dropDown != null) {
			this.dropDown.setContentComponent(component?.getMainElement());
			if ((component as any).onItemClicked) {
				((component as any).onItemClicked as ProjectorEvent<any>).addListener(eventObject => {
					this.dropDown.close();
				});
			}
		}
	}

	getDropDownComponent() {
		return this.dropDownComponent;
	}

	setVisible(visible: boolean) {
		this.visible = visible;
		this.getMainDomElement().classList.toggle("hidden", !visible);
	}

	isVisible(): boolean {
		return this.visible;
	}

	public setHasDropDown(hasDropDown: boolean) {
		this.hasDropDown = hasDropDown;
		this.$dropDownCaret.classList.toggle("hidden", !hasDropDown);
	}

	get id() {
		return this.config.id;
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

	closeDropDown() {
		this.dropDown.close();
	}
}
