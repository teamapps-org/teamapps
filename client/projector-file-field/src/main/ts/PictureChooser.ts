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
	executeAfterAttached,
	FieldEditingMode,
	FileUploader,
	humanReadableFileSize,
	parseHtml,
	ProjectorEvent
} from "projector-client-object-api";
import {
	DtoPictureChooser,
	DtoPictureChooser_UploadCanceledEvent,
	DtoPictureChooser_UploadFailedEvent,
	DtoPictureChooser_UploadInitiatedByUserEvent,
	DtoPictureChooser_UploadStartedEvent, DtoPictureChooser_UploadSuccessfulEvent, DtoPictureChooser_UploadTooLargeEvent,
	DtoPictureChooserCommandHandler,
	DtoPictureChooserEventSource
} from "./generated";
import {ProgressCircle, ProgressIndicator} from "projector-progress-indicator";

/**
 * @author Yann Massard (yamass@gmail.com)
 */
export class PictureChooser extends AbstractField<DtoPictureChooser, string> implements DtoPictureChooserEventSource, DtoPictureChooserCommandHandler {

	public readonly onUploadCanceled: ProjectorEvent<DtoPictureChooser_UploadCanceledEvent> = new ProjectorEvent();
	public readonly onUploadFailed: ProjectorEvent<DtoPictureChooser_UploadFailedEvent> = new ProjectorEvent();
	public readonly onUploadInitiatedByUser: ProjectorEvent<DtoPictureChooser_UploadInitiatedByUserEvent> = new ProjectorEvent();
	public readonly onUploadStarted: ProjectorEvent<DtoPictureChooser_UploadStartedEvent> = new ProjectorEvent();
	public readonly onUploadSuccessful: ProjectorEvent<DtoPictureChooser_UploadSuccessfulEvent> = new ProjectorEvent();
	public readonly onUploadTooLarge: ProjectorEvent<DtoPictureChooser_UploadTooLargeEvent> = new ProjectorEvent();

	private $main: HTMLElement;
	private $picture: HTMLElement;
	private $uploadButton: HTMLElement;
	private $fileInput: HTMLInputElement;
	private $fileItemWrapper: HTMLElement;
	private $progressWrapper: HTMLElement;
	private $pictureWrapper: HTMLElement;
	private $deleteButton: HTMLElement;

	private maxFileSize: number;
	private fileTooLargeMessage: string;
	private uploadErrorMessage: string;
	private uploadUrl: string;
	private uploader: FileUploader;
	private progressIndicator: ProgressIndicator;

