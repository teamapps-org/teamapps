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
package org.teamapps.localize;


import org.teamapps.ux.i18n.TeamAppsResourceBundleControl;

import java.util.*;
import java.util.function.Function;

public interface ResourceBundleInfo {

	static ResourceBundleInfo create(String baseName, Locale... translations) {
		return create(baseName, "properties", translations);
	}

	static ResourceBundleInfo create(String baseName, String resourceFileSuffix, Locale... translations) {
		Function<Locale, ResourceBundle> resourceBundleByLocaleFunction = locale -> ResourceBundle.getBundle(baseName, locale, new TeamAppsResourceBundleControl(resourceFileSuffix, Locale.getDefault()));
		return create(resourceBundleByLocaleFunction, translations);
	}

	static ResourceBundleInfo create(Function<Locale, ResourceBundle> resourceBundleByLocaleFunction, Locale... translations) {
		return new ResourceBundleInfo() {
			@Override
			public Function<Locale, ResourceBundle> getResourceBundleByLocaleFunction() {
				return resourceBundleByLocaleFunction;
			}

			@Override
			public List<Locale> getTranslations() {
				return Arrays.asList(translations);
			}
		};
	}

	Function<Locale, ResourceBundle> getResourceBundleByLocaleFunction();

	List<Locale> getTranslations();
}
