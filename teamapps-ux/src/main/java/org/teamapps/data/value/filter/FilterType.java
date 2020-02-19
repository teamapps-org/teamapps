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

public enum FilterType {

	/*
	NumericFilter: type: equal, unequal, smaller, smallerOrEqual, greater, greaterOrEqual, interval, intervalInclusive; value, value2
	TextFilter: type: equal, unequal, startsWith, startsNotWith, wildcard, unequal wildcard, phrase, unequal phrase, String query
	BooleanFilter: true, false

	 */
	ALL_MATCHING,
	AND,
	OR,
	NUMERIC,
	NUMERIC_RANGE,
	TEXT,
	BOOLEAN,
	ID_VALUE,
	FULL_TEXT,

}
