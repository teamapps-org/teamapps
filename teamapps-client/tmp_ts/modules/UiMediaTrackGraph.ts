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
///<reference path="../custom-declarations/d3v3.d.ts"/>


import * as d3 from "d3v3";
import {AbstractComponent} from "teamapps-client-core";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {TeamAppsUiContext} from "teamapps-client-core";
import {UiMediaTrackGraph_HandleTimeSelectionEvent, UiMediaTrackGraphCommandHandler, DtoMediaTrackGraph, UiMediaTrackGraphEventSource} from "../generated/DtoMediaTrackGraph";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";

interface DataPoint {
	date: Date,
	value: number[]
}

interface Marker {
	track: number,
	start: Date,
	end: Date,
	color: string,
	bg: string
}

export class UiMediaTrackGraph extends AbstractComponent<DtoMediaTrackGraph> implements UiMediaTrackGraphCommandHandler, UiMediaTrackGraphEventSource {

	public readonly onHandleTimeSelection: TeamAppsEvent<UiMediaTrackGraph_HandleTimeSelectionEvent> = new TeamAppsEvent<UiMediaTrackGraph_HandleTimeSelectionEvent>();

	private static MARGINS = {top: 5, right: 5, bottom: 15, left: 5};

	private $graph: HTMLElement;
	private brush: any;
	private brushExtent: any;
	private x: d3.time.Scale<number, number>;
	private xExtent: [Date, Date];
	private trackCount: number;
	private areaElements: any;
	private lineElements: any;
	private areas: any;
	private lines: any;
	private xAxisElement: any;
	private svg: any;
	private xAxis: d3.svg.Axis;
	private markerGroup: any;
	private cursor: any;
	private audioUrl: any;

	private data: DataPoint[];
	private markerData: Marker[];


	constructor(config: DtoMediaTrackGraph) {
		super(config);
		this.$graph = parseHtml('<div class="UiMediaTrackGraph" id="' + this.getId() + '">');

		this.trackCount = config.trackCount;
		var data: DataPoint[] = [];
		for (let i = 0; i < config.trackData.length; i++) {
			var trackData = config.trackData[i];
			data[i] = {date: new Date(trackData.time), value: trackData.values};
		}
		this.data = data;
		var markers: Marker[] = [];
		for (let i = 0; i < config.markers.length; i++) {
			var marker = config.markers[i];
			markers[i] = {track: marker.track, start: new Date(marker.start), end: new Date(marker.end), color: marker.color, bg: marker.backgroundColor};
		}
		this.markerData = markers;

		//this.createAudioPlayer();
	}

	public doGetMainElement(): HTMLElement {
		return this.$graph;
	}

