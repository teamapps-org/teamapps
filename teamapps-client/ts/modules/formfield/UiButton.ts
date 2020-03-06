/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {UiItemView} from "../UiItemView";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiDropDown} from "../micro-components/UiDropDown";
import {UiTemplateConfig} from "../../generated/UiTemplateConfig";
import {UiButton_ClickedEvent, UiButton_DropDownOpenedEvent, UiButtonCommandHandler, UiButtonConfig, UiButtonEventSource} from "../../generated/UiButtonConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {bind} from "../util/Bind";
import {parseHtml} from "../Common";
import {UiComponent} from "../UiComponent";
import {AbstractUiComponent} from "../AbstractUiComponent";

export class UiButton extends AbstractUiComponent<UiButtonConfig> implements UiButtonEventSource, UiButtonCommandHandler {

	public readonly onClicked: TeamAppsEvent<UiButton_ClickedEvent> = new TeamAppsEvent(this);
	public readonly onDropDownOpened: TeamAppsEvent<UiButton_DropDownOpenedEvent> = new TeamAppsEvent(this);

	private template: UiTemplateConfig;
	private templateRecord: any;

	private $main: HTMLElement;

	private _dropDown: UiDropDown; // lazy-init!
	private dropDownComponent: UiComponent;
	private minDropDownWidth: number;
	private minDropDownHeight: number;
	private openDropDownIfNotSet: boolean;

	constructor(config: UiButtonConfig, context: TeamAppsUiContext) {
		super(config, context);
		
		this.$main = parseHtml(`<div class="UiButton btn field-border field-border-glow" tabindex="0"></div>`);
		this.setTemplate(config.template, config.templateRecord);

		this.minDropDownWidth = config.minDropDownWidth;
		this.minDropDownHeight = config.minDropDownHeight;
		this.openDropDownIfNotSet = config.openDropDownIfNotSet;
		['mousedown', 'keydown'].forEach((eventName) => this.$main.addEventListener(eventName, (e: Event) => {
			if (e.type === "mousedown" || (e as KeyboardEvent).key === "Enter" || (e as KeyboardEvent).key === " ") {
				if (this.dropDownComponent != null || this.openDropDownIfNotSet) {
					if (!this.dropDown.isOpen) {
						const width = this.$main.offsetWidth;
						this.dropDown.open({$reference: this.$main, width: Math.max(this.minDropDownWidth, width), minHeight: this.minDropDownHeight});
						this.onDropDownOpened.fire({});
						this.$main.classList.add("open");
					} else {
						this.dropDown.close(); // not needed for clicks, but for keydown!
					}
				}
			}
		}));

		// It is necessary to commit only after a full "click", since fields commit on blur. E.g. a UiTextField should blur-commit first, before the button click-commits. This would not work with "mousedown".
		["click", "keypress"].forEach(eventName => this.$main.addEventListener(eventName, (e) => {
			if (e.type === "click" || (e as KeyboardEvent).key === "Enter" || (e as KeyboardEvent).key === " ") {
				this.onClicked.fire({});
			}
		}));

		this.$main.addEventListener("focus", () => this.$main.classList.add("focus"));
		this.$main.addEventListener("blur", () => this.$main.classList.remove("focus"));
		this.setDropDownComponent(config.dropDownComponent as UiComponent);
	}

	setDropDownSize(minDropDownWidth: number, minDropDownHeight: number): void {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
	}

	setDropDownComponent(component: UiComponent): void {
		if (this.dropDownComponent != null && this.dropDownComponent instanceof UiItemView) {
			this.dropDownComponent.onItemClicked.removeListener(this.closeDropDown);
		}
		this.dropDownComponent = component;
		if (component != null) {
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
	private closeDropDown() {
		this.dropDown.close();
	}

	private get dropDown(): UiDropDown {
		// lazy-init!
		if (this._dropDown == null) {
			this._dropDown = new UiDropDown();
			this._dropDown.getMainDomElement().classList.add("UiButton-dropdown");
			this._dropDown.onClose.addListener(() => this.$main.classList.remove("open"))
		}
		return this._dropDown;
	}

	setOpenDropDownIfNotSet(openDropDownIfNotSet: boolean): void {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	setTemplate(template: UiTemplateConfig, templateRecord: any): void {
		this.template = template;
		this.setTemplateRecord(templateRecord);
	}

	setTemplateRecord(data: any): void {
		this.templateRecord = data;
		this.$main.innerHTML = this.template != null ? this._context.templateRegistry.createTemplateRenderer(this.template).render(this.templateRecord) : this.templateRecord;
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiButton", UiButton);
