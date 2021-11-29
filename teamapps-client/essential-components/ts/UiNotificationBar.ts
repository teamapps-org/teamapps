/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import {AbstractUiComponent} from "@teamapps/teamapps-client-core";
import {TeamAppsUiContext} from "@teamapps/teamapps-client-core";
import {animateCSS, Constants, parseHtml} from "@teamapps/teamapps-client-core";
import {TeamAppsEvent} from "@teamapps/teamapps-client-core";
import {TeamAppsUiComponentRegistry} from "@teamapps/teamapps-client-core";
import {
	UiNotificationBar_ItemClickedEvent,
	UiNotificationBar_ItemClosedEvent,
	UiNotificationBarCommandHandler,
	UiNotificationBarConfig,
	UiNotificationBarEventSource
} from "./generated/UiNotificationBarConfig";
import {UiNotificationBarItemConfig} from "./generated/UiNotificationBarItemConfig";
import {createUiSpacingValueCssString} from "@teamapps/teamapps-client-core";
import {ProgressBar} from "@teamapps/teamapps-client-core";
import {UiExitAnimation} from "./generated/UiExitAnimation";
import {UiEntranceAnimation} from "./generated/UiEntranceAnimation";

export class UiNotificationBar extends AbstractUiComponent<UiNotificationBarConfig> implements UiNotificationBarCommandHandler, UiNotificationBarEventSource {

	public readonly onItemClicked: TeamAppsEvent<UiNotificationBar_ItemClickedEvent> = new TeamAppsEvent(this);
	public readonly onItemClosed: TeamAppsEvent<UiNotificationBar_ItemClosedEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private itemsById: { [id: string]: UiNotificationBarItem } = {};

	constructor(config: UiNotificationBarConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiNotificationBar"></div>`);
		config.initialItems.forEach(item => this.addItem(item))
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	addItem(itemConfig: UiNotificationBarItemConfig): void {
		this.removeItem(itemConfig.id, null);
		let item = new UiNotificationBarItem(itemConfig);
		this.itemsById[itemConfig.id] = item;
		this.$main.appendChild(item.getMainElement());
		if (itemConfig.entranceAnimation != null) {
			animateCSS(item.getMainElement(), Constants.ENTRANCE_ANIMATION_CSS_CLASSES[itemConfig.entranceAnimation]);
		}
		item.startCloseTimeout();
		item.onClicked.addListener(() => this.onItemClicked.fire({id: itemConfig.id}));
		item.onClosed.addListener(wasTimeout => {
			this.removeItem(itemConfig.id);
			this.onItemClosed.fire({id: itemConfig.id, wasTimeout: wasTimeout});
		});
	}

	removeItem(id: string, exitAnimation?: UiExitAnimation): void {
		let item = this.itemsById[id];
		if (item != null) {
			if (exitAnimation != null || item.config.exitAnimation != null) {
				animateCSS(item.getMainElement(), Constants.EXIT_ANIMATION_CSS_CLASSES[exitAnimation || item.config.exitAnimation], 300, () => {
					item.getMainElement().remove();
				});
			} else {
				item.getMainElement().remove();
			}
		}
		delete this.itemsById[id];
	}

}

class UiNotificationBarItem {

	public readonly onClicked: TeamAppsEvent<void> = new TeamAppsEvent<void>(this);
	public readonly onClosed: TeamAppsEvent<boolean> = new TeamAppsEvent<boolean>(this);

	private $main: HTMLElement;
	private $progressBarContainer: HTMLElement;
	private progressBar: ProgressBar;

	constructor(public readonly config: UiNotificationBarItemConfig) {
		this.$main = parseHtml(`<div class="UiNotificationBarItem ${config.displayTimeInMillis > 0 && config.progressBarVisible ? "with-progress" : ""}">
	<div class="content-container">
		<div class="icon img img-20 ${config.icon == null ? "hidden" : ""}" style="background-image: url('${config.icon}')"></div>
		<div class="text">${config.text}</div>
		<div class="close-button"></div>
	</div>
	<div class="progress-container"></div>
</div>`);
		let $closeButton: HTMLElement = this.$main.querySelector(":scope .close-button");
		$closeButton.classList.toggle("hidden", !config.dismissible);
		$closeButton.addEventListener("click", ev => this.onClosed.fire(false));
		this.$main.style.backgroundColor = config.backgroundColor;
		this.$main.style.borderColor = config.borderColor;
		let $contentContainer: HTMLElement = this.$main.querySelector(":scope > .content-container");
		$contentContainer.style.padding = createUiSpacingValueCssString(config.padding);
		let $text: HTMLElement = this.$main.querySelector(":scope .text");
		$text.style.color = config.textColor;

		this.$main.addEventListener("click", () => this.onClicked.fire())

		this.$progressBarContainer = this.$main.querySelector(":scope > .progress-container");
		if (config.displayTimeInMillis > 0 && config.progressBarVisible) {
			this.progressBar = new ProgressBar(0, {height: 3, transitionTime: 500});
			this.$progressBarContainer.appendChild(this.progressBar.getMainDomElement());
		}

		let $icon: HTMLElement = this.$main.querySelector(":scope .icon");
		if (config.iconAnimation != null) {
			$icon.classList.add(...Constants.REPEATABLE_ANIMATION_CSS_CLASSES[config.iconAnimation].split(/ +/));
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

TeamAppsUiComponentRegistry.registerComponentClass("UiNotificationBar", UiNotificationBar);
