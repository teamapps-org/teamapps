/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiTreeGraphNodeConfig} from "./UiTreeGraphNodeConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiTreeGraphConfig extends UiComponentConfig {
	_type?: string;
	nodes?: UiTreeGraphNodeConfig[];
	backgroundColor?: string;
	zoomFactor?: number;
	compact?: boolean;
	verticalLayerGap?: number;
	sideListIndent?: number;
	sideListVerticalGap?: number;
	horizontalSiblingGap?: number;
	horizontalNonSignlingGap?: number
}

export interface UiTreeGraphCommandHandler extends UiComponentCommandHandler {
	update(config: UiTreeGraphConfig): any;
	setZoomFactor(zoomFactor: number): any;
	setNodes(nodes: UiTreeGraphNodeConfig[]): any;
	addNode(node: UiTreeGraphNodeConfig): any;
	removeNode(nodeId: string): any;
	setNodeExpanded(nodeId: string, expanded: boolean): any;
	updateNode(node: UiTreeGraphNodeConfig): any;
	moveToRootNode(): any;
	moveToNode(nodeId: string): any;
}

export interface UiTreeGraphEventSource {
	onNodeClicked: TeamAppsEvent<UiTreeGraph_NodeClickedEvent>;
	onNodeExpandedOrCollapsed: TeamAppsEvent<UiTreeGraph_NodeExpandedOrCollapsedEvent>;
	onParentExpandedOrCollapsed: TeamAppsEvent<UiTreeGraph_ParentExpandedOrCollapsedEvent>;
	onSideListExpandedOrCollapsed: TeamAppsEvent<UiTreeGraph_SideListExpandedOrCollapsedEvent>;
}

export interface UiTreeGraph_NodeClickedEvent extends UiEvent {
	nodeId: string
}

export interface UiTreeGraph_NodeExpandedOrCollapsedEvent extends UiEvent {
	nodeId: string;
	expanded: boolean;
	lazyLoad: boolean
}

export interface UiTreeGraph_ParentExpandedOrCollapsedEvent extends UiEvent {
	nodeId: string;
	expanded: boolean;
	lazyLoad: boolean
}

export interface UiTreeGraph_SideListExpandedOrCollapsedEvent extends UiEvent {
	nodeId: string;
	expanded: boolean
}

