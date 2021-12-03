package org.teamapps.ux.component.field.multicurrency.value;

import java.util.Locale;
import java.util.function.Function;

public class PlainCurrencyUnit implements CurrencyUnit {

	private final String code;
	private final Integer fractionDigits;
	private final Function<Locale, String> nameByLocale;
	private final Function<Locale, String> symbolByLocale;

	public PlainCurrencyUnit(String code, Integer fractionDigits, Function<Locale, String> nameByLocale, Function<Locale, String> symbolByLocale) {
		this.code = code;
		this.fractionDigits = fractionDigits;
		this.nameByLocale = nameByLocale;
		this.symbolByLocale = symbolByLocale;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public int getFractionDigits() {
		return fractionDigits != null ? fractionDigits : CurrencyUnit.super.getFractionDigits();
	}

	@Override
	public String getName(Locale locale) {
		return nameByLocale != null ? nameByLocale.apply(locale) : CurrencyUnit.super.getName(locale);
	}

	@Override
	public String getSymbol(Locale locale) {
		return symbolByLocale != null ? symbolByLocale.apply(locale) : CurrencyUnit.super.getSymbol(locale);
	}

	@Override
	public String toString() {
		return code;
	}
}
