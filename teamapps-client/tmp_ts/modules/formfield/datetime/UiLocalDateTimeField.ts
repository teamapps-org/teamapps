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
import {TeamAppsUiContext} from "teamapps-client-core";
import {TeamAppsUiComponentRegistry} from "teamapps-client-core";
import {DtoAbstractDateTimeField} from "./DtoAbstractDateTimeField";
import {
	UiLocalDateTimeFieldCommandHandler,
	DtoLocalDateTimeField,
	UiLocalDateTimeFieldEventSource
} from "../../../generated/DtoLocalDateTimeField";
import {arraysEqual} from "../../Common";
import {DateTime} from "luxon";

type LocalDateTimeArray = [number, number, number, number, number, number, number];

export class UiLocalDateTimeField extends DtoAbstractDateTimeField<DtoLocalDateTimeField, LocalDateTimeArray> implements UiLocalDateTimeFieldEventSource, UiLocalDateTimeFieldCommandHandler {

	protected initialize(config: DtoLocalDateTimeField) {
		super.initialize(config);
		this.getMainInnerDomElement().classList.add("UiDateTimeField");
	}

	protected getTimeZone(): string {
		return "UTC";
	}

	isValidData(v: LocalDateTimeArray): boolean {
		return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number" && typeof v[2] === "number" && typeof v[3] === "number" && typeof v[4] === "number");
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialDateTimeField.setValue(UiLocalDateTimeField.toDateTime(uiValue));
		} else {
			this.trivialDateTimeField.setValue(null);
		}
	}

	private static toDateTime(uiValue: LocalDateTimeArray): DateTime {
		return DateTime.fromObject({
			year: uiValue[0],
			month: uiValue[1],
			day: uiValue[2],
			hour: uiValue[3],
			minute: uiValue[4],
			second: uiValue[5] || 0,
			millisecond: uiValue[6] || 0,
			zone: "UTC"
		});
	}

	public getTransientValue(): LocalDateTimeArray {
		let value = this.trivialDateTimeField.getValue();
		if (value) {
			return [value.year || 0, value.month || 0, value.day || 0, value.hour || 0, value.minute || 0, 0, 0];
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: LocalDateTimeArray, availableWidth: number): string {
		if (value != null) {
			return `<div class="static-readonly-UiDateTimeField">`
				+ this.dateRenderer(UiLocalDateTimeField.toDateTime(value))
				+ this.timeRenderer(UiLocalDateTimeField.toDateTime(value))
				+ '</div>';
		} else {
			return "";
		}
	}

	getDefaultValue(): LocalDateTimeArray {
		return null;
	}

	public valuesChanged(v1: LocalDateTimeArray, v2: LocalDateTimeArray): boolean {
		return !arraysEqual(v1, v2);
	}

}


