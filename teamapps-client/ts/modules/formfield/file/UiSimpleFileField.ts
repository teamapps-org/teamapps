/*
 * Copyright (c) 2019 teamapps.org (see code comments for author's name)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {UiField} from "../UiField";
import {UiFileFieldDisplayType} from "../../../generated/UiFileFieldDisplayType";
import {UiFieldEditingMode} from "../../../generated/UiFieldEditingMode";
import {TeamAppsUiContext} from "../../TeamAppsUiContext";
import {arraysEqual, generateUUID, parseHtml} from "../../Common";
import {TeamAppsEvent} from "../../util/TeamAppsEvent";
import {TeamAppsUiComponentRegistry} from "../../TeamAppsUiComponentRegistry";
import {keyCodes} from "trivial-components";
import {createUiFileItemConfig, UiFileItemConfig} from "../../../generated/UiFileItemConfig";
import {
	UiSimpleFileField_FileItemClickedEvent,
	UiSimpleFileField_FileItemRemovedEvent,
	UiSimpleFileField_UploadCanceledEvent,
	UiSimpleFileField_UploadFailedEvent,
	UiSimpleFileField_UploadInitiatedByUserEvent,
	UiSimpleFileField_UploadStartedEvent,
	UiSimpleFileField_UploadSuccessfulEvent,
	UiSimpleFileField_UploadTooLargeEvent,
	UiSimpleFileFieldCommandHandler,
	UiSimpleFileFieldConfig,
	UiSimpleFileFieldEventSource
} from "../../../generated/UiSimpleFileFieldConfig";
import {FileItemState, UiFileItem} from "./UiFileItem";

/**
 * @author Yann Massard (yamass@gmail.com)
 */
export class UiSimpleFileField extends UiField<UiSimpleFileFieldConfig, UiFileItemConfig[]> implements UiSimpleFileFieldEventSource, UiSimpleFileFieldCommandHandler {

	public readonly onFileItemClicked: TeamAppsEvent<UiSimpleFileField_FileItemClickedEvent> = new TeamAppsEvent(this);
	public readonly onFileItemRemoved: TeamAppsEvent<UiSimpleFileField_FileItemRemovedEvent> = new TeamAppsEvent(this);
	public readonly onUploadCanceled: TeamAppsEvent<UiSimpleFileField_UploadCanceledEvent> = new TeamAppsEvent(this);
	public readonly onUploadFailed: TeamAppsEvent<UiSimpleFileField_UploadFailedEvent> = new TeamAppsEvent(this);
	public readonly onUploadInitiatedByUser: TeamAppsEvent<UiSimpleFileField_UploadInitiatedByUserEvent> = new TeamAppsEvent(this);
	public readonly onUploadStarted: TeamAppsEvent<UiSimpleFileField_UploadStartedEvent> = new TeamAppsEvent(this);
	public readonly onUploadSuccessful: TeamAppsEvent<UiSimpleFileField_UploadSuccessfulEvent> = new TeamAppsEvent(this);
	public readonly onUploadTooLarge: TeamAppsEvent<UiSimpleFileField_UploadTooLargeEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private $uploadButton: HTMLElement;
	private $fileInput: HTMLInputElement;
	private $fileList: HTMLElement;

	private displayMode: any;
	private maxFiles: number;
	private fileItems: { [uuid: string]: UiFileItem };
	private maxBytesPerFile: number;
	private fileTooLargeMessage: string;
	private uploadErrorMessage: string;
	private uploadUrl: string;

