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
package org.teamapps.server.jetty.embedded;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.teamapps.client.ClientCodeExtractor;
import org.teamapps.config.TeamAppsConfiguration;
import org.teamapps.ux.servlet.TeamAppsServletContextListener;
import org.teamapps.webcontroller.WebController;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class TeamAppsJettyEmbeddedServer {

	private final File webAppDirectory;
	private final Server server;
	private WebAppContext webapp;

	public TeamAppsJettyEmbeddedServer(WebController webController, File webAppDirectory) throws ServletException {
		this(webController, webAppDirectory, new TeamAppsConfiguration());
	}

	public TeamAppsJettyEmbeddedServer(WebController webController, File webAppDirectory, TeamAppsConfiguration config) throws ServletException {
		this(webController, webAppDirectory, 8080, config);
	}

	public TeamAppsJettyEmbeddedServer(WebController webController, File webAppDirectory, int port) throws ServletException {
		this(webController, webAppDirectory, port, new TeamAppsConfiguration());
	}

	public TeamAppsJettyEmbeddedServer(WebController webController, File webAppDirectory, int port, TeamAppsConfiguration config) throws ServletException {
		this.webAppDirectory = webAppDirectory;

		server = new Server(port);
		webapp = new WebAppContext();
		webapp.setConfigurations(new Configuration[]{new WebXmlConfiguration()});
		webapp.setContextPath("/");
		webapp.addEventListener(new TeamAppsServletContextListener(config, webController));
		webapp.setResourceBase(webAppDirectory.getAbsolutePath());
		webapp.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
		// The following will not actually set the secure flag on the cookie if the session is started without encryption.
		// Use getWebapp().getSessionHandler().getSessionCookieConfig().setSecure(true) if you want to force secure cookies.
		webapp.getSessionHandler().setSecureRequestOnly(true);
		server.setHandler(webapp);
		WebSocketServerContainerInitializer.configureContext(webapp);
	}

	public void configureHttpsUsingP12File(int port, File keyStoreFile, String keyStorePassword) {
		if (server.isStarting() || server.isStarted()) {
			throw new IllegalStateException("HTTPS must be configured before starting the server!");
		}

		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setSecureScheme("https");
		http_config.setSecurePort(port);

		HttpConfiguration https_config = new HttpConfiguration(http_config);
		https_config.addCustomizer(new SecureRequestCustomizer());

		SslContextFactory sslContextFactory = new SslContextFactory.Server();
		try {
			sslContextFactory.setKeyStore(KeyStore.getInstance(keyStoreFile, keyStorePassword.toCharArray()));
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		sslContextFactory.setKeyStorePassword(keyStorePassword);

		ServerConnector httpsConnector = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, "http/1.1"),
				new HttpConnectionFactory(https_config));
		httpsConnector.setPort(8443);
		httpsConnector.setIdleTimeout(50000);

		server.addConnector(httpsConnector);
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

	public WebAppContext getWebapp() {
		return webapp;
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
