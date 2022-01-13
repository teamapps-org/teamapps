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
package org.teamapps.ux.component.tree;

public interface TreeNodeInfo {

	Object getParent();

	/**
	 * Whether or not this node is selectable by the user.
	 */
	default boolean isSelectable() {
		return true;
	}

	/**
	 * Wheter or not this node is <b>initially</b> expanded. The user can of course change the expansion state.
	 */
	boolean isExpanded();

	/**
	 * Whether or not this node has children that are not sent to the client directly but need to be lazy-loaded.
	 */
	boolean isLazyChildren();

}
