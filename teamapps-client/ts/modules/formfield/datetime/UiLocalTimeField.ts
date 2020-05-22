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
import {UiLocalTimeFieldConfig, UiLocalTimeFieldCommandHandler, UiLocalTimeFieldEventSource} from "../../../generated/UiLocalTimeFieldConfig";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {AbstractUiTimeField} from "./AbstractUiTimeField";
import {arraysEqual} from "../../Common";

type LocalTime = [number, number, number, number];

export class UiLocalTimeField extends AbstractUiTimeField<UiLocalTimeFieldConfig, LocalTime> implements UiLocalTimeFieldEventSource, UiLocalTimeFieldCommandHandler {

	protected initialize(config: UiLocalTimeFieldConfig, context: TeamAppsUiContext) {
		super.initialize(config, context);
		this.getMainInnerDomElement().classList.add("UiLocalTimeField");
	}

	isValidData(v: LocalTime): boolean {
		return v == null || (Array.isArray(v) && typeof v[0] === "number" && typeof v[1] === "number");
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialComboBox.setSelectedEntry(UiLocalTimeField.createTimeComboBoxEntry(uiValue[0], uiValue[1], this.getTimeFormat()), true);
		} else {
			this.trivialComboBox.setSelectedEntry(null, true);
		}
	}

	public getTransientValue(): LocalTime {
		let selectedEntry = this.trivialComboBox.getSelectedEntry();
		if (selectedEntry) {
			return [selectedEntry.hour, selectedEntry.minute, 0, 0];
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: LocalTime, availableWidth: number): string {
		if (value != null && value != null) {
			return `<div class="static-readonly-UiLocalTimeField">` + Mustache.render(UiLocalTimeField.comboBoxTemplate, UiLocalTimeField.createTimeComboBoxEntry(value[0], value[1], this._config.timeFormat || this._context.config.timeFormat)) + '</div>';
		} else {
			return "";
		}
	}

	public valuesChanged(v1: LocalTime, v2: LocalTime): boolean {
		return !arraysEqual(v1, v2);
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiLocalTimeField", UiLocalTimeField);
