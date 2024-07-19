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
	applyCss,
	Component,
	EntranceAnimation,
	ExitAnimation,
	extractCssValues,
	PageTransition,
	parseHtml,
	RepeatableAnimation
} from "projector-client-object-api";

export const defaultSpinnerTemplate = `<div class="tr-default-spinner"><div class="spinner"></div><div>Fetching data...</div></div>`;

export class Constants {
	private static _SCROLLBAR_WIDTH: number;

	public static POINTER_EVENTS = {
		start: 'pointerdown',
		move: 'pointermove',
		end: 'pointerup'
	};

	static get SCROLLBAR_WIDTH() {
		if (Constants._SCROLLBAR_WIDTH == null) {
			Constants._SCROLLBAR_WIDTH = Constants.calculateScrollbarWidth();
		}
		return Constants._SCROLLBAR_WIDTH;
	}

	private static calculateScrollbarWidth() {
		const $div = parseHtml(`<div id="ASDF" style="width: 100px; height: 100px; position: absolute; top: -10000px">`);
		document.body.appendChild($div);
		const widthNoScroll = $div.clientWidth;
		$div.style.overflowY = "scroll";
		const widthWithScroll = $div.clientWidth;
		$div.remove();
		return widthNoScroll - widthWithScroll;
	}
}

export function hasVerticalScrollBar(element: HTMLElement): boolean {
	return element.scrollWidth < element.offsetWidth;
}

export function humanReadableFileSize(bytes: number, decimalK = true) {
	const thresh = decimalK ? 1000 : 1024;
	if (Math.abs(bytes) < thresh) {
		return bytes + ' B';
	}
	const units = decimalK
		? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
		: ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
	let u = -1;
	do {
		bytes /= thresh;
		++u;
	} while (Math.abs(bytes) >= thresh && u < units.length - 1);
	return bytes.toFixed(1) + ' ' + units[u];
}

export function calculateDisplayModeInnerSize(containerDimensions: { width: number, height: number },
											  innerPreferredDimensions: { width: number, height: number },
											  displayMode: "FIT_WIDTH" | "FIT_HEIGHT" | "FIT_SIZE" | "COVER" | "ORIGINAL_SIZE",
											  zoomFactor: number = 1,
											  considerScrollbars = false
): { width: number, height: number } {
	let viewPortAspectRatio = containerDimensions.width / containerDimensions.height;
	let imageAspectRatio = innerPreferredDimensions.width / innerPreferredDimensions.height;

	console.debug(`outer dimensions: ${containerDimensions.width}x${containerDimensions.height}`);
	console.debug(`inner dimensions: ${innerPreferredDimensions.width}x${innerPreferredDimensions.height}`);
	console.debug(`displayMode: ${displayMode}`);

	if (displayMode === "FIT_WIDTH") {
		let width = Math.floor(containerDimensions.width * zoomFactor);
		if (considerScrollbars && zoomFactor <= 1 && Math.ceil(width / imageAspectRatio) > containerDimensions.height) {
			// There will be a vertical scroll bar, so make sure the width will not result in a horizontal scrollbar, too
			// NOTE: Chrome still shows scrollbars sometimes. https://bugs.chromium.org/p/chromium/issues/detail?id=240772&can=2&start=0&num=100&q=&colspec=ID%20Pri%20M%20Stars%20ReleaseBlock%20Component%20Status%20Owner%20Summary%20OS%20Modified&groupby=&sort=
			width = Math.min(width, containerDimensions.width - Constants.SCROLLBAR_WIDTH);
		}
		return {width: width, height: width / imageAspectRatio};
	} else if (displayMode === "FIT_HEIGHT") {
		let height = Math.floor(containerDimensions.height * zoomFactor);
		if (considerScrollbars && zoomFactor <= 1 && height * imageAspectRatio > containerDimensions.width) {
			// There will be a horizontal scroll bar, so make sure the width will not result in a vertical scrollbar, too
			// NOTE: Chrome still shows scrollbars sometimes. https://bugs.chromium.org/p/chromium/issues/detail?id=240772&can=2&start=0&num=100&q=&colspec=ID%20Pri%20M%20Stars%20ReleaseBlock%20Component%20Status%20Owner%20Summary%20OS%20Modified&groupby=&sort=
			height = Math.min(height, containerDimensions.height - Constants.SCROLLBAR_WIDTH);
		}
		return {width: height * imageAspectRatio, height: height};
	} else if (displayMode === "FIT_SIZE") {
		if (imageAspectRatio > viewPortAspectRatio) {
			let width = Math.floor(containerDimensions.width * zoomFactor);
			return {width: width, height: width / imageAspectRatio};
		} else {
			let height = Math.floor(containerDimensions.height * zoomFactor);
			return {width: height * imageAspectRatio, height: height};
		}
	} else if (displayMode === "COVER") {
		if (imageAspectRatio < viewPortAspectRatio) {
			let width = Math.floor(containerDimensions.width * zoomFactor);
			return {width: width, height: width / imageAspectRatio};
		} else {
			let height = Math.floor(containerDimensions.height * zoomFactor);
			return {width: height * imageAspectRatio, height: height};
		}
	} else { // ORIGINAL_SIZE
		let width = innerPreferredDimensions.width * zoomFactor;
		return {width: width, height: width / imageAspectRatio};
	}
}

