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

import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiToolbarCommandHandler, UiToolbarConfig, UiToolbarEventSource} from "../../../generated/UiToolbarConfig";
import {UiToolbarButtonGroupConfig} from "../../../generated/UiToolbarButtonGroupConfig";
import {UiToolbarButtonConfig} from "../../../generated/UiToolbarButtonConfig";
import {UiToolAccordion} from "../tool-accordion/UiToolAccordion";
import {AbstractUiToolContainer} from "../AbstractUiToolContainer";
import {OrderedDictionary} from "../../util/OrderedDictionary";
import {UiDropDown} from "../../micro-components/UiDropDown";
import {UiComponent} from "../../UiComponent";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {AbstractUiToolContainer_ToolbarButtonClickEvent, AbstractUiToolContainer_ToolbarDropDownItemClickEvent} from "../../../generated/AbstractUiToolContainerConfig";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {Emptyable} from "../../util/Emptyable";
import {UiToolbarButton} from "./UiToolbarButton";
import {UiToolbarButtonGroup} from "./UiToolbarButtonGroup";
import {insertBefore, outerWidthIncludingMargins, parseHtml} from "../../Common";

interface FQButtonId {
	groupId: string,
	buttonId: string,
	button: UiToolbarButton
}

export type ButtonVisibilities = { fittingButtons: FQButtonId[], nonFittingButtons: FQButtonId[], hiddenButtons: FQButtonId[] };

export class UiToolbar extends AbstractUiToolContainer<UiToolbarConfig> implements Emptyable, UiToolbarCommandHandler, UiToolbarEventSource {

	public readonly onEmptyStateChanged: TeamAppsEvent<boolean> = new TeamAppsEvent<boolean>(this);

	public readonly onToolbarButtonClick: TeamAppsEvent<AbstractUiToolContainer_ToolbarButtonClickEvent> = new TeamAppsEvent<AbstractUiToolContainer_ToolbarButtonClickEvent>(this);
	public readonly onToolbarDropDownItemClick: TeamAppsEvent<AbstractUiToolContainer_ToolbarDropDownItemClickEvent> = new TeamAppsEvent<AbstractUiToolContainer_ToolbarDropDownItemClickEvent>(this);

	public static DEFAULT_TOOLBAR_MAX_HEIGHT = 70;

