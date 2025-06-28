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
import {AbstractField, executeAfterAttached, type FieldEditingMode, parseHtml} from "projector-client-object-api";
import {type DtoColorPicker, type DtoColorPickerCommandHandler, type DtoColorPickerEventSource} from "../../generated";
import {escapeHtml} from "../../util/Common";

export class ColorPicker<C extends DtoColorPicker = DtoColorPicker> extends AbstractField<C, string> implements DtoColorPickerEventSource, DtoColorPickerCommandHandler {

	private $wrapper: HTMLElement;
	protected $field: HTMLInputElement;
	private clearable: boolean;

	protected initialize(config: C) {
		this.$wrapper = parseHtml(`<div class="ColorPicker field-border field-border-glow field-background clearable-field-wrapper form-control">
    <input type="color"></input>
    <div class="clear-button tr-remove-button"></div>  
</div>`);
		this.$field = this.$wrapper.querySelector(":scope input");

		let $clearButton = this.$wrapper.querySelector<HTMLElement>(':scope .clear-button');
		$clearButton.addEventListener('click',() => {
			this.$field.value = "";
			this.commit();
			this.updateClearButton();
		});

		this.setClearable(config.clearable);

		this.$wrapper.addEventListener('click',(e) => {
			if (e.target !== this.$field) {
				this.focus();
			}
		});
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	setClearable(clearable: boolean): void {
		this.clearable = clearable;
		this.updateClearButton();
	}

	private updateClearButton() {
		this.$wrapper.classList.toggle("clearable", !!(this.clearable && this.$field.value));
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$wrapper;
	}
	protected displayCommittedValue(): void {
		let value = this.getCommittedValue();
		this.$field.value = value || "";
		this.updateClearButton();
	}

	public getTransientValue(): string {
		return this.$field.value;
	}

	@executeAfterAttached()
	focus(): void {
		this.$field.focus();
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode): void {
		AbstractField.defaultOnEditingModeChangedImpl(this, () => this.$field);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-ColorPicker"><div class="color-display" style="background-color: ${value == null ? "" : escapeHtml(value)}"></div>`;
	}

	getDefaultValue(): string {
		return "";
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}

}


