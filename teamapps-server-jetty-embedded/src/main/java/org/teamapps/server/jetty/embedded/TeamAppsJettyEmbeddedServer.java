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
package org.teamapps.server.jetty.embedded;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.teamapps.client.ClientCodeExtractor;
import org.teamapps.ux.servlet.TeamAppsServletContextListener;
import org.teamapps.webcontroller.WebController;

import javax.servlet.ServletException;
import java.io.File;
import java.nio.file.Files;

public class TeamAppsJettyEmbeddedServer {

	private final WebController webController;
	private final File webAppDirectory;
	private final Server server;

	public TeamAppsJettyEmbeddedServer(WebController webController, File webAppDirectory) throws ServletException {
		this(webController, webAppDirectory, 8080);
	}

	public TeamAppsJettyEmbeddedServer(WebController webController, File webAppDirectory, int port) throws ServletException {
		this.webController = webController;
		this.webAppDirectory = webAppDirectory;

		server = new Server(port);
		WebAppContext webapp = new WebAppContext();
		webapp.setConfigurations(new Configuration[]{new WebXmlConfiguration()});
		webapp.setContextPath("/");
		// webapp.setInitParameter("teamapps.webController.className", "...");
		webapp.addEventListener(new TeamAppsServletContextListener(webController));
		webapp.setResourceBase(webAppDirectory.getAbsolutePath());
		server.setHandler(webapp);
		WebSocketServerContainerInitializer.configureContext(webapp);
	}

	public void start() throws Exception {
		ClientCodeExtractor.initializeWebserverDirectory(webAppDirectory);
		server.start();
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public Server getServer() {
		return server;
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: webControllerClass [webappDirectory]");
			return;
		}
		WebController webController = (WebController) Class.forName(args[0]).getConstructor().newInstance();
		new TeamAppsJettyEmbeddedServer(webController, Files.createTempDirectory("sf").toFile(), 8080).start();
	}
}
