/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import {TrivialComponent} from "../TrivialCore";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";

export interface DropDownComponent<E> extends TrivialComponent {

	readonly onValueChanged: TeamAppsEvent<{ value: E, finalSelection: boolean }>;

	setValue(value: E): void;

	getValue(): E;

	/**
	 * @param event
	 * @return true if the event was processed by this component and should not lead to any other action
	 */
	handleKeyboardInput(event: KeyboardEvent): boolean;

	/**
	 * @param query
	 * @param selectionDirection
	 * @param referenceValue normally the current value of the comboBox
	 * @return true if it got results
	 */
	handleQuery(query: string, selectionDirection: SelectionDirection, referenceValue: E): Promise<boolean>;

	getComponent(): TrivialComponent;

}

export enum SelectionDirection {
	SELECT_FIRST = 1,
	NO_SELECTION = 0,
	SELECT_LAST = -1
}
