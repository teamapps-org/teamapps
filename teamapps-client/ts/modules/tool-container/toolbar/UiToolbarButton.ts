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
import {UiDropDown} from "../../micro-components/UiDropDown";
import {UiToolbarButtonConfig} from "../../../generated/UiToolbarButtonConfig";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {UiToolbar} from "./UiToolbar";
import {AbstractUiToolContainer} from "../AbstractUiToolContainer";
import {UiTextCellTemplateConfig} from "../../../generated/UiTextCellTemplateConfig";
import {enterFullScreen, exitFullScreen, isFullScreen} from "../../Common";
import {createUiDropDownButtonClickInfoConfig, UiDropDownButtonClickInfoConfig} from "../../../generated/UiDropDownButtonClickInfoConfig";
import {UiComponent} from "../../UiComponent";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {UiItemView} from "../../UiItemView";
import * as $ from "jquery";

export class UiToolbarButton {

	public readonly onClicked: TeamAppsEvent<UiDropDownButtonClickInfoConfig> = new TeamAppsEvent(this);
	public readonly onDropDownItemClicked: TeamAppsEvent<{groupId: string, itemId: number}> = new TeamAppsEvent(this);

	private $buttonWrapper: JQuery;
	private $button: JQuery;
	private $dropDownCaret: JQuery;
	public optimizedWidth: number;
	private visible: boolean;

	private hasDropDown: boolean;
	private dropDown: UiDropDown;
	private dropDownComponent: UiComponent;

	constructor(public config: UiToolbarButtonConfig, private context: TeamAppsUiContext) {
		this.$buttonWrapper = $(`<div class="toolbar-button-wrapper" data-buttonId="${config.buttonId}">
	<div class="toolbar-button-caret ${config.hasDropDown ? '' : 'hidden'}">
	  <div class="caret"></div>
	</div>
</div>`);
		let renderer = context.templateRegistry.createTemplateRenderer(config.template);
		this.$button = $(renderer.render(config.recordData)).prependTo(this.$buttonWrapper);
		this.$dropDownCaret = this.$buttonWrapper.find(".toolbar-button-caret");
		this.optimizedWidth = AbstractUiToolContainer.optimizeButtonWidth(this.$buttonWrapper, this.$button, (config.template as UiTextCellTemplateConfig).maxHeight || UiToolbar.DEFAULT_TOOLBAR_MAX_HEIGHT);
		this.setVisible(config.visible);
		this.setHasDropDown(config.hasDropDown);
		this.setDropDownComponent(config.dropDownComponent);

		this.$buttonWrapper.on("mousedown", (e) => {
			if (this.config.togglesFullScreenOnComponent) {
				if (isFullScreen()) {
					exitFullScreen();
				} else {
					enterFullScreen(this.context.getComponentById(this.config.togglesFullScreenOnComponent));
				}
			}
			if (this.config.openNewTabWithUrl) {
				window.open(this.config.openNewTabWithUrl, '_blank');
			}

			let dropdownClickInfo: UiDropDownButtonClickInfoConfig = null;
			if (this.hasDropDown) {
				if (this.dropDown == null) {
					this.dropDown = new UiDropDown(this.dropDownComponent);
				}
				dropdownClickInfo = createUiDropDownButtonClickInfoConfig(!this.dropDown.isOpen, this.dropDownComponent != null);
				if (!this.dropDown.isOpen) {
					this.dropDown.setContentComponent(this.dropDownComponent);
					this.dropDown.open({$reference: this.$buttonWrapper, width: config.dropDownPanelWidth});
				} else {
					this.dropDown.close();
				}
			}

			setTimeout(() => this.onClicked.fire(dropdownClickInfo)); // make sure that any blur event is processed before this!
		});
	}

	public getMainDomElement() {
		return this.$buttonWrapper;
	}

	setDropDownComponent(component: UiComponent) {
		this.dropDownComponent = component;
		if (this.dropDown != null) {
			this.dropDown.setContentComponent(component);
			if (component instanceof UiItemView) {
				component.onItemClicked.addListener(eventObject => {
					this.onDropDownItemClicked.fire({groupId: eventObject.groupId, itemId: eventObject.itemId});
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
		this.getMainDomElement().toggleClass("hidden", !visible);
	}

	isVisible(): boolean {
		return this.visible;
	}

	public setHasDropDown(hasDropDown: boolean) {
		this.hasDropDown = hasDropDown;
		this.$dropDownCaret.toggleClass("hidden", !hasDropDown);
	}

	get id() {
		return this.config.buttonId;
	}
}