	protected initialize(config: UiSimpleFileFieldConfig, context: TeamAppsUiContext): void {
		this.fileItems = {};

		this.$main = parseHtml(`<div class="UiSimpleFileField drop-zone form-control field-border field-border-glow field-background">
    <div class="file-list"></div>
    <div class="upload-button field-border" tabindex="0">
    	<div class="icon img img-16" style="background-image: url(${context.getIconPath(config.browseButtonIcon, 16)});"></div>
    	<div class="caption">${config.browseButtonCaption}</div>
    	<input class="file-input" type="file" multiple tabindex="-1"></input>
    </div>
</div>`);

		this.$main.addEventListener("dragover", (e) => {
			this.$main.classList.add("drop-zone-active");
			// preventDefault() is important as it indicates that the drop is possible!!! see https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Drag_operations#droptargets
			e.preventDefault();
		});
		this.$main.addEventListener("dragleave", (e) => {
			this.$main.classList.remove("drop-zone-active");
			e.preventDefault();
		});
		this.$main.addEventListener("dragend", (e) => {
			this.$main.classList.remove("drop-zone-active");
			e.preventDefault();
		});
		this.$main.addEventListener("mousedown", (e) => {
			this.focus();
		});
		this.$main.ondrop = (e) => { // I don't know why I cannot just register this the same way as the other handlers...
			e.stopPropagation();
			e.preventDefault();
			this.$main.classList.remove("drop-zone-active");
			const files = e.dataTransfer.files;
			this.handleFiles(files);
		};

		this.$uploadButton = this.$main.querySelector<HTMLElement>(':scope .upload-button');
		["click", "keypress"].forEach(eventName => this.$uploadButton.addEventListener(eventName, (e: any) => {
			if (e.button == 0 || e.keyCode === keyCodes.enter || e.keyCode === keyCodes.space) {
				this.$fileInput.click();
				return false; // no scrolling when space is pressed!
			}
		}));
		this.$fileInput = this.$main.querySelector<HTMLInputElement>(':scope .file-input');
		this.$fileInput.addEventListener('change',() => {
			const files = (<HTMLInputElement>this.$fileInput).files;
			this.handleFiles(files);
		});

		this.$fileList = this.$main.querySelector<HTMLElement>(':scope .file-list');
		this.$fileList.addEventListener('click', e => {
			if (this.getTransientValue().length == 0) {
				this.$fileInput.click();
			}
		});

		this.setBrowseButtonCaption(config.browseButtonCaption);
		this.setBrowseButtonIcon(config.browseButtonIcon);
		this.setDisplayMode(config.displayMode);
		this.setMaxBytesPerFile(config.maxBytesPerFile);
		this.setMaxFiles(config.maxFiles);
		this.setFileTooLargeMessage(config.fileTooLargeMessage);
		this.setUploadErrorMessage(config.uploadErrorMessage);
		this.setUploadUrl(config.uploadUrl);
	}

	protected displayCommittedValue(): void {
		const existingItemUuids = Object.keys(this.fileItems || []);
		const committedItemUuids = this.getCommittedValue().map(item => item.uuid);
		existingItemUuids.filter(exId => committedItemUuids.indexOf(exId) === -1)
			.forEach(uuid => this.removeFileItem(uuid));
		committedItemUuids.filter(cId => existingItemUuids.indexOf(cId) === -1)
			.forEach(uuid => this.addFileItem(this.getCommittedValue().filter(item => item.uuid === uuid)[0]));
	}

	getDefaultValue(): UiFileItemConfig[] {
		return [];
	}

