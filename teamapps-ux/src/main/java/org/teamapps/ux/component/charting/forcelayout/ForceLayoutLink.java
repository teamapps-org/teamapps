package org.teamapps.ux.component.charting.forcelayout;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiNetworkLink;
import org.teamapps.util.UiUtil;

public class ForceLayoutLink {

	private final ForceLayoutNode source;
	private final ForceLayoutNode target;

	private float lineWidth = 1f;
	private Color lineColor = Color.MATERIAL_GREY_500;
	private String lineDashArray;

	public ForceLayoutLink(ForceLayoutNode source, ForceLayoutNode target) {
		this.source = source;
		this.target = target;
	}

	public UiNetworkLink toUiNetworkLink() {
		UiNetworkLink ui = new UiNetworkLink(source.getId(), target.getId());
		ui.setLineWidth(lineWidth);
		ui.setLineColor(UiUtil.createUiColor(lineColor));
		ui.setLineDashArray(lineDashArray);
		return ui;
	}

	public ForceLayoutNode getSource() {
		return source;
	}

	public ForceLayoutNode getTarget() {
		return target;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(float lineWidth) {
		this.lineWidth = lineWidth;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public String getLineDashArray() {
		return lineDashArray;
	}

	public void setLineDashArray(String lineDashArray) {
		this.lineDashArray = lineDashArray;
	}
}
