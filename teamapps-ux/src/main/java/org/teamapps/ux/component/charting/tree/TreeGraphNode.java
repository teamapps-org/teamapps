package org.teamapps.ux.component.charting.tree;

import java.util.List;

public class TreeGraphNode<RECORD> extends BaseTreeGraphNode<RECORD> {

	private TreeGraphNode<RECORD> parent;
	private boolean parentCollapsible = false;
	private boolean parentExpanded = true;

	private boolean expanded;
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

	public boolean isParentCollapsible() {
		return parentCollapsible;
	}

	public void setParentCollapsible(boolean parentCollapsible) {
		this.parentCollapsible = parentCollapsible;
	}

	public boolean isParentExpanded() {
		return parentExpanded;
	}

	public void setParentExpanded(boolean parentExpanded) {
		this.parentExpanded = parentExpanded;
	}
}