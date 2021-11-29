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
import {UiNewChatMessageConfig} from "./UiNewChatMessageConfig";
import {UiComponentCommandHandler} from "./UiComponentConfig";


export interface UiChatInputConfig extends UiComponentConfig {
	_type?: string;
	defaultFileIcon: string;
	maxBytesPerUpload?: number;
	uploadUrl?: string;
	messageLengthLimit?: number;
	attachmentsEnabled?: boolean
}

export interface UiChatInputCommandHandler extends UiComponentCommandHandler {
	setAttachmentsEnabled(attachmentsEnabled: boolean): any;
}

export interface UiChatInputEventSource {
	onMessageSent: TeamAppsEvent<UiChatInput_MessageSentEvent>;
	onUploadTooLarge: TeamAppsEvent<UiChatInput_UploadTooLargeEvent>;
	onUploadStarted: TeamAppsEvent<UiChatInput_UploadStartedEvent>;
	onUploadCanceled: TeamAppsEvent<UiChatInput_UploadCanceledEvent>;
	onUploadFailed: TeamAppsEvent<UiChatInput_UploadFailedEvent>;
	onUploadSuccessful: TeamAppsEvent<UiChatInput_UploadSuccessfulEvent>;
	onFileItemClicked: TeamAppsEvent<UiChatInput_FileItemClickedEvent>;
	onFileItemRemoved: TeamAppsEvent<UiChatInput_FileItemRemovedEvent>;
}

export interface UiChatInput_MessageSentEvent extends UiEvent {
	message: UiNewChatMessageConfig
}

export interface UiChatInput_UploadTooLargeEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number
}

export interface UiChatInput_UploadStartedEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiChatInput_UploadCanceledEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiChatInput_UploadFailedEvent extends UiEvent {
	fileItemUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiChatInput_UploadSuccessfulEvent extends UiEvent {
	fileItemUuid: string;
	uploadedFileUuid: string;
	fileName: string;
	mimeType: string;
	sizeInBytes: number;
	incompleteUploadsCount: number
}

export interface UiChatInput_FileItemClickedEvent extends UiEvent {
	fileItemUuid: string
}

export interface UiChatInput_FileItemRemovedEvent extends UiEvent {
	fileItemUuid: string
}

