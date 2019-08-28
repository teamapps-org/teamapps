package org.teamapps.ux.component.timegraph;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public interface LineChartDataPoints {

	int size();

	double getX(int index);

	double getY(int index);

	LineChartDataPoint getDataPoint(int index);

	default DoubleStream streamX() {
		int[] i = {0};
		return DoubleStream.generate(() -> getX(i[0]++))
				.limit(size());
	}

	default DoubleStream streamY() {
		int[] i = {0};
		return DoubleStream.generate(() -> getX(i[0]++))
				.limit(size());
	}

	default Stream<LineChartDataPoint> streamDataPoints() {
		int[] i = {0};
		return Stream.generate(() -> getDataPoint(i[0]++))
				.limit(size());
	}

}
