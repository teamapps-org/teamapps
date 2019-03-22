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
package org.teamapps.ux.application.assembler;

import org.teamapps.icons.api.Icons;
import org.teamapps.ux.application.*;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.itemview.SimpleItemGroup;
import org.teamapps.ux.component.itemview.SimpleItemView;
import org.teamapps.ux.component.mobile.MobileLayout;
import org.teamapps.ux.component.mobile.MobileLayoutAnimation;
import org.teamapps.ux.component.mobile.NavigationBar;
import org.teamapps.ux.component.mobile.NavigationBarButton;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.toolbar.AbstractToolContainer;
import org.teamapps.ux.component.tree.Tree;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;

import java.util.ArrayList;
import java.util.List;

public class MobileApplicationAssembler implements ApplicationAssembler {

    private final NavigationBarButton<BaseTemplateRecord> applicationLauncherButton;
    private final NavigationBarButton<BaseTemplateRecord> applicationTreeButton;
    private final NavigationBarButton<BaseTemplateRecord> applicationViewsButton;
    private final NavigationBarButton<BaseTemplateRecord> applicationToolbarButton;
    private final NavigationBarButton<BaseTemplateRecord> applicationBackButton;

    private MobileLayout mobileLayout;
    private NavigationBar<BaseTemplateRecord> navigationBar;
    private SimpleItemView<Void> viewsItemView;
    private AbstractToolContainer mainToolbar;


    private View applicationLauncher;
    private View appTree;
    private List<View> applicationViews = new ArrayList<>();
    private List<View> perspectiveViews = new ArrayList<>();
    private List<View> viewHistory = new ArrayList<>();
    private View activeView;


    public MobileApplicationAssembler() {
        this.mobileLayout = new MobileLayout();
        this.navigationBar = new NavigationBar<>();
        this.viewsItemView = new SimpleItemView<>();

        navigationBar.preloadFanOutComponent(viewsItemView);
        mobileLayout.setNavigationBar(navigationBar);

        applicationLauncherButton = NavigationBarButton.create(Icons.WINDOW_EXPLORER);
        applicationTreeButton = NavigationBarButton.create(Icons.ELEMENTS_TREE);
        applicationViewsButton = NavigationBarButton.create(Icons.WINDOWS);
        applicationToolbarButton = NavigationBarButton.create(Icons.DROP_DOWN_LIST);
        applicationBackButton = NavigationBarButton.create(Icons.ARROW_LEFT);


        applicationLauncherButton.setVisible(false);
        applicationTreeButton.setVisible(false);

        navigationBar.addButton(applicationLauncherButton);
        navigationBar.addButton(applicationTreeButton);
        navigationBar.addButton(applicationViewsButton);
        navigationBar.addButton(applicationToolbarButton);
        navigationBar.addButton(applicationBackButton);

        applicationLauncherButton.onClick.addListener(aVoid -> {
            navigationBar.hideFanOutComponent();
            mobileLayout.showView(applicationLauncher.getPanel(), MobileLayoutAnimation.BACKWARD);
            activeView = applicationLauncher;
        });

        applicationTreeButton.onClick.addListener(aVoid -> {
            navigationBar.hideFanOutComponent();
            MobileLayoutAnimation animation = MobileLayoutAnimation.BACKWARD;
            if (activeView.equals(applicationLauncher)) {
                animation = MobileLayoutAnimation.FORWARD;
            }
            mobileLayout.showView(appTree.getPanel(), animation);
            activeView = appTree;
        });

        applicationViewsButton.onClick.addListener(aVoid -> {
            navigationBar.showOrHideFanoutComponent(viewsItemView);
        });

        applicationToolbarButton.onClick.addListener(aVoid -> {
            navigationBar.showOrHideFanoutComponent(mainToolbar);
        });

        applicationBackButton.onClick.addListener(aVoid -> {
            navigationBar.hideFanOutComponent();
            goBack();
        });

        viewsItemView.onItemClicked.addListener(data -> {
            //ScopeComponent component = data.getItem().getPayload();
            //component.refreshPanelToolbar();
            //displayComponent(component, MobileLayoutAnimation.FORWARD);
            navigationBar.hideFanOutComponent();
        });
    }

    public void setApplicationLauncher(View applicationLauncher) {
        if (applicationLauncher == null) {
            if (this.applicationLauncher != null) {
                mobileLayout.removeView(applicationLauncher.getPanel());
            }
            applicationLauncherButton.setVisible(false);
        } else {
            mobileLayout.preloadView(applicationLauncher.getPanel());
            applicationLauncherButton.setVisible(true);
        }
        this.applicationLauncher = applicationLauncher;
    }

    public void setApplicationTree(View appTree) {
        //view should be part of application views - this is just the explicit setting
        this.appTree = appTree;
        this.applicationTreeButton.setVisible(true);
    }

    public void showView(View view) {
        if (activeView != null) {
            //todo check when to add to history
            //viewHistory.add(view);
        }
        activeView = view;
        mobileLayout.showView(view.getPanel(), MobileLayoutAnimation.FORWARD);
    }

