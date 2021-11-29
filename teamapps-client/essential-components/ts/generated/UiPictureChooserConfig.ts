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
import {UiComponentConfig} from "./UiComponentConfig";
import {UiClientObjectConfig} from "./UiClientObjectConfig";
import {UiFieldMessageConfig} from "./UiFieldMessageConfig";
import {UiFileItemConfig} from "./UiFileItemConfig";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiPictureChooserConfig extends UiFieldConfig {
	_type?: string;
	uploadUrl?: string;
	maxFileSize?: number;
	fileTooLargeMessage?: string;
	uploadErrorMessage?: string;
	browseButtonIcon?: string;
	deleteButtonIcon?: string;
	imageDisplayWidth?: number;
	imageDisplayHeight?: number;
	fileItem?: UiFileItemConfig
}

export interface UiPictureChooserCommandHandler extends UiFieldCommandHandler {
	setBrowseButtonIcon(browseButtonIcon: string): any;
	setUploadUrl(uploadUrl: string): any;
	setMaxFileSize(maxFileSize: number): any;
	setFileTooLargeMessage(fileTooLargeMessage: string): any;
	setUploadErrorMessage(uploadErrorMessage: string): any;
	cancelUpload(): any;
}

export interface UiPictureChooserEventSource extends UiFieldEventSource {
	onUploadInitiatedByUser: TeamAppsEvent<UiPictureChooser_UploadInitiatedByUserEvent>;
	onUploadTooLarge: TeamAppsEvent<UiPictureChooser_UploadTooLargeEvent>;
	onUploadStarted: TeamAppsEvent<UiPictureChooser_UploadStartedEvent>;
	onUploadCanceled: TeamAppsEvent<UiPictureChooser_UploadCanceledEvent>;
	onUploadFailed: TeamAppsEvent<UiPictureChooser_UploadFailedEvent>;
	onUploadSuccessful: TeamAppsEvent<UiPictureChooser_UploadSuccessfulEvent>;
}

export interface UiPictureChooser_UploadInitiatedByUserEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiPictureChooser_UploadTooLargeEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiPictureChooser_UploadStartedEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiPictureChooser_UploadCanceledEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiPictureChooser_UploadFailedEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiPictureChooser_UploadSuccessfulEvent extends UiEvent {
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	uploadedFileUuid: string
}

