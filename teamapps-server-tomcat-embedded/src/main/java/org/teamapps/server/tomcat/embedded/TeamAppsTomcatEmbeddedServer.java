/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import org.teamapps.webcontroller.WebController;
import org.teamapps.ux.servlet.TeamAppsServletContextListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class TeamAppsTomcatEmbeddedServer {

	private final WebController webController;
	private final File webAppDirectory;
	private TeamAppsConfiguration config;
	private Tomcat server;

	boolean initialized = false;

	public TeamAppsTomcatEmbeddedServer(WebController webController, File webAppDirectory) {
		this(webController, webAppDirectory, new TeamAppsConfiguration());
	}
	
	public TeamAppsTomcatEmbeddedServer(WebController webController, File webAppDirectory, TeamAppsConfiguration config) {
		this.webController = webController;
		this.webAppDirectory = webAppDirectory;
		this.config = config;
	}

	public void start(int port) throws Exception {
		ClientCodeExtractor.initializeWebserverDirectory(webAppDirectory);

		server = new Tomcat();
		server.setPort(port);
		Context context = server.addContext("", webAppDirectory.getAbsolutePath());
		TeamAppsServletContextListener listener = new TeamAppsServletContextListener(config, webController);

		context.addServletContainerInitializer(new WsSci(), null);
		context.addServletContainerInitializer((c, servletContext) -> {
			listener.contextInitialized(new ServletContextEvent(context.getServletContext()));
		}, null);
		context.addLifecycleListener(event -> {
			if (event.getLifecycle().getState() == LifecycleState.DESTROYING) {
				listener.contextDestroyed(new ServletContextEvent(context.getServletContext()));
				initialized = false;
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

	public void start() throws Exception {
		this.start(8080);
	}

	public void stop() throws Exception {
		server.stop();
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: webControllerClass [webappDirectory]");
		}
		WebController webController = (WebController) Class.forName(args[0]).getConstructor().newInstance();
		new TeamAppsTomcatEmbeddedServer(webController, new File(args[1])).start();
	}

}
