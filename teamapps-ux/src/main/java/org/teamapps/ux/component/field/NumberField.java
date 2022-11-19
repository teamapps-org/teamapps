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
package org.teamapps.ux.component.field;

import com.ibm.icu.util.ULocale;
import org.teamapps.dto.DtoEventWrapper;
import org.teamapps.dto.DtoNumberField;
import org.teamapps.dto.DtoField;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.component.CoreComponentLibrary;
import org.teamapps.ux.component.TeamAppsComponent;
import org.teamapps.ux.session.SessionContext;

import java.util.Locale;

@TeamAppsComponent(library = CoreComponentLibrary.class)
public class NumberField extends AbstractField<Number> implements TextInputHandlingField {

	public final ProjectorEvent<String> onTextInput = createProjectorEventBoundToUiEvent(DtoNumberField.TextInputEvent.TYPE_ID);
	public final ProjectorEvent<SpecialKey> onSpecialKeyPressed = createProjectorEventBoundToUiEvent(DtoNumberField.SpecialKeyPressedEvent.TYPE_ID);

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
	public DtoField createUiClientObject() {
		DtoNumberField field = new DtoNumberField();
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
	public void handleUiEvent(DtoEventWrapper event) {
		super.handleUiEvent(event);
		defaultHandleTextInputEvent(event);
	}

	public int getPrecision() {
		return precision;
	}

	public NumberField setPrecision(int precision) {
		this.precision = precision;
		sendCommandIfRendered(() -> new DtoNumberField.SetPrecisionCommand(precision));
		return this;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public NumberField setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		sendCommandIfRendered(() -> new DtoNumberField.SetPlaceholderTextCommand(emptyText));
		return this;
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public NumberField setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		sendCommandIfRendered(() -> new DtoNumberField.SetShowClearButtonCommand(showClearButton));
		return this;
	}

	public double getMinValue() {
		return minValue;
	}

	public NumberField setMinValue(double minValue) {
		this.minValue = minValue;
		sendCommandIfRendered(() -> new DtoNumberField.SetMinValueCommand(minValue));
		return this;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public NumberField setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		sendCommandIfRendered(() -> new DtoNumberField.SetMaxValueCommand(maxValue));
		return this;
	}

	public NumberFieldSliderMode getSliderMode() {
		return sliderMode;
	}

	public NumberField setSliderMode(NumberFieldSliderMode sliderMode) {
		this.sliderMode = sliderMode;
		sendCommandIfRendered(() -> new DtoNumberField.SetSliderModeCommand(sliderMode.toUiNumberFieldSliderMode()));
		return this;
	}

	public double getSliderStep() {
		return sliderStep;
	}

	public NumberField setSliderStep(double sliderStep) {
		this.sliderStep = sliderStep;
		sendCommandIfRendered(() -> new DtoNumberField.SetSliderStepCommand(sliderStep));
		return this;
	}

	public boolean isCommitOnSliderChange() {
		return commitOnSliderChange;
	}

	public NumberField setCommitOnSliderChange(boolean commitOnSliderChange) {
		this.commitOnSliderChange = commitOnSliderChange;
		sendCommandIfRendered(() -> new DtoNumberField.SetCommitOnSliderChangeCommand(commitOnSliderChange));
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
		sendCommandIfRendered(() -> new DtoNumberField.SetLocaleCommand(locale.toLanguageTag()));
	}

	@Override
	public ProjectorEvent<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public ProjectorEvent<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}
}
