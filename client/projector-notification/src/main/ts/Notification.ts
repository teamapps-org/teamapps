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
// @ts-ignore
import ICON_CLOSE from "@material-symbols/svg-400/outlined/close.svg";
import {
	DtoNotification,
	DtoNotification_ClosedEvent,
	DtoNotification_OpenedEvent,
	DtoNotificationCommandHandler,
	DtoNotificationEventSource
} from "./generated";
import {
	AbstractLegacyComponent,
	Component, EntranceAnimation, ExitAnimation,
	executeWhenFirstDisplayed,
	parseHtml,
	ServerObjectChannel,
	ProjectorEvent, animateCSS, NotificationPosition
} from "projector-client-object-api";
import {createUiSpacingValueCssString} from "projector-client-object-api";
import {ProgressBar} from "projector-progress-indicator";
import {NotificationHandle, showNotificationLike} from "projector-client-object-api";


export class Notification extends AbstractLegacyComponent<DtoNotification> implements DtoNotificationCommandHandler, DtoNotificationEventSource {

	public readonly onOpened: ProjectorEvent<DtoNotification_OpenedEvent> = new ProjectorEvent();
	public readonly onClosed: ProjectorEvent<DtoNotification_ClosedEvent> = new ProjectorEvent();

	private $main: HTMLElement;
	private $contentContainer: HTMLElement;
	private $progressBarContainer: HTMLElement;
	private progressBar: ProgressBar;

	private timeoutMillis: number;
	private notificationHandle: NotificationHandle;

	constructor(config: DtoNotification, serverObjectChannel: ServerObjectChannel) {
		super(config);

		this.$main = parseHtml(`<div class="Notification">
	<div class="close-button"><img class="hoverable-icon" src="${ICON_CLOSE}"></div>
	<div class="content-container"></div>
	<div class="progress-container"></div>
</div>`);
		this.$contentContainer = this.$main.querySelector(":scope > .content-container");
		this.$progressBarContainer = this.$main.querySelector(":scope > .progress-container");
		this.$main.querySelector(":scope > .close-button").addEventListener("mousedown", () => {
			this.notificationHandle?.close();
			this.onClosed.fire({byUser: true});
		});
		this.update(config);
	}

	public update(config: DtoNotification) {
		this.config = config;
		this.$main.style.backgroundColor = config.backgroundColor;
		// this.$main.style.borderColor = createUiColorCssString(config.borderColor, "#00000022");
		this.$contentContainer.style.padding = createUiSpacingValueCssString(config.padding);
		this.$main.classList.toggle("dismissible", config.dismissible);
		this.$main.classList.toggle("show-progress", config.progressBarVisible && this.timeoutMillis > 0);

		if (config.progressBarVisible && this.progressBar == null) {
			this.progressBar = new ProgressBar(0, {height: 5});
			this.$progressBarContainer.appendChild(this.progressBar.getMainDomElement());
		} else if (!config.progressBarVisible && this.progressBar != null) {
			this.progressBar.getMainDomElement().remove();
			this.progressBar = null;
		}

		if (this.$contentContainer.firstChild !== (config.content && (config.content as Component).getMainElement())) {
			this.$contentContainer.innerHTML = '';
			if (config.content != null) {
				this.$contentContainer.appendChild((config.content as Component).getMainElement());
			}
		}
	}

	public show(position: NotificationPosition, entranceAnimation: EntranceAnimation, exitAnimation: ExitAnimation, timeout: number) {
		this.timeoutMillis = timeout;
		this.notificationHandle = showNotificationLike(this, position, entranceAnimation, exitAnimation, timeout);
		this.notificationHandle.onTimeout.addListener(() => this.onClosed.fire({byUser: false}));

	}

	close() {
		this.notificationHandle.close();
	}

	@executeWhenFirstDisplayed(true)
	public startCloseTimeout() {
		if (this.progressBar != null) {
			this.progressBar.reset();
			this.progressBar.setProgress(1, this.timeoutMillis); // mind the css transition!
		}
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

}


