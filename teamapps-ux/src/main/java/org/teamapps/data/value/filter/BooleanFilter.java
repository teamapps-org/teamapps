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
package org.teamapps.data.value.filter;

import org.teamapps.data.value.DataRecord;

import java.util.Objects;

public class BooleanFilter extends AbstractFilter {

	private final boolean value;

	public BooleanFilter(String property, boolean value) {
		super(property);
		this.value = value;
	}

	@Override
	public FilterType getType() {
		return FilterType.BOOLEAN;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		Object recordValue = record.getValue(getProperty());
		if (recordValue == null) {
			if (treatNullAsDefaultValue) {
				return !value;
			} else {
				return false;
			}
		}
		return Objects.equals(value, recordValue);
	}

	@Override
	public String explain(int filterLevel) {
		return createLevelIndentString(filterLevel) + "BOOLEAN: " + getProperty() + " = " + value + "\n";
	}


	public Boolean getValue() {
		return value;
	}
}
