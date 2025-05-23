/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.dataextraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This interface extends {@link PropertyProvider}, providing an alternative way of implementing it that might
 * in some cases be more convenient (but potentially has worse performing).
 *
 * @param <RECORD> the type of the record object
 */
public interface PropertyExtractor<RECORD> extends PropertyProvider<RECORD> {

	/**
	 * Gets a single property value from a record object.
	 *
	 * @param record the record object to get the property value from
	 * @param propertyName the name of the property to get
	 * @return the property value, or null
	 */
	Object getValue(RECORD record, String propertyName);

	/**
	 * Gets multiple property values from a record object.
	 * <p>
	 * This default implementation uses the {@link #getValue(Object, String)} method to get each property value.
	 *
	 * @param record the record object to get the property values from
	 * @param propertyNames the names of the properties to get
	 * @return a map of property names to property values
	 */
	default Map<String, Object> getValues(RECORD record, Collection<String> propertyNames) {
		return propertyNames.stream()
				.collect(
						HashMap::new,
						(map, propertyName) -> map.put(propertyName, getValue(record, propertyName)), // nullable values!
						HashMap::putAll
				);
	}

}
