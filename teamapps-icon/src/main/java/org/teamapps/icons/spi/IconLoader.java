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
package org.teamapps.icons.spi;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconLoaderContext;
import org.teamapps.icons.IconResource;

/**
 * Loads icons, providing them in binary form.
 *
 * @param <ICON>  The icon class this encoder can handle.
 */
public interface IconLoader<ICON extends Icon<ICON, ?>> {

	/**
	 * Loads an icon.
	 * <p>
	 * The specified icon MUST have a style set (unless the STYLE type is {@link Void}). Callers need to ensure this!
	 * <p>
	 * Implementations MAY therefore assume the icon's to be styled.
	 *
	 * @param size The size of the requested icon.
	 * @return The icon in binary form and type and size of the icon (as {@link IconResource}), or null, if the icon could not be loaded.
	 */
	IconResource loadIcon(ICON icon, int size, IconLoaderContext context);

}
