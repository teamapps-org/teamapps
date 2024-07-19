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
package org.teamapps.projector.component.common.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface TreeNode extends TreeNodeInfo {

	@Override
	TreeNode getParent();

	default int getDepth() {
		TreeNode node = this;
		int i = 0;
		while (node.getParent() != null) {
			i++;
			node = node.getParent();
		}
		return i;
	}

	default List<TreeNode> getPath() {
		return getPathToNode(this);
	}

	default boolean isDescendantOf(TreeNode potentialAncestor) {
		TreeNode ancestor = this.getParent();
		while (ancestor != null) {
			if (ancestor == potentialAncestor) {
				return true;
			}
			ancestor = ancestor.getParent();
		}
		return false;
	}

	default boolean isDescendantOf(Collection<? extends TreeNode> potentialAncestors) {
		TreeNode ancestor = this.getParent();
		while (ancestor != null) {
			if (potentialAncestors.contains(ancestor)) {
				return true;
			}
			ancestor = ancestor.getParent();
		}
		return false;
	}

	static List<TreeNode> getPathToNode(TreeNode node) {
		List<TreeNode> path = new ArrayList<>();
		TreeNode selfOrAncestor = node;
		while (selfOrAncestor != null) {
			path.add(selfOrAncestor);
			selfOrAncestor = selfOrAncestor.getParent();
		}
		Collections.reverse(path);
		return path;
	}

}
