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
import {UiImageCropperSelectionConfig} from "./UiImageCropperSelectionConfig";
import {UiImageCropperSelectionMode} from "./UiImageCropperSelectionMode";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiImageCropperConfig extends UiComponentConfig {
	_type?: string;
	imageUrl: string;
	selectionMode: UiImageCropperSelectionMode;
	aspectRatio: number;
	selection?: UiImageCropperSelectionConfig
}

export interface UiImageCropperCommandHandler extends UiComponentCommandHandler {
	setImageUrl(imageUrl: string): any;
	setSelectionMode(selectionMode: UiImageCropperSelectionMode): any;
	setAspectRatio(aspectRatio: number): any;
	setSelection(selection: UiImageCropperSelectionConfig): any;
}

export interface UiImageCropperEventSource {
	onSelectionChanged: TeamAppsEvent<UiImageCropper_SelectionChangedEvent>;
}

export interface UiImageCropper_SelectionChangedEvent extends UiEvent {
	selection: UiImageCropperSelectionConfig
}

