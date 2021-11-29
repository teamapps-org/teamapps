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
import {TeamAppsUiContext} from "@teamapps/teamapps-client-core";
import {TeamAppsUiComponentRegistry} from "@teamapps/teamapps-client-core";
import {UiLinkButton_ClickedEvent, UiLinkButtonCommandHandler, UiLinkButtonConfig, UiLinkButtonEventSource} from "./generated/UiLinkButtonConfig";
import {AbstractUiComponent} from "@teamapps/teamapps-client-core";
import {TeamAppsEvent} from "@teamapps/teamapps-client-core";
import {parseHtml} from "@teamapps/teamapps-client-core";
import {UiLinkTarget} from "./generated/UiLinkTarget";

export class UiLinkButton extends AbstractUiComponent<UiLinkButtonConfig> implements UiLinkButtonEventSource, UiLinkButtonCommandHandler {

	public readonly onClicked: TeamAppsEvent<UiLinkButton_ClickedEvent> = new TeamAppsEvent(this);
	
	private readonly $main: HTMLAnchorElement;

	constructor(config: UiLinkButtonConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<a class="UiLinkButton"></a>`)
		this.$main.addEventListener("click", ev => {
			if (this._config.onClickJavaScript != null) {
				let context = this._context; // make context available in evaluated javascript
				eval(this._config.onClickJavaScript);
			}
			this.onClicked.fire({});
		});
		this.update(config);
	}


	protected doGetMainElement(): HTMLElement {
		return this.$main;
	}

	public update(config: UiLinkButtonConfig) {
		this.$main.text = config.text;
		if (config.url) {
			this.$main.href= config.url;
		} else {
			this.$main.removeAttribute("href");
		}
		this.$main.target = '_' + UiLinkTarget[config.target].toLocaleLowerCase();

	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiLinkButton", UiLinkButton);