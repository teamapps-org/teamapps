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

import {AbstractLegacyComponent, fadeOut, FileUploader, parseHtml, ServerObjectChannel, TeamAppsEvent} from "projector-client-object-api";
import {
	createDtoChatNewFile,
	createDtoNewChatMessage,
	DtoChatInput,
	DtoChatInput_FileItemClickedEvent,
	DtoChatInput_FileItemRemovedEvent,
	DtoChatInput_MessageSentEvent,
	DtoChatInput_UploadCanceledEvent,
	DtoChatInput_UploadFailedEvent,
	DtoChatInput_UploadStartedEvent, DtoChatInput_UploadSuccessfulEvent, DtoChatInput_UploadTooLargeEvent,
	DtoChatInputCommandHandler,
	DtoChatInputEventSource
} from "./generated";
import {createImageThumbnailUrl, insertAtCursorPosition} from "teamapps-client-core-components";
import {ProgressBar} from "projector-progress-indicator";

export class ChatInput extends AbstractLegacyComponent<DtoChatInput> implements DtoChatInputCommandHandler, DtoChatInputEventSource {

	onFileItemClicked: TeamAppsEvent<DtoChatInput_FileItemClickedEvent> = new TeamAppsEvent();
	onFileItemRemoved: TeamAppsEvent<DtoChatInput_FileItemRemovedEvent> = new TeamAppsEvent();
	onMessageSent: TeamAppsEvent<DtoChatInput_MessageSentEvent> = new TeamAppsEvent();
	onUploadCanceled: TeamAppsEvent<DtoChatInput_UploadCanceledEvent> = new TeamAppsEvent();
	onUploadFailed: TeamAppsEvent<DtoChatInput_UploadFailedEvent> = new TeamAppsEvent();
	onUploadStarted: TeamAppsEvent<DtoChatInput_UploadStartedEvent> = new TeamAppsEvent();
	onUploadSuccessful: TeamAppsEvent<DtoChatInput_UploadSuccessfulEvent> = new TeamAppsEvent();
	onUploadTooLarge: TeamAppsEvent<DtoChatInput_UploadTooLargeEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $uploadItems: HTMLElement;
	private uploadItems: FileUploadItem[] = [];
	private $textInput: HTMLInputElement;
	private $sendButton: HTMLElement;
	private $attachmentButton: Element;

