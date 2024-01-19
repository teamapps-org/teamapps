/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {createImageThumbnailUrl, fadeOut, insertAtCursorPosition, parseHtml} from "./Common";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {
	UiChatInput_FileItemClickedEvent,
	UiChatInput_FileItemRemovedEvent,
	UiChatInput_MessageSentEvent,
	UiChatInput_UploadCanceledEvent,
	UiChatInput_UploadFailedEvent,
	UiChatInput_UploadStartedEvent,
	UiChatInput_UploadSuccessfulEvent,
	UiChatInput_UploadTooLargeEvent,
	UiChatInputCommandHandler,
	UiChatInputConfig,
	UiChatInputEventSource
} from "../generated/UiChatInputConfig";
import {FileUploader} from "./util/FileUploader";
import {ProgressBar} from "./micro-components/ProgressBar";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import * as log from "loglevel";
import {createUiNewChatMessageConfig} from "../generated/UiNewChatMessageConfig";
import {createUiChatNewFileConfig} from "../generated/UiChatNewFileConfig";

export class UiChatInput extends AbstractUiComponent<UiChatInputConfig> implements UiChatInputCommandHandler, UiChatInputEventSource {

	onFileItemClicked: TeamAppsEvent<UiChatInput_FileItemClickedEvent> = new TeamAppsEvent();
	onFileItemRemoved: TeamAppsEvent<UiChatInput_FileItemRemovedEvent> = new TeamAppsEvent();
	onMessageSent: TeamAppsEvent<UiChatInput_MessageSentEvent> = new TeamAppsEvent();
	onUploadCanceled: TeamAppsEvent<UiChatInput_UploadCanceledEvent> = new TeamAppsEvent();
	onUploadFailed: TeamAppsEvent<UiChatInput_UploadFailedEvent> = new TeamAppsEvent();
	onUploadStarted: TeamAppsEvent<UiChatInput_UploadStartedEvent> = new TeamAppsEvent();
	onUploadSuccessful: TeamAppsEvent<UiChatInput_UploadSuccessfulEvent> = new TeamAppsEvent();
	onUploadTooLarge: TeamAppsEvent<UiChatInput_UploadTooLargeEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $uploadItems: HTMLElement;
	private uploadItems: FileUploadItem[] = [];
	private $textInput: HTMLInputElement;
	private $sendButton: HTMLElement;
	private $attachmentButton: Element;

	constructor(config: UiChatInputConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiChatInput drop-zone">
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
			if (!this._config.attachmentsEnabled) {
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

	private send() {
		if (!this.sendable()) {
			return;
		}

		this.onMessageSent.fire({
			message: createUiNewChatMessageConfig({
				text: this.$textInput.value,
				uploadedFiles: this.uploadItems
					.filter(item => item.state === UploadState.SUCCESS)
					.map(item => createUiChatNewFileConfig({
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
		if (!this._config.attachmentsEnabled) {
			return;
		}
		for (let i = 0; i < files.length; i++) {
			const file = files[i];
			const uploadItem = new FileUploadItem(file, this._config.defaultFileIcon, this._config.uploadUrl, this._context);
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
		this._config.attachmentsEnabled = enabled;
		this.$attachmentButton.classList.toggle("hidden", !enabled);
	}
}

enum UploadState {
	IN_PROGRESS, SUCCESS, ERROR
}

class FileUploadItem {
	private static LOGGER: log.Logger = log.getLogger("UploadItem");

	public readonly onComplete: TeamAppsEvent<void> = new TeamAppsEvent();

	private $main: HTMLElement;
	public state: UploadState = UploadState.IN_PROGRESS;
	public uploadedFileUuid: string;

	constructor(public file: File, defaultFileIcon: string, uploadUrl: string, context: TeamAppsUiContext) {
		this.$main = parseHtml(`<div class="upload-item">
	<div class="icon img img-24" style="background-image: url('${defaultFileIcon}')"></div>
	<div class="name">${file.name}</div>
</div>`);
		const $icon = this.$main.querySelector<HTMLElement>(":scope .icon");
		createImageThumbnailUrl(file)
			.then(url => $icon.style.backgroundImage = `url('${url}')`)
			.catch(reason => FileUploadItem.LOGGER.debug(`Could not create thumbnail for file ${file}. Reason: ${reason}.`));
		const progressBar = new ProgressBar(0, {});

		this.$main.appendChild(progressBar.getMainDomElement());

		const uploader = new FileUploader();
		uploader.onProgress.addListener(progress => progressBar.setProgress(progress));
		uploader.onError.addListener(() => {
			this.state = UploadState.ERROR;
			this.$main.classList.add("error");
			this.onComplete.fire(null);
		});
		uploader.onSuccess.addListener(uuid => {
			this.state = UploadState.SUCCESS;
			this.uploadedFileUuid = uuid;
			this.onComplete.fire(null);
			fadeOut(progressBar.getMainDomElement());
		});
		uploader.upload(file, uploadUrl)
	}

	public getMainDomElement() {
		return this.$main;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiChatInput", UiChatInput);

