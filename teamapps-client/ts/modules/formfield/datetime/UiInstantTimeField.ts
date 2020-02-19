/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import Mustache from "mustache";
import moment from "moment-timezone";
import {UiInstantTimeFieldCommandHandler, UiInstantTimeFieldConfig, UiInstantTimeFieldEventSource} from "../../../generated/UiInstantTimeFieldConfig";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {AbstractUiTimeField} from "./AbstractUiTimeField";

export class UiInstantTimeField extends AbstractUiTimeField<UiInstantTimeFieldConfig, number> implements UiInstantTimeFieldEventSource, UiInstantTimeFieldCommandHandler {

	private timeZoneId: string;

	protected initialize(config: UiInstantTimeFieldConfig, context: TeamAppsUiContext) {
		super.initialize(config, context);
		this.timeZoneId = config.timeZoneId;
		this.getMainInnerDomElement().classList.add("UiInstantTimeField");
	}

	isValidData(v: number): boolean {
		return v == null || typeof v === "number";
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			let newMoment = moment.tz(uiValue, "UTC").tz(this.getTimeZoneId());
			this.trivialComboBox.setSelectedEntry(UiInstantTimeField.createTimeComboBoxEntry(newMoment.hour(), newMoment.minute(), this.getTimeFormat()), true);
		} else {
			this.trivialComboBox.setSelectedEntry(null, true);
		}
	}

	public getTransientValue(): number {
		let selectedEntry = this.trivialComboBox.getSelectedEntry();
		if (selectedEntry) {
			let selectedMoment = moment.tz({
				hour: selectedEntry.hour,
				minute: selectedEntry.minute
			}, this.getTimeZoneId());
			return selectedMoment.valueOf();
		} else {
			return null;
		}
	}

	private getTimeZoneId() {
		return this.timeZoneId || this._context.config.timeZoneId;
	}

	public getReadOnlyHtml(value: number, availableWidth: number): string {
		if (value != null && value != null) {
			let mom = moment.tz(value, "UTC").tz(this.getTimeZoneId());
			return `<div class="static-readonly-UiInstantTimeField">` + Mustache.render(UiInstantTimeField.comboBoxTemplate, UiInstantTimeField.createTimeComboBoxEntryFromMoment(mom, this._config.timeFormat || this._context.config.timeFormat)) + '</div>';
		} else {
			return "";
		}
	}

	setTimeZoneId(timeZoneId: string): void {
		this.timeZoneId = timeZoneId;
		this.displayCommittedValue();
	}

	public valuesChanged(v1: number, v2: number): boolean {
		return v1 !== v2;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiInstantTimeField", UiInstantTimeField);
