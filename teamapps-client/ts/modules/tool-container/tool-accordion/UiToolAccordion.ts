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

import * as log from "loglevel";
import {AbstractUiToolContainer} from "../AbstractUiToolContainer";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiToolbarButtonGroupConfig} from "../../../generated/UiToolbarButtonGroupConfig";
import {UiToolbarButtonConfig} from "../../../generated/UiToolbarButtonConfig";
import {createUiDropDownButtonClickInfoConfig, UiDropDownButtonClickInfoConfig} from "../../../generated/UiDropDownButtonClickInfoConfig";
import {DEFAULT_TEMPLATES} from "../../trivial-components/TrivialCore";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {doOnceOnClickOutsideElement, insertAfter, parseHtml} from "../../Common";
import {UiToolAccordionCommandHandler, UiToolAccordionConfig, UiToolAccordionEventSource} from "../../../generated/UiToolAccordionConfig";
import {AbstractUiToolContainer_ToolbarButtonClickEvent} from "../../../generated/AbstractUiToolContainerConfig";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {OrderedDictionary} from "../../util/OrderedDictionary";
import {UiComponent} from "../../UiComponent";
import {UiToolAccordionButton} from "./UiToolAccordionButton";
require("jquery-ui/ui/scroll-parent.js")

export class UiToolAccordion extends AbstractUiToolContainer<UiToolAccordionConfig> implements UiToolAccordionCommandHandler, UiToolAccordionEventSource {

	public static DEFAULT_TOOLBAR_MAX_HEIGHT = 70;

	public readonly onToolbarButtonClick: TeamAppsEvent<AbstractUiToolContainer_ToolbarButtonClickEvent> = new TeamAppsEvent<AbstractUiToolContainer_ToolbarButtonClickEvent>();

	private buttonGroupsById: OrderedDictionary<UiButtonGroup> = new OrderedDictionary<UiButtonGroup>();

	private $mainDomElement: HTMLElement;
	private $backgroundColorDiv: HTMLElement;

	constructor(config: UiToolAccordionConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$mainDomElement = parseHtml(`<div class="UiToolAccordion teamapps-blurredBackgroundImage"></div>`);
		this.$backgroundColorDiv = parseHtml('<div class="background-color-div"></div>');
		this.$mainDomElement.appendChild(this.$backgroundColorDiv);
		let allButtonGroups = [...config.leftButtonGroups, ...config.rightButtonGroups];
		for (let i = 0; i < allButtonGroups.length; i++) {
			const buttonGroupConfig = allButtonGroups[i];
			let buttonGroup = this.createButtonGroup(buttonGroupConfig);
			this.buttonGroupsById.push(buttonGroupConfig.groupId, buttonGroup);
			this.$backgroundColorDiv.appendChild(buttonGroup.getMainDomElement());
		}

		this.refreshEnforcedButtonWidth();
	}

	public doGetMainElement(): HTMLElement {
		return this.$mainDomElement;
	}

	private createButtonGroup(buttonGroupConfig: UiToolbarButtonGroupConfig): UiButtonGroup {
		return new UiButtonGroup(buttonGroupConfig, this, this._context, AbstractUiToolContainer.$sizeTestingContainer);
	}

	public setButtonHasDropDown(groupId: string, buttonId: string, hasDropDown: boolean): void {
		this.buttonGroupsById.getValue(groupId).setButtonHasDropDown(buttonId, hasDropDown);
	}

	public setDropDownComponent(groupId: string, buttonId: string, component: UiComponent): void {
		this.buttonGroupsById.getValue(groupId).setDropDownComponent(buttonId, component);
	}

	public setButtonVisible(groupId: string, buttonId: string, visible: boolean) {
		this.buttonGroupsById.getValue(groupId).setButtonVisible(buttonId, visible);
		this.refreshEnforcedButtonWidth();
	}

	setButtonColors(groupId: string, buttonId: string, backgroundColor: string, hoverBackgroundColor: string): void {
		this.buttonGroupsById.getValue(groupId).setButtonColors(buttonId, backgroundColor, hoverBackgroundColor);
	}

	public setButtonGroupVisible(groupId: string, visible: boolean) {
		this.buttonGroupsById.getValue(groupId).setVisible(visible);
		this.refreshEnforcedButtonWidth();
	}

