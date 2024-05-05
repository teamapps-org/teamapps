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
import {DtoAbstractField} from "./DtoAbstractField";
import {DtoImageFieldCommandHandler, DtoImageField, DtoImageFieldEventSource} from "../../generated/DtoImageField";
import {DtoFieldEditingMode} from "../../generated/DtoFieldEditingMode";
import {TeamAppsUiContext} from "teamapps-client-core";

import {DtoBorder} from "../../generated/DtoBorder";
import {DtoBoxShadow} from "../../generated/DtoBoxShadow";
import {UiImageSizing} from "../../generated/UiImageSizing";
import {createImageSizingCssObject, createUiBorderCssObject, createUiShadowCssObject, cssObjectToString} from "../util/CssFormatUtil";
import {parseHtml} from "../Common";

export class UiImageField extends AbstractField<DtoImageField, string> implements DtoImageFieldEventSource, DtoImageFieldCommandHandler {

	private _$field: HTMLElement;

	protected initialize(config: DtoImageField) {
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

	setShadow(shadow: DtoBoxShadow): void {
		Object.assign(this._$field.style, createUiShadowCssObject(shadow));
	}

	setImageSizing(imageSizing: UiImageSizing): void {
		Object.assign(this._$field.style, createImageSizingCssObject(imageSizing));
	}

	update(config: DtoImageField) {
		this.config = config;
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

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode): void {
		DtoAbstractField.defaultOnEditingModeChangedImpl(this, () => null);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		let css = cssObjectToString({
			width: `${this.config.width}px`,
			height: `${this.config.height}px`,
			...createUiBorderCssObject(this.config.border),
			...createUiShadowCssObject(this.config.shadow),
			...createImageSizingCssObject(this.config.imageSizing),
			'background-image': value ? `url('${value}')` : 'none'
		});
		return `<div class="static-readonly-UiImageField" style="${css}"></div>`;
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}
}


