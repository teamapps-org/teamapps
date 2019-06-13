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

import {Radio} from "@material-ui/core";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import RadioGroup from "@material-ui/core/RadioGroup";
import {UiField} from "./formfield/UiField";
import {UiRadioGroupCommandHandler, UiRadioGroupConfig, UiRadioGroupEventSource} from "../generated/UiRadioGroupConfig";
import {UiRadioButtonConfig} from "../generated/UiRadioButtonConfig";
import {UiFieldEditingMode} from "../generated/UiFieldEditingMode";

export class UiRadioGroup extends UiField<UiRadioGroupConfig, string> implements UiRadioGroupCommandHandler, UiRadioGroupEventSource {

	private $main: HTMLElement;

	private transientValue: string;

	constructor(config: UiRadioGroupConfig, context: TeamAppsUiContext) {
		super(config, context);
		this.transientValue = config.value || '';
	}

	protected initialize(config: UiRadioGroupConfig, context: TeamAppsUiContext): void {
		let htmlDivElement = document.createElement('div');
		htmlDivElement.classList.add('UiRadioGroup');
		this.$main = htmlDivElement;
		this.render();
	}

	private render() {
		let radioGroup = <RadioGroup
			value={this.transientValue}
			onChange={(event, value) => {
				this.transientValue = value;
				this.commit();
				this.render();
			}}>
			{this._config.radioButtons.map(button => this.renderRadioButton(button, !this.isEditable()))}
		</RadioGroup>;
		return ReactDOM.render(
			radioGroup,
			this.$main
		);
	}

	private renderRadioButton(button: UiRadioButtonConfig, disabled: boolean) {
		return <FormControlLabel key={button.value} value={button.value} control={<Radio/>} label={button.label} disabled={disabled}/>;
	}

	addButton(button: UiRadioButtonConfig): void {
		this._config.radioButtons = [...this._config.radioButtons, button];
		this.render();
	}

	removeButton(value: string): void {
		this._config.radioButtons = this._config.radioButtons.filter(b => b.value === value);
		this.render();
	}

	protected displayCommittedValue(): void {
		this.transientValue = this.getCommittedValue();
		this.render();
	}

	getFocusableElement(): HTMLElement {
		return this.getMainInnerDomElement();
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	getTransientValue(): string {
		return this.transientValue;
	}

	isValidData(v: String): boolean {
		return true;
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		this.render();
	}

	valuesChanged(v1: String, v2: String): boolean {
		return v1 !== v2;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiReactTestComponent", UiRadioGroup);