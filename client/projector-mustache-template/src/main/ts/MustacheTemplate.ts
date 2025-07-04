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

import {type DtoMustacheTemplate} from "./generated";
import {default as mustache} from "mustache";
import {type Template} from "projector-client-object-api";

export class MustacheTemplate implements Template {

	private config: DtoMustacheTemplate;

	constructor(config: DtoMustacheTemplate) {
		this.config = config;
		mustache.parse(config.templateString);
	}

	render(data: any): string {
		if (data == null) {
			return '';
		} else {
			return mustache.render(this.config.templateString, data);
		}
	}

	destroy(): void {
		// nothing to do
	}

}
