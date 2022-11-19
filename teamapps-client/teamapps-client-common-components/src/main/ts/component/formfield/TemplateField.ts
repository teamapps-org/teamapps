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
import {AbstractField} from "./AbstractUiField";
import {
	UiClientRecord,
	UiFieldEditingMode,
	UiTemplateField_ClickedEvent,
	UiTemplateFieldCommandHandler,
	UiTemplateFieldConfig,
	UiTemplateFieldEventSource
} from "../../generated";
import {TeamAppsUiComponentRegistry} from "teamapps-client-core";
import {TeamAppsUiContext} from "teamapps-client-core";
import {parseHtml, Renderer} from "../../Common";
import {TeamAppsEvent} from "teamapps-client-core";

export class TemplateField extends AbstractField<UiTemplateFieldConfig, UiClientRecord> implements UiTemplateFieldCommandHandler, UiTemplateFieldEventSource {

    public readonly onClicked: TeamAppsEvent<UiTemplateField_ClickedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private templateRenderer: Renderer;

	constructor(config: UiTemplateFieldConfig, context: TeamAppsUiContext) {
		super(config, context);
	}

	protected initialize(config: UiTemplateFieldConfig, context: TeamAppsUiContext): void {
		this.$main = parseHtml(`<div class="UiTemplateField"></div>`);
		this.$main.addEventListener("click", ev => this.onClicked.fire({}));
		this.update(config);
	}

	update(config: UiTemplateFieldConfig): void {
		this.templateRenderer = this._context.templateRegistry.createTemplateRenderer(config.template);
		this.displayCommittedValue();
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	protected displayCommittedValue(): void {
		this.$main.innerHTML = this.templateRenderer.render(this.getCommittedValue() && this.getCommittedValue().values);
	}

	focus() {
		// do nothing
	}

	getTransientValue(): UiClientRecord {
		return this.getCommittedValue();
	}

	isValidData(v: UiClientRecord): boolean {
		return true;
	}

	protected onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void {
		// nothing to do!
	}

	valuesChanged(v1: UiClientRecord, v2: UiClientRecord): boolean {
		return false;
	}


	getReadOnlyHtml(value: UiClientRecord, availableWidth: number): string {
		return `<div class="static-readonly-UiTemplateField">${value != null ? this.templateRenderer.render(value.values) : ""}</div>`;
	}
}

TeamAppsUiComponentRegistry.registerComponentClass("UiTemplateField", TemplateField);
