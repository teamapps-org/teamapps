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
import {Component, parseHtml, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {DtoFieldEditingMode, DtoLabel, DtoLabel_ClickedEvent, DtoLabelCommandHandler, DtoLabelEventSource} from "../../generated";

export class Label extends AbstractField<DtoLabel, string> implements DtoLabelEventSource, DtoLabelCommandHandler {
	public readonly onClicked: TeamAppsEvent<DtoLabel_ClickedEvent> = new TeamAppsEvent<DtoLabel_ClickedEvent>();

	private $main: HTMLElement;
	private $icon: HTMLElement;
	private $caption: HTMLElement;
	private targetComponent: Component;
	private targetFieldVisibilityChangeHandler: (visible: boolean) => void;

	protected initialize(config: DtoLabel, context: TeamAppsUiContext): void {
		this.$main = parseHtml(`<div class="Label"><div class="icon img img-16 hidden"></div><span class="caption">${config.caption}</span></div>`);
		this.$icon = this.$main.querySelector<HTMLElement>(":scope .icon");
		this.$caption = this.$main.querySelector<HTMLElement>(":scope .caption");
		this.setIcon(config.icon);
		this.$main.addEventListener('click',() => {
			this.onClicked.fire({});
			if (this.targetComponent != null) {
				if (this.targetComponent instanceof AbstractField) {
					this.targetComponent.focus();
				}
			}
		});
		this.targetFieldVisibilityChangeHandler = (visible: boolean) => this.setVisible(visible);
		this.setTargetComponent(config.targetComponent as Component);
	}

	public setTargetComponent(targetField: Component) {
		if (this.targetComponent != null) {
			this.targetComponent.onVisibilityChanged.removeListener(this.targetFieldVisibilityChangeHandler)
		}
		this.targetComponent = targetField;
		if (targetField != null) {
			this.setVisible(targetField.isVisible());
			targetField.onVisibilityChanged.addListener(this.targetFieldVisibilityChangeHandler);
		}
	}

	isValidData(v: string): boolean {
		return v == null || typeof v === "string";
	}

	setCaption(caption: string): void {
		this.$caption.textContent = caption || '';
	}

	setIcon(icon: string): void {
		this.$icon.classList.toggle("hidden", !icon);
		this.$icon.style.backgroundImage = icon ? `url('${icon}')` : null;
	}

	public getMainInnerDomElement(): HTMLElement {
		return this.$main;
	}

	focus() {
		// do nothing
	}

	public getTransientValue(): string {
		return this.getCommittedValue();
	}

	public getDefaultValue(): string {
		return null;
	}

	protected displayCommittedValue(): void {
		this.$caption.textContent = this.getCommittedValue() || this.config.caption || "";
	}

	protected onEditingModeChanged(editingMode: DtoFieldEditingMode): void {
		// does not have any editing mode support...
	}

	public valuesChanged(v1: string, v2: string): boolean {
		return v1 !== v2;
	}
}


