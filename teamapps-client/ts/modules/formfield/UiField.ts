/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import * as log from "loglevel";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {UiField_ValueChangedEvent, UiFieldCommandHandler, UiFieldConfig, UiFieldEventSource} from "../../generated/UiFieldConfig";
import {TeamAppsEvent} from "../util/TeamAppsEvent";
import {UiFieldEditingMode} from "../../generated/UiFieldEditingMode";
import {AbstractUiComponent} from "../AbstractUiComponent";
import {UiFieldMessageConfig} from "../../generated/UiFieldMessageConfig";
import {UiFieldMessageSeverity} from "../../generated/UiFieldMessageSeverity";
import {UiFieldMessagePosition} from "../../generated/UiFieldMessagePosition";
import {UiFieldMessageVisibilityMode} from "../../generated/UiFieldMessageVisibilityMode";
import {createPopper, Instance as Popper} from '@popperjs/core';
import {bind} from "../util/Bind";
import {parseHtml, prependChild} from "../Common";
import Logger = log.Logger;


export interface ValueChangeEventData {
	value: any
}

interface FieldMessage {
	message: UiFieldMessageConfig,
	$message: HTMLElement
}

export abstract class UiField<C extends UiFieldConfig = UiFieldConfig, V = any> extends AbstractUiComponent<C> implements UiFieldCommandHandler, UiFieldEventSource {

	public readonly onValueChanged: TeamAppsEvent<UiField_ValueChangedEvent> = new TeamAppsEvent<UiField_ValueChangedEvent>(this);
	public readonly onFocused: TeamAppsEvent<void> = new TeamAppsEvent<void>(this);
	public readonly onBlurred: TeamAppsEvent<void> = new TeamAppsEvent<void>(this);
	public readonly onUserManipulation: TeamAppsEvent<void> = new TeamAppsEvent<void>(this);

	public static editingModeCssClasses: { [x: number]: string } = {
		[UiFieldEditingMode.EDITABLE]: "editable",
		[UiFieldEditingMode.EDITABLE_IF_FOCUSED]: "editable-if-focused",
		[UiFieldEditingMode.DISABLED]: "disabled",
		[UiFieldEditingMode.READONLY]: "readonly"
	};

	protected logger: Logger = log.getLogger(((<any>(this.constructor)).name || this.constructor.toString().match(/\w+/g)[1]));

	private committedValue: V;

	private editingMode: UiFieldEditingMode = UiFieldEditingMode.EDITABLE;
	private $fieldWrapper: HTMLElement;

	private _messageTooltip: {
		popper: Popper,
		$popperElement: HTMLElement,
		$messageContainer: HTMLElement
	};
	private $messagesContainerAbove: HTMLElement;
	private $messagesContainerBelow: HTMLElement;
	private fieldMessages: FieldMessage[] = [];
	private hovering: boolean;

	constructor(_config: C,
	            _context: TeamAppsUiContext) {
		super(_config, _context);
		this.$messagesContainerAbove = parseHtml(`<div class="messages messages-above"></div>`);
		this.$messagesContainerBelow = parseHtml(`<div class="messages messages-below"></div>`);
		this.$fieldWrapper = parseHtml(`<div class="UiField"></div>`);
		this.initialize(_config, _context);
		this.$fieldWrapper.appendChild(this.$messagesContainerAbove);
		this.$fieldWrapper.appendChild(this.getMainInnerDomElement());
		this.$fieldWrapper.appendChild(this.$messagesContainerBelow);
		this.setEditingMode(_config.editingMode);
		this.setCommittedValue(_config.value);
		this.onValueChanged.addListener(() => this.onUserManipulation.fire(null));
		this.getFocusableElement() && this.getFocusableElement().addEventListener("focus", () => {
			this.getMainElement().classList.add("focus");
			this.onFocused.fire(null);
		});
		this.getFocusableElement() && this.getFocusableElement().addEventListener("blur", () => {
			this.getMainElement().classList.remove("focus");
			this.onBlurred.fire(null);
		});

		this.onFocused.addListener(() => {
			this.updateFieldMessageVisibilities();
		});
		this.onBlurred.addListener(() => {
			this.updateFieldMessageVisibilities();
		});

		["mouseenter", "mouseleave"].forEach(eventName => this.getMainInnerDomElement().addEventListener(eventName, (e) => {
			this.hovering = e.type === 'mouseenter';
			this.updateFieldMessageVisibilities();
		}));
	}

