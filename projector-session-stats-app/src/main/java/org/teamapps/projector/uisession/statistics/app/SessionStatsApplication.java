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
package org.teamapps.projector.uisession.statistics.app;

import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.application.ResponsiveApplication;
import org.teamapps.projector.application.layout.ExtendedLayout;
import org.teamapps.projector.application.perspective.Perspective;
import org.teamapps.projector.application.view.View;

public class SessionStatsApplication {

	private final ResponsiveApplication responsiveApplication;

	public SessionStatsApplication(SessionStatsSharedBaseTableModel baseTableModel) {
		responsiveApplication = ResponsiveApplication.createApplication();
		Perspective perspective = Perspective.createPerspective();

		SessionStatsPerspective sessionStatsPerspective = new SessionStatsPerspective(baseTableModel);
		View listView = View.createView(MaterialIcon.LIST, "Sessions", sessionStatsPerspective.getTable());
		perspective.addView(listView, ExtendedLayout.CENTER);

		View sessionView = View.createView(MaterialIcon.LIST, "Session", sessionStatsPerspective.getDetailVerticalLayout());
		sessionView.addLocalButtonGroup(sessionStatsPerspective.getDetailsToolbarButtonGroup());
		perspective.addView(sessionView, ExtendedLayout.RIGHT);

		responsiveApplication.addPerspective(perspective);
		responsiveApplication.showPerspective(perspective);
	}

	public ResponsiveApplication getResponsiveApplication() {
		return responsiveApplication;
	}
}
