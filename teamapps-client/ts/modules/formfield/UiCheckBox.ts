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
import {UiCheckBoxCommandHandler, UiCheckBoxConfig, UiCheckBoxEventSource} from "../../generated/UiCheckBoxConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {generateUUID} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiColorConfig} from "../../generated/UiColorConfig";
import {createUiColorCssString} from "../util/CssFormatUtil";
import {keyCodes} from "trivial-components";
import {UiFieldMessageConfig} from "../../generated/UiFieldMessageConfig";
import {getHighestSeverity} from "../micro-components/FieldMessagesPopper";
import {UiFieldMessageSeverity} from "../../generated/UiFieldMessageSeverity";


export class UiCheckBox extends UiField<UiCheckBoxConfig, boolean> implements UiCheckBoxEventSource, UiCheckBoxCommandHandler {

	private $main: JQuery;
	private $check: JQuery;
	private $label: JQuery;
	private $style: JQuery;

	private backgroundColor: UiColorConfig;
	private checkColor: UiColorConfig;
	private borderColor: UiColorConfig;

	protected initialize(config: UiCheckBoxConfig, context: TeamAppsUiContext) {
		const uuid = "cb-" + generateUUID();
		this.$main = $(`<div class="UiCheckBox" data-teamapps-id="${config.id}">
				<style></style>
                <div class="checkbox-check field-border field-border-glow field-background" tabindex="0"></div>
                <div class="checkbox-label"></div>
            </div>`);
		this.$check = this.$main.find(".checkbox-check");
		this.$label = this.$main.find(".checkbox-label");
		this.$style = this.$main.find("style");

		this.setCaption(config.caption);
		this.setBackgroundColor(config.backgroundColor);
		this.setCheckColor(config.checkColor);
		this.setBorderColor(config.borderColor);

		this.$main.mousedown(() => {
			setTimeout(() => this.focus());
		});
		this.$main.click(() => {
			if (this.getEditingMode() === UiFieldEditingMode.DISABLED || this.getEditingMode() === UiFieldEditingMode.READONLY) {
				return;
			}
			this.toggleCommittedValue();
		});
		this.$check.on("keydown", (e) => {
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

	public getMainInnerDomElement(): JQuery {
		return this.$main;
	}

	setCaption(caption: string): void {
		this.$label.text(caption);
	}

	setBackgroundColor(backgroundColor: UiColorConfig): void {
		this.backgroundColor = backgroundColor;
		this.updateStyles();
	}

	setCheckColor(checkColor: UiColorConfig): void {
		this.checkColor = checkColor;
		this.updateStyles();
	}

	setBorderColor(borderColor: UiColorConfig): void {
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
			this.$style.text(''); // styles are defined by message severity styles
		} else {
			this.$style.text(`[data-teamapps-id=${this._config.id}] > .checkbox-check {
			background-color: ${createUiColorCssString(this.backgroundColor)};
			color: ${createUiColorCssString(this.checkColor)};
			border: 1px solid ${createUiColorCssString(this.borderColor)};  
		}`);
		}
	}

	public getFocusableElement(): JQuery {
		return this.$check;
	}

	protected displayCommittedValue(): void {
		let v = this.getCommittedValue();
		this.$check.text(v ? "\ue013" : "");
	}

	focus(): void {
		this.$check.focus();
	}

	doDestroy(): void {
		this.$main.detach();
	}

	getTransientValue(): boolean {
		return this.getCommittedValue();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		if (editingMode === UiFieldEditingMode.DISABLED || editingMode === UiFieldEditingMode.READONLY) {
			this.$main.addClass("disabled");
			this.$check.attr("tabindex", "");
		} else {
			this.$main.removeClass("disabled");
			this.$check.attr("tabindex", "0");
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
