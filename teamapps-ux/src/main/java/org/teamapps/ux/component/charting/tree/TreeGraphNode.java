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
package org.teamapps.ux.component.charting.tree;

import java.util.List;

public class TreeGraphNode<RECORD> extends BaseTreeGraphNode<RECORD> {

	private TreeGraphNode<RECORD> parent;
	private boolean parentExpandable = false;
	private boolean parentExpanded = true;

	private boolean expanded = true;
	private boolean hasLazyChildren = false;

	private List<BaseTreeGraphNode<RECORD>> sideListNodes;
	private boolean sideListExpanded;

	public TreeGraphNode<RECORD> getParent() {
		return parent;
	}

	public TreeGraphNode<RECORD> setParent(TreeGraphNode<RECORD> parent) {
		this.parent = parent;
		return this;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public TreeGraphNode<RECORD> setExpanded(boolean expanded) {
		this.expanded = expanded;
		return this;
	}

	public boolean isHasLazyChildren() {
		return hasLazyChildren;
	}

	public TreeGraphNode<RECORD> setHasLazyChildren(boolean hasLazyChildren) {
		this.hasLazyChildren = hasLazyChildren;
		return this;
	}

	public List<BaseTreeGraphNode<RECORD>> getSideListNodes() {
		return sideListNodes;
	}

	public TreeGraphNode<RECORD> setSideListNodes(List<BaseTreeGraphNode<RECORD>> sideListNodes) {
		this.sideListNodes = sideListNodes;
		return this;
	}

	public boolean isSideListExpanded() {
		return sideListExpanded;
	}

	public TreeGraphNode<RECORD> setSideListExpanded(boolean sideListExpanded) {
		this.sideListExpanded = sideListExpanded;
		return this;
	}

	public boolean isParentExpandable() {
		return parentExpandable;
	}

	public void setParentExpandable(boolean parentExpandable) {
		this.parentExpandable = parentExpandable;
	}

	public boolean isParentExpanded() {
		return parentExpanded;
	}

	public void setParentExpanded(boolean parentExpanded) {
		this.parentExpanded = parentExpanded;
	}
}
