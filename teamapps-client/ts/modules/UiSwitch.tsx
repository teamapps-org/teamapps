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

export class UiSwitch extends UiField<UiSwitchConfig, boolean> implements UiSwitchCommandHandler, UiSwitchEventSource {

	private $main: HTMLElement;

	private v: { value: boolean } = {value: this.getDefaultValue()};

	private CustomStyledSwitch: React.ComponentType<SwitchProps>;

	constructor(config: UiSwitchConfig, context: TeamAppsUiContext) {
		super(config, context);
	}

	protected initialize(config: UiSwitchConfig, context: TeamAppsUiContext): void {
		this.v = {value: config.value || false};
		let htmlDivElement = document.createElement('div');
		htmlDivElement.classList.add('UiSwitch');
		this.$main = htmlDivElement;

		this.CustomStyledSwitch = withStyles(createStyles({
			switchBase: {
				color: createUiColorCssString(this._config.uncheckedButtonColor),
				'&$checked': {
					color: createUiColorCssString(this._config.checkedButtonColor)
				},
				'& + $track': {
					backgroundColor: createUiColorCssString(this._config.uncheckedTrackColor)
				},
				'&$checked + $track': {
					backgroundColor: createUiColorCssString(this._config.checkedTrackColor)
				},
			},
			checked: {},
			track: {}
		}))(Switch);

		this.render();
	}

	private render() {
		return ReactDOM.render(
			<this.CustomStyledSwitch
				checked={this.v.value}
				onChange={(event, value) => {
					this.v.value = value;
					this.commit();
					this.render();
				}}
			/>,
			this.$main
		);
	}

	getDefaultValue(): boolean {
		return false;
	}

	protected displayCommittedValue(): void {
		this.v.value = this.getCommittedValue();
		this.render();
	}

	getFocusableElement(): HTMLElement {
		return this.getMainInnerDomElement();
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	getTransientValue(): boolean {
		return this.v.value;
	}

	isValidData(v: boolean): boolean {
		return v === true || v == false;
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		this.render();
	}

	valuesChanged(v1: boolean, v2: boolean): boolean {
		return v1 !== v2;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiReactTestComponent", UiSwitch);