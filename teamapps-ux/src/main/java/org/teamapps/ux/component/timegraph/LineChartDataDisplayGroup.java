/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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

import org.teamapps.dto.AbstractUiLineChartDataDisplay;
import org.teamapps.dto.UiLineChartDataDisplayGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LineChartDataDisplayGroup extends AbstractLineChartDataDisplay {

	private LineChartDataDisplayChangeListener changeListener;

	private final List<AbstractLineChartDataDisplay> dataDisplays = new ArrayList<>();

	public LineChartDataDisplayGroup() {
	}

	public LineChartDataDisplayGroup(AbstractLineChartDataDisplay... dataDisplays) {
		this(Arrays.asList(dataDisplays));
	}

	public LineChartDataDisplayGroup(List<AbstractLineChartDataDisplay> dataDisplays) {
		this.dataDisplays.addAll(dataDisplays);
		dataDisplays.forEach(dd -> {
			if (this.changeListener != null) {
				this.changeListener.handleChange(this);
			}
		});
	}

	@Override
	public AbstractUiLineChartDataDisplay createUiFormat() {
		UiLineChartDataDisplayGroup ui = new UiLineChartDataDisplayGroup();
		mapAbstractLineChartDataDisplayProperties(ui);
		ui.setDataDisplays(dataDisplays.stream().map(AbstractLineChartDataDisplay::createUiFormat).collect(Collectors.toList()));
		return ui;
	}

	@Override
	public List<String> getDataSeriesIds() {
		return dataDisplays.stream()
				.flatMap(dd -> dd.getDataSeriesIds().stream())
				.collect(Collectors.toList());
	}

	@Override
	public void setChangeListener(LineChartDataDisplayChangeListener listener) {
		this.changeListener = listener;
	}

	public void addDataDisplay(AbstractLineChartDataDisplay dataDisplay) {
		this.dataDisplays.add(dataDisplay);
		if (this.changeListener != null) {
			this.changeListener.handleChange(this);
		}
	}

	public List<AbstractLineChartDataDisplay> getDataDisplays() {
		return dataDisplays;
	}
}
