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

import org.apache.commons.lang3.NotImplementedException;
import org.teamapps.data.value.DataRecord;

import java.util.Objects;

public class TextFilter extends AbstractFilter {

	private final String value;
	private final Comparator comparator;

	public enum Comparator {
		EQUAL, UNEQUAL, STARTS_WITH, STARTS_NOT_WITH, WILDCARD, UNEQUAL_WILDCARD, FUZZY, UNEQUAL_FUZZY
	}

	public TextFilter(String property, String value, Comparator comparator) {
		super(property);
		this.value = value;
		this.comparator = comparator;
	}

	@Override
	public FilterType getType() {
		return FilterType.TEXT;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		Object recordValue = record.getValue(getProperty());
		String matchValue = "";
		if (recordValue != null) {
			matchValue = recordValue.toString();
		}
		if (!treatNullAsDefaultValue && recordValue == null) {
			return false;
		}
		switch (comparator) {
			case EQUAL:
				return Objects.equals(matchValue, value);
			case UNEQUAL:
				return !Objects.equals(matchValue, value);
			case STARTS_WITH:
				return matchValue.startsWith(value);
			case STARTS_NOT_WITH:
				return !matchValue.startsWith(value);
			case WILDCARD:
				return matchValue.matches(createMatchingRegex(value));
			case UNEQUAL_WILDCARD:
				return !matchValue.matches(createMatchingRegex(value));
			case FUZZY:
				throw new NotImplementedException(Comparator.FUZZY.name());
				// int ratio = FuzzySearch.ratio(matchValue, value);
				// if (ratio > 80) {
				// 	return true;
				// } else {
				// 	return false;
				// }
			case UNEQUAL_FUZZY:
				throw new NotImplementedException(Comparator.UNEQUAL_FUZZY.name());
				// int ratio2 = FuzzySearch.ratio(matchValue, value);
				// if (ratio2 < 80) {
				// 	return true;
				// } else {
				// 	return false;
				// }
		}
		return false;
	}

	private static String createMatchingRegex(String value) {
		StringBuilder out = new StringBuilder("^");
		for(int i = 0; i < value.length(); ++i) {
			final char c = value.charAt(i);
			switch(c) {
				case '*': out.append(".*"); break;
				case '?': out.append('.'); break;
				case '.': out.append("\\."); break;
				case '\\': out.append("\\\\"); break;
				default: out.append(c);
			}
		}
		out.append('$');
		return out.toString();
	}

	@Override
	public String explain(int filterLevel) {
		return createLevelIndentString(filterLevel) + "TEXT: " + getProperty() + " " + comparator.name() + " " + value + "\n";
	}

	public String getValue() {
		return value;
	}

	public Comparator getComparator() {
		return comparator;
	}
}
