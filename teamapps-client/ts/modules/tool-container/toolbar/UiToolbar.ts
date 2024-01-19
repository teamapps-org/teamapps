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

import {EventSubscription, TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiToolbarCommandHandler, UiToolbarConfig, UiToolbarEventSource} from "../../../generated/UiToolbarConfig";
import {UiToolbarButtonGroupConfig} from "../../../generated/UiToolbarButtonGroupConfig";
import {UiToolbarButtonConfig} from "../../../generated/UiToolbarButtonConfig";
import {UiToolAccordion} from "../tool-accordion/UiToolAccordion";
import {AbstractUiToolContainer} from "../AbstractUiToolContainer";
import {UiDropDown} from "../../micro-components/UiDropDown";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {AbstractUiToolContainer_ToolbarButtonClickEvent} from "../../../generated/AbstractUiToolContainerConfig";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {Emptyable} from "../../util/Emptyable";
import {UiToolbarButton} from "./UiToolbarButton";
import {UiToolbarButtonGroup} from "./UiToolbarButtonGroup";
import {insertAfter, insertBefore, outerWidthIncludingMargins, parseHtml} from "../../Common";
import {UiComponent} from "../../UiComponent";
import {UiToolbarButtonGroupPosition} from "../../../generated/UiToolbarButtonGroupPosition";

interface FQButtonId {
	groupId: string,
	buttonId: string,
	button: UiToolbarButton
}

export type ButtonVisibilities = { fittingButtons: FQButtonId[], nonFittingButtons: FQButtonId[], hiddenButtons: FQButtonId[] };

export class UiToolbar extends AbstractUiToolContainer<UiToolbarConfig> implements Emptyable, UiToolbarCommandHandler, UiToolbarEventSource {

	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent<boolean>();

	public readonly onToolbarButtonClick: TeamAppsEvent<AbstractUiToolContainer_ToolbarButtonClickEvent> = new TeamAppsEvent<AbstractUiToolContainer_ToolbarButtonClickEvent>();

	public static DEFAULT_TOOLBAR_MAX_HEIGHT = 70;

	private buttonGroupsById: Map<string, UiToolbarButtonGroup> = new Map();
	private buttonGroupEmptyEventSubscriptions: Map<string, EventSubscription> = new Map();
	private leftButtonGroups: UiToolbarButtonGroup[] = [];
	private rightButtonGroups: UiToolbarButtonGroup[] = [];

	private _$toolbar: HTMLElement;
	private _$backgroundColorDiv: HTMLElement;
	private _$innerContainer: HTMLElement;
	private $toolbarFiller: HTMLElement;
	private _$logo: HTMLElement;
	private overflowDropDown: UiDropDown;
	private overflowToolAccordion: UiToolAccordion;
	private $overflowDropDownButton: HTMLElement;
	private totalWidthOfOverflowButtons: number;

	constructor(config: UiToolbarConfig, context: TeamAppsUiContext) {
		super(config, context);
		this._$toolbar = parseHtml(`<div class="UiToolbar teamapps-blurredBackgroundImage"></div>`);
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
		this.overflowDropDown = new UiDropDown();
		this.overflowToolAccordion = this.createDropDownAccordion();
		this.overflowDropDown.setContentComponent(this.overflowToolAccordion);
		this.$overflowDropDownButton.addEventListener("mousedown", () => {
			if (!this.overflowDropDown.isOpen) {
				this.overflowDropDown.open({$reference: this.$overflowDropDownButton, width: Math.min(400, this.totalWidthOfOverflowButtons * 1.2)});
				this.updateButtonOverflow();
			}
		});

		config.leftButtonGroups && config.leftButtonGroups.forEach(bg => this.addButtonGroup(bg, false))
		config.rightButtonGroups && config.rightButtonGroups.forEach(bg => this.addButtonGroup(bg, true))

		this.onEmptyStateChanged.addListener(empty =>  {
			this.getMainElement().classList.toggle("empty", empty)
		})
		this.getMainElement().classList.toggle("empty", this.empty);
	}

