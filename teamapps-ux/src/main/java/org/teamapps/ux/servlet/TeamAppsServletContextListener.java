/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.core.TeamAppsCore;
import org.teamapps.icons.IconProvider;
import org.teamapps.ux.servlet.component.ComponentLibraryResourceServlet;
import org.teamapps.ux.servlet.resourceprovider.TeamAppsSessionResourceProvider;
import org.teamapps.ux.servlet.resourceprovider.IconResourceProvider;
import org.teamapps.ux.servlet.resourceprovider.ResourceProviderServlet;
import org.teamapps.ux.session.SessionContextResourceManager;

import jakarta.servlet.*;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.websocket.Extension;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.EnumSet;
import java.util.List;

public class TeamAppsServletContextListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamAppsServletContextListener.class);

	private final TeamAppsCore teamAppsCore;

	public TeamAppsServletContextListener(TeamAppsCore teamAppsCore) {
		this.teamAppsCore = teamAppsCore;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext context = servletContextEvent.getServletContext();

		FilterRegistration.Dynamic indexHtmlHeaderFilter = context.addFilter("teamapps-index-html-header-filter", new IndexHtmlHeaderFilter());
		indexHtmlHeaderFilter.setAsyncSupported(true);
		indexHtmlHeaderFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/");
		indexHtmlHeaderFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/index.html");

		FilterRegistration.Dynamic downloadFilterRegistration = context.addFilter("teamapps-download-header-filter", new DownloadHttpHeaderFilter());
		downloadFilterRegistration.setAsyncSupported(true);
		downloadFilterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*");

		Dynamic uploadServletRegistration = context.addServlet("teamapps-upload-servlet", new UploadServlet(teamAppsCore.getConfig().getUploadDirectory(), teamAppsCore.getUploadManager()::addUploadedFile));
		uploadServletRegistration.addMapping("/upload/*");
		uploadServletRegistration.setMultipartConfig(new MultipartConfigElement(null, -1L, -1L, 1000_000));

		Dynamic leaveBeaconServletRegistration = context.addServlet("teamapps-leave", new LeaveBeaconServlet(teamAppsCore.getSessionManager()));
		leaveBeaconServletRegistration.addMapping("/leave/*");

		Dynamic iconServletRegistration = context.addServlet("teamapps-icons", new ResourceProviderServlet(new IconResourceProvider(new IconProvider(teamAppsCore.getIconLibraryRegistry()))));
		iconServletRegistration.addMapping("/icons/*");

		Dynamic componentServletRegistration = context.addServlet("teamapps-components", new ComponentLibraryResourceServlet());
		componentServletRegistration.addMapping("/components/*");

		Dynamic filesServletRegistration = context.addServlet("teamapps-files", new ResourceProviderServlet(new TeamAppsSessionResourceProvider(teamAppsCore.getSessionManager()::getSessionContextById)));
		filesServletRegistration.addMapping(SessionContextResourceManager.BASE_PATH + "*");

		context.addListener(new ServletRequestListener());
		context.addListener(teamAppsCore.getSessionManager());

		try {
			// WebSocket
			ServerContainer serverContainer = (ServerContainer) context.getAttribute("jakarta.websocket.server.ServerContainer");
			ServerEndpointConfig communicationEndpointConfig = ServerEndpointConfig.Builder.create(WebSocketCommunicationEndpoint.class, "/communication")
					.configurator(new WebSocketServerEndpointConfigurator(teamAppsCore.getWebSocketCommunicationEndpoint()))
					.extensions(List.of(new WebsocketExtension("permessage-deflate")))
					.build();
			serverContainer.addEndpoint(communicationEndpointConfig);
		} catch (Exception e) {
			String msg = "Could not register TeamApps communication endpoint";
			LOGGER.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		teamAppsCore.getSessionManager().destroy();
	}


	private static class WebsocketExtension implements Extension {

		private final String name;

		public WebsocketExtension(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public List<Parameter> getParameters() {
			return List.of();
		}
	}
}
