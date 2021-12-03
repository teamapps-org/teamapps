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
import {
	DEFAULT_RENDERING_FUNCTIONS,
	DEFAULT_TEMPLATES,
	defaultListQueryFunctionFactory,
	EditingMode,
	HighlightDirection,
	keyCodes,
	QueryFunction,
	TrivialComponent,
	unProxyEntry
} from "./TrivialCore";
import {Instance as Popper} from '@popperjs/core';
import {TrivialTreeBox, TrivialTreeBoxConfig} from "./TrivialTreeBox";
import {NumberParser} from "../util/NumberParser";
import {DropDownComponent} from "./dropdown/DropDownComponent";
import {parseHtml} from "../Common";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {TreeBoxDropdown} from "./dropdown/TreeBoxDropdown";
import {createComboBoxPopper} from "./ComboBoxPopper";

export interface TrivialUnitBoxConfig<U> extends TrivialTreeBoxConfig<U> {
	defaultNumberFormat: Intl.NumberFormat,
	numberFormatFunction: (entry: U) => Intl.NumberFormat,
	unitDisplayPosition?: 'left' | 'right',
	allowNullAmount?: boolean,
	selectedEntryRenderingFunction: (entry: U) => string,
	amount?: number,
	queryFunction?: QueryFunction<U>,
	queryOnNonNumberCharacters?: boolean,
	openDropdownOnEditorClick?: boolean,
	showTrigger?: boolean,
	editingMode?: EditingMode,
}

export type TrivialUnitBoxChangeEvent<U> = {
	unitEntry: U,
	amount: string
}

export class TrivialUnitBox<U> implements TrivialComponent {

	private config: TrivialUnitBoxConfig<U>;

	public readonly onChange = new TeamAppsEvent<TrivialUnitBoxChangeEvent<U>>(this);
	public readonly onSelectedEntryChanged = new TeamAppsEvent<U>(this);
	public readonly onFocus = new TeamAppsEvent<void>(this);
	public readonly onBlur = new TeamAppsEvent<void>(this);

	private selectedEntry: U;
	private blurCausedByClickInsideComponent = false;
	private $editor: HTMLInputElement;
	private $unitBox: HTMLElement;
	private $selectedEntryAndTriggerWrapper: HTMLElement;
	private $selectedEntryWrapper: HTMLElement;
	private $dropDown: HTMLElement;
	private $trigger: HTMLElement;
	private usingDefaultQueryFunction: boolean;

	private numberParser: NumberParser;
	private popper: Popper;

	private listBox: TrivialTreeBox<U>;
	private dropDownComponent: DropDownComponent<U>;
	private editingMode: EditingMode;
	private dropDownOpen = false;

