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
package org.teamapps.projector.i18n;

import java.util.*;

public class ResourceBundleTranslationProvider implements TranslationProvider {

	private final String baseName;
	private final ResourceBundle.Control resourceBundleControl;
	private final Map<Locale, ResourceBundle> resourceBundleByLocale = new HashMap<>();

	public ResourceBundleTranslationProvider(String baseName) {
		this(baseName, "properties");
	}

	public ResourceBundleTranslationProvider(String baseName, String resourceFileSuffix) {
		this(baseName, resourceFileSuffix, Locale.getDefault());
	}

	public ResourceBundleTranslationProvider(String baseName, Locale fallbackLocale) {
		this(baseName, "properties", fallbackLocale);
	}

	public ResourceBundleTranslationProvider(String baseName, String resourceFileSuffix, Locale fallbackLocale) {
		this(baseName, new ProjectorResourceBundleControl(resourceFileSuffix, fallbackLocale));
	}

	public ResourceBundleTranslationProvider(String baseName, ResourceBundle.Control control) {
		this.baseName = baseName;
		this.resourceBundleControl = control;
	}

	@Override
	public String getRawTranslationString(String key, Locale locale) {
		ResourceBundle propertyResourceBundle = getResourceBundle(locale);
			return propertyResourceBundle.getString(key);
	}

	@Override
	public List<String> getKeys(Locale locale) {
		return Collections.list(getResourceBundle(locale).getKeys());
	}

	private ResourceBundle getResourceBundle(Locale locale) {
		return resourceBundleByLocale.computeIfAbsent(locale,
				language -> ResourceBundle.getBundle(this.baseName, language, resourceBundleControl));
	}
}
