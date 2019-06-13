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
import {ReactElement} from "react";
import {UiFieldConfig} from "../generated/UiFieldConfig";

export abstract class AbstractUiReactField<C extends UiFieldConfig = UiFieldConfig, V = any> extends UiField<C, V> {

	private $main: HTMLElement;
	protected transientValue: V;

	constructor(config: C, context: TeamAppsUiContext) {
		super(config, context);
	}

	protected initialize(config: C, context: TeamAppsUiContext): void {
		this.transientValue = config.value || this.getDefaultValue();
		let htmlDivElement = document.createElement('div');
		htmlDivElement.classList.add((this.constructor as any).name || this.constructor.toString().match(/\w+/g)[1]);
		this.$main = htmlDivElement;
		this.updateDom();
	}

	protected updateDom() {
		return ReactDOM.render(this.render(), this.$main);
	}

	protected abstract render(): ReactElement;

	protected displayCommittedValue(): void {
		this.transientValue = this.getCommittedValue();
		this.updateDom();
	}

	getFocusableElement(): HTMLElement {
		return this.getMainInnerDomElement();
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	getTransientValue(): V {
		return this.transientValue;
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		this.updateDom();
	}

}