	constructor(options: TrivialUnitBoxConfig<U>) {
		this.config = $.extend(<TrivialUnitBoxConfig<U>>{
			defaultNumberFormat: new Intl.NumberFormat("en-US", {useGrouping: true, minimumFractionDigits: 2, maximumFractionDigits: 2}),
			numberFormatFunction: entry => this.config.defaultNumberFormat,
			unitDisplayPosition: 'right', // right or left
			allowNullAmount: true,
			entryRenderingFunction: DEFAULT_RENDERING_FUNCTIONS.currency2Line,
			selectedEntryRenderingFunction: DEFAULT_RENDERING_FUNCTIONS.currencySingleLineShort,
			noEntriesTemplate: DEFAULT_TEMPLATES.defaultNoEntriesTemplate,
			entries: null,
			queryFunction: null, // defined below...
			queryOnNonNumberCharacters: true,
			openDropdownOnEditorClick: false,
			showTrigger: true,
			matchingOptions: {
				matchingMode: 'prefix-word',
				ignoreCase: true,
				maxLevenshteinDistance: 2
			},
			editingMode: 'editable', // one of 'editable', 'disabled' and 'readonly'
		}, options);

		if (!this.config.queryFunction) {
			this.config.queryFunction = defaultListQueryFunctionFactory(this.config.entries || [], ["code", "name", "symbol"], this.config.matchingOptions);
			this.usingDefaultQueryFunction = true;
		}

		this.$unitBox = parseHtml(`<div class="tr-unitbox tr-input-wrapper">
			<input type="text" autocomplete="false"></input>
			<div class="tr-unitbox-selected-entry-and-trigger-wrapper">
				<div class="tr-unitbox-selected-entry-wrapper"></div>
				<div class="tr-trigger"><span class="tr-trigger-icon"></div>
			</div>
		</div>`);
		this.$editor = this.$unitBox.querySelector(':scope input');
		this.$selectedEntryAndTriggerWrapper = this.$unitBox.querySelector(':scope .tr-unitbox-selected-entry-and-trigger-wrapper');
		this.$selectedEntryWrapper = this.$selectedEntryAndTriggerWrapper.querySelector(':scope .tr-unitbox-selected-entry-wrapper')
		this.$trigger = this.$selectedEntryAndTriggerWrapper.querySelector(':scope .tr-trigger');
		if (!this.config.showTrigger) {
			this.$trigger.classList.add('hidden');
		}
		this.$selectedEntryAndTriggerWrapper.addEventListener('mousedown', () => {
			if (this.dropDownOpen) {
				this.closeDropDown();
			} else if (this.editingMode === "editable") {
				this.openDropDown();
				this.$editor.focus();
				this.query();
			}
		});
		this.$dropDown = parseHtml('<div class="tr-dropdown"></div>');
		this.$dropDown.addEventListener("scroll", e => {
			e.stopPropagation();
			e.preventDefault();
		});
		this.setEditingMode(this.config.editingMode);

		this.$editor.classList.add("tr-unitbox-editor", "tr-editor")
		this.$editor.addEventListener("focus", () => {
			if (this.editingMode !== "editable") {
				this.$editor.blur(); // must not get focus!
				return false;
			}
			if (this.blurCausedByClickInsideComponent) {
				// do nothing!
			} else {
				this.onFocus.fire();
				this.$unitBox.classList.add('focus');
				this.cleanupEditorValue();
				this.$editor.select();
			}
		});
		this.$editor.addEventListener("blur", () => {
			if (this.blurCausedByClickInsideComponent) {
				this.$editor.focus();
			} else {
				this.onBlur.fire();
				this.$unitBox.classList.remove('focus');
				this.formatEditorValue();
				this.closeDropDown();
			}
		});
		this.$editor.addEventListener("keydown", (e: KeyboardEvent) => {
			const usedByDropdownComponent = this.dropDownComponent.handleKeyboardInput(e)
			if (keyCodes.isModifierKey(e)) {
				return;
			} else if (e.key === "Tab" || e.key === "Enter") {
				e.key === "Enter" && e.preventDefault(); // do not submit form
				const highlightedEntry = this.dropDownComponent.getValue();
				if (this.dropDownOpen && highlightedEntry) {
					this.setSelectedEntry(highlightedEntry, false, e);
				}
				this.closeDropDown();
				this.fireChangeEvents();
				return;
			} else if (e.key === "ArrowUp" || e.key === "ArrowDown") {
				const direction = e.key === "ArrowUp" ? -1 : 1;
				if (!this.dropDownOpen) {
					this.openDropDown();
					this.query(direction);
				}
				e.preventDefault(); // some browsers move the caret to the beginning on up key
			} else if (e.key === "Escape") {
				this.closeDropDown();
				this.cleanupEditorValue();
			}
		});
		this.$editor.addEventListener("keyup", (e) => {
			if (keyCodes.specialKeys.indexOf(e.which) != -1
				&& e.key !== "Backspace"
				&& e.key !== "Delete") {
				return; // ignore
			}
			const hasDoubleDecimalSeparator = new RegExp("(?:\\" + this.getDecimalSeparator() + ".*)" + "\\" + this.getDecimalSeparator(), "g").test(this.$editor.value as string);
			if (hasDoubleDecimalSeparator) {
				this.cleanupEditorValue();
			}
			if (this.config.queryOnNonNumberCharacters) {
				if (this.getQueryString().length > 0) {
					this.openDropDown();
					this.query(1);
				} else {
					this.closeDropDown();
				}
			}
		});
		this.$editor.addEventListener("mousedown", () => {
			if (this.editingMode === "editable") {
				if (this.config.openDropdownOnEditorClick) {
					this.openDropDown();
					this.query();
				}
			}
		});
		this.$editor.addEventListener("change", () => {
			this.fireChangeEvents();
		});

		[this.$unitBox, this.$dropDown].forEach(el => {
			el.addEventListener("mousedown", () => {
				if (document.activeElement == this.$editor) {
					this.blurCausedByClickInsideComponent = true;
				}
			});
			el.addEventListener("mouseup", () => {
				if (this.blurCausedByClickInsideComponent) {
					this.$editor.focus();
					this.blurCausedByClickInsideComponent = false;
				}
			});
			el.addEventListener("mouseout", () => {
				if (this.blurCausedByClickInsideComponent) {
					this.$editor.focus();
					this.blurCausedByClickInsideComponent = false;
				}
			});
		});

		this.listBox = new TrivialTreeBox(this.config);
		this.dropDownComponent = new TreeBoxDropdown({
			queryFunction: this.config.queryFunction,
			preselectionMatcher: query => true,
			textHighlightingEntryLimit: 100
		}, this.listBox);
		this.$dropDown.append(this.dropDownComponent.getMainDomElement());
		this.dropDownComponent.onValueChanged.addListener(({value, finalSelection}) => {
			if (value && finalSelection) {
				this.setSelectedEntry(value, true);
				this.listBox.setSelectedEntryById(null);
				this.closeDropDown();
			}
		});

		this.setUnitDisplayPosition(this.config.unitDisplayPosition);
		if (this.config.amount != null) {
			this.$editor.value = this.config.amount.toString();
		}
		this.formatEditorValue();
		this.setSelectedEntry(this.config.selectedEntry || null, false, null);


		this.popper = createComboBoxPopper(this.$unitBox, this.$dropDown, () => this.closeDropDown);
	}

