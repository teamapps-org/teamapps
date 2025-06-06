/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.data.extract;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface PropertyExtractor<RECORD> extends PropertyProvider<RECORD> {

	Object getValue(RECORD record, String propertyName);

	default Map<String, Object> getValues(RECORD record, Collection<String> propertyNames) {
		return propertyNames.stream()
				.collect(
						HashMap::new,
						(map, propertyName) -> map.put(propertyName, getValue(record, propertyName)), // nullable values!
						HashMap::putAll
				);
	}

}
