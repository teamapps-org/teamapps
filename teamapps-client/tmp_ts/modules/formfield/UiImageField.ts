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
import {UiField} from "./UiField";
import {UiImageFieldCommandHandler, DtoImageField, UiImageFieldEventSource} from "../../generated/DtoImageField";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {TeamAppsUiContext} from "teamapps-client-core";

import {DtoBorder} from "../../generated/DtoBorder";
import {DtoShadow} from "../../generated/DtoShadow";
import {UiImageSizing} from "../../generated/UiImageSizing";
import {createImageSizingCssObject, createUiBorderCssObject, createUiShadowCssObject, cssObjectToString} from "../util/CssFormatUtil";
import {parseHtml} from "../Common";

export class UiImageField extends UiField<DtoImageField, string> implements UiImageFieldEventSource, UiImageFieldCommandHandler {

	private _$field: HTMLElement;

	protected initialize(config: DtoImageField, context: TeamAppsUiContext) {
		this._$field = parseHtml(`<div class="UiImageField">`);

		this.update(config);

		this._$field.addEventListener('click',(e) => {
			this.commit(true);
		});
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	setSize(width: string, height: string): void {
		Object.assign(this._$field.style, {
			width: `${width}`,
			height: `${height}`,
		});
	}

	setBorder(border: DtoBorder): void {
		Object.assign(this._$field.style, createUiBorderCssObject(border));
	}

	setShadow(shadow: DtoShadow): void {
		Object.assign(this._$field.style, createUiShadowCssObject(shadow));
	}

	setImageSizing(imageSizing: UiImageSizing): void {
		Object.assign(this._$field.style, createImageSizingCssObject(imageSizing));
	}

	update(config: DtoImageField) {
		this._config = config;
		this.setSize(config.width, config.height);
		this.setBorder(config.border);
		this.setShadow(config.shadow);
		this.setImageSizing(config.imageSizing);
		this._$field.style.backgroundColor = config.backgroundColor;
	}

	getDefaultValue(): string {
		return null;
	}

	public getMainInnerDomElement(): HTMLElement {
		return this._$field;
	}
	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		this._$field.style.backgroundImage = uiValue ? `url('${uiValue}')` : 'none';
	}

	focus(): void {
		// do nothing
	}

	getTransientValue(): string {
		return this.getCommittedValue();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this, () => null);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		let css = cssObjectToString({
			width: `${this._config.width}px`,
			height: `${this._config.height}px`,
			...createUiBorderCssObject(this._config.border),
			...createUiShadowCssObject(this._config.shadow),
			...createImageSizingCssObject(this._config.imageSizing),
			'background-image': value ? `url('${value}')` : 'none'
		});
		return `<div class="static-readonly-UiImageField" style="${css}"></div>`;
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}
}


