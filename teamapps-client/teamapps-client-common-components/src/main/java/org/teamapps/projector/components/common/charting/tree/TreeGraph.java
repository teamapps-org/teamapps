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
package org.teamapps.projector.components.common.charting.tree;

import org.teamapps.projector.components.common.dto.DtoBaseTreeGraphNode;
import org.teamapps.projector.components.common.dto.DtoClientRecord;
import org.teamapps.projector.components.common.dto.DtoTreeGraph;
import org.teamapps.projector.components.common.dto.DtoTreeGraphNode;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;

import java.util.*;
import java.util.stream.Collectors;

public class TreeGraph<RECORD> extends AbstractComponent {

	public final ProjectorEvent<TreeGraphNode<RECORD>> onNodeClicked = createProjectorEventBoundToUiEvent(DtoTreeGraph.NodeClickedEvent.TYPE_ID);
	public final ProjectorEvent<NodeExpandedOrCollapsedEvent<RECORD>> onNodeExpandedOrCollapsed = createProjectorEventBoundToUiEvent(DtoTreeGraph.NodeExpandedOrCollapsedEvent.TYPE_ID);
	public final ProjectorEvent<NodeExpandedOrCollapsedEvent<RECORD>> onParentExpandedOrCollapsed = createProjectorEventBoundToUiEvent(DtoTreeGraph.ParentExpandedOrCollapsedEvent.TYPE_ID);
	public final ProjectorEvent<SideListExpandedOrCollapsedEvent<RECORD>> onSideListExpandedOrCollapsed = createProjectorEventBoundToUiEvent(DtoTreeGraph.SideListExpandedOrCollapsedEvent.TYPE_ID);

	private float zoomFactor;
	private boolean compact = false;
	private int verticalLayerGap = 36;
	private int horizontalSiblingGap = 20;
	private int horizontalNonSignlingGap = 36;
	private int sideListIndent = 20;
	private int sideListVerticalGap = 20;

	private final LinkedHashMap<String, TreeGraphNode<RECORD>> nodesById = new LinkedHashMap<>();
	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();

	public TreeGraph() {
	}

	@Override
	public DtoTreeGraph createDto() {
		DtoTreeGraph ui = new DtoTreeGraph();
		mapAbstractUiComponentProperties(ui);
		ui.setNodes(createUiNodes(nodesById.values()));
		ui.setZoomFactor(zoomFactor);
		ui.setCompact(compact);
		ui.setVerticalLayerGap(verticalLayerGap);
		ui.setSideListIndent(sideListIndent);
		ui.setSideListVerticalGap(sideListVerticalGap);
		ui.setHorizontalSiblingGap(horizontalSiblingGap);
		ui.setHorizontalNonSignlingGap(horizontalNonSignlingGap);
		return ui;
	}

	private List<DtoTreeGraphNode> createUiNodes(Collection<TreeGraphNode<RECORD>> nodes) {
		return nodes.stream()
				.map(this::createUiNode)
				.collect(Collectors.toList());
	}

	private DtoTreeGraphNode createUiNode(TreeGraphNode<RECORD> node) {
		DtoTreeGraphNode uiNode = new DtoTreeGraphNode(node.getId(), node.getWidth(), node.getHeight());
		mapBaseTreeGraphNodeAttributes(node, uiNode);
		uiNode.setParentId(node.getParent() != null ? node.getParent().getId() : null);
		uiNode.setParentExpandable(node.isParentExpandable());
		uiNode.setParentExpanded(node.isParentExpanded());
		uiNode.setExpanded(node.isExpanded());
		uiNode.setHasLazyChildren(node.isHasLazyChildren());
		uiNode.setSideListNodes(node.getSideListNodes() != null ? node.getSideListNodes().stream().map(this::createBaseUiNode).collect(Collectors.toList()) : null);
		uiNode.setSideListExpanded(node.isSideListExpanded());
		return uiNode;
	}

	private DtoBaseTreeGraphNode createBaseUiNode(BaseTreeGraphNode<RECORD> node) {
		DtoBaseTreeGraphNode uiNode = new DtoBaseTreeGraphNode(node.getId(), node.getWidth(), node.getHeight());
		mapBaseTreeGraphNodeAttributes(node, uiNode);
		return uiNode;
	}

