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
import {BaseType, HierarchyNode, HierarchyPointLink, HierarchyPointNode, Selection, ZoomBehavior} from "d3";
import {UiColorConfig} from '../generated/UiColorConfig';
import {AbstractUiComponent} from "./AbstractUiComponent";
import {
	UiTreeGraph_NodeClickedEvent,
	UiTreeGraph_NodeExpandedOrCollapsedEvent,
	UiTreeGraph_ParentExpandedOrCollapsedEvent,
	UiTreeGraph_SideListExpandedOrCollapsedEvent,
	UiTreeGraphCommandHandler,
	UiTreeGraphConfig,
	UiTreeGraphEventSource
} from "../generated/UiTreeGraphConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiTreeGraphNodeConfig} from "../generated/UiTreeGraphNodeConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiTreeGraphNodeImage_CornerShape} from "../generated/UiTreeGraphNodeImageConfig";
import {parseHtml} from "./Common";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {flextree, FlexTreeLayout} from "d3-flextree";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";

export class UiTreeGraph extends AbstractUiComponent<UiTreeGraphConfig> implements UiTreeGraphCommandHandler, UiTreeGraphEventSource {

	public readonly onNodeClicked: TeamAppsEvent<UiTreeGraph_NodeClickedEvent> = new TeamAppsEvent(this);
	public readonly onNodeExpandedOrCollapsed: TeamAppsEvent<UiTreeGraph_NodeExpandedOrCollapsedEvent> = new TeamAppsEvent(this);
	public readonly onParentExpandedOrCollapsed: TeamAppsEvent<UiTreeGraph_ParentExpandedOrCollapsedEvent> = new TeamAppsEvent(this);
	public readonly onSideListExpandedOrCollapsed: TeamAppsEvent<UiTreeGraph_SideListExpandedOrCollapsedEvent> = new TeamAppsEvent(this);

	private chart: TreeChart;
	private $main: HTMLElement;

	constructor(config: UiTreeGraphConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiTreeGraph">`);
		this.chart = new TreeChart(context)
			.container(this.$main);
		this.update(config);
		this.moveToRootNode();
		(window as any).moveToRootNode = () => this.moveToRootNode();
	}

	public update(config: UiTreeGraphConfig) {
		this.chart
			.backgroundColor(createUiColorCssString(config.backgroundColor))
			.initialZoom(config.zoomFactor)
			.data(config.nodes)
			.compact(config.compact)
			.verticalLayerGap(config.verticalLayerGap)
			.horizontalSiblingGap(config.horizontalSiblingGap)
			.horizontalNonSignlingGap(config.horizontalNonSignlingGap)
			.sideListIndent(config.sideListIndent)
			.sideListVerticalGap(config.sideListVerticalGap)
			.onNodeClick((nodeId: string) => {
				this.onNodeClicked.fire({nodeId: nodeId});
			})
			.onNodeExpandedOrCollapsed((nodeId: string, expanded: boolean, lazyLoad: boolean) => {
				this.onNodeExpandedOrCollapsed.fire({nodeId, expanded, lazyLoad});
			})
			.onParentExpandedOrCollapsed((nodeId: string, expanded: boolean, lazyLoad: boolean) => {
				this.onParentExpandedOrCollapsed.fire({nodeId, expanded, lazyLoad});
			})
			.onSideListExpandedOrCollapsed((nodeId: string, expanded: boolean) => {
				this.onSideListExpandedOrCollapsed.fire({nodeId, expanded});
			});
		this.chart.render();
	}

	@executeWhenFirstDisplayed(true)
	public moveToRootNode() {
		this.onResize();
		this.chart.moveToRootNode(400);
	}

