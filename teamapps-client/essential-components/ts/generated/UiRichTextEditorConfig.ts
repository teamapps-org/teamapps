/**
 * THIS IS GENERATED CODE!
 * PLEASE DO NOT MODIFY - ALL YOUR WORK WOULD BE LOST!
 */
export const typescriptDeclarationFixConstant = 1;

import {UiCommand} from "./UiCommand";
import {UiEvent} from "./UiEvent";
import {UiQuery} from "./UiQuery";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFieldConfig} from "./UiFieldConfig";
import {UiTextInputHandlingFieldConfig} from "./UiTextInputHandlingFieldConfig";
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiToolbarVisibilityMode} from "./UiToolbarVisibilityMode";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";
import {UiTextInputHandlingFieldEventSource} from "./UiTextInputHandlingFieldConfig";


export interface UiRichTextEditorConfig extends UiFieldConfig, UiTextInputHandlingFieldConfig {
	_type?: string;
	locale?: string;
	imageUploadEnabled?: boolean;
	uploadUrl?: string;
	maxImageFileSizeInBytes?: number;
	toolbarVisibilityMode?: UiToolbarVisibilityMode;
	minHeight?: number;
	maxHeight?: number
}

export interface UiRichTextEditorCommandHandler extends UiFieldCommandHandler {
	setMinHeight(minHeight: number): any;
	setMaxHeight(maxHeight: number): any;
	setUploadUrl(uploadUrl: string): any;
	setMaxImageFileSizeInBytes(maxImageFileSizeInBytes: number): any;
	setUploadedImageUrl(fileUuid: string, url: string): any;
	setToolbarVisibilityMode(toolbarVisibilityMode: UiToolbarVisibilityMode): any;
}

export interface UiRichTextEditorEventSource extends UiFieldEventSource, UiTextInputHandlingFieldEventSource {
	onImageUploadTooLarge: TeamAppsEvent<UiRichTextEditor_ImageUploadTooLargeEvent>;
	onImageUploadStarted: TeamAppsEvent<UiRichTextEditor_ImageUploadStartedEvent>;
	onImageUploadSuccessful: TeamAppsEvent<UiRichTextEditor_ImageUploadSuccessfulEvent>;
	onImageUploadFailed: TeamAppsEvent<UiRichTextEditor_ImageUploadFailedEvent>;
}

export interface UiRichTextEditor_ImageUploadTooLargeEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiRichTextEditor_ImageUploadStartedEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiRichTextEditor_ImageUploadSuccessfulEvent extends UiEvent {
	fileUuid: string;
	name: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiRichTextEditor_ImageUploadFailedEvent extends UiEvent {
	name: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

