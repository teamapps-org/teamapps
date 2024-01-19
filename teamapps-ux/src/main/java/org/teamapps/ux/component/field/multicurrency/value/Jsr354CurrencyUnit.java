/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import javax.money.CurrencyUnit;

public class Jsr354CurrencyUnit implements org.teamapps.ux.component.field.multicurrency.value.CurrencyUnit {

	private final CurrencyUnit currencyUnit;

	public Jsr354CurrencyUnit(CurrencyUnit currencyUnit) {
		this.currencyUnit = currencyUnit;
	}

	@Override
	public String getCode() {
		return currencyUnit.getCurrencyCode();
	}

	@Override
	public int getFractionDigits() {
		return currencyUnit.getDefaultFractionDigits();
	}

	@Override
	public String toString() {
		return currencyUnit.getCurrencyCode();
	}
}
