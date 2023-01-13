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
package org.teamapps.ux.component.workspacelayout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.*;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.*;
import org.teamapps.ux.component.panel.Panel;
import org.teamapps.ux.component.progress.DefaultMultiProgressDisplay;
import org.teamapps.ux.component.progress.MultiProgressDisplay;
import org.teamapps.ux.component.splitpane.SplitSizePolicy;
import org.teamapps.ux.component.toolbar.Toolbar;
import org.teamapps.ux.component.workspacelayout.definition.LayoutItemDefinition;

import java.util.*;
import java.util.stream.Collectors;

@TeamAppsComponent(library = CommonComponentLibrary.class)
public class WorkSpaceLayout extends AbstractComponent implements Component {

	public static String ROOT_WINDOW_ID = "ROOT_WINDOW";
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkSpaceLayout.class);

	public final ProjectorEvent<ViewSelectedEventData> onViewSelected = createProjectorEventBoundToUiEvent(DtoWorkSpaceLayout.ViewSelectedEvent.TYPE_ID);
	public final ProjectorEvent<WorkSpaceLayoutView> onChildWindowCreationFailed = createProjectorEventBoundToUiEvent(DtoWorkSpaceLayout.ChildWindowCreationFailedEvent.TYPE_ID);
	public final ProjectorEvent<ChildWindowClosedEventData> onChildWindowClosed = createProjectorEventBoundToUiEvent(DtoWorkSpaceLayout.ChildWindowClosedEvent.TYPE_ID);
	public final ProjectorEvent<WorkSpaceLayoutView> onViewClosed = createProjectorEventBoundToUiEvent(DtoWorkSpaceLayout.ViewClosedEvent.TYPE_ID);
	public final ProjectorEvent<WorkSpaceLayoutViewGroup> onViewGroupPanelStateChanged = createProjectorEventBoundToUiEvent(DtoWorkSpaceLayout.ViewGroupPanelStateChangedEvent.TYPE_ID);

	private final String childWindowPageTitle = "Application window";
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
		sendCommandIfRendered(() -> {
			Map<String, DtoWorkSpaceLayoutItem> uiRootItemsByWindowId = rootItemsByWindowId.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().createUiItem()));
			List<DtoWorkSpaceLayoutView> newUiViews = newViews.stream()
					.map(WorkSpaceLayoutView::createUiView)
					.collect(Collectors.toList());
			return new DtoWorkSpaceLayout.RedefineLayoutCommand(uiRootItemsByWindowId, newUiViews);
		});
	}

	@Override
	public DtoComponent createDto() {
		DtoWorkSpaceLayoutItem uiInitialLayout = getMainRootItem().createUiItem();
		List<DtoWorkSpaceLayoutView> uiViews = getMainRootItem().getAllViews().stream()
				.map(WorkSpaceLayoutView::createUiView)
				.collect(Collectors.toList());
		DtoWorkSpaceLayout uiLayout = new DtoWorkSpaceLayout(uiViews, uiInitialLayout, childWindowPageTitle);
		mapAbstractUiComponentProperties(uiLayout);
		if (toolbar != null) {
			uiLayout.setToolbar(toolbar.createDtoReference());
		}
		uiLayout.setNewWindowBackgroundImage(newWindowBackgroundImage);
		uiLayout.setNewWindowBlurredBackgroundImage(newWindowBlurredBackgroundImage);
		uiLayout.setMultiProgressDisplay(ClientObject.createDtoReference(multiProgressDisplay));
		return uiLayout;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoWorkSpaceLayout.LayoutChangedEvent.TYPE_ID -> {
				var layoutChangedEvent = event.as(DtoWorkSpaceLayout.LayoutChangedEventWrapper.class);
				this.rootItemsByWindowId = new LayoutApplyer(this).applyFromUiLayoutDescriptor(rootItemsByWindowId, layoutChangedEvent.getLayoutsByWindowId());
				printLayoutSyncTraceMessage(layoutChangedEvent.getLayoutsByWindowId());
			}
			case DtoWorkSpaceLayout.ViewDraggedToNewWindowEvent.TYPE_ID -> {
				var newWindowEvent = event.as(DtoWorkSpaceLayout.ViewDraggedToNewWindowEventWrapper.class);
				Map<String, DtoWorkSpaceLayoutItemWrapper> newLayoutsByWindowId = newWindowEvent.getLayoutsByWindowId();
				this.rootItemsByWindowId = new LayoutApplyer(this).applyFromUiLayoutDescriptor(rootItemsByWindowId, newLayoutsByWindowId);
				printLayoutSyncTraceMessage(newWindowEvent.getLayoutsByWindowId());
			}
			case DtoWorkSpaceLayout.ViewNeedsRefreshEvent.TYPE_ID -> {
				var needsRefreshEvent = event.as(DtoWorkSpaceLayout.ViewNeedsRefreshEventWrapper.class);
				String viewName = needsRefreshEvent.getViewName();
				WorkSpaceLayoutView view = getViewById(viewName);
				getSessionContext().sendCommandIfRendered(this, new DtoWorkSpaceLayout.RefreshViewComponentCommand(viewName, view.createUiView().getComponent()));
			}
			case DtoWorkSpaceLayout.ChildWindowCreationFailedEvent.TYPE_ID -> {
				var windowCreationFailedEvent = event.as(DtoWorkSpaceLayout.ChildWindowCreationFailedEventWrapper.class);
				WorkSpaceLayoutView view = getViewById(windowCreationFailedEvent.getViewName());
				if (view != null) {
					this.onChildWindowCreationFailed.fire(view);
				}
			}
			case DtoWorkSpaceLayout.ChildWindowClosedEvent.TYPE_ID -> {
				var windowClosedEvent = event.as(DtoWorkSpaceLayout.ChildWindowClosedEventWrapper.class);
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
			}
			case DtoWorkSpaceLayout.ViewSelectedEvent.TYPE_ID -> {
				var tabSelectedEvent = event.as(DtoWorkSpaceLayout.ViewSelectedEventWrapper.class);
				WorkSpaceLayoutViewGroup viewGroup = this.getViewGroupById(tabSelectedEvent.getViewGroupId());
				if (viewGroup != null) {
					viewGroup.handleViewSelectedByClient(tabSelectedEvent.getViewName());
					WorkSpaceLayoutView view = getViewById(tabSelectedEvent.getViewName());
					this.onViewSelected.fire(new ViewSelectedEventData(viewGroup, view));
				}
			}
			case DtoWorkSpaceLayout.ViewClosedEvent.TYPE_ID -> {
				var viewClosedEvent = event.as(DtoWorkSpaceLayout.ViewClosedEventWrapper.class);
				WorkSpaceLayoutView view = getViewById(viewClosedEvent.getViewName());
				if (view != null) {
					WorkSpaceLayoutViewGroup viewGroup = getViewGroupForViewName(viewClosedEvent.getViewName());
					if (viewGroup != null) {
						viewGroup.removeViewSilently(view);
					}
					this.onViewClosed.fire(view);
				}
			}
			case DtoWorkSpaceLayout.ViewGroupPanelStateChangedEvent.TYPE_ID -> {
				var stateChangedEvent = event.as(DtoWorkSpaceLayout.ViewGroupPanelStateChangedEventWrapper.class);
				WorkSpaceLayoutViewGroup viewGroup = getViewGroupById(stateChangedEvent.getViewGroupId());
				if (viewGroup != null) {
					viewGroup.setPanelStateSilently(ViewGroupPanelState.valueOf(stateChangedEvent.getPanelState().name()));
					this.onViewGroupPanelStateChanged.fire(viewGroup);
				}
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

	private void printLayoutSyncTraceMessage(Map<String, DtoWorkSpaceLayoutItemWrapper> layoutsByWindowId) {
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

	public void setNewWindowBackgroundImage(String newWindowBackgroundImage) {
		this.newWindowBackgroundImage = newWindowBackgroundImage;
	}

	public void setNewWindowBlurredBackgroundImage(String newWindowBlurredBackgroundImage) {
		this.newWindowBlurredBackgroundImage = newWindowBlurredBackgroundImage;
	}

	// ============== Internal API ======================

	/*package-private*/ void handleViewAddedToGroup(WorkSpaceLayoutViewGroup workSpaceLayoutViewGroup, WorkSpaceLayoutView view, boolean selected) {
		sendCommandIfRendered(() -> new DtoWorkSpaceLayout.AddViewAsTabCommand(view.createUiView(), workSpaceLayoutViewGroup.getId(), selected));
	}

	/*package-private*/ void handleViewGroupPanelStateChangedViaApi(WorkSpaceLayoutViewGroup viewGroup, ViewGroupPanelState panelState) {
		sendCommandIfRendered(() -> new DtoWorkSpaceLayout.SetViewGroupPanelStateCommand(viewGroup.getId(), panelState.toUiViewGroupPanelState()));
	}

	/*package-private*/ void handleViewSelectedViaApi(WorkSpaceLayoutViewGroup viewGroup, WorkSpaceLayoutView workSpaceLayoutView) {
		sendCommandIfRendered(() -> new DtoWorkSpaceLayout.SelectViewCommand(workSpaceLayoutView.getId()));
	}

	/*package-private*/ void handleViewRemovedViaApi(WorkSpaceLayoutViewGroup viewGroup, WorkSpaceLayoutView view) {
		if (viewGroup.getViews().isEmpty() && !viewGroup.isPersistent()) {
			removeEmptyViewGroupFromItemTree(viewGroup);
		}
		this.updateClientSideLayout(Collections.emptyList());
	}

	/*package-private*/ void handleViewAttributeChangedViaApi(WorkSpaceLayoutView view) {
		sendCommandIfRendered(() -> new DtoWorkSpaceLayout.RefreshViewAttributesCommand(view.getId(), getSessionContext().resolveIcon(view.getIcon()), view.getTabTitle(), view.isCloseable(), view.isVisible()));
	}

	/*package-private*/ void handleSplitPaneSizingChanged(SplitSizePolicy sizePolicy, double referenceChildSize) {
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
		sendCommandIfRendered(() -> new DtoWorkSpaceLayout.SetMultiProgressDisplayCommand(multiProgressDisplay.createDtoReference()));
	}

	public MultiProgressDisplay getMultiProgressDisplay() {
		return multiProgressDisplay;
	}
}
