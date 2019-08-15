package org.teamapps.ux.component.charting.forcelayout;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiNetworkLink;

public class ForceLayoutLink {

	private final ForceLayoutNode source;
	private final ForceLayoutNode target;

	private float width = 1.5f;
	private int distance = 30;
	private Color color = Color.BLACK;

	public ForceLayoutLink(ForceLayoutNode source, ForceLayoutNode target) {
		this.source = source;
		this.target = target;
	}

	public UiNetworkLink toUiNetworkLink() {
		UiNetworkLink ui = new UiNetworkLink(source.getId(), target.getId());
		ui.setWidth(width);
		ui.setDistance(distance);
		ui.setColor(color.toHtmlColorString());
		return ui;
	}

	public ForceLayoutNode getSource() {
		return source;
	}

	public ForceLayoutNode getTarget() {
		return target;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
