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
package org.teamapps.ux.component.workspacelayout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiWorkSpaceLayout;
import org.teamapps.dto.UiWorkSpaceLayoutItem;
import org.teamapps.dto.UiWorkSpaceLayoutView;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.Container;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.progress.DefaultMultiProgressDisplay;
import org.teamapps.ux.component.splitpane.SplitSizePolicy;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.ux.component.progress.MultiProgressDisplay;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WorkSpaceLayout extends AbstractComponent implements Container {

	public static String ROOT_WINDOW_ID = "ROOT_WINDOW";
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkSpaceLayout.class);

	public final Event<ViewSelectedEventData> onViewSelected = new Event<>();
	public final Event<WorkSpaceLayoutView> onChildWindowCreationFailed = new Event<>();
	public final Event<ChildWindowClosedEventData> onChildWindowClosed = new Event<>();
	public final Event<WorkSpaceLayoutView> onViewClosed = new Event<>();
	public final Event<WorkSpaceLayoutViewGroup> onViewGroupPanelStateChanged = new Event<>();

	private String childWindowPageTitle = "Application window";
	private Toolbar toolbar;
	private String newWindowBackgroundImage;
	private String newWindowBlurredBackgroundImage;
	private MultiProgressDisplay multiProgressDisplay = new DefaultMultiProgressDisplay();

	private Map<String, WorkSpaceLayoutItem> rootItemsByWindowId = new HashMap<>();

	public WorkSpaceLayout(LayoutItemDefinition layoutDefinition) {
		WorkSpaceLayoutItem rootItem = layoutDefinition.createHeavyWeightItem(this);
		setMainRootItem(rootItem);
	}

	public WorkSpaceLayout() {
		setMainRootItem(new WorkSpaceLayoutViewGroup(this));
	}

	protected void setMainRootItem(WorkSpaceLayoutItem item) {
		rootItemsByWindowId.put(ROOT_WINDOW_ID, item);
		updateClientSideLayout(item.getAllViews());
	}

	public WorkSpaceLayoutItem getMainRootItem() {
		return rootItemsByWindowId.get(ROOT_WINDOW_ID);
	}

	// TODO CAUTION the method signature will change when multi-window gets reactivated!
	public void applyLayoutDefinition(LayoutItemDefinition layoutDefinition) {
		this.rootItemsByWindowId = new LayoutApplyer(this).applyFromLayoutDefinition(rootItemsByWindowId, Collections.singletonMap(ROOT_WINDOW_ID, layoutDefinition));
		updateClientSideLayout(Collections.emptyList());
	}

	// TODO CAUTION the method signature will change when multi-window gets reactivated!
	public LayoutItemDefinition extractLayoutDefinition() {
		return getMainRootItem().createLayoutDefinitionItem();
	}

	private void updateClientSideLayout(List<WorkSpaceLayoutView> newViews) {
		queueCommandIfRendered(() -> {
			Map<String, UiWorkSpaceLayoutItem> uiRootItemsByWindowId = rootItemsByWindowId.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().createUiItem()));
			List<UiWorkSpaceLayoutView> newUiViews = newViews.stream()
					.map(WorkSpaceLayoutView::createUiView)
					.collect(Collectors.toList());
			return new UiWorkSpaceLayout.RedefineLayoutCommand(getId(), uiRootItemsByWindowId, newUiViews);
		});
	}

	@Override
	public UiComponent createUiComponent() {
		UiWorkSpaceLayoutItem uiInitialLayout = getMainRootItem().createUiItem();
		List<UiWorkSpaceLayoutView> uiViews = getMainRootItem().getAllViews().stream()
				.map(WorkSpaceLayoutView::createUiView)
				.collect(Collectors.toList());
		UiWorkSpaceLayout uiLayout = new UiWorkSpaceLayout(uiViews, uiInitialLayout, childWindowPageTitle);
		mapAbstractUiComponentProperties(uiLayout);
		if (toolbar != null) {
			uiLayout.setToolbar(toolbar.createUiReference());
		}
		uiLayout.setNewWindowBackgroundImage(newWindowBackgroundImage);
		uiLayout.setNewWindowBlurredBackgroundImage(newWindowBlurredBackgroundImage);
		uiLayout.setMultiProgressDisplay(Component.createUiClientObjectReference(multiProgressDisplay));
		return uiLayout;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_WORK_SPACE_LAYOUT_LAYOUT_CHANGED: {
				UiWorkSpaceLayout.LayoutChangedEvent layoutChangedEvent = (UiWorkSpaceLayout.LayoutChangedEvent) event;
				this.rootItemsByWindowId = new LayoutApplyer(this).applyFromUiLayoutDescriptor(rootItemsByWindowId, layoutChangedEvent.getLayoutsByWindowId());
				printLayoutSyncTraceMessage(layoutChangedEvent.getLayoutsByWindowId());
				break;
			}
			case UI_WORK_SPACE_LAYOUT_VIEW_DRAGGED_TO_NEW_WINDOW: {
				UiWorkSpaceLayout.ViewDraggedToNewWindowEvent newWindowEvent = (UiWorkSpaceLayout.ViewDraggedToNewWindowEvent) event;
				Map<String, UiWorkSpaceLayoutItem> newLayoutsByWindowId = newWindowEvent.getLayoutsByWindowId();
				this.rootItemsByWindowId = new LayoutApplyer(this).applyFromUiLayoutDescriptor(rootItemsByWindowId, newLayoutsByWindowId);
				printLayoutSyncTraceMessage(newWindowEvent.getLayoutsByWindowId());
				break;
			}
			case UI_WORK_SPACE_LAYOUT_VIEW_NEEDS_REFRESH: {
				UiWorkSpaceLayout.ViewNeedsRefreshEvent needsRefreshEvent = (UiWorkSpaceLayout.ViewNeedsRefreshEvent) event;
				String viewName = needsRefreshEvent.getViewName();
				WorkSpaceLayoutView view = getViewById(viewName);
				getSessionContext().queueCommand(new UiWorkSpaceLayout.RefreshViewComponentCommand(getId(), viewName, view.createUiView().getComponent()));
				break;
			}
			case UI_WORK_SPACE_LAYOUT_CHILD_WINDOW_CREATION_FAILED: {
				UiWorkSpaceLayout.ChildWindowCreationFailedEvent windowCreationFailedEvent = (UiWorkSpaceLayout.ChildWindowCreationFailedEvent) event;
				WorkSpaceLayoutView view = getViewById(windowCreationFailedEvent.getViewName());
				if (view != null) {
					this.onChildWindowCreationFailed.fire(view);
				}
				break;
			}
			case UI_WORK_SPACE_LAYOUT_CHILD_WINDOW_CLOSED: {
				UiWorkSpaceLayout.ChildWindowClosedEvent windowClosedEvent = (UiWorkSpaceLayout.ChildWindowClosedEvent) event;
				WorkSpaceLayoutItem windowRootItem = this.rootItemsByWindowId.remove(windowClosedEvent.getWindowId());
				if (windowRootItem != null) {
					getMainRootItem().getSelfAndAncestors().stream()
							.filter(item -> item instanceof WorkSpaceLayoutViewGroup)
							.map(item -> ((WorkSpaceLayoutViewGroup) item))
							.findFirst().ifPresent(viewGroup -> {
						windowRootItem.getAllViews().forEach(viewGroup::addView);
					});
				}
				this.onChildWindowClosed.fire(new ChildWindowClosedEventData(windowClosedEvent.getWindowId(), windowRootItem));
				break;
			}
			case UI_WORK_SPACE_LAYOUT_VIEW_SELECTED: {
				UiWorkSpaceLayout.ViewSelectedEvent tabSelectedEvent = (UiWorkSpaceLayout.ViewSelectedEvent) event;
				WorkSpaceLayoutViewGroup viewGroup = this.getViewGroupById(tabSelectedEvent.getViewGroupId());
				if (viewGroup != null) {
					viewGroup.handleViewSelectedByClient(tabSelectedEvent.getViewName());
					WorkSpaceLayoutView view = getViewById(tabSelectedEvent.getViewName());
					this.onViewSelected.fire(new ViewSelectedEventData(viewGroup, view));
				}
				break;
			}
			case UI_WORK_SPACE_LAYOUT_VIEW_CLOSED: {
				UiWorkSpaceLayout.ViewClosedEvent viewClosedEvent = (UiWorkSpaceLayout.ViewClosedEvent) event;
				WorkSpaceLayoutView view = getViewById(viewClosedEvent.getViewName());
				if (view != null) {
					WorkSpaceLayoutViewGroup viewGroup = getViewGroupForViewName(viewClosedEvent.getViewName());
					if (viewGroup != null) {
						viewGroup.removeViewSilently(view);
					}
					this.onViewClosed.fire(view);
				}
				break;
			}
			case UI_WORK_SPACE_LAYOUT_VIEW_GROUP_PANEL_STATE_CHANGED: {
				UiWorkSpaceLayout.ViewGroupPanelStateChangedEvent stateChangedEvent = (UiWorkSpaceLayout.ViewGroupPanelStateChangedEvent) event;
				WorkSpaceLayoutViewGroup viewGroup = getViewGroupById(stateChangedEvent.getViewGroupId());
				if (viewGroup != null) {
					viewGroup.setPanelStateSilently(ViewGroupPanelState.valueOf(stateChangedEvent.getPanelState().name()));
					this.onViewGroupPanelStateChanged.fire(viewGroup);
				}
				break;
			}
		}
	}

	public WorkSpaceLayoutViewGroup getViewGroupById(String itemId) {
		return rootItemsByWindowId.values().stream()
				.flatMap(root -> root.getSelfAndAncestors().stream())
				.filter(item -> Objects.equals(item.getId(), itemId) && item instanceof WorkSpaceLayoutViewGroup)
				.map(item -> ((WorkSpaceLayoutViewGroup) item))
				.findAny().orElse(null);
	}

	private WorkSpaceLayoutViewGroup getViewGroupForViewName(String viewName) {
		return rootItemsByWindowId.values().stream()
				.flatMap(root -> root.getSelfAndAncestors().stream())
				.filter(item -> item instanceof WorkSpaceLayoutViewGroup)
				.map(item -> ((WorkSpaceLayoutViewGroup) item))
				.filter(viewGroup -> viewGroup.getViews().stream()
						.anyMatch(view -> Objects.equals(view.getId(), viewName))
				)
				.findAny().orElse(null);
	}

	private void printLayoutSyncTraceMessage(Map<String, UiWorkSpaceLayoutItem> layoutsByWindowId) {
		if (LOGGER.isTraceEnabled()) {
			try {
				LOGGER.trace("---------------------");
				LOGGER.trace(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(layoutsByWindowId));
				LOGGER.trace(rootItemsByWindowId.toString());
				LOGGER.trace("/---------------------");
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	private WorkSpaceLayoutView getViewById(String viewName) {
		return rootItemsByWindowId.values().stream()
				.flatMap(rootItem -> rootItem.getAllViews().stream())
				.filter(view -> view.getId().equals(viewName))
				.findFirst().orElse(null);
	}

	public WorkSpaceLayoutView getViewByPanel(Panel panel) {
		return rootItemsByWindowId.values().stream()
				.flatMap(rootItem -> rootItem.getAllViews().stream())
				.filter(view -> view.getPanel().equals(panel))
				.findFirst().orElse(null);
	}

	public Toolbar getToolbar() {
		return toolbar;
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
	}

	@Override
	public boolean isChildVisible(Component child) {
		if (child instanceof Panel) {
			WorkSpaceLayoutView view = getViewByPanel(((Panel) child));
			if (view != null && view.getViewGroup() != null) {
				return isEffectivelyVisible() && view.getViewGroup().getPanelState() != ViewGroupPanelState.MINIMIZED && view.isSelected();
			}
		}
		if (child == toolbar) {
			return isEffectivelyVisible();
		}
		return false;
	}

	public void setNewWindowBackgroundImage(String newWindowBackgroundImage) {
		this.newWindowBackgroundImage = newWindowBackgroundImage;
	}

	public void setNewWindowBlurredBackgroundImage(String newWindowBlurredBackgroundImage) {
		this.newWindowBlurredBackgroundImage = newWindowBlurredBackgroundImage;
	}

	// ============== Internal API ======================

	/*package-private*/ void handleViewAddedToGroup(WorkSpaceLayoutViewGroup workSpaceLayoutViewGroup, WorkSpaceLayoutView view, boolean selected) {
		queueCommandIfRendered(() -> new UiWorkSpaceLayout.AddViewAsTabCommand(getId(), view.createUiView(), workSpaceLayoutViewGroup.getId(), selected));
	}

	/*package-private*/ void handleViewGroupPanelStateChangedViaApi(WorkSpaceLayoutViewGroup viewGroup, ViewGroupPanelState panelState) {
		queueCommandIfRendered(() -> new UiWorkSpaceLayout.SetViewGroupPanelStateCommand(getId(), viewGroup.getId(), panelState.toUiViewGroupPanelState()));
	}

	/*package-private*/ void handleViewSelectedViaApi(WorkSpaceLayoutViewGroup viewGroup, WorkSpaceLayoutView workSpaceLayoutView) {
		queueCommandIfRendered(() -> new UiWorkSpaceLayout.SelectViewCommand(getId(), workSpaceLayoutView.getId()));
	}

	/*package-private*/ void handleViewRemovedViaApi(WorkSpaceLayoutViewGroup viewGroup, WorkSpaceLayoutView view) {
		if (viewGroup.getViews().isEmpty() && !viewGroup.isPersistent()) {
			removeEmptyViewGroupFromItemTree(viewGroup);
		}
		this.updateClientSideLayout(Collections.emptyList());
	}

	/*package-private*/ void handleViewAttributeChangedViaApi(WorkSpaceLayoutView view) {
		queueCommandIfRendered(() -> new UiWorkSpaceLayout.RefreshViewAttributesCommand(getId(), view.getId(), getSessionContext().resolveIcon(view.getIcon()), view.getTabTitle(), view.isCloseable(), view.isVisible()));
	}

	/*package-private*/ void handleSplitPaneSizingChanged(SplitSizePolicy sizePolicy, float referenceChildSize) {
		this.updateClientSideLayout(Collections.emptyList());
	}

	private String getWindowIdForViewGroup(WorkSpaceLayoutViewGroup viewGroup) {
		String windowId = rootItemsByWindowId.entrySet().stream()
				.filter(entry -> entry.getValue().getSelfAndAncestors().contains(viewGroup))
				.map(Map.Entry::getKey)
				.findFirst().orElse(null);
		if (windowId == null) {
			LOGGER.error("Cannot find windowId for viewGroup " + viewGroup.getId());
		}
		return windowId;

	}

	private void removeEmptyViewGroupFromItemTree(WorkSpaceLayoutViewGroup viewGroup) {
		String windowId = getWindowIdForViewGroup(viewGroup);
		WorkSpaceLayoutSplitPane parentSplitPane = findParentItem(viewGroup);
		if (parentSplitPane != null) { // else viewGroup is the rootItem, so do NOT remove it!
			// remove this viewGroup and the parent splitpane
			boolean viewGroupIsFirstChild = parentSplitPane.getFirstChild() == viewGroup;
			WorkSpaceLayoutItem siblingItem = viewGroupIsFirstChild ? parentSplitPane.getLastChild() : parentSplitPane.getFirstChild();
			WorkSpaceLayoutSplitPane grandParentSplitPane = findParentItem(parentSplitPane);
			if (grandParentSplitPane != null) {
				boolean parentSplitPaneIsFirstChild = grandParentSplitPane.getFirstChild() == parentSplitPane;
				if (parentSplitPaneIsFirstChild) {
					grandParentSplitPane.setFirstChild(siblingItem);
				} else {
					grandParentSplitPane.setLastChild(siblingItem);
				}
			} else { // parentSplitPane is the rootItem
				rootItemsByWindowId.put(windowId, siblingItem);
			}
			viewGroup.handleRemoved();
			parentSplitPane.handleRemoved();
		}
	}

	private WorkSpaceLayoutSplitPane findParentItem(WorkSpaceLayoutItem child) {
		return rootItemsByWindowId.values().stream()
				.flatMap(rootItem -> rootItem.getSelfAndAncestors().stream())
				.filter(item -> item instanceof WorkSpaceLayoutSplitPane)
				.map(item -> ((WorkSpaceLayoutSplitPane) item))
				.filter(splitPane -> splitPane.getFirstChild() == child || splitPane.getLastChild() == child)
				.findFirst().orElse(null);
	}

	public void setMultiProgressDisplay(MultiProgressDisplay multiProgressDisplay) {
		this.multiProgressDisplay = multiProgressDisplay;
		queueCommandIfRendered(() -> new UiWorkSpaceLayout.SetMultiProgressDisplayCommand(getId(), multiProgressDisplay.createUiReference()));
	}

	public MultiProgressDisplay getMultiProgressDisplay() {
		   return multiProgressDisplay;
	}
}