	@executeWhenFirstDisplayed(true)
	public moveToNode(nodeId: string) {
		this.onResize();
		this.chart.moveToNode(nodeId);
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

	updateNode(node: UiTreeGraphNodeConfig): void {
		let index = this.chart.data().findIndex(n => n.id === node.id);
		if (index !== -1) {
			this.chart.data()[index] = node;
		} else {
			this.chart.data().push(node);
		}
		this.chart.render();
	}

	setZoomFactor(zoomFactor: number): void {
		this.chart.setZoomFactor(zoomFactor);
	}

	doGetMainElement(): HTMLElement {
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

	compact(compact: boolean): this;

	verticalLayerGap(): number,

	verticalLayerGap(verticalLayerGap: number): this,

	horizontalSiblingGap(): number,

	horizontalSiblingGap(horizontalSiblingGap: number): this,

	horizontalNonSignlingGap(): number,

	horizontalNonSignlingGap(horizontalNonSignlingGap: number): this,

	sideListIndent(): number,

	sideListIndent(sideListIndent: number): this,

	sideListVerticalGap(): number,

	sideListVerticalGap(sideListVerticalGap: number): this,

	onNodeClick(): (nodeId: string) => void;

	onNodeClick(onNodeClick: (nodeId: string) => void): TreeChart;

	onNodeExpandedOrCollapsed(): (nodeId: string, expanded: boolean, lazyLoad: boolean) => void;

	onNodeExpandedOrCollapsed(onNodeExpandedOrCollapsed: (nodeId: string, expanded: boolean, lazyLoad: boolean) => void): TreeChart;

	onParentExpandedOrCollapsed(): (nodeId: string, expanded: boolean, lazyLoad: boolean) => void;

	onParentExpandedOrCollapsed(onParentExpandedOrCollapsed: (nodeId: string, expanded: boolean, lazyLoad: boolean) => void): TreeChart;

	onSideListExpandedOrCollapsed(): (nodeId: string, expanded: boolean) => void;

	onSideListExpandedOrCollapsed(onSideListExpandedOrCollapsed: (nodeId: string, expanded: boolean) => void): TreeChart;

}

interface TreeNode extends HierarchyPointNode<UiTreeGraphNodeConfig> {
	hasChildren?: boolean,
	sideListNodes: TreeNodeLike[]
}

interface TreeNodeLike {
	data: UiTreeGraphNodeConfig,
	id?: string,
	hasChildren?: boolean,
	parent?: TreeNode | null,
	x: number,
	y: number
}

export interface Layouts {
	treemap: FlexTreeLayout<UiTreeGraphNodeConfig>
}

export interface TreeChartAttributes {
	[key: string]: any,

	id: string,
	data?: UiTreeGraphNodeConfig[],
	root: TreeNode,
	svg: Selection<SVGElement, any, any, any>,
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
	onNodeExpandedOrCollapsed?: (nodeId: string, expanded: boolean, lazyLoad: boolean) => void,
	onParentExpandedOrCollapsed?: (nodeId: string, expanded: boolean, lazyLoad: boolean) => void,
	onSideListExpandedOrCollapsed?: (nodeId: string, expanded: boolean) => void,
	verticalLayerGap: number,
	horizontalSiblingGap: number,
	horizontalNonSignlingGap: number,
	sideListIndent: number,
	sideListVerticalGap: number,
	defs: Selection<BaseType, unknown, SVGElement, void>,
	compact: boolean,
}

export interface PatternifyParameter {
	selector: string,
	tag: string,
	data?: any
}

class TreeChart {
	private chart: Selection<SVGGElement, void, SVGElement, void>;
	private zoomBehavior: ZoomBehavior<SVGElement, void>;

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
			verticalLayerGap: 36,
			horizontalSiblingGap: 20,
			horizontalNonSignlingGap: 36,
			sideListIndent: 20,
			sideListVerticalGap: 20,
			compact: false,
			onNodeClick: (): void => {
			},
			onNodeExpandedOrCollapsed: () => {
			},
			onParentExpandedOrCollapsed: () => {
			},
			onSideListExpandedOrCollapsed: () => {
			}
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

		//Drawing containers
		const container: Selection<Element, void, null, void> = d3.select(attrs.container);
		const containerRect = container.node().getBoundingClientRect();
		if (containerRect.width > 0) attrs.svgWidth = containerRect.width;
		if (containerRect.height > 0) attrs.svgHeight = containerRect.height;

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

		// ******************* BEHAVIORS . **********************
		this.zoomBehavior = d3.zoom<SVGElement, void>().on("zoom", () => this.zoomed());

		// *************************  DRAWING **************************
		//Add svg

		const svg = patternify<SVGElement, void, Element, void>(container, {
			tag: 'svg',
			selector: 'svg-chart-container'
		})
			.attr('width', attrs.svgWidth)
			.attr('height', attrs.svgHeight)
			.attr('font-family', attrs.defaultFont)
			.call(this.zoomBehavior)
			.style('background-color', attrs.backgroundColor);
		attrs.svg = svg;

		//Add container g element
		this.chart = patternify<SVGGElement, void, SVGElement, void>(svg, {
			tag: 'g',
			selector: 'chart'
		});
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
			.attr("y", 8);

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
			.attr('in', 'offsetBlur');

		// Add another feMergeNode element for shadows
		patternify(feMerge, {
			tag: 'feMergeNode',
			selector: 'feMergeNode-graphic'
		})
			.attr('in', 'SourceGraphic');

		// Display tree contenrs
		this.update();


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
		const attrs = this.getChartState();

		let visibleNodeRecords = this.getVisibleNodeRecords(attrs.data);
		let visibleNodeRecordsInOrder = attrs.data.filter(r => visibleNodeRecords[r.id] != null);

		// Store new root by converting flat data to hierarchy
		let hierarchy = d3.stratify<UiTreeGraphNodeConfig>()
			.id((d: UiTreeGraphNodeConfig) => d.id)
			.parentId((d: UiTreeGraphNodeConfig) => visibleNodeRecords[d.parentId] != null ? d.parentId : null)
			(visibleNodeRecordsInOrder) as TreeNode;
		attrs.root = hierarchy;

		let recordsByParentId = getById(attrs.data.filter(r => r.id !== attrs.root.id), n => n.parentId);
		attrs.root.eachBefore(n => {
			n.hasChildren = recordsByParentId[n.id] != null || (n.data as any).hasLazyChildren;
		});

		let layerHeightByDepth: number[];
		if (!attrs.compact) {
			layerHeightByDepth = hierarchy.descendants().reduce((heightsByDepth: number[], d: TreeNode) => {
				let nodeHeight = this.calculateNodeSize(d).height;
				if (heightsByDepth[d.depth] == null || heightsByDepth[d.depth] < nodeHeight) {
					heightsByDepth[d.depth] = nodeHeight;
				}
				return heightsByDepth;
			}, []);
		}

		//  Assigns the x and y position for the nodes
		// Generate tree layout function
		let treeLayout = flextree<UiTreeGraphNodeConfig>()
			.nodeSize(node => {
				let width = node.data.width;
				if (node.data.sideListExpanded && node.data.sideListNodes && node.data.sideListNodes.length > 0) {
					let sideListOverlapping = attrs.sideListIndent + this.calculateSideListSize(node).width - (1/5) * width;
					width = width + Math.max(0, sideListOverlapping * 2);
				}
				return [width, attrs.compact ? this.calculateNodeSize(node).height : layerHeightByDepth[node.depth]];
			})
			.spacing((node1, node2) => {
				// Calculates only the horizontal spacing! The layout algorithm only honors the node's height for vertical spacing!
				if (node1.depth !== node2.depth || node1.path(node2).length > 3) {
					return attrs.horizontalNonSignlingGap;
				} else { // siblings
					return attrs.horizontalSiblingGap;
				}
			});
		const treeData = treeLayout(attrs.root);

		treeData.each((d: TreeNode) => {
			if (d.data.sideListNodes != null && d.data.sideListNodes.length > 0 && d.data.sideListExpanded) {
				let currentY = 0;
				d.sideListNodes = d.data.sideListNodes.map(r => {
					const treeNodeLike = {
						data: r,
						id: r.id,
						x: attrs.sideListIndent,
						y: currentY + attrs.sideListVerticalGap
					};
					currentY += r.height + attrs.sideListVerticalGap;
					return treeNodeLike;
				});
			}
		});

		// Get tree nodes and links and attach some properties
		const nodes = treeData.descendants() as TreeNode[];

		// ------------------- FILTERS ---------------------

		this.drawLinks(this.chart, attrs.root.links());
		const nodesSelection = this.drawNodes(this.chart, () => nodes);

		let sideListG = nodesSelection.selectAll<SVGGElement, any>(':scope > g.side-list-g')
			.data(d => [d])
			.join(enter => enter
				.append('g')
				.classed('side-list-g', true)
			)
			.attr('transform', d => `translate(${d.data.width * 4 / 5},${d.data.height})`);
		const sideListLinkSelection = sideListG.selectAll(':scope > .side-list-line')
			.data((d: TreeNode) => {
				return (d.data.sideListExpanded && d.sideListNodes) || [];
			})
			.join(
				enter => enter
					.insert('path', "path")
					.attr("class", "side-list-line")
					.attr('d', d => this.hookLine({x: 0, y: 0}, {x: 0, y: 0})),
				update => update,
				exit => exit.transition()
					.duration(attrs.duration)
					.attr('d', d => this.hookLine({x: 0, y: 0}, {x: 0, y: 0}))
					.remove()
			)
			.attr("fill", "none")
			.attr("stroke-width", d => d.data.connectorLineWidth || 2)
			.attr('stroke', d => d.data.connectorLineColor ? this.rgbaObjToColor(d.data.connectorLineColor) : 'white')
			.attr('stroke-dasharray', d => d.data.dashArray ? d.data.dashArray : '')
			.transition()
			.duration(attrs.duration)
			.attr('d', (d, i, groups) => {
				return this.hookLine({x: 0, y: 0}, {x: d.x, y: d.y + d.data.height / 2})
			});
		this.drawNodes(sideListG, (d: TreeNode) => d.sideListNodes || []);

		this.drawExpanderButton(
			nodesSelection,
			'node-button-g',
			(d: TreeNodeLike) => d.hasChildren,
			d => ({x: d.data.width / 2, y: d.data.height}),
			(d: TreeNodeLike) => d.data.expanded,
			(d: TreeNode) => {
				d.data.expanded = !d.data.expanded;
				attrs.onNodeExpandedOrCollapsed(d.data.id, d.data.expanded, d.data.expanded && d.data.hasLazyChildren && attrs.data.filter(c => c.parentId === d.id).length == 0);
				this.update();
			}
		);

		this.drawExpanderButton(
			nodesSelection,
			'node-side-list-expander-g',
			(d: TreeNodeLike) => d.data.sideListNodes && d.data.sideListNodes.length > 0,
			(d: TreeNodeLike) => ({x: d.data.width * 4 / 5, y: d.data.height}),
			(d: TreeNodeLike) => d.data.sideListExpanded,
			(d: TreeNode) => {
				d.data.sideListExpanded = !d.data.sideListExpanded;
				attrs.onSideListExpandedOrCollapsed(d.data.id, d.data.sideListExpanded);
				this.update();
			}
		);

		this.drawExpanderButton(
			nodesSelection,
			'lazy-parent-expander-g',
			(d: TreeNodeLike) => {
				return d.data.parentExpandable;
			},
			(d: TreeNodeLike) => ({x: d.data.width / 2, y: 0}),
			(d: TreeNodeLike) => d.parent != null,
			(d: TreeNode) => {
				d.data.parentExpanded = !d.data.parentExpanded;
				attrs.onParentExpandedOrCollapsed(d.data.id, d.data.parentExpanded, attrs.data.filter(c => c.id === d.data.parentId).length == 0);
				this.update();
			}
		);
	}

	drawExpanderButton(
		parentSelection: Selection<SVGElement, TreeNodeLike, any, any>,
		cssClassName: string,
		visible: (d: TreeNodeLike) => boolean,
		position: (d: TreeNodeLike) => { x: number; y: any },
		expanded: (d: TreeNodeLike) => boolean,
		onMouseDown: (d: TreeNode) => void
	) {
		const attrs = this.getChartState();
// Add Node button circle's group (expand-collapse button)
		const childrenExpanderButtonG = parentSelection.selectAll(':scope > g.' + cssClassName)
			.data(d => visible(d) ? [d] : [])
			.join(enter => enter
				.append("g")
				.classed(cssClassName, true)
				.on('mousedown', onMouseDown)
			)
			.attr('transform', d => {
				let pos = position(d);
				return `translate(${pos.x},${pos.y})`;
			});

		// Add expand collapse button circle
		childrenExpanderButtonG.selectAll(':scope > circle.node-button-circle')
			.data(d => [d])
			.join(enter => enter
				.append("circle")
				.classed("node-button-circle", true)
			)
			.attr('r', 10)
			.attr('stroke-width', d => d.data.borderWidth || attrs.strokeWidth)
			.attr('fill', "white")
			.attr('stroke', d => createUiColorCssString(d.data.borderColor));

// Add button text
		childrenExpanderButtonG.selectAll(':scope > text.node-button-text')
			.data(d => [d])
			.join(enter => enter
				.append("text")
				.classed("node-button-text", true)
			)
			.attr('text-anchor', 'middle')
			.attr('alignment-baseline', 'middle')
			.attr('fill', attrs.defaultTextFill)
			.attr('font-size', d => expanded(d) ? 30 : 20)
			.text(d => expanded(d) ? '-' : '+')
			.attr('y', this.isEdge() ? 10 : 0);

		return childrenExpanderButtonG;
	}

	calculateNodeSize(d: HierarchyNode<UiTreeGraphNodeConfig>) {
		let attrs = this.getChartState();
		let nodeWidth = d.data.width;
		let nodeHeight = d.data.height;
		if (d.data.sideListNodes != null && d.data.sideListNodes.length > 0 && d.data.sideListExpanded) {
			let sideListSize = this.calculateSideListSize(d);
			nodeWidth = nodeWidth * (4 / 5) + sideListSize.width;
			nodeHeight += sideListSize.height;
		}
		nodeHeight += attrs.verticalLayerGap;
		return {width: nodeWidth, height: nodeHeight};
	}

	calculateSideListSize(d: HierarchyNode<UiTreeGraphNodeConfig>) {
		return d.data.sideListNodes.reduce((size, node) => {
			size.width = Math.max(size.width, node.width);
			size.height += node.height + this.getChartState().sideListVerticalGap;
			return size;
		}, {width: 0, height: 0});
	}

	private drawNodes(parentSelection: Selection<SVGElement, any, any, any>, dataFunction: (d: any) => TreeNodeLike[]) {
		const attrs = this.getChartState();

		// Add patterns for each node (it's needed for rounded image implementation)
		let allNormalNodes = attrs.root.descendants();
		let allSideListNodes = attrs.root.descendants().flatMap(n => n.sideListNodes || []);
		const patternsSelection = attrs.defs.selectAll('.pattern')
			.data([...allNormalNodes, ...allSideListNodes].filter(n => n.data.image != null), (d: TreeNode) => d.id)
			.join(enter => enter.append('pattern'))
			.attr('class', 'pattern')
			.attr('height', 1)
			.attr('width', 1)
			.attr('id', d => d.id);


		// Add images to patterns
		const patternImages = patternify(patternsSelection, {
			tag: 'image',
			selector: 'pattern-image',
			data: (d: TreeNode) => [d].filter(d => d.data.image != null)
		})
			.attr('x', 0)
			.attr('y', 0)
			.attr('height', (d: TreeNode) => d.data.image.width)
			.attr('width', (d: TreeNode) => d.data.image.height)
			.attr('xlink:href', (d: TreeNode) => d.data.image && d.data.image.url)
			.attr('viewbox', (d: TreeNode) => `0 0 ${d.data.image.width * 2} ${d.data.image.height}`)
			.attr('preserveAspectRatio', 'xMidYMin slice');

		// Remove patterns exit selection after animation
		patternsSelection.exit().transition().duration(attrs.duration).remove();

		// --------------------------  NODES ----------------------
		// Get nodes selection
		const nodesSelection = parentSelection.selectAll<SVGGElement, TreeNodeLike>(':scope > .node')
			.data(dataFunction, d => (d as any).id)
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
					.remove()
			);

		nodesSelection.transition()
			.attr('opacity', 0)
			.duration(attrs.duration)
			.attr("transform", d => {
				return `translate(${d.x},${d.y})`;
			})
			.attr('opacity', 1);


		// Style node rectangles
		nodesSelection.selectAll(':scope > .node-rect')
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
			.attr('stroke-width', d => d.data.borderWidth)
			.attr('stroke', d => createUiColorCssString(d.data.borderColor))
			.style("fill", d => createUiColorCssString(d.data.backgroundColor));

		// Add node icon image inside node
		nodesSelection.selectAll(':scope > image.node-icon-image')
			.data(d => d.data.icon ? [d] : [])
			.join(enter => enter
				.append('image')
				.classed('node-icon-image', true)
			)
			.attr('width', d => d.data.icon.size)
			.attr('height', d => d.data.icon.size)
			.attr("xlink:href", d => d.data.icon.icon)
			.attr('x', d => -d.data.icon.size / 2)
			.attr('y', d => -d.data.icon.size / 2);

		// Defined node images wrapper group
		const imageGroups = nodesSelection.selectAll(':scope > g.node-image-group')
			.data(d => [d].filter(d => d.data.image != null))
			.join(enter => enter
				.append("g")
				.classed("node-image-group", true)
			)
			.attr('transform', (d: TreeNode) => {
				let x = -d.data.image.width / 2;
				let y = -d.data.image.height / 2;
				return `translate(${x},${y})`
			});

		imageGroups.selectAll(':scope > rect.node-image-rect')
			.data(d => [d].filter(d => d.data.image != null))
			.join(enter => enter
				.append("rect")
				.classed("node-image-rect", true)
			)
			.attr('fill', d => `url(#${d.id})`)
			.attr('width', d => d.data.image.width)
			.attr('height', d => d.data.image.height)
			.attr('stroke', d => createUiColorCssString(d.data.image.borderColor))
			.attr('stroke-width', d => d.data.image.borderWidth)
			.attr('rx', d => d.data.image.cornerShape == UiTreeGraphNodeImage_CornerShape.CIRCLE ? Math.max(d.data.image.width, d.data.image.height)
				: d.data.image.cornerShape == UiTreeGraphNodeImage_CornerShape.ROUNDED ? Math.min(d.data.image.width, d.data.image.height) / 10
					: 0)
			.attr('y', d => d.data.image.centerTopDistance)
			.attr('x', d => d.data.image.centerLeftDistance)
			.attr('filter', d => d.data.image.shadow ? `url(#${attrs.dropShadowId})` : 'none');

		// Add foreignObject element inside rectangle
		const fo = nodesSelection.selectAll(':scope > foreignObject.node-foreign-object')
			.data(d => [d])
			.join(enter => enter
				.append("foreignObject")
				.classed("node-foreign-object", true)
			);

		fo.selectAll(':scope > .node-foreign-object-div')
			.data(d => [d])
			.join(enter => enter
				.append("xhtml:div")
				.classed("node-foreign-object-div", true)
			);

		this.restyleForeignObjectElements();
		return nodesSelection;
	}

