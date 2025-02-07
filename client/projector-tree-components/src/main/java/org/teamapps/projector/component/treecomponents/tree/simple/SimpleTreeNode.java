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
package org.teamapps.projector.component.treecomponents.tree.simple;

import org.teamapps.projector.component.treecomponents.tree.model.TreeNodeInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface SimpleTreeNode<T extends SimpleTreeNode<T>> {

	TreeNodeInfo<T> getTreeNodeInfo();

	default SimpleTreeNode<T> getParent() {return getTreeNodeInfo().getParent();}

	default boolean isExpanded() {return getTreeNodeInfo().isExpanded();}

	default boolean isSelectable() {return getTreeNodeInfo().isSelectable();}

	default boolean isLazyChildren() {return getTreeNodeInfo().isLazyChildren();}

	default int getDepth() {
		SimpleTreeNode<T> node = this;
		int i = 0;
		while (node.getParent() != null) {
			i++;
			node = node.getParent();
		}
		return i;
	}

	default List<SimpleTreeNode<T>> getPath() {
		return getPathToNode(this);
	}

	default boolean isDescendantOf(SimpleTreeNode<T> potentialAncestor) {
		SimpleTreeNode<T> ancestor = this.getParent();
		while (ancestor != null) {
			if (ancestor == potentialAncestor) {
				return true;
			}
			ancestor = ancestor.getParent();
		}
		return false;
	}

	default boolean isDescendantOf(Collection<? extends SimpleTreeNode<T>> potentialAncestors) {
		SimpleTreeNode<T> ancestor = this.getParent();
		while (ancestor != null) {
			if (potentialAncestors.contains(ancestor)) {
				return true;
			}
			ancestor = ancestor.getParent();
		}
		return false;
	}

	static <T extends SimpleTreeNode<T>> List<SimpleTreeNode<T>> getPathToNode(SimpleTreeNode<T> node) {
		List<SimpleTreeNode<T>> path = new ArrayList<>();
		SimpleTreeNode<T> selfOrAncestor = node;
		while (selfOrAncestor != null) {
			path.add(selfOrAncestor);
			selfOrAncestor = selfOrAncestor.getParent();
		}
		Collections.reverse(path);
		return path;
	}

}
