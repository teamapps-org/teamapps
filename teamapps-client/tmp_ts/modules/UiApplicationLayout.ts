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

import {UiToolbar} from "./tool-container/toolbar/UiToolbar";
import {DtoToolbar} from "../generated/DtoToolbar";
import {DtoSplitPane} from "../generated/DtoSplitPane";
import {UiSplitPane} from "./UiSplitPane";
import {AbstractComponent} from "teamapps-client-core";
import {DtoApplicationLayout} from "../generated/DtoApplicationLayout";
import {TeamAppsUiContext} from "teamapps-client-core";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {parseHtml} from "./Common";

export class UiApplicationLayout extends AbstractComponent<DtoApplicationLayout> {
	private $mainDiv: HTMLElement;
	private _toolbar: UiToolbar;
	private _rootSplitPane: UiSplitPane;

	private _$toolbarContainer: HTMLElement;
	private _$contentContainer: HTMLElement;

	constructor(config: DtoApplicationLayout,
	            context: TeamAppsUiContext) {
		super(config, context);
		this.$mainDiv = parseHtml('<div id="' + config.id + '" class="UiApplicationLayout"></div>');

		this._$toolbarContainer = parseHtml('<div class="UiApplicationLayout_toolbarContainer"></div>');
		this.$mainDiv.appendChild(this._$toolbarContainer);
		this.setToolbar(config.toolbar as UiToolbar);

		var $contentContainerWrapper = parseHtml('<div class="UiApplicationLayout_contentContainerWrapper"></div>');
		this.$mainDiv.appendChild($contentContainerWrapper);
		this._$contentContainer = parseHtml('<div class="UiApplicationLayout_contentContainer"></div>');
		$contentContainerWrapper.appendChild(this._$contentContainer);
		this.setRootSplitPane(config.rootSplitPane as UiSplitPane);
	}

	public setToolbar(toolbar: UiToolbar): void {
		if (this._toolbar) {
			this._$toolbarContainer.innerHTML = '';
		}
		this._toolbar = toolbar;
		this._$toolbarContainer.classList.toggle('hidden', !toolbar);
		if (toolbar) {
			this._$toolbarContainer.appendChild(this._toolbar.getMainElement());
		}
	}

	public setRootSplitPane(splitPane: UiSplitPane): void {
		if (this._rootSplitPane) {
			this._$contentContainer.innerHTML = '';
			this._rootSplitPane = null;
		}
		if (splitPane) {
			this._rootSplitPane = splitPane;
			this._$contentContainer.appendChild(this._rootSplitPane.getMainElement());
		}
	}

	public doGetMainElement(): HTMLElement {
		return this.$mainDiv;
	}

}


