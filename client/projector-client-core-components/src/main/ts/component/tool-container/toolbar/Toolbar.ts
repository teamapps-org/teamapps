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
	insertAfter,
	insertBefore,
	noOpServerObjectChannel, outerWidthIncludingMargins,
	parseHtml,
	ServerObjectChannel,
	TeamAppsEvent
} from "projector-client-object-api";
import {
	DtoAbstractToolContainer_ToolbarButtonClickEvent,
	DtoToolbar,
	DtoToolbarButtonGroupPosition,
	DtoToolbarCommandHandler,
	DtoToolbarEventSource
} from "../../../generated";
import {DtoToolbarButtonGroup as DtoToolbarButtonGroup} from "../../../generated/DtoToolbarButtonGroup";
import {DtoToolbarButton as DtoToolbarButton} from "../../../generated/DtoToolbarButton";
import {ToolAccordion} from "../tool-accordion/ToolAccordion";
import {AbstractToolContainer} from "../AbstractToolContainer";
import {DropDown} from "../../../micro-components/DropDown";
import {Emptyable} from "../../../util/Emptyable";
import {ToolbarButton} from "./ToolbarButton";
import {ToolbarButtonGroup} from "./ToolbarButtonGroup";

interface FQButtonId {
	groupId: string,
	buttonId: string,
	button: ToolbarButton
}

export type ButtonVisibilities = { fittingButtons: FQButtonId[], nonFittingButtons: FQButtonId[], hiddenButtons: FQButtonId[] };

export class Toolbar extends AbstractToolContainer<DtoToolbar> implements Emptyable, DtoToolbarCommandHandler, DtoToolbarEventSource {

	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent<boolean>();

	public readonly onToolbarButtonClick: TeamAppsEvent<DtoAbstractToolContainer_ToolbarButtonClickEvent> = new TeamAppsEvent();

	public static DEFAULT_TOOLBAR_MAX_HEIGHT = 70;

	private buttonGroupsById: { [id: string]: ToolbarButtonGroup } = {};
	private leftButtonGroups: ToolbarButtonGroup[] = [];
	private rightButtonGroups: ToolbarButtonGroup[] = [];

	private _$toolbar: HTMLElement;
	private _$backgroundColorDiv: HTMLElement;
	private _$innerContainer: HTMLElement;
	private $toolbarFiller: HTMLElement;
	private _$logo: HTMLElement;
	private overflowDropDown: DropDown;
	private overflowToolAccordion: ToolAccordion;
	private $overflowDropDownButton: HTMLElement;
	private totalWidthOfOverflowButtons: number;

	constructor(config: DtoToolbar, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
		this._$toolbar = parseHtml(`<div class="Toolbar teamapps-blurredBackgroundImage"></div>`);
		this._$backgroundColorDiv = parseHtml('<div class="background-color-div"></div>');
		this._$toolbar.appendChild(this._$backgroundColorDiv);
		this._$innerContainer = parseHtml('<div class="inner-container">');
		this._$backgroundColorDiv.appendChild(this._$innerContainer);

		this.$toolbarFiller = parseHtml('<div class="toolbar-filler">');
		this._$innerContainer.appendChild(this.$toolbarFiller);

		if (config.logoImage) {
			this.setLogoImage(config.logoImage);
		}

		this.$overflowDropDownButton = parseHtml('<div class="overflow-dropdown-button toolbar-button-wrapper"><div class="caret"></div></div>');
		this._$innerContainer.appendChild(this.$overflowDropDownButton);
		this.overflowDropDown = new DropDown();
		this.overflowToolAccordion = this.createDropDownAccordion();
		this.overflowDropDown.setContentComponent(this.overflowToolAccordion?.getMainElement());
		this.$overflowDropDownButton.addEventListener("mousedown", () => {
			if (!this.overflowDropDown.isOpen) {
				this.overflowDropDown.open({
					$reference: this.$overflowDropDownButton,
					width: Math.min(400, this.totalWidthOfOverflowButtons * 1.2)
				});
				this.updateButtonOverflow();
			}
		});

		config.leftButtonGroups && config.leftButtonGroups.forEach(bg => this.addButtonGroup(bg, false))
		config.rightButtonGroups && config.rightButtonGroups.forEach(bg => this.addButtonGroup(bg, true))
	}

