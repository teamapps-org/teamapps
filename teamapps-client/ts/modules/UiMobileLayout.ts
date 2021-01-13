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

import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiNavigationBar} from "./UiNavigationBar";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiMobileLayoutCommandHandler, UiMobileLayoutConfig} from "../generated/UiMobileLayoutConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {pageTransition, pageTransitionAnimationPairs, parseHtml} from "./Common";
import {UiComponent} from "./UiComponent";
import {UiPageTransition} from "../generated/UiPageTransition";


export class UiMobileLayout extends AbstractUiComponent<UiMobileLayoutConfig> implements UiMobileLayoutCommandHandler {

	private $mainDiv: HTMLElement;
	private $toolbarContainer: HTMLElement;
	private $contentContainerWrapper: HTMLElement;
	private $navBarContainer: HTMLElement;

	private toolbar: UiToolbar;
	private navBar: UiNavigationBar;

	private content: UiComponent;
	private $contentContainer: HTMLElement;

	constructor(config: UiMobileLayoutConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$mainDiv = parseHtml(`<div id="${config.id}" class="UiMobileLayout">
                             <div class="toolbar-container"></div>
                             <div class="content-container-wrapper"></div>
                             <div class="navigation-bar-container"></div>
            </div>`);

		this.$toolbarContainer = this.$mainDiv.querySelector<HTMLElement>(':scope >.toolbar-container');
		this.$contentContainerWrapper = this.$mainDiv.querySelector<HTMLElement>(':scope >.content-container-wrapper');
		this.$navBarContainer = this.$mainDiv.querySelector<HTMLElement>(':scope >.navigation-bar-container');

		this.setToolbar(config.toolbar as UiToolbar);
		this.setNavigationBar(config.navigationBar as UiNavigationBar);

		if (config.initialView) {
			this.showView(config.initialView as UiComponent, null);
		}
	}

	public showView(view: UiComponent, transition: UiPageTransition = null, animationDuration = 0) {
		if (view === this.content) {
			return;
		}

		let oldContent = this.content;
		let $oldContentContainer = this.$contentContainer;

		this.content = view;

		this.$contentContainer = parseHtml(`<div class="content-container"></div>`);
		if (view != null) {
			this.$contentContainer.appendChild(view.getMainElement());
		}
		this.$contentContainerWrapper.appendChild(this.$contentContainer);

		if (transition != null && animationDuration > 0) {
			pageTransition($oldContentContainer, this.$contentContainer, transition, animationDuration, () => {
				$oldContentContainer && $oldContentContainer.remove();
			});
		} else {
			$oldContentContainer && $oldContentContainer.remove();
		}
	}

	public setToolbar(toolbar: UiToolbar): void {
		if (this.toolbar) {
			this.$toolbarContainer.innerHTML = '';
		}
		this.toolbar = toolbar;
		this.$toolbarContainer.classList.toggle('hidden', !toolbar);
		if (toolbar) {
			this.$toolbarContainer.appendChild(this.toolbar.getMainElement());
		}
	}

	public setNavigationBar(navBar: UiNavigationBar) {
		if (this.navBar) {
			this.$navBarContainer.innerHTML = '';
		}
		this.navBar = navBar;
		this.$navBarContainer.classList.toggle('hidden', !navBar);
		if (navBar) {
			this.$navBarContainer.appendChild(this.navBar.getMainElement());
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$mainDiv;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiMobileLayout", UiMobileLayout);
