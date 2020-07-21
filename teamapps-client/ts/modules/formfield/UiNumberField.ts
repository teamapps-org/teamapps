/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {UiField} from "./UiField";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiNumberFieldCommandHandler, UiNumberFieldConfig, UiNumberFieldEventSource} from "../../generated/UiNumberFieldConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {Constants, formatNumber, parseHtml} from "../Common";
import {keyCodes} from "../trivial-components/TrivialCore";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiTextInputHandlingField_SpecialKeyPressedEvent, UiTextInputHandlingField_TextInputEvent} from "../../generated/UiTextInputHandlingFieldConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiSpecialKey} from "../../generated/UiSpecialKey";
import {UiNumberFieldSliderMode} from "../../generated/UiNumberFieldSliderMode";

export class UiNumberField extends UiField<UiNumberFieldConfig, number> implements UiNumberFieldEventSource, UiNumberFieldCommandHandler {

	public readonly onTextInput: TeamAppsEvent<UiTextInputHandlingField_TextInputEvent> = new TeamAppsEvent<UiTextInputHandlingField_TextInputEvent>(this, 250);
	public readonly onSpecialKeyPressed: TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent> = new TeamAppsEvent<UiTextInputHandlingField_SpecialKeyPressedEvent>(this, 250);

	private $wrapper: HTMLElement;
	private $clearableFieldWrapper: HTMLElement;
	private $field: HTMLInputElement;
	private precision: number;
	private showClearButton: boolean;
	private $slider: HTMLElement;
	private $sliderHandle: HTMLElement;

	private minValue: number;
	private maxValue: number;
	private sliderMode: UiNumberFieldSliderMode;
	private sliderStep: number;
	private commitOnSliderChange: boolean;

