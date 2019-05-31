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
 * =====

 */

import {UiComponent} from "./UiComponent";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiPieChart_DataPointClickedEvent, UiPieChartCommandHandler, UiPieChartConfig, UiPieChartEventSource} from "../generated/UiPieChartConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiChartNamedDataPointConfig} from "../generated/UiChartNamedDataPointConfig";
import * as d3 from "d3"
import {EventFactory} from "../generated/EventFactory";
import {UiColorConfig} from '../generated/UiColorConfig';
import {UiDataPointWeighting} from '../generated/UiDataPointWeighting';
import {UiChartLegendStyle} from "../generated/UiChartLegendStyle";

export class UiPieChart extends UiComponent<UiPieChartConfig> implements UiPieChartCommandHandler, UiPieChartEventSource {

	readonly onDataPointClicked: TeamAppsEvent<UiPieChart_DataPointClickedEvent> = new TeamAppsEvent(this);

	chart: Chart;
	config: UiPieChartConfig;


	constructor(config: UiPieChartConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.config = config;
		this.createChart();
	}

	createChart() {
		let htmlDivElement = document.createElement("div");

		//@ts-ignore
		this.chart = new Chart()
			.container(htmlDivElement)
			.data(this.config)
			.onDataPointClicked((name: string) => {
				this.onDataPointClicked.fire(EventFactory.createUiPieChart_DataPointClickedEvent(this.getId(), name));
			})
			.render();
	}

	getMainDomElement(): JQuery<HTMLElement> {
		return $(this.chart.container() as any);
	}

	onResize(): void {

		// Get our dimensions
		let newWidth = this.getWidth()
		let newHeight = this.getHeight()


		// Update chart dimensions
		this.chart.svgWidth(newWidth)
			.svgHeight(newHeight - 5)
			.render()

	}


	setDataPoints(dataPoints: UiChartNamedDataPointConfig[], animationDuration: number): void {
		this.config.dataPoints = dataPoints;
		this.chart
			.data(this.config)
			.duration(animationDuration)
			.render();
	}

	setDataPointWeighting(dataPointWeighting: UiDataPointWeighting): void {
		this.config.dataPointWeighting = dataPointWeighting;
		this.chart.data(this.config)
			.duration(this.config.initialAnimationDuration)
			.render();
	}

	setHeight3D(height3D: number): void {
		this.config.height3D = height3D;
		this.chart.data(this.config)
			.duration(this.config.initialAnimationDuration)
			.render();
	}

	setInnerRadiusProportion(innerRadiusProportion: number): void {
		this.config.innerRadiusProportion = innerRadiusProportion;
		this.chart.data(this.config)
			.duration(this.config.initialAnimationDuration)
			.render();
	}

	setLegendStyle(legendStyle: UiChartLegendStyle): void {
		this.config.legendStyle = legendStyle;
		this.chart.data(this.config)
			.duration(this.config.initialAnimationDuration)
			.render();
	}

	setRotation3D(rotation3D: number): void {
		this.config.rotation3D = rotation3D;
		this.chart.data(this.config)
			.duration(this.config.initialAnimationDuration)
			.render();
	}