	protected initialize(config: DtoPictureChooser): void {
		this.$main = parseHtml(`<div class="UiPictureChooser drop-zone form-control field-border field-border-glow field-background">
    <div class="picture-wrapper">
    	<div class="picture hidden"></div>
		<div class="progress-wrapper hidden"></div>
		<input class="file-input" type="file" multiple tabindex="-1" accept="image/png,image/jpeg,image/bmp,image/gif"></input>
		<div class="button upload-button icon img img-16" style="background-image: url('${config.browseButtonIcon}');"></div>
		<div class="button delete-button icon img img-16" style="background-image: url('${config.deleteButtonIcon}');"></div>
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

		this.$pictureWrapper = this.$main.querySelector(":scope .picture-wrapper");
		this.$picture = this.$main.querySelector(":scope .picture");

		this.$uploadButton = this.$main.querySelector<HTMLElement>(':scope .upload-button');
		["click", "keypress"].forEach(eventName => this.$uploadButton.addEventListener(eventName, (e: MouseEvent & KeyboardEvent) => {
			if (e.button == 0 || e.key === "Enter" || e.key === " ") {
				this.$fileInput.click();
				e.stopPropagation();
				return false; // no scrolling when space is pressed!
			}
		}));
		this.$main.addEventListener('click', e => {
			if (this.isEditable() && this.getCommittedValue() == null) {
				this.$fileInput.click();
			}
		});

		this.$deleteButton = this.$main.querySelector(':scope .delete-button');
		this.$deleteButton.addEventListener('click', e => {
			e.stopPropagation();
			if (this.getCommittedValue() != null) {
				this.setCommittedValue(null);
				this.commit(true);
			}
		});
		this.$fileInput = this.$main.querySelector<HTMLInputElement>(':scope .file-input');
		this.$fileInput.addEventListener('change', () => {
			const files = (<HTMLInputElement>this.$fileInput).files;
			this.handleFiles(files);
		});

		this.$fileItemWrapper = this.$main.querySelector<HTMLElement>(':scope .picture-wrapper');
		
		this.$progressWrapper = this.$main.querySelector(':scope .progress-wrapper');
		this.progressIndicator = new ProgressCircle(0, {
			circleRadius: 24, circleStrokeWidth: 3
		});
		this.$main.querySelector(":scope .progress-wrapper")
			.appendChild(this.progressIndicator.getMainDomElement());

		this.setBrowseButtonIcon(config.browseButtonIcon);
		this.setMaxFileSize(config.maxFileSize);
		this.setFileTooLargeMessage(config.fileTooLargeMessage);
		this.setUploadErrorMessage(config.uploadErrorMessage);
		this.setUploadUrl(config.uploadUrl);
		this.setImageDisplaySize(config.imageDisplayWidth, config.imageDisplayHeight);
	}

	protected displayCommittedValue(): void {
		this.getMainInnerDomElement().classList.toggle("empty", this.getCommittedValue() == null);
		this.$deleteButton.classList.toggle("hidden", this.getCommittedValue() == null);
		if (this.getCommittedValue() != null) {
			this.$picture.classList.remove("hidden");
			this.$picture.style.backgroundImage = `url('${this.getCommittedValue()}')`;
		} else {
			this.$picture.classList.add("hidden");
		}
	}

	getDefaultValue(): string {
		return null;
	}

	@executeAfterAttached()
	focus(): void {
		this.$uploadButton.focus();
	}
	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	getTransientValue(): string {
		return this.getCommittedValue();
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode, oldEditingMode?: FieldEditingMode): void {
		AbstractField.defaultOnEditingModeChangedImpl(this, () => this.$uploadButton);
		this.updateVisibilities();
	}

	valuesChanged(v1: string, v2: string): boolean {
		return v1 === v2;
	}

	setBrowseButtonIcon(browseButtonIcon: string): void {
		this.$uploadButton.style.backgroundImage = browseButtonIcon;
	}

	setFileTooLargeMessage(fileTooLargeMessage: string): void {
		this.fileTooLargeMessage = fileTooLargeMessage;
	}

	setMaxFileSize(maxFileSize: number): void {
		this.maxFileSize = maxFileSize;
	}

	setUploadErrorMessage(uploadErrorMessage: string): void {
		this.uploadErrorMessage = uploadErrorMessage;
	}

	setUploadUrl(uploadUrl: string): void {
		this.uploadUrl = uploadUrl
	}

	private updateVisibilities() {
		this.$uploadButton.classList.toggle("hidden", !this.isEditable());
	}

	private handleFiles(files: FileList) {
		const file = files[0];

		this.onUploadInitiatedByUser.fire({
			fileName: file.name,
			mimeType: file.type,
			sizeInBytes: file.size
		});

		this.$progressWrapper.classList.remove("hidden");

		if (file.size > this.maxFileSize) {
			this.onUploadTooLarge.fire({
				fileName: file.name,
				mimeType: file.type,
				sizeInBytes: file.size
			});
			this.progressIndicator.setErrorMessage(formatString(this.fileTooLargeMessage, humanReadableFileSize(this.maxFileSize)));
			return;
		}

		this.onUploadStarted.fire({
			fileName: file.name,
			mimeType: file.type,
			sizeInBytes: file.size
		});

		this.uploader = new FileUploader();
		this.uploader.upload(file, this.uploadUrl);
		this.uploader.onProgress.addListener(progress => this.progressIndicator.setProgress(progress));
		this.uploader.onSuccess.addListener(fileUuid => {
			this.onUploadSuccessful.fire({
				fileName: file.name,
				mimeType: file.type,
				sizeInBytes: file.size,
				uploadedFileUuid: fileUuid
			});
			this.$progressWrapper.classList.add("hidden");
		});
		this.uploader.onError.addListener(() => {
			this.progressIndicator.setErrorMessage(this.uploadErrorMessage);
			this.onUploadFailed.fire({
				fileName: file.name,
				mimeType: file.type,
				sizeInBytes: file.size
			});
		});
		this.uploader.onComplete.addListener(() => {
			this.uploader = null;
			this.progressIndicator.setProgress(0);
		});
		this.$fileInput.value = '';
	}


	private setImageDisplaySize(imageDisplayWidth: number, imageDisplayHeight: number) {
		this.$pictureWrapper.style.width = imageDisplayWidth + "px";
		this.$pictureWrapper.style.height = imageDisplayHeight + "px";
	}

	cancelUpload(): any {
		// TODO
	}
}

export function formatString(s: string, ...params: any[]) {
	for (let i = 0; i < params.length; i++) {
		s = s.replace(`{${i}}`, params[i]);
	}
	return s;
}


