/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiPanelConfig} from "./UiPanelConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiPanelHeaderFieldConfig} from "./UiPanelHeaderFieldConfig";
import {UiToolbarConfig} from "./UiToolbarConfig";
import {UiToolButtonConfig} from "./UiToolButtonConfig";
import {UiPanelCommandHandler} from "./UiPanelConfig";
import {UiPanelEventSource} from "./UiPanelConfig";


export interface UiWindowConfig extends UiPanelConfig {
	_type?: string;
	modal?: boolean;
	width?: number;
	height?: number;
	headerBackgroundColor?: string;
	modalBackgroundDimmingColor?: string;
	closeable?: boolean;
	closeOnEscape?: boolean;
	closeOnClickOutside?: boolean
}

export interface UiWindowCommandHandler extends UiPanelCommandHandler {
	show(animationDuration: number): any;
	close(animationDuration: number): any;
	setCloseable(closeable: boolean): any;
	setCloseOnEscape(closeOnEscape: boolean): any;
	setCloseOnClickOutside(closeOnClickOutside: boolean): any;
	setModal(modal: boolean): any;
	setModalBackgroundDimmingColor(modalBackgroundDimmingColor: string): any;
	setSize(width: number, height: number): any;
}

export interface UiWindowEventSource extends UiPanelEventSource {
}


