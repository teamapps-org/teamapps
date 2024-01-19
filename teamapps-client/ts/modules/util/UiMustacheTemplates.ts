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
import {Renderer} from "../Common";
import {UiMustacheTemplateConfig} from "../../generated/UiMustacheTemplateConfig";
import Mustache = require("mustache");

export function createMustacheTemplateRenderer(template: UiMustacheTemplateConfig, idPropertyName: string): Renderer {
	Mustache.parse(template.templateString);

	return {
		render: (data: any) => {
			if (data == null) {
				return '';
			} else {
				return Mustache.render(template.templateString, data);
			}
		},
		template
	};
}
