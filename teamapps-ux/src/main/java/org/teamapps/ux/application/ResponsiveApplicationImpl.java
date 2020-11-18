/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import org.teamapps.ux.application.assembler.ApplicationAssembler;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.perspective.PerspectiveChangeHandler;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResponsiveApplicationImpl implements ResponsiveApplication {

	private final ApplicationAssembler assembler;
	private Component ui;

	private List<View> applicationViews = new ArrayList<>();
	private List<Perspective> perspectives = new ArrayList<>();
	private Perspective activePerspective;
	private List<View> activeViews;
	private List<ToolbarButtonGroup> workspaceToolbarButtonGroups = new ArrayList<>();
	private List<ApplicationChangeHandler> changeHandlers = new ArrayList<>();
	private ResponsiveApplicationToolbar responsiveApplicationToolbar = new ResponsiveApplicationToolbar();

	public ResponsiveApplicationImpl(ApplicationAssembler assembler) {
		this.assembler = assembler;
		this.assembler.setWorkSpaceToolbar(responsiveApplicationToolbar);
		addApplicationChangeHandler(assembler);
		addApplicationChangeHandler(responsiveApplicationToolbar);
	}


	protected Component createUi() {
		return assembler.createApplication(this);
	}

	@Override
	public Component getUi() {
		if (ui == null) {
			ui = createUi();
		}
		return ui;
	}

	@Override
	public void addApplicationChangeHandler(ApplicationChangeHandler changeHandler) {
		changeHandlers.remove(changeHandler);
		changeHandlers.add(changeHandler);
	}

	@Override
	public void removeApplicationChangeHandler(ApplicationChangeHandler changeHandler) {
		changeHandlers.remove(changeHandler);
	}

	@Override
	public void addApplicationView(View view) {
		applicationViews.add(view);
		changeHandlers.forEach(changeHandler -> changeHandler.handleApplicationViewAdded(this, view));
	}

	@Override
	public void removeApplicationView(View view) {
		applicationViews.remove(view);
		changeHandlers.forEach(changeHandler -> changeHandler.handleApplicationViewRemoved(this, view));
	}

	@Override
	public List<View> getApplicationViews() {
		return applicationViews;
	}

	@Override
	public List<Perspective> getPerspectives() {
		return perspectives;
	}

	@Override
	public Perspective addPerspective(Perspective perspective) {
		ResponsiveApplication application = this;
		perspectives.add(perspective);
		perspective.addPerspectiveChangeHandler(new PerspectiveChangeHandler() {
			@Override
			public void handleLayoutChange(Perspective perspective, LayoutItemDefinition layoutItemDefinition) {
				changeHandlers.forEach(changeHandler -> changeHandler.handleLayoutChange(application, isActivePerspective(perspective), perspective, layoutItemDefinition));
			}

			@Override
			public void handleViewAdded(Perspective perspective, View view) {
				if (isActivePerspective(perspective)) {
					checkViewChange(view, true);
				}
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewAdded(application, isActivePerspective(perspective), perspective, view));
			}

			@Override
			public void handleViewRemoved(Perspective perspective, View view) {
				if (isActivePerspective(perspective)) {
					checkViewChange(view, false);
				}
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewRemoved(application, isActivePerspective(perspective), perspective, view));
			}

			@Override
			public void handlePerspectiveToolbarButtonGroupAdded(Perspective perspective, ToolbarButtonGroup buttonGroup) {
				changeHandlers.forEach(changeHandler -> changeHandler.handlePerspectiveToolbarButtonGroupAdded(application, isActivePerspective(perspective), perspective, buttonGroup));
			}

			@Override
			public void handlePerspectiveToolbarButtonGroupRemoved(Perspective perspective, ToolbarButtonGroup buttonGroup) {
				changeHandlers.forEach(changeHandler -> changeHandler.handlePerspectiveToolbarButtonGroupRemoved(application, isActivePerspective(perspective), perspective, buttonGroup));
			}

			@Override
			public void handleViewVisibilityChange(Perspective perspective, View view, boolean visible) {
				if (isActivePerspective(perspective)) {
					checkViewChange(view, visible);
				}
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewVisibilityChange(application, isActivePerspective(perspective), perspective, view, visible));
			}

			@Override
			public void handleViewFocusRequest(Perspective perspective, View view, boolean ensureVisible) {
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewFocusRequest(application, isActivePerspective(perspective), perspective, view, ensureVisible));
			}

			@Override
			public void handleViewSizeChange(Perspective perspective, View view, ViewSize viewSize) {
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewSizeChange(application, isActivePerspective(perspective), perspective, view, viewSize));
			}

			@Override
			public void handleViewTabTitleChange(Perspective perspective, View view, String title) {
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewTabTitleChange(application, isActivePerspective(perspective), perspective, view, title));
			}

			@Override
			public void handleViewLayoutPositionChange(Perspective perspective, View view, String position) {
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewLayoutPositionChange(application, isActivePerspective(perspective), perspective, view, position));
			}

			@Override
			public void handleViewWorkspaceToolbarButtonGroupAdded(Perspective perspective, View view, ToolbarButtonGroup buttonGroup) {
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewWorkspaceToolbarButtonGroupAdded(application, isActivePerspective(perspective), perspective, view, buttonGroup));
			}

			@Override
			public void handleViewWorkspaceToolbarButtonGroupRemoved(Perspective perspective, View view, ToolbarButtonGroup buttonGroup) {
				changeHandlers.forEach(changeHandler -> changeHandler.handleViewWorkspaceToolbarButtonGroupRemoved(application, isActivePerspective(perspective), perspective, view, buttonGroup));
			}

		});
		return perspective;
	}

	private void checkViewChange(View view, boolean show) {
		if (activeViews != null) {
			if (show) {
				activeViews.add(view);
			} else {
				activeViews.remove(view);
			}
		}
	}

	private boolean isActivePerspective(Perspective perspective) {
		if (activePerspective != null && activePerspective.equals(perspective)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void showPerspective(Perspective perspective) {
		if (!perspectives.contains(perspective)) {
			addPerspective(perspective);
		}
		List<View> previousViews = activeViews;
		activeViews = perspective.getVisibleAndLayoutReferencedViews();

		List<View> removedViews = new ArrayList<>();
		List<View> addedViews = new ArrayList<>();

		if (previousViews != null) {
			Set<View> commonViews = new HashSet<>(activeViews);
			commonViews.retainAll(previousViews);
			addedViews.addAll(activeViews);
			addedViews.removeAll(commonViews);
			removedViews.addAll(previousViews);
			removedViews.removeAll(commonViews);
		} else {
			addedViews.addAll(activeViews);
		}

		Perspective previousPerspective = activePerspective;
		activePerspective = perspective;
		changeHandlers.forEach(changeHandler -> changeHandler.handlePerspectiveChange(this, perspective, previousPerspective, activeViews, addedViews, removedViews));
	}

	@Override
	public List<View> getActiveViews() {
		return activeViews;
	}

	@Override
	public Perspective getActivePerspective() {
		return activePerspective;
	}

	@Override
	public ToolbarButtonGroup addApplicationButtonGroup(ToolbarButtonGroup buttonGroup) {
		workspaceToolbarButtonGroups.add(buttonGroup);
		changeHandlers.forEach(changeHandler -> changeHandler.handleApplicationToolbarButtonGroupAdded(this, buttonGroup));
		return buttonGroup;
	}

	@Override
	public void removeApplicationButtonGroup(ToolbarButtonGroup buttonGroup) {
		workspaceToolbarButtonGroups.remove(buttonGroup);
		changeHandlers.forEach(changeHandler -> changeHandler.handleApplicationToolbarButtonGroupRemoved(this, buttonGroup));
	}

	@Override
	public List<ToolbarButtonGroup> getWorkspaceButtonGroups() {
		return workspaceToolbarButtonGroups;
	}

	@Override
	public void setToolbarBackgroundColor(Color backgroundColor) {
		responsiveApplicationToolbar.getToolbar().setBackgroundColor(backgroundColor);
	}

	@Override
	public MultiProgressDisplay getMultiProgressDisplay() {
		return assembler.getMultiProgressDisplay();
	}

}
