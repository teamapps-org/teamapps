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

import org.apache.commons.lang3.NotImplementedException;
import org.teamapps.data.value.DataRecord;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FullTextFilter implements Filter {

	private String value;

	public FullTextFilter(String value) {
		this.value = value;
	}

	@Override
	public FilterType getType() {
		return FilterType.FULL_TEXT;
	}

	@Override
	public String getProperty() {
		return null;
	}

	@Override
	public boolean matches(DataRecord record, boolean treatNullAsDefaultValue) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		List<String> keys = matchKeys(record.getPropertyNames());
		String compareValue = getValue().toLowerCase();
		boolean isExactSearch = isExactSearch(compareValue);
		boolean isFuzzySearch = isFuzzySearch(compareValue);
		boolean isWildcardSearch = isWildcardSearch(compareValue);
		String wildcardMatcher = createWildcardMatcher(compareValue);

		compareValue = cleanValue(compareValue);
		for (String key : keys) {
			Object value = record.getValue(key);
			if (value != null && value.toString() != null) {
				String stringValue = value.toString().toLowerCase();
				if (isFuzzySearch) {
					if (matchFuzzy(stringValue, compareValue)) {
						return true;
					}
				} else if (isExactSearch) {
					if (stringValue.equals(compareValue)) {
						return true;
					}
				} else if (isWildcardSearch) {
					if (stringValue.matches(wildcardMatcher)) {
						return true;
					}
				} else {
					if (stringValue.contains(compareValue)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String explain(int filterLevel) {
		return createLevelIndentString(filterLevel) + "FULL-TEXT: " + " " + value + "\n";
	}


	private boolean isExactSearch(String value) {
		if (value.startsWith("\"") || value.startsWith("'")) {
			return true;
		}
		return false;
	}

	private String cleanValue(String value) {
		if (value.endsWith("+")) {
			int len = 1;
			if (value.endsWith("++")) {
				len = 2;
			}
			value = value.substring(0, value.length() - len);
		}
		value = value.replace("\"", "");
		value = value.replace("'", "");
		return value;
	}

	private boolean isFuzzySearch(String value) {
		if (value.endsWith("+")) {
			return true;
		} else {
			return false;
		}
	}

	private boolean matchFuzzy(String val1, String val2) {
		// int ratio = FuzzySearch.ratio(val1, val2);
		// if (ratio > 50) {
		// 	return true;
		// } else {
		// 	return false;
		// }
		throw new NotImplementedException("matchFuzzy");
	}

	private boolean isWildcardSearch(String value) {
		if (value.contains("*") || value.contains("?")) {
			return true;
		} else {
			return false;
		}
	}

	private String createWildcardMatcher(String value) {
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
		out.append(".*$");
		return out.toString();
	}

	private String getFieldMatcher() {
		int pos = value.indexOf(':');
		if (pos <= 0) {
			return null;
		}
		return value.substring(0, pos).toLowerCase();
	}

	private String getValue() {
		int pos = value.indexOf(':');
		if (pos <= 0) {
			return value;
		}
		return value.substring(pos +1);
	}

	private List<String> matchKeys(List<String> keys) {
		int pos = value.indexOf(':');
		if (pos <= 0) {
			return keys;
		}
		String matcher = value.substring(0, pos).toLowerCase();
		return keys.stream().filter(key -> key.toLowerCase().contains(matcher)).collect(Collectors.toList());
	}

	public Filter parseSearch(List<String> fieldNames, Map<String, String> captionsByFieldName) { //missing field type
		if (value == null || value.isEmpty()) {
			return null;
		}
		// [field-caption-part][:][! | NOT]["|']text-part[?][*]text-part[+][+]["|'] [=,>,<,!=,] [value] [OR |][AND &] ...

		//Filter.textEqual("a").and(Filter.textStartsWith())


		return null;
	}

	private static class AndOrPart {
		private String query;
		private boolean isOr;
	}

	public static void main(String[] args) {
		String a = "test:gg";
		int pos = a.indexOf(':');
		System.out.println(a.substring(0, pos));
		System.out.println(a.substring(pos));
	}
}
