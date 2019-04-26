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

import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;

import java.util.List;

public interface ApplicationChangeHandler {

    void handleApplicationViewAdded(ResponsiveApplication application, View view);

    void handleApplicationViewRemoved(ResponsiveApplication application, View view);

    void handlePerspectiveChange(ResponsiveApplication application, Perspective perspective, Perspective previousPerspective, List<View> activeViews, List<View> addedViews, List<View> removedViews);

    void handleApplicationToolbarButtonGroupAdded(ResponsiveApplication application, ToolbarButtonGroup buttonGroup);

    void handleApplicationToolbarButtonGroupRemoved(ResponsiveApplication application, ToolbarButtonGroup buttonGroup);

    void handleLayoutChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, LayoutItemDefinition layout);

    void handleViewAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view);

    void handleViewRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view);

    void handlePerspectiveToolbarButtonGroupAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, ToolbarButtonGroup buttonGroup);

    void handlePerspectiveToolbarButtonGroupRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, ToolbarButtonGroup buttonGroup);

    void handleViewVisibilityChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, boolean visible);

    void handleViewFocusRequest(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, boolean ensureVisible);

    void handleViewSizeChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ViewSize viewSize);

    void handleViewTabTitleChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, String title);

    void handleViewLayoutPositionChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, String position);

    void handleViewWorkspaceToolbarButtonGroupAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ToolbarButtonGroup buttonGroup);

    void handleViewWorkspaceToolbarButtonGroupRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ToolbarButtonGroup buttonGroup);

}
