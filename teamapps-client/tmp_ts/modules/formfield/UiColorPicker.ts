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
import {UiField} from "./UiField";
import {DtoColorPicker, UiColorPickerEventSource} from "../../generated/DtoColorPicker";
import {TeamAppsUiContext} from "teamapps-client-core";

import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {create as createPickr, HSVaColor, Pickr} from "pickr-widget";
import {executeWhenFirstDisplayed} from "../util/ExecuteWhenFirstDisplayed";
import {keyCodes} from "../trivial-components/TrivialCore";
import {parseHtml} from "../Common";

export class UiColorPicker extends UiField<DtoColorPicker, string> implements UiColorPickerEventSource {
	private $main: HTMLElement;
	private pickr: Pickr;
	private doNotCommit: boolean;

	protected initialize(config: DtoColorPicker) {
		this.$main = parseHtml(`<div class="UiColorPicker" tabindex="-1"><div class="pickr"></div></div>`);
		this.doNotCommit = true;
		this.pickr = createPickr({
			el: this.$main.querySelector<HTMLElement>(':scope .pickr'),
			parent: document.body,
			position: "middle",

			default: config.defaultColor,

			comparison: false,

			components: {

				preview: true,
				opacity: true,
				hue: true,

				interaction: {
					hex: true,
					rgba: true,
					hsla: true,
					hsva: true,
					input: true,
					save: true
				}
			},

			onChange: (color: HSVaColor) => {
				// TODO option for that?
			},
			onSave: (hsva, instance) => {
				if (!this.doNotCommit) {
					this.commit();
					this.$main.focus();
				}
			},

			strings: {
				save: config.saveButtonCaption,
				clear: config.clearButtonCaption
			}
		});
		this.doNotCommit = false;

		this.$main.addEventListener("keydown", (e) => {
			if (e.keyCode === keyCodes.enter || e.keyCode === keyCodes.space) {
				this.pickr.show();
			}
		});

		this.$main.querySelector<HTMLElement>(":scope .pcr-button").classList.add("field-border", "field-border-glow");
	}

	isValidData(v: string): boolean {
		return v == null || typeof v == 'string';
	}

	commit(forceEvenIfNotChanged?: boolean): boolean {
		return super.commit(forceEvenIfNotChanged);
	}

	@executeWhenFirstDisplayed()
	protected displayCommittedValue(): void {
		let committedValue = this.getCommittedValue();
		let colorString = committedValue ?? this.getDefaultValue();

		try {
			this.doNotCommit = true;
			this.pickr.setColor(colorString);
		} finally {
			this.doNotCommit = false;
		}
	}

	getDefaultValue(): string {
		return this.config.defaultColor;
	}

	focus(): void {
		return this.$main.focus();
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	getTransientValue(): string {
		const color = this.pickr.getColor();
		let rgb = color.toRGBA(); // the alpha value is buggy
		return `rgba(${rgb[0]}, ${rgb[1]}, ${rgb[2]}, ${color.a})`;
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		if (editingMode === UiFieldEditingMode.DISABLED || editingMode === UiFieldEditingMode.READONLY) {
			this.pickr.disable();
		} else {
			this.pickr.enable();
		}
	}

	valuesChanged(v1: string, v2: string): boolean {
		return (v1 == null) !== (v2 == null)
			|| (v1 != null && v2 != null && v1 !== v2);
	}

	destroy(): void {
		super.destroy();
		this.pickr.destroyAndRemove();
	}

}