	public addButtonGroup(buttonGroupConfig: UiToolbarButtonGroupConfig) {
		const existingButtonGroup = this.buttonGroupsById.getValue(buttonGroupConfig.groupId);
		if (existingButtonGroup) {
			this.removeButtonGroup(buttonGroupConfig.groupId);
		}

		const buttonGroup = this.createButtonGroup(buttonGroupConfig);
		this.buttonGroupsById.push(buttonGroupConfig.groupId, buttonGroup);

		let allButtonGroups = this.buttonGroupsById.values;
		let otherGroupIndex = 0;
		for (; otherGroupIndex < allButtonGroups.length; otherGroupIndex++) {
			let otherGroup = allButtonGroups[otherGroupIndex];
			if (otherGroup.position > buttonGroupConfig.position) {
				break;
			}
		}

		if (allButtonGroups[otherGroupIndex]) {
			this.$backgroundColorDiv.insertBefore(buttonGroup.getMainDomElement(), allButtonGroups[otherGroupIndex].getMainDomElement());
		} else {
			this.$backgroundColorDiv.appendChild(buttonGroup.getMainDomElement());
		}

		this.refreshEnforcedButtonWidth();
	}

	public removeButtonGroup(groupId: string): void {
		const buttonGroup = this.buttonGroupsById.getValue(groupId);

		if (buttonGroup) {
			this.buttonGroupsById.remove(groupId);
			buttonGroup.getMainDomElement().remove();
		}
		this.refreshEnforcedButtonWidth();
	}

	public addButton(groupId: string, buttonConfig: UiToolbarButtonConfig, neighborButtonId: string, beforeNeighbor: boolean) {
		this.buttonGroupsById.getValue(groupId).addButton(buttonConfig, neighborButtonId, beforeNeighbor);
		this.refreshEnforcedButtonWidth();
	}

	public removeButton(groupId: string, buttonId: string): void {
		this.buttonGroupsById.getValue(groupId).removeButton(buttonId);
		this.refreshEnforcedButtonWidth();
	}

	updateButtonGroups(buttonGroups: UiToolbarButtonGroupConfig[]): void {
		// TODO implement only if really needed
	}

	public refreshEnforcedButtonWidth() {
		let maxButtonWidth = this.buttonGroupsById.values
			.filter(group => group.isVisible())
			.reduce((maxButtonWidth, buttonGroup) => Math.max(maxButtonWidth, buttonGroup.getMaxOptimizedButtonWidth()), 1);
		this.buttonGroupsById.values.forEach(group => group.setEnforcedButtonWidth(maxButtonWidth));
	}

	public onResize(): void {
		this.buttonGroupsById.values.forEach(group => group.onResize());
	}

}

class UiButtonGroup {
	private static logger = log.getLogger("UiToolAccordion.UiButtonGroup");

	private config: UiToolbarButtonGroupConfig;
	private visible: boolean = true;
	private $buttonGroupWrapper: HTMLElement;
	private $buttonGroup: HTMLElement;
	private buttonsById: { [index: string]: UiToolAccordionButton } = {};
	private buttons: UiToolAccordionButton[] = [];
	private $buttonRows: HTMLElement[] = [];
	private enforcedButtonWidth: number = 1;

	constructor(buttonGroupConfig: UiToolbarButtonGroupConfig, private toolAccordion: UiToolAccordion, private context: TeamAppsUiContext, private $sizeTestingContainer: HTMLElement) {
		const $buttonGroupWrapper = parseHtml('<div class="button-group-wrapper"></div>');

		const $buttonGroup = parseHtml(`<div class="toolbar-button-group" id="${this.toolAccordionId}_${buttonGroupConfig.groupId}">`);
		$buttonGroupWrapper.appendChild($buttonGroup);

		this.config = buttonGroupConfig;
		this.$buttonGroupWrapper = $buttonGroupWrapper;
		this.$buttonGroup = $buttonGroup;

		for (let j = 0; j < buttonGroupConfig.buttons.length; j++) {
			this.addButton(buttonGroupConfig.buttons[j]);
		}

		this.setVisible(buttonGroupConfig.visible);
	}

	private get toolAccordionId() {
		return this.toolAccordion.getId();
	}

	public get position() {
		return this.config.position;
	}

