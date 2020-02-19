/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
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

	public void setChangeListener(LineChartDataDisplayChangeListener listener) {
		this.changeListener = listener;
	}

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
