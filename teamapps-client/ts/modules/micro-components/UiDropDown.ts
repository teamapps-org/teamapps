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
import {UiComponent} from "../UiComponent";
import {positionDropDown} from "../Common";
import {UiComponentConfig} from "../../generated/UiComponentConfig";
import {AbstractDropDown} from "./AbstractDropDown";

interface OpenConfig {
	$reference: JQuery | Element,
	width?: number,
	viewPortPadding?: number,
	minHeight?: number
}

export class UiDropDown extends AbstractDropDown<OpenConfig> {
	constructor(content?: UiComponent<UiComponentConfig>) {
		super(content);
		this.getMainDomElement().addClass("UiDropDown");
	}

	doOpen(options: OpenConfig) {
		options.width = options.width || 250;
		options.viewPortPadding = options.viewPortPadding || 5;
		options.minHeight = options.minHeight || 0;
		// this.$contentContainer.css("height", minHeight + "px");
		this.$contentContainer.find(">*").css("min-height", options.minHeight + "px");
		this.$dropDown.css("width", options.width + "px");
		this.$dropDown.appendTo(document.body);
		this.$dropDown.addClass('open');
		positionDropDown($(this.currentOpenConfig.$reference as any), this.$dropDown, this.currentOpenConfig);
	}

	protected doClose(): void {
		this.$dropDown.removeClass('open');
		this.$dropDown.detach();
	}
}
