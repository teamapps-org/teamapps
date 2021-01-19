/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import com.google.common.io.Files;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.field.MultiLineTextField;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {

		WebController controller = (SessionContext sessionContext) -> {
			sessionContext.showNotification(MaterialIcon.MESSAGE, "Hello World");
			RootPanel rootPanel = new RootPanel();
			sessionContext.addRootComponent(null, rootPanel);
			MultiLineTextField tf = new MultiLineTextField();
			rootPanel.setContent(tf);


			tf.setAttribute("XXXXXXX", "yyyyy");

			tf.setAttribute("data-teamapps-debugging-id", null);
			
		};

		TeamAppsJettyEmbeddedServer jettyServer = new TeamAppsJettyEmbeddedServer(controller, Files.createTempDir(), 8082);

		jettyServer.addServletContextListener(new ServletContextListener() {
			@Override
			public void contextInitialized(ServletContextEvent sce) {

			}

			@Override
			public void contextDestroyed(ServletContextEvent sce) {

			}
		});

		// Test custom configurations:
		// jettyServer.configureHttpsUsingP12File(8443, new File("/path/to/cert.p12"), "changeit");
		jettyServer.getWebapp().getSessionHandler().setSecureRequestOnly(true);
		jettyServer.getWebapp().getSessionHandler().getSessionCookieConfig().setHttpOnly(true);
		jettyServer.getWebapp().getSessionHandler().getSessionCookieConfig().setComment("__SAME_SITE_STRICT__");

		// start server
		jettyServer.start();

	}

}
