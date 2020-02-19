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
import * as noUiSlider from "nouislider";
import {UiField} from "./UiField";
import {UiSliderConfig, UiSliderCommandHandler, UiSliderEventSource} from "../../generated/UiSliderConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {UiColorConfig} from "../../generated/UiColorConfig";
import {generateUUID, humanReadableFileSize, parseHtml} from "../Common";
import {createUiColorCssString} from "../util/CssFormatUtil";

const wNumb: (options: wNumb.Options) => wNumb.Instance = require('wnumb');


export class UiSlider extends UiField<UiSliderConfig, number> implements UiSliderEventSource, UiSliderCommandHandler {

	private uuid: string;
	private $main: HTMLElement;
	private $style: HTMLElement;
	private $slider: HTMLInputElement;
	private slider: noUiSlider.noUiSlider;

	private min: number | undefined;
	private max: number | undefined;
	private step: number | undefined;
	private displayedDecimals: number | undefined;
	private tooltipPrefix: string | undefined;
	private tooltipPostfix: string | undefined;
	private humanReadableFileSize: boolean | undefined;

	protected initialize(config: UiSliderConfig, context: TeamAppsUiContext) {
		this.uuid = generateUUID();
		this.$main = parseHtml(`<div class="UiSlider" data-uuid="${this.uuid}">
	<style></style>
	<div class="slider"></div>
</div>`);
		this.$style = this.$main.querySelector<HTMLElement>(':scope style');
		this.$slider = this.$main.querySelector<HTMLInputElement>(':scope .slider');

		this.min = config.min;
		this.max = config.max;
		this.step = config.step;
		this.displayedDecimals = config.displayedDecimals;
		this.tooltipPrefix = config.tooltipPrefix;
		this.tooltipPostfix = config.tooltipPostfix;
		this.humanReadableFileSize = config.humanReadableFileSize;

		noUiSlider.create(this.$slider, this.createNoUiSliderOptions());
		this.slider = (this.$slider as any).noUiSlider;
		this.slider.on('set', () => this.commit());

		const $handle = this.$slider.querySelector<HTMLElement>(':scope .noUi-handle');
		$handle.addEventListener('keydown', (e) => {
			const value = Number(this.slider.get());
			if (e.which === 37) {
				this.slider.set(value - this.step);
			} else if (e.which === 39) {
				this.slider.set(value + this.step);
			}
		});

		this.setSelectionColor(config.selectionColor);
	}

	isValidData(v: number): boolean {
		return v == null || typeof v === "number";
	}

	private createNoUiSliderOptions(): noUiSlider.Options {
		return {
			start: [this.min],
			tooltips: this.humanReadableFileSize ? {
				to: (num: number) => (this.tooltipPrefix || "") + humanReadableFileSize(num, true) + (this.tooltipPostfix || "")
			} : wNumb({
				decimals: this.displayedDecimals,
				thousand: this._context.config.thousandsSeparator,
				mark: this._context.config.decimalSeparator,
				prefix: this.tooltipPrefix,
				postfix: this.tooltipPostfix
			}),
			step: this.step,
			connect: [true, false],
			range: {
				'min': this.min,
				'max': this.max
			}
		};
	}

	protected displayCommittedValue(): void {
		this.slider.set(this.getCommittedValue());
	}

	getDefaultValue(): number {
		return this._config.min;
	}

	getFocusableElement(): HTMLElement {
		return this.$slider.querySelector<HTMLElement>(':scope .noUi-handle');
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	getTransientValue(): number {
		return parseFloat(this.slider.get() as string);
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		if (editingMode === UiFieldEditingMode.DISABLED || editingMode === UiFieldEditingMode.READONLY) {
			this.$slider.disabled = true;
		} else if (editingMode === UiFieldEditingMode.EDITABLE || editingMode === UiFieldEditingMode.EDITABLE_IF_FOCUSED) {
			this.$slider.disabled = false;
		}
	}

	valuesChanged(v1: number, v2: number): boolean {
		return v1 !== v2;
	}

	public setSelectionColor(selectionColor: UiColorConfig) {
		this.$style.innerHTML = `[data-uuid="${this.uuid}"] .noUi-connect {
			background-color: ${createUiColorCssString(selectionColor)};
		}`;
	}

	setDisplayedDecimals(displayedDecimals: number): void {
		this.displayedDecimals = displayedDecimals;
		this.slider.updateOptions(this.createNoUiSliderOptions(), false);
	}

	setMax(max: number): void {
		this.max = max;
		this.slider.updateOptions(this.createNoUiSliderOptions(), false);
	}

	setMin(min: number): void {
		this.min = min;
		this.slider.updateOptions(this.createNoUiSliderOptions(), false);
	}

	setStep(step: number): void {
		this.step = step;
		this.slider.updateOptions(this.createNoUiSliderOptions(), false);
	}

	setTooltipPostfix(tooltipPostfix: string): void {
		this.tooltipPostfix = tooltipPostfix;
		this.slider.updateOptions(this.createNoUiSliderOptions(), false);
	}

	setTooltipPrefix(tooltipPrefix: string): void {
		this.tooltipPrefix = tooltipPrefix;
		this.slider.updateOptions(this.createNoUiSliderOptions(), false);
	}

	setHumanReadableFileSize(humanReadableFileSize: boolean): void {
		this.humanReadableFileSize = humanReadableFileSize;
		this.slider.updateOptions(this.createNoUiSliderOptions(), false);
	}

}

TeamAppsUiComponentRegistry.registerFieldClass("UiSlider", UiSlider);
