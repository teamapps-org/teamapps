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
	autoUpdate,
	computePosition,
	detectOverflow,
	flip,
	hide,
	type MiddlewareState,
	shift,
	size,
	type Strategy
} from "@floating-ui/dom";

type Options = {
	padding?: number,
	strategy?: Strategy,
	referenceOutOfViewPortHandler?: () => void
};
let DEFAULT_OPTIONS: Options = {
	padding: 5,
	strategy: 'fixed',
};

export type Disposable = () => void;

export function positionDropdownWithAutoUpdate(reference: Element, dropdown: HTMLElement, options: Options = {}): Disposable {
	options = {...DEFAULT_OPTIONS, ...options};
	return autoUpdate(reference, dropdown, () => positionDropdown(reference, dropdown, options));
}

export function positionDropdown(reference: Element, dropdown: HTMLElement, options: Options = {}) {
	options = {...DEFAULT_OPTIONS, ...options};
	computePosition(reference, dropdown, {
		placement: 'bottom-start',
		strategy: options.strategy, // TODO ################################ check !!!
		middleware: [
			hide(),
			flip(),
			size({
				apply({rects, elements, availableHeight, placement}) {
					// const isFlipped = placement.indexOf('bottom') === -1;
					Object.assign(elements.floating.style, {
						width: `${rects.reference.width}px`,
						maxHeight: `${Math.ceil(availableHeight - options.padding)}px`
					});
				}
			}),
			shift({padding: 5}),
			{
				name: 'detectOverflow',
				async fn(state: MiddlewareState) {
					const sideObject = await detectOverflow(state as any, {elementContext: "reference"});
					if (sideObject.left > state.rects.reference.width || sideObject.right > state.rects.reference.width
						|| sideObject.top > state.rects.reference.height || sideObject.bottom > state.rects.reference.height) {
						options.referenceOutOfViewPortHandler?.();
					}
					return {};
				},
			}
		],
	}).then((values) => {
		console.log(values.x, values.y);
		Object.assign(dropdown.style, {
			left: `${(values.x)}px`,
			top: `${(values.y)}px`,
			visibility: values.middlewareData.hide.referenceHidden ? 'hidden' : null,
			pointerEvents: values.middlewareData.hide.referenceHidden ? 'none' : null
		});
	});
}
