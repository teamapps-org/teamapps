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
///<reference path="../custom-declarations/d3v3.d.ts"/>


import * as d3 from "d3v3";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiNetworkGraphConfig} from "../generated/UiNetworkGraphConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";

export class UiNetworkGraph extends UiComponent<UiNetworkGraphConfig> {

	private $graph: HTMLElement;
	private svg: any;
	private svgContainer: any;
	private rect: any;
	private container: any;
	private node: any;
	private link: any;
	private force: any;
	private zoom: any;
	private linkedByIndex:any = {};
	private toggle = 0;

	constructor(config: UiNetworkGraphConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$graph = parseHtml('<div class="UiNetworkGraph" id="' + this.getId() + '">');

		var width = $(this.$graph).width();
		var height = $(this.$graph).height();

		this.logger.debug("width:" + width + ", height:" + height);
		if (width < 100) {
			width = 300;
		}
		if (height < 100) {
			height = 300;
		}

		this.createGraph(width, height, config.gravity, config.images, config.nodes, config.links);
	}

	public getMainDomElement(): HTMLElement {
		return this.$graph;
	}

	protected onAttachedToDom(): void {
		this.reLayout();
	}

	public createGraph(width: any, height: any, gravity: any, images: any, nodesData: any, linksData: any) {
		let me = this;

		let color = d3.scale.category20();

		this.force = d3.layout.force<any>()
			.charge((d) => d.charge)
			.linkDistance((d) => (d as any).distance)
			.gravity(gravity)
			.size([width, height]);


		this.zoom = d3.behavior.zoom().translate([500, 500]).scale(.1)
			.scaleExtent([0.02, 50])
			.on("zoom", function () {
				me.container.attr("transform", "translate(" + (d3.event as d3.ZoomEvent).translate + ")scale(" + (d3.event as d3.ZoomEvent).scale + ")");
			});

		let drag = d3.behavior.drag<any>()
			.origin((d) => d)
			.on("dragstart", function (d) {
				(d3.event as d3.BaseEvent).sourceEvent.stopPropagation();
				d3.select(this).classed("dragging", true);
				//test:
				//d3.select(this).classed("fixed", d.fixed = true);
				d3.select(this).classed("fixed", true);
				me.force.start();
			})
			.on("drag", function (d) {
				d3.select(this).attr("cx", d.x = (d3.event as DragEvent).x).attr("cy", d.y = (d3.event as DragEvent).y);
			})
			// .on("dblclick", function(d) {
			//     d3.select(this).classed("fixed", d.fixed = false);
			// })
			.on("dragend", function (d) {
				d3.select(this).classed("dragging", false);
			});


		if (this.svgContainer) {
			this.logger.debug("remove svg-container");
			this.svgContainer.remove();
		}

		//this.svg = d3.select("#" + this.getId())
		//    .append("div")
		//    .classed("svg-container", true) //container class to make it responsive
		//    .append("svg")
		//    //responsive SVG needs these 2 attributes and no width and height attr
		//    .attr("preserveAspectRatio", "xMinYMin meet")
		//    .attr("viewBox", "0 0 600 400")
		//    //class to make it responsive
		//    .classed("svg-content-responsive", true)
		//    .append("g")
		//    .call(zoom);


		//this.svg = d3.select("#" + this.getId()).append("svg")
		//    .attr("width", width)
		//    .attr("height", height)
		//    .append("g")
		//    .call(zoom);

		this.logger.debug("Add svg:" + "#" + this.getId() + ":" + d3.select("#" + this.getId()));
		this.svgContainer = d3.select(this.getMainDomElement()).append("svg")
			.attr("width", width)
			.attr("height", height);
		this.svg = this.svgContainer
			.append("g")
			.call(this.zoom);

		this.rect = this.svg.append("rect")
			.attr("width", width)
			.attr("height", height)
			.style("fill", "none")
			.style("pointer-events", "all");

		this.container = this.svg.append("g").attr("transform", "translate(500,500)scale(.1,.1)");

		var defs = this.svg.append("defs").attr("id", "imgdefs");
		for (let i = 0; i < images.length; i++) {
			var img = images[i];
			defs.append("pattern").attr("id", img.imageId)
				.attr("height", 1)
				.attr("width", 1)
				.attr("viewBox", "0 0 20 20")
				.attr("x", "0")
				.attr("y", "0")
				.append("image")
				.attr("x", 0).attr("y", 0)
				.attr("preserveAspectRatio", "none")
				.attr("height", 20)
				.attr("width", 20)
				.attr("xlink:href", img.image);

		}

		this.force.nodes(nodesData).links(linksData).start();

		this.link = this.container.append("g")
			.attr("class", "links")
			.selectAll(".link")
			.data(linksData)
			.enter().append("path")
			.attr("class", "link")
			.style("stroke-width", (d: any) => {
				if (d.width && d.width > 0) {
					return d.width;
				} else {
					return 1.5;
				}
			})
			.style("stroke", (d: any) => {
				if (d.color) {
					return d.color;
				} else {
					return "#555";
				}
			});

		this.node = this.container.append("g")
			.attr("class", "nodes")
			.selectAll(".node")
			.data(nodesData)
			.enter().append("g")
			.attr("class", "node")
			.attr("cx", (d: any) => {
				return d.x;
			})
			.attr("cy", (d: any) => {
				return d.y;
			})
			.call(drag);

		var circle = this.node.append("circle")
		//.attr("r", (d)=> { return d.weight + (d.size / 2); })
			.attr("r", (d: any) => {
				return (d.size + Math.sqrt(d.weight)) * 1;
			})
			.style("fill", (d: any) => {
				if (d.imageId) {
					return "url('#" + d.imageId + "')";
				} else {
					return color((1 / d.rating) as any);
				}
			})
			.style("stroke-width", (d: any) => {
				if (d.border) {
					return d.border;
				} else {
					return 0;
				}
			})
			.style("stroke", (d: any) => {
				if (d.borderColor) {
					return d.borderColor;
				} else {
					return "none";
				}
			});

		//this.svg.append("rect")
		//    .attr("x", 10)
		//    .attr("y", 10)
		//    .attr("width", 50)
		//    .attr("height", 25)
		//    .on("click", (d,i) => {
		//        if (this.force.gravity() < 0.3) {
		//            this.force.gravity(0.3).resume();
		//        } else {
		//            this.force.gravity(0.05).resume();
		//        }
		//    });

		this.node.append("text")
			.attr("x", (d: any) => {
				return (d.size + Math.sqrt(d.weight)) * 1 + 4;
			})
			.text((d: any) => {
				return d.caption
			});


		var tickCount = 0;
		var tickRate = 3;
		this.force.on("tick", () => {
			tickCount++;
			if (tickCount > 30) {
				if (tickRate > 0) {
					tickRate--;
				} else {
					tickRate = 3;
					this.link.attr("d", (d: any) => {
						var dx = d.target.x - d.source.x,
							dy = d.target.y - d.source.y,
							dr = Math.sqrt(dx * dx + dy * dy);
						return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
					});
					this.node.attr("transform", (d: any) => {
						return "translate(" + d.x + "," + d.y + ")";
					});
				}
			}
		});


		this.force.on("end", () => {
			this.link.attr("d", (d: any) => {
				var dx = d.target.x - d.source.x,
					dy = d.target.y - d.source.y,
					dr = Math.sqrt(dx * dx + dy * dy);
				return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
			});
		});

		linksData.forEach((d: any) => {
			this.linkedByIndex[d.source.index + "," + d.target.index] = 1;
		});


		this.node.on("mouseover", function (d: any) {

			me.node.classed("node-active", function (o: any) {
				let thisOpacity = me.isConnected(d, o) ? true : false;
				this.setAttribute('fill-opacity', thisOpacity);
				return thisOpacity;
			});

			me.link.classed("link-active", (o: any) => {
				return o.source === d || o.target === d ? true : false;
			});

			d3.select(this).classed("node-active", true);
			d3.select(this).select("circle").transition()
				.duration(500)
				.attr("r", (d.size + Math.sqrt(d.weight)) * 1.5);

			d3.select(this).select("text").transition().duration(500)
				.attr("x", (d) => {
					return (d.size + Math.sqrt(d.weight)) * 1.5 + 4;
				})
				.style("font-size", "20px")
				.style("stroke", "red");

			d = (d3.select(this).node() as any).__data__;

			me.link.transition().duration(250).style("opacity", function (o: any) {
				return d.index == o.source.index || d.index == o.target.index ? 1 : 0.3;
			});
			me.node.transition().duration(750).style("opacity", function (o: any) {
				return me.isConnected(d, o) ? 1 : 0.2;
			});

		});

		this.node.on("mouseout", function (d: any) {
			me.node.classed("node-active", false);
			me.link.classed("link-active", false);
			d3.select(this).select("circle").transition()
				.duration(750)
				.attr("r", (d.size + Math.sqrt(d.weight)) * 1);

			d3.select(this).select("text").transition().duration(750)
				.attr("x", (d) => (d.size + Math.sqrt(d.weight)) * 1 + 4)
				.style("font-size", "10px")
				.style("stroke", "black");


			me.link.transition().duration(1000).style("opacity", 1);
			me.node.transition().duration(500).style("opacity", 1);

		});

		this.node.on('click', function () {
			let d = (d3.select(this).node() as any).__data__;
			var id = d.nodeId;
			this._context.fireEvent({
				nodeId: id
			});

			var evt = d3.event;
			if (evt != null) {
				if ((evt as MouseEvent).shiftKey) {
					this.connectedNodes();
				} else if ((evt as MouseEvent).ctrlKey) {

				} else {
					this.logger.debug(id);
				}
			}
		});

	}


