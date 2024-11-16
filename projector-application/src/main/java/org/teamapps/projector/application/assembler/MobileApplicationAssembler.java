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
package org.teamapps.projector.application.assembler;

import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.icons.Icon;
import org.teamapps.projector.animation.PageTransition;
import org.teamapps.projector.application.ResponsiveApplication;
import org.teamapps.projector.application.ResponsiveApplicationToolbar;
import org.teamapps.projector.application.perspective.Perspective;
import org.teamapps.projector.application.view.View;
import org.teamapps.projector.application.view.ViewSize;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.toolbar.AbstractToolContainer;
import org.teamapps.projector.component.core.toolbar.ToolAccordion;
import org.teamapps.projector.component.core.toolbar.ToolbarButton;
import org.teamapps.projector.component.core.toolbar.ToolbarButtonGroup;
import org.teamapps.projector.component.mobilelayout.MobileLayout;
import org.teamapps.projector.component.mobilelayout.NavigationBar;
import org.teamapps.projector.component.mobilelayout.NavigationBarButton;
import org.teamapps.projector.component.progress.MultiProgressDisplay;
import org.teamapps.projector.component.workspacelayout.definition.LayoutItemDefinition;

import java.util.ArrayList;
import java.util.List;

public class MobileApplicationAssembler implements ApplicationAssembler {

	private static final int PAGE_TRANSITION_ANIMATION_DURATION = 300;

	private final NavigationBarButton applicationLauncherButton;
	private final NavigationBarButton applicationTreeButton;
	private final NavigationBarButton applicationViewsButton;
	private final NavigationBarButton applicationToolbarButton;
	private final NavigationBarButton applicationBackButton;

	private final MobileLayout mobileLayout;
	private final NavigationBar navigationBar;
	private final ToolAccordion viewsItemView;
	private AbstractToolContainer mainToolbar;


	private View applicationLauncher;
	private View appTree;
	private final List<View> applicationViews = new ArrayList<>();
	private List<View> perspectiveViews = new ArrayList<>();
	private View activeView;

	public MobileApplicationAssembler() {
		this(MaterialIcon.VIEW_MODULE,
				MaterialIcon.TOC,
				MaterialIcon.VIEW_CAROUSEL,
				MaterialIcon.SUBTITLES,
				MaterialIcon.SUBTITLES,
				null);
	}

	public MobileApplicationAssembler(Icon<?, ?> launcherIcon, Icon<?, ?> treeIcon, Icon<?, ?> viewsIcon, Icon<?, ?> toolbarIcon, Icon<?, ?> backIcon, List<AdditionalNavigationButton> additionalLeftButtons) {
		this.mobileLayout = new MobileLayout();
		this.navigationBar = new NavigationBar();
		this.viewsItemView = new ToolAccordion();

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
				NavigationBarButton button = NavigationBarButton.create(leftButton.getIcon());
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

		viewsItemView.onButtonClick.addListener(data -> {
			navigationBar.hideFanOutComponent();
		});
	}

	public void setApplicationLauncher(View applicationLauncher) {
		if (applicationLauncher == null) {
			applicationLauncherButton.setVisible(false);
		} else {
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
	}

	@Override
	public void handleApplicationViewRemoved(ResponsiveApplication application, View view) {
		applicationViews.remove(view);
	}

	@Override
	public void handlePerspectiveChange(ResponsiveApplication application, Perspective perspective, Perspective previousPerspective, List<View> activeViews, List<View> addedViews,
										List<View> removedViews) {
		perspectiveViews = activeViews;

		viewsItemView.removeAllToolbarButtonGroups();
		ToolbarButtonGroup applicationViewsButtonGroup = new ToolbarButtonGroup();
		viewsItemView.addButtonGroup(applicationViewsButtonGroup);
		if (!applicationViews.isEmpty()) {
			applicationViews.forEach(view -> {
				ToolbarButton toolbarButton = ToolbarButton.create(view.getPanel().getIcon(), view.getPanel().getTitle(), null);
				toolbarButton.onClick.addListener(aVoid -> {
					showView(view);
				});
				applicationViewsButtonGroup.addButton(toolbarButton);
			});
		}

		ToolbarButtonGroup perspectiveViewsButtonGroup = new ToolbarButtonGroup();
		viewsItemView.addButtonGroup(perspectiveViewsButtonGroup);
		if (!perspectiveViews.isEmpty()) {
			perspectiveViews.forEach(view -> {
				ToolbarButton toolbarButton = ToolbarButton.create(view.getPanel().getIcon(), view.getPanel().getTitle(), null);
				toolbarButton.onClick.addListener(aVoid -> {
					showView(view);
				});
				perspectiveViewsButtonGroup.addButton(toolbarButton);
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
