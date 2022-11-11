/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2022 TeamApps.org
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
import {AbstractUiComponent} from "./AbstractUiComponent";
import {UiComponentConfig} from "./generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiComponent} from "./UiComponent";

type ComponentClass<T extends UiComponent> = { new(config: UiComponentConfig, context: TeamAppsUiContext): T };

export class TeamAppsUiComponentRegistry {

	private static componentClasses: { [componentName: string]: ComponentClass<UiComponent> } = {};

	public static registerComponentClass<F extends UiComponent>(componentName: string, componentClass: ComponentClass<F>): void {
		this.componentClasses[componentName] = componentClass;
	}

	public static getComponentClassForName(componentName: string, logErrorIfNotFound = true): ComponentClass<UiComponent> {
		let componentClass = this.componentClasses[componentName];
		if (!componentClass && logErrorIfNotFound) {
			console.error("There is no registered component type with name: " + componentName);
		}
		return componentClass;
	}

}

if (!(window as any).TeamAppsUiComponentRegistry) {
	(window as any).TeamAppsUiComponentRegistry = TeamAppsUiComponentRegistry;
}