	private void mapBaseTreeGraphNodeAttributes(BaseTreeGraphNode<RECORD> node, DtoBaseTreeGraphNode uiNode) {
		uiNode.setBackgroundColor(node.getBackgroundColor() != null ? node.getBackgroundColor().toHtmlColorString() : null);
		uiNode.setBorderColor(node.getBorderColor() != null ? node.getBorderColor().toHtmlColorString() : null);
		uiNode.setBorderWidth(node.getBorderWidth());
		uiNode.setBorderRadius(node.getBorderRadius());
		uiNode.setImage(node.getImage() != null ? node.getImage().createUiTreeGraphNodeImage() : null);
		uiNode.setIcon(node.getIcon() != null ? node.getIcon().createUiTreeGraphNodeIcon() : null);
		uiNode.setTemplate(node.getTemplate() != null ? node.getTemplate().createDtoReference() : null);
		uiNode.setRecord(node.getRecord() != null ? createUiRecord(node.getRecord(), node.getTemplate()) : null);
		uiNode.setConnectorLineColor(node.getConnectorLineColor() != null ? node.getConnectorLineColor().toHtmlColorString() : null);
		uiNode.setConnectorLineWidth(node.getConnectorLineWidth());
		uiNode.setDashArray(node.getDashArray());
	}

