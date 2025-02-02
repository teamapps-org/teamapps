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
package org.teamapps.projector.resourceprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.icons.IconResource;
import org.teamapps.icons.IconProvider;
import org.teamapps.projector.resource.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Date;

public class IconResourceProvider implements ResourceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static final Date lastModifiedDate = new Date();

	private final IconProvider iconResolver;

	public IconResourceProvider(IconProvider iconProviderDispatcher) {
		this.iconResolver = iconProviderDispatcher;
	}

	@Override
	public Resource getResource(String servletPath, String relativeResourcePath, String httpSessionId) {
		if (relativeResourcePath == null) {
			return null;
		}
		if (relativeResourcePath.startsWith("/")) {
			relativeResourcePath = relativeResourcePath.substring(1);
		}
		if (relativeResourcePath.endsWith("/")) {
			relativeResourcePath = relativeResourcePath.substring(0, relativeResourcePath.length() - 1);
		}
		String[] parts = relativeResourcePath.split("@");
		String qualifiedEncodedIcon = parts[0];
		int size = parts.length > 1 ? getInt(parts[parts.length - 1]) : -1;
		IconResource iconResource = iconResolver.loadIcon(qualifiedEncodedIcon, size);
		if (iconResource != null) {
			return new Resource() {
				@Override
				public InputStream getInputStream() {
					return new ByteArrayInputStream(iconResource.getBytes());
				}

				@Override
				public long getLength() {
					return iconResource.getLength();
				}

				@Override
				public Date getLastModified() {
					return lastModifiedDate;
				}

				@Override
				public Date getExpires() {
					return new Date(System.currentTimeMillis() + 86_400_000L * 60);
				}

				@Override
				public String getMimeType() {
					return iconResource.getMimeType();
				}
			};
		} else {
			return null;
		}
	}

	private int getInt(String s) {
		if (s == null) {
			return -1;
		}
		try {
			return Integer.parseInt(s);
		} catch (Throwable ignore) {
		}
		return -1;
	}

}
