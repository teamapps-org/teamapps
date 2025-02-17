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
