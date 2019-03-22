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
import org.teamapps.ux.component.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RootWindowHandler {

    void showTopLevelComponent(ResponsiveApplicationBuilder applicationBuilder, Icon icon, String title, Component component);

    void removeTopLevelComponent(ResponsiveApplicationBuilder applicationBuilder, Component component);

    String createCustomApplicationEntryURL(ResponsiveApplicationBuilder applicationBuilder, boolean authenticateUser, boolean loadApplicationLauncher, TimeUnit validityTimeUnit, long validityDuration, Map<String, String> parameterMap);

    String createCustomApplicationEntryURL(ResponsiveApplicationBuilder applicationBuilder, boolean authenticateUser, boolean loadApplicationLauncher, Map<String, String> parameterMap);

}
