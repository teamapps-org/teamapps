/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {createPopper, detectOverflow} from "@popperjs/core";
import ResizeObserver from "resize-observer-polyfill";

export function createComboBoxPopper($reference: Element, $dropdown: HTMLElement, referenceOutOfViewPortHandler: () => void) {
	return createPopper($reference, $dropdown, {
		placement: 'bottom',
		strategy: "fixed",
		modifiers: [
			{
				name: "flip",
				options: {
					fallbackPlacements: ['top']
				}
			}, {
				name: "preventOverflow"
			}, {
				name: 'hideDropdownIfFieldOutOfViewport',
				enabled: true,
				phase: 'main',
				requiresIfExists: ['offset'],
				fn: ({state}) => {
					let sideObject = detectOverflow(state, {elementContext: "reference"});
					if (sideObject.left > state.rects.reference.width || sideObject.right > state.rects.reference.width
						|| sideObject.top > state.rects.reference.height || sideObject.bottom > state.rects.reference.height) {
						referenceOutOfViewPortHandler?.();
					}
				}
			}, {
				name: 'dropDownCornerSmoother',
				enabled: true,
				phase: 'write',
				fn: ({state}) => {
					$reference.classList.toggle("dropdown-flipped", state.placement === 'top');
					$dropdown.classList.toggle("flipped", state.placement === 'top');

				}
			}, {
				name: 'forceCorrectDropDownWidth_EvenIf_TemporarilyOverflowedViewPortAndThereforeScrollbarsCausedResizeBeforeFlip',
				enabled: true,
				phase: 'afterWrite',
				fn: ({state}) => {
					const comboBoxWidth = state.elements.reference.getBoundingClientRect().width;
					$dropdown.style.width = comboBoxWidth + "px";
					$dropdown.classList.toggle("broader-than-combobox", $dropdown.clientWidth > comboBoxWidth)
				}
			}, {
				name: "observeReferenceModifier",
				enabled: true,
				phase: "main",
				effect: ({state, instance}) => {
					const RO_PROP = "__popperjsResizeObserver__";
					const {reference} = state.elements;
					(reference as any)[RO_PROP] = new ResizeObserver(() => instance.update());
					(reference as any)[RO_PROP].observe(reference);
					return () => {
						(reference as any)[RO_PROP].disconnect();
						delete (reference as any)[RO_PROP];
					};
				}
			}
		]
	});
}
