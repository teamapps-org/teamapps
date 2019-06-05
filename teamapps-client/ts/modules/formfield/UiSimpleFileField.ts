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

// import "../../../less/formfield/UiSimpleFileField.less"
import {UiField} from "./UiField";
import {UiFileFieldDisplayType} from "../../generated/UiFileFieldDisplayType";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {arraysEqual, generateUUID, humanReadableFileSize, parseHtml, removeClassesByFunction} from "../Common";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {StaticIcons} from "../util/StaticIcons";
import {ProgressIndicator} from "../micro-components/ProgressIndicator";
import {ProgressCircle} from "../micro-components/ProgressCircle";
import {ProgressBar} from "../micro-components/ProgressBar";
import * as log from "loglevel";
import {Logger} from "loglevel";
import {keyCodes} from "trivial-components";
import {EventFactory} from "../../generated/EventFactory";
import {FileUploader} from "../util/FileUploader";
import {createUiFileItemConfig, UiFileItemConfig} from "../../generated/UiFileItemConfig";
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
} from "../../generated/UiSimpleFileFieldConfig";

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
			this.onUploadInitiatedByUser.fire(EventFactory.createUiSimpleFileField_UploadInitiatedByUserEvent(this.getId(), fileItem.uuid, file.name, file.type, file.size));
			this.$fileList.appendChild(fileItem.getMainDomElement());
			this.fileItems[fileItem.uuid] = fileItem;
			fileItem.upload(file);
			this.onUploadStarted.fire(EventFactory.createUiSimpleFileField_UploadStartedEvent(this.getId(), fileItem.uuid));
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
			this.onFileItemClicked.fire(EventFactory.createUiSimpleFileField_FileItemClickedEvent(this.getId(), fileItem.uuid));
		});
		fileItem.onDeleteButtonClick.addListener(() => {
			this.onFileItemRemoved.fire(EventFactory.createUiSimpleFileField_FileItemRemovedEvent(this.getId(), fileItem.uuid));
			this.removeFileItem(fileItem.uuid);
			this.updateVisibilities();
			this.commit();
		});
		fileItem.onUploadTooLarge.addListener(() => {
			this.onUploadTooLarge.fire(EventFactory.createUiSimpleFileField_UploadTooLargeEvent(this.getId(), fileItem.uuid));
			this.updateVisibilities();
		});
		fileItem.onUploadSuccessful.addListener(uploadedFileUuid => {
			this.onUploadSuccessful.fire(EventFactory.createUiSimpleFileField_UploadSuccessfulEvent(this.getId(), fileItem.uuid, uploadedFileUuid));
			this.updateVisibilities();
		});
		fileItem.onUploadCanceled.addListener(() => {
			this.onUploadCanceled.fire(EventFactory.createUiSimpleFileField_UploadCanceledEvent(this.getId(), fileItem.uuid));
			this.updateVisibilities();
		});
		fileItem.onUploadFailed.addListener(() => {
			this.onUploadFailed.fire(EventFactory.createUiSimpleFileField_UploadFailedEvent(this.getId(), fileItem.uuid));
			this.updateVisibilities();
		});
		return fileItem;
	}

}

class UiFileItem {
	private static LOGGER: Logger = log.getLogger("UploadItem");
	public readonly onClick: TeamAppsEvent<void> = new TeamAppsEvent<void>(this);
	public readonly onDeleteButtonClick: TeamAppsEvent<void> = new TeamAppsEvent<void>(this);
	public readonly onUploadCanceled: TeamAppsEvent<void> = new TeamAppsEvent(this);
	public readonly onUploadFailed: TeamAppsEvent<void> = new TeamAppsEvent(this);
	public readonly onUploadSuccessful: TeamAppsEvent<string> = new TeamAppsEvent(this);
	public readonly onUploadTooLarge: TeamAppsEvent<void> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private $progressIndicator: HTMLElement;
	private $deleteButton: HTMLElement;
	private $fileIcon: HTMLElement;
	private $fileName: HTMLElement;
	private $fileDescription: HTMLElement;
	private $fileSize: HTMLElement;

	private progressIndicator: ProgressIndicator;
	private uploader: FileUploader;