	private getNumberFormat() {
		return this.selectedEntry != null ? this.config.numberFormatFunction(this.selectedEntry) : this.config.defaultNumberFormat;
	}

	private getDecimalSeparator() {
		return NumberParser.getDecimalSeparatorForLocale(this.getNumberFormat().resolvedOptions().locale);
	}

	private getThousandsSeparator() {
		return NumberParser.getGroupSeparatorForLocale(this.getNumberFormat().resolvedOptions().locale);
	}

	private getNumerals() {
		return NumberParser.getNumeralsForLocale(this.getNumberFormat().resolvedOptions().locale);
	}

	private getMinFractionDigits() {
		return this.getNumberFormat().resolvedOptions().minimumFractionDigits;
	}

	private getMaxFractionDigits() {
		return this.getNumberFormat().resolvedOptions().maximumFractionDigits;
	}

	private getQueryString() {
		return (this.$editor.value || "").toString()
			.replace(/[\d\W]/g, '').trim();
	}

	private getEditorValueNumberPart(fillupDecimals?: boolean): string {
		const rawNumber = (this.$editor.value as string || "").replace(new RegExp(`[^${this.getDecimalSeparator()}${this.getNumerals().join("")}]`, "g"), '').trim();
		const decimalSeparatorIndex = rawNumber.indexOf(this.getDecimalSeparator());

		let integerPart: string;
		let fractionalPart: string;
		if (decimalSeparatorIndex !== -1) {
			integerPart = rawNumber.substring(0, decimalSeparatorIndex);
			fractionalPart = rawNumber.substring(decimalSeparatorIndex + 1, rawNumber.length).replace(/\D/g, '');
		} else {
			integerPart = rawNumber;
			fractionalPart = "";
		}

		if (integerPart.length == 0 && fractionalPart.length == 0) {
			return "";
		} else {
			if (fillupDecimals) {
				fractionalPart = fractionalPart.length < this.getMinFractionDigits() ? (fractionalPart + this.getNumerals()[0].repeat(this.getMinFractionDigits() - fractionalPart.length)) : fractionalPart.substr(0, this.getMaxFractionDigits());
			}
			return integerPart + (fractionalPart.length > 0 ? this.getDecimalSeparator() + fractionalPart : "");
		}
	}

	private toParsableNumber(localizedNumber: string): string {
		let numerals = this.getNumerals();
		return localizedNumber
			.replace(this.getThousandsSeparator(), '')
			.replace(this.getDecimalSeparator(), '.')
			.split('').map(c => c == "." ? "." : "" + numerals.indexOf(c))
			.join("");

	}

	private query(highlightDirection?: HighlightDirection) {
		// call queryFunction asynchronously to be sure the input field has been updated before the result callback is called. Note: the query() method is called on keydown...
		setTimeout(async () => {
			let newEntries = await this.config.queryFunction(this.getQueryString());
			this.updateEntries(newEntries);

			const queryString = this.getQueryString();
			if (queryString.length > 0) {
				this.listBox.highlightTextMatches(queryString);
			}
			this.listBox.selectNextEntry(highlightDirection);

			if (this.dropDownOpen) {
				this.openDropDown(); // only for repositioning!
			}
		});
	}

	private fireSelectedEntryChangedEvent() {
		this.onSelectedEntryChanged.fire(this.selectedEntry);
	}

	private fireChangeEvents() {
		this.onChange.fireIfChanged({
			unitEntry: unProxyEntry(this.selectedEntry),
			amount: this.getAmount()
		});
	}

