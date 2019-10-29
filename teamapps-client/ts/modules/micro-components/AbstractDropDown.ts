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
import {UiComponentConfig} from "../../generated/UiComponentConfig";
import {ClickOutsideHandle, doOnceOnClickOutsideElement, parseHtml} from "../Common";
import {UiSpinner} from "./UiSpinner";
import {UiComponent} from "../UiComponent";

export abstract class AbstractDropDown<OPEN_CONFIG> {
	public onClose: TeamAppsEvent<void> = new TeamAppsEvent(this);
	public onComponentRemoved: TeamAppsEvent<UiComponent> = new TeamAppsEvent(this);

	protected $dropDown: HTMLElement;
	protected $contentContainer: HTMLElement;
	protected clickOutsideHandle: ClickOutsideHandle;
	protected spinner = new UiSpinner({fixedSize: "25%"});
	protected currentOpenConfig: OPEN_CONFIG;
	private _isOpen = false;
	private _contentComponent: UiComponent<UiComponentConfig>;

	constructor(content?: UiComponent<UiComponentConfig>) {
		this.$dropDown = parseHtml(`<div class="DropDown teamapps-blurredBackgroundImage">
                <div class="background-color-div"></div>
              </div>`);
		this.$contentContainer = this.$dropDown.querySelector<HTMLElement>(':scope .background-color-div');

		if (content) {
			this.setContentComponent(content);
		}
	}

	setContentComponent(component: UiComponent<UiComponentConfig>) {
		this.$contentContainer.innerHTML = '';
		if (this._contentComponent && this.onComponentRemoved) {
			this.onComponentRemoved.fire(this._contentComponent);
		}

		this._contentComponent = component;

		if (component != null) {
			this.$contentContainer.append(component.getMainElement())
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
			this.$contentContainer.innerHTML = '';
			this.$contentContainer.appendChild(this.spinner.getMainDomElement());
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

	public getMainDomElement(): HTMLElement {
		return this.$dropDown;
	}

	public getScrollContainer(): HTMLElement {
		return this.$contentContainer;
	}

}
