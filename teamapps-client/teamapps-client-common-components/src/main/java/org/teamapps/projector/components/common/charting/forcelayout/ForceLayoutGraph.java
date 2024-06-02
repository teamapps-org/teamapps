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
package org.teamapps.projector.components.common.charting.forcelayout;

import org.teamapps.dto.*;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForceLayoutGraph<RECORD> extends AbstractComponent {

	public final ProjectorEvent<ForceLayoutNode<RECORD>> onNodeClicked = new ProjectorEvent<>(clientObjectChannel::toggleNodeClickedEvent);
	public final ProjectorEvent<ForceLayoutNode<RECORD>> onNodeDoubleClicked = new ProjectorEvent<>(clientObjectChannel::toggleNodeDoubleClickedEvent);
	public final ProjectorEvent<NodeExpandedOrCollapsedEvent<RECORD>> onNodeExpandedOrCollapsed = new ProjectorEvent<>(clientObjectChannel::toggleNodeExpandedOrCollapsedEvent);

	private final List<ForceLayoutNode<RECORD>> nodes;
	private final List<ForceLayoutLink<RECORD>> links;

	private int animationDuration = 1000;
	// private float gravity = 0.1f;
	// private float theta = 0.3f;
	// private float alpha = 0.1f;
	// private int charge = -300;
	// private int distance = 30;
	// private Color highlightColor;

	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	public ForceLayoutGraph() {
		this(Collections.emptyList(), Collections.emptyList());
	}

	public ForceLayoutGraph(List<ForceLayoutNode<RECORD>> nodes, List<ForceLayoutLink<RECORD>> links) {
		this.nodes = new ArrayList<>(nodes);
		this.links = new ArrayList<>(links);
	}

	public void setModel(ForceLayoutModel<RECORD> model) {
		model.onNodesAdded.addListener(change -> {
			addNodesAndLinks(change.getAddedNodes(), change.getAddedLinks());
		});
		model.onNodesRemoved.addListener(change -> {
			removeNodesAndLinks(change.getRemovedNodes(), change.getRemovedLinks());
		});
	}

	@Override
	public DtoComponent createDto() {
		List<DtoNetworkNode> nodes = createUiNodes(this.nodes);
		List<DtoNetworkLink> links = createUiLinks(this.links);
		DtoNetworkGraph ui = new DtoNetworkGraph(nodes, links, Collections.emptyList());
		ui.setAnimationDuration(animationDuration);
		mapAbstractUiComponentProperties(ui);
		return ui;
	}

	private List<DtoNetworkNode> createUiNodes(List<ForceLayoutNode<RECORD>> nodes) {
		return nodes.stream()
				.map(n -> createUiNode(n))
				.collect(Collectors.toList());
	}

	private List<DtoNetworkLink> createUiLinks(List<ForceLayoutLink<RECORD>> links) {
		return links.stream()
				.map(l -> l.toUiNetworkLink())
				.collect(Collectors.toList());
	}

	private DtoNetworkNode createUiNode(ForceLayoutNode<RECORD> node) {
		DtoNetworkNode uiNode = new DtoNetworkNode(node.getId(), node.getWidth(), node.getHeight());
		uiNode.setBackgroundColor(node.getBackgroundColor() != null ? node.getBackgroundColor().toHtmlColorString() : null);
		uiNode.setBorderColor(node.getBorderColor() != null ? node.getBorderColor().toHtmlColorString() : null);
		uiNode.setBorderWidth(node.getBorderWidth());
		uiNode.setBorderRadius(node.getBorderRadius());
		uiNode.setTemplate(node.getTemplate() != null ? node.getTemplate().createDtoReference() : null);
		uiNode.setRecord(node.getRecord() != null ? createUiRecord(node) : null);
		uiNode.setExpandState(node.getExpandedState().toExpandState());
		uiNode.setIcon(node.getIcon() != null ? node.getIcon().createUiTreeGraphNodeIcon() : null);
		uiNode.setImage(node.getImage() != null ? node.getImage().createUiTreeGraphNodeImage() : null);
		uiNode.setDistanceFactor(node.getDistanceFactor());
		return uiNode;
	}

	private DtoClientRecord createUiRecord(ForceLayoutNode<RECORD> node) {
		DtoClientRecord uiClientRecord = new DtoClientRecord();
		uiClientRecord.setValues(propertyProvider.getValues(node.getRecord(), node.getTemplate().getPropertyNames()));
		return uiClientRecord;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoNetworkGraph.NodeClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoNetworkGraph.NodeClickedEventWrapper.class);
				nodes.stream()
						.filter(n -> n.getId().equals(clickEvent.getNodeId()))
						.findFirst()
						.ifPresent(onNodeClicked::fire);
			}
			case DtoNetworkGraph.NodeDoubleClickedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoNetworkGraph.NodeDoubleClickedEventWrapper.class);
				nodes.stream()
						.filter(n -> n.getId().equals(clickEvent.getNodeId()))
						.findFirst()
						.ifPresent(onNodeDoubleClicked::fire);
			}
			case DtoNetworkGraph.NodeExpandedOrCollapsedEvent.TYPE_ID -> {
				var clickEvent = event.as(DtoNetworkGraph.NodeExpandedOrCollapsedEventWrapper.class);
				nodes.stream()
						.filter(n -> n.getId().equals(clickEvent.getNodeId()))
						.findFirst()
						.ifPresent(n -> onNodeExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<RECORD>(n, clickEvent.getExpanded())));
			}
		}
	}

	public int getAnimationDuration() {
		return animationDuration;
	}

	public void setAnimationDuration(int animationDuration) {
		this.animationDuration = animationDuration;
	}

	public void addNodesAndLinks(List<ForceLayoutNode<RECORD>> nodes, List<ForceLayoutLink<RECORD>> links) {
		this.nodes.addAll(nodes);
		this.links.addAll(links);
		clientObjectChannel.addNodesAndLinks(CreateUiNodes(Nodes), createUiLinks(links));
	}

	public void removeNodesAndLinks(List<ForceLayoutNode<RECORD>> nodes) {
		List<ForceLayoutLink<RECORD>> linksToRemove = links.stream()
				.filter(l -> nodes.contains(l.getSource()) || nodes.contains(l.getTarget()))
				.collect(Collectors.toList());
		removeNodesAndLinks(nodes, linksToRemove);
	}

	public void removeNodesAndLinks(List<ForceLayoutNode<RECORD>> nodes, List<ForceLayoutLink<RECORD>> links) {
		this.nodes.removeAll(nodes);
		this.links.removeAll(links);
		List<String> nodeIds = nodes.stream().map(n -> n.getId()).collect(Collectors.toList());
		Map<String, List<String>> linksBySourceNodeId = links.stream()
				.collect(Collectors.groupingBy(l -> l.getSource().getId(), Collectors.mapping(l -> l.getTarget().getId(), Collectors.toList())));
		clientObjectChannel.removeNodesAndLinks(NodeIds, LinksBySourceNodeId);
	}

	public List<ForceLayoutNode<RECORD>> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public List<ForceLayoutLink<RECORD>> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.setPropertyProvider(propertyExtractor);
	}

	public void setDistance(float linkDistanceFactor, float nodeDistanceFactor) {
		clientObjectChannel.setDistance(LinkDistanceFactor, NodeDistanceFactor);
	}
}