	private createDropDownAccordion(): UiToolAccordion {
		let accordionConfig = $.extend({}, this._config, {
			buttonGroups: [...this.leftButtonGroups, ...this.rightButtonGroups].map(group => $.extend({}, group.getConfig(), {buttons: group.getButtonConfigs()}))
		});
		let toolAccordion = new UiToolAccordion(accordionConfig, this._context);
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

	public setDropDownComponent(groupId: string, buttonId: string, component: UiComponent): void {
		this.buttonGroupsById.get(groupId).setDropDownComponent(buttonId, component);
		this.overflowToolAccordion.setDropDownComponent(groupId, buttonId, component);
	}

	public setButtonHasDropDown(groupId: string, buttonId: string, hasDropDown: boolean): void {
		this.buttonGroupsById.get(groupId).setButtonHasDropDown(buttonId, hasDropDown);
	}

	public closeDropDown(groupId: string, buttonId: string) {
		this.buttonGroupsById.get(groupId).closeDropDown(buttonId);
	}

	public setButtonVisible(groupId: string, buttonId: string, visible: boolean) {
		this.buttonGroupsById.forEach(buttonGroup => buttonGroup.setButtonVisible(buttonId, visible));
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.setButtonVisible(groupId, buttonId, visible);
		}
		this.updateButtonOverflow();
	}

	public setButtonGroupVisible(groupId: string, visible: boolean) {
		this.buttonGroupsById.get(groupId)?.setVisible(visible);
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.setButtonGroupVisible(groupId, visible);
		}
		this.updateButtonOverflow();
	}

	public addButtonGroup(groupConfig: UiToolbarButtonGroupConfig, rightSide: boolean) {
		const existingButtonGroup = this.buttonGroupsById.get(groupConfig.groupId);
		if (existingButtonGroup) {
			this.removeButtonGroup(groupConfig.groupId);
		}

		const buttonGroup = new UiToolbarButtonGroup(groupConfig, this, this._context);
		buttonGroup.getMainDomElement().classList.toggle("right-side", rightSide);
		buttonGroup.onButtonClicked.addListener(e => {
			return this.onToolbarButtonClick.fire({
				groupId: groupConfig.groupId,
				buttonId: e.buttonId,
				dropDownClickInfo: e.dropDownButtonClickInfo
			});
		});
		this.buttonGroupsById.set(groupConfig.groupId, buttonGroup);
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

		this.buttonGroupEmptyEventSubscriptions.set(buttonGroup.getId(),
			buttonGroup.onEmptyStateChanged.addListener(eventObject => this.updateEmptyState()));
		this.updateEmptyState();
	}

	private insertGroupAtCorrectSortingPosition(buttonGroup: UiToolbarButtonGroup, position: UiToolbarButtonGroupPosition, rightSide: boolean) {
		let otherButtonGroups = (rightSide ? this.rightButtonGroups: this.leftButtonGroups).sort((group1, group2) => group1.position - group2.position);
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
		const buttonGroup = this.buttonGroupsById.get(groupId);

		if (buttonGroup) {
			this.buttonGroupsById.delete(groupId);
			this.leftButtonGroups = this.leftButtonGroups.filter(g => g.getId() !== groupId);
			this.rightButtonGroups = this.rightButtonGroups.filter(g => g.getId() !== groupId);
			buttonGroup.getMainDomElement().remove();
		}

		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.removeButtonGroup(groupId);
		}
		this.updateButtonOverflow();

		this.buttonGroupEmptyEventSubscriptions.get(buttonGroup.getId())?.unsubscribe();
		this.updateEmptyState();
	}

	private updateEmptyState() {
		this.onEmptyStateChanged.fireIfChanged(this.empty);
	}

	public addButton(groupId: string, buttonConfig: UiToolbarButtonConfig, neighborButtonId: string, beforeNeighbor: boolean) {
		this.buttonGroupsById.get(groupId)?.addButton(buttonConfig, neighborButtonId, beforeNeighbor);
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.addButton(groupId, buttonConfig, neighborButtonId, beforeNeighbor);
		}
		this.updateButtonOverflow();
	}

	public removeButton(groupId: string, buttonId: string): void {
		this.buttonGroupsById.forEach(group => group.removeButton(buttonId));
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
		return ![...this.buttonGroupsById.values()].some(group => !group.empty);
	}

	public destroy(): void {
		super.destroy();
		// TODO destroy all dropdowns
	}

	setButtonColors(groupId: string, buttonId: string, backgroundColor: string, hoverBackgroundColor: string): void {
		this.buttonGroupsById.get(groupId)?.setButtonColors(buttonId, backgroundColor, hoverBackgroundColor);
	}

}


TeamAppsUiComponentRegistry.registerComponentClass("UiToolbar", UiToolbar);