	private createButton(buttonConfig: UiToolbarButtonConfig): UiToolAccordionButton {
		let button = new UiToolAccordionButton(buttonConfig, this.context);

		button.onClick.addListener(eventObject => {
			let dropdownClickInfo: UiDropDownButtonClickInfoConfig = null;
			if (button.hasDropDown) {
				if (button.$dropDown == null) {
					this.createDropDown(button);
				}
				let dropdownVisible = $(button.$dropDown).is(":visible");
				dropdownClickInfo = createUiDropDownButtonClickInfoConfig(!dropdownVisible, button.dropDownComponent != null);
				if (!dropdownVisible) {
					if (button.dropDownComponent != null) {
						button.$dropDown.appendChild(button.dropDownComponent.getMainElement());
					}
					this.showDropDown(button);
					doOnceOnClickOutsideElement(button.getMainDomElement(), e => $(button.$dropDown).slideUp(200))
				} else {
					$(button.$dropDown).slideUp(200);
				}
			}

			this.toolAccordion.onToolbarButtonClick.fire({
				groupId: this.config.groupId,
				buttonId: button.id,
				dropDownClickInfo: dropdownClickInfo
			});
		})

		return button;
	}

	private createDropDown(button: UiToolAccordionButton) {
		button.$dropDown = parseHtml(`<div class="tool-accordion-dropdown"></div>`);
		button.$dropDownSourceButtonIndicator = parseHtml(`<div class="source-button-indicator">`);
		button.$dropDown.appendChild(button.$dropDownSourceButtonIndicator);

		if (button.dropDownComponent) {
			this.setButtonDropDownComponent(button, button.dropDownComponent);
		} else {
			button.$dropDown.appendChild(parseHtml(DEFAULT_TEMPLATES.defaultSpinnerTemplate));
		}

		return button.$dropDown;
	}

	private showDropDown(button: UiToolAccordionButton) {
		UiButtonGroup.logger.debug(button.$dropDown.offsetHeight);
		const me = this;
		button.$dropDown.style.display = "none";
		this.insertDropdownUnderButtonRow(button);

		$(button.$dropDown).slideDown({
			duration: 200,
			progress: (animation, progress: number, remainingMs: number) => {
				let buttonHeight = button.getMainDomElement().offsetHeight;
				let dropDownHeight = button.$dropDown.offsetHeight;
				let totalInterestingPartHeight = buttonHeight + dropDownHeight;
				let buttonY = button.getMainDomElement().getBoundingClientRect().top - me.getMainDomElement().closest('.UiToolAccordion').getBoundingClientRect().top;
				let $scrollContainer = $(me.getMainDomElement()).scrollParent();
				let scrollY = $scrollContainer.scrollTop();
				let viewPortHeight = $scrollContainer[0].offsetHeight;

				if (scrollY < buttonY + totalInterestingPartHeight - viewPortHeight) {
					scrollY = buttonY + totalInterestingPartHeight - viewPortHeight;
					$scrollContainer.scrollTop(scrollY);
					UiButtonGroup.logger.debug(scrollY);
				}
				if (buttonY < scrollY) { // TODO scrollY
					$scrollContainer.scrollTop(buttonY);
				}
			}
		});
		$(button.$dropDownSourceButtonIndicator).position({
			my: "center bottom",
			at: "center bottom",
			of: button.getMainDomElement()
		});
	}

	private insertDropdownUnderButtonRow(button: UiToolAccordionButton) {
		let $row = this.$buttonRows.filter($row => $.contains($row, button.getMainDomElement()))[0];
		insertAfter(button.$dropDown, $row);
	}

	public setDropDownComponent(buttonId: string, component: UiComponent) {
		let button = this.buttonsById[buttonId];
		this.setButtonDropDownComponent(button, component);
	}

	setButtonHasDropDown(buttonId: string, hasDropDown: boolean) {
		const button = this.buttonsById[buttonId];
		if (button != null) {
			button.hasDropDown = hasDropDown;
		}
	}

	private setButtonDropDownComponent(button: UiToolAccordionButton, component: UiComponent) {
		if (button.dropDownComponent != null) {
			button.dropDownComponent.getMainElement().remove();
		}

		button.dropDownComponent = component;

		if (component != null) {
			if (button.$dropDown != null) {
				button.$dropDown.querySelectorAll<HTMLElement>(":scope :not(.source-button-indicator)").forEach(b => b.remove()); // remove spinner or old component, if present...
				if ($(button.$dropDown).is(":visible")) {
					button.$dropDown.appendChild(component.getMainElement());
				}
			}
		}
	}

