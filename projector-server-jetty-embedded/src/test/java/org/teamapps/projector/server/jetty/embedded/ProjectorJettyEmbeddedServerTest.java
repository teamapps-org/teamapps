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
package org.teamapps.projector.server.jetty.embedded;

import org.teamapps.projector.component.core.rootpanel.RootPanel;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.tree.model.ListTreeModel;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.server.webcontroller.WebController;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.util.List;
import java.util.Map;

public class ProjectorJettyEmbeddedServerTest {

	private static record User(String name, Icon icon) { }

	public static void main(String[] args) throws Exception {

		WebController controller = (SessionContext sessionContext) -> {
			RootPanel rootPanel = new RootPanel();
			rootPanel.setCssStyle("border", "20px solid black");
			sessionContext.addRootComponent(rootPanel);

			ComboBox<User> comboBox = new ComboBox<>(BaseTemplates.LIST_ITEM_MEDIUM_ICON_SINGLE_LINE);
			comboBox.setPropertyProvider((user, propertyNames) -> Map.of("icon", user.icon, "caption", user.name));
			comboBox.setModel(new ListTreeModel<>(List.of(
					new User("Adam", MaterialIcon.ALARM_ON),
					new User("Bob", MaterialIcon.GRID_ON),
					new User("Joe", MaterialIcon.HD)
			)));

			rootPanel.setContent(comboBox);

			sessionContext.subscribeToGlobalKeyEvents(true, false, false, false, false, true, true, System.out::println);
		};

		ProjectorJettyEmbeddedServer jettyServer = ProjectorJettyEmbeddedServer.builder(controller)
				.withPort(8082)
				.build();
		jettyServer.start();
	}

}
