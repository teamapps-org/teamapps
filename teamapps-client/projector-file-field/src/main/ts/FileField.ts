/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

// @ts-ignore
import ICON_CLOSE from "@material-symbols/svg-400/outlined/close.svg";

import {
	AbstractField,
	DtoIdentifiableClientRecord,
	DtoTemplate, FieldEditingMode, FileUploader,
	generateUUID, humanReadableFileSize,
	parseHtml, prependChild, removeClassesByFunction,
	TeamAppsEvent,
	Template
} from "projector-client-object-api";
import {
	DtoFileField,
	DtoFileField_FileItemClickedEvent,
	DtoFileField_FileItemRemoveButtonClickedEvent,
	DtoFileField_UploadCanceledEvent,
	DtoFileField_UploadFailedEvent,
	DtoFileField_UploadStartedEvent, DtoFileField_UploadSuccessfulEvent, DtoFileField_UploadTooLargeEvent,
	DtoFileFieldCommandHandler,
	DtoFileFieldEventSource, FileFieldDisplayType
} from "./generated";
import {ProgressBar, ProgressCircle, ProgressIndicator} from "projector-progress-indicator";

export class FileField extends AbstractField<DtoFileField, DtoIdentifiableClientRecord[]> implements DtoFileFieldEventSource, DtoFileFieldCommandHandler {

	public readonly onFileItemClicked: TeamAppsEvent<DtoFileField_FileItemClickedEvent> = new TeamAppsEvent();
	public readonly onFileItemRemoveButtonClicked: TeamAppsEvent<DtoFileField_FileItemRemoveButtonClickedEvent> = new TeamAppsEvent();
	public readonly onUploadCanceled: TeamAppsEvent<DtoFileField_UploadCanceledEvent> = new TeamAppsEvent();
	public readonly onUploadFailed: TeamAppsEvent<DtoFileField_UploadFailedEvent> = new TeamAppsEvent();
	public readonly onUploadStarted: TeamAppsEvent<DtoFileField_UploadStartedEvent> = new TeamAppsEvent();
	public readonly onUploadSuccessful: TeamAppsEvent<DtoFileField_UploadSuccessfulEvent> = new TeamAppsEvent();
	public readonly onUploadTooLarge: TeamAppsEvent<DtoFileField_UploadTooLargeEvent> = new TeamAppsEvent();

	private $wrapper: HTMLElement;
	private $uploadButton: HTMLElement;
	private $fileInput: HTMLInputElement;
	private $uploadButtonTemplate: HTMLElement;
	private fileItems: UploadItem[];
	private itemRenderer: Template;

	private displayType: FileFieldDisplayType;
	private maxFiles: number;
	private maxBytesPerFile: number;
	private uploadButtonData: any;
	private uploadButtonTemplate: Template;
	private uploadUrl: string;
	private $fileList: HTMLElement;

