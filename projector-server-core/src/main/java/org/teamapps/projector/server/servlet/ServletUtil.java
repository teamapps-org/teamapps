package org.teamapps.projector.server.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.teamapps.projector.resourceprovider.ResourceProvider;
import org.teamapps.projector.resourceprovider.ResourceProviderServlet;

public class ServletUtil {

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
