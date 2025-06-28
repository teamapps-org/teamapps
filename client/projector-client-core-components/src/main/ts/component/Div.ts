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
import {type DtoDiv, type DtoDivCommandHandler} from "../generated";

import {AbstractComponent, type Component, type ServerObjectChannel} from "projector-client-object-api";

export class Div extends AbstractComponent<DtoDiv> implements DtoDivCommandHandler {

	private readonly $div: HTMLDivElement;

	constructor(config: DtoDiv, serverObjectChannel: ServerObjectChannel) {
		super(config);
		this.$div = document.createElement("div");
		if (config.innerHtml != null) {
			this.setInnerHtml(config.innerHtml);
		}
		if (config.content != null) {
			this.setContent(config.content);
		}
	}

	setContent(content: unknown): any {
		if (content == null) {
			this.$div.innerHTML = '';
		} else {
			this.$div.appendChild((content as Component).getMainElement())
		}
	}

	setInnerHtml(innerHtml: string): any {
		if (innerHtml == null) {
			this.$div.innerHTML = '';
		} else {
			this.$div.innerHTML = innerHtml;
		}
	}

	protected doGetMainElement(): HTMLElement {
		return this.$div;
	}
}