	getFocusableElement(): HTMLElement {
		return this.$uploadButton;
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	getTransientValue(): UiFileItemConfig[] {
		return Object.values(this.fileItems)
		// .filter(item => item.state === FileItemState.DONE)
			.map(item => createUiFileItemConfig({
				uuid: item.uuid,
				icon: item.icon,
				thumbnail: item.thumbnail,
				fileName: item.fileName,
				description: item.description,
				size: item.size,
				linkUrl: item.linkUrl
			}));
	}

	isValidData(v: UiFileItemConfig[]): boolean {
		return v == null || Array.isArray(v);
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		UiField.defaultOnEditingModeChangedImpl(this);
		this.updateVisibilities();
	}

	valuesChanged(v1: UiFileItemConfig[], v2: UiFileItemConfig[]): boolean {
		return !arraysEqual(v1.map(item => item.uuid), v2.map(item => item.uuid));
	}

	addFileItem(itemConfig: UiFileItemConfig, state: FileItemState = FileItemState.DONE): void {
		const fileItem = new UiFileItem(this.displayMode, this.maxBytesPerFile, this.fileTooLargeMessage, this.uploadErrorMessage, this.uploadUrl, itemConfig, state, this._context);
		this.fileItems[itemConfig.uuid] = fileItem;
		this.$fileList.appendChild(fileItem.getMainDomElement());
	}

	removeFileItem(itemUuid: string): void {
		const fileItem = this.fileItems[itemUuid];
		fileItem.getMainDomElement().remove();
		delete this.fileItems[itemUuid];
	}

	setBrowseButtonCaption(browseButtonCaption: string): void {
		this.$uploadButton.querySelector<HTMLElement>(":scope .caption").textContent = browseButtonCaption;
	}

	setBrowseButtonIcon(browseButtonIcon: string): void {
		this.$uploadButton.querySelector<HTMLElement>(":scope .icon")
			.style.backgroundImage = this._context.getIconPath(browseButtonIcon, 16);
	}

	setDisplayMode(displayMode: UiFileFieldDisplayType): void {
		this.displayMode = displayMode;
		this.$main.classList.toggle("float-style-vertical-list", displayMode === UiFileFieldDisplayType.LIST);
		this.$main.classList.toggle("float-style-horizontal", displayMode === UiFileFieldDisplayType.FLOATING);
		Object.values(this.fileItems).forEach(item => item.setDisplayMode(displayMode));
	}

	setFileTooLargeMessage(fileTooLargeMessage: string): void {
		this.fileTooLargeMessage = fileTooLargeMessage;
	}

	setMaxBytesPerFile(maxBytesPerFile: number): void {
		this.maxBytesPerFile = maxBytesPerFile;
	}

	setMaxFiles(maxFiles: number): void {
		this.maxFiles = maxFiles || Number.MAX_SAFE_INTEGER;
		this.updateVisibilities();
	}

	setUploadErrorMessage(uploadErrorMessage: string): void {
		this.uploadErrorMessage = uploadErrorMessage;
	}

	setUploadUrl(uploadUrl: string): void {
		this.uploadUrl = uploadUrl
	}

	updateFileItem(itemConfig: UiFileItemConfig): void {
		const fileItem = this.fileItems[itemConfig.uuid];
		if (fileItem != null) {
			fileItem.update(itemConfig);
		}
	}

	private get numberOfNonErrorFileItems() {
		return Object.values(this.fileItems).filter(item => item.state !== FileItemState.FAILED && item.state !== FileItemState.TOO_LARGE).length;
	}

	private get numberOfUploadingFileItems() {
		return Object.values(this.fileItems).filter(item => item.state === FileItemState.UPLOADING).length;
	}

	private updateVisibilities() {
		this.$uploadButton.classList.toggle("hidden", !this.isEditable() || this.numberOfNonErrorFileItems >= this.maxFiles);
	}

	private resetFileInput() {
		this.$fileInput.value = '';
	}

	private handleFiles(files: FileList) {
		let numberOfFilesToAdd = files.length;
		if (this.maxFiles && this.numberOfNonErrorFileItems + files.length > this.maxFiles) {
			numberOfFilesToAdd = this.maxFiles - this.numberOfNonErrorFileItems;
		}

		for (let i = 0; i < numberOfFilesToAdd; i++) {
			const file = files[i];
			let fileItem = this.createUploadFileItem();
			this.onUploadInitiatedByUser.fire({
				uuid: fileItem.uuid,
				fileName: file.name,
				mimeType: file.type,
				sizeInBytes: file.size
			});
			this.$fileList.appendChild(fileItem.getMainDomElement());
			this.fileItems[fileItem.uuid] = fileItem;
			fileItem.upload(file);
			this.onUploadStarted.fire({
				fileItemUuid: fileItem.uuid
			});
		}

		this.updateVisibilities();
		this.resetFileInput();
		this.commit();
	}

	private createUploadFileItem() {
		let fileItem = new UiFileItem(this.displayMode, this.maxBytesPerFile, this.fileTooLargeMessage, this.uploadErrorMessage, this.uploadUrl, {
			uuid: generateUUID()
		}, FileItemState.INITIATING, this._context);
		fileItem.onClick.addListener(() => {
			this.onFileItemClicked.fire({
				fileItemUuid: fileItem.uuid
			});
		});
		fileItem.onDeleteButtonClick.addListener(() => {
			this.onFileItemRemoved.fire({
				fileItemUuid: fileItem.uuid
			});
			this.removeFileItem(fileItem.uuid);
			this.updateVisibilities();
			this.commit();
		});
		fileItem.onUploadTooLarge.addListener(() => {
			this.onUploadTooLarge.fire({
				fileItemUuid: fileItem.uuid
			});
			this.updateVisibilities();
		});
		fileItem.onUploadSuccessful.addListener(uploadedFileUuid => {
			this.onUploadSuccessful.fire({
				fileItemUuid: fileItem.uuid,
				uploadedFileUuid: uploadedFileUuid
			});
			this.updateVisibilities();
		});
		fileItem.onUploadCanceled.addListener(() => {
			this.onUploadCanceled.fire({
				fileItemUuid: fileItem.uuid
			});
			this.updateVisibilities();
		});
		fileItem.onUploadFailed.addListener(() => {
			this.onUploadFailed.fire({
				fileItemUuid: fileItem.uuid
			});
			this.updateVisibilities();
		});
		return fileItem;
	}

}




TeamAppsUiComponentRegistry.registerFieldClass("UiSimpleFileField", UiSimpleFileField);