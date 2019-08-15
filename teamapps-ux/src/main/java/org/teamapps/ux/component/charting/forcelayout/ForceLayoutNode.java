package org.teamapps.ux.component.charting.forcelayout;

import org.teamapps.common.format.Color;
import org.teamapps.ux.component.charting.common.GraphNodeIcon;
import org.teamapps.ux.component.charting.common.GraphNodeImage;
import org.teamapps.ux.component.template.Template;

import java.util.UUID;

public class ForceLayoutNode<RECORD> {

	private final String id = UUID.randomUUID().toString();
	private final RECORD record;

	private int width = 100;
	private int height = 100;
	private Color backgroundColor = new Color(255, 255, 255);
	private Color borderColor = new Color(100, 100, 100);
	private float borderWidth = 1;
	private float borderRadius = 3;

	private GraphNodeImage image;
	private GraphNodeIcon icon;

	private Template template;

	private ExpandedState expandedState = ExpandedState.NOT_EXPANDABLE;

	public ForceLayoutNode(RECORD record, int width, int height) {
		this.record = record;
		this.width = width;
		this.height = height;
	}

	protected String getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public ForceLayoutNode<RECORD> setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public ForceLayoutNode<RECORD> setHeight(int height) {
		this.height = height;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public ForceLayoutNode<RECORD> setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public ForceLayoutNode<RECORD> setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public ForceLayoutNode<RECORD> setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	public float getBorderRadius() {
		return borderRadius;
	}

	public ForceLayoutNode<RECORD> setBorderRadius(float borderRadius) {
		this.borderRadius = borderRadius;
		return this;
	}

	public GraphNodeImage getImage() {
		return image;
	}

	public ForceLayoutNode<RECORD> setImage(GraphNodeImage image) {
		this.image = image;
		return this;
	}

	public GraphNodeIcon getIcon() {
		return icon;
	}

	public ForceLayoutNode<RECORD> setIcon(GraphNodeIcon icon) {
		this.icon = icon;
		return this;
	}

	public Template getTemplate() {
		return template;
	}

	public ForceLayoutNode<RECORD> setTemplate(Template template) {
		this.template = template;
		return this;
	}

	public RECORD getRecord() {
		return record;
	}

	public ExpandedState getExpandedState() {
		return expandedState;
	}

	public void setExpandedState(ExpandedState expandedState) {
		this.expandedState = expandedState;
	}
}