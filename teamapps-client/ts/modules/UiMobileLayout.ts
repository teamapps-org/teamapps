/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
import {UiMobileLayoutCommandHandler, UiMobileLayoutConfig, UiMobileLayout_NavigationRequestedEvent} from "../generated/UiMobileLayoutConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {pageTransition, pageTransitionAnimationPairs, parseHtml} from "./Common";
import {UiComponent} from "./UiComponent";
import {UiMobileNavigationStateConfig} from "../generated/UiMobileNavigationStateConfig";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {MobileEdgeSwipe} from "./util/MobileEdgeSwipe";
import {MobileHistoryOwner, mobileNavigationHistory} from "./util/MobileNavigationHistory";
import {UiPageTransition} from "../generated/UiPageTransition";


export class UiMobileLayout extends AbstractUiComponent<UiMobileLayoutConfig> implements UiMobileLayoutCommandHandler {

	public readonly onNavigationRequested = new TeamAppsEvent<UiMobileLayout_NavigationRequestedEvent>();
	private navigationState: UiMobileNavigationStateConfig;
	private edgeSwipe: MobileEdgeSwipe;
	private navigationPending = false;
	private transitionUntil = 0;
	private disposed = false;
	private historyUpdateQueued = false;
	private readonly historyOwner: MobileHistoryOwner = {
		isActive: () => this.isNavigationActive() && !!this.navigationState.browserHistory,
		getPosition: () => this.navigationState.entries.indexOf(this.navigationState.currentEntry),
		getLength: () => this.navigationState.entries.length,
		navigateTo: position => this.requestNavigation(position)
	};

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
		this.$mainDiv = parseHtml(`<div class="UiMobileLayout">
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
		this.edgeSwipe = new MobileEdgeSwipe(this.$contentContainerWrapper, () => {
			if (!this.navigationState?.edgeSwipes || !this.isNavigationActive() || this.navigationPending
					|| Date.now() < this.transitionUntil || document.querySelector('.UiWindow.modal')) return null;
			return this.navigationState;
		}, direction => this.requestNavigation(this.historyOwner.getPosition() + direction));
		this.deFactoVisibilityChanged.addListener(() => this.updateHistory());
		this.setNavigationState(config.navigationState);
	}

	public setNavigationState(state: UiMobileNavigationStateConfig): void {
		this.edgeSwipe?.cancel();
		this.navigationState = state;
		this.navigationPending = false;
		this.updateHistory();
	}

	private isNavigationActive(): boolean {
		return !this.disposed && !!this.navigationState && this.$mainDiv.isConnected
			&& this.$mainDiv.getBoundingClientRect().width > 0 && this.$mainDiv.getBoundingClientRect().height > 0;
	}

	private updateHistory(): void {
		if (this.historyUpdateQueued) return;
		this.historyUpdateQueued = true;
		Promise.resolve().then(() => {
			this.historyUpdateQueued = false;
			if (this.historyOwner.isActive()) mobileNavigationHistory.update(this.historyOwner);
			else mobileNavigationHistory.release(this.historyOwner);
		});
	}

	private requestNavigation(position: number): void {
		const state = this.navigationState;
		if (this.navigationPending || !this.isNavigationActive() || position < 0 || position >= state.entries.length
				|| state.entries[position] === state.currentEntry) return;
		this.edgeSwipe.cancel();
		this.navigationPending = true;
		this.onNavigationRequested.fire({revision: state.revision, entry: state.entries[position]});
	}

	public destroy(): void {
		this.disposed = true;
		this.edgeSwipe.destroy();
		mobileNavigationHistory.release(this.historyOwner);
		super.destroy();
	}

	public showView(view: UiComponent, transition: UiPageTransition = null, animationDuration = 0) {
		if (view === this.content) {
			return;
		}

		this.edgeSwipe?.cancel();
		this.transitionUntil = Date.now() + animationDuration;
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
