package org.teamapps.ux.component.tree;

public class TreeNodeInfoImpl<PARENT> implements TreeNodeInfo {

	private final PARENT parent;
	private final boolean isExpanded;
	private final boolean lazyChildren;

	public TreeNodeInfoImpl(PARENT parent, boolean isExpanded, boolean lazyChildren) {
		this.parent = parent;
		this.isExpanded = isExpanded;
		this.lazyChildren = lazyChildren;
	}

	public TreeNodeInfoImpl(PARENT parent) {
		this(parent, false, false);
	}

	public TreeNodeInfoImpl(PARENT parent, boolean isExpanded) {
		this(parent, isExpanded, false);
	}

	@Override
	public PARENT getParent() {
		return parent;
	}

	@Override
	public boolean isExpanded() {
		return isExpanded;
	}

	@Override
	public boolean isLazyChildren() {
		return lazyChildren;
	}

}
