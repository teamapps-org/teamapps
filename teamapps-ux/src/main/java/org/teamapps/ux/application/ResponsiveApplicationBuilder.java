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

import java.util.function.Function;

public interface ResponsiveApplicationBuilder {

    static ResponsiveApplicationBuilder create(String fullyQualifiedApplicationName, ApplicationResourceBundleInfo applicationResourceBundleInfo, Function<ApplicationLauncher, ResponsiveApplication> applicationFunction) {
        return create(fullyQualifiedApplicationName, applicationResourceBundleInfo, applicationFunction, null);
    }

    static ResponsiveApplicationBuilder create(String fullyQualifiedApplicationName, ApplicationResourceBundleInfo applicationResourceBundleInfo, Function<ApplicationLauncher, ResponsiveApplication> applicationFunction, ApplicationTheme applicationTheme) {
        return new ResponsiveApplicationBuilder() {
            @Override
            public String getFullyQualifiedApplicationName() {
                return fullyQualifiedApplicationName;
            }

            @Override
            public ApplicationResourceBundleInfo getApplicationResourceBundleInfo() {
                return applicationResourceBundleInfo;
            }

            @Override
            public ResponsiveApplication createApplication(ApplicationLauncher launcher) {
                return applicationFunction.apply(launcher);
            }

            @Override
            public ApplicationTheme getCustomApplicationTheme() {
                return applicationTheme;
            }
        };
    }

    String getFullyQualifiedApplicationName();

    ApplicationResourceBundleInfo getApplicationResourceBundleInfo();

    ResponsiveApplication createApplication(ApplicationLauncher launcher);

    default ApplicationTheme getCustomApplicationTheme() {
        return null;
    }


}
