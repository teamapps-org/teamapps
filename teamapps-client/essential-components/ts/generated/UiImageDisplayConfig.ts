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
import {UiCachedImageConfig} from "./UiCachedImageConfig";
import {UiPageDisplayMode} from "./UiPageDisplayMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiImageDisplayConfig extends UiComponentConfig {
	_type?: string;
	totalNumberOfRecords: number;
	cachedImages?: UiCachedImageConfig[];
	displayMode?: UiPageDisplayMode;
	zoomFactor?: number;
	backgroundColor?: string;
	padding?: number;
	loop?: boolean;
	cacheSize?: number
}

export interface UiImageDisplayCommandHandler extends UiComponentCommandHandler {
	setCachedImages(startIndex: number, cachedImages: UiCachedImageConfig[], totalNumberOfRecords: number): any;
	setDisplayMode(displayMode: UiPageDisplayMode, zoomFactor: number): any;
	setZoomFactor(zoomFactor: number): any;
	showImage(id: string): any;
}

export interface UiImageDisplayEventSource {
	onImagesRequest: TeamAppsEvent<UiImageDisplay_ImagesRequestEvent>;
	onImageDisplayed: TeamAppsEvent<UiImageDisplay_ImageDisplayedEvent>;
}

export interface UiImageDisplay_ImagesRequestEvent extends UiEvent {
	startIndex: number;
	length: number
}

export interface UiImageDisplay_ImageDisplayedEvent extends UiEvent {
	imageId: string
}

