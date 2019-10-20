package org.teamapps.ux.component.charting.tree;

public class SideListExpandedOrCollapsedEvent<RECORD> {

	private final TreeGraphNode<RECORD> node;
	private final boolean expanded;

	public SideListExpandedOrCollapsedEvent(TreeGraphNode<RECORD> node, boolean expanded) {
		this.node = node;
		this.expanded = expanded;
	}

	public TreeGraphNode<RECORD> getNode() {
		return node;
	}

	public boolean isExpanded() {
		return expanded;
	}

}
