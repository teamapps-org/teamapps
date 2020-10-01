/*
 * Copyright (C) 2014 - 2020 TeamApps.org
 *
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
 */
package org.teamapps.ux.application.perspective;

import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;

public interface PerspectiveChangeHandler {


    void handleLayoutChange(Perspective perspective, LayoutItemDefinition layoutItemDefinition);

    void handleViewAdded(Perspective perspective, View view);

    void handleViewRemoved(Perspective perspective, View view);

    void handlePerspectiveToolbarButtonGroupAdded(Perspective perspective, ToolbarButtonGroup buttonGroup);

    void handlePerspectiveToolbarButtonGroupRemoved(Perspective perspective, ToolbarButtonGroup buttonGroup);

    void handleViewVisibilityChange(Perspective perspective, View view, boolean visible);

    void handleViewFocusRequest(Perspective perspective, View view, boolean ensureVisible);

    void handleViewSizeChange(Perspective perspective, View view, ViewSize viewSize);

    void handleViewTabTitleChange(Perspective perspective, View view, String title);

    void handleViewLayoutPositionChange(Perspective perspective, View view, String position);

    void handleViewWorkspaceToolbarButtonGroupAdded(Perspective perspective, View view, ToolbarButtonGroup buttonGroup);

    void handleViewWorkspaceToolbarButtonGroupRemoved(Perspective perspective, View view, ToolbarButtonGroup buttonGroup);


}
