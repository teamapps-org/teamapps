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
import {UiTemplateConfig} from "./UiTemplateConfig";
import {UiIdentifiableClientRecordConfig} from "./UiIdentifiableClientRecordConfig";
import {UiFileFieldDisplayType} from "./UiFileFieldDisplayType";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiFileFieldConfig extends UiFieldConfig {
	_type?: string;
	itemTemplate: UiTemplateConfig;
	maxBytesPerFile?: number;
	uploadUrl?: string;
	fileTooLargeMessage?: string;
	uploadErrorMessage?: string;
	displayType?: UiFileFieldDisplayType;
	maxFiles?: number;
	uploadButtonTemplate: UiTemplateConfig;
	uploadButtonData: any;
	showEntriesAsButtonsOnHover?: boolean
}

export interface UiFileFieldCommandHandler extends UiFieldCommandHandler {
	replaceFileItem(fileItemUuid: string, data: UiIdentifiableClientRecordConfig): any;
	setItemTemplate(itemTemplate: UiTemplateConfig): any;
	setMaxBytesPerFile(maxBytesPerFile: number): any;
	setUploadUrl(uploadUrl: string): any;
	setDisplayType(displayType: UiFileFieldDisplayType): any;
	setMaxFiles(maxFiles: number): any;
	setUploadButtonTemplate(uploadButtonTemplate: UiTemplateConfig): any;
	setUploadButtonData(uploadButtonData: any): any;
	setShowEntriesAsButtonsOnHover(showEntriesAsButtonsOnHover: boolean): any;
	cancelAllUploads(): any;
	cancelUpload(fileItemUuid: string): any;
}

export interface UiFileFieldEventSource extends UiFieldEventSource {
	onUploadTooLarge: TeamAppsEvent<UiFileField_UploadTooLargeEvent>;
	onUploadStarted: TeamAppsEvent<UiFileField_UploadStartedEvent>;
	onUploadCanceled: TeamAppsEvent<UiFileField_UploadCanceledEvent>;
	onUploadFailed: TeamAppsEvent<UiFileField_UploadFailedEvent>;
	onUploadSuccessful: TeamAppsEvent<UiFileField_UploadSuccessfulEvent>;
	onFileItemClicked: TeamAppsEvent<UiFileField_FileItemClickedEvent>;
	onFileItemRemoveButtonClicked: TeamAppsEvent<UiFileField_FileItemRemoveButtonClickedEvent>;
}

export interface UiFileField_UploadTooLargeEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiFileField_UploadStartedEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiFileField_UploadCanceledEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiFileField_UploadFailedEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiFileField_UploadSuccessfulEvent extends UiEvent {
	fileItemUuid: string;
	uploadedFileUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiFileField_FileItemClickedEvent extends UiEvent {
	clientId: number
}

export interface UiFileField_FileItemRemoveButtonClickedEvent extends UiEvent {
	clientId: number
}

