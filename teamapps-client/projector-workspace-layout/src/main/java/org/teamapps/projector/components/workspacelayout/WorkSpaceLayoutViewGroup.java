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

import org.teamapps.icons.Icon;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.components.workspacelayout.definition.ViewDefinition;
import org.teamapps.projector.components.workspacelayout.definition.ViewGroupDefinition;
import org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutItem;
import org.teamapps.projector.components.workspacelayout.dto.DtoWorkSpaceLayoutViewGroupItem;

import java.util.*;
import java.util.stream.Collectors;

public class WorkSpaceLayoutViewGroup extends WorkSpaceLayoutItem {

	private final boolean persistent;
	private final List<WorkSpaceLayoutView> views = new ArrayList<>();
	private WorkSpaceLayoutView selectedView;
	private ViewGroupPanelState panelState = ViewGroupPanelState.NORMAL;

	public WorkSpaceLayoutViewGroup(WorkSpaceLayout workSpaceLayout) {
		this(null, null, false, workSpaceLayout);
	}

	public WorkSpaceLayoutViewGroup(String id, WorkSpaceLayout workSpaceLayout) {
		this(id, null, null, false, workSpaceLayout);
	}

	public WorkSpaceLayoutViewGroup(String id, boolean persistent, WorkSpaceLayout workSpaceLayout) {
		this(id, null, null, persistent, workSpaceLayout);
	}

	public WorkSpaceLayoutViewGroup(boolean persistent, WorkSpaceLayout workSpaceLayout) {
		this(null, null, persistent, workSpaceLayout);
	}

	public WorkSpaceLayoutViewGroup(List<WorkSpaceLayoutView> views, WorkSpaceLayoutView selectedView, boolean persistent, WorkSpaceLayout workSpaceLayout) {
		this(null, views, selectedView, persistent, workSpaceLayout);
	}

	public WorkSpaceLayoutViewGroup(String id, List<WorkSpaceLayoutView> views, WorkSpaceLayoutView selectedView, boolean persistent, WorkSpaceLayout workSpaceLayout) {
		super(id != null ? id : UUID.randomUUID().toString(), workSpaceLayout);
		if (views != null) {
			views.forEach(view -> {
				this.views.add(view);
				view.setViewGroup(this);
			});
		}
		this.selectedView = selectedView;
		this.persistent = persistent;
		this.updateSelectedViewPanelWindowButtons();
	}

	private void updateSelectedViewPanelWindowButtons() {
		if (this.selectedView != null) {
			this.selectedView.updateWindowButtons(this.views.size() == 1);
		}
	}

	@Override
	public DtoWorkSpaceLayoutItem createUiItem() {
		List<String> viewNames = getAllViews().stream()
				.map(WorkSpaceLayoutView::getId)
				.collect(Collectors.toList());
		DtoWorkSpaceLayoutViewGroupItem item = new DtoWorkSpaceLayoutViewGroupItem(getId(), viewNames);
		item.setSelectedViewName(selectedView != null ? selectedView.getId() : null);
		item.setPanelState(panelState.toUiViewGroupPanelState());
		item.setPersistent(persistent);
		return item;
	}

	@Override
	public ViewGroupDefinition createLayoutDefinitionItem() {
		List<ViewDefinition> viewDefinitions = views.stream().map(view -> view.createViewDefinition()).collect(Collectors.toList());
		ViewDefinition selectedViewDefinition = selectedView != null ? selectedView.createViewDefinition() : null;
		return new ViewGroupDefinition(getId(), persistent, viewDefinitions, selectedViewDefinition, panelState);
	}

	@Override
	public List<WorkSpaceLayoutView> getAllViews() {
		return getViews();
	}

	@Override
	public List<WorkSpaceLayoutItem> getSelfAndAncestors() {
		return Collections.singletonList(this);
	}

