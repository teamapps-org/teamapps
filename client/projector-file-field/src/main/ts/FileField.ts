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

import {
	AbstractField,
	type DtoIdentifiableClientRecord,
	executeAfterAttached,
	FileUploader,
	generateUUID,
	humanReadableFileSize,
	parseHtml,
	prependChild,
	ProjectorEvent,
	removeClassesByFunction,
	type Template
} from "projector-client-object-api";
import {
	type DtoFileField,
	type DtoFileField_FileItemClickedEvent,
	type DtoFileField_FileItemRemoveButtonClickedEvent,
	type DtoFileField_UploadCanceledEvent,
	type DtoFileField_UploadFailedEvent,
	type DtoFileField_UploadStartedEvent,
	type DtoFileField_UploadSuccessfulEvent,
	type DtoFileField_UploadTooLargeEvent,
	type DtoFileFieldCommandHandler,
	type DtoFileFieldEventSource,
	type DtoFileFieldServerObjectChannel,
	type FileFieldDisplayType,
	FileFieldDisplayTypes
} from "./generated";
import {ProgressBar, ProgressCircle, type ProgressIndicator} from "projector-progress-indicator";

const FileItemStates = Object.freeze({
	UPLOADING: "uploading",
	ERROR: "error",
	SUCCESS: "success",
	DISPLAYING: "displaying"
});
type FileItemState = typeof FileItemStates[keyof typeof FileItemStates]

export class FileField extends AbstractField<DtoFileField, DtoIdentifiableClientRecord[]> implements DtoFileFieldEventSource, DtoFileFieldCommandHandler {

	public readonly onFileItemClicked: ProjectorEvent<DtoFileField_FileItemClickedEvent> = new ProjectorEvent();
	public readonly onFileItemRemoveButtonClicked: ProjectorEvent<DtoFileField_FileItemRemoveButtonClickedEvent> = new ProjectorEvent();
	public readonly onUploadCanceled: ProjectorEvent<DtoFileField_UploadCanceledEvent> = new ProjectorEvent();
	public readonly onUploadFailed: ProjectorEvent<DtoFileField_UploadFailedEvent> = new ProjectorEvent();
	public readonly onUploadStarted: ProjectorEvent<DtoFileField_UploadStartedEvent> = new ProjectorEvent();
	public readonly onUploadSuccessful: ProjectorEvent<DtoFileField_UploadSuccessfulEvent> = new ProjectorEvent();
	public readonly onUploadTooLarge: ProjectorEvent<DtoFileField_UploadTooLargeEvent> = new ProjectorEvent();

	private $wrapper: HTMLElement;
	private $uploadButton: HTMLElement;
	private $fileInput: HTMLInputElement;
	private $uploadButtonTemplate: HTMLElement;
	private $fileList: HTMLElement;

	private fileItems: UploadItem[] = [];

