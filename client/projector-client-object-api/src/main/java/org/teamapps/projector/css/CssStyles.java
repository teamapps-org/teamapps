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
package org.teamapps.projector.css;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A collection of CSS style properties with their corresponding values.
 * <p>
 * This class implements the Map interface where keys are CSS property names (e.g., "color", "font-size")
 * and values are the corresponding CSS values (e.g., "#FF0000", "12px").
 * It provides a convenient way to manipulate CSS styles programmatically.
 */
public class CssStyles implements Map<String, String> {

	private final Map<String, String> properties = new HashMap<>();

	public CssStyles() {
	}

	/**
	 * Creates a CSS styles collection with the provided properties.
	 *
	 * @param properties A map of CSS property names to their values
	 */
	public CssStyles(Map<String, String> properties) {
		this.properties.putAll(properties);
	}

	/**
	 * Creates a CSS styles collection with a single property.
	 *
	 * @param propertyName The CSS property name
	 * @param value The CSS property value
	 */
	public CssStyles(String propertyName, String value) {
		this.properties.put(propertyName, value);
	}

	@Override
	public int size() {
		return properties.size();
	}

	/**
	 * Checks if this collection contains no CSS properties.
	 *
	 * @return true if this collection contains no properties, false otherwise
	 */
	@Override
	public boolean isEmpty() {
		return properties.isEmpty();
	}

	@Override
	public boolean containsKey(Object propertyName) {
		return properties.containsKey(propertyName);
	}

	@Override
	public boolean containsValue(Object value) {
		return properties.containsValue(value);
	}

	/**
	 * Returns the value for the specified CSS property.
	 *
	 * @param propertyName The CSS property name
	 * @return The value for the property, or null if the property is not in this collection
	 */
	@Override
	public String get(Object propertyName) {
		return properties.get(propertyName);
	}

	/**
	 * Associates the specified value with the specified CSS property.
	 *
	 * @param propertyName The CSS property name
	 * @param value The CSS property value
	 * @return The previous value associated with the property, or null if there was no mapping
	 */
	@Override
	public String put(String propertyName, String value) {
		return properties.put(propertyName, value);
	}

	/**
	 * Removes the specified CSS property from this collection.
	 *
	 * @param propertyName The CSS property name to be removed
	 * @return The previous value associated with the property, or null if there was no mapping
	 */
	@Override
	public String remove(Object propertyName) {
		return properties.remove(propertyName);
	}

	/**
	 * Adds all the CSS properties from the specified map to this collection.
	 *
	 * @param m The map of CSS properties to be added
	 */
	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
        properties.putAll(m);
	}

	/**
	 * Removes all CSS properties from this collection.
	 */
	@Override
	public void clear() {
		properties.clear();
	}

	@Override
	public Set<String> keySet() {
		return properties.keySet();
	}

	@Override
	public Collection<String> values() {
		return properties.values();
	}

	@Override
	public Set<Entry<String, String>> entrySet() {
		return properties.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return properties.equals(o);
	}

	@Override
	public int hashCode() {
		return properties.hashCode();
	}

}
