/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

public class IdValueFilter extends AbstractFilter {

	private final Object value;
	private final Comparator comparator;

	public enum Comparator {
		EQUAL, UNEQUAL, SMALLER, SMALLER_OR_EQUAL, GREATER, GREATER_OR_EQUAL
	}

	public IdValueFilter(String property, Object value, Comparator comparator) {
		super(property);
		this.value = value;
		this.comparator = comparator;
	}

	@Override
	public FilterType getType() {
		return FilterType.ID_VALUE;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		Number recordValue = (Number) record.getValue(getProperty());
		long matchValue = 0;
		if (recordValue != null) {
			matchValue = recordValue.longValue();
		}
		if (!treatNullAsDefaultValue && recordValue == null) {
			return false;
		}
		switch (comparator) {
			case EQUAL:
				return matchEqual(recordValue, treatNullAsDefaultValue);
			case UNEQUAL:
				return !matchEqual(recordValue, treatNullAsDefaultValue);
			case SMALLER:
				return matchValue < ((Number) value).longValue();
			case SMALLER_OR_EQUAL:
				return matchValue <= ((Number) value).longValue();
			case GREATER:
				return matchValue > ((Number) value).longValue();
			case GREATER_OR_EQUAL:
				return matchValue >= ((Number) value).longValue();
		}
		return false;
	}

	private boolean matchEqual(Object recordValue, boolean treatNullAsDefaultValue) {
		if (recordValue == null) {
			if (treatNullAsDefaultValue) {
				if (value == null) { // || value.isDefaultValue()) { // TODO #pojo @mb
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return Objects.equals(recordValue, value);
	}

	@Override
	public String explain(int filterLevel) {
		return createLevelIndentString(filterLevel) + "ID-VALUE: " + getProperty() + " " + comparator.name() + " " + value + "\n";
	}

	public Object getValue() {
		return value;
	}

	public Comparator getComparator() {
		return comparator;
	}


}
