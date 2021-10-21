/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

public class ResourceWrapper implements Resource {

	private final Resource delegate;

	public ResourceWrapper(Resource delegate) {
		this.delegate = delegate;
	}

	@Override
	public InputStream getInputStream() {
		return delegate.getInputStream();
	}

	@Override
	public long getLength() {
		return delegate.getLength();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Date getLastModified() {
		return delegate.getLastModified();
	}

	@Override
	public Date getExpires() {
		return delegate.getExpires();
	}

	@Override
	public String getMimeType() {
		return delegate.getMimeType();
	}

	@Override
	public boolean isAttachment() {
		return delegate.isAttachment();
	}

	@Override
	public File getAsFile() {
		return delegate.getAsFile();
	}

	@Override
	public Resource lastModified(Date date) {
		return delegate.lastModified(date);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ResourceWrapper that = (ResourceWrapper) o;
		return Objects.equals(delegate, that.delegate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(delegate);
	}
}
