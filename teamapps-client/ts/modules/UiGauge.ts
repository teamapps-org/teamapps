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

import {UiGaugeCommandHandler, UiGaugeConfig} from "../generated/UiGaugeConfig";
import {UiGaugeOptionsConfig} from "../generated/UiGaugeOptionsConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {LinearGauge, RadialGauge} from "canvas-gauges";
import {debouncedMethod, DebounceMode} from "./util/debounce";
import {parseHtml} from "./Common";

export class UiGauge extends AbstractUiComponent<UiGaugeConfig> implements UiGaugeCommandHandler {
	private $main: HTMLElement;
	private gauge: LinearGauge;
	private value: number;

	constructor(config: UiGaugeConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiGauge" data-teamapps-id="${this.getId()}"><canvas></canvas></div>`);
		this.value = config.options.value;
		this.createGauge();
	}

	@executeWhenFirstDisplayed()
	private createGauge() {
		if (this.getWidth() > 0 && this.getHeight() > 0) {
			let options = this.createOptions(this._config.options);
			options.renderTo = this.$main.querySelector<HTMLElement>(":scope canvas");
			if (this._config.options.linearGauge) {
				this.gauge = new LinearGauge(options);
			} else {
				this.gauge = new RadialGauge(options);
			}
			this.gauge.draw();
		}
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	onResize(): void {
		if (this.getWidth() <= 0 || this.getHeight() <= 0) {
			return;
		}
		if (this.gauge == null) {
			this.createGauge();
		}
		this.gauge.update({
			width: this.getWidth(),
			height: this.getHeight(),
		} as any);
		this.gauge.draw();
	}

	@debouncedMethod(500, DebounceMode.BOTH)
	setValue(value: number): void {
		this.value = value;
		if (this.gauge != null) {
			this.gauge.value = value;
		}
	}

	setOptions(options: UiGaugeOptionsConfig): void {
		let options1 = this.createOptions(options);
		this.gauge.update(options1);

	}

	createOptions(options: UiGaugeOptionsConfig): any {
		let gaugeOptions = {} as any;
		if (typeof options.borderRadius !== "undefined") gaugeOptions.borderRadius = options.borderRadius;
		if (typeof options.barBeginCircle !== "undefined") gaugeOptions.barBeginCircle = options.barBeginCircle;
		if (typeof options.colorBarEnd !== "undefined") gaugeOptions.colorBarEnd = options.colorBarEnd;
		if (typeof options.colorBarProgressEnd !== "undefined") gaugeOptions.colorBarProgressEnd = options.colorBarProgressEnd;
		if (typeof options.tickSide !== "undefined") gaugeOptions.tickSide = options.tickSide;
		if (typeof options.needleSide !== "undefined") gaugeOptions.needleSide = options.needleSide;
		if (typeof options.numberSide !== "undefined") gaugeOptions.numberSide = options.numberSide;
		if (typeof options.ticksWidth !== "undefined") gaugeOptions.ticksWidth = options.ticksWidth;
		if (typeof options.ticksWidthMinor !== "undefined") gaugeOptions.ticksWidthMinor = options.ticksWidthMinor;
		if (typeof options.ticksPadding !== "undefined") gaugeOptions.ticksPadding = options.ticksPadding;
		if (typeof options.barLength !== "undefined") gaugeOptions.barLength = options.barLength;
		if (typeof options.ticksAngle !== "undefined") gaugeOptions.ticksAngle = options.ticksAngle;
		if (typeof options.startAngle !== "undefined") gaugeOptions.startAngle = options.startAngle;
		if (typeof options.colorNeedleCircleOuter !== "undefined") gaugeOptions.colorNeedleCircleOuter = options.colorNeedleCircleOuter;
		if (typeof options.colorNeedleCircleOuterEnd !== "undefined") gaugeOptions.colorNeedleCircleOuterEnd = options.colorNeedleCircleOuterEnd;
		if (typeof options.colorNeedleCircleInner !== "undefined") gaugeOptions.colorNeedleCircleInner = options.colorNeedleCircleInner;
		if (typeof options.colorNeedleCircleInnerEnd !== "undefined") gaugeOptions.colorNeedleCircleInnerEnd = options.colorNeedleCircleInnerEnd;
		if (typeof options.needleCircleSize !== "undefined") gaugeOptions.needleCircleSize = options.needleCircleSize;
		if (typeof options.needleCircleInner !== "undefined") gaugeOptions.needleCircleInner = options.needleCircleInner;
		if (typeof options.needleCircleOuter !== "undefined") gaugeOptions.needleCircleOuter = options.needleCircleOuter;
		if (typeof options.animationTarget !== "undefined") gaugeOptions.animationTarget = options.animationTarget;
		if (typeof options.useMinPath !== "undefined") gaugeOptions.useMinPath = options.useMinPath;

		if (typeof options.minValue !== "undefined") gaugeOptions.minValue = options.minValue;
		if (typeof options.maxValue !== "undefined") gaugeOptions.maxValue = options.maxValue;
		if (typeof options.value !== "undefined") gaugeOptions.value = options.value;
		if (typeof options.units !== "undefined") gaugeOptions.units = options.units;
		if (typeof options.exactTicks !== "undefined") gaugeOptions.exactTicks = options.exactTicks;
		if (typeof options.majorTicks !== "undefined") gaugeOptions.majorTicks = options.majorTicks;
		if (typeof options.minorTicks !== "undefined") gaugeOptions.minorTicks = options.minorTicks;
		if (typeof options.strokeTicks !== "undefined") gaugeOptions.strokeTicks = options.strokeTicks;
		if (typeof options.animatedValue !== "undefined") gaugeOptions.animatedValue = options.animatedValue;
		if (typeof options.animateOnInit !== "undefined") gaugeOptions.animateOnInit = options.animateOnInit;
		if (typeof options.title !== "undefined") gaugeOptions.title = options.title;
		if (typeof options.borders !== "undefined") gaugeOptions.borders = options.borders;
		if (typeof options.numbersMargin !== "undefined") gaugeOptions.numbersMargin = options.numbersMargin;
		if (typeof options.valueInt !== "undefined") gaugeOptions.valueInt = options.valueInt;
		if (typeof options.valueDec !== "undefined") gaugeOptions.valueDec = options.valueDec;
		if (typeof options.majorTicksInt !== "undefined") gaugeOptions.majorTicksInt = options.majorTicksInt;
		if (typeof options.majorTicksDec !== "undefined") gaugeOptions.majorTicksDec = options.majorTicksDec;
		if (typeof options.animation !== "undefined") gaugeOptions.animation = options.animation;
		if (typeof options.animationDuration !== "undefined") gaugeOptions.animationDuration = options.animationDuration;
		if (typeof options.animationRule !== "undefined") gaugeOptions.animationRule = options.animationRule;
		if (typeof options.colorPlate !== "undefined") gaugeOptions.colorPlate = options.colorPlate;
		if (typeof options.colorPlateEnd !== "undefined") gaugeOptions.colorPlateEnd = options.colorPlateEnd;
		if (typeof options.colorMajorTicks !== "undefined") gaugeOptions.colorMajorTicks = options.colorMajorTicks;
		if (typeof options.colorMinorTicks !== "undefined") gaugeOptions.colorMinorTicks = options.colorMinorTicks;
		if (typeof options.colorTitle !== "undefined") gaugeOptions.colorTitle = options.colorTitle;
		if (typeof options.colorUnits !== "undefined") gaugeOptions.colorUnits = options.colorUnits;
		if (typeof options.colorNumbers !== "undefined") gaugeOptions.colorNumbers = options.colorNumbers;
		if (typeof options.colorNeedle !== "undefined") gaugeOptions.colorNeedle = options.colorNeedle;
		if (typeof options.colorNeedleEnd !== "undefined") gaugeOptions.colorNeedleEnd = options.colorNeedleEnd;
		if (typeof options.colorValueText !== "undefined") gaugeOptions.colorValueText = options.colorValueText;
		if (typeof options.colorValueTextShadow !== "undefined") gaugeOptions.colorValueTextShadow = options.colorValueTextShadow;
		if (typeof options.colorBorderShadow !== "undefined") gaugeOptions.colorBorderShadow = options.colorBorderShadow;
		if (typeof options.colorBorderOuter !== "undefined") gaugeOptions.colorBorderOuter = options.colorBorderOuter;
		if (typeof options.colorBorderOuterEnd !== "undefined") gaugeOptions.colorBorderOuterEnd = options.colorBorderOuterEnd;
		if (typeof options.colorBorderMiddle !== "undefined") gaugeOptions.colorBorderMiddle = options.colorBorderMiddle;
		if (typeof options.colorBorderMiddleEnd !== "undefined") gaugeOptions.colorBorderMiddleEnd = options.colorBorderMiddleEnd;
		if (typeof options.colorBorderInner !== "undefined") gaugeOptions.colorBorderInner = options.colorBorderInner;
		if (typeof options.colorBorderInnerEnd !== "undefined") gaugeOptions.colorBorderInnerEnd = options.colorBorderInnerEnd;
		if (typeof options.colorValueBoxRect !== "undefined") gaugeOptions.colorValueBoxRect = options.colorValueBoxRect;
		if (typeof options.colorValueBoxRectEnd !== "undefined") gaugeOptions.colorValueBoxRectEnd = options.colorValueBoxRectEnd;
		if (typeof options.colorValueBoxBackground !== "undefined") gaugeOptions.colorValueBoxBackground = options.colorValueBoxBackground;
		if (typeof options.colorValueBoxShadow !== "undefined") gaugeOptions.colorValueBoxShadow = options.colorValueBoxShadow;
		if (typeof options.colorNeedleShadowUp !== "undefined") gaugeOptions.colorNeedleShadowUp = options.colorNeedleShadowUp;
		if (typeof options.colorNeedleShadowDown !== "undefined") gaugeOptions.colorNeedleShadowDown = options.colorNeedleShadowDown;
		if (typeof options.colorBarStroke !== "undefined") gaugeOptions.colorBarStroke = options.colorBarStroke;
		if (typeof options.colorBar !== "undefined") gaugeOptions.colorBar = options.colorBar;
		if (typeof options.colorBarProgress !== "undefined") gaugeOptions.colorBarProgress = options.colorBarProgress;
		if (typeof options.colorBarShadow !== "undefined") gaugeOptions.colorBarShadow = options.colorBarShadow;
		if (typeof options.fontNumbers !== "undefined") gaugeOptions.fontNumbers = options.fontNumbers;
		if (typeof options.fontTitle !== "undefined") gaugeOptions.fontTitle = options.fontTitle;
		if (typeof options.fontUnits !== "undefined") gaugeOptions.fontUnits = options.fontUnits;
		if (typeof options.fontValue !== "undefined") gaugeOptions.fontValue = options.fontValue;
		if (typeof options.fontTitleSize !== "undefined") gaugeOptions.fontTitleSize = options.fontTitleSize;
		if (typeof options.fontValueSize !== "undefined") gaugeOptions.fontValueSize = options.fontValueSize;
		if (typeof options.fontUnitsSize !== "undefined") gaugeOptions.fontUnitsSize = options.fontUnitsSize;
		if (typeof options.fontNumbersSize !== "undefined") gaugeOptions.fontNumbersSize = options.fontNumbersSize;
		if (typeof options.fontTitleWeight !== "undefined") gaugeOptions.fontTitleWeight = options.fontTitleWeight;
		if (typeof options.fontValueWeight !== "undefined") gaugeOptions.fontValueWeight = options.fontValueWeight;
		if (typeof options.fontUnitsWeight !== "undefined") gaugeOptions.fontUnitsWeight = options.fontUnitsWeight;
		if (typeof options.fontNumbersWeight !== "undefined") gaugeOptions.fontNumbersWeight = options.fontNumbersWeight;
		if (typeof options.needle !== "undefined") gaugeOptions.needle = options.needle;
		if (typeof options.needleShadow !== "undefined") gaugeOptions.needleShadow = options.needleShadow;
		if (typeof options.needleType !== "undefined") gaugeOptions.needleType = options.needleType;
		if (typeof options.needleStart !== "undefined") gaugeOptions.needleStart = options.needleStart;
		if (typeof options.needleEnd !== "undefined") gaugeOptions.needleEnd = options.needleEnd;
		if (typeof options.needleWidth !== "undefined") gaugeOptions.needleWidth = options.needleWidth;
		if (typeof options.borderOuterWidth !== "undefined") gaugeOptions.borderOuterWidth = options.borderOuterWidth;
		if (typeof options.borderMiddleWidth !== "undefined") gaugeOptions.borderMiddleWidth = options.borderMiddleWidth;
		if (typeof options.borderInnerWidth !== "undefined") gaugeOptions.borderInnerWidth = options.borderInnerWidth;
		if (typeof options.borderShadowWidth !== "undefined") gaugeOptions.borderShadowWidth = options.borderShadowWidth;
		if (typeof options.valueBox !== "undefined") gaugeOptions.valueBox = options.valueBox;
		if (typeof options.valueBoxWidth !== "undefined") gaugeOptions.valueBoxWidth = options.valueBoxWidth;
		if (typeof options.valueBoxStroke !== "undefined") gaugeOptions.valueBoxStroke = options.valueBoxStroke;
		if (typeof options.valueText !== "undefined") gaugeOptions.valueText = options.valueText;
		if (typeof options.valueTextShadow !== "undefined") gaugeOptions.valueTextShadow = options.valueTextShadow;
		if (typeof options.valueBoxBorderRadius !== "undefined") gaugeOptions.valueBoxBorderRadius = options.valueBoxBorderRadius;
		if (typeof options.highlights !== "undefined") gaugeOptions.highlights = options.highlights;
		if (typeof options.highlightsWidth !== "undefined") gaugeOptions.highlightsWidth = options.highlightsWidth;
		if (typeof options.barWidth !== "undefined") gaugeOptions.barWidth = options.barWidth;
		if (typeof options.barStrokeWidth !== "undefined") gaugeOptions.barStrokeWidth = options.barStrokeWidth;
		if (typeof options.barProgress !== "undefined") gaugeOptions.barProgress = options.barProgress;
		if (typeof options.barShadow !== "undefined") gaugeOptions.barShadow = options.barShadow;

		return gaugeOptions;
	}


}

TeamAppsUiComponentRegistry.registerComponentClass("UiGauge", UiGauge);
