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
import {ScaleContinuousNumeric, ScaleTime} from "d3-scale";
import {fakeZeroIfLogScale, SVGSelection} from "./Charting";
import {UiLineChartYScaleZoomMode} from "../../generated/UiLineChartYScaleZoomMode";
import * as d3 from "d3";
import {Axis, NamespaceLocalObject} from "d3";
import {AbstractUiLineChartDataDisplayConfig} from "../../generated/AbstractUiLineChartDataDisplayConfig";
import {TimeGraphDataStore} from "./TimeGraphDataStore";
import {UiScaleType} from "../../generated/UiScaleType";
import {yTickFormat} from "./UiTimeGraph";

export interface UiLineChartDataDisplay<C extends AbstractUiLineChartDataDisplayConfig = AbstractUiLineChartDataDisplayConfig> {

	setConfig(config: C): void;
	getDataSeriesIds(): string[];
	getMainSelection(): SVGSelection<any>;

	updateZoomX(zoomLevelIndex: number, scaleX: ScaleTime<number, number>): void;

	getYAxis(): YAxis | null;

	setYRange(range: [number, number]): void;

	redraw():void;
	destroy(): void;

}

export class YAxis {

	private $axis: SVGSelection<any>;
	private axis: Axis<number | { valueOf(): number }>;
	private scale: ScaleContinuousNumeric<number, number>;

	constructor(
		private scaleType: UiScaleType,
		private color: string
	) {
		this.$axis = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement);
		this.axis = d3.axisLeft(null);
	}

	getSelection(): SVGSelection<any> {
		return this.$axis;
	}

	getWidth(): number {
		return 33;

	}

	setScale(scaleY: ScaleContinuousNumeric<number, number>) {
		this.scale = scaleY;
		this.axis.scale(scaleY);
		this.updateTickFormat();
	}

	public draw() {
		this.$axis.call(this.axis);

		let $ticks = this.$axis.node().querySelectorAll('.tick');
		for (let i = 0; i < $ticks.length; i++) {
			let $text: SVGTextElement = $ticks[i].querySelector("text");
			let querySelector: any = $ticks[i].querySelector('line');
			if ($text.innerHTML === '') {
				querySelector.setAttribute("visibility", 'hidden');
			} else {
				querySelector.setAttribute("visibility", 'visible');
			}
		}

		this.$axis.style("color", this.color);
	}

	public setScaleType(value: UiScaleType) {
		this.scaleType = value;
		this.updateTickFormat();
	}

	private updateTickFormat() {
		let availableHeight = Math.abs(this.scale.range()[1] - this.scale.range()[0]);
		let minY = this.scale.domain()[0];
		let maxY = this.scale.domain()[1];
		let delta = maxY - minY;
		let numberOfYTickGroups = Math.log10(delta) + 1;
		let heightPerYTickGroup = availableHeight / numberOfYTickGroups;

		if (this.scaleType === UiScaleType.LOG10) {
			this.axis.tickFormat((value: number) => {
				if (value < 1) {
					return "";
				} else {
					if (heightPerYTickGroup >= 150) {
						return yTickFormat(value);
					} else if (heightPerYTickGroup >= 80) {
						let firstDigitOfValue = Number(("" + value)[0]);
						return firstDigitOfValue <= 5 ? yTickFormat(value) : "";
					} else if (heightPerYTickGroup >= 30) {
						let firstDigitOfValue = Number(("" + value)[0]);
						return firstDigitOfValue === 1 || firstDigitOfValue === 5 ? yTickFormat(value) : "";
					} else {
						let firstDigitOfValue = Number(("" + value)[0]);
						return firstDigitOfValue === 1 ? yTickFormat(value) : "";
					}
				}
			});
		} else {
			this.axis.tickFormat((domainValue: number) => {
				if (delta < 2) {
					return d3.format("-,.4r")(domainValue)
				} else {
					return yTickFormat(domainValue)
				}
			})
				.ticks(availableHeight / 20);
		}
	}
}