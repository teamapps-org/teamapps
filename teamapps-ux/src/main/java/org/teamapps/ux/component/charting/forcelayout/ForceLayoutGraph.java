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

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ForceLayoutGraph<RECORD> extends AbstractComponent {

	public final Event<ForceLayoutNode<RECORD>> onNodeClicked = new Event<>();
	public final Event<ForceLayoutNode<RECORD>> onNodeDoubleClicked = new Event<>();
	public final Event<NodeExpandedOrCollapsedEvent<RECORD>> onNodeExpandedOrCollapsed = new Event<>();

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
	public UiComponent createUiComponent() {
		List<UiNetworkNode> nodes = createUiNodes(this.nodes);
		List<UiNetworkLink> links = createUiLinks(this.links);
		UiNetworkGraph ui = new UiNetworkGraph(nodes, links, Collections.emptyList());
		ui.setAnimationDuration(animationDuration);
		mapAbstractUiComponentProperties(ui);
		return ui;
	}

	private List<UiNetworkNode> createUiNodes(List<ForceLayoutNode<RECORD>> nodes) {
		return nodes.stream()
				.map(n -> createUiNode(n))
				.collect(Collectors.toList());
	}

	private List<UiNetworkLink> createUiLinks(List<ForceLayoutLink<RECORD>> links) {
		return links.stream()
				.map(l -> l.toUiNetworkLink())
				.collect(Collectors.toList());
	}

	private UiNetworkNode createUiNode(ForceLayoutNode<RECORD> node) {
		UiNetworkNode uiNode = new UiNetworkNode(node.getId(), node.getWidth(), node.getHeight());
		uiNode.setBackgroundColor(node.getBackgroundColor() != null ? node.getBackgroundColor().toHtmlColorString() : null);
		uiNode.setBorderColor(node.getBorderColor() != null ? node.getBorderColor().toHtmlColorString() : null);
		uiNode.setBorderWidth(node.getBorderWidth());
		uiNode.setBorderRadius(node.getBorderRadius());
		uiNode.setTemplate(node.getTemplate() != null ? node.getTemplate().createUiTemplate() : null);
		uiNode.setRecord(node.getRecord() != null ? createUiRecord(node) : null);
		uiNode.setExpandState(node.getExpandedState().toExpandState());
		uiNode.setIcon(node.getIcon() != null ? node.getIcon().createUiTreeGraphNodeIcon() : null);
		uiNode.setImage(node.getImage() != null ? node.getImage().createUiTreeGraphNodeImage() : null);
		uiNode.setDistanceFactor(node.getDistanceFactor());
		return uiNode;
	}

	private UiClientRecord createUiRecord(ForceLayoutNode<RECORD> node) {
		UiClientRecord uiClientRecord = new UiClientRecord();
		uiClientRecord.setValues(propertyProvider.getValues(node.getRecord(), node.getTemplate().getPropertyNames()));
		return uiClientRecord;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiNetworkGraph.NodeClickedEvent) {
			UiNetworkGraph.NodeClickedEvent clickEvent = (UiNetworkGraph.NodeClickedEvent) event;
			nodes.stream()
					.filter(n -> n.getId().equals(clickEvent.getNodeId()))
					.findFirst()
					.ifPresent(onNodeClicked::fire);
		}    else if (event instanceof UiNetworkGraph.NodeDoubleClickedEvent) {
			UiNetworkGraph.NodeDoubleClickedEvent clickEvent = (UiNetworkGraph.NodeDoubleClickedEvent) event;
			nodes.stream()
					.filter(n -> n.getId().equals(clickEvent.getNodeId()))
					.findFirst()
					.ifPresent(onNodeDoubleClicked::fire);
		}
		if (event instanceof UiNetworkGraph.NodeExpandedOrCollapsedEvent) {
			UiNetworkGraph.NodeExpandedOrCollapsedEvent clickEvent = (UiNetworkGraph.NodeExpandedOrCollapsedEvent) event;
			nodes.stream()
					.filter(n -> n.getId().equals(clickEvent.getNodeId()))
					.findFirst()
					.ifPresent(n -> onNodeExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<RECORD>(n, clickEvent.getExpanded())));
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
		queueCommandIfRendered(() -> new UiNetworkGraph.AddNodesAndLinksCommand(getId(), createUiNodes(nodes), createUiLinks(links)));
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
		queueCommandIfRendered(() -> new UiNetworkGraph.RemoveNodesAndLinksCommand(getId(), nodeIds, linksBySourceNodeId));
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
		queueCommandIfRendered(() -> new UiNetworkGraph.SetDistanceCommand(getId(), linkDistanceFactor, nodeDistanceFactor));
	}
}
