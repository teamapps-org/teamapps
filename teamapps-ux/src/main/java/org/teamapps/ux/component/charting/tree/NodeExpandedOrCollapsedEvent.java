package org.teamapps.ux.component.charting.tree;

public class NodeExpandedOrCollapsedEvent<RECORD> {

	private final TreeGraphNode<RECORD> node;
	private final boolean expanded;
	private final boolean lazyLoad;

	public NodeExpandedOrCollapsedEvent(TreeGraphNode<RECORD> node, boolean expanded, boolean lazyLoad) {
		this.node = node;
		this.expanded = expanded;
		this.lazyLoad = lazyLoad;
	}

	public TreeGraphNode<RECORD> getNode() {
		return node;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public boolean isLazyLoad() {
		return lazyLoad;
	}
}
