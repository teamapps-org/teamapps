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
package org.teamapps.ux.application.view;

import org.teamapps.common.format.Color;
import org.teamapps.icons.api.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewImpl implements View {


    private final boolean closable;
    private boolean visible = true;
    private String layoutPosition;
    private ViewSize viewSize;
    private String tabTitle;

    private final Panel panel = new Panel();
    private Toolbar toolbar;
    private final List<ToolbarButtonGroup> workspaceToolbarButtonGroups = new ArrayList<>();
    private final List<ViewChangeHandler> changeHandlers = new ArrayList<>();

    public ViewImpl() {
        closable = false;
    }

    public ViewImpl(String layoutPosition) {
        this.layoutPosition = layoutPosition;
        closable = false;
    }

    public ViewImpl(Icon icon, String title, Component component) {
        this(null, icon, title, component);
    }

    public ViewImpl(String layoutPosition, Icon icon, String title, Component component) {
        this(layoutPosition, icon, title, component, false);
    }

    public ViewImpl(String layoutPosition, Icon icon, String title, Component component, boolean closable) {
        this.layoutPosition = layoutPosition;
        panel.setIcon(icon);
        panel.setTitle(title);
        panel.setContent(component);
        this.closable = closable;
    }


    @Override
    public void addViewChangeHandler(ViewChangeHandler viewChangeHandler) {
        changeHandlers.add(viewChangeHandler);
    }

    @Override
    public void removeViewChangeHandler(ViewChangeHandler viewChangeHandler) {
        changeHandlers.remove(viewChangeHandler);
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        } else {
            this.visible = visible;
            changeHandlers.forEach(changeHandler -> changeHandler.handleVisibilityChange(visible));
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void focus() {
        focus(false);
    }

    @Override
    public void focus(boolean ensureVisible) {
        changeHandlers.forEach(changeHandler -> changeHandler.handleViewFocusRequest(ensureVisible));
    }

    @Override
    public ViewSize getCustomViewSize() {
        return viewSize;
    }

    @Override
    public void setSize(ViewSize viewSize) {
        this.viewSize = viewSize;
        changeHandlers.forEach(changeHandler -> changeHandler.handleViewSizeChange(viewSize));
    }

    @Override
    public ToolbarButtonGroup addLocalButtonGroup(ToolbarButtonGroup buttonGroup) {
        checkToolbar();
        toolbar.addButtonGroup(buttonGroup);
        return buttonGroup;
    }

    @Override
    public void removeLocalButtonGroup(ToolbarButtonGroup buttonGroup) {
        toolbar.removeToolbarButtonGroup(buttonGroup);
    }

    @Override
    public List<ToolbarButtonGroup> getLocalButtonGroups() {
        return toolbar.getToolbarButtonGroups();
    }

    private void checkToolbar() {
        if (toolbar == null) {
            toolbar = new Toolbar();
            panel.setToolbar(toolbar);
        }
    }

    @Override
    public ToolbarButtonGroup addWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup) {
        workspaceToolbarButtonGroups.add(buttonGroup);
        changeHandlers.forEach(changeHandler -> changeHandler.handleWorkspaceButtonGroupAdded(buttonGroup));
        return buttonGroup;
    }

    @Override
    public void removeWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup) {
        workspaceToolbarButtonGroups.remove(buttonGroup);
        changeHandlers.forEach(changeHandler -> changeHandler.handleWorkspaceButtonGroupRemoved(buttonGroup));
    }

    @Override
    public List<ToolbarButtonGroup> getWorkspaceButtonGroups() {
        return workspaceToolbarButtonGroups;
    }

    @Override
    public void setComponent(Component component) {
        panel.setContent(component);
    }

    @Override
    public Component getComponent() {
        return panel.getContent();
    }

    @Override
    public Panel getPanel() {
        return panel;
    }

    @Override
    public boolean isClosable() {
        return closable;
    }

    @Override
    public void setLayoutPosition(String position) {
        this.layoutPosition = position;
        changeHandlers.forEach(changeHandler -> changeHandler.handleLayoutPositionChange(position));
    }

    @Override
    public String getLayoutPosition() {
        return layoutPosition;
    }

    @Override
    public void setTitle(String title) {
        panel.setTitle(title);
    }

    @Override
    public String getTitle() {
        return panel.getTitle();
    }

    @Override
    public void setTabTitle(String title) {
        this.tabTitle = title;
        changeHandlers.forEach(changeHandler -> changeHandler.handleViewTabTitleChange(title));
    }

    @Override
    public String getTabTitle() {
        return tabTitle;
    }

    @Override
    public void setLocalToolbarBackgroundColor(Color color) {
        checkToolbar();
        toolbar.setBackgroundColor(color);
    }
}
