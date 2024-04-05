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
import {LocalDateTime} from "./LocalDateTime";
import {createTimeRenderer} from "./datetime-rendering";
import {DtoLocalTimeField, DtoLocalTimeFieldCommandHandler, DtoLocalTimeFieldEventSource} from "./generated";
import {arraysEqual} from "teamapps-client-core-components";
import {AbstractTimeField} from "./AbstractTimeField";

type LocalTime = [number, number, number, number];

export class LocalTimeField extends AbstractTimeField<DtoLocalTimeField, LocalTime> implements DtoLocalTimeFieldEventSource, DtoLocalTimeFieldCommandHandler {

	protected initialize(config: DtoLocalTimeField) {
		super.initialize(config);
		this.getMainInnerDomElement().classList.add("UiLocalTimeField");
	}

	isValidData(v: LocalTime): boolean {
		return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number");
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialComboBox.setValue(LocalTimeField.localTimeToLocalDateTime(uiValue));
		} else {
			this.trivialComboBox.setValue(null);
		}
	}

	public getTransientValue(): LocalTime {
		let selectedEntry = this.trivialComboBox.getValue();
		if (selectedEntry) {
			return [selectedEntry.hour, selectedEntry.minute, 0, 0];
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: LocalTime, availableWidth: number): string {
		if (value != null) {
			return this.timeRenderer(LocalTimeField.localTimeToLocalDateTime(value));
		} else {
			return "";
		}
	}

	public valuesChanged(v1: LocalTime, v2: LocalTime): boolean {
		return !arraysEqual(v1, v2);
	}

	private static localTimeToLocalDateTime(uiValue: [number, number, number, number]) {
		return LocalDateTime.fromObject({hour: uiValue[0], minute: uiValue[1], second: uiValue[2], millisecond: uiValue[3]});
	}

	protected localDateTimeToString(entry: LocalDateTime): string {
		return entry.setLocale(this.config.locale).toLocaleString(this.config.timeFormat);
	}

	protected createTimeRenderer(): (time: LocalDateTime) => string {
		let timeRenderer = createTimeRenderer(this.config.locale, this.config.timeFormat);
		return entry => timeRenderer(entry?.toUTC());
	}
}


