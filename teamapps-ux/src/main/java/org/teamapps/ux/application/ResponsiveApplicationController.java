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
import org.teamapps.server.ServletRegistration;
import org.teamapps.server.UxServerContext;
import org.teamapps.webcontroller.WebController;
import org.teamapps.ux.resource.ClassPathResourceProvider;
import org.teamapps.ux.resource.ResourceProviderServlet;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.login.LoginAuthenticator;
import org.teamapps.ux.session.SessionContext;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResponsiveApplicationController implements WebController, ManagedResourceBundleProvider, RootWindowHandler {

    private View applicationLauncher;
    private List<ResponsiveApplicationGroup> applicationGroups = new ArrayList<>();

    private LoginAuthenticator authenticator;
    private List<ServletRegistration> servletRegistrations = new ArrayList<>();
    private List<Function<UxServerContext, ServletRegistration>> servletRegistrationFactories = new ArrayList<>();


    private String loginBackground = "/resources/backgrounds/login3.jpg";
    private String loginBackgroundBlurred = "/resources/backgrounds/login3-bl.jpg";
    private String defaultBackground = "/resources/default-bl.jpg";

    @Override
    public void onSessionStart(SessionContext context) {

    }

    public void addClassPathResourceProvider(String basePackage, String prefix) {
        if (!prefix.endsWith("/")) {
            prefix += "/";
        }
        addServletRegistration(new ServletRegistration(new ResourceProviderServlet(new ClassPathResourceProvider(basePackage)),  prefix+ "*"));
    }

    @Override
    public Collection<ServletRegistration> getServletRegistrations(UxServerContext serverContext) {
        ArrayList<ServletRegistration> registrations = new ArrayList<>();
        registrations.addAll(this.servletRegistrations);
        registrations.addAll(servletRegistrationFactories.stream()
                .map(f -> f.apply(serverContext))
                .collect(Collectors.toList()));
        return registrations;
    }




//    @Override
//    public IconTheme getDefaultIconTheme(boolean isMobile) {
//        return null;
//    }
//
//    @Override
//    public IconProvider getIconProvider() {
//        return null;
//    }

    public ResponsiveApplicationGroup addApplicationGroup(Icon icon, String title) {
        ResponsiveApplicationGroup applicationGroup = new ResponsiveApplicationGroup(applicationLauncher, this, this);
        applicationGroups.add(applicationGroup);
        return applicationGroup;
    }

    @Override
    public void registerApplicationResourceBundle(ResponsiveApplicationBuilder applicationBuilder) {

    }

    @Override
    public ResourceBundle getManagedResourceBundle(ResponsiveApplicationBuilder applicationBuilder, Locale locale) {
        return null;
    }

    @Override
    public void showTopLevelComponent(ResponsiveApplicationBuilder applicationBuilder, Icon icon, String title, Component component) {

    }

    @Override
    public void removeTopLevelComponent(ResponsiveApplicationBuilder applicationBuilder, Component component) {

    }

    @Override
    public String createCustomApplicationEntryURL(ResponsiveApplicationBuilder applicationBuilder, boolean authenticateUser, boolean loadApplicationLauncher, Map<String, String> parameterMap) {
        //todo use application base prefix - link is valid without registration!
        return null;
    }

    @Override
    public String createCustomApplicationEntryURL(ResponsiveApplicationBuilder applicationBuilder, boolean authenticateUser, boolean loadApplicationLauncher, TimeUnit validityTimeUnit, long validityDuration, Map<String, String> parameterMap) {
        //todo save in map, remove old entries, check --> create single UUID parameter: baselinkg?uuid
        return null;
    }

    public void addServletRegistration(ServletRegistration servletRegistration) {
        this.servletRegistrations.add(servletRegistration);
    }

    public void addServletRegistrationFactory(Function<UxServerContext, ServletRegistration> servletRegistrationFactory) {
        this.servletRegistrationFactories.add(servletRegistrationFactory);
    }

}