	protected initialize(config: DtoFileField) {
		this.fileItems = [];
		let uuid = "ui-file-field" + generateUUID();
		this.$wrapper = parseHtml(`<div class="UiFileField drop-zone" id="${uuid}">
        <div class="list"></div>
        <div class="upload-button-wrapper field-border field-border-glow field-background" tabindex="0">
            <div class="upload-button-template"></div>
            <input class="file-input" type="file" multiple tabindex="-1"></input>
        </div>
</div>`);

		this.$wrapper.addEventListener("dragover", (e) => {
			this.$wrapper.classList.add("drop-zone-active");
			// preventDefault() is important as it indicates that the drop is possible!!! see https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Drag_operations#droptargets
			e.preventDefault();
		});
		this.$wrapper.addEventListener("dragleave", (e) => {
			this.$wrapper.classList.remove("drop-zone-active");
			e.preventDefault();
		});
		this.$wrapper.addEventListener("dragend", (e) => {
			this.$wrapper.classList.remove("drop-zone-active");
			e.preventDefault();
		});
		this.$wrapper.ondrop = (e) => { // I don't know why I cannot just register this the same way as the other handlers...
			e.stopPropagation();
			e.preventDefault();
			this.$wrapper.classList.remove("drop-zone-active");
			const files = e.dataTransfer.files;
			this.handleFiles(files);
		};

		this.$uploadButtonTemplate = this.$wrapper.querySelector<HTMLElement>(':scope .upload-button-template');
		this.$uploadButton = this.$wrapper.querySelector<HTMLElement>(':scope .upload-button-wrapper');
		this.$uploadButton.addEventListener('keydown', e => {
			if (e.key === "Enter" || e.key === " ") {
				this.$fileInput.click();
				return false; // no scrolling when space is pressed!
			}
		});
		this.$fileInput = this.$wrapper.querySelector<HTMLInputElement>(':scope .file-input');
		this.$fileInput.addEventListener('change', () => {
			const files = (<HTMLInputElement>this.$fileInput).files;
			this.handleFiles(files);
		});

		this.$fileList = this.$wrapper.querySelector<HTMLElement>(':scope .list');

		this.setItemTemplate(config.itemTemplate as Template);
		this.setMaxBytesPerFile(config.maxBytesPerFile);
		this.setShowEntriesAsButtonsOnHover(config.showEntriesAsButtonsOnHover);
		this.setUploadButtonTemplate(config.uploadButtonTemplate as Template);
		this.setUploadButtonData(config.uploadButtonData);
		this.setUploadUrl(config.uploadUrl);
		this.setDisplayType(config.displayType);
		this.setMaxFiles(config.maxFiles);
	}

	isValidData(v: any[]): boolean {
		return v == null || Array.isArray(v);
	}

	public setItemTemplate(template: Template): void {
		this.itemRenderer = template;
		this.displayCommittedValue();
	}

	setDisplayType(displayType: FileFieldDisplayType): void {
		this.displayType = displayType;
		this.$wrapper.classList.toggle("float-style-vertical-list", displayType === FileFieldDisplayType.LIST);
		this.$wrapper.classList.toggle("float-style-horizontal", displayType === FileFieldDisplayType.FLOATING);
	}

	setMaxFiles(maxFiles: number): void {
		this.maxFiles = maxFiles || Number.MAX_SAFE_INTEGER;
		this.updateVisibilities();
	}

	public setMaxBytesPerFile(maxBytesPerFile: number): void {
		this.maxBytesPerFile = maxBytesPerFile;
	}

	public setShowEntriesAsButtonsOnHover(showEntriesAsButtonsOnHover: boolean): void {
		this.$wrapper.classList.toggle("show-entries-as-buttons-on-hover", showEntriesAsButtonsOnHover);
	}

	public setUploadButtonData(uploadButtonData: any): void {
		this.uploadButtonData = uploadButtonData;
		this.$uploadButtonTemplate.innerHTML = '';
		if (this.uploadButtonTemplate && this.uploadButtonData) {
			this.$uploadButtonTemplate.appendChild(parseHtml(this.uploadButtonTemplate.render(this.uploadButtonData)));
		}
	}

	public setUploadButtonTemplate(uploadButtonTemplate: Template): void {
		this.uploadButtonTemplate = uploadButtonTemplate;
		this.$uploadButtonTemplate.innerHTML = '';
		if (this.uploadButtonTemplate && this.uploadButtonData) {
			this.$uploadButtonTemplate.appendChild(parseHtml(this.uploadButtonTemplate.render(this.uploadButtonData)));
		}
	}

	public setUploadUrl(uploadUrl: string): void {
		this.uploadUrl = uploadUrl;
	}


