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
import { createPopper, Instance as Popper } from '@popperjs/core';
import {parseHtml, DtoFieldMessage, getHighestSeverity, FieldMessageSeverity, FieldMessagePosition} from "projector-client-object-api";

export class FieldMessagesPopper {

	private referenceElement: Element;
	private popper: Popper;
	private $popperElement: HTMLElement;
	private $messagesContainer: HTMLElement;

	constructor(referenceElement?: Element | null) {
		this.referenceElement = referenceElement;
		this.$popperElement = parseHtml(`<div class="ta-tooltip ${referenceElement != null ? "" : "hidden"}" role="tooltip">
			<div class="ta-tooltip-arrow" x-arrow></div>
			<div class="ta-tooltip-inner"></div>
		</div>`);
		document.body.appendChild(this.$popperElement);
		this.$messagesContainer = this.$popperElement.querySelector<HTMLElement>(":scope .ta-tooltip-inner");
		this.popper = createPopper(referenceElement || document.body, this.$popperElement, {
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
				}
			]
		});
	}

	public setMessages(messages: DtoFieldMessage[] = []) {
		this.$messagesContainer.innerHTML = '';
		if (messages.length > 0) {
			const highestSeverity = getHighestSeverity(messages);
			this.$popperElement.classList.remove('ta-tooltip-info', 'ta-tooltip-success',  'ta-tooltip-warning', 'ta-tooltip-error');
			this.$popperElement.classList.add(`ta-tooltip-${highestSeverity}`);
			messages.forEach(message => {
				this.$messagesContainer.append(createMessageElement(message));
			});
			this.$popperElement.classList.remove("empty");
			this.popper.update();
		} else {
			this.$popperElement.classList.add("empty");
		}
		this.popper.update();
	}

	public setReferenceElement(referenceElement: Element) {
		(this.popper as any).reference = referenceElement;
		this.popper.update();
	}

	public updatePosition() {
		this.popper.update();
	}

	public setVisible(visible: boolean) {
		this.$popperElement.classList.toggle("hidden", !visible);
		this.popper.update();
	}

	public destroy() {
		this.popper.destroy();
		this.$popperElement.remove();
	}
}

export function createMessageElement(message: DtoFieldMessage) {
	const severityCssClass = `field-message-${message.severity}`;
	const positionCssClass = `position-${message.position}`;
	const visibilityCssClass = `visibility-${message.visibilityMode}`;
	return parseHtml(`<div class="field-message ${severityCssClass} ${positionCssClass} ${visibilityCssClass}">${message.message}</div>`);
}
