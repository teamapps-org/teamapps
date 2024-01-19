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
import {UiItemView} from "../UiItemView";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiDropDown} from "../micro-components/UiDropDown";
import {UiTemplateConfig} from "../../generated/UiTemplateConfig";
import {
	UiButton_ClickedEvent,
	UiButton_DropDownOpenedEvent,
	UiButtonCommandHandler,
	UiButtonConfig,
	UiButtonEventSource
} from "../../generated/UiButtonConfig";
import {UiField} from "./UiField";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {bind} from "../util/Bind";
import {UiFieldMessageConfig} from "../../generated/UiFieldMessageConfig";
import {parseHtml} from "../Common";
import {UiComponent} from "../UiComponent";

export class UiButton extends UiField<UiButtonConfig, void> implements UiButtonEventSource, UiButtonCommandHandler {

	public readonly onClicked: TeamAppsEvent<UiButton_ClickedEvent> = new TeamAppsEvent();
	public readonly onDropDownOpened: TeamAppsEvent<UiButton_DropDownOpenedEvent> = new TeamAppsEvent();

	private template: UiTemplateConfig;
	private templateRecord: any;

	private $main: HTMLElement;

	private _dropDown: UiDropDown; // lazy-init!
	private dropDownComponent: UiComponent;
	private minDropDownWidth: number;
	private minDropDownHeight: number;
	private openDropDownIfNotSet: boolean;
	private onClickJavaScript: string;

	protected initialize(config: UiButtonConfig, context: TeamAppsUiContext) {
		this.template = config.template;
		this.templateRecord = config.templateRecord;

		this.$main = parseHtml(this.getReadOnlyHtml(this.templateRecord, -1));

		this.minDropDownWidth = config.minDropDownWidth;
		this.minDropDownHeight = config.minDropDownHeight;
		this.openDropDownIfNotSet = config.openDropDownIfNotSet;
		['mousedown', 'keydown'].forEach((eventName) => this.getMainInnerDomElement().addEventListener(eventName, (e: Event) => {
			if (e.type === "mousedown" || (e as KeyboardEvent).key === "Enter" || (e as KeyboardEvent).key === " ") {
				if (this.dropDownComponent != null || this.openDropDownIfNotSet) {
					if (!this.dropDown.isOpen) {
						const width = this.getMainInnerDomElement().offsetWidth;
						this.dropDown.open({$reference: this.getMainInnerDomElement(), width: Math.max(this.minDropDownWidth, width), minHeight: this.minDropDownHeight});
						this.onDropDownOpened.fire({});
						this.getMainInnerDomElement().classList.add("open");
					} else {
						this.dropDown.close(); // not needed for clicks, but for keydown!
					}
				}
			}
		}));

		// It is necessary to commit only after a full "click", since fields commit on blur. E.g. a UiTextField should blur-commit first, before the button click-commits. This would not work with "mousedown".
		["click", "keypress"].forEach(eventName => this.getMainInnerDomElement().addEventListener(eventName, (e) => {
			if (e.type === "click" || (e as KeyboardEvent).key === "Enter" || (e as KeyboardEvent).key === " ") {
				if (this.getEditingMode() === UiFieldEditingMode.EDITABLE || this.getEditingMode() === UiFieldEditingMode.EDITABLE_IF_FOCUSED) {
					if (this.onClickJavaScript != null) {
						let context = this._context; // make context available in evaluated javascript
						eval(this.onClickJavaScript);
					}
					this.onClicked.fire({});
					this.commit(true);
				}
			}
		}));
		this.setDropDownComponent(config.dropDownComponent as UiComponent);
		this.setOnClickJavaScript(config.onClickJavaScript);
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
	public closeDropDown() {
		this.dropDown.close();
	}

	private get dropDown(): UiDropDown {
		// lazy-init!
		if (this._dropDown == null) {
			this._dropDown = new UiDropDown();
			this._dropDown.getMainDomElement().classList.add("UiButton-dropdown");
			this._dropDown.onClose.addListener(eventObject => this.getMainInnerDomElement().classList.remove("open"))
		}
		return this._dropDown;
	}

	setOpenDropDownIfNotSet(openDropDownIfNotSet: boolean): void {
		this.openDropDownIfNotSet = openDropDownIfNotSet;
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	setTemplate(template: UiTemplateConfig, templateRecord: any): void {
		this.template = template;
		this.setTemplateRecord(templateRecord);
	}

	setTemplateRecord(data: any): void {
		this.templateRecord = data;
		let innerHtml = this.renderContent();
		this.$main.innerHTML = innerHtml;
	}

	private renderContent(): string {
		return this.template != null ? this._context.templateRegistry.createTemplateRenderer(this.template).render(this.templateRecord) : this.templateRecord;
	}

	setFieldMessages(fieldMessageConfigs: UiFieldMessageConfig[]): void {
		super.setFieldMessages(fieldMessageConfigs);
	}

	focus(): void {
		this.$main.focus();
	}

	protected displayCommittedValue(): void {
		// do nothing...
	}

	getTransientValue(): true {
		return null;
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this, () => this.$main);
	}

	public getReadOnlyHtml(value: void, availableWidth: number): string {
		return `<div class="UiButton btn field-border field-border-glow field-background">
			${this.renderContent()}
        </div>`;
	}

	public valuesChanged(v1: void, v2: void): boolean {
		return false;
	}

	isValidData(v: void): boolean {
		return true;
	}

	getDefaultValue(): true {
		return true;
	}

	setOnClickJavaScript(onClickJavaScript: string): void {
		this.onClickJavaScript = onClickJavaScript;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiButton", UiButton);
