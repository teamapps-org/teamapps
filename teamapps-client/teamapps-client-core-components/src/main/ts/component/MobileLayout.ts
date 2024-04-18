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

import {Toolbar} from "./tool-container/toolbar/Toolbar";
import {NavigationBar} from "./NavigationBar";
import {AbstractLegacyComponent, Component, parseHtml, ServerObjectChannel, TeamAppsUiContext} from "teamapps-client-core";
import {DtoMobileLayout, DtoMobileLayoutCommandHandler, DtoPageTransition} from "../generated";

import {pageTransition} from "../Common";


export class MobileLayout extends AbstractLegacyComponent<DtoMobileLayout> implements DtoMobileLayoutCommandHandler {

	private $mainDiv: HTMLElement;
	private $toolbarContainer: HTMLElement;
	private $contentContainerWrapper: HTMLElement;
	private $navBarContainer: HTMLElement;

	private toolbar: Toolbar;
	private navBar: NavigationBar;

	private content: Component;
	private $contentContainer: HTMLElement;

	constructor(config: DtoMobileLayout, serverChannel: ServerObjectChannel) {
		super(config, serverChannel);
		this.$mainDiv = parseHtml(`<div class="MobileLayout">
                             <div class="toolbar-container"></div>
                             <div class="content-container-wrapper"></div>
                             <div class="navigation-bar-container"></div>
            </div>`);

		this.$toolbarContainer = this.$mainDiv.querySelector<HTMLElement>(':scope >.toolbar-container');
		this.$contentContainerWrapper = this.$mainDiv.querySelector<HTMLElement>(':scope >.content-container-wrapper');
		this.$navBarContainer = this.$mainDiv.querySelector<HTMLElement>(':scope >.navigation-bar-container');

		this.setToolbar(config.toolbar as Toolbar);
		this.setNavigationBar(config.navigationBar as NavigationBar);

		if (config.initialView) {
			this.showView(config.initialView as Component, null);
		}
	}

	public showView(view: Component, transition: DtoPageTransition = null, animationDuration = 0) {
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

	public setToolbar(toolbar: Toolbar): void {
		if (this.toolbar) {
			this.$toolbarContainer.innerHTML = '';
		}
		this.toolbar = toolbar;
		this.$toolbarContainer.classList.toggle('hidden', !toolbar);
		if (toolbar) {
			this.$toolbarContainer.appendChild(this.toolbar.getMainElement());
		}
	}

	public setNavigationBar(navBar: NavigationBar) {
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


