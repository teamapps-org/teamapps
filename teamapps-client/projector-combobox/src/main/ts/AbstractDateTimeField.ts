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
import {TrivialDateTimeField} from "./trivial-components/TrivialDateTimeField";
import {DateTime} from "luxon";
import {DateSuggestionEngine} from "./DateSuggestionEngine";
import {createDateRenderer, createTimeRenderer} from "./datetime-rendering";
import {
	DtoAbstractDateTimeField, DtoAbstractDateTimeField_TextInputEvent,
	DtoAbstractDateTimeFieldCommandHandler,
	DtoAbstractDateTimeFieldEventSource,
	DtoComboBox_TextInputEvent
} from "./generated";
import {AbstractField, DebounceMode, DtoDateTimeFormatDescriptor, FieldEditingMode, TeamAppsEvent} from "projector-client-object-api";

export abstract class AbstractDateTimeField<C extends DtoAbstractDateTimeField, V> extends AbstractField<C, V> implements DtoAbstractDateTimeFieldEventSource, DtoAbstractDateTimeFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<DtoAbstractDateTimeField_TextInputEvent> = TeamAppsEvent.createDebounced(250, DebounceMode.BOTH);

	protected trivialDateTimeField: TrivialDateTimeField;
	protected dateSuggestionEngine: DateSuggestionEngine;
	protected dateRenderer: (time: DateTime) => string;
	protected timeRenderer: (time: DateTime) => string;

	protected initialize(config: DtoAbstractDateTimeField) {
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.timeRenderer = this.createTimeRenderer();

		this.trivialDateTimeField = new TrivialDateTimeField({
			timeZone: this.getTimeZone(),
			locale: config.locale,
			dateFormat: config.dateFormat,
			timeFormat: config.timeFormat,
			showTrigger: config.showDropDownButton,
			editingMode: config.editingMode === FieldEditingMode.READONLY ? 'readonly' : config.editingMode === FieldEditingMode.DISABLED ? 'disabled' : 'editable',
			favorPastDates: config.favorPastDates
		});
		this.trivialDateTimeField.getMainDomElement().classList.add("DtoAbstractDateTimeField");
		this.trivialDateTimeField.onChange.addListener(() => this.commit());

		this.trivialDateTimeField.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialDateTimeField.getMainDomElement().querySelectorAll<HTMLElement>(":scope .tr-date-editor, :scope .tr-time-editor").forEach(element => element.classList.add("field-background"));
		this.trivialDateTimeField.getMainDomElement().querySelector<HTMLElement>(":scope .tr-trigger").classList.add("field-border");
	}

	protected abstract getTimeZone(): string;

	protected createDateRenderer(): (time: DateTime) => string {
		return createDateRenderer(this.config.locale, this.config.dateFormat);
	}

	protected createTimeRenderer(): (time: DateTime) => string {
		return createTimeRenderer(this.config.locale, this.config.timeFormat);
	}

	protected dateTimeToDateString(dateTime: DateTime): string {
		return dateTime.setLocale(this.config.locale).toLocaleString(this.config.dateFormat);
	}

	protected dateTimeToTimeString(dateTime: DateTime): string {
		return dateTime.setLocale(this.config.locale).toLocaleString(this.config.timeFormat);
	}

	private updateDateSuggestionEngine() {
		this.dateSuggestionEngine = new DateSuggestionEngine({
			locale: this.config.locale,
			favorPastDates: this.config.favorPastDates
		});
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialDateTimeField.getMainDomElement() as HTMLElement;
	}

	protected initFocusHandling() {
		this.trivialDateTimeField.onFocus.addListener(() => this.onFocus.fire({}));
		this.trivialDateTimeField.onBlur.addListener(() => this.onBlur.fire({}));
	}

	focus(): void {
		this.trivialDateTimeField.focus();
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.keys(FieldEditingMode));
		this.getMainElement().classList.add(editingMode);
		if (editingMode === FieldEditingMode.READONLY) {
			this.trivialDateTimeField.setEditingMode("readonly");
		} else if (editingMode === FieldEditingMode.DISABLED) {
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

	setLocaleAndFormats(locale: string, dateFormat: DtoDateTimeFormatDescriptor, timeFormat: DtoDateTimeFormatDescriptor): void {
		this.config.locale = locale;
		this.config.dateFormat = dateFormat;
		this.config.timeFormat = timeFormat;
		this.updateDateSuggestionEngine();
		this.dateRenderer = this.createDateRenderer();
		this.trivialDateTimeField.setLocaleAndFormats(locale, dateFormat, timeFormat);
	}

	setFavorPastDates(favorPastDates: boolean): void {
		// TODO
		console.warn("TODO: implement DtoAbstractDateTimeField.setFavorPastDates()")
	}

	setShowDropDownButton(showDropDownButton: boolean): void {
		// TODO
		console.warn("TODO: implement DtoAbstractDateTimeField.setShowDropDownButton()")
	}

}
