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
import {DtoToolbarButtonGroup as DtoToolbarButtonGroup} from "../../../generated/DtoToolbarButtonGroup";
import {OrderedDictionary} from "../../../util/OrderedDictionary";
import {ToolbarButton} from "./ToolbarButton";
import {Component, insertAfter, insertBefore, parseHtml, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";

import {DtoToolbarButton as DtoToolbarButton} from "../../../generated/DtoToolbarButton";
import {ButtonVisibilities, Toolbar} from "./Toolbar";
import {DtoDropDownButtonClickInfo} from "../../../generated";
import {outerWidthIncludingMargins} from "../../../Common";

export class ToolbarButtonGroup {
	public readonly onButtonClicked: TeamAppsEvent<{buttonId: string, dropDownButtonClickInfo: DtoDropDownButtonClickInfo}> = new TeamAppsEvent();

	private config: DtoToolbarButtonGroup;
	private visible: boolean = true;
	private $buttonGroupWrapper: HTMLElement;
	private $buttonGroup: HTMLElement;
	private buttons: OrderedDictionary<ToolbarButton>;
	private $separator: HTMLElement;
	private buttonsShiftedToOverflowDropDown: ToolbarButton[] = [];

	constructor(buttonGroupConfig: DtoToolbarButtonGroup, private toolbar: Toolbar) {
		const $buttonGroupWrapper = parseHtml('<div class="button-group-wrapper"></div>');

		const $buttonGroup = parseHtml(`<div class="toolbar-button-group"></div>`);
		$buttonGroupWrapper.appendChild($buttonGroup);

		this.config = buttonGroupConfig;
		this.$buttonGroupWrapper = $buttonGroupWrapper;
		this.$buttonGroup = $buttonGroup;
		this.buttons = new OrderedDictionary<ToolbarButton>();

		for (let j = 0; j < buttonGroupConfig.buttons.length; j++) {
			const buttonConfig: DtoToolbarButton = buttonGroupConfig.buttons[j];
			const button = this.createButton(buttonConfig);
			this.buttons.push(buttonConfig.buttonId, button);
			$buttonGroup.append(button.getMainDomElement());
		}

		if (buttonGroupConfig.showGroupSeparator) {
			this.$separator = parseHtml('<div class="toolbar-group-separator">');
			$buttonGroupWrapper.append(this.$separator);
		}

		this.setVisible(buttonGroupConfig.visible);


	}

	public getId() {
		return this.config.groupId;
	}

	public get position() {
		return this.config.position;
	}

	private createButton(buttonConfig: DtoToolbarButton) {
		const button = new ToolbarButton(buttonConfig);
		button.onClicked.addListener(dropDownButtonClickInfo => this.onButtonClicked.fire({buttonId: buttonConfig.buttonId, dropDownButtonClickInfo}));
		return button;
	}

	public setDropDownComponent(buttonId: string, component: Component) {
		this.buttons.getValue(buttonId).setDropDownComponent(component);
	}

	public setButtonVisible(buttonId: string, visible: boolean) {
		const button = this.buttons.getValue(buttonId);
		if (button) {
			button.setVisible(visible);
			this.updateVisibility();
		}
	}

	public addButton(buttonConfig: DtoToolbarButton, neighborButtonId: string, beforeNeighbor: boolean) {
		const button = this.createButton(buttonConfig);

		const existingButton = this.buttons.getValue(buttonConfig.buttonId);
		if (existingButton) {
			this.removeButton(buttonConfig.buttonId);
		}

		const neighborButton = this.buttons.getValue(neighborButtonId);
		if (neighborButton) {
			if (beforeNeighbor) {
				insertBefore(button.getMainDomElement(), neighborButton.getMainDomElement());
				this.buttons.insertBeforeValue(buttonConfig.buttonId, button, neighborButton);
			} else {
				insertAfter(button.getMainDomElement(), neighborButton.getMainDomElement());
				this.buttons.insertAfterValue(buttonConfig.buttonId, button, neighborButton);
			}
		} else {
			this.$buttonGroup.appendChild(button.getMainDomElement());
			this.buttons.push(buttonConfig.buttonId, button);
		}

		this.updateVisibility();
	}

	public removeButton(buttonId: string): void {
		const button = this.buttons.getValue(buttonId);

		if (button) {
			this.buttons.remove(buttonId);
			button.getMainDomElement().remove();
		}

		// TODO destroy dropdown

		this.updateVisibility();
	}

	public setVisible(visible: boolean) {
		this.visible = visible;
		this.updateVisibility();
	}

	public isVisible() {
		return this.visible;
	}

	private updateVisibility() {
		let hasVisibleButton = this.buttons.values.some(button => {
			return button.isVisible() && this.buttonsShiftedToOverflowDropDown.indexOf(button) === -1;
		});
		this.$buttonGroupWrapper.classList.toggle("pseudo-hidden", !(this.visible && hasVisibleButton));
	}

	setButtonColors(buttonId: string, backgroundColor: string, hoverBackgroundColor: string) {
		this.buttons.getValue(buttonId).setColors(backgroundColor, hoverBackgroundColor);
	}

	public calculateButtonVisibilities(availableWidth: number): ButtonVisibilities {
		let info: ButtonVisibilities = {
			fittingButtons: [],
			nonFittingButtons: [],
			hiddenButtons: []
		};
		let usedWidth = this.$separator ? outerWidthIncludingMargins(this.$separator) : 0;
		this.buttons.values.forEach((button) => {
			if (this.visible && button.isVisible()) {
				if (usedWidth + button.optimizedWidth <= availableWidth) {
					this.buttonsShiftedToOverflowDropDown = this.buttonsShiftedToOverflowDropDown.filter(b => b !== button);
					info.fittingButtons.push({groupId: this.config.groupId, buttonId: button.id, button});
				} else {
					this.buttonsShiftedToOverflowDropDown.push(button);
					info.nonFittingButtons.push({groupId: this.config.groupId, buttonId: button.id, button});
				}
				usedWidth += button.optimizedWidth;
			} else {
				this.buttonsShiftedToOverflowDropDown = this.buttonsShiftedToOverflowDropDown.filter(b => b !== button);
				info.hiddenButtons.push({groupId: this.config.groupId, buttonId: button.id, button});
			}
		});
		this.updateVisibility();
		return info;
	}

	public getMainDomElement(): HTMLElement {
		return this.$buttonGroupWrapper;
	}

	public getButtonConfigs() {
		return this.buttons.values.map(b => b.config);
	}

	setButtonHasDropDown(buttonId: string, hasDropDown: boolean) {
		this.buttons.getValue(buttonId).setHasDropDown(hasDropDown);
	}

	public getConfig() {
		return this.config;
	}

	closeDropDown(buttonId: string) {
		this.buttons.getValue(buttonId)?.closeDropDown();
	}
}
