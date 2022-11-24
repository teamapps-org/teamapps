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
import {AbstractField} from "./AbstractField";
import {
	DtoClientRecord,
	DtoFieldEditingMode,
	DtoTemplateField,
	DtoTemplateField_ClickedEvent,
	DtoTemplateFieldCommandHandler,
	DtoTemplateFieldEventSource
} from "../../generated";
import {parseHtml, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {Renderer} from "../../Common";

export class TemplateField extends AbstractField<DtoTemplateField, DtoClientRecord> implements DtoTemplateFieldCommandHandler, DtoTemplateFieldEventSource {

    public readonly onClicked: TeamAppsEvent<DtoTemplateField_ClickedEvent> = new TeamAppsEvent();

	private $main: HTMLElement;
	private templateRenderer: Renderer;

	constructor(config: DtoTemplateField, context: TeamAppsUiContext) {
		super(config, context);
	}

	protected initialize(config: DtoTemplateField, context: TeamAppsUiContext): void {
		this.$main = parseHtml(`<div class="DtoTemplateField"></div>`);
		this.$main.addEventListener("click", ev => this.onClicked.fire({}));
		this.update(config);
	}

	update(config: DtoTemplateField): void {
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

	getTransientValue(): DtoClientRecord {
		return this.getCommittedValue();
	}

	isValidData(v: DtoClientRecord): boolean {
		return true;
	}

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode, oldEditingMode?: DtoFieldEditingMode): void {
		// nothing to do!
	}

	valuesChanged(v1: DtoClientRecord, v2: DtoClientRecord): boolean {
		return false;
	}


	getReadOnlyHtml(value: DtoClientRecord, availableWidth: number): string {
		return `<div class="static-readonly-DtoTemplateField">${value != null ? this.templateRenderer.render(value.values) : ""}</div>`;
	}
}


