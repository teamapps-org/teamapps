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
import {UiDisplayFieldCommandHandler, UiDisplayFieldConfig, UiDisplayFieldEventSource, UiFieldEditingMode} from "../../generated";
import {AbstractUiField} from "./AbstractUiField";
import {TeamAppsUiContext} from "teamapps-client-core";
import {escapeHtml, parseHtml, removeTags} from "../../Common";
import {TeamAppsUiComponentRegistry} from "teamapps-client-core";


export class UiDisplayField extends AbstractUiField<UiDisplayFieldConfig, string> implements UiDisplayFieldEventSource, UiDisplayFieldCommandHandler {

	private _$field: HTMLElement;
	private showHtml: boolean;
	private removeStyleTags: boolean;

	protected initialize(config: UiDisplayFieldConfig, context: TeamAppsUiContext) {
		this._$field = parseHtml(`<div class="UiDisplayField">`);

		this.setShowBorder(config.showBorder);
		this.setShowHtml(config.showHtml);
		this.setRemoveStyleTags(config.removeStyleTags);
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	setShowBorder(showBorder: boolean): void {
		this._$field.classList.toggle("border", showBorder);
	}

	setShowHtml(showHtml: boolean): void {
		this.showHtml = showHtml;
		this._$field.classList.toggle("show-html", showHtml);
		this.displayCommittedValue();
	}

	setRemoveStyleTags(removeStyleTags: boolean): void {
		this.removeStyleTags = removeStyleTags;
		this.displayCommittedValue();
	}

	public getMainInnerDomElement(): HTMLElement {
		return this._$field;
	}
	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			if (this.showHtml) {
				const displayedValue = this.removeStyleTags ? removeTags(uiValue, "style") : uiValue;
				this._$field.innerHTML = displayedValue;
			} else {
				this._$field.textContent = uiValue || "";
			}
		} else {
			this._$field.innerHTML = '';
		}
	}

	focus(): void {
		// do nothing
	}

	getTransientValue(): string {
		return this.getCommittedValue();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		AbstractUiField.defaultOnEditingModeChangedImpl(this, () => null);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		let content: string;
		if (value != null && value) {
			if (this.showHtml) {
				content = value;
			} else {
				content = escapeHtml(value);
			}
		} else {
			content = "";
		}
		return `<div class="static-readonly-UiDisplayField">${content}</div>`;
	}

	getDefaultValue() {
		return "";
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return false;
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiDisplayField", UiDisplayField);
