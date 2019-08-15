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
import {Simulation, SimulationLinkDatum} from "d3";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {
	UiNetworkGraph_NodeClickedEvent,
	UiNetworkGraph_NodeExpandedOrCollapsedEvent,
	UiNetworkGraphCommandHandler,
	UiNetworkGraphConfig,
	UiNetworkGraphEventSource
} from "../generated/UiNetworkGraphConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";
import {UiNetworkNode_ExpandState, UiNetworkNodeConfig} from "../generated/UiNetworkNodeConfig";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {patternify} from "./UiTreeGraph";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {UiTreeGraphNodeImage_CornerShape} from "../generated/UiTreeGraphNodeImageConfig";

export class UiNetworkGraph extends AbstractUiComponent<UiNetworkGraphConfig> implements UiNetworkGraphCommandHandler, UiNetworkGraphEventSource {

	public readonly onNodeClicked: TeamAppsEvent<UiNetworkGraph_NodeClickedEvent> = new TeamAppsEvent(this);
	public readonly onNodeExpandedOrCollapsed: TeamAppsEvent<UiNetworkGraph_NodeExpandedOrCollapsedEvent> = new TeamAppsEvent(this);

	private $graph: HTMLElement;
	private svg: d3.Selection<SVGGElement, any, null, undefined>;
	private svgContainer: d3.Selection<SVGElement, any, null, undefined>;
	private rect: any;
	private container: d3.Selection<SVGGElement, any, null, undefined>;
	private linksContainer: d3.Selection<SVGGElement, any, null, undefined>;
	private nodeEnter: any;
	private linkEnter: any;
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
		this.simulation = d3.forceSimulation<UiNetworkNodeConfig & any>()
			.force("charge", d3.forceManyBody())
			.nodes(nodesData)
			.force("link", d3.forceLink(linksData)
				.id(d => (d as UiNetworkNodeConfig).id)
				.distance((link: SimulationLinkDatum<UiNetworkNodeConfig & any>) => {
					let number1 = Math.max(link.source.width, link.source.height);
					let number2 = Math.max(link.target.width, link.target.height);
					let number = (number1 + number2) * 0.75;
					console.log(number1, number2, number);
					return number;
				}))
			.force("center", d3.forceCenter(this.getWidth() / 2, this.getWidth() / 2))
			.force("collide", d3.forceCollide((a: UiNetworkNodeConfig & any) => {
				return Math.sqrt(a.width * a.width + a.height * a.height) * .6;
			}));

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

		this.svgContainer = d3.select(this.getMainDomElement())
			.append("svg");
		this.svg = this.svgContainer
			.append("g")
			.call(this.zoom)
			.on("dblclick.zoom", null); // disable doubleclick zoom!

		this.rect = this.svg.append("rect")
			.attr("width", this.getWidth())
			.attr("height", this.getHeight())
			.style("fill", "none")
			.style("pointer-events", "all");

		this.container = this.svg.append("g");

		console.log(this.getWidth());
		this.zoom.translateBy(this.svg, this.getWidth() / 2, this.getHeight() / 2);

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


		this.linksContainer = this.container.append("g")
			.attr("class", "links")
			.attr("stroke", "#999");

