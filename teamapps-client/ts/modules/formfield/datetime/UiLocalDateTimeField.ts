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
import {UiLocalDateTimeFieldConfig, UiLocalDateTimeFieldCommandHandler, UiLocalDateTimeFieldEventSource} from "../../../generated/UiLocalDateTimeFieldConfig";
import {arraysEqual} from "../../Common";
import Moment = moment.Moment;

type LocalDateTime = [number, number, number, number, number, number, number];

export class UiLocalDateTimeField extends AbstractUiDateTimeField<UiLocalDateTimeFieldConfig, LocalDateTime> implements UiLocalDateTimeFieldEventSource, UiLocalDateTimeFieldCommandHandler {

	protected initialize(config: UiLocalDateTimeFieldConfig, context: TeamAppsUiContext) {
		super.initialize(config, context);
		this.getMainInnerDomElement().classList.add("UiDateTimeField");
	}

	isValidData(v: LocalDateTime): boolean {
		return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number" && typeof v[2] === "number" && typeof v[3] === "number" && typeof v[4] === "number");
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			let newMoment: Moment = moment([uiValue[0], uiValue[1] - 1, uiValue[2], uiValue[3], uiValue[4], uiValue[5] || 0, uiValue[6] || 0]);
			this.trivialDateTimeField.setValue(newMoment as any);
		} else {
			this.trivialDateTimeField.setValue(null);
		}
	}

	public getTransientValue(): LocalDateTime {
		let value = this.trivialDateTimeField.getValue();
		if (value) {
			return [value.year, value.month, value.day, value.hour, value.minute, 0, 0];
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: LocalDateTime, availableWidth: number): string {
		if (value != null) {
			// console.log(value[0], value[1], value[2]);
			return `<div class="static-readonly-UiDateTimeField">`
				+ Mustache.render(UiInstantDateField.comboBoxTemplate, UiInstantDateField.createDateComboBoxEntryFromLocalValues(value[0], value[1], value[2], this._config.dateFormat || this._context.config.dateFormat))
				+ Mustache.render(AbstractUiTimeField.comboBoxTemplate, AbstractUiTimeField.createTimeComboBoxEntry(value[3], value[4], this._config.timeFormat || this._context.config.timeFormat))
				+ '</div>';
		} else {
			return "";
		}
	}

	getDefaultValue(): LocalDateTime {
		return null;
	}

	public valuesChanged(v1: LocalDateTime, v2: LocalDateTime): boolean {
		return !arraysEqual(v1, v2);
	}

}

TeamAppsUiComponentRegistry.registerFieldClass("UiLocalDateTimeField", UiLocalDateTimeField);