	constructor(
		private displayMode: UiFileFieldDisplayType,
		private maxBytes: number,
		private fileTooLargeMessage: string,
		private uploadErrorMessage: string,
		private uploadUrl: string,
		private config: UiFileItemConfig,
		public state: FileItemState,
		private context: TeamAppsUiContext
	) {
		this.$main = parseHtml(`<a class="file-item">
			<div class="delete-button-wrapper">
				<img class="delete-button img img-16" alt="delete" tabindex="0" src="${StaticIcons.CLOSE}"></img>
			</div>
			<div class="progress-indicator"></div>
			<div class="file-icon img img-48"></div>
			<div class="file-name"></div>
			<div class="file-description"></div>
			<div class="file-size"></div>
		</a>`);
		this.$fileIcon = this.$main.querySelector<HTMLElement>(":scope .file-icon");
		this.$fileName = this.$main.querySelector<HTMLElement>(":scope .file-name");
		this.$fileDescription = this.$main.querySelector<HTMLElement>(":scope .file-description");
		this.$fileSize = this.$main.querySelector<HTMLElement>(":scope .file-size");
		this.$progressIndicator = this.$main.querySelector<HTMLElement>(":scope .progress-indicator");
		this.$deleteButton = this.$main.querySelector<HTMLElement>(":scope .delete-button");
		this.$main.addEventListener('click', (e) => {
			if (e.target === this.$deleteButton) {
				if (this.uploader) {
					this.uploader.abort();
					this.onUploadCanceled.fire(null);
				}
				this.onDeleteButtonClick.fire(null);
				e.preventDefault(); // do not download!
				e.stopPropagation();
			} else {
				this.onClick.fire(null)
			}
		});
		this.setDisplayMode(displayMode);
		this.update(config);
	}

	public upload(file: File) {
		this.config.fileName = file.name;
		this.config.description = file.type;
		this.config.size = file.size;
		this.update(this.config);

		this.progressIndicator = UiFileItem.createProgressIndicator(this.displayMode);
		this.$progressIndicator.appendChild(this.progressIndicator.getMainDomElement());

		if (file.size > this.maxBytes) {
			this.progressIndicator.setErrorMessage(this.fileTooLargeMessage);
			this.setState(FileItemState.FAILED);
			this.onUploadTooLarge.fire(null);
		} else {
			this.setState(FileItemState.UPLOADING);

			this.uploader = new FileUploader();
			this.uploader.upload(file, this.uploadUrl);
			this.uploader.onProgress.addListener(progress => this.progressIndicator.setProgress(progress));
			this.uploader.onSuccess.addListener(fileUuid => {
				this.setState(FileItemState.DONE);
				this.onUploadSuccessful.fire(fileUuid);
			});
			this.uploader.onError.addListener(() => {
				this.setState(FileItemState.FAILED);
				this.progressIndicator.setErrorMessage(this.uploadErrorMessage);
				this.onUploadFailed.fire(null);
			});
			this.uploader.onComplete.addListener(() => this.uploader = null)
		}
	}

	private static createProgressIndicator(displayMode: UiFileFieldDisplayType) {
		return displayMode == UiFileFieldDisplayType.FLOATING ? new ProgressCircle(0, {
			circleRadius: 24, circleStrokeWidth: 3
		}) : new ProgressBar(0, {});
	}

	public setDisplayMode(displayMode: UiFileFieldDisplayType) {
		this.displayMode = displayMode;
		if (this.progressIndicator != null) {
			this.progressIndicator.getMainDomElement().remove();
			this.progressIndicator = UiFileItem.createProgressIndicator(displayMode);
			this.$progressIndicator.appendChild(this.progressIndicator.getMainDomElement());
		}
		this.$fileName.classList.toggle("line-clamp-2", displayMode == UiFileFieldDisplayType.FLOATING);
		this.$fileIcon.classList.toggle("img-48", displayMode == UiFileFieldDisplayType.FLOATING);
		this.$fileIcon.classList.toggle("img-32", displayMode == UiFileFieldDisplayType.LIST);
	}

	private setState(state: FileItemState) {
		this.state = state;
		removeClassesByFunction(this.$main.classList, className => className.startsWith("state-"));
		this.$main.classList.add("state-" + FileItemState[state].toLowerCase());
	}

	public getMainDomElement() {
		return this.$main;
	}

	update(config: UiFileItemConfig) {
		this.config = config;

		if (config.thumbnail) {
			this.$fileIcon.style.backgroundImage = `url(${config.thumbnail}`;
		} else {
			this.$fileIcon.style.backgroundImage = `url(${this.context.getIconPath(config.icon, 48)})`;
		}
		this.$fileName.textContent = config.fileName;
		this.$fileDescription.textContent = config.description;
		this.$fileSize.textContent = humanReadableFileSize(config.size);
		this.$main.classList.toggle("no-link", config.linkUrl == null);
		this.$main.classList.toggle("no-link", config.linkUrl == null);
		this.$main.setAttribute("href",  config.linkUrl);
	}

	public get uuid() {
		return this.config.uuid
	}

	public get icon() {
		return this.config.icon
	}

	public get thumbnail() {
		return this.config.thumbnail
	}

	public get fileName() {
		return this.config.fileName
	}

	public get description() {
		return this.config.description
	}

	public get size() {
		return this.config.size
	}

	public get linkUrl() {
		return this.config.linkUrl
	}
}

enum FileItemState {
	INITIATING,
	TOO_LARGE,
	UPLOADING,
	FAILED,
	DONE
}


TeamAppsUiComponentRegistry.registerFieldClass("UiSimpleFileField", UiSimpleFileField);