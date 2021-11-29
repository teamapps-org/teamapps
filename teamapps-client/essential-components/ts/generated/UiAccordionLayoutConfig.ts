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
import {UiAccordionPanelConfig} from "./UiAccordionPanelConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiAccordionLayoutConfig extends UiComponentConfig {
	_type?: string;
	panels: UiAccordionPanelConfig[];
	animate?: boolean;
	showAllPanels?: boolean
}

export interface UiAccordionLayoutCommandHandler extends UiComponentCommandHandler {
	addAccordionPanel(panel: UiAccordionPanelConfig, neighborPanelId: string, beforeNeighbor: boolean): any;
	addAccordionPanelContent(panelId: string, content: unknown): any;
	removeAccordionPanel(panelId: string): any;
	removeAllPanels(): any;
	selectPanel(panelId: string): any;
	setPanelOpen(panelId: string, open: boolean): any;
}


