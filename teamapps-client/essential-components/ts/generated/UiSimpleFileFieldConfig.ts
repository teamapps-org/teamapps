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
import {UiFileFieldDisplayType} from "./UiFileFieldDisplayType";
import {UiFieldCommandHandler} from "./UiFieldConfig";
import {UiFieldEventSource} from "./UiFieldConfig";


export interface UiSimpleFileFieldConfig extends UiFieldConfig {
	_type?: string;
	browseButtonIcon?: string;
	browseButtonCaption?: string;
	uploadUrl?: string;
	maxBytesPerFile?: number;
	fileTooLargeMessage?: string;
	uploadErrorMessage?: string;
	maxFiles?: number;
	displayMode?: UiFileFieldDisplayType;
	fileItems?: UiFileItemConfig[]
}

export interface UiSimpleFileFieldCommandHandler extends UiFieldCommandHandler {
	addFileItem(item: UiFileItemConfig): any;
	updateFileItem(item: UiFileItemConfig): any;
	removeFileItem(itemUuid: string): any;
	setBrowseButtonIcon(browseButtonIcon: string): any;
	setBrowseButtonCaption(browseButtonCaption: string): any;
	setUploadUrl(uploadUrl: string): any;
	setMaxBytesPerFile(maxBytesPerFile: number): any;
	setFileTooLargeMessage(fileTooLargeMessage: string): any;
	setUploadErrorMessage(uploadErrorMessage: string): any;
	setMaxFiles(maxFiles: number): any;
	setDisplayMode(displayType: UiFileFieldDisplayType): any;
}

export interface UiSimpleFileFieldEventSource extends UiFieldEventSource {
	onUploadInitiatedByUser: TeamAppsEvent<UiSimpleFileField_UploadInitiatedByUserEvent>;
	onUploadTooLarge: TeamAppsEvent<UiSimpleFileField_UploadTooLargeEvent>;
	onUploadStarted: TeamAppsEvent<UiSimpleFileField_UploadStartedEvent>;
	onUploadCanceled: TeamAppsEvent<UiSimpleFileField_UploadCanceledEvent>;
	onUploadFailed: TeamAppsEvent<UiSimpleFileField_UploadFailedEvent>;
	onUploadSuccessful: TeamAppsEvent<UiSimpleFileField_UploadSuccessfulEvent>;
	onFileItemClicked: TeamAppsEvent<UiSimpleFileField_FileItemClickedEvent>;
	onFileItemRemoved: TeamAppsEvent<UiSimpleFileField_FileItemRemovedEvent>;
}

export interface UiSimpleFileField_UploadInitiatedByUserEvent extends UiEvent {
	uuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiSimpleFileField_UploadTooLargeEvent extends UiEvent {
	fileItemUuid: string
}

export interface UiSimpleFileField_UploadStartedEvent extends UiEvent {
	fileItemUuid: string
}

export interface UiSimpleFileField_UploadCanceledEvent extends UiEvent {
	fileItemUuid: string
}

export interface UiSimpleFileField_UploadFailedEvent extends UiEvent {
	fileItemUuid: string
}

export interface UiSimpleFileField_UploadSuccessfulEvent extends UiEvent {
	fileItemUuid: string;
	uploadedFileUuid: string
}

export interface UiSimpleFileField_FileItemClickedEvent extends UiEvent {
	fileItemUuid: string
}

export interface UiSimpleFileField_FileItemRemovedEvent extends UiEvent {
	fileItemUuid: string
}

