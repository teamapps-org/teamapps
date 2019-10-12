package org.teamapps.ux.component.charting.tree;

import org.jetbrains.annotations.NotNull;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiBaseTreeGraphNode;
import org.teamapps.dto.UiClientRecord;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiTreeGraph;
import org.teamapps.dto.UiTreeGraphNode;
import org.teamapps.event.Event;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TreeGraph<RECORD> extends AbstractComponent {

	public final Event<TreeGraphNode<RECORD>> onNodeClicked = new Event<>();
	public final Event<NodeExpandedOrCollapsedEvent<RECORD>> onNodeExpandedOrCollapsed = new Event<>();
	public final Event<SideListExpandedOrCollapsedEvent<RECORD>> onSideListExpandedOrCollapsed = new Event<>();

	private float zoomFactor;
	private List<TreeGraphNode<RECORD>> nodes = new ArrayList<>();

	/**
	 * The visibility propagation root is the node that is guaranteed to be visible. It is always the root of the visible tree.
	 * If parent nodes are added to the tree, the tree must make sure the visibilityPropagationRoot becomes the new visible root by bubbling up as long as n.parentCollapsed is false.
	 */
	private TreeGraphNode<RECORD> visibilityPropagationRoot; // can only bubble upward, unless explicitly set by the developer
	private boolean compact = false;
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();

	public TreeGraph() {
	}

	@Override
	public UiTreeGraph createUiComponent() {
		UiTreeGraph ui = new UiTreeGraph();
		mapAbstractUiComponentProperties(ui);
		ui.setNodes(createUiNodes(nodes));
		ui.setZoomFactor(zoomFactor);
		ui.setCompact(compact);
		return ui;
	}

	private List<UiTreeGraphNode> createUiNodes(List<TreeGraphNode<RECORD>> nodes) {
		return nodes.stream()
				.map(this::createUiNode)
				.collect(Collectors.toList());
	}

	@NotNull
	private UiTreeGraphNode createUiNode(TreeGraphNode<RECORD> node) {
		UiTreeGraphNode uiNode = new UiTreeGraphNode(node.getId(), node.getWidth(), node.getHeight());
		mapBaseTreeGraphNodeAttributes(node, uiNode);
		uiNode.setParentId(node.getParent() != null ? node.getParent().getId() : null);
		uiNode.setParentCollapsible(node.isParentCollapsible());
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
		uiNode.setBackgroundColor(node.getBackgroundColor() != null ? UiUtil.createUiColor(node.getBackgroundColor()) : null);
		uiNode.setBorderColor(node.getBorderColor() != null ? UiUtil.createUiColor(node.getBorderColor()) : null);
		uiNode.setBorderWidth(node.getBorderWidth());
		uiNode.setBorderRadius(node.getBorderRadius());
		uiNode.setImage(node.getImage() != null ? node.getImage().createUiTreeGraphNodeImage() : null);
		uiNode.setIcon(node.getIcon() != null ? node.getIcon().createUiTreeGraphNodeIcon() : null);
		uiNode.setTemplate(node.getTemplate() != null ? node.getTemplate().createUiTemplate() : null);
		uiNode.setRecord(node.getRecord() != null ? createUiRecord(node.getRecord(), node.getTemplate()) : null);
		uiNode.setConnectorLineColor(node.getConnectorLineColor() != null ? UiUtil.createUiColor(node.getConnectorLineColor()) : null);
		uiNode.setConnectorLineWidth(node.getConnectorLineWidth());
		uiNode.setDashArray(node.getDashArray());
	}

	private UiClientRecord createUiRecord(RECORD record, Template template) {
		UiClientRecord uiClientRecord = new UiClientRecord();
		uiClientRecord.setValues(propertyExtractor.getValues(record, template.getDataKeys()));
		return uiClientRecord;
	}

	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
		queueCommandIfRendered(() -> new UiTreeGraph.SetZoomFactorCommand(getId(), zoomFactor));
	}

	public void setNodes(List<TreeGraphNode<RECORD>> nodes) {
		this.nodes.clear();
		this.nodes.addAll(nodes);
		setVisibilityPropagationRoot(this.nodes.size() > 0 ? nodes.get(0) : null);
		queueCommandIfRendered(() -> new UiTreeGraph.SetNodesCommand(getId(), createUiNodes(nodes)));
	}

	public void addNode(TreeGraphNode<RECORD> node) {
		this.nodes.add(node);
		updateVisibilityPropagation();
		queueCommandIfRendered(() -> new UiTreeGraph.AddNodeCommand(getId(), createUiNode(node)));
	}

	public void addNodes(List<TreeGraphNode<RECORD>> node) {
		this.nodes.addAll(node);
		updateVisibilityPropagation();
		update();
	}

	public void removeNode(TreeGraphNode<RECORD> node) {
		this.nodes.remove(node);
		if (visibilityPropagationRoot == node) {
			setVisibilityPropagationRoot(nodes.size() > 0 ? nodes.get(0) : null);
		}
		queueCommandIfRendered(() -> new UiTreeGraph.RemoveNodeCommand(getId(), node.getId()));
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TREE_GRAPH_NODE_CLICKED: {
				UiTreeGraph.NodeClickedEvent clickEvent = (UiTreeGraph.NodeClickedEvent) event;
				this.nodes.stream()
						.filter(node -> node.getId().equals(clickEvent.getNodeId()))
						.findFirst()
						.ifPresent(onNodeClicked::fire);
				break;
			}
			case UI_TREE_GRAPH_NODE_EXPANDED_OR_COLLAPSED: {
				UiTreeGraph.NodeExpandedOrCollapsedEvent e = (UiTreeGraph.NodeExpandedOrCollapsedEvent) event;
				this.nodes.stream()
						.filter(node -> node.getId().equals(e.getNodeId()))
						.findFirst()
						.ifPresent(node -> {
							node.setExpanded(e.getExpanded());
							onNodeExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<>(node, e.getExpanded(), e.getLazyLoad()));
						});
				break;
			}
			case UI_TREE_GRAPH_SIDE_LIST_EXPANDED_OR_COLLAPSED: {
				UiTreeGraph.SideListExpandedOrCollapsedEvent e = (UiTreeGraph.SideListExpandedOrCollapsedEvent) event;
				this.nodes.stream()
						.filter(node -> node.getId().equals(e.getNodeId()))
						.findFirst()
						.ifPresent(node -> {
							node.setSideListExpanded(e.getExpanded());
							onSideListExpandedOrCollapsed.fire(new SideListExpandedOrCollapsedEvent<>(node, e.getExpanded()));
						});
				break;
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
		queueCommandIfRendered(() -> new UiTreeGraph.UpdateCommand(getId(), createUiComponent()));
	}

	public void moveToRootNode() {
		queueCommandIfRendered(() -> new UiTreeGraph.MoveToRootNodeCommand(getId()));
	}

	public void moveToNode(TreeGraphNode<RECORD> node) {
		queueCommandIfRendered(() -> new UiTreeGraph.MoveToNodeCommand(getId(), node.getId()));
	}

	public void setVisibilityPropagationRoot(TreeGraphNode<RECORD> centralNode) {
		this.visibilityPropagationRoot = centralNode;
		updateVisibilityPropagation();
	}

	private void updateVisibilityPropagation() {
		this.bubbleUpVisibilityPropagationRoot();
		this.propagateParentVisibility();
	}

	private void bubbleUpVisibilityPropagationRoot() {
		if (visibilityPropagationRoot == null) {
			return;
		}
		while (visibilityPropagationRoot.getParent() != null && visibilityPropagationRoot.isParentExpanded()) {
			visibilityPropagationRoot.getParent().setExpanded(true); // the former visibility propagation root must stay visible!
			visibilityPropagationRoot = visibilityPropagationRoot.getParent();
		}
	}

	private void propagateParentVisibility() {
		if (visibilityPropagationRoot == null) {
			return;
		}
		getAllDescendants(visibilityPropagationRoot, false).forEach(d -> d.setParentExpanded(true));
	}

	private Collection<TreeGraphNode<RECORD>> getAllDescendants(TreeGraphNode<RECORD> node, boolean includeSelf) {
		// O(h^2) where h is the height of the tree.
		// So the worst case performance is O(n * n/2) for a totally linear tree.
		// Average case: O(n * log(n))
		// If used for a root node (see usages!!): O(n) due to optimization!

		Set<TreeGraphNode<RECORD>> descendants = new HashSet<>();
		descendants.add(node);
		Set<TreeGraphNode<RECORD>> nonDescendants = new HashSet<>(getAncestors(node, false)); // common case optimization!

		List<TreeGraphNode<RECORD>> untaggedNodes = new ArrayList<>(nodes);
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
}














































