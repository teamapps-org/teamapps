package org.teamapps.app;

import org.teamapps.agent.UserAgentParser;
import org.teamapps.config.ClientConfigProvider;
import org.teamapps.geoip.GeoIpLookupService;
import org.teamapps.icons.api.IconTheme;
import org.teamapps.icons.provider.IconProvider;
import org.teamapps.server.ServletRegistration;
import org.teamapps.server.UxServerContext;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.resource.ClassPathResourceProvider;
import org.teamapps.ux.resource.ResourceProviderServlet;
import org.teamapps.ux.session.*;
import org.teamapps.webcontroller.WebController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApplicationWebController implements WebController {

	private final ComponentBuilder componentBuilder;
	private final boolean darkTheme;
	private GeoIpLookupService geoIpLookupService;
	private UserAgentParser userAgentParser = new UserAgentParser();
	private List<ServletRegistration> servletRegistrations = new ArrayList<>();
	private List<Function<UxServerContext, ServletRegistration>> servletRegistrationFactories = new ArrayList<>();
	private IconProvider defaultIconProvider;
	private List<IconProvider> iconProviders;

	public ApplicationWebController(ComponentBuilder componentBuilder) {
		this(componentBuilder, false, null);
	}

	public ApplicationWebController(ComponentBuilder componentBuilder, boolean darkTheme) {
		this(componentBuilder, darkTheme, null);
	}

	public ApplicationWebController(ComponentBuilder componentBuilder, boolean darkTheme, String geoIpDatabasePath) {
		this.componentBuilder = componentBuilder;
		this.darkTheme = darkTheme;
		setGeoIpLookupService(geoIpDatabasePath);
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
		RootPanel rootPanel = new RootPanel();
		context.addRootComponent(null, rootPanel);
		rootPanel.setContent(componentBuilder.buildComponent(component -> rootPanel.setContent(component)));
	}


	private void setGeoIpLookupService(String geoIpDatabasePath)  {
		if (geoIpDatabasePath == null || !new File(geoIpDatabasePath).exists()) {
			return;
		} else {
			new Thread(() -> {
				try {
					this.geoIpLookupService = new GeoIpLookupService(geoIpDatabasePath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}).start();
		}
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
		return ClientConfigProvider.createUserAgentSessionConfiguration(darkTheme, context);
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