	public getMaxOptimizedButtonWidth(): number {
		return this.buttons
			.filter(button => button.visible)
			.reduce((maxWidth, button) => Math.max(maxWidth, button.optimizedWidth), 0);
	}

	public setEnforcedButtonWidth(enforcedButtonWidth: number) {
		this.enforcedButtonWidth = enforcedButtonWidth;
		this.updateRows();
	}

	public setButtonVisible(buttonId: string, visible: boolean) {
		const button = this.buttonsById[buttonId];
		if (button) {
			button.visible = visible;
			this.updateVisibility();
			this.updateRows();
		}
	}

	public addButton(buttonConfig: UiToolbarButtonConfig, neighborButtonId?: string, beforeNeighbor?: boolean) {
		const button = this.createButton(buttonConfig);

		const existingButton = this.buttonsById[buttonConfig.buttonId];
		if (existingButton) {
			this.removeButton(buttonConfig.buttonId);
		}

		this.buttonsById[buttonConfig.buttonId] = button;
		const neighborButton = this.buttonsById[neighborButtonId];
		if (neighborButton) {
			let neighborButtonIndex = this.buttons.indexOf(neighborButton);
			if (beforeNeighbor) {
				this.buttons.splice(neighborButtonIndex, 0, button);
			} else {
				this.buttons.splice(neighborButtonIndex + 1, 0, button);
			}
		} else {
			this.buttons.push(button);
		}

		this.updateVisibility();
		this.updateRows();
	}


	public removeButton(buttonId: string): void {
		const button = this.buttonsById[buttonId];

		if (button) {
			delete this.buttonsById[buttonId];
			this.buttons = this.buttons.filter(b => b.id !== buttonId);
			button.getMainDomElement().remove();
		}

		this.updateVisibility();
		this.updateRows();
	}

	public updateRows() {
		let availableWidth = this.$buttonGroupWrapper.offsetWidth;
		if (availableWidth == 0) {
			return;
		}

		this.$buttonRows.forEach($row => {
			$row.remove();
			$row.innerHTML = '';
		});
		let buttonsPerRow = Math.floor(availableWidth / Math.max(16, this.enforcedButtonWidth));

		if (buttonsPerRow === 0) {
			buttonsPerRow = 1;
		}

		let visibleButtonsCount = 0;
		for (let i = 0; i < this.buttons.length; i++) {
			let button = this.buttons[i];
			if (button.visible) {
				let rowIndex = Math.floor(visibleButtonsCount / buttonsPerRow);
				let $row = this.$buttonRows[rowIndex] || (this.$buttonRows[rowIndex] = parseHtml(`<div class="button-row">`));
				button.getMainDomElement().style.flexBasis = this.enforcedButtonWidth + "px";
				$row.appendChild(button.getMainDomElement());
				visibleButtonsCount++;
			}
		}
		// fill with dummy elements
		for (let i = visibleButtonsCount; i % buttonsPerRow != 0; i++) {
			let rowIndex = Math.floor(i / buttonsPerRow);
			let $row = this.$buttonRows[rowIndex];
			$row.appendChild(parseHtml(`<div class="row-filler" style="flex-basis: ${this.enforcedButtonWidth}px">`));
		}

		this.$buttonRows.forEach($row => {
			if ($row.children.length > 0) {
				this.$buttonGroup.appendChild($row);
			}
		});
	}

	public setVisible(visible: boolean) {
		this.visible = visible;
		this.updateVisibility();
	}

	public isVisible() {
		return this.visible;
	}

	private updateVisibility() {
		let hasVisibleButton = this.buttons.some(b => b.visible);
		this.$buttonGroupWrapper.classList.toggle("hidden", !(this.visible && hasVisibleButton));
	}

	public getMainDomElement(): HTMLElement {
		return this.$buttonGroupWrapper;
	}

	public onResize(): void {
		this.updateRows();
		this.buttons.forEach(b => {
			if (b.$dropDown) {
				this.insertDropdownUnderButtonRow(b);
			}
		})
	}

	setButtonColors(buttonId: string, backgroundColor: string, hoverBackgroundColor: string) {
		this.buttons.filter(b => b.id === buttonId)[0].setColors(backgroundColor, hoverBackgroundColor);
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiToolAccordion", UiToolAccordion);
