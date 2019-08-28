package org.teamapps.ux.component.timegraph;

public class PrimitiveArrayLineChartDataPoints implements LineChartDataPoints {

	private double[] x;
	private double[] y;

	public PrimitiveArrayLineChartDataPoints(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("Arrays must have the same length!");
		}
		this.x = x;
		this.y = y;
	}

	@Override
	public int size() {
		return x.length;
	}

	@Override
	public double getX(int index) {
		return x[index];
	}

	@Override
	public double getY(int index) {
		return y[index];
	}

	@Override
	public LineChartDataPoint getDataPoint(int index) {
		return new LineChartDataPoint(x[index], y[index]);
	}
	
}
