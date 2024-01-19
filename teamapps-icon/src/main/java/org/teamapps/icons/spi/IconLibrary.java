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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines an IconLibrary. Icon classes SHOULD be annotated with this annotation.
 * <p>
 * All of the classes specified in this annotation MUST provide a default constructor!
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface IconLibrary {

	/**
	 * @return The name of this icon library. Should consist only of characters, digits and underscores. No "." allowed!
	 */
	String name();

	/**
	 * @return The {@link IconEncoder} class that handles these icons.
	 */
	Class<? extends IconEncoder> encoder();

	/**
	 * @return The {@link IconDecoder} class that handles these icons.
	 */
	Class<? extends IconDecoder> decoder();

	/**
	 * @return The {@link IconLoader} class that handles these icons.
	 */
	Class<? extends IconLoader> loader();

	/**
	 * @return A supplier class supplying the default style for this icon library.
	 */
	Class<? extends DefaultStyleSupplier> defaultStyleSupplier();

}
