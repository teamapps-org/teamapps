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
import {Axis, NamespaceLocalObject} from "d3";
import {SVGSelection} from "./Charting";
import {ScaleContinuousNumeric, scaleLog, ScaleLogarithmic} from "d3-scale";
import {UiScaleType} from "../../generated/UiScaleType";
import {yTickFormat} from "./UiTimeGraph";
import d3 = require("d3");

function isLogScale(scale: ScaleContinuousNumeric<unknown, unknown>): scale is ScaleLogarithmic<unknown, unknown> {
	return typeof ((scale as any).base) === "function"
		|| typeof ((scale as any).constant) === "function";
}

export class YAxis {

	private $axis: SVGSelection<any>;
	private axis: Axis<number | { valueOf(): number }>;
	private scale: ScaleContinuousNumeric<number, number>;

	constructor(
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
		this.updateTickFormat();

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

	private updateTickFormat() {
		let availableHeight = Math.abs(this.scale.range()[1] - this.scale.range()[0]);
		let minY = this.scale.domain()[0];
		let maxY = this.scale.domain()[1];
		let delta = maxY - minY;
		let numberOfYTickGroups = Math.log10(delta) + 1;
		let heightPerYTickGroup = availableHeight / numberOfYTickGroups;

		this.scale instanceof scaleLog
		if (isLogScale(this.scale)) {
			this.axis.tickFormat((value: number) => {
				if (value < 1) {
					return d3.format("-,.2r")(value);
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
					return d3.format("-,.3r")(domainValue)
				} else {
					return yTickFormat(domainValue)
				}
			})
				.ticks(availableHeight / 20);
		}
	}
}