	private createDropDownAccordion(): ToolAccordion {
		let accordionConfig = {
			...this.config,
			buttonGroups: [...this.leftButtonGroups, ...this.rightButtonGroups].map(group => {
				return {...group.getConfig(), buttons: group.getButtonConfigs()}
			})
		};
		let toolAccordion = new ToolAccordion(accordionConfig, noOpServerObjectChannel);
		toolAccordion.onToolbarButtonClick.addListener(eventObject => {
			if (!eventObject.dropDownClickInfo) { //
				this.overflowDropDown.close();
			}
			this.onToolbarButtonClick.fire({
				groupId: eventObject.groupId,
				buttonId: eventObject.buttonId,
				dropDownClickInfo: eventObject.dropDownClickInfo
			});
		});
		return toolAccordion;
	}


	public doGetMainElement(): HTMLElement {
		return this._$toolbar;
	}

	public setLogoImage(logoImage: string) {
		if (this._$logo) {
			this._$logo.remove();
			this._$logo = null;
		}
		if (logoImage) {
			this._$logo = parseHtml(`<div class="logo" style="background-image: url('${logoImage}');"></div>`);
			this._$innerContainer.appendChild(this._$logo)
		}
	}

	public setDropDownComponent(groupId: string, buttonId: string, component: Component): void {
		this.buttonGroupsById[groupId].setDropDownComponent(buttonId, component);
		this.overflowToolAccordion.setDropDownComponent(groupId, buttonId, component);
	}

	public setButtonHasDropDown(groupId: string, buttonId: string, hasDropDown: boolean): void {
		this.buttonGroupsById[groupId].setButtonHasDropDown(buttonId, hasDropDown);
	}

	public closeDropDown(groupId: string, buttonId: string) {
		this.buttonGroupsById[groupId].closeDropDown(buttonId);
	}

