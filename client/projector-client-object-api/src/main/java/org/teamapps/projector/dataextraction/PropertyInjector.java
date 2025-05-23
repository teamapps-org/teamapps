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

import java.util.Map;

/**
 * An interface for injecting property values into a record object.
 * <p>
 * This interface is used for many purposes. Here are some examples:
 * <ul>
 *     <li>tables: injecting column values to records after editing</li>
 *     <li>forms: injecting column values to records after editing</li>
 * </ul>
 * <p>
 * Implementations of this interface should provide a way to set property values on a specific type of record.
 * The {@link BeanPropertyInjector} class provides an implementation for JavaBean objects.
 *
 * @param <RECORD> the type of the record object
 */
public interface PropertyInjector<RECORD> {

	/**
	 * Sets a single property value on a record object.
	 *
	 * @param record the record object to set the property value on
	 * @param propertyName the name of the property to set
	 * @param value the value to set
	 */
	void setValue(RECORD record, String propertyName, Object value);

	/**
	 * Sets multiple property values on a record object.
	 * <p>
	 * This default implementation uses the {@link #setValue(Object, String, Object)} method to set each property value.
	 *
	 * @param record the record object to set the property values on
	 * @param values a map of property names to property values
	 */
	default void setValues(RECORD record, Map<String, Object> values) {
		values.forEach((propertyName, value) -> {
			setValue(record, propertyName, value);
		});
	}

}
