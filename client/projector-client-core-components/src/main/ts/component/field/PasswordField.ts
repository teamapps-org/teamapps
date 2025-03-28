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
import {DtoPasswordField, DtoPasswordFieldCommandHandler, DtoPasswordFieldEventSource} from "../../generated";
import {TextField} from "./TextField";


export class PasswordField extends TextField<DtoPasswordField> implements DtoPasswordFieldEventSource, DtoPasswordFieldCommandHandler {

	protected initialize(config: DtoPasswordField): void {
		super.initialize(config);
		this.$field.type = "password";
		if (!config.autofill) {
			this.$field.autocomplete = "new-password";
			this.$field.setAttribute("autocomplete", "no");
			this.$field.setAttribute("autocorrect", "off");
			this.$field.setAttribute("autocapitalize", "off");
			this.$field.setAttribute("spellcheck", "off");
		}
		this.getMainElement().classList.add("PasswordField")
	}

	getTransientValue(): string {
		return this.$field.value;
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-TextField static-readonly-DtoPasswordField">${value != null ? "&#8226;".repeat(value.length) : ""}</div>`;
	}
}


