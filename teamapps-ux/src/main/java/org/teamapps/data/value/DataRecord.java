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
package org.teamapps.data.value;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @deprecated will get removed soon
 */
@Deprecated
public interface DataRecord {

	@JsonIgnore
	List<String> getPropertyNames();

	Object getValue(String propertyName);

	@JsonIgnore
	default boolean getBooleanValue(String key) {
		Object value = getValue(key);
		return value != null && (boolean) value;
	}

	@JsonIgnore
	default int getIntValue(String key) {
		Object value = getValue(key);
		return value != null ? ((Number) value).intValue() : 0;
	}

	@JsonIgnore
	default long getLongValue(String key) {
		Object value = getValue(key);
		return value != null ? ((Number) value).longValue() : 0;
	}

	@JsonIgnore
	default double getDoubleValue(String key) {
		Object value = getValue(key);
		return value != null ? ((Number) value).doubleValue() : 0;
	}

	@JsonIgnore
	default String getStringValue(String key) {
		return (String) getValue(key);
	}


	@JsonAnyGetter
	default Map<String, Object> getAllValuesAsMap() {
		return getPropertyNames().stream()
				.filter(pn -> getValue(pn) != null)
				.collect(Collectors.toMap(name -> name, this::getValue));
	}

}
