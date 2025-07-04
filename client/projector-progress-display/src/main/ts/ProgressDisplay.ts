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

import {AbstractComponent, parseHtml, removeClassesByFunction, type ServerObjectChannel, ProjectorEvent} from "projector-client-object-api";
import {
	type DtoProgressDisplay,
	type DtoProgressDisplay_CancelButtonClickedEvent, type DtoProgressDisplay_ClickEvent,
	type DtoProgressDisplayCommandHandler,
	type DtoProgressDisplayEventSource
} from "./generated";
import {ProgressBar} from "projector-progress-indicator";


export class ProgressDisplay extends AbstractComponent<DtoProgressDisplay> implements DtoProgressDisplayCommandHandler, DtoProgressDisplayEventSource {
	onCancelButtonClicked: ProjectorEvent<DtoProgressDisplay_CancelButtonClickedEvent> = new ProjectorEvent();
	onClick: ProjectorEvent<DtoProgressDisplay_ClickEvent> = new ProjectorEvent();

	private $main: HTMLElement;
	private $icon: HTMLElement;
	private $taskName: HTMLElement;
	private $statusMessage: HTMLElement;
	private $progress: HTMLElement;
	private $cancelButton: HTMLElement;
	private progressBar: ProgressBar;

	constructor(config: DtoProgressDisplay, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);

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

		this.$icon = this.$main.querySelector<HTMLElement>(":scope .icon")!;
		this.$taskName = this.$main.querySelector<HTMLElement>(":scope .task-name")!;
		this.$statusMessage = this.$main.querySelector<HTMLElement>(":scope .status-string")!;
		this.$progress = this.$main.querySelector<HTMLElement>(":scope .progress-bar-wrapper")!;
		this.progressBar = new ProgressBar(0, {height: 4});
		this.$progress.appendChild(this.progressBar.getMainDomElement());
		this.$cancelButton = this.$main.querySelector<HTMLElement>(":scope .cancel-button")!;

		this.$cancelButton.addEventListener("click", () => this.onCancelButtonClicked.fire({}));

		this.update(config);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	update(config: DtoProgressDisplay): void {
		this.$icon.style.backgroundImage = `url('${config.icon}')`;
		this.$taskName.textContent = config.taskName;
		this.$statusMessage.textContent = config.statusMessage;
		this.progressBar.setProgress(config.progress, 400);

		this.$main.classList.toggle("unknown-progress", config.progress < 0);

		removeClassesByFunction(this.$main.classList, className => className.startsWith("status-"));
		let statusClass = `status-${config.status}`;
		this.$main.classList.add(statusClass);

		this.$main.classList.toggle(`cancelable`, config.cancelable);
	}

}