	private createBrush(w: number, h: number, data: DataPoint[], markers: Marker[], tracks: number) {
		var width = w - UiMediaTrackGraph.MARGINS.left - UiMediaTrackGraph.MARGINS.right;
		var height = h - UiMediaTrackGraph.MARGINS.top - UiMediaTrackGraph.MARGINS.bottom;
		console.debug("create graph:" + w + ":" + h + ", width:" + width + ", height:" + height + ", tracks:" + tracks);
		this.trackCount = tracks;
		this.x = d3.time.scale().range([0, width]);
		var y: d3.scale.Linear<number, number> = d3.scale.linear().range([height, 0]);
		this.xAxis = d3.svg.axis()
			.scale(this.x)
			.orient("bottom")
			.tickSize(3, 3);
		var yAxis: d3.svg.Axis = d3.svg.axis()
			.scale(y)
			.orient("left")
			.ticks(0)
			.tickSize(0, 0);

		this.xExtent = d3.extent(data.map((d: DataPoint) => {
			return d.date;
		}));
		this.x.domain(this.xExtent);
		y.domain([0, 200 * tracks]);

		var zoom: d3.behavior.Zoom<any /*TODO*/> = d3.behavior.zoom()
			.x(this.x as any)
			.scaleExtent([1, 100])
			.on("zoom", () => this.zoomFunc());

		this.brush = d3.svg.brush()
			.x(this.x as any)
			.on("brushend", () => this.brushedData());
		this.brushExtent = this.brush.extent();

		if (this.svg) {
			console.debug("remove old svg..");
			this.svg.remove();
		}

		console.debug("id:" + this.getId() + ", el:" + d3.select("#" + this.getId()));
		this.svg = d3.select("#" + this.getId()).append("svg")
			.attr("width", width + UiMediaTrackGraph.MARGINS.left + UiMediaTrackGraph.MARGINS.right)
			.attr("height", height + UiMediaTrackGraph.MARGINS.top + UiMediaTrackGraph.MARGINS.bottom);


		// filters go in defs element
		var defs = this.svg.append("defs");


		defs.append("clipPath")
			.attr("id", `clip-${this.getId()}`)
			.append("rect")
			.attr("width", width)
			.attr("height", height);

		// create filter with id #drop-shadow
		// height=130% so that the shadow is not clipped
		var filter = defs.append("filter")
			.attr("id", "drop-shadow")
			.attr("height", "110%");

		// SourceAlpha refers to opacity of graphic that this filter will be applied to
		// convolve that with a Gaussian with standard deviation 3 and store result
		// in blur
		filter.append("feGaussianBlur")
			.attr("in", "SourceAlpha")
			.attr("stdDeviation", 0.9)
			.attr("result", "blur");

		// translate output of Gaussian blur to the right and downwards with 2px
		// store result in offsetBlur
		filter.append("feOffset")
			.attr("in", "blur")
			.attr("dx", 0.3)
			.attr("dy", 0.3)
			.attr("result", "offsetBlur");

		// overlay original SourceGraphic over translated blurred opacity by using
		// feMerge filter. Order of specifying inputs is important!
		var feMerge = filter.append("feMerge");

		feMerge.append("feMergeNode")
			.attr("in", "offsetBlur");
		feMerge.append("feMergeNode")
			.attr("in", "SourceGraphic");

		var gradient = this.svg
			.append("linearGradient")
			.attr("y1", 0)
			.attr("y2", height)
			.attr("x1", "0")
			.attr("x2", "0")
			.attr("id", "gradient")
			.attr("gradientUnits", "userSpaceOnUse");

		gradient.append("stop")
			.attr("offset", "0")
			.attr("stop-color", "rgba(73,128,192, 1.0)");

		gradient.append("stop")
			.attr("offset", "1.0")
			.attr("stop-color", "rgba(73,128,192, 0.0)");

		var focus = this.svg.append("g")
			.attr("class", "focus")
			.attr("transform", "translate(" + UiMediaTrackGraph.MARGINS.left + "," + UiMediaTrackGraph.MARGINS.top + ") scale(1, 1)");

		//var focus = this.svg.append("g")
		//    .attr("class", "focus")
		//    .attr("transform", "translate(" + margin.left + "," + margin.top + ") scale(1, 1)");
		//
		//focus.attr("transform", "translate(" + margin.left + "," + height + ") scale(0.75, 0)")
		//    .transition().duration(750)
		//    .attr("transform", "translate(" + margin.left + "," + margin.top + ") scale(1, 1)");


		this.markerGroup = focus.append("g");
		for (let i = 0; i < markers.length; i++) {
			var marker = markers[i];
			this.markerGroup.append("rect")
				.attr("x", this.x(marker.start))
				.attr("y", y(this.base(marker.track) + 95))
				.attr("width", this.x(marker.end) - this.x(marker.start))
				.attr("height", y((200 * tracks) - 190))
				.attr("class", "marker")
				.style("stroke", marker.color)
				.style("fill", marker.bg);
			console.debug("marker:" + marker.color + ", bg:" + marker.bg);
		}

		this.lines = [];
		this.lineElements = [];
		this.areas = [];
		this.areaElements = [];
		for (let i = 0; i < this.trackCount; i++) {
			for (let j = 0; j < 4; j++) {
				if (j < 2) {
					this.areas[(i * 2) + j] = d3.svg.area()
						.interpolate("monotone")
						.x((d) => this.x((<any>d).date))
						.y0(this.areaValue(y, this.base(i), i, j, true))
						.y1(this.areaValue(y, this.base(i), i, j, false));
					this.areaElements[(i * 2) + j] = focus.append("path")
						.datum(data)
						.attr("class", "area")
						.attr("d", this.areas[(i * 2) + j]);
					if (j == 1) {
						this.areaElements[(i * 2) + j].attr("class", "area2");
					}
				}
				this.lines[(i * 4) + j] = d3.svg.line()
					.interpolate("monotone")
					.x((d) => this.x((<any>d).date))
					//.y((d) => y((<any>d).value[(i * 4) + j] + this.base(i)));
					.y(this.lineValue(y, this.base(i), i, j));
				this.lineElements[(i * 4) + j] = focus.append("path")
					.datum(data)
					.attr("class", "line")
					.attr("d", this.lines[(i * 4) + j]);
				if (j == 2 || j == 3) {
					this.lineElements[(i * 4) + j].attr("class", "line2");
				}
			}
		}

		this.cursor = this.markerGroup.append("line")
			.attr("x1", this.x(new Date(1000 * 0)))
			.attr("x2", this.x(new Date(1000 * 0)))
			.attr("y1", y(0))
			.attr("y2", y(tracks * 200))
			.attr("class", "cursor");
		//console.debug(y(tracks * 200));
		//console.debug(y(0));


		this.xAxisElement = focus.append("g")
			.attr("class", "x axis")
			.attr("transform", "translate(0," + height + ")")
			.call(this.xAxis);

		var yAxisElement = focus.append("g")
			.attr("class", "y axis")
			.call(yAxis);


		var zoomBrush = focus.append("g")
			.attr("class", "x brush")
			.attr('id', 'brush')
			.call(zoom)
			.call(this.brush);

		zoomBrush
			.selectAll("rect")
			.attr("y", -6)
			.attr("height", height + 7);

		zoomBrush.on("mousedown.zoom", null);
		zoomBrush.on("touchstart.zoom", null);
	}

