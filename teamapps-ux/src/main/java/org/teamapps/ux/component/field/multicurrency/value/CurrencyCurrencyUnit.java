package org.teamapps.ux.component.field.multicurrency.value;

import java.util.Currency;
import java.util.Locale;

public class CurrencyCurrencyUnit implements CurrencyUnit {

	private final Currency currency;

	public CurrencyCurrencyUnit(Currency currency) {
		this.currency = currency;
	}

	@Override
	public String getCode() {
		return currency.getCurrencyCode();
	}

	@Override
	public int getFractionDigits() {
		return currency.getDefaultFractionDigits();
	}

	@Override
	public String getSymbol(Locale locale) {
		return currency.getSymbol(locale);
	}

	@Override
	public String getName(Locale locale) {
		return currency.getDisplayName(locale);
	}

	@Override
	public String toString() {
		return currency.getCurrencyCode();
	}
}
