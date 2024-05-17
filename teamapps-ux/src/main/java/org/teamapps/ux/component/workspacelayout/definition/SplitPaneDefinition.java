/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import org.teamapps.ux.component.splitpane.SplitSizePolicy;
import org.teamapps.ux.component.workspacelayout.SplitDirection;
import org.teamapps.ux.component.workspacelayout.WorkSpaceLayout;
import org.teamapps.ux.component.workspacelayout.WorkSpaceLayoutItem;
import org.teamapps.ux.component.workspacelayout.WorkSpaceLayoutSplitPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplitPaneDefinition extends LayoutItemDefinition {

	private final SplitDirection splitDirection;
	private SplitSizePolicy sizePolicy;
	private float referenceChildSize;
	private LayoutItemDefinition firstChild;
	private LayoutItemDefinition lastChild;

	public SplitPaneDefinition(String id, SplitDirection splitDirection) {
		super(id);
		this.splitDirection = splitDirection;
	}

	public SplitPaneDefinition(String id, SplitDirection splitDirection, SplitSize splitSize, LayoutItemDefinition firstChild, LayoutItemDefinition lastChild) {
		super(id);
		this.splitDirection = splitDirection;
		this.sizePolicy = splitSize.getSizePolicy();
		this.referenceChildSize = splitSize.getSize();
		this.firstChild = firstChild;
		this.lastChild = lastChild;
		updateViewGroupParents();
	}

	public SplitPaneDefinition(String id, SplitDirection splitDirection, SplitSizePolicy sizePolicy, float referenceChildSize, LayoutItemDefinition firstChild, LayoutItemDefinition lastChild) {
		super(id);
		this.splitDirection = splitDirection;
		this.sizePolicy = sizePolicy;
		this.referenceChildSize = referenceChildSize;
		this.firstChild = firstChild;
		this.lastChild = lastChild;
		updateViewGroupParents();
	}

	private void updateViewGroupParents() {
		if (firstChild != null && firstChild instanceof ViewGroupDefinition) {
			((ViewGroupDefinition) firstChild).setParentSplitPane(this);
		}
		if (lastChild != null && lastChild instanceof ViewGroupDefinition) {
			((ViewGroupDefinition) lastChild).setParentSplitPane(this);
		}
	}

	@Override
	public List<ViewDefinition> getAllViews() {
		List<ViewDefinition> allViews = new ArrayList<>();
		if (firstChild != null) {
			allViews.addAll(firstChild.getAllViews());
		}
		if (lastChild != null) {
			allViews.addAll(lastChild.getAllViews());
		}
		return allViews;
	}

	@Override
	public List<ViewDefinition> getEffectivelyVisibleViews() {
		List<ViewDefinition> effectivelyVisibleViews = new ArrayList<>();
		if (firstChild != null) {
			effectivelyVisibleViews.addAll(firstChild.getEffectivelyVisibleViews());
		}
		if (lastChild != null) {
			effectivelyVisibleViews.addAll(lastChild.getEffectivelyVisibleViews());
		}
		return effectivelyVisibleViews;
	}

	@Override
	public List<LayoutItemDefinition> getSelfAndAncestors() {
		ArrayList<LayoutItemDefinition> result = new ArrayList<>();
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
	public WorkSpaceLayoutItem createHeavyWeightItem(WorkSpaceLayout workSpaceLayout) {
		WorkSpaceLayoutSplitPane layoutSplitPane = new WorkSpaceLayoutSplitPane(getId(), splitDirection, sizePolicy, referenceChildSize, workSpaceLayout);
		if (firstChild != null) {
			layoutSplitPane.setFirstChild(firstChild.createHeavyWeightItem(workSpaceLayout));
		}
		if (lastChild != null) {
			layoutSplitPane.setLastChild(lastChild.createHeavyWeightItem(workSpaceLayout));
		}
		return layoutSplitPane;
	}

	public SplitDirection getSplitDirection() {
		return splitDirection;
	}

	public SplitSizePolicy getSizePolicy() {
		return sizePolicy;
	}

	public void setSizePolicy(SplitSizePolicy sizePolicy) {
		this.sizePolicy = sizePolicy;
	}

	public float getReferenceChildSize() {
		return referenceChildSize;
	}

	public void setReferenceChildSize(float referenceChildSize) {
		this.referenceChildSize = referenceChildSize;
	}

	public LayoutItemDefinition getFirstChild() {
		return firstChild;
	}

	public void setFirstChild(LayoutItemDefinition firstChild) {
		this.firstChild = firstChild;
		updateViewGroupParents();
	}

	public LayoutItemDefinition getLastChild() {
		return lastChild;
	}

	public void setLastChild(LayoutItemDefinition lastChild) {
		this.lastChild = lastChild;
		updateViewGroupParents();
	}

	public String getComparableDefinition() {
		return "SplitPaneDefinition{" +
				"splitDirection=" + splitDirection +
				", sizePolicy=" + sizePolicy +
				", referenceChildSize=" + referenceChildSize +
				", firstChild=" + getChildLayoutDefinition(firstChild) +
				", lastChild=" + getChildLayoutDefinition(lastChild) +
				'}';
	}

	private String getChildLayoutDefinition(LayoutItemDefinition layoutItemDefinition) {
		if (layoutItemDefinition == null) {
			return null;
		}
		if (layoutItemDefinition instanceof SplitPaneDefinition) {
			SplitPaneDefinition splitPaneDefinition = (SplitPaneDefinition) layoutItemDefinition;
			return splitPaneDefinition.getComparableDefinition();
		} else {
			return layoutItemDefinition.getClass().getName();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SplitPaneDefinition that = (SplitPaneDefinition) o;
		return getComparableDefinition().equals(that.getComparableDefinition());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getComparableDefinition());
	}
}
