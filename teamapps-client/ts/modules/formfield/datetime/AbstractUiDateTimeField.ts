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
import {TrivialDateTimeField} from "../../trivial-components/TrivialDateTimeField";
import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {UiField} from "../UiField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {
	AbstractUiDateTimeFieldCommandHandler,
	AbstractUiDateTimeFieldConfig,
	AbstractUiDateTimeFieldEventSource
} from "../../../generated/AbstractUiDateTimeFieldConfig";
import {UiDateTimeFormatDescriptorConfig} from "../../../generated/UiDateTimeFormatDescriptorConfig";
import {DateTime} from "luxon";
import {DateSuggestionEngine} from "./DateSuggestionEngine";
import {createDateRenderer, createTimeRenderer} from "./datetime-rendering";

export abstract class AbstractUiDateTimeField<C extends AbstractUiDateTimeFieldConfig, V> extends UiField<C, V> implements AbstractUiDateTimeFieldEventSource, AbstractUiDateTimeFieldCommandHandler {

	protected trivialDateTimeField: TrivialDateTimeField;
	protected dateSuggestionEngine: DateSuggestionEngine;
	protected dateRenderer: (time: DateTime) => string;
	protected timeRenderer: (time: DateTime) => string;

	protected initialize(config: AbstractUiDateTimeFieldConfig, context: TeamAppsUiContext) {
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.timeRenderer = this.createTimeRenderer();

		this.trivialDateTimeField = new TrivialDateTimeField({
			timeZone: this.getTimeZone(),
			locale: config.locale,
			dateFormat: config.dateFormat,
			timeFormat: config.timeFormat,
			showTrigger: config.showDropDownButton,
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			favorPastDates: config.favorPastDates
		});
		this.trivialDateTimeField.getMainDomElement().classList.add("AbstractUiDateTimeField");
		this.trivialDateTimeField.onChange.addListener(() => this.commit());

		this.trivialDateTimeField.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialDateTimeField.getMainDomElement().querySelectorAll<HTMLElement>(":scope .tr-date-editor, :scope .tr-time-editor").forEach(element => element.classList.add("field-background"));
		this.trivialDateTimeField.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
		this.trivialDateTimeField.getMainDomElement().querySelectorAll<HTMLElement>(":scope .tr-date-editor, :scope .tr-time-editor")
			.forEach(element => {
				element.addEventListener("focus", e => this.getMainElement().classList.add("focus"));
				element.addEventListener("blur", e => this.getMainElement().classList.remove("focus"));
			});
	}

	protected abstract getTimeZone(): string;

	protected createDateRenderer(): (time: DateTime) => string {
		return createDateRenderer(this._config.locale, this._config.dateFormat);
	}

	protected createTimeRenderer(): (time: DateTime) => string {
		return createTimeRenderer(this._config.locale, this._config.timeFormat);
	}

	protected dateTimeToDateString(dateTime: DateTime): string {
		return dateTime.setLocale(this._config.locale).toLocaleString(this._config.dateFormat);
	}

	protected dateTimeToTimeString(dateTime: DateTime): string {
		return dateTime.setLocale(this._config.locale).toLocaleString(this._config.timeFormat);
	}

	private updateDateSuggestionEngine() {
		this.dateSuggestionEngine = new DateSuggestionEngine({
			locale: this._config.locale,
			favorPastDates: this._config.favorPastDates
		});
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialDateTimeField.getMainDomElement() as HTMLElement;
	}

	public getFocusableElement(): HTMLElement {
		return null; // will handle all focus relevant stuff here...
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
			this.trivialDateTimeField.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
			this.trivialDateTimeField.setEditingMode("disabled");
		} else {
			this.trivialDateTimeField.setEditingMode("editable");
		}
	}

	destroy(): void {
		super.destroy();
		this.trivialDateTimeField.destroy();
	}

	getDefaultValue(): V {
		return null;
	}

	setLocaleAndFormats(locale: string, dateFormat: UiDateTimeFormatDescriptorConfig, timeFormat: UiDateTimeFormatDescriptorConfig): void {
		this._config.locale = locale;
		this._config.dateFormat = dateFormat;
		this._config.timeFormat = timeFormat;
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.trivialDateTimeField.setLocaleAndFormats(locale, dateFormat, timeFormat);
	}

	setFavorPastDates(favorPastDates: boolean): void {
		// TODO
		this.logger.warn("TODO: implement AbstractUiDateTimeField.setFavorPastDates()")
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		// TODO
		this.logger.warn("TODO: implement AbstractUiDateTimeField.setShowDropDownButton()")
	}

}
