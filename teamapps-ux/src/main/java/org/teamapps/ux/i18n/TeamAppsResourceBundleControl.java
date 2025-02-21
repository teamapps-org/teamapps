/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Handles UTF-8 resource bundles (properties files only).
 * Makes fallback locale explicitly configurable.
 */
public class TeamAppsResourceBundleControl extends ResourceBundle.Control {

	private final String resourceFileSuffix;
	private final Locale fallbackLocale;

	public TeamAppsResourceBundleControl(String resourceFileSuffix, Locale fallbackLocale) {
		this.resourceFileSuffix = resourceFileSuffix;
		this.fallbackLocale = fallbackLocale;
	}

	@Override
public ResourceBundle newBundle(
    String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
    throws IllegalAccessException, InstantiationException, IOException {

    // The below is a copy of the default implementation.
    String bundleName = toBundleName(baseName, locale);
    String resourceName = toResourceName(bundleName, "properties");
    ResourceBundle bundle = null;

    if (reload) {
        URL url = loader.getResource(resourceName);
        if (url != null) {
            URLConnection connection = url.openConnection();
            if (connection != null) {
                connection.setUseCaches(false);
                try (InputStream stream = connection.getInputStream();
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                    bundle = new PropertyResourceBundle(reader);
                }
            }
        }
    } else {
        try (InputStream stream = loader.getResourceAsStream(resourceName);
             InputStreamReader reader = stream != null ? new InputStreamReader(stream, StandardCharsets.UTF_8) : null) {
            if (reader != null) {
                bundle = new PropertyResourceBundle(reader);
            }
        }
    }
    return bundle;
}


	@Override
	public Locale getFallbackLocale(String baseName, Locale locale) {
		if (baseName == null) {
			throw new NullPointerException();
		}
		return locale.equals(fallbackLocale) ? null : fallbackLocale;
	}

	@Override
	public List<Locale> getCandidateLocales(String baseName, Locale locale) {
		List<Locale> list = super.getCandidateLocales(baseName, locale);
		list.add(fallbackLocale);
		return list;
	}
}
