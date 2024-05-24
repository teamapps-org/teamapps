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
package org.teamapps.projector.components.workspacelayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.components.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.projector.components.workspacelayout.definition.SplitPaneDefinition;
import org.teamapps.projector.components.workspacelayout.definition.ViewGroupDefinition;
import org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutItemWrapper;
import org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutSplitItem;
import org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutViewGroupItem;
import org.teamapps.projector.dto.SplitDirection;
import org.teamapps.projector.dto.SplitSizePolicy;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;

class LayoutApplyer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final WorkSpaceLayout workSpaceLayout;

	private Map<String, LayoutItemWrapper> descriptorItemById;
	private Set<String> descriptorViewNames;
	private Map<String, WorkSpaceLayoutItem> itemsById;
	private Map<String, WorkSpaceLayoutView> viewsByName;

	public LayoutApplyer(WorkSpaceLayout workSpaceLayout) {
		this.workSpaceLayout = workSpaceLayout;
	}

	public Map<String, WorkSpaceLayoutItem> applyFromUiLayoutDescriptor(
			Map<String, WorkSpaceLayoutItem> currentRootItemsByWindowId,
			Map<String, DtoWorkSpaceLayoutItemWrapper> newRootDescriptorsByWindowId
	) {
		Map<String, LayoutItemWrapper> wrappedDescriptors = newRootDescriptorsByWindowId.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> createWrapperFromUi(entry.getValue())));
		return apply(currentRootItemsByWindowId, wrappedDescriptors);
	}

	public Map<String, WorkSpaceLayoutItem> applyFromLayoutDefinition(
			Map<String, WorkSpaceLayoutItem> currentRootItemsByWindowId,
			Map<String, LayoutItemDefinition> newRootDescriptorsByWindowId
	) {
		Map<String, LayoutItemWrapper> wrappedDescriptors = newRootDescriptorsByWindowId.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> createWrapperFromDefinition(entry.getValue())));
		return apply(currentRootItemsByWindowId, wrappedDescriptors);
	}

	public Map<String, WorkSpaceLayoutItem> apply(
			Map<String, WorkSpaceLayoutItem> currentRootItemsByWindowId,
			Map<String, LayoutItemWrapper> newRootDescriptorsByWindowId
	) {
		this.reset();

		newRootDescriptorsByWindowId.forEach((windowId, layoutDescriptor) -> this.putToDescriptorDictionaries(layoutDescriptor));
		currentRootItemsByWindowId.forEach((windowId, currentRootItem) -> this.putToServerItemDictionaries(currentRootItem));

		currentRootItemsByWindowId.forEach((windowId, currentRootItem) -> {
			LayoutItemWrapper newLayoutDescriptor = newRootDescriptorsByWindowId.get(windowId);
			this.cleanupUnknownServerItems(currentRootItem, newLayoutDescriptor, null);
		});


		Map<String, WorkSpaceLayoutItem> newRootItemsByWindowId = newRootDescriptorsByWindowId.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> this.addNewStructure(e.getValue(), null, false)));
		return newRootItemsByWindowId;
	}

	private void reset() {
		descriptorItemById = new HashMap<>();
		descriptorViewNames = new HashSet<>();
		itemsById = new HashMap<>();
		viewsByName = new HashMap<>();
	}

	private void putToDescriptorDictionaries(LayoutItemWrapper descriptorItem) {
		this.descriptorItemById.put(descriptorItem.getId(), descriptorItem);
		if (descriptorItem instanceof ViewGroupWrapper) {
			ViewGroupWrapper viewGroupItem = (ViewGroupWrapper) descriptorItem;
			this.descriptorViewNames.addAll(viewGroupItem.getViewNames());
		} else if (descriptorItem instanceof SplitPaneWrapper) {
			SplitPaneWrapper splitItem = (SplitPaneWrapper) descriptorItem;
			if (splitItem.getFirstChild() != null) {
				this.putToDescriptorDictionaries(splitItem.getFirstChild());
			}
			if (splitItem.getLastChild() != null) {
				this.putToDescriptorDictionaries(splitItem.getLastChild());
			}
		}
	}

	private void putToServerItemDictionaries(WorkSpaceLayoutItem item) {
		if (item == null) {
			return;
		}
		this.itemsById.put(item.getId(), item);
		if (item instanceof WorkSpaceLayoutViewGroup) {
			((WorkSpaceLayoutViewGroup) item).getViews().forEach(view -> this.viewsByName.put(view.getId(), view));
		} else if (item instanceof WorkSpaceLayoutSplitPane) {
			WorkSpaceLayoutItem firstChild = ((WorkSpaceLayoutSplitPane) item).getFirstChild();
			if (firstChild != null) {
				this.putToServerItemDictionaries(firstChild);
			}
			WorkSpaceLayoutItem lastChild = ((WorkSpaceLayoutSplitPane) item).getLastChild();
			if (lastChild != null) {
				this.putToServerItemDictionaries(lastChild);
			}
		}
	}

	public void cleanupUnknownServerItems(WorkSpaceLayoutItem item, LayoutItemWrapper descriptorItem, WorkSpaceLayoutSplitPane parent) {
		// descriptorItem may be null for recursive executions of this method!
		if (descriptorItem != null && Objects.equals(descriptorItem.getId(), item.getId())) {
			if (item instanceof WorkSpaceLayoutSplitPane) {
				WorkSpaceLayoutSplitPane splitPane = (WorkSpaceLayoutSplitPane) item;
				WorkSpaceLayoutItem firstChild = splitPane.getFirstChild();
				if (firstChild != null) {
					this.cleanupUnknownServerItems(firstChild, ((SplitPaneWrapper) descriptorItem).getFirstChild(), splitPane);
				}
				WorkSpaceLayoutItem lastChild = splitPane.getLastChild();
				if (lastChild != null) {
					this.cleanupUnknownServerItems(lastChild, ((SplitPaneWrapper) descriptorItem).getLastChild(), splitPane);
				}
			} else if (item instanceof WorkSpaceLayoutViewGroup) {
				removeUnknownViews((WorkSpaceLayoutViewGroup) item, ((ViewGroupWrapper) descriptorItem).getViewNames());
			}
		} else {
			LayoutItemWrapper correspondingDescriptorItem = this.descriptorItemById.get(item.getId());
			if (correspondingDescriptorItem != null) {
				this.cleanupUnknownServerItems(item, correspondingDescriptorItem, null);
			} else {
				// not referenced in the descriptor! however, descendants might well be referenced in the descriptor!
				if (item instanceof WorkSpaceLayoutViewGroup) {
					this.removeUnknownViews((WorkSpaceLayoutViewGroup) item, Collections.emptyList());
				} else if (item instanceof WorkSpaceLayoutSplitPane) {
					this.cleanupUnknownServerItems(((WorkSpaceLayoutSplitPane) item).getFirstChild(), null, (WorkSpaceLayoutSplitPane) item);
					this.cleanupUnknownServerItems(((WorkSpaceLayoutSplitPane) item).getLastChild(), null, (WorkSpaceLayoutSplitPane) item);
				}
			}
			if (parent != null) {
				if (item == parent.getFirstChild()) {
					parent.setFirstChildSilently(null);
				} else {
					parent.setLastChildSilently(null);
				}
			} // otherwise this is the root item... nothing to do, since it will be exchanged, anyway.
		}
	}

	private void removeUnknownViews(WorkSpaceLayoutViewGroup viewGroup, List<String> descriptorViewNames) {
		List<WorkSpaceLayoutView> viewsToBeRemoved = viewGroup.getViews().stream()
				.filter(view -> !descriptorViewNames.contains(view.getId()))
				.collect(Collectors.toList());
		viewsToBeRemoved.forEach(viewGroup::handleViewRemovedByClient);
	}

	private WorkSpaceLayoutItem addNewStructure(LayoutItemWrapper descriptor, WorkSpaceLayoutSplitPane parent, boolean firstChild) {
		if (descriptor == null) {
			return null;
		}
		WorkSpaceLayoutItem serverSideItem = this.itemsById.get(descriptor.getId());
		WorkSpaceLayoutItem item;
		if (serverSideItem != null) {
			item = serverSideItem;
			if (descriptor instanceof SplitPaneWrapper) {
				SplitPaneWrapper splitDescriptor = (SplitPaneWrapper) descriptor;
				WorkSpaceLayoutSplitPane splitPaneItem = (WorkSpaceLayoutSplitPane) serverSideItem;
				this.addNewStructure(splitDescriptor.getFirstChild(), splitPaneItem, true);
				this.addNewStructure(splitDescriptor.getLastChild(), splitPaneItem, false);

				splitPaneItem.setSizePolicySilently(SplitSizePolicy.valueOf(splitDescriptor.getSizePolicy().name()));
				splitPaneItem.setReferenceChildSizeSilently(splitDescriptor.getReferenceChildSize());
			} else if (descriptor instanceof ViewGroupWrapper) {
				ViewGroupWrapper viewGroupDescriptor = (ViewGroupWrapper) descriptor;
				WorkSpaceLayoutViewGroup viewGroup = (WorkSpaceLayoutViewGroup) serverSideItem;
				addViews(viewGroup, viewGroupDescriptor);
				viewGroup.setPanelStateSilently(ViewGroupPanelState.valueOf(viewGroupDescriptor.getPanelState().name()));
			}
		} else { // this is a descriptor for a new item
			item = this.createTreeItemFromLayoutDescriptor(descriptor);
		}

		if (parent != null) {
			if (firstChild) {
				parent.setFirstChildSilently(item);
			} else {
				parent.setLastChildSilently(item);
			}
		}
		return item;
	}

	private WorkSpaceLayoutItem createTreeItemFromLayoutDescriptor(LayoutItemWrapper descriptor) {
		if (descriptor instanceof ViewGroupWrapper) {
			ViewGroupWrapper viewGroupWrapper = (ViewGroupWrapper) descriptor;
			var tabPanelItem = new WorkSpaceLayoutViewGroup(descriptor.getId(), viewGroupWrapper.isPersistent(), workSpaceLayout);
			this.addViews(tabPanelItem, viewGroupWrapper);
			return tabPanelItem;
		} else {
			SplitPaneWrapper splitDescriptor = (SplitPaneWrapper) descriptor;
			var splitPaneItem = new WorkSpaceLayoutSplitPane(
					descriptor.getId(),
					SplitDirection.valueOf(splitDescriptor.getSplitDirection().name()),
					SplitSizePolicy.valueOf(splitDescriptor.getSizePolicy().name()),
					splitDescriptor.getReferenceChildSize(),
					workSpaceLayout
			);
			splitPaneItem.setFirstChildSilently(this.addNewStructure(splitDescriptor.getFirstChild(), splitPaneItem, true));
			splitPaneItem.setLastChildSilently(this.addNewStructure(splitDescriptor.getLastChild(), splitPaneItem, false));
			return splitPaneItem;
		}
	}

	private void addViews(WorkSpaceLayoutViewGroup viewGroup, ViewGroupWrapper viewGroupDescriptor) {
		for (int i = 0; i < viewGroupDescriptor.getViewNames().size(); i++) {
			String viewName = viewGroupDescriptor.getViewNames().get(i);
			boolean selected = Objects.equals(viewName, viewGroupDescriptor.getSelectedViewName()) || viewGroupDescriptor.getSelectedViewName() == null && i == 0;
			WorkSpaceLayoutView view = viewsByName.get(viewName);
			if (view != null) {
				viewGroup.addViewSilently(view, i, selected);
			}
		}
	}

	private static LayoutItemWrapper createWrapperFromUi(DtoWorkSpaceLayoutItemWrapper child) {
		switch (child.getTypeId()) {
			case DtoWorkSpaceLayoutSplitItem.TYPE_ID -> {
				return new DtoWorkSpaceLayoutSplitItemWrapper(child.as(org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutSplitItemWrapper.class));
			}
			case DtoWorkSpaceLayoutViewGroupItem.TYPE_ID -> {
				return new DtoWorkSpaceLayoutViewGroupItemWrapper(child.as(org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutViewGroupItemWrapper.class));
			}
			default -> throw new IllegalArgumentException("Unknown layout item type " + child.getClass().getCanonicalName());
		}
	}

	private static LayoutApplyer.LayoutItemWrapper createWrapperFromDefinition(LayoutItemDefinition child) {
		if (child instanceof SplitPaneDefinition) {
			return new LayoutSplitPaneDefinitionWrapper((SplitPaneDefinition) child);
		} else if (child instanceof ViewGroupDefinition) {
			return new LayoutViewGroupDefinitionWrapper(((ViewGroupDefinition) child));
		} else {
			throw new IllegalArgumentException("Unknown layout item type " + child.getClass().getCanonicalName());
		}
	}

	private interface LayoutItemWrapper {
		String getId();
	}

	private interface SplitPaneWrapper extends LayoutItemWrapper {
		SplitDirection getSplitDirection();

		SplitSizePolicy getSizePolicy();

		double getReferenceChildSize();

		LayoutItemWrapper getFirstChild();

		LayoutItemWrapper getLastChild();
	}

	private interface ViewGroupWrapper extends LayoutItemWrapper {
		List<String> getViewNames();

		String getSelectedViewName();

		boolean isPersistent();

		ViewGroupPanelState getPanelState();
	}

	private static class DtoWorkSpaceLayoutSplitItemWrapper implements SplitPaneWrapper {
		private final org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutSplitItemWrapper item;

		public DtoWorkSpaceLayoutSplitItemWrapper(org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutSplitItemWrapper item) {
			this.item = item;
		}

		@Override
		public String getId() {
			return item.getId();
		}

		@Override
		public SplitDirection getSplitDirection() {
			return SplitDirection.valueOf(item.getSplitDirection().name());
		}

		@Override
		public SplitSizePolicy getSizePolicy() {
			return SplitSizePolicy.valueOf(item.getSizePolicy().name());
		}

		@Override
		public double getReferenceChildSize() {
			return item.getReferenceChildSize();
		}

		@Override
		public LayoutItemWrapper getFirstChild() {
			return createWrapperFromUi(item.getFirstChild());
		}

		@Override
		public LayoutItemWrapper getLastChild() {
			return createWrapperFromUi(item.getLastChild());
		}

	}

	private static class DtoWorkSpaceLayoutViewGroupItemWrapper implements ViewGroupWrapper {
		private final org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutViewGroupItemWrapper item;

		public DtoWorkSpaceLayoutViewGroupItemWrapper(org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutViewGroupItemWrapper item) {
			this.item = item;
		}

		@Override
		public List<String> getViewNames() {
			return item.getViewNames();
		}

		@Override
		public String getSelectedViewName() {
			return item.getSelectedViewName();
		}

		@Override
		public boolean isPersistent() {
			return item.isPersistent();
		}

		@Override
		public ViewGroupPanelState getPanelState() {
			return ViewGroupPanelState.valueOf(item.getPanelState().name());
		}

		@Override
		public String getId() {
			return item.getId();
		}
	}

	private static class LayoutSplitPaneDefinitionWrapper implements SplitPaneWrapper {
		private final SplitPaneDefinition item;

		public LayoutSplitPaneDefinitionWrapper(SplitPaneDefinition item) {
			this.item = item;
		}

		@Override
		public String getId() {
			return item.getId();
		}

		@Override
		public SplitDirection getSplitDirection() {
			return item.getSplitDirection();
		}

		@Override
		public SplitSizePolicy getSizePolicy() {
			return item.getSizePolicy();
		}

		@Override
		public double getReferenceChildSize() {
			return item.getReferenceChildSize();
		}

		@Override
		public LayoutItemWrapper getFirstChild() {
			return createWrapperFromDefinition(item.getFirstChild());
		}

		@Override
		public LayoutItemWrapper getLastChild() {
			return createWrapperFromDefinition(item.getLastChild());
		}

	}

	private static class LayoutViewGroupDefinitionWrapper implements ViewGroupWrapper {
		private final ViewGroupDefinition item;

		public LayoutViewGroupDefinitionWrapper(ViewGroupDefinition item) {
			this.item = item;
		}

		@Override
		public String getId() {
			return item.getId();
		}

		@Override
		public List<String> getViewNames() {
			return item.getViews().stream()
					.map(v -> v.getId())
					.collect(Collectors.toList());
		}

		@Override
		public String getSelectedViewName() {
			return item.getSelectedView() != null ? item.getSelectedView().getId() : null;
		}

		@Override
		public boolean isPersistent() {
			return item.isPersistent();
		}

		@Override
		public ViewGroupPanelState getPanelState() {
			return ViewGroupPanelState.valueOf(item.getPanelState().name());
		}

	}


}
