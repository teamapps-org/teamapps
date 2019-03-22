/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.teamapps.dto.UiCurrencyField;
import org.teamapps.dto.UiCurrencyValue;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiField;
import org.teamapps.event.Event;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.SpecialKey;
import org.teamapps.ux.component.field.TextInputHandlingField;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurrencyField extends AbstractField<CurrencyValue> implements TextInputHandlingField {

	public final Event<String> onTextInput = new Event<>();
	public final Event<SpecialKey> onSpecialKeyPressed = new Event<>();

	private int precision = 2;
	private boolean alphaKeysQueryForCurrency = true;
	private Currency defaultCurrency; // the selected or default currency code (ISO 4217 - e.g. "USD", "EUR"). Nullable. However, the user has to select a currency.
	private List<Currency> currencyList; //list of ISO 4217 currencies - e.g. "USD", "EUR", etc. to be displayed as dropdown selection if available
	private boolean showCurrencyBeforeAmount; // If true, the currency indicator is on the left of the amount. If false, it is on the right of the amount.
	private boolean showCurrencySymbol = true; // If false, show "EUR", "USD", etc. as selected value of dropdown button. If true, show "$" for "USD", "€" for "EUR". The dopdown list shows always
	// both ISO code and currency symbol (e.g. "$ (USD)" or "€ (EUR)"). Use "¤" as generic symbol (as defined in ISO standard).

	public CurrencyField() {
		super();
	}

	@Override
	public UiField createUiComponent() {
		UiCurrencyField field = new UiCurrencyField(getId());
		mapAbstractFieldAttributesToUiField(field);
		field.setDefaultCurrencyCode(defaultCurrency != null ? defaultCurrency.isoCode : null);
		field.setCurrencyCodeList(currencyList != null ? currencyList.stream()
				.map(Currency::getIsoCode)
				.collect(Collectors.toList()) : null);
		field.setPrecision(precision);
		field.setShowCurrencyBeforeAmount(showCurrencyBeforeAmount);
		field.setShowCurrencySymbol(showCurrencySymbol);
		field.setAlphaKeysQueryForCurrency(alphaKeysQueryForCurrency);
		return field;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		super.handleUiEvent(event);
		defaultHandleTextInputEvent(event);
	}

	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	public CurrencyField setDefaultCurrency(Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
		queueCommandIfRendered(() -> new UiCurrencyField.SetDefaultCurrencyCodeCommand(getId(), defaultCurrency != null ? defaultCurrency.isoCode : null));
		return this;
	}

	public List<Currency> getCurrencyList() {
		return currencyList;
	}

	public CurrencyField setCurrencyList(List<Currency> currencyList) {
		this.currencyList = currencyList;
		queueCommandIfRendered(() -> new UiCurrencyField.SetCurrencyCodeListCommand(getId(), currencyList != null ? currencyList.stream()
				.map(Currency::getIsoCode)
				.collect(Collectors.toList()) : null));
		return this;
	}

	@Override
	public Object convertUxValueToUiValue(CurrencyValue currencyValue) {
		return currencyValue != null ? currencyValue.toUiCurrencyValue() : null;
	}

	@Override
	public CurrencyValue convertUiValueToUxValue(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof UiCurrencyValue) {
			UiCurrencyValue uiCurrencyValue = (UiCurrencyValue) value;
			return new CurrencyValue(uiCurrencyValue.getValue(), uiCurrencyValue.getCurrencyCode());
		} else if (value instanceof Map) {
			Map map = (Map) value;
			return new CurrencyValue(((Number) map.get("value")).longValue(), ((String) map.get("currencyCode")));
		} else {
			throw new IllegalArgumentException("Unknown value type for CurrencyField: " + value.getClass().getCanonicalName());
		}
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}

	public int getPrecision() {
		return precision;
	}

	public boolean isShowCurrencyBeforeAmount() {
		return showCurrencyBeforeAmount;
	}

	public CurrencyField setShowCurrencyBeforeAmount(boolean showCurrencyBeforeAmount) {
		this.showCurrencyBeforeAmount = showCurrencyBeforeAmount;
		queueCommandIfRendered(() -> new UiCurrencyField.SetShowCurrencyBeforeAmountCommand(getId(), showCurrencyBeforeAmount));
		return this;
	}

	public boolean isShowCurrencySymbol() {
		return showCurrencySymbol;
	}

	public CurrencyField setShowCurrencySymbol(boolean showCurrencySymbol) {
		this.showCurrencySymbol = showCurrencySymbol;
		queueCommandIfRendered(() -> new UiCurrencyField.SetShowCurrencySymbolCommand(getId(), showCurrencySymbol));
		return this;
	}

	public boolean isAlphaKeysQueryForCurrency() {
		return alphaKeysQueryForCurrency;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
		reRenderIfRendered();
	}

	public void setAlphaKeysQueryForCurrency(boolean alphaKeysQueryForCurrency) {
		this.alphaKeysQueryForCurrency = alphaKeysQueryForCurrency;
		reRenderIfRendered();
	}

	@Override
	public Event<String> onTextInput() {
		return onTextInput;
	}

	@Override
	public Event<SpecialKey> onSpecialKeyPressed() {
		return onSpecialKeyPressed;
	}

	public enum Currency {
		AED("AED", null, "Dirhams", "United Arab Emirates Dirhams"),
		AFN("AFN", "؋", "Afghanis", "Afghan Afghanis"),
		ALL("ALL", "LEK", "Leke", "Albanian Lek"),
		AMD("AMD", null, "Drams", "Armenian Drams"),
		ANG("ANG", "ƒ", "Guilders", "Netherlands Antillean Guilders"),
		AOA("AOA", null, "Kwanza", "Angolan Kwanza"),
		ARS("ARS", "$", "Pesos", "Argentine Pesos"),
		AUD("AUD", "$", "Dollars", "Australian Dollars"),
		AWG("AWG", "ƒ", "Guilders", "Aruban Guilders"),
		AZN("AZN", "ман", "New Manats", "Azerbaijan New Manats"),
		BAM("BAM", "KM", "Convertible Marka", "Convertible Marka"),
		BBD("BBD", "$", "Dollars", "Barbados Dollars"),
		BDT("BDT", null, "Taka", "Bangladeshi Taka"),
		BGN("BGN", "лв", "Leva", "Bulgarian Leva"),
		BHD("BHD", null, "Dinars", "Bahraini Dinars"),
		BIF("BIF", null, "Francs", "Burundi Francs"),
		BMD("BMD", "$", "Dollars", "Bermudian Dollars"),
		BND("BND", "$", "Dollars", "Brunei Dollars"),
		BOB("BOB", "$b", "Bolivianos", "Bolivianos"),
		BRL("BRL", "R$", "Real", "Brazilian Real"),
		BSD("BSD", "$", "Dollars", "Bahamian Dollars"),
		BTN("BTN", null, "Ngultrum", "Bhutan Ngultrum"),
		BWP("BWP", "P", "Pulas", "Botswana Pulas"),
		BYR("BYR", "p.", "Rubles", "Belarussian Rubles"),
		BZD("BZD", "BZ$", "Dollars", "Belize Dollars"),
		CAD("CAD", "$", "Dollars", "Canadian Dollars"),
		CDF("CDF", null, "Franc", "Franc"),
		CHF("CHF", null, "Switzerland Francs", "Switzerland Francs"),
		CLP("CLP", "$", "Pesos", "Chilean Pesos"),
		CNY("CNY", "¥", "Yuan Renminbi", "Yuan Renminbi"),
		COP("COP", "$", "Pesos", "Colombian Pesos"),
		CRC("CRC", "₡", "Colones", "Costa Rican Colones"),
		CUP("CUP", "₱", "Pesos", "Cuban Pesos"),
		CVE("CVE", null, "Escudos", "Cape Verde Escudos"),
		CZK("CZK", "Kč", "Koruny", "Czech Koruny"),
		DJF("DJF", null, "Francs", "Djibouti Francs"),
		DKK("DKK", "kr", "Kroner", "Danish Kroner"),
		DOP("DOP", "RD$", "Pesos", "Dominican Pesos"),
		DZD("DZD", null, "Dinars", "Algerian Dinar"),
		ECS("ECS", null, "Sucre", "Ecuador Sucre"),
		EEK("EEK", "kr", "Krooni", "Krooni"),
		EGP("EGP", "£", "Pounds", "Egyptian Pounds"),
		ETB("ETB", null, "Ethopia Birr", "Ethopia Birr"),
		EUR("EUR", "€", "Euro", "Euro"),
		FJD("FJD", null, "Dollar", "Fiji Dollar"),
		FKP("FKP", "£", "Pounds", "Falkland Islands Pounds"),
		GBP("GBP", "£", "Pounds", "Pound Sterling"),
		GEL("GEL", null, "Lari", "Lari"),
		GGP("GGP", "£", "Pounds", "Pound Sterling"),
		GHS("GHS", "¢", "Cedis", "Ghanaian Cedis"),
		GIP("GIP", "£", "Pounds", "Gibraltar Pounds"),
		GMD("GMD", null, "Lari", "Gambian Dalasi"),
		GNF("GNF", null, "Francs", "Guinea Francs"),
		GTQ("GTQ", "Q", "Quetzales", "Quetzales"),
		GYD("GYD", null, "Dollars", "Guyana Dollars"),
		HKD("HKD", "$", "Dollars", "Hong Kong Dollars"),
		HNL("HNL", "L", "Lempiras", "Honduaran Lempiras"),
		HRK("HRK", "kn", "Kuna", "Croatian Kuna"),
		HTG("HTG", null, "Haitian Gourde", "Haitian Gourde"),
		HUF("HUF", "Ft", "Forint", "Hungarian Forint"),
		IDR("IDR", "Rp", "Indonesian Rupiahs", "Indonesian Rupiahs"),
		ILS("ILS", "₪", "New Shekels", "New Shekels"),
		IMP("IMP", "£", "Pounds", "Pounds"),
		INR("INR", "₨", "Rupees", "Indian Rupees"),
		IQD("IQD", null, "Dinars", "Iraqi Dinars"),
		IRR("IRR", "﷼", "Riais", "Iranian Riais"),
		ISK("ISK", "kr", "Kronur", "Iceland Kronur"),
		JEP("JEP", "£", "Pounds", "Pounds"),
		JMD("JMD", null, "Dollars", "Jamaican Dollars"),
		JOD("JOD", null, "Dinars", "Jordanian Dinar"),
		JPY("JPY", "¥", "Yen", "Japanese Yen"),
		KES("KES", null, "Shillings", "Kenyan Shillings"),
		KGS("KGS", "лв", "Soms", "Soms"),
		KHR("KHR", null, "Rieis", "Kampuchean Rieis"),
		KMF("KMF", null, "Francs", "Comoros Francs"),
		KPW("KPW", "₩", "Won", "North Korean Won"),
		KRW("KRW", "₩", "Won", "Korean Won"),
		KWD("KWD", null, "Dinars", "Kuwaiti Dinars"),
		KYD("KYD", "$", "Dollars", "Cayman Islands Dollars"),
		KZT("KZT", "лв", "Tenege", "Kazhakstan Tenege"),
		LAK("LAK", "₭", "Kips", "Lao Kips"),
		LBP("LBP", "£", "Pounds", "Lebanese Pounds"),
		LKR("LKR", "₨", "Rupees", "Sri Lanka Rupees"),
		LRD("LRD", "$", "Dollars", "Liberian Dollars"),
		LSL("LSL", null, "Maloti", "Lesotho Maloti"),
		LTL("LTL", "Lt", "Litai", "Lithuanian Litai"),
		LVL("LVL", "Ls", "Lati", "Latvian Lati"),
		LYD("LYD", null, "Dinars", "Libyan Dinars"),
		MAD("MAD", null, "Dirhams", "Moroccan Dirhams"),
		MDL("MDL", null, "Lei", "Moldovan Lei"),
		MKD("MKD", null, "Macedonian Denar", "Macedonian Denar"),
		MGA("MGA", null, "Ariary", "Ariary"),
		MMK("MMK", null, "Kyat", "Myanmar Kyat"),
		MNK("MNK", null, "Kyats", "Kyats"),
		MNT("MNT", "₮", "Tugriks", "Mongolian Tugriks"),
		MRO("MRO", null, "Ouguiyas", "Mauritanian Ouguiyas"),
		MUR("MUR", "₨", "Rupees", "Mauritius Rupees"),
		MVR("MVR", null, "Rufiyaa", "Maldive Rufiyaa"),
		MWK("MWK", null, "Kwachas", "Malawi Kwachas"),
		MXN("MXN", "$", "Pesos", "Mexican Nuevo Pesos"),
		MYR("MYR", "RM", "Ringgits", "Malaysian Ringgits"),
		MZN("MZN", "MT", "Meticals", "Mozambique Meticals"),
		NAD("NAD", "$", "Dollars", "Namibian Dollars"),
		NGN("NGN", "₦", "Nairas", "Nigerian Nairas"),
		NIO("NIO", "C$", "Cordobas", "Nicaraguan Cordobas Oro"),
		NOK("NOK", "kr", "Kroner", "Norwegian Kroner"),
		NPR("NPR", "₨", "Rupees", "Nepalese Rupees"),
		NZD("NZD", "$", "New Zealand Dollars", "New Zealand Dollars"),
		OMR("OMR", "﷼", "Riais", "Riais"),
		PAB("PAB", "B/.", "Balboa", "Balboa"),
		PEN("PEN", "S/.", "Nuevos Soles", "Peru Nuevos Soles"),
		PGK("PGK", null, "Kina", "Kina"),
		PHP("PHP", "Php", "Pesos", "Phillippines Pesos"),
		PKR("PKR", "₨", "Rupees", "Pakistani Rupees"),
		PLN("PLN", "zł", "Zlotych", "Poland Zlotych"),
		PYG("PYG", "Gs", "Guarani", "Paraguay Guarani"),
		QAR("QAR", "﷼", "Rials", "Qatar Rials"),
		RON("RON", "lei", "New Lei", "Romanian New Lei"),
		RSD("RSD", "Дин.", "Dinars", "Serbia Dinars"),
		RUB("RUB", "руб", "Rubles", "Russia Rubles"),
		RWF("RWF", null, "Francs", "Francs"),
		SAR("SAR", "﷼", "Riyals", "Saudi Arabia Riyals"),
		SBD("SBD", "$", "Dollars", "Solomon Islands Dollars"),
		SCR("SCR", "₨", "Rupees", "Seychelles Rupees"),
		SDG("SDG", null, "Pounds", "Pounds"),
		SEK("SEK", "kr", "Kronor", "Sweden Kronor"),
		SGD("SGD", "$", "Dollars", "Singapore Dollars"),
		SHP("SHP", "£", "Pounds", "Saint Helena Pounds"),
		SLL("SLL", null, "Leones", "Leones"),
		SOS("SOS", "S", "Shillings", "Somalia Shillings"),
		SRD("SRD", "$", "Dollars", "SurifullName Dollars"),
		STD("STD", null, "Dobras", "Dobras"),
		SVC("SVC", null, "Salvadoran Colón", "Salvadoran Colón"),
		SYP("SYP", "£", "Pounds", "Syria Pounds"),
		SZL("SZL", null, "Emalangeni", "Emalangeni"),
		THB("THB", "฿", "Baht", "Thaliand Baht"),
		TJS("TJS", null, "Somoni", "Somoni"),
		TMM("TMM", null, "Manat", "Manat"),
		TND("TND", null, "Dinars", "Tunisian Dinars"),
		TOP("TOP", null, "Pa'anga", "Pa'anga"),
		TRY("TRY", "TL", "Lira", "Turkey Lira"),
		TTD("TTD", "$", "Dollars", "Trinidad and Tobago Dollars"),
		TVD("TVD", null, "Tuvalu Dollars", "Tuvalu Dollars"),
		TWD("TWD", "NT$", "New Dollars", "Taiwan New Dollars"),
		TZS("TZS", null, "Shillings", "Shillings"),
		UAH("UAH", "₴", "Hryvnia", "Ukraine Hryvnia"),
		UGX("UGX", null, "Shillings", "Shillings"),
		USD("USD", "$", "Dollars", "United States Dollars"),
		UYU("UYU", "$U", "Pesos", "Uruguay Pesos"),
		UZS("UZS", "лв", "Sums", "Uzbekistan Sums"),
		VEF("VEF", "Bs", "Bolivares Fuertes", "Venezuela Bolivares Fuertes"),
		VND("VND", "₫", "Dong", "Viet Nam Dong"),
		XAF("XAF", null, "Communauté Financière Africaine Francs", "Communauté Financière Africaine Francs"),
		XCD("XCD", "$", "East Caribbean Dollars", "East Caribbean Dollars"),
		XOF("XOF", null, "Communauté Financière Africaine Francs", "Communauté Financière Africaine Francs"),
		XPF("XPF", null, "Comptoirs Français du Pacifique Francs", "Comptoirs Français du Pacifique Francs"),
		YER("YER", "﷼", "Rials", "Yemen Rials"),
		ZAR("ZAR", "R", "Rand", "South Africa Rand"),
		ZMK("ZMK", null, "Kwacha", "Kwacha"),
		ZWD("ZWD", "Z$", "Zimbabwe Dollars", "Zimbabwe Dollars");


		private final String isoCode;
		private final String symbol;
		private final String name;
		private final String fullName;

		Currency(String isoCode, String symbol, String name, String fullName) {
			this.isoCode = isoCode;
			this.symbol = symbol;
			this.name = name;
			this.fullName = fullName;
		}

		public String getIsoCode() {
			return isoCode;
		}

		public String getSymbol() {
			return symbol;
		}

		public String getName() {
			return name;
		}

		public String getFullName() {
			return fullName;
		}
	}

}
