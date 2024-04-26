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
import {AbstractLegacyComponent, bind, parseHtml, ServerChannel, TeamAppsEvent, TeamAppsUiContext} from "teamapps-client-core";
import {
	DtoAbstractField,
	DtoAbstractField_BlurEvent,
	DtoAbstractField_FocusEvent,
	DtoAbstractField_ValueChangedEvent,
	DtoAbstractFieldCommandHandler,
	DtoFieldEditingMode,
	DtoAbstractFieldEventSource,
	DtoFieldMessage,
	DtoFieldMessagePosition,
	DtoFieldMessageSeverity,
	DtoFieldMessageVisibilityMode
} from "../../generated";
import {createPopper, Instance as Popper} from '@popperjs/core';
import {prependChild} from "teamapps-client-core";

interface FieldMessage {
	message: DtoFieldMessage,
	$message: HTMLElement
}

export abstract class AbstractField<C extends DtoAbstractField = DtoAbstractField, V = any> extends AbstractLegacyComponent<C> implements DtoAbstractFieldCommandHandler, DtoAbstractFieldEventSource {

	public readonly onValueChanged: TeamAppsEvent<DtoAbstractField_ValueChangedEvent> = new TeamAppsEvent();
	public readonly onFocus: TeamAppsEvent<DtoAbstractField_FocusEvent> = new TeamAppsEvent();
	public readonly onBlur: TeamAppsEvent<DtoAbstractField_BlurEvent> = new TeamAppsEvent();
	public readonly onUserManipulation: TeamAppsEvent<void> = new TeamAppsEvent();

	public static editingModeCssClasses: { [x: number]: string } = {
		[DtoFieldEditingMode.EDITABLE]: "editable",
		[DtoFieldEditingMode.EDITABLE_IF_FOCUSED]: "editable-if-focused",
		[DtoFieldEditingMode.DISABLED]: "disabled",
		[DtoFieldEditingMode.READONLY]: "readonly"
	};

	private committedValue: V;

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
	private focused: boolean;

	constructor(config: C, serverChannel: ServerChannel) {
		super(config, serverChannel);
		this.$messagesContainerAbove = parseHtml(`<div class="messages messages-above"></div>`);
		this.$messagesContainerBelow = parseHtml(`<div class="messages messages-below"></div>`);
		this.$fieldWrapper = parseHtml(`<div class="Field"></div>`);
		this.initialize(config);
		this.$fieldWrapper.appendChild(this.$messagesContainerAbove);
		this.$fieldWrapper.appendChild(this.getMainInnerDomElement());
		this.$fieldWrapper.appendChild(this.$messagesContainerBelow);
		this.setEditingMode(config.editingMode);
		this.setCommittedValue(config.value);
		this.setFieldMessages(config.fieldMessages);

		this.onValueChanged.addListener(() => this.onUserManipulation.fire(null));
		this.initFocusHandling();

		this.onFocus.addListener(() => {
			this.focused = true;
			this.getMainElement().classList.add("focus");
			this.updateFieldMessageVisibilities();

		});
		this.onBlur.addListener(() => {
			this.focused = false;
			this.getMainElement().classList.remove("focus");
			this.updateFieldMessageVisibilities();
		});

		["mouseenter", "mouseleave"].forEach(eventName => this.getMainInnerDomElement().addEventListener(eventName, (e) => {
			this.hovering = e.type === 'mouseenter';
			this.updateFieldMessageVisibilities();
		}));
	}

	protected initFocusHandling() {
		this.getMainElement()?.addEventListener("focusin", () => this.onFocus.fire(null));
		this.getMainElement()?.addEventListener("focusout", () => this.onBlur.fire(null));
	}

