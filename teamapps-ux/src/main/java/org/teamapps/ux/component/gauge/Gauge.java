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
package org.teamapps.ux.component.gauge;

import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiGauge;
import org.teamapps.dto.UiGaugeOptions;
import org.teamapps.ux.component.AbstractComponent;

public class Gauge extends AbstractComponent {

	private UiGaugeOptions options;
	private double value;

	public Gauge(UiGaugeOptions options) {
		super();
		this.options = options;
	}

	@Override
	public UiComponent createUiComponent() {
		UiGauge uiGauge = new UiGauge(options);
		mapAbstractUiComponentProperties(uiGauge);
		return uiGauge;
	}

	@Override
	public void handleUiEvent(UiEvent event) {

	}

	public void setValue(double value) {
		if (this.value == value) {
			return;
		}
		this.value = value;
		queueCommandIfRendered(() -> new UiGauge.SetValueCommand(getId(), value));
	}

	public void setOptions(UiGaugeOptions options) {
		this.options = options;
		queueCommandIfRendered(() -> new UiGauge.SetOptionsCommand(getId(), options));
	}

	public double getValue() {
		return value;
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
