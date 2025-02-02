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
package org.teamapps.projector.component.trivial.tree.model;

public class TreeNodeInfo<RECORD> {

	private final RECORD parent;
	private final boolean isExpanded;
	private final boolean selectable;
	private final boolean lazyChildren;

	public TreeNodeInfo(RECORD parent) {
		this(parent, false, true, false);
	}

	public TreeNodeInfo(RECORD parent, boolean isExpanded) {
		this(parent, isExpanded, true, false);
	}

	public TreeNodeInfo(RECORD parent, boolean isExpanded, boolean selectable) {
		this(parent, isExpanded, selectable, false);
	}

	public TreeNodeInfo(RECORD parent, boolean isExpanded, boolean selectable, boolean lazyChildren) {
		this.parent = parent;
		this.isExpanded = isExpanded;
		this.selectable = selectable;
		this.lazyChildren = lazyChildren;
	}

	public RECORD getParent() {
		return parent;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public boolean isLazyChildren() {
		return lazyChildren;
	}

}
