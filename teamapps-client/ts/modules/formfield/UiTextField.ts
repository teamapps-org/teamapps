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
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiCompositeFieldConfig} from "../../generated/UiCompositeFieldConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {escapeHtml} from "../Common";
import {UiTextFieldConfig, UiTextFieldCommandHandler, UiTextFieldEventSource} from "../../generated/UiTextFieldConfig";
import {keyCodes} from "trivial-components";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../generated/UiTextInputHandlingFieldConfig";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {EventFactory} from "../../generated/EventFactory";

export class UiTextField<C extends UiTextFieldConfig = UiTextFieldConfig> extends UiField<C, string> implements UiTextFieldEventSource, UiTextFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, 250);

	private $wrapper: JQuery;
	protected $field: JQuery;
	private showClearButton: boolean;

	protected initialize(config: C, context: TeamAppsUiContext) {
		this.$wrapper = $(`<div class="UiTextField field-border field-border-glow field-background clearable-field-wrapper form-control">
    <input autocomplete="off" type="text"/>
    <div class="clear-button tr-remove-button"/>  
</div>`);
		this.$field = this.$wrapper.find("input");
		let $clearButton = this.$wrapper.find('.clear-button');
		$clearButton.click(() => {
			this.$field.val("");
			this.fireTextInput();
			this.commit();
			this.updateClearButton();
		});

		this.setEmptyText(config.emptyText);
		this.setMaxCharacters(config.maxCharacters);
		this.setShowClearButton(config.showClearButton);

		this.$field.on("focus", () => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
				this.$field.select();
			}
		});
		this.$field.on("blur", () => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
				this.commit();
				this.updateClearButton();
			}
		});
		this.$field.on("input", () => {
			this.fireTextInput();
			this.updateClearButton();
		});
		this.$field.keydown((e) => {
			if (e.keyCode === keyCodes.escape) {
				this.displayCommittedValue(); // back to committedValue
				this.fireTextInput();
				this.$field.select();
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ESCAPE));
			} else if (e.keyCode === keyCodes.enter) {
				this.commit();
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ENTER));
			}
		});

		this.$wrapper.click((e) => {
			if (e.target !== this.$field[0]) {
				this.focus();
			}
		});
	}

	private fireTextInput() {
		this.onTextInput.fire(EventFactory.createUiTextInputHandlingField_TextInputEvent(this.getId(), this.$field.val().toString()));
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	setMaxCharacters(maxCharacters: number): void {
		this.$field.attr('maxlength', maxCharacters || "");
	}

	setShowClearButton(showClearButton: boolean): void {
		this.showClearButton = showClearButton;
		this.updateClearButton();
	}

	private updateClearButton() {
		this.$wrapper.toggleClass("clearable", !!(this.showClearButton && this.$field.val()));
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
	}

	public getTransientValue(): string {
		return this.$field.val().toString();
	}

	focus(): void {
		this.$field.focus();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-UiTextField">${value == null ? "" : escapeHtml(value)}</div>`;
	}

	getDefaultValue(): string {
		return "";
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}

}

TeamAppsUiComponentRegistry.registerFieldClass("UiTextField", UiTextField);
