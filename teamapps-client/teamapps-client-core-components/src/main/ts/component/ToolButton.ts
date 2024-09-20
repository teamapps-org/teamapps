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

import {AbstractLegacyComponent, bind, Component, parseHtml, ServerObjectChannel, TeamAppsEvent} from "projector-client-object-api";
import {
	DtoToolButton,
	DtoToolButton_ClickedEvent,
	DtoToolButton_DropDownOpenedEvent,
	DtoToolButtonCommandHandler,
	DtoToolButtonEventSource
} from "../generated";
import {DropDown} from "../micro-components/DropDown";

export class ToolButton extends AbstractLegacyComponent<DtoToolButton> implements DtoToolButtonEventSource, DtoToolButtonCommandHandler {

	public readonly onClicked: TeamAppsEvent<DtoToolButton_ClickedEvent> = new TeamAppsEvent();
	public readonly onDropDownOpened: TeamAppsEvent<DtoToolButton_DropDownOpenedEvent> = new TeamAppsEvent();

	private $button: HTMLElement;
	private $icon: HTMLElement;
	private $caption: HTMLElement;

	private _dropDown: DropDown; // lazy-init!
	private dropDownComponent: Component;
	private minDropDownWidth: number;
	private minDropDownHeight: number;
	private openDropDownIfNotSet: boolean;

	constructor(config: DtoToolButton, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.minDropDownWidth = config.minDropDownWidth;
		this.minDropDownHeight = config.minDropDownHeight;
		this.openDropDownIfNotSet = config.openDropDownIfNotSet;

		this.$button = parseHtml(`<div class="ToolButton">
	<div class="img ${config.grayOutIfNotHovered ? 'gray-out-if-not-hovered' : ''}" style="background-image: url('${config.icon}');"></div>
	<div class="caption">${config.caption ?? ""}</div>
</div>`);
		this.$icon = this.$button.querySelector(":scope .img");
		this.$caption = this.$button.querySelector(":scope .caption");
		this.$button.addEventListener('click', () => {
			if (this.dropDownComponent != null || this.openDropDownIfNotSet) {
				if (!this.dropDown.isOpen) {
					const width = this.getMainElement().offsetWidth;
					this.dropDown.open({
						$reference: this.getMainElement(),
						width: Math.max(this.minDropDownWidth, width),
						minHeight: this.minDropDownHeight
					});
					this.onDropDownOpened.fire({});
					this.getMainElement().classList.add("open");
				} else {
					this.dropDown.close(); // not needed for clicks, but for keydown!
				}
			}
			this.onClicked.fire({});
		});
		this.setIconSize(config.iconSize);
		this.setDropDownComponent(config.dropDownComponent as Component);
	}

	private get dropDown(): DropDown {
		// lazy-init!
		if (this._dropDown == null) {
			this._dropDown = new DropDown();
			this._dropDown.getMainDomElement().classList.add("DtoButton-dropdown");
			this._dropDown.onClose.addListener(eventObject => this.getMainElement().classList.remove("open"))
		}
		return this._dropDown;
	}

	doGetMainElement(): HTMLElement {
		return this.$button;
	}

	setDropDownSize(minDropDownWidth: number, minDropDownHeight: number): void {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
	}

	setDropDownComponent(component: Component): void {
		if (this.dropDownComponent != null && (this.dropDownComponent as any).onItemClicked != null) {
			(this.dropDownComponent as any).onItemClicked.removeListener(this.closeDropDown);
		}
		if (component != null) {
			this.dropDownComponent = component;
			if ((this.dropDownComponent as any).onItemClicked != null) {
				(this.dropDownComponent as any).onItemClicked.addListener(this.closeDropDown);
			}
			this.dropDown.setContentComponent(this.dropDownComponent?.getMainElement());
		} else {
			this.dropDownComponent = null;
			this.dropDown.setContentComponent(null);
		}
	}

	@bind
	public closeDropDown() {
		this.dropDown.close();
	}

	setOpenDropDownIfNotSet(openDropDownIfNotSet: boolean): void {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
	}

	setGrayOutIfNotHovered(grayOutIfNotHovered: boolean): void {
		// TODO
	}

	setIcon(icon: string): void {
		this.$icon.style.backgroundImage = `url('${icon}')`;
	}

	setIconSize(iconSize: number | null): any {
		if (iconSize != null) {
			this.$icon.style.width = `${iconSize}px`;
			this.$icon.style.height = `${iconSize}px`;
			this.$icon.style.backgroundSize = `${iconSize}px`;
		} else {
			this.$icon.style.width = null;
			this.$icon.style.height = null;
			this.$icon.style.backgroundSize = null;
		}
	}

	setCaption(caption: string) {
		this.$caption.innerText = caption;
	}

	setPopoverText(popoverText: string): void {
		// TODO
	}

}


