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
package org.teamapps.projector.server.undertow.embedded;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.util.ImmediateInstanceHandle;
import io.undertow.websockets.extensions.PerMessageDeflateHandshake;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import jakarta.servlet.ServletContextListener;
import org.teamapps.projector.server.core.ProjectorServerCore;
import org.teamapps.projector.server.servlet.ProjectorServletContextListener;
import org.teamapps.projector.server.servlet.ServletUtil;
import org.teamapps.projector.resourceprovider.ResourceProvider;
import org.teamapps.projector.server.webcontroller.WebController;

import java.util.List;

public class ProjectorUndertowEmbeddedServer {

	private final ProjectorServerCore projectorServerCore;
	private final int port;
	private final ResourceProvider baseResourceProvider;
	private final List<ServletContextListener> additionalServletContextListeners;

	private Undertow server;
	private boolean started;

	public static ProjectorUndertowEmbeddedServerBuilder builder(WebController webController) {
		return new ProjectorUndertowEmbeddedServerBuilder(webController);
	}

	public ProjectorUndertowEmbeddedServer(ProjectorServerCore projectorServerCore, int port, ResourceProvider baseResourceProvider, List<ServletContextListener> additionalServletContextListeners) {
		this.projectorServerCore = projectorServerCore;
		this.port = port;
		this.baseResourceProvider = baseResourceProvider;
		this.additionalServletContextListeners = additionalServletContextListeners;
	}

	public void start() throws Exception {
		if (started) {
			throw new IllegalStateException("Server already started!");
		}
		this.started = true;

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		DeploymentInfo deploymentInfo = new DeploymentInfo()
				.setContextPath("/")
				.addWelcomePage("index.html")
				.setDeploymentName("teamapps")
				.addListener(new ListenerInfo(ServletContextListener.class, () -> new ImmediateInstanceHandle<>(
						new ProjectorServletContextListener(projectorServerCore))))
				.addListener(new ListenerInfo(ServletContextListener.class, () -> new ImmediateInstanceHandle<>(
						ServletUtil.createResourceProviderServletContextListener("projector-base-resources-servlet", baseResourceProvider, "/*"))))
				.setAllowNonStandardWrappers(true)
				.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo()
						.addExtension(new PerMessageDeflateHandshake(false, 6)))
				.setClassLoader(classLoader);

		additionalServletContextListeners.forEach(l ->
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
		this.started = false;
	}

}
