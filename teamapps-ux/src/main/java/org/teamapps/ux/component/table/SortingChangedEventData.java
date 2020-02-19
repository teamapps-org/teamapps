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
package org.teamapps.ux.component.table;

import org.teamapps.data.value.SortDirection;

public class SortingChangedEventData {

	private final String sortField;
	private final SortDirection sortDirection;

	public SortingChangedEventData(String sortField, SortDirection sortDirection) {
		this.sortField = sortField;
		this.sortDirection = sortDirection;
	}

	public String getSortField() {
		return sortField;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}
}