	setRotationClockwise(rotationClockwise: number): void {
		this.config.rotationClockwise = rotationClockwise;
		this.chart.data(this.config)
			.duration(this.config.initialAnimationDuration)
			.render();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiPieChart", UiPieChart);


/*
This code is based on following convention:
https://github.com/bumbeishvili/d3-coding-conventions/blob/84b538fa99e43647d0d4717247d7b650cb9049eb/README.md
*/

// Mixins
interface Chart {
	getChartState: () => PieChartAttributes;

	svgWidth(): number,
	svgWidth(svgWidth: number): Chart,
	svgHeight(): number,
	svgHeight(svgHeight: number): Chart,
	marginTop(): number,
	marginTop(marginTop: number): Chart,
	marginBottom(): number,
	marginBottom(marginBottom: number): Chart,
	marginRight(): number,
	marginRight(marginRight: number): Chart,
	marginLeft(): number,
	marginLeft(marginLeft: number): Chart,
	container(): Element,
	container(container: Element): Chart,
	defaultTextFill(): string,
	defaultTextFill(defaultTextFill: string): Chart,
	defaultFont(): string,
	defaultFont(defaultFont: string): Chart,
	data(): UiPieChartConfig,
	data(data: UiPieChartConfig): Chart,
	duration(): number,
	duration(duration: number): Chart,
	firstRun(): boolean,
	firstRun(firstRun: boolean): Chart,
	onDataPointClicked(): (id: string) => void,
	onDataPointClicked(onDataPointClicked: (id: string) => void): Chart,
	rx(): number,
	rx(rx: number): Chart,
	ry(): number,
	ry(ry: number): Chart,
	h(): number,
	h(h: number): Chart,
	ir(): number
	ir(ir: number): Chart

}

class Chart {

	/** It feels so wrong declaring this variable
	 *  We don't use it, if we have used it, it won't be in static context
	 *  Basically we use this variable to make typescript compiler to not to complain about this._current usage in the static methods
	 *  where 'this' is bound externally using method.bind(this) syntax
	 */
	static _current: any;


	constructor() {
		// Exposed variables
		const attrs: PieChartAttributes = {
			id: 'ID' + Math.floor(Math.random() * 1000000), // Id for event handlings
			svgWidth: 400,
			svgHeight: 400,
			marginTop: 5,
			marginBottom: 5,
			marginRight: 5,
			marginLeft: 5,
			container: 'body',
			defaultTextFill: '#2C3E50',
			defaultFont: 'Helvetica',
			data: null,
			duration: 750,
			firstRun: true,
			onDataPointClicked: (name) => {
			},
			rx: null,
			ry: null,
			h: null,
			ir: null
		};

		this.getChartState = () => attrs;

		// Dynamically create accessor functions
		Object.keys(attrs).forEach((key) => {
			//@ts-ignore
			this[key] = function (_) {
				var string = `attrs['${key}'] = _`;
				if (!arguments.length) {
					return eval(`attrs['${key}'];`);
				}
				eval(string);
				return this;
			};

		});

		this.initializeEnterExitUpdatePattern();
	}

	// Make charts updatable
	initializeEnterExitUpdatePattern() {
		d3.selection.prototype.patternify = function ({selector, tag, data}: PatternifyParameter) {
			var container = this;
			data = data || [selector];
			// Pattern in action
			var selection = container.selectAll('.' + selector).data(data, (d: any, i: number) => {
					if (typeof d === 'object') {
						if (d.id) {
							return d.id;
						}
					}
					return i;
				});
			selection.exit().remove();
			selection = selection.enter().append(tag).merge(selection);
			selection.attr('class', selector);
			return selection;
		};
	}

	// Render elements
	render() {
		const attrs = this.getChartState();
		const self = this;
		attrs.marginBottom = 5 + attrs.data.height3D;

		// Drawing containers
		var container: any = d3.select(attrs.container);
		// var containerRect = container.node().getBoundingClientRect();
		// if (containerRect.width > 0) attrs.svgWidth = containerRect.width;

		//Calculated properties
		var calc: any = {
			id: null,
			chartTopMargin: null,
			chartLeftMargin: null,
			chartWidth: null,
			chartHeight: null
		};
		calc.id = 'ID' + Math.floor(Math.random() * 1000000); // id for event handlings
		calc.chartLeftMargin = attrs.marginLeft;
		calc.chartTopMargin = attrs.marginTop;
		calc.chartWidth = attrs.svgWidth - attrs.marginRight - calc.chartLeftMargin;
		calc.chartHeight = attrs.svgHeight - attrs.marginBottom - calc.chartTopMargin;
		calc.centerX = calc.chartWidth / 2;
		calc.centerY = calc.chartHeight / 2;
		attrs.calc = calc;

		// -----------------  LAYOUTS  ----------------
		const layouts: Layouts = {};
		layouts.pie = d3.pie().sort(null).value((d: any) => d.value);
		attrs.layouts = layouts;

		// ------------------ OVERRIDES & CONVERSATION ------------------
		const rx = Math.min(calc.chartWidth, calc.chartHeight * 90 / attrs.data.rotation3D) / 2;
		const ry = attrs.data.rotation3D / 90 * rx;
		const h = attrs.data.height3D;
		const ir = attrs.data.innerRadiusProportion;
		const rotation = attrs.data.rotationClockwise;
		attrs.rx = rx;
		attrs.ry = ry;
		attrs.h = h;
		attrs.ir = ir;
		attrs.rotation = rotation;

		// Converting data to desired format
		const convertedData = attrs.data.dataPoints.map(point => {
			const label = point.name;
			const color = Chart.colorToRGBAString(point.color);
			const value = point.y;
			return {
				label: label,
				color: color,
				value: value,
				type: 'main'
			}
		});


		// Check the weighting
		if (attrs.data.dataPointWeighting == UiDataPointWeighting.ABSOLUTE) {

			// Check if all values are  positive
			let everyValueIsPositive = convertedData.every(d => d.value >= 0);

			if (everyValueIsPositive) {

				//Check if sum of them is not more than 1
				const sum = d3.sum(convertedData, d => d.value);

				// Check if it;s correct weighting
				if (sum < 1) {
					// Attach dummy data element, which will be transparent
					convertedData.push({
						label: 'dummy',
						type: 'dummy',
						color: 'rgba(1,0,0,0)',
						value: 1 - sum
					})
				}

			}

		}

		// Storing sum of elements
		calc.sum = d3.sum(convertedData, d => d.value);

		// Generate data, which contains info about pie angles
		const pieData = layouts.pie(convertedData)
			.map((d: { startAngle: number, endAngle: number }) => {
				// Add configs  clockwise rotation, to the generated angles
				return Object.assign(d, {
					startAngle: d.startAngle + (Math.PI * 2 * (rotation % 360) / 360),
					endAngle: d.endAngle + (Math.PI * 2 * (rotation % 360) / 360),
				})
			})

		//Add svg
		var svg = container
			.patternify({
				tag: 'svg',
				selector: 'svg-chart-container'
			})
			.attr('width', attrs.svgWidth)
			.attr('height', attrs.svgHeight)
			.attr('font-family', attrs.defaultFont);

		//Add container g element
		var chart = svg
			.patternify({
				tag: 'g',
				selector: 'chart'
			})
			.attr('transform', 'translate(' + calc.chartLeftMargin + ',' + calc.chartTopMargin + ')');

		// Center point
		attrs.centerPoint = chart.patternify({
			tag: 'g',
			selector: 'center-point'
		})
			.attr('transform', `translate(${calc.centerX},${calc.centerY})`);


		// Draw pie
		this.draw(pieData, attrs);


		//#########################################  UTIL FUNCS ##################################
		d3.select(window).on('resize.' + attrs.id, () => {
			var containerRect = container.node().getBoundingClientRect();
			if (containerRect.width > 0) this.svgWidth(containerRect.width);
			this.render();
		});

		// Store state, whether app was first run or not
		attrs.firstRun = false;
		return this;
	}

	draw(
		data: PieDataObject[],
		{
			rx /*radius x*/,
			ry /*radius y*/,
			h /*height*/,
			ir /*inner radius*/
		}: PieChartAttributes
	) {

		const attrs = this.getChartState();
		const calc = attrs.calc;
		const self = this;

		// Placeholder data
		const _data = data;

		// Create Slices and shape containers
		var slices = attrs.centerPoint
			.patternify({
				tag: 'g',
				selector: 'slices'
			})

		// Store reference for func access
		attrs.slices = slices;

		const outerSliceWrapper = attrs.centerPoint
			.patternify({
				tag: 'g',
				selector: 'outerSliceWrapper'
			})

		const topSliceWrapper = attrs.centerPoint
			.patternify({
				tag: 'g',
				selector: 'topSliceWrapper'
			})


		// Creating inner slice custom paths
		const pieInners = slices
			.patternify({
				tag: 'path',
				selector: 'innerSlice',
				data: _data
			})
			.style("fill", function (d: PieDataObject) {
				return d3.hsl(d.data.color).darker(2).toString()
			})
			.attr("d", function (d: PieDataObject) {
				return Chart.pieInner(d, attrs);
			})
			.classed('slice-sort', true)
			.on('click', (d: PieDataObject) => self.onSliceClick(d))
			.on('mouseenter', (d: PieDataObject) => self.onSliceMouseEnter(d))
			.on('mouseleave', (d: PieDataObject) => self.onSliceMouseLeave(d))

		// Transition  inner  elements
		pieInners
			.transition()
			.duration(attrs.duration)
			.attrTween("d", function (d: PieDataObject) {
				return Chart.arcTweenInner.bind(this)(d, attrs)
			})
			.on('end', function (d: PieDataObject) {
				this._current = d;
			})

		// Create corner slice paths
		const cornerSliceElements = slices
			.patternify({
				tag: 'path',
				selector: 'cornerSlices',
				data: _data.map((d) => Object.assign({}, d))
			})
			.style("fill", (d: PieDataObject) => {
				return d3.hsl(d.data.color).darker(0.7).toString();
			})
			.attr("d", function (d: PieDataObject) {
				return Chart.pieCorner(d, attrs);
			})
			.classed('slice-sort', true)
			.attr('pointer-events', '')
			.style("stroke", function (d: PieDataObject) {
				return d3.hsl(d.data.color).darker(0.7).toString()
			})
			.on('click', (d: PieDataObject) => self.onSliceClick(d))
			.on('mouseenter', (d: PieDataObject) => self.onSliceMouseEnter(d))
			.on('mouseleave', (d: PieDataObject) => self.onSliceMouseLeave(d))
			.attr('opacity', (d: PieDataObject, i: number, arr: PieDataObject[]) => {
				if (d.endAngle > Math.PI && d.endAngle <= (Math.PI + Math.PI / 2)
				) {
					return 0;
				}
				if (arr.length - 2 == i) {
					return 1;
				}
				return 0;
			})

		// Store reference for function access
		attrs.cornerSliceElements = cornerSliceElements;

		// Transition corner elements
		cornerSliceElements
			.transition()
			.duration(attrs.duration)
			.attrTween("d", function (d: PieDataObject) {
				return Chart.arcTweenCorner.bind(this)(d, attrs)
			})
			.on('end', function (d: PieDataObject) {
				this._current = d;
			})

		// Create corner slice surface paths
		const cornerSliceSurfaceElements = slices
			.patternify({
				tag: 'path',
				selector: 'cornerSlicesSurface',
				data: _data.map((d) => Object.assign({}, d))
			})
			.style("fill", function (d: PieDataObject) {
				return d3.hsl(d.data.color).darker(0.7).toString()
			})
			.attr("d", function (d: PieDataObject) {
				return Chart.pieCornerSurface(d, attrs);
			})
			.classed('slice-sort', true)
			.style("stroke", function (d: PieDataObject) {
				return d3.hsl(d.data.color).darker(0.7).toString()
			})
			.on('click', (d: PieDataObject) => self.onSliceClick(d))
			.on('mouseenter', (d: PieDataObject) => self.onSliceMouseEnter(d))
			.on('mouseleave', (d: PieDataObject) => self.onSliceMouseLeave(d))
			.attr('opacity', (d: PieDataObject, i: number, arr: PieDataObject[]) => {
				if (d.startAngle < Math.PI / 2 ||
					d.startAngle > Math.PI * 3 / 2
				) {
					return 0;
				}
				if (0 == i) {
					console.log(d)
					return 1;
				}
				return 0;
			})

		// Store reference for function access
		attrs.cornerSliceSurfaceElements = cornerSliceSurfaceElements;

		// Transition corner Surface elements
		cornerSliceSurfaceElements
			.transition()
			.duration(attrs.duration)
			.attrTween("d", function (d: PieDataObject) {
				return Chart.arcTweenCornerSurface.bind(this)(d, attrs)
			})
			.on('end', function (d: PieDataObject) {
				this._current = d;
			})

		// Draw outer slices
		const outerSlices = outerSliceWrapper
			.patternify({
				tag: 'path',
				selector: 'outerSlice',
				data: _data
			})
			.style("fill", function (d: PieDataObject) {
				return d3.hsl(d.data.color).darker(0.7).toString()
			})
			.on('click', (d: PieDataObject) => self.onSliceClick(d))
			.on('mouseenter', (d: PieDataObject) => self.onSliceMouseEnter(d))
			.on('mouseleave', (d: PieDataObject) => self.onSliceMouseLeave(d))

		// Transition  outer   elements
		outerSlices.transition()
			.duration(attrs.duration)
			.attrTween("d", function (d: PieDataObject) {
				return Chart.arcTweenOuter.bind(this)(d, attrs)
			})
			.on('end', function (d: PieDataObject) {
				this._current = d;
			})

		// Draw top slices
		const topSlices = topSliceWrapper
			.patternify({
				tag: 'path',
				selector: 'topSlice',
				data: _data
			})
			.style("fill", function (d: PieDataObject) {
				return d.data.color;
			})
			.style("stroke", function (d: PieDataObject) {
				return d.data.color;
			})
			//	.attr("d", function (d: any) { return pieTop(d, rx, ry, ir); })
			.on('click', (d: PieDataObject) => self.onSliceClick(d))
			.on('mouseenter', (d: PieDataObject) => self.onSliceMouseEnter(d))
			.on('mouseleave', (d: PieDataObject) => self.onSliceMouseLeave(d))

		// Transition  top  elements
		topSlices.transition()
			.duration(attrs.duration)
			.attrTween("d", function (d: PieDataObject) {
				return Chart.arcTweenTop.bind(this)(d, attrs)
			})
			.on('end', function (d: PieDataObject) {
				this._current = d;
			})

		// Draw Texts
		const slicesTexts = topSliceWrapper
			.patternify({
				tag: 'text',
				selector: 'pie-labels',
				data: _data.map((d) => Object.assign({}, d))
			})
			.attr('text-anchor', 'middle')
			.attr('font-size', 10)
			.attr('transform', (d: PieDataObject) => {
				const centerAngle = ((d.startAngle + d.endAngle) / 2) % (Math.PI * 2);
				const x = rx * 0.8 * Math.cos(centerAngle);
				const y = ry * 0.8 * Math.sin(centerAngle);
				return `translate(${x},${y}) `
			})
			.text((d: PieDataObject) => d.data.label + ' (' + Math.round(d.value / calc.sum * 100) + '%)')
			.attr('opacity', (d: PieDataObject) => d.data.type == "dummy" ? -1 : 1)

		// Transition  text  elements
		slicesTexts.transition()
			.duration(attrs.duration)
			.attrTween("transform", function (d: PieDataObject) {
				return Chart.textTweenTransform.bind(this)(d, attrs)
			})

	}

	updateData(transitionTime: number) {

		return this;
	}

	onSliceClick(d: PieDataObject) {
		const attrs = this.getChartState();
		attrs.onDataPointClicked(d.data.label);
	}

	onSliceMouseEnter(d: PieDataObject) {

	}

	onSliceMouseLeave(d: PieDataObject) {

	}

	// This function converts RGBA color object to js compatible rgba string color
	static colorToRGBAString(color: UiColorConfig) {
		return `rgba(${color.red},${color.green},${color.blue},${color.alpha == undefined ? 0 : color.alpha})`
	}

	//********** Function is responsible for building outer  shape paths */
	static pieOuter(d: PieDataObject, {rx, ry, h, ir}: PieChartAttributes) {

		// Process corner Cases
		if (d.endAngle == Math.PI * 2 && d.startAngle > Math.PI && d.startAngle < Math.PI * 2) {
			return ""
		}
		if (d.startAngle > Math.PI * 3 && d.startAngle < Math.PI * 4 &&
			d.endAngle > Math.PI * 3 && d.endAngle <= Math.PI * 4) {
			return ""
		}

		// Reassign startAngle  and endAngle based on their positions
		var startAngle = d.startAngle;
		var endAngle = d.endAngle;
		if (d.startAngle > Math.PI && d.startAngle < Math.PI * 2) {
			startAngle = Math.PI;
			if (d.endAngle > Math.PI * 2) {
				startAngle = 0;
			}
		}
		if (d.endAngle > Math.PI && d.endAngle < Math.PI * 2) {
			endAngle = Math.PI;
		}
		if (d.startAngle > Math.PI * 2) {
			startAngle = d.startAngle % (Math.PI * 2);
		}
		if (d.endAngle > Math.PI * 2) {
			endAngle = d.endAngle % (Math.PI * 2);
			if (d.startAngle <= Math.PI) {
				endAngle = Math.PI;
				startAngle = 0
			}
		}
		if (d.endAngle > Math.PI * 3) {
			endAngle = Math.PI
		}
		if (d.startAngle < Math.PI && d.endAngle >= 2 * Math.PI) {
			endAngle = Math.PI;
			startAngle = d.startAngle
		}

		if (d.startAngle >= Math.PI && d.startAngle <= Math.PI * 2 &&
			d.endAngle >= Math.PI * 2 && d.endAngle <= Math.PI * 3) {
			startAngle = 0;
			endAngle = d.endAngle % (Math.PI * 2)
		}
		// Calculating shape key points
		var sx = rx * Math.cos(startAngle),
			sy = ry * Math.sin(startAngle),
			ex = rx * Math.cos(endAngle),
			ey = ry * Math.sin(endAngle);

		// Creating custom path  commands based on calculation
		var ret = [];
		ret.push("M", sx, h + sy, "A", rx, ry, "0 0 1", ex, h + ey, "L", ex, ey, "A", rx, ry, "0 0   0", sx, sy, "z");

		// If shape is big enough, that it needs two separate outer shape , then draw second shape as well
		if (d.startAngle < Math.PI && d.endAngle >= 2 * Math.PI) {
			startAngle = 0;
			endAngle = d.endAngle;
			var sx = rx * Math.cos(startAngle),
				sy = ry * Math.sin(startAngle),
				ex = rx * Math.cos(endAngle),
				ey = ry * Math.sin(endAngle);
			ret.push("M", sx, h + sy, "A", rx, ry, "0 0 1", ex, h + ey, "L", ex, ey, "A", rx, ry, "0 0   0", sx, sy, "z");
		}

		// Assemble shape commands
		return ret.join(" ");
	}


	static pieInner(d: PieDataObject, {
		rx,
		ry,
		h,
		ir
	}: PieChartAttributes) {

		// Normalize angles before we start any calculations
		var startAngle = (d.startAngle < Math.PI ? Math.PI : d.startAngle);
		var endAngle = (d.endAngle < Math.PI ? Math.PI : d.endAngle);

		// Take care of corner cases
		if (d.startAngle > Math.PI * 2 && d.endAngle < Math.PI * 3) {
			return "";
		}
		if (d.startAngle >= Math.PI * 2 && d.endAngle >= Math.PI * 2 && d.endAngle <= Math.PI * 3) {
			return "";
		}

		// Reassign startAngle  and endAngle based on their positions
		if (d.startAngle <= Math.PI && d.endAngle > Math.PI * 2) {
			startAngle = Math.PI;
			endAngle = 2 * Math.PI;
		}
		if (d.startAngle > Math.PI && d.endAngle >= Math.PI * 3) {
			endAngle = 2 * Math.PI;
		}
		if (d.startAngle > Math.PI && d.endAngle > Math.PI * 2 && d.endAngle < Math.PI * 3) {
			endAngle = 2 * Math.PI;
		}
		if (d.startAngle > Math.PI && d.startAngle < Math.PI * 2 && d.endAngle > Math.PI * 3) {
			endAngle = 2 * Math.PI;
			startAngle = Math.PI
		}
		if (d.startAngle > Math.PI && d.startAngle < Math.PI * 2 && d.endAngle > Math.PI * 3) {
			endAngle = 2 * Math.PI;
			startAngle = Math.PI
		}
		if (d.startAngle > Math.PI &&
			d.startAngle < Math.PI * 2 &&
			d.endAngle > Math.PI * 3) {
			startAngle = Math.PI;
			endAngle = Math.PI + d.endAngle % Math.PI;
		}
		if (d.startAngle > Math.PI * 2 &&
			d.startAngle < Math.PI * 3 &&
			d.endAngle > Math.PI * 3) {
			startAngle = Math.PI;
			endAngle = Math.PI + d.endAngle % Math.PI;
		}
		if (d.startAngle > Math.PI * 3 &&
			d.endAngle > Math.PI * 3) {
			startAngle = d.startAngle % (Math.PI * 2)
			endAngle = d.endAngle % (Math.PI * 2)
		}

		// Calculating shape key points
		var sx = ir * rx * Math.cos(startAngle),
			sy = ir * ry * Math.sin(startAngle),
			ex = ir * rx * Math.cos(endAngle),
			ey = ir * ry * Math.sin(endAngle);

		// Creating custom path  commands based on calculation
		var ret = [];
		ret.push("M", sx, sy, "A", ir * rx, ir * ry, "0 0 1", ex, ey, "L", ex, h + ey, "A", ir * rx, ir * ry, "0 0 0", sx, h + sy, "z");


		// If shape is big enough, that it needs two separate outer shape , then draw second shape as well
		if (d.startAngle > Math.PI &&
			d.startAngle < Math.PI * 2 &&
			d.endAngle > Math.PI * 3) {
			startAngle = d.startAngle % (Math.PI * 2);
			endAngle = Math.PI * 2;
			var sx = ir * rx * Math.cos(startAngle),
				sy = ir * ry * Math.sin(startAngle),
				ex = ir * rx * Math.cos(endAngle),
				ey = ir * ry * Math.sin(endAngle);
			ret.push("M", sx, sy, "A", ir * rx, ir * ry, "0 0 1", ex, ey, "L", ex, h + ey, "A", ir * rx, ir * ry, "0 0 0", sx, h + sy, "z");
		}

		// Assemble shape commands
		return ret.join(" ");
	}

	//********** Function is responsible for building top  shape paths */
	static pieTop(d: PieDataObject, {
		rx,
		ry,
		ir
	}: PieChartAttributes) {

		// If angles are equal, then we got nothing to draw
		if (d.endAngle - d.startAngle == 0) return "M 0 0";

		// Calculating shape key points
		var sx = rx * Math.cos(d.startAngle),
			sy = ry * Math.sin(d.startAngle),
			ex = rx * Math.cos(d.endAngle),
			ey = ry * Math.sin(d.endAngle);

		// Creating custom path based on calculation
		var ret = [];
		ret.push("M", sx, sy, "A", rx, ry, "0", (d.endAngle - d.startAngle > Math.PI ? 1 : 0), "1", ex, ey, "L", ir * ex, ir * ey);
		ret.push("A", ir * rx, ir * ry, "0", (d.endAngle - d.startAngle > Math.PI ? 1 : 0), "0", ir * sx, ir * sy, "z");
		return ret.join(" ");
	}

	//******* Function is responsible for building left corner shape paths */
	static pieCornerSurface(d: PieDataObject, {
		rx,
		ry,
		h,
		ir
	}: PieChartAttributes) {

		//  Calculating  left corner surface key points
		var sxFirst = ir * rx * Math.cos(d.startAngle);
		var syFirst = ir * ry * Math.sin(d.startAngle)
		var sxSecond = rx * Math.cos(d.startAngle);
		var sySecond = ry * Math.sin(d.startAngle);
		var sxThird = sxSecond;
		var syThird = sySecond + h;
		var sxFourth = sxFirst;
		var syFourth = syFirst + h;

		// Creating custom path based on calculation
		return `
			M ${sxFirst} ${syFirst} 
			L ${sxSecond} ${sySecond}
			L ${sxThird} ${syThird} 
			L ${sxFourth} ${syFourth}
			z
		`
	}

	//********** Function is responsible for building right corner shape paths */
	static pieCorner(d: PieDataObject, {
		rx,
		ry,
		h,
		ir
	}: PieChartAttributes) {

		//  Calculating  right corner surface key points
		var sxFirst = ir * rx * Math.cos(d.endAngle);
		var syFirst = ir * ry * Math.sin(d.endAngle);
		var sxSecond = rx * Math.cos(d.endAngle);
		var sySecond = ry * Math.sin(d.endAngle);
		var sxThird = sxSecond;
		var syThird = sySecond + h;
		var sxFourth = sxFirst;
		var syFourth = syFirst + h;

		// Creating custom path based on calculation
		return `
			M ${sxFirst} ${syFirst} 
			L ${sxSecond} ${sySecond}
			L ${sxThird} ${syThird} 
			L ${sxFourth} ${syFourth}
			z
			`
	}

	//Text  transitions
	static textTweenTransform(a: PieDataObject, {
		rx,
		ry
	}: PieChartAttributes) {

		if (!this._current) {
			this._current = Object.assign({}, a, {
				startAngle: 0,
				endAngle: 0
			})
		}
		var i = d3.interpolate(this._current, a);

		this._current = i(0);
		return function (t: any) {
			const d = i(t);
			const centerAngle = ((d.startAngle + d.endAngle) / 2) % (Math.PI * 2);
			const x = rx * 0.8 * Math.cos(centerAngle);
			const y = ry * 0.8 * Math.sin(centerAngle);
			return `translate(${x},${y}) `
		};
	}

	//Corner shape transitions
	static arcTweenCorner(a: PieDataObject, {
		rx,
		ry,
		h,
		ir
	}: PieChartAttributes) {
		if (!this._current) {
			this._current = Object.assign({}, a, {
				startAngle: 0,
				endAngle: 0
			})
		}
		var i = d3.interpolate(this._current, a);
		this._current = i(0);
		return function (t: any) {
			return Chart.pieCorner(i(t), {
				rx: rx,
				ry: ry,
				h: h,
				ir: ir
			});
		};
	}

	//Corner surface shape transitions
	static arcTweenCornerSurface(a: PieDataObject, {
		rx,
		ry,
		h,
		ir
	}: PieChartAttributes) {

		if (!this._current) {
			this._current = Object.assign({}, a, {
				startAngle: 0,
				endAngle: 0
			})
		}
		var i = d3.interpolate(this._current, a);
		this._current = i(0);
		return function (t: any) {
			return Chart.pieCornerSurface(i(t), {
				rx: rx,
				ry: ry,
				h: h,
				ir: ir
			});
		};
	}

	//Inner shape transitions
	static arcTweenInner(a: PieDataObject, {
		rx,
		ry,
		h,
		ir
	}: PieChartAttributes) {
		if (!this._current) {
			this._current = Object.assign({}, a, {
				startAngle: 0,
				endAngle: 0
			})
		}
		var i = d3.interpolate(this._current, a);
		this._current = i(0);
		return function (t: any) {
			return Chart.pieInner(i(t), {
				rx: rx,
				ry: ry,
				h: h,
				ir: ir
			});
		};
	}

	//Top shape transitions
	static arcTweenTop(a: PieDataObject, {
		rx,
		ry,
		ir
	}: PieChartAttributes) {
		if (!this._current) {
			this._current = Object.assign({}, a, {
				startAngle: 0,
				endAngle: 0
			})
		}
		var i = d3.interpolate(this._current, a);
		this._current = i(0);
		return function (t: any) {
			return Chart.pieTop(i(t), {
				rx,
				ry,
				ir
			});
		};
	}

	//Outer shape transitions
	static arcTweenOuter(a: PieDataObject, {
		rx,
		ry,
		h,
		ir
	}: PieChartAttributes) {
		if (!this._current) {
			this._current = Object.assign({}, a, {
				startAngle: 0,
				endAngle: 0
			})
		}
		var i = d3.interpolate(this._current, a);
		this._current = i(0);
		return function (t: any) {
			return Chart.pieOuter(i(t), {
				rx,
				ry,
				h,
				ir
			});
		};
	}


}

export interface Layouts {
	[key: string]: Function
}

export interface PieDataObject {
	value: number,
	startAngle: number,
	endAngle: number,
	data: any
}

export interface PatternifyParameter {
	selector: string,
	tag: string,
	data?: any
}

export interface PieChartAttributes {
	[key: string]: any,

	data?: UiPieChartConfig,
	svgWidth?: number,
	svgHeight?: number,
	marginTop?: number,
	marginBottom?: number,
	marginRight?: number,
	marginLeft?: number,
	container?: any,
	defaultTextFill?: string,
	defaultFont?: string,
	onDataPointClicked?: (name: string) => void,
	rx?: number,
	ry?: number,
	h?: number,
	ir?: number
}