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
package org.teamapps.ux.component.field.multicurrency.value;

import org.teamapps.dto.UiCurrencyValue;

import java.math.BigDecimal;
import java.util.Locale;

public class CurrencyValue {

	/**
	 * May be null!
	 */
	private final CurrencyUnit currencyUnit;

	/**
	 * May be null!
	 */
	private final BigDecimal amount;

	public CurrencyValue(CurrencyUnit currencyUnit, BigDecimal amount) {
		this.currencyUnit = currencyUnit;
		this.amount = amount;
	}

	public CurrencyUnit getCurrencyUnit() {
		return currencyUnit;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public UiCurrencyValue toUiCurrencyValue(Locale locale) {
		return new UiCurrencyValue(currencyUnit != null ? currencyUnit.toUiCurrencyUnit(locale) : null, amount != null ? amount.toString() : null);
	}
}
