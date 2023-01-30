/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {TrivialCalendarBox} from "../TrivialCalendarBox";
import {NavigationDirection} from "../TrivialCore";
import {LocalDateTime} from "../../datetime/LocalDateTime";
import {DropDownComponent, SelectionDirection} from "./DropDownComponent";

export class CalendarBoxDropdown implements DropDownComponent<LocalDateTime> {

	public readonly onValueChanged: TeamAppsEvent<{ value: LocalDateTime; finalSelection: boolean }> = new TeamAppsEvent();


	constructor(
		private calendarBox: TrivialCalendarBox,
		private queryFunction: (query: string) => Promise<LocalDateTime | null> | LocalDateTime | null,
		public defaultDate: LocalDateTime
	) {
		this.calendarBox.onChange.addListener(event => {
			this.onValueChanged.fire({value: event.value, finalSelection: event.timeUnitEdited == "day"});
		});
	}

	getMainDomElement(): HTMLElement {
		return this.calendarBox.getMainDomElement();
	}

	setValue(value: LocalDateTime): void {
		this.calendarBox.setSelectedDate(value ?? this.defaultDate ?? LocalDateTime.local());
	}

	getValue(): LocalDateTime {
		return this.calendarBox.getSelectedDate();
	}

	handleKeyboardInput(event: KeyboardEvent): boolean {
		if (["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight"].indexOf(event.code) !== -1) {
			this.calendarBox.navigateTimeUnit("day", event.code.substr(5).toLowerCase() as NavigationDirection);
			this.onValueChanged.fire({value: this.calendarBox.getSelectedDate(), finalSelection: false});
			return true;
		}
	}

	async handleQuery(query: string, selectionDirection: SelectionDirection, currentComboBoxValue: LocalDateTime): Promise<boolean> {
		if (!query && currentComboBoxValue != null) {
			this.calendarBox.setSelectedDate(currentComboBoxValue);
		} else {
			let suggestedDate = await this.queryFunction(query);
			if (suggestedDate != null) {
				this.calendarBox.setSelectedDate(suggestedDate);
				return true;
			} else {
				return false;
			}
		}
	}

	destroy(): void {
		this.calendarBox.destroy();
	}

	getComponent(): TrivialCalendarBox {
		return this.calendarBox;
	}

}
