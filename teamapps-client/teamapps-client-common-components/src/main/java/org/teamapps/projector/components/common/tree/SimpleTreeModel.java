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
package org.teamapps.projector.components.common.tree;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.projector.components.common.model.AbstractTreeModel;
import org.teamapps.projector.components.common.model.ComboBoxModel;
import org.teamapps.projector.components.common.model.TreeModelChangedEventData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleTreeModel<PAYLOAD> extends AbstractTreeModel<BaseTemplateTreeNode<PAYLOAD>> implements ComboBoxModel<BaseTemplateTreeNode<PAYLOAD>> {

	private final List<BaseTemplateTreeNode<PAYLOAD>> nodes;
	private int maxResultNodes = Integer.MAX_VALUE;

	public SimpleTreeModel() {
		nodes = new ArrayList<>();
	}

	public SimpleTreeModel(List<BaseTemplateTreeNode<PAYLOAD>> nodes) {
		this.nodes = new ArrayList<>(nodes);
	}

	public void setNodes(List<BaseTemplateTreeNode<PAYLOAD>> nodes) {
		this.nodes.clear();
		this.nodes.addAll(nodes);
		onAllNodesChanged.fire();
	}

	public void addNode(BaseTemplateTreeNode<PAYLOAD> node) {
		nodes.add(node);
		onChanged.fire(new TreeModelChangedEventData<>(Collections.emptyList(), Collections.singletonList(node)));
	}

	public void addNodes(List<BaseTemplateTreeNode<PAYLOAD>> nodes) {
		this.nodes.addAll(nodes);
		onChanged.fire(new TreeModelChangedEventData<>(Collections.emptyList(), nodes));
	}

	public void removeChildren(Collection<BaseTemplateTreeNode<PAYLOAD>> parents) {
		replaceChildren(parents, Collections.emptyList());
	}

	public void replaceChildren(Collection<BaseTemplateTreeNode<PAYLOAD>> parentsToEmpty, List<BaseTemplateTreeNode<PAYLOAD>> nodesToAdd) {
		List<BaseTemplateTreeNode<PAYLOAD>> nodesToBeRemoved = nodes.stream()
				.filter(node -> node.isDescendantOf(parentsToEmpty))
				.collect(Collectors.toList());
		nodes.removeAll(nodesToBeRemoved);
		nodes.addAll(nodesToAdd);
		onChanged.fire(new TreeModelChangedEventData<>(nodesToBeRemoved, nodesToAdd));
	}

	public void updateNode(BaseTemplateTreeNode<PAYLOAD> node) {
		onChanged.fire(new TreeModelChangedEventData<>(Collections.emptyList(), Collections.singletonList(node)));
	}

	public void relocateNode(BaseTemplateTreeNode<PAYLOAD> node) {
		onChanged.fire(new TreeModelChangedEventData<>(Collections.singletonList(node), Collections.singletonList(node)));
	}

	public void removeNode(BaseTemplateTreeNode<PAYLOAD> node) {
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
	public List<BaseTemplateTreeNode<PAYLOAD>> getRecords(String query) {
		if (StringUtils.isEmpty(query)) {
			return this.nodes.stream()
					.filter(new EagerNodesFilter())
					.limit(maxResultNodes)
					.collect(Collectors.toList());
		} else {
			List<BaseTemplateTreeNode<PAYLOAD>> filteredTree = this.nodes.stream()
					.filter(node -> StringUtils.containsIgnoreCase(node.getCaption(), query))
					.flatMap(node -> ((List<BaseTemplateTreeNode<PAYLOAD>>) (List) node.getPath()).stream())
					.distinct()
					.collect(Collectors.toList());
			List<BaseTemplateTreeNode<PAYLOAD>> treeCopy = BaseTemplateTreeNode.copyTree(filteredTree);
			treeCopy.forEach(node -> node.setExpanded(true));
			return treeCopy;
		}
	}

	@Override
	public List<BaseTemplateTreeNode<PAYLOAD>> getRecords() {
		return this.getRecords(null);
	}

	@Override
	public List<BaseTemplateTreeNode<PAYLOAD>> getChildRecords(BaseTemplateTreeNode<PAYLOAD> parentRecord) {
		return nodes.stream()
				.filter(record -> record.getPath().contains(parentRecord) && record != parentRecord)
				.filter(new EagerNodesFilter(parentRecord))
				.collect(Collectors.toList());
	}
}
