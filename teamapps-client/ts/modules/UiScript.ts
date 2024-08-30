/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsUiComponentRegistry} from "./TeamAppsUiComponentRegistry";
import {UiScriptCommandHandler, UiScriptConfig} from "../generated/UiScriptConfig";

export class UiScript extends AbstractUiComponent<UiScriptConfig> implements UiScriptCommandHandler {
	private scriptElement: HTMLScriptElement;

	private modulePromise: Promise<any>;

	constructor(config: UiScriptConfig, context: TeamAppsUiContext) {
		super(config, context);

		this.scriptElement = document.createElement('script');
		this.scriptElement.type = 'module';
		this.scriptElement.innerText = config.script;
		const someExistingScriptElement = document.getElementsByTagName('script')[0];
		someExistingScriptElement.parentNode.insertBefore(this.scriptElement, someExistingScriptElement);

		this.modulePromise = loadModuleFromString(config.script);
	}

	protected doGetMainElement(): HTMLElement {
		throw new Error("Method not implemented.");
	}

	async callFunction(name: string, parameters: any[]) {
		((await this.modulePromise)[name] as Function).apply(null, parameters)
	}

}

async function loadModuleFromString(moduleCode: string) {
	const url = URL.createObjectURL(new Blob([moduleCode], {type: 'application/javascript'}));
	try {
		return await eval(`import("${url}")`); // needed so webpack does not try to be intelligent...
	} finally {
		URL.revokeObjectURL(url);
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiScript", UiScript);
