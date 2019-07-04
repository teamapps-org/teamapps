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
import {BaseType, Selection, ZoomBehavior} from "d3";
import {UiColorConfig} from '../generated/UiColorConfig';
import {UiComponent} from "./UiComponent";
import {UiTreeGraph_NodeClickedEvent, UiTreeGraph_NodeExpandedOrCollapsedEvent, UiTreeGraphCommandHandler, UiTreeGraphConfig, UiTreeGraphEventSource} from "../generated/UiTreeGraphConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiTreeGraphNodeConfig} from "../generated/UiTreeGraphNodeConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTreeGraphNodeImage_CornerShape} from "../generated/UiTreeGraphNodeImageConfig";
import {parseHtml} from "./Common";

export class UiTreeGraph extends UiComponent<UiTreeGraphConfig> implements UiTreeGraphCommandHandler, UiTreeGraphEventSource {

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

interface HierarchyNode {
	id: 'string',
	data: UiTreeGraphNodeConfig,
	_children: HierarchyNode[],
	children: HierarchyNode[],
	descendants: Function,
	parent: HierarchyNode,
	x0: number,
	y0: number,
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
	depth: number,

}

export interface Layouts {
	[key: string]: Function
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
	depth: number,
	duration: number,
	strokeWidth: number,
	dropShadowId: string,
	initialZoom: number,
	onNodeClick?: (name: string) => void,
	onNodeExpandedOrCollapsed?: (nodeId: string, expanded: boolean) => void
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
			depth: 180,
			duration: 600,
			strokeWidth: 3,
			dropShadowId: null,
			initialZoom: 1,
			onNodeClick: () => undefined,
			onNodeExpandedOrCollapsed: () => undefined,
		};

		this.getChartState = () => attrs;

