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
import org.teamapps.ux.session.SessionContext;

import java.util.List;
import java.util.stream.Stream;

public class TeamAppsJettyEmbeddedServerTest {

	public static final List<MaterialIcon> ALL_ICONS = List.copyOf(MaterialIcon.getAllIcons());

	public static void main(String[] args) throws Exception {
		TeamAppsJettyEmbeddedServer.builder((SessionContext sessionContext) -> {
					RootPanel rootPanel = new RootPanel();
					sessionContext.addRootPanel(null, rootPanel);

					//create a responsive application that will run on desktops as well as on smart phones
					ResponsiveApplication application = ResponsiveApplication.createApplication();

					Perspective perspectiveA = createPerspective("A");
					application.addPerspective(perspectiveA);
					application.showPerspective(perspectiveA);

					Perspective perspectiveB = createPerspective("B");
					application.addPerspective(perspectiveB);
					application.showPerspective(perspectiveB);

					ToolbarButtonGroup buttonGroup = new ToolbarButtonGroup();
					buttonGroup.addButton(ToolbarButton.create(MaterialIcon.SAVE, "Switch Perspective", "")).onClick.addListener(toolbarButtonClickEvent -> {
						if (application.getActivePerspective() == perspectiveA) {
							application.showPerspective(perspectiveB);
						} else {
							application.showPerspective(perspectiveA);
						}
					});
					application.addApplicationButtonGroup(buttonGroup);

					rootPanel.setContent(application.getUi());
				})
				.setPort(8082)
				.build()
				.start();
	}

	private static Perspective createPerspective(String prefix) {
		//create perspective with default layout
		Perspective perspective = Perspective.createPerspective();

		View leftPanel = View.createView(StandardLayout.LEFT, MaterialIcon.MESSAGE, prefix + " - Left panel", null);
		View centerPanel = View.createView(StandardLayout.CENTER, MaterialIcon.SEARCH, prefix + " - Center panel", null, true);
		View centerPanel2 = View.createView(StandardLayout.CENTER, MaterialIcon.PEOPLE, prefix + " - Center panel 2", null);
		View rightPanel = View.createView(StandardLayout.RIGHT, MaterialIcon.FOLDER, prefix + " - Right panel", null);
		View rightBottomPanel = View.createView(StandardLayout.RIGHT_BOTTOM, MaterialIcon.VIEW_CAROUSEL, prefix + " - Right bottom panel", null);

		Stream.of(leftPanel,
				centerPanel,
				centerPanel2,
				rightPanel,
				rightBottomPanel).forEach(view -> view.onEffectiveVisibilityChanged().addListener(aBoolean -> {
			System.out.println(view.getTitle() + " -> " + aBoolean);
		}));

		//create an empty left panel
		perspective.addView(leftPanel);

		//create a tabbed center panel
		perspective.addView(centerPanel);
		perspective.addView(centerPanel2);

		//create a right panel
		perspective.addView(rightPanel);

		//create a right bottom panel
		perspective.addView(rightBottomPanel);

		//create toolbar buttons
		ToolbarButtonGroup buttonGroup = new ToolbarButtonGroup();
		buttonGroup.addButton(ToolbarButton.create(MaterialIcon.SAVE, "Save", "Save changes")).onClick.addListener(toolbarButtonClickEvent -> {
			boolean visible = !centerPanel.isVisible();
			centerPanel.setVisible(visible);
			if (visible) {
				centerPanel.select();
			}
		});

		//display these buttons only when this perspective is visible
		perspective.addWorkspaceButtonGroup(buttonGroup);

		return perspective;
	}

}
