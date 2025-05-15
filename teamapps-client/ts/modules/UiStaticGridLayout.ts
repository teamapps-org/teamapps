/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiStaticGridLayoutCommandHandler, UiStaticGridLayoutConfig} from "../generated/UiStaticGridLayoutConfig";
import {UiGridLayout} from "./micro-components/UiGridLayout";
import {UiGridLayoutConfig} from "../generated/UiGridLayoutConfig";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiResponsiveGridLayout} from "./UiResponsiveGridLayout";
import {parseHtml} from "./Common";

export class UiStaticGridLayout extends AbstractUiComponent<UiStaticGridLayoutConfig> implements UiStaticGridLayoutCommandHandler{

	private $main: HTMLElement;
	private layout: UiGridLayout;

	constructor(config: UiStaticGridLayoutConfig,
	            context: TeamAppsUiContext) {
		super(config, context);
		this.$main = parseHtml(`<div class="UiStaticGridLayout"></div>`);
		this.updateLayout(config.descriptor);
	}

	doGetMainElement(): HTMLElement {
		return this.$main;
	}

	updateLayout(descriptor: UiGridLayoutConfig): void {
		this.layout = new UiGridLayout(descriptor);
		this.layout.applyTo(this.$main);
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiStaticGridLayout", UiStaticGridLayout);