	protected initialize(config: UiNumberFieldConfig, context: TeamAppsUiContext) {
		this.$wrapper = parseHtml(`<div class="UiNumberField form-control field-border field-border-glow field-background">
	<div class="clearable-field-wrapper">
		<input autocomplete="off" type="text"></input>
		<div class="clear-button tr-remove-button"></div> 
	</div>             
    <div class="slider field-readonly-invisible field-border-visibility">
		<div class="slider-track field-border"></div>
		<div class="slider-handle field-border field-border-glow"></div>			
	</div>         
</div>`);
		this.$clearableFieldWrapper = this.$wrapper.querySelector<HTMLElement>(":scope .clearable-field-wrapper");
		this.$field = this.$wrapper.querySelector(":scope input");
		this.$slider = this.$wrapper.querySelector<HTMLElement>(":scope .slider");
		const $sliderTrack = this.$wrapper.querySelector<HTMLElement>(":scope .slider-track");
		this.$sliderHandle = this.$wrapper.querySelector<HTMLElement>(":scope .slider-handle");
		let $clearButton = this.$wrapper.querySelector<HTMLElement>(':scope .clear-button');
		$clearButton.addEventListener('click',() => {
			this.$field.value = "";
			this.fireTextInput();
			this.commit();
			this.updateClearButton();
			this.setSliderPositionByValue(this.getTransientValue());
		});

		this.setEmptyText(config.emptyText);
		this.setPrecision(config.precision);
		this.setShowClearButton(config.showClearButton);
		this.setCommitOnSliderChange(config.commitOnSliderChange);

		this.$field.addEventListener("keyup", () => {
			this.setSliderPositionByValue(this.getTransientValue());
		});

		this.$field.addEventListener("focus", () => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
				this.$field.select();
			}
		});
		this.$field.addEventListener("blur", (e) => {
			if (this.getEditingMode() !== UiFieldEditingMode.READONLY) {
				this.commit();
				this.updateClearButton();
			}
		});
		this.$field.addEventListener("input", () => {
			this.fireTextInput();
			this.updateClearButton();
		});
		this.$field.addEventListener("keydown", (e) => {
			if (e.keyCode === keyCodes.escape) {
				this.displayCommittedValue(); // back to committedValue
				this.fireTextInput();
				this.$field.select();
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ESCAPE
				});
			} else if (e.keyCode === keyCodes.up_arrow || e.keyCode == keyCodes.down_arrow) {
				if (this.getTransientValue() != null) {
					e.preventDefault(); // no jumping cursor to start or end
					const newValue = this.getBoundedValue(this.getTransientValue() + (e.keyCode === keyCodes.up_arrow ? this.sliderStep : -this.sliderStep));
					this.setEditorValue(newValue);
					this.setSliderPositionByValue(newValue);
					this.$field.select();
				}
			} else if (e.keyCode === keyCodes.enter) {
				this.commit();
				this.onSpecialKeyPressed.fire({
					key: UiSpecialKey.ENTER
				});
			}
		});

		this.$wrapper.addEventListener('click',(e) => {
			if (e.target !== this.$field) {
				this.focus();
			}
		});

		this.$sliderHandle.addEventListener(`${Constants.POINTER_EVENTS.start}`, (e: PointerEvent & TouchEvent) => {
			const isSpecialMouseButton = e.button != null && e.button !== 0;
			if (isSpecialMouseButton || this.getEditingMode() === UiFieldEditingMode.DISABLED) {
				return;
			}
			if (!this.hasFocus() && !this._context.config.optimizedForTouch) {
				this.focus();
			}
			e.preventDefault(); // do not lose the focus!
			let pointerSliderHandleOffset = (e.clientX || e.touches[0].clientX) - this.$sliderHandle.getBoundingClientRect().left;
			let moveHandler = (e: any) => {
				let sliderClientX = this.$slider.getBoundingClientRect().left;
				let aPrioriNewSliderPositionX = (e.clientX || e.touches[0].clientX) - pointerSliderHandleOffset - sliderClientX;
				let [minSliderX, maxSliderX] = this.getSliderMinMaxPositionX();
				const aPrioriNewValue = this.minValue + ((aPrioriNewSliderPositionX / (maxSliderX - minSliderX)) * (this.maxValue - this.minValue));
				let newValue = this.coerceToSteppedValue(aPrioriNewValue);
				this.setSliderPositionByValue(newValue);
				this.$field.value = this.formatNumber(newValue);
				if (!this._context.config.optimizedForTouch) {
					this.$field.select();
				}
				this.ensureDecimalInput();
				this.updateClearButton();
			};
			document.addEventListener(`${Constants.POINTER_EVENTS.move}`, moveHandler);
			let endHandler = () => {
				document.removeEventListener(`${Constants.POINTER_EVENTS.move}`, moveHandler);
				document.removeEventListener(`${Constants.POINTER_EVENTS.end}`, endHandler);
				if (!this._context.config.optimizedForTouch) {
					this.focus();
				}
				if (this.commitOnSliderChange) {
					this.commit();
				}
			};
			document.addEventListener(`${Constants.POINTER_EVENTS.end}`, endHandler);
		});

		this.setMinValue(config.minValue);
		this.setMaxValue(config.maxValue);
		this.setSliderMode(config.sliderMode);
		this.setSliderStep(config.sliderStep);
	}

	private fireTextInput() {
		this.onTextInput.fire({
			enteredString: this.$field.value
		});
	}

	commit(forceEvenIfNotChanged?: boolean): boolean {
		let boundedValue = this.getBoundedValue(this.getTransientValue());
		this.setEditorValue(boundedValue);
		return super.commit(forceEvenIfNotChanged);
	}

	private setEditorValue(value: number) {
		this.$field.value = this.formatNumber(value);
	}

	private setSliderPositionByValue(newValue: number) {
		if (newValue == null) {
			newValue = (this.minValue + this.maxValue) / 2; // display slider at middle of track
		}
		let [minSliderX, maxSliderX] = this.getSliderMinMaxPositionX();
		let newHandleX = minSliderX + (maxSliderX - minSliderX) * ((newValue - this.minValue) / (this.maxValue - this.minValue));
		newHandleX = Math.max(minSliderX, Math.min(newHandleX, maxSliderX));
		this.$sliderHandle.style.left = `${newHandleX}px`;
	}

	private getSliderMinMaxPositionX() {
		return [1, this.$slider.offsetWidth - 1 - this.$sliderHandle.offsetWidth];
	}

	private coerceToSteppedValue(aPrioriNewValue: number) {
		const boundedNewValue = this.getBoundedValue(aPrioriNewValue);
		let totalNumberOfTicks = ((this.maxValue - this.minValue) / this.sliderStep);
		let tickIndex = Math.round(((boundedNewValue - this.minValue) / (this.maxValue - this.minValue)) * totalNumberOfTicks);
		let newValue = Math.min(this.maxValue, this.minValue + tickIndex * this.sliderStep);
		if (newValue > this.maxValue) {
			newValue -= this.sliderStep;
		}
		return newValue;
	}

	private getBoundedValue(unboundedValue: number) {
		if (unboundedValue == null) {
			return unboundedValue;
		}
		return Math.max(this.minValue, Math.min(unboundedValue, this.maxValue));
	}

	onResize(): void {
		this.setSliderPositionByValue(this.getTransientValue());
	}

	private ensureDecimalInput() {
		const cursorPosition = (<HTMLInputElement>this.$field).selectionEnd;
		const oldValue = this.$field.value;

		let newValue = this.convertInputToDecimalValue(oldValue);

		if (oldValue !== this.formatNumber(newValue)) {
			this.setEditorValue(newValue);

			const newCursorPosition = Math.min(this.$field.value.length, cursorPosition);

			try {
				(<HTMLInputElement>this.$field).setSelectionRange(newCursorPosition, newCursorPosition);
			} catch (e) {
				// IE throws an error when invoking setSelectionRange before the element is rendered...
			}
		}
	}

	isValidData(v: number): boolean {
		return v == null || typeof v === "number";
	}

	private convertInputToDecimalValue(oldValue: string): number {
		let decimalSeparator = this._context.config.decimalSeparator;
		let newValue = oldValue.replace(new RegExp('[^\-0-9' + decimalSeparator + ']', 'g'), '');
		newValue = newValue.replace(/(\d*\.\d*)\./g, '$1'); // only one decimal separator!!
		newValue = newValue.replace(/(.)-*/g, '$1'); // minus may only occure at the beginning

		const decimalSeparatorIndex = newValue.indexOf(decimalSeparator);
		if (this.precision >= 0 && decimalSeparatorIndex != -1 && newValue.length - decimalSeparatorIndex - 1 > this.precision) {
			newValue = newValue.substring(0, decimalSeparatorIndex + 1 + this.precision);
		}
		newValue = newValue.replace(decimalSeparator, '.');
		return parseFloat(newValue);
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$wrapper;
	}

	public getFocusableElement(): HTMLElement {
		return this.$field;
	}

	protected displayCommittedValue(): void {
		let value = this.getCommittedValue();
		this.$field.value = value != null ? this.formatNumber(value) : "";
		this.updateClearButton();
		this.setSliderPositionByValue(value);
	}

	private formatNumber(value: number) {
		return formatNumber(value, this.precision, this._context.config.decimalSeparator, "");
	}

	public getTransientValue(): number {
		if (!this.$field.value) {
			return null;
		} else {
			let value = this.convertInputToDecimalValue(this.$field.value);
			if (value > this.maxValue) {
				value = this.maxValue;
			} else if (value < this.minValue) {
				value = this.minValue;
			}
			return value;
		}
	}

	focus(): void {
		this.$field.select();
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this);
	}

	public getReadOnlyHtml(value: number, availableWidth: number): string {
		let formatedValue: string;
		if (value != null && value != null) {
			formatedValue = formatNumber(value, this.precision, this._context.config.decimalSeparator, this._context.config.thousandsSeparator);
		} else {
			formatedValue = "";
		}
		return `<div class="static-readonly-UiNumberField">${formatedValue}</div>`;
	}

	getDefaultValue(): number {
		return null; // yes
	}

	setPrecision(precision: number): void {
		this.precision = precision;
		this.ensureDecimalInput();
	}

	setEmptyText(emptyText: string): void {
		this.$field.placeholder = emptyText || '';
	}

	setShowClearButton(showClearButton: boolean): void {
		this.showClearButton = showClearButton;
		this.updateClearButton();
	}

	private updateClearButton() {
		this.$clearableFieldWrapper.classList.toggle("clearable", !!(this.showClearButton && this.getTransientValue()));
	}

	public valuesChanged(v1: number, v2: number): boolean {
		return v1 !== v2;
	}

	setMinValue(minValue: number): void {
		this.minValue = minValue;
		this.setSliderPositionByValue(this.getTransientValue());
	}

	setMaxValue(maxValue: number): void {
		this.maxValue = maxValue;
		this.setSliderPositionByValue(this.getTransientValue());
	}

	setSliderMode(sliderMode: UiNumberFieldSliderMode): void {
		this.sliderMode = sliderMode;
		this.$wrapper.classList.toggle("slider-mode-disabled", sliderMode === UiNumberFieldSliderMode.DISABLED);
		this.$wrapper.classList.toggle("slider-mode-visible", sliderMode === UiNumberFieldSliderMode.VISIBLE);
		this.$wrapper.classList.toggle("slider-mode-visible-if-focused", sliderMode === UiNumberFieldSliderMode.VISIBLE_IF_FOCUSED);
		this.$wrapper.classList.toggle("slider-mode-slider-only", sliderMode === UiNumberFieldSliderMode.SLIDER_ONLY);
	}

	setSliderStep(sliderStep: number): void {
		this.sliderStep = sliderStep;
	}

	setCommitOnSliderChange(commitOnSliderChange: boolean): void {
		this.commitOnSliderChange = commitOnSliderChange;
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiNumberField", UiNumberField);
