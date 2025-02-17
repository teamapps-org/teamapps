/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.application.view;

import org.teamapps.common.format.Color;
import org.teamapps.event.Event;
import org.teamapps.icons.Icon;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;

import java.util.List;

public interface View {

    Event<Boolean> onEffectiveVisibilityChanged();
    
    static View createView() {
        return new ViewImpl();
    }

    static View createView(String layoutPosition) {
        return new ViewImpl(layoutPosition);
    }

    static View createView(Icon icon, String title, Component component) {
        return new ViewImpl(icon, title, component);
    }

    static View createView(String layoutPosition, Icon icon, String title, Component component) {
        return new ViewImpl(layoutPosition, icon, title, component);
    }

    static View createView(String layoutPosition, Icon icon, String title, Component component, boolean closable) {
        return new ViewImpl(layoutPosition, icon, title, component, closable);
    }

    void addViewChangeHandler(ViewChangeHandler viewChangeHandler);

    void removeViewChangeHandler(ViewChangeHandler viewChangeHandler);

    void setVisible(boolean visible);

    boolean isVisible();

    void focus();

    void focus(boolean ensureVisible);

    ViewSize getCustomViewSize();

    void setSize(ViewSize viewSize);

    ToolbarButtonGroup addLocalButtonGroup(ToolbarButtonGroup buttonGroup);

    void removeLocalButtonGroup(ToolbarButtonGroup buttonGroup);

    List<ToolbarButtonGroup> getLocalButtonGroups();

    ToolbarButtonGroup addWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup);

    void removeWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup);

    List<ToolbarButtonGroup> getWorkspaceButtonGroups();

    void setComponent(Component component);

    Component getComponent();

    Panel getPanel();

    boolean isClosable();

    void setLayoutPosition(String position);

    String getLayoutPosition();

    void setTitle(String title);

    String getTitle();

    void setTabTitle(String title);

    String getTabTitle();

    void setLocalToolbarBackgroundColor(Color color);

    void select();
}
