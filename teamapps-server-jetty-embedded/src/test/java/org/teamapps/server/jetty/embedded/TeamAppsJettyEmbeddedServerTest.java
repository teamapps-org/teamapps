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

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {

		WebController controller = (SessionContext sessionContext) -> {
			sessionContext.onDestroyed.addListener(uiSessionClosingReason -> System.out.println("Session destroyed: " + uiSessionClosingReason));

			sessionContext.showNotification(MaterialIcon.MESSAGE, "Hello World");
			RootPanel rootPanel = sessionContext.addRootPanel();
			Button<BaseTemplateRecord> button = Button.create("destroy session!");
			button.onClicked.addListener(() -> {
				sessionContext.setFavicon(MaterialIcon.SMS);
				sessionContext.setTitle("My new title " + System.currentTimeMillis());
			});
			rootPanel.setContent(button);
		};

		TeamAppsJettyEmbeddedServer jettyServer = new TeamAppsJettyEmbeddedServer(controller, 8082);
		jettyServer.start();

	}

}
