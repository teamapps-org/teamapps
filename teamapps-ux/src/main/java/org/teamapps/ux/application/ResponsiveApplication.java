/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.application;

import org.teamapps.common.format.Color;
import org.teamapps.ux.application.assembler.DesktopApplicationAssembler;
import org.teamapps.ux.application.assembler.MobileApplicationAssembler;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.session.CurrentSessionContext;

import java.util.List;

public interface ResponsiveApplication extends Application {

	static ResponsiveApplication createApplication() {
		return createApplication(null);
	}

	static ResponsiveApplication createApplication(View applicationLauncher) {
		boolean mobileDevice = CurrentSessionContext.get().getClientInfo().isMobileDevice();
		if (mobileDevice) {
			MobileApplicationAssembler mobileAssembler = new MobileApplicationAssembler();
			mobileAssembler.setApplicationLauncher(applicationLauncher);
			return new ResponsiveApplicationImpl(mobileAssembler);
		} else {
			return new ResponsiveApplicationImpl(new DesktopApplicationAssembler());
		}
	}

	void addApplicationChangeHandler(ApplicationChangeHandler changeHandler);

	void removeApplicationChangeHandler(ApplicationChangeHandler changeHandler);

	void addApplicationView(View view);

	void removeApplicationView(View view);

	List<View> getApplicationViews();

	List<Perspective> getPerspectives();

	Perspective addPerspective(Perspective perspective);

	void showPerspective(Perspective perspective);

	Perspective getActivePerspective();

	List<View> getActiveViews();

	ToolbarButtonGroup addApplicationButtonGroup(ToolbarButtonGroup buttonGroup);

	void removeApplicationButtonGroup(ToolbarButtonGroup buttonGroup);

	List<ToolbarButtonGroup> getWorkspaceButtonGroups();

	void setToolbarBackgroundColor(Color backgroundColor);
}
