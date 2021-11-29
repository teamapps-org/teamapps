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
import {UiSplitPaneConfig} from "./UiSplitPaneConfig";
import {UiToolbarConfig} from "./UiToolbarConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiApplicationLayoutConfig extends UiComponentConfig {
	_type?: string;
	rootSplitPane?: unknown;
	toolbar?: unknown
}

export interface UiApplicationLayoutCommandHandler extends UiComponentCommandHandler {
	setToolbar(toolbar: unknown): any;
	setRootSplitPane(splitPane: unknown): any;
}