	constructor(config: DtoChatInput, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$main = parseHtml(`<div class="ChatInput drop-zone">
	<div class="upload-items"></div>
	<textarea class="text-input" maxlength="${config.messageLengthLimit}"></textarea>
	<div class="button attachment-button glyphicon glyphicon-paperclip glyphicon-button glyphicon-button-md"></div>
	<div class="button send-button glyphicon glyphicon-send glyphicon-button glyphicon-button-md"></div>
	<input class="file-input" type="file" multiple tabindex="-1"></input>
</div>`);
		this.$uploadItems = this.$main.querySelector(":scope .upload-items");
		this.$textInput = this.$main.querySelector(":scope .text-input");
		this.$sendButton = this.$main.querySelector(":scope .send-button");
		this.$attachmentButton = this.$main.querySelector(":scope .attachment-button");
		const $fileInput = this.$main.querySelector<HTMLInputElement>(":scope .file-input");

		this.$textInput.addEventListener("keyup", () => this.updateSendability());
		this.$textInput.addEventListener("keydown", (e: MouseEvent) => {
			if ((e as any).key === 'Enter') {
				if (e.shiftKey) {
					insertAtCursorPosition(this.$textInput, "\n");
				} else {
					this.send();
				}
				e.preventDefault(); // no new-line character in the text input, please
			}
		});

		this.$attachmentButton.addEventListener("click", () => $fileInput.click());
		$fileInput.addEventListener("change", e => {
			this.upload($fileInput.files);
			$fileInput.value = "";
		});

		this.$sendButton.addEventListener("click", () => this.send());

		this.$main.addEventListener("dragover", (e) => {
			if (!this.config.attachmentsEnabled) {
				return;
			}
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
		this.$main.addEventListener("drop", (e) => { // I don't know why I cannot just register this the same way as the other handlers...
			e.stopPropagation();
			e.preventDefault();
			this.$main.classList.remove("drop-zone-active");
			const files = e.dataTransfer.files;
			this.upload(files);
		});

		this.setAttachmentsEnabled(config.attachmentsEnabled);
		this.updateSendability();
	}

	setDefaultFileIcon(defaultFileIcon: string) {
		this.config.defaultFileIcon = defaultFileIcon;
	}

	setMaxBytesPerUpload(maxBytesPerUpload: number) {
		this.config.maxBytesPerUpload = maxBytesPerUpload;
	}

	setUploadUrl(uploadUrl: string) {
		this.config.uploadUrl = uploadUrl;
	}

	setMessageLengthLimit(messageLengthLimit: number) {
		this.config.messageLengthLimit = messageLengthLimit;
		this.$textInput.maxLength = messageLengthLimit;
	}

	private send() {
		if (!this.sendable()) {
			return;
		}

		this.onMessageSent.fire({
			message: createDtoNewChatMessage({
				text: this.$textInput.value,
				uploadedFiles: this.uploadItems
					.filter(item => item.state === UploadState.SUCCESS)
					.map(item => createDtoChatNewFile({
						uploadedFileUuid: item.uploadedFileUuid,
						fileName: item.file.name
					}))
			})
		});
		this.$uploadItems.innerHTML = "";
		this.uploadItems = [];
		this.$textInput.value = "";
		this.updateSendability();
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	private upload(files: FileList) {
		if (!this.config.attachmentsEnabled) {
			return;
		}
		for (let i = 0; i < files.length; i++) {
			const file = files[i];
			const uploadItem = new FileUploadItem(file, this.config.defaultFileIcon, this.config.uploadUrl);
			if (file.size <= this.config.maxBytesPerUpload) {
				uploadItem.upload();
			} else {
				uploadItem.setError();
			}
			const $itemWrapper = parseHtml(`<div class="upload-item-wrapper">
	<div class="delete-button glyphicon glyphicon-remove glyphicon-button"></div>
</div>`);
			const $deleteButton = $itemWrapper.querySelector(":scope .delete-button");
			$deleteButton.addEventListener("click", () => {
				$itemWrapper.remove();
				this.uploadItems = this.uploadItems.filter(otherItem => otherItem !== uploadItem);
				this.updateSendability();
			});
			$itemWrapper.insertBefore(uploadItem.getMainDomElement(), $deleteButton);
			this.$uploadItems.appendChild($itemWrapper);

			this.uploadItems.push(uploadItem);
			uploadItem.onComplete.addListener(() => this.updateSendability())
		}
		this.updateSendability();
	}

	private updateSendability() {
		this.$sendButton.classList.toggle("disabled", !this.sendable());
	}

	private sendable() {
		const uploading = this.uploadItems.some(item => item.state === UploadState.IN_PROGRESS);
		const hasSuccessfulFileUploads = this.uploadItems.filter(item => item.state === UploadState.SUCCESS).length > 0;
		const hasTextInput = this.$textInput.value.length > 0;
		return !uploading && (hasSuccessfulFileUploads || hasTextInput);
	}

	public setAttachmentsEnabled(enabled: boolean) {
		this.config.attachmentsEnabled = enabled;
		this.$attachmentButton.classList.toggle("hidden", !enabled);
	}
}

enum UploadState {
	IN_PROGRESS, SUCCESS, ERROR
}

class FileUploadItem {
	public readonly onComplete: TeamAppsEvent<void> = new TeamAppsEvent();

	private $main: HTMLElement;
	public state: UploadState = UploadState.IN_PROGRESS;
	public uploadedFileUuid: string;
	private uploader: FileUploader;

	constructor(public file: File, defaultFileIcon: string, private uploadUrl: string) {
		this.$main = parseHtml(`<div class="upload-item">
	<div class="icon img img-24" style="background-image: url('${defaultFileIcon}')"></div>
	<div class="name">${file.name}</div>
</div>`);
		const $icon = this.$main.querySelector<HTMLElement>(":scope .icon");
		createImageThumbnailUrl(file)
			.then(url => $icon.style.backgroundImage = `url('${url}')`)
			.catch(reason => console.debug(`Could not create thumbnail for file ${file}. Reason: ${reason}.`));
		const progressBar = new ProgressBar(0, {});

		this.$main.appendChild(progressBar.getMainDomElement());

		this.uploader = new FileUploader();
		this.uploader.onProgress.addListener(progress => progressBar.setProgress(progress));
		this.uploader.onError.addListener(() => this.setError());
		this.uploader.onSuccess.addListener(uuid => {
			this.state = UploadState.SUCCESS;
			this.uploadedFileUuid = uuid;
			this.onComplete.fire(null);
			fadeOut(progressBar.getMainDomElement());
		});
	}

	public upload() {
		this.uploader.upload(this.file, this.uploadUrl);
	}

	public setError() {
		this.state = UploadState.ERROR;
		this.$main.classList.add("error");
		this.onComplete.fire(null);
	}

	public getMainDomElement() {
		return this.$main;
	}

}


