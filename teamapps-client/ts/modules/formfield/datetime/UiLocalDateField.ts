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
import {
	UiLocalDateFieldCommandHandler,
	UiLocalDateFieldConfig,
	UiLocalDateFieldEventSource
} from "../../../generated/UiLocalDateFieldConfig";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {AbstractUiDateField} from "./AbstractUiDateField";
import {arraysEqual} from "../../Common";
import {LocalDateTime} from "../../util/LocalDateTime";

type LocalDate = [number, number, number];

export class UiLocalDateField extends AbstractUiDateField<UiLocalDateFieldConfig, LocalDate> implements UiLocalDateFieldEventSource, UiLocalDateFieldCommandHandler {

	protected initialize(config: UiLocalDateFieldConfig, context: TeamAppsUiContext) {
		super.initialize(config, context);
		this.getMainInnerDomElement().classList.add("UiLocalDateField");
	}

	isValidData(v: LocalDate): boolean {
		return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number" && typeof v[2] === "number");
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialComboBox.setSelectedEntry(UiLocalDateField.localDateToLocalDateTime(uiValue), true);
		} else {
			this.trivialComboBox.setSelectedEntry(null, true);
		}
	}

	public getTransientValue(): LocalDate {
		let selectedEntry = this.trivialComboBox.getSelectedEntry();
		if (selectedEntry) {
			return [selectedEntry.year, selectedEntry.month, selectedEntry.day];
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: LocalDate, availableWidth: number): string {
		if (value != null) {
			return this.dateRenderer(UiLocalDateField.localDateToLocalDateTime(value));
		} else {
			return "";
		}
	}

	public valuesChanged(v1: LocalDate, v2: LocalDate): boolean {
		return !arraysEqual(v1, v2);
	}

	private static localDateToLocalDateTime(uiValue: [number, number, number]) {
		return LocalDateTime.fromObject({
			year: uiValue[0],
			month: uiValue[1],
			day: uiValue[2]
		});
	}

}

TeamAppsUiComponentRegistry.registerFieldClass("UiLocalDateField", UiLocalDateField);
