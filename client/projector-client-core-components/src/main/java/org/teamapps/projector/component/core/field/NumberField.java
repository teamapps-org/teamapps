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
package org.teamapps.projector.component.core.field;

import com.fasterxml.jackson.databind.JsonNode;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.projector.component.core.*;
import org.teamapps.projector.component.field.AbstractField;
import org.teamapps.projector.component.field.DtoAbstractField;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.session.SessionContext;

import java.util.Locale;

@ClientObjectLibrary(value = CoreComponentLibrary.class)
public class NumberField extends AbstractField<Number> implements DtoNumberFieldEventHandler {

	private final DtoNumberFieldClientObjectChannel clientObjectChannel = new DtoNumberFieldClientObjectChannel(getClientObjectChannel());

	public final ProjectorEvent<String> onTextInput = new ProjectorEvent<>(clientObjectChannel::toggleTextInputEvent);
	public final ProjectorEvent<SpecialKey> onSpecialKeyPressed = new ProjectorEvent<>(clientObjectChannel::toggleSpecialKeyPressedEvent);

	private int precision; // if == 0: integer, if < 0: maximum available precision
	private String emptyText;
	private boolean showClearButton;

	private double minValue = Integer.MIN_VALUE;
	private double maxValue = Integer.MAX_VALUE;
	private NumberFieldSliderMode sliderMode = NumberFieldSliderMode.DISABLED;
	private double sliderStep = 1;
	private boolean commitOnSliderChange = true;

	private Locale locale = SessionContext.current().getLocale();

	public NumberField(int precision) {
		super();
		this.precision = precision;
	}

	@Override
	public DtoAbstractField createDto() {
		DtoNumberField field = new DtoNumberField();
		mapAbstractFieldAttributes(field);
		field.setPrecision(precision);
		field.setPlaceholderText(emptyText);
		field.setShowClearButton(showClearButton);
		field.setMinValue(minValue);
		field.setMaxValue(maxValue);
		field.setSliderMode(sliderMode);
		field.setSliderStep(sliderStep);
		field.setCommitOnSliderChange(commitOnSliderChange);
		field.setLocale(locale.toLanguageTag());
		return field;
	}

	@Override
	public void handleTextInput(String enteredString) {
		onTextInput.fire(enteredString);
	}

	@Override
	public void handleSpecialKeyPressed(SpecialKey key) {
		onSpecialKeyPressed.fire(key);
	}

	public int getPrecision() {
		return precision;
	}

	public NumberField setPrecision(int precision) {
		this.precision = precision;
		clientObjectChannel.setPrecision(precision);
		return this;
	}

	public String getEmptyText() {
		return emptyText;
	}

	public NumberField setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		clientObjectChannel.setPlaceholderText(emptyText);
		return this;
	}

	public boolean isShowClearButton() {
		return showClearButton;
	}

	public NumberField setShowClearButton(boolean showClearButton) {
		this.showClearButton = showClearButton;
		clientObjectChannel.setShowClearButton(showClearButton);
		return this;
	}

	public double getMinValue() {
		return minValue;
	}

	public NumberField setMinValue(double minValue) {
		this.minValue = minValue;
		clientObjectChannel.setMinValue(minValue);
		return this;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public NumberField setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		clientObjectChannel.setMaxValue(maxValue);
		return this;
	}

	public NumberFieldSliderMode getSliderMode() {
		return sliderMode;
	}

	public NumberField setSliderMode(NumberFieldSliderMode sliderMode) {
		this.sliderMode = sliderMode;
		clientObjectChannel.setSliderMode(sliderMode);
		return this;
	}

	public double getSliderStep() {
		return sliderStep;
	}

	public NumberField setSliderStep(double sliderStep) {
		this.sliderStep = sliderStep;
		clientObjectChannel.setSliderStep(sliderStep);
		return this;
	}

	public boolean isCommitOnSliderChange() {
		return commitOnSliderChange;
	}

	public NumberField setCommitOnSliderChange(boolean commitOnSliderChange) {
		this.commitOnSliderChange = commitOnSliderChange;
		clientObjectChannel.setCommitOnSliderChange(commitOnSliderChange);
		return this;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		clientObjectChannel.setLocale(locale.toLanguageTag());
	}

	@Override
	public Number doConvertClientValueToServerValue(JsonNode value) {
		return value.asDouble();
	}
}
