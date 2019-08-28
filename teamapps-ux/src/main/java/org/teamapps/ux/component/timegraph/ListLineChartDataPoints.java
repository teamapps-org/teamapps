package org.teamapps.ux.component.timegraph;

import java.util.List;

public class ListLineChartDataPoints implements LineChartDataPoints {

	private List<LineChartDataPoint> dataPoints;

	public ListLineChartDataPoints(List<LineChartDataPoint> dataPoints) {
		this.dataPoints = dataPoints;
	}

	@Override
	public int size() {
		return dataPoints.size();
	}

	@Override
	public double getX(int index) {
		return dataPoints.get(index).getX();
	}

	@Override
	public double getY(int index) {
		return dataPoints.get(index).getY();
	}

	@Override
	public LineChartDataPoint getDataPoint(int index) {
		return dataPoints.get(index);
	}

}
