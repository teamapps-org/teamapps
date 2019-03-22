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

import org.teamapps.icons.api.Icon;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ResponsiveApplicationGroup {

    private final View applicationLauncher;
    private final ManagedResourceBundleProvider managedResourceBundleProvider;
    private final RootWindowHandler rootWindowHandler;
    private final List<ResponsiveApplicationBuilder> applicationBuilders = new ArrayList<>();

    public ResponsiveApplicationGroup(View applicationLauncher, ManagedResourceBundleProvider managedResourceBundleProvider, RootWindowHandler rootWindowHandler) {
        this.applicationLauncher = applicationLauncher;
        this.managedResourceBundleProvider = managedResourceBundleProvider;
        this.rootWindowHandler = rootWindowHandler;
    }

    public ApplicationLauncher addApplication(ResponsiveApplicationBuilder applicationBuilder) {
        if (applicationBuilder.getApplicationResourceBundleInfo() != null) {
            managedResourceBundleProvider.registerApplicationResourceBundle(applicationBuilder);
        }
        applicationBuilders.add(applicationBuilder);

        return new ApplicationLauncher() {
            @Override
            public View getLauncherView() {
                return applicationLauncher;
            }

            @Override
            public ResourceBundle getManagedApplicationResourceBundle(Locale locale) {
                if (applicationBuilder.getApplicationResourceBundleInfo() == null) {
                    return null;
                }
                return managedResourceBundleProvider.getManagedResourceBundle(applicationBuilder, locale);
            }

            @Override
            public void showTopLevelComponent(Icon icon, String title, Component component) {
                rootWindowHandler.showTopLevelComponent(applicationBuilder, icon, title, component);
            }

            @Override
            public void removeTopLevelComponent(Component component) {
                rootWindowHandler.removeTopLevelComponent(applicationBuilder, component);
            }

            @Override
            public String createCustomApplicationEntryURL(boolean authenticateUser, boolean loadApplicationLauncher, Map<String, String> parameterMap) {
                return rootWindowHandler.createCustomApplicationEntryURL(applicationBuilder, authenticateUser, loadApplicationLauncher, parameterMap);
            }

            @Override
            public String createCustomApplicationEntryURL(boolean authenticateUser, boolean loadApplicationLauncher, TimeUnit validityTimeUnit, long validityDuration, Map<String, String> parameterMap) {
                return rootWindowHandler.createCustomApplicationEntryURL(applicationBuilder, authenticateUser, loadApplicationLauncher, validityTimeUnit, validityDuration, parameterMap);
            }
        };
    }
}
