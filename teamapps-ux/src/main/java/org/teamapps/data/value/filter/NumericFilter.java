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

public class NumericFilter extends AbstractFilter  {

	private final double value;
	private final Comparator comparator;

	public enum Comparator {
		EQUAL, UNEQUAL, SMALLER, SMALLER_OR_EQUAL, GREATER, GREATER_OR_EQUAL
	}

	public NumericFilter(String property, double value, Comparator comparator) {
		super(property);
		this.value = value;
		this.comparator = comparator;
	}

	@Override
	public FilterType getType() {
		return FilterType.NUMERIC;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		Number recordValue = (Number) record.getValue(getProperty());
		double matchValue = 0;
		if (recordValue != null) {
			matchValue = recordValue.doubleValue();
		}
		if (!treatNullAsDefaultValue && recordValue == null) {
			return false;
		}
		switch (comparator) {
			case EQUAL:
				return matchValue == value;
			case UNEQUAL:
				return matchValue != value;
			case SMALLER:
				return matchValue < value;
			case SMALLER_OR_EQUAL:
				return matchValue <= value;
			case GREATER:
				return matchValue > value;
			case GREATER_OR_EQUAL:
				return matchValue >= value;
		}
		return false;
	}

	@Override
	public String explain(int filterLevel) {
		return createLevelIndentString(filterLevel) + "NUMERIC: " + getProperty() + " " + comparator.name() + " " + String.format("%1.2f" , value) + "\n";
	}


	public Double getValue() {
		return value;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public int getValueAsInt() {
		return new Double(value).intValue();
	}

	public long getValueAsLong() {
		return new Double(value).longValue();
	}
}
