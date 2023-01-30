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

/**
 * @param <ICON> Must be a self class reference.
 * @param <STYLE> The style class these icons support.
 */
public interface Icon<ICON extends Icon<ICON, STYLE>, STYLE> {

	/**
	 * Creates a copy of this icon with the specified style. The style may be null!
	 * @param style The style to apply. May be null!
	 * @return A copy of this icon with the specified style.
	 */
	ICON withStyle(STYLE style);

	/**
	 * @return The style of this icon. May be null!
	 */
	STYLE getStyle();

}
