package org.teamapps.ux.component.timegraph;

import org.teamapps.common.format.Color;
import org.teamapps.dto.AbstractUiLineChartDataDisplay;
import org.teamapps.dto.UiLongInterval;

import java.util.List;
import java.util.UUID;

import static org.teamapps.util.UiUtil.createUiColor;

public abstract class AbstractLineChartDataDisplay  {

	private final String id = UUID.randomUUID().toString();
	protected LineChartDataDisplayChangeListener changeListener;
	private Interval intervalY;
	private ScaleType yScaleType = ScaleType.LINEAR;
	private LineChartYScaleZoomMode yScaleZoomMode = LineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO;
	private boolean yZeroLineVisible = false;
	private Color yAxisColor = Color.BLACK;

	public String getId() {
		return id;
	}

	abstract public List<String> getDataSeriesIds();

	abstract public AbstractUiLineChartDataDisplay createUiFormat();

	public Interval getIntervalY() {
		return intervalY;
	}

	public AbstractLineChartDataDisplay setIntervalY(Interval intervalY) {
		this.intervalY = intervalY;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public ScaleType getyScaleType() {
		return yScaleType;
	}

	public AbstractLineChartDataDisplay setYScaleType(ScaleType yScaleType) {
		this.yScaleType = yScaleType;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public LineChartYScaleZoomMode getYScaleZoomMode() {
		return yScaleZoomMode;
	}

	public AbstractLineChartDataDisplay setYScaleZoomMode(LineChartYScaleZoomMode yScaleZoomMode) {
		this.yScaleZoomMode = yScaleZoomMode;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	abstract public void setChangeListener(LineChartDataDisplayChangeListener listener);

	protected void mapAbstractLineChartDataDisplayProperties(AbstractUiLineChartDataDisplay ui) {
		ui.setId(id);
		ui.setYAxisColor(yAxisColor != null ? createUiColor(yAxisColor) : null);
		ui.setIntervalY(intervalY != null ? intervalY.createUiLongInterval() : new UiLongInterval(0, 1000));
		ui.setYScaleType(yScaleType.toUiScaleType());
		ui.setYScaleZoomMode(yScaleZoomMode.toUiLineChartYScaleZoomMode());
		ui.setYZeroLineVisible(yZeroLineVisible);
	}

	public boolean isYZeroLineVisible() {
		return yZeroLineVisible;
	}

	public AbstractLineChartDataDisplay setYZeroLineVisible(boolean yZeroLineVisible) {
		this.yZeroLineVisible = yZeroLineVisible;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}

	public Color getYAxisColor() {
		return yAxisColor;
	}

	public AbstractLineChartDataDisplay setYAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
		return this;
	}
}
