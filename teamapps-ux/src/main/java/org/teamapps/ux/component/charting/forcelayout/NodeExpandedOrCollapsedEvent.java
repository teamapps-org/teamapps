package org.teamapps.ux.component.charting.forcelayout;

public class NodeExpandedOrCollapsedEvent<RECORD> {

	private final ForceLayoutNode<RECORD> node;
	private final boolean expanded;

	public NodeExpandedOrCollapsedEvent(ForceLayoutNode<RECORD> node, boolean expanded) {
		this.node = node;
		this.expanded = expanded;
	}

	public ForceLayoutNode<RECORD> getNode() {
		return node;
	}

	public boolean isExpanded() {
		return expanded;
	}
}