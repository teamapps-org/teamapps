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
