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

import {UiComponent} from "../UiComponent";
import {positionDropDown} from "../Common";
import {UiComponentConfig} from "../../generated/UiComponentConfig";
import {AbstractDropDown} from "./AbstractDropDown";

interface OpenConfig {
	$reference: HTMLElement | Element,
	width?: number,
	viewPortPadding?: number,
	minHeight?: number
}

export class UiDropDown extends AbstractDropDown<OpenConfig> {
	constructor(content?: UiComponent<UiComponentConfig>) {
		super(content);
		this.getMainDomElement().classList.add("UiDropDown");
	}

	doOpen(options: OpenConfig) {
		options.width = options.width || 250;
		options.viewPortPadding = options.viewPortPadding || 5;
		options.minHeight = options.minHeight || 0;
		// this.$contentContainer.style.height = minHeight + "px";
		this.$contentContainer.querySelector<HTMLElement>(":scope >*")
			.style.minHeight = options.minHeight + "px";
		this.$dropDown.style.width = options.width + "px";
		document.body.appendChild(this.$dropDown);
		this.$dropDown.classList.add('open');
		positionDropDown(this.currentOpenConfig.$reference, this.$dropDown, this.currentOpenConfig);
	}

	protected doClose(): void {
		this.$dropDown.classList.remove('open');
		this.$dropDown.remove();
	}
}
