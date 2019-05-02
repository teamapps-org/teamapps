package org.teamapps.ux.component.charting.pie;

import org.teamapps.common.format.Color;
import org.teamapps.dto.UiChartNamedDataPoint;
import org.teamapps.util.UiUtil;

public class ChartNamedDataPoint {

	private final String name;
	private final double value;
	private final Color color;

	public ChartNamedDataPoint(String name, double value, Color color) {
		this.name = name;
		this.value = value;
		this.color = color;
	}

	public UiChartNamedDataPoint createUiChartNamedDataPoint() {
		UiChartNamedDataPoint ui = new UiChartNamedDataPoint(name, value);
		ui.setColor(UiUtil.createUiColor(color));
		return ui;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public Color getColor() {
		return color;
	}
}
