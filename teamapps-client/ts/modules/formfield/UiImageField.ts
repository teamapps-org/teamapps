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
import {UiImageFieldConfig, UiImageFieldCommandHandler, UiImageFieldEventSource} from "../../generated/UiImageFieldConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiBorderConfig} from "../../generated/UiBorderConfig";
import {UiShadowConfig} from "../../generated/UiShadowConfig";
import {UiImageSizing} from "../../generated/UiImageSizing";
import {createImageSizingCssObject, createUiBorderCssObject, createUiShadowCssObject, cssObjectToString} from "../util/CssFormatUtil";

export class UiImageField extends UiField<UiImageFieldConfig, string> implements UiImageFieldEventSource, UiImageFieldCommandHandler {

	private _$field: JQuery;

	protected initialize(config: UiImageFieldConfig, context: TeamAppsUiContext) {
		this._$field = $(`<div class="UiImageField">`);

		this.setSize(config.width, config.height);
		this.setBorder(config.border);
		this.setShadow(config.shadow);
		this.setImageSizing(config.imageSizing);

		this._$field.click((e) => {
			this.commit(true);
		});
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	setSize(width: number, height: number): void {
		this._$field.css({
			width: `${width}px`,
			height: `${height}px`,
		})
	}

	setBorder(border: UiBorderConfig): void {
		this._$field.css(createUiBorderCssObject(border));
	}

	setShadow(shadow: UiShadowConfig): void {
		this._$field.css(createUiShadowCssObject(shadow));
	}

	setImageSizing(imageSizing: UiImageSizing): void {
		this._$field.css(createImageSizingCssObject(imageSizing));
	}

	getDefaultValue(): string {
		return null;
	}

	public getMainInnerDomElement(): JQuery {
		return this._$field;
	}

	public getFocusableElement(): JQuery {
		return null;
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		this._$field.css("background-image", uiValue ? `url(${uiValue})` : 'none');
	}

	focus(): void {
		// do nothing
	}

	getTransientValue(): string {
		return this.getCommittedValue();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this);
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		let css = cssObjectToString({
			width: `${this._config.width}px`,
			height: `${this._config.height}px`,
			...createUiBorderCssObject(this._config.border),
			...createUiShadowCssObject(this._config.shadow),
			...createImageSizingCssObject(this._config.imageSizing),
			'background-image': value ? `url(${value})` : 'none'
		});
		return `<div class="static-readonly-UiImageField" style="${css}"></div>`;
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiImageField", UiImageField);
