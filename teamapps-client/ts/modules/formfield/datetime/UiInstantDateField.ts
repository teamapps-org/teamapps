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
import * as Mustache from "mustache";
import * as moment from "moment-timezone";
import {UiInstantDateFieldConfig, UiInstantDateFieldCommandHandler, UiInstantDateFieldEventSource} from "../../../generated/UiInstantDateFieldConfig";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {AbstractUiDateField, DateComboBoxEntry} from "./AbstractUiDateField";
import Moment = moment.Moment;
import {convertJavaDateTimeFormatToMomentDateTimeFormat} from "../../Common";

export class UiInstantDateField extends AbstractUiDateField<UiInstantDateFieldConfig, number> implements UiInstantDateFieldEventSource, UiInstantDateFieldCommandHandler {

	private timeZoneId: string;

	protected initialize(config: UiInstantDateFieldConfig, context: TeamAppsUiContext) {
		super.initialize(config, context);
		this.getMainInnerDomElement().addClass("UiInstantDateField");
		this.timeZoneId = config.timeZoneId;
	}

	isValidData(v: number): boolean {
		return v == null || typeof v === "number";
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			let newMoment: Moment = moment.tz(uiValue, this.getTimeZoneId());
			this.trivialComboBox.setSelectedEntry(UiInstantDateField.createDateComboBoxEntryFromMoment(newMoment, this.getDateFormat()), true);
		} else {
			this.trivialComboBox.setSelectedEntry(null, true);
		}
	}

	private getTimeZoneId() {
		return this.timeZoneId || this._context.config.timeZoneId;
	}

	public getTransientValue(): number {
		let selectedEntry: DateComboBoxEntry = this.trivialComboBox.getSelectedEntry();
		if (selectedEntry) {
			return moment.tz({
				year: selectedEntry.year,
				month: selectedEntry.month - 1,
				day: selectedEntry.day
			}, this.getTimeZoneId()).valueOf();
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: number, availableWidth: number): string {
		if (value != null) {
			let mom = moment.tz(value, this.timeZoneId || this._context.config.timeZoneId);
			return `<div class="static-readonly-UiInstantDateField">` + Mustache.render(UiInstantDateField.comboBoxTemplate, UiInstantDateField.createDateComboBoxEntryFromMoment(mom, this._config.dateFormat || this._context.config.dateFormat)) + '</div>';
		} else {
			return "";
		}
	}

	setTimeZoneId(timeZoneId: string): void {
		this.timeZoneId = timeZoneId;
	}

	public valuesChanged(v1: number, v2: number): boolean {
		return v1 !== v2;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiInstantDateField", UiInstantDateField);
