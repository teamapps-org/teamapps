/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import * as log from "loglevel";
import {UiComponent} from "./UiComponent";
import {UiField} from "./formfield/UiField";
import {UiFieldConfig} from "../generated/UiFieldConfig";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";

type ComponentClass<T extends UiComponent<UiComponentConfig>> = { new (config: UiComponentConfig, context: TeamAppsUiContext): T };
type FieldClass<T extends UiField> = { new (config: UiFieldConfig, context: TeamAppsUiContext): T };

export class TeamAppsUiComponentRegistry {

	private static logger: log.Logger = log.getLogger("TeamAppsUiComponentRegistry");

	private static componentClasses: { [componentName: string]: ComponentClass<UiComponent<UiComponentConfig>> } = {};
	private static fieldClasses: { [fieldName: string]: FieldClass<UiField> } = {};

	public static registerComponentClass<F extends UiComponent<UiComponentConfig>>(componentName: string, componentClass: ComponentClass<F>): void {
		this.componentClasses[componentName] = componentClass;
	}

	public static getComponentClassForName(componentName: string): ComponentClass<UiComponent<UiComponentConfig>> {
		let componentClass = this.componentClasses[componentName];
		if (!componentClass) {
			TeamAppsUiComponentRegistry.logger.error("There is no registered component type with name: " + componentName);
		}
		return componentClass;
	}


	public static registerFieldClass<F extends UiField>(fieldName: string, fieldClass: FieldClass<F>): void {
		this.fieldClasses[fieldName] = fieldClass;
		this.registerComponentClass(fieldName, fieldClass as any);
	}

	public static getFieldClassForName(fieldName: string): FieldClass<UiField> {
		let fieldClass = this.fieldClasses[fieldName];
		if (!fieldClass) {
			TeamAppsUiComponentRegistry.logger.error("There is no registered field type with name: " + fieldName);
		}
		return fieldClass;
	}

}

(window as any).TeamAppsUiComponentRegistry = TeamAppsUiComponentRegistry;
