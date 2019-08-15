package org.teamapps.ux.component.charting.forcelayout;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiNetworkNode;
import org.teamapps.ux.component.charting.tree.TreeGraphNodeIcon;
import org.teamapps.ux.component.charting.tree.TreeGraphNodeImage;
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
	private float borderRadius = 0;

	private TreeGraphNodeImage image;
	private TreeGraphNodeIcon icon;

	private Template template;

	public ForceLayoutNode(RECORD record) {
		this.record = record;
	}

	public UiNetworkNode toUiNetworkNode() {
		UiNetworkNode ui = new UiNetworkNode(id, record.toString() /* TODO #force */, null, width /* TODO #force */);
		ui.setBorder(borderWidth)     ;
		ui.setBorderColor(borderColor.toHtmlColorString());
		return ui;
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

	public TreeGraphNodeImage getImage() {
		return image;
	}

	public ForceLayoutNode<RECORD> setImage(TreeGraphNodeImage image) {
		this.image = image;
		return this;
	}

	public TreeGraphNodeIcon getIcon() {
		return icon;
	}

	public ForceLayoutNode<RECORD> setIcon(TreeGraphNodeIcon icon) {
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

}