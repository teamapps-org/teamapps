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
import {UiBorderConfig} from "./UiBorderConfig";
import {UiShadowConfig} from "./UiShadowConfig";
import {UiPageDisplayMode} from "./UiPageDisplayMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiDocumentViewerConfig extends UiComponentConfig {
	_type?: string;
	pageUrls?: string[];
	displayMode?: UiPageDisplayMode;
	zoomFactor?: number;
	pageBorder?: UiBorderConfig;
	pageShadow?: UiShadowConfig;
	padding?: number;
	pageSpacing?: number
}

export interface UiDocumentViewerCommandHandler extends UiComponentCommandHandler {
	setPageUrls(pageUrls: string[]): any;
	setDisplayMode(displayMode: UiPageDisplayMode, zoomFactor: number): any;
	setZoomFactor(zoomFactor: number): any;
	setPageBorder(pageBorder: UiBorderConfig): any;
	setPageShadow(pageShadow: UiShadowConfig): any;
	setPaddding(padding: number): any;
	setPageSpacing(pageSpacing: number): any;
}


