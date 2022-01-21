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
package org.teamapps.data.value;

import java.util.Objects;

public class Sorting {

	private final String fieldName;
	private final SortDirection sortDirection;

	public Sorting(String fieldName, SortDirection sortDirection) {
		Objects.requireNonNull(fieldName, "fieldName must not be null!");
		Objects.requireNonNull(fieldName, "sortDirection must not be null!");
		this.fieldName = fieldName;
		this.sortDirection = sortDirection;
	}

	public String getFieldName() {
		return fieldName;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public boolean isSorted() {
		if (sortDirection != null && fieldName !=null && !fieldName.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Sorting sorting1 = (Sorting) o;

		if (fieldName != null ? !fieldName.equals(sorting1.fieldName) : sorting1.fieldName != null) {
			return false;
		}
		return sortDirection == sorting1.sortDirection;
	}

	@Override
	public int hashCode() {
		int result = fieldName != null ? fieldName.hashCode() : 0;
		result = 31 * result + (sortDirection != null ? sortDirection.hashCode() : 0);
		return result;
	}
}
