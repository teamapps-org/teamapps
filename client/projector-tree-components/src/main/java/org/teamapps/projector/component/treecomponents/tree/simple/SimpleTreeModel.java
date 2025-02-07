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

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.component.treecomponents.tree.model.AbstractTreeModel;
import org.teamapps.projector.component.treecomponents.tree.model.ComboBoxModel;
import org.teamapps.projector.component.treecomponents.tree.model.TreeModelChangedEventData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleTreeModel<PAYLOAD> extends AbstractTreeModel<SimpleTreeNodeImpl<PAYLOAD>> implements ComboBoxModel<SimpleTreeNodeImpl<PAYLOAD>> {

	private final List<SimpleTreeNodeImpl<PAYLOAD>> nodes;
	private int maxResultNodes = Integer.MAX_VALUE;

	public SimpleTreeModel() {
		nodes = new ArrayList<>();
	}

	public SimpleTreeModel(List<SimpleTreeNodeImpl<PAYLOAD>> nodes) {
		this.nodes = new ArrayList<>(nodes);
	}

	public void setNodes(List<SimpleTreeNodeImpl<PAYLOAD>> nodes) {
		this.nodes.clear();
		this.nodes.addAll(nodes);
		onAllNodesChanged.fire();
	}

	public void addNode(SimpleTreeNodeImpl<PAYLOAD> node) {
		nodes.add(node);
		onChanged.fire(new TreeModelChangedEventData<>(Collections.emptyList(), Collections.singletonList(node)));
	}

	public void addNodes(List<SimpleTreeNodeImpl<PAYLOAD>> nodes) {
		this.nodes.addAll(nodes);
		onChanged.fire(new TreeModelChangedEventData<>(Collections.emptyList(), nodes));
	}

	public void removeChildren(Collection<SimpleTreeNodeImpl<PAYLOAD>> parents) {
		replaceChildren(parents, Collections.emptyList());
	}

	public void replaceChildren(Collection<SimpleTreeNodeImpl<PAYLOAD>> parentsToEmpty, List<SimpleTreeNodeImpl<PAYLOAD>> nodesToAdd) {
		List<SimpleTreeNodeImpl<PAYLOAD>> nodesToBeRemoved = nodes.stream()
				.filter(node -> node.isDescendantOf(parentsToEmpty))
				.collect(Collectors.toList());
		nodes.removeAll(nodesToBeRemoved);
		nodes.addAll(nodesToAdd);
		onChanged.fire(new TreeModelChangedEventData<>(nodesToBeRemoved, nodesToAdd));
	}

	public void updateNode(SimpleTreeNodeImpl<PAYLOAD> node) {
		onChanged.fire(new TreeModelChangedEventData<>(Collections.emptyList(), Collections.singletonList(node)));
	}

	public void relocateNode(SimpleTreeNodeImpl<PAYLOAD> node) {
		onChanged.fire(new TreeModelChangedEventData<>(Collections.singletonList(node), Collections.singletonList(node)));
	}

	public void removeNode(SimpleTreeNodeImpl<PAYLOAD> node) {
		nodes.remove(node);
		onChanged.fire(new TreeModelChangedEventData<>(Collections.singletonList(node), Collections.emptyList()));
	}

	public void removeAllNodes() {
		nodes.clear();
		onAllNodesChanged.fire();
	}

	public int getMaxResultNodes() {
		return maxResultNodes;
	}

	public void setMaxResultNodes(int maxResultNodes) {
		this.maxResultNodes = maxResultNodes;
	}

	@Override
	public List<SimpleTreeNodeImpl<PAYLOAD>> getRecords(String query) {
		if (StringUtils.isEmpty(query)) {
			return this.nodes.stream()
					.filter(new EagerNodesFilter())
					.limit(maxResultNodes)
					.collect(Collectors.toList());
		} else {
			List<SimpleTreeNodeImpl<PAYLOAD>> filteredTree = this.nodes.stream()
					.filter(node -> StringUtils.containsIgnoreCase(node.getCaption(), query))
					.flatMap(node -> ((List<SimpleTreeNodeImpl<PAYLOAD>>) (List) node.getPath()).stream())
					.distinct()
					.collect(Collectors.toList());
			List<SimpleTreeNodeImpl<PAYLOAD>> treeCopy = SimpleTreeNodeImpl.copyTree(filteredTree);
			treeCopy.forEach(node -> node.setExpanded(true));
			return treeCopy;
		}
	}

	@Override
	public List<SimpleTreeNodeImpl<PAYLOAD>> getRecords() {
		return this.getRecords(null);
	}

	@Override
	public List<SimpleTreeNodeImpl<PAYLOAD>> getChildRecords(SimpleTreeNodeImpl<PAYLOAD> parentRecord) {
		return nodes.stream()
				.filter(record -> record.getPath().contains(parentRecord) && record != parentRecord)
				.filter(new EagerNodesFilter(parentRecord))
				.collect(Collectors.toList());
	}
}
