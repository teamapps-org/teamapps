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
	AbstractComponent,
	addDelegatedEventListener,
	type Component,
	debouncedMethod,
	DebounceModes,
	executeAfterAttached,
	parseHtml,
	prependChild,
} from "projector-client-object-api";
import {
	type DtoChatDisplay,
	type DtoChatDisplayCommandHandler,
	type DtoChatDisplayServerObjectChannel,
	type DtoChatMessage,
	type DtoChatMessageBatch
} from "./generated";
import {ContextMenu} from "projector-client-core-components";
import {ChatMessage} from "./ChatMessage";

export class ChatDisplay extends AbstractComponent<DtoChatDisplay> implements DtoChatDisplayCommandHandler {

	private $main: HTMLElement;
	private gotFirstMessage: boolean = false;
	private requestingPreviousMessages: boolean = false;
	private uiChatMessages: ChatMessage[] = [];
	private $loadingIndicatorWrapper: HTMLElement;
	private $messages: HTMLElement;
	private contextMenu: ContextMenu;

	private serverObjectChannel: DtoChatDisplayServerObjectChannel;

	constructor(config: DtoChatDisplay, serverObjectChannel: DtoChatDisplayServerObjectChannel) {
		super(config);
		this.serverObjectChannel = serverObjectChannel;
		this.$main = parseHtml(`<div class="ChatDisplay">
	<div class="loading-indicator-wrapper hidden">
		<div class="teamapps-spinner"></div>
	</div>
	<div class="messages"></div>
</div>`);
		this.setDeletedMessageIcon(config.deletedMessageIcon);
		this.$loadingIndicatorWrapper = this.$main.querySelector(":scope .loading-indicator-wrapper")!;
		this.$messages = this.$main.querySelector(":scope .messages")!;
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

	@debouncedMethod(500, DebounceModes.LATER)
	private requestPreviousMessages() {
		if (!this.requestingPreviousMessages) {
			this.requestingPreviousMessages = true;
			this.serverObjectChannel.sendQuery("requestPreviousMessages").then(batch => {
				const heightBefore = this.$messages.offsetHeight;
				batch.messages.reverse().forEach(messageConfig => {
					const uiChatMessage = new ChatMessage(messageConfig);
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
			const chatMessage = new ChatMessage(messageConfig);
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

	@executeAfterAttached(true)
	private scrollToBottom() {
		const scrollToBottom = ()=>  {
			this.$main.scroll({
				top: 100000000,
				behavior: 'smooth'
			});
		}
		this.doWhenAllImagesAreLoaded(scrollToBottom);
	}

	private doWhenAllImagesAreLoaded(action: () => void) {
		let allImages = Array.from(this.$messages.querySelectorAll(":scope img"));
		let incompleteImages: HTMLImageElement[] = allImages
			.map(img => img as HTMLImageElement)
			.filter(img => !img.complete);
		if (incompleteImages.length > 0) {
			Promise.all(incompleteImages.map(img => new Promise((resolve) => {
				img.addEventListener("load", resolve, {once:true});
				img.addEventListener("error", resolve, {once:true}); // since we are only interested in when loading activity is done
			}))).then(action)
		} else {
			action();
		}
	}

	onResize() {
		this.scrollToBottom(); // hack. actually, I want to scrollToBottom only when this component gets re-attached...
	}

	closeContextMenu(): void {
		this.contextMenu.close();
	}
}
