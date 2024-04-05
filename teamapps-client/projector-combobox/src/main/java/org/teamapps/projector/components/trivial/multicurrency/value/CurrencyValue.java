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
package org.teamapps.projector.components.trivial.multicurrency.value;

import org.teamapps.projector.components.trivial.dto.DtoCurrencyValue;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class CurrencyValue {

	/**
	 * May be null!
	 */
	private final CurrencyUnit currency;

	/**
	 * May be null!
	 */
	private final BigDecimal amount;

	public CurrencyValue(CurrencyUnit currency, BigDecimal amount) {
		this.currency = currency;
		this.amount = amount;
	}

	public Optional<CurrencyUnit> getCurrency() {
		return Optional.ofNullable(currency);
	}

	public Optional<BigDecimal> getAmount() {
		return Optional.ofNullable(amount);
	}

	public Optional<Long> getAmountAsLong(int pointRightShift) {
		return getAmount()
				.map(amount -> amount.movePointRight(pointRightShift).longValue());
	}

	public CurrencyValue withAmount(BigDecimal amount) {
		return new CurrencyValue(currency, amount);
	}

	public CurrencyValue withCurrencyUnit(CurrencyUnit currencyUnit) {
		return new CurrencyValue(currencyUnit, amount);
	}

	public DtoCurrencyValue toUiCurrencyValue(Locale locale) {
		return new DtoCurrencyValue(currency != null ? currency.toUiCurrencyUnit(locale) : null, amount != null ? amount.toString() : null);
	}

	@Override
	public String toString() {
		return "CurrencyValue{" +
				"currency=" + currency +
				", amount=" + amount +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CurrencyValue that = (CurrencyValue) o;
		return Objects.equals(currency, that.currency) && Objects.equals(amount, that.amount);
	}

	@Override
	public int hashCode() {
		return Objects.hash(currency, amount);
	}
}
