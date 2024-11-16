/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
package org.teamapps.projector.component.timegraph.graph;

import org.teamapps.common.format.Color;
import org.teamapps.common.format.RgbaColor;
import org.teamapps.projector.component.timegraph.*;
import org.teamapps.projector.component.timegraph.datapoints.GraphData;
import org.teamapps.projector.component.timegraph.model.GraphModel;

import java.util.UUID;

public abstract class AbstractGraph<D extends GraphData, M extends GraphModel<D>> {

	private final String id = UUID.randomUUID().toString();
	protected GraphChangeListener changeListener;
	private M model;

	private Interval displayedIntervalY;
	private ScaleType yScaleType = ScaleType.LINEAR;
	private LineChartYScaleZoomMode yScaleZoomMode = LineChartYScaleZoomMode.DYNAMIC_INCLUDING_ZERO;
	private boolean yZeroLineVisible = false;
	private boolean yAxisVisible = true;
	private Color yAxisColor = RgbaColor.BLACK;

	public AbstractGraph(M model) {
		this.model = model;
	}

	public String getId() {
		return id;
	}

	abstract public DtoGraph createDtoFormat();

	public Interval getDisplayedIntervalY() {
		return displayedIntervalY;
	}

	public AbstractGraph setDisplayedIntervalY(Interval displayedIntervalY) {
		this.displayedIntervalY = displayedIntervalY;
		fireChange();
		return this;
	}

	public ScaleType getYScaleType() {
		return yScaleType;
	}

	public AbstractGraph setYScaleType(ScaleType yScaleType) {
		this.yScaleType = yScaleType;
		fireChange();
		return this;
	}

	public LineChartYScaleZoomMode getYScaleZoomMode() {
		return yScaleZoomMode;
	}

	public AbstractGraph setYScaleZoomMode(LineChartYScaleZoomMode yScaleZoomMode) {
		this.yScaleZoomMode = yScaleZoomMode;
		fireChange();
		return this;
	}

	public void setChangeListener(GraphChangeListener listener) {
		this.changeListener = listener;
	}

	protected void mapAbstractLineChartDataDisplayProperties(DtoGraph ui) {
		ui.setId(id);
		ui.setYAxisVisible(yAxisVisible);
		ui.setYAxisColor(yAxisColor != null ? yAxisColor.toHtmlColorString() : null);
		ui.setIntervalY(displayedIntervalY != null ? displayedIntervalY.toUiLongInterval() : new DtoLongInterval(0, 1000));
		ui.setYScaleType(yScaleType);
		ui.setYScaleZoomMode(yScaleZoomMode);
		ui.setYZeroLineVisible(yZeroLineVisible);
	}

	public boolean isYZeroLineVisible() {
		return yZeroLineVisible;
	}

	public AbstractGraph setYZeroLineVisible(boolean yZeroLineVisible) {
		this.yZeroLineVisible = yZeroLineVisible;
		fireChange();
		return this;
	}

	public boolean isyAxisVisible() {
		return yAxisVisible;
	}

	public AbstractGraph setYAxisVisible(boolean yAxisVisible) {
		this.yAxisVisible = yAxisVisible;
		fireChange();
		return this;
	}

	public Color getYAxisColor() {
		return yAxisColor;
	}

	public AbstractGraph setYAxisColor(Color yAxisColor) {
		this.yAxisColor = yAxisColor;
		fireChange();
		return this;
	}

	public M getModel() {
		return model;
	}

	public AbstractGraph<D, M> setModel(M model) {
		this.model = model;
		fireChange();
		return this;
	}

	protected void fireChange() {
		if (this.changeListener != null) {
			changeListener.handleChange(this);
		}
	}
}