	private zoomFunc() {
		this.brushExtent = this.brush.extent();

		var xDomain = this.x.domain();
		if (xDomain[0] < this.xExtent[0]) xDomain[0] = this.xExtent[0];
		if (xDomain[1] > this.xExtent[1]) xDomain[1] = this.xExtent[1];
		this.x.domain(xDomain);
		this.markerGroup.attr("transform", "translate(" + (d3.event as d3.ZoomEvent).translate[0] + ", 0) scale(" + (d3.event as d3.ZoomEvent).scale + ", 1)");
		for (let i = 0; i < this.trackCount; i++) {
			for (let j = 0; j < 4; j++) {
				if (j < 2) {
					this.areaElements[(i * 2) + j].attr('d', this.areas[(i * 2) + j]);
				}
				this.lineElements[(i * 4) + j].attr('d', this.lines[(i * 4) + j]);
			}
		}
		this.xAxisElement.call(this.xAxis);
		this.brush.extent(this.brushExtent);
		this.svg.select(".brush").call(this.brush);

	}

	private areaValue(y: d3.scale.Linear<number, number>, base: number, i: number, j: number, first: boolean) {
		if (first) {
			return function (d: any) {
				return y(d.value[(i * 4) + (j * 2)] + base)
			}
		} else {
			return function (d: any) {
				return y(d.value[(i * 4) + (j * 2) + 1] + base)
			}
		}
	}

	private lineValue(y: d3.scale.Linear<number, number>, base: number, i: number, j: number) {
		return function (d: any) {
			return y(d.value[(i * 4) + j] + base)
		}
	}

	private base(track: number) {
		return 200 * track + 100;
	}

	private createAudioPlayer() {
		if (d3.select("audio")) d3.select("audio").remove();
		var audio = d3.select("body").append("audio");
		audio.attr("id", "audio");  // TODO @mb this is definitely going to fail with multiple players
		audio.attr("src", this.audioUrl);
		audio.attr("autoplay", true);
		var audioPlayer: HTMLMediaElement = document.getElementById('audio') as HTMLMediaElement; // TODO @mb this is definitely going to fail with multiple players
		audioPlayer.addEventListener('timeupdate', () => {
			this.setCursorPosition(audioPlayer.currentTime * 1000);
		});
	}

	private brushedData() {
		let start: number = 0;
		let end: number = 0;
		if (this.brush.empty( )) {
			if ((d3.event as d3.BaseEvent).sourceEvent) {
				start = end = this.x.invert(((d3.event as d3.BaseEvent).sourceEvent as MouseEvent).pageX - UiMediaTrackGraph.MARGINS.left - (this.getMainElement().getBoundingClientRect().left + document.body.scrollLeft)).getTime();
			}
		} else {
			start = (<Date><any>this.brush.extent()[0]).getTime();
			end = (<Date><any>this.brush.extent()[1]).getTime();
		}
		this.onHandleTimeSelection.fire({
			start: start,
			end: end
		});
	}

	public setCursorPosition(time: number) {
		if (this.cursor && time) {
			this.cursor.transition().duration(750).attr("x1", this.x(new Date(time)))
				.attr("x2", this.x(new Date(time)));
		}
		return new Date(time).getTime();
	}

	public onResize(): void {
		var width = $(this.$graph).width();
		var height = $(this.$graph).height();

		if (width > 0 && height > 0 && this.data) {
			this.createBrush(width, height, this.data, this.markerData, this.trackCount);
		}
	}

}


