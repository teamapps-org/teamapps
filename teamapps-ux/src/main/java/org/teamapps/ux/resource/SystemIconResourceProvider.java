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
package org.teamapps.ux.resource;

import org.teamapps.icons.api.CustomIconStyle;
import org.teamapps.icons.provider.IconProvider;
import org.teamapps.icons.systemprovider.IconResource;
import org.teamapps.icons.systemprovider.SystemIconProvider;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

public class SystemIconResourceProvider implements ResourceProvider {

	private final Date lastModifiedDate = new Date();
	private final SystemIconProvider iconProvider;

	public SystemIconResourceProvider() {
		iconProvider = new SystemIconProvider();
	}

	public SystemIconResourceProvider(File cachingDirectory) {
		iconProvider = new SystemIconProvider(cachingDirectory);
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
		String[] parts = relativeResourcePath.split("/");
		int size = getInt(parts[0]);
		String iconId = parts[parts.length - 1];
		IconResource iconResource = iconProvider.getIcon(size, iconId);
		if (iconResource != null) {
			return new Resource() {
				@Override
				public InputStream getInputStream() {
					return iconResource.getInputStream();
				}

				@Override
				public long getLength() {
					return iconResource.getLength();
				}

				@Override
				public Date getLastModified() {
					return iconResource.getLastModified();
				}

				@Override
				public Date getExpires() {
					return iconResource.getExpires();
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
			return 0;
		}
		try {
			return Integer.parseInt(s);
		} catch (Throwable ignore) {
		}
		return 0;
	}

	public void registerStandardIconProvider(IconProvider iconProvider) {
		this.iconProvider.registerStandardIconProvider(iconProvider);
	}

	public void registerCustomIconProvider(IconProvider iconProvider) {
		this.iconProvider.registerCustomIconProvider(iconProvider);
	}

	public void registerCustomIconStyle(CustomIconStyle customIconStyle) {
		iconProvider.registerCustomIconStyle(customIconStyle);
	}

}
