/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
import org.teamapps.ux.component.infiniteitemview.InfiniteItemView2;
import org.teamapps.ux.component.infiniteitemview.ListInfiniteItemViewModel;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.toolbutton.ToolButton;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.Map;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<MaterialIcon> ALL_ICONS = List.copyOf(MaterialIcon.getAllIcons());

	public static void main(String[] args) throws Exception {
		new TeamAppsJettyEmbeddedServer((SessionContext sessionContext) -> {
			RootPanel rootPanel = sessionContext.addRootPanel();

			Panel panel = new Panel();

//			Table<User> table = new Table<>();
//			table.addColumn("name", "Name", new TextField()).setValueExtractor(User::getFirstName);
//			table.setModel(new ListTableModel<>(List.of(
//					new User(null, "Heinz", null),
//					new User(null, "Jens", null),
//					new User(null, "George", null),
//					new User(null, "John", null)
//			)));
//			table.setAutoHeight(true);

			InfiniteItemView2<User> infiniteItemView2 = new InfiniteItemView2<>();
			User john = new User(null, "John", null);
			infiniteItemView2.setModel(new ListInfiniteItemViewModel<>(List.of(
					new User(null, "Heinz", null),
					new User(null, "Jens", null),
					new User(null, "George", null),
					john
			)));
			infiniteItemView2.setItemTemplate(BaseTemplate.ITEM_VIEW_ITEM);
			infiniteItemView2.setItemPropertyProvider((o, propertyNames) -> Map.of("icon", MaterialIcon.TV, "caption", o.getFirstName()));
			infiniteItemView2.setSelectionEnabled(true);

			infiniteItemView2.onItemSelected.addListener((eventData, disposable) -> {
				System.out.println(eventData.getFirstName());
				System.out.println(infiniteItemView2.getSelectedRecord());
			});

			panel.setContent(infiniteItemView2);
			panel.setStretchContent(false);

			ToolButton toolButton = new ToolButton(MaterialIcon.ALARM_ON);
			toolButton.onClick.addListener((eventData, disposable) -> infiniteItemView2.setSelectedRecord(john));
			panel.addToolButton(toolButton);

			rootPanel.setContent(panel);
		}, 8082).start();
	}

}
