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
///<reference path="../custom-declarations/d3v3.d.ts"/>


import * as d3 from "d3";
import {ForceLink, Simulation, SimulationLinkDatum, ZoomBehavior} from "d3";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {
	UiNetworkGraph_NodeClickedEvent,
	UiNetworkGraph_NodeDoubleClickedEvent,
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
import {UiNetworkLinkConfig} from "../generated/UiNetworkLinkConfig";

export class UiNetworkGraph extends AbstractUiComponent<UiNetworkGraphConfig> implements UiNetworkGraphCommandHandler, UiNetworkGraphEventSource {

	public readonly onNodeClicked: TeamAppsEvent<UiNetworkGraph_NodeClickedEvent> = new TeamAppsEvent(this);
	public readonly onNodeDoubleClicked: TeamAppsEvent<UiNetworkGraph_NodeDoubleClickedEvent> = new TeamAppsEvent(this);

	public readonly onNodeExpandedOrCollapsed: TeamAppsEvent<UiNetworkGraph_NodeExpandedOrCollapsedEvent> = new TeamAppsEvent(this);

	private $graph: HTMLElement;
	private svg: d3.Selection<SVGElement, any, null, undefined>;
	private pointerEventsRect: d3.Selection<SVGRectElement, any, null, undefined>;
	private container: d3.Selection<SVGGElement, any, null, undefined>;
	private linksContainer: d3.Selection<SVGGElement, any, null, undefined>;
	private zoom: ZoomBehavior<Element, unknown>;

	private simulation: Simulation<UiNetworkNodeConfig & any, undefined>;
	private linkForce: ForceLink<UiNetworkNodeConfig & any, UiNetworkLinkConfig & any>;

	private nodes: (UiNetworkNodeConfig & any)[];
	private links: (UiNetworkLinkConfig & any)[];


	constructor(config: UiNetworkGraphConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$graph = parseHtml('<div class="UiNetworkGraph" id="' + this.getId() + '">');

		this.links = config.links;
		this.nodes = config.nodes;

		this.createGraph(config.gravity, config.images);
	}

	public doGetMainElement(): HTMLElement {
		return this.$graph;
	}

	@executeWhenFirstDisplayed()
	public createGraph(gravity: any, images: any) {
		this.linkForce = d3.forceLink(this.links)
			.id(d => (d as UiNetworkNodeConfig).id)
			.distance((link: SimulationLinkDatum<UiNetworkNodeConfig & any>) => {
				return (Math.max(link.source.width, link.source.height) + Math.max(link.target.width, link.target.height)) * 0.75;
			});
		let force = d3.forceManyBody();
		force.strength(-30)
		this.simulation = d3.forceSimulation<UiNetworkNodeConfig & any>()
			.nodes(this.nodes)
			.force("charge", force)
			.force("link", this.linkForce)
			.force("center", d3.forceCenter(this.getWidth() / 2, this.getWidth() / 2))
			.force("collide", d3.forceCollide((a: UiNetworkNodeConfig & any) => {
				return Math.sqrt(a.width * a.width + a.height * a.height) * a.distanceFactor;
			}))
			.stop();

		this.simulation.on("tick", () => {
			this.updateLinks();
			this.updateNodes();
		});

		this.calculateFinalNodePositions();

		if (this.svg) {
			this.logger.debug("remove svg-container");
			this.svg.remove();
		}

		this.zoom = d3.zoom()
			.extent([[0, 0], [this.getWidth(), this.getHeight()]])
			.scaleExtent([.01, 8])
			.on("zoom", () => {
				this.container.attr("transform", d3.event.transform);
			});

		this.svg = d3.select(this.getMainElement())
			.append("svg")
			.call(this.zoom)
			.on("dblclick.zoom", null); // disable doubleclick zoom!;

		this.pointerEventsRect = this.svg.append("rect")
			.attr("width", this.getWidth())
			.attr("height", this.getHeight())
			.style("fill", "none")
			.style("pointer-events", "all");

		this.container = this.svg.append("g");

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

		// set zoom to center before animating...
		this.svg.call(
			this.zoom.transform,
			d3.zoomIdentity.scale(1).translate(this.getWidth()/2, this.getHeight()/2)
		);

		this.updateLinks(this._config.animationDuration);
		this.updateNodes(this._config.animationDuration);
		this.zoomAllNodesIntoView(this._config.animationDuration);
	}

	private calculateFinalNodePositions() {
		let iterations = Math.ceil(Math.log(this.simulation.alphaMin()) / Math.log(1 - this.simulation.alphaDecay()));
		for (var i = 0, n = iterations; i < n; ++i) {
			this.simulation.tick();
		}
	}

	public zoomAllNodesIntoView(animationDuration: number) {
		let bounds = this.nodes.reduce((globalBounds, node) => {
			let leftBound = node.x - node.width / 2;
			let rightBound = node.x + node.width / 2;
			let topBound = node.y - node.height / 2;
			let bottomBound = node.y + 10 + node.height / 2;
			if (leftBound < globalBounds.minX) {
				globalBounds.minX = leftBound;
			}
			if (rightBound > globalBounds.maxX) {
				globalBounds.maxX = rightBound;
			}
			if (topBound < globalBounds.minY) {
				globalBounds.minY = topBound;
			}
			if (bottomBound > globalBounds.maxY) {
				globalBounds.maxY = bottomBound;
			}
			return globalBounds;
		}, {
			minX: 0,
			maxX: 0,
			minY: 0,
			maxY: 0,
			get width() {
				return this.maxX - this.minX;
			},
			get height() {
				return this.maxY - this.minY;
			},
			get centerX() {
				return (this.maxX + this.minX) / 2
			},
			get centerY() {
				return (this.maxY + this.minY) / 2
			}
		});
		// console.log(bounds.minX, bounds.maxX, bounds.minY, bounds.maxY, bounds.centerX, bounds.centerY);
		const k = Math.min(this.getWidth() / bounds.width, this.getHeight() / bounds.height);

		let translateX: number;
		let translateY: number;
		if (bounds.width / bounds.height > this.getWidth() / this.getHeight()) {
			translateX = -bounds.minX;
			translateY = (this.getHeight() / 2) / k - bounds.centerY;
		} else {
			translateX = (this.getWidth() / 2) / k - bounds.centerX;
			translateY = -bounds.minY;
		}
		// console.log(translateX, translateY);
		this.svg.transition().duration(animationDuration).call(
			this.zoom.transform,
			d3.zoomIdentity.scale(k).translate(translateX, translateY)
		);
	}

	public onResize(): void {
		this.pointerEventsRect.attr('width', this.getWidth()).attr('height', this.getHeight());
	}

	public setZoomFactor(zoomFactor: number): void {
		this.zoom.scaleTo(this.svg, zoomFactor);
		this.logger.debug(zoomFactor);
	}

	public setGravity(gravity: number): void {
		// this.force.gravity(gravity).start();
		this.logger.debug(gravity);
	}

	public setDistance(linkDistance: number, nodeDistance: number): void {
		this.linkForce.distance((link: SimulationLinkDatum<UiNetworkNodeConfig & any>) => {
			return (Math.max(link.source.width, link.source.height) + Math.max(link.target.width, link.target.height)) * linkDistance;
		});

		this.simulation.force("collide", d3.forceCollide((a: UiNetworkNodeConfig & any) => {
			return Math.sqrt(a.width * a.width + a.height * a.height) * a.distanceFactor * nodeDistance;
		}));

		this.simulation.nodes(this.nodes);
		this.linkForce.links(this.links);
		this.simulation.alphaTarget(0.3).restart()
			.stop();
		this.calculateFinalNodePositions();
		this.updateNodes(this._config.animationDuration);
		this.updateLinks(this._config.animationDuration);

		this.logger.debug("distance:" + linkDistance + ", " + nodeDistance);
	}

	public setCharge(charge: number, overrideNodeCharge: boolean): void {
		// this.force.charge(charge).start();
		this.logger.debug("charge:" + charge);
	}

	private updateLinks(animationDuration: number = 0) {
		this.linksContainer.selectAll("line")
			.data(this.links)
			.join("line")
			.attr("stroke-width", (d: UiNetworkLinkConfig) => d.lineWidth || 2)
			.attr('stroke', (d: UiNetworkLinkConfig) => createUiColorCssString(d.lineColor))
			.attr('stroke-dasharray', (d: UiNetworkLinkConfig) => d.lineDashArray ? d.lineDashArray : null)
			.transition()
			.duration(animationDuration)
			.attr("x1", (d: any) => d.source.x)
			.attr("y1", (d: any) => d.source.y)
			.attr("x2", (d: any) => d.target.x)
			.attr("y2", (d: any) => d.target.y)
	}


	private updateNodes(animationDuration: number = 0): void {
		const nodesSelection: d3.Selection<SVGGElement, any, SVGGElement, any> = this.container.selectAll<SVGGElement, any>('g.node')
			.data(this.nodes, ({
				                   id
			                   }: UiNetworkNodeConfig) => id);

		// Enter any new nodes at the parent's previous position.
		const nodeEnter = nodesSelection.enter().append('g')
			.attr('class', 'node')
			.attr('cursor', 'pointer')
			.on('click', (d: UiNetworkNodeConfig) => {
				if (d3.event.srcElement.classList.contains('node-button-circle')) {
					return;
				}
				this.onNodeClicked.fire({nodeId: d.id});
			})
			.on('dblclick', (d: UiNetworkNodeConfig) => {
				if (d3.event.srcElement.classList.contains('node-button-circle')) {
					return;
				}
				this.onNodeDoubleClicked.fire({nodeId: d.id});
			})
		;
		// Add background rectangle for the nodes
		patternify(nodeEnter, {
			tag: 'rect',
			selector: 'node-rect',
			data: (d: UiNetworkNodeConfig) => [d]
		});

		nodeEnter.call(d3.drag()
			.on("start", (d: any, i: number, nodes: Element[]) => {
				// if (!d3.event.active) {
				// 	this.simulation.alphaTarget(0.3).restart();
				// }
				d3.select(nodes[i]).raise();

				d.fx = d.x;
				d.fy = d.y;

				this.container.attr("cursor", "grabbing");
			})
			.on("drag", (d: any, i: number, nodes: Element[]) => {
				d.fx = d.x = d3.event.x;
				d.fy = d.y = d3.event.y;
				this.updateLinks();
				this.updateNodes();
			})
			.on("end", (d: any) => {
				// if (!d3.event.active) {
				// 	this.simulation.alphaTarget(0);
				// }
				d.fx = null;
				d.fy = null;
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
			.attr("xlink:href", (data: UiNetworkNodeConfig) => data.icon.icon)
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
			.style('font', '12px sans-serif');
		nodeUpdate
			.transition()
			.duration(animationDuration)
			.attr("transform", (d: any) => `translate(${d.fx != null ? d.fx : d.x},${d.fy != null ? d.fy : d.y})`);


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
				this.updateNodes();
			});

		// Add expand collapse button circle
		patternify(nodeButtonGroups, {
			tag: 'circle',
			selector: 'node-button-circle',
			data: (d: UiNetworkNodeConfig) => [d]
		});
		// Restyle node button circle
		nodeUpdate.select('.node-button-circle')
			.attr('r', 10)
			.attr('stroke-width', (d: UiNetworkNodeConfig) => d.borderWidth)
			.attr('fill', (d: UiNetworkNodeConfig) => createUiColorCssString(d.backgroundColor))
			.attr('stroke', (d: UiNetworkNodeConfig) => createUiColorCssString(d.borderColor));

		// Add button text
		patternify(nodeButtonGroups, {
			tag: 'text',
			selector: 'node-button-text',
			data: (d: UiNetworkNodeConfig) => [d]
		})
			.attr('pointer-events', 'none')
		// Restyle button texts
		nodeUpdate.select('.node-button-text')
			.attr('text-anchor', 'middle')
			.attr('alignment-baseline', 'middle')
			.attr('stroke', 'none')
			.attr('fill', 'black')
			.attr('font-size', 22)
			.text((d: UiNetworkNodeConfig) => d.expandState === UiNetworkNode_ExpandState.EXPANDED ? '–' : '+');

		// Move node button group to the desired position
		nodeUpdate.select('.node-button-g')
			.attr('transform', (d: UiNetworkNodeConfig) => `translate(0,${d.height / 2})`)
			.attr('display', (data: UiNetworkNodeConfig) => data.expandState === UiNetworkNode_ExpandState.NOT_EXPANDABLE ? 'none' : 'inherit');

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
			.attr('x', (d: UiNetworkNodeConfig) => d.image && d.image.centerLeftDistance);
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

	public addNodesAndLinks(newNodes: UiNetworkNodeConfig[], newLinks: UiNetworkLinkConfig[]): void {
		this.nodes.push(...newNodes);
		this.links.push(...newLinks);

		this.placeNewNodesNearExistingParentsOnes(newLinks, newNodes);

		this.simulation.nodes(this.nodes);
		this.linkForce.links(this.links);
		this.simulation.alphaTarget(0.3).restart()
			.stop();
		this.calculateFinalNodePositions();
		this.updateNodes(this._config.animationDuration);
		this.updateLinks(this._config.animationDuration);
	}

	private placeNewNodesNearExistingParentsOnes(newLinks: UiNetworkLinkConfig[], newNodes: UiNetworkNodeConfig[]) {
		let changed: boolean;
		do {
			changed = false;
			newLinks = newLinks.filter((l) => {
				let newSourceNode = newNodes.filter(n => n.id === l.source)[0];
				let newTargetNode = newNodes.filter(n => n.id === l.target)[0];
				if (newSourceNode != null && newTargetNode == null) {
					let existingTargetNode = this.nodes.filter(n => n.id === l.target)[0];
					if (existingTargetNode != null) {
						(newSourceNode as any).x = (existingTargetNode as any).x;
						(newSourceNode as any).y = (existingTargetNode as any).y;
					}
					newNodes = newNodes.filter(n => n !== newSourceNode);
					changed = true;
					return false;
				} else if (newSourceNode == null && newTargetNode != null) {
					let existingSourceNode = this.nodes.filter(n => n.id === l.source)[0];
					if (existingSourceNode != null) {
						(newTargetNode as any).x = (existingSourceNode as any).x;
						(newTargetNode as any).y = (existingSourceNode as any).y;
					}
					newNodes = newNodes.filter(n => n !== newTargetNode);
					changed = true;
					return false;
				}
				return true;
			});
		} while (changed);
	}

	public removeNodesAndLinks(nodeIds: string[], linksBySourceNodeId: {[name: string]: string[]}): void {
		const nodeIdsSet = new Set(nodeIds);
		this.nodes = this.nodes.filter(n => {
			return !nodeIdsSet.has(n.id);
		});
		this.links = this.links.filter(l => {
			let targetIdsToDelete = linksBySourceNodeId[l.source && l.source.id];
			if (targetIdsToDelete == null) {
				return true;
			}
			return !targetIdsToDelete.filter(targetId => (l.target && l.target.id) === targetId)[0]
		});

		this.simulation.nodes(this.nodes);
		this.linkForce.links(this.links);
		this.simulation.alphaTarget(0.3).restart()
			.stop();
		this.calculateFinalNodePositions();
		this.updateNodes(this._config.animationDuration);
		this.updateLinks(this._config.animationDuration);
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiNetworkGraph", UiNetworkGraph);
