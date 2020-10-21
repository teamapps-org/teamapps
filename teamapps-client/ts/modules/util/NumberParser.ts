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
		return new Intl.NumberFormat(locale, {useGrouping: false}).format(9876543210).split('').reverse();
	}

	public static getGroupSeparatorForLocale(locale: string) {
		const parts = new Intl.NumberFormat(locale).formatToParts(12345.6);
		return parts.find(d => d.type === "group").value;
	}

	public static getDecimalSeparatorForLocale(locale: string) {
		const parts = new Intl.NumberFormat(locale).formatToParts(12345.6);
		return parts.find(d => d.type === "decimal").value;
	}
}