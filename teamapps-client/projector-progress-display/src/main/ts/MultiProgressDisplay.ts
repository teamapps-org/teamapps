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
	AbstractLegacyComponent, animateCollapse,
	ComponentLike,
	EntranceAnimation,
	ExitAnimation, NotificationHandle,
	parseHtml, prepareNotificationLike,
	ServerObjectChannel,
	TeamAppsEvent
} from "projector-client-object-api";
import {
	DtoMultiProgressDisplay,
	DtoMultiProgressDisplay_ClosedEvent,
	DtoMultiProgressDisplay_OpenedEvent,
	DtoMultiProgressDisplayCommandHandler,
	DtoMultiProgressDisplayEventSource
} from "./generated";
import {ProgressDisplay} from "./ProgressDisplay";

export class MultiProgressDisplay extends AbstractLegacyComponent<DtoMultiProgressDisplay> implements DtoMultiProgressDisplayCommandHandler, DtoMultiProgressDisplayEventSource {

	public readonly onOpened: TeamAppsEvent<DtoMultiProgressDisplay_OpenedEvent> = new TeamAppsEvent();
	public readonly onClosed: TeamAppsEvent<DtoMultiProgressDisplay_ClosedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $spinner: HTMLElement;
	private $runningCount: HTMLElement;
	private $popup: HTMLElement;
	private $progressList: HTMLElement;
	private popupComponentLike: ComponentLike;
	private notificationHandle: NotificationHandle;
	private progressDisplays: ProgressDisplay[] = [];

	constructor(config: DtoMultiProgressDisplay, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);

		this.$main = parseHtml(`<div class="DefaultMultiProgressDisplay">
	<div class="spinner teamapps-spinner"></div>					
	<div class="running-count">0</div>					
</div>`);
		this.$spinner = this.$main.querySelector<HTMLElement>(":scope .spinner");
		this.$runningCount = this.$main.querySelector<HTMLElement>(":scope .running-count");

		this.$popup = parseHtml(`<div class="MultiProgressDisplay-popup">
	<div class="close-button"></div>
	<div class="progress-list"></div>
</div>`);
		this.popupComponentLike = {getMainElement: () => this.$popup};
		this.notificationHandle = prepareNotificationLike(this.popupComponentLike, this.config.position, EntranceAnimation.SLIDE_IN_LEFT, ExitAnimation.ZOOM_OUT, -1);
		this.$progressList = this.$popup.querySelector<HTMLElement>(":scope .progress-list");

		this.$main.addEventListener("click", ev => {
			if (this.notificationHandle.isOpen) {
				this.notificationHandle.close();
			} else {
				this.notificationHandle.open(this.config.position, -1);
			}
		});
	}

	open(timeout: number) {
		this.notificationHandle.open(this.config.position, timeout);
	}

	close() {
		this.notificationHandle.close();
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	add(progressDisplay: ProgressDisplay): void {
		this.progressDisplays.push(progressDisplay);
		this.$progressList.appendChild(progressDisplay.doGetMainElement());
		if (this.notificationHandle.isOpen) {
			animateCollapse(progressDisplay.getMainElement(), false, 300);
		}
	}

	remove(progressDisplay: ProgressDisplay): any {
		this.progressDisplays = this.progressDisplays.filter(pd => pd !== progressDisplay);
		if (this.notificationHandle.isOpen) {
			animateCollapse(progressDisplay.getMainElement(), true, 300, () => progressDisplay.doGetMainElement().remove());
		} else {
			progressDisplay.doGetMainElement().remove();
		}
	}

	updateRunningCount(count: number) {
		this.$runningCount.innerText = count > 0 ? "" + count : "";
		this.$spinner.classList.toggle("hidden", count == 0);
	}

}


