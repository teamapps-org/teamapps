/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {BigDecimal} from "../util/BigDecimalString";

export interface TrivialUnitBoxConfig<U> extends TrivialTreeBoxConfig<U> {
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
	amount: BigDecimal
}

export class TrivialUnitBox<U> implements TrivialComponent {

	private config: TrivialUnitBoxConfig<U>;

	public readonly onChange = new TeamAppsEvent<TrivialUnitBoxChangeEvent<U>>();
	public readonly onSelectedEntryChanged = new TeamAppsEvent<U>();
	public readonly onFocus = new TeamAppsEvent<void>();
	public readonly onBlur = new TeamAppsEvent<void>();

	private selectedEntry: U;
	private clickInsideEditorWasWhileNotHavingFocus = false;
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

	private dropDownComponent: DropDownComponent<U>;
	private editingMode: EditingMode;
	private dropDownOpen = false;


	constructor(options: TrivialUnitBoxConfig<U>) {
		this.config = $.extend(<TrivialUnitBoxConfig<U>>{
			numberFormatFunction: entry => new Intl.NumberFormat("en-US", {
				useGrouping: true,
				minimumFractionDigits: 2,
				maximumFractionDigits: 2
			}),
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
				this.$editor.selectionStart = this.$editor.selectionEnd = this.config.unitDisplayPosition == "left" ? 0 : Number.MAX_SAFE_INTEGER;
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
		this.$editor.addEventListener("focus", (e) => {
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
			this.clickInsideEditorWasWhileNotHavingFocus = (document.activeElement != this.$editor);
			if (this.editingMode === "editable") {
				if (this.config.openDropdownOnEditorClick) {
					this.openDropDown();
					this.query();
				}
			}
		});
		this.$editor.addEventListener("click", ev => {
			if (this.clickInsideEditorWasWhileNotHavingFocus) {
				this.$editor.select()
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

		const listBox = new TrivialTreeBox({
			selectOnHover: true,
			...this.config
		});
		this.dropDownComponent = new TreeBoxDropdown({
			queryFunction: this.config.queryFunction,
			preselectionMatcher: query => true,
			textHighlightingEntryLimit: 100
		}, listBox);
		this.$dropDown.append(this.dropDownComponent.getMainDomElement());
		this.dropDownComponent.onValueChanged.addListener(({value, finalSelection}) => {
			if (value && finalSelection) {
				this.setSelectedEntry(value, true);
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
		return this.config.numberFormatFunction(this.selectedEntry);
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
		return (this.$editor.value || "").toString().replace(new RegExp(`[${this.getDecimalSeparator()}${this.getThousandsSeparator()}${this.getNumerals().join("")}\\s]`, "g"), '').trim();
	}

	private getEditorValueLocalNumberPart(fillupDecimals = false): string {
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
			if (fillupDecimals && fractionalPart.length < this.getMinFractionDigits()) {
				fractionalPart = fractionalPart + this.getNumerals()[0].repeat(this.getMinFractionDigits() - fractionalPart.length);
			}
			if (fractionalPart.length > this.getMaxFractionDigits()) {
				fractionalPart =  fractionalPart.substr(0, this.getMaxFractionDigits())
			}
			return integerPart + (fractionalPart.length > 0 ? this.getDecimalSeparator() + fractionalPart : "");
		}
	}

	private localNumberStringToBigDecimal(localizedNumber: string): BigDecimal {
		if (localizedNumber == null || localizedNumber.length == 0) {
			return null;
		}
		let numerals = this.getNumerals();
		return BigDecimal.of(localizedNumber
			.replace(this.getThousandsSeparator(), '')
			.replace(this.getDecimalSeparator(), '.')
			.split('').map(c => c == "." ? "." : "" + numerals.indexOf(c))
			.join(""));

	}

	private async query(highlightDirection?: HighlightDirection) {
		let gotResultsForQuery = await this.dropDownComponent.handleQuery(this.getQueryString(), highlightDirection, this.getSelectedEntry());
		if (gotResultsForQuery && document.activeElement == this.$editor) {
			this.openDropDown();
		}
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
		this.onChange.resetChangeValue();
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
		let amount = this.orFallback(this.localNumberStringToBigDecimal(this.getEditorValueLocalNumberPart()));
		this.$editor.value = amount != null ? amount.format(this.getNumberFormat(), this.$editor == document.activeElement) : "";
	}

	private orFallback(amount: BigDecimal): BigDecimal {
		if (amount == null && !this.config.allowNullAmount) {
			return BigDecimal.of("0");
		}
		return amount;
	}

	private cleanupEditorValue() {
		if (this.$editor.value) {
			this.$editor.value = this.getEditorValueLocalNumberPart(true);
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

	public getAmount(): BigDecimal {
		const editorValueNumberPart = this.getEditorValueLocalNumberPart();
		if (editorValueNumberPart.length === 0 && this.config.allowNullAmount) {
			return null;
		} else if (editorValueNumberPart.length === 0) {
			return BigDecimal.of("0");
		} else {
			return this.localNumberStringToBigDecimal(this.getEditorValueLocalNumberPart());
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

	public getSelectedEntry(): U {
		if (this.selectedEntry == null) {
			return null;
		} else {
			return unProxyEntry(this.selectedEntry);
		}
	}

	public setAmount(amount: BigDecimal) {
		amount = this.orFallback(amount);
		this.$editor.value = amount != null ? amount.format(this.getNumberFormat()): "";
		this.onChange.resetChangeValue();
	}

	public focus() {
		this.$editor.select();
	}

	public getEditor(): Element {
		return this.$editor;
	}

	public setUnitDisplayPosition(unitDisplayPosition: "left" | "right") {
		this.config.unitDisplayPosition = unitDisplayPosition;
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

}


