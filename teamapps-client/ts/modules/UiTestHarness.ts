/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import {UiConfigurationConfig} from "../generated/UiConfigurationConfig";
import {TemplateRegistry} from "./TemplateRegistry";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiRadioGroup} from "./UiRadioGroup";
import {UiFieldEditingMode} from "../generated/UiFieldEditingMode";
import {UiSwitch} from "./UiSwitch";
import {createUiColorConfig} from "../generated/UiColorConfig";
import {UiPictureChooser} from "./formfield/file/UiPictureChooser";
import {UiFieldGroup} from "./formfield/UiFieldGroup";
import {UiTextField} from "./formfield/UiTextField";
import {UiButton} from "./formfield/UiButton";
import {UiComponent} from "./UiComponent";


export class UiTestHarness {

	constructor() {
		let context = new TestTeamAppsUiContext();

		// let component = this.createRadioGroup();
		// let component = this.createRadioGroup();
		// let component = this.createUiPictureChooser();

		let component = new UiFieldGroup({
			id: "asdf",
			fields: [
				new UiTextField({stylesBySelector: {"": {flex: "1 1 auto"}}}, context),
				new UiButton({template: null, templateRecord: null}, context)
			]
		}, context);

		(window as any).c = component;
		document.body.appendChild(component.getMainElement());
	}

	private createRadioGroup() {
		let component = new UiRadioGroup({
			id: "asdf",
			radioButtons: [{
				label: "AAA",
				value: "aaa"
			}, {
				label: "BBB",
				value: "bbb"
			}, {
				label: "CCC",
				value: "ccc"
			}],
			editingMode: UiFieldEditingMode.EDITABLE_IF_FOCUSED
		}, new TestTeamAppsUiContext());
		component.onValueChanged.addListener(eventObject => console.log("Value changed: " + eventObject.value));
		return component;
	}

	private createSwitch() {
		let component = new UiSwitch({
			id: "asdf",
			value: false,
			editingMode: UiFieldEditingMode.EDITABLE_IF_FOCUSED,
			uncheckedTrackColor: createUiColorConfig(0, 255, 0),
			checkedTrackColor: createUiColorConfig(0, 0, 255),
			uncheckedButtonColor: createUiColorConfig(0, 0, 0),
			checkedButtonColor: createUiColorConfig(255, 255, 255)
		}, new TestTeamAppsUiContext());
		component.onValueChanged.addListener(eventObject => console.log("Value changed: " + eventObject.value));
		return component;
	}

	private createUiPictureChooser() {
		let uiPictureChooser = new UiPictureChooser({
			id: "asdf",
			value: null,
			uploadUrl: "/upload"
		}, new TestTeamAppsUiContext());
		uiPictureChooser.onValueChanged.addListener(eventObject => console.log("Value changed: " + eventObject.value));
		return uiPictureChooser;
	}
}

class TestTeamAppsUiContext implements TeamAppsUiContext {
	readonly sessionId: string = "1234567890";
	readonly isHighDensityScreen: boolean = false;
	readonly executingCommand: boolean = false;
	readonly config: UiConfigurationConfig = {};
	readonly templateRegistry: TemplateRegistry = new TemplateRegistry(this);

	getClientObjectById(id: string): UiComponent<UiComponentConfig> {
		return null;
	}
}
