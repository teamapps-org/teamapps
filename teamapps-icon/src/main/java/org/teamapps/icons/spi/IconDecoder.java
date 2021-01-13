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
package org.teamapps.icons.spi;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconDecoderContext;

/**
 * Decodes icons from an encoded icon {@link String}s such as they are produced by the {@link IconEncoder}.
 * <p>
 * Implementations MUST provide a default constructor!
 *
 * @param <ICON>  The icon class this provider will return.
 */
public interface IconDecoder<ICON extends Icon<ICON, ?>> {

	/**
	 * Decodes an icon from an encoded icon {@link String}.
	 * <p>
	 * Implementations MUST return an icon, even if the requested size is not available.
	 * In this case, a larger or smaller icon should be returned.
	 * The resizing will be done elsewhere.
	 * <p>
	 * Implementations MUST support unstyled icons.
	 *
	 * @param encodedIconString The encoded icon String as produced by the corresponding {@link IconEncoder}.
	 * @return The icon.
	 */
	ICON decodeIcon(String encodedIconString, IconDecoderContext context);

}
