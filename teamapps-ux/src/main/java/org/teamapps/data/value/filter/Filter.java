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
import org.teamapps.data.value.Limit;
import org.teamapps.data.value.SortDirection;
import org.teamapps.data.value.Sorting;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface Filter {

	FilterType getType();

	String getProperty();

	default boolean matches(DataRecord record) {
		return matches(record, true);
	}

	boolean matches(DataRecord record, boolean treatNullAsDefaultValue);


	default String explain() {
		return explain(0);
	}

	String explain(int filterLevel);

	default String createLevelIndentString(int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("\t");
		}
		return sb.toString();
	}

	default Filter and(Filter filter) {
		if (filter == null) {
			return this;
		}
		return new AndFilter(this, filter);
	}

	default Filter or(Filter filter) {
		if (filter == null) {
			return this;
		}
		return new OrFilter(this, filter);
	}

	default Filter asOrFilter() {
		return new OrFilter(this);
	}

	static Filter allMatchingFilter() {
		return new AllMatchingFilter();
	}

	static Filter booleanFilter(String property, boolean value) {
		return new BooleanFilter(property, value);
	}

	static Filter numericEqual(String property, double value) {
		return new NumericFilter(property, value, NumericFilter.Comparator.EQUAL);
	}

	static Filter numericUnEqual(String property, double value) {
		return new NumericFilter(property, value, NumericFilter.Comparator.UNEQUAL);
	}

	static Filter numericSmaller(String property, double value) {
		return new NumericFilter(property, value, NumericFilter.Comparator.SMALLER);
	}

	static Filter numericSmallerOrEqual(String property, double value) {
		return new NumericFilter(property, value, NumericFilter.Comparator.SMALLER_OR_EQUAL);
	}

	static Filter numericGreater(String property, double value) {
		return new NumericFilter(property, value, NumericFilter.Comparator.GREATER);
	}

	static Filter numericGreaterOrEqual(String property, double value) {
		return new NumericFilter(property, value, NumericFilter.Comparator.GREATER_OR_EQUAL);
	}

	static Filter numericRangeBetween(String property, double start, double end) {
		return new NumericRangeFilter(property, start, end, NumericRangeFilter.Comparator.BETWEEN);
	}

	static Filter numericRangeBetweenInclusive(String property, double start, double end) {
		return new NumericRangeFilter(property, start, end, NumericRangeFilter.Comparator.BETWEEN_INCLUSIVE);
	}

	static Filter numericRangeOutside(String property, double start, double end) {
		return new NumericRangeFilter(property, start, end, NumericRangeFilter.Comparator.OUTSIDE);
	}

	static Filter numericRangeOutsideInclusive(String property, double start, double end) {
		return new NumericRangeFilter(property, start, end, NumericRangeFilter.Comparator.OUTSIDE_INCLUSIVE);
	}

	static Filter idValueEqual(String property, Object value) {
		return new IdValueFilter(property, value, IdValueFilter.Comparator.EQUAL);
	}

	static Filter idValueUnEqual(String property, Object value) {
		return new IdValueFilter(property, value, IdValueFilter.Comparator.UNEQUAL);
	}

	static Filter idValueSmaller(String property, Object value) {
		return new IdValueFilter(property, value, IdValueFilter.Comparator.SMALLER);
	}

	static Filter idValueSmallerOrEqual(String property, Object value) {
		return new IdValueFilter(property, value, IdValueFilter.Comparator.SMALLER_OR_EQUAL);
	}

	static Filter idValueGreater(String property, Object value) {
		return new IdValueFilter(property, value, IdValueFilter.Comparator.GREATER);
	}

	static Filter idValueGreaterOrEqual(String property, Object value) {
		return new IdValueFilter(property, value, IdValueFilter.Comparator.GREATER_OR_EQUAL);
	}

	static Filter textEqual(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.EQUAL);
	}

	static Filter textUnequal(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.UNEQUAL);
	}

	static Filter textStartsWith(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.STARTS_WITH);
	}

	static Filter textStartsNotWith(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.STARTS_NOT_WITH);
	}

	static Filter textWildcard(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.WILDCARD);
	}

	static Filter textUnequalWildcard(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.UNEQUAL_WILDCARD);
	}

	static Filter textFuzzy(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.FUZZY);
	}

	static Filter textUnequalFuzzy(String property, String value) {
		return new TextFilter(property, value, TextFilter.Comparator.UNEQUAL_FUZZY);
	}

	static Filter rawFullTextFilter(String value) {
		return new FullTextFilter(value);
	}

	static <T extends DataRecord> List<T> filterRecords(List<T> records, Filter filter) {
		return records.stream()
				.filter(record -> filter.matches(record))
				.collect(Collectors.toList());
	}

	static <T extends DataRecord> List<T> sortRecords(List<T> records, Sorting sorting) {
		if (sorting != null && sorting.getFieldName() != null) {
			String fieldName = sorting.getFieldName();
			Comparator<String> stringComparator = Comparator.nullsFirst(String::compareToIgnoreCase);
			List<T> sortedRecords = records.stream()
					.sorted((o1, o2) -> stringComparator.compare(extractString(o1, fieldName), extractString(o2, fieldName)))
					.collect(Collectors.toList());
			if (sorting.getSorting() == SortDirection.DESC) {
				Collections.reverse(sortedRecords);
			}
			return sortedRecords;
		} else {
			return records;
		}
	}

	static String extractString(DataRecord o, String fieldName) {
		if (o == null || o.getValue(fieldName) == null) {
			return null;
		} else {
			return o.getValue(fieldName).toString();
		}
	}


	static <T extends DataRecord> List<T> limitRecords(List<T> records, Limit limit) {
		if (limit != null) {
			return records.subList(limit.getStartIndex(), Math.min(records.size(), limit.getLength() + limit.getStartIndex()));
		} else {
			return records;
		}
	}

	static <T extends DataRecord> List<T> queryRecords(List<T> records, Filter query, Sorting sorting, Limit limit) {
		return limitRecords(sortRecords(filterRecords(records, query), sorting), limit);
	}

}
