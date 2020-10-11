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
package org.teamapps.ux.component.field;

import org.teamapps.common.format.RgbaColor;
import org.teamapps.common.format.Color;
import org.teamapps.dto.UiField;
import org.teamapps.dto.UiSlider;

public class Slider extends AbstractField<Number> {

	private double min = 0;
	private double max = 100;
	private double step = 1;
	private int displayedDecimals = 0;
	private Color selectionColor = new RgbaColor(51, 122, 183);
	private String tooltipPrefix;
	private String tooltipPostfix;
	private boolean humanReadableFileSize;

	public Slider() {
		super();
	}

	@Override
	public UiField createUiComponent() {
		UiSlider uiSlider = new UiSlider();
		mapAbstractFieldAttributesToUiField(uiSlider);
		uiSlider.setMin(min);
		uiSlider.setMax(max);
		uiSlider.setStep(step);
		uiSlider.setDisplayedDecimals(displayedDecimals);
		uiSlider.setSelectionColor(selectionColor != null ? selectionColor.toHtmlColorString() : null);
		uiSlider.setTooltipPrefix(tooltipPrefix);
		uiSlider.setTooltipPostfix(tooltipPostfix);
		uiSlider.setHumanReadableFileSize(humanReadableFileSize);
		return uiSlider;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
		queueCommandIfRendered(() -> new UiSlider.SetMinCommand(getId(), min));
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
		queueCommandIfRendered(() -> new UiSlider.SetMaxCommand(getId(), max));
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
		queueCommandIfRendered(() -> new UiSlider.SetStepCommand(getId(), step));
	}

	public int getDisplayedDecimals() {
		return displayedDecimals;
	}

	public void setDisplayedDecimals(int displayedDecimals) {
		this.displayedDecimals = displayedDecimals;
		queueCommandIfRendered(() -> new UiSlider.SetDisplayedDecimalsCommand(getId(), displayedDecimals));
	}

	public Color getSelectionColor() {
		return selectionColor;
	}

	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
		queueCommandIfRendered(() -> new UiSlider.SetSelectionColorCommand(getId(), selectionColor != null ? selectionColor.toHtmlColorString() : null));
	}

	public String getTooltipPrefix() {
		return tooltipPrefix;
	}

	public void setTooltipPrefix(String tooltipPrefix) {
		this.tooltipPrefix = tooltipPrefix;
		queueCommandIfRendered(() -> new UiSlider.SetTooltipPrefixCommand(getId(), tooltipPrefix));
	}

	public String getTooltipPostfix() {
		return tooltipPostfix;
	}

	public void setTooltipPostfix(String tooltipPostfix) {
		this.tooltipPostfix = tooltipPostfix;
		queueCommandIfRendered(() -> new UiSlider.SetTooltipPostfixCommand(getId(), tooltipPostfix));
	}

	public boolean isHumanReadableFileSize() {
		return humanReadableFileSize;
	}

	public void setHumanReadableFileSize(boolean humanReadableFileSize) {
		this.humanReadableFileSize = humanReadableFileSize;
		queueCommandIfRendered(() -> new UiSlider.SetHumanReadableFileSizeCommand(getId(), humanReadableFileSize));
	}
}
