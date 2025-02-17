/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
package org.teamapps.ux.component.tree;

import org.teamapps.ux.component.node.TreeNode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class EagerNodesFilter implements Predicate<TreeNode> {

	private final TreeNode evaluationRootNode;
	private final Map<TreeNode, Boolean> includedInResultByNode = new HashMap<>();

	public EagerNodesFilter() {
		evaluationRootNode = null;
	}

	public EagerNodesFilter(TreeNode evaluationRootNode) {
		this.evaluationRootNode = evaluationRootNode;
	}

	@Override
	public boolean test(TreeNode node) {
		// CAUTION: cannot use computeIfAbsent() here since the recursive calls make updates to the map, causing ConcurrentModificationExceptions
		if (includedInResultByNode.containsKey(node)) {
			return includedInResultByNode.get(node);
		} else if (node.getParent() == null || node.getParent() == evaluationRootNode) { // this is a root node, so it is considered eager
			includedInResultByNode.put(node, true);
			return true;
		} else if (node.getParent().isLazyChildren() && !node.getParent().isExpanded()) { // parent has lazy children
			includedInResultByNode.put(node, false);
			return false;
		} else { // whatever holds for the parent, also holds for this node
			boolean parentIncluded = test(node.getParent());
			includedInResultByNode.put(node, parentIncluded);
			return parentIncluded;
		}
	}

}
