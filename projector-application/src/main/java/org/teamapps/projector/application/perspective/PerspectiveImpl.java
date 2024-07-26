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
package org.teamapps.projector.application.perspective;

import org.teamapps.projector.application.view.View;
import org.teamapps.projector.application.view.ViewChangeHandler;
import org.teamapps.projector.application.view.ViewSize;
import org.teamapps.projector.component.essential.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.component.workspacelayout.definition.LayoutItemDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PerspectiveImpl implements Perspective {

    private List<View> views = new ArrayList<>();
    private List<ToolbarButtonGroup> workspaceToolbarButtonGroups = new ArrayList<>();
    private List<PerspectiveChangeHandler> changeHandlers = new ArrayList<>();
    private LayoutItemDefinition layout;
    private View focusedView;

    public PerspectiveImpl(LayoutItemDefinition layout) {
        this.layout = layout;
    }

    @Override
    public void addPerspectiveChangeHandler(PerspectiveChangeHandler changeHandler) {
        changeHandlers.add(changeHandler);
    }

    @Override
    public void removePerspectiveChangeHandler(PerspectiveChangeHandler changeHandler) {
        changeHandlers.remove(changeHandler);
    }

    @Override
    public LayoutItemDefinition getLayout() {
        return layout;
    }

    @Override
    public void setLayout(LayoutItemDefinition layout) {
        this.layout = layout;
        changeHandlers.forEach(changeHandler -> changeHandler.handleLayoutChange(this, layout));
    }

    @Override
    public List<View> getViews() {
        return views;
    }

    @Override
    public List<View> getVisibleViews() {
        return views.stream()
                .filter(view -> view.isVisible())
                .collect(Collectors.toList());
    }

    @Override
    public List<View> getVisibleAndLayoutReferencedViews() {
        Set<String> layoutPositions = layout.getAllLayoutPositions();
        return views.stream()
                .filter(view -> view.isVisible())
                .filter(view -> layoutPositions.contains(view.getLayoutPosition()))
                .collect(Collectors.toList());
    }

    @Override
    public void setFocusedView(View view) {
        view.focus();
        this.focusedView = view;
    }

    @Override
    public View getFocusedView() {
        return focusedView;
    }

    @Override
    public ToolbarButtonGroup addWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup) {
        workspaceToolbarButtonGroups.add(buttonGroup);
        changeHandlers.forEach(changeHandler -> changeHandler.handlePerspectiveToolbarButtonGroupAdded(this, buttonGroup));
        return buttonGroup;
    }

    @Override
    public void removeWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup) {
        workspaceToolbarButtonGroups.remove(buttonGroup);
        changeHandlers.forEach(changeHandler -> changeHandler.handlePerspectiveToolbarButtonGroupRemoved(this, buttonGroup));
    }

    @Override
    public List<ToolbarButtonGroup> getWorkspaceButtonGroups() {
        return workspaceToolbarButtonGroups;
    }

    @Override
    public View addView(View view) {
        views.add(view);
        Perspective perspective = this;
        view.addViewChangeHandler(new ViewChangeHandler() {
            @Override
            public void handleVisibilityChange(boolean visible) {
                changeHandlers.forEach(changeHandler -> changeHandler.handleViewVisibilityChange(perspective, view, visible));
            }

            @Override
            public void handleViewFocusRequest(boolean ensureVisible) {
                focusedView = view;
                changeHandlers.forEach(changeHandler -> changeHandler.handleViewFocusRequest(perspective, view, ensureVisible));
            }

            @Override
            public void handleViewSizeChange(ViewSize viewSize) {
                changeHandlers.forEach(changeHandler -> changeHandler.handleViewSizeChange(perspective, view, viewSize));
            }

            @Override
            public void handleViewTabTitleChange(String title) {
                changeHandlers.forEach(changeHandler -> changeHandler.handleViewTabTitleChange(perspective, view, title));
            }

            @Override
            public void handleLayoutPositionChange(String position) {
                changeHandlers.forEach(changeHandler -> changeHandler.handleViewLayoutPositionChange(perspective, view, position));
            }

            @Override
            public void handleWorkspaceButtonGroupAdded(ToolbarButtonGroup buttonGroup) {
                changeHandlers.forEach(changeHandler -> changeHandler.handleViewWorkspaceToolbarButtonGroupAdded(perspective, view, buttonGroup));
            }

            @Override
            public void handleWorkspaceButtonGroupRemoved(ToolbarButtonGroup buttonGroup) {
                changeHandlers.forEach(changeHandler -> changeHandler.handleViewWorkspaceToolbarButtonGroupRemoved(perspective, view, buttonGroup));
            }
        });
        changeHandlers.forEach(changeHandler -> changeHandler.handleViewAdded(this, view));
        return view;
    }

    @Override
    public void removeView(View view) {
        views.remove(view);
        changeHandlers.forEach(changeHandler -> changeHandler.handleViewRemoved(this, view));
    }
}
