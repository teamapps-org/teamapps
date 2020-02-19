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

public class NumericRangeFilter extends AbstractFilter  {

	private final Double start;
	private final Double end;
	private final Comparator comparator;

	public enum Comparator {
		BETWEEN, BETWEEN_INCLUSIVE, OUTSIDE, OUTSIDE_INCLUSIVE
	}

	public NumericRangeFilter(String property, Double start, Double end, Comparator comparator) {
		super(property);
		this.start = start;
		this.end = end;
		this.comparator = comparator;
	}

	@Override
	public FilterType getType() {
		return FilterType.NUMERIC_RANGE;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		Number recordValue = (Number) record.getValue(getProperty());
		double value = 0;
		if (recordValue != null) {
			value = recordValue.doubleValue();
		}
		if (!treatNullAsDefaultValue && recordValue == null) {
			return false;
		}
		switch (comparator) {
			case BETWEEN:
				return value > start && value < end;
			case BETWEEN_INCLUSIVE:
				break;
			case OUTSIDE:
				break;
			case OUTSIDE_INCLUSIVE:
				break;
		}
		return false;
	}

	@Override
	public String explain(int filterLevel) {
		return createLevelIndentString(filterLevel) + "NUMERIC-RANGE: " + getProperty() + " " + comparator.name() + " " + String.format("%1.2f" , start) + " AND " + String.format("%1.2f" , end) + "\n";
	}


	public Double getStart() {
		return start;
	}

	public Double getEnd() {
		return end;
	}

	public Comparator getComparator() {
		return comparator;
	}

}
