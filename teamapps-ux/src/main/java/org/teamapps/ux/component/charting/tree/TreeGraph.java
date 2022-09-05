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
package org.teamapps.ux.component.charting.tree;

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeGraph<RECORD> extends AbstractComponent {

	public final Event<TreeGraphNode<RECORD>> onNodeClicked = new Event<>();
	public final Event<NodeExpandedOrCollapsedEvent<RECORD>> onNodeExpandedOrCollapsed = new Event<>();
	public final Event<NodeExpandedOrCollapsedEvent<RECORD>> onParentExpandedOrCollapsed = new Event<>();
	public final Event<SideListExpandedOrCollapsedEvent<RECORD>> onSideListExpandedOrCollapsed = new Event<>();

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
	public UiTreeGraph createUiClientObject() {
		UiTreeGraph ui = new UiTreeGraph();
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

	private List<UiTreeGraphNode> createUiNodes(Collection<TreeGraphNode<RECORD>> nodes) {
		return nodes.stream()
				.map(this::createUiNode)
				.collect(Collectors.toList());
	}

	private UiTreeGraphNode createUiNode(TreeGraphNode<RECORD> node) {
		UiTreeGraphNode uiNode = new UiTreeGraphNode(node.getId(), node.getWidth(), node.getHeight());
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

	private UiBaseTreeGraphNode createBaseUiNode(BaseTreeGraphNode<RECORD> node) {
		UiBaseTreeGraphNode uiNode = new UiBaseTreeGraphNode(node.getId(), node.getWidth(), node.getHeight());
		mapBaseTreeGraphNodeAttributes(node, uiNode);
		return uiNode;
	}

	private void mapBaseTreeGraphNodeAttributes(BaseTreeGraphNode<RECORD> node, UiBaseTreeGraphNode uiNode) {
		uiNode.setBackgroundColor(node.getBackgroundColor() != null ? node.getBackgroundColor().toHtmlColorString() : null);
		uiNode.setBorderColor(node.getBorderColor() != null ? node.getBorderColor().toHtmlColorString() : null);
		uiNode.setBorderWidth(node.getBorderWidth());
		uiNode.setBorderRadius(node.getBorderRadius());
		uiNode.setImage(node.getImage() != null ? node.getImage().createUiTreeGraphNodeImage() : null);
		uiNode.setIcon(node.getIcon() != null ? node.getIcon().createUiTreeGraphNodeIcon() : null);
		uiNode.setTemplate(node.getTemplate() != null ? node.getTemplate().createUiTemplate() : null);
		uiNode.setRecord(node.getRecord() != null ? createUiRecord(node.getRecord(), node.getTemplate()) : null);
		uiNode.setConnectorLineColor(node.getConnectorLineColor() != null ? node.getConnectorLineColor().toHtmlColorString() : null);
		uiNode.setConnectorLineWidth(node.getConnectorLineWidth());
		uiNode.setDashArray(node.getDashArray());
	}

	private UiClientRecord createUiRecord(RECORD record, Template template) {
		UiClientRecord uiClientRecord = new UiClientRecord();
		uiClientRecord.setValues(propertyProvider.getValues(record, template.getPropertyNames()));
		return uiClientRecord;
	}

	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
		queueCommandIfRendered(() -> new UiTreeGraph.SetZoomFactorCommand(zoomFactor));
	}

	public void setNodes(List<TreeGraphNode<RECORD>> nodes) {
		this.nodesById.clear();
		nodes.forEach(n -> nodesById.put(n.getId(), n));
		queueCommandIfRendered(() -> new UiTreeGraph.SetNodesCommand(createUiNodes(nodes)));
	}

	public void addNode(TreeGraphNode<RECORD> node) {
		nodesById.put(node.getId(), node);
		queueCommandIfRendered(() -> new UiTreeGraph.AddNodeCommand(createUiNode(node)));
	}

	public void addNodes(List<TreeGraphNode<RECORD>> nodes) {
		nodes.forEach(n -> nodesById.put(n.getId(), n));
		update();
	}

	public void removeNode(TreeGraphNode<RECORD> node) {
		this.nodesById.remove(node.getId());
		queueCommandIfRendered(() -> new UiTreeGraph.RemoveNodeCommand(node.getId()));
	}

	public void updateNode(TreeGraphNode<RECORD> node) {
		nodesById.put(node.getId(), node);
		queueCommandIfRendered(() -> new UiTreeGraph.UpdateNodeCommand(createUiNode(node)));
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiTreeGraph.NodeClickedEvent) {
			UiTreeGraph.NodeClickedEvent e = (UiTreeGraph.NodeClickedEvent) event;
			TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
			if (node != null) {
				onNodeClicked.fire(node);
			}
		} else if (event instanceof UiTreeGraph.NodeExpandedOrCollapsedEvent) {
			UiTreeGraph.NodeExpandedOrCollapsedEvent e = (UiTreeGraph.NodeExpandedOrCollapsedEvent) event;
			TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
			if (node != null) {
				node.setExpanded(e.getExpanded());
				onNodeExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<>(node, e.getExpanded(), e.getLazyLoad()));
			}
		} else if (event instanceof UiTreeGraph.ParentExpandedOrCollapsedEvent) {
			UiTreeGraph.ParentExpandedOrCollapsedEvent e = (UiTreeGraph.ParentExpandedOrCollapsedEvent) event;
			TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
			if (node != null) {
				node.setParentExpanded(e.getExpanded());
				onParentExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<>(node, e.getExpanded(), e.getLazyLoad()));
			}
		} else if (event instanceof UiTreeGraph.SideListExpandedOrCollapsedEvent) {
			UiTreeGraph.SideListExpandedOrCollapsedEvent e = (UiTreeGraph.SideListExpandedOrCollapsedEvent) event;
			TreeGraphNode<RECORD> node = this.nodesById.get(e.getNodeId());
			if (node != null) {
				node.setSideListExpanded(e.getExpanded());
				onSideListExpandedOrCollapsed.fire(new SideListExpandedOrCollapsedEvent<>(node, e.getExpanded()));
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
		queueCommandIfRendered(() -> new UiTreeGraph.UpdateCommand(createUiClientObject()));
	}

	public void moveToRootNode() {
		queueCommandIfRendered(() -> new UiTreeGraph.MoveToRootNodeCommand());
	}

	public void moveToNode(TreeGraphNode<RECORD> node) {
		queueCommandIfRendered(() -> new UiTreeGraph.MoveToNodeCommand(node.getId()));
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














































