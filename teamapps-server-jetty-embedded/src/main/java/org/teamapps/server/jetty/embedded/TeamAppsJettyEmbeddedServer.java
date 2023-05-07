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
package org.teamapps.server.jetty.embedded;

import jakarta.servlet.ServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.teamapps.core.TeamAppsCore;
import org.teamapps.ux.servlet.TeamAppsServletContextListener;
import org.teamapps.ux.servlet.TeamAppsServletUtil;
import org.teamapps.ux.servlet.resourceprovider.ResourceProvider;
import org.teamapps.webcontroller.WebController;

import java.util.List;

public class TeamAppsJettyEmbeddedServer {

	private final Server server;
	private final WebAppContext webapp;

	public static TeamAppsJettyEmbeddedServerBuilder builder(WebController webController) {
		return new TeamAppsJettyEmbeddedServerBuilder(webController);
	}

	TeamAppsJettyEmbeddedServer(TeamAppsCore teamAppsCore, int port, ResourceProvider baseResourceProvider, List<ServletContextListener> additionalServletContextListeners) {
		server = new Server(port);
		webapp = new WebAppContext();
		webapp.setClassLoader(TeamAppsJettyEmbeddedServer.class.getClassLoader());
		webapp.setConfigurations(new Configuration[]{new WebXmlConfiguration()});
		webapp.setContextPath("/");

		webapp.addEventListener(TeamAppsServletUtil.createResourceProviderServletContextListener("teamapps-base-resources-servlet", baseResourceProvider, "/*"));
		webapp.addEventListener(new TeamAppsServletContextListener(teamAppsCore));
		additionalServletContextListeners.forEach(webapp::addEventListener);

		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		// The following will not actually set the secure flag on the cookie if the session is started without encryption.
		// Use getWebapp().getSessionHandler().getSessionCookieConfig().setSecure(true) if you want to force secure cookies.
		webapp.getSessionHandler().setSecureRequestOnly(true);
		server.setHandler(webapp);
		JakartaWebSocketServletContainerInitializer.configure(webapp, null);
	}

	public Server getServer() {
		return server;
	}

	public WebAppContext getWebapp() {
		return webapp;
	}

	public void start() throws Exception {
		server.start();
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

}
