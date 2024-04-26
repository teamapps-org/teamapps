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

import {AbstractLegacyComponent, parseHtml, ServerChannel, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {removeClassesByFunction} from "../Common";
import {
	DtoProgressDisplay,
	DtoProgressDisplay_CancelButtonClickedEvent,
	DtoProgressDisplay_ClickedEvent,
	DtoProgressDisplayCommandHandler,
	DtoProgressDisplayEventSource
} from "../generated";
import {ProgressBar} from "../micro-components/ProgressBar";


export class ProgressDisplay extends AbstractLegacyComponent<DtoProgressDisplay> implements DtoProgressDisplayCommandHandler, DtoProgressDisplayEventSource {
	onCancelButtonClicked: TeamAppsEvent<DtoProgressDisplay_CancelButtonClickedEvent> = new TeamAppsEvent();
	onClicked: TeamAppsEvent<DtoProgressDisplay_ClickedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private $icon: HTMLElement;
	private $taskName: HTMLElement;
	private $statusMessage: HTMLElement;
	private $progress: HTMLElement;
	private $cancelButton: HTMLElement;
	private progressBar: ProgressBar;

	constructor(config: DtoProgressDisplay, serverChannel: ServerChannel) {
		super(config, serverChannel);

		this.$main = parseHtml(`<div class="ProgressDisplay">
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

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	update(config: DtoProgressDisplay): void {
		this.$icon.style.backgroundImage = `url('${config.icon}')`;
		this.$taskName.textContent = config.taskName;
		this.$statusMessage.textContent = config.statusMessage;
		this.progressBar.setProgress(config.progress);

		this.$main.classList.toggle("unknown-progress", config.progress < 0);

		removeClassesByFunction(this.$main.classList, className => className.startsWith("status-"));
		let statusClass = `status-${config.status}`;
		this.$main.classList.add(statusClass);

		this.$main.classList.toggle(`cancelable`, config.cancelable);
	}

}


