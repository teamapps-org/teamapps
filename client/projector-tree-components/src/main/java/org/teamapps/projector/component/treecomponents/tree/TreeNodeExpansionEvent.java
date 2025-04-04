package org.teamapps.projector.component.treecomponents.tree;

public class TreeNodeExpansionEvent<RECORD> {

	private final RECORD node;
	private final boolean expanded;

	public TreeNodeExpansionEvent(RECORD node, boolean expanded) {
		this.node = node;
		this.expanded = expanded;
	}

	public RECORD getNode() {
		return node;
	}

	public boolean isExpanded() {
		return expanded;
	}
}