	public setSelectedEntry(entry: U, fireEvent?: boolean, originalEvent?: unknown) {
		this.selectedEntry = entry;
		const $selectedEntry = parseHtml(this.config.selectedEntryRenderingFunction(entry));
		$selectedEntry.classList.add("tr-combobox-entry");
		this.$selectedEntryWrapper.innerHTML = "";
		this.$selectedEntryWrapper.append($selectedEntry);

		this.cleanupEditorValue();
		if (document.activeElement !== this.$editor) {
			this.formatEditorValue();
		}
		if (fireEvent) {
			this.fireSelectedEntryChangedEvent();
			this.fireChangeEvents();
		}
	}

	private formatEditorValue() {
		this.$editor.value = this.formatAmount(this.getAmount());
	}

	private cleanupEditorValue() {
		if (this.$editor.value) {
			this.$editor.value = this.getEditorValueNumberPart(true);
		}
	}

	private parentElement: Element;

	public openDropDown() {
		if (this.getMainDomElement().parentElement !== this.parentElement) {
			this.popper.destroy();
			this.popper = createComboBoxPopper(this.$unitBox, this.$dropDown, () => this.closeDropDown());
			this.parentElement = this.getMainDomElement().parentElement;
		}
		if (!this.dropDownOpen) {
			this.$unitBox.classList.add("open");
			this.popper.update();
			this.dropDownOpen = true;
		}
	}

	public closeDropDown() {
		this.$unitBox.classList.remove("open");
		this.dropDownOpen = false;
	}

	public getAmount(): string {
		const editorValueNumberPart = this.getEditorValueNumberPart(false);
		if (editorValueNumberPart.length === 0 && this.config.allowNullAmount) {
			return null;
		} else if (editorValueNumberPart.length === 0) {
			return "0";
		} else {
			return this.toParsableNumber(this.getEditorValueNumberPart(true));
		}
	}

	private isDropDownNeeded() {
		return this.editingMode == 'editable' && (this.config.entries && this.config.entries.length > 0 || !this.usingDefaultQueryFunction || this.config.showTrigger);
	}

	public setEditingMode(newEditingMode: EditingMode) {
		this.editingMode = newEditingMode;
		this.$unitBox.classList.remove("editable", "readonly", "disabled");
		this.$unitBox.classList.add(this.editingMode);
		this.$editor.readOnly = newEditingMode !== "editable";
		if (this.isDropDownNeeded()) {
			this.$unitBox.append(this.$dropDown);
		}
	}

	public updateEntries(newEntries: U[]) {
		this.listBox.setEntries(newEntries);
	}

	public getSelectedEntry(): U {
		if (this.selectedEntry == null) {
			return null;
		} else {
			return unProxyEntry(this.selectedEntry);
		}
	}

	public setAmount(amount: string) {
		if (amount == null) {
			if (this.config.allowNullAmount) {
				this.$editor.value = "";
			} else {
				this.$editor.value = this.formatAmount("0");
			}
		} else if (this.$editor == document.activeElement) {
			this.$editor.value = this.formatAmount(amount);
		} else {
			this.$editor.value = this.formatAmount(amount, false);
		}
	};

	public focus() {
		this.$editor.select();
	}

	public getEditor(): Element {
		return this.$editor;
	}

	public setUnitDisplayPosition(unitDisplayPosition: "left" | "right") {
		this.$unitBox.classList.toggle('unit-display-left', unitDisplayPosition === 'left');
		this.$unitBox.classList.toggle('unit-display-right', unitDisplayPosition === 'right');
	}

	public isDropDownOpen(): boolean {
		return this.dropDownOpen;
	}

	public destroy() {
		this.$unitBox.remove();
		this.$dropDown.remove();
	}

	getMainDomElement(): HTMLElement {
		return this.$unitBox;
	}

	private formatAmount(amount: string, noThousandSeparators: boolean = false) {
		if (amount == null) {
			return "";
		}
		let [mainString, decimalString] = amount.split(".");
		decimalString = decimalString ?? "";

		let decimalSeparator = NumberParser.getGroupSeparatorForFormat(this.getNumberFormat());
		let resolvedNumberFormatOptions: ResolvedNumberFormatOptions = this.getNumberFormat().resolvedOptions();
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
			throw "TODO";
		}
		decimalString.split('').map(digit => numerals[Number(digit)]).join("")

		let mainBigInt = mainString.length > 0 ? BigInt(mainString) : BigInt(0);
		if (roundUpMain) {
			mainBigInt += BigInt(1);
		}
		mainString = this.getNumberFormat().format(mainBigInt).split(decimalSeparator)[0];

		if (noThousandSeparators) {
			mainString.replace(this.getThousandsSeparator(), "");
		}

		return `${mainString}${decimalString}`;
	}
}

interface ResolvedNumberFormatOptions {
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
