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
package org.teamapps.ux.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.json.TeamAppsObjectMapperFactory;
import org.teamapps.server.ServletRegistration;
import org.teamapps.webcontroller.WebController;
import org.teamapps.uisession.TeamAppsUiSessionManager;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration.Dynamic;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Collection;
import java.util.EnumSet;
import java.util.UUID;

public class TeamAppsServletContextListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamAppsServletContextListener.class);

	private WebController webController;
	private TeamAppsUiSessionManager uiSessionManager;

	public TeamAppsServletContextListener() {
		this(null);
	}

	public TeamAppsServletContextListener(WebController webController) {
		this.webController = webController;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext context = servletContextEvent.getServletContext();

		WebController webController = this.webController != null ? this.webController : getWebController(context);
		if (webController == null) {
			throw new IllegalArgumentException("No WebController specified!");
		}

		ObjectMapper objectMapper = TeamAppsObjectMapperFactory.create();
		uiSessionManager = new TeamAppsUiSessionManager(objectMapper);
		TeamAppsUxClientGate teamAppsUxClientGate = new TeamAppsUxClientGate(webController, uiSessionManager, objectMapper);
		uiSessionManager.setUiSessionListener(teamAppsUxClientGate);

		context.addFilter("teamapps-download-header-filter", new DownloadHttpHeaderFilter()).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*");

		Dynamic uploadServletRegistration = context.addServlet("teamapps-upload-servlet", new UploadServlet((file, uuid) -> teamAppsUxClientGate.handleFileUpload(file, uuid)));
		uploadServletRegistration.addMapping("/upload/*");
		uploadServletRegistration.setMultipartConfig(new MultipartConfigElement(null, -1L, -1L, 1000_000));

		Dynamic leaveBeaconServletRegistration = context.addServlet("teamapps-leave", new LeaveBeaconServlet(uiSessionManager));
		leaveBeaconServletRegistration.addMapping("/leave/*");

		context.addListener(new ServletRequestListener());
		context.addListener(uiSessionManager);

		try {
			// WebSocket
			ServerContainer serverContainer = (ServerContainer) servletContextEvent.getServletContext().getAttribute("javax.websocket.server.ServerContainer");
			ServerEndpointConfig communicationEndpointConfig = ServerEndpointConfig.Builder.create(TeamAppsCommunicationEndpoint.class, "/communication")
					.configurator(new WebSocketServerEndpointConfigurator(uiSessionManager)).build();
			serverContainer.addEndpoint(communicationEndpointConfig);
		} catch (Exception e) {
			String msg = "Could not register TeamApps communication endpoint";
			LOGGER.error(msg, e);
			throw new RuntimeException(msg, e);
		}

		Collection<ServletRegistration> servletRegistrations = teamAppsUxClientGate.getServletRegistrations();
		for (ServletRegistration servletRegistration : servletRegistrations) {
			for (String mapping : servletRegistration.getMappings()) {
				LOGGER.info("Registering servlet on url path: " + mapping);
				context.addServlet("teamapps-registered-" + servletRegistration.getServlet().getClass().getSimpleName() + UUID.randomUUID().toString(), servletRegistration.getServlet()).addMapping(mapping);
			}
		}
	}

	private WebController getWebController(ServletContext context) {
		if (this.webController != null) {
			return webController;
		} else {
			String classNameInitParameter = context.getInitParameter("teamapps.webController.className");
			if (classNameInitParameter != null) {
				try {
					return ((WebController) Class.forName(classNameInitParameter).getDeclaredConstructor().newInstance());
				} catch (Exception e) {
					String msg = "Could instantiate WebController";
					LOGGER.error(msg, e);
					throw new IllegalArgumentException(msg, e);
				}
			} else {
				throw new IllegalArgumentException("No WebController specified!");
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		uiSessionManager.destroy();
		webController.destroy();
	}

}
