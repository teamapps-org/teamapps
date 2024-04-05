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
package org.teamapps.projector.components.trivial.tree.model;

import java.util.List;

public class TreeModelChangedEventData<RECORD> {
	private List<RECORD> removedNodes;
	private List<RECORD> addedOrUpdatedNodes;

	public TreeModelChangedEventData(List<RECORD> removedNodes, List<RECORD> addedOrUpdatedNodes) {
		this.removedNodes = removedNodes;
		this.addedOrUpdatedNodes = addedOrUpdatedNodes;
	}

	public List<RECORD> getRemovedNodes() {
		return removedNodes;
	}

	public void setRemovedNodes(List<RECORD> removedNodes) {
		this.removedNodes = removedNodes;
	}

	public List<RECORD> getAddedOrUpdatedNodes() {
		return addedOrUpdatedNodes;
	}

	public void setAddedOrUpdatedNodes(List<RECORD> addedOrUpdatedNodes) {
		this.addedOrUpdatedNodes = addedOrUpdatedNodes;
	}
}
