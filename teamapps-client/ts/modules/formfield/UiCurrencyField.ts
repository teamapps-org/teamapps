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
import {DEFAULT_RENDERING_FUNCTIONS, defaultListQueryFunctionFactory, keyCodes, QueryFunction, TrivialUnitBox} from "trivial-components";

import {createUiCurrencyValueConfig, UiCurrencyValueConfig} from "../../generated/UiCurrencyValueConfig";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiCurrencyFieldConfig, UiCurrencyFieldCommandHandler, UiCurrencyFieldEventSource} from "../../generated/UiCurrencyFieldConfig";
import {UiField} from "./UiField";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {CURRENCIES, Currency} from "../util/currencies";
import {formatDecimalNumber, parseHtml, selectElementContents} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../generated/UiTextInputHandlingFieldConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {EventFactory} from "../../generated/EventFactory";

export class UiCurrencyField extends UiField<UiCurrencyFieldConfig, UiCurrencyValueConfig> implements UiCurrencyFieldEventSource, UiCurrencyFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, 250);

	private trivialUnitBox: TrivialUnitBox<Currency>;
	private $originalInput: HTMLElement;

	private currencyCodeList: string[];
	private showCurrencySymbol: boolean;
	private defaultCurrencyCode: string;
	
	private queryFunction: QueryFunction<Currency>;

	protected initialize(config: UiCurrencyFieldConfig, context: TeamAppsUiContext) {
		this.$originalInput = parseHtml('<input type="text" autocomplete="off">');

		this.trivialUnitBox = new TrivialUnitBox<Currency>(this.$originalInput, {
			idFunction: entry => entry.code,
			unitValueProperty: 'code',
			unitIdProperty: 'code',
			decimalPrecision: config.precision,
			decimalSeparator: context.config.decimalSeparator,
			thousandsSeparator: context.config.thousandsSeparator,
			unitDisplayPosition: config.showCurrencyBeforeAmount ? 'left' : 'right', // right or left
			entryRenderingFunction: DEFAULT_RENDERING_FUNCTIONS.currencySingleLineLong,
			selectedEntryRenderingFunction: (entry) => {
				if (entry == null) {
					return `<div class="tr-template-currency-single-line-short">-</div>`
				} else if (this.showCurrencySymbol && entry.symbol) {
					return `<div class="tr-template-currency-single-line-short">${entry.symbol} (${entry.code})</div>`;
				} else {
					return `<div class="tr-template-currency-single-line-short">${entry.code}</div>`;
				}
			},
			selectedEntry: {...CURRENCIES[this.defaultCurrencyCode]},
			queryOnNonNumberCharacters: config.alphaKeysQueryForCurrency,
			editingMode: this.convertToTrivialComponentsEditingMode(config.editingMode),
			queryFunction: (queryString, callback) => this.queryFunction(queryString, callback)
		});
		this.trivialUnitBox.getMainDomElement().classList.add("UiCurrencyField");
		this.trivialUnitBox.onChange.addListener((value: {
			unit: string,
			unitEntry: any,
			amount: number,
			amountAsFloatingPointNumber: number
		}) => {
			this.commit();
		});
		this.trivialUnitBox.getEditor().addEventListener('keyup', (e: KeyboardEvent) => {
			if (e.keyCode !== keyCodes.enter
				&& e.keyCode !== keyCodes.tab
				&& !keyCodes.isModifierKey(e)) {
				this.onTextInput.fire(EventFactory.createUiTextInputHandlingField_TextInputEvent(this.getId(), (this.trivialUnitBox.getEditor() as HTMLInputElement).value));
			} else 	if (e.keyCode === keyCodes.escape) {
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ESCAPE));
			} else if (e.keyCode === keyCodes.enter) {
				this.onSpecialKeyPressed.fire(EventFactory.createUiTextInputHandlingField_SpecialKeyPressedEvent(this.getId(), UiSpecialKey.ENTER));
			}
		});

		this.setDefaultCurrencyCode(config.defaultCurrencyCode);
		this.setCurrencyCodeList(config.currencyCodeList || Object.keys(CURRENCIES));
		this.setShowCurrencySymbol(config.showCurrencySymbol);

		this.trivialUnitBox.getMainDomElement().classList.add("field-border", "field-border-glow", "field-background");
		this.trivialUnitBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor").classList.add("field-background");
		this.trivialUnitBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-unitbox-selected-entry-and-trigger-wrapper").classList.add("field-border");
		this.trivialUnitBox.onFocus.addListener(() => this.getMainDomElement().classList.add("focus"));
		this.trivialUnitBox.onBlur.addListener(() => this.getMainDomElement().classList.remove("focus"));
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

	public getFocusableElement(): HTMLElement {
		return this.trivialUnitBox.getMainDomElement().querySelector<HTMLElement>(":scope .tr-editor");
	}

	public hasFocus(): boolean {
		return this.getMainInnerDomElement().matches('.focus');
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (uiValue) {
			this.trivialUnitBox.setSelectedEntry(CURRENCIES[uiValue.currencyCode]);
			this.trivialUnitBox.setAmount(uiValue.value);
		} else {
			this.trivialUnitBox.setAmount(null);
			this.trivialUnitBox.setSelectedEntry(CURRENCIES[this.defaultCurrencyCode]);
		}
	}

	focus(): void {
		this.trivialUnitBox.focus();
		selectElementContents(this.getFocusableElement());
	}

	doDestroy(): void {
		this.trivialUnitBox.destroy();
		this.$originalInput.remove();
	}

	getTransientValue(): UiCurrencyValueConfig {
		return createUiCurrencyValueConfig(this.trivialUnitBox.getAmount(), this.trivialUnitBox.getSelectedEntry() && this.trivialUnitBox.getSelectedEntry().code);
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		this.getMainDomElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		this.getMainDomElement().classList.add(UiField.editingModeCssClasses[editingMode]);
		this.trivialUnitBox.setEditingMode(this.convertToTrivialComponentsEditingMode(editingMode));
	}

	public getReadOnlyHtml(value: UiCurrencyValueConfig, availableWidth: number): string {
		let content: string;
		if (value != null) {
			const currency = CURRENCIES[value.currencyCode || this.defaultCurrencyCode || ""];
			const displayedCurrency = this.showCurrencySymbol ? currency.symbol : currency.code;
			const amountAsString = formatDecimalNumber(value.value, this._config.precision, this._context.config.decimalSeparator, this._context.config.thousandsSeparator);
			content = (this._config.showCurrencyBeforeAmount ? displayedCurrency + ' ' : '') + amountAsString + (this._config.showCurrencyBeforeAmount ? '' : ' ' + displayedCurrency);
		} else {
			content = "";
		}
		return `<div class="static-readonly-UiCurrencyField">${content}</div>`;
	}

	getDefaultValue() {
		return createUiCurrencyValueConfig(0, this.defaultCurrencyCode);
	}

	setCurrencyCodeList(currencyCodeList: string[]): void {
		this.currencyCodeList = currencyCodeList;
		this.queryFunction = defaultListQueryFunctionFactory(this.currencyCodeList.map((code) => CURRENCIES[code]), ["code", "name", "symbol"], {matchingMode: "contains", ignoreCase: true});
		if (this.trivialUnitBox.isDropDownOpen()) {
			this.trivialUnitBox.updateEntries(this.currencyCodeList.map((code) => CURRENCIES[code]));
		}
	}

	setShowCurrencyBeforeAmount(showCurrencyBeforeAmount: boolean): void {
		this.trivialUnitBox.setUnitDisplayPosition(showCurrencyBeforeAmount ? 'left' : "right")
	}

	setShowCurrencySymbol(showCurrencySymbol: boolean): void {
		this.showCurrencySymbol = showCurrencySymbol;
		this.trivialUnitBox.setSelectedEntry(this.trivialUnitBox.getSelectedEntry());
	}

	setDefaultCurrencyCode(defaultCurrencyCode: string): void {
		this.defaultCurrencyCode = defaultCurrencyCode;
	}

	public valuesChanged(v1: UiCurrencyValueConfig, v2: UiCurrencyValueConfig): boolean {
		let nullAndNonNull = ((v1 == null) !== (v2 == null));
		let nonNullAndValuesDifferent = (v1 != null && v2 != null && (v1.value !== v2.value || v1.currencyCode !== v2.currencyCode));
		return nullAndNonNull || nonNullAndValuesDifferent;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiCurrencyField", UiCurrencyField);
