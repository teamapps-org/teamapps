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
export type Currency = {
	code: string,
	name: string,
	alt_name?: string,
	fullName: string,
	symbol?: string
};

export var CURRENCIES: { [code: string]: Currency } = {
	"AED": {
		"code": "AED",
		"name": "Dirhams",
		"fullName": "United Arab Emirates Dirhams"
	},
	"AFN": {
		"code": "AFN",
		"name": "Afghanis",
		"fullName": "Afghan Afghanis",
		"symbol": "؋"
	},
	"ALL": {
		"code": "ALL",
		"name": "Leke",
		"fullName": "Albanian Lek",
		"symbol": "LEK"
	},
	"AMD": {
		"code": "AMD",
		"name": "Drams",
		"fullName": "Armenian Drams"
	},
	"ANG": {
		"code": "ANG",
		"name": "Guilders",
		"fullName": "Netherlands Antillean Guilders",
		"symbol": "ƒ"
	},
	"AOA": {
		"code": "AOA",
		"name": "Kwanza",
		"fullName": "Angolan Kwanza"
	},
	"ARS": {
		"code": "ARS",
		"name": "Pesos",
		"fullName": "Argentine Pesos",
		"symbol": "$"
	},
	"AUD": {
		"code": "AUD",
		"name": "Dollars",
		"fullName": "Australian Dollars",
		"symbol": "$"
	},
	"AWG": {
		"alt_name": "Florins",
		"code": "AWG",
		"name": "Guilders",
		"fullName": "Aruban Guilders",
		"symbol": "ƒ"
	},
	"AZN": {
		"code": "AZN",
		"name": "New Manats",
		"fullName": "Azerbaijan New Manats",
		"symbol": "ман"
	},
	"BAM": {
		"code": "BAM",
		"name": "Convertible Marka",
		"fullName": "Convertible Marka",
		"symbol": "KM"
	},
	"BBD": {
		"code": "BBD",
		"name": "Dollars",
		"fullName": "Barbados Dollars",
		"symbol": "$"
	},
	"BDT": {
		"code": "BDT",
		"name": "Taka",
		"fullName": "Bangladeshi Taka"
	},
	"BGN": {
		"code": "BGN",
		"name": "Leva",
		"fullName": "Bulgarian Leva",
		"symbol": "лв"
	},
	"BHD": {
		"code": "BHD",
		"name": "Dinars",
		"fullName": "Bahraini Dinars"
	},
	"BIF": {
		"code": "BIF",
		"name": "Francs",
		"fullName": "Burundi Francs"
	},
	"BMD": {
		"code": "BMD",
		"name": "Dollars",
		"fullName": "Bermudian Dollars",
		"symbol": "$"
	},
	"BND": {
		"code": "BND",
		"name": "Dollars",
		"fullName": "Brunei Dollars",
		"symbol": "$"
	},
	"BOB": {
		"code": "BOB",
		"name": "Bolivianos",
		"fullName": "Bolivianos",
		"symbol": "$b"
	},
	"BRL": {
		"code": "BRL",
		"name": "Real",
		"fullName": "Brazilian Real",
		"symbol": "R$"
	},
	"BSD": {
		"code": "BSD",
		"name": "Dollars",
		"fullName": "Bahamian Dollars",
		"symbol": "$"
	},
	"BTN": {
		"code": "BTN",
		"name": "Ngultrum",
		"fullName": "Bhutan Ngultrum"
	},
	"BWP": {
		"code": "BWP",
		"name": "Pulas",
		"fullName": "Botswana Pulas",
		"symbol": "P"
	},
	"BYR": {
		"code": "BYR",
		"name": "Rubles",
		"fullName": "Belarussian Rubles",
		"symbol": "p."
	},
	"BZD": {
		"code": "BZD",
		"name": "Dollars",
		"fullName": "Belize Dollars",
		"symbol": "BZ$"
	},
	"CAD": {
		"code": "CAD",
		"name": "Dollars",
		"fullName": "Canadian Dollars",
		"symbol": "$"
	},
	"CDF": {
		"code": "CDF",
		"name": "Franc",
		"fullName": "Franc"
	},
	"CHF": {
		"code": "CHF",
		"name": "Switzerland Francs",
		"fullName": "Switzerland Francs"
	},
	"CLP": {
		"code": "CLP",
		"name": "Pesos",
		"fullName": "Chilean Pesos",
		"symbol": "$"
	},
	"CNY": {
		"code": "CNY",
		"name": "Yuan Renminbi",
		"fullName": "Yuan Renminbi",
		"symbol": "¥"
	},
	"COP": {
		"code": "COP",
		"name": "Pesos",
		"fullName": "Colombian Pesos",
		"symbol": "$"
	},
	"CRC": {
		"code": "CRC",
		"name": "Colones",
		"fullName": "Costa Rican Colones",
		"symbol": "₡"
	},
	"CUP": {
		"code": "CUP",
		"name": "Pesos",
		"fullName": "Cuban Pesos",
		"symbol": "₱"
	},
	"CVE": {
		"code": "CVE",
		"name": "Escudos",
		"fullName": "Cape Verde Escudos"
	},
	"CZK": {
		"code": "CZK",
		"name": "Koruny",
		"fullName": "Czech Koruny",
		"symbol": "Kč"
	},
	"DJF": {
		"code": "DJF",
		"name": "Francs",
		"fullName": "Djibouti Francs"
	},
	"DKK": {
		"code": "DKK",
		"name": "Kroner",
		"fullName": "Danish Kroner",
		"symbol": "kr"
	},
	"DOP": {
		"code": "DOP",
		"name": "Pesos",
		"fullName": "Dominican Pesos",
		"symbol": "RD$"
	},
	"DZD": {
		"code": "DZD",
		"name": "Dinars",
		"fullName": "Algerian Dinar"
	},
	"ECS": {
		"code": "ECS",
		"name": "Sucre",
		"fullName": "Ecuador Sucre"
	},
	"EEK": {
		"code": "EEK",
		"name": "Krooni",
		"fullName": "Krooni",
		"symbol": "kr"
	},
	"EGP": {
		"code": "EGP",
		"name": "Pounds",
		"fullName": "Egyptian Pounds",
		"symbol": "£"
	},
	"ETB": {
		"code": "ETB",
		"name": "Ethopia Birr",
		"fullName": "Ethopia Birr"
	},
	"EUR": {
		"code": "EUR",
		"name": "Euro",
		"fullName": "Euro",
		"symbol": "€"
	},
	"FJD": {
		"code": "FJD",
		"name": "Dollar",
		"fullName": "Fiji Dollar"
	},
	"FKP": {
		"code": "FKP",
		"name": "Pounds",
		"fullName": "Falkland Islands Pounds",
		"symbol": "£"
	},
	"GBP": {
		"code": "GBP",
		"name": "Pounds",
		"fullName": "Pound Sterling",
		"symbol": "£"
	},
	"GEL": {
		"code": "GEL",
		"name": "Lari",
		"fullName": "Lari"
	},
	"GGP": {
		"code": "GGP",
		"name": "Pounds",
		"fullName": "Pound Sterling",
		"symbol": "£"
	},
	"GHS": {
		"code": "GHS",
		"name": "Cedis",
		"fullName": "Ghanaian Cedis",
		"symbol": "¢"
	},
	"GIP": {
		"code": "GIP",
		"name": "Pounds",
		"fullName": "Gibraltar Pounds",
		"symbol": "£"
	},
	"GMD": {
		"code": "GMD",
		"name": "Lari",
		"fullName": "Gambian Dalasi"
	},
	"GNF": {
		"code": "GNF",
		"name": "Francs",
		"fullName": "Guinea Francs"
	},
	"GTQ": {
		"code": "GTQ",
		"name": "Quetzales",
		"fullName": "Quetzales",
		"symbol": "Q"
	},
	"GYD": {
		"code": "GYD",
		"name": "Dollars",
		"fullName": "Guyana Dollars"
	},
	"HKD": {
		"code": "HKD",
		"name": "Dollars",
		"fullName": "Hong Kong Dollars",
		"symbol": "$"
	},
	"HNL": {
		"code": "HNL",
		"name": "Lempiras",
		"fullName": "Honduaran Lempiras",
		"symbol": "L"
	},
	"HRK": {
		"code": "HRK",
		"name": "Kuna",
		"fullName": "Croatian Kuna",
		"symbol": "kn"
	},
	"HTG": {
		"code": "HTG",
		"name": "Haitian Gourde",
		"fullName": "Haitian Gourde"
	},
	"HUF": {
		"code": "HUF",
		"name": "Forint",
		"fullName": "Hungarian Forint",
		"symbol": "Ft"
	},
	"IDR": {
		"code": "IDR",
		"name": "Indonesian Rupiahs",
		"fullName": "Indonesian Rupiahs",
		"symbol": "Rp"
	},
	"ILS": {
		"code": "ILS",
		"name": "New Shekels",
		"fullName": "New Shekels",
		"symbol": "₪"
	},
	"IMP": {
		"code": "IMP",
		"name": "Pounds",
		"fullName": "Pounds",
		"symbol": "£"
	},
	"INR": {
		"code": "INR",
		"name": "Rupees",
		"fullName": "Indian Rupees",
		"symbol": "₨"
	},
	"IQD": {
		"code": "IQD",
		"name": "Dinars",
		"fullName": "Iraqi Dinars"
	},
	"IRR": {
		"code": "IRR",
		"name": "Riais",
		"fullName": "Iranian Riais",
		"symbol": "﷼"
	},
	"ISK": {
		"code": "ISK",
		"name": "Kronur",
		"fullName": "Iceland Kronur",
		"symbol": "kr"
	},
	"JEP": {
		"code": "JEP",
		"name": "Pounds",
		"fullName": "Pounds",
		"symbol": "£"
	},
	"JMD": {
		"code": "JMD",
		"name": "Dollars",
		"fullName": "Jamaican Dollars"
	},
	"JOD": {
		"code": "JOD",
		"name": "Dinars",
		"fullName": "Jordanian Dinar"
	},
	"JPY": {
		"code": "JPY",
		"name": "Yen",
		"fullName": "Japanese Yen",
		"symbol": "¥"
	},
	"KES": {
		"code": "KES",
		"name": "Shillings",
		"fullName": "Kenyan Shillings"
	},
	"KGS": {
		"code": "KGS",
		"name": "Soms",
		"fullName": "Soms",
		"symbol": "лв"
	},
	"KHR": {
		"code": "KHR",
		"name": "Rieis",
		"fullName": "Kampuchean Rieis"
	},
	"KMF": {
		"code": "KMF",
		"name": "Francs",
		"fullName": "Comoros Francs"
	},
	"KPW": {
		"code": "KPW",
		"name": "Won",
		"fullName": "North Korean Won",
		"symbol": "₩"
	},
	"KRW": {
		"code": "KRW",
		"name": "Won",
		"fullName": "Korean Won",
		"symbol": "₩"
	},
	"KWD": {
		"code": "KWD",
		"name": "Dinars",
		"fullName": "Kuwaiti Dinars"
	},
	"KYD": {
		"code": "KYD",
		"name": "Dollars",
		"fullName": "Cayman Islands Dollars",
		"symbol": "$"
	},
	"KZT": {
		"code": "KZT",
		"name": "Tenege",
		"fullName": "Kazhakstan Tenege",
		"symbol": "лв"
	},
	"LAK": {
		"code": "LAK",
		"name": "Kips",
		"fullName": "Lao Kips",
		"symbol": "₭"
	},
	"LBP": {
		"code": "LBP",
		"name": "Pounds",
		"fullName": "Lebanese Pounds",
		"symbol": "£"
	},
	"LKR": {
		"code": "LKR",
		"name": "Rupees",
		"fullName": "Sri Lanka Rupees",
		"symbol": "₨"
	},
	"LRD": {
		"code": "LRD",
		"name": "Dollars",
		"fullName": "Liberian Dollars",
		"symbol": "$"
	},
	"LSL": {
		"code": "LSL",
		"name": "Maloti",
		"fullName": "Lesotho Maloti"
	},
	"LTL": {
		"code": "LTL",
		"name": "Litai",
		"fullName": "Lithuanian Litai",
		"symbol": "Lt"
	},
	"LVL": {
		"code": "LVL",
		"name": "Lati",
		"fullName": "Latvian Lati",
		"symbol": "Ls"
	},
	"LYD": {
		"code": "LYD",
		"name": "Dinars",
		"fullName": "Libyan Dinars"
	},
	"MAD": {
		"code": "MAD",
		"name": "Dirhams",
		"fullName": "Moroccan Dirhams"
	},
	"MDL": {
		"code": "MDL",
		"name": "Lei",
		"fullName": "Moldovan Lei"
	},
	"MKD": {
		"code": "MKD",
		"name": "Macedonian Denar",
		"fullName": "Macedonian Denar"
	},
	"MGA": {
		"code": "MGA",
		"name": "Ariary",
		"fullName": "Ariary"
	},
	"MMK": {
		"code": "MMK",
		"name": "Kyat",
		"fullName": "Myanmar Kyat"
	},
	"MNK": {
		"code": "MNK",
		"name": "Kyats",
		"fullName": "Kyats"
	},
	"MNT": {
		"code": "MNT",
		"name": "Tugriks",
		"fullName": "Mongolian Tugriks",
		"symbol": "₮"
	},
	"MRO": {
		"code": "MRO",
		"name": "Ouguiyas",
		"fullName": "Mauritanian Ouguiyas"
	},
	"MUR": {
		"code": "MUR",
		"name": "Rupees",
		"fullName": "Mauritius Rupees",
		"symbol": "₨"
	},
	"MVR": {
		"code": "MVR",
		"name": "Rufiyaa",
		"fullName": "Maldive Rufiyaa"
	},
	"MWK": {
		"code": "MWK",
		"name": "Kwachas",
		"fullName": "Malawi Kwachas"
	},
	"MXN": {
		"code": "MXN",
		"name": "Pesos",
		"fullName": "Mexican Nuevo Pesos",
		"symbol": "$"
	},
	"MYR": {
		"code": "MYR",
		"name": "Ringgits",
		"fullName": "Malaysian Ringgits",
		"symbol": "RM"
	},
	"MZN": {
		"code": "MZN",
		"name": "Meticals",
		"fullName": "Mozambique Meticals",
		"symbol": "MT"
	},
	"NAD": {
		"code": "NAD",
		"name": "Dollars",
		"fullName": "Namibian Dollars",
		"symbol": "$"
	},
	"NGN": {
		"code": "NGN",
		"name": "Nairas",
		"fullName": "Nigerian Nairas",
		"symbol": "₦"
	},
	"NIO": {
		"code": "NIO",
		"name": "Cordobas",
		"fullName": "Nicaraguan Cordobas Oro",
		"symbol": "C$"
	},
	"NOK": {
		"code": "NOK",
		"name": "Kroner",
		"fullName": "Norwegian Kroner",
		"symbol": "kr"
	},
	"NPR": {
		"code": "NPR",
		"name": "Rupees",
		"fullName": "Nepalese Rupees",
		"symbol": "₨"
	},
	"NZD": {
		"code": "NZD",
		"name": "New Zealand Dollars",
		"fullName": "New Zealand Dollars",
		"symbol": "$"
	},
	"OMR": {
		"code": "OMR",
		"name": "Riais",
		"fullName": "Riais",
		"symbol": "﷼"
	},
	"PAB": {
		"code": "PAB",
		"name": "Balboa",
		"fullName": "Balboa",
		"symbol": "B/."
	},
	"PEN": {
		"code": "PEN",
		"name": "Nuevos Soles",
		"fullName": "Peru Nuevos Soles",
		"symbol": "S/."
	},
	"PGK": {
		"code": "PGK",
		"name": "Kina",
		"fullName": "Kina"
	},
	"PHP": {
		"code": "PHP",
		"name": "Pesos",
		"fullName": "Phillippines Pesos",
		"symbol": "Php"
	},
	"PKR": {
		"code": "PKR",
		"name": "Rupees",
		"fullName": "Pakistani Rupees",
		"symbol": "₨"
	},
	"PLN": {
		"code": "PLN",
		"name": "Zlotych",
		"fullName": "Poland Zlotych",
		"symbol": "zł"
	},
	"PYG": {
		"code": "PYG",
		"name": "Guarani",
		"fullName": "Paraguay Guarani",
		"symbol": "Gs"
	},
	"QAR": {
		"code": "QAR",
		"name": "Rials",
		"fullName": "Qatar Rials",
		"symbol": "﷼"
	},
	"RON": {
		"code": "RON",
		"name": "New Lei",
		"fullName": "Romanian New Lei",
		"symbol": "lei"
	},
	"RSD": {
		"code": "RSD",
		"name": "Dinars",
		"fullName": "Serbia Dinars",
		"symbol": "Дин."
	},
	"RUB": {
		"code": "RUB",
		"name": "Rubles",
		"fullName": "Russia Rubles",
		"symbol": "руб"
	},
	"RWF": {
		"code": "RWF",
		"name": "Francs",
		"fullName": "Francs"
	},
	"SAR": {
		"code": "SAR",
		"name": "Riyals",
		"fullName": "Saudi Arabia Riyals",
		"symbol": "﷼"
	},
	"SBD": {
		"code": "SBD",
		"name": "Dollars",
		"fullName": "Solomon Islands Dollars",
		"symbol": "$"
	},
	"SCR": {
		"code": "SCR",
		"name": "Rupees",
		"fullName": "Seychelles Rupees",
		"symbol": "₨"
	},
	"SDG": {
		"code": "SDG",
		"name": "Pounds",
		"fullName": "Pounds"
	},
	"SEK": {
		"code": "SEK",
		"name": "Kronor",
		"fullName": "Sweden Kronor",
		"symbol": "kr"
	},
	"SGD": {
		"code": "SGD",
		"name": "Dollars",
		"fullName": "Singapore Dollars",
		"symbol": "$"
	},
	"SHP": {
		"code": "SHP",
		"name": "Pounds",
		"fullName": "Saint Helena Pounds",
		"symbol": "£"
	},
	"SLL": {
		"code": "SLL",
		"name": "Leones",
		"fullName": "Leones"
	},
	"SOS": {
		"code": "SOS",
		"name": "Shillings",
		"fullName": "Somalia Shillings",
		"symbol": "S"
	},
	"SRD": {
		"code": "SRD",
		"name": "Dollars",
		"fullName": "SurifullName Dollars",
		"symbol": "$"
	},
	"STD": {
		"code": "STD",
		"name": "Dobras",
		"fullName": "Dobras"
	},
	"SVC": {
		"code": "SVC",
		"name": "Salvadoran Colón",
		"fullName": "Salvadoran Colón"
	},
	"SYP": {
		"code": "SYP",
		"name": "Pounds",
		"fullName": "Syria Pounds",
		"symbol": "£"
	},
	"SZL": {
		"code": "SZL",
		"name": "Emalangeni",
		"fullName": "Emalangeni"
	},
	"THB": {
		"code": "THB",
		"name": "Baht",
		"fullName": "Thaliand Baht",
		"symbol": "฿"
	},
	"TJS": {
		"code": "TJS",
		"name": "Somoni",
		"fullName": "Somoni"
	},
	"TMM": {
		"code": "TMM",
		"name": "Manat",
		"fullName": "Manat"
	},
	"TND": {
		"code": "TND",
		"name": "Dinars",
		"fullName": "Tunisian Dinars"
	},
	"TOP": {
		"code": "TOP",
		"name": "Pa'anga",
		"fullName": "Pa'anga"
	},
	"TRY": {
		"code": "TRY",
		"name": "Lira",
		"fullName": "Turkey Lira",
		"symbol": "TL"
	},
	"TTD": {
		"code": "TTD",
		"name": "Dollars",
		"fullName": "Trinidad and Tobago Dollars",
		"symbol": "$"
	},
	"TVD": {
		"code": "TVD",
		"name": "Tuvalu Dollars",
		"fullName": "Tuvalu Dollars"
	},
	"TWD": {
		"code": "TWD",
		"name": "New Dollars",
		"fullName": "Taiwan New Dollars",
		"symbol": "NT$"
	},
	"TZS": {
		"code": "TZS",
		"name": "Shillings",
		"fullName": "Shillings"
	},
	"UAH": {
		"code": "UAH",
		"name": "Hryvnia",
		"fullName": "Ukraine Hryvnia",
		"symbol": "₴"
	},
	"UGX": {
		"code": "UGX",
		"name": "Shillings",
		"fullName": "Shillings"
	},
	"USD": {
		"code": "USD",
		"name": "Dollars",
		"fullName": "United States Dollars",
		"symbol": "$"
	},
	"UYU": {
		"code": "UYU",
		"name": "Pesos",
		"fullName": "Uruguay Pesos",
		"symbol": "$U"
	},
	"UZS": {
		"code": "UZS",
		"name": "Sums",
		"fullName": "Uzbekistan Sums",
		"symbol": "лв"
	},
	"VEF": {
		"code": "VEF",
		"name": "Bolivares Fuertes",
		"fullName": "Venezuela Bolivares Fuertes",
		"symbol": "Bs"
	},
	"VND": {
		"code": "VND",
		"name": "Dong",
		"fullName": "Viet Nam Dong",
		"symbol": "₫"
	},
	"XAF": {
		"code": "XAF",
		"name": "Communauté Financière Africaine Francs",
		"fullName": "Communauté Financière Africaine Francs"
	},
	"XCD": {
		"code": "XCD",
		"name": "East Caribbean Dollars",
		"fullName": "East Caribbean Dollars",
		"symbol": "$"
	},
	"XOF": {
		"code": "XOF",
		"name": "Communauté Financière Africaine Francs",
		"fullName": "Communauté Financière Africaine Francs"
	},
	"XPF": {
		"code": "XPF",
		"name": "Comptoirs Français du Pacifique Francs",
		"fullName": "Comptoirs Français du Pacifique Francs"
	},
	"YER": {
		"code": "YER",
		"name": "Rials",
		"fullName": "Yemen Rials",
		"symbol": "﷼"
	},
	"ZAR": {
		"code": "ZAR",
		"name": "Rand",
		"fullName": "South Africa Rand",
		"symbol": "R"
	},
	"ZMK": {
		"code": "ZMK",
		"name": "Kwacha",
		"fullName": "Kwacha"
	},
	"ZWD": {
		"code": "ZWD",
		"name": "Zimbabwe Dollars",
		"fullName": "Zimbabwe Dollars",
		"symbol": "Z$"
	}
};
