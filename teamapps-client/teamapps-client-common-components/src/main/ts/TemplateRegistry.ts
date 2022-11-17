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
import {UiTemplate} from "./generated/UiTemplate";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {TeamAppsEvent} from "./util/TeamAppsEvent";
import {UiGridTemplate} from "./generated/UiGridTemplate";
import {createGridTemplateRenderer} from "./util/UiGridTemplates";
import {UiTemplateReference} from "./generated/UiTemplateReference";
import {Renderer} from "./Common";
import {createHtmlTemplateRenderer} from "./util/UiHtmlTemplates";
import {UiHtmlTemplate} from "./generated/UiHtmlTemplate";
import {createMustacheTemplateRenderer} from "./util/UiMustacheTemplates";
import {UiMustacheTemplate} from "./generated/UiMustacheTemplate";

export class TemplateRegistry {

	public static readonly NOOP_RENDERER: Renderer = {
		render: (o: any) => "<div></div>",
		template: {
			_type: "NO-OP"
		}
	};

	public readonly onTemplateRegistered: TeamAppsEvent<string> = new TeamAppsEvent<string>();

	private renderersByName: { [name: string]: Renderer } = {};

	constructor(private context: TeamAppsUiContext) {
	}

	public registerTemplate(name: string, template: UiTemplate): void {
		let oldRenderer = this.renderersByName[name];
		if (oldRenderer != null) {
			console.warn(`Template with name ${name} is already registered. Overwriting!`)
		}
		this.renderersByName[name] = this.createTemplateRenderer(template);
		this.onTemplateRegistered.fire(name);
	}

	public getTemplateRendererByName(name: string): Renderer {
		let renderer = this.renderersByName[name];
		if (renderer == null) {
			console.error(`TemplateRegistry: Cannot find template with name ${name}! Returning no-op renderer.`);
			return TemplateRegistry.NOOP_RENDERER;
		} else if (isTemplateReference(renderer.template)) {
			renderer = this.getTemplateRendererByName(renderer.template.templateId);
		}
		return renderer;
	}

	public getRegisteredTemplates(): { [name: string]: UiTemplate } {
		return Object.keys(this.renderersByName)
			.reduce((templatesByName, name) => {
				templatesByName[name] = this.renderersByName[name].template;
				return templatesByName;
			}, {} as { [name: string]: UiTemplate });
	}

	public createTemplateRenderer(template: UiTemplate, idPropertyName?: string): Renderer {
		if (isTemplateReference(template)) {
			return this.getTemplateRendererByName(template.templateId);
		} else if (isGridTemplate(template)) {
			return createGridTemplateRenderer(template, idPropertyName);
		} else if (isHtmlTemplate(template)) {
			return createHtmlTemplateRenderer(template, idPropertyName);
		} else if (isMustacheTemplate(template)) {
			return createMustacheTemplateRenderer(template, idPropertyName);
		}
	}

	public createTemplateRenderers(templates: { [name: string]: UiTemplate }, idPropertyName?: string): { [name: string]: Renderer } {
		return Object.keys(templates).reduce((templateStringMapObject, templateName) => {
			templateStringMapObject[templateName] = this.createTemplateRenderer(templates[templateName], idPropertyName);
			return templateStringMapObject;
		}, <{ [name: string]: Renderer }> {});
	}

}

export function isTemplateReference(template: UiTemplate): template is UiTemplateReference {
	return template._type === "UiTemplateReference";
}

export function isGridTemplate(template: UiTemplate): template is UiGridTemplate {
	return template._type === "UiGridTemplate";
}

export function isHtmlTemplate(template: UiTemplate): template is UiHtmlTemplate {
	return template._type === "UiHtmlTemplate";
}

export function isMustacheTemplate(template: UiTemplate): template is UiMustacheTemplate {
	return template._type === "UiMustacheTemplate";
}
