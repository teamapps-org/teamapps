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
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiTreeRecordConfig} from "./UiTreeRecordConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiTreeConfig extends UiComponentConfig {
	_type?: string;
	templates?: {[name: string]: UiTemplateConfig};
	defaultTemplateId?: string;
	initialData?: UiTreeRecordConfig[];
	selectedNodeId?: number;
	animate?: boolean;
	showExpanders?: boolean;
	openOnSelection?: boolean;
	enforceSingleExpandedPath?: boolean;
	indentation?: number
}

export interface UiTreeCommandHandler extends UiComponentCommandHandler {
	replaceData(nodes: UiTreeRecordConfig[]): any;
	bulkUpdate(nodesToBeRemoved: number[], nodesToBeAdded: UiTreeRecordConfig[]): any;
	setSelectedNode(recordId: number): any;
	registerTemplate(id: string, template: UiTemplateConfig): any;
}

export interface UiTreeEventSource {
	onTextInput: TeamAppsEvent<UiTree_TextInputEvent>;
	onNodeSelected: TeamAppsEvent<UiTree_NodeSelectedEvent>;
	onRequestTreeData: TeamAppsEvent<UiTree_RequestTreeDataEvent>;
}

export interface UiTree_TextInputEvent extends UiEvent {
	text: string
}

export interface UiTree_NodeSelectedEvent extends UiEvent {
	nodeId: number
}

export interface UiTree_RequestTreeDataEvent extends UiEvent {
	parentNodeId: number
}

