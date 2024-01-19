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

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.component.field.datetime.InstantDateTimeField;
import org.teamapps.ux.component.field.datetime.LocalDateField;
import org.teamapps.ux.component.field.datetime.LocalTimeField;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.table.ListTableModel;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.toolbutton.ToolButton;
import org.teamapps.ux.session.SessionContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<MaterialIcon> ALL_ICONS = List.copyOf(MaterialIcon.getAllIcons());

	public static void main(String[] args) throws Exception {
		TeamAppsJettyEmbeddedServer.builder((SessionContext sessionContext) -> {
					sessionContext.setLocale(Locale.US);


					RootPanel rootPanel = sessionContext.addRootPanel();

					Panel panel = new Panel(MaterialIcon.ALARM_ON, "Panel");

					Table<User> table = new Table<>();
					LocalDateField dateField = new LocalDateField();
					LocalDateField dateField2 = new LocalDateField();
					table.addColumn("x", "halo", dateField).setValueExtractor(user -> LocalDate.now());
					LocalTimeField timeField = new LocalTimeField();
					LocalTimeField timeField2 = new LocalTimeField();
					table.addColumn("y", "ad", timeField).setValueExtractor(user -> LocalTime.now());
					table.setModel(new ListTableModel<>(IntStream.range(0, 100)
							.mapToObj(i -> new User(null, null, null))
							.collect(Collectors.toList())));

					ToolButton toolButton1 = new ToolButton(MaterialIcon.HELP);
					toolButton1.onClick.addListener(() -> {
						dateField.setCalendarIconEnabled(!dateField.isCalendarIconEnabled());
						table.refresh();
						dateField2.setCalendarIconEnabled(!dateField2.isCalendarIconEnabled());
					});
					panel.addToolButton(toolButton1);

					ToolButton toolButton2 = new ToolButton(MaterialIcon.HELP);
					toolButton2.onClick.addListener(() -> {
						timeField.setClockIconEnabled(!timeField.isClockIconEnabled());
						table.refresh();
						timeField2.setClockIconEnabled(!timeField2.isClockIconEnabled());
					});
					panel.addToolButton(toolButton2);


					VerticalLayout verticalLayout = new VerticalLayout();
					verticalLayout.addComponentFillRemaining(table);
					verticalLayout.addComponent(dateField2);
					verticalLayout.addComponent(timeField2);
					verticalLayout.addComponent(new InstantDateTimeField());

					panel.setContent(verticalLayout);

					rootPanel.setContent(panel);
				})
				.setPort(8082)
				.build()
				.start();
	}

}
