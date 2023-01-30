/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.ux.application.perspective;

import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.layout.ExtendedLayout;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;

import java.util.List;

public interface Perspective {

    static Perspective createPerspective() {
        return new PerspectiveImpl(ExtendedLayout.createLayout());
    }

    static Perspective createPerspective(LayoutItemDefinition layout) {
        return new PerspectiveImpl(layout);
    }
    void addPerspectiveChangeHandler(PerspectiveChangeHandler changeHandler);

    void removePerspectiveChangeHandler(PerspectiveChangeHandler changeHandler);

    LayoutItemDefinition getLayout();

    void setLayout(LayoutItemDefinition layout);

    default void addViews(View ... views) {
        for (View view : views) {
            addView(view);
        }
    }

    View addView(View view);

    default void addView(View view, String position) {
        addView(view);
        view.setLayoutPosition(position);
    }

    void removeView(View view);

    List<View> getViews();

    List<View> getVisibleViews();

    List<View> getVisibleAndLayoutReferencedViews();

    ToolbarButtonGroup addWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup);

    void removeWorkspaceButtonGroup(ToolbarButtonGroup buttonGroup);

    List<ToolbarButtonGroup> getWorkspaceButtonGroups();

    default void setFocusedView(View view) {
        view.focus();
    }

    View getFocusedView();

    default void setViewPosition(View view, String position) {
        view.setLayoutPosition(position);
    }

    default void setViewSize(View view, ViewSize viewSize) {
        view.setSize(viewSize);
    }

}
