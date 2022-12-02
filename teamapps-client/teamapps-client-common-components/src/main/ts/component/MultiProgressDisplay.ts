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

import {AbstractComponent, parseHtml, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {
	DtoDefaultMultiProgressDisplay,
	DtoDefaultMultiProgressDisplayCommandHandler,
	DtoDefaultMultiProgressDisplayEventSource,
	DtoMultiProgressDisplay,
	DtoMultiProgressDisplay_ClickedEvent,
	DtoMultiProgressDisplayCommandHandler,
	DtoMultiProgressDisplayEventSource
} from "../generated";

export abstract class MultiProgressDisplay<C extends DtoMultiProgressDisplay = DtoMultiProgressDisplay> extends AbstractComponent<C> implements DtoMultiProgressDisplayCommandHandler, DtoMultiProgressDisplayEventSource {
	public readonly onClicked: TeamAppsEvent<DtoMultiProgressDisplay_ClickedEvent> = new TeamAppsEvent();
	abstract update(config: C): void;
}

export class DefaultMultiProgressDisplay extends MultiProgressDisplay<DtoDefaultMultiProgressDisplay> implements DtoDefaultMultiProgressDisplayCommandHandler, DtoDefaultMultiProgressDisplayEventSource {

	private $main: HTMLElement;
	private $spinner: HTMLElement;
	private $runningCount: HTMLElement;

	constructor(config: DtoDefaultMultiProgressDisplay, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="DefaultMultiProgressDisplay">
	<div class="spinner teamapps-spinner"></div>					
	<div class="running-count">0</div>					
</div>`);

		this.$spinner = this.$main.querySelector<HTMLElement>(":scope .spinner");
		this.$runningCount = this.$main.querySelector<HTMLElement>(":scope .running-count");

		this.$main.addEventListener("mousedown", ev => this.onClicked.fire({}));

		this.update(config);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	update(config: DtoDefaultMultiProgressDisplay): void {
		this.$main.classList.toggle("no-tasks", config.runningCount === 0);
		this.$spinner.classList.toggle('hidden', config.runningCount === 0);
		this.$runningCount.innerText = "" + config.runningCount;
	}

}


