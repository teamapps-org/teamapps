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
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.layout.StandardLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.rootpanel.RootPanel;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.webcontroller.WebController;

import java.util.List;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<MaterialIcon> ALL_ICONS = List.copyOf(MaterialIcon.getAllIcons());

	public static void main(String[] args) throws Exception {
		WebController controller = sessionContext -> {
			RootPanel rootPanel = new RootPanel();
			sessionContext.addRootPanel(null, rootPanel);

			//create a responsive application that will run on desktops as well as on smart phones
			ResponsiveApplication application = ResponsiveApplication.createApplication();

			//create perspective with default layout
			Perspective perspective = Perspective.createPerspective();
			application.addPerspective(perspective);

			//create an empty left panel
			perspective.addView(View.createView(StandardLayout.LEFT, MaterialIcon.MESSAGE, "Left panel", null));

			//create a tabbed center panel
			perspective.addView(View.createView(StandardLayout.CENTER, MaterialIcon.SEARCH, "Center panel", null));
			perspective.addView(View.createView(StandardLayout.CENTER, MaterialIcon.PEOPLE, "Center panel 2", null));

			//create a right panel
			perspective.addView(View.createView(StandardLayout.RIGHT, MaterialIcon.FOLDER, "Left panel", null));

			//create a right bottom panel
			perspective.addView(View.createView(StandardLayout.RIGHT_BOTTOM, MaterialIcon.VIEW_CAROUSEL, "Left bottom panel", null));

			//create toolbar buttons
			ToolbarButtonGroup buttonGroup = new ToolbarButtonGroup();
			buttonGroup.addButton(ToolbarButton.create(MaterialIcon.SAVE, "Save", "Save changes")).onClick.addListener(toolbarButtonClickEvent -> {
				sessionContext.showNotification(MaterialIcon.MESSAGE, "Save was clicked!");
			});
			buttonGroup.addButton(ToolbarButton.create(MaterialIcon.DELETE, "Delete", "Delete some items"));

			//display these buttons only when this perspective is visible
			perspective.addWorkspaceButtonGroup(buttonGroup);

			application.showPerspective(perspective);
			rootPanel.setContent(application.getUi());

			// set Background Image
			String defaultBackground = "/resources/backgrounds/default-bl.jpg";
			sessionContext.registerBackgroundImage("default", defaultBackground, defaultBackground);
			sessionContext.setBackgroundImage("default", 0);
		};

		TeamAppsJettyEmbeddedServer.builder(controller)
				.setPort(8082)
				.build()
				.start();
	}


}
