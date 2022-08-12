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
import {animateCSS, Constants, parseHtml} from "../Common";
import {UiEntranceAnimation} from "../../generated/UiEntranceAnimation";

export class CalendarEventListPopper {

	private referenceElement: Element;
	private popper: Popper;
	private $popperElement: HTMLElement;
	private _$allDayEventsContainer: HTMLElement;
	private _$normalEventsContainer: HTMLElement;

	private visibilityTimeout: number;

	constructor(referenceElement?: Element | null) {
		this.referenceElement = referenceElement;
		this.$popperElement = parseHtml(`<div class="CalendarEventListPopper ta-tooltip calendar-event-list-popper ${referenceElement != null ? "" : "hidden"}" role="tooltip">
			<div class="ta-tooltip-arrow" x-arrow></div>
			<div class="ta-tooltip-inner">
				<div class="all-day-events"></div>
				<div class="normal-events"></div>
			</div>
		</div>`);
		document.body.appendChild(this.$popperElement);
		this._$allDayEventsContainer = this.$popperElement.querySelector<HTMLElement>(":scope .all-day-events");
		this._$normalEventsContainer = this.$popperElement.querySelector<HTMLElement>(":scope .normal-events");
		this.popper = createPopper(referenceElement || document.body, this.$popperElement, {
			placement: 'top',
			modifiers: [
				{
					name: "flip",
					options: {
						fallbackPlacements: ['right', 'left', 'bottom']
					}
				},
				{
					name: "preventOverflow"
				} ,
				{
					name: "offset",
					options: {
						offset: [0, 8]
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

		this.$popperElement.addEventListener("pointerenter", ev => this.setVisible(false, false));
	}

	get $allDayEventsContainer(): HTMLElement {
		return this._$allDayEventsContainer;
	}

	get $normalEventsContainer(): HTMLElement {
		return this._$normalEventsContainer;
	}

	public setReferenceElement(referenceElement: Element) {
		(this.popper as any).state.elements.reference = referenceElement;
	}

	public updatePosition() {
		this.popper.update();
	}

	public setVisible(visible: boolean, animate = true) {
		window.clearTimeout(this.visibilityTimeout);

		if (visible) {
			if (animate) {
				this.visibilityTimeout = window.setTimeout(() => {
					let wasHidden = this.$popperElement.classList.contains('hidden');
					this.$popperElement.classList.remove("hidden");
					this.popper.update();
					if (wasHidden) {
						animateCSS(this.$popperElement, Constants.EXIT_ANIMATION_CSS_CLASSES[UiEntranceAnimation.FADE_IN], 200)
					}
				}, 200);
			} else {
				this.$popperElement.classList.remove("hidden");
				this.popper.update();
			}
		} else {
			if (animate) {
				this.visibilityTimeout = window.setTimeout(() => {
					this.$popperElement.classList.add("hidden");
				}, 200);
			} else {
				this.$popperElement.classList.add("hidden");
			}
		}
	}

	public destroy() {
		this.popper.destroy();
		this.$popperElement.remove();
	}
}


