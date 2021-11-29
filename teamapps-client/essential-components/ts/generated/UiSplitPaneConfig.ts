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
import {EmptyableConfig} from "./EmptyableConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiSplitDirection} from "./UiSplitDirection";
import {UiSplitSizePolicy} from "./UiSplitSizePolicy";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiSplitPaneConfig extends UiComponentConfig, EmptyableConfig {
	_type?: string;
	splitDirection: UiSplitDirection;
	sizePolicy: UiSplitSizePolicy;
	referenceChildSize?: number;
	firstChild?: unknown;
	lastChild?: unknown;
	firstChildMinSize?: number;
	lastChildMinSize?: number;
	resizable?: boolean;
	fillIfSingleChild?: boolean;
	collapseEmptyChildren?: boolean
}

export interface UiSplitPaneCommandHandler extends UiComponentCommandHandler {
	setFirstChild(firstChild: unknown): any;
	setLastChild(lastChild: unknown): any;
	setSize(referenceChildSize: number, sizePolicy: UiSplitSizePolicy): any;
	setFirstChildMinSize(firstChildMinSize: number): any;
	setLastChildMinSize(lastChildMinSize: number): any;
}

export interface UiSplitPaneEventSource {
	onSplitResized: TeamAppsEvent<UiSplitPane_SplitResizedEvent>;
}

export interface UiSplitPane_SplitResizedEvent extends UiEvent {
	referenceChildSize: number
}