		this.simulation.on("tick", () => {
			this.renderLinks(linksData);
			this.renderNodes(nodesData);
		});
	}

	public onResize(): void {
		this.rect.attr('width', this.getWidth()).attr('height', this.getHeight());
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

	private renderLinks(linksData: any[]) {
		const link = this.linksContainer.selectAll("line")
			.data(linksData)
			.join(
				enter => enter.append("line"),
				update => update
					.attr("x1", (d: any) => d.source.x)
					.attr("y1", (d: any) => d.source.y)
					.attr("x2", (d: any) => d.target.x)
					.attr("y2", (d: any) => d.target.y)
			);
	}


	private renderNodes(nodesData: any[]): void {
		const nodesSelection: d3.Selection<SVGGElement, any, SVGGElement, any> = this.container.selectAll<SVGGElement, any>('g.node')
			.data(nodesData, ({
				                  id
			                  }: UiNetworkNodeConfig) => id);

		// Enter any new nodes at the parent's previous position.
		const nodeEnter = nodesSelection.enter().append('g')
			.attr('class', 'node')
			.attr('cursor', 'pointer')
			.on('mousedown', (d: UiNetworkNodeConfig) => {
				console.log("click");
				if (d3.event.srcElement.classList.contains('node-button-circle')) {
					return;
				}
				this.onNodeClicked.fire({nodeId: d.id});
			});
		// Add background rectangle for the nodes
		patternify(nodeEnter, {
			tag: 'rect',
			selector: 'node-rect',
			data: (d: UiNetworkNodeConfig) => [d]
		});

		nodeEnter.call(d3.drag()
			.on("start", (d: any, i: number, nodes: Element[]) => {
				if (!d3.event.active) {
					this.simulation.alphaTarget(0.3).restart();
				}
				d3.select(nodes[i]).raise();

				d.fx = d.x;
				d.fy = d.y;

				this.container.attr("cursor", "grabbing");
			})
			.on("drag", (d: any, i: number, nodes: Element[]) => {
				d.fx = d3.event.x;
				d.fy = d3.event.y;
			})
			.on("end", (d: any) => {
				if (!d3.event.active) {
					this.simulation.alphaTarget(0);
				}
				// if (!d.fixed) {
				// 	d.fx = null;
				// 	d.fy = null;
				// }
				this.container.attr("cursor", "grab")
			})
		);


		// Add node icon image inside node
		patternify(nodeEnter, {
			tag: 'image',
			selector: 'node-icon-image',
			data: (d: UiNetworkNodeConfig) => d.icon ? [d] : []
		})
			.attr('width', (data: UiNetworkNodeConfig) => data.icon.size)
			.attr('height', (data: UiNetworkNodeConfig) => data.icon.size)
			.attr("xlink:href", (data: UiNetworkNodeConfig) => this._context.getIconPath(data.icon.icon, data.icon.size))
			.attr('x', (data: UiNetworkNodeConfig) => -data.width / 2 - data.icon.size / 2)
			.attr('y', (data: UiNetworkNodeConfig) => -data.height / 2 - data.icon.size / 2);

		// Defined node images wrapper group
		const imageGroups = patternify(nodeEnter, {
			tag: 'g',
			selector: 'node-image-group',
			data: (d: UiNetworkNodeConfig) => [d]
		});

		// Add background rectangle for node image
		patternify(imageGroups, {
			tag: 'rect',
			selector: 'node-image-rect',
			data: (d: UiNetworkNodeConfig) => [d]
		});

		// Node update styles
		const nodeUpdate = nodeEnter.merge(nodesSelection)
			.style('font', '12px sans-serif')
			.attr("transform", (d: any) => `translate(${d.x},${d.y})`);


		// Add foreignObject element inside rectangle
		const foreignObject = patternify(nodeUpdate, {
			tag: 'foreignObject',
			selector: 'node-foreign-object',
			data: (d: UiNetworkNodeConfig) => [d]
		});

		let foreignObjectInner = foreignObject.selectAll('.node-foreign-object-div')
			.data((d: UiNetworkNodeConfig) => [d], d => (d as UiNetworkNodeConfig).id);
		foreignObjectInner
			.enter()
			.append('xhtml:div')
			.classed('node-foreign-object-div', true)
			.html((d: UiNetworkNodeConfig) => this._context.templateRegistry.createTemplateRenderer(d.template).render(d.record.values));
		foreignObjectInner.exit().remove();

		this.restyleForeignObjectElements();

		// Add Node button circle's group (expand-collapse button)
		const nodeButtonGroups = patternify(nodeEnter, {
			tag: 'g',
			selector: 'node-button-g',
			data: (d: UiNetworkNodeConfig) => [d]
		})
			.on('mousedown', (d: UiNetworkNodeConfig) => {
				if (d.expandState == UiNetworkNode_ExpandState.EXPANDED) {
					d.expandState = UiNetworkNode_ExpandState.COLLAPSED;
				} else {
					d.expandState = UiNetworkNode_ExpandState.EXPANDED;
				}
				this.onNodeExpandedOrCollapsed.fire({nodeId: d.id, expanded: d.expandState == UiNetworkNode_ExpandState.EXPANDED});
			});

		// Add expand collapse button circle
		patternify(nodeButtonGroups, {
			tag: 'circle',
			selector: 'node-button-circle',
			data: (d: UiNetworkNodeConfig) => [d]
		});

		// Move images to desired positions
		nodeUpdate.selectAll('.node-image-group')
			.attr('transform', (d: UiNetworkNodeConfig) => {
				if (d.image) {
					let x = -d.image.width / 2 - d.width / 2;
					let y = -d.image.height / 2 - d.height / 2;
					return `translate(${x},${y})`
				} else {
					return null;
				}
			});

		// Style node image rectangles
		nodeUpdate.select('.node-image-rect')
			.attr('fill', (d: UiNetworkNodeConfig) => `url(#${d.id})`)
			.attr('width', (d: UiNetworkNodeConfig) => d.image && d.image.width)
			.attr('height', (d: UiNetworkNodeConfig) => d.image && d.image.height)
			.attr('stroke', (d: UiNetworkNodeConfig) => d.image && createUiColorCssString(d.image.borderColor))
			.attr('stroke-width', (d: UiNetworkNodeConfig) => d.image && d.image.borderWidth)
			.attr('rx', (d: UiNetworkNodeConfig) => d.image && (d.image.cornerShape == UiTreeGraphNodeImage_CornerShape.CIRCLE ? Math.max(d.image.width, d.image.height)
				: d.image.cornerShape == UiTreeGraphNodeImage_CornerShape.ROUNDED ? Math.min(d.image.width, d.image.height) / 10
					: 0))
			.attr('y', (d: UiNetworkNodeConfig) => d.image && d.image.centerTopDistance)
			.attr('x', (d: UiNetworkNodeConfig) => d.image && d.image.centerLeftDistance)
		// .attr('filter', (d: UiNetworkNodeConfig) => d.image && d.image.shadowId);

		// Style node rectangles
		nodeUpdate.select('.node-rect')
			.attr('width', (d: UiNetworkNodeConfig) => d.width)
			.attr('height', (d: UiNetworkNodeConfig) => d.height)
			.attr('x', (d: UiNetworkNodeConfig) => -d.width / 2)
			.attr('y', (d: UiNetworkNodeConfig) => -d.height / 2)
			.attr('rx', (d: UiNetworkNodeConfig) => d.borderRadius || 0)
			.attr('stroke-width', (d: UiNetworkNodeConfig) => d.borderWidth)
			.attr('cursor', 'pointer')
			.attr('stroke', ({borderColor}: UiNetworkNodeConfig) => createUiColorCssString(borderColor))
			.style("fill", ({backgroundColor}: UiNetworkNodeConfig) => createUiColorCssString(backgroundColor));

		// Move node button group to the desired position
		nodeUpdate.select('.node-button-g')
			.attr('transform', (d: UiNetworkNodeConfig) => `translate(0,${d.height / 2})`)
			.attr('display', (data: UiNetworkNodeConfig) => data.expandState === UiNetworkNode_ExpandState.NOT_EXPANDABLE ? 'none' : 'inherit');

		// Restyle node button circle
		nodeUpdate.select('.node-button-circle')
			.attr('r', 10)
			.attr('stroke-width', (d: UiNetworkNodeConfig) => d.borderWidth)
			.attr('fill', "white")
			.attr('stroke', (d: UiNetworkNodeConfig) => createUiColorCssString(d.borderColor));

		// Remove any exiting nodes after transition
		const nodeExitTransition = nodesSelection.exit()
			.remove();
	}


	restyleForeignObjectElements() {
		this.container.selectAll('.node-foreign-object')
			.attr('width', ({
				                width
			                }: UiNetworkNodeConfig) => width)
			.attr('height', ({
				                 height
			                 }: UiNetworkNodeConfig) => height)
			.attr('x', ({
				            width
			            }: UiNetworkNodeConfig) => -width / 2)
			.attr('y', ({
				            height
			            }: UiNetworkNodeConfig) => -height / 2);
		this.container.selectAll('.node-foreign-object-div')
			.style('width', ({
				                 width
			                 }: UiNetworkNodeConfig) => `${width}px`)
			.style('height', ({
				                  height
			                  }: UiNetworkNodeConfig) => `${height}px`)
			.select('*')
			.style('display', 'inline-grid')
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiNetworkGraph", UiNetworkGraph);