	private handleFiles(files: FileList) {
		let numberOfFilesToAdd = files.length;
		if (this.maxFiles && this.numberOfNonErrorFileItems + files.length > this.maxFiles) {
			numberOfFilesToAdd = this.maxFiles - this.numberOfNonErrorFileItems;
		}

		for (let i = 0; i < numberOfFilesToAdd; i++) {
			const file = files[i];
			let fileItem = this.createFileItem();
			this.$fileList.appendChild(fileItem.getMainDomElement());
			this.fileItems.push(fileItem);
			fileItem.upload(file);
			this.onUploadStarted.fire({
				fileItemUuid: fileItem.uuid,
				fileName: file.name,
				mimeType: file.type,
				sizeInBytes: file.size,
				incompleteUploadsCount: this.numberOfUploadingFileItems
			});
		}

		this.updateVisibilities();
		this.resetFileInput();
	}

	private get numberOfNonErrorFileItems() {
		return this.fileItems.filter(fileItem => fileItem.state !== FileItemState.ERROR).length;
	}

	private get numberOfUploadingFileItems() {
		return this.fileItems.filter(fileItem => fileItem.state === FileItemState.UPLOADING).length;
	}

	private resetFileInput() {
		this.$fileInput.value = '';
	}

	private updateVisibilities() {
		this.$uploadButton.classList.toggle("hidden", !(this.isEditable() && this.numberOfNonErrorFileItems < this.maxFiles));
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$wrapper;
	}
	focus(): void {
		this.$uploadButton.focus();
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (!uiValue) {
			this.fileItems
				.filter(fileItem => fileItem.state === FileItemState.DISPLAYING)
				.forEach(fileItem => {
					this.removeFileItem(fileItem);
				});
		} else {
			this.fileItems
				.filter(fileItem => fileItem.state === FileItemState.DISPLAYING)
				.forEach(unmatchedFileItem => {
					this.removeFileItem(unmatchedFileItem);
				});
			uiValue.slice().reverse().forEach(record => {
				let fileItem = this.createFileItem();
				fileItem.data = record;
				prependChild(this.$fileList, fileItem.getMainDomElement());
				this.fileItems.push(fileItem);
			});
		}
		this.updateVisibilities();
	}

	replaceFileItem(uuid: string, data: DtoIdentifiableClientRecord): void {
		let correspondingItem = uuid && this.fileItems.filter(fileItem => fileItem.uuid === uuid)[0];
		if (correspondingItem) {
			correspondingItem.data = data;
			this.commit();
		} else {
			console.warn("Could not replace non-existing file item: " + uuid);
		}
	}

	protected convertValueForSendingToServer(clientRecords: DtoIdentifiableClientRecord[]): any {
		return clientRecords.map(clientRecord => clientRecord.id);
	}

	private removeFileItem(itemToBeRemoved: UploadItem) {
		itemToBeRemoved.getMainDomElement().remove();
		this.fileItems = this.fileItems.filter(item => item !== itemToBeRemoved);
	}

	private createFileItem() {
		let fileItem = new UploadItem(this.displayType === FileFieldDisplayType.FLOATING, this.maxBytesPerFile, this.config.fileTooLargeMessage, this.config.uploadErrorMessage, this.uploadUrl, this.itemRenderer);
		fileItem.onClick.addListener((eventObject) => {
			this.onFileItemClicked.fire({
				clientId: fileItem.data.id
			});
		});
		fileItem.onDeleteButtonClick.addListener((eventObject) => {
			let data = fileItem.data;
			if (data) {
				this.onFileItemRemoveButtonClicked.fire({
					clientId: data.id
				});
			}
			this.removeItem(fileItem);
		});
		fileItem.onUploadTooLarge.addListener(subEvent => {
			this.onUploadTooLarge.fire(subEvent);
			this.updateVisibilities();
		});
		fileItem.onUploadSuccessful.addListener(subEvent => {
			subEvent.incompleteUploadsCount = this.numberOfUploadingFileItems;
			this.onUploadSuccessful.fire(subEvent);
			this.updateVisibilities();
		});
		fileItem.onUploadCanceled.addListener(subEvent => {
			subEvent.incompleteUploadsCount = this.numberOfUploadingFileItems;
			this.onUploadCanceled.fire(subEvent);
			this.updateVisibilities();
		});
		fileItem.onUploadFailed.addListener(subEvent => {
			this.onUploadFailed.fire(subEvent);
			this.updateVisibilities();
		});
		return fileItem;
	}