	private updateFieldMessageVisibilities() {
		let highestVisibilityByPosition = this.getHighestVisibilitiesByMessagePosition();
		let messagesVisible = (position: DtoFieldMessagePosition) => highestVisibilityByPosition[position] === DtoFieldMessageVisibilityMode.ALWAYS_VISIBLE
			|| highestVisibilityByPosition[position] === DtoFieldMessageVisibilityMode.ON_HOVER_OR_FOCUS && this.hovering
			|| this.hasFocus();
		if (messagesVisible(DtoFieldMessagePosition.ABOVE)) {
			this.$messagesContainerAbove.classList.remove("hidden");
		} else {
			this.$messagesContainerAbove.classList.add("hidden");
		}
		if (messagesVisible(DtoFieldMessagePosition.BELOW)) {
			this.$messagesContainerBelow.classList.remove("hidden");
		} else {
			this.$messagesContainerBelow.classList.add("hidden");
		}
		if (this._messageTooltip != null) {
			if (messagesVisible(DtoFieldMessagePosition.POPOVER)) {
				this._messageTooltip.$popperElement.classList.remove("hidden");
				this._messageTooltip.popper.update();
			} else {
				this._messageTooltip.$popperElement.classList.add("hidden");
			}
		}
	}

	private getHighestVisibilitiesByMessagePosition() {
		let highestVisibilityByPosition: { [position in DtoFieldMessagePosition]: DtoFieldMessageVisibilityMode } = {
			[DtoFieldMessagePosition.ABOVE]: this.fieldMessages.filter(m => m.message.position == DtoFieldMessagePosition.ABOVE)
				.reduce((current, m) => m.message.visibilityMode > current ? m.message.visibilityMode : current, DtoFieldMessageVisibilityMode.ON_FOCUS),
			[DtoFieldMessagePosition.BELOW]: this.fieldMessages.filter(m => m.message.position == DtoFieldMessagePosition.BELOW)
				.reduce((current, m) => m.message.visibilityMode > current ? m.message.visibilityMode : current, DtoFieldMessageVisibilityMode.ON_FOCUS),
			[DtoFieldMessagePosition.POPOVER]: this.fieldMessages.filter(m => m.message.position == DtoFieldMessagePosition.POPOVER)
				.reduce((current, m) => m.message.visibilityMode > current ? m.message.visibilityMode : current, DtoFieldMessageVisibilityMode.ON_FOCUS)
		};
		return highestVisibilityByPosition;
	}

	protected abstract initialize(config: C): void;

	public getTeamAppsType(): string {
		return this.config._type;
	}

	public doGetMainElement(): HTMLElement {
		return this.$fieldWrapper;
	}

	abstract getMainInnerDomElement(): HTMLElement;

	public hasFocus() {
		return this.focused;
	}

