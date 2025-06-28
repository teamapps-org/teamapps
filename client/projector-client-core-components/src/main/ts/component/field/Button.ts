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
import {
	bind,
	type Component,
	type FieldEditingMode,
	type DtoFieldMessage,
	parseHtml,
	ProjectorEvent,
	type Template,
	executeAfterAttached, FieldEditingModes
} from "projector-client-object-api";
import {DropDown} from "../../micro-components/DropDown";
import {
	type DtoButton,
	type DtoButton_ClickEvent,
	type DtoButton_DropDownOpenedEvent,
	type DtoButtonCommandHandler,
	type DtoButtonEventSource
} from "../../generated";
import {AbstractField} from "projector-client-object-api";

export class Button extends AbstractField<DtoButton, void> implements DtoButtonEventSource, DtoButtonCommandHandler {

	public readonly onClick: ProjectorEvent<DtoButton_ClickEvent> = new ProjectorEvent();
	public readonly onDropDownOpened: ProjectorEvent<DtoButton_DropDownOpenedEvent> = new ProjectorEvent();

	private templateRecord: any;

	private $main: HTMLElement;

	private _dropDown: DropDown; // lazy-init!
	private dropDownComponent: Component;
	private minDropDownWidth: number;
	private minDropDownHeight: number;
	private openDropDownIfNotSet: boolean;

	protected initialize(config: DtoButton) {
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
						this.dropDown.open({
							$reference: this.getMainInnerDomElement(),
							width: Math.max(this.minDropDownWidth, width),
							minHeight: this.minDropDownHeight
						});
						this.onDropDownOpened.fire({});
						this.getMainInnerDomElement().classList.add("open");
					} else {
						this.dropDown.close(); // not needed for clicks, but for keydown!
					}
				}
			}
		}));

		// It is necessary to commit only after a full "click", since fields commit on blur. E.g. a DtoTextField should blur-commit first, before the button click-commits. This would not work with "mousedown".
		["click", "keypress"].forEach(eventName => this.getMainInnerDomElement().addEventListener(eventName, (e) => {
			if (e.type === "click" || (e as KeyboardEvent).key === "Enter" || (e as KeyboardEvent).key === " ") {
				if (this.getEditingMode() === FieldEditingModes.EDITABLE || this.getEditingMode() === FieldEditingModes.EDITABLE_IF_FOCUSED) {
					this.onClick.fire({});
					this.commit(true);
				}
			}
		}));
		this.setDropDownComponent(config.dropDownComponent as Component);
	}

	setDropDownSize(minDropDownWidth: number, minDropDownHeight: number): void {
		this.minDropDownWidth = minDropDownWidth;
		this.minDropDownHeight = minDropDownHeight;
	}

	setDropDownComponent(component: Component): void {
		if (this.dropDownComponent != null && (this.dropDownComponent as any).onItemClicked != null) {
			(this.dropDownComponent as any).onItemClicked.removeListener(this.closeDropDown);
		}
		this.dropDownComponent = component;
		if (component != null) {
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

	private get dropDown(): DropDown {
		// lazy-init!
		if (this._dropDown == null) {
			this._dropDown = new DropDown();
			this._dropDown.getMainDomElement().classList.add("DtoButton-dropdown");
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

	setTemplate(template: Template, templateRecord: any): void {
		this.config.template = template;
		this.setTemplateRecord(templateRecord);
	}

	setTemplateRecord(data: any): void {
		this.templateRecord = data;
		let innerHtml = this.renderContent();
		this.$main.innerHTML = innerHtml;
	}

	private renderContent(): string {
		return (this.config.template as Template)?.render(this.templateRecord) ?? this.templateRecord;
	}

	setFieldMessages(fieldMessageConfigs: DtoFieldMessage[]): void {
		super.setFieldMessages(fieldMessageConfigs);
	}

	@executeAfterAttached()
	focus(): void {
		this.$main.focus();
	}

	protected displayCommittedValue(): void {
		// do nothing...
	}

	getTransientValue(): true {
		return null;
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode): void {
		AbstractField.defaultOnEditingModeChangedImpl(this, () => this.$main);
	}

	public getReadOnlyHtml(value: void, availableWidth: number): string {
		return `<div class="Button field-border field-border-glow field-background">
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

}