		// Dynamically set getter and setter functions for Chart class
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
	}

	// This method retrieves passed node's children ID's (including node)
	getNodeChildrenIds({
		                   data,
		                   children,
		                   _children
	                   }: HierarchyNode, nodeIdsStore: string[]) {

		// Store current node ID
		nodeIdsStore.push(data.id);

		// Loop over children and recursively store descendants id (expanded nodes)
		if (children) {
			children.forEach(d => {
				this.getNodeChildrenIds(d, nodeIdsStore)
			})
		}

		// Loop over _children and recursively store descendants id (collapsed nodes)
		if (_children) {
			_children.forEach(d => {
				this.getNodeChildrenIds(d, nodeIdsStore)
			})
		}

		// Return result
		return nodeIdsStore;
	}

	// This method can be invoked via chart.setZoomFactor API, it zooms to particulat scale
	setZoomFactor(zoomLevel: number) {
		const attrs = this.getChartState();
		const calc = attrs.calc;

		// Store passed zoom level
		attrs.initialZoom = zoomLevel;

		// Rescale container element accordingly
		attrs.centerG.attr('transform', ` translate(${calc.centerX}, ${calc.nodeMaxHeight / 2})`)
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
		calc.nodeMaxWidth = d3.max(attrs.data, ({
			                                        width
		                                        }) => width);
		calc.nodeMaxHeight = d3.max(attrs.data, ({
			                                         height
		                                         }) => height);

		// Calculate max node depth (it's needed for layout heights calculation)
		attrs.depth = calc.nodeMaxHeight + 100;
		calc.centerX = calc.chartWidth / 2;

		//********************  LAYOUTS  ***********************
		const layouts: Layouts = {
			treemap: null
		}
		attrs.layouts = layouts;

		// Generate tree layout function
		layouts.treemap = d3.tree()
			.size([calc.chartWidth, calc.chartHeight])
			.separation((a, b) => {
				return a.parent == b.parent ? 1 : 1.1;
			})
			.nodeSize([calc.nodeMaxWidth + 100, calc.nodeMaxHeight + attrs.depth])

		// ******************* BEHAVIORS . **********************
		this.behaviors = {
			zoom: null
		}

		// Get zooming function
		this.behaviors.zoom = d3.zoom<SVGElement, void>().on("zoom", () => this.zoomed())

		//****************** ROOT node work ************************

		// Convert flat data to hierarchical
		attrs.root = d3.stratify()
			.id(({
				     id
			     }) => id)
			.parentId(({
				           parentId
			           }) => parentId)
			(attrs.data)

		// Set child nodes enter appearance positions
		attrs.root.x0 = 0;
		attrs.root.y0 = 0;

		/** Get all nodes as array (with extended parent & children properties set)
		 This way we can access any node's parent directly using node.parent - pretty cool, huh?
		 */
		attrs.allNodes = attrs.layouts.treemap(attrs.root).descendants()

		// Assign direct children and total subordinate children's cound
		attrs.allNodes.forEach((d: HierarchyNode) => {
			Object.assign(d.data, {
				directSubordinates: d.children ? d.children.length : 0,
				totalSubordinates: d.descendants().length - 1
			})
		})

		// Collapse all children at first
		attrs.root.children && attrs.root.children.forEach((d: HierarchyNode) => this.collapse(d));

		// Then expand some nodes, which have `expanded` property set
		attrs.root.children && attrs.root.children.forEach((d: HierarchyNode) => this.expandSomeNodes(d));

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

		// Add one more container g element, for better positioning controls
		attrs.centerG = patternify(this.chart, {
			tag: 'g',
			selector: 'center-group'
		})
			.attr('transform', `translate(${calc.centerX},${calc.nodeMaxHeight / 2})`);

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
		this.update(attrs.root)


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


	// This function can be invoked via chart.addNode API, and it adds node in tree at runtime
	addNode(obj: UiTreeGraphNodeConfig) {
		const attrs = this.getChartState();
		attrs.data.push(obj);

		// Update state of nodes and redraw graph
		this.updateNodesState();
		return this;
	}

	// This function can be invoked via chart.removeNode API, and it removes node from tree at runtime
	removeNode(nodeId: string) {
		const attrs = this.getChartState();
		const node = attrs.allNodes.filter(({data}: HierarchyNode) => data.id == nodeId)[0];

		// Remove all node children
		if (node) {
			// Retrieve all children nodes ids (including current node itself)
			const nodeChildrenIds = this.getNodeChildrenIds(node, []);

			// Filter out retrieved nodes and reassign data
			attrs.data = attrs.data.filter(d => !nodeChildrenIds.includes(d.id))

			const updateNodesState = this.updateNodesState.bind(this);
			// Update state of nodes and redraw graph
			updateNodesState();
		}
	}

	// This function basically redraws visible graph, based on nodes state
	update({
		       x0,
		       y0,
		       x,
		       y
	       }: HierarchyNode) {

		const attrs = this.getChartState();
		const calc = attrs.calc;

		//  Assigns the x and y position for the nodes
		const treeData = attrs.layouts.treemap(attrs.root);

		// Get tree nodes and links and attach some properties
		const nodes = treeData.descendants()
			.map((d: HierarchyNode) => {
				// If at least one property is already set, then we don't want to reset other properties
				if (d.width) return d;

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
				});
			});

		// Get all links
		const links = treeData.descendants().slice(1);

		// Set constant depth for each nodes
		nodes.forEach((d: HierarchyNode) => d.y = d.depth * attrs.depth);

		// ------------------- FILTERS ---------------------

		// Add patterns for each node (it's needed for rounded image implementation)
		const patternsSelection = attrs.defs.selectAll('.pattern')
			.data(nodes, ({
				              id
			              }: HierarchyNode) => id);

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
			             }: HierarchyNode) => id)

		// Add images to patterns
		const patternImages = patternify(patterns, {
			tag: 'image',
			selector: 'pattern-image',
			data: (d: HierarchyNode) => [d]
		})
			.attr('x', 0)
			.attr('y', 0)
			.attr('height', ({
				                 imageWidth
			                 }: HierarchyNode) => imageWidth)
			.attr('width', ({
				                imageHeight
			                }: HierarchyNode) => imageHeight)
			.attr('xlink:href', ({
				                     data
			                     }: HierarchyNode) => data.image && data.image.url)
			.attr('viewbox', ({
				                  imageWidth,
				                  imageHeight
			                  }: HierarchyNode) => `0 0 ${imageWidth * 2} ${imageHeight}`)
			.attr('preserveAspectRatio', 'xMidYMin slice')

		// Remove patterns exit selection after animation
		patternsSelection.exit().transition().duration(attrs.duration).remove();

		// --------------------------  LINKS ----------------------
		// Get links selection
		const linkSelection = attrs.centerG.selectAll('path.link')
			.data(links, ({
				              id
			              }: HierarchyNode) => id);

		// Enter any new links at the parent's previous position.
		const linkEnter = linkSelection.enter()
			.insert('path', "g")
			.attr("class", "link")
			.attr('d', (d: HierarchyNode) => {
				const o = {
					x: x0,
					y: y0
				};
				//@ts-ignore
				return this.diagonal(o, o)
			});

		// Get links update selection
		const linkUpdate = linkEnter.merge(linkSelection);

		// Styling links
		linkUpdate
			.attr("fill", "none")
			.attr("stroke-width", ({
				                       data
			                       }: HierarchyNode) => data.connectorLineWidth || 2)
			.attr('stroke', ({
				                 data
			                 }: HierarchyNode) => {
				if (data.connectorLineColor) {
					return this.rgbaObjToColor(data.connectorLineColor);
				}
				return 'green';
			})
			.attr('stroke-dasharray', ({
				                           data
			                           }: HierarchyNode) => {
				if (data.dashArray) {
					return data.dashArray;
				}
				return '';
			})

		// Transition back to the parent element position
		linkUpdate.transition()
			.duration(attrs.duration)
			.attr('d', (d: HierarchyNode) => this.diagonal(d, d.parent));

		// Remove any  links which is exiting after animation
		const linkExit = linkSelection.exit().transition()
			.duration(attrs.duration)
			.attr('d', () => {
				const o = {
					x: x,
					y: y
				};
				//@ts-ignore
				return this.diagonal(o, o)
			})
			.remove();

		// --------------------------  NODES ----------------------
		// Get nodes selection
		const nodesSelection = attrs.centerG.selectAll('g.node')
			.data(nodes, ({
				              id
			              }: HierarchyNode) => id)

		// Enter any new nodes at the parent's previous position.
		const nodeEnter = nodesSelection.enter().append('g')
			.attr('class', 'node')
			.attr("transform", () => `translate(${x0},${y0})`)
			.attr('cursor', 'pointer')
			.on('click', ({
				              data
			              }: HierarchyNode) => {
				if (d3.event.srcElement.classList.contains('node-button-circle')) {
					return;
				}
				attrs.onNodeClick(data.id);
			});

		// Add background rectangle for the nodes
		patternify(nodeEnter, {
				tag: 'rect',
				selector: 'node-rect',
				data: (d: HierarchyNode) => [d]
			})
			.style("fill", ({
				                _children
			                }: HierarchyNode) => _children ? "lightsteelblue" : "#fff")

		// Add node icon image inside node
		patternify(nodeEnter, {
				tag: 'image',
				selector: 'node-icon-image',
				data: (d: HierarchyNode) => d.data.icon ? [d] : []
			})
			.attr('width', ({
				                data
			                }: HierarchyNode) => data.icon.size)
			.attr('height', ({
				                 data
			                 }: HierarchyNode) => data.icon.size)
			.attr("xlink:href", ({
				                     data
			                     }: HierarchyNode) => this.context.getIconPath(data.icon.icon, data.icon.size))
			.attr('x', ({
				            width, data
			            }: HierarchyNode) => -width / 2 - data.icon.size / 2)
			.attr('y', ({
				            height,
				            data
			            }: HierarchyNode) => -height / 2 - data.icon.size / 2)

		// Defined node images wrapper group
		const imageGroups = patternify(nodeEnter, {
			tag: 'g',
			selector: 'node-image-group',
			data: (d: HierarchyNode) => [d]
		})

		// Add background rectangle for node image
		patternify(imageGroups, {
				tag: 'rect',
				selector: 'node-image-rect',
				data: (d: HierarchyNode) => [d]
			})

		// Node update styles
		const nodeUpdate = nodeEnter.merge(nodesSelection)
			.style('font', '12px sans-serif');


		// Add foreignObject element inside rectangle
		const fo = patternify(nodeUpdate, {
				tag: 'foreignObject',
				selector: 'node-foreign-object',
				data: (d: HierarchyNode) => [d]
			})


		// Add foreign object
		patternify(fo, {
			tag: 'xhtml:div',
			selector: 'node-foreign-object-div',
			data: (d: HierarchyNode) => [d]
		})

		this.restyleForeignObjectElements();


		// Add Node button circle's group (expand-collapse button)
		const nodeButtonGroups = patternify(nodeEnter, {
				tag: 'g',
				selector: 'node-button-g',
				data: (d: HierarchyNode) => [d]
			})
        .on('mousedown', (d: HierarchyNode) => {
	        let expanding = !d.children;
        	this.onButtonClick(d);
	        attrs.onNodeExpandedOrCollapsed(d.data.id, expanding);
        })

		// Add expand collapse button circle
		patternify(nodeButtonGroups, {
				tag: 'circle',
				selector: 'node-button-circle',
				data: (d: HierarchyNode) => [d]
			})

		// Add button text
		patternify(nodeButtonGroups, {
				tag: 'text',
				selector: 'node-button-text',
				data: (d: HierarchyNode) => [d]
			})
			.attr('pointer-events', 'none')

		// Transition to the proper position for the node
		nodeUpdate.transition()
			.attr('opacity', 0)
			.duration(attrs.duration)
			.attr("transform", ({
				                    x,
				                    y
			                    }: HierarchyNode) => `translate(${x},${y})`)
			.attr('opacity', 1)

		// Move images to desired positions
		nodeUpdate.selectAll('.node-image-group')
			.attr('transform', ({
				                    imageWidth,
				                    width,
				                    imageHeight,
				                    height
			                    }: HierarchyNode) => {
				let x = -imageWidth / 2 - width / 2;
				let y = -imageHeight / 2 - height / 2;
				return `translate(${x},${y})`
			})

		// Style node image rectangles
		nodeUpdate.select('.node-image-rect')
			.attr('fill', ({
				               id
			               }: HierarchyNode) => `url(#${id})`)
			.attr('width', ({
				                imageWidth
			                }: HierarchyNode) => imageWidth)
			.attr('height', ({
				                 imageHeight
			                 }: HierarchyNode) => imageHeight)
			.attr('stroke', ({
				                 imageBorderColor
			                 }: HierarchyNode) => imageBorderColor)
			.attr('stroke-width', ({
				                       imageBorderWidth
			                       }: HierarchyNode) => imageBorderWidth)
			.attr('rx', ({
				             imageRx
			             }: HierarchyNode) => imageRx)
			.attr('y', ({
				            imageCenterTopDistance
			            }: HierarchyNode) => imageCenterTopDistance)
			.attr('x', ({
				            imageCenterLeftDistance
			            }: HierarchyNode) => imageCenterLeftDistance)
			.attr('filter', ({
				                 dropShadowId
			                 }: HierarchyNode) => dropShadowId)

		// Style node rectangles
		nodeUpdate.select('.node-rect')
			.attr('width', ({
				                data
			                }: HierarchyNode) => data.width)
			.attr('height', ({
				                 data
			                 }: HierarchyNode) => data.height)
			.attr('x', ({
				            data
			            }: HierarchyNode) => -data.width / 2)
			.attr('y', ({
				            data
			            }: HierarchyNode) => -data.height / 2)
			.attr('rx', ({
				             data
			             }: HierarchyNode) => data.borderRadius || 0)
			.attr('stroke-width', ({
				                       data
			                       }: HierarchyNode) => data.borderWidth || attrs.strokeWidth)
			.attr('cursor', 'pointer')
			.attr('stroke', ({
				                 borderColor
			                 }: HierarchyNode) => borderColor)
			.style("fill", ({
				                backgroundColor
			                }: HierarchyNode) => backgroundColor)

		// Move node button group to the desired position
		nodeUpdate.select('.node-button-g')
			.attr('transform', ({
				                    data
			                    }: HierarchyNode) => `translate(0,${data.height / 2})`)
			.attr('display', ({
				                  children,
				                  _children
			                  }: HierarchyNode) => {
				if (children || _children) {
					return "inherit";
				}
				return "none";
			})

		// Restyle node button circle
		nodeUpdate.select('.node-button-circle')
			.attr('r', 10)
			.attr('stroke-width', ({
				                       data
			                       }: HierarchyNode) => data.borderWidth || attrs.strokeWidth)
			.attr('fill', "white")
			.attr('stroke', ({
				                 borderColor
			                 }: HierarchyNode) => borderColor)

		// Restyle button texts
		nodeUpdate.select('.node-button-text')
			.attr('text-anchor', 'middle')
			.attr('alignment-baseline', 'middle')
			.attr('fill', attrs.defaultTextFill)
			.attr('font-size', ({
				                    children
			                    }: HierarchyNode) => {
				if (children) return 30;
				return 20;
			})
			.text(({
				       children
			       }: HierarchyNode) => {
				if (children) return '-';
				return '+';
			})
			.attr('y', this.isEdge() ? 10 : 0)

		// Remove any exiting nodes after transition
		const nodeExitTransition = nodesSelection.exit()
			.attr('opacity', 1)
			.transition()
			.duration(attrs.duration)
			.attr("transform", (d: HierarchyNode) => `translate(${x},${y})`)
			.on('end', function () {
				d3.select(this).remove();
			})
			.attr('opacity', 0);

		// On exit reduce the node rects size to 0
		nodeExitTransition.selectAll('.node-rect')
			.attr('width', 10)
			.attr('height', 10)
			.attr('x', 0)
			.attr('y', 0);

		// On exit reduce the node image rects size to 0
		nodeExitTransition.selectAll('.node-image-rect')
			.attr('width', 10)
			.attr('height', 10)
			.attr('x', ({
				            width
			            }: HierarchyNode) => width / 2)
			.attr('y', ({
				            height
			            }: HierarchyNode) => height / 2)

		// Store the old positions for transition.
		nodes.forEach((d: HierarchyNode) => {
			d.x0 = d.x;
			d.y0 = d.y;
		});
	}

	// This function detects whether current browser is edge
	isEdge() {
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

	// Generate custom diagonal - play with it here - https://to.ly/1zhTK
	diagonal(s: HierarchyNode, t: HierarchyNode) {

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
		const path = `
           M ${x} ${y}
           L ${x} ${y + h * yrvs}
           C  ${x} ${y + h * yrvs + r * yrvs} ${x} ${y + h * yrvs + r * yrvs} ${x + r * xrvs} ${y + h * yrvs + r * yrvs}
           L ${x + w * xrvs + r * xrvs} ${y + h * yrvs + r * yrvs}
           C ${ex}  ${y + h * yrvs + r * yrvs} ${ex}  ${y + h * yrvs + r * yrvs} ${ex} ${ey - h * yrvs}
           L ${ex} ${ey}
         `
		// Return result
		return path;
	}

	restyleForeignObjectElements() {
		const attrs = this.getChartState();

		attrs.svg.selectAll('.node-foreign-object')
			.attr('width', ({
				                width
			                }: HierarchyNode) => width)
			.attr('height', ({
				                 height
			                 }: HierarchyNode) => height)
			.attr('x', ({
				            width
			            }: HierarchyNode) => -width / 2)
			.attr('y', ({
				            height
			            }: HierarchyNode) => -height / 2)
		attrs.svg.selectAll('.node-foreign-object-div')
			.style('width', ({
				                 width
			                 }: HierarchyNode) => `${width}px`)
			.style('height', ({
				                  height
			                  }: HierarchyNode) => `${height}px`)
			.html(({
				       data
			       }: HierarchyNode) => this.context.templateRegistry.createTemplateRenderer(data.template).render(data.record.values))
			.select('*')
			.style('display', 'inline-grid')
	}

	// Toggle children on click.
	onButtonClick(d: HierarchyNode) {

		// If childrens are expanded
		if (d.children) {

			//Collapse them
			d._children = d.children;
			d.children = null;

			// Set descendants expanded property to false
			this.setExpansionFlagToChildren(d, false);
		} else {

			// Expand children
			d.children = d._children;
			d._children = null;

			// Set each children as expanded
			d.children.forEach(({
				                    data
			                    }: HierarchyNode) => data.expanded = true)
		}

		// Redraw Graph
		this.update(d);
	}

	// This function changes `expanded` property to descendants
	setExpansionFlagToChildren({
		                           data,
		                           children,
		                           _children
	                           }: HierarchyNode, flag: boolean) {

		// Set flag to the current property
		data.expanded = flag;

		// Loop over and recursively update expanded children's descendants
		if (children) {
			children.forEach(d => {
				this.setExpansionFlagToChildren(d, flag)
			})
		}

		// Loop over and recursively update collapsed children's descendants
		if (_children) {
			_children.forEach(d => {
				this.setExpansionFlagToChildren(d, flag)
			})
		}
	}

	// This function can be invoked via chart.setExpanded API, it expands or collapses particular node
	setExpanded(id: string, expandedFlag: boolean) {
		const attrs = this.getChartState();
		// Retrieve node by node Id
		const node = attrs.allNodes.filter(({
			                                    data
		                                    }: HierarchyNode) => data.id == id)[0]

		// If node exists, set expansion flag
		if (node) node.data.expanded = expandedFlag;

		// First expand all nodes
		attrs.root.children.forEach((d: HierarchyNode) => this.expand(d));

		// Then collapse all nodes
		attrs.root.children.forEach((d: HierarchyNode) => this.collapse(d));

		// Then expand only the nodes, which were previously expanded, or have an expand flag set
		attrs.root.children.forEach((d: HierarchyNode) => this.expandSomeNodes(d));

		// Redraw graph
		this.update(attrs.root);
	}

	// Method which only expands nodes, which have property set "expanded=true"
	expandSomeNodes(d: HierarchyNode) {

		// If node has expanded property set
		if (d.data.expanded) {

			// Retrieve node's parent
			let parent = d.parent;

			// While we can go up
			while (parent) {

				// Expand all current parent's children
				if (parent._children) {
					parent.children = parent._children;
				}

				// Replace current parent holding object
				parent = parent.parent;
			}
		}

		// Recursivelly do the same for collapsed nodes
		if (d._children) {
			d._children.forEach((ch: HierarchyNode) => this.expandSomeNodes(ch));
		}

		// Recursivelly do the same for expanded nodes
		if (d.children) {
			d.children.forEach((ch: HierarchyNode) => this.expandSomeNodes(ch));
		}
	}


	// This function updates nodes state and redraws graph, usually after data change
	updateNodesState() {
		const attrs = this.getChartState();
		// Store new root by converting flat data to hierarchy
		attrs.root = d3.stratify()
			.id(({
				     id
			     }) => id)
			.parentId(({
				           parentId
			           }) => parentId)
			(attrs.data)

		// Store positions, where children appear during their enter animation
		attrs.root.x0 = 0;
		attrs.root.y0 = 0;

		// Store all nodes in flat format (although, now we can browse parent, see depth e.t.c. )
		attrs.allNodes = attrs.layouts.treemap(attrs.root).descendants()

		// Store direct and total descendants count
		attrs.allNodes.forEach((d: HierarchyNode) => {
			Object.assign(d.data, {
				directSubordinates: d.children ? d.children.length : 0,
				totalSubordinates: d.descendants().length - 1
			})
		})

		// Expand all nodes first
		attrs.root.children.forEach(this.expand);

		// Then collapse them all
		attrs.root.children.forEach((d: HierarchyNode) => this.collapse(d));

		// Then only expand nodes, which have expanded proprty set to true
		attrs.root.children.forEach((ch: HierarchyNode) => this.expandSomeNodes(ch));

		// Redraw Graphs
		this.update(attrs.root)
	}


	// Function which collapses passed node and it's descendants
	collapse(d: HierarchyNode) {
		if (d.children) {
			d._children = d.children;
			d._children.forEach(ch => this.collapse(ch));
			d.children = null;
		}
	}

	// Function which expands passed node and it's descendants
	expand(d: HierarchyNode) {
		if (d._children) {
			d.children = d._children;
			d.children.forEach(ch => this.expand(ch));
			d._children = null;
		}
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

export function patternify<E extends BaseType, ED, P extends BaseType = null, PD = null>(container: Selection<P, PD, any, any>, params: PatternifyParameter) {
	var selector = params.selector;
	var elementTag = params.tag;
	var data = params.data || [selector];

	// Pattern in action
	var selection = container.selectAll<E, ED>('.' + selector).data(data, (d: any, i: number) => {
		if (typeof d === 'object') {
			if (d.id) {
				return d.id;
			}
		}
		return i;
	}) as Selection<E, ED, P, PD>;
	selection.exit().remove();
	selection = selection.enter().append<E>(elementTag).merge(selection);
	selection.attr('class', selector);
	return selection;
}


module.exports = TreeChart;