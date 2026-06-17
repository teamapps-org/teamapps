/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
import {Autolinker} from "autolinker";
import {
	UiChatDisplay_MessageViewedEvent,
	UiChatDisplay_PhotoClickedEvent,
	UiChatDisplayCommandHandler,
	UiChatDisplayConfig,
	UiChatDisplayEventSource
} from "../generated/UiChatDisplayConfig";
import {UiChatMessageBatchConfig} from "../generated/UiChatMessageBatchConfig";
import {UiChatMessageConfig} from "../generated/UiChatMessageConfig";
import {UiChatMessageContentConfig} from "../generated/UiChatMessageContentConfig";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {addDelegatedEventListener, humanReadableFileSize, parseHtml, prependChild, removeDangerousTags} from "./Common";
import {ContextMenu} from "./micro-components/ContextMenu";
import {UiSpinner} from "./micro-components/UiSpinner";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiComponent} from "./UiComponent";
import {debouncedMethod, DebounceMode} from "./util/debounce";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";
import {TeamAppsEvent} from "./util/TeamAppsEvent";

type MessageScrollAlignment = "top" | "bottom" | "nearest";

export class UiChatDisplay extends AbstractUiComponent<UiChatDisplayConfig> implements UiChatDisplayCommandHandler, UiChatDisplayEventSource {

	public readonly onMessageViewed: TeamAppsEvent<UiChatDisplay_MessageViewedEvent> = new TeamAppsEvent();
	public readonly onPhotoClicked: TeamAppsEvent<UiChatDisplay_PhotoClickedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private gotFirstMessage: boolean = false;
	private requestingPreviousMessages: boolean;
	private uiChatMessages: UiChatMessage[] = [];
	private $loadingIndicatorWrapper: HTMLElement;
	private $messages: HTMLElement;
	private contextMenu: ContextMenu;
	private lastMessageViewedEventMessageId = Number.NEGATIVE_INFINITY;
	private viewedEventsEnabled = false;

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
			this.fireMessageViewedForNewestVisibleMessage();
		});

		if (config.initialMessages) {
			this.appendMessages(config.initialMessages);
			this.applyInitialScrollPosition();
		} else {
			this.requestPreviousMessages();
		}

		this.contextMenu = new ContextMenu();
		addDelegatedEventListener(this.$messages, ".message", "contextmenu", (element, ev) => {
			if (this._config.contextMenuEnabled) {
				const chatMessageId = Number(element.getAttribute("data-id"));
				this.contextMenu.open(ev, async requestId => {
					const contextMenuComponent = await config.requestContextMenu({chatMessageId}) as UiComponent;
					if (contextMenuComponent != null) {
						this.contextMenu.setContent(contextMenuComponent, requestId);
					} else {
						this.contextMenu.close(requestId);
					}
				});
			}
		});

		setTimeout(() => this.deFactoVisibilityChanged.addListener(visible => {
			if (visible) { // to fix scroll position when this component gets re-attached
				this.applyScrollPosition(this.lastMessageViewedEventMessageId, "bottom");
			}
		}), 500); // delay to ensure the component is initially attached first
	}

	@debouncedMethod(500, DebounceMode.LATER)
	private requestPreviousMessages() {
		if (!this.requestingPreviousMessages) {
			this.requestingPreviousMessages = true;
			this._config.requestPreviousMessages({}).then(batch => {
				const heightBefore = this.$messages.offsetHeight;
				batch.messages.reverse().forEach(messageConfig => {
					const uiChatMessage = new UiChatMessage(messageConfig, this.handlePhotoClicked);
					prependChild(this.$messages, uiChatMessage.getMainDomElement());
					this.uiChatMessages.splice(0, 0, uiChatMessage);
				});
				const heightAfter = this.$messages.offsetHeight;
				this.setScrollTop(heightAfter - heightBefore, "auto");

				if (batch.containsFirstMessage) {
					this.gotFirstMessage = true;
				}
				this.requestingPreviousMessages = false;
				this.$loadingIndicatorWrapper.classList.add('hidden');
			});
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public addMessages(batch: UiChatMessageBatchConfig): void {
		this.appendMessages(batch);
		this.scrollToBottom("smooth", true);
	}

	private appendMessages(batch: UiChatMessageBatchConfig): void {
		batch.messages.forEach(messageConfig => {
			const chatMessage = new UiChatMessage(messageConfig, this.handlePhotoClicked);
			this.$messages.appendChild(chatMessage.getMainDomElement());
			this.uiChatMessages.push(chatMessage);
		});
		if (batch.containsFirstMessage) {
			this.gotFirstMessage = true;
		}
	}

	public updateMessage(message: UiChatMessageConfig) {
		this.uiChatMessages.find(m => m.id === message.id)
			?.update(message);
		this.doWhenAllImagesAreLoaded(() => this.fireMessageViewedForNewestVisibleMessage());
	}

	public deleteMessage(messageId: number) {
		const messageIndex = this.uiChatMessages.findIndex(m => m.id === messageId);
		if (messageIndex >= 0) {
			const uiChatMessage = this.uiChatMessages[messageIndex];
			uiChatMessage.getMainDomElement().remove();
			this.uiChatMessages.splice(messageIndex, 1);
			this.fireMessageViewedForNewestVisibleMessage();
		}
	}

	public clearMessages(batch: UiChatMessageBatchConfig): void {
		this.uiChatMessages = [];
		this.$messages.innerHTML = '';
		this.gotFirstMessage = false;
		this.requestingPreviousMessages = false;
		this.viewedEventsEnabled = false;
		this.appendMessages(batch);
		this.applyInitialScrollPosition();
	}

	public scrollToMessage(messageId: number): void {
		this.doWhenAllImagesAreLoaded(() => this.scrollToMessageById(messageId, "nearest", "smooth", true));
	}

	private scrollToMessageById(messageId: number, alignment: MessageScrollAlignment, behavior: ScrollBehavior, fireViewed: boolean): boolean {
		const message = this.uiChatMessages.find(m => m.id === messageId);
		if (message == null) {
			return false;
		}
		this.setScrollTop(this.getScrollTopForMessage(message.getMainDomElement(), alignment), behavior);
		if (fireViewed) {
			this.fireMessageViewedAfterScroll(behavior);
		}
		return true;
	}

	private getScrollTopForMessage($message: HTMLElement, alignment: MessageScrollAlignment): number {
		const messageTop = $message.offsetTop;
		const messageBottom = messageTop + $message.offsetHeight;
		if (alignment === "top" || $message.offsetHeight > this.$main.clientHeight) {
			return messageTop;
		} else if (alignment === "bottom") {
			return messageBottom - this.$main.clientHeight;
		}

		const viewportTop = this.$main.scrollTop;
		const viewportBottom = viewportTop + this.$main.clientHeight;
		if (messageTop < viewportTop) {
			return messageTop;
		} else if (messageBottom > viewportBottom) {
			return messageBottom - this.$main.clientHeight;
		}
		return viewportTop;
	}

	@executeWhenFirstDisplayed(true)
	private applyInitialScrollPosition() {
		this.applyScrollPosition(this._config.initialTopMessageId);
	}

	private applyScrollPosition(topMessageId?: number, alignment: MessageScrollAlignment = "top") {
		this.doWhenAllImagesAreLoaded(() => {
			let initialMessageFound = false;
			if (topMessageId != null) {
				initialMessageFound = this.scrollToMessageById(topMessageId, alignment, "auto", false);
			}
			if (!initialMessageFound) {
				this.scrollToBottom("auto", false);
			}
			requestAnimationFrame(() => {
				this.viewedEventsEnabled = true;
				this.fireMessageViewedForNewestVisibleMessage();
			});
		});
	}

	private scrollToBottom(behavior: ScrollBehavior, fireViewed: boolean) {
		this.doWhenAllImagesAreLoaded(() => {
			this.setScrollTop(this.$main.scrollHeight - this.$main.clientHeight, behavior);
			if (fireViewed) {
				this.fireMessageViewedAfterScroll(behavior);
			}
		});
	}

	private setScrollTop(scrollTop: number, behavior: ScrollBehavior) {
		const top = Math.max(0, scrollTop);
		if (behavior === "auto") {
			this.$main.scrollTop = top;
		} else {
			this.$main.scroll({top, behavior});
		}
	}

	private fireMessageViewedAfterScroll(behavior: ScrollBehavior) {
		if (behavior === "smooth") {
			setTimeout(() => this.fireMessageViewedForNewestVisibleMessage(), 300);
		} else {
			requestAnimationFrame(() => this.fireMessageViewedForNewestVisibleMessage());
		}
	}

	private doWhenAllImagesAreLoaded(action: () => void) {
		const allImages = Array.from(this.$messages.querySelectorAll(":scope img"));
		const incompleteImages: HTMLImageElement[] = allImages
			.map(img => img as HTMLImageElement)
			.filter(img => !img.complete);
		if (incompleteImages.length > 0) {
			Promise.all(incompleteImages.map(img => new Promise((resolve, reject) => {
				img.addEventListener("load", resolve, {once: true});
				img.addEventListener("error", resolve, {once: true}); // since we are only interested in when loading activity is done
			}))).then(action);
		} else {
			action();
		}
	}

	private fireMessageViewedForNewestVisibleMessage() {
		if (!this.viewedEventsEnabled || this.$main.clientHeight <= 0) {
			return;
		}
		let newestVisibleMessageId = this.lastMessageViewedEventMessageId;
		for (const message of this.uiChatMessages) {
			if (newestVisibleMessageId < message.id && this.isMessageInViewport(message.getMainDomElement())) {
				newestVisibleMessageId = message.id;
			}
		}
		if (newestVisibleMessageId > this.lastMessageViewedEventMessageId) {
			this.lastMessageViewedEventMessageId = newestVisibleMessageId;
			this.onMessageViewed.fire({messageId: newestVisibleMessageId});
		}
	}

	private isMessageInViewport($message: HTMLElement) {
		const viewportRect = this.$main.getBoundingClientRect();
		const messageRect = $message.getBoundingClientRect();
		return messageRect.bottom > viewportRect.top && messageRect.top < viewportRect.bottom;
	}

	private readonly handlePhotoClicked = (messageId: number, photoIndex: number) => {
		this.onPhotoClicked.fire({messageId, photoIndex});
	}

	public onResize() {
		this.doWhenAllImagesAreLoaded(() => this.fireMessageViewedForNewestVisibleMessage());
	}

	public closeContextMenu(): void {
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
	private $content: HTMLElement;
	private $photos: HTMLElement;
	private $files: HTMLElement;
	private config: UiChatMessageConfig;
	private readonly onPhotoClicked: (messageId: number, photoIndex: number) => void;

	constructor(config: UiChatMessageConfig, onPhotoClicked: (messageId: number, photoIndex: number) => void) {
		this.onPhotoClicked = onPhotoClicked;
		this.$main = parseHtml(`<div class="message UiChatMessage" data-id="${config.id}"></div>`);
		this.update(config);
	}

	public update(config: UiChatMessageConfig) {
		this.config = config;
		this.$main.classList.toggle("deleted", config.deleted);
		this.$main.innerHTML = "";
		this.$main.appendChild(parseHtml(`<img class="user-image" src="${this.config.userImageUrl}"></img>`));
		this.$main.appendChild(parseHtml(`<div class="user-nickname">${this.config.userNickname}</div>`));
		this.$main.appendChild(parseHtml(`<div class="content"></div>`));
		this.$main.appendChild(parseHtml(`<div class="photos"></div>`));
		this.$main.appendChild(parseHtml(`<div class="files"></div>`));
		this.$main.appendChild(parseHtml(`<div class="deleted-icon"></div>`));

		this.$content = this.$main.querySelector(":scope .content");
		this.$photos = this.$main.querySelector(":scope .photos");
		this.$files = this.$main.querySelector(":scope .files");

		this.getContent().forEach(content => this.renderContent(content));
		if (this.config.photos != null) {
			this.config.photos.forEach((photo, photoIndex) => {
				const $photo = document.createElement("img");
				$photo.classList.add("photo");
				$photo.src = photo.thumbnailUrl ?? photo.imageUrl;
				$photo.title = photo.fileName ?? "";
				$photo.addEventListener("click", ev => {
					ev.preventDefault();
					this.onPhotoClicked(this.config.id, photoIndex);
				});
				this.$photos.appendChild($photo);
			});
		}
		if (this.config.files != null) {
			this.config.files.forEach(file => {
				this.$files.appendChild(parseHtml(`<a class="file" target="_blank" href="${file.downloadUrl}">
					<div class="file-icon img img-32" style="background-image: url('${file.thumbnailUrl || file.icon}')"> </div>
					<div class="file-name">${file.name}</div>
					<div class="file-size">${humanReadableFileSize(file.length)}</div>
				</a>`));
			});
		}
	}

	private getContent(): UiChatMessageContentConfig[] {
		if (this.config.content != null && this.config.content.length > 0) {
			return this.config.content;
		}
		return this.config.text != null ? [{text: this.config.text}] : [];
	}

	private renderContent(content: UiChatMessageContentConfig) {
		if (content.component != null) {
			const $componentWrapper = parseHtml(`<div class="content-component"></div>`);
			$componentWrapper.appendChild((content.component as UiComponent).getMainElement());
			this.$content.appendChild($componentWrapper);
		}
		if (content.text != null) {
			this.$content.appendChild(this.createTextElement(content.text));
		}
	}

	private createTextElement(text: string) {
		text = removeDangerousTags(text);
		text = UiChatMessage.AUTOLINKER.link(text);
		return parseHtml(`<div class="content-text">${text}</div>`);
	}

	public get id() {
		return this.config.id;
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}
}
