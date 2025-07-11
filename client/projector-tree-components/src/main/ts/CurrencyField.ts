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
import {defaultListQueryFunctionFactory, isModifierKey, type QueryFunction} from "./trivial-components/TrivialCore";
import {TrivialUnitBox, type TrivialUnitBoxChangeEvent} from "./trivial-components/TrivialUnitBox";
import {
	AbstractField,
	BigDecimal,
	DebounceModes,
	deepEquals,
	executeAfterAttached,
	type FieldEditingMode,
	FieldEditingModes,
	ProjectorEvent
} from "projector-client-object-api";
import {
	type DtoCurrencyField,
	type DtoCurrencyField_TextInputEvent,
	type DtoCurrencyFieldCommandHandler,
	type DtoCurrencyFieldEventSource,
	type DtoCurrencyUnit,
	type DtoCurrencyValue
} from "./generated";
import {selectElementContents} from "./util";

export class CurrencyField extends AbstractField<DtoCurrencyField, DtoCurrencyValue> implements DtoCurrencyFieldEventSource, DtoCurrencyFieldCommandHandler {

	public readonly onTextInput: ProjectorEvent<DtoCurrencyField_TextInputEvent> = ProjectorEvent.createDebounced(250, DebounceModes.BOTH);

	private trivialUnitBox: TrivialUnitBox<DtoCurrencyUnit>;
	private queryFunction: QueryFunction<DtoCurrencyUnit>;

	protected initialize(config: DtoCurrencyField) {

		this.trivialUnitBox = new TrivialUnitBox<DtoCurrencyUnit>({
			numberFormatFunction: entry => this.getNumberFormat(entry),
			idFunction: entry => entry.code,
			unitDisplayPosition: config.showCurrencyBeforeAmount ? 'left' : 'right', // right or left
			entryRenderingFunction: entry => {
				entry = entry ?? {} as DtoCurrencyUnit;
				return `<div class="tr-template-currency-single-line-long">
				  <div class="content-wrapper tr-editor-area"> 
					<div class="symbol-and-code">${entry.code != null ? `<span class="currency-code">${entry.code || ''}</span>` : ''} ${entry.symbol != null ? `<span class="currency-symbol">${entry.symbol || ''}</span>` : ''}</div>
					<div class="currency-name">${entry.name || ''}</div>
				  </div>
				</div>`;
			},
			selectedEntryRenderingFunction: (entry) => {
				if (entry == null) {
					return `<div class="tr-template-currency-single-line-short">-</div>`
				} else if (this.config.showCurrencySymbol && entry.symbol) {
					return `<div class="tr-template-currency-single-line-short">${entry.symbol} (${entry.code})</div>`;
				} else {
					return `<div class="tr-template-currency-single-line-short">${entry.code}</div>`;
				}
			},
			queryOnNonNumberCharacters: config.alphabeticKeysQueryEnabled,
			editingMode: this.convertToTrivialComponentsEditingMode(config.editingMode),
			queryFunction: (queryString) => this.queryFunction(queryString)
		});
		this.trivialUnitBox.getMainDomElement().classList.add("DtoCurrencyField", "default-min-field-width");
		this.trivialUnitBox.onChange.addListener((value: TrivialUnitBoxChangeEvent<any>) => {
			this.commit();
		});
		this.trivialUnitBox.getEditor().addEventListener('keyup', (e: KeyboardEvent) => {
			if (e.key !== "Enter"
				&& e.key !== "Tab"
				&& !isModifierKey(e)) {
				this.onTextInput.fire({
					enteredString: (this.trivialUnitBox.getEditor() as HTMLInputElement).value
				});
			}
		});

		this.setCurrencyUnits(config.currencyUnits);

		this.trivialUnitBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialUnitBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialUnitBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-unitbox-selected-entry-and-trigger-wrapper").classList.add("field-border");
		this.trivialUnitBox.onFocus.addListener(() => this.getMainElement().classList.add("focus"));
		this.trivialUnitBox.onBlur.addListener(() => this.getMainElement().classList.remove("focus"));
	}

