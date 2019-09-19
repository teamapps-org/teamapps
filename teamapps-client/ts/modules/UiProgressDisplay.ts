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
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {parseHtml, removeClassesByFunction} from "./Common";
import {
	UiProgressDisplay_CancelButtonClickedEvent,
	UiProgressDisplay_ClickedEvent,
	UiProgressDisplayCommandHandler,
	UiProgressDisplayConfig,
	UiProgressDisplayEventSource
} from "../generated/UiProgressDisplayConfig";
import {ProgressBar} from "./micro-components/ProgressBar";
import {UiProgressStatus} from "../generated/UiProgressStatus";


export class UiProgressDisplay extends AbstractUiComponent<UiProgressDisplayConfig> implements UiProgressDisplayCommandHandler, UiProgressDisplayEventSource {
	onCancelButtonClicked: TeamAppsEvent<UiProgressDisplay_CancelButtonClickedEvent> = new TeamAppsEvent(this);
	onClicked: TeamAppsEvent<UiProgressDisplay_ClickedEvent> = new TeamAppsEvent(this);

	private $main: HTMLElement;
	private $icon: HTMLElement;
	private $taskName: HTMLElement;
	private $statusMessage: HTMLElement;
	private $progress: HTMLElement;
	private $cancelButton: HTMLElement;
	private progressBar: ProgressBar;

	constructor(config: UiProgressDisplayConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiProgressDisplay">
			<div class="title">
				<div class="icon img img-16"></div>
        		<div class="task-name"></div>
			</div>
			<div class="content">
	            <div class="status-string"></div>
	            <div class="progress-bar-wrapper"></div>
	            <div class="cancel-button img img-16"></div>
			</div>
		</div>`);

		this.$icon = this.$main.querySelector<HTMLElement>(":scope .icon");
		this.$taskName = this.$main.querySelector<HTMLElement>(":scope .task-name");
		this.$statusMessage = this.$main.querySelector<HTMLElement>(":scope .status-string");
		this.$progress = this.$main.querySelector<HTMLElement>(":scope .progress-bar-wrapper");
		this.progressBar = new ProgressBar(0, {height: 4, transitionTime: 400});
		this.$progress.appendChild(this.progressBar.getMainDomElement());
		this.$cancelButton = this.$main.querySelector<HTMLElement>(":scope .cancel-button");

		this.$cancelButton.addEventListener("click", ev => this.onCancelButtonClicked.fire({}));

		this.update(config);
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

	update(config: UiProgressDisplayConfig): void {
		this.$icon.style.backgroundImage = `url(${this._context.getIconPath(config.icon, 16)})`;
		this.$taskName.textContent = config.taskName;
		this.$statusMessage.textContent = config.statusMessage;
		this.progressBar.setProgress(config.progress);

		this.$main.classList.toggle("unknown-progress", config.progress < 0);

		removeClassesByFunction(this.$main.classList, className => className.startsWith("status-"));
		let statusClass = `status-${UiProgressStatus[config.status].toLowerCase().replace(/_/g, '-')}`;
		this.$main.classList.add(statusClass);

		this.$main.classList.toggle(`cancelable`, config.cancelable);
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiProgressDisplay", UiProgressDisplay);
