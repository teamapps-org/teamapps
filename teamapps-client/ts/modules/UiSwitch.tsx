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
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import Switch, {SwitchProps} from "@material-ui/core/Switch";
import {UiSwitchCommandHandler, UiSwitchConfig, UiSwitchEventSource} from "../generated/UiSwitchConfig";
import withStyles from "@material-ui/core/styles/withStyles";
import {createUiColorCssString} from "./util/CssFormatUtil";
import createStyles from "@material-ui/core/styles/createStyles";
import {AbstractUiReactField} from "./AbstractUiReactField";

export class UiSwitch extends AbstractUiReactField<UiSwitchConfig, boolean> implements UiSwitchCommandHandler, UiSwitchEventSource {

	private CustomStyledSwitch: React.ComponentType<SwitchProps>;

	protected initialize(config: UiSwitchConfig, context: TeamAppsUiContext): void {
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
		super.initialize(config, context);
	}

	protected render() {
		return <this.CustomStyledSwitch
			checked={this.transientValue}
			onChange={(event, value) => {
				this.transientValue = value;
				this.commit();
				this.updateDom();
			}}
		/>
	}

	getDefaultValue(): boolean {
		return false;
	}

	isValidData(v: boolean): boolean {
		return v === true || v == false;
	}

	valuesChanged(v1: boolean, v2: boolean): boolean {
		return v1 !== v2;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiSwitch", UiSwitch);