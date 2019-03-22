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
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiToolButton_ClickedEvent, UiToolButton_DropDownOpenedEvent, UiToolButtonCommandHandler, UiToolButtonConfig, UiToolButtonEventSource} from "../../generated/UiToolButtonConfig";
import {UiComponent} from "../UiComponent";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "../../generated/UiComponentConfig";
import {EventFactory} from "../../generated/EventFactory";
import {UiDropDown} from "./UiDropDown";
import {UiItemView} from "../UiItemView";
import {bind} from "../util/Bind";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiCalendar} from "../UiCalendar";

export class UiToolButton extends UiComponent<UiToolButtonConfig> implements UiToolButtonEventSource, UiToolButtonCommandHandler {

	public readonly onClicked: TeamAppsEvent<UiToolButton_ClickedEvent> = new TeamAppsEvent(this);
	public readonly onDropDownOpened: TeamAppsEvent<UiToolButton_DropDownOpenedEvent> = new TeamAppsEvent(this);

	private $button: JQuery;

	private _dropDown: UiDropDown; // lazy-init!
	private dropDownComponent: UiComponent;
	private minDropDownWidth: number;
	private minDropDownHeight: number;
	private openDropDownIfNotSet: boolean;

	constructor(config: UiToolButtonConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.minDropDownWidth = config.minDropDownWidth;
		this.minDropDownHeight = config.minDropDownHeight;
		this.openDropDownIfNotSet = config.openDropDownIfNotSet;

		this.$button = $(`<div class="UiToolButton img img-12 ${config.grayOutIfNotHovered ? 'gray-out-if-not-hovered' : ''}" style="background-image: url(${context.getIconPath(config.icon, 12)});"></div>`)
			.on('click', () => {
				if (this.dropDownComponent != null || this.openDropDownIfNotSet) {
					if (!this.dropDown.isOpen) {
						const width = this.getMainDomElement()[0].offsetWidth;
						console.log(width);
						this.dropDown.open({$reference: this.getMainDomElement(), width: Math.max(this.minDropDownWidth, width), minHeight: this.minDropDownHeight});
						this.onDropDownOpened.fire(EventFactory.createUiToolButton_DropDownOpenedEvent(this.getId()));
						this.getMainDomElement().addClass("open");
					} else {
						this.dropDown.close(); // not needed for clicks, but for keydown!
					}
				}
				this.onClicked.fire(EventFactory.createUiToolButton_ClickedEvent(this.getId()));
			});
		this.setDropDownComponent(config.dropDownComponent);
	}

	private get dropDown(): UiDropDown {
		// lazy-init!
		if (this._dropDown == null) {
			this._dropDown = new UiDropDown();
			this._dropDown.getMainDomElement().addClass("UiButton-dropdown");
			this._dropDown.onClose.addListener(eventObject => this.getMainDomElement().removeClass("open"))
		}
		return this._dropDown;
	}

	getMainDomElement(): JQuery {
		return this.$button;
	}

	setDropDownSize(minDropDownWidth: number, minDropDownHeight: number): void {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
	}

	setDropDownComponent(component: UiComponent): void {
		if (this.dropDownComponent != null && this.dropDownComponent instanceof UiItemView) {
			this.dropDownComponent.onItemClicked.removeListener(this.closeDropDown);
		}
		if (component != null) {
			this.dropDownComponent = component;
			if (this.dropDownComponent instanceof UiItemView) {
				this.dropDownComponent.onItemClicked.addListener(this.closeDropDown);
			}
			this.dropDown.setContentComponent(this.dropDownComponent);
			this.dropDownComponent.attachedToDom = true;
		} else {
			this.dropDownComponent = null;
			this.dropDown.setContentComponent(null);
		}
	}

	@bind
	private closeDropDown() {
		this.dropDown.close();
	}

	setOpenDropDownIfNotSet(openDropDownIfNotSet: boolean): void {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
	}

	setGrayOutIfNotHovered(grayOutIfNotHovered: boolean): void {
		// TODO
	}

	setIcon(icon: string): void {
		this.$button.css("background-image", `url(${this._context.getIconPath(icon, 12)})`)
	}

	setPopoverText(popoverText: string): void {
		// TODO
	}

	destroy(): void {
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiToolButton", UiToolButton);
