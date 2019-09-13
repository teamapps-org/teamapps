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
package org.teamapps.webcontroller;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import org.teamapps.icons.api.IconThemeImpl;
import org.teamapps.server.ServletRegistration;
import org.teamapps.server.UxServerContext;
import org.teamapps.ux.application.ApplicationBuilder;
import org.teamapps.ux.component.login.LoginAuthenticator;
import org.teamapps.ux.component.login.LoginWindow;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.ux.session.StylingTheme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleApplicationWebController implements WebController {

	public static final String CMD_LOGOUT = "cmdLogout";

	private List<ApplicationBuilder> applicationBuilders = new ArrayList<>();
	private ApplicationBuilder settingsBuilder;
	private LoginAuthenticator authenticator;
	private List<String> preLaunchedApps = new ArrayList<>();

	private String loginBackground = "/resources/backgrounds/login3.jpg";
	private String loginBackgroundBlurred = "/resources/backgrounds/login3-bl.jpg";

	private String defaultBackground = "/resources/backgrounds/default-bl.jpg";

	private Map<String, String> customAppBackgroundByAppName = new HashMap<>();
	private Map<String, StylingTheme> stylingThemeByAppName = new HashMap<>();
	private Function<Locale, ResourceBundle> messageBundleProvider;
	private IconThemeImpl desktopIconTheme;
	private IconThemeImpl mobileIconTheme;
	private List<ServletRegistration> servletRegistrations = new ArrayList<>();
	private List<Function<UxServerContext, ServletRegistration>> servletRegistrationFactories = new ArrayList<>();

	public void setLoginBackgroundUrls(String background, String backgroundBlurred) {
		this.loginBackground = background;
		this.loginBackgroundBlurred = backgroundBlurred;

	}

	public void setDefaultApplicationBackground(String background) {
		this.defaultBackground = background;
	}

	public void scanForApplications() {
		try {
			ClassInfoList widgetClasses =
					new ClassGraph()
							.enableAllInfo()
							.scan()
							.getClassesImplementing(ApplicationBuilder.class.getName());
			widgetClasses.getStandardClasses().loadClasses().stream().forEach(app -> {
				try {
					ApplicationBuilder applicationBuilder = (ApplicationBuilder) app.getDeclaredConstructor().newInstance();
					addApplicationBuilder(applicationBuilder);
				} catch (Exception e) {
					e.printStackTrace();
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addApplicationBuilder(ApplicationBuilder applicationBuilder) {
		applicationBuilders.add(applicationBuilder);
	}

	public void addPreLaunchedApp(String applicationName) {
		preLaunchedApps.add(applicationName);
	}

	public void setSettingsBuilder(ApplicationBuilder settingsBuilder) {
		this.settingsBuilder = settingsBuilder;
	}

	public ApplicationBuilder getApplicationBuilderByName(String appName) {
		return applicationBuilders.stream()
				.filter(builder -> builder.getApplicationInfo().getName().equals(appName))
				.findAny()
				.orElse(null);
	}

	public void registerCustomAppBackground(String appName, String background) {
		customAppBackgroundByAppName.put(appName, background);
	}

	public void registerCustomAppTheme(String appName, StylingTheme theme) {
		stylingThemeByAppName.put(appName, theme);
	}

	@Override
	public void onSessionStart(SessionContext context) {
		context.registerBackgroundImage("login", loginBackground, loginBackgroundBlurred);
		context.registerBackgroundImage("default", defaultBackground, defaultBackground);
		context.setBackgroundImage("login", 0);
		((SessionContext) context).setCustomMessageBundleProvider(messageBundleProvider);

		if (!context.getClientInfo().getClientParameters().isEmpty()) {
			String clientUrl = context.getClientInfo().getClientUrl();
			//registered
		}

		RootPanel rootPanel = new RootPanel();
		context.addRootComponent(null, rootPanel);
		LoginWindow loginWindow = new LoginWindow();
		if (authenticator != null) {
			loginWindow.onLogin.addListener(loginData -> {
				if (authenticator.loginSuccess(loginData.login, loginData.password, context)) {
					createAndShowApplication(context, rootPanel, loginWindow);
				} else {
					loginWindow.setError();
				}
			});
			rootPanel.setContent(loginWindow.getElegantPanel());
		} else {
			createAndShowApplication(context, rootPanel, loginWindow);
		}
		context.setBackgroundImage("login", 0);


		for (Map.Entry<String, String> entry : customAppBackgroundByAppName.entrySet()) {
			context.registerBackgroundImage(entry.getKey(), entry.getValue(), entry.getValue());
		}
	}

	private void createAndShowApplication(SessionContext context, RootPanel rootPanel, LoginWindow loginWindow) {
		context.setBackgroundImage("default", 750);
		ApplicationsController controller = new ApplicationsController(rootPanel, loginWindow, applicationBuilders, settingsBuilder, preLaunchedApps, customAppBackgroundByAppName,
				stylingThemeByAppName, context);
		controller.createUi();
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

//	@Override
//	public IconTheme getDefaultIconTheme(boolean isMobile) {
//		return isMobile ? mobileIconTheme : desktopIconTheme;
//	}

	public void setAuthenticator(LoginAuthenticator authenticator) {
		this.authenticator = authenticator;
	}

	public void setMessageBundleProvider(Function<Locale, ResourceBundle> messageBundleProvider) {
		this.messageBundleProvider = messageBundleProvider;
	}

	public void setDefaultDesktopIconTheme(IconThemeImpl desktopIconTheme) {
		this.desktopIconTheme = desktopIconTheme;
	}

	public void setDefaultMobileIconTheme(IconThemeImpl mobileIconTheme) {
		this.mobileIconTheme = mobileIconTheme;
	}

	public void addServletRegistration(ServletRegistration servletRegistration) {
		this.servletRegistrations.add(servletRegistration);
	}

	public void addServletRegistrationFactory(Function<UxServerContext, ServletRegistration> servletRegistrationFactory) {
		this.servletRegistrationFactories.add(servletRegistrationFactory);
	}
}
