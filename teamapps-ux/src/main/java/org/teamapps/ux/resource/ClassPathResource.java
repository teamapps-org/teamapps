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
package org.teamapps.ux.resource;

import java.io.InputStream;
import java.util.Objects;

public class ClassPathResource implements Resource {

	private final String resourceName;
	private final String name;
	private final ClassLoader classLoader;
	private final String mimeType;
	private long length = -1;

	public ClassPathResource(String resourceName) {
		this(resourceName, null, null);
	}

	public ClassPathResource(String resourceName, String mimeType) {
		this(resourceName, null, mimeType);
	}

	public ClassPathResource(String resourceName, ClassLoader classLoader) {
		this(resourceName, classLoader, null);
	}

	public ClassPathResource(String resourceName, ClassLoader classLoader, String mimeType) {
		this.resourceName = resourceName;
		this.name = resourceName.contains("/") ? resourceName.substring(resourceName.lastIndexOf('/') + 1) : resourceName;
		this.classLoader = classLoader;
		this.mimeType = mimeType;
	}

	@Override
	public InputStream getInputStream() {
		InputStream is;
		if (classLoader != null) {
			is = classLoader.getResourceAsStream(resourceName);
		} else {
			is = getClass().getResourceAsStream(resourceName);
			if (is == null) {
				is = ClassLoader.getSystemResourceAsStream(resourceName);
			}
		}
		return is;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public long getLength() {
		if (length < 0) {
			length = Resource.super.getLength();
		}
		return length;
	}

	@Override
	public String toString() {
		return "ClassPathResource{" +
				"resourceName='" + resourceName + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ClassPathResource that = (ClassPathResource) o;
		return length == that.length && Objects.equals(resourceName, that.resourceName) && Objects.equals(name, that.name) && Objects.equals(classLoader, that.classLoader) && Objects.equals(mimeType, that.mimeType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(resourceName, name, classLoader, mimeType, length);
	}
}