	private getNumberFormat(entry: DtoCurrencyUnit) {
		if (entry == null) {
			let fractionDigits = this.config.fixedPrecision >= 0 ? this.config.fixedPrecision : 2;
			return new Intl.NumberFormat(this.config.locale, {
				useGrouping: true,
				minimumFractionDigits: fractionDigits,
				maximumFractionDigits: fractionDigits
			});
		} else {
			let fractionDigits = this.config.fixedPrecision >= 0 ? this.config.fixedPrecision : entry.fractionDigits >= 0 ? entry.fractionDigits : 4;
			return new Intl.NumberFormat(this.config.locale, {
				minimumFractionDigits: fractionDigits,
				maximumFractionDigits: fractionDigits,
				useGrouping: true
			})
		}
	}

	isValidData(v: DtoCurrencyValue): boolean {
		return v == null || typeof v === "object";
	}

	private convertToTrivialComponentsEditingMode(editingMode: FieldEditingMode) {
		return editingMode === FieldEditingModes.READONLY ? 'readonly' : editingMode === FieldEditingModes.DISABLED ? 'disabled' : 'editable';
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialUnitBox.getMainDomElement() as HTMLElement;
	}

	protected displayCommittedValue(): void {
		let DtoValue = this.getCommittedValue();
		if (DtoValue) {
			this.trivialUnitBox.setSelectedEntry(DtoValue?.currencyUnit);
			this.trivialUnitBox.setAmount(BigDecimal.of(DtoValue?.amount));
		} else {
			this.trivialUnitBox.setAmount(null);
			this.trivialUnitBox.setSelectedEntry(null);
		}
	}

	@executeAfterAttached()
	focus(): void {
		this.trivialUnitBox.focus();
		selectElementContents(this.trivialUnitBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor"));
	}

	destroy(): void {
		super.destroy();
		this.trivialUnitBox.destroy();
	}

	getTransientValue(): DtoCurrencyValue {
		return {
			currencyUnit: this.trivialUnitBox.getSelectedEntry() && this.trivialUnitBox.getSelectedEntry(),
			amount: this.trivialUnitBox.getAmount()?.value
		};
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.values(FieldEditingModes));
		this.getMainElement().classList.add(editingMode);
		this.trivialUnitBox.setEditingMode(this.convertToTrivialComponentsEditingMode(editingMode));
	}

	public getReadOnlyHtml(value: DtoCurrencyValue, availableWidth: number): string {
		let content: string;
		if (value != null) {
			const currency = value.currencyUnit;
			let displayedCurrency: string;
			if (currency != null) {
				displayedCurrency = this.config.showCurrencySymbol ? `${currency?.symbol} (${currency?.code})` : currency?.code;
			} else {
				displayedCurrency = null;
			}
			let amount = BigDecimal.of(value.amount);
			let formattedAmount = amount?.format(this.getNumberFormat(currency));
			if (this.config.showCurrencyBeforeAmount) {
				content = [displayedCurrency, formattedAmount].filter(x => x != null).join(' ');
			} else {
				content = [formattedAmount, displayedCurrency].filter(x => x != null).join(' ');
			}
		} else {
			content = "";
		}
		return `<div class="static-readonly-DtoCurrencyField">${content}</div>`;
	}

	getDefaultValue() {
		return {
			currencyUnit: null, amount: null
		};
	}

	setCurrencyUnits(currencyUnits: DtoCurrencyUnit[]): void {
		this.config.currencyUnits = currencyUnits;
		this.queryFunction = defaultListQueryFunctionFactory(currencyUnits, ["code", "name", "symbol"], {matchingMode: "contains", ignoreCase: true});
	}

	setShowCurrencyBeforeAmount(showCurrencyBeforeAmount: boolean): void {
		this.trivialUnitBox.setUnitDisplayPosition(showCurrencyBeforeAmount ? 'left' : "right")
	}

	setShowCurrencySymbol(showCurrencySymbol: boolean): void {
		this.config.showCurrencySymbol = showCurrencySymbol;
		this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry());
	}

	public valuesChanged(v1: DtoCurrencyValue, v2: DtoCurrencyValue): boolean {
		return !deepEquals(v1, v2);
	}

	setLocale(locale: string): void {
		this.config.locale = locale;
		this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry()); // update format
	}

	setFixedPrecision(fixedPrecision: number): void {
		this.config.fixedPrecision = fixedPrecision;
		this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry()); // update format
	}

	setAlphabeticKeysQueryEnabled(alphabeticKeysQueryEnabled: boolean) {
		this.config.alphabeticKeysQueryEnabled = alphabeticKeysQueryEnabled;
		this.trivialUnitBox.setQueryOnNonNumberCharacters(alphabeticKeysQueryEnabled);
	}
}


