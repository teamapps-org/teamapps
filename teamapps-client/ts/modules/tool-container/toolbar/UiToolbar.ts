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
import {EventFactory} from "../../../generated/EventFactory";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {Emptyable} from "../../util/Emptyable";
import {UiColorConfig} from "../../../generated/UiColorConfig";
import {createUiColorCssString} from "../../util/CssFormatUtil";
import {UiToolbarButton} from "./UiToolbarButton";
import {UiToolbarButtonGroup} from "./UiToolbarButtonGroup";

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

	private _$toolbar: JQuery;
	private _$backgroundColorDiv: JQuery;
	private _$innerContainer: JQuery;
	private $toolbarFiller: JQuery;
	private _$logo: JQuery;
	private overflowDropDown: UiDropDown;
	private overflowToolAccordion: UiToolAccordion;
	private $overflowDropDownButton: JQuery;
	private totalWidthOfOverflowButtons: number;

	constructor(config: UiToolbarConfig, context: TeamAppsUiContext) {
		super(config, context);
		this._$toolbar = $(`<div class="UiToolbar teamapps-blurredBackgroundImage" id="${config.id}"></div>`);
		this._$backgroundColorDiv = $('<div class="background-color-div"></div>')
			.appendTo(this._$toolbar);
		this._$innerContainer = $('<div class="inner-container">')
			.appendTo(this._$backgroundColorDiv);

		this.$toolbarFiller = $('<div class="toolbar-filler">').appendTo(this._$innerContainer);

		if (config.logoImage) {
			this.setLogoImage(config.logoImage);
		}

		this.$overflowDropDownButton = $('<div class="overflow-dropdown-button toolbar-button-wrapper"><div class="caret"></div></div>');
		this.$overflowDropDownButton.appendTo(this._$innerContainer);
		this.overflowDropDown = new UiDropDown();
		this.overflowToolAccordion = this.createDropDownAccordion();
		this.overflowDropDown.setContentComponent(this.overflowToolAccordion);
		this.$overflowDropDownButton.mousedown(() => {
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
			this.onToolbarButtonClick.fire(EventFactory.createAbstractUiToolContainer_ToolbarButtonClickEvent(this.getId(), eventObject.groupId, eventObject.buttonId, eventObject.dropDownClickInfo));
		});
		return toolAccordion;
	}


	public getMainDomElement(): JQuery {
		return this._$toolbar;
	}

	public setLogoImage(logoImage: string) {
		if (this._$logo) {
			this._$logo.detach();
			this._$logo = null;
		}
		if (logoImage) {
			this._$logo = $(`<div class="logo" style="background-image: url(${logoImage});">`)
				.appendTo(this._$innerContainer);
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
		let changesFromEmptyToNonEmpty = this.empty && groupConfig != null;
		const existingButtonGroup = this.buttonGroupsById.getValue(groupConfig.groupId);
		if (existingButtonGroup) {
			this.removeButtonGroup(groupConfig.groupId);
		}

		const buttonGroup = new UiToolbarButtonGroup(groupConfig, this, this._context);
		buttonGroup.onButtonClicked.addListener(e => {
			return this.onToolbarButtonClick.fire(EventFactory.createAbstractUiToolContainer_ToolbarButtonClickEvent(this.getId(), groupConfig.groupId, e.buttonId, e.dropDownButtonClickInfo));
		});
		buttonGroup.onDropDownItemClicked.addListener(e => {
			return this.onToolbarDropDownItemClick.fire(EventFactory.createAbstractUiToolContainer_ToolbarDropDownItemClickEvent(this.getId(), groupConfig.groupId, e.buttonId, e.groupId, e.buttonId));
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
			buttonGroup.getMainDomElement().insertBefore(allButtonGroups[otherGroupIndex].getMainDomElement());
		} else {
			buttonGroup.getMainDomElement().insertBefore(this.$toolbarFiller);
		}

		if (this.overflowToolAccordion) {
			this.overflowToolAccordion.addButtonGroup(groupConfig);
		}
		this.updateButtonOverflow();

		if (changesFromEmptyToNonEmpty) {
			this.onEmptyStateChanged.fire(false);
		}
	}

	public removeButtonGroup(groupId: string): void {
		let wasAlreadyEmpty = this.empty;

		const buttonGroup = this.buttonGroupsById.getValue(groupId);

		if (buttonGroup) {
			this.buttonGroupsById.remove(groupId);
			buttonGroup.getMainDomElement().detach();
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
		let availableWidth = this._$innerContainer[0].offsetWidth;
		this._$innerContainer.addClass("overflow-measurement-mode");
		let logoWidth = 0;
		if (this._$logo) {
			this._$logo.removeClass("hidden");
			logoWidth = this._$logo.outerWidth(true);
		}
		let overflowDropDownButtonWidth = this.$overflowDropDownButton.removeClass("hidden").outerWidth(true);
		let consumedWidth = Math.max(logoWidth, overflowDropDownButtonWidth);
		let hasOverflowingButton = false;
		this.totalWidthOfOverflowButtons = 0;
		for (let group of this.buttonGroupsById.values) {
			let buttonVisibilityInfo = group.calculateButtonVisibilities(availableWidth - consumedWidth);
			this.totalWidthOfOverflowButtons += buttonVisibilityInfo.nonFittingButtons.reduce((sum, b) => sum + b.button.optimizedWidth, 0);
			buttonVisibilityInfo.fittingButtons.forEach(b => {
				b.button.getMainDomElement().removeClass("moved-to-overflow-dropdown");
				this.overflowToolAccordion.setButtonVisible(group.getId(), b.buttonId, false);
			});
			buttonVisibilityInfo.nonFittingButtons.forEach(b => {
				b.button.getMainDomElement().addClass("moved-to-overflow-dropdown");
				this.overflowToolAccordion.setButtonVisible(group.getId(), b.buttonId, true);
				this.overflowToolAccordion.setDropDownComponent(b.groupId, b.buttonId, b.button.getDropDownComponent()); // move to overflow accordion
			});
			buttonVisibilityInfo.hiddenButtons.forEach(b => {
				this.overflowToolAccordion.setButtonVisible(group.getId(), b.buttonId, false);
			});
			consumedWidth += group.isVisible() ? group.getMainDomElement()[0].offsetWidth : 0;
			hasOverflowingButton = hasOverflowingButton || buttonVisibilityInfo.nonFittingButtons.length > 0;
		}
		this.$overflowDropDownButton.toggleClass("hidden", !hasOverflowingButton);
		if (this._$logo) {
			this._$logo.toggleClass("hidden", hasOverflowingButton);
		}
		this._$innerContainer.removeClass("overflow-measurement-mode");
	}

	public get empty() {
		return this.buttonGroupsById.length == 0;
	}

	public destroy(): void {
		// TODO destroy all dropdowns
	}

}


TeamAppsUiComponentRegistry.registerComponentClass("UiToolbar", UiToolbar);
