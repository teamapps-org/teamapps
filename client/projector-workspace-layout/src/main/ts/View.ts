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
import {Panel, WindowButtonTypes} from "projector-client-core-components";
import {type DtoPanel_WindowButtonClickedEvent, type WindowButtonType} from "projector-client-core-components";
import {bind, type Component, ProjectorEvent} from "projector-client-object-api";
import {TabPanelItem} from "./TabPanelItem";
import {type ViewInfo} from "./ViewInfo";

export class View implements ViewInfo {

	public readonly onPanelWindowButtonClicked: ProjectorEvent<WindowButtonType> = new ProjectorEvent();

	private _parent: TabPanelItem;
	private _component: Component;

	public viewName: string;

	public tabIcon: string | null;

	public tabCaption: string | null;

	public tabCloseable: boolean;

	public lazyLoading: boolean;

	public visible: boolean;

	constructor(viewName: string,
	            tabIcon: string | null,
	            tabCaption: string | null,
	            tabCloseable: boolean,
	            lazyLoading: boolean,
	            visible: boolean,
	            component: Component) {
		this.viewName = viewName;
		this.tabIcon = tabIcon;
		this.tabCaption = tabCaption;
		this.tabCloseable = tabCloseable;
		this.lazyLoading = lazyLoading;
		this.visible = visible;
		this.component = component;
	}

	public get component() {
		return this._component;
	}

	public set component(component) {
		if (this._component instanceof Panel) {
			this._component.onWindowButtonClicked.removeListener(this.handlePanelWindowButtonClicked);
		}
		this._component = component;
		if (this._component instanceof Panel) {
			this._component.onWindowButtonClicked.addListener(this.handlePanelWindowButtonClicked);
		}
		if (this._parent) {
			this._parent.updateTab(this.viewName, this._component);
		}
		if (component != null && component instanceof Panel) {
			component.setDraggable(true);
		}
	}

	@bind
	private handlePanelWindowButtonClicked(event: DtoPanel_WindowButtonClickedEvent) {
		if (event.windowButton === WindowButtonTypes.MINIMIZE && this._component instanceof Panel) {
			this._component.restore(); // could be maximized, so first restore!
		}
		this.onPanelWindowButtonClicked.fire(event.windowButton);
	}

	updateTabAttributes(tabIcon: string, tabCaption: string, tabCloseable: boolean, visible: boolean) {
		this.tabIcon = tabIcon;
		this.tabCaption = tabCaption;
		this.tabCloseable = tabCloseable;
		this.visible = visible;
		this._parent.setTabConfiguration(this.viewName, tabIcon, tabCaption, tabCloseable, visible);
	}

	setVisible(visible: boolean): any {
		this.visible = visible;
		this.updateTabAttributes(this.tabIcon, this.tabCaption, this.tabCloseable, visible);
	}

	public get viewInfo(): ViewInfo {
		return {
			viewName: this.viewName,
			tabIcon: this.tabIcon,
			tabCaption: this.tabCaption,
			tabCloseable: this.tabCloseable,
			visible: this.visible,
			lazyLoading: this.lazyLoading
		};
	}

	setWindowButtons(toolButtons: WindowButtonType[]) {
		if (this.component instanceof Panel) {
			(this.component as Panel).setWindowButtons(toolButtons);
		}
	}


	get parent(): TabPanelItem {
		return this._parent;
	}

	set parent(value: TabPanelItem) {
		this._parent = value;
	}
}
