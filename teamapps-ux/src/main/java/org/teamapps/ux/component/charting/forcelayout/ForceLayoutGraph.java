package org.teamapps.ux.component.charting.forcelayout;

import org.jetbrains.annotations.NotNull;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiClientRecord;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiNetworkGraph;
import org.teamapps.dto.UiNetworkLink;
import org.teamapps.dto.UiNetworkNode;
import org.teamapps.event.Event;
import org.teamapps.util.UiUtil;
import org.teamapps.ux.component.AbstractComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ForceLayoutGraph<RECORD> extends AbstractComponent {

	public final Event<ForceLayoutNode> onNodeClicked = new Event<>();
	public final Event<NodeExpandedOrCollapsedEvent> onNodeExpandedOrCollapsed = new Event<>();

	private final List<ForceLayoutNode<RECORD>> nodes;
	private final List<ForceLayoutLink> links;

	// private float gravity = 0.1f;
	// private float theta = 0.3f;
	// private float alpha = 0.1f;
	// private int charge = -300;
	// private int distance = 30;
	// private Color highlightColor;
	
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();

	public ForceLayoutGraph() {
		this(Collections.emptyList(), Collections.emptyList());
	}

	public ForceLayoutGraph(List<ForceLayoutNode<RECORD>> nodes, List<ForceLayoutLink> links) {
		this.nodes = new ArrayList<>(nodes);
		this.links = new ArrayList<>(links);
	}

	@Override
	public UiComponent createUiComponent() {
		List<UiNetworkNode> nodes = this.nodes.stream()
				.map(n -> createUiNode(n))
				.collect(Collectors.toList());
		List<UiNetworkLink> links = this.links.stream()
				.map(l -> l.toUiNetworkLink())
				.collect(Collectors.toList());
		UiNetworkGraph ui = new UiNetworkGraph(nodes, links, Collections.emptyList());
		mapAbstractUiComponentProperties(ui);
		return ui;
	}

	@NotNull
	private UiNetworkNode createUiNode(ForceLayoutNode<RECORD> node) {
		UiNetworkNode uiNode = new UiNetworkNode(node.getId(), node.getWidth(), node.getHeight());
		uiNode.setBackgroundColor(node.getBackgroundColor() != null ? UiUtil.createUiColor(node.getBackgroundColor()) : null);
		uiNode.setBorderColor(node.getBorderColor() != null ? UiUtil.createUiColor(node.getBorderColor()) : null);
		uiNode.setBorderWidth(node.getBorderWidth());
		uiNode.setBorderRadius(node.getBorderRadius());
		uiNode.setTemplate(node.getTemplate() != null ? node.getTemplate().createUiTemplate() : null);
		uiNode.setRecord(node.getRecord() != null ? createUiRecord(node) : null);
		uiNode.setExpandState(node.getExpandedState().toExpandState());
		uiNode.setIcon(node.getIcon() != null ? node.getIcon().createUiTreeGraphNodeIcon() : null);
		uiNode.setImage(node.getImage() != null ? node.getImage().createUiTreeGraphNodeImage() : null);
		return uiNode;
	}

	private UiClientRecord createUiRecord(ForceLayoutNode<RECORD> node) {
		UiClientRecord uiClientRecord = new UiClientRecord();
		uiClientRecord.setValues(propertyExtractor.getValues(node.getRecord(), node.getTemplate().getDataKeys()));
		return uiClientRecord;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_NETWORK_GRAPH_NODE_CLICKED: {
				UiNetworkGraph.NodeClickedEvent clickEvent = (UiNetworkGraph.NodeClickedEvent) event;
				nodes.stream()
						.filter(n -> n.getId().equals(clickEvent.getNodeId()))
						.findFirst()
						.ifPresent(onNodeClicked::fire);
				break;
			}
			case UI_NETWORK_GRAPH_NODE_EXPANDED_OR_COLLAPSED:
				UiNetworkGraph.NodeExpandedOrCollapsedEvent clickEvent = (UiNetworkGraph.NodeExpandedOrCollapsedEvent) event;
				nodes.stream()
						.filter(n -> n.getId().equals(clickEvent.getNodeId()))
						.findFirst()
						.ifPresent(n -> onNodeExpandedOrCollapsed.fire(new NodeExpandedOrCollapsedEvent<RECORD>(n, clickEvent.getExpanded())));
				break;
		}
	}


}
