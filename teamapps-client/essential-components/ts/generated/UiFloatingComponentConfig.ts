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
import {UiFloatingComponentPosition} from "./UiFloatingComponentPosition";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiFloatingComponentConfig extends UiComponentConfig {
	_type?: string;
	containerComponent?: unknown;
	contentComponent?: unknown;
	width?: number;
	height?: number;
	marginX?: number;
	marginY?: number;
	position?: UiFloatingComponentPosition;
	backgroundColor?: string;
	expanderHandleColor?: string;
	collapsible?: boolean;
	expanded?: boolean
}

export interface UiFloatingComponentCommandHandler extends UiComponentCommandHandler {
	setContentComponent(contentComponent: unknown): any;
	setExpanded(expanded: boolean): any;
	setPosition(position: UiFloatingComponentPosition): any;
	setDimensions(width: number, height: number): any;
	setMargins(marginX: number, marginY: number): any;
	setBackgroundColor(backgroundColor: string): any;
	setExpanderHandleColor(expanderHandleColor: string): any;
}

export interface UiFloatingComponentEventSource {
	onExpandedOrCollapsed: TeamAppsEvent<UiFloatingComponent_ExpandedOrCollapsedEvent>;
}

export interface UiFloatingComponent_ExpandedOrCollapsedEvent extends UiEvent {
	expanded: boolean
}

