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
	DtoNotification,
	DtoNotification_ClosedEvent,
	DtoNotification_OpenedEvent,
	DtoNotificationCommandHandler,
	DtoNotificationEventSource,
	NotificationPosition
} from "../generated";
import {
	AbstractLegacyComponent,
	Component, EntranceAnimation, ExitAnimation,
	executeWhenFirstDisplayed,
	parseHtml,
	ServerObjectChannel,
	TeamAppsEvent, animateCSS
} from "projector-client-object-api";
import {createUiSpacingValueCssString} from "projector-client-object-api";
import {ProgressBar} from "../micro-components/ProgressBar";

const containersByPosition: {
	[NotificationPosition.TOP_LEFT]: HTMLElement,
	[NotificationPosition.TOP_CENTER]: HTMLElement,
	[NotificationPosition.TOP_RIGHT]: HTMLElement,
	[NotificationPosition.BOTTOM_LEFT]: HTMLElement,
	[NotificationPosition.BOTTOM_CENTER]: HTMLElement,
	[NotificationPosition.BOTTOM_RIGHT]: HTMLElement
} = {
	[NotificationPosition.TOP_LEFT]: parseHtml(`<div class="Notification-container top-left"></div>`),
	[NotificationPosition.TOP_CENTER]: parseHtml(`<div class="Notification-container top-center"></div>`),
	[NotificationPosition.TOP_RIGHT]: parseHtml(`<div class="Notification-container top-right"></div>`),
	[NotificationPosition.BOTTOM_LEFT]: parseHtml(`<div class="Notification-container bottom-left"></div>`),
	[NotificationPosition.BOTTOM_CENTER]: parseHtml(`<div class="Notification-container bottom-center"></div>`),
	[NotificationPosition.BOTTOM_RIGHT]: parseHtml(`<div class="Notification-container bottom-right"></div>`)
};

let notifications: {
	notification: Notification;
	position: NotificationPosition;
	$wrapper: HTMLElement;
}[] = [];

function getNotificationsByPosition(position: NotificationPosition) {
	return notifications.filter(n => n.position == position);
}

let updateContainerVisibilities = function () {
	[NotificationPosition.TOP_LEFT,
		NotificationPosition.TOP_CENTER,
		NotificationPosition.TOP_RIGHT,
		NotificationPosition.BOTTOM_LEFT,
		NotificationPosition.BOTTOM_CENTER,
		NotificationPosition.BOTTOM_RIGHT].forEach(pos => {
		let hasNotifications = getNotificationsByPosition(pos).length > 0;
		if (hasNotifications && containersByPosition[pos].parentNode !== document.body) {
			document.body.appendChild(containersByPosition[pos]);
		} else if (!hasNotifications) {
			containersByPosition[pos].remove();
		}
	});
};

export function showNotification(notification: Notification, position: NotificationPosition, entranceAnimation: EntranceAnimation, exitAnimation: ExitAnimation) {
	let notif = notifications.filter(n => n.notification == notification)[0];

	if (notif == null || notif.position != position) {
		if (notif == null) {
			let $wrapper = parseHtml(`<div class="notification-wrapper"></div>`);
			$wrapper.appendChild(notification.getMainElement());
			notif = {notification, position, $wrapper};
			notifications.push(notif)
		} else {
			notif.position = position;
		}
		notif.$wrapper.style.height = null;
		notif.$wrapper.style.marginBottom = null;
		notif.$wrapper.style.zIndex = null;
		containersByPosition[position].appendChild(notif.$wrapper);

		updateContainerVisibilities();

		animateCSS(notification.getMainElement(), entranceAnimation, 700);

		let closeListener = () => {
			notification.onClosedAnyWay.removeListener(closeListener);
			notif.$wrapper.style.height = `${notif.$wrapper.offsetHeight}px`;
			notif.$wrapper.offsetHeight; // make sure the style above is applied so we get a transition!
			notif.$wrapper.style.height = "0px";
			notif.$wrapper.style.marginBottom = "0px";
			notif.$wrapper.style.zIndex = "0";

			animateCSS(notification.getMainElement(), exitAnimation, 700, () => {
				notif.$wrapper.remove();
				updateContainerVisibilities();
			});

			notifications = notifications.filter(n => n.notification !== notification);
		};
		notification.onClosedAnyWay.addListener(closeListener);

		notification.onOpened.fire({});
	}

	setTimeout(() => notification.startCloseTimeout());
}

export class Notification extends AbstractLegacyComponent<DtoNotification> implements DtoNotificationCommandHandler, DtoNotificationEventSource {

	public readonly onOpened: TeamAppsEvent<DtoNotification_OpenedEvent> = new TeamAppsEvent();
	public readonly onClosed: TeamAppsEvent<DtoNotification_ClosedEvent> = new TeamAppsEvent();
	public readonly onClosedAnyWay: TeamAppsEvent<void> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $contentContainer: HTMLElement;
	private $progressBarContainer: HTMLElement;
	private progressBar: ProgressBar;

	constructor(config: DtoNotification, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);

		this.$main = parseHtml(`<div class="Notification">
	<div class="close-button"></div>
	<div class="content-container"></div>
	<div class="progress-container"></div>
</div>`);
		this.$contentContainer = this.$main.querySelector(":scope > .content-container");
		this.$progressBarContainer = this.$main.querySelector(":scope > .progress-container");
		this.$main.querySelector(":scope > .close-button").addEventListener("mousedown", () => {
			this.close();
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
		this.$main.classList.toggle("show-progress", config.progressBarVisible && config.displayTimeInMillis > 0);

		if (config.progressBarVisible && this.progressBar == null) {
			this.progressBar = new ProgressBar(0, {height: 5, transitionTime: config.displayTimeInMillis});
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

	public static showNotification(notification: Notification, position: NotificationPosition, entranceAnimation: EntranceAnimation, exitAnimation: ExitAnimation) {
		showNotification(notification, position, entranceAnimation, exitAnimation);
	}

	private closeTimeout: number;

	@executeWhenFirstDisplayed(true)
	public startCloseTimeout() {
		if (this.progressBar != null) {
			this.progressBar.setProgress(1); // mind the css transition!
		}
		if (this.closeTimeout != null) {
			window.clearTimeout(this.closeTimeout);
		}
		if (this.config.displayTimeInMillis > 0) {
			this.closeTimeout = window.setTimeout(() => {
				this.close();
				this.onClosed.fire({byUser: false});
				this.onClosedAnyWay.fire();
			}, this.config.displayTimeInMillis);
		}
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	close(): void {
		this.onClosedAnyWay.fire();
	}

}


