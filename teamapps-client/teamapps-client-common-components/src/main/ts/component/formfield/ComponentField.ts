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
import {DtoComponentField, DtoComponentFieldCommandHandler, DtoComponentFieldEventSource, DtoFieldEditingMode} from "../../generated";
import {AbstractField} from "./AbstractField";
import {Component, parseHtml, TeamAppsUiContext} from "teamapps-client-core";


export class ComponentField extends AbstractField<DtoComponentField, void> implements DtoComponentFieldEventSource, DtoComponentFieldCommandHandler {

	private component: Component;
	private $componentWrapper: HTMLElement;

	protected initialize(config: DtoComponentField, context: TeamAppsUiContext) {
		this.$componentWrapper = parseHtml('<div class="DtoComponentField"></div>');
		this.setHeight(config.height);
		this.setComponent(config.component as Component);
		this.setBordered(!!config.bordered)
	}

	isValidData(v: void): boolean {
		return true;
	}

	setBordered(bordered: boolean): void {
		this.$componentWrapper.classList.toggle("bordered", bordered);
		this.$componentWrapper.classList.toggle("field-border", bordered);
		this.$componentWrapper.classList.toggle("field-border-glow", bordered)
	}

	setComponent(component: Component): void {
		if (this.component != null) {
			this.component.getMainElement().remove();
		}
		this.component = component;
		this.$componentWrapper.appendChild(this.component.getMainElement());
	}

	setHeight(height: number): void {
		this.$componentWrapper.style.height = height ? `${height}px` : '';
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$componentWrapper;
	}

	protected displayCommittedValue(): void {
		// do nothing
	}

	focus(): void {
		// do nothing
	}

	getTransientValue(): void {
	}

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode): void {
		// do nothing (default implementation)
	}

	getDefaultValue(): void {
	}

	public valuesChanged(v1: void, v2: void): boolean {
		return false;
	}

}


