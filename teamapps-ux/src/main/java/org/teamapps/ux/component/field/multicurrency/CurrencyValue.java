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
package org.teamapps.ux.component.field.multicurrency;

import org.teamapps.dto.UiCurrencyValue;

public class CurrencyValue {


	protected long value;
	protected String currencyCode;

	public CurrencyValue() {
		// default constructor for Jackson
	}

	public CurrencyValue(long value, String currencyCode) {
		this.value = value;
		this.currencyCode = currencyCode;
	}

	@SuppressWarnings("unchecked")
	public String toString() {
		return new StringBuilder(getClass().getSimpleName()).append(": ")
				.append("value=" + value).append(", ")
				.append("currencyCode=" + currencyCode)
				.toString();
	}

	public long getValue() {
		return value;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}


	public UiCurrencyValue toUiCurrencyValue() {
		return new UiCurrencyValue(value, currencyCode);
	}
}
