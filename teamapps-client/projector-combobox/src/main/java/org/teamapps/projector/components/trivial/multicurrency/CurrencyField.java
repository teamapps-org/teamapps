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
package org.teamapps.projector.components.trivial.multicurrency;

import com.ibm.icu.util.ULocale;
import org.teamapps.projector.dto.DtoAbstractField;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.components.trivial.TrivialComponentsLibrary;
import org.teamapps.projector.components.trivial.dto.DtoCurrencyField;
import org.teamapps.projector.components.trivial.dto.DtoCurrencyUnit;
import org.teamapps.projector.components.trivial.dto.DtoCurrencyValue;
import org.teamapps.projector.components.trivial.multicurrency.value.CurrencyUnit;
import org.teamapps.projector.components.trivial.multicurrency.value.CurrencyValue;
import org.teamapps.projector.clientobject.ProjectorComponent;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;
import org.teamapps.projector.session.SessionContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ProjectorComponent(library = TrivialComponentsLibrary.class)
public class CurrencyField extends AbstractField<CurrencyValue> implements TextInputHandlingField {

	public final ProjectorEvent<String> onTextInput = createProjectorEventBoundToUiEvent(DtoCurrencyField.TextInputEvent.TYPE_ID);
	public final ProjectorEvent<SpecialKey> onSpecialKeyPressed = createProjectorEventBoundToUiEvent(DtoCurrencyField.SpecialKeyPressedEvent.TYPE_ID);

	private ULocale locale = SessionContext.current().getULocale();

	private List<CurrencyUnit> currencies;

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

	public CurrencyField(List<CurrencyUnit> currencies) {
		this.currencies = currencies;
	}

	@Override
	public DtoAbstractField createConfig() {
		DtoCurrencyField field = new DtoCurrencyField();
		mapAbstractFieldAttributesToUiField(field);
		field.setCurrencyUnits(currencies.stream()
				.map(unit -> unit.toUiCurrencyUnit(locale.toLocale()))
				.collect(Collectors.toList()));
		field.setFixedPrecision(fixedPrecision);
		field.setShowCurrencyBeforeAmount(currencyBeforeAmount);
		field.setShowCurrencySymbol(currencySymbolsEnabled);
		field.setAlphabeticKeysQueryEnabled(alphabeticKeysQueryEnabled);
		field.setLocale(locale.toLanguageTag());
		return field;
	}

	@Override
	public void handleUiEvent(String name, JsonWrapper params) {
		super.handleUiEvent(name, params);
		defaultHandleTextInputEvent(event);
	}

	public List<CurrencyUnit> getCurrencies() {
		return currencies;
	}

	public CurrencyField setCurrencies(List<CurrencyUnit> currencies) {
		this.currencies = currencies;
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> new DtoCurrencyField.SetCurrencyUnitsCommand(currencies != null ? currencies.stream()
				.map(unit -> unit.toUiCurrencyUnit(locale.toLocale()))
				.collect(Collectors.toList()) : null)).get(), null);
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
		} else if (value instanceof DtoCurrencyValue) {
			DtoCurrencyValue uiCurrencyValue = (DtoCurrencyValue) value;
			String uiAmount = uiCurrencyValue.getAmount();
			BigDecimal uxAmount = uiAmount != null ? new BigDecimal(uiAmount) : null;

			DtoCurrencyUnit uiCurrencyUnit = uiCurrencyValue.getCurrencyUnit();
			CurrencyUnit uxCurrencyUnit;
			if (uiCurrencyUnit != null) {
				uxCurrencyUnit = currencies.stream()
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

	public Optional<CurrencyUnit> getCurrency() {
		CurrencyValue value = getValue();
		return value != null ? value.getCurrency() : Optional.empty();
	}

	public void setCurrency(CurrencyUnit currencyUnit) {
		CurrencyValue value = getValue();
		setValue(value != null ? value.withCurrencyUnit(currencyUnit) : new CurrencyValue(currencyUnit, null));
	}

	public Optional<BigDecimal> getAmount() {
		CurrencyValue value = getValue();
		return value != null ? value.getAmount() : Optional.empty();
	}

	public void setAmount(BigDecimal amount) {
		CurrencyValue value = getValue();
		setValue(value != null ? value.withAmount(amount) : new CurrencyValue(null, amount));
	}

	public int getFixedPrecision() {
		return fixedPrecision;
	}

	public boolean isCurrencyBeforeAmount() {
		return currencyBeforeAmount;
	}

	public CurrencyField setCurrencyBeforeAmount(boolean currencyBeforeAmount) {
		this.currencyBeforeAmount = currencyBeforeAmount;
		getClientObjectChannel().sendCommandIfRendered(new DtoCurrencyField.SetShowCurrencyBeforeAmountCommand(currencyBeforeAmount), null);
		return this;
	}

	public boolean isCurrencySymbolsEnabled() {
		return currencySymbolsEnabled;
	}

	public CurrencyField setCurrencySymbolsEnabled(boolean currencySymbolsEnabled) {
		this.currencySymbolsEnabled = currencySymbolsEnabled;
		getClientObjectChannel().sendCommandIfRendered(new DtoCurrencyField.SetShowCurrencySymbolCommand(currencySymbolsEnabled), null);
		return this;
	}

	public boolean isAlphabeticKeysQueryEnabled() {
		return alphabeticKeysQueryEnabled;
	}

	public void setFixedPrecision(int fixedPrecision) {
		this.fixedPrecision = fixedPrecision;
		getClientObjectChannel().sendCommandIfRendered(new DtoCurrencyField.SetFixedPrecisionCommand(fixedPrecision), null);
	}

	public void setAlphabeticKeysQueryEnabled(boolean alphabeticKeysQueryEnabled) {
		this.alphabeticKeysQueryEnabled = alphabeticKeysQueryEnabled;
		getClientObjectChannel().sendCommandIfRendered(new DtoCurrencyField.SetAlphabeticKeysQueryEnabledCommand(alphabeticKeysQueryEnabled), null);
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
		setCurrencies(currencies);
		getClientObjectChannel().sendCommandIfRendered(new DtoCurrencyField.SetLocaleCommand(locale.toLanguageTag()), null);
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
