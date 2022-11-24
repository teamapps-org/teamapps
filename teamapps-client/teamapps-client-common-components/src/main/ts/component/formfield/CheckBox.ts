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
	DtoCheckBox,
	DtoCheckBoxCommandHandler,
	DtoCheckBoxEventSource,
	DtoFieldEditingMode,
	DtoFieldMessage,
	DtoFieldMessageSeverity
} from "../../generated";
import {AbstractField, getHighestSeverity} from "./AbstractField";
import {generateUUID, parseHtml, TeamAppsUiContext} from "teamapps-client-core";


export class CheckBox extends AbstractField<DtoCheckBox, boolean> implements DtoCheckBoxEventSource, DtoCheckBoxCommandHandler {

	private $main: HTMLElement;
	private $check: HTMLElement;
	private $label: HTMLElement;
	private $style: HTMLElement;

	private backgroundColor: string;
	private checkColor: string;
	private borderColor: string;

	protected initialize(config: DtoCheckBox, context: TeamAppsUiContext) {
		const uuid = "cb-" + generateUUID();
		this.$main = parseHtml(`<div class="DtoCheckBox">
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
			if (this.getEditingMode() === DtoFieldEditingMode.DISABLED || this.getEditingMode() === DtoFieldEditingMode.READONLY) {
				return;
			}
			this.toggleCommittedValue();
		});
		this.$check.addEventListener("keydown", (e) => {
			if (e.key === ' ') {
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
		if (!this.config.htmlEnabled) {
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

	setFieldMessages(fieldMessageConfigs: DtoFieldMessage[]): void {
		super.setFieldMessages(fieldMessageConfigs);
		this.updateStyles();
	}

	private updateStyles() {
		const highestMessageSeverity = getHighestSeverity(this.getFieldMessages());
		if (highestMessageSeverity > DtoFieldMessageSeverity.INFO) {
			this.$style.textContent = ''; // styles are defined by message severity styles
		} else {
			this.$style.textContent = `[data-teamapps-id=${this.config.id}] > .checkbox-check {
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

	focus(): void {
		this.$check.focus();
	}

	getTransientValue(): boolean {
		return this.getCommittedValue();
	}

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode): void {
		if (editingMode === DtoFieldEditingMode.DISABLED || editingMode === DtoFieldEditingMode.READONLY) {
			this.$main.classList.add("disabled");
			this.$check.removeAttribute("tabIndex");
		} else {
			this.$main.classList.remove("disabled");
			this.$check.tabIndex = 0;
		}
	}

	public getReadOnlyHtml(value: boolean, availableWidth: number): string {
		return `<div class="DtoCheckBox">
                    <div class="checkbox-check">${value ? '\ue013' : '\u200b'}</div>
                    <div class="checkbox-label">${this.config.caption /*TODO make caption a changeable instance field*/ || ""}</div>
                </div>`;
	}

	getDefaultValue() {
		return false;
	}

	public valuesChanged(v1: boolean, v2: boolean): boolean {
		return v1 !== v2;
	}

}


