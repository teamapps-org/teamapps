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

import * as React from "react";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";

import {Radio} from "@material-ui/core";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import RadioGroup from "@material-ui/core/RadioGroup";
import {UiRadioGroupConfig} from "../generated/UiRadioGroupConfig";
import {UiRadioButtonConfig} from "../generated/UiRadioButtonConfig";
import {AbstractUiReactField} from "./AbstractUiReactField";

export class UiRadioGroup extends AbstractUiReactField<UiRadioGroupConfig, string> {

	protected render() {
		return <RadioGroup
			value={this.transientValue || ""}
			onChange={(event, value) => {
				this.transientValue = value;
				this.commit();
				this.updateDom();
			}}>
			{this._config.radioButtons.map(button => this.renderRadioButton(button, !this.isEditable()))}
		</RadioGroup>;
	}

	private renderRadioButton(button: UiRadioButtonConfig, disabled: boolean) {
		return <FormControlLabel key={button.value} value={button.value} control={<Radio/>} label={button.label} disabled={disabled}/>;
	}

	addButton(button: UiRadioButtonConfig): void {
		this._config.radioButtons = [...this._config.radioButtons, button];
		this.updateDom();
	}

	removeButton(value: string): void {
		this._config.radioButtons = this._config.radioButtons.filter(b => b.value === value);
		this.updateDom();
	}

	isValidData(v: String): boolean {
		return true;
	}

	valuesChanged(v1: String, v2: String): boolean {
		return v1 !== v2;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiRadioGroup", UiRadioGroup);