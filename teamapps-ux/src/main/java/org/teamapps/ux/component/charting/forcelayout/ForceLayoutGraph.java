package org.teamapps.ux.component.charting.forcelayout;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiNetworkGraph;
import org.teamapps.dto.UiNetworkLink;
import org.teamapps.dto.UiNetworkNode;
import org.teamapps.ux.component.AbstractComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ForceLayoutGraph<RECORD> extends AbstractComponent {

	private final List<ForceLayoutNode> nodes;
	private final List<ForceLayoutLink> links;

	private float gravity = 0.1f;
	private float theta = 0.3f;
	private float alpha = 0.1f;
	private int charge = -300;
	private int distance = 30;
	private Color highlightColor;

	public ForceLayoutGraph() {
		this(Collections.emptyList(), Collections.emptyList());
	}

	public ForceLayoutGraph(List<ForceLayoutNode> nodes, List<ForceLayoutLink> links) {
		this.nodes = new ArrayList<>(nodes);
		this.links = new ArrayList<>(links);
	}

	@Override
	public UiComponent createUiComponent() {
		List<UiNetworkNode> nodes = this.nodes.stream()
				.map(n -> n.toUiNetworkNode())
				.collect(Collectors.toList());
		List<UiNetworkLink> links = this.links.stream()
				.map(l -> l.toUiNetworkLink())
				.collect(Collectors.toList());
		UiNetworkGraph ui = new UiNetworkGraph(nodes, links, Collections.emptyList());
		mapAbstractUiComponentProperties(ui);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}


}
