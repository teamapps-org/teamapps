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
package org.teamapps.icons;

import org.teamapps.icons.cache.IconCache;
import org.teamapps.icons.cache.FileIconCache;
import org.teamapps.icons.spi.IconDecoder;
import org.teamapps.icons.spi.IconEncoder;
import org.teamapps.icons.spi.IconLoader;

import java.io.IOException;
import java.nio.file.Files;

public class IconProvider implements IconLoaderContext, IconDecoderContext {

	private final IconLibraryRegistry iconLibraryRegistry;
	private final IconCache iconCache;

	public IconProvider(IconLibraryRegistry iconLibraryRegistry) {
		this.iconLibraryRegistry = iconLibraryRegistry;
		try {
			this.iconCache = new FileIconCache(Files.createTempDirectory("teamapps-icon-cache").toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public IconProvider(IconLibraryRegistry iconLibraryRegistry, IconCache iconCache) {
		this.iconLibraryRegistry = iconLibraryRegistry;
		this.iconCache = iconCache;
	}

	/**
	 * Encodes the given icon. Does not provide any fallback style mechanism. If the icon's style is null, it is encoded without style.
	 * @return the encoded icon
	 */
	public String encodeIcon(Icon<?, ?> icon) {
		return getLibraryName(icon) + "." + getIconEncoder(icon.getClass()).encodeIcon(icon, this::encodeIcon);
	}

	@Override
	public Icon<?, ?> decodeIcon(String qualifiedEncodedIcon) {
		String libraryName = getLibraryName(qualifiedEncodedIcon);
		IconDecoder iconDecoder = iconLibraryRegistry.getIconDecoder(libraryName);
		String encodedIconString = qualifiedEncodedIcon.substring(libraryName.length() + 1);
		return iconDecoder.decodeIcon(encodedIconString, this);
	}

	public IconResource loadIcon(String qualifiedEncodedIcon, int size) {
		if (iconCache != null) {
			IconResource cachedIcon = iconCache.getIcon(qualifiedEncodedIcon, size);
			if (cachedIcon != null) {
				return cachedIcon;
			}
		}

		String libraryName = getLibraryName(qualifiedEncodedIcon);
		IconDecoder iconDecoder = iconLibraryRegistry.getIconDecoder(libraryName);
		String encodedIconString = qualifiedEncodedIcon.substring(libraryName.length() + 1);
		Icon<?, ?> icon = iconDecoder.decodeIcon(encodedIconString, this);

		IconResource iconResource = loadIconWithoutCaching(icon, size);
		if (iconResource == null) {
			return null;
		}

		if (iconCache != null) {
			iconCache.putIcon(qualifiedEncodedIcon, size, iconResource);
		}

		return iconResource;
	}

	@Override
	public IconResource loadIcon(Icon<?, ?> icon, int size) {
		return loadIconWithoutCaching(icon, size);
	}

	private IconResource loadIconWithoutCaching(Icon<?, ?> icon, int size) {
		String libraryName = getLibraryName(icon);
		IconLoader iconLoader = iconLibraryRegistry.getIconLoader(libraryName);
		IconResource iconResource = iconLoader.loadIcon(icon, size, this);
		if (iconResource == null) {
			return null;
		}

		if (iconResource.getIconType().isRasterImage() && iconResource.getSize() > 0 && size > 0 &&  iconResource.getSize() != size) {
			byte[] resizedIconBytes = new PngIconResizer().resizeIcon(iconResource.getBytes(), size);
			iconResource = new IconResource(resizedIconBytes, IconType.PNG, size);
		}
		return iconResource;
	}

	public <I extends Icon<I, S>, S> IconEncoder<I> getIconEncoder(Class<I> iconClass) {
		return iconLibraryRegistry.getIconEncoder(iconClass);
	}

	public <I extends Icon<I, S>, S> IconDecoder<I> getIconDecoder(String libraryName) {
		return iconLibraryRegistry.getIconDecoder(libraryName);
	}

	public <I extends Icon<I, S>, S> String getLibraryName(Icon<?, ?> icon) {
		return iconLibraryRegistry.getLibraryName(icon);
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass) {
		iconLibraryRegistry.registerIconLibrary(iconClass);
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass, String libraryName, IconEncoder<I> iconEncoder, IconDecoder<I> iconDecoder, IconLoader<I> iconLoader, S defaultStyle) {
		iconLibraryRegistry.registerIconLibrary(iconClass, libraryName, iconEncoder, iconDecoder, iconLoader, defaultStyle);
	}

	public <I extends Icon<I, S>, S> S getDefaultStyle(Class<I> iconClass) {
		return iconLibraryRegistry.getDefaultStyle(iconClass);
	}

	private String getLibraryName(String qualifiedEncodedIcon) {
		int firstDotIndex = qualifiedEncodedIcon.indexOf('.');
		return qualifiedEncodedIcon.substring(0, firstDotIndex);
	}
}
