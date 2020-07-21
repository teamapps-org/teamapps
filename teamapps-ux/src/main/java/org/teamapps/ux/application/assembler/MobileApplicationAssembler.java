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
package org.teamapps.ux.application.assembler;

import org.teamapps.icons.api.Icon;
import org.teamapps.ux.application.ResponsiveApplication;
import org.teamapps.ux.application.ResponsiveApplicationToolbar;
import org.teamapps.ux.application.perspective.Perspective;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.view.ViewSize;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.animation.PageTransition;
import org.teamapps.ux.component.itemview.SimpleItemGroup;
import org.teamapps.ux.component.itemview.SimpleItemView;
import org.teamapps.ux.component.mobile.MobileLayout;
import org.teamapps.ux.component.mobile.NavigationBar;
import org.teamapps.ux.component.mobile.NavigationBarButton;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.toolbar.AbstractToolContainer;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.component.tree.Tree;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.ux.icon.TeamAppsIconBundle;
import org.teamapps.ux.session.SessionContext;

import java.util.ArrayList;
import java.util.List;

public class MobileApplicationAssembler implements ApplicationAssembler {

	private static final int PAGE_TRANSITION_ANIMATION_DURATION = 300;

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
	private View activeView;

	public MobileApplicationAssembler() {
		this(
				SessionContext.current().getIcon(TeamAppsIconBundle.APPLICATION_LAUNCHER.getKey()),
				SessionContext.current().getIcon(TeamAppsIconBundle.TREE.getKey()),
				SessionContext.current().getIcon(TeamAppsIconBundle.VIEWS.getKey()),
				SessionContext.current().getIcon(TeamAppsIconBundle.TOOLBAR.getKey()),
				SessionContext.current().getIcon(TeamAppsIconBundle.TOOLBAR.getKey()),
				null);
	}

	public MobileApplicationAssembler(Icon launcherIcon, Icon treeIcon, Icon viewsIcon, Icon toolbarIcon, Icon backIcon, List<AdditionalNavigationButton> additionalLeftButtons) {
		this.mobileLayout = new MobileLayout();
		this.navigationBar = new NavigationBar<>();
		this.viewsItemView = new SimpleItemView<>();

		navigationBar.preloadFanOutComponent(viewsItemView);
		mobileLayout.setNavigationBar(navigationBar);

		applicationLauncherButton = NavigationBarButton.create(launcherIcon);
		applicationTreeButton = NavigationBarButton.create(treeIcon);
		applicationViewsButton = NavigationBarButton.create(viewsIcon);
		applicationToolbarButton = NavigationBarButton.create(toolbarIcon);
		// TODO make toolbar button invisible when no application toolbar
		applicationBackButton = NavigationBarButton.create(backIcon);


		applicationLauncherButton.setVisible(false);
		applicationTreeButton.setVisible(false);

		if (additionalLeftButtons != null) {
			for (AdditionalNavigationButton leftButton : additionalLeftButtons) {
				NavigationBarButton<BaseTemplateRecord> button = NavigationBarButton.create(leftButton.getIcon(), leftButton.getCaption());
				navigationBar.addButton(button);
				button.onClick.addListener(aVoid -> leftButton.getHandler().run());
			}
		}
		navigationBar.addButton(applicationLauncherButton);
		navigationBar.addButton(applicationTreeButton);
		navigationBar.addButton(applicationViewsButton);
		navigationBar.addButton(applicationToolbarButton);
		navigationBar.addButton(applicationBackButton);

		applicationLauncherButton.onClick.addListener(aVoid -> {
			navigationBar.hideFanOutComponent();
			mobileLayout.setContent(applicationLauncher.getPanel(), PageTransition.MOVE_TO_RIGHT_VS_MOVE_FROM_LEFT, PAGE_TRANSITION_ANIMATION_DURATION);
			activeView = applicationLauncher;
		});

		applicationTreeButton.onClick.addListener(aVoid -> {
			navigationBar.hideFanOutComponent();
			PageTransition animation = PageTransition.MOVE_TO_RIGHT_VS_MOVE_FROM_LEFT;
			if (activeView.equals(applicationLauncher)) {
				animation = PageTransition.MOVE_TO_LEFT_VS_MOVE_FROM_RIGHT;
			}
			mobileLayout.setContent(appTree.getPanel(), animation, PAGE_TRANSITION_ANIMATION_DURATION);
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
			navigationBar.hideFanOutComponent();
		});
	}

	public void setApplicationLauncher(View applicationLauncher) {
		if (applicationLauncher == null) {
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
		mobileLayout.setContent(view.getPanel(), PageTransition.MOVE_TO_LEFT_VS_MOVE_FROM_RIGHT, PAGE_TRANSITION_ANIMATION_DURATION);
	}

	public void showInitialView() {
		View view = null;
		if (!applicationViews.isEmpty()) {
			view = applicationViews.get(0);
		} else if (!perspectiveViews.isEmpty()) {
			view = perspectiveViews.get(0);
		}
		if (view != null) {
			mobileLayout.setContent(view.getPanel(), PageTransition.MOVE_TO_LEFT_VS_MOVE_FROM_RIGHT, PAGE_TRANSITION_ANIMATION_DURATION);
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
			mobileLayout.setContent(activeView.getPanel(), PageTransition.MOVE_TO_RIGHT_VS_MOVE_FROM_LEFT, PAGE_TRANSITION_ANIMATION_DURATION);
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
	public MultiProgressDisplay getMultiProgressDisplay() {
		return navigationBar.getMultiProgressDisplay();
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
	public void handlePerspectiveChange(ResponsiveApplication application, Perspective perspective, Perspective previousPerspective, List<View> activeViews, List<View> addedViews,
                                        List<View> removedViews) {
		addedViews.forEach(view -> mobileLayout.preloadView(view.getPanel()));
		perspectiveViews = activeViews;

		viewsItemView.removeAllGroups();
		SimpleItemGroup<Void> itemGroup = viewsItemView.addSingleColumnGroup(null, null);
		if (!applicationViews.isEmpty()) {
			applicationViews.forEach(view -> {
				itemGroup.addItem(view.getPanel().getIcon(), view.getPanel().getTitle(), null).onClick.addListener(aVoid -> {
					showView(view);
				});
			});
		}
		if (!perspectiveViews.isEmpty()) {
			perspectiveViews.forEach(view -> {
				itemGroup.addItem(view.getPanel().getIcon(), view.getPanel().getTitle(), null).onClick.addListener(aVoid -> {
					showView(view);
				});
			});
		}

		if (!perspectiveViews.isEmpty()) {
			View view = perspective.getFocusedView() != null ? perspective.getFocusedView() : perspectiveViews.get(0);
			activeView = view;
			mobileLayout.setContent(view.getPanel(), PageTransition.MOVE_TO_LEFT_VS_MOVE_FROM_RIGHT, PAGE_TRANSITION_ANIMATION_DURATION);
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
	public void handleViewTabTitleChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, String title) {

	}

	@Override
	public void handleViewLayoutPositionChange(ResponsiveApplication application, boolean isActivePerspective, Perspective perspective, View view, String position) {

	}

	@Override
	public void handleApplicationToolbarButtonGroupAdded(ResponsiveApplication application, ToolbarButtonGroup buttonGroup) {
		applicationToolbarButton.setVisible(true);
	}
}
