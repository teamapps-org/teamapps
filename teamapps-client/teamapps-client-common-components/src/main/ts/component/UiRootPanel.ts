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
import {UiPageTransition, UiRootPanelCommandHandler, UiRootPanelConfig} from "../generated";
import {AbstractUiComponent} from "teamapps-client-core";
import {TeamAppsUiContext} from "teamapps-client-core";
import {pageTransition, parseHtml} from "../Common";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiComponent} from "./UiComponent";

// noinspection JSUnusedGlobalSymbols
export class UiRootPanel extends AbstractUiComponent<UiRootPanelConfig> implements UiRootPanelCommandHandler {

	private $root: HTMLElement;
	private content: UiComponent;
	private $contentWrapper: HTMLElement;
	private $imagePreloadDiv: HTMLElement;

	constructor(config: UiRootPanelConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.$root = parseHtml(`<div data-background-container-id="${config.id}" class="UiRootPanel teamapps-backgroundImage">
              <div class="image-preload-div"></div>
		</div>`);
		this.$imagePreloadDiv = this.$root.querySelector<HTMLElement>(":scope .image-preload-div");
		this.setContent(config.content as UiComponent);
	}

	public doGetMainElement(): HTMLElement {
		return this.$root;
	}

	public setContent(content: UiComponent, transition: UiPageTransition | null = null, animationDuration: number = 0): void {
		if (content == this.content) {
			return;
		}

		let oldContent = this.content;
		let $oldContentWrapper = this.$contentWrapper;

		this.content = content;

		this.$contentWrapper = parseHtml(`<div class="child-component-wrapper">`);
		if (content != null) {
			this.$contentWrapper.appendChild(content.getMainElement());
		}
		this.$root.appendChild(this.$contentWrapper);

		if (transition != null && animationDuration > 0) {
			pageTransition($oldContentWrapper, this.$contentWrapper, transition, animationDuration, () => {
				$oldContentWrapper && $oldContentWrapper.remove();
			});
		} else {
			$oldContentWrapper && $oldContentWrapper.remove();
		}
	}

	public destroy(): void {
		super.destroy();
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiRootPanel", UiRootPanel);