	private buttonGroupsById: OrderedDictionary<UiToolbarButtonGroup> = new OrderedDictionary<UiToolbarButtonGroup>();

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
		this._$toolbar = parseHtml(`<div class="UiToolbar teamapps-blurredBackgroundImage" id="${config.id}"></div>`);
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
				this.overflowToolAccordion.attachedToDom = true; // obviously...
				this.updateButtonOverflow();
			}
		});

		for (let i = 0; i < config.buttonGroups.length; i++) {
			const buttonGroupConfig = config.buttonGroups[i];
			this.addButtonGroup(buttonGroupConfig);
		}
	}

	private createDropDownAccordion(): UiToolAccordion {
		let accordionConfig = $.extend({}, this._config, {
			buttonGroups: this.buttonGroupsById.values.map(group => $.extend({}, group.getConfig(), {buttons: group.getButtonConfigs()}))
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


	public getMainDomElement(): HTMLElement {
		return this._$toolbar;
	}

	public setLogoImage(logoImage: string) {
		if (this._$logo) {
			this._$logo.remove();
			this._$logo = null;
		}
		if (logoImage) {
			this._$logo = parseHtml(`<div class="logo" style="background-image: url(${logoImage});"></div>`);
			this._$innerContainer.appendChild(this._$logo)
		}
	}

	public setDropDownComponent(groupId: string, buttonId: string, component: UiComponent): void {
		this.buttonGroupsById.getValue(groupId).setDropDownComponent(buttonId, component);
		this.overflowToolAccordion.setDropDownComponent(groupId, buttonId, component);
	}

	public setButtonHasDropDown(groupId: string, buttonId: string, hasDropDown: boolean): void {
		this.buttonGroupsById.getValue(groupId).setButtonHasDropDown(buttonId, hasDropDown);
	}

	public setButtonVisible(groupId: string, buttonId: string, visible: boolean) {
		this.buttonGroupsById.values.forEach(buttonGroup => buttonGroup.setButtonVisible(buttonId, visible));
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.setButtonVisible(groupId, buttonId, visible);
		}
		this.updateButtonOverflow();
	}

	public setButtonGroupVisible(groupId: string, visible: boolean) {
		this.buttonGroupsById.getValue(groupId) && this.buttonGroupsById.getValue(groupId).setVisible(visible);
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.setButtonGroupVisible(groupId, visible);
		}
		this.updateButtonOverflow();
	}

	public addButtonGroup(groupConfig: UiToolbarButtonGroupConfig) {
		const existingButtonGroup = this.buttonGroupsById.getValue(groupConfig.groupId);
		if (existingButtonGroup) {
			this.removeButtonGroup(groupConfig.groupId);
		}
		let emptyStateChanges = this.empty && groupConfig != null;

		const buttonGroup = new UiToolbarButtonGroup(groupConfig, this, this._context);
		buttonGroup.onButtonClicked.addListener(e => {
			return this.onToolbarButtonClick.fire({
				groupId: groupConfig.groupId,
				buttonId: e.buttonId,
				dropDownClickInfo: e.dropDownButtonClickInfo
			});
		});
		buttonGroup.onDropDownItemClicked.addListener(e => {
			return this.onToolbarDropDownItemClick.fire({
				groupId: groupConfig.groupId,
				buttonId: e.buttonId,
				dropDownGroupId: e.groupId,
				dropDownItemId: e.buttonId
			});
		});
		this.buttonGroupsById.push(groupConfig.groupId, buttonGroup);

		let allButtonGroups = this.buttonGroupsById.values.sort((group1, group2) => group1.position - group2.position);
		let otherGroupIndex = 0;
		for (; otherGroupIndex < allButtonGroups.length; otherGroupIndex++) {
			let otherGroup = allButtonGroups[otherGroupIndex];
			if (otherGroup.position > groupConfig.position) {
				break;
			}
		}

		if (allButtonGroups[otherGroupIndex]) {
			insertBefore(buttonGroup.getMainDomElement(), allButtonGroups[otherGroupIndex].getMainDomElement())
		} else {
			insertBefore(buttonGroup.getMainDomElement(), this.$toolbarFiller)
		}

		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.addButtonGroup(groupConfig);
		}
		this.updateButtonOverflow();

		if (emptyStateChanges) {
			this.onEmptyStateChanged.fire(false);
		}
	}

	public removeButtonGroup(groupId: string): void {
		let wasAlreadyEmpty = this.empty;

		const buttonGroup = this.buttonGroupsById.getValue(groupId);

		if (buttonGroup) {
			this.buttonGroupsById.remove(groupId);
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

	public addButton(groupId: string, buttonConfig: UiToolbarButtonConfig, neighborButtonId: string, beforeNeighbor: boolean) {
		this.buttonGroupsById.getValue(groupId) && this.buttonGroupsById.getValue(groupId).addButton(buttonConfig, neighborButtonId, beforeNeighbor);
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.addButton(groupId, buttonConfig, neighborButtonId, beforeNeighbor);
		}
		this.updateButtonOverflow();
	}

	public removeButton(groupId: string, buttonId: string): void {
		this.buttonGroupsById.values.forEach(group => group.removeButton(buttonId));
		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.removeButton(groupId, buttonId);
		}
		this.updateButtonOverflow();
	}

	updateButtonGroups(buttonGroups: UiToolbarButtonGroupConfig[]): void {
		// TODO implement only if really needed
	}

	public onResize(): void {
		this.updateButtonOverflow();
	}

	private updateButtonOverflow() {
		if (!this.attachedToDom) return;
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
		for (let group of this.buttonGroupsById.values) {
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
		return this.buttonGroupsById.length == 0;
	}

	public destroy(): void {
		// TODO destroy all dropdowns
	}

}


TeamAppsUiComponentRegistry.registerComponentClass("UiToolbar", UiToolbar);
