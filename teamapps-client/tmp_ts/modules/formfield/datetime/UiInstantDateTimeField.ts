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
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {AbstractUiDateTimeField} from "./AbstractUiDateTimeField";
import {
	UiInstantDateTimeFieldCommandHandler,
	UiInstantDateTimeFieldConfig,
	UiInstantDateTimeFieldEventSource
} from "../../../generated/UiInstantDateTimeFieldConfig";
import {DateTime} from "luxon";

export class UiInstantDateTimeField extends AbstractUiDateTimeField<UiInstantDateTimeFieldConfig, number> implements UiInstantDateTimeFieldEventSource, UiInstantDateTimeFieldCommandHandler {

	protected initialize(config: UiInstantDateTimeFieldConfig, context: TeamAppsUiContext) {
		super.initialize(config, context);
		this.getMainInnerDomElement().classList.add("UiDateTimeField");
	}

	protected getTimeZone(): string {
		return this._config.timeZoneId;
	}

	isValidData(v: number): boolean {
		return v == null || typeof v === "number";
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialDateTimeField.setValue(this.toDateTime(uiValue));
		} else {
			this.trivialDateTimeField.setValue(null);
		}
	}

	private toDateTime(uiValue: number) {
		return DateTime.fromMillis(uiValue).setZone(this._config.timeZoneId);
	}

	public getTransientValue(): number {
		let value = this.trivialDateTimeField.getValue();
		if (value) {
			return value.valueOf();
		} else {
			return null;
		}
	}

	public getReadOnlyHtml(value: number, availableWidth: number): string {
		if (value != null) {
			return `<div class="static-readonly-UiDateTimeField">`
				+ this.dateRenderer(this.toDateTime(value))
				+ this.timeRenderer(this.toDateTime(value))
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
		this._config.timeZoneId = timeZoneId;
		this.displayCommittedValue();
	}

}

TeamAppsUiComponentRegistry.registerFieldClass("UiInstantDateTimeField", UiInstantDateTimeField);
