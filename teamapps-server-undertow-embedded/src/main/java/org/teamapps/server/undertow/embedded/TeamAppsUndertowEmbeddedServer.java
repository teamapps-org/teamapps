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
package org.teamapps.server.undertow.embedded;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ListenerInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.util.ImmediateInstanceHandle;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.teamapps.client.ClientCodeExtractor;
import org.teamapps.webcontroller.WebController;
import org.teamapps.ux.servlet.TeamAppsServletContextListener;

import java.io.File;

public class TeamAppsUndertowEmbeddedServer {

    private final WebController webController;
    private final File webAppDirectory;
    private Undertow server;

    public TeamAppsUndertowEmbeddedServer(WebController webController, File webAppDirectory) {
        this.webController = webController;
        this.webAppDirectory = webAppDirectory;
    }

    public void start(int port, String host) throws Exception {
        ClientCodeExtractor.initializeWebserverDirectory(webAppDirectory);

        TeamAppsServletContextListener servletContextListener = new TeamAppsServletContextListener(webController);

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        DeploymentInfo deploymentInfo = new DeploymentInfo()
                .setContextPath("/")
                .addWelcomePage("index.html")
                .setDeploymentName("teamapps")
                .addListener(new ListenerInfo(TeamAppsServletContextListener.class, () -> new ImmediateInstanceHandle<>(servletContextListener)))
                .setResourceManager(new FileResourceManager(webAppDirectory.getAbsoluteFile()))
                .setAllowNonStandardWrappers(true)
                .addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, new WebSocketDeploymentInfo())
                .setClassLoader(classLoader);

        ServletContainer servletContainer = Servlets.defaultContainer();
        DeploymentManager deploymentManager = servletContainer.addDeployment(deploymentInfo);
        deploymentManager.deploy();

        HttpHandler httpHandler = deploymentManager.start();
        server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(httpHandler)
                .build();
        server.start();

    }
    public void start(int port) throws Exception {
        this.start(port,"0.0.0.0" );
    }
    public void start() throws Exception {
        this.start(8080, "0.0.0.0");
    }

    public void stop() throws Exception {
        server.stop();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: webControllerClass [webappDirectory]");
        }
        WebController webController = (WebController) Class.forName(args[0]).getConstructor().newInstance();
        new TeamAppsUndertowEmbeddedServer(webController, new File(args[1])).start();
    }

}
