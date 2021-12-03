package org.teamapps.ux.component.field.multicurrency.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiCurrencyUnit;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface CurrencyUnit {

	Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	static PlainCurrencyUnit forCode(String code) {
		return new PlainCurrencyUnit(code, null, null, null);
	}

	static PlainCurrencyUnit from(String code, int fractionDigits, String name, String symbol) {
		return new PlainCurrencyUnit(code, fractionDigits, locale -> name, locale -> symbol);
	}

	static PlainCurrencyUnit from(String code, Integer fractionDigits, Function<Locale, String> nameByLocale, Function<Locale, String> symbolByLocale) {
		return new PlainCurrencyUnit(code, fractionDigits, nameByLocale, symbolByLocale);
	}

	static CurrencyUnit fromCurrency(Currency currency) {
		return new CurrencyCurrencyUnit(currency);
	}

	static CurrencyUnit fromJsrCurrencyUnit(javax.money.CurrencyUnit currencyUnit) {
		return new Jsr354CurrencyUnit(currencyUnit);
	}

	static List<CurrencyUnit> getAllAvailableFromJdk() {
		return Currency.getAvailableCurrencies().stream()
				.sorted(Comparator.comparing(currency -> currency.getCurrencyCode()))
				.map(c -> fromCurrency(c))
				.collect(Collectors.toList());
	}

	// ==================================

	String getCode();

	default int getFractionDigits() {
		return findCurrency()
				.map(Currency::getDefaultFractionDigits)
				.orElse(2);
	}

	default String getSymbol() {
		return getSymbol(Locale.getDefault(Locale.Category.DISPLAY));
	}

	default String getSymbol(Locale locale) {
		return getCurrencySymbol(locale);
	}

	default String getName() {
		return getName(Locale.getDefault(Locale.Category.DISPLAY));
	}

	default String getName(Locale locale) {
		return getCurrencyName(locale);
	}

	default UiCurrencyUnit toUiCurrencyUnit(Locale locale) {
		UiCurrencyUnit uiCurrencyUnit = new UiCurrencyUnit();
		uiCurrencyUnit.setCode(getCode());
		uiCurrencyUnit.setFractionDigits(getFractionDigits());
		uiCurrencyUnit.setName(getName(locale));
		uiCurrencyUnit.setSymbol(getSymbol(locale));
		return uiCurrencyUnit;
	}

	// =================================

	private String getCurrencySymbol(Locale locale) {
		return findCurrency()
				.map(c -> c.getSymbol(locale))
				.orElse(getCode());
	}

	private String getCurrencyName(Locale locale) {
		return findCurrency()
				.map(c -> c.getDisplayName(locale))
				.orElse(getCode());
	}

	private Optional<Currency> findCurrency() {
		try {
			return Optional.of(Currency.getInstance(getCode()));
		} catch (Exception e) {
			LOGGER.warn("Cannot get Currency instance for code {}", getCode());
			return Optional.empty();
		}
	}

}
