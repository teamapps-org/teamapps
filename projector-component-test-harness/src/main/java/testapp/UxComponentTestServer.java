package testapp;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamapps.projector.component.core.rootpanel.RootPanel;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.icon.material.MaterialIconStyle;
import org.teamapps.projector.icon.material.MaterialIconStyleType;
import org.teamapps.projector.resource.provider.ClassPathResourceProvider;
import org.teamapps.projector.resource.provider.ResourceProviderServlet;
import org.teamapps.projector.server.config.ProjectorConfiguration;
import org.teamapps.projector.server.jetty.embedded.ProjectorJettyEmbeddedServer;
import org.teamapps.projector.server.webcontroller.WebController;
import org.teamapps.projector.session.CurrentSessionContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.teamapps.projector.common.format.Color.*;

public class UxComponentTestServer {

	public static final List<MaterialIconStyle> ICON_STYLES = Arrays.asList(
			new MaterialIconStyle(MaterialIconStyleType.PLAIN, MATERIAL_PURPLE_500),
			new MaterialIconStyle(MaterialIconStyleType.STICKER, MATERIAL_GREEN_200),
			new MaterialIconStyle(MaterialIconStyleType.GRADIENT, MATERIAL_ORANGE_500, MATERIAL_BLUE_700, MATERIAL_GREEN_500),
			new MaterialIconStyle(MaterialIconStyleType.OUTLINE_FILLED, MATERIAL_BROWN_600),
			new MaterialIconStyle(MaterialIconStyleType.PLAIN_SHADOW, MATERIAL_BLUE_GREY_300));

	public static void main(String[] args) throws Exception {
		System.setProperty("java.awt.headless", "true");

		WebController webController = (sessionContext) -> {
			ScheduledFuture<?> scheduledFuture = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
				try {
					sessionContext.setDefaultStyleForIconClass(MaterialIcon.class, ICON_STYLES.get(ThreadLocalRandom.current().nextInt(ICON_STYLES.size())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, 1, 1, TimeUnit.SECONDS);
			sessionContext.onDestroyed.addListener(() -> scheduledFuture.cancel(false));

			RootPanel rootPanel = new RootPanel();
			sessionContext.addRootComponent(rootPanel);
			sessionContext.subscribeToGlobalKeyEvents(true, false, false, false, false, true, false, System.out::println);
			UxComponentTestApp uxComponentTestApp = new UxComponentTestApp(CurrentSessionContext.get());
			rootPanel.setContent(uxComponentTestApp.getRootComponent());
		};


		ProjectorConfiguration config = new ProjectorConfiguration();
//		config.setUiSessionTimeoutMillis(45_000);
//		config.setUiSessionInactivityTimeoutMillis(30_000);
//		config.setKeepaliveMessageIntervalMillis(10_000);
//		config.setUiSessionPreInactivityPingMillis(10_000);
		ProjectorJettyEmbeddedServer server = ProjectorJettyEmbeddedServer.builder(webController)
				.withConfig(config)
				.withPort(8083)
				.withServletContextListener(new ServletContextListener() {
					@Override
					public void contextInitialized(ServletContextEvent sce) {
						ServletContext servletContext = sce.getServletContext();
						servletContext.addServlet("static-resources", new ResourceProviderServlet(new ClassPathResourceProvider("/static-resources")))
								.addMapping("/static-resources/*");
						servletContext.addFilter("appUrlForwardFilter", new HttpFilter() {
									@Override
									protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
										req.getRequestDispatcher("/").forward(req, res);
									}

									@Override
									public void destroy() {
										// do nothing
									}
								})
								.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/app/*");
					}

					@Override
					public void contextDestroyed(ServletContextEvent sce) {

					}
				})
				.build();
		server.start();
	}

}

