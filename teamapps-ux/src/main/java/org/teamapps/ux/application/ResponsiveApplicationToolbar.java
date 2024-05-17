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
package org.teamapps.ux.application;

import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.toolbar.AbstractToolContainer;
import org.teamapps.ux.component.toolbar.ToolAccordion;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.util.ArrayList;
import java.util.List;

public class ResponsiveApplicationToolbar implements ApplicationChangeHandler {

    private AbstractToolContainer toolbar;

    public ResponsiveApplicationToolbar() {
        SessionContext context = CurrentSessionContext.get();
        if (context.getClientInfo().isMobileDevice()) {
            toolbar = new ToolAccordion();
        } else {
            toolbar = new Toolbar();
        }
    }

    public AbstractToolContainer getToolbar() {
        return toolbar;
    }

    @Override
    public void handleApplicationViewAdded(ResponsiveApplication application, View view) {
        //todo check if already in toolbar
        view.getWorkspaceButtonGroups().forEach(group -> toolbar.addButtonGroup(group));
    }

    @Override
    public void handleApplicationViewRemoved(ResponsiveApplication application, View view) {
        view.getWorkspaceButtonGroups().forEach(group -> toolbar.removeToolbarButtonGroup(group));
    }

    @Override
    public void handlePerspectiveChange(ResponsiveApplication application, Perspective perspective, Perspective previousPerspective, List<View> activeViews, List<View> addedViews, List<View> removedViews) {
        List<ToolbarButtonGroup> addGroups = new ArrayList<>();
        List<ToolbarButtonGroup> removeGroups = new ArrayList<>();

        addedViews.forEach(view -> addGroups.addAll(view.getWorkspaceButtonGroups()));
        addGroups.addAll(perspective.getWorkspaceButtonGroups());

        if (previousPerspective != null) {
            removedViews.forEach(view -> removeGroups.addAll(view.getWorkspaceButtonGroups()));
            removeGroups.addAll(previousPerspective.getWorkspaceButtonGroups());
            removeGroups.removeAll(addGroups);
        }

        removeGroups.stream().distinct().forEach(group -> toolbar.removeToolbarButtonGroup(group));
        addGroups.stream().distinct().forEach(group -> toolbar.addButtonGroup(group));
    }

    @Override
    public void handleApplicationToolbarButtonGroupAdded(ResponsiveApplication application, ToolbarButtonGroup buttonGroup) {
        toolbar.addButtonGroup(buttonGroup);
    }

    @Override
    public void handleApplicationToolbarButtonGroupRemoved(ResponsiveApplication application, ToolbarButtonGroup buttonGroup) {
        toolbar.removeToolbarButtonGroup(buttonGroup);
    }

    @Override
    public void handleLayoutChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, LayoutItemDefinition layout) {
        //todo check removed views through layout change...
    }

    @Override
    public void handleViewAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view) {
        if (isActivePerspective) {
            //todo check if already in toolbar
            view.getWorkspaceButtonGroups().forEach(group -> toolbar.addButtonGroup(group));
        }
    }

    @Override
    public void handleViewRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view) {
        if (isActivePerspective) {
            view.getWorkspaceButtonGroups().forEach(group -> toolbar.removeToolbarButtonGroup(group));
        }
    }

    @Override
    public void handlePerspectiveToolbarButtonGroupAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, ToolbarButtonGroup buttonGroup) {
        if (isActivePerspective) {
            toolbar.addButtonGroup(buttonGroup);
        }
    }

    @Override
    public void handlePerspectiveToolbarButtonGroupRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, ToolbarButtonGroup buttonGroup) {
        if (isActivePerspective) {
            toolbar.removeToolbarButtonGroup(buttonGroup);
        }
    }

    @Override
    public void handleViewVisibilityChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, boolean visible) {
        if (isActivePerspective) {
            if (visible) {
                view.getWorkspaceButtonGroups().forEach(group -> toolbar.addButtonGroup(group));
            } else {
                view.getWorkspaceButtonGroups().forEach(group -> toolbar.removeToolbarButtonGroup(group));
            }
        }
    }

    @Override
    public void handleViewFocusRequest(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, boolean ensureVisible) {

    }

    @Override
    public void handleViewSizeChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ViewSize viewSize) {

    }

    @Override
    public void handleViewTabTitleChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, String title) {

    }

    @Override
    public void handleViewLayoutPositionChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, String position) {

    }

    @Override
    public void handleViewWorkspaceToolbarButtonGroupAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ToolbarButtonGroup buttonGroup) {
        if (isActivePerspective) {
            toolbar.addButtonGroup(buttonGroup);
        }
    }

    @Override
    public void handleViewWorkspaceToolbarButtonGroupRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ToolbarButtonGroup buttonGroup) {
        if (isActivePerspective) {
            toolbar.removeToolbarButtonGroup(buttonGroup);
        }
    }

    @Override
    public void handleViewSelect(View view) {

    }
}
