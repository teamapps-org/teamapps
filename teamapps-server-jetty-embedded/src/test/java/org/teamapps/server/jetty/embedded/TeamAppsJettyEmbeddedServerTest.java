/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import org.teamapps.dto.ClosedSessionHandlingType;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.session.SessionConfiguration;
import org.teamapps.ux.session.SessionContext;

import java.util.List;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<MaterialIcon> ALL_ICONS = List.copyOf(MaterialIcon.getAllIcons());

	public static void main(String[] args) throws Exception {
		TeamAppsJettyEmbeddedServer.builder((SessionContext sessionContext) -> {
					SessionConfiguration config = sessionContext.getConfiguration();
					config.setClosedSessionHandling(ClosedSessionHandlingType.REFRESH_PAGE);
					sessionContext.setConfiguration(config);

					RootPanel rootPanel = sessionContext.addRootPanel();
					var button = Button.create("click");
					button.onClicked.addListener(() -> sessionContext.destroy());
					rootPanel.setContent(button);
				})
				.setPort(8082)
				.build()
				.start();
	}


}
