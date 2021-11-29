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
import {UiPanelHeaderFieldConfig} from "./UiPanelHeaderFieldConfig";
import {UiToolbarConfig} from "./UiToolbarConfig";
import {UiToolButtonConfig} from "./UiToolButtonConfig";
import {UiWindowButtonType} from "./UiWindowButtonType";
import {UiComponentCommandHandler} from "./UiComponentConfig";

export enum UiPanel_HeaderComponentMinimizationPolicy {
	LEFT_COMPONENT_FIRST, RIGHT_COMPONENT_FIRST
}

export interface UiPanelConfig extends UiComponentConfig {
	_type?: string;
	icon?: string;
	title?: string;
	leftHeaderField?: UiPanelHeaderFieldConfig;
	rightHeaderField?: UiPanelHeaderFieldConfig;
	headerComponentMinimizationPolicy?: UiPanel_HeaderComponentMinimizationPolicy;
	alwaysShowHeaderFieldIcons?: boolean;
	hideTitleBar?: boolean;
	toolbar?: unknown;
	content?: unknown;
	stretchContent?: boolean;
	padding?: number;
	windowButtons?: UiWindowButtonType[];
	toolButtons?: unknown[]
}

export interface UiPanelCommandHandler extends UiComponentCommandHandler {
	setContent(content: unknown): any;
	setLeftHeaderField(field: UiPanelHeaderFieldConfig): any;
	setRightHeaderField(field: UiPanelHeaderFieldConfig): any;
	setTitle(title: string): any;
	setIcon(icon: string): any;
	setToolbar(toolbar: unknown): any;
	setMaximized(maximized: boolean): any;
	setWindowButtons(windowButtons: UiWindowButtonType[]): any;
	setToolButtons(toolButtons: unknown[]): any;
	setStretchContent(stretch: boolean): any;
}

export interface UiPanelEventSource {
	onWindowButtonClicked: TeamAppsEvent<UiPanel_WindowButtonClickedEvent>;
}

export interface UiPanel_WindowButtonClickedEvent extends UiEvent {
	windowButton: UiWindowButtonType
}

