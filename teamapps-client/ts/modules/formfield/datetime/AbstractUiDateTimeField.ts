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
import {TrivialDateTimeField} from "trivial-components";
import * as moment from "moment-timezone";
import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {UiField} from "../UiField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {AbstractUiDateTimeFieldConfig, AbstractUiDateTimeFieldCommandHandler, AbstractUiDateTimeFieldEventSource} from "../../../generated/AbstractUiDateTimeFieldConfig";
import {convertJavaDateTimeFormatToMomentDateTimeFormat} from "../../Common";

export abstract class AbstractUiDateTimeField<C extends AbstractUiDateTimeFieldConfig, V> extends UiField<C, V> implements AbstractUiDateTimeFieldEventSource, AbstractUiDateTimeFieldCommandHandler {

	private $originalInput: JQuery;
	protected trivialDateTimeField: TrivialDateTimeField;
	private dateFormat: string;
	private timeFormat: string;

	protected initialize(config: AbstractUiDateTimeFieldConfig, context: TeamAppsUiContext) {
		this.$originalInput = $('<input type="text" autocomplete="off">');

		this.dateFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(config.dateFormat);
		this.timeFormat = convertJavaDateTimeFormatToMomentDateTimeFormat(config.timeFormat);

		this.trivialDateTimeField = new TrivialDateTimeField(this.$originalInput, {
			dateFormat: this.getDateFormat(),
			timeFormat: this.getTimeFormat(),
			showTrigger: config.showDropDownButton,
			editingMode: config.editingMode === UiFieldEditingMode.READONLY ? 'readonly' : config.editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable',
			favorPastDates: config.favorPastDates
		});
		$(this.trivialDateTimeField.getMainDomElement()).addClass("AbstractUiDateTimeField");
		this.trivialDateTimeField.onChange.addListener(() => this.commit());

		$(this.trivialDateTimeField.getMainDomElement()).addClass("field-border field-border-glow field-background");
		$(this.trivialDateTimeField.getMainDomElement()).find(".tr-date-editor, .tr-time-editor").addClass("field-background");
		$(this.trivialDateTimeField.getMainDomElement()).find(".tr-trigger").addClass("field-border");
		$(this.trivialDateTimeField.getMainDomElement()).find(".tr-date-editor, .tr-time-editor").on("focus blur", e => this.getMainDomElement().toggleClass("focus", e.type === "focus"));
	}

	protected getDateFormat() {
		return this.dateFormat || this._context.config.dateFormat;
	}

	protected getTimeFormat() {
		return this.timeFormat || this._context.config.timeFormat;
	}

	public getMainInnerDomElement(): JQuery {
		return $(this.trivialDateTimeField.getMainDomElement());
	}

	public getFocusableElement(): JQuery {
		return $(this.trivialDateTimeField.getMainDomElement()).find('.tr-editor');
	}

	focus(): void {
		this.trivialDateTimeField.focus();
	}

	public hasFocus(): boolean {
		return this.getMainInnerDomElement().is('.focus');
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainDomElement()
			.removeClass(Object.values(UiField.editingModeCssClasses).join(" "))
			.addClass(UiField.editingModeCssClasses[editingMode]);
		if (editingMode === UiFieldEditingMode.READONLY) {
			// this.trivialDateTimeField.setEditingMode("readonly");
		} else if (editingMode === UiFieldEditingMode.DISABLED) {
			// this.trivialDateTimeField.setEditingMode("disabled");
		} else {
			// this.trivialDateTimeField.setEditingMode("editable");
		}
	}

	doDestroy(): void {
		this.trivialDateTimeField.destroy();
		this.$originalInput.detach();
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
