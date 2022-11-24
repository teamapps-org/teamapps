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
import {AbstractField} from "./AbstractField";
import {
	DtoFieldEditingMode,
	DtoSpecialKey,
	DtoTextField,
	DtoTextFieldCommandHandler,
	DtoTextFieldEventSource,
	DtoTextInputHandlingField_SpecialKeyPressedEvent,
	DtoTextInputHandlingField_TextInputEvent
} from "../../generated";
import {parseHtml, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {escapeHtml} from "../../Common";

export class TextField<C extends DtoTextField = DtoTextField> extends AbstractField<C, string> implements DtoTextFieldEventSource, DtoTextFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<DtoTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<DtoTextInputHandlingField_TextInputEvent>({throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<DtoTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<DtoTextInputHandlingField_SpecialKeyPressedEvent>({throttlingMode: "debounce", delay: 250});

	private $wrapper: HTMLElement;
	protected $field: HTMLInputElement;
	private showClearButton: boolean;

	protected initialize(config: C, context: TeamAppsUiContext) {
		this.$wrapper = parseHtml(`<div class="DtoTextField field-border field-border-glow field-background clearable-field-wrapper form-control">
    <input type="text"></input>
    <div class="clear-button tr-remove-button"></div>  
</div>`);
		this.$field = this.$wrapper.querySelector(":scope input");
		if (!config.autofill) {
			this.$field.autocomplete = "no";
		}
		let $clearButton = this.$wrapper.querySelector<HTMLElement>(':scope .clear-button');
		$clearButton.addEventListener('click',() => {
			this.$field.value = "";
			this.fireTextInput();
			this.commit();
			this.updateClearButton();
		});

		this.setPlaceholderText(config.placeholderText);
		this.setMaxCharacters(config.maxCharacters);
		this.setShowClearButton(config.showClearButton);

		this.$field.addEventListener("focus", () => {
			if (this.getEditingMode() !== DtoFieldEditingMode.READONLY) {
				this.$field.select();
			}
		});
		this.$field.addEventListener("change", () => {
			if (this.getEditingMode() !== DtoFieldEditingMode.READONLY) {
				this.commit();
				this.updateClearButton();
			}
		});
		this.$field.addEventListener("input", () => {
			this.fireTextInput();
			this.updateClearButton();
		});
		this.$field.addEventListener("keydown", (e) => {
			if (e.key === 'Escape') {
				this.displayCommittedValue(); // back to committedValue
				this.fireTextInput();
				this.$field.select();
				this.onSpecialKeyPressed.fire({
					key: DtoSpecialKey.ESCAPE
				});
			} else if (e.key === 'Enter') {
				if (this.getEditingMode() !== DtoFieldEditingMode.READONLY) {
					// this needs to be done here, additionally, since otherwise the onSpecialKeyPressed gets fired before the commit...
					this.commit();
				}
				this.onSpecialKeyPressed.fire({
					key: DtoSpecialKey.ENTER
				});
			}
		});

		this.$wrapper.addEventListener('click',(e) => {
			if (e.target !== this.$field) {
				this.focus();
			}
		});
	}

	private fireTextInput() {
		this.onTextInput.fire({
			enteredString: this.$field.value
		});
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	setMaxCharacters(maxCharacters: number): void {
		if (maxCharacters) {
			this.$field.maxLength = maxCharacters;
		} else {
			this.$field.removeAttribute("maxLength");
		}
	}

	setShowClearButton(showClearButton: boolean): void {
		this.showClearButton = showClearButton;
		this.updateClearButton();
	}

	private updateClearButton() {
		this.$wrapper.classList.toggle("clearable", !!(this.showClearButton && this.$field.value));
	}

	setPlaceholderText(placeholderText: string): void {
		this.$field.placeholder = placeholderText || '';
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

	focus(): void {
		this.$field.focus();
	}

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode): void {
		AbstractField.defaultOnEditingModeChangedImpl(this, () => this.$field);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-DtoTextField">${value == null ? "" : escapeHtml(value)}</div>`;
	}

	getDefaultValue(): string {
		return "";
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}

}


