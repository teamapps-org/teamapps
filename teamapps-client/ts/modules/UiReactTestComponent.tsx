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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiReactTestComponentConfig} from "../generated/UiReactTestComponentConfig";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";

import {Radio} from "@material-ui/core";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import RadioGroup from "@material-ui/core/RadioGroup";


export class UiReactTestComponent extends AbstractUiComponent<UiReactTestComponentConfig> {

	private $main: HTMLElement;

	constructor(config: UiComponentConfig, context: TeamAppsUiContext) {
		super(config, context);
		let htmlDivElement = document.createElement('div');
		htmlDivElement.classList.add('UiReactTestComponent');
		this.$main = htmlDivElement;
		let classList = ``;
		ReactDOM.render(
				<RadioGroup onChange={(event, value) => console.log(event, value)}>
					<FormControlLabel value="female" control={<Radio />} label="Female"/>
					<FormControlLabel value="male" control={<Radio />} label="Male" />
					<FormControlLabel value="other" control={<Radio />} label="Other" />
					<FormControlLabel value="disabled" disabled control={<Radio />} label="(Disabled option)"/>
				</RadioGroup>,
			htmlDivElement
		);
	}

	destroy(): void {
	}

	getMainDomElement(): HTMLElement {
		return this.$main;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiReactTestComponent", UiReactTestComponent);