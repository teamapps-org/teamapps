/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

public class ByteArrayResource implements Resource {

	private final byte[] data;
	private final String name;
	private final String mimeType;

	public ByteArrayResource(byte[] data, String name) {
		this(data, name, null);
	}

	public ByteArrayResource(byte[] data, String name, String mimeType) {
		this.data = data;
		this.name = name;
		this.mimeType = mimeType;
	}

	@Override
	public String getMimeType() {
		if (mimeType != null) {
			return mimeType;
		} else {
			return Resource.super.getMimeType();
		}
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(data);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getLength() {
		return data.length;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ByteArrayResource that = (ByteArrayResource) o;
		return Arrays.equals(data, that.data) // this is extremely fast (<100µs for 1GB), so no problem
				&& Objects.equals(name, that.name);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(name);
		result = 31 * result + Arrays.hashCode(data);
		return result;
	}
}
