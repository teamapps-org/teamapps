/*
 * Copyright (c) 2018 teamapps.org (see code comments for author's name)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "@less/components/UiReactTestComponent.less";

import * as React from "react";
import {ReactElement} from "react";
import * as ReactDOM from "react-dom";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiComponentConfig} from "../generated/UiComponentConfig";

export abstract class AbstractUiReactComponent<C extends UiComponentConfig = UiComponentConfig> extends AbstractUiComponent<C> {

	private $main: HTMLElement;

	constructor(config: C, context: TeamAppsUiContext) {
		super(config, context);
		let htmlDivElement = document.createElement('div');
		htmlDivElement.classList.add((this.constructor as any).name || this.constructor.toString().match(/\w+/g)[1]);
		this.$main = htmlDivElement;
		this.updateDom();
	}

	protected updateDom() {
		return ReactDOM.render(this.render(), this.$main);
	}

	protected abstract render(): ReactElement;

	doGetMainElement(): HTMLElement {
		return this.$main;
	}
}