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


import * as d3 from "d3";
import {Simulation} from "d3";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiNetworkGraph_NodeClickedEvent, UiNetworkGraphCommandHandler, UiNetworkGraphConfig, UiNetworkGraphEventSource} from "../generated/UiNetworkGraphConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";
import {UiNetworkNodeConfig} from "../generated/UiNetworkNodeConfig";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {TeamAppsEvent} from "./util/TeamAppsEvent";

export class UiNetworkGraph extends AbstractUiComponent<UiNetworkGraphConfig> implements UiNetworkGraphCommandHandler, UiNetworkGraphEventSource{

	public readonly onNodeClicked: TeamAppsEvent<UiNetworkGraph_NodeClickedEvent> = new TeamAppsEvent(this);

	private $graph: HTMLElement;
	private svg: any;
	private svgContainer: any;
	private rect: any;
	private container: any;
	private node: any;
	private link: any;
	private simulation: Simulation<UiNetworkNodeConfig & any, undefined>;
	private zoom: any;
	private linkedByIndex: any = {};
	private toggle = 0;

	constructor(config: UiNetworkGraphConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$graph = parseHtml('<div class="UiNetworkGraph" id="' + this.getId() + '">');

		this.createGraph(config.gravity, config.images, config.nodes, config.links);
	}

	public getMainDomElement(): HTMLElement {
		return this.$graph;
	}

	@executeWhenFirstDisplayed()
	public createGraph(gravity: any, images: any, nodesData: any, linksData: any) {
		let me = this;

		this.simulation = d3.forceSimulation<UiNetworkNodeConfig & any>()
			.force("charge", d3.forceManyBody().distanceMax(1000))
			.nodes(nodesData)
			.force("link", d3.forceLink(linksData).id(d => (d as UiNetworkNodeConfig).id))
			.force("center", d3.forceCenter(this.getWidth() / 2, this.getWidth() / 2));

		if (this.svgContainer) {
			this.logger.debug("remove svg-container");
			this.svgContainer.remove();
		}

		this.zoom = d3.zoom()
			.extent([[0, 0], [this.getWidth(), this.getHeight()]])
			.scaleExtent([.1, 8])
			.on("zoom", () => {
				this.container.attr("transform", d3.event.transform);
			});

		this.logger.debug("Add svg:" + "#" + this.getId() + ":" + d3.select("#" + this.getId()));
		this.svgContainer = d3.select(this.getMainDomElement()).append("svg")
			.attr("width", this.getWidth())
			.attr("height", this.getHeight());
		this.svg = this.svgContainer
			.append("g")
			.call(this.zoom);

		this.rect = this.svg.append("rect")
			.attr("width", this.getWidth())
			.attr("height", this.getHeight())
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
			.call(d3.drag()
				.on("start", (d: any, i: number, nodes: Element[]) => {
					console.log("start");
					if (!d3.event.active) {
						this.simulation.alphaTarget(0.3).restart();
					}
					d3.select(nodes[i]).raise();

					d.fx = d.x;
					d.fy = d.y;

					this.container.attr("cursor", "grabbing");
				})
				.on("drag", (d: any, i: number, nodes: Element[]) => {
					console.log("drag");
					d.fx = d3.event.x;
					d.fy = d3.event.y;
				})
				.on("end", (d: any) => {
					console.log("end");
					if (!d3.event.active) {
						this.simulation.alphaTarget(0);
					}
					if (!d.fixed) {
						d.fx = null;
						d.fy = null;
					}
					this.container.attr("cursor", "grab")
				})
			);

		var circle = this.node.append("circle")
			.attr("r", (d: any) => {
				return d.size * 1;
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
				return d.size + 4;
			})
			.text((d: any) => {
				return d.caption
			});


		this.simulation.on("tick", () => {
			console.log("tick");
			this.link.attr("d", (d: any) => {
				var dx = d.target.x - d.source.x,
					dy = d.target.y - d.source.y,
					dr = Math.sqrt(dx * dx + dy * dy);
				return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
			});

			// this.node
			// 	.attr("cx", (d: any) => d.x)
			// 	.attr("cy", (d: any) => d.y);
			this.node.attr("transform", (d: any) => {
				return "translate(" + d.x + "," + d.y + ")";
			});
		});


		this.simulation.on("end", () => {
			console.log("end");
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


		this.node.on("mouseenter", function (d: any) {

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
				.attr("r", d.size * 1.5);

			d3.select(this).select("text").transition().duration(500)
				.attr("x", (d: UiNetworkNodeConfig) => {
					return d.size * 1.5 + 4;
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

		this.node.on("mouseleave", function (d: any) {
			me.node.classed("node-active", false);
			me.link.classed("link-active", false);
			d3.select(this).select("circle").transition()
				.duration(750)
				.attr("r", d.size * 1);

			d3.select(this).select("text").transition().duration(750)
				.attr("x", (d: UiNetworkNodeConfig) => d.size + 4)
				.style("font-size", "10px")
				.style("stroke", "black");


			me.link.transition().duration(1000).style("opacity", 1);
			me.node.transition().duration(500).style("opacity", 1);

		});

		this.node.on('click', (d: any, i: number, nodes: Element[]) => {
			var id = d.nodeId;
			this.onNodeClicked.fire({
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

			d.fixed = !d.fixed;
			if (d.fixed) {
				d.fx = d.x;
				d.fy = d.y;
			} else {
				d.fx = null;
				d.fy = null;
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
		// if (this.force) {
		// 	this.force.size([width, height]).resume();
		// }
	}

	public setZoomFactor(zoomFactor: number): void {
		this.zoom.scale(zoomFactor);
		this.logger.debug(zoomFactor);
	}

	public setGravity(gravity: number): void {
		// this.force.gravity(gravity).start();
		this.logger.debug(gravity);
	}

	public setDistance(distance: number, overrideNodeCharge: boolean): void {
		// this.force.distance(distance).start();
		this.logger.debug("distance:" + distance);
	}

	public setCharge(charge: number, overrideNodeCharge: boolean): void {
		// this.force.charge(charge).start();
		this.logger.debug("charge:" + charge);
	}

	public destroy(): void {
		// nothing to do
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiNetworkGraph", UiNetworkGraph);
