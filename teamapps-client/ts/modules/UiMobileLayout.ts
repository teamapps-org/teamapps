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
import * as $ from "jquery";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiToolbarConfig} from "../generated/UiToolbarConfig";
import {UiNavigationBar} from "./UiNavigationBar";
import {UiNavigationBarConfig} from "../generated/UiNavigationBarConfig";
import {UiComponent} from "./UiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiMobileLayoutCommandHandler, UiMobileLayoutConfig} from "../generated/UiMobileLayoutConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiMobileLayoutAnimation} from "../generated/UiMobileLayoutAnimation";

class View {
	component: UiComponent<UiComponentConfig>;
	$container: JQuery;
}

export class UiMobileLayout extends UiComponent<UiMobileLayoutConfig> implements UiMobileLayoutCommandHandler {

	private $mainDiv: JQuery;
	private $toolbarContainer: JQuery;
	private $contentContainerWrapper: JQuery;
	private $navBarContainer: JQuery;

	private toolbar: UiToolbar;
	private navBar: UiNavigationBar;
	private views: { [id: string]: View } = {};
	private currentView: View;

	constructor(config: UiMobileLayoutConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.$mainDiv = $(`<div id="${config.id}" class="UiMobileLayout">
                             <div class="toolbar-container"></div>
                             <div class="content-container-wrapper"></div>
                             <div class="navigation-bar-container"></div>
            </div>`);

		this.$toolbarContainer = this.$mainDiv.find('>.toolbar-container');
		this.$contentContainerWrapper = this.$mainDiv.find('>.content-container-wrapper');
		this.$navBarContainer = this.$mainDiv.find('>.navigation-bar-container');

		this.setToolbar(config.toolbar);
		this.setNavigationBar(config.navigationBar);

		if (config.views) {
			config.views.forEach(v => this.addView(v));
		}

		if (config.initialViewId) {
			this.showView(config.initialViewId, null);
		}
	}

	public addView(viewComponent: UiComponent) {
		var $container = $(`<div class="content-container">`);
		this.$contentContainerWrapper.append($container);
		viewComponent.getMainDomElement().appendTo($container);
		this.views[viewComponent.getId()] = {
			component: viewComponent,
			$container: $container
		};
		this.resizeChildren();
		viewComponent.attachedToDom = this.attachedToDom;
	}

	public removeView(viewId: string) {
		let view = this.views[viewId];
		view.$container.detach();
		delete this.views[viewId];
	}

	public showView(viewId: string, animationType: UiMobileLayoutAnimation) {
		var newView = this.views[viewId];

		if (newView == null) {
			this.logger.warn("View with id " + viewId + " not registered!");
			return;
		}
		if (newView === this.currentView) {
			return;
		}

		if (animationType == UiMobileLayoutAnimation.FORWARD) {
			this.doViewTransition(newView, "forward-offsite", "backward-offsite");
		} else if (animationType == UiMobileLayoutAnimation.BACKWARD) {
			this.doViewTransition(newView, "backward-offsite", "forward-offsite");
		} else {
			newView.$container.addClass("active");
			this.currentView && this.currentView.$container.removeClass("active");
		}

		newView.component.attachedToDom = true;
		newView.component.reLayout();

		this.currentView = newView;
	}

	private doViewTransition(newView: View, inwardStyle: string, outwardStyle: string) {
		newView.$container.addClass(inwardStyle + " active")[0].offsetWidth;
		this.$mainDiv.addClass("transitions")[0].offsetWidth;
		this.currentView && this.currentView.$container.addClass(outwardStyle);
		newView.$container.removeClass(inwardStyle);

		let currentViewLocalVar = this.currentView;
		setTimeout(() => {
			currentViewLocalVar && currentViewLocalVar.$container.removeClass("active " + outwardStyle);
			this.$mainDiv.removeClass("transitions");
		}, 500);
	};

	public onResize(): void {
		this.toolbar && this.toolbar.reLayout();
		this.resizeChildren();
		this.navBar && this.navBar.reLayout(true);
	}

	private resizeChildren() {
		let dimensionCss = this.$contentContainerWrapper.css(["width", "height"]);
		Object.keys(this.views).forEach(viewId => {
			let view = this.views[viewId];
			view.$container.css(dimensionCss);
			view.component.reLayout();
		});
	}

	public setToolbar(toolbar: UiToolbar): void {
		if (this.toolbar) {
			this.$toolbarContainer[0].innerHTML = '';
		}
		this.toolbar = toolbar;
		this.$toolbarContainer.toggleClass('hidden', !toolbar);
		if (toolbar) {
			this.toolbar.getMainDomElement().appendTo(this.$toolbarContainer);
			this.toolbar.attachedToDom = this.attachedToDom;
		}
	}

	public setNavigationBar(navBar: UiNavigationBar) {
		if (this.navBar) {
			this.$navBarContainer[0].innerHTML = '';
		}
		this.navBar = navBar;
		this.$navBarContainer.toggleClass('hidden', !navBar);
		if (navBar) {
			this.navBar.getMainDomElement().appendTo(this.$navBarContainer);
			this.navBar.attachedToDom = this.attachedToDom;
		}
	}

	public getMainDomElement(): JQuery {
		return this.$mainDiv;
	}

	protected onAttachedToDom() {
		if (this.toolbar) this.toolbar.attachedToDom = true;
		if (this.navBar) this.navBar.attachedToDom = true;
		if (this.currentView) this.currentView.component.attachedToDom = true;
	}

	public destroy(): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiMobileLayout", UiMobileLayout);
