package org.teamapps.ux.component.timegraph;

import org.teamapps.dto.AbstractUiLineChartDataDisplay;

import java.util.List;

public interface LineChartDataDisplay {

	/**
	 * @deprecated only for internal use!
	 */
	@Deprecated
	String getId();

	List<String> getDataSourceIds();

	AbstractUiLineChartDataDisplay createUiFormat();

	void setChangeListener(LineChartDataDisplayChangeListener listener);

	default void mapAbstractLineChartDataDisplayProperties(AbstractUiLineChartDataDisplay ui) {
		ui.setId(getId());
	}

}
