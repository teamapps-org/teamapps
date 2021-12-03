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
package org.teamapps.ux.component.field.multicurrency;

import com.ibm.icu.util.ULocale;
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.ux.component.field.multicurrency.value.CurrencyUnit;
import org.teamapps.ux.component.field.multicurrency.value.CurrencyValue;
import org.teamapps.ux.session.SessionContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CurrencyField extends AbstractField<CurrencyValue> implements TextInputHandlingField {

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	private ULocale locale = SessionContext.current().getULocale();

	private List<CurrencyUnit> currencyUnits;

	/**
	 * If this is >= 0 it will overwrite the precision of the currencies.
	 */
	private int fixedPrecision = -1;

	private boolean alphabeticKeysQueryEnabled = true;

	/**
	 * If true, the currency indicator is on the left of the amount. If false, it is on the right of the amount.
	 */
	private boolean currencyBeforeAmount;

	/**
	 * Whether to show the currency symbol additionally to the currency code.
	 */
	private boolean currencySymbolsEnabled = true;

	public CurrencyField() {
		this(CurrencyUnit.getAllAvailableFromJdk());
	}

	public CurrencyField(List<CurrencyUnit> currencyUnits) {
		this.currencyUnits = currencyUnits;
	}

	@Override
	public UiField createUiComponent() {
		UiCurrencyField field = new UiCurrencyField();
		mapAbstractFieldAttributesToUiField(field);
		field.setCurrencyUnits(currencyUnits.stream()
				.map(unit -> unit.toUiCurrencyUnit(locale.toLocale()))
				.collect(Collectors.toList()));
		field.setFixedPrecision(fixedPrecision);
		field.setShowCurrencyBeforeAmount(currencyBeforeAmount);
		field.setShowCurrencySymbol(currencySymbolsEnabled);
		field.setAlphaKeysQueryForCurrency(alphabeticKeysQueryEnabled);
		field.setLocale(locale.toLanguageTag());
		return field;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		defaultHandleTextInputEvent(event);
	}

	public List<CurrencyUnit> getCurrencyUnits() {
		return currencyUnits;
	}

	public CurrencyField setCurrencyUnits(List<CurrencyUnit> currencyUnits) {
		this.currencyUnits = currencyUnits;
		queueCommandIfRendered(() -> new UiCurrencyField.SetCurrencyUnitsCommand(getId(), currencyUnits != null ? currencyUnits.stream()
				.map(unit -> unit.toUiCurrencyUnit(locale.toLocale()))
				.collect(Collectors.toList()) : null));
		return this;
	}

	@Override
	public Object convertUxValueToUiValue(CurrencyValue currencyValue) {
		return currencyValue != null ? currencyValue.toUiCurrencyValue(locale.toLocale()) : null;
	}

	@Override
	public CurrencyValue convertUiValueToUxValue(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof UiCurrencyValue) {
			UiCurrencyValue uiCurrencyValue = (UiCurrencyValue) value;
			String uiAmount = uiCurrencyValue.getAmount();
			BigDecimal uxAmount = uiAmount != null ? new BigDecimal(uiAmount) : null;

			UiCurrencyUnit uiCurrencyUnit = uiCurrencyValue.getCurrencyUnit();
			CurrencyUnit uxCurrencyUnit;
			if (uiCurrencyUnit != null) {
				uxCurrencyUnit = currencyUnits.stream()
						.filter(cu -> cu.getCode().equals(uiCurrencyUnit.getCode()) && cu.getFractionDigits() == uiCurrencyUnit.getFractionDigits())
						.findFirst()
						.orElseGet(() -> CurrencyUnit.from(uiCurrencyUnit.getCode(), uiCurrencyUnit.getFractionDigits(), uiCurrencyUnit.getName(), uiCurrencyUnit.getSymbol()));
			} else {
				uxCurrencyUnit = null;
			}
			return new CurrencyValue(uxCurrencyUnit, uxAmount);
		} else {
			throw new IllegalArgumentException("Unknown value type for CurrencyField: " + value.getClass().getCanonicalName());
		}
	}

	public int getFixedPrecision() {
		return fixedPrecision;
	}

	public boolean isCurrencyBeforeAmount() {
		return currencyBeforeAmount;
	}

	public CurrencyField setCurrencyBeforeAmount(boolean currencyBeforeAmount) {
		this.currencyBeforeAmount = currencyBeforeAmount;
		queueCommandIfRendered(() -> new UiCurrencyField.SetShowCurrencyBeforeAmountCommand(getId(), currencyBeforeAmount));
		return this;
	}

	public boolean isCurrencySymbolsEnabled() {
		return currencySymbolsEnabled;
	}

	public CurrencyField setCurrencySymbolsEnabled(boolean currencySymbolsEnabled) {
		this.currencySymbolsEnabled = currencySymbolsEnabled;
		queueCommandIfRendered(() -> new UiCurrencyField.SetShowCurrencySymbolCommand(getId(), currencySymbolsEnabled));
		return this;
	}

	public boolean isAlphabeticKeysQueryEnabled() {
		return alphabeticKeysQueryEnabled;
	}

	public void setFixedPrecision(int fixedPrecision) {
		this.fixedPrecision = fixedPrecision;
		queueCommandIfRendered(() -> new UiCurrencyField.SetFixedPrecisionCommand(getId(), fixedPrecision));
	}

	public void setAlphabeticKeysQueryEnabled(boolean alphabeticKeysQueryEnabled) {
		this.alphabeticKeysQueryEnabled = alphabeticKeysQueryEnabled;
		reRenderIfRendered();
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
		setCurrencyUnits(currencyUnits);
		queueCommandIfRendered(() -> new UiCurrencyField.SetLocaleCommand(getId(), locale.toLanguageTag()));
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
