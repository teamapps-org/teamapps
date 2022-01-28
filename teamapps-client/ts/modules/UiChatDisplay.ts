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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {addDelegatedEventListener, humanReadableFileSize, parseHtml, prependChild, removeDangerousTags} from "./Common";
import {UiChatMessageConfig} from "../generated/UiChatMessageConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiChatDisplayCommandHandler, UiChatDisplayConfig,} from "../generated/UiChatDisplayConfig";
import {UiSpinner} from "./micro-components/UiSpinner";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {Autolinker} from "autolinker";
import {ContextMenu} from "./micro-components/ContextMenu";
import {UiComponent} from "./UiComponent";
import {UiChatMessageBatchConfig} from "../generated/UiChatMessageBatchConfig";
import {debounce, debouncedMethod, DebounceMode} from "./util/debounce";

export class UiChatDisplay extends AbstractUiComponent<UiChatDisplayConfig> implements UiChatDisplayCommandHandler {

	private $main: HTMLElement;
	private gotFirstMessage: boolean = false;
	private requestingPreviousMessages: boolean;
	private uiChatMessages: UiChatMessage[] = [];
	private $loadingIndicatorWrapper: HTMLElement;
	private $messages: HTMLElement;
	private contextMenu: ContextMenu;

	constructor(config: UiChatDisplayConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiChatDisplay">
	<style> [data-teamapps-id=${config.id}] .message.deleted .deleted-icon {
		background-image: url('${config.deletedMessageIcon}');				
	}</style>
	<div class="loading-indicator-wrapper hidden"></div>
	<div class="messages"></div>
</div>`);
		this.$loadingIndicatorWrapper = this.$main.querySelector(":scope .loading-indicator-wrapper");
		this.$loadingIndicatorWrapper.appendChild(new UiSpinner({fixedSize: 20}).getMainDomElement());
		this.$messages = this.$main.querySelector(":scope .messages");
		this.$main.addEventListener("scroll", () => {
			if (this.$main.scrollTop == 0 && !this.gotFirstMessage) {
				this.$loadingIndicatorWrapper.classList.remove('hidden');
				this.requestPreviousMessages();
			}
		});

		if (config.initialMessages) {
			this.addMessages(config.initialMessages);
		} else {
			this.requestPreviousMessages();
		}

		this.contextMenu = new ContextMenu();
		addDelegatedEventListener(this.$messages, ".message", "contextmenu", (element, ev) => {
			if (this._config.contextMenuEnabled) {
				let chatMessageId = Number(element.getAttribute("data-id"));
				this.contextMenu.open(ev, async requestId => {
					let contentComponent = await config.requestContextMenu({chatMessageId}) as UiComponent;
					this.contextMenu.setContent(contentComponent, requestId)
				});
			}
		})
	}

	@debouncedMethod(500, DebounceMode.LATER)
	private requestPreviousMessages() {
		if (!this.requestingPreviousMessages) {
			this.requestingPreviousMessages = true;
			this._config.requestPreviousMessages({}).then(batch => {
				const heightBefore = this.$messages.offsetHeight;
				batch.messages.reverse().forEach(messageConfig => {
					const uiChatMessage = new UiChatMessage(messageConfig);
					prependChild(this.$messages, uiChatMessage.getMainDomElement());
					this.uiChatMessages.splice(0, 0, uiChatMessage);
				});
				const heightAfter = this.$messages.offsetHeight;
				this.$main.scroll({top: heightAfter - heightBefore});

				if (batch.containsFirstMessage) {
					this.gotFirstMessage = true;
				}
				this.requestingPreviousMessages = false;
				this.$loadingIndicatorWrapper.classList.add('hidden');
			});
		}
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addMessages(batch: UiChatMessageBatchConfig): void {
		batch.messages.forEach(messageConfig => {
			const chatMessage = new UiChatMessage(messageConfig);
			this.$messages.appendChild(chatMessage.getMainDomElement());
			this.uiChatMessages.push(chatMessage);
		});
		if (batch.containsFirstMessage) {
			this.gotFirstMessage = true;
		}
		this.scrollToBottom();
	}

	updateMessage(message: UiChatMessageConfig) {
		this.uiChatMessages.find(m => m.id === message.id)
			?.update(message);
	}

	deleteMessage(messageId: number) {
		let messageIndex = this.uiChatMessages.findIndex(m => m.id === messageId);
		if (messageIndex >= 0) {
			let uiChatMessage = this.uiChatMessages[messageIndex];
			uiChatMessage.getMainDomElement().remove();
			this.uiChatMessages.splice(messageIndex, 1);
		}
	}

	clearMessages(batch: UiChatMessageBatchConfig): void {
		this.uiChatMessages = [];
		this.$messages.innerHTML = '';
		this.gotFirstMessage = false;
		this.requestingPreviousMessages = false;
		this.addMessages(batch);
	}

	@executeWhenFirstDisplayed(true)
	private scrollToBottom() {
		this.$main.scroll({
			top: 100000000,
			behavior: 'smooth'
		});
	}

	closeContextMenu(): void {
		this.contextMenu.close();
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiChatDisplay", UiChatDisplay);

class UiChatMessage {

	private static readonly AUTOLINKER = new Autolinker({
		urls: {
			schemeMatches: true,
			wwwMatches: true,
			tldMatches: true
		},
		email: true,
		phone: true,
		mention: false,
		hashtag: false,

		stripPrefix: false,
		stripTrailingSlash: false,
		newWindow: true,

		truncate: {
			length: 70,
			location: 'smart'
		},

		className: ''
	});

	private $main: HTMLElement;
	private $photos: HTMLElement;
	private $files: HTMLElement;
	private config: UiChatMessageConfig;

	constructor(config: UiChatMessageConfig) {
		this.$main = parseHtml(`<div class="message UiChatMessage" data-id="${config.id}"></div>`);
		this.update(config);
	}

	public update(config: UiChatMessageConfig) {
		this.config = config;
		this.$main.classList.toggle("deleted", config.deleted);
		this.$main.innerHTML = "";
		let text = removeDangerousTags(this.config.text);
		text = UiChatMessage.AUTOLINKER.link(text);
		this.$main.appendChild(parseHtml(`<img class="user-image" src="${this.config.userImageUrl}"></img>`))
		this.$main.appendChild(parseHtml(`<div class="user-nickname">${this.config.userNickname}</div>`))
		this.$main.appendChild(parseHtml(`<div class="text">${text}</div>`))
		this.$main.appendChild(parseHtml(`<div class="photos"></div>`))
		this.$main.appendChild(parseHtml(`<div class="files"></div>`))
		this.$main.appendChild(parseHtml(`<div class="deleted-icon"></div>`))

		this.$photos = this.$main.querySelector(":scope .photos");
		this.$files = this.$main.querySelector(":scope .files");

		if (this.config.photos != null) {
			this.config.photos.forEach(photo => {
				this.$photos.appendChild(parseHtml(`<img class="photo" src="${photo.imageUrl}">`))
			});
		}
		if (this.config.files != null) {
			this.config.files.forEach(file => {
				this.$files.appendChild(parseHtml(`<a class="file" target="_blank" href="${file.downloadUrl}">
					<div class="file-icon img img-32" style="background-image: url('${file.thumbnailUrl || file.icon}')"> </div>
					<div class="file-name">${file.name}</div>
					<div class="file-size">${humanReadableFileSize(file.length)}</div>
				</a>`))
			});
		}
	}

	public get id() {
		return this.config.id;
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}
}
