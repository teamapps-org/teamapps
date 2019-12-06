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
import {TrivialDateTimeField} from "trivial-components";
import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {UiField} from "../UiField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {AbstractUiDateTimeFieldConfig, AbstractUiDateTimeFieldCommandHandler, AbstractUiDateTimeFieldEventSource} from "../../../generated/AbstractUiDateTimeFieldConfig";
import {convertJavaDateTimeFormatToMomentDateTimeFormat, parseHtml} from "../../Common";

export abstract class AbstractUiDateTimeField<C extends AbstractUiDateTimeFieldConfig, V> extends UiField<C, V> implements AbstractUiDateTimeFieldEventSource, AbstractUiDateTimeFieldCommandHandler {

	private $originalInput: HTMLElement;
	protected trivialDateTimeField: TrivialDateTimeField;
	private dateFormat: string;
	private timeFormat: string;

	protected initialize(config: AbstractUiDateTimeFieldConfig, context: TeamAppsUiContext) {
		this.$originalInput = parseHtml('<input type="text" autocomplete="off">');

		this.dateFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(config.dateFormat);
		this.timeFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(config.timeFormat);

		this.trivialDateTimeField = new TrivialDateTimeField(this.$originalInput, {
			dateFormat: this.getDateFormat(),
			timeFormat: this.getTimeFormat(),
			showTrigger: config.showDropDownButton,
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			favorPastDates: config.favorPastDates
		});
		this.trivialDateTimeField.getMainDomElement().classList.add("AbstractUiDateTimeField");
		this.trivialDateTimeField.onChange.addListener(() => this.commit());

		this.trivialDateTimeField.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialDateTimeField.getMainDomElement().querySelector<HTMLElement>(":scope .tr-date-editor, .tr-time-editor").classList.add("field-background");
		this.trivialDateTimeField.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
		this.trivialDateTimeField.getMainDomElement().querySelector<HTMLElement>(":scope .tr-date-editor, .tr-time-editor")
			.addEventListener("focus blur", e => this.getMainElement().classList.toggle("focus", e.type === "focus"));
	}

	protected getDateFormat() {
		return this.dateFormat || this._context.config.dateFormat;
	}

	protected getTimeFormat() {
		return this.timeFormat || this._context.config.timeFormat;
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialDateTimeField.getMainDomElement() as HTMLElement;
	}

	public getFocusableElement(): HTMLElement {
		return this.trivialDateTimeField.getMainDomElement().querySelector<HTMLElement>(':scope .tr-editor');
	}

	focus(): void {
		this.trivialDateTimeField.focus();
	}

	public hasFocus(): boolean {
		return this.getMainInnerDomElement().matches('.focus');
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainElement().classList.add(UiField.editingModeCssClasses[editingMode]);
		if (editingMode === UiFieldEditingMode.READONLY) {
			// this.trivialDateTimeField.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
			// this.trivialDateTimeField.setEditingMode("disabled");
		} else {
			// this.trivialDateTimeField.setEditingMode("editable");
		}
	}

	destroy(): void {
		super.destroy();
		this.trivialDateTimeField.destroy();
		this.$originalInput.remove();
	}

	getDefaultValue(): V {
		return null;
	}

	setDateFormat(dateFormat: string): void {
		// TODO
		this.logger.warn("TODO: implement AbstractUiDateTimeField.setDateFormat()")
	}

	setFavorPastDates(favorPastDates: boolean): void {
		// TODO
		this.logger.warn("TODO: implement AbstractUiDateTimeField.setFavorPastDates()")
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		// TODO
		this.logger.warn("TODO: implement AbstractUiDateTimeField.setShowDropDownButton()")
	}

	setTimeFormat(timeFormat: string): void {
		// TODO
		this.logger.warn("TODO: implement AbstractUiDateTimeField.setTimeFormat()")
	}

}
