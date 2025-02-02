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
import {computePosition, detectOverflow, flip, hide, MiddlewareState, shift, size} from "@floating-ui/dom";
import {animateCSS, EntranceAnimation, ExitAnimation, parseHtml} from "projector-client-object-api";

export class TimeGraphPopper {

	private referenceElement: Element;
	private $popperElement: HTMLElement;
	private visibilityTimeout: number;
	private $contentContainer: HTMLElement;

	constructor() {
		this.$popperElement = parseHtml(`<div class="TimeGraphPopper ta-tooltip hidden" role="tooltip">
			<div class="ta-tooltip-arrow" x-arrow></div>
			<div class="ta-tooltip-inner">
			</div>
		</div>`);
		document.body.appendChild(this.$popperElement);
		this.$contentContainer = this.$popperElement.querySelector<HTMLElement>(":scope .ta-tooltip-inner");
		this.$popperElement.addEventListener("pointerenter", ev => this.setVisible(false, false));
	}

	public update(referenceElement: Element, content: Element|string) {
		let oldReferenceElement = this.referenceElement;
		this.$contentContainer.innerHTML = "";
		if (typeof content === "string") {
			content = parseHtml(content);
		}
		this.$contentContainer.append(content);
		this.setVisible(referenceElement != null && content != null, oldReferenceElement != null && referenceElement != null);
	}

	private setVisible(visible: boolean, animate = true) {
		window.clearTimeout(this.visibilityTimeout);

		if (visible) {
			if (animate) {
				this.visibilityTimeout = window.setTimeout(() => {
					let wasHidden = this.$popperElement.classList.contains('hidden');
					this.$popperElement.classList.remove("hidden");
					this.updatePosition(this.referenceElement)
					if (wasHidden) {
						animateCSS(this.$popperElement, EntranceAnimation.FADE_IN, 200)
					}
				}, 200);
			} else {
				this.$popperElement.classList.remove("hidden");
				this.updatePosition(this.referenceElement)
			}
		} else {
			if (animate) {
				animateCSS(this.$popperElement, ExitAnimation.FADE_OUT, 200, () => {
					this.$popperElement.classList.add("hidden");
				});
			} else {
				this.$popperElement.classList.add("hidden");
			}
		}
	}

	private updatePosition(reference: Element) {
		computePosition(reference, this.$popperElement, {
			placement: 'top',
			strategy: 'fixed',
			middleware: [
				hide(),
				flip()
			],
		}).then((values) => {
			console.log(values.x, values.y);
			Object.assign(this.$popperElement.style, {
				left: `${(values.x)}px`,
				top: `${(values.y)}px`,
				visibility: values.middlewareData.hide.referenceHidden ? 'hidden' : null,
				pointerEvents: values.middlewareData.hide.referenceHidden ? 'none' : null
			});
		});
	}

	public destroy() {
		this.$popperElement.remove();
	}
}


