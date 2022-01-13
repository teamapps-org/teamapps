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
package org.teamapps.server.tomcat.embedded;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.WsSci;
import org.teamapps.client.ClientCodeExtractor;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.core.TeamAppsCore;
import org.teamapps.util.threading.CompletableFutureChainSequentialExecutorFactory;
import org.teamapps.ux.servlet.TeamAppsServletContextListener;
import org.teamapps.webcontroller.WebController;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated This server variant is neither maintained nor tested in production. Please use one of jetty or undertow.
 */
@Deprecated
public class TeamAppsTomcatEmbeddedServer {

	private final TeamAppsCore teamAppsCore;
	private final File webAppDirectory;
	private final List<ServletContextListener> customServletContextListeners = new ArrayList<>();

	private final int port;
	private Tomcat server;
	private boolean started;

	public TeamAppsTomcatEmbeddedServer(WebController webController) throws IOException {
		this(webController, Files.createTempDirectory("teamapps").toFile(), new TeamAppsConfiguration());
	}

	public TeamAppsTomcatEmbeddedServer(WebController webController, int port) throws IOException {
		this(webController, Files.createTempDirectory("teamapps").toFile(), new TeamAppsConfiguration(), port);
	}

	public TeamAppsTomcatEmbeddedServer(WebController webController, File webAppDirectory, int port) {
		this(webController, webAppDirectory, new TeamAppsConfiguration(), port);
	}

	public TeamAppsTomcatEmbeddedServer(WebController webController, File webAppDirectory, TeamAppsConfiguration config) {
		this(webController, webAppDirectory, config, 8080);
	}

	public TeamAppsTomcatEmbeddedServer(WebController webController, File webAppDirectory, TeamAppsConfiguration config, int port) {
		this.teamAppsCore = new TeamAppsCore(config, new CompletableFutureChainSequentialExecutorFactory(config.getMaxNumberOfSessionExecutorThreads()), webController);
		this.webAppDirectory = webAppDirectory;
		this.port = port;
	}

	public TeamAppsCore getTeamAppsCore() {
		return teamAppsCore;
	}

	public void addServletContextListener(ServletContextListener servletContextListener) {
		if (started) {
			throw new IllegalStateException("ServletContextListeners need to be registered before the server is started!");
		}
		this.customServletContextListeners.add(servletContextListener);
	}

	public void start() throws Exception {
		this.started = true;
		ClientCodeExtractor.initializeWebserverDirectory(webAppDirectory);

		server = new Tomcat();
		server.setPort(port);
		Context context = server.addContext("", webAppDirectory.getAbsolutePath());
		TeamAppsServletContextListener listener = new TeamAppsServletContextListener(teamAppsCore);

		context.addServletContainerInitializer(new WsSci(), null);
		context.addServletContainerInitializer((c, servletContext) -> {
			ServletContextEvent servletContextEvent = new ServletContextEvent(servletContext);
			listener.contextInitialized(servletContextEvent);
			customServletContextListeners.forEach(l -> l.contextInitialized(servletContextEvent));
		}, null);
		context.addLifecycleListener(event -> {
			if (event.getLifecycle().getState() == LifecycleState.DESTROYING) {
				ServletContextEvent servletContextEvent = new ServletContextEvent(context.getServletContext());
				listener.contextDestroyed(servletContextEvent);
				customServletContextListeners.forEach(l -> l.contextDestroyed(servletContextEvent));
			}
		});

		DefaultServlet defaultServlet = new DefaultServlet() {
			@Override
			protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
				if (request.getPathInfo() == null || request.getPathInfo().equals("/")) {
					super.doGet(new HttpServletRequestWrapper(request) {
						@Override
						public String getPathInfo() {
							return "/index.html";
						}
					}, response);
				} else {
					super.doGet(request, response);
				}
			}
		};
		Wrapper wrapper = Tomcat.addServlet(context, "default", defaultServlet);
		wrapper.addInitParameter("listings", "true");
		wrapper.addMapping("/*");

		server.getConnector();
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
	}

}
