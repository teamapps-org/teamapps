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
import {parseHtml} from "./Common";
import {UiDefaultMultiProgressDisplayCommandHandler, UiDefaultMultiProgressDisplayConfig, UiDefaultMultiProgressDisplayEventSource} from "../generated/UiDefaultMultiProgressDisplayConfig";
import {UiMultiProgressDisplay_ClickedEvent, UiMultiProgressDisplayCommandHandler, UiMultiProgressDisplayConfig, UiMultiProgressDisplayEventSource} from "../generated/UiMultiProgressDisplayConfig";

export abstract class UiMultiProgressDisplay<C extends UiMultiProgressDisplayConfig = UiMultiProgressDisplayConfig> extends AbstractUiComponent<C> implements UiMultiProgressDisplayCommandHandler, UiMultiProgressDisplayEventSource {
	public readonly onClicked: TeamAppsEvent<UiMultiProgressDisplay_ClickedEvent> = new TeamAppsEvent(this);
	abstract update(config: C): void;
}

export class UiDefaultMultiProgressDisplay extends UiMultiProgressDisplay<UiDefaultMultiProgressDisplayConfig> implements UiDefaultMultiProgressDisplayCommandHandler, UiDefaultMultiProgressDisplayEventSource {

	private $main: HTMLElement;
	private $spinner: HTMLElement;
	private $runningCount: HTMLElement;

	constructor(config: UiDefaultMultiProgressDisplayConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$main = parseHtml(`<div class="UiDefaultMultiProgressDisplay">
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

	update(config: UiDefaultMultiProgressDisplayConfig): void {
		this.$main.classList.toggle("no-tasks", config.runningCount === 0);
		this.$spinner.classList.toggle('hidden', config.runningCount === 0);
		this.$runningCount.innerText = "" + config.runningCount;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiDefaultMultiProgressDisplay", UiDefaultMultiProgressDisplay);