	private updateFieldMessageVisibilities() {
		let highestVisibilityByPosition = this.getHighestVisibilitiesByMessagePosition();
		let messagesVisible = (position: UiFieldMessagePosition) => highestVisibilityByPosition[position] === UiFieldMessageVisibilityMode.ALWAYS_VISIBLE
			|| highestVisibilityByPosition[position] === UiFieldMessageVisibilityMode.ON_HOVER_OR_FOCUS && this.hovering
			|| this.hasFocus();
		if (messagesVisible(UiFieldMessagePosition.ABOVE)) {
			this.$messagesContainerAbove.classList.remove("hidden");
		} else {
			this.$messagesContainerAbove.classList.add("hidden");
		}
		if (messagesVisible(UiFieldMessagePosition.BELOW)) {
			this.$messagesContainerBelow.classList.remove("hidden");
		} else {
			this.$messagesContainerBelow.classList.add("hidden");
		}
		if (this._messageTooltip != null) {
			if (messagesVisible(UiFieldMessagePosition.POPOVER)) {
				this._messageTooltip.$popperElement.classList.remove("hidden");
				this._messageTooltip.popper.update();
			} else {
				this._messageTooltip.$popperElement.classList.add("hidden");
			}
		}
	}

	private getHighestVisibilitiesByMessagePosition() {
		let highestVisibilityByPosition: { [position in UiFieldMessagePosition]: UiFieldMessageVisibilityMode } = {
			[UiFieldMessagePosition.ABOVE]: this.fieldMessages.filter(m => m.message.position == UiFieldMessagePosition.ABOVE)
				.reduce((current, m) => m.message.visibilityMode > current ? m.message.visibilityMode : current, UiFieldMessageVisibilityMode.ON_FOCUS),
			[UiFieldMessagePosition.BELOW]: this.fieldMessages.filter(m => m.message.position == UiFieldMessagePosition.BELOW)
				.reduce((current, m) => m.message.visibilityMode > current ? m.message.visibilityMode : current, UiFieldMessageVisibilityMode.ON_FOCUS),
			[UiFieldMessagePosition.POPOVER]: this.fieldMessages.filter(m => m.message.position == UiFieldMessagePosition.POPOVER)
				.reduce((current, m) => m.message.visibilityMode > current ? m.message.visibilityMode : current, UiFieldMessageVisibilityMode.ON_FOCUS)
		};
		return highestVisibilityByPosition;
	}

	protected abstract initialize(config: C, context: TeamAppsUiContext): void;

	public getTeamAppsType(): string {
		return this._config._type;
	}

	public doGetMainElement(): HTMLElement {
		return this.$fieldWrapper;
	}

	abstract getMainInnerDomElement(): HTMLElement;

	abstract getFocusableElement(): HTMLElement;

	public hasFocus() {
		let focusableElement = this.getFocusableElement();
		return focusableElement && focusableElement.matches(":focus");
	}

	public focus(): void {
		let focusableElement = this.getFocusableElement();
		focusableElement && focusableElement.focus();
	}

	destroy() {
		super.destroy();
		if (this._messageTooltip) {
			this._messageTooltip.popper.destroy();
			this._messageTooltip.$popperElement.remove();
			this.onResized.removeListener(this.updatePopperPosition)
		}
	}

	abstract isValidData(v: V): boolean;

	abstract getTransientValue(): V;

	/**
	 * the value to be set if no other value has been set.
	 */
	public getDefaultValue(): V {
		return null;
	}

	protected abstract displayCommittedValue(): void;

	public abstract valuesChanged(v1: V, v2: V): boolean;

	setValue(value: any): void {
		this.setCommittedValue(value);
	}

	public setCommittedValue(v: V): void {
		if (!this.isValidData(v)) {
			this.logger.error(this.constructor.toString().match(/\w+/g)[1] + ": Invalid data: " + JSON.stringify(v));
			return;
		}
		this.logger.debug("setCommittedValue(): " + JSON.stringify(v));
		this.committedValue = v != null ? v : this.getDefaultValue();
		this.displayCommittedValue();
	}

