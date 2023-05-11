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

import org.teamapps.common.format.Color;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.table.ListTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<MaterialIcon> ALL_ICONS = List.copyOf(MaterialIcon.getAllIcons());

	public static void main(String[] args) throws Exception {
		new TeamAppsJettyEmbeddedServer((SessionContext sessionContext) -> {
			RootPanel rootPanel = sessionContext.addRootPanel();

			Table<User> table = new Table<>();
			ListTableModel<User> model = new ListTableModel<>(
					IntStream.rangeClosed(1, 100)
							.mapToObj(i -> new User(ALL_ICONS.get(i), "Heinz " + i, Color.fromRgbValue(ThreadLocalRandom.current().nextInt())))
							.collect(Collectors.toList())
			);
			table.setModel(model);

			table.addColumn("a", "A", new TextField())
					.setValueExtractor(user1 -> {
						String firstName = user1.getFirstName();
						System.out.println("a: " + firstName);
						return firstName;
					})
					.setDisplayTemplate(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE)
					.setDisplayPropertyProvider((user, propertyNames) -> Map.of("icon", user.getIcon(), "caption", user.getFirstName()))
					.setValueInjector((user, firstName) -> {
						System.out.println("Injecting from a: " + firstName);
						user.setFirstName(firstName);
					});
			table.addColumn("b", "B", new TextField())
					.setValueExtractor(user1 -> {
						String firstName = user1.getFirstName();
						System.out.println("b: " + firstName);
						return firstName;
					})
					.setDisplayTemplate(BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE)
					.setDisplayPropertyProvider((user, propertyNames) -> Map.of("icon", user.getIcon(), "caption", user.getFirstName()))
					.setValueInjector((user, firstName) -> {
						System.out.println("Injecting from b: " + firstName);
						user.setFirstName(firstName);
					});

			table.setEditable(true);

			table.onCellValueChanged.addListener((eventData) -> {
				table.applyCellValuesToRecord(eventData.getRecord());
			});

			rootPanel.setContent(table);
		}, 8082).start();
	}

}