	private DtoClientRecord createUiRecord(RECORD record, Template template) {
		DtoClientRecord uiClientRecord = new DtoClientRecord();
		uiClientRecord.setValues(propertyProvider.getValues(record, template.getPropertyNames()));
		return uiClientRecord;
	}

	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
		sendCommandIfRendered(() -> new DtoTreeGraph.SetZoomFactorCommand(zoomFactor));
	}

	public void setNodes(List<TreeGraphNode<RECORD>> nodes) {
		this.nodesById.clear();
		nodes.forEach(n -> nodesById.put(n.getId(), n));
		sendCommandIfRendered(() -> new DtoTreeGraph.SetNodesCommand(createUiNodes(nodes)));
	}

	public void addNode(TreeGraphNode<RECORD> node) {
		nodesById.put(node.getId(), node);
		sendCommandIfRendered(() -> new DtoTreeGraph.AddNodeCommand(createUiNode(node)));
	}

	public void addNodes(List<TreeGraphNode<RECORD>> nodes) {
		nodes.forEach(n -> nodesById.put(n.getId(), n));
		update();
	}

	public void removeNode(TreeGraphNode<RECORD> node) {
		this.nodesById.remove(node.getId());
		sendCommandIfRendered(() -> new DtoTreeGraph.RemoveNodeCommand(node.getId()));
	}

	public void updateNode(TreeGraphNode<RECORD> node) {
		nodesById.put(node.getId(), node);
		sendCommandIfRendered(() -> new DtoTreeGraph.UpdateNodeCommand(createUiNode(node)));
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoTreeGraph.NodeClickedEvent.TYPE_ID -> {
				var e = event.as(DtoTreeGraph.NodeClickedEventWrapper.class);
				TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
				if (node != null) {
					onNodeClicked.fire(node);
				}
			}
			case DtoTreeGraph.NodeExpandedOrCollapsedEvent.TYPE_ID -> {
				var e = event.as(DtoTreeGraph.NodeExpandedOrCollapsedEventWrapper.class);
				TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
				if (node != null) {
					node.setExpanded(e.getExpanded());
					onNodeExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<>(node, e.getExpanded(), e.getLazyLoad()));
				}
			}
			case DtoTreeGraph.ParentExpandedOrCollapsedEvent.TYPE_ID -> {
				var e = event.as(DtoTreeGraph.ParentExpandedOrCollapsedEventWrapper.class);
				TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
				if (node != null) {
					node.setParentExpanded(e.getExpanded());
					onParentExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<>(node, e.getExpanded(), e.getLazyLoad()));
				}
			}
			case DtoTreeGraph.SideListExpandedOrCollapsedEvent.TYPE_ID -> {
				var e = event.as(DtoTreeGraph.SideListExpandedOrCollapsedEventWrapper.class);
				TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
				if (node != null) {
					node.setSideListExpanded(e.getExpanded());
					onSideListExpandedOrCollapsed.fire(new SideListExpandedOrCollapsedEvent<>(node, e.getExpanded()));
				}
			}

		}
	}

	public boolean isCompact() {
		return compact;
	}

	public void setCompact(boolean compact) {
		this.compact = compact;
		update();
	}

	private void update() {
		sendCommandIfRendered(() -> new DtoTreeGraph.UpdateCommand(createDto()));
	}

	public void moveToRootNode() {
		sendCommandIfRendered(() -> new DtoTreeGraph.MoveToRootNodeCommand());
	}

	public void moveToNode(TreeGraphNode<RECORD> node) {
		sendCommandIfRendered(() -> new DtoTreeGraph.MoveToNodeCommand(node.getId()));
	}

	private Collection<TreeGraphNode<RECORD>> getAllDescendants(TreeGraphNode<RECORD> node, boolean includeSelf) {
		// O(h^2) where h is the height of the tree.
		// So the worst case performance is O(n * n/2) for a totally linear tree.
		// Average case: O(n * log(n))
		// If used for a root node (see usages!!): O(n) due to optimization!

		Set<TreeGraphNode<RECORD>> descendants = new HashSet<>();
		descendants.add(node);
		Set<TreeGraphNode<RECORD>> nonDescendants = new HashSet<>(getAncestors(node, false)); // common case optimization!

		List<TreeGraphNode<RECORD>> untaggedNodes = new ArrayList<>(nodesById.values());
		untaggedNodes.remove(node);

		boolean[] descendantsChanged = new boolean[1];
		boolean[] nonDescendantsChanged = new boolean[1];
		do {
			descendantsChanged[0] = false;
			nonDescendantsChanged[0] = false;
			untaggedNodes = untaggedNodes.stream()
					.filter(n -> {
						if (descendants.contains(n.getParent())) {
							descendants.add(n);
							descendantsChanged[0] = true;
							return false;
						} else if (nonDescendants.contains(n.getParent())) {
							nonDescendants.add(n);
							nonDescendantsChanged[0] = true;
							return false;
						} else {
							return true;
						}
					})
					.collect(Collectors.toList());
		} while (descendantsChanged[0] && nonDescendantsChanged[0]);

		descendants.addAll(untaggedNodes); // if nonDescendantsChanged[0] == false, all remaining nodes must be descendants!

		if (!includeSelf) {
			descendants.remove(node);
		}

		return descendants;
	}

	private List<TreeGraphNode<RECORD>> getAncestors(TreeGraphNode<RECORD> node, boolean includeSelf) {
		ArrayList<TreeGraphNode<RECORD>> ancestors = new ArrayList<>();
		if (includeSelf) {
			ancestors.add(node);
		}
		while (node.getParent() != null) {
			node = node.getParent();
			ancestors.add(node);
		}
		return ancestors;
	}

	public int getVerticalLayerGap() {
		return verticalLayerGap;
	}

	public void setVerticalLayerGap(int verticalLayerGap) {
		this.verticalLayerGap = verticalLayerGap;
		this.update();
	}

	public int getSideListIndent() {
		return sideListIndent;
	}

	public void setSideListIndent(int sideListIndent) {
		this.sideListIndent = sideListIndent;
		this.update();
	}

	public int getSideListVerticalGap() {
		return sideListVerticalGap;
	}

	public void setSideListVerticalGap(int sideListVerticalGap) {
		this.sideListVerticalGap = sideListVerticalGap;
		this.update();
	}

	public int getHorizontalSiblingGap() {
		return horizontalSiblingGap;
	}

	public void setHorizontalSiblingGap(int horizontalSiblingGap) {
		this.horizontalSiblingGap = horizontalSiblingGap;
		this.update();
	}

	public int getHorizontalNonSignlingGap() {
		return horizontalNonSignlingGap;
	}

	public void setHorizontalNonSignlingGap(int horizontalNonSignlingGap) {
		this.horizontalNonSignlingGap = horizontalNonSignlingGap;
		this.update();
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
}














































