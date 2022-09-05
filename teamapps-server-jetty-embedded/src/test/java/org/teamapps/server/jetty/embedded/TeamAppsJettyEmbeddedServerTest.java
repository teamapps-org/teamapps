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

import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.div.Div;
import org.teamapps.ux.component.dummy.DummyComponent;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionContext;
import org.teamapps.webcontroller.WebController;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {

		WebController controller = (SessionContext sessionContext) -> {
			RootPanel rootPanel = sessionContext.addRootPanel();
			rootPanel.setCssStyle("background-color", "blue");
			rootPanel.setCssStyle("ui-div", "background-color", "green");
			Div div = new Div(new DummyComponent());
			div.setAttribute("blah", "blub");
			div.toggleCssClass("xxxx", true);
			div.toggleCssClass("slot", "asdflksjdf", true);
			div.setCssStyle("font-size", "200%");
			div.setCssStyle("slot", "color", "red");
			rootPanel.setContent(div);
		};

		TeamAppsJettyEmbeddedServer jettyServer = new TeamAppsJettyEmbeddedServer(controller, 8082);
		System.out.println(jettyServer.getTeamAppsCore().getComponentLibraryRegistry().registerComponentLibrary(new CoreComponentLibrary()));
		jettyServer.start();

	}

}
