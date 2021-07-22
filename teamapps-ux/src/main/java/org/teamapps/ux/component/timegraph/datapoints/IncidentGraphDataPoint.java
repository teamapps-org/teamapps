package org.teamapps.ux.component.timegraph.datapoints;

import org.teamapps.common.format.Color;

public class IncidentGraphDataPoint {

	private final double x1;
	private final double x2;
	private final double y;
	private final Color color;
	private final String tooltipHtml;

	public IncidentGraphDataPoint(double x1, double x2, double y, Color color, String tooltipHtml) {
		this.x1 = x1;
		this.x2 = x2;
		this.y = y;
		this.color = color;
		this.tooltipHtml = tooltipHtml;
	}

	public IncidentGraphDataPoint(double x1, double x2, double y, Color color) {
		this(x1, x2, y, color, null);
	}

	public double getX1() {
		return x1;
	}

	public double getX2() {
		return x2;
	}

	public double getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public String getTooltipHtml() {
		return tooltipHtml;
	}
}
