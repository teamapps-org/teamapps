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
	UiFieldEditingMode,
	UiMultiLineTextFieldCommandHandler,
	UiMultiLineTextFieldConfig,
	UiMultiLineTextFieldEventSource,
	UiSpecialKey,
	UiTextInputHandlingField_SpecialKeyPressedEvent,
	UiTextInputHandlingField_TextInputEvent
} from "../../generated";
import {Constants, escapeHtml, hasVerticalScrollBar, parseHtml} from "../../Common";
import {TeamAppsUiComponentRegistry} from "teamapps-client-core";
import {TeamAppsUiContext} from "teamapps-client-core";
import {executeWhenFirstDisplayed} from "../../util/ExecuteWhenFirstDisplayed";
import {TeamAppsEvent} from "teamapps-client-core";
import {AbstractUiField} from "./AbstractUiField";

export class UiMultiLineTextField extends AbstractUiField<UiMultiLineTextFieldConfig, string> implements UiMultiLineTextFieldEventSource, UiMultiLineTextFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>({
		throttlingMode: "debounce",
		delay: 250
	});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>({
		throttlingMode: "debounce",
		delay: 250
	});

	private $wrapper: HTMLElement;
	private $field: HTMLTextAreaElement;
	private $clearButton: HTMLElement;
	private showClearButton: boolean;

	protected initialize(config: UiMultiLineTextFieldConfig, context: TeamAppsUiContext) {
		this.$wrapper = parseHtml(`<div class="UiMultiLineTextField teamapps-input-wrapper field-border field-border-glow field-background">
	<textarea></textarea>
	<div class="clear-button tr-remove-button"></div>
</div>`);
		this.$field = this.$wrapper.querySelector(":scope textarea");
		this.$clearButton = this.$wrapper.querySelector<HTMLElement>(':scope .clear-button');
		this.$clearButton.addEventListener('click', () => {
			this.$field.value = "";
			this.fireTextInput();
			this.commit();
			this.updateClearButton();
		});

		this.setPlaceholderText(config.placeholderText);
		this.setMaxCharacters(config.maxCharacters);
		this.setShowClearButton(config.showClearButton);

		this.$field.addEventListener('focus', () => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
			}
		});
		this.$field.addEventListener('blur', () => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
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
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ESCAPE
				});
			} else if (e.key === 'Enter') {
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ENTER
				});
			}
		});

		this.updateClearButton();

		this.$field.addEventListener('input', () => this.updateTextareaHeight());
		this.updateTextareaHeight();
	}

	private fireTextInput() {
		this.onTextInput.fire({
			enteredString: this.$field.value
		});
	}

	public isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	@executeWhenFirstDisplayed(true)
	private updateTextareaHeight() {
		if (this.config.adjustHeightToContent) {
			this.$field.style.height = '2px';
			this.$field.style.height = this.$field.scrollHeight + 'px';
		}
	}

	public setMaxCharacters(maxCharacters: number): void {
		if (maxCharacters) {
			this.$field.maxLength = maxCharacters;
		} else {
			this.$field.removeAttribute("maxLength");
		}
	}

	public setShowClearButton(showClearButton: boolean): void {
		this.showClearButton = showClearButton;
		this.updateClearButton();
	}

	@executeWhenFirstDisplayed()
	private updateClearButton() {
		this.$wrapper.classList.toggle("clearable", !!(this.showClearButton && this.$field.value));
		this.$clearButton.style.right = hasVerticalScrollBar(this.$field) ? Constants.SCROLLBAR_WIDTH + "px" : "0";
	}

	public setPlaceholderText(placeholderText: string): void {
		this.$field.placeholder = placeholderText || '';
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$wrapper;
	}
	protected displayCommittedValue(): void {
		const value = this.getCommittedValue();
		this.$field.value = value || "";
		this.updateClearButton();
		this.updateTextareaHeight();
	}

	public getTransientValue(): string {
		return this.$field.value;
	}

	public focus(): void {
		this.$field.focus();
	}

	public append(s: string, scrollToBottom: boolean): void {
		const transientValue = this.getTransientValue();
		const transientValueString = (transientValue && transientValue) || '';
		this.setCommittedValue(transientValueString + s);
		if (scrollToBottom) {
			this.$wrapper.scrollTop = 10000000;
		}
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		AbstractUiField.defaultOnEditingModeChangedImpl(this, () => this.$field);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-UiMultiLineTextField">${value == null ? "" : escapeHtml(value)}</div>`;
	}

	public getDefaultValue(): string {
		return "";
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMultiLineTextField", UiMultiLineTextField);