	private removeItem(fileItem: UploadItem) {
		this.removeFileItem(fileItem);
		this.updateVisibilities();
		if (fileItem.state === FileItemState.DISPLAYING) {
			this.commit();
		}
	}

	public getTransientValue(): DtoIdentifiableClientRecord[] {
		return this.fileItems
			.filter(fileItem => fileItem.state === FileItemState.DISPLAYING)
			.map(fileItem => fileItem.data);
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode): void {
		AbstractField.defaultOnEditingModeChangedImpl(this, () => this.$uploadButton);
		this.updateVisibilities();
	}

	public getReadOnlyHtml(value: DtoIdentifiableClientRecord[], availableWidth: number): string {
		let content: string;
		if (value != null) {
			content = value.map((entry) => this.itemRenderer.render(entry.values)).join("");
		} else {
			content = "";
		}
		return `<div class="static-readonly-UiFileField">${content}</div>`
	}

	getDefaultValue(): DtoIdentifiableClientRecord[] {
		return [];
	}

	public valuesChanged(v1: DtoIdentifiableClientRecord[], c2: DtoIdentifiableClientRecord[]): boolean {
		return true; // TODO pojos
	}

	public cancelAllUploads() {
		console.log("cancelAllUploads()");
		this.fileItems.forEach(fi => this.cancelUpload(fi.uuid));
	}

	public cancelUpload(uuid: string) {
		console.log("Canceling upload for " + uuid);
		let correspondingItem = uuid && this.fileItems.filter(fileItem => fileItem.uuid === uuid)[0];
		if (correspondingItem) {
			correspondingItem.cancelUpload(false);
			this.removeItem(correspondingItem);
		} else {
			console.warn("Could not cancel upload of non-existing file item");
		}
	}
}

enum FileItemState {
	UPLOADING = "uploading",
	ERROR = "error",
	SUCCESS = "success",
	DISPLAYING = "displaying"
}

class UploadItem {
	public readonly onDeleteButtonClick: TeamAppsEvent<void> = new TeamAppsEvent<void>();
	public readonly onClick: TeamAppsEvent<void> = new TeamAppsEvent<void>();
	public readonly onUploadCanceled: TeamAppsEvent<DtoFileField_UploadCanceledEvent> = new TeamAppsEvent();
	public readonly onUploadFailed: TeamAppsEvent<DtoFileField_UploadFailedEvent> = new TeamAppsEvent();
	public readonly onUploadSuccessful: TeamAppsEvent<DtoFileField_UploadSuccessfulEvent> = new TeamAppsEvent();
	public readonly onUploadTooLarge: TeamAppsEvent<DtoFileField_UploadTooLargeEvent> = new TeamAppsEvent();

	private $main: HTMLElement;

	private _data: DtoIdentifiableClientRecord;
	private _state: FileItemState | null;
	private $fileInfo: HTMLElement;
	private $progressIndicator: HTMLElement;
	private $deleteButton: HTMLElement;
	private $fileSize: HTMLElement;
	private $fileName: HTMLElement;
	public readonly uuid: string = generateUUID();
	private progressIndicator: ProgressIndicator;
	private $templateWrapper: HTMLElement;

	private uploadingFile: File;
	private uploader: FileUploader;

