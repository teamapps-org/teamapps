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
package org.teamapps.ux.application.assembler;

import org.teamapps.ux.application.*;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

public interface ApplicationAssembler extends ApplicationChangeHandler {

    Component createApplication(ResponsiveApplication application);

    void setWorkSpaceToolbar(ResponsiveApplicationToolbar toolbar);

    MultiProgressDisplay getMultiProgressDisplay();

    @Override
    default void handleApplicationToolbarButtonGroupAdded(ResponsiveApplication application, ToolbarButtonGroup buttonGroup) {

    }

    @Override
    default void handleApplicationToolbarButtonGroupRemoved(ResponsiveApplication application, ToolbarButtonGroup buttonGroup) {

    }

    @Override
    default void handlePerspectiveToolbarButtonGroupAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, ToolbarButtonGroup buttonGroup) {

    }

    @Override
    default void handlePerspectiveToolbarButtonGroupRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, ToolbarButtonGroup buttonGroup) {

    }

    @Override
    default void handleViewWorkspaceToolbarButtonGroupAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ToolbarButtonGroup buttonGroup) {

    }

    @Override
    default void handleViewWorkspaceToolbarButtonGroupRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ToolbarButtonGroup buttonGroup) {

    }
}
