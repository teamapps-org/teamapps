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
	animateCSS,
	ExitAnimation,
	parseHtml, removeClassesByFunction,
	ServerObjectChannel,
	TeamAppsEvent
} from "projector-client-object-api";
import {createUiSpacingValueCssString} from "projector-client-object-api";
import {
	DtoNotificationBar, DtoNotificationBar_ItemActionLinkClickedEvent,
	DtoNotificationBar_ItemClickedEvent, DtoNotificationBar_ItemClosedEvent,
	DtoNotificationBarCommandHandler,
	DtoNotificationBarEventSource, DtoNotificationBarItem
} from "./generated";
import {ProgressBar} from "projector-progress-indicator";

export class NotificationBar extends AbstractLegacyComponent<DtoNotificationBar> implements DtoNotificationBarCommandHandler, DtoNotificationBarEventSource {

	public readonly onItemClicked: TeamAppsEvent<DtoNotificationBar_ItemClickedEvent> = new TeamAppsEvent();
	public readonly onItemActionLinkClicked: TeamAppsEvent<DtoNotificationBar_ItemActionLinkClickedEvent> = new TeamAppsEvent();
	public readonly onItemClosed: TeamAppsEvent<DtoNotificationBar_ItemClosedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private itemsById: { [id: string]: NotificationBarItem } = {};

	constructor(config: DtoNotificationBar, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$main = parseHtml(`<div class="NotificationBar"></div>`);
		config.initialItems.forEach(item => this.addItem(item))
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addItem(itemConfig: DtoNotificationBarItem): void {
		this.removeItem(itemConfig.id, null);
		let item = new NotificationBarItem(itemConfig);
		this.itemsById[itemConfig.id] = item;
		this.$main.appendChild(item.getMainElement());
		if (itemConfig.entranceAnimation != null) {
			animateCSS(item.getMainElement(), itemConfig.entranceAnimation);
		}
		item.startCloseTimeout();
		item.onClick.addListener(() => this.onItemClicked.fire({id: itemConfig.id}));
		item.onActionLinkClicked.addListener(() => this.onItemActionLinkClicked.fire({id: itemConfig.id}));
		item.onClosed.addListener(wasTimeout => {
			this.removeItem(itemConfig.id);
			this.onItemClosed.fire({id: itemConfig.id, wasTimeout: wasTimeout});
		});
	}

	updateItem(itemConfig: DtoNotificationBarItem): any {
		let item = this.itemsById[itemConfig.id];
		if (item != null) {
			item.update(itemConfig);
		}
	}

	removeItem(id: string, exitAnimation?: ExitAnimation): void {
		let item = this.itemsById[id];
		if (item != null) {
			if (exitAnimation != null || item.config.exitAnimation != null) {
				animateCSS(item.getMainElement(), exitAnimation || item.config.exitAnimation, 300, () => {
					item.getMainElement().remove();
				});
			} else {
				item.getMainElement().remove();
			}
		}
		delete this.itemsById[id];
	}

}

class NotificationBarItem {

	public readonly onClick: TeamAppsEvent<void> = new TeamAppsEvent();
	public readonly onActionLinkClicked: TeamAppsEvent<void> = new TeamAppsEvent();
	public readonly onClosed: TeamAppsEvent<boolean> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $progressBarContainer: HTMLElement;
	private progressBar: ProgressBar;
	private $closeButton: HTMLElement;
	private $contentContainer: HTMLElement;
	private $icon: HTMLElement;
	private $text: HTMLElement;
	private $actionLink: HTMLElement;

	constructor(public readonly config: DtoNotificationBarItem) {
		this.$main = parseHtml(`<div class="NotificationBarItem">
	<div class="content-container">
		<div class="icon img img-20"></div>
		<div class="text-container">
			<span class="text"></span>
			<a class="action-link"></a>
		</div>
		<div class="img img-16 ta-icon-close"></div>
	</div>
	<div class="progress-container"></div>
</div>`);
		this.$closeButton = this.$main.querySelector(":scope .close-button");
		this.$contentContainer = this.$main.querySelector(":scope > .content-container");
		this.$icon = this.$main.querySelector(":scope .icon");
		this.$text = this.$main.querySelector(":scope .text");
		this.$actionLink = this.$main.querySelector(":scope .action-link");
		this.$progressBarContainer = this.$main.querySelector(":scope > .progress-container");
		this.progressBar = new ProgressBar(0, {height: 3});
		this.$progressBarContainer.appendChild(this.progressBar.getMainDomElement());

		this.$main.addEventListener("click", () => this.onClick.fire())
		this.$actionLink.addEventListener("click", (e) => this.onActionLinkClicked.fire())
		this.$closeButton.addEventListener("click", ev => this.onClosed.fire(false));

		this.update(config);
	}

	public update(config: DtoNotificationBarItem) {
		this.$closeButton.classList.toggle("hidden", !config.dismissible);
		this.$main.style.backgroundColor = config.backgroundColor;
		this.$main.style.borderColor = config.borderColor;
		this.$contentContainer.style.padding = createUiSpacingValueCssString(config.padding);

		this.$text.textContent = config.text;
		this.$text.style.color = config.textColor;

		this.$actionLink.textContent = config.actionLinkText;
		this.$actionLink.classList.toggle("hidden", config.actionLinkText == null);
		this.$actionLink.style.color = config.actionLinkColor;

		this.$main.classList.toggle("with-progress", config.displayTimeInMillis > 0 && config.progressBarVisible)

		this.$icon.classList.toggle("hidden", config.icon == null)
		this.$icon.style.backgroundImage = `url('${config.icon}')`;
		removeClassesByFunction(this.$icon.classList, className => className.startsWith("animate__"));

		if (config.iconAnimation != null) {
			this.$icon.classList.add(...config.iconAnimation.split(/ +/));
		}
	}

	public startCloseTimeout() {
		if (this.config.displayTimeInMillis > 0) {
			if (this.config.progressBarVisible) {
				let startTime = +(new Date());
				let interval = setInterval(() => {
					let duration = +(new Date()) - startTime + 500; // make sure the bar reaches the end!
					let progress = Math.min(1, duration / this.config.displayTimeInMillis);
					this.progressBar.setProgress(progress);
					(this.progressBar.getMainDomElement().querySelector(":scope .progress-bar") as HTMLElement).style.backgroundColor = this.config.borderColor;
					if (progress >= 1) {
						window.clearInterval(interval);
					}
				}, 100);
			}
			window.setTimeout(() => {
				this.onClosed.fire(true);
			}, this.config.displayTimeInMillis);
		}
	}

	public getMainElement() {
		return this.$main;
	}
}


