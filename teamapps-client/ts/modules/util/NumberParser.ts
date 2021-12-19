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
export class NumberParser {

	private readonly digitsByNumeral: Map<string, string>;
	private readonly groupSeparatorRegex: RegExp;
	private readonly decimalSeparatorRegex: RegExp;
	private readonly numeralRegex: RegExp;
	private readonly unknownRegex: RegExp;

	constructor(locale: string) {
		const numerals: string[] = NumberParser.getNumeralsForLocale(locale);
		this.digitsByNumeral = new Map(numerals.map((d, i) => [d, "" + i]));
		const decimalSeparator = NumberParser.getDecimalSeparatorForLocale(locale);
		const groupSeparator = NumberParser.getGroupSeparatorForLocale(locale);
		this.decimalSeparatorRegex = new RegExp(`[${decimalSeparator}]`);
		this.groupSeparatorRegex = new RegExp(`[${groupSeparator}]`, "g");
		this.numeralRegex = new RegExp(`[${numerals.join("")}]`, "g");
		this.unknownRegex = new RegExp(`[^${numerals.join("")}${groupSeparator}${decimalSeparator}0123456789]`, "g");
	}

	parse(string: string, allowUnknownCharacters = false) {
		let numberString = string.trim()
			.replace(this.groupSeparatorRegex, "")
			.replace(this.decimalSeparatorRegex, ".")
			.replace(this.numeralRegex, numeral => this.digitsByNumeral.get(numeral));
		if (allowUnknownCharacters) {
			numberString = numberString.replace(this.unknownRegex, "");
		}
		return numberString ? +numberString : NaN;
	}

	public static getNumeralsForLocale(locale: string) {
		let numerals: string = new Intl.NumberFormat(locale, {useGrouping: false}).format(9876543210);
		return numerals.split('').reverse();
	}

	public static getGroupSeparatorForLocale(locale: string) {
		const parts = new Intl.NumberFormat(locale, {useGrouping: true}).formatToParts(12345.6);
		return parts.find(d => d.type === "group").value;
	}

	public static getDecimalSeparatorForLocale(locale: string) {
		const parts = new Intl.NumberFormat(locale).formatToParts(12345.6);
		return parts.find(d => d.type === "decimal").value;
	}

	public static getGroupSeparatorForFormat(format: Intl.NumberFormat) {
		const parts: any[] = (format as any).formatToParts(12345.6); // TODO add types once typescript has this
		return parts.find(d => d.type === "group").value;
	}

	public static getDecimalSeparatorForFormat(format: Intl.NumberFormat) {
		const parts: any[] = (format as any).formatToParts(5.6); // TODO add types once typescript has this
		return parts.find(d => d.type === "decimal")?.value ?? '.';
	}
}
