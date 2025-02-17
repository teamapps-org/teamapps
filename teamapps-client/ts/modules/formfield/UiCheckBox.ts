/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import {UiCheckBoxCommandHandler, UiCheckBoxConfig, UiCheckBoxEventSource} from "../../generated/UiCheckBoxConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {generateUUID, parseHtml} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {keyCodes} from "../trivial-components/TrivialCore";
import {UiFieldMessageConfig} from "../../generated/UiFieldMessageConfig";
import {getHighestSeverity} from "../micro-components/FieldMessagesPopper";
import {UiFieldMessageSeverity} from "../../generated/UiFieldMessageSeverity";
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";


export class UiCheckBox extends UiField<UiCheckBoxConfig, boolean> implements UiCheckBoxEventSource, UiCheckBoxCommandHandler {

	private $main: HTMLElement;
	private $check: HTMLElement;
	private $label: HTMLElement;
	private $style: HTMLElement;

	private backgroundColor: string;
	private checkColor: string;
	private borderColor: string;

	protected initialize(config: UiCheckBoxConfig, context: TeamAppsUiContext) {
		const uuid = "cb-" + generateUUID();
		this.$main = parseHtml(`<div class="UiCheckBox">
				<style></style>
                <div class="checkbox-check field-border field-border-glow field-background" tabindex="0"></div>
                <div class="checkbox-label"></div>
            </div>`);
		this.$check = this.$main.querySelector<HTMLElement>(":scope .checkbox-check");
		this.$label = this.$main.querySelector<HTMLElement>(":scope .checkbox-label");
		this.$style = this.$main.querySelector<HTMLElement>(":scope style");

		this.setCaption(config.caption);
		this.setBackgroundColor(config.backgroundColor);
		this.setCheckColor(config.checkColor);
		this.setBorderColor(config.borderColor);

		this.$main.addEventListener("mousedown", () => {
			setTimeout(() => this.focus());
		});
		this.$main.addEventListener('click', () => {
			if (this.getEditingMode() === UiFieldEditingMode.DISABLED || this.getEditingMode() === UiFieldEditingMode.READONLY) {
				return;
			}
			this.toggleCommittedValue();
		});
		this.$check.addEventListener("keydown", (e) => {
			if (e.keyCode === keyCodes.space) {
				this.toggleCommittedValue();
				e.preventDefault(); // no scroll-down!
			}
		});
	}

	isValidData(v: boolean): boolean {
		return v == null || typeof v == "boolean";
	}

	private toggleCommittedValue() {
		this.setCommittedValue(!this.getCommittedValue());
		this.commit(true);
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	setCaption(caption: string): void {
		if (!this._config.htmlEnabled) {
			this.$label.textContent = caption || '';
		} else {
			this.$label.innerHTML = caption || '';
		}
	}

	setBackgroundColor(backgroundColor: string): void {
		this.backgroundColor = backgroundColor;
		this.updateStyles();
	}

	setCheckColor(checkColor: string): void {
		this.checkColor = checkColor;
		this.updateStyles();
	}

	setBorderColor(borderColor: string): void {
		this.borderColor = borderColor;
		this.updateStyles();
	}

	setFieldMessages(fieldMessageConfigs: UiFieldMessageConfig[]): void {
		super.setFieldMessages(fieldMessageConfigs);
		this.updateStyles();
	}

	private updateStyles() {
		const highestMessageSeverity = getHighestSeverity(this.getFieldMessages());
		if (highestMessageSeverity > UiFieldMessageSeverity.INFO) {
			this.$style.textContent = ''; // styles are defined by message severity styles
		} else {
			this.$style.textContent = `[data-teamapps-id=${this._config.id}] > .checkbox-check {
			background-color: ${(this.backgroundColor ?? '')};
			color: ${(this.checkColor ?? '')};
			border: 1px solid ${(this.borderColor ?? '')};  
		}`;
		}
	}

	protected displayCommittedValue(): void {
		let v = this.getCommittedValue();
		this.$check.textContent = v ? "\ue013" : "";
	}

	@executeWhenFirstDisplayed()
	focus(): void {
		this.$check.focus();
	}

	getTransientValue(): boolean {
		return this.getCommittedValue();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this, () => this.$check);
		if (editingMode === UiFieldEditingMode.DISABLED || editingMode === UiFieldEditingMode.READONLY) {
			this.$main.classList.add("disabled");
		} else {
			this.$main.classList.remove("disabled");
		}
	}

	public getReadOnlyHtml(value: boolean, availableWidth: number): string {
		return `<div class="UiCheckBox">
                    <div class="checkbox-check">${value ? '\ue013' : '\u200b'}</div>
                    <div class="checkbox-label">${this._config.caption /*TODO make caption a changeable instance field*/ || ""}</div>
                </div>`;
	}

	getDefaultValue() {
		return false;
	}

	public valuesChanged(v1: boolean, v2: boolean): boolean {
		return v1 !== v2;
	}

}

TeamAppsUiComponentRegistry.registerFieldClass("UiCheckBox", UiCheckBox);
