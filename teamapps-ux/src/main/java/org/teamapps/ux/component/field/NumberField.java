/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import com.ibm.icu.util.ULocale;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.dto.UiNumberField;
import org.teamapps.event.Event;
import org.teamapps.ux.session.SessionContext;

import java.util.Locale;

public class NumberField extends AbstractField<Number> implements TextInputHandlingField {

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	private int precision; // if == 0: integer, if < 0: maximum available precision
	private String emptyText;
	private boolean showClearButton;

	private double minValue = Integer.MIN_VALUE;
	private double maxValue = Integer.MAX_VALUE;
	private NumberFieldSliderMode sliderMode = NumberFieldSliderMode.DISABLED;
	private double sliderStep = 1;
	private boolean commitOnSliderChange = true;

	private ULocale locale = SessionContext.current().getULocale();

	public NumberField(int precision) {
		super();
		this.precision = precision;
	}

	@Override
	public UiField createUiComponent() {
		UiNumberField field = new UiNumberField();
		mapAbstractFieldAttributesToUiField(field);
		field.setPrecision(precision);
		field.setPlaceholderText(emptyText);
		field.setShowClearButton(showClearButton);
		field.setMinValue(minValue);
		field.setMaxValue(maxValue);
		field.setSliderMode(sliderMode.toUiNumberFieldSliderMode());
		field.setSliderStep(sliderStep);
		field.setCommitOnSliderChange(commitOnSliderChange);
		field.setLocale(locale.toLanguageTag());
		return field;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		defaultHandleTextInputEvent(event);
	}

	public int getPrecision() {
		return precision;
	}

	public NumberField setPrecision(int precision) {
		this.precision = precision;
		queueCommandIfRendered(() -> new UiNumberField.SetPrecisionCommand(getId(), precision));
		return this;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public NumberField setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		queueCommandIfRendered(() -> new UiNumberField.SetPlaceholderTextCommand(getId(), emptyText));
		return this;
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public NumberField setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		queueCommandIfRendered(() -> new UiNumberField.SetShowClearButtonCommand(getId(), showClearButton));
		return this;
	}

	public double getMinValue() {
		return minValue;
	}

	public NumberField setMinValue(double minValue) {
		this.minValue = minValue;
		queueCommandIfRendered(() -> new UiNumberField.SetMinValueCommand(getId(), minValue));
		return this;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public NumberField setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		queueCommandIfRendered(() -> new UiNumberField.SetMaxValueCommand(getId(), maxValue));
		return this;
	}

	public NumberFieldSliderMode getSliderMode() {
		return sliderMode;
	}

	public NumberField setSliderMode(NumberFieldSliderMode sliderMode) {
		this.sliderMode = sliderMode;
		queueCommandIfRendered(() -> new UiNumberField.SetSliderModeCommand(getId(), sliderMode.toUiNumberFieldSliderMode()));
		return this;
	}

	public double getSliderStep() {
		return sliderStep;
	}

	public NumberField setSliderStep(double sliderStep) {
		this.sliderStep = sliderStep;
		queueCommandIfRendered(() -> new UiNumberField.SetSliderStepCommand(getId(), sliderStep));
		return this;
	}

	public boolean isCommitOnSliderChange() {
		return commitOnSliderChange;
	}

	public NumberField setCommitOnSliderChange(boolean commitOnSliderChange) {
		this.commitOnSliderChange = commitOnSliderChange;
		queueCommandIfRendered(() -> new UiNumberField.SetCommitOnSliderChangeCommand(getId(), commitOnSliderChange));
		return this;
	}

	public Locale getLocale() {
		return locale.toLocale();
	}

	public ULocale getULocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		setULocale(ULocale.forLocale(locale));
	}

	public void setULocale(ULocale locale) {
		this.locale = locale;
		queueCommandIfRendered(() -> new UiNumberField.SetLocaleCommand(getId(), locale.toLanguageTag()));
	}

	@Override
	public Event<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public Event<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}
}
