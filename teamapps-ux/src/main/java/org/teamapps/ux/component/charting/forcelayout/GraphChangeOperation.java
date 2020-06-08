package org.teamapps.ux.component.charting.forcelayout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphChangeOperation<RECORD> {

	private List<ForceLayoutNode<RECORD>> addedNodes = new ArrayList<>();
	private List<ForceLayoutLink<RECORD>> addedLinks = new ArrayList<>();
	private List<ForceLayoutNode<RECORD>> removedNodes = new ArrayList<>();
	private List<ForceLayoutLink<RECORD>> removedLinks = new ArrayList<>();

	private Map<RECORD, ForceLayoutNode<RECORD>> graphNodeByNode = new HashMap<>();

	public GraphChangeOperation() {
	}

	public GraphChangeOperation(List<ForceLayoutNode<RECORD>> nodes, List<ForceLayoutLink<RECORD>> links, boolean remove) {
		if (remove) {
			this.removedNodes = nodes;
			this.removedLinks = links;
		} else {
			addedNodes.forEach(record -> addNode(record));
			this.addedLinks = links;
		}
	}

	public boolean containsAddOperations() {
		if (!addedNodes.isEmpty() || !addedLinks.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean containsRemoveOperations() {
		if (!removedNodes.isEmpty() || !removedLinks.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public void addNode(ForceLayoutNode<RECORD> record) {
		addedNodes.add(record);
		graphNodeByNode.put(record.getRecord(), record);
	}

	public void addLink(ForceLayoutLink<RECORD> link) {
		addedLinks.add(link);
	}

	public List<ForceLayoutNode<RECORD>> getAddedNodes() {
		return addedNodes;
	}

	public void setAddedNodes(List<ForceLayoutNode<RECORD>> addedNodes) {
		addedNodes.forEach(record -> addNode(record));
	}

	public List<ForceLayoutLink<RECORD>> getAddedLinks() {
		return addedLinks;
	}

	public void setAddedLinks(List<ForceLayoutLink<RECORD>> addedLinks) {
		this.addedLinks = addedLinks;
	}

	public List<ForceLayoutNode<RECORD>> getRemovedNodes() {
		return removedNodes;
	}

	public void setRemovedNodes(List<ForceLayoutNode<RECORD>> removedNodes) {
		this.removedNodes = removedNodes;
	}

	public List<ForceLayoutLink<RECORD>> getRemovedLinks() {
		return removedLinks;
	}

	public void setRemovedLinks(List<ForceLayoutLink<RECORD>> removedLinks) {
		this.removedLinks = removedLinks;
	}

	public Map<RECORD, ForceLayoutNode<RECORD>> getGraphNodeByNode() {
		return graphNodeByNode;
	}
}