	abstract focus(): void;

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
			console.error(this.constructor.toString().match(/\w+/g)[1] + ": Invalid data: ", v);
			return;
		}
		console.debug("setCommittedValue(): ", v);
		this.committedValue = v != null ? v : this.getDefaultValue();
		this.displayCommittedValue();
	}

	public getCommittedValue(): V {
		return this.committedValue;
	}

	private fireCommittedChangeEvent(): void {
		console.trace("firing committed change event: ", this.committedValue);
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

	public setEditingMode(editingMode: DtoFieldEditingMode = DtoFieldEditingMode.EDITABLE): void {
		const oldEditingMode = this.config.editingMode;
		this.config.editingMode = editingMode;
		this.onEditingModeChanged(editingMode, oldEditingMode);
	}

	protected abstract onEditingModeChanged(editingMode: DtoFieldEditingMode, oldEditingMode?: DtoFieldEditingMode): void;

	public getEditingMode(): DtoFieldEditingMode {
		return this.config.editingMode;
	}

	public isEditable(): boolean {
		return this.getEditingMode() === DtoFieldEditingMode.EDITABLE || this.getEditingMode() === DtoFieldEditingMode.EDITABLE_IF_FOCUSED;
	}

	public static defaultOnEditingModeChangedImpl(field: AbstractField<DtoAbstractField, any>, $focusableElementProvider: () => HTMLElement) {
		field.getMainElement().classList.remove(...Object.values(AbstractField.editingModeCssClasses));
		field.getMainElement().classList.add(AbstractField.editingModeCssClasses[field.getEditingMode()]);

		const $focusableElement = $focusableElementProvider();
		if ($focusableElement) {
			switch (field.getEditingMode()) {
				case DtoFieldEditingMode.EDITABLE:
					$focusableElement.removeAttribute("readonly");
					$focusableElement.removeAttribute("disabled");
					$focusableElement.setAttribute("tabindex", "0");
					break;
				case DtoFieldEditingMode.EDITABLE_IF_FOCUSED:
					$focusableElement.removeAttribute("readonly");
					$focusableElement.removeAttribute("disabled");
					$focusableElement.setAttribute("tabindex", "0");
					break;
				case DtoFieldEditingMode.DISABLED:
					$focusableElement.removeAttribute("readonly");
					$focusableElement.setAttribute("disabled", "disabled");
					break;
				case DtoFieldEditingMode.READONLY:
					$focusableElement.setAttribute("readonly", "readonly");
					$focusableElement.removeAttribute("disabled");
					$focusableElement.setAttribute("tabindex", "-1");
					break;
				default:
					console.error("unknown editing mode! " + field.getEditingMode());
			}
		}
	}

	public getReadOnlyHtml(value: V, availableWidth: number): string {
		return "TODO: Override getReadOnlyHtml()";
	}

	getFieldMessages() {
		return this.fieldMessages.map(m => m.message);
	}

	setFieldMessages(fieldMessageConfigs: DtoFieldMessage[]): void {
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
			return messages.reduce((highestSeverity, message) => message.message.severity > highestSeverity ? message.message.severity : highestSeverity, DtoFieldMessageSeverity.INFO);
		};
		if (this.fieldMessages && this.fieldMessages.length > 0) {
			this.getMainElement().classList.add("message-" + DtoFieldMessageSeverity[getHighestSeverity(this.fieldMessages)].toLowerCase());
		}

		let fieldMessagesByPosition: { [position in DtoFieldMessagePosition]: FieldMessage[] } = {
			[DtoFieldMessagePosition.ABOVE]: this.fieldMessages.filter(m => m.message.position == DtoFieldMessagePosition.ABOVE),
			[DtoFieldMessagePosition.BELOW]: this.fieldMessages.filter(m => m.message.position == DtoFieldMessagePosition.BELOW),
			[DtoFieldMessagePosition.POPOVER]: this.fieldMessages.filter(m => m.message.position == DtoFieldMessagePosition.POPOVER)
		};

		fieldMessagesByPosition[DtoFieldMessagePosition.ABOVE].forEach(message => prependChild(this.getMessagesContainer(message.message.position), message.$message));
		fieldMessagesByPosition[DtoFieldMessagePosition.BELOW].forEach(message => this.getMessagesContainer(message.message.position).appendChild(message.$message));
		if (fieldMessagesByPosition[DtoFieldMessagePosition.POPOVER].length > 0) {
			const highestPopoverSeverity = getHighestSeverity(fieldMessagesByPosition[DtoFieldMessagePosition.POPOVER]);
			this.messageTooltip.$popperElement.classList.add(`ta-tooltip-${DtoFieldMessageSeverity[highestPopoverSeverity].toLowerCase()}`);
			fieldMessagesByPosition[DtoFieldMessagePosition.POPOVER].forEach(message => {
				this.messageTooltip.$messageContainer.appendChild(message.$message);
			});
			this.messageTooltip.$popperElement.classList.remove("empty");
			this.messageTooltip.popper.update();
		} else if (this._messageTooltip != null) {
			this.messageTooltip.$popperElement.classList.add("empty");
		}

		this.updateFieldMessageVisibilities();
	}

	private createMessageElement(message: DtoFieldMessage) {
		const severityCssClass = `field-message-${DtoFieldMessageSeverity[message.severity].toLowerCase()}`;
		const positionCssClass = `position-${DtoFieldMessagePosition[message.position].toLowerCase()}`;
		const visibilityCssClass = `visibility-${DtoFieldMessageVisibilityMode[message.visibilityMode].toLowerCase()}`;
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

	protected getMessagesContainer(position: DtoFieldMessagePosition) {
		if (position === DtoFieldMessagePosition.ABOVE) {
			return this.$messagesContainerAbove;
		} else if (position === DtoFieldMessagePosition.BELOW) {
			return this.$messagesContainerBelow;
		} else if (position === DtoFieldMessagePosition.POPOVER) {
			return this.messageTooltip.$messageContainer;
		}
	}

}

export function getHighestSeverity (messages: DtoFieldMessage[], defaultSeverity: DtoFieldMessageSeverity | null = DtoFieldMessageSeverity.INFO) {
	if (messages == null) {
		return defaultSeverity;
	}
	return messages.reduce((highestSeverity, message) => (highestSeverity == null || message.severity > highestSeverity) ? message.severity : highestSeverity, defaultSeverity);
}