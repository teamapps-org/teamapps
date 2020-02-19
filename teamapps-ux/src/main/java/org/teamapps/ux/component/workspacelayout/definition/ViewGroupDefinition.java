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
package org.teamapps.ux.component.workspacelayout.definition;

import org.teamapps.ux.component.workspacelayout.ViewGroupPanelState;
import org.teamapps.ux.component.workspacelayout.WorkSpaceLayout;
import org.teamapps.ux.component.workspacelayout.WorkSpaceLayoutItem;
import org.teamapps.ux.component.workspacelayout.WorkSpaceLayoutViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewGroupDefinition extends LayoutItemDefinition {

	private final boolean persistent;
	private List<ViewDefinition> views;
	private ViewDefinition selectedView;
	private ViewGroupPanelState panelState;

	private SplitPaneDefinition parentSplitPane;

	public ViewGroupDefinition(String id, boolean persistent) {
		this(id, persistent, new ArrayList<>(), null, ViewGroupPanelState.NORMAL);
	}

	public ViewGroupDefinition(String id, boolean persistent, List<ViewDefinition> views, ViewDefinition selectedView, ViewGroupPanelState panelState) {
		super(id);
		this.persistent = persistent;
		this.views = views;
		this.selectedView = selectedView;
		this.panelState = panelState;
	}

	@Override
	public List<ViewDefinition> getAllViews() {
		return views;
	}

	@Override
	public List<LayoutItemDefinition> getSelfAndAncestors() {
		return Collections.singletonList(this);
	}

	@Override
	public WorkSpaceLayoutItem createHeavyWeightItem(WorkSpaceLayout workSpaceLayout) {
		WorkSpaceLayoutViewGroup layoutViewGroup = new WorkSpaceLayoutViewGroup(getId(), persistent, workSpaceLayout);
		layoutViewGroup.setPanelState(panelState);
		return layoutViewGroup;
	}

	public void switchPosition(ViewGroupDefinition otherViewGroup) {
		SplitPaneDefinition previousParent = otherViewGroup.getParentSplitPane();
		if (this.getId().equals(this.parentSplitPane.getFirstChild().getId())) {
			this.parentSplitPane.setFirstChild(otherViewGroup);
		} else {
			this.parentSplitPane.setLastChild(otherViewGroup);
		}
		if (otherViewGroup.getId().equals(previousParent.getFirstChild().getId())) {
			previousParent.setFirstChild(this);
		} else {
			previousParent.setLastChild(this);
		}
	}

	public void addViewDefinition(ViewDefinition viewDefinition) {
		views.add(viewDefinition);
	}

	public boolean isPersistent() {
		return persistent;
	}

	public List<ViewDefinition> getViews() {
		return views;
	}

	public void setViews(List<ViewDefinition> views) {
		this.views = views;
	}

	public ViewDefinition getSelectedView() {
		return selectedView;
	}

	public void setSelectedView(ViewDefinition selectedView) {
		this.selectedView = selectedView;
	}

	public ViewGroupPanelState getPanelState() {
		return panelState;
	}

	public void setPanelState(ViewGroupPanelState panelState) {
		this.panelState = panelState;
	}

	public SplitPaneDefinition getParentSplitPane() {
		return parentSplitPane;
	}

	protected void setParentSplitPane(SplitPaneDefinition parentSplitPane) {
		this.parentSplitPane = parentSplitPane;
	}
}
