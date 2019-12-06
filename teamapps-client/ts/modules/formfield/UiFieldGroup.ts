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
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {AbstractUiComponent} from "../AbstractUiComponent";
import {UiFieldGroupCommandHandler, UiFieldGroupConfig} from "../../generated/UiFieldGroupConfig";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {parseHtml} from "../Common";
import {UiField} from "./UiField";

export class UiFieldGroup extends AbstractUiComponent<UiFieldGroupConfig> implements UiFieldGroupCommandHandler {

	private $main: HTMLElement;
	private fields: UiField[];

	constructor(config: UiFieldGroupConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiFieldGroup"></div>`);
		this.setFields(config.fields as UiField[]);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public setFields(fields: UiField[]) {
		this.fields = fields;
		this.$main.innerHTML = '';
		fields.forEach(f => {
			this.$main.appendChild(f.getMainElement())
		});
	}

	destroy() {
		super.destroy();
		this.fields.forEach(f => f.destroy());
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiFieldGroup", UiFieldGroup);