	public getCommittedValue(): V {
		return this.committedValue;
	}

	private fireCommittedChangeEvent(): void {
		this.logger.trace("firing committed change event: " + JSON.stringify(this.committedValue));
		this.onValueChanged.fire({
			value: this.convertValueForSendingToServer(this.committedValue)
		});
	}

	protected convertValueForSendingToServer(value: V): any {
		return value;
	}

	public commit(forceEvenIfNotChanged?: boolean): boolean {
		let value = this.getTransientValue();
		let changed = this.valuesChanged(value, this.committedValue);
		if (changed || forceEvenIfNotChanged) {
			this.committedValue = value;
			this.fireCommittedChangeEvent();
		}
		return changed;
	}

	public setEditingMode(editingMode: UiFieldEditingMode = UiFieldEditingMode.EDITABLE): void {
		const oldEditingMode = this.editingMode;
		this.editingMode = editingMode;
		this.onEditingModeChanged(editingMode, oldEditingMode);
	}

	protected abstract onEditingModeChanged(editingMode: UiFieldEditingMode, oldEditingMode?: UiFieldEditingMode): void;

	public getEditingMode(): UiFieldEditingMode {
		return this.editingMode;
	}

	public isEditable(): boolean {
		return this.getEditingMode() === UiFieldEditingMode.EDITABLE || this.getEditingMode() === UiFieldEditingMode.EDITABLE_IF_FOCUSED;
	}

	public static defaultOnEditingModeChangedImpl(field: UiField<UiFieldConfig, any>) {
		field.getMainElement().classList.remove(...Object.values(UiField.editingModeCssClasses));
		field.getMainElement().classList.add(UiField.editingModeCssClasses[field.getEditingMode()]);

		let $focusable = field.getFocusableElement();
		if ($focusable) {
			switch (field.getEditingMode()) {
				case UiFieldEditingMode.EDITABLE:
					$focusable.removeAttribute("readonly");
					$focusable.removeAttribute("disabled");
					$focusable.setAttribute("tabindex", "0");
					break;
				case UiFieldEditingMode.EDITABLE_IF_FOCUSED:
					$focusable.removeAttribute("readonly");
					$focusable.removeAttribute("disabled");
					$focusable.setAttribute("tabindex", "0");
					break;
				case UiFieldEditingMode.DISABLED:
					$focusable.removeAttribute("readonly");
					$focusable.setAttribute("disabled", "disabled");
					break;
				case UiFieldEditingMode.READONLY:
					$focusable.setAttribute("readonly", "readonly");
					$focusable.removeAttribute("disabled");
					$focusable.setAttribute("tabindex", "-1");
					break;
				default:
					log.getLogger("UiField").error("unknown editing mode! " + field.getEditingMode());
			}
		}
	}

	public getReadOnlyHtml(value: V, availableWidth: number): string {
		return "TODO: Override getReadOnlyHtml()";
	}

	getFieldMessages() {
		return this.fieldMessages.map(m => m.message);
	}

