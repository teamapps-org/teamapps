package org.teamapps.uisession.statistics.app;

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.layout.ExtendedLayout;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;

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
