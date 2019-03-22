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
import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {UiToolbarConfig} from "../generated/UiToolbarConfig";
import {UiSplitPaneConfig} from "../generated/UiSplitPaneConfig";
import {UiSplitPane} from "./UiSplitPane";
import {UiComponent} from "./UiComponent";
import {UiApplicationLayoutConfig} from "../generated/UiApplicationLayoutConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";

export class UiApplicationLayout extends UiComponent<UiApplicationLayoutConfig> {
	private $mainDiv: JQuery;
	private _toolbar: UiToolbar;
	private _rootSplitPane: UiSplitPane;

	private _$toolbarContainer: JQuery;
	private _$contentContainer: JQuery;

	constructor(config: UiApplicationLayoutConfig,
	            context: TeamAppsUiContext) {
		super(config, context);
		this.$mainDiv = $('<div id="' + config.id + '" class="UiApplicationLayout"></div>');

		this._$toolbarContainer = $('<div class="UiApplicationLayout_toolbarContainer"></div>').appendTo(this.$mainDiv);
		this.setToolbar(config.toolbar);

		var $contentContainerWrapper = $('<div class="UiApplicationLayout_contentContainerWrapper"></div>').appendTo(this.$mainDiv);
		this._$contentContainer = $('<div class="UiApplicationLayout_contentContainer"></div>').appendTo($contentContainerWrapper);
		this.setRootSplitPane(config.rootSplitPane);
	}

	public onResize(): void {
		this._toolbar && this._toolbar.reLayout();
		this._rootSplitPane && this._rootSplitPane.reLayout();
	}

	public setToolbar(toolbar: UiToolbar): void {
		if (this._toolbar) {
			this._$toolbarContainer[0].innerHTML = '';
		}
		this._toolbar = toolbar;
		this._$toolbarContainer.toggleClass('hidden', !toolbar);
		if (toolbar) {
			this._toolbar.getMainDomElement().appendTo(this._$toolbarContainer);
			this._toolbar.attachedToDom = this.attachedToDom;
		}
	}

	public setRootSplitPane(splitPane: UiSplitPane): void {
		if (this._rootSplitPane) {
			this._$contentContainer[0].innerHTML = '';
			this._rootSplitPane = null;
		}
		if (splitPane) {
			this._rootSplitPane = splitPane;
			this._rootSplitPane.getMainDomElement().appendTo(this._$contentContainer);
			this._rootSplitPane.attachedToDom = this.attachedToDom;
		}
	}

	public getMainDomElement(): JQuery {
		return this.$mainDiv;
	}


	protected onAttachedToDom() {
		if (this._toolbar) this._toolbar.attachedToDom = true;
		if (this._rootSplitPane) this._rootSplitPane.attachedToDom = true;
	}

	public destroy(): void {
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiApplicationLayout", UiApplicationLayout);
