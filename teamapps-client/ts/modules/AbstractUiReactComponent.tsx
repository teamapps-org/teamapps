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
import * as ReactDOM from "react-dom";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import Switch, {SwitchProps} from "@material-ui/core/Switch";
import {UiField} from "./formfield/UiField";
import {UiSwitchCommandHandler, UiSwitchConfig, UiSwitchEventSource} from "../generated/UiSwitchConfig";
import {UiFieldEditingMode} from "../generated/UiFieldEditingMode";
import withStyles from "@material-ui/core/styles/withStyles";
import {createUiColorCssString} from "./util/CssFormatUtil";
import createStyles from "@material-ui/core/styles/createStyles";
import {UiComponent} from "./UiComponent";
import {UiSwitch} from "./UiSwitch";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {ReactComponentLike} from "prop-types";
import {ReactElement, ReactInstance} from "react";

export abstract class AbstractUiReactComponent<C extends UiComponentConfig = UiComponentConfig> extends UiComponent<C> {

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

	getMainDomElement(): HTMLElement {
		return this.$main;
	}
}