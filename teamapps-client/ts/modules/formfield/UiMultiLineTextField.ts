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
import {UiField} from "./UiField";
import {UiMultiLineTextFieldConfig, UiMultiLineTextFieldCommandHandler, UiMultiLineTextFieldEventSource} from "../../generated/UiMultiLineTextFieldConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {Constants, escapeHtml, hasVerticalScrollBar} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../generated/UiTextInputHandlingFieldConfig";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {keyCodes} from "trivial-components";
import {executeWhenAttached} from "../util/ExecuteWhenAttached";
import {EventFactory} from "../../generated/EventFactory";

export class UiMultiLineTextField extends UiField<UiMultiLineTextFieldConfig, string> implements UiMultiLineTextFieldEventSource, UiMultiLineTextFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, 250);

	private $wrapper: JQuery;
	private $field: JQuery;
	private $clearButton: JQuery;
	private showClearButton: boolean;
	private minHeight: number;
	private maxHeight: number;


	protected initialize(config: UiMultiLineTextFieldConfig, context: TeamAppsUiContext) {
		this.$wrapper = $(`<div class="UiMultiLineTextField teamapps-input-wrapper field-border field-border-glow field-background">
	<textarea></textarea>
	<div class="clear-button tr-remove-button"/>  
</div>`);
		this.$field = this.$wrapper.find("textarea");
		this.$clearButton = this.$wrapper.find('.clear-button');
		this.$clearButton.click(() => {
			this.$field.val("");
			this.fireTextInput();
			this.commit();
			this.updateClearButton();
		});

		this.setEmptyText(config.emptyText);
		this.setMaxCharacters(config.maxCharacters);
		this.setShowClearButton(config.showClearButton);

		this.$field.on('focus', (e) => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
			}
		});
		this.$field.on('blur', (e) => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
				this.commit();
				this.updateClearButton();
			}
		});
		this.$field.on("input", () => {
			this.fireTextInput();
			this.updateClearButton();
		});
		this.$field.on("keydown", (e) => {
			if (e.keyCode === keyCodes.escape) {
				this.displayCommittedValue(); // back to committedValue
				this.fireTextInput();
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ESCAPE));
			} else if (e.keyCode === keyCodes.enter) {
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ENTER));
			}
		});

		this.updateClearButton();

		this.setMinHeight(config.minHeight);
		this.setMaxHeight(config.maxHeight);

		this.$field.on('input', () => this.updateTextareaHeight());
		this.updateTextareaHeight();
	}

	private fireTextInput() {
		this.onTextInput.fire(EventFactory.createUiTextInputHandlingField_TextInputEvent(this.getId(), this.$field.val().toString()));
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	@executeWhenAttached(true)
	private updateTextareaHeight() {
		this.$field[0].style.height = '0px';
		this.$field[0].style.height = (Math.max(this.minHeight - 2, Math.min(this.$field[0].scrollHeight, this.maxHeight - 2))) + 'px';
	}

	setMaxCharacters(maxCharacters: number): void {
		this.$field.attr('maxlength', maxCharacters || "");
	}

	setShowClearButton(showClearButton: boolean): void {
		this.showClearButton = showClearButton;
		this.updateClearButton();
	}

	@executeWhenAttached()
	private updateClearButton() {
		this.$wrapper.toggleClass("clearable", !!(this.showClearButton && this.$field.val()));
		this.$clearButton.css("right", hasVerticalScrollBar(this.$field[0]) ? Constants.SCROLLBAR_WIDTH + "px" : 0);
	}

	setEmptyText(emptyText: string): void {
		this.$field.attr("placeholder", emptyText || "");
	}

	public getMainInnerDomElement(): JQuery {
		return this.$wrapper;
	}

	public getFocusableElement(): JQuery {
		return this.$field;
	}

	protected displayCommittedValue(): void {
		let value = this.getCommittedValue();
		this.$field.val(value || "");
		this.updateClearButton();
		this.updateTextareaHeight();
	}

	public getTransientValue(): string {
		return this.$field.val().toString();
	}

	focus(): void {
		this.$field.focus();
	}

	append(s: string, scrollToBottom: boolean): void {
		let transientValue = this.getTransientValue();
		let transientValueString = (transientValue && transientValue) || '';
		this.setCommittedValue(transientValueString + s);
		if (scrollToBottom) {
			this.$wrapper.scrollTop(10000000);
		}
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-UiMultiLineTextField">${value == null ? "" : escapeHtml(value)}</div>`;
	}

	getDefaultValue(): string {
		return "";
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}

	public setMinHeight(minHeight: number) {
		this.minHeight = minHeight;
		this.$wrapper.css("min-height", minHeight ? minHeight + "px" : "");
		this.updateTextareaHeight();
	}

	public setMaxHeight(maxHeight: number) {
		this.maxHeight = maxHeight || Number.MAX_SAFE_INTEGER;
		this.$wrapper.css("max-height", maxHeight ? maxHeight + "px" : "");
		this.updateTextareaHeight();
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiMultiLineTextField", UiMultiLineTextField);
