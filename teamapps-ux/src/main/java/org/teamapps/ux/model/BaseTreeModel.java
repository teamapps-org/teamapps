package org.teamapps.ux.model;

import org.teamapps.ux.component.node.TreeNode;
import org.teamapps.ux.component.tree.TreeNodeInfo;
import org.teamapps.ux.component.tree.TreeNodeInfoExtractor;

import java.util.Collections;
import java.util.List;

public interface BaseTreeModel<RECORD> extends TreeNodeInfoExtractor<RECORD> {

	/**
	 * Get the child records of a node. This is currently only used for lazy parent nodes but might be used
	 * for other reasons later.
	 */
	default List<RECORD> getChildRecords(RECORD parentRecord) {
		return Collections.emptyList();
	}

	default TreeNodeInfo getTreeNodeInfo(RECORD record) {
		if (record instanceof TreeNode) {
			return (TreeNode) record;
		} else {
			return null;
		}
	}

}
