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

import {ClickOutsideHandle, doOnceOnClickOutsideElement} from "../Common";
import {Component, parseHtml, TeamAppsEvent} from "projector-client-object-api";
import {Spinner} from "./Spinner";
import {positionDropdownWithAutoUpdate} from "../util/dropdownPosition";

interface OpenConfig {
	$reference: HTMLElement | Element,
	width?: number,
	viewPortPadding?: number,
	minHeight?: number
}

export class DropDown {

	public onClose: TeamAppsEvent<void> = new TeamAppsEvent();

	protected $dropDown: HTMLElement;
	protected $contentContainer: HTMLElement;
	protected clickOutsideHandle: ClickOutsideHandle;
	protected spinner = new Spinner({fixedSize: "25%"});
	protected currentOpenConfig: OpenConfig;
	private _isOpen = false;
	private $contentElement: HTMLElement;
	private cleanupPositionAutoUpdate: () => void = () => {};

	constructor(contentElement?: HTMLElement) {
		this.$dropDown = parseHtml(`<div class="DropDown teamapps-blurredBackgroundImage">
                <div class="background-color-div"></div>
              </div>`);
		this.$contentContainer = this.$dropDown.querySelector<HTMLElement>(':scope .background-color-div');

		if (contentElement) {
			this.setContentComponent(contentElement);
		}
	}

	setContentComponent(element: HTMLElement) {
		this.$contentContainer.innerHTML = '';

		this.$contentElement = element;

		if (element != null) {
			this.$contentContainer.append(element)
		}

		if (this._isOpen) {
			this.open(this.currentOpenConfig);
		}
	}

	open(config: OpenConfig) {
		config.width = config.width || 250;
		config.viewPortPadding = config.viewPortPadding || 5;
		config.minHeight = config.minHeight || 0;

		this.currentOpenConfig = config;

		if (this.$contentElement == null) {
			this.$contentContainer.innerHTML = '';
			this.$contentContainer.appendChild(this.spinner.getMainDomElement());
		}

		if (!this._isOpen) {
			this.$contentContainer.querySelector<HTMLElement>(":scope >*")
				.style.minHeight = config.minHeight + "px";
			this.$dropDown.style.width = config.width + "px";
			document.body.appendChild(this.$dropDown);
			this.$dropDown.classList.add('open');

			this.cleanupPositionAutoUpdate = positionDropdownWithAutoUpdate(this.currentOpenConfig.$reference, this.$dropDown);

			this._isOpen = true;
			this.clickOutsideHandle = doOnceOnClickOutsideElement(this.$dropDown, () => {
				if (this._isOpen) {
					this.close();
				}
			});
		}
	}

	close() {
		if (this.clickOutsideHandle != null) {
			this.clickOutsideHandle.cancel();
		}
		if (this._isOpen) {
			this.$dropDown.classList.remove('open');
			this.$dropDown.remove();
			this.onClose && this.onClose.fire(null);
			this.cleanupPositionAutoUpdate();
		}
		this._isOpen = false;
	}

	get isOpen() {
		return this._isOpen;
	}

	public getMainDomElement(): HTMLElement {
		return this.$dropDown;
	}

}