	public setButtonVisible(groupId: string, buttonId: string, visible: boolean) {
		Object.values(this.buttonGroupsById).forEach(buttonGroup => buttonGroup.setButtonVisible(buttonId, visible));
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.setButtonVisible(groupId, buttonId, visible);
		}
		this.updateButtonOverflow();
	}

	public setButtonGroupVisible(groupId: string, visible: boolean) {
		this.buttonGroupsById[groupId] && this.buttonGroupsById[groupId].setVisible(visible);
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.setButtonGroupVisible(groupId, visible);
		}
		this.updateButtonOverflow();
	}

	public addButtonGroup(groupConfig: DtoToolbarButtonGroup, rightSide: boolean) {
		const existingButtonGroup = this.buttonGroupsById[groupConfig.groupId];
		if (existingButtonGroup) {
			this.removeButtonGroup(groupConfig.groupId);
		}
		let emptyStateChanges = this.empty;

		const buttonGroup = new ToolbarButtonGroup(groupConfig, this);
		buttonGroup.getMainDomElement().classList.toggle("right-side", rightSide);
		buttonGroup.onButtonClicked.addListener(e => {
			return this.onToolbarButtonClick.fire({
				groupId: groupConfig.groupId,
				buttonId: e.buttonId,
				dropDownClickInfo: e.dropDownButtonClickInfo
			});
		});
		this.buttonGroupsById[groupConfig.groupId] = buttonGroup;
		if (rightSide) {
			this.rightButtonGroups.push(buttonGroup);
		} else {
			this.leftButtonGroups.push(buttonGroup);
		}

		this.insertGroupAtCorrectSortingPosition(buttonGroup, groupConfig.position, rightSide);

		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.addButtonGroup(groupConfig);
		}
		this.updateButtonOverflow();

		if (emptyStateChanges) {
			this.onEmptyStateChanged.fire(false);
		}
	}

	private insertGroupAtCorrectSortingPosition(buttonGroup: ToolbarButtonGroup, position: DtoToolbarButtonGroupPosition, rightSide: boolean) {
		let otherButtonGroups = (rightSide ? this.rightButtonGroups : this.leftButtonGroups).sort((group1, group2) => group1.position - group2.position);
		let otherGroupIndex = 0;
		for (; otherGroupIndex < otherButtonGroups.length; otherGroupIndex++) {
			let otherGroup = otherButtonGroups[otherGroupIndex];
			if (otherGroup.position > position) {
				break;
			}
		}

		if (otherButtonGroups[otherGroupIndex]) {
			insertBefore(buttonGroup.getMainDomElement(), otherButtonGroups[otherGroupIndex].getMainDomElement())
		} else if (!rightSide) {
			insertBefore(buttonGroup.getMainDomElement(), this.$toolbarFiller)
		} else {
			insertAfter(buttonGroup.getMainDomElement(), this.$toolbarFiller)
		}
	}

	public removeButtonGroup(groupId: string): void {
		let wasAlreadyEmpty = this.empty;

		const buttonGroup = this.buttonGroupsById[groupId];

		if (buttonGroup) {
			delete this.buttonGroupsById[groupId];
			this.leftButtonGroups = this.leftButtonGroups.filter(g => g.getId() !== groupId);
			this.rightButtonGroups = this.rightButtonGroups.filter(g => g.getId() !== groupId);
			buttonGroup.getMainDomElement().remove();
		}

		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.removeButtonGroup(groupId);
		}
		this.updateButtonOverflow();

		if (this.empty && !wasAlreadyEmpty) {
			this.onEmptyStateChanged.fire(true);
		}
	}

	public addButton(groupId: string, buttonConfig: DtoToolbarButton, neighborButtonId: string, beforeNeighbor: boolean) {
		this.buttonGroupsById[groupId] && this.buttonGroupsById[groupId].addButton(buttonConfig, neighborButtonId, beforeNeighbor);
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.addButton(groupId, buttonConfig, neighborButtonId, beforeNeighbor);
		}
		this.updateButtonOverflow();
	}

	public removeButton(groupId: string, buttonId: string): void {
		Object.values(this.buttonGroupsById).forEach(group => group.removeButton(buttonId));
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.removeButton(groupId, buttonId);
		}
		this.updateButtonOverflow();
	}

	public onResize(): void {
		this.updateButtonOverflow();
	}

	private updateButtonOverflow() {
		let availableWidth = this._$innerContainer.offsetWidth;
		this._$innerContainer.classList.add("overflow-measurement-mode");
		let logoWidth = 0;
		if (this._$logo) {
			this._$logo.classList.remove("hidden");
			logoWidth = outerWidthIncludingMargins(this._$logo);
		}
		this.$overflowDropDownButton.classList.remove("hidden")
		let overflowDropDownButtonWidth = outerWidthIncludingMargins(this.$overflowDropDownButton);
		let consumedWidth = Math.max(logoWidth, overflowDropDownButtonWidth);
		let hasOverflowingButton = false;
		this.totalWidthOfOverflowButtons = 0;
		for (let group of [...this.rightButtonGroups, ...this.leftButtonGroups]) {
			let buttonVisibilityInfo = group.calculateButtonVisibilities(availableWidth - consumedWidth);
			this.totalWidthOfOverflowButtons += buttonVisibilityInfo.nonFittingButtons.reduce((sum, b) => sum + b.button.optimizedWidth, 0);
			buttonVisibilityInfo.fittingButtons.forEach(b => {
				b.button.getMainDomElement().classList.remove("moved-to-overflow-dropdown");
				this.overflowToolAccordion.setButtonVisible(group.getId(), b.buttonId, false);
			});
			buttonVisibilityInfo.nonFittingButtons.forEach(b => {
				b.button.getMainDomElement().classList.add("moved-to-overflow-dropdown");
				this.overflowToolAccordion.setButtonVisible(group.getId(), b.buttonId, true);
				this.overflowToolAccordion.setDropDownComponent(b.groupId, b.buttonId, b.button.getDropDownComponent()); // move to overflow accordion
			});
			buttonVisibilityInfo.hiddenButtons.forEach(b => {
				this.overflowToolAccordion.setButtonVisible(group.getId(), b.buttonId, false);
			});
			consumedWidth += group.isVisible() ? group.getMainDomElement().offsetWidth : 0;
			hasOverflowingButton = hasOverflowingButton || buttonVisibilityInfo.nonFittingButtons.length > 0;
		}
		this.$overflowDropDownButton.classList.toggle("hidden", !hasOverflowingButton);
		if (this._$logo) {
			this._$logo.classList.toggle("hidden", hasOverflowingButton);
		}
		this._$innerContainer.classList.remove("overflow-measurement-mode");
	}

	public get empty() {
		return this.leftButtonGroups.length === 0 && this.rightButtonGroups.length === 0;
	}

	public destroy(): void {
		super.destroy();
		// TODO destroy all dropdowns
	}

	setButtonColors(groupId: string, buttonId: string, backgroundColor: string, hoverBackgroundColor: string): void {
		this.buttonGroupsById[groupId].setButtonColors(buttonId, backgroundColor, hoverBackgroundColor);
	}

}



