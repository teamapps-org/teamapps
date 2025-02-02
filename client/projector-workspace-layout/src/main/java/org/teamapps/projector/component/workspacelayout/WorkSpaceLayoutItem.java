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
package org.teamapps.projector.component.workspacelayout;

import org.teamapps.projector.component.workspacelayout.definition.LayoutItemDefinition;
import org.teamapps.projector.component.workspacelayout.DtoWorkSpaceLayoutItem;
import org.teamapps.projector.event.ProjectorEvent;

import java.util.List;
import java.util.UUID;

public abstract class WorkSpaceLayoutItem {

	public final ProjectorEvent<Void> onRemoved = new ProjectorEvent<>();

	private final String id;
	private final WorkSpaceLayout workSpaceLayout;
	private WorkSpaceLayoutSplitPane parent;

	public WorkSpaceLayoutItem(String id, WorkSpaceLayout workSpaceLayout) {
		this.id = id;
		this.workSpaceLayout = workSpaceLayout;
	}

	public WorkSpaceLayoutItem(WorkSpaceLayout workSpaceLayout) {
		this(UUID.randomUUID().toString(), workSpaceLayout);
	}

	public String getId() {
		return id;
	}

	public abstract DtoWorkSpaceLayoutItem createDtoItem();

	public abstract LayoutItemDefinition createLayoutDefinitionItem();

	protected WorkSpaceLayout getWorkSpaceLayout() {
		return workSpaceLayout;
	}

	public WorkSpaceLayoutSplitPane getParent() {
		return parent;
	}

	public void setParent(WorkSpaceLayoutSplitPane parent) {
		this.parent = parent;
	}

	public abstract List<WorkSpaceLayoutView> getAllViews();

	public abstract List<WorkSpaceLayoutItem> getSelfAndAncestors();

	/*package-private*/ void handleRemoved() {
		this.callHandleRemovedOnChildren();
		onRemoved.fire(null);
	}

	protected abstract void callHandleRemovedOnChildren();
	
}
