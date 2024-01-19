/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import java.util.Objects;

class ClassAndPropertyName {
	final Class<?> clazz;
	final String propertyName;
	final boolean fallbackToFields;

	public ClassAndPropertyName(Class<?> clazz, String propertyName, boolean fallbackToFields) {
		this.clazz = clazz;
		this.propertyName = propertyName;
		this.fallbackToFields = fallbackToFields;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClassAndPropertyName that = (ClassAndPropertyName) o;
		return fallbackToFields == that.fallbackToFields && Objects.equals(clazz, that.clazz) && Objects.equals(propertyName, that.propertyName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clazz, propertyName, fallbackToFields);
	}
}