export type Direction = "n" | "e" | "s" | "w" | "ne" | "se" | "nw" | "sw";

export function boundSelection(
	selection: { left: number, top: number, width: number, height: number },
	bounds: { width: number, height: number },
	aspectRatio?: number,
	fixedAt?: Direction
) {
	let newSelection = {...selection};

	if (fixedAt == null) {
		if (newSelection.width > bounds.width) {
			newSelection.width = bounds.width;
		}
		if (newSelection.height > bounds.height) {
			newSelection.height = bounds.height;
		}
		if (aspectRatio != null && aspectRatio > 0) {
			if (newSelection.width / newSelection.height > aspectRatio) {
				newSelection.width = newSelection.height * aspectRatio;
			} else {
				newSelection.height = newSelection.width / aspectRatio;
			}
		}
		if (newSelection.left < 0) {
			newSelection.left = 0;
		}
		if (newSelection.left + newSelection.width > bounds.width) {
			newSelection.left = bounds.width - newSelection.width;
		}
		if (newSelection.top < 0) {
			newSelection.top = 0;
		}
		if (newSelection.top + newSelection.height > bounds.height) {
			newSelection.top = bounds.height - newSelection.height;
		}
	} else {
		let selectionXCenter = selection.left + selection.width / 2;
		let selectionYCenter = selection.top + selection.height / 2;
		let maxWidth =
			fixedAt == "n" || fixedAt == "s" ? Math.min(Math.min(selectionXCenter, bounds.width - selectionXCenter) * 2, bounds.width) :
				fixedAt == "e" || fixedAt == "ne" || fixedAt == "se" ? Math.min(selection.left + selection.width, bounds.width) :
					fixedAt == "w" || fixedAt == "nw" || fixedAt == "sw" ? Math.min(bounds.width - selection.left, bounds.width)
						: bounds.width;
		let maxHeight =
			fixedAt == "e" || fixedAt == "w" ? Math.min(Math.min(selectionYCenter, bounds.height - selectionYCenter) * 2, bounds.height) :
				fixedAt == "s" || fixedAt == "se" || fixedAt == "sw" ? Math.min(selection.top + selection.height, bounds.height) :
					fixedAt == "n" || fixedAt == "ne" || fixedAt == "nw" ? Math.min(bounds.height - selection.top, bounds.height)
						: bounds.height;

		newSelection.width = Math.min(newSelection.width, maxWidth);
		newSelection.height = Math.min(newSelection.height, maxHeight);

		if (aspectRatio != null && aspectRatio > 0) {
			if (aspectRatio > newSelection.width / newSelection.height) {
				newSelection.height = newSelection.width / aspectRatio;
			} else if (aspectRatio < newSelection.width / newSelection.height) {
				newSelection.width = newSelection.height * aspectRatio;
			}
		}

		newSelection.left =
			fixedAt == "n" || fixedAt == "s" ? selectionXCenter - newSelection.width / 2 :
				fixedAt == "e" || fixedAt == "ne" || fixedAt == "se" ? selection.left + selection.width - newSelection.width :
					fixedAt == "w" || fixedAt == "nw" || fixedAt == "sw" ? selection.left
						: 0;
		newSelection.top =
			fixedAt == "e" || fixedAt == "w" ? selectionYCenter - newSelection.height / 2 :
				fixedAt == "s" || fixedAt == "se" || fixedAt == "sw" ? selection.top + selection.height - newSelection.height :
					fixedAt == "n" || fixedAt == "ne" || fixedAt == "nw" ? selection.top
						: 0;
	}
	return newSelection;
}



const entityMap: { [c: string]: string } = {
	'&': '&amp;',
	'<': '&lt;',
	'>': '&gt;',
	'"': '&quot;',
	"'": '&#39;',
	'/': '&#x2F;',
	'`': '&#x60;',
	'=': '&#x3D;'
};

export function escapeHtml(string: string): string {
	return String(string).replace(/[&<>"'`=\/]/g, function fromEntityMap(s: string) {
		return entityMap[s] as string;
	});
}

export function getMicrosoftBrowserVersion() {
	const ua = window.navigator.userAgent;
	const msie = ua.indexOf('MSIE ');
	if (msie > 0) {
		// IE 10 or older => return version number
		return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
	}
	const trident = ua.indexOf('Trident/');
	if (trident > 0) {
		// IE 11 => return version number
		const rv = ua.indexOf('rv:');
		return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
	}
	const edge = ua.indexOf('Edge/');
	if (edge > 0) {
		// Edge (IE 12+) => return version number
		return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
	}
	// other browser
	return false;
}