    public void showInitialView() {
        View view = null;
        if (!applicationViews.isEmpty()) {
            view = applicationViews.get(0);
        } else if (!perspectiveViews.isEmpty()){
            view = perspectiveViews.get(0);
        }
        if (view != null) {
            mobileLayout.showView(view.getPanel(), MobileLayoutAnimation.FORWARD);
            activeView = view;
        }
    }


    public void goBack() {
        View view = null;
        for (int i = 0; i < perspectiveViews.size(); i++) {
            if (perspectiveViews.get(i).equals(activeView)) {
                if (i > 0) {
                    view = perspectiveViews.get(i - 1);
                } else if (!applicationViews.isEmpty()) {
                    view = applicationViews.get(applicationViews.size() - 1);
                }
                break;
            }
        }

        if (view == null) {
            for (int i = 0; i < applicationViews.size(); i++) {
                if (applicationViews.get(i).equals(activeView)) {
                    if (i > 0) {
                        view = applicationViews.get(i - 1);
                    } else {
                        view = applicationLauncher;
                    }
                    break;
                }
            }
        }

        if (view != null) {
            activeView = view;
            mobileLayout.showView(activeView.getPanel(), MobileLayoutAnimation.BACKWARD);
        }
    }

    public void goForward() {
        //todo...
    }



    @Override
    public Component createApplication(ResponsiveApplication application) {
        return mobileLayout;
    }

    @Override
    public void setWorkSpaceToolbar(ResponsiveApplicationToolbar toolbar) {
        mainToolbar = toolbar.getToolbar();
        mainToolbar.onButtonClick.addListener(event -> {
            if (event.getDropDownButtonClickInfo() != null && !event.getDropDownButtonClickInfo().isOpening()) {
                navigationBar.hideFanOutComponent();
            }
        });
        navigationBar.preloadFanOutComponent(mainToolbar);
    }

    @Override
    public void handleApplicationViewAdded(ResponsiveApplication application, View view) {
        applicationViews.add(view);
        if (view.getComponent() instanceof Tree) {
            setApplicationTree(view);
        }
    }

    @Override
    public void handleApplicationViewRemoved(ResponsiveApplication application, View view) {
        applicationViews.remove(view);
    }

    @Override
    public void handlePerspectiveChange(ResponsiveApplication application, Perspective perspective, Perspective previousPerspective, List<View> activeViews, List<View> addedViews, List<View> removedViews) {
        removedViews.forEach(view -> mobileLayout.removeView(view.getPanel()));
        addedViews.forEach(view -> mobileLayout.preloadView(view.getPanel()));
        perspectiveViews = activeViews;

        viewsItemView.removeAllGroups();
        if (applicationLauncher != null) {
            SimpleItemGroup<Void> group = viewsItemView.addSingleColumnGroup(Icons.WINDOW_EXPLORER, "Launcher");
            group.addItem(applicationLauncher.getPanel().getIcon(), applicationLauncher.getPanel().getTitle(), null).onClick.addListener(aVoid -> {
                showView(applicationLauncher);
            });
        }

        if (!applicationViews.isEmpty()) {
            SimpleItemGroup<Void> group = viewsItemView.addSingleColumnGroup(Icons.ELEMENTS_TREE, "Application views");
            applicationViews.forEach(view -> {
                group.addItem(view.getPanel().getIcon(), view.getPanel().getTitle(), null).onClick.addListener(aVoid -> {
                    showView(view);
                });
            });
        }

        if (!perspectiveViews.isEmpty()) {
            SimpleItemGroup<Void> group = viewsItemView.addSingleColumnGroup(Icons.WINDOWS, "Perspective views");
            perspectiveViews.forEach(view -> {
                group.addItem(view.getPanel().getIcon(), view.getPanel().getTitle(), null).onClick.addListener(aVoid -> {
                    showView(view);
                });
            });
        }

        if (!perspectiveViews.isEmpty()) {
            View view = perspectiveViews.get(0);
            activeView = view;
            mobileLayout.showView(view.getPanel(), MobileLayoutAnimation.FORWARD);
        }
    }

    @Override
    public void handleLayoutChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, LayoutItemDefinition layout) {

    }

    @Override
    public void handleViewAdded(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view) {
        if (isActivePerspective) {
            mobileLayout.preloadView(view.getPanel());
            perspectiveViews.add(view);
        }
    }

    @Override
    public void handleViewRemoved(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view) {
        if (isActivePerspective) {
            mobileLayout.removeView(view.getPanel());
            perspectiveViews.remove(view);
        }
    }

    @Override
    public void handleViewVisibilityChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, boolean visible) {
        if (isActivePerspective) {

        }
    }

    @Override
    public void handleViewFocusRequest(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, boolean ensureVisible) {
        if (isActivePerspective) {
            showView(view);
        }
    }

    @Override
    public void handleViewSizeChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, ViewSize viewSize) {

    }

    @Override
    public void handleViewLayoutPositionChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, String position) {

    }
}
