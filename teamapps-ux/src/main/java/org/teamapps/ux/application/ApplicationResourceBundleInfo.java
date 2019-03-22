/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.application;

import org.teamapps.ux.session.SimpleSessionContext;

import java.util.*;
import java.util.function.Function;

public interface ApplicationResourceBundleInfo {

    static ApplicationResourceBundleInfo createUTF8PropertyBundlesInfo(String baseName, Locale baseLocale, Locale... availableTranslations) {
        return createUTF8PropertyBundlesInfo(baseName, "properties", baseLocale, availableTranslations);
    }

    static ApplicationResourceBundleInfo createUTF8PropertyBundlesInfo(String baseName, String suffix, Locale baseLocale, Locale... availableTranslations) {
        Function<Locale, ResourceBundle> resourceBundleByLocaleFunction = locale -> ResourceBundle.getBundle(baseName, locale, new SimpleSessionContext.UTF8Control(suffix));
        return createInfo(resourceBundleByLocaleFunction, baseLocale, availableTranslations);
    }

    static ApplicationResourceBundleInfo createInfo(Function<Locale, ResourceBundle> resourceBundleByLocaleFunction, Locale baseLocale, Locale... availableTranslations) {
        return new ApplicationResourceBundleInfo() {
            @Override
            public Function<Locale, ResourceBundle> getResourceBundleByLocaleFunction() {
                return resourceBundleByLocaleFunction;
            }

            @Override
            public Locale getBaseLocale() {
                return baseLocale;
            }

            @Override
            public List<Locale> getAvailableTranslations() {
                if (availableTranslations == null) {
                    return Collections.emptyList();
                } else {
                    return Arrays.asList(availableTranslations);
                }
            }
        };
    }


    Function<Locale, ResourceBundle> getResourceBundleByLocaleFunction();

    Locale getBaseLocale();

    List<Locale> getAvailableTranslations();
}
