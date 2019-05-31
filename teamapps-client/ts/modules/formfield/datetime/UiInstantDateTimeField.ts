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
import {UiInstantDateField} from "./UiInstantDateField";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {AbstractUiTimeField} from "./AbstractUiTimeField";
import {AbstractUiDateTimeField} from "./AbstractUiDateTimeField";
import {UiInstantDateTimeFieldConfig, UiInstantDateTimeFieldCommandHandler, UiInstantDateTimeFieldEventSource} from "../../../generated/UiInstantDateTimeFieldConfig";
import Moment = moment.Moment;
import {convertJavaDateTimeFormatToMomentDateTimeFormat} from "../../Common";

export class UiInstantDateTimeField extends AbstractUiDateTimeField<UiInstantDateTimeFieldConfig, number> implements UiInstantDateTimeFieldEventSource, UiInstantDateTimeFieldCommandHandler {

	private timeZoneId: string;

	protected initialize(config: UiInstantDateTimeFieldConfig, context: TeamAppsUiContext) {
		super.initialize(config, context);
		this.getMainInnerDomElement().classList.add("UiDateTimeField");
		this.timeZoneId = config.timeZoneId;
	}

	isValidData(v: number): boolean {
		return v == null || typeof v === "number";
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			let newMoment: Moment = moment.tz(uiValue, this.getTimeZoneId());
			this.trivialDateTimeField.setValue(newMoment as any);
		} else {
			this.trivialDateTimeField.setValue(null);
		}
	}

	public getTransientValue(): number {
		let value = this.trivialDateTimeField.getValue();
		if (value) {
			return moment.tz({
				year: value.year,
				month: value.month - 1,
				day: value.day,
				hour: value.hour,
				minute: value.minute
			}, this.getTimeZoneId()).valueOf();
		} else {
			return null;
		}
	}

	private getTimeZoneId() {
		return this.timeZoneId || this._context.config.timeZoneId;
	}

	public getReadOnlyHtml(value: number, availableWidth: number): string {
		if (value != null) {
			let mom = moment.tz(value, this.timeZoneId || this._context.config.timeZoneId);
			return `<div class="static-readonly-UiDateTimeField">`
				+ Mustache.render(UiInstantDateField.comboBoxTemplate, UiInstantDateField.createDateComboBoxEntryFromMoment(mom, this._config.dateFormat || this._context.config.dateFormat))
				+ Mustache.render(AbstractUiTimeField.comboBoxTemplate, AbstractUiTimeField.createTimeComboBoxEntryFromMoment(mom, this._config.timeFormat || this._context.config.timeFormat))
				+ '</div>';
		} else {
			return "";
		}
	}

	getDefaultValue(): number {
		return null;
	}

	public valuesChanged(v1: number, v2: number): boolean {
		return v1 !== v2;
	}

	setTimeZoneId(timeZoneId: string): void {
		this.timeZoneId = timeZoneId;
		// TODO update display!
		this.logger.warn("TODO: implement UiInstantDateTimeField.setTimeZoneId()");
	}

}

TeamAppsUiComponentRegistry.registerFieldClass("UiInstantDateTimeField", UiInstantDateTimeField);
