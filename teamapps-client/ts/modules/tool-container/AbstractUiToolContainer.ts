/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import * as $ from "jquery";
import {UiComponent} from "../UiComponent";
import {TeamAppsUiContext} from "../TeamAppsUiContext";
import {AbstractUiToolContainerConfig} from "../../generated/AbstractUiToolContainerConfig";


interface Button {
	$buttonWrapper: JQuery;
	$button: JQuery;
}

export abstract class AbstractUiToolContainer<C extends AbstractUiToolContainerConfig> extends UiComponent<C> {

	protected static $sizeTestingContainer: JQuery;

	public static initialize() {
		$(() => { // wait until there IS a document.body
			this.$sizeTestingContainer = $('<div class="AbstractUiToolContainer-size-testing-container">').appendTo(document.body);
		});
	}

	constructor(config: C, context: TeamAppsUiContext) {
		super(config, context);
	}

	public static optimizeButtonWidth($buttonWrapper: JQuery, $button: JQuery, maxHeight: number): number {
		$buttonWrapper.appendTo(this.$sizeTestingContainer);
		const $templateDiv = $button.find('>.custom-entry-template');
		let optimizedWidth;
		if ($templateDiv.length > 0) {
			const oldHeightAttribute = $templateDiv[0].style.height; // read the style attribute of the templateDiv! (not the computed css!)
			// var oldMaxHeightAttribute = $button[0].style.maxHeight; // read the style attribute of the templateDiv! (not the computed css!)
			$templateDiv[0].style.height = null;
			$button[0].style.maxHeight = null;

			let width = 120; // don't write novels inside of buttons...
			$button.width(width);

			let $directButtonChildren = $button.find('>*');
			let linesWithChildren: { lineElement: HTMLElement, textElements: HTMLElement[] }[] = $button.find(".line")
				.toArray().map(lineElement => {
					let textElements = $(lineElement).find('>.text-element');
					return {
						lineElement: lineElement,
						textElements: textElements.toArray()
					}
				});

			// binary search for the optimal size
			let jumpSize = width;
			let jumpDirection;
			while (jumpSize > 2) {
				jumpSize = Math.ceil(jumpSize / 2);
				const buttonRight = $button[0].getBoundingClientRect().right;
				const childOverflow = $directButtonChildren.filter((index, childElement) => {
					return childElement.getBoundingClientRect().right > buttonRight
				}).length > 0;
				let hasTextCellElementOverflow = linesWithChildren.some(lineWithChildren => {
					return lineWithChildren.textElements.some(textElement => textElement.offsetWidth > lineWithChildren.lineElement.offsetWidth)
				});
				jumpDirection = (hasTextCellElementOverflow || $button[0].offsetHeight > maxHeight || $button[0].offsetWidth > width || childOverflow) ? 1 : -1;
				width = width + (jumpSize * jumpDirection);
				$button.width(width);
			}
			if ($button[0].offsetHeight > maxHeight || $button[0].offsetWidth > width) {
				width += 2;
			}
			$button.width(width);
			$button.attr("optimized-width", width);
			optimizedWidth = $buttonWrapper[0].offsetWidth;

			$templateDiv[0].style.height = oldHeightAttribute;
			// $button[0].style.maxHeight = oldMaxHeightAttribute;
		} else {
			optimizedWidth = $buttonWrapper[0].offsetWidth;
		}
		$buttonWrapper.detach();
		return optimizedWidth;
	}

}

AbstractUiToolContainer.initialize();
