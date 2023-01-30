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
package org.teamapps.icons;

import org.teamapps.icons.spi.IconDecoder;
import org.teamapps.icons.spi.IconEncoder;
import org.teamapps.icons.spi.IconLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * TeamApps session-specific icon provider. Allows for custom (session-specific) default styles per icon library.
 */
public class SessionIconProvider {

	private final IconProvider iconProvider;

	private final Map<Class<? extends Icon>, Object> defaultStyleByIconClass = new HashMap<>();

	public SessionIconProvider(IconProvider iconProvider) {
		this.iconProvider = iconProvider;
	}

	public <I extends Icon<I, S>, S> String encodeIcon(I icon) {
		return encodeIcon(icon, false);
	}

	public <I extends Icon<I, S>, S> String encodeIcon(I icon, boolean fallbackToDefaultStyle) {
		IconEncoder encoder = iconProvider.getIconEncoder(icon.getClass());

		if (icon.getStyle() == null && fallbackToDefaultStyle) {
			S style =  (S) defaultStyleByIconClass.computeIfAbsent(icon.getClass(), iClass -> iconProvider.getDefaultStyle(iClass));
			icon = icon.withStyle(style);
		}

		return iconProvider.getLibraryName(icon) + "."
				+ encoder.encodeIcon(icon, i -> encodeIcon((Icon) i, fallbackToDefaultStyle));
	}

	public Icon<?, ?> decodeIcon(String qualifiedEncodedIconString) {
		return iconProvider.decodeIcon(qualifiedEncodedIconString);
	}

	public IconResource loadIcon(String qualifiedEncodedIconString, int size) {
		return iconProvider.loadIcon(qualifiedEncodedIconString, size);
	}

	public IconResource loadIcon(Icon<?, ?> icon, int size) {
		return iconProvider.loadIcon(icon, size);
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass) {
		iconProvider.registerIconLibrary(iconClass);
	}

	public <I extends Icon<I, S>, S> void registerIconLibrary(Class<I> iconClass, String libraryName, IconEncoder<I> iconEncoder, IconDecoder<I> iconDecoder, IconLoader<I> iconLoader, S defaultStyle) {
		iconProvider.registerIconLibrary(iconClass, libraryName, iconEncoder, iconDecoder, iconLoader, defaultStyle);
	}

	public <I extends Icon<I, S>, S> void setDefaultStyleForIconClass(Class<I> iconClass, S defaultStyle) {
		iconProvider.registerIconLibrary(iconClass);
		defaultStyleByIconClass.put(iconClass, defaultStyle);
	}

}