	constructor(
		private renderAsCircle: boolean,
		private maxBytes: number,
		private fileTooLargeMessage: string,
		private uploadErrorMessage: string,
		private uploadUrl: string,
		private renderer: Template,
	) {
		this.$main = parseHtml(`<div class="file-item">
			<div class="progress-indicator"></div>
			<div class="file-info">
				<div class="file-name"></div>
				<div class="file-size"></div>
			</div>
			<div class="template-wrapper"></div>
			<img class="delete-button img img-16" alt="delete" tabindex="0" src="${ICON_CLOSE}"></img>
		</div>`);
		this.$fileInfo = this.$main.querySelector<HTMLElement>(":scope .file-info");
		this.$fileName = this.$main.querySelector<HTMLElement>(":scope .file-name");
		this.$fileSize = this.$main.querySelector<HTMLElement>(":scope .file-size");
		this.$templateWrapper = this.$main.querySelector<HTMLElement>(":scope .template-wrapper");
		this.$progressIndicator = this.$main.querySelector<HTMLElement>(":scope .progress-indicator");
		this.$deleteButton = this.$main.querySelector<HTMLElement>(":scope .delete-button");
		this.$main.addEventListener('click', (e) => {
			if (e.target === this.$deleteButton) {
				this.cancelUpload(true);
				this.onDeleteButtonClick.fire(null);
			} else {
				this.onClick.fire(null)
			}
		});
	}

	public upload(file: File) {
		this.uploadingFile = file;
		this.$fileName.textContent = file.name;
		this.$fileSize.textContent = humanReadableFileSize(file.size, true);

		this.progressIndicator = this.renderAsCircle ? new ProgressCircle(0, {
			circleRadius: 16, circleStrokeWidth: 2
		}) : new ProgressBar(0, {});
		this.$progressIndicator.appendChild(this.progressIndicator.getMainDomElement());

		if (file.size > this.maxBytes) {
			this.progressIndicator.setErrorMessage(this.fileTooLargeMessage);
			this.setState(FileItemState.ERROR);
			this.onUploadTooLarge.fire({
				fileItemUuid: this.uuid,
				fileName: file.name,
				mimeType: file.type,
				sizeInBytes: file.size
			});
		} else {
			this.setState(FileItemState.UPLOADING);

			this.uploader = new FileUploader();
			this.uploader.upload(file, this.uploadUrl);
			this.uploader.onProgress.addListener(progress => this.progressIndicator.setProgress(progress));
			this.uploader.onSuccess.addListener(fileUuid => {
				this.setState(FileItemState.SUCCESS);
				this.onUploadSuccessful.fire({
					fileItemUuid: this.uuid,
					uploadedFileUuid: fileUuid,
					fileName: file.name,
					mimeType: file.type,
					sizeInBytes: file.size,
					incompleteUploadsCount: null
				});
			});
			this.uploader.onError.addListener(() => {
				this.setState(FileItemState.ERROR);
				this.progressIndicator.setErrorMessage(this.uploadErrorMessage);
				this.onUploadFailed.fire({
					fileItemUuid: this.uuid,
					fileName: file.name,
					mimeType: file.type,
					sizeInBytes: file.size,
					incompleteUploadsCount: null
				});
			});
			this.uploader.onComplete.addListener(() => this.uploader = null)
		}
	}

	public cancelUpload(fireEvent: boolean) {
		if (this.uploader) {
			this.uploader.abort();
			this.onUploadCanceled.fire({
				fileItemUuid: this.uuid,
				fileName: this.uploadingFile.name,
				mimeType: this.uploadingFile.type,
				sizeInBytes: this.uploadingFile.size,
				incompleteUploadsCount: null
			});
			this._state = FileItemState.ERROR;
		}
	}

	public set data(data: DtoIdentifiableClientRecord) {
		this._data = data;
		this.$templateWrapper.innerHTML = this.renderer.render(data.values);
		this.setState(FileItemState.DISPLAYING);
	}

	public get data() {
		return this._data;
	}

	public setRenderAsCircle(renderAsCircle: boolean) {
		this.renderAsCircle = renderAsCircle;
		// TODO update
	}

	private setState(state: FileItemState) {
		this._state = state;
		removeClassesByFunction(this.$main.classList, className => className.startsWith("state-"));
		this.$main.classList.add("state-" + state);
	}

	public get state() {
		return this._state;
	}

	public getMainDomElement() {
		return this.$main;
	}

}


