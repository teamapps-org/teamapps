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
package org.teamapps.ux.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MultiResourceBundle extends ResourceBundle {

	private List<ResourceBundle> resourceBundles = new ArrayList<>();

	public MultiResourceBundle() {
		this(new ResourceBundle[0]);
	}

	public MultiResourceBundle(ResourceBundle... resourceBundles) {
		for (ResourceBundle resourceBundle : resourceBundles) {
			if (resourceBundle != null) {
				addResourceBundle(resourceBundle);
			}
		}
	}

	public void addResourceBundle(ResourceBundle resourceBundle) {
		resourceBundles.add(resourceBundle);
	}

	@Override
	protected Object handleGetObject(String key) {
		for (ResourceBundle resourceBundle : resourceBundles) {
			String value = getString(key, resourceBundle);
			if (value != null) {
				return value;
			}
		}
		return key;
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(resourceBundles.stream()
				.flatMap(bundle -> bundle.keySet().stream())
				.collect(Collectors.toSet()));
	}

	private String getString(String key, ResourceBundle resourceBundle) {
		if (resourceBundle == null) {
			return null;
		}
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException ignore) { }
		return null;
	}
}
