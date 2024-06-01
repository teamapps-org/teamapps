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
import {DtoDisplayField, DtoDisplayFieldCommandHandler, DtoDisplayFieldEventSource} from "../../generated";
import {AbstractField} from "projector-client-object-api";
import {FieldEditingMode, parseHtml} from "projector-client-object-api";
import {escapeHtml, removeTags} from "../../Common";


export class DisplayField extends AbstractField<DtoDisplayField, string> implements DtoDisplayFieldEventSource, DtoDisplayFieldCommandHandler {

	private _$field: HTMLElement;
	private showHtml: boolean;
	private removeStyleTags: boolean;

	protected initialize(config: DtoDisplayField) {
		this._$field = parseHtml(`<div class="DisplayField">`);

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

	protected onEditingModeChanged(editingMode: FieldEditingMode): void {
		AbstractField.defaultOnEditingModeChangedImpl(this, () => null);
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
		return `<div class="static-readonly-DisplayField">${content}</div>`;
	}

	getDefaultValue() {
		return "";
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return false;
	}
}


