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

import {NumberParser} from "./NumberParser";

export class BigDecimal {
	private constructor(public readonly value: string) {
	}

	public static of(value: string): BigDecimal {
		if (value == null || value.length == null) {
			return null;
		}
		return new BigDecimal(value);
	}

	public format(numberFormat: Intl.NumberFormat, skipThousandSeparators: boolean = false) {
		if ((this.value == null || this.value.length === 0)) {
			return "";
		}

		let [mainString, decimalString] = this.value.split(".");
		decimalString = decimalString ?? "";

		let decimalSeparator = NumberParser.getDecimalSeparatorForFormat(numberFormat);
		let resolvedNumberFormatOptions: ResolvedNumberFormatOptions = numberFormat.resolvedOptions();
		let numerals = NumberParser.getNumeralsForLocale(resolvedNumberFormatOptions.locale);
		let roundUpMain = false;
		if (resolvedNumberFormatOptions.minimumFractionDigits != null) {
			if (decimalString.length < resolvedNumberFormatOptions.minimumFractionDigits) {
				decimalString = decimalString + "0".repeat(resolvedNumberFormatOptions.minimumFractionDigits - decimalString.length)
			} else if (decimalString.length > resolvedNumberFormatOptions.maximumFractionDigits) {
				const truncatedPart = decimalString.substring(resolvedNumberFormatOptions.maximumFractionDigits);
				decimalString = decimalString.substr(0, resolvedNumberFormatOptions.maximumFractionDigits);
				if (truncatedPart.charCodeAt(0) >= "5".charCodeAt(0)) {
					let incremented = BigInt(decimalString) + BigInt(1);
					if (incremented.toString().length > decimalString.length) {
						decimalString = "0".repeat(resolvedNumberFormatOptions.minimumIntegerDigits);
						roundUpMain = true;
					} else {
						decimalString = incremented.toString(10);
					}
				}
			}
		} else {
			throw "Could not format amount!";
		}
		decimalString.split('').map(digit => numerals[Number(digit)]).join("")

		let mainBigInt = mainString.length > 0 ? BigInt(mainString) : BigInt(0);
		if (roundUpMain) {
			mainBigInt += BigInt(1);
		}
		mainString = numberFormat.format(mainBigInt).split(decimalSeparator)[0];

		if (skipThousandSeparators) {
			let thousandSeparator = NumberParser.getGroupSeparatorForLocale(numberFormat.resolvedOptions().locale);
			mainString.replace(thousandSeparator, "");
		}

		return `${mainString}${decimalString.length > 0 ? decimalSeparator + decimalString : ""}`;
	}
}

export interface ResolvedNumberFormatOptions {
	locale: string;
	numberingSystem: string;
	style: string;
	currency?: string;
	currencyDisplay?: string;
	minimumIntegerDigits?: number;
	minimumFractionDigits?: number;
	maximumFractionDigits?: number;
	minimumSignificantDigits?: number;
	maximumSignificantDigits?: number;
	useGrouping: boolean;
}
