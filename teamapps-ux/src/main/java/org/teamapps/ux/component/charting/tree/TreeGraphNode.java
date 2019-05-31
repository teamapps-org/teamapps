package org.teamapps.ux.component.charting.tree;

import org.teamapps.common.format.Color;
import org.teamapps.ux.component.template.Template;

import java.util.UUID;

public class TreeGraphNode<RECORD> {

	private final String id = UUID.randomUUID().toString();
	private TreeGraphNode<RECORD> parent;
	private int width;
	private int height;
	private Color backgroundColor = new Color(255, 255, 255);
	private Color borderColor = new Color(100, 100, 100);
	private float borderWidth = 1;
	private float borderRadius = 0;

	private TreeGraphNodeImage image;
	private TreeGraphNodeIcon icon;

	private Template template;
	private RECORD record;
	private Color connectorLineColor = new Color(100, 100, 100);
	private int connectorLineWidth;
	private String dashArray;
	private boolean expanded;

	protected String getId() {
		return id;
	}

	public TreeGraphNode<RECORD> getParent() {
		return parent;
	}

	public TreeGraphNode<RECORD> setParent(TreeGraphNode<RECORD> parent) {
		this.parent = parent;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public TreeGraphNode<RECORD> setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public TreeGraphNode<RECORD> setHeight(int height) {
		this.height = height;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public TreeGraphNode<RECORD> setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public TreeGraphNode<RECORD> setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public TreeGraphNode<RECORD> setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	public float getBorderRadius() {
		return borderRadius;
	}

	public TreeGraphNode<RECORD> setBorderRadius(float borderRadius) {
		this.borderRadius = borderRadius;
		return this;
	}

	public TreeGraphNodeImage getImage() {
		return image;
	}

	public TreeGraphNode<RECORD> setImage(TreeGraphNodeImage image) {
		this.image = image;
		return this;
	}

	public TreeGraphNodeIcon getIcon() {
		return icon;
	}

	public TreeGraphNode<RECORD> setIcon(TreeGraphNodeIcon icon) {
		this.icon = icon;
		return this;
	}

	public Template getTemplate() {
		return template;
	}

	public TreeGraphNode<RECORD> setTemplate(Template template) {
		this.template = template;
		return this;
	}

	public RECORD getRecord() {
		return record;
	}

	public TreeGraphNode<RECORD> setRecord(RECORD record) {
		this.record = record;
		return this;
	}

	public Color getConnectorLineColor() {
		return connectorLineColor;
	}

	public TreeGraphNode<RECORD> setConnectorLineColor(Color connectorLineColor) {
		this.connectorLineColor = connectorLineColor;
		return this;
	}

	public int getConnectorLineWidth() {
		return connectorLineWidth;
	}

	public TreeGraphNode<RECORD> setConnectorLineWidth(int connectorLineWidth) {
		this.connectorLineWidth = connectorLineWidth;
		return this;
	}

	public String getDashArray() {
		return dashArray;
	}

	public TreeGraphNode<RECORD> setDashArray(String dashArray) {
		this.dashArray = dashArray;
		return this;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public TreeGraphNode<RECORD> setExpanded(boolean expanded) {
		this.expanded = expanded;
		return this;
	}
}