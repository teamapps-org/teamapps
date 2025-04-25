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
import {Axis, NamespaceLocalObject} from "d3";
import {SVGSelection} from "./Charting";
import {ScaleContinuousNumeric, ScaleLogarithmic} from "d3-scale";

import * as d3 from "d3";

function isLogScale(scale: ScaleContinuousNumeric<unknown, unknown>): scale is ScaleLogarithmic<unknown, unknown> {
	return typeof ((scale as any).base) === "function"
		|| typeof ((scale as any).constant) === "function";
}

interface YAxisConfig {
	color: string,
	label: string,
	maxTickDigits: number
}

export class YAxis {

	private $axis: SVGSelection<any>;
	private $label: SVGSelection<SVGTextElement>;
	private axis: Axis<number | { valueOf(): number }>;
	private scale: ScaleContinuousNumeric<number, number>;

	constructor(private config: YAxisConfig) {
		this.$axis = d3.select(document.createElementNS((d3.namespace("svg:text") as NamespaceLocalObject).space, "g") as SVGGElement)
			.classed("axis", true)
			.classed("y-axis", true);
		this.$label = this.$axis.append<SVGTextElement>("text")
			.classed("label", true)
			.attr("x", 0)
			.attr("dy", -5)
			.attr("fill", "currentColor");
		this.scale = d3.scaleLinear(); // will be replaced...
		this.axis = d3.axisLeft(this.scale);
	}

	setConfig(config: YAxisConfig) {
		this.config = config;
		this.draw();
	}

	getSelection(): SVGSelection<any> {
		return this.$axis;
	}

	getWidth(): number {
		let fontSize = parseFloat(getComputedStyle(this.$axis.node()).fontSize);
		return 10 + (this.config.maxTickDigits + 1) * .6 * fontSize;
	}

	setScale(scaleY: ScaleContinuousNumeric<number, number>) {
		this.scale = scaleY;
		// Note that the scale is dynamic. the reference to scaleY is set, not its values, so changes to scaleY (e.g. for animations)
		// have an effect on the axis!
		this.axis.scale(scaleY);
		this.updateTickFormat();
	}

	public draw() {
		this.updateTickFormat();

		this.$label.text(this.config.label)
		this.$axis.call(this.axis as any);

		let $ticks = this.$axis.node().querySelectorAll('.tick');
		for (let i = 0; i < $ticks.length; i++) {
			let $text: SVGTextElement = $ticks[i].querySelector("text");
			let querySelector: any = $ticks[i].querySelector('line');
			if ($text.innerHTML === '') {
				querySelector.setAttribute("opacity", '.3');
			} else {
				querySelector.setAttribute("opacity", '1');
			}
		}

		this.$axis.style("color", this.config.color);
	}

	private updateTickFormat() {
		const tickFormat = d3.format(`-,.${this.config.maxTickDigits}~r`);
		const largeNumberTickFormat = d3.format(`-,.${this.config.maxTickDigits}~s`);

		let availableHeight = Math.abs(this.scale.range()[1] - this.scale.range()[0]);
		let minY = this.scale.domain()[0];
		let maxY = this.scale.domain()[1];
		const numberOfDisplayableTicks = Math.ceil(availableHeight / 20);

		// make sure we only display as many ticks that all ticks have different (displayed) values!
		let numberOfTicks = numberOfDisplayableTicks;
		if (minY != maxY) {
			while (numberOfTicks > 0) {
				const integerPartLen = maxTickIntegerPartLength(minY, maxY, numberOfTicks);
				const inc: number = d3.tickIncrement(minY, maxY, numberOfTicks);
				const numberOfDigitsAddedByTickIncrements = inc < 0 ? Math.ceil(Math.log10(-inc)) : 0;
				const numberOfSignificantDigits = integerPartLen + numberOfDigitsAddedByTickIncrements;
				if (integerPartLen >= this.config.maxTickDigits) {
					break;
				} else if (numberOfSignificantDigits > this.config.maxTickDigits) {
					console.debug(`Decreasing the number of ticks from ${numberOfTicks} to ${numberOfTicks - 1}`)
					numberOfTicks--;
				} else {
					break;
				}
			}
		}
		this.axis.ticks(numberOfTicks);

		this.axis.tickFormat((value: number) => {
			const log10 = Math.log10(Math.abs(value));
			let isPowerOfTen = value === 0 || Math.abs(Math.round(log10) - log10) < 1e-6;
			if (!isLogScale(this.scale) || numberOfTicks <= 4 || isPowerOfTen) {
				if (Math.ceil(log10) > this.config.maxTickDigits) {
					// If the integer part alone is taking more digits than allowed, fallback to SI notation. No way to make this fit...
					return largeNumberTickFormat(value);
				} else {
					return tickFormat(value)
				}
			} else {
				// do not display all numbers on logarithmic scales, since they tend to be displayed very narrowly...
				return "";
			}
		});
	}

}

export function maxTickIntegerPartLength(minY: number, maxY: number, numberOfTicks: number) {
	return Math.max(...d3.ticks(minY, maxY, numberOfTicks).map(t => Math.abs(t) < 1 ? 1 : Math.floor(Math.log10(Math.abs(t))) + 1));
}