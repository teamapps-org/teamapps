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

import * as d3 from "d3";
import {BaseType, HierarchyPointNode, Selection, TreeLayout, ZoomBehavior} from "d3";
import {UiColorConfig} from '../generated/UiColorConfig';
import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiTreeGraph_NodeClickedEvent, UiTreeGraph_NodeExpandedOrCollapsedEvent, UiTreeGraphCommandHandler, UiTreeGraphConfig, UiTreeGraphEventSource} from "../generated/UiTreeGraphConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiTreeGraphNodeConfig} from "../generated/UiTreeGraphNodeConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTreeGraphNodeImage_CornerShape} from "../generated/UiTreeGraphNodeImageConfig";
import {parseHtml} from "./Common";

export class UiTreeGraph extends AbstractUiComponent<UiTreeGraphConfig> implements UiTreeGraphCommandHandler, UiTreeGraphEventSource {

	public readonly onNodeClicked: TeamAppsEvent<UiTreeGraph_NodeClickedEvent> = new TeamAppsEvent(this);
	public readonly onNodeExpandedOrCollapsed: TeamAppsEvent<UiTreeGraph_NodeExpandedOrCollapsedEvent> = new TeamAppsEvent(this);

	private chart: TreeChart;
	private $main: HTMLElement;

	constructor(config: UiTreeGraphConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiTreeGraph">`);
		this.chart = new TreeChart(context)
			.container(this.$main)
			.data(this._config.nodes)
			.onNodeClick((nodeId: string) => {
				this.onNodeClicked.fire({
					nodeId: nodeId
				});
			})
			.onNodeExpandedOrCollapsed((nodeId: string, expanded: boolean) => {
				this.onNodeExpandedOrCollapsed.fire({
					nodeId: nodeId,
					expanded: expanded
				});
			})
			.render();
	}

	setNodes(nodes: UiTreeGraphNodeConfig[]): void {
		this.chart.data(nodes)
			.render();
	}

	addNode(node: UiTreeGraphNodeConfig): void {
		this.chart.data().push(node);
		this.chart.render();
	}

	removeNode(nodeId: string): void {
		this.chart.data(this.chart.data().filter(n => n.id !== nodeId));
		this.chart.render();
	}

	setNodeExpanded(nodeId: string, expanded: boolean): void {
		this.chart.data().filter(n => n.id === nodeId)[0].expanded = expanded;
		this.chart.render();
	}

	setZoomFactor(zoomFactor: number): void {
		this.chart.setZoomFactor(zoomFactor);
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	onResize(): void {
		this.chart
			.svgWidth(this.getWidth())
			.svgHeight(this.getHeight())
			.render();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiTreeGraph", UiTreeGraph);

interface TreeChart {
	getChartState: () => TreeChartAttributes;

	svgWidth(): number;

	svgWidth(svgWidth: number): TreeChart;

	svgHeight(): number;

	svgHeight(marginTop: number): TreeChart;

	marginTop(): number;

	marginTop(marginTop: number): TreeChart;

	marginBottom(): number;

	marginBottom(marginBottom: number): TreeChart;

	marginRight(): number;

	marginRight(marginRight: number): TreeChart;

	marginLeft(): number;

	marginLeft(marginLeft: number): TreeChart;

	container(): string | HTMLElement;

	container(container: string | HTMLElement): TreeChart;

	defaultTextFill(): string;

	defaultTextFill(defaultTextFill: string): TreeChart;

	nodeTextFill(): string;

	nodeTextFill(nodeTextFill: string): TreeChart;

	defaultFont(): string;

	defaultFont(defaultFont: string): TreeChart;

	backgroundColor(): string;

	backgroundColor(backgroundColor: string): TreeChart;

	data(): UiTreeGraphNodeConfig[];

	data(data: UiTreeGraphNodeConfig[]): TreeChart;

	depth(): number;

	depth(depth: number): TreeChart;

	duration(): number;

	duration(duration: number): TreeChart;

	strokeWidth(): number;

	strokeWidth(strokeWidth: number): TreeChart;

	initialZoom(): number;

	initialZoom(initialZoom: number): TreeChart;

	onNodeClick(): (nodeId: string) => void;

	onNodeClick(onNodeClick: (nodeId: string) => void): TreeChart;

	onNodeExpandedOrCollapsed(): (nodeId: string, expanded: boolean) => void;

	onNodeExpandedOrCollapsed(onNodeExpandedOrCollapsed: (nodeId: string, expanded: boolean) => void): TreeChart;
}

interface TreeNode extends HierarchyPointNode<UiTreeGraphNodeConfig> {
	id: 'string',
	parentId: 'string' | null,
	data: UiTreeGraphNodeConfig,
	children: this[],
	hasChildren: boolean,
	descendants: () => this[],
	parent: this | null,
	x: number,
	y: number,
	imageWidth: number,
	imageHeight: number,
	imageBorderColor: string,
	imageBorderWidth: number,
	borderColor: string,
	backgroundColor: string,
	imageRx: number,
	width: number,
	height: number,
	imageCenterTopDistance: number,
	imageCenterLeftDistance: number,
	dropShadowId: string,

}

export interface Layouts {
	treemap: TreeLayout<UiTreeGraphNodeConfig>
}

export interface NodeImage {
	url: string;
	width: number;
	height: number;
	centerTopDistance: number; //top distance of image center from top-left corner of node, e.g. -10 and height 30 means image will be 25 pixels above the top line of the node
	centerLeftDistance: number; //left distance of image center from top-left corner of node
	cornerShape: string; //enum { ORIGINAL, ROUNDED, CIRCLE }
	shadow: boolean;
	borderWidth: number;
	borderColor: UiColorConfig
}

export interface NodeIcon {
	icon: string;
	size: number;
}

export interface TreeChartAttributes {
	[key: string]: any,

	id: string,
	data?: UiTreeGraphNodeConfig[],
	root: TreeNode,
	svgWidth?: number,
	svgHeight?: number,
	marginTop?: number,
	marginBottom?: number,
	marginRight?: number,
	marginLeft?: number,
	container?: any,
	defaultTextFill?: string,
	defaultFont?: string,
	backgroundColor: string,
	duration: number,
	strokeWidth: number,
	dropShadowId: string,
	initialZoom: number,
	onNodeClick?: (name: string) => void,
	onNodeExpandedOrCollapsed?: (nodeId: string, expanded: boolean) => void,
	layouts: Layouts,
	verticalGap: number,
}

export interface PatternifyParameter {
	selector: string,
	tag: string,
	data?: any
}

export interface Behaviors {
	[key: string]: ZoomBehavior<SVGElement, void>
}

class TreeChart {
	private chart: Selection<SVGGElement, void, SVGElement, void>;
	private behaviors: Behaviors;

	constructor(private context: TeamAppsUiContext) {
		// Exposed variables
		const attrs: TreeChartAttributes = {
			id: `ID${Math.floor(Math.random() * 1000000)}`, // Id for event handlings
			svgWidth: 800,
			svgHeight: 600,
			marginTop: 0,
			marginBottom: 0,
			marginRight: 0,
			marginLeft: 0,
			container: 'body',
			defaultTextFill: '#2C3E50',
			nodeTextFill: 'white',
			defaultFont: 'Helvetica',
			backgroundColor: 'transparent',
			data: null,
			duration: 400,
			strokeWidth: 3,
			dropShadowId: null,
			initialZoom: 1,
			layouts: null,
			verticalGap: 40,
			onNodeClick: (): void => undefined,
			onNodeExpandedOrCollapsed: (): void => undefined,
		} as any;

		this.getChartState = () => attrs;

		// Dynamically set getter and setter functions for Chart class
		Object.keys(attrs).forEach((key) => {
			//@ts-ignore
			this[key] = function (_) {
				if (!arguments.length) {
					return attrs[key];
				}
				attrs[key] = _;
				return this;
			};
		});
	}

	// This method can be invoked via chart.setZoomFactor API, it zooms to particulat scale
	setZoomFactor(zoomLevel: number) {
		const attrs = this.getChartState();

		// Store passed zoom level
		attrs.initialZoom = zoomLevel;
	}

	render() {
		//InnerFunctions which will update visuals
		const attrs = this.getChartState();
		const thisObjRef = this;

		//Drawing containers
		const container: Selection<Element, void, null, void> = d3.select(attrs.container);
		const containerRect = container.node().getBoundingClientRect();
		if (containerRect.width > 0) attrs.svgWidth = containerRect.width;

		//Attach drop shadow id to attrs object
		this.setDropShadowId(attrs);

		//Calculated properties
		const calc: any = {
			id: null,
			chartTopMargin: null,
			chartLeftMargin: null,
			chartWidth: null,
			chartHeight: null
		};
		calc.id = `ID${Math.floor(Math.random() * 1000000)}`; // id for event handlings
		calc.chartLeftMargin = attrs.marginLeft;
		calc.chartTopMargin = attrs.marginTop;
		calc.chartWidth = attrs.svgWidth - attrs.marginRight - calc.chartLeftMargin;
		calc.chartHeight = attrs.svgHeight - attrs.marginBottom - calc.chartTopMargin;
		attrs.calc = calc;

		// Get maximum node width and height
		calc.nodeMaxWidth = d3.max(attrs.data, (d) => d.width);

		// Calculate max node depth (it's needed for layout heights calculation)
		calc.centerX = calc.chartWidth / 2;

		//********************  LAYOUTS  ***********************
		const layouts: Layouts = {
			treemap: null
		};
		attrs.layouts = layouts;

		// Generate tree layout function
		layouts.treemap = d3.tree<UiTreeGraphNodeConfig>()
			.size([calc.chartWidth, calc.chartHeight])
			.separation((a, b) => {
				return a.parent == b.parent ? 1 : 1.1;
			})
			.nodeSize([calc.nodeMaxWidth + 100, 0]);

		// ******************* BEHAVIORS . **********************
		this.behaviors = {
			zoom: null
		}

		// Get zooming function
		this.behaviors.zoom = d3.zoom<SVGElement, void>().on("zoom", () => this.zoomed())

		// *************************  DRAWING **************************
		//Add svg

		const svg = patternify<SVGElement, void, Element, void>(container, {
			tag: 'svg',
			selector: 'svg-chart-container'
		})
			.attr('width', attrs.svgWidth)
			.attr('height', attrs.svgHeight)
			.attr('font-family', attrs.defaultFont)
			.call(this.behaviors.zoom)
			.attr('cursor', 'move')
			.style('background-color', attrs.backgroundColor);
		attrs.svg = svg;

		//Add container g element
		this.chart = patternify<SVGGElement, void, SVGElement, void>(svg, {
			tag: 'g',
			selector: 'chart'
		})
		// .attr('transform', `translate(${calc.chartLeftMargin},${calc.chartTopMargin}) scale(${d3.zoomTransform(svg.node()).k})`);

		attrs.chart = this.chart;

		// ************************** ROUNDED AND SHADOW IMAGE  WORK USING SVG FILTERS **********************

		//Adding defs element for rounded image
		attrs.defs = patternify(svg, {
			tag: 'defs',
			selector: 'image-defs'
		});

		// Adding defs element for image's shadow
		const filterDefs = patternify(svg, {
			tag: 'defs',
			selector: 'filter-defs'
		});

		// Adding shadow element - (play with svg filter here - https://bit.ly/2HwnfyL)
		const filter = patternify(filterDefs, {
			tag: 'filter',
			selector: 'shadow-filter-element'
		})
			.attr('id', attrs.dropShadowId)
			.attr('y', `${-50}%`)
			.attr('x', `${-50}%`)
			.attr('height', `${200}%`)
			.attr('width', `${200}%`);

		// Add gaussian blur element for shadows - we can control shadow length with this
		patternify(filter, {
			tag: 'feGaussianBlur',
			selector: 'feGaussianBlur-element'
		})
			.attr('in', 'SourceAlpha')
			.attr('stdDeviation', 3.1)
			.attr('result', 'blur');

		// Add fe-offset element for shadows -  we can control shadow positions with it
		patternify(filter, {
			tag: 'feOffset',
			selector: 'feOffset-element'
		})
			.attr('in', 'blur')
			.attr('result', 'offsetBlur')
			.attr("dx", 4.28)
			.attr("dy", 4.48)
			.attr("x", 8)
			.attr("y", 8)

		// Add fe-flood element for shadows - we can control shadow color and opacity with this element
		patternify(filter, {
			tag: 'feFlood',
			selector: 'feFlood-element'
		})
			.attr("in", "offsetBlur")
			.attr("flood-color", 'black')
			.attr("flood-opacity", 0.3)
			.attr("result", "offsetColor");

		// Add feComposite element for shadows
		patternify(filter, {
			tag: 'feComposite',
			selector: 'feComposite-element'
		})
			.attr("in", "offsetColor")
			.attr("in2", "offsetBlur")
			.attr("operator", "in")
			.attr("result", "offsetBlur");

		// Add feMerge element for shadows
		const feMerge = patternify(filter, {
			tag: 'feMerge',
			selector: 'feMerge-element'
		});

		// Add feMergeNode element for shadows
		patternify(feMerge, {
			tag: 'feMergeNode',
			selector: 'feMergeNode-blur'
		})
			.attr('in', 'offsetBlur')

		// Add another feMergeNode element for shadows
		patternify(feMerge, {
			tag: 'feMergeNode',
			selector: 'feMergeNode-graphic'
		})
			.attr('in', 'SourceGraphic')

		// Display tree contenrs
		this.update()


		//#########################################  UTIL FUNCS ##################################
		// This function restyles foreign object elements ()


		d3.select(window).on(`resize.${attrs.id}`, () => {
			const containerRect = container.node().getBoundingClientRect();
			//  if (containerRect.width > 0) attrs.svgWidth = containerRect.width;
			//	main();
		});


		return this;
	}

	// This function sets drop shadow ID to the passed object
	setDropShadowId(d: { id: string, dropShadowId: string }) {

		// If it's already set, then return
		if (d.dropShadowId) return;

		// Generate drop shadow ID
		let id = `${d.id}-drop-shadow`;

		// If DOM object is available, then use UID method to generated shadow id
		//@ts-ignore
		if (typeof DOM != 'undefined') {
			//@ts-ignore
			id = DOM.uid(d.id).id;
		}

		// Extend passed object with drop shadow ID
		Object.assign(d, {
			dropShadowId: id
		})
	}


	// This function basically redraws visible graph, based on nodes state
	update() {
		this.updateNodesState();

		const attrs = this.getChartState();
		const calc = attrs.calc;

		//  Assigns the x and y position for the nodes
		const treeData = attrs.layouts.treemap(attrs.root);
		const layerHeightByDepth = treeData.descendants().reduce((heightsByDepth: number[], d) => {
			if (heightsByDepth[d.depth] == null || heightsByDepth[d.depth] < d.data.height) {
				heightsByDepth[d.depth] = d.data.height;
			}
			return heightsByDepth;
		}, []);
		const layerYByDepth: number[] = [];
		layerYByDepth[0] = 0;
		for (let i = 1; i < layerHeightByDepth.length; i++) {
			layerYByDepth[i] = layerYByDepth[i - 1] + layerHeightByDepth[i - 1] + attrs.verticalGap;
		}
		treeData.eachBefore(d => {
			return d.y = layerYByDepth[d.depth];
		});

		// Get tree nodes and links and attach some properties
		const nodes = treeData.descendants()
			.map((d) => {
				// If at least one property is already set, then we don't want to reset other properties
				if ((d as any).width) return d as TreeNode;

				// Declare properties with deffault values
				let imageWidth = 100;
				let imageHeight = 100;
				let imageBorderColor = 'steelblue';
				let imageBorderWidth = 0;
				let imageRx = 0;
				let imageCenterTopDistance = 0;
				let imageCenterLeftDistance = 0;
				let borderColor = 'steelblue';
				let backgroundColor = 'steelblue';
				let width = d.data.width;
				let height = d.data.height;
				let dropShadowId = `none`;

				// Override default values based on data
				if (d.data.image && d.data.image.shadow) {
					dropShadowId = `url(#${attrs.dropShadowId})`
				}
				if (d.data.image && d.data.image.width) {
					imageWidth = d.data.image.width
				}
				if (d.data.image && d.data.image.height) {
					imageHeight = d.data.image.height
				}
				if (d.data.image && d.data.image.borderColor) {
					imageBorderColor = this.rgbaObjToColor(d.data.image.borderColor)
				}
				if (d.data.image && d.data.image.borderWidth) {
					imageBorderWidth = d.data.image.borderWidth
				}
				if (d.data.image && d.data.image.centerTopDistance) {
					imageCenterTopDistance = d.data.image.centerTopDistance
				}
				if (d.data.image && d.data.image.centerLeftDistance) {
					imageCenterLeftDistance = d.data.image.centerLeftDistance
				}
				if (d.data.borderColor) {
					borderColor = this.rgbaObjToColor(d.data.borderColor);
				}
				if (d.data.backgroundColor) {
					backgroundColor = this.rgbaObjToColor(d.data.backgroundColor);
				}
				if (d.data.image &&
					d.data.image.cornerShape == UiTreeGraphNodeImage_CornerShape.CIRCLE) {
					imageRx = Math.max(imageWidth, imageHeight);
				}
				if (d.data.image &&
					d.data.image.cornerShape == UiTreeGraphNodeImage_CornerShape.ROUNDED) {
					imageRx = Math.min(imageWidth, imageHeight) / 10;
				}

				// Extend node object with calculated properties
				return Object.assign(d, {
					imageWidth,
					imageHeight,
					imageBorderColor,
					imageBorderWidth,
					borderColor,
					backgroundColor,
					imageRx,
					width,
					height,
					imageCenterTopDistance,
					imageCenterLeftDistance,
					dropShadowId
				}) as TreeNode;
			});

		// Get all links
		const links = treeData.descendants().slice(1);

		// ------------------- FILTERS ---------------------

		// Add patterns for each node (it's needed for rounded image implementation)
		const patternsSelection = attrs.defs.selectAll('.pattern')
			.data(nodes, ({
				              id
			              }: TreeNode) => id);

		// Define patterns enter selection
		const patternEnterSelection = patternsSelection.enter().append('pattern')

		// Patters update selection
		const patterns = patternEnterSelection
			.merge(patternsSelection)
			.attr('class', 'pattern')
			.attr('height', 1)
			.attr('width', 1)
			.attr('id', ({
				             id
			             }: TreeNode) => id)

		// Add images to patterns
		const patternImages = patternify(patterns, {
			tag: 'image',
			selector: 'pattern-image',
			data: (d: TreeNode) => [d]
		})
			.attr('x', 0)
			.attr('y', 0)
			.attr('height', ({
				                 imageWidth
			                 }: TreeNode) => imageWidth)
			.attr('width', ({
				                imageHeight
			                }: TreeNode) => imageHeight)
			.attr('xlink:href', ({
				                     data
			                     }: TreeNode) => data.image && data.image.url)
			.attr('viewbox', ({
				                  imageWidth,
				                  imageHeight
			                  }: TreeNode) => `0 0 ${imageWidth * 2} ${imageHeight}`)
			.attr('preserveAspectRatio', 'xMidYMin slice')

		// Remove patterns exit selection after animation
		patternsSelection.exit().transition().duration(attrs.duration).remove();

		// --------------------------  LINKS ----------------------
		// Get links selection
		const linkSelection = this.chart.selectAll('path.link')
			.data(links, (d: TreeNode) => d.id);

		// NOTE: cannot use join() here! Enter any new links at the parent's previous position.
		const linkEnter = linkSelection.enter()
			.insert('path', "g")
			.attr("class", "link")
			.attr('d', (d: TreeNode) => {
				let transitionOrigin = this.getParentExpanderPosition(d);
				return this.diagonal(transitionOrigin, transitionOrigin);
			});

		// Get links update selection
		const linkUpdate = linkEnter.merge(linkSelection as any);

		// Styling links
		linkUpdate
			.attr("fill", "none")
			.attr("stroke-width", d => d.data.connectorLineWidth || 2)
			.attr('stroke', d => {
				if (d.data.connectorLineColor) {
					return this.rgbaObjToColor(d.data.connectorLineColor);
				}
				return 'green';
			})
			.attr('stroke-dasharray', d => {
				if (d.data.dashArray) {
					return d.data.dashArray;
				}
				return '';
			})

		// Transition back to the parent element position
		linkUpdate.transition()
			.duration(attrs.duration)
			.attr('d', d => this.diagonal({x: d.x + d.data.width / 2, y: d.y}, {x: d.parent.x + d.parent.data.width / 2, y: d.parent.y + d.parent.data.height}));

		// Remove any  links which is exiting after animation
		const linkExit = linkSelection.exit().transition()
			.duration(attrs.duration)
			.attr('d', (d: TreeNode) => {
				let transitionOrigin = this.getParentExpanderPosition(d);
				return this.diagonal(transitionOrigin, transitionOrigin);
			})
			.remove();

		// --------------------------  NODES ----------------------
		// Get nodes selection
		const nodesSelection = this.chart.selectAll('g.node')
			.data(nodes, d => (d as any).id)
			.join(
				enter => enter
					.append('g')
					.attr('class', 'node')
					.attr("transform", d => {
						let transitionOrigin = this.getParentExpanderPosition(d);
						return `translate(${transitionOrigin.x - d.data.width / 2},${transitionOrigin.y})`;
					})
					.on('click', d => {
						if (d3.event.srcElement.classList.contains('node-button-circle')) {
							return;
						}
						attrs.onNodeClick(d.data.id);
					}),
				update => update,
				exit => exit.attr('opacity', 1)
					.transition()
					.duration(attrs.duration)
					.attr("transform", (d: TreeNode) => {
						let transitionTarget = this.getParentExpanderPosition(d);
						return `translate(${transitionTarget.x - d.data.width / 2},${transitionTarget.y})`;
					})
					.on('end', function () {
						d3.select(this).remove();
					})
					.attr('opacity', 0)
			);

		nodesSelection.transition()
			.attr('opacity', 0)
			.duration(attrs.duration)
			.attr("transform", d => {
				return `translate(${d.x},${d.y})`;
			})
			.attr('opacity', 1)


		// Style node rectangles
		nodesSelection.selectAll('.node-rect')
			.data(d => [d])
			.join(enter => enter
				.append('rect')
				.classed("node-rect", true)
			)
			.attr('width', d => d.data.width)
			.attr('height', d => d.data.height)
			.attr('x', 0)
			.attr('y', 0)
			.attr('rx', d => d.data.borderRadius || 0)
			.attr('stroke-width', d => d.data.borderWidth || attrs.strokeWidth)
			.attr('cursor', 'pointer')
			.attr('stroke', d => d.borderColor)
			.style("fill", d => d.backgroundColor);

		// Add node icon image inside node
		nodesSelection.selectAll('image.node-icon-image')
			.data(d => d.data.icon ? [d] : [])
			.join(enter => enter
				.append('image')
				.classed('node-icon-image', true)
			)
			.attr('width', d => d.data.icon.size)
			.attr('height', d => d.data.icon.size)
			.attr("xlink:href", d => this.context.getIconPath(d.data.icon.icon, d.data.icon.size))
			.attr('x', d => -d.data.icon.size / 2)
			.attr('y', d => -d.data.icon.size / 2);

		// Defined node images wrapper group
		const imageGroups = nodesSelection.selectAll('g.node-image-group')
			.data(d => [d])
			.join(enter => enter
				.append("g")
				.classed("node-image-group", true)
			)
			.attr('transform', (d: TreeNode) => {
				let x = -d.imageWidth / 2;
				let y = -d.imageHeight / 2;
				return `translate(${x},${y})`
			});

		nodesSelection.selectAll('rect.node-image-rect')
			.data(d => [d])
			.join(enter => enter
				.append("rect")
				.classed("node-image-rect", true)
			)
			.attr('fill', d => `url(#${d.id})`)
			.attr('width', d => d.imageWidth)
			.attr('height', d => d.imageHeight)
			.attr('stroke', d => d.imageBorderColor)
			.attr('stroke-width', d => d.imageBorderWidth)
			.attr('rx', d => d.imageRx)
			.attr('y', d => d.imageCenterTopDistance)
			.attr('x', d => d.imageCenterLeftDistance)
			.attr('filter', d => d.dropShadowId);

		// Add foreignObject element inside rectangle
		const fo = nodesSelection.selectAll('foreignObject.node-foreign-object')
			.data(d => [d])
			.join(enter => enter
				.append("foreignObject")
				.classed("node-foreign-object", true)
			);

		fo.selectAll('.node-foreign-object-div')
			.data(d => [d])
			.join(enter => enter
				.append("xhtml:div")
				.classed("node-foreign-object-div", true)
			)

		this.restyleForeignObjectElements();

		// Add Node button circle's group (expand-collapse button)
		const nodeButtonGroups = nodesSelection.selectAll('g.node-button-g')
			.data(d => [d])
			.join(enter => enter
				.append("g")
				.classed("node-button-g", true)
				.on('mousedown', (d: TreeNode) => {
					let expanding = !d.children;
					this.onButtonClick(d);
					attrs.onNodeExpandedOrCollapsed(d.data.id, expanding);
				})
			)
			.attr('transform', d => `translate(${d.data.width / 2},${d.data.height})`)
			.attr('display', d => {
				return d.hasChildren ? "inherit" : "none";
			});

		// Add expand collapse button circle
		nodeButtonGroups.selectAll('circle.node-button-circle')
			.data(d => [d])
			.join(enter => enter
				.append("circle")
				.classed("node-button-circle", true)
			)
			.attr('r', 10)
			.attr('stroke-width', d => d.data.borderWidth || attrs.strokeWidth)
			.attr('fill', "white")
			.attr('stroke', d => d.borderColor)

		// Add button text
		nodeButtonGroups.selectAll('text.node-button-text')
			.data(d => [d])
			.join(enter => enter
				.append("text")
				.classed("node-button-text", true)
			)
			.attr('text-anchor', 'middle')
			.attr('alignment-baseline', 'middle')
			.attr('fill', attrs.defaultTextFill)
			.attr('font-size', d => d.children ? 30 : 20)
			.text(d => d.children ? '-' : '+')
			.attr('y', this.isEdge() ? 10 : 0);
	}

	private getParentExpanderPosition(d: TreeNode) {
		if (d.parent == null) {
			return {x: d.width / 2, y: 0};
		}
		return {x: d.parent.x + d.parent.data.width / 2, y: d.parent.y + d.parent.data.height};
	}

	private isEdge() {
		return window.navigator.userAgent.includes("Edge");
	}

	/* Function converts rgba objects to rgba color string
	  {red:110,green:150,blue:255,alpha:1}  => rgba(110,150,255,1)
	*/
	rgbaObjToColor({
		               red,
		               green,
		               blue,
		               alpha
	               }: UiColorConfig) {
		return `rgba(${red},${green},${blue},${alpha})`;
	}

	diagonal(s: { x: number, y: number }, t: { x: number, y: number }) {
		// Calculate some variables based on source and target (s,t) coordinates
		const x = s.x;
		const y = s.y;
		const ex = t.x;
		const ey = t.y;
		let xrvs = ex - x < 0 ? -1 : 1;
		let yrvs = ey - y < 0 ? -1 : 1;
		let rdef = 35;
		let rInitial = Math.abs(ex - x) / 2 < rdef ? Math.abs(ex - x) / 2 : rdef;
		let r = Math.abs(ey - y) / 2 < rInitial ? Math.abs(ey - y) / 2 : rInitial;
		let h = Math.abs(ey - y) / 2 - r;
		let w = Math.abs(ex - x) - r * 2;

		// Build the path
		return `
           M ${x} ${y}
           L ${x} ${y + h * yrvs}
           C  ${x} ${y + h * yrvs + r * yrvs} ${x} ${y + h * yrvs + r * yrvs} ${x + r * xrvs} ${y + h * yrvs + r * yrvs}
           L ${x + w * xrvs + r * xrvs} ${y + h * yrvs + r * yrvs}
           C ${ex}  ${y + h * yrvs + r * yrvs} ${ex}  ${y + h * yrvs + r * yrvs} ${ex} ${ey - h * yrvs}
           L ${ex} ${ey}
         `;
	}

	restyleForeignObjectElements() {
		const attrs = this.getChartState();

		attrs.svg.selectAll('.node-foreign-object')
			.attr('width', (n: TreeNode) => n.width)
			.attr('height', (n: TreeNode) => n.height);
		attrs.svg.selectAll('.node-foreign-object-div')
			.style('width', (n: TreeNode) => `${n.width}px`)
			.style('height', (n: TreeNode) => `${n.height}px`)
			.html((n: TreeNode) => this.context.templateRegistry.createTemplateRenderer(n.data.template).render(n.data.record.values))
			.select('*')
			.style('display', 'inline-grid')
	}

	// Toggle children on click.
	onButtonClick(d: TreeNode) {
		d.data.expanded = !d.data.expanded;
		this.update();
	}

	// This function updates nodes state and redraws graph, usually after data change
	updateNodesState() {
		const attrs = this.getChartState();

		let visibleNodeRecords = this.getVisibleNodeRecords(attrs.data);

		// Store new root by converting flat data to hierarchy
		attrs.root = d3.stratify<UiTreeGraphNodeConfig>()
			.id((d: UiTreeGraphNodeConfig) => d.id)
			.parentId((d: UiTreeGraphNodeConfig) => d.parentId)
			(visibleNodeRecords) as TreeNode;

		let recordsByParentId = getById(attrs.data.filter(r => r.id !== attrs.root.id), n => n.parentId);
		attrs.root.eachBefore(n => {
			n.hasChildren = recordsByParentId[n.id] != null || (n.data as any).hasLazyChildren;
		});
	}

	private getVisibleNodeRecords(records: UiTreeGraphNodeConfig[]) {
		const recordsById = getById(records);
		const rootNodeRecords = records.filter(r => recordsById[r.parentId] == null);

		const visibleNodeRecordsById: { [id: string]: UiTreeGraphNodeConfig } = getById(rootNodeRecords);
		const invisibleNodeRecordsById: { [id: string]: UiTreeGraphNodeConfig } = {};
		let remainingRecords: UiTreeGraphNodeConfig[] = records.filter(r => visibleNodeRecordsById[r.id] == null);
		do {
			remainingRecords = remainingRecords.filter(r => {
				let visibleParent = visibleNodeRecordsById[r.parentId];
				let invisibleParent = invisibleNodeRecordsById[r.parentId];
				if (visibleParent != null) {
					if (visibleParent.expanded) {
						visibleNodeRecordsById[r.id] = r;
						return false;
					} else {
						invisibleNodeRecordsById[r.id] = r;
						return false;
					}
				} else if (invisibleParent != null) {
					invisibleNodeRecordsById[r.id] = r;
					return false;
				} else {
					return true; // we don't know yet for this node...
				}
			});
		} while (remainingRecords.length > 0);
		return Object.values(visibleNodeRecordsById);
	}

	// Zoom handler function
	zoomed() {
		const attrs = this.getChartState();
		const chart = attrs.chart;

		// Get d3 event's transform object
		const transform = d3.event.transform;

		// Store it
		attrs.lastTransform = transform;

		// Reposition and rescale chart accordingly
		chart.attr('transform', transform);

		// Apply new styles to the foreign object element
		if (this.isEdge()) {
			this.restyleForeignObjectElements();
		}

	}

}

function getById<R>(recs: R[], idExtractor: (r: R) => string = r => (r as any).id) {
	return recs.reduce((recordsById, record) => {
		recordsById[idExtractor(record)] = record;
		return recordsById;
	}, {} as { [id: string]: R })
}

export function patternify<E extends BaseType, ED, P extends BaseType = null, PD = null>(container: Selection<P, PD, any, any>, params: PatternifyParameter) {
	var selector = params.selector;
	var elementTag = params.tag;
	var data = params.data || [selector];

	// Pattern in action
	var selection = container.selectAll<E, ED>('.' + selector)
		.data(data, (d: any, i: number) => {
			if (typeof d === 'object' && d.id) {
				return d.id;
			}
			return i;
		}) as Selection<E, ED, P, PD>;
	selection.exit().remove();
	selection = selection.enter().append<E>(elementTag).merge(selection);
	selection.attr('class', selector);
	return selection;
}
