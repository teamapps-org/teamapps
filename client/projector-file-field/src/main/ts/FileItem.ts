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

import {FileUploader, humanReadableFileSize, parseHtml, removeClassesByFunction, ProjectorEvent} from "projector-client-object-api";
import {ProgressBar, ProgressCircle, ProgressIndicator} from "projector-progress-indicator";
import {DtoFileItem, FileFieldDisplayType} from "./generated";

export class FileItem {
	public readonly onClick: ProjectorEvent<void> = new ProjectorEvent<void>();
	public readonly onDeleteButtonClick: ProjectorEvent<void> = new ProjectorEvent<void>();
	public readonly onUploadCanceled: ProjectorEvent<void> = new ProjectorEvent();
	public readonly onUploadFailed: ProjectorEvent<void> = new ProjectorEvent();
	public readonly onUploadSuccessful: ProjectorEvent<string> = new ProjectorEvent();
	public readonly onUploadTooLarge: ProjectorEvent<void> = new ProjectorEvent();

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
		private displayMode: FileFieldDisplayType,
		private maxBytes: number,
		private fileTooLargeMessage: string,
		private uploadErrorMessage: string,
		private uploadUrl: string,
		private config: DtoFileItem,
		public state: FileItemState,
	) {
		this.$main = parseHtml(`<a class="file-item">
			<div class="delete-button-wrapper">
				<div class="delete-button img img-16 ta-icon-close hoverable-icon" tabindex="0"></div>
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

		this.progressIndicator = FileItem.createProgressIndicator(this.displayMode);
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

	private static createProgressIndicator(displayMode: FileFieldDisplayType) {
		return displayMode == FileFieldDisplayType.FLOATING ? new ProgressCircle(0, {
			circleRadius: 24, circleStrokeWidth: 3
		}) : new ProgressBar(0, {});
	}

	public setDisplayMode(displayMode: FileFieldDisplayType) {
		this.displayMode = displayMode;
		if (this.progressIndicator != null) {
			this.progressIndicator.getMainDomElement().remove();
			this.progressIndicator = FileItem.createProgressIndicator(displayMode);
			this.$progressIndicator.appendChild(this.progressIndicator.getMainDomElement());
		}
		this.$fileName.classList.toggle("line-clamp-2", displayMode == FileFieldDisplayType.FLOATING);
		this.$fileIcon.classList.toggle("img-48", displayMode == FileFieldDisplayType.FLOATING);
		this.$fileIcon.classList.toggle("img-32", displayMode == FileFieldDisplayType.LIST);
	}

	private setState(state: FileItemState) {
		this.state = state;
		removeClassesByFunction(this.$main.classList, className => className.startsWith("state-"));
		this.$main.classList.add("state-" + state);
	}

	public getMainDomElement() {
		return this.$main;
	}

	public destroy() {
		this.uploader && this.uploader.abort();
		this.getMainDomElement().remove();
	}

	update(config: DtoFileItem) {
		this.config = config;

		if (config.thumbnail) {
			this.$fileIcon.style.backgroundImage = `url('${config.thumbnail}')`;
		} else {
			this.$fileIcon.style.backgroundImage = `url('${config.icon}')`;
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

export enum FileItemState {
	INITIATING = "initiating",
	TOO_LARGE = "too-large",
	UPLOADING = "uploading",
	FAILED = "failed",
	DONE = "done"
}
