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
package org.teamapps.icons.spi;

import org.teamapps.icons.Icon;
import org.teamapps.icons.IconEncoderContext;

/**
 * Responsible for creating {@link String} representations for icons. These strings will get parsed by the corresponding
 * {@link IconDecoder}.
 * <p>
 * A String representation may be an arbitrary String, as long as it is an allowed String for URL path segments.
 * It should be safe to use <code>a-z A-Z 0-9 . - _ ~ ! $ ' ( ) * + , ; = : @</code>.
 * If the String contains parenthesis ("(" or ")"), it must make sure to close any opening one of them.
 * <p>
 * Implementations MUST be able to encode unstyled icons, i.e. icons that have no style set (null).
 * <p>
 * Implementations MUST provide a default constructor!
 *
 * @param <ICON>  The icon class this encoder can handle.
 */
public interface IconEncoder<ICON extends Icon<ICON, ?>> {

	/**
	 * Creates a string representation of the provided icon.
	 * <p>
	 * The string representation may be an arbitrary string, as long as it is an allowed string for URL path segments.
	 * It should be safe to use <code>a-z A-Z 0-9 . - _ ~ ! $ ' ( ) * + , ; = : @</code>.
	 * If the String contains parenthesis ("(" or ")"), it must make sure to close any opening one of them.
	 * <p>
	 * Note that this method MUST support encoding unstyled icons, i.e. icons that have no style set (null).
	 *
	 * @param icon    The icon to encode.
	 * @param context
	 * @return The encoded icon
	 */
	String encodeIcon(ICON icon, IconEncoderContext context);

}