export function maximizeComponent(component: Component, animationDuration: number = 300, callback?: () => void) {
	const $parentDomElement = component.getMainElement().parentElement;
	const scrollTop = window.scrollY;
	const scrollLeft = window.scrollX;
	const offset = component.getMainElement().getBoundingClientRect();
	const originalCssValues = extractCssValues(component.getMainElement(), ["top", "left", "width", "height"]);

	const animationStartCssValues = {
		top: (offset.top - scrollTop) + "px",
		left: (offset.left - scrollLeft) + "px",
		width: offset.width + "px",
		height: offset.height + "px",
	};
	Object.assign(component.getMainElement().style, {
		...animationStartCssValues
	});

	document.body.appendChild(component.getMainElement());
	component.getMainElement().classList.add("teamapps-component-maximized");

	transition(component.getMainElement(), {
		top: "5px",
		left: "5px",
		width: (window.innerWidth - 10) + "px",
		height: (window.innerHeight - 10) + "px"
	}, animationDuration, () => {
		applyCss(component.getMainElement(), {
			width: "calc(100% - 10px)",
			height: "calc(100% - 10px)"
		});
		callback && callback();
	});

	return (restoreAnimationCallback?: () => void) => {
		transition(component.getMainElement(), animationStartCssValues, animationDuration, () => {
			Object.assign(component.getMainElement().style, originalCssValues);
			component.getMainElement().classList.remove("teamapps-component-maximized");
			$parentDomElement.appendChild(component.getMainElement());
			restoreAnimationCallback && restoreAnimationCallback();
		});
	};
}

export function removeTags(value: string, ...tagNames: string[]) {
	if (value == null) {
		value = "";
	}
	for (let tagName of tagNames) {
		value = value.replace(new RegExp(`<${tagName}?[^]*?>[^]*?</${tagName}[^]*?>`, "g"), "");
	}
	return value;
}

export function removeDangerousTags(value: string) {
	return removeTags(value, "script", "style");
}








export async function createImageThumbnailUrl(file: File): Promise<string> {
	if (["image/bmp", "image/gif", "image/heic", "image/heic-sequence", "image/heif", "image/heif-sequence", "image/ief", "image/jls", "image/jp2", "image/jpeg", "image/jpm", "image/jpx", "image/ktx", "image/png", "image/sgi", "image/svg+xml", "image/tiff", "image/webp", "image/wmf"].includes(file.type)) {
		return new Promise<string | ArrayBuffer>((resolve, reject) => {
			const reader = new FileReader();
			reader.onloadend = function () {
				resolve(reader.result);
			};
			reader.onerror = function () {
				reject("Error while reading file.");
			};
			reader.readAsDataURL(file);
		}) as Promise<string>;
	} else {
		return Promise.reject("Not a known image file type.");
	}
}

function transition(el: HTMLElement, targetValues: {[style: string]: string}, animationDuration: number = 300, callback?: () => any) {
		const changingCssProperties = Object.keys(targetValues) as (keyof CSSStyleDeclaration)[];
		const originalCssValues = ["transition", ...changingCssProperties].reduce((properties, cssPropertyName) => {
			properties[cssPropertyName] = (el.style)[cssPropertyName] as string;
			return properties;
		}, {} as { [x: string]: string });
		el.style.setProperty("transition", changingCssProperties.map(p => `${p} ${animationDuration}ms`).join(','), "important");
		el.offsetWidth; // force applying style!

		const fallbackTimeout = window.setTimeout(transitionEndHandler, animationDuration + 500);
		function transitionEndHandler() {
			window.clearTimeout(fallbackTimeout);
			el.style.transition = originalCssValues.transition;
			callback && callback();
		}
		el.addEventListener("transitionend", () => transitionEndHandler(), {once: true});

		Object.assign(el.style, targetValues);
}

let lastPointerCoordinates: [number, number] = [0, 0];
document.body.addEventListener("pointermove", ev => lastPointerCoordinates = [ev.clientX, ev.clientY], {capture: true});

export function getLastPointerCoordinates() {
	return lastPointerCoordinates;
}

export function insertAtCursorPosition(input: HTMLInputElement | HTMLTextAreaElement, text: string) {
	if (input.selectionStart != null) {
		const startPos = input.selectionStart;
		const endPos = input.selectionEnd;
		input.value = input.value.substring(0, startPos)
			+ text
			+ input.value.substring(endPos, input.value.length);
	} else {
		input.value += text;
	}
}

export function getScrollParent(element, includeHidden) {
	let style = getComputedStyle(element);
	const excludeStaticParent = style.position === "absolute";
	const overflowRegex = includeHidden ? /(auto|scroll|hidden)/ : /(auto|scroll)/;

	if (style.position === "fixed") return document.body;
	for (let parent = element; (parent = parent.parentElement);) {
		style = getComputedStyle(parent);
		if (excludeStaticParent && style.position === "static") {
			continue;
		}
		if (overflowRegex.test(style.overflow + style.overflowY + style.overflowX)) return parent;
	}

	return document.body;
}