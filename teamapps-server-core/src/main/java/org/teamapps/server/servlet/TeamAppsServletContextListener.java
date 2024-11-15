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
package org.teamapps.server.servlet;

import jakarta.servlet.*;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.websocket.Extension;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.server.core.TeamAppsServerCore;
import org.teamapps.icons.IconProvider;
import org.teamapps.projector.resourceprovider.IconResourceProvider;
import org.teamapps.projector.resourceprovider.ResourceProviderServlet;
import org.teamapps.projector.session.SessionContextResourceManager;

import java.lang.invoke.MethodHandles;
import java.util.EnumSet;
import java.util.List;

public class TeamAppsServletContextListener implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final TeamAppsServerCore teamAppsServerCore;

	public TeamAppsServletContextListener(TeamAppsServerCore teamAppsServerCore) {
		this.teamAppsServerCore = teamAppsServerCore;
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

		Dynamic uploadServletRegistration = context.addServlet("teamapps-upload-servlet", new UploadServlet(teamAppsServerCore.getConfig().getUploadDirectory(), teamAppsServerCore.getUploadManager()::addUploadedFile));
		uploadServletRegistration.addMapping("/upload/*");
		uploadServletRegistration.setMultipartConfig(new MultipartConfigElement(null, -1L, -1L, 1000_000));

		Dynamic leaveBeaconServletRegistration = context.addServlet("teamapps-leave", new LeaveBeaconServlet(teamAppsServerCore.getSessionManager()));
		leaveBeaconServletRegistration.addMapping("/leave/*");

		Dynamic iconServletRegistration = context.addServlet("teamapps-icons", new ResourceProviderServlet(new IconResourceProvider(new IconProvider(teamAppsServerCore.getIconLibraryRegistry()))));
		iconServletRegistration.addMapping("/icons/*");

		Dynamic componentServletRegistration = context.addServlet("teamapps-components", new ComponentLibraryResourceServlet(teamAppsServerCore.getComponentLibraryRegistry()));
		componentServletRegistration.addMapping("/components/*");

		Dynamic filesServletRegistration = context.addServlet("teamapps-files", new ResourceProviderServlet(new TeamAppsSessionResourceProvider(teamAppsServerCore.getSessionManager()::getSessionContextById)));
		filesServletRegistration.addMapping(SessionContextResourceManager.BASE_PATH + "*");

		context.addListener(new ServletRequestListener());
		context.addListener(teamAppsServerCore.getSessionManager());

		try {
			// WebSocket
			ServerContainer serverContainer = (ServerContainer) context.getAttribute("jakarta.websocket.server.ServerContainer");
			ServerEndpointConfig communicationEndpointConfig = ServerEndpointConfig.Builder.create(WebSocketCommunicationEndpoint.class, "/communication")
					.configurator(new WebSocketServerEndpointConfigurator(teamAppsServerCore.getWebSocketCommunicationEndpoint()))
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
		teamAppsServerCore.getSessionManager().destroy();
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