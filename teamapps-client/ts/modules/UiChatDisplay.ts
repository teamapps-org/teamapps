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
import ResizeObserver from "resize-observer-polyfill";
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
interface IScrollAnchor { message: UiChatMessage; offsetTop: number; }

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
	private stickToBottom = false;
	private scrollAnchor: IScrollAnchor | null = null;
	private readonly messageResizeObserver = new ResizeObserver(entries => this.handleMessagesResized(entries));

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
			this.rememberScrollAnchor();
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
					const uiChatMessage = new UiChatMessage(messageConfig, this);
					prependChild(this.$messages, uiChatMessage.getMainDomElement());
					this.uiChatMessages.splice(0, 0, uiChatMessage);
					this.observeMessage(uiChatMessage);
				});
				const heightAfter = this.$messages.offsetHeight;
				this.scrollToTopPositionNow(heightAfter - heightBefore, "auto");

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
			const chatMessage = new UiChatMessage(messageConfig, this);
			this.$messages.appendChild(chatMessage.getMainDomElement());
			this.uiChatMessages.push(chatMessage);
			this.observeMessage(chatMessage);
		});
		if (batch.containsFirstMessage) {
			this.gotFirstMessage = true;
		}
	}

	public updateMessage(message: UiChatMessageConfig) {
		const uiChatMessage = this.uiChatMessages.find(m => m.id === message.id);
		if (uiChatMessage != null) {
			this.unobserveMessage(uiChatMessage);
			uiChatMessage.update(message);
			this.observeMessage(uiChatMessage);
		}
		requestAnimationFrame(() => this.fireMessageViewedForNewestVisibleMessage());
	}

	public deleteMessage(messageId: number) {
		const messageIndex = this.uiChatMessages.findIndex(m => m.id === messageId);
		if (messageIndex >= 0) {
			const uiChatMessage = this.uiChatMessages[messageIndex];
			this.unobserveMessage(uiChatMessage);
			uiChatMessage.getMainDomElement().remove();
			this.uiChatMessages.splice(messageIndex, 1);
			this.rememberScrollAnchor();
			this.fireMessageViewedForNewestVisibleMessage();
		}
	}

	public clearMessages(batch: UiChatMessageBatchConfig): void {
		this.messageResizeObserver.disconnect();
		this.scrollAnchor = null;
		this.uiChatMessages = [];
		this.$messages.innerHTML = '';
		this.gotFirstMessage = false;
		this.requestingPreviousMessages = false;
		this.viewedEventsEnabled = false;
		this.appendMessages(batch);
		this.applyInitialScrollPosition();
	}

	public scrollToMessage(messageId: number): void {
		requestAnimationFrame(() => this.scrollToMessageNow(messageId, "nearest", "smooth", true));
	}

	private scrollToMessageNow(messageId: number, alignment: MessageScrollAlignment, behavior: ScrollBehavior, fireViewed: boolean): boolean {
		const message = this.uiChatMessages.find(m => m.id === messageId);
		if (message == null) {
			return false;
		}
		this.scrollToTopPositionNow(this.getScrollTopPositionForMessage(message.getMainDomElement(), alignment), behavior);
		if (fireViewed) {
			this.fireMessageViewedAfterScroll(behavior);
		}
		return true;
	}

	private getScrollTopPositionForMessage($message: HTMLElement, alignment: MessageScrollAlignment): number {
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
		requestAnimationFrame(() => {
			let initialMessageFound = false;
			if (topMessageId != null) {
				initialMessageFound = this.scrollToMessageNow(topMessageId, alignment, "auto", false);
			}
			if (!initialMessageFound) {
				this.scrollToBottomNow("auto");
			}
			requestAnimationFrame(() => {
				this.viewedEventsEnabled = true;
				this.fireMessageViewedForNewestVisibleMessage();
			});
		});
	}

	private scrollToBottom(behavior: ScrollBehavior, fireViewed: boolean) {
		requestAnimationFrame(() => {
			this.scrollToBottomNow(behavior);
			if (behavior === "smooth") {
				setTimeout(() => this.scrollToBottomNow("auto"), 350); // needed if layout shifts while scrolling
			}
			if (fireViewed) {
				this.fireMessageViewedAfterScroll(behavior);
			}
		});
	}

	private scrollToBottomNow(behavior: ScrollBehavior) {
		this.scrollToTopPositionNow(this.$main.scrollHeight - this.$main.clientHeight, behavior);
		this.stickToBottom = true;
	}

	private scrollToTopPositionNow(scrollTopPosition: number, behavior: ScrollBehavior) {
		const top = Math.max(0, scrollTopPosition);
		if (behavior === "auto") {
			this.$main.scrollTop = top;
			this.rememberScrollAnchor();
		} else {
			this.stickToBottom = false;
			this.$main.scroll({top, behavior});
		}
	}

	private isScrolledToBottom(): boolean {
		return this.$main.scrollTop + this.$main.clientHeight + 5 >= this.$main.scrollHeight;
	}

	private observeMessage(message: UiChatMessage): void {
		this.messageResizeObserver.observe(message.getMainDomElement());
	}

	private unobserveMessage(message: UiChatMessage): void {
		this.messageResizeObserver.unobserve(message.getMainDomElement());
	}

	private handleMessagesResized(entries: ResizeObserverEntry[]): void {
		if (this.stickToBottom) {
			this.scrollToBottomNow("auto");
			return;
		}

		const anchor = this.scrollAnchor;
		const anchorId = anchor != null ? anchor.message.id : Number.NEGATIVE_INFINITY;
		if (anchorId >= 0 && entries.some(entry => {
			const messageId = Number((entry.target as HTMLElement).getAttribute("data-id") ?? Number.NEGATIVE_INFINITY);
			return messageId >= 0 && messageId < anchorId;
		})) {
			this.restoreScrollAnchor(anchor);
		}
	}

	private rememberScrollAnchor(): void {
		this.stickToBottom = this.isScrolledToBottom();
		const viewportRect = this.$main.getBoundingClientRect();
		const message = this.uiChatMessages.find(msg => msg.getMainDomElement().getBoundingClientRect().bottom > viewportRect.top);
		this.scrollAnchor = message != null ? {
			message,
			offsetTop: message.getMainDomElement().getBoundingClientRect().top - viewportRect.top,
		} : null;
	}

	private restoreScrollAnchor(anchor: IScrollAnchor): void {
		if (anchor == null || this.uiChatMessages.indexOf(anchor.message) < 0) {
			this.rememberScrollAnchor();
			return;
		}
		const viewportTop = this.$main.getBoundingClientRect().top;
		const anchorTop = anchor.message.getMainDomElement().getBoundingClientRect().top;
		this.$main.scrollTop += anchorTop - viewportTop - anchor.offsetTop;
		this.rememberScrollAnchor();
	}

	private fireMessageViewedAfterScroll(behavior: ScrollBehavior) {
		if (behavior === "smooth") {
			setTimeout(() => this.fireMessageViewedForNewestVisibleMessage(), 300);
		} else {
			requestAnimationFrame(() => this.fireMessageViewedForNewestVisibleMessage());
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

	public onResize() {
		requestAnimationFrame(() => this.fireMessageViewedForNewestVisibleMessage());
	}

	public closeContextMenu(): void {
		this.contextMenu.close();
	}

	public destroy(): void {
		this.messageResizeObserver.disconnect();
		super.destroy();
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
	private readonly parent: UiChatDisplay;
	private readonly $main: HTMLElement;
	private $body: HTMLElement;
	private $content: HTMLElement;
	private $photos: HTMLElement;
	private $files: HTMLElement;
	private $footer: HTMLElement;
	private config: UiChatMessageConfig;

	constructor(config: UiChatMessageConfig, parent: UiChatDisplay) {
		this.parent = parent;
		this.$main = parseHtml(`<div class="message UiChatMessage" data-id="${config.id}"></div>`);
		this.update(config);
	}

	public update(config: UiChatMessageConfig) {
		this.config = config;
		this.$main.classList.toggle("deleted", config.deleted);
		this.$main.classList.toggle("decorated", this.hasFrameDecoration());
		this.$main.innerHTML = "";
		this.$main.appendChild(parseHtml(`<img class="user-image" src="${this.config.userImageUrl}"></img>`));
		this.$main.appendChild(parseHtml(`<div class="user-nickname">${this.config.userNickname}</div>`));
		this.$main.appendChild(parseHtml(`<div class="message-body">
			<div class="content"></div>
			<div class="photos"></div>
			<div class="files"></div>
		</div>`));
		this.$main.appendChild(parseHtml(`<div class="message-footer"></div>`));
		this.$main.appendChild(parseHtml(`<div class="deleted-icon"></div>`));

		this.$body = this.$main.querySelector(":scope .message-body");
		this.$content = this.$main.querySelector(":scope .content");
		this.$photos = this.$main.querySelector(":scope .photos");
		this.$files = this.$main.querySelector(":scope .files");
		this.$footer = this.$main.querySelector(":scope .message-footer");
		if (this.config.backgroundColor != null) {
			this.$body.style.backgroundColor = this.config.backgroundColor;
		}
		if (this.config.borderColor != null) {
			this.$body.style.border = `1px solid ${this.config.borderColor}`;
		}
		if (this.config.textColor != null) {
			this.$body.style.color = this.config.textColor;
		}

		this.getContent().forEach(content => this.renderContent(content));
		if (this.config.photos != null) {
			this.config.photos.forEach((photo, photoIndex) => {
				const $photo = document.createElement("img");
				$photo.classList.add("photo");
				$photo.setAttribute("loading", "lazy");
				$photo.setAttribute("decoding", "async");
				$photo.title = photo.fileName ?? "";
				$photo.alt = photo.fileName ?? "";

				const $tile = document.createElement("button");
				$tile.type = "button";
				$tile.classList.add("photo-tile");

				$tile.addEventListener("click", ev => {
					ev.preventDefault();
					this.parent.onPhotoClicked.fire({messageId: this.config.id, photoIndex});
				});

				$tile.appendChild($photo);
				this.$photos.appendChild($tile);
				$photo.src = photo.thumbnailUrl ?? photo.imageUrl;
			});
		}
		if (this.config.files != null) {
			this.config.files.forEach(file => {
				const $file = parseHtml(`<button class="file" type="button">
					<div class="file-icon img img-32"></div>
					<div class="file-name"></div>
					<div class="file-size"></div>
				</button>`);
				$file.addEventListener("click", () => {
					if (file.downloadUrl != null) {
						this.downloadFile(file.downloadUrl, file.name);
					}
				});
				const $fileName = $file.querySelector(":scope .file-name");
				$fileName.textContent = file.name ?? "";
				const $fileSize = $file.querySelector(":scope .file-size");
				$fileSize.textContent = humanReadableFileSize(file.length ?? 0);
				const $fileIcon = $file.querySelector(":scope .file-icon") as HTMLElement;
				const fileIconUrl = file.thumbnailUrl || file.icon;
				if (fileIconUrl != null) {
					$fileIcon.style.backgroundImage = `url('${fileIconUrl}')`;
				}
				this.$files.appendChild($file);
			});
		}
		if (this.config.footer != null && this.config.footer.length > 0) {
			this.$footer.appendChild(this.createFooterElement(this.config.footer));
		}
	}

	private getContent(): UiChatMessageContentConfig[] {
		if (this.config.content != null && this.config.content.length > 0) {
			return this.config.content;
		}
		return this.config.text != null ? [{text: this.config.text}] : [];
	}

	private hasFrameDecoration(): boolean {
		return this.config.backgroundColor != null || this.config.borderColor != null;
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

	private createFooterElement(text: string) {
		text = removeDangerousTags(text);
		text = UiChatMessage.AUTOLINKER.link(text);
		return parseHtml(`<div class="footer-text">${text}</div>`);
	}

	private downloadFile(url: string, fileName?: string): void {
		const $link = document.createElement("a");
		$link.href = url;
		$link.download = fileName ?? "";
		$link.style.display = "none";
		document.body.appendChild($link);
		$link.click();
		$link.remove();
	}

	public get id() {
		return this.config.id;
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}
}
