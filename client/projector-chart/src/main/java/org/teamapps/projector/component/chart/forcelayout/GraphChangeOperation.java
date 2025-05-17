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
package org.teamapps.projector.component.chart.forcelayout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphChangeOperation<RECORD> {

	private final List<ForceLayoutNode<RECORD>> addedNodes = new ArrayList<>();
	private List<ForceLayoutLink<RECORD>> addedLinks = new ArrayList<>();
	private List<ForceLayoutNode<RECORD>> removedNodes = new ArrayList<>();
	private List<ForceLayoutLink<RECORD>> removedLinks = new ArrayList<>();

	private final Map<RECORD, ForceLayoutNode<RECORD>> graphNodeByNode = new HashMap<>();

	public GraphChangeOperation() {
	}

	public GraphChangeOperation(List<ForceLayoutNode<RECORD>> nodes, List<ForceLayoutLink<RECORD>> links, boolean remove) {
		if (remove) {
			this.removedNodes = nodes;
			this.removedLinks = links;
		} else {
			addedNodes.forEach(record -> addNode(record));
			this.addedLinks = links;
		}
	}

	public boolean containsAddOperations() {
		return !addedNodes.isEmpty() || !addedLinks.isEmpty();
	}

	public boolean containsRemoveOperations() {
		return !removedNodes.isEmpty() || !removedLinks.isEmpty();
	}

	public void addNode(ForceLayoutNode<RECORD> record) {
		addedNodes.add(record);
		graphNodeByNode.put(record.getRecord(), record);
	}

	public void addLink(ForceLayoutLink<RECORD> link) {
		addedLinks.add(link);
	}

	public List<ForceLayoutNode<RECORD>> getAddedNodes() {
		return addedNodes;
	}

	public void setAddedNodes(List<ForceLayoutNode<RECORD>> addedNodes) {
		addedNodes.forEach(record -> addNode(record));
	}

	public List<ForceLayoutLink<RECORD>> getAddedLinks() {
		return addedLinks;
	}

	public void setAddedLinks(List<ForceLayoutLink<RECORD>> addedLinks) {
		this.addedLinks = addedLinks;
	}

	public List<ForceLayoutNode<RECORD>> getRemovedNodes() {
		return removedNodes;
	}

	public void setRemovedNodes(List<ForceLayoutNode<RECORD>> removedNodes) {
		this.removedNodes = removedNodes;
	}

	public List<ForceLayoutLink<RECORD>> getRemovedLinks() {
		return removedLinks;
	}

	public void setRemovedLinks(List<ForceLayoutLink<RECORD>> removedLinks) {
		this.removedLinks = removedLinks;
	}

	public Map<RECORD, ForceLayoutNode<RECORD>> getGraphNodeByNode() {
		return graphNodeByNode;
	}
}
