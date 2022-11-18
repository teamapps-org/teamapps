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
import {
	UiComponentConfig,
	UiComponentFieldCommandHandler,
	UiComponentFieldConfig,
	UiComponentFieldEventSource,
	UiFieldEditingMode
} from "../../generated";
import {AbstractUiField} from "./AbstractUiField";
import {TeamAppsUiContext} from "teamapps-client-core";
import {TeamAppsUiComponentRegistry} from "teamapps-client-core";
import {parseHtml} from "teamapps-client-core";
import {UiComponent} from "teamapps-client-core";


export class UiComponentField extends AbstractUiField<UiComponentFieldConfig, void> implements UiComponentFieldEventSource, UiComponentFieldCommandHandler {

	private component: UiComponent<UiComponentConfig>;
	private $componentWrapper: HTMLElement;

	protected initialize(config: UiComponentFieldConfig, context: TeamAppsUiContext) {
		this.$componentWrapper = parseHtml('<div class="UiComponentField"></div>');
		this.setHeight(config.height);
		this.setComponent(config.component as UiComponent);
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

	setComponent(component: UiComponent): void {
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

	protected onEditingModeChanged(editingMode: UiFieldEditingMode): void {
		// do nothing (default implementation)
	}

	getDefaultValue(): void {
	}

	public valuesChanged(v1: void, v2: void): boolean {
		return false;
	}

}

TeamAppsUiComponentRegistry.registerComponentClass("UiComponentField", UiComponentField);
