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
import org.teamapps.ux.component.field.Button;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.field.combobox.ComboBox;
import org.teamapps.ux.component.flexcontainer.VerticalLayout;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.window.Window;
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.Locale;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<MaterialIcon> ALL_ICONS = List.copyOf(MaterialIcon.getAllIcons());

	public static void main(String[] args) throws Exception {
		TeamAppsJettyEmbeddedServer.builder((SessionContext sessionContext) -> {
					sessionContext.setLocale(Locale.US);


					RootPanel rootPanel = sessionContext.addRootPanel();

					Window window = new Window();
					window.setTitle("Window");
					window.enableAutoHeight();

					TextField textField = new TextField();

					ComboBox<Object> combobox = new ComboBox<>();

					combobox.focus();
					textField.focus();

					VerticalLayout verticalLayout = new VerticalLayout();
					verticalLayout.addComponent(textField);

					window.setContent(verticalLayout);

					Button<BaseTemplateRecord> button = Button.create("show");
					button.onClicked.addListener((eventData, disposable) -> {
						window.show();

						new Thread(() -> {
							try {
								Thread.sleep(1000L);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
							sessionContext.runWithContext(() -> {
								verticalLayout.addComponent(combobox);
							});
						}).start();
					});
					rootPanel.setContent(button);




				})
				.setPort(8082)
				.build()
				.start();
	}

}
