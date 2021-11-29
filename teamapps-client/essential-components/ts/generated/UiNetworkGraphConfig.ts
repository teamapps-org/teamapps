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
import {UiNetworkNodeConfig} from "./UiNetworkNodeConfig";
import {UiNetworkLinkConfig} from "./UiNetworkLinkConfig";
import {UiNetworkImageConfig} from "./UiNetworkImageConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiNetworkGraphConfig extends UiComponentConfig {
	_type?: string;
	nodes: UiNetworkNodeConfig[];
	links: UiNetworkLinkConfig[];
	images: UiNetworkImageConfig[];
	gravity?: number;
	theta?: number;
	alpha?: number;
	charge?: number;
	distance?: number;
	strength?: number;
	highlightColor?: string;
	animationDuration?: number
}

export interface UiNetworkGraphCommandHandler extends UiComponentCommandHandler {
	setZoomFactor(zoomFactor: number): any;
	setGravity(gravity: number): any;
	setCharge(charge: number, overrideNodeCharge: boolean): any;
	setDistance(linkDistance: number, nodeDistance: number): any;
	zoomAllNodesIntoView(animationDuration: number): any;
	addNodesAndLinks(nodes: UiNetworkNodeConfig[], links: UiNetworkLinkConfig[]): any;
	removeNodesAndLinks(nodeIds: string[], linksBySourceNodeId: {[name: string]: string[]}): any;
}

export interface UiNetworkGraphEventSource {
	onNodeClicked: TeamAppsEvent<UiNetworkGraph_NodeClickedEvent>;
	onNodeDoubleClicked: TeamAppsEvent<UiNetworkGraph_NodeDoubleClickedEvent>;
	onNodeExpandedOrCollapsed: TeamAppsEvent<UiNetworkGraph_NodeExpandedOrCollapsedEvent>;
}

export interface UiNetworkGraph_NodeClickedEvent extends UiEvent {
	nodeId: string
}

export interface UiNetworkGraph_NodeDoubleClickedEvent extends UiEvent {
	nodeId: string
}

export interface UiNetworkGraph_NodeExpandedOrCollapsedEvent extends UiEvent {
	nodeId: string;
	expanded: boolean
}

