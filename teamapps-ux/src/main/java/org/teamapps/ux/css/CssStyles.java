/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.css;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CssStyles implements Map<String, String> {

	private final Map<String, String> properties = new HashMap<>();

	public CssStyles() {
	}

	public CssStyles(Map<String, String> properties) {
		this.properties.putAll(properties);
	}

	public CssStyles(String propertyName, String value) {
		this.properties.put(propertyName, value);
	}

	@Override
	public int size() {
		return properties.size();
	}

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

	@Override
	public String get(Object propertyName) {
		return properties.get(propertyName);
	}

	@Override
	public String put(String propertyName, String value) {
		return properties.put(kebabToCamelCase(propertyName), value);
	}

	@Override
	public String remove(Object propertyName) {
		return properties.remove(propertyName);
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		properties.putAll(m);
	}

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

	public static String kebabToCamelCase(String kebab) {
		if (kebab == null) {
			return null;
		}
		if (!kebab.contains("-")) {
			return kebab;
		}
		return StringUtils.uncapitalize(Arrays.stream(kebab.split("-"))
				.map(part -> StringUtils.capitalize(part))
				.collect(Collectors.joining()));
	}
}
