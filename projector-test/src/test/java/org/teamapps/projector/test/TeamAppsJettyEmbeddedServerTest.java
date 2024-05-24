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
package org.teamapps.projector.test;

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.projector.components.calendar.Calendar;
import org.teamapps.projector.components.infinitescroll.table.ListTableModel;
import org.teamapps.projector.components.infinitescroll.table.Table;
import org.teamapps.projector.components.trivial.combobox.ComboBox;
import org.teamapps.server.jetty.embedded.TeamAppsJettyEmbeddedServer;
import org.teamapps.projector.components.core.dummy.DummyComponent;
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.projector.components.core.flexcontainer.VerticalLayout;
import org.teamapps.projector.components.core.rootpanel.RootPanel;
import org.teamapps.projector.components.core.window.Window;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.webcontroller.WebController;

import java.util.List;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {

		WebController controller = (SessionContext sessionContext) -> {
			RootPanel rootPanel = sessionContext.addRootPanel();
			DummyComponent content = new DummyComponent();
			content.onClick.addListener((eventData, disposable) -> {
				System.out.println("Clicked!");
				disposable.dispose();
			});
			VerticalLayout verticalLayout = new VerticalLayout();
			verticalLayout.addComponent(content);
			Button<?> button = Button.create(MaterialIcon.CLOSED_CAPTION, "re-register");
			button.onClicked.addListener(() -> {
				System.out.println("button clicked");
				content.onClick.addListener((e, d) -> {
					System.out.println("dummy component clicked!");
					d.dispose();
				});
			});
			verticalLayout.addComponent(button);
			verticalLayout.addComponentFillRemaining(Calendar.builder().build());
			Table<Object> table = Table.builder().build();
			table.addColumn("x", "Hello", new TextField()).setValueExtractor(x -> "Hey!");
			table.setModel(new ListTableModel<>(List.of("a", "", "")));
			verticalLayout.addComponentFillRemaining(table);

			verticalLayout.addComponent(new ComboBox<>(query -> List.of("a", "b")));
//			verticalLayout.addComponent(new TagComboBox<>(query -> List.of("a", "b")));
//			verticalLayout.addComponent(new CurrencyField(List.of(CurrencyUnit.forCode("EUR"), CurrencyUnit.forCode("USD"))));
//			verticalLayout.addComponent(new LocalDateTimeField());
//			verticalLayout.addComponent(new InstantDateTimeField());
//			verticalLayout.addComponent(new LocalDateField());
//			verticalLayout.addComponent(new LocalTimeField());
//			verticalLayout.addComponent(new Tree<>(new AbstractTreeModel<String>() {
//				@Override
//				public List<String> getRecords() {
//					return List.of("a", "b");
//				}
//			}));

			rootPanel.setContent(verticalLayout);

			sessionContext.onGlobalKeyEventOccurred.addListener((eventData, disposable) -> {
				System.out.println(eventData);
			});


			Window window = new Window(null, "asdf", null, 300, 300, true, true, true);
			window.enableAutoHeight();
			window.setMaximizable(true);
			window.setContent(new DummyComponent());
			window.show();
		};

		TeamAppsJettyEmbeddedServer jettyServer = TeamAppsJettyEmbeddedServer.builder(controller)
				.withPort(8082)
				.build();
//		System.out.println(jettyServer.getTeamAppsCore().getComponentLibraryRegistry().registerComponentLibrary(new CoreComponentLibrary()));
		jettyServer.start();
	}

}
