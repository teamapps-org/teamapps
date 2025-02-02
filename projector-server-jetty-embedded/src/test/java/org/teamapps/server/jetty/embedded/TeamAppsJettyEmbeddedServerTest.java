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
import org.teamapps.projector.ClosedSessionHandlingType;
import org.teamapps.projector.component.core.dummy.DummyComponent;
import org.teamapps.projector.component.core.field.Button;
import org.teamapps.projector.component.core.flexcontainer.VerticalLayout;
import org.teamapps.projector.component.core.rootpanel.RootPanel;
import org.teamapps.projector.session.SessionContext;
import org.teamapps.server.webcontroller.WebController;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TeamAppsJettyEmbeddedServerTest {

	public static void main(String[] args) throws Exception {

		WebController controller = (SessionContext sessionContext) -> {
			RootPanel rootPanel = new RootPanel();
			sessionContext.addRootComponent(rootPanel);
			DummyComponent dummyComponent = new DummyComponent();
			dummyComponent.onClick.addListener((eventData, disposable) -> {
				System.out.println("Click!");
				disposable.dispose();
			});
			VerticalLayout verticalLayout = new VerticalLayout();
			verticalLayout.addComponent(dummyComponent);
			Button button = Button.create(MaterialIcon.CLOSED_CAPTION, "re-register");
			button.onClick.addListener(() -> {
				System.out.println("button click");
				dummyComponent.onClick.addListener((e, d) -> {
					System.out.println("dummy component click!");
					d.dispose();
				});
			});
			verticalLayout.addComponent(button);

			rootPanel.setContent(verticalLayout);

			sessionContext.subscribeToGlobalKeyEvents(true, false, false, false, false, true, true, System.out::println);



			sessionContext.setClosedSessionHandling(ClosedSessionHandlingType.MESSAGE_WINDOW);

			Executors.newSingleThreadScheduledExecutor().schedule(() -> sessionContext.runWithContext(() -> {
				throw new RuntimeException();
			}), 1, TimeUnit.SECONDS);
		};

		TeamAppsJettyEmbeddedServer jettyServer = TeamAppsJettyEmbeddedServer.builder(controller)
				.withPort(8082)
				.build();
//		System.out.println(jettyServer.getTeamAppsCore().getComponentLibraryRegistry().registerComponentLibrary(new CoreComponentLibrary()));
		jettyServer.start();
	}

}