	@Override
	protected void callHandleRemovedOnChildren() {
		views.forEach(view -> view.fireOnRemoved());
	}

	public List<WorkSpaceLayoutView> getViews() {
		return Collections.unmodifiableList(views);
	}

	public WorkSpaceLayoutView addView(Icon<?, ?> icon, String title, Component component) {
		return addView(icon, title, component, views.size(), false);
	}

	public WorkSpaceLayoutView addView(Icon<?, ?> icon, String title, Component component, int index, boolean select) {
		WorkSpaceLayoutView view = new WorkSpaceLayoutView(getWorkSpaceLayout(), icon, title, component);
		addView(view, index, select);
		return view;
	}

	public WorkSpaceLayoutViewGroup addView(WorkSpaceLayoutView view) {
		addView(view, views.size(), false);
		return this;
	}

	public WorkSpaceLayoutViewGroup addView(WorkSpaceLayoutView view, int index, boolean select) {
		addViewSilently(view, index, select);
		getWorkSpaceLayout().handleViewAddedToGroup(this, view, select);
		if (selectedView == null) {
			selectedView = view;
		}
		return this;
	}

	public WorkSpaceLayoutViewGroup removeView(WorkSpaceLayoutView view) {
		boolean removed = this.views.remove(view);
		view.setViewGroup(null);
		if (removed && getWorkSpaceLayout() != null) {
			getWorkSpaceLayout().handleViewRemovedViaApi(this, view);
		}
		if (views.size() == 0) {
			selectedView = null;
		}
		return this;
	}

	public WorkSpaceLayoutView getSelectedView() {
		return selectedView;
	}

	public ViewGroupPanelState getPanelState() {
		return panelState;
	}

	public void setPanelState(ViewGroupPanelState panelState) {
		this.panelState = panelState;
		getWorkSpaceLayout().handleViewGroupPanelStateChangedViaApi(this, panelState);
	}

	@Override
	public String toString() {
		return "WorkSpaceLayoutViewGroup{" +
				"\n \"id\" : '" + getId() + '\'' +
				",\n \"viewNames\" : " + views.stream().map(WorkSpaceLayoutView::getId).collect(Collectors.joining(", ")) +
				",\n \"selectedViewName\" : " + (selectedView != null ? selectedView.getId() : null) +
				",\n \"persistent\" : " + persistent +
				",\n \"panelState\" : " + panelState +
				"\n}";
	}

	/*package-private*/ void handleViewSelectedByClient(String viewName) {
		this.selectedView = getViewById(viewName);
		this.updateSelectedViewPanelWindowButtons();
	}

	/*package-private*/ WorkSpaceLayoutView getViewById(String viewName) {
		return views.stream()
				.filter(view -> Objects.equals(view.getId(), viewName))
				.findFirst().orElse(null);
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setSelectedView(WorkSpaceLayoutView view) {
		this.selectedView = view;
		if (this.getWorkSpaceLayout() != null) {
			this.getWorkSpaceLayout().handleViewSelectedViaApi(this, view);
		}
	}

	// ============== Internal API ======================

	void handleViewRemovedByClient(WorkSpaceLayoutView view) {
		this.views.remove(view);
		view.setViewGroup(null);
	}

	void addViewSilently(WorkSpaceLayoutView view, int index, boolean select) {
		this.views.remove(view);
		this.views.add(Math.min(views.size(), index), view);
		view.setViewGroup(this);
		if (select || this.views.size() == 1) {
			this.selectedView = view;
			updateSelectedViewPanelWindowButtons();
		}
	}

	void removeViewSilently(WorkSpaceLayoutView view) {
		this.views.remove(view);
	}

	void setSelectedViewSilently(WorkSpaceLayoutView selectedView) {
		this.selectedView = selectedView;
		updateSelectedViewPanelWindowButtons();
	}

	void setPanelStateSilently(ViewGroupPanelState panelState) {
		this.panelState = panelState;
	}
}
