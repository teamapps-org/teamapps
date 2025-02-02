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
	AbstractLegacyComponent,
	addDelegatedEventListener,
	Component,
	debouncedMethod, DebounceMode, executeWhenFirstDisplayed, humanReadableFileSize,
	parseHtml, prependChild,
} from "projector-client-object-api";
import {
	DtoChatDisplay,
	DtoChatDisplayCommandHandler,
	DtoChatDisplayServerObjectChannel,
	DtoChatMessage,
	DtoChatMessageBatch
} from "./generated";
import {ContextMenu, removeDangerousTags} from "projector-client-core-components";
import {Autolinker} from "autolinker";

export class ChatDisplay extends AbstractLegacyComponent<DtoChatDisplay> implements DtoChatDisplayCommandHandler {

	private $main: HTMLElement;
	private gotFirstMessage: boolean = false;
	private requestingPreviousMessages: boolean;
	private uiChatMessages: UiChatMessage[] = [];
	private $loadingIndicatorWrapper: HTMLElement;
	private $messages: HTMLElement;
	private contextMenu: ContextMenu;

	constructor(config: DtoChatDisplay, private serverObjectChannel: DtoChatDisplayServerObjectChannel) {
		super(config);
		this.$main = parseHtml(`<div class="ChatDisplay">
	<div class="loading-indicator-wrapper hidden">
		<div class="teamapps-spinner"></div>
	</div>
	<div class="messages"></div>
</div>`);
		this.setDeletedMessageIcon(config.deletedMessageIcon);
		this.$loadingIndicatorWrapper = this.$main.querySelector(":scope .loading-indicator-wrapper");
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
			if (this.config.contextMenuEnabled) {
				let chatMessageId = Number(element.getAttribute("data-id"));
				this.contextMenu.open(ev, async requestId => {
					let contentComponent  = await serverObjectChannel.sendQuery("requestContextMenu", chatMessageId) as Component;
					if (contentComponent != null) {
						this.contextMenu.setContent(contentComponent, requestId);
					} else {
						this.contextMenu.close(requestId);
					}
				});
			}
		})
	}

	public setDeletedMessageIcon(deletedMessageIcon: string) {
		this.setStyle(".message.deleted .deleted-icon", {"background-image": `url('${deletedMessageIcon}')`})
	}

	setContextMenuEnabled(contextMenuEnabled: boolean) {
		this.config.contextMenuEnabled = contextMenuEnabled;
    }

	@debouncedMethod(500, DebounceMode.LATER)
	private requestPreviousMessages() {
		if (!this.requestingPreviousMessages) {
			this.requestingPreviousMessages = true;
			this.serverObjectChannel.sendQuery("requestPreviousMessages").then(batch => {
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

	addMessages(batch: DtoChatMessageBatch): void {
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

	updateMessage(message: DtoChatMessage) {
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

	clearMessages(batch: DtoChatMessageBatch): void {
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
	private config: DtoChatMessage;

	constructor(config: DtoChatMessage) {
		this.$main = parseHtml(`<div class="message UiChatMessage" data-id="${config.id}"></div>`);
		this.update(config);
	}

	public update(config: DtoChatMessage) {
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
