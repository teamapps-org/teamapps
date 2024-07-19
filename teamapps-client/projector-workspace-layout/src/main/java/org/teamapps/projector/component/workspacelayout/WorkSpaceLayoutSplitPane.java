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

import org.teamapps.projector.component.essential.dto.SplitDirection;
import org.teamapps.projector.component.essential.dto.SplitSizePolicy;
import org.teamapps.projector.component.workspacelayout.definition.SplitPaneDefinition;
import org.teamapps.projector.component.workspacelayout.dto.DtoWorkSpaceLayoutItem;
import org.teamapps.projector.component.workspacelayout.dto.DtoWorkSpaceLayoutSplitItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkSpaceLayoutSplitPane extends WorkSpaceLayoutItem {

	private final SplitDirection splitDirection;
	private SplitSizePolicy sizePolicy;
	private double referenceChildSize;
	private WorkSpaceLayoutItem firstChild;
	private WorkSpaceLayoutItem lastChild;

	public WorkSpaceLayoutSplitPane(String id, SplitDirection splitDirection, SplitSizePolicy sizePolicy, double referenceChildSize, WorkSpaceLayout workSpaceLayout) {
		this(id, splitDirection, sizePolicy, referenceChildSize, null, null, workSpaceLayout);
	}

	public WorkSpaceLayoutSplitPane(SplitDirection splitDirection, SplitSizePolicy sizePolicy, float referenceChildSize, WorkSpaceLayout workSpaceLayout) {
		this(splitDirection, sizePolicy, referenceChildSize, null, null, workSpaceLayout);
	}

	public WorkSpaceLayoutSplitPane(SplitDirection splitDirection, SplitSizePolicy sizePolicy, float referenceChildSize, WorkSpaceLayoutItem firstChild, WorkSpaceLayoutItem lastChild,
	                                WorkSpaceLayout workSpaceLayout) {
		this(null, splitDirection, sizePolicy, referenceChildSize, firstChild, lastChild, workSpaceLayout);
	}

	public WorkSpaceLayoutSplitPane(
			String id,
			SplitDirection splitDirection,
			SplitSizePolicy sizePolicy,
			double referenceChildSize,
			WorkSpaceLayoutItem firstChild,
			WorkSpaceLayoutItem lastChild,
			WorkSpaceLayout workSpaceLayout
	) {
		super(id != null ? id : UUID.randomUUID().toString(), workSpaceLayout);
		this.splitDirection = splitDirection;
		this.firstChild = firstChild;
		if (this.firstChild != null) {
			this.firstChild.setParent(this);
		}
		this.lastChild = lastChild;
		if (this.lastChild != null) {
			this.lastChild.setParent(this);
		}
		this.sizePolicy = sizePolicy;
		this.referenceChildSize = referenceChildSize;
	}

	@Override
	public List<WorkSpaceLayoutView> getAllViews() {
		List<WorkSpaceLayoutView> allViews = new ArrayList<>();
		if (firstChild != null) {
			allViews.addAll(firstChild.getAllViews());
		}
		if (lastChild != null) {
			allViews.addAll(lastChild.getAllViews());
		}
		return allViews;
	}

	@Override
	public List<WorkSpaceLayoutItem> getSelfAndAncestors() {
		ArrayList<WorkSpaceLayoutItem> result = new ArrayList<>();
		result.add(this);
		if (firstChild != null) {
			result.addAll(firstChild.getSelfAndAncestors());
		}
		if (lastChild != null) {
			result.addAll(lastChild.getSelfAndAncestors());
		}
		return result;
	}

	@Override
	public DtoWorkSpaceLayoutItem createUiItem() {
		DtoWorkSpaceLayoutSplitItem item = new DtoWorkSpaceLayoutSplitItem(
				getId(),
				splitDirection,
				firstChild != null ? firstChild.createUiItem() : null,
				lastChild != null ? lastChild.createUiItem() : null
		);
		item.setSizePolicy(sizePolicy);
		item.setReferenceChildSize(referenceChildSize);
		return item;
	}

	@Override
	public SplitPaneDefinition createLayoutDefinitionItem() {
		return new SplitPaneDefinition(getId(), splitDirection, sizePolicy, referenceChildSize, firstChild.createLayoutDefinitionItem(), lastChild.createLayoutDefinitionItem());
	}

	@Override
	protected void callHandleRemovedOnChildren() {
		if (firstChild != null) {
			firstChild.handleRemoved();
		}
		if (lastChild != null) {
			lastChild.handleRemoved();
		}
	}

	public SplitDirection getSplitDirection() {
		return splitDirection;
	}

	public WorkSpaceLayoutItem getFirstChild() {
		return firstChild;
	}

	public WorkSpaceLayoutSplitPane setFirstChild(WorkSpaceLayoutItem firstChild) {
		this.firstChild = firstChild;
		if (this.firstChild != null) {
			this.firstChild.setParent(this);
		}
		// TODO command(s)
		return this;
	}

	public WorkSpaceLayoutItem getLastChild() {
		return lastChild;
	}

	public WorkSpaceLayoutSplitPane setLastChild(WorkSpaceLayoutItem lastChild) {
		this.lastChild = lastChild;
		if (this.lastChild != null) {
			this.lastChild.setParent(this);
		}
		return this;
		// TODO command(s)
	}

	public WorkSpaceLayoutSplitPane setChildren(WorkSpaceLayoutItem firstChild, WorkSpaceLayoutItem lastChild) {
		this.firstChild = firstChild;
		if (this.firstChild != null) {
			this.firstChild.setParent(this);
		}
		this.lastChild = lastChild;
		if (this.lastChild != null) {
			this.lastChild.setParent(this);
		}
		return this;
		// TODO command(s)
	}

	public SplitSizePolicy getSizePolicy() {
		return sizePolicy;
	}

	public void setSizePolicy(SplitSizePolicy sizePolicy) {
		this.sizePolicy = sizePolicy;
		if (this.getWorkSpaceLayout() != null) {
			this.getWorkSpaceLayout().handleSplitPaneSizingChanged(sizePolicy, referenceChildSize);
		}
	}

	public double getReferenceChildSize() {
		return referenceChildSize;
	}

	public void setReferenceChildSize(float referenceChildSize) {
		this.referenceChildSize = referenceChildSize;
		if (this.getWorkSpaceLayout() != null) {
			this.getWorkSpaceLayout().handleSplitPaneSizingChanged(sizePolicy, referenceChildSize);
		}
	}

	public void setSizing(SplitSizePolicy sizePolicy, float referenceChildSize) {
		this.sizePolicy = sizePolicy;
		this.referenceChildSize = referenceChildSize;
		if (this.getWorkSpaceLayout() != null) {
			this.getWorkSpaceLayout().handleSplitPaneSizingChanged(sizePolicy, referenceChildSize);
		}
	}

	@Override
	public String toString() {
		return "WorkSpaceLayoutSplitPane{"
				+ "\n \"id\" : '" + getId() + '\''
				+ ",\n \"splitDirection\" : " + splitDirection
				+ ",\n \"sizePolicy\" : " + sizePolicy
				+ ",\n \"referenceChildSize\" : " + referenceChildSize
				+ ",\n \"firstChild\" : " + (firstChild != null ? firstChild.toString().replace("\n", "\n ") : null)
				+ ",\n \"lastChild\" : " + (lastChild != null ? lastChild.toString().replace("\n", "\n ") : null) + "\n}";
	}

	// ============== Client-side actions handling ======================

	/*package-private*/ void setSizePolicySilently(SplitSizePolicy sizePolicy) {
		this.sizePolicy = sizePolicy;
	}

	/*package-private*/ void setReferenceChildSizeSilently(double referenceChildSize) {
		this.referenceChildSize = referenceChildSize;
	}

	/*package-private*/ void setFirstChildSilently(WorkSpaceLayoutItem item) {
		this.firstChild = item;
		if (this.firstChild != null) {
			this.firstChild.setParent(this);
		}
	}

	/*package-private*/ void setLastChildSilently(WorkSpaceLayoutItem item) {
		this.lastChild = item;
		if (this.lastChild != null) {
			this.lastChild.setParent(this);
		}
	}
}
