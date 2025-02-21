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
import org.teamapps.projector.component.filefield.imagecropper.ImageCropper;
import org.teamapps.projector.icon.Icon;
import org.teamapps.projector.server.webcontroller.WebController;
import org.teamapps.projector.session.SessionContext;

public class ProjectorJettyEmbeddedServerTest {

	private static record User(String name, Icon icon) { }

	public static void main(String[] args) throws Exception {

		WebController controller = (SessionContext sessionContext) -> {
			RootPanel rootPanel = new RootPanel();
			rootPanel.setCssStyle("border", "20px solid black");
			sessionContext.addRootComponent(rootPanel);

			ImageCropper imageCropper = new ImageCropper();
			imageCropper.setImageUrl("https://307a6ed092846b809be7-9cfa4cf7c673a59966ad8296f4c88804.ssl.cf3.rackcdn.com/Data-Uri-Chrome/Chrome-Data-Uri-Data-URL.png");


			rootPanel.setContent(imageCropper);
		};

		ProjectorJettyEmbeddedServer jettyServer = ProjectorJettyEmbeddedServer.builder(controller)
				.withPort(8082)
				.build();
		jettyServer.start();
	}

}