	constructor(config: DtoFileField, serverObjectChannel: DtoFileFieldServerObjectChannel) {
		super(config, serverObjectChannel);
		let uuid = "ui-file-field" + generateUUID();
		this.$wrapper = parseHtml(`<div class="FileField drop-zone" id="${uuid}">
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
			const files = e.dataTransfer?.files ?? new FileList();
			this.handleFiles(files);
		};

		this.$uploadButtonTemplate = this.$wrapper.querySelector<HTMLElement>(':scope .upload-button-template')!;
		this.$uploadButton = this.$wrapper.querySelector<HTMLElement>(':scope .upload-button-wrapper')!;
		this.$uploadButton.addEventListener('keydown', e => {
			if (e.key === "Enter" || e.key === " ") {
				this.$fileInput.click();
				return false; // no scrolling when space is pressed!
			}
		});
		this.$fileInput = this.$wrapper.querySelector<HTMLInputElement>(':scope .file-input')!;
		this.$fileInput.addEventListener('change', () => {
			const files = (this.$fileInput as HTMLInputElement).files ?? new FileList();
			this.handleFiles(files);
		});

		this.$fileList = this.$wrapper.querySelector<HTMLElement>(':scope .list')!;

		this.setShowEntriesAsButtonsOnHover(config.showEntriesAsButtonsOnHover);
		this.setUploadButtonTemplate(config.uploadButtonTemplate as Template);
		this.setUploadButtonData(config.uploadButtonData);
		this.setDisplayType(config.displayType ?? null);
		this.setAcceptedFileTypes(config.acceptedFileTypes ?? []);

		this.displayCommittedValue();
		this.updateVisibilities();
	}

	isValidData(v: any[]): boolean {
		return v == null || Array.isArray(v);
	}

	public setItemTemplate(template: Template): void {
		this.config.itemTemplate = template;
		this.displayCommittedValue();
	}

	setDisplayType(displayType: FileFieldDisplayType): void {
		this.config.displayType = displayType;
		this.$wrapper.classList.toggle("float-style-vertical-list", displayType === FileFieldDisplayTypes.LIST);
		this.$wrapper.classList.toggle("float-style-horizontal", displayType === FileFieldDisplayTypes.FLOATING);
	}

	setMaxFiles(maxFiles: number): void {
		this.config.maxFiles = maxFiles || Number.MAX_SAFE_INTEGER;
		this.updateVisibilities();
	}

	setAcceptedFileTypes(acceptedFileTypes: string[]) {
		this.config.acceptedFileTypes = acceptedFileTypes;
		this.$fileInput.accept = acceptedFileTypes.join(',');
	}

	public setMaxBytesPerFile(maxBytesPerFile: number): void {
		this.config.maxBytesPerFile = maxBytesPerFile;
	}

	public setShowEntriesAsButtonsOnHover(showEntriesAsButtonsOnHover: boolean): void {
		this.config.showEntriesAsButtonsOnHover = showEntriesAsButtonsOnHover;
		this.$wrapper.classList.toggle("show-entries-as-buttons-on-hover", showEntriesAsButtonsOnHover);
	}

	public setUploadButtonData(uploadButtonData: any): void {
		this.config.uploadButtonData = uploadButtonData;
		this.$uploadButtonTemplate.innerHTML = '';
		if (this.config.uploadButtonTemplate && this.config.uploadButtonData) {
			this.$uploadButtonTemplate.appendChild(parseHtml((this.config.uploadButtonTemplate as Template).render(this.config.uploadButtonData)));
		}
	}

	public setUploadButtonTemplate(uploadButtonTemplate: Template): void {
		this.config.uploadButtonTemplate = uploadButtonTemplate;
		this.$uploadButtonTemplate.innerHTML = '';
		if (this.config.uploadButtonTemplate && this.config.uploadButtonData) {
			this.$uploadButtonTemplate.appendChild(parseHtml((this.config.uploadButtonTemplate as Template).render(this.config.uploadButtonData)));
		}
	}

	public setUploadUrl(uploadUrl: string): void {
		this.config.uploadUrl = uploadUrl;
	}


	private handleFiles(files: FileList) {
		let numberOfFilesToAdd = files.length;
		if (this.config.maxFiles && this.numberOfNonErrorFileItems + files.length > this.config.maxFiles) {
			numberOfFilesToAdd = this.config.maxFiles - this.numberOfNonErrorFileItems;
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
		return this.fileItems.filter(fileItem => fileItem.state !== FileItemStates.ERROR).length;
	}

	private get numberOfUploadingFileItems() {
		return this.fileItems.filter(fileItem => fileItem.state === FileItemStates.UPLOADING).length;
	}

	private resetFileInput() {
		this.$fileInput.value = '';
	}

	private updateVisibilities() {
		this.$uploadButton.classList.toggle("hidden", !(this.isEditable() && this.numberOfNonErrorFileItems < this.config.maxFiles));
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$wrapper;
	}

	@executeAfterAttached()
	focus(): void {
		this.$uploadButton.focus();
	}

	protected displayCommittedValue(): void {
		let uiValue = this.getCommittedValue();
		if (!uiValue) {
			this.fileItems
				.filter(fileItem => fileItem.state === FileItemStates.DISPLAYING)
				.forEach(fileItem => {
					this.removeFileItem(fileItem);
				});
		} else {
			this.fileItems
				.filter(fileItem => fileItem.state === FileItemStates.DISPLAYING)
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
		let fileItem = new UploadItem(this.config.displayType === FileFieldDisplayTypes.FLOATING, this.config.maxBytesPerFile, this.config.fileTooLargeMessage, this.config.uploadErrorMessage, this.config.uploadUrl, this.config.itemTemplate as Template, this.isEditable());
		fileItem.onClick.addListener(() => {
			this.onFileItemClicked.fire({
				clientId: fileItem.data!.id
			});
		});
		fileItem.onDeleteButtonClick.addListener(() => {
			let data = fileItem.data;
			if (data) {
				this.onFileItemRemoveButtonClicked.fire({
					clientId: data.id
				});
			}
			this.removeItem(fileItem);
		});
		fileItem.onUploadTooLarge.addListener(e => {
			this.onUploadTooLarge.fire(e);
			this.updateVisibilities();
		});
		fileItem.onUploadSuccessful.addListener(e => {
			e.incompleteUploadsCount = this.numberOfUploadingFileItems;
			this.onUploadSuccessful.fire(e);
			this.updateVisibilities();
		});
		fileItem.onUploadCanceled.addListener(e => {
			e.incompleteUploadsCount = this.numberOfUploadingFileItems;
			this.onUploadCanceled.fire(e);
			this.updateVisibilities();
		});
		fileItem.onUploadFailed.addListener(e => {
			this.onUploadFailed.fire(e);
			this.updateVisibilities();
		});
		return fileItem;
	}

	private removeItem(fileItem: UploadItem) {
		this.removeFileItem(fileItem);
		this.updateVisibilities();
		if (fileItem.state === FileItemStates.DISPLAYING) {
			this.commit();
		}
	}

	public getTransientValue(): DtoIdentifiableClientRecord[] {
		return this.fileItems
			.filter(fileItem => fileItem.state === FileItemStates.DISPLAYING)
			.map(fileItem => fileItem.data!);
	}

	protected onEditingModeChanged(): void {
		AbstractField.defaultOnEditingModeChangedImpl(this, () => this.$uploadButton);
		this.fileItems.forEach(fi => fi.deletable = this.isEditable());
		this.updateVisibilities();
	}

	public getReadOnlyHtml(value: DtoIdentifiableClientRecord[], availableWidth: number): string {
		let content: string;
		if (value != null) {
			content = value.map((entry) => (this.config.itemTemplate as Template).render(entry.values)).join("");
		} else {
			content = "";
		}
		return `<div class="static-readonly-FileField default-min-field-width">${content}</div>`
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

	setFileTooLargeMessage(fileTooLargeMessage: string): void {
		this.config.fileTooLargeMessage = fileTooLargeMessage;
	}

	setUploadErrorMessage(uploadErrorMessage: string): void {
		this.config.uploadErrorMessage = uploadErrorMessage;
	}
}

class UploadItem {
	public readonly onDeleteButtonClick: ProjectorEvent<void> = new ProjectorEvent<void>();
	public readonly onClick: ProjectorEvent<void> = new ProjectorEvent<void>();
	public readonly onUploadCanceled: ProjectorEvent<DtoFileField_UploadCanceledEvent> = new ProjectorEvent();
	public readonly onUploadFailed: ProjectorEvent<DtoFileField_UploadFailedEvent> = new ProjectorEvent();
	public readonly onUploadSuccessful: ProjectorEvent<DtoFileField_UploadSuccessfulEvent> = new ProjectorEvent();
	public readonly onUploadTooLarge: ProjectorEvent<DtoFileField_UploadTooLargeEvent> = new ProjectorEvent();

	private $main: HTMLElement;

	private _data: DtoIdentifiableClientRecord | null = null;
	private _state: FileItemState | null = null;
	private $progressIndicator: HTMLElement;
	private $deleteButton: HTMLElement;
	private $fileSize: HTMLElement;
	private $fileName: HTMLElement;
	public readonly uuid: string = generateUUID();
	private progressIndicator: ProgressIndicator | null = null;
	private $templateWrapper: HTMLElement;

	private uploadingFile: File | null = null;
	private uploader: FileUploader | null = null;

	private renderAsCircle: boolean;
	private maxBytes: number;
	private fileTooLargeMessage: string;
	private uploadErrorMessage: string;
	private uploadUrl: string;
	private renderer: Template;
	private _deletable: boolean;

	constructor(
		renderAsCircle: boolean,
		maxBytes: number,
		fileTooLargeMessage: string,
		uploadErrorMessage: string,
		uploadUrl: string,
		renderer: Template,
		_deletable: boolean
	) {
		this.renderAsCircle = renderAsCircle;
		this.maxBytes = maxBytes;
		this.fileTooLargeMessage = fileTooLargeMessage;
		this.uploadErrorMessage = uploadErrorMessage;
		this.uploadUrl = uploadUrl;
		this.renderer = renderer;
		this._deletable = _deletable;
		this.$main = parseHtml(`<div class="file-item ${this._deletable ? 'deletable' : ''}">
			<div class="progress-indicator"></div>
			<div class="file-info">
				<div class="file-name"></div>
				<div class="file-size"></div>
			</div>
			<div class="template-wrapper"></div>
			<div class="delete-button img img-16 ta-icon-close hoverable-icon" tabindex="0"></d>
		</div>`);
		this.$fileName = this.$main.querySelector<HTMLElement>(":scope .file-name")!;
		this.$fileSize = this.$main.querySelector<HTMLElement>(":scope .file-size")!;
		this.$templateWrapper = this.$main.querySelector<HTMLElement>(":scope .template-wrapper")!;
		this.$progressIndicator = this.$main.querySelector<HTMLElement>(":scope .progress-indicator")!;
		this.$deleteButton = this.$main.querySelector<HTMLElement>(":scope .delete-button")!;
		this.$main.addEventListener('click', (e) => {
			if (e.target === this.$deleteButton) {
				this.cancelUpload(true);
				this.onDeleteButtonClick.fire();
			} else {
				this.onClick.fire()
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
			this.setState(FileItemStates.ERROR);
			this.onUploadTooLarge.fire({
				fileItemUuid: this.uuid,
				fileName: file.name,
				mimeType: file.type,
				sizeInBytes: file.size
			});
		} else {
			this.setState(FileItemStates.UPLOADING);

			this.uploader = new FileUploader();
			this.uploader.upload(file, this.uploadUrl);
			this.uploader.onProgress.addListener(progress => this.progressIndicator!.setProgress(progress));
			this.uploader.onSuccess.addListener(fileUuid => {
				this.setState(FileItemStates.SUCCESS);
				this.onUploadSuccessful.fire({
					fileItemUuid: this.uuid,
					uploadedFileUuid: fileUuid,
					fileName: file.name,
					mimeType: file.type,
					sizeInBytes: file.size,
					incompleteUploadsCount: 0
				});
			});
			this.uploader.onError.addListener(() => {
				this.setState(FileItemStates.ERROR);
				this.progressIndicator!.setErrorMessage(this.uploadErrorMessage);
				this.onUploadFailed.fire({
					fileItemUuid: this.uuid,
					fileName: file.name,
					mimeType: file.type,
					sizeInBytes: file.size,
					incompleteUploadsCount: 0
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
				fileName: this.uploadingFile!.name,
				mimeType: this.uploadingFile!.type,
				sizeInBytes: this.uploadingFile!.size,
				incompleteUploadsCount: 0
			});
			this._state = FileItemStates.ERROR;
		}
	}

	public set data(data: DtoIdentifiableClientRecord) {
		this._data = data;
		this.$templateWrapper.innerHTML = this.renderer.render(data.values);
		this.setState(FileItemStates.DISPLAYING);
	}

	public get data(): DtoIdentifiableClientRecord | null {
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

	public set deletable(deletable: boolean) {
		this._deletable = deletable;
		this.$main.classList.toggle("deletable", deletable)
	}

}


