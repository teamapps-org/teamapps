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
package org.teamapps.data.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @deprecated will be removed soon
 */
@Deprecated
public class SimpleDataRecord implements MutableDataRecord {

	private Map<String, Object> values = new HashMap<>();

	public SimpleDataRecord() {
	}

	public SimpleDataRecord(String key, Object value) {
		setValue(key, value);
	}

	public SimpleDataRecord(Map<String, Object> values) {
		this.values = values;
	}

	public SimpleDataRecord(DataRecord record) {
		if (record != null) {
			this.values.putAll(record.getPropertyNames().stream()
					.collect(Collectors.toMap(key -> key, record::getValue)));
		}
	}

	public SimpleDataRecord setValue(String key, Object value) {
		values.put(key, value);
		return this;
	}

	// TODO #pojo @mb setValueIfPresent() ?

	@Override
	public Object getValue(String key) {
		return values.get(key);
	}

	@Override
	public List<String> getPropertyNames() {
		return new ArrayList<>(values.keySet());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		values.keySet().stream().forEach(key -> {
			sb.append(values.get(key).toString()).append("; ");
		});
		return sb.toString();
	}
}
