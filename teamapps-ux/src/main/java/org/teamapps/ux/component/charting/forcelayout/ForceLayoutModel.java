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
package org.teamapps.ux.component.charting.forcelayout;

import org.teamapps.event.Event;

import java.util.*;
import java.util.stream.Collectors;

public class ForceLayoutModel<RECORD> {

	public Event<GraphChangeOperation<RECORD>> onNodesAdded = new Event<>();
	public Event<GraphChangeOperation<RECORD>> onNodesRemoved = new Event<>();

	private final List<ForceLayoutNode<RECORD>> displayedNodes = new ArrayList<>();
	private final List<ForceLayoutLink<RECORD>> displayedLinks = new ArrayList<>();
	private final Map<RECORD, ForceLayoutNode<RECORD>> graphNodeByNode = new HashMap<>();
	private final Set<RECORD> availableNodes = new HashSet<>();
	private final Set<LinkId<RECORD>> availableLinks = new HashSet<>();


	public boolean containsNode(RECORD record) {
		return availableNodes.contains(record);
	}

	public boolean containsNode(RECORD record, GraphChangeOperation<RECORD> changeOperation) {
		return (getGraphNode(record, changeOperation) != null);
	}

	public boolean containsLink(RECORD node1, RECORD node2) {
		return availableLinks.contains(getLinkId(node1, node2));
	}

	public void removeAll() {
		onNodesRemoved.fire(new GraphChangeOperation<>(displayedNodes, displayedLinks, true));
		availableNodes.clear();
		availableLinks.clear();
		displayedLinks.clear();
		displayedNodes.clear();
		graphNodeByNode.clear();
	}

	public void applyChange(GraphChangeOperation<RECORD> changeOperation) {
		if (changeOperation.containsAddOperations()) {
			changeOperation.getAddedLinks().forEach(link -> {
				availableLinks.add(getLinkId(link.getSource().getRecord(), link.getTarget().getRecord()));
			});
			changeOperation.getAddedNodes().forEach(graphNode -> {
				RECORD record = graphNode.getRecord();
				graphNodeByNode.put(record, graphNode);
				availableNodes.add(record);
			});
			displayedNodes.addAll(changeOperation.getAddedNodes());
			displayedLinks.addAll(changeOperation.getAddedLinks());
			onNodesAdded.fire(changeOperation);
		}
		if (changeOperation.containsRemoveOperations()) {
			changeOperation.getRemovedLinks().forEach(link -> {
				availableLinks.remove(getLinkId(link.getSource().getRecord(), link.getTarget().getRecord()));
			});
			changeOperation.getRemovedNodes().forEach(graphNode -> {
				RECORD record = graphNode.getRecord();
				graphNodeByNode.remove(record);
				availableNodes.remove(record);
			});
			displayedNodes.removeAll(changeOperation.getRemovedNodes());
			displayedLinks.removeAll(changeOperation.getRemovedLinks());
			onNodesRemoved.fire(changeOperation);
		}
	}

	public ForceLayoutNode<RECORD> getGraphNode(RECORD record, GraphChangeOperation<RECORD> changeOperation) {
		ForceLayoutNode<RECORD> graphNode = graphNodeByNode.get(record);
		if (graphNode == null) {
			graphNode = changeOperation.getGraphNodeByNode().get(record);
		}
		return graphNode;
	}

	public void removeHigherLevels(ForceLayoutNode<RECORD> graphNode) {
		List<ForceLayoutLink<RECORD>> connectedLinks = new ArrayList<>();
		Set<ForceLayoutNode<RECORD>> connectedNodes = new HashSet<>();
		int level = graphNode.getLevel();
		getConnectedNodesAndLinks(graphNode, connectedLinks, connectedNodes, true, level);
		applyChange(new GraphChangeOperation<>(new ArrayList<>(connectedNodes), connectedLinks, true));
	}

	private void getConnectedNodesAndLinks(ForceLayoutNode<RECORD> node, List<ForceLayoutLink<RECORD>> connectedLinks, Set<ForceLayoutNode<RECORD>> connectedNodes, boolean fullSubTree, int level) {
		List<ForceLayoutLink<RECORD>> links = displayedLinks.stream()
				.filter(link -> (link.getSource().equals(node) && (link.getTarget().getLevel() > level)
						|| (link.getTarget().equals(node) && (link.getSource()).getLevel() > level)))
				.collect(Collectors.toList());
		List<ForceLayoutNode<RECORD>> nodes = new ArrayList<>();
		links.forEach(link -> {
			if (!link.getSource().equals(node)) {
				if (!connectedNodes.contains(link.getSource())) {
					nodes.add(link.getSource());
				}
			} else {
				if (!connectedNodes.contains(link.getTarget())) {
					nodes.add(link.getTarget());
				}
			}
		});

		connectedLinks.addAll(links);
		connectedNodes.addAll(nodes);
		if (fullSubTree) {
			nodes.forEach(subNode -> getConnectedNodesAndLinks(subNode, connectedLinks, connectedNodes, true, level));
		}
	}

	public LinkId<RECORD> getLinkId(RECORD node1, RECORD node2) {
		return new LinkId<>(node1, node2);
	}
}
