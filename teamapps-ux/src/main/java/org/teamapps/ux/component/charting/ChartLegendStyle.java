package org.teamapps.ux.component.charting;

import org.teamapps.dto.UiChartLegendStyle;

public enum ChartLegendStyle {

	NONE,
	INLINE,
	SEPARATE_TOP,
	SEPARATE_LEFT,
	SEPARATE_BOTTOM,
	SEPARATE_RIGHT;

	public UiChartLegendStyle toUiChartLegendStyle() {
		return UiChartLegendStyle.valueOf(name());
	}

}
