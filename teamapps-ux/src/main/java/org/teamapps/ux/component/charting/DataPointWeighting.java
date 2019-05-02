package org.teamapps.ux.component.charting;

import org.teamapps.dto.UiDataPointWeighting;

public enum DataPointWeighting {

	RELATIVE, ABSOLUTE;

	public UiDataPointWeighting toUiDataPointWeighting() {
		return UiDataPointWeighting.valueOf(name());
	}

}
