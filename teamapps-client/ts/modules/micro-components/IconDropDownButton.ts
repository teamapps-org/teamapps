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

import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {
	UiToolButton_ClickedEvent,
	UiToolButton_DropDownClosedEvent,
	UiToolButton_DropDownOpenedEvent,
	UiToolButtonCommandHandler,
	UiToolButtonConfig,
	UiToolButtonEventSource
} from "../../generated/UiToolButtonConfig";
import {AbstractUiComponent} from "../AbstractUiComponent";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiDropDown} from "./UiDropDown";
import {UiItemView} from "../UiItemView";
import {bind} from "../util/Bind";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {parseHtml} from "../Common";
import {UiComponent} from "../UiComponent";
import {UiComponentConfig} from "../../generated/UiComponentConfig";

export class IconDropDownButtonConfig {
	icon: string;
	popoverText: string;
	dropDownWidth?: number;
	dropDownComponent: { getMainElement(): HTMLElement };
}

export class IconDropDownButton {

	public readonly onClicked: TeamAppsEvent<UiToolButton_ClickedEvent> = new TeamAppsEvent(this);
	public readonly onDropDownOpened: TeamAppsEvent<UiToolButton_DropDownOpenedEvent> = new TeamAppsEvent(this);
	public readonly onDropDownClosed: TeamAppsEvent<UiToolButton_DropDownClosedEvent> = new TeamAppsEvent(this);

	private $button: HTMLElement;

	private _dropDown: UiDropDown; // lazy-init!
	private dropDownComponent: UiComponent;

	constructor(private config: IconDropDownButtonConfig) {
		this.$button = parseHtml(`<img class="IconDropDownButton" src="${config.icon}"></img>`);
		this.$button.addEventListener('click', (e) => {
			e.stopPropagation();
			if (!this.dropDown.isOpen) {
				const width = this.getMainElement().offsetWidth;
				this.dropDown.open({$reference: this.getMainElement(), width: Math.max(config.dropDownWidth ?? 250, width), minHeight: 50});
				this.onDropDownOpened.fire({});
				this.getMainElement().classList.add("open");
			} else {
				this.closeDropDown(); // not needed for clicks, but for keydown!
			}
			this.onClicked.fire({});
		});
		this.setDropDownComponent(config.dropDownComponent as UiComponent);
		this.setPopoverText(config.popoverText);
	}

	private get dropDown(): UiDropDown {
		// lazy-init!
		if (this._dropDown == null) {
			this._dropDown = new UiDropDown();
			this._dropDown.getMainDomElement().classList.add("UiButton-dropdown");
			this._dropDown.onClose.addListener(eventObject => {
				this.getMainElement().classList.remove("open");
				this.onDropDownClosed.fire({});
			})
		}
		return this._dropDown;
	}

	getMainElement(): HTMLElement {
		return this.$button;
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
		} else {
			this.dropDownComponent = null;
			this.dropDown.setContentComponent(null);
		}
	}

	@bind
	public closeDropDown() {
		this.dropDown.close();
	}

	setGrayOutIfNotHovered(grayOutIfNotHovered: boolean): void {
		// TODO
	}

	setIcon(icon: string): void {
		this.$button.style.backgroundImage = `url('${icon}')`;
	}

	setPopoverText(popoverText: string): void {
		this.config.popoverText = popoverText;
		this.getMainElement().title = popoverText;
	}

}
