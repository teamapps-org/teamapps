/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import md5 from "md5";
import {UiPasswordFieldCommandHandler, UiPasswordFieldConfig, UiPasswordFieldEventSource} from "../../generated/UiPasswordFieldConfig";
import {TeamAppsUiComponentRegistry} from "../TeamAppsUiComponentRegistry";
import {UiTextField} from "./UiTextField";
import {getAutoCompleteOffValue, insertBefore, parseHtml} from "../Common";
import {parseHTML} from "jquery";


export class UiPasswordField extends UiTextField<UiPasswordFieldConfig> implements UiPasswordFieldEventSource, UiPasswordFieldCommandHandler {

	protected initialize(config: UiPasswordFieldConfig, context: TeamAppsUiContext): void {
		super.initialize(config, context);
		this.$field.type = "password";
		if (!config.autofill) {
			this.$field.autocomplete = "new-password";
			this.$field.setAttribute("autocomplete", getAutoCompleteOffValue());
			this.$field.setAttribute("autocorrect", "off");
			this.$field.setAttribute("autocapitalize", "off");
			this.$field.setAttribute("spellcheck", "off");
		}
		this.getMainInnerDomElement().classList.add("UiPasswordField");
		let $passwordVisibilityToggleButton: HTMLElement = parseHtml('<div class="password-visibility-button"></div>');
		this.setPasswordVisibilityToggleEnabled(config.passwordVisibilityToggleEnabled);
		insertBefore($passwordVisibilityToggleButton, this.getMainInnerDomElement().querySelector(':scope .clear-button'));

		$passwordVisibilityToggleButton.addEventListener('click', ev => {
			let visible = this.getMainInnerDomElement().classList.toggle('password-visible');
			this.$field.type = visible ? 'text' : 'password';
		})
	}

	setSalt(salt: string): void {
		this._config.salt = salt;
	}

	setSendValueAsMd5(sendValueAsMd5: boolean): void {
		this._config.sendValueAsMd5 = sendValueAsMd5;
	}

	getTransientValue(): string {
		return this.calculateValueString(this.$field.value);
	}

	private calculateValueString(value: any) {
		let valueString: string;
		if (this._config.sendValueAsMd5) {
			if (this._config.salt) {
				valueString = md5(this._config.salt + md5(value));
			} else {
				valueString = md5(value);
			}
		} else {
			valueString = value;
		}
		return valueString;
	}

	public getReadOnlyHtml(value: string, availableWidth: number): string {
		return `<div class="static-readonly-UiTextField static-readonly-UiPasswordField">${value != null ? "&#8226;".repeat(value.length) : ""}</div>`;
	}

	setPasswordVisibilityToggleEnabled(enabled: boolean): any {
		this._config.passwordVisibilityToggleEnabled = enabled; 
		this.getMainInnerDomElement().classList.toggle("password-visibility-toggle-enabled", enabled);
	}
}

TeamAppsUiComponentRegistry.registerFieldClass("UiPasswordField", UiPasswordField);
