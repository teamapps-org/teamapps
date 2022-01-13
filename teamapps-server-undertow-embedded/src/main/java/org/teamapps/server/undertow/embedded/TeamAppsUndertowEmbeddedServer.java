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
package org.teamapps.server.undertow.embedded;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.*;
import io.undertow.servlet.util.ImmediateInstanceHandle;
import io.undertow.websockets.extensions.PerMessageDeflateHandshake;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.teamapps.client.ClientCodeExtractor;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.core.TeamAppsCore;
import org.teamapps.util.threading.CompletableFutureChainSequentialExecutorFactory;
import org.teamapps.ux.servlet.TeamAppsServletContextListener;
import org.teamapps.webcontroller.WebController;

import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TeamAppsUndertowEmbeddedServer {

	private final TeamAppsCore teamAppsCore;
	private final File webAppDirectory;
	private final List<ServletContextListener> customServletContextListeners = new ArrayList<>();
	private Function<DeploymentInfo, DeploymentInfo> deploymentInfoManipulator;

	private final int port;
	private Undertow server;
	private boolean started;

	public TeamAppsUndertowEmbeddedServer(WebController webController) throws IOException {
		this(webController, Files.createTempDirectory("teamapps").toFile(), new TeamAppsConfiguration());
	}

	public TeamAppsUndertowEmbeddedServer(WebController webController, int port) throws IOException {
		this(webController, Files.createTempDirectory("teamapps").toFile(), new TeamAppsConfiguration(), port);
	}

	public TeamAppsUndertowEmbeddedServer(WebController webController, TeamAppsConfiguration config) throws IOException {
		this(webController, Files.createTempDirectory("teamapps").toFile(), config, 8080);
	}

	public TeamAppsUndertowEmbeddedServer(WebController webController, TeamAppsConfiguration config, int port) throws IOException {
		this(webController, Files.createTempDirectory("teamapps").toFile(), config, port);
	}

	public TeamAppsUndertowEmbeddedServer(WebController webController, File webAppDirectory, int port) throws IOException {
		this(webController, webAppDirectory, new TeamAppsConfiguration(), port);
	}

	public TeamAppsUndertowEmbeddedServer(WebController webController, File webAppDirectory, TeamAppsConfiguration config) throws IOException {
		this(webController, webAppDirectory, config, 8080);
	}

	public TeamAppsUndertowEmbeddedServer(WebController webController, File webAppDirectory, TeamAppsConfiguration config, int port) throws IOException {
		this.teamAppsCore = new TeamAppsCore(config, new CompletableFutureChainSequentialExecutorFactory(config.getMaxNumberOfSessionExecutorThreads()), webController);
		this.webAppDirectory = webAppDirectory.toPath().toRealPath().toFile();
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

	public void setDeploymentInfoCallback(Function<DeploymentInfo, DeploymentInfo> deploymentInfoCallback) {
		if (started) {
			throw new IllegalStateException("deploymentInfoManipulator need to be registered before the server is started!");
		}
		this.deploymentInfoManipulator = deploymentInfoCallback;
	}

	public void start() throws Exception {
		this.started = true;
		ClientCodeExtractor.initializeWebserverDirectory(webAppDirectory);

		TeamAppsServletContextListener servletContextListener = new TeamAppsServletContextListener(teamAppsCore);

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		DeploymentInfo deploymentInfo = new DeploymentInfo()
				.setContextPath("/")
				.addWelcomePage("index.html")
				.setDeploymentName("teamapps")
				.addListener(new ListenerInfo(ServletContextListener.class, () -> new ImmediateInstanceHandle<>(servletContextListener)))
				.setResourceManager(new FileResourceManager(webAppDirectory.getAbsoluteFile()))
				.setAllowNonStandardWrappers(true)
				.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo()
						.addExtension(new PerMessageDeflateHandshake(false, 6)))
				.setClassLoader(classLoader);

		if (deploymentInfoManipulator != null ) {
			deploymentInfoManipulator.apply(deploymentInfo);
		}

		customServletContextListeners.forEach(l ->
				deploymentInfo.addListener(new ListenerInfo(ServletContextListener.class, () -> new ImmediateInstanceHandle<>(l))));

		ServletContainer servletContainer = Servlets.defaultContainer();
		DeploymentManager deploymentManager = servletContainer.addDeployment(deploymentInfo);
		deploymentManager.deploy();

		HttpHandler httpHandler = deploymentManager.start();
		server = Undertow.builder()
				.addHttpListener(port, "0.0.0.0")
				.setHandler(httpHandler)
				.setIoThreads(Math.max(Runtime.getRuntime().availableProcessors() * 4, 10))
				.setWorkerThreads(Math.max(Runtime.getRuntime().availableProcessors() * 8, 10))
				.build();
		server.start();

	}

	public void stop() throws Exception {
		server.stop();
	}

}