	private getParentExpanderPosition(d: TreeNodeLike) {
		if (d.parent == null) {
			return {x: d.data.width / 2, y: 0};
		}
		return {x: d.parent.x + d.parent.data.width / 2, y: d.parent.y + d.parent.data.height};
	}

	private drawLinks(parentSelection: Selection<SVGElement, any, any, any>, links: HierarchyPointLink<UiTreeGraphNodeConfig>[]) {
		const attrs = this.getChartState();
		// --------------------------  LINKS ----------------------
		// Get links selection
		const linkSelection = parentSelection.selectAll(':scope > path.link')
			.data(links, (d: HierarchyPointLink<UiTreeGraphNodeConfig>) => d.target.id)
			.join(
				enter => enter
					.insert('path', "g")
					.attr("class", "link")
					.attr('d', (d: HierarchyPointLink<UiTreeGraphNodeConfig>) => {
						let transitionOrigin = this.getParentExpanderPosition(d.target as TreeNode);
						return this.diagonalLine(transitionOrigin, transitionOrigin);
					}),
				update => update,
				exit => exit.transition()
					.duration(attrs.duration)
					.attr('d', (d: HierarchyPointLink<UiTreeGraphNodeConfig>) => {
						let transitionOrigin = this.getParentExpanderPosition(d.target as TreeNode);
						return this.diagonalLine(transitionOrigin, transitionOrigin);
					})
					.remove()
			)
			.attr("fill", "none")
			.attr("stroke-width", d => d.target.data.connectorLineWidth || 2)
			.attr('stroke', d => d.target.data.connectorLineColor ? this.rgbaObjToColor(d.target.data.connectorLineColor) : 'white')
			.attr('stroke-dasharray', d => d.target.data.dashArray ? d.target.data.dashArray : '')
			.transition()
			.duration(attrs.duration)
			.attr('d', d => this.diagonalLine({x: d.source.x + d.source.data.width / 2, y: d.source.y + d.source.data.height}, {x: d.target.x + d.target.data.width / 2, y: d.target.y}));
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

	diagonalLine(s: { x: number, y: number }, t: { x: number, y: number }) {
		// Calculate some variables based on source and target (s,t) coordinates
		let deltaX = t.x - s.x;
		let directionX = deltaX < 0 ? -1 : 1;
		let deltaY = t.y - s.y;
		let directionY = deltaY < 0 ? -1 : 1;
		let defaultRadius = this.getChartState().verticalLayerGap / 3;
		let r = Math.min(Math.abs(deltaX) / 2, Math.abs(deltaY) / 2, defaultRadius);
		let h = Math.abs(deltaY) - r - this.getChartState().verticalLayerGap / 2;
		let w = Math.abs(deltaX) - r * 2;

		// Build the path
		return `
           M ${(s.x)} ${(s.y)}
           L ${(s.x)} ${s.y + h * directionY}
           C  ${(s.x)} ${s.y + h * directionY + r * directionY} ${(s.x)} ${s.y + h * directionY + r * directionY} ${s.x + r * directionX} ${s.y + h * directionY + r * directionY}
           L ${s.x + w * directionX + r * directionX} ${s.y + h * directionY + r * directionY}
           C ${(t.x)}  ${s.y + h * directionY + r * directionY} ${(t.x)}  ${s.y + h * directionY + r * directionY} ${(t.x)} ${t.y - (this.getChartState().verticalLayerGap / 2 - r) * directionY}
           L ${(t.x)} ${(t.y)}
         `;
	}

	hookLine(s: { x: number, y: number }, t: { x: number, y: number }) {
		// Calculate some variables based on source and target (s,t) coordinates
		let deltaX = t.x - s.x;
		let directionX = deltaX < 0 ? -1 : 1;
		let deltaY = t.y - s.y;
		let directionY = deltaY < 0 ? -1 : 1;
		let defaultRadius = 35;
		let r = Math.min(Math.abs(deltaX), Math.abs(deltaY), defaultRadius);
		let h = Math.abs(deltaY) - r;
		let w = Math.abs(deltaX) - r;

		// Build the path
		return `
           M ${(s.x)} ${(s.y)}
           L ${(s.x)} ${s.y + h * directionY}
           C  ${(s.x)} ${s.y + h * directionY + r * directionY} ${(s.x)} ${s.y + h * directionY + r * directionY} ${s.x + r * directionX} ${s.y + h * directionY + r * directionY}
           L ${(t.x)} ${(t.y)}
         `;
	}

	restyleForeignObjectElements() {
		const attrs = this.getChartState();

		attrs.svg.selectAll('.node-foreign-object')
			.attr('width', (n: TreeNode) => n.data.width)
			.attr('height', (n: TreeNode) => n.data.height);
		attrs.svg.selectAll('.node-foreign-object-div')
			.style('width', (n: TreeNode) => `${n.data.width}px`)
			.style('height', (n: TreeNode) => `${n.data.height}px`)
			.html((n: TreeNode) => this.context.templateRegistry.createTemplateRenderer(n.data.template).render(n.data.record.values))
			.select('*')
			.style('display', 'inline-grid')
	}

	// Toggle children on click.
	private getVisibleNodeRecords(records: UiTreeGraphNodeConfig[]) {
		const recordsById = getById(records);

		function depth(r: UiTreeGraphNodeConfig) {
			let depth = 0;
			while (r.parentId != null && recordsById[r.parentId] != null) {
				depth++;
				r = recordsById[r.parentId];
			}
			return depth;
		}

		// find the lowest root (without parent node or with parentExpanded == false)
		const visibleRoot = records
			.filter(r => r.parentId == null || recordsById[r.parentId] == null || r.parentExpanded == false)
			.sort((r1, r2) => depth(r2) - depth(r1))
			[0];

		const visibleNodeRecordsById: { [id: string]: UiTreeGraphNodeConfig } = {[visibleRoot.id]: visibleRoot};
		const invisibleNodeRecordsById: { [id: string]: UiTreeGraphNodeConfig } = {};

		// everything above the visible root is invisible (by definition)
		let invisibleParent = visibleRoot; // only for initialization. the visible root remains visible
		do {
			invisibleParent = invisibleParent.parentId != null ? recordsById[invisibleParent.parentId] : null;
			if (invisibleParent != null) {
				invisibleNodeRecordsById[invisibleParent.id] = invisibleParent;
			}
		} while (invisibleParent != null);

		// propagate visibility/invisibility knowledge downwards
		let remainingRecords: UiTreeGraphNodeConfig[] = records.filter(r => visibleNodeRecordsById[r.id] == null && invisibleNodeRecordsById[r.id] == null);
		let visibleNodesChanged: boolean;
		do {
			visibleNodesChanged = false;
			remainingRecords = remainingRecords.filter(r => {
				let visibleParent = visibleNodeRecordsById[r.parentId];
				let invisibleParent = invisibleNodeRecordsById[r.parentId];
				if (visibleParent != null) {
					if (visibleParent.expanded) {
						visibleNodeRecordsById[r.id] = r;
						visibleNodesChanged = true;
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
		} while (visibleNodesChanged);
		return visibleNodeRecordsById;
	}

	private getAllDescendants(node: UiTreeGraphNodeConfig, includeSelf: boolean) {
		// O(h^2) where h is the height of the tree.
		// So the worst case performance is O(n * n/2) for a totally linear tree.
		// Average case: O(n * log(n))
		// If used for a root node (see usages!!): O(n) due to optimization!

		const descendantsById: { [id: string]: UiTreeGraphNodeConfig } = {};
		descendantsById[node.id] = node;
		const nonDescendantsById: { [id: string]: UiTreeGraphNodeConfig } = {}; // common case optimization!

		let untaggedNodes = this.getChartState().data.filter(n => n.id != node.id);

		let descendantsChanged: boolean;
		let nonDescendantsChanged: boolean;
		do {
			descendantsChanged = false;
			nonDescendantsChanged = false;
			untaggedNodes = untaggedNodes.filter(n => {
				if (descendantsById[n.parentId] != null) {
					descendantsById[n.id] = n;
					descendantsChanged = true;
					return false;
				} else if (nonDescendantsById[n.parentId] != null) {
					nonDescendantsById[n.id] = n;
					nonDescendantsChanged = true;
					return false;
				} else {
					return true;
				}
			});
		} while (descendantsChanged && nonDescendantsChanged);

		for (let untaggedNode of untaggedNodes) {
			descendantsById[untaggedNode.id] = untaggedNode; // if nonDescendantsChanged[0] == false, all remaining nodes must be descendants!
		}

		if (!includeSelf) {
			delete descendantsById[node.id];
		}

		return descendantsById;
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

	public moveToRootNode(animationDuration: number = 400) {
		let attrs: TreeChartAttributes = this.getChartState();
		if (attrs.root) {
			this.moveToHierarchyNode(attrs.root, animationDuration);
		}
	}

	public moveToNode(nodeId: string, animationDuration: number = 400) {
		let attrs: TreeChartAttributes = this.getChartState();
		let treeNode = attrs.root.descendants().filter(d => d.id === nodeId)[0];
		if (treeNode != null) {
			this.moveToHierarchyNode(treeNode, animationDuration);
		}
	}

	private moveToHierarchyNode(node: HierarchyPointNode<UiTreeGraphNodeConfig>, animationDuration: number = 400) {
		let attrs: TreeChartAttributes = this.getChartState();
		let zoomTransform = d3.zoomTransform(attrs.svg.node());

		let nodeSize = this.calculateNodeSize(node);

		attrs.svg.transition().duration(animationDuration).call(
			this.zoomBehavior.transform,
			d3.zoomIdentity.scale(zoomTransform.k).translate(-node.x - nodeSize.width / 2 + attrs.svgWidth / (2 * zoomTransform.k), -node.y + (attrs.verticalLayerGap))
		);
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
	var selection = container.selectAll<E, ED>(':scope > .' + selector)
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
