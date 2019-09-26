package org.teamapps.app;

import org.jetbrains.annotations.NotNull;
import org.teamapps.agent.UserAgentParser;
import org.teamapps.app.background.Background;
import org.teamapps.auth.AuthenticationProvider;
import org.teamapps.auth.AuthenticationResult;
import org.teamapps.geoip.GeoIpLookupService;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.icons.provider.IconProvider;
import org.teamapps.server.ServletRegistration;
import org.teamapps.server.UxServerContext;
import org.teamapps.ux.component.login.LoginWindow;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.resource.ClassPathResourceProvider;
import org.teamapps.ux.resource.ResourceProviderServlet;
import org.teamapps.ux.session.*;
import org.teamapps.webcontroller.WebController;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApplicationWebController<USER> implements WebController {

	private Theme loginTheme = new Theme(Background.createDefaultLoginBackground(), false);
	private Theme defaultTheme = new Theme(Background.createDefaultBackground(), false);
	private ThemeHandler<USER> userThemeHandler;

	private final AuthenticationProvider<USER> authenticationProvider;
	private UserAgentParser userAgentParser = new UserAgentParser();
	private GeoIpLookupService geoIpLookupService;
	private List<ServletRegistration> servletRegistrations = new ArrayList<>();
	private List<Function<UxServerContext, ServletRegistration>> servletRegistrationFactories = new ArrayList<>();
	private IconProvider defaultIconProvider;
	private List<IconProvider> iconProviders;

	public ApplicationWebController(AuthenticationProvider<USER> authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	@Override
	public void onSessionStart(SessionContext context) {
		if (context.getClientInfo() != null && context.getClientInfo().getUserAgent() != null) {
			ClientUserAgent clientUserAgent = userAgentParser.parseUserAgent(context.getClientInfo().getUserAgent());
			context.getClientInfo().setUserAgentData(clientUserAgent);
			if (geoIpLookupService != null) {
				ClientGeoIpInfo clientGeoIpInfo = geoIpLookupService.getClientGeoIpInfo(context.getClientInfo().getIp());
				context.getClientInfo().setGeoIpInfo(clientGeoIpInfo);
			}
		}
		loginTheme.getBackground().registerBackground(context);
		defaultTheme.getBackground().registerBackground(context);

		RootPanel rootPanel = new RootPanel();
		context.addRootComponent(null, rootPanel);

		LoginWindow loginWindow = new LoginWindow();
		rootPanel.setContent(loginWindow.getElegantPanel());
		loginWindow.onLogin.addListener(loginData -> {
			AuthenticationResult<USER> authenticationResult = authenticationProvider.authenticate(loginData.login, loginData.password);
			if (authenticationResult.isSuccess()) {
				USER authenticatedUser = authenticationResult.getAuthenticatedUser();
				Theme theme = userThemeHandler != null ? userThemeHandler.getUserTheme(authenticatedUser, context.getClientInfo().isMobileDevice(), context) : defaultTheme;
				updateTheme(theme, context);

			} else {
				loginWindow.setError();
			}
		});


	}

	private void updateTheme(Theme theme, SessionContext context) {
		if (theme.getBackground() != null) {
			theme.getBackground().registerAndApply(context);
		}
		if (theme.isDarkTheme() != loginTheme.isDarkTheme()) {
			context.setConfiguration(createSessionConfiguration(theme.isDarkTheme(), context));
		}
	}

	public void setGeoIpLookupService(String geoIpDatabasePath) throws IOException {
		this.geoIpLookupService = new GeoIpLookupService(geoIpDatabasePath);
	}

	public void setLoginTheme(Theme loginTheme) {
		this.loginTheme = loginTheme;
	}

	public void setDefaultTheme(Theme defaultTheme) {
		this.defaultTheme = defaultTheme;
	}

	public void setUserThemeHandler(ThemeHandler<USER> userThemeHandler) {
		this.userThemeHandler = userThemeHandler;
	}

	public void setDefaultIconProvider(IconProvider defaultIconProvider) {
		this.defaultIconProvider = defaultIconProvider;
	}

	public void addIconProvider(IconProvider iconProvider) {
		iconProviders.add(iconProvider);
	}

	@Override
	public IconTheme getDefaultIconTheme(boolean isMobile) {
		return WebController.super.getDefaultIconTheme(isMobile);
	}

	@Override
	public IconProvider getIconProvider() {
		return defaultIconProvider != null ? defaultIconProvider : WebController.super.getIconProvider();
	}

	@Override
	public List<IconProvider> getAdditionalIconProvider() {
		return iconProviders;
	}

	@Override
	public SessionConfiguration createSessionConfiguration(SessionContext context) {
		return createSessionConfiguration(loginTheme.isDarkTheme(), context);
	}

	@NotNull
	private SessionConfiguration createSessionConfiguration(boolean darkTheme, SessionContext context) {
		boolean optimizedForTouch = false;
		StylingTheme theme = StylingTheme.DEFAULT;
		if (context.getClientInfo().isMobileDevice()) {
			optimizedForTouch = true;
			theme = StylingTheme.MODERN;
		}
		if (darkTheme) {
			theme = StylingTheme.DARK;
		}

		Locale locale = Locale.forLanguageTag(context.getClientInfo().getPreferredLanguageIso());
		return SessionConfiguration.create(
				locale,
				ZoneId.of(context.getClientInfo().getTimeZone()),
				theme,
				optimizedForTouch
		);
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

	public void addClassPathResourceProvider(String basePackage, String prefix) {
		if (!prefix.endsWith("/")) {
			prefix += "/";
		}
		addServletRegistration(new ServletRegistration(new ResourceProviderServlet(new ClassPathResourceProvider(basePackage)), prefix + "*"));
	}

	public void addServletRegistration(ServletRegistration servletRegistration) {
		this.servletRegistrations.add(servletRegistration);
	}

	public void addServletRegistrationFactory(Function<UxServerContext, ServletRegistration> servletRegistrationFactory) {
		this.servletRegistrationFactories.add(servletRegistrationFactory);
	}
}
