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
import {AbstractField, executeAfterAttached} from "projector-client-object-api";
import {
	DtoTemplateField,
	DtoTemplateField_ClickEvent,
	DtoTemplateFieldCommandHandler,
	DtoTemplateFieldEventSource
} from "../../generated";
import {DtoClientRecord, FieldEditingMode, parseHtml, ServerObjectChannel, ProjectorEvent, Template} from "projector-client-object-api";


export class TemplateField extends AbstractField<DtoTemplateField, DtoClientRecord> implements DtoTemplateFieldCommandHandler, DtoTemplateFieldEventSource {

    public readonly onClick: ProjectorEvent<DtoTemplateField_ClickEvent> = new ProjectorEvent();

	private $main: HTMLElement;

	constructor(config: DtoTemplateField, serverObjectChannel: ServerObjectChannel) {
		super(config, serverObjectChannel);
	}

	protected initialize(config: DtoTemplateField): void {
		this.$main = parseHtml(`<div class="TemplateField"></div>`);
		this.$main.addEventListener("click", ev => this.onClick.fire({}));
		this.update(config);
	}

	update(config: DtoTemplateField): void {
		this.config = config;
		this.displayCommittedValue();
	}

	getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	protected displayCommittedValue(): void {
		this.$main.innerHTML = (this.config.template as Template).render(this.getCommittedValue() && this.getCommittedValue().values);
	}

	@executeAfterAttached()
	focus(): void {
		// do nothing
	}

	getTransientValue(): DtoClientRecord {
		return this.getCommittedValue();
	}

	isValidData(v: DtoClientRecord): boolean {
		return true;
	}

	protected onEditingModeChanged(editingMode: FieldEditingMode, oldEditingMode?: FieldEditingMode): void {
		// nothing to do!
	}

	valuesChanged(v1: DtoClientRecord, v2: DtoClientRecord): boolean {
		return false;
	}


	getReadOnlyHtml(value: DtoClientRecord, availableWidth: number): string {
		return `<div class="static-readonly-TemplateField">${value != null ? (this.config.template as Template).render(value.values) : ""}</div>`;
	}
}


