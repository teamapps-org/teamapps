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
import {AbstractComponent, parseHtml, type ServerObjectChannel, ProjectorEvent} from "projector-client-object-api";

import {
	type DtoLinkButton,
	type DtoLinkButton_ClickEvent,
	type DtoLinkButtonCommandHandler,
	type DtoLinkButtonEventSource,
	type LinkTarget
} from "../generated";

export class LinkButton extends AbstractComponent<DtoLinkButton> implements DtoLinkButtonEventSource, DtoLinkButtonCommandHandler {

	public readonly onClick: ProjectorEvent<DtoLinkButton_ClickEvent> = new ProjectorEvent();
	
	private readonly $main: HTMLAnchorElement;

	constructor(config: DtoLinkButton, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$main = parseHtml(`<a class="LinkButton" tabindex="0"></a>`)
		this.$main.addEventListener("click", ev => this.onClick.fire({}));
		this.update(config);
	}


	protected doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public update(config: DtoLinkButton) {
		this.$main.text = config.text;
		if (config.url) {
			this.$main.href= config.url;
		} else {
			this.$main.removeAttribute("href");
		}
		this.$main.target = '_' + config.target;

	}
}