	private connectedNodes() {
		if (this.toggle == 0) {
			let d = (d3.select(this as any).node() as any).__data__;
			this.node.style("opacity", function (o: any) {
				return this.isConnected(d, o) ? 1 : 0.1;
			});
			this.link.style("opacity", function (o: any) {
				return d.index == o.source.index || d.index == o.target.index ? 1 : 0.1;
			});
			this.toggle = 1;
		} else {
			this.node.style("opacity", 1);
			this.link.style("opacity", 1);
			this.toggle = 0;
		}
	}

	private isConnected(a: any, b: any) {
		if (a.index == b.index) return true;
		return this.linkedByIndex[a.index + "," + b.index] || this.linkedByIndex[b.index + "," + a.index];
	}

	private dottype(d: any) {
		d.x = +d.x;
		d.y = +d.y;
		return d;
	}

	public onResize(): void {
		var width = $(this.$graph).width();
		var height = $(this.$graph).height();

		this.logger.debug("resize w:" + width + ", h:" + height);

		this.svgContainer.attr('width', width).attr('height', height);
		this.rect.attr('width', width).attr('height', height);
		this.container.attr('width', width).attr('height', height);
		if (this.force) {
			this.force.size([width, height]).resume();
		}
	}

	public setZoomFactor(zoomFactor: number): void {
		this.zoom.scale(zoomFactor);
		this.logger.debug(zoomFactor);
	}

	public setGravity(gravity: number): void {
		this.force.gravity(gravity).start();
		this.logger.debug(gravity);
	}

	public setDistance(distance: number, overrideNodeCharge: boolean): void {
		this.force.distance(distance).start();
		this.logger.debug("distance:" + distance);
	}

	public setCharge(charge: number, overrideNodeCharge: boolean): void {
		this.force.charge(charge).start();
		this.logger.debug("charge:" + charge);
	}

	public destroy(): void {
		// nothing to do
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiNetworkGraph", UiNetworkGraph);
