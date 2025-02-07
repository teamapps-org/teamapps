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
package org.teamapps.projector.icon;

import org.teamapps.projector.icon.spi.IconLibrary;

import java.util.HashMap;
import java.util.Map;

/**
 * TeamApps session-specific icon provider. Allows for custom (session-specific) default styles per icon library.
 */
public class SessionIconProvider {

	private final IconProvider iconProvider;

	private final Map<Class<? extends Icon>, IconStyle<?>> defaultStyleByIconClass = new HashMap<>();

	public SessionIconProvider(IconProvider iconProvider) {
		this.iconProvider = iconProvider;
	}

	public String encodeIcon(Icon icon) {
		return encodeIcon(icon, false);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public String encodeIcon(Icon icon, boolean fallbackToDefaultStyle) {
		IconLibrary library = iconProvider.getIconLibrary(icon.getClass());

		if (icon.getStyle() == null && fallbackToDefaultStyle) {
			IconStyle defaultStyle = defaultStyleByIconClass.computeIfAbsent(icon.getClass(), c -> library.getDefaultStyle());
			icon = defaultStyle.apply(icon);
		}

		return library.getName() + "." + library.encodeIcon(icon, i -> encodeIcon(i, fallbackToDefaultStyle));
	}

	public Icon decodeIcon(String qualifiedEncodedIconString) {
		return iconProvider.decodeIcon(qualifiedEncodedIconString);
	}

	public IconResource loadIcon(String qualifiedEncodedIconString, int size) {
		return iconProvider.loadIcon(qualifiedEncodedIconString, size);
	}

	public IconResource loadIcon(Icon icon, int size) {
		return iconProvider.loadIcon(icon, size);
	}

	public void registerIconLibrary(Class<? extends Icon> iconClass) {
		iconProvider.registerIconLibrary(iconClass);
	}

	public <I extends Icon> void registerIconLibrary(IconLibrary<I> iconLibrary) {
		iconProvider.registerIconLibrary(iconLibrary);
	}

	public <I extends Icon> void setDefaultStyleForIconClass(Class<I> iconClass, IconStyle<I> defaultStyle) {
		iconProvider.registerIconLibrary(iconClass);
		defaultStyleByIconClass.put(iconClass, defaultStyle);
	}

}
