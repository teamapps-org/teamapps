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

import {AbstractComponent, parseHtml, TeamAppsUiContext} from "teamapps-client-core";
import {DtoAbstractToolContainer} from "../../generated/DtoAbstractToolContainer";


interface Button {
	$buttonWrapper: HTMLElement;
	$button: HTMLElement;
}

export abstract class AbstractToolContainer<C extends DtoAbstractToolContainer> extends AbstractComponent<C> {

	protected static $sizeTestingContainer: HTMLElement;

	public static initialize() {
		window.addEventListener("load", () => { // wait until there IS a document.body
			this.$sizeTestingContainer = parseHtml('<div class="DtoAbstractToolContainer-size-testing-container">');
			document.body.appendChild(this.$sizeTestingContainer);
		});
	}

	constructor(config: C, context: TeamAppsUiContext) {
		super(config, context);
	}

	public static optimizeButtonWidth($buttonWrapper: HTMLElement, $button: HTMLElement, maxHeight: number): number {
		this.$sizeTestingContainer.appendChild($buttonWrapper);
		const $templateDiv = $button.querySelector<HTMLElement>(':scope >.custom-entry-template');
		let optimizedWidth;
		if ($templateDiv != null) {
			const oldHeightAttribute = $templateDiv.style.height; // read the style attribute of the templateDiv! (not the computed css!)
			// var oldMaxHeightAttribute = $button.style.maxHeight; // read the style attribute of the templateDiv! (not the computed css!)
			$templateDiv.style.height = null;
			$button.style.maxHeight = null;

			let width = 120; // don't write novels inside of buttons...
			$button.style.width = width + "px";

			let $directButtonChildren = Array.from($button.querySelectorAll<HTMLElement>(':scope >*'));
			let linesWithChildren: { lineElement: HTMLElement, textElements: HTMLElement[] }[] = Array.from($button.querySelectorAll<HTMLElement>(":scope .line"))
				.map(lineElement => {
					let textElements = lineElement.querySelectorAll<HTMLElement>(':scope >.text-element'); // TODO these css classes are no more used!!!
					return {
						lineElement: lineElement,
						textElements: Array.from(textElements)
					}
				});

			// binary search for the optimal size
			let jumpSize = width;
			let jumpDirection;
			while (jumpSize > 2) {
				jumpSize = Math.ceil(jumpSize / 2);
				const buttonRight = $button.getBoundingClientRect().right;
				const childOverflow = $directButtonChildren.filter(childElement => {
					return childElement.getBoundingClientRect().right > buttonRight
				}).length > 0;
				let hasTextCellElementOverflow = linesWithChildren.some(lineWithChildren => {
					return lineWithChildren.textElements.some(textElement => textElement.offsetWidth > lineWithChildren.lineElement.offsetWidth)
				});
				jumpDirection = (hasTextCellElementOverflow || $button.offsetHeight > maxHeight || $button.offsetWidth > width || childOverflow) ? 1 : -1;
				width = width + (jumpSize * jumpDirection);
				$button.style.width = width + "px";
			}
			if ($button.offsetHeight > maxHeight || $button.offsetWidth > width) {
				width += 2;
			}
			$button.style.width = width + "px";
			$button.setAttribute("optimized-width", "" + width);
			optimizedWidth = $buttonWrapper.offsetWidth;

			$templateDiv.style.height = oldHeightAttribute;
			// $button.style.maxHeight = oldMaxHeightAttribute;
		} else {
			optimizedWidth = $buttonWrapper.offsetWidth;
		}
		$buttonWrapper.remove();
		return optimizedWidth;
	}

}

AbstractToolContainer.initialize();