	setFieldMessages(fieldMessageConfigs: UiFieldMessageConfig[]): void {
		if (fieldMessageConfigs == null) {
			fieldMessageConfigs = [];
		}

		this.getMainElement().classList.remove("message-info", "message-success", "message-warning", "message-error");
		this.$messagesContainerAbove.innerHTML = '';
		this.$messagesContainerBelow.innerHTML = '';
		if (this._messageTooltip != null) {
			this._messageTooltip.$messageContainer.innerHTML = '';
			this._messageTooltip.$popperElement.classList.remove("ta-tooltip-info", "ta-tooltip-success", "ta-tooltip-warning", "ta-tooltip-error");
		}

		this.fieldMessages = fieldMessageConfigs
			.sort((a, b) => b.severity - a.severity)
			.map(message => {
				const $message = this.createMessageElement(message);
				return {message, $message};
			});

		let getHighestSeverity = function (messages: FieldMessage[]) {
			return messages.reduce((highestSeverity, message) => message.message.severity > highestSeverity ? message.message.severity : highestSeverity, UiFieldMessageSeverity.INFO);
		};
		if (this.fieldMessages && this.fieldMessages.length > 0) {
			this.getMainElement().classList.add("message-" + UiFieldMessageSeverity[getHighestSeverity(this.fieldMessages)].toLowerCase());
		}

		let fieldMessagesByPosition: { [position in UiFieldMessagePosition]: FieldMessage[] } = {
			[UiFieldMessagePosition.ABOVE]: this.fieldMessages.filter(m => m.message.position == UiFieldMessagePosition.ABOVE),
			[UiFieldMessagePosition.BELOW]: this.fieldMessages.filter(m => m.message.position == UiFieldMessagePosition.BELOW),
			[UiFieldMessagePosition.POPOVER]: this.fieldMessages.filter(m => m.message.position == UiFieldMessagePosition.POPOVER)
		};

		fieldMessagesByPosition[UiFieldMessagePosition.ABOVE].forEach(message => prependChild(this.getMessagesContainer(message.message.position), message.$message));
		fieldMessagesByPosition[UiFieldMessagePosition.BELOW].forEach(message => this.getMessagesContainer(message.message.position).appendChild(message.$message));
		if (fieldMessagesByPosition[UiFieldMessagePosition.POPOVER].length > 0) {
			const highestPopoverSeverity = getHighestSeverity(fieldMessagesByPosition[UiFieldMessagePosition.POPOVER]);
			this.messageTooltip.$popperElement.classList.add(`ta-tooltip-${UiFieldMessageSeverity[highestPopoverSeverity].toLowerCase()}`);
			fieldMessagesByPosition[UiFieldMessagePosition.POPOVER].forEach(message => {
				this.messageTooltip.$messageContainer.appendChild(message.$message);
			});
			this.messageTooltip.$popperElement.classList.remove("empty");
			this.messageTooltip.popper.update();
		} else if (this._messageTooltip != null) {
			this.messageTooltip.$popperElement.classList.add("empty");
		}

		this.updateFieldMessageVisibilities();
	}

	private createMessageElement(message: UiFieldMessageConfig) {
		const severityCssClass = `field-message-${UiFieldMessageSeverity[message.severity].toLowerCase()}`;
		const positionCssClass = `position-${UiFieldMessagePosition[message.position].toLowerCase()}`;
		const visibilityCssClass = `visibility-${UiFieldMessageVisibilityMode[message.visibilityMode].toLowerCase()}`;
		return parseHtml(`<div class="field-message ${severityCssClass} ${positionCssClass} ${visibilityCssClass}">${message.message}</div>`);
	}

	private get messageTooltip() {
		if (this._messageTooltip == null) {
			let $popperElement = parseHtml(`<div class="ta-tooltip" role="tooltip"><div class="ta-tooltip-arrow"></div><div class="ta-tooltip-inner"></div></div>`);
			document.body.appendChild($popperElement);
			let $messageContainer = $popperElement.querySelector<HTMLElement>(":scope .ta-tooltip-inner");
			let popper = createPopper(this.getMainInnerDomElement(), $popperElement, {
				placement: 'right',
				modifiers: [
					{
						name: "flip",
						options: {
							fallbackPlacements: ['bottom', 'left', 'top']
						}
					},
					{
						name: "preventOverflow"
					},
					{
						name: "offset",
						options: {
							offset: [0, 6]
						}
					},
					{
						name: "arrow",
						options: {
							element: ".ta-tooltip-arrow", // "[data-popper-arrow]"
							padding: 10, // 0
						}
					}
				]
			});
			this.onResized.addListener(this.updatePopperPosition);
			this.deFactoVisibilityChanged.addListener(visible => {
				if (visible) {
					document.body.appendChild(this._messageTooltip.$popperElement);
				} else {
					this._messageTooltip.$popperElement.remove();
				}
			});
			this._messageTooltip = {
				popper,
				$popperElement,
				$messageContainer
			};
		}
		return this._messageTooltip;
	}

	@bind
	private updatePopperPosition() {
		this.messageTooltip.popper.update();
	}

	protected getMessagesContainer(position: UiFieldMessagePosition) {
		if (position === UiFieldMessagePosition.ABOVE) {
			return this.$messagesContainerAbove;
		} else if (position === UiFieldMessagePosition.BELOW) {
			return this.$messagesContainerBelow;
		} else if (position === UiFieldMessagePosition.POPOVER) {
			return this.messageTooltip.$messageContainer;
		}
	}

}
