package org.teamapps.ux.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.teamapps.ux.servlet.resourceprovider.ResourceProvider;
import org.teamapps.ux.servlet.resourceprovider.ResourceProviderServlet;

public class TeamAppsServletUtil {

	public static ServletContextListener createResourceProviderServletContextListener(final String name, final ResourceProvider resourceProvider, final String... urlPatterns) {
		return new ServletContextListener() {
			@Override
			public void contextInitialized(ServletContextEvent sce) {
				ServletContext servletContext = sce.getServletContext();
				servletContext.addServlet(name, new ResourceProviderServlet(resourceProvider))
						.addMapping(urlPatterns);
			}
		};
	}

}
