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
package org.teamapps.ux.resource;

import java.io.InputStream;

public class ClassPathResource implements Resource {

	private final String resourceName;
	private final String name;
	private long length = -1;

	public ClassPathResource(String resourceName) {
		this.resourceName = resourceName;
		this.name = resourceName.contains("/") ? resourceName.substring(resourceName.lastIndexOf('/') + 1) : resourceName;
	}

	@Override
	public InputStream getInputStream() {
		return ClassLoader.getSystemResourceAsStream(resourceName);
	}

	@Override
	public String getName() {
		return name;
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
}
