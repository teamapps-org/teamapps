/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
import {defaultListQueryFunctionFactory, keyCodes, QueryFunction} from "../trivial-components/TrivialCore";
import {TrivialUnitBox, TrivialUnitBoxChangeEvent} from "../trivial-components/TrivialUnitBox";

import {createUiCurrencyValueConfig, UiCurrencyValueConfig} from "../../generated/UiCurrencyValueConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiCurrencyFieldCommandHandler, UiCurrencyFieldConfig, UiCurrencyFieldEventSource} from "../../generated/UiCurrencyFieldConfig";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {deepEquals, selectElementContents} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {
	UiTextInputHandlingField_SpecialKeyPressedEvent,
	UiTextInputHandlingField_TextInputEvent
} from "../../generated/UiTextInputHandlingFieldConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {UiCurrencyUnitConfig} from "../../generated/UiCurrencyUnitConfig";
import {BigDecimal} from "../util/BigDecimalString";

export class UiCurrencyField extends UiField<UiCurrencyFieldConfig, UiCurrencyValueConfig> implements UiCurrencyFieldEventSource, UiCurrencyFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>({throttlingMode: "debounce", delay: 250});
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>({throttlingMode: "debounce", delay: 250});

	private trivialUnitBox: TrivialUnitBox<UiCurrencyUnitConfig>;
	private queryFunction: QueryFunction<UiCurrencyUnitConfig>;
	private numberFormat: Intl.NumberFormat;

	protected initialize(config: UiCurrencyFieldConfig, context: TeamAppsUiContext) {
		let initialPrecision = config.fixedPrecision >= 0 ? config.fixedPrecision : 2;

		this.trivialUnitBox = new TrivialUnitBox<UiCurrencyUnitConfig>({
			numberFormatFunction: entry => this.getNumberFormat(entry),
			idFunction: entry => entry.code,
			unitDisplayPosition: config.showCurrencyBeforeAmount ? 'left' : 'right', // right or left
			entryRenderingFunction: entry => {
				entry = entry || {};
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
				} else if (this._config.showCurrencySymbol && entry.symbol) {
					return `<div class="tr-template-currency-single-line-short">${entry.symbol} (${entry.code})</div>`;
				} else {
					return `<div class="tr-template-currency-single-line-short">${entry.code}</div>`;
				}
			},
			queryOnNonNumberCharacters: config.alphaKeysQueryForCurrency,
			editingMode: this.convertToTrivialComponentsEditingMode(config.editingMode),
			queryFunction: (queryString) => this.queryFunction(queryString)
		});
		this.trivialUnitBox.getMainDomElement().classList.add("UiCurrencyField", "default-min-field-width");
		this.trivialUnitBox.onChange.addListener((value: TrivialUnitBoxChangeEvent<any>) => {
			this.commit();
		});
		this.trivialUnitBox.getEditor().addEventListener('keyup', (e: KeyboardEvent) => {
			if (e.keyCode !== keyCodes.enter
				&& e.keyCode !== keyCodes.tab
				&& !keyCodes.isModifierKey(e)) {
				this.onTextInput.fire({
					enteredString: (this.trivialUnitBox.getEditor() as HTMLInputElement).value
				});
			} else 	if (e.keyCode === keyCodes.escape) {
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ESCAPE
				});
			} else if (e.keyCode === keyCodes.enter) {
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ENTER
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

	private getNumberFormat(entry: UiCurrencyUnitConfig) {
		if (entry == null) {
			let fractionDigits = this._config.fixedPrecision >= 0 ? this._config.fixedPrecision : 2;
			return new Intl.NumberFormat(this._config.locale, {
				useGrouping: true,
				minimumFractionDigits: fractionDigits,
				maximumFractionDigits: fractionDigits
			});
		} else {
			let fractionDigits = this._config.fixedPrecision >= 0 ? this._config.fixedPrecision : entry.fractionDigits >= 0 ? entry.fractionDigits : 4;
			return new Intl.NumberFormat(this._config.locale, {
				minimumFractionDigits: fractionDigits,
				maximumFractionDigits: fractionDigits,
				useGrouping: true
			})
		}
	}

	isValidData(v: UiCurrencyValueConfig): boolean {
		return v == null || typeof v === "object";
	}

	private convertToTrivialComponentsEditingMode(editingMode: UiFieldEditingMode) {
		return editingMode === UiFieldEditingMode.READONLY ? 'readonly' : editingMode === UiFieldEditingMode.DISABLED ? 'disabled' : 'editable';
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.trivialUnitBox.getMainDomElement() as HTMLElement;
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialUnitBox.setSelectedEntry(uiValue?.currencyUnit);
			this.trivialUnitBox.setAmount(BigDecimal.of(uiValue?.amount));
		} else {
			this.trivialUnitBox.setAmount(null);
			this.trivialUnitBox.setSelectedEntry(null);
		}
	}

	focus(): void {
		this.trivialUnitBox.focus();
		selectElementContents(this.trivialUnitBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor"));
	}

	destroy(): void {
		super.destroy();
		this.trivialUnitBox.destroy();
	}

	getTransientValue(): UiCurrencyValueConfig {
		let amount = this.trivialUnitBox.getAmount();
		return createUiCurrencyValueConfig(this.trivialUnitBox.getSelectedEntry() && this.trivialUnitBox.getSelectedEntry(), this.trivialUnitBox.getAmount()?.value);
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainElement().classList.add(UiField.editingModeCssClasses[editingMode]);
		this.trivialUnitBox.setEditingMode(this.convertToTrivialComponentsEditingMode(editingMode));
	}

	public getReadOnlyHtml(value: UiCurrencyValueConfig, availableWidth: number): string {
		let content: string;
		if (value != null) {
			const currency = value.currencyUnit;
			let displayedCurrency: string;
			if (currency != null) {
				displayedCurrency = this._config.showCurrencySymbol ? `${currency?.symbol} (${currency?.code})` : currency?.code;
			} else {
				displayedCurrency = null;
			}
			let amount = BigDecimal.of(value.amount);
			let formattedAmount = amount?.format(this.getNumberFormat(currency));
			if (this._config.showCurrencyBeforeAmount) {
				content = [displayedCurrency, formattedAmount].filter(x => x != null).join(' ');
			} else {
				content = [formattedAmount, displayedCurrency].filter(x => x != null).join(' ');
			}
		} else {
			content = "";
		}
		return `<div class="static-readonly-UiCurrencyField">${content}</div>`;
	}

	getDefaultValue() {
		return createUiCurrencyValueConfig(null, null);
	}

	setCurrencyUnits(currencyUnits: UiCurrencyUnitConfig[]): void {
		this._config.currencyUnits = currencyUnits;
		this.queryFunction = defaultListQueryFunctionFactory(currencyUnits, ["code", "name", "symbol"], {matchingMode: "contains", ignoreCase: true});
	}

	setShowCurrencyBeforeAmount(showCurrencyBeforeAmount: boolean): void {
		this.trivialUnitBox.setUnitDisplayPosition(showCurrencyBeforeAmount ? 'left' : "right")
	}

	setShowCurrencySymbol(showCurrencySymbol: boolean): void {
		this._config.showCurrencySymbol = showCurrencySymbol;
		this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry());
	}

	public valuesChanged(v1: UiCurrencyValueConfig, v2: UiCurrencyValueConfig): boolean {
		return !deepEquals(v1, v2);
	}

	setLocale(locale: string): void {
		this._config.locale = locale;
		this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry()); // update format
	}

	setFixedPrecision(fixedPrecision: number): void {
		this._config.fixedPrecision = fixedPrecision;
		this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry()); // update format
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiCurrencyField", UiCurrencyField);
