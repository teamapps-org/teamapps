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
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiComponent} from "../UiComponent";
import {UiComponentConfig} from "../../generated/UiComponentConfig";
import {ClickOutsideHandle, doOnceOnClickOutsideElement, positionDropDown} from "../Common";
import {UiSpinner} from "./UiSpinner";

export abstract class AbstractDropDown<OPEN_CONFIG> {
	public onClose: TeamAppsEvent<void> = new TeamAppsEvent(this);
	public onComponentRemoved: TeamAppsEvent<UiComponent> = new TeamAppsEvent(this);

	protected $dropDown: JQuery;
	protected $contentContainer: JQuery;
	protected clickOutsideHandle: ClickOutsideHandle;
	protected spinner = new UiSpinner({fixedSize: "25%"});
	protected currentOpenConfig: OPEN_CONFIG;
	private _isOpen = false;
	private _contentComponent: UiComponent<UiComponentConfig>;

	constructor(content?: UiComponent<UiComponentConfig>) {
		this.$dropDown = $(`<div class="DropDown teamapps-blurredBackgroundImage">
                <div class="background-color-div">
              </div>`);
		this.$contentContainer = this.$dropDown.find('.background-color-div');

		if (content) {
			this.setContentComponent(content);
		}
	}

	setContentComponent(component: UiComponent<UiComponentConfig>) {
		this.$contentContainer[0].innerHTML = '';
		if (this._contentComponent && this.onComponentRemoved) {
			this.onComponentRemoved.fire(this._contentComponent);
		}

		this._contentComponent = component;

		if (component != null) {
			this.$contentContainer.append(component.getMainDomElement())
		}

		if (this._isOpen) {
			this.open(this.currentOpenConfig);
		}
	}

	getContentComponent(): UiComponent<UiComponentConfig> {
		return this._contentComponent;
	}

	open(config: OPEN_CONFIG) {
		this.currentOpenConfig = config;

		if (this._contentComponent == null) {
			this.$contentContainer[0].innerHTML = '';
			this.$contentContainer.append(this.spinner.getMainDomElement());
		}

		if (!this._isOpen) {

			this.doOpen(config);

			this._isOpen = true;
			this.clickOutsideHandle = doOnceOnClickOutsideElement(this.$dropDown, () => {
				if (this._isOpen) {
					this.close();
				}
			});
		}

		if (this._contentComponent != null) {
			this._contentComponent.attachedToDom = true;
		}
	}

	protected abstract doOpen(config: OPEN_CONFIG): void;

	close() {
		if (this.clickOutsideHandle != null) {
			this.clickOutsideHandle.cancel();
		}
		if (this._isOpen) {
			this.doClose();
			this.onClose && this.onClose.fire(null);
		}
		this._isOpen = false;
	}

	protected abstract doClose(): void;

	get isOpen() {
		return this._isOpen;
	}

	public getMainDomElement(): JQuery {
		return this.$dropDown;
	}

	public getScrollContainer(): JQuery {
		return this.$contentContainer;
	}

}
