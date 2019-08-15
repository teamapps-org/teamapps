package org.teamapps.ux.component.charting.common;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiTreeGraphNodeImage;
import org.teamapps.util.UiUtil;

public class GraphNodeImage {

	public enum CornerShape {
		ORIGINAL, ROUNDED, CIRCLE;

		public UiTreeGraphNodeImage.CornerShape toUiCornerShape() {
			return UiTreeGraphNodeImage.CornerShape.valueOf(this.name());
		}
	}
	private String url;

	private int width;
	private int height;
	private int centerTopDistance;
	private int centerLeftDistance;
	private CornerShape cornerShape = CornerShape.ROUNDED;
	private boolean shadow = false;
	private float borderWidth = 0;
	private Color borderColor = new Color(100, 100, 100);

	public GraphNodeImage(String url, int width, int height) {
		this.url = url;
		this.width = width;
		this.height = height;
	}

	public UiTreeGraphNodeImage createUiTreeGraphNodeImage() {
		UiTreeGraphNodeImage ui = new UiTreeGraphNodeImage(url, width, height);
		ui.setCenterTopDistance(centerTopDistance);
		ui.setCenterLeftDistance(centerLeftDistance);
		ui.setCornerShape(cornerShape.toUiCornerShape());
		ui.setShadow(shadow);
		ui.setBorderWidth(borderWidth);
		ui.setBorderColor(UiUtil.createUiColor(borderColor));
		return ui;
	}

	public String getUrl() {
		return url;
	}

	public GraphNodeImage setUrl(String url) {
		this.url = url;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public GraphNodeImage setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getHeight() {
		return height;
	}

	public GraphNodeImage setHeight(int height) {
		this.height = height;
		return this;
	}

	public int getCenterTopDistance() {
		return centerTopDistance;
	}

	public GraphNodeImage setCenterTopDistance(int centerTopDistance) {
		this.centerTopDistance = centerTopDistance;
		return this;
	}

	public int getCenterLeftDistance() {
		return centerLeftDistance;
	}

	public GraphNodeImage setCenterLeftDistance(int centerLeftDistance) {
		this.centerLeftDistance = centerLeftDistance;
		return this;
	}

	public CornerShape getCornerShape() {
		return cornerShape;
	}

	public GraphNodeImage setCornerShape(CornerShape cornerShape) {
		this.cornerShape = cornerShape;
		return this;
	}

	public boolean isShadow() {
		return shadow;
	}

	public GraphNodeImage setShadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}

	public float getBorderWidth() {
		return borderWidth;
	}

	public GraphNodeImage setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		return this;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public GraphNodeImage setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		return this;
	}
}