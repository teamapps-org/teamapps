/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import {parseHtml, prependChild, removeDangerousTags} from "./Common";
import {UiChatMessageConfig} from "../generated/UiChatMessageConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiChatDisplay_PreviousMessagesRequestedEvent, UiChatDisplayCommandHandler, UiChatDisplayConfig, UiChatDisplayEventSource} from "../generated/UiChatDisplayConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiSpinner} from "./micro-components/UiSpinner";
import {executeWhenFirstDisplayed} from "./util/ExecuteWhenFirstDisplayed";

export class UiChatDisplay extends AbstractUiComponent<UiChatDisplayConfig> implements UiChatDisplayCommandHandler, UiChatDisplayEventSource {

	public readonly onPreviousMessagesRequested: TeamAppsEvent<UiChatDisplay_PreviousMessagesRequestedEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private gotFirstMessage: boolean = false;
	private requestingPreviousMessages: boolean;
	private chatMessages: UiChatMessageConfig[] = [];
	private $loadingIndicatorWrapper: HTMLElement;
	private $messages: HTMLElement;

	constructor(config: UiChatDisplayConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiChatDisplay">
	<div class="loading-indicator-wrapper"></div>
	<div class="messages"></div>
</div>`);
		this.$loadingIndicatorWrapper = this.$main.querySelector(":scope .loading-indicator-wrapper");
		this.$loadingIndicatorWrapper.appendChild(new UiSpinner({fixedSize: 20}).getMainDomElement());
		this.$messages = this.$main.querySelector(":scope .messages");
		this.$main.addEventListener("scroll", () => {
			if (this.$main.scrollTop == 0 && !this.gotFirstMessage) {
				this.requestPreviousMessages();
			}
		});

		if (config.messages) {
			this.addChatMessages(config.messages, false, config.includesFirstMessage);
		} else if (!config.includesFirstMessage) {
			this.requestPreviousMessages();
		}
	}

	private requestPreviousMessages() {
		if (!this.requestingPreviousMessages) {
			this.requestingPreviousMessages = true;
			this.onPreviousMessagesRequested.fire({
				earliestKnownMessageId: this.getEarliestChatMessageId()
			});
			this.$loadingIndicatorWrapper.classList.remove('hidden');
		}
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addChatMessages(chatMessages: UiChatMessageConfig[], prepend: boolean, includesFirstMessage: boolean): void {
		if (prepend) {
			const heightBefore = this.$messages.offsetHeight;
			chatMessages.reverse().forEach(messageConfig => {
				const chatMessage = new UiChatMessage(messageConfig, this._context);
				prependChild(this.$messages, chatMessage.getMainDomElement());
				this.chatMessages.splice(0, 0, messageConfig);
			});
			const heightAfter = this.$messages.offsetHeight;
			this.$main.scroll({top: heightAfter - heightBefore});
		} else {
			chatMessages.forEach(messageConfig => {
				const chatMessage = new UiChatMessage(messageConfig, this._context);
				this.$messages.appendChild(chatMessage.getMainDomElement());
				this.chatMessages.push(messageConfig);
			});
			this.scrollToBottom();
		}
		if (includesFirstMessage) {
			this.gotFirstMessage = true;
		}
		this.requestingPreviousMessages = false;
		this.$loadingIndicatorWrapper.classList.add('hidden');
	}

	replaceChatMessages(chatMessages: UiChatMessageConfig[], includesFirstMessage: boolean): void {
		this.chatMessages = [];
		this.$messages.innerHTML = '';
		this.gotFirstMessage = false;
		this.requestingPreviousMessages = false;
		this.addChatMessages(chatMessages, true, includesFirstMessage);
	}

	@executeWhenFirstDisplayed(true)
	private scrollToBottom() {
		this.$main.scroll({
			top: 100000000,
			behavior: 'smooth'
		});
	}

	private getEarliestChatMessageId() {
		return this.chatMessages[0] && this.chatMessages[0].id;
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiChatDisplay", UiChatDisplay);

class UiChatMessage {
	private $main: HTMLElement;
	private $photos: HTMLElement;
	private $files: HTMLElement;

	constructor(private config: UiChatMessageConfig, private context: TeamAppsUiContext) {
		this.$main = parseHtml(`<div class="message">
	<img class="user-image" src="${config.userImageUrl}">
	<div class="user-nickname">${config.userNickname}</div>
	<div class="text">${removeDangerousTags(config.text)}</div>
	<div class="photos"></div>
	<div class="files"></div>
</div>`);
		this.$photos = this.$main.querySelector(":scope .photos");
		this.$files = this.$main.querySelector(":scope .files");

		if (config.photos != null) {
			config.photos.forEach(photo => {
				this.$photos.appendChild(parseHtml(`<img class="photo" src="${photo.imageUrl}">`))
			});
		}
		if (config.files != null) {
			config.files.forEach(file => {
				this.$files.appendChild(parseHtml(`<a class="file" target="_blank" href="${file.downloadUrl}">
	<div class="file-icon img img-32" style="background-image: url(${file.thumbnailUrl || file.icon})"> </div>
	<div class="file-name">${file.name}</div>
	<div class="file-size">23.4 kB</div>
</a>`))
			});
		}
	}

	public getMainDomElement(): HTMLElement {
		return this.$main;
	}
}
