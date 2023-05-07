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
import {DtoEntranceAnimation, DtoExitAnimation, DtoPageTransition, DtoRepeatableAnimation,} from "./generated";
import rgba from "color-rgba";
import {Component, parseHtml} from "teamapps-client-core";
import {applyCss, extractCssValues} from "./util/cssUtil";

export const defaultSpinnerTemplate = `<div class="tr-default-spinner"><div class="spinner"></div><div>Fetching data...</div></div>`;

export class Constants {
	private static _SCROLLBAR_WIDTH: number;

	public static ENTRANCE_ANIMATION_CSS_CLASSES = {
		[DtoEntranceAnimation.BACK_IN_DOWN]: "animate__animated animate__backInDown",
		[DtoEntranceAnimation.BACK_IN_LEFT]: "animate__animated animate__backInLeft",
		[DtoEntranceAnimation.BACK_IN_RIGHT]: "animate__animated animate__backInRight",
		[DtoEntranceAnimation.BACK_IN_UP]: "animate__animated animate__backInUp",

		[DtoEntranceAnimation.LIGHT_SPEED_IN_RIGHT]: "animate__animated animate__lightSpeedIn",
		[DtoEntranceAnimation.LIGHT_SPEED_IN_LEFT]: "animate__animated animate__lightSpeedIn",
		[DtoEntranceAnimation.JACK_IN_THE_BOX]: "animate__animated animate__lightSpeedIn",
		[DtoEntranceAnimation.ROLL_IN]: "animate__animated animate__rollIn",

		[DtoEntranceAnimation.ZOOM_IN]: "animate__animated animate__zoomIn",
		[DtoEntranceAnimation.ZOOM_IN_DOWN]: "animate__animated animate__zoomInDown",
		[DtoEntranceAnimation.ZOOM_IN_LEFT]: "animate__animated animate__zoomInLeft",
		[DtoEntranceAnimation.ZOOM_IN_RIGHT]: "animate__animated animate__zoomInRight",
		[DtoEntranceAnimation.ZOOM_IN_UP]: "animate__animated animate__zoomInUp",

		[DtoEntranceAnimation.SLIDE_IN_UP]: "animate__animated animate__slideInUp",
		[DtoEntranceAnimation.SLIDE_IN_DOWN]: "animate__animated animate__slideInDown",
		[DtoEntranceAnimation.SLIDE_IN_LEFT]: "animate__animated animate__slideInLeft",
		[DtoEntranceAnimation.SLIDE_IN_RIGHT]: "animate__animated animate__slideInRight",

		[DtoEntranceAnimation.ROTATE_IN]: "animate__animated animate__rotateIn",
		[DtoEntranceAnimation.ROTATE_IN_DOWNLEFT]: "animate__animated animate__rotateInDownLeft",
		[DtoEntranceAnimation.ROTATE_IN_DOWNRIGHT]: "animate__animated animate__rotateInDownRight",
		[DtoEntranceAnimation.ROTATE_IN_UPLEFT]: "animate__animated animate__rotateInUpLeft",
		[DtoEntranceAnimation.ROTATE_IN_UPRIGHT]: "animate__animated animate__rotateInUpRight",

		[DtoEntranceAnimation.FLIP_IN_X]: "animate__animated animate__flipInX",
		[DtoEntranceAnimation.FLIP_IN_Y]: "animate__animated animate__flipInY",

		[DtoEntranceAnimation.FADE_IN]: "animate__animated animate__fadeIn",
		[DtoEntranceAnimation.FADE_IN_DOWN]: "animate__animated animate__fadeInDown",
		[DtoEntranceAnimation.FADE_IN_DOWNBIG]: "animate__animated animate__fadeInDownBig",
		[DtoEntranceAnimation.FADE_IN_LEFT]: "animate__animated animate__fadeInLeft",
		[DtoEntranceAnimation.FADE_IN_LEFTBIG]: "animate__animated animate__fadeInLeftBig",
		[DtoEntranceAnimation.FADE_IN_RIGHT]: "animate__animated animate__fadeInRight",
		[DtoEntranceAnimation.FADE_IN_RIGHTBIG]: "animate__animated animate__fadeInRightBig",
		[DtoEntranceAnimation.FADE_IN_UP]: "animate__animated animate__fadeInUp",
		[DtoEntranceAnimation.FADE_IN_UPBIG]: "animate__animated animate__fadeInUpBig",
		[DtoEntranceAnimation.FADE_IN_TOP_LEFT]: "animate__animated animate__fadeInTopLeft",
		[DtoEntranceAnimation.FADE_IN_TOP_RIGHT]: "animate__animated animate__fadeInTopRight",
		[DtoEntranceAnimation.FADE_IN_BOTTOM_LEFT]: "animate__animated animate__fadeInBottomLeft",
		[DtoEntranceAnimation.FADE_IN_BOTTOM_RIGHT]: "animate__animated animate__fadeInBottomRight",

		[DtoEntranceAnimation.BOUNCE_IN]: "animate__animated animate__bounceIn",
		[DtoEntranceAnimation.BOUNCE_IN_DOWN]: "animate__animated animate__bounceInDown",
		[DtoEntranceAnimation.BOUNCE_IN_LEFT]: "animate__animated animate__bounceInLeft",
		[DtoEntranceAnimation.BOUNCE_IN_RIGHT]: "animate__animated animate__bounceInRight",
		[DtoEntranceAnimation.BOUNCE_IN_UP]: "animate__animated animate__bounceInUp"
	};

	public static EXIT_ANIMATION_CSS_CLASSES = {
		[DtoExitAnimation.BACK_OUT_DOWN]: "animate__animated animate__backOutDown",
		[DtoExitAnimation.BACK_OUT_LEFT]: "animate__animated animate__backOutLeft",
		[DtoExitAnimation.BACK_OUT_RIGHT]: "animate__animated animate__backOutRight",
		[DtoExitAnimation.BACK_OUT_UP]: "animate__animated animate__backOutUp",

		[DtoExitAnimation.LIGHT_SPEED_OUT_RIGHT]: "animate__animated animate__lightSpeedOutRight",
		[DtoExitAnimation.LIGHT_SPEED_OUT_LEFT]: "animate__animated animate__lightSpeedOutLeft",
		[DtoExitAnimation.ROLL_OUT]: "animate__animated animate__rollOut",
		[DtoExitAnimation.HINGE]: "animate__animated animate__hinge",

		[DtoExitAnimation.ZOOM_OUT]: "animate__animated animate__zoomOut",
		[DtoExitAnimation.ZOOM_OUT_DOWN]: "animate__animated animate__zoomOutDown",
		[DtoExitAnimation.ZOOM_OUT_LEFT]: "animate__animated animate__zoomOutLeft",
		[DtoExitAnimation.ZOOM_OUT_RIGHT]: "animate__animated animate__zoomOutRight",
		[DtoExitAnimation.ZOOM_OUT_UP]: "animate__animated animate__zoomOutUp",

		[DtoExitAnimation.SLIDE_OUT_UP]: "animate__animated animate__slideOutUp",
		[DtoExitAnimation.SLIDE_OUT_DOWN]: "animate__animated animate__slideOutDown",
		[DtoExitAnimation.SLIDE_OUT_LEFT]: "animate__animated animate__slideOutLeft",
		[DtoExitAnimation.SLIDE_OUT_RIGHT]: "animate__animated animate__slideOutRight",

		[DtoExitAnimation.ROTATE_OUT]: "animate__animated animate__rotateOut",
		[DtoExitAnimation.ROTATE_OUT_DOWNLEFT]: "animate__animated animate__rotateOutDownLeft",
		[DtoExitAnimation.ROTATE_OUT_DOWNRIGHT]: "animate__animated animate__rotateOutDownRight",
		[DtoExitAnimation.ROTATE_OUT_UPLEFT]: "animate__animated animate__rotateOutUpLeft",
		[DtoExitAnimation.ROTATE_OUT_UPRIGHT]: "animate__animated animate__rotateOutUpRight",

		[DtoExitAnimation.FLIP_OUT_X]: "animate__animated animate__flipOutX",
		[DtoExitAnimation.FLIP_OUT_Y]: "animate__animated animate__flipOutY",

		[DtoExitAnimation.FADE_OUT]: "animate__animated animate__fadeOut",
		[DtoExitAnimation.FADE_OUT_DOWN]: "animate__animated animate__fadeOutDown",
		[DtoExitAnimation.FADE_OUT_DOWNBIG]: "animate__animated animate__fadeOutDownBig",
		[DtoExitAnimation.FADE_OUT_LEFT]: "animate__animated animate__fadeOutLeft",
		[DtoExitAnimation.FADE_OUT_LEFTBIG]: "animate__animated animate__fadeOutLeftBig",
		[DtoExitAnimation.FADE_OUT_RIGHT]: "animate__animated animate__fadeOutRight",
		[DtoExitAnimation.FADE_OUT_RIGHTBIG]: "animate__animated animate__fadeOutRightBig",
		[DtoExitAnimation.FADE_OUT_UP]: "animate__animated animate__fadeOutUp",
		[DtoExitAnimation.FADE_OUT_UPBIG]: "animate__animated animate__fadeOutUpBig",
		[DtoExitAnimation.FADE_OUT_TOP_LEFT]: "animate__animated animate__fadeOutTopLeft",
		[DtoExitAnimation.FADE_OUT_TOP_RIGHT]: "animate__animated animate__fadeOutTopRight",
		[DtoExitAnimation.FADE_OUT_BOTTOM_RIGHT]: "animate__animated animate__fadeOutBottomRight",
		[DtoExitAnimation.FADE_OUT_BOTTOM_LEFT]: "animate__animated animate__fadeOutBottomLeft",

		[DtoExitAnimation.BOUNCE_OUT]: "animate__animated animate__bounceOut",
		[DtoExitAnimation.BOUNCE_OUT_DOWN]: "animate__animated animate__bounceOutDown",
		[DtoExitAnimation.BOUNCE_OUT_LEFT]: "animate__animated animate__bounceOutLeft",
		[DtoExitAnimation.BOUNCE_OUT_RIGHT]: "animate__animated animate__bounceOutRight",
		[DtoExitAnimation.BOUNCE_OUT_UP]: "animate__animated animate__bounceOutUp"
	};

	public static REPEATABLE_ANIMATION_CSS_CLASSES = {
		[DtoRepeatableAnimation.BOUNCE]: "animate__animated animate__bounce",
		[DtoRepeatableAnimation.FLASH]: "animate__animated animate__flash",
		[DtoRepeatableAnimation.PULSE]: "animate__animated animate__pulse",
		[DtoRepeatableAnimation.RUBBER_BAND]: "animate__animated animate__rubberBand",
		[DtoRepeatableAnimation.SHAKE_X]: "animate__animated animate__shakeX",
		[DtoRepeatableAnimation.SHAKE_Y]: "animate__animated animate__shakeY",
		[DtoRepeatableAnimation.HEAD_SHAKE]: "animate__animated animate__headShake",
		[DtoRepeatableAnimation.SWING]: "animate__animated animate__swing",
		[DtoRepeatableAnimation.TADA]: "animate__animated animate__tada",
		[DtoRepeatableAnimation.WOBBLE]: "animate__animated animate__wobble",
		[DtoRepeatableAnimation.JELLO]: "animate__animated animate__jello",
		[DtoRepeatableAnimation.HEART_BEAT]: "animate__animated animate__heartBeat",
		[DtoRepeatableAnimation.FLIP]: "animate__animated animate__flip",

		// custom:
		[DtoRepeatableAnimation.BLINK]: "ta-blink",
		[DtoRepeatableAnimation.BLINK_SUBTLE]: "ta-blink-subtle"
	}

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

export interface ClickOutsideHandle {
	cancel: () => void
}

export function doOnceOnClickOutsideElement(elements: Element | NodeList | Element[], handler: (e?: MouseEvent) => any, useCapture = false): ClickOutsideHandle {
	const eventType = "mousedown";
	const elementsAsArray = elements instanceof Element ? [elements] : Array.from(elements);
	let handlerWrapper = (e: MouseEvent) => {
		if (closestAncestorMatching(e.target as Element, ancestor => (elementsAsArray.indexOf(ancestor) !== -1), true) == null) {
			handler(e);
			removeMouseDownListener();
		}
	};
	let removeMouseDownListener = function () {
		document.body.removeEventListener(eventType, handlerWrapper, useCapture);
	};
	setTimeout(() => document.body.addEventListener(eventType, handlerWrapper, useCapture));
	return {
		cancel: () => removeMouseDownListener()
	};
}

export type TreeEntry = any & { __children?: TreeEntry[], _isFreeTextEntry?: boolean };

export function buildTreeEntryHierarchy(entryList: any[], idPropertyName: string, parentIdPropertyName: string): TreeEntry[] {
	const rootEntries: TreeEntry[] = [];
	const entriesById: { [id: string]: TreeEntry } = {};
	if (entryList) {
		for (let i = 0; i < entryList.length; i++) {
			const entry = entryList[i];
			entriesById[idPropertyName ? entry[idPropertyName] : entry] = entry;
		}
	}
	// place children under parents
	for (let i = 0; i < entryList.length; i++) {
		const entry = entryList[i];
		let parentId = entry[parentIdPropertyName];
		if (parentId) {
			const parent = entriesById[parentId];
			if (parent != null) {
				if (!parent.__children) {
					parent.__children = [];
				}
				parent.__children.push(entry);
			} else {
				rootEntries.push(entry);
			}
		} else {
			rootEntries.push(entry);
		}
	}
	return rootEntries;
}

export type NodeWithChildren<T> = T & { __children?: NodeWithChildren<T>[] };

export function buildObjectTree<T extends object>(nodes: T[], idPropertyName: string, parentIdPropertyName: string): NodeWithChildren<T>[] {
	if (nodes == null) {
		return [];
	}
	nodes = nodes.map((node: T) => {
		return {...(node as object)};
	}) as T[];

	const rootNodes: TreeEntry[] = [];
	const nodesById: { [id: string]: TreeEntry } = {};
	for (let i = 0; i < nodes.length; i++) {
		const node = nodes[i];
		nodesById[(node as any)[idPropertyName]] = node;
	}
	// place children under parents
	for (let i = 0; i < nodes.length; i++) {
		const node = nodes[i];
		let parentId = (node as any)[parentIdPropertyName];
		if (parentId != null) {
			const parent = nodesById[parentId];
			if (parent != null) {
				if (!parent.__children) {
					parent.__children = [];
				}
				parent.__children.push(node);
			} else {
				rootNodes.push(node);
			}
		} else {
			rootNodes.push(node);
		}
	}
	return rootNodes;
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

export function formatNumber(number: number, precision: number, decimalSeparator: string, thousandsSeparator: string) {
	if (number == null || isNaN(number)) {
		return "";
	}
	let numberString = precision >= 0 ? Number(number).toFixed(precision) : number.toString();
	let separatorIndex = numberString.indexOf('.');
	numberString = numberString.replace('.', decimalSeparator);

	let integerPart = numberString.substring(0, separatorIndex === -1 ? numberString.length : separatorIndex);
	let rest = numberString.substring(separatorIndex === -1 ? numberString.length : separatorIndex, numberString.length);
	let formattedIntegerPart = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, thousandsSeparator); // see http://stackoverflow.com/a/2901298/524913
	return formattedIntegerPart + rest;
}

export function formatDecimalNumber(integerNumber: number, precision: number, decimalSeparator: string, thousandsSeparator: string) {
	if (integerNumber == null || isNaN(integerNumber)) {
		return "";
	}
	let absoluteNumberAsString = "" + Math.abs(integerNumber);
	if (absoluteNumberAsString.length < precision + 1) {
		absoluteNumberAsString = new Array(precision - absoluteNumberAsString.length + 1).join("0") + absoluteNumberAsString;
	}

	let integerPart = absoluteNumberAsString.substring(0, absoluteNumberAsString.length - precision);
	if (integerPart.length === 0) {
		integerPart = "0";
	}
	let formattedIntegerPart = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, thousandsSeparator); // see http://stackoverflow.com/a/2901298/524913

	let fractionalPart = absoluteNumberAsString.substring(absoluteNumberAsString.length - precision, absoluteNumberAsString.length);

	return (integerNumber < 0 ? '-' : '') + formattedIntegerPart + decimalSeparator + fractionalPart;
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

export function manipulateWithoutTransitions($element: HTMLElement, action: Function, transitionEnabled = false) {
	if (!transitionEnabled) {
		$element.classList.add('notransition');
	}
	action();
	if (!transitionEnabled) {
		$element.offsetHeight; // Trigger a reflow, flushing the CSS changes
		$element.classList.remove('notransition');
	}
}

export function focusNextByTabIndex(navigatableElements: HTMLElement[], navDirection: -1 | 1): boolean {
	navigatableElements.sort((e1: HTMLElement, e2: HTMLElement) => e2.tabIndex - e1.tabIndex);
	let current = document.activeElement;
	let currentIndex = navigatableElements.indexOf(current as HTMLElement);
	console.debug("selectables: " + navigatableElements.length + "; current: " + currentIndex);
	let newIndex = currentIndex + navDirection;
	if (newIndex > 0 && newIndex < navigatableElements.length) {
		console.debug(navigatableElements[newIndex]);
		navigatableElements[newIndex].focus();
		return true;
	} else {
		return false;
	}
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

export function stableSort<T>(arr: T[], cmpFunc: (a: T, b: T) => number) {
	let arrOfWrapper = arr.map(function (elem, idx) {
		return {elem: elem, idx: idx};
	});
	arrOfWrapper.sort(function (wrapperA, wrapperB) {
		let cmpDiff = cmpFunc(wrapperA.elem, wrapperB.elem);
		return cmpDiff === 0
			? wrapperA.idx - wrapperB.idx
			: cmpDiff;
	});
	return arrOfWrapper.map(function (wrapper) {
		return wrapper.elem;
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

export function enableScrollViaDragAndDrop($scrollContainer: HTMLElement) {
	function mousedownHandler(startEvent: MouseEvent) {
		$scrollContainer.style.cursor = "move";
		startEvent.preventDefault();
		let initialScrollLeft = $scrollContainer.scrollLeft;
		let initialScrollTop = $scrollContainer.scrollTop;
		let dragHandler = (e: PointerEvent) => {
			let diffX = e.pageX - startEvent.pageX;
			let diffY = e.pageY - startEvent.pageY;
			$scrollContainer.scrollLeft = initialScrollLeft - diffX;
			$scrollContainer.scrollTop = initialScrollTop - diffY;
		};
		let dropHandler = (e: PointerEvent) => {
			document.removeEventListener('pointermove', dragHandler);
			document.removeEventListener('pointerup', dropHandler);
			$scrollContainer.style.cursor = "";
		};
		document.addEventListener('pointermove', dragHandler);
		document.addEventListener('pointerup', dropHandler);
	}

	$scrollContainer.addEventListener("mousedown", (e) => mousedownHandler(e));
}

export function arraysEqual(a: any[], b: any[]) {
	if (a === b) {
		return true;
	} else if (a == null && b == null) {
		return true;
	} else if (a == null || b == null) {
		return false; // only one of them is null
	} else if (a.length != b.length) {
		return false;
	} else {
		for (let i = 0; i < a.length; ++i) {
			if (a[i] !== b[i]) return false;
		}
		return true;
	}
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

export function selectElementContents(domElement: Node, start?: number, end?: number) {
	if (domElement == null || !document.body.contains(domElement)) {
		return;
	}
	domElement = domElement.firstChild || domElement;
	const range = document.createRange();
	if (start == null || end == null) {
		range.selectNodeContents(domElement);
	} else {
		end = end || start;
		range.setStart(domElement, start);
		range.setEnd(domElement, end);
	}
	const sel = window.getSelection();
	try {
		sel.removeAllRanges();
	} catch (e) {
		// ignore (ie 11 problem, can be ignored even in ie 11)
	}
	sel.addRange(range);
}

export function outerWidthIncludingMargins(el: HTMLElement) {
	let width = el.offsetWidth;
	const style = getComputedStyle(el);
	width += parseInt(style.marginLeft) + parseInt(style.marginRight);
	return width;
}

export function outerHeightIncludingMargins(el: HTMLElement) {
	let height = el.offsetHeight;
	const style = getComputedStyle(el);
	height += parseInt(style.marginTop) + parseInt(style.marginBottom);
	return height;
}

export function addDelegatedEventListener<K extends keyof HTMLElementEventMap>(rootElement: HTMLElement, selector: string, eventTypes: K | K[], listener: (element: HTMLElement, ev: HTMLElementEventMap[K]) => any, options?: boolean | AddEventListenerOptions) {
	if (!Array.isArray(eventTypes)) {
		eventTypes = [eventTypes];
	}
	for (const eventType of eventTypes) {
		rootElement.addEventListener(eventType, ev => {
			const target = selector != null ? closestAncestor(ev.target as HTMLElement, selector, true, rootElement) : ev.target as HTMLElement;
			if (target != null) {
				listener(target, ev);
			}
		}, options)
	}
}

export function closestAncestor(el: HTMLElement, selector: string, includeSelf = false, $root: Element = document.body) {
	let currentNode: HTMLElement = (includeSelf ? el : el.parentNode) as HTMLElement;
	while (currentNode) {
		if (currentNode.matches(selector)) {
			return currentNode;
		}
		if (currentNode == $root) {
			break;
		}
		currentNode = currentNode.parentNode as HTMLElement;
	}
	return null;
}

export function closestAncestorMatching(el: Element, predicate: (ancestor: Element) => boolean, includeSelf = false) {
	let currentNode: Element = (includeSelf ? el : el.parentNode) as Element;
	while (currentNode) {
		if (predicate(currentNode)) {
			return currentNode;
		}
		currentNode = currentNode.parentNode as HTMLElement;
	}
	return null;
}

export function isDescendantOf(child: Element, potentialAncestor: Element, includeSelf = false) {
	let currentNode = includeSelf ? child : child.parentNode;
	while (currentNode) {
		if (currentNode == potentialAncestor) {
			return currentNode;
		}
		currentNode = currentNode.parentNode as HTMLElement;
	}
	return null;
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

export function removeClassesByFunction(classList: DOMTokenList, deleteDecider: (className: string) => boolean) {
	let matches = findClassesByFunction(classList, deleteDecider);
	matches.forEach(function (value) {
		classList.remove(value);
	});
}

export function findClassesByFunction(classList: DOMTokenList, matcher: (className: string) => boolean) {
	let matches: string[] = [];
	classList.forEach(function (className) {
		if (matcher(className)) {
			matches.push(className);
		}
	});
	return matches;
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

function animate(el: HTMLElement, animationClassNames: string[], animationDuration: number = 300, callback?: () => any) {
	if (animationClassNames == null || animationClassNames.length == 0) {
		callback();
		return;
	}
	if (!document.body.contains(el)) {
		console.warn("Cannot animate detached element! Will fire callback directly.");
		callback && callback();
		return;
	}
	let oldAnimationDurationValue = el.style.animationDuration;
	el.style.animationDuration = animationDuration + "ms";
	el.classList.add(...animationClassNames);

	function handleAnimationEnd() {
		el.classList.remove(...animationClassNames);
		el.removeEventListener('animationend', handleAnimationEnd);
		el.style.animationDuration = oldAnimationDurationValue;

		if (typeof callback === 'function') {
			callback();
		}
	}

	el.addEventListener('animationend', handleAnimationEnd);
}

export function animateCSS(el: HTMLElement, animationCssClasses: string, animationDuration: number = 300, callback?: () => any) {
	animate(el, animationCssClasses ? animationCssClasses.split(/ +/) : null, animationDuration, callback);
}

export function fadeOut(el: HTMLElement) {
	animateCSS(el, Constants.EXIT_ANIMATION_CSS_CLASSES[DtoExitAnimation.FADE_OUT], 300, () => el.classList.add("hidden"));
}

export function fadeIn(el: HTMLElement) {
	el.classList.remove("hidden");
	animateCSS(el, Constants.ENTRANCE_ANIMATION_CSS_CLASSES[DtoEntranceAnimation.FADE_IN]);
}

export var pageTransitionAnimationPairs = {
	'moveToLeftVsMoveFromRight': {
		outClass: ['pt-page-moveToLeft'],
		inClass: ['pt-page-moveFromRight']
	},
	'moveToRightVsMoveFromLeft': {
		outClass: ['pt-page-moveToRight'],
		inClass: ['pt-page-moveFromLeft']
	},
	'moveToTopVsMoveFromBottom': {
		outClass: ['pt-page-moveToTop'],
		inClass: ['pt-page-moveFromBottom']
	},
	'moveToBottomVsMoveFromTop': {
		outClass: ['pt-page-moveToBottom'],
		inClass: ['pt-page-moveFromTop']
	},
	'fadeVsMoveFromRight': {
		outClass: ['pt-page-fade'],
		inClass: ['pt-page-moveFromRight', 'pt-page-ontop']
	},
	'fadeVsMoveFromLeft': {
		outClass: ['pt-page-fade'],
		inClass: ['pt-page-moveFromLeft', 'pt-page-ontop']
	},
	'fadeVsMoveFromBottom': {
		outClass: ['pt-page-fade'],
		inClass: ['pt-page-moveFromBottom', 'pt-page-ontop']
	},
	'fadeVsMoveFromTop': {
		outClass: ['pt-page-fade'],
		inClass: ['pt-page-moveFromTop', 'pt-page-ontop']
	},
	'moveToLeftFadeVsMoveFromRightFade': {
		outClass: ['pt-page-moveToLeftFade'],
		inClass: ['pt-page-moveFromRightFade']
	},
	'moveToRightFadeVsMoveFromLeftFade': {
		outClass: ['pt-page-moveToRightFade'],
		inClass: ['pt-page-moveFromLeftFade']
	},
	'moveToTopFadeVsMoveFromBottomFade': {
		outClass: ['pt-page-moveToTopFade'],
		inClass: ['pt-page-moveFromBottomFade']
	},
	'moveToBottomFadeVsMoveFromTopFade': {
		outClass: ['pt-page-moveToBottomFade'],
		inClass: ['pt-page-moveFromTopFade']
	},
	'scaleDownVsMoveFromRight': {
		outClass: ['pt-page-scaleDown'],
		inClass: ['pt-page-moveFromRight', 'pt-page-ontop']
	},
	'scaleDownVsMoveFromLeft': {
		outClass: ['pt-page-scaleDown'],
		inClass: ['pt-page-moveFromLeft', 'pt-page-ontop']
	},
	'scaleDownVsMoveFromBottom': {
		outClass: ['pt-page-scaleDown'],
		inClass: ['pt-page-moveFromBottom', 'pt-page-ontop']
	},
	'scaleDownVsMoveFromTop': {
		outClass: ['pt-page-scaleDown'],
		inClass: ['pt-page-moveFromTop', 'pt-page-ontop']
	},
	'scaleDownVsScaleUpDown': {
		outClass: ['pt-page-scaleDown'],
		inClass: ['pt-page-scaleUpDown']
	},
	'scaleDownUpVsScaleUp': {
		outClass: ['pt-page-scaleDownUp'],
		inClass: ['pt-page-scaleUp']
	},
	'moveToLeftVsScaleUp': {
		outClass: ['pt-page-moveToLeft', 'pt-page-ontop'],
		inClass: ['pt-page-scaleUp']
	},
	'moveToRightVsScaleUp': {
		outClass: ['pt-page-moveToRight', 'pt-page-ontop'],
		inClass: ['pt-page-scaleUp']
	},
	'moveToTopVsScaleUp': {
		outClass: ['pt-page-moveToTop', 'pt-page-ontop'],
		inClass: ['pt-page-scaleUp']
	},
	'moveToBottomVsScaleUp': {
		outClass: ['pt-page-moveToBottom', 'pt-page-ontop'],
		inClass: ['pt-page-scaleUp']
	},
	'scaleDownCenterVsScaleUpCenter': {
		outClass: ['pt-page-scaleDownCenter'],
		inClass: ['pt-page-scaleUpCenter']
	},
	'rotateRightSideFirstVsMoveFromRight': {
		outClass: ['pt-page-rotateRightSideFirst'],
		inClass: ['pt-page-moveFromRight', 'pt-page-ontop']
	},
	'rotateLeftSideFirstVsMoveFromLeft': {
		outClass: ['pt-page-rotateLeftSideFirst'],
		inClass: ['pt-page-moveFromLeft', 'pt-page-ontop']
	},
	'rotateTopSideFirstVsMoveFromTop': {
		outClass: ['pt-page-rotateTopSideFirst'],
		inClass: ['pt-page-moveFromTop', 'pt-page-ontop']
	},
	'rotateBottomSideFirstVsMoveFromBottom': {
		outClass: ['pt-page-rotateBottomSideFirst'],
		inClass: ['pt-page-moveFromBottom', 'pt-page-ontop']
	},
	'flipOutRightVsFlipInLeft': {
		outClass: ['pt-page-flipOutRight'],
		inClass: ['pt-page-flipInLeft']
	},
	'flipOutLeftVsFlipInRight': {
		outClass: ['pt-page-flipOutLeft'],
		inClass: ['pt-page-flipInRight']
	},
	'flipOutTopVsFlipInBottom': {
		outClass: ['pt-page-flipOutTop'],
		inClass: ['pt-page-flipInBottom']
	},
	'flipOutBottomVsFlipInTop': {
		outClass: ['pt-page-flipOutBottom'],
		inClass: ['pt-page-flipInTop']
	},
	'rotateFallVsScaleUp': {
		outClass: ['pt-page-rotateFall', 'pt-page-ontop'],
		inClass: ['pt-page-scaleUp']
	},
	'rotateOutNewspaperVsRotateInNewspaper': {
		outClass: ['pt-page-rotateOutNewspaper'],
		inClass: ['pt-page-rotateInNewspaper']
	},
	'rotatePushLeftVsMoveFromRight': {
		outClass: ['pt-page-rotatePushLeft'],
		inClass: ['pt-page-moveFromRight']
	},
	'rotatePushRightVsMoveFromLeft': {
		outClass: ['pt-page-rotatePushRight'],
		inClass: ['pt-page-moveFromLeft']
	},
	'rotatePushTopVsMoveFromBottom': {
		outClass: ['pt-page-rotatePushTop'],
		inClass: ['pt-page-moveFromBottom']
	},
	'rotatePushBottomVsMoveFromTop': {
		outClass: ['pt-page-rotatePushBottom'],
		inClass: ['pt-page-moveFromTop']
	},
	'rotatePushLeftVsRotatePullRight': {
		outClass: ['pt-page-rotatePushLeft'],
		inClass: ['pt-page-rotatePullRight']
	},
	'rotatePushRightVsRotatePullLeft': {
		outClass: ['pt-page-rotatePushRight'],
		inClass: ['pt-page-rotatePullLeft']
	},
	'rotatePushTopVsRotatePullBottom': {
		outClass: ['pt-page-rotatePushTop'],
		inClass: ['pt-page-rotatePullBottom']
	},
	'rotatePushBottomVsRotatePullTop': {
		outClass: ['pt-page-rotatePushBottom'],
		inClass: ['pt-page-rotatePullTop']
	},
	'rotateFoldLeftVsMoveFromRightFade': {
		outClass: ['pt-page-rotateFoldLeft'],
		inClass: ['pt-page-moveFromRightFade']
	},
	'rotateFoldRightVsMoveFromLeftFade': {
		outClass: ['pt-page-rotateFoldRight'],
		inClass: ['pt-page-moveFromLeftFade']
	},
	'rotateFoldTopVsMoveFromBottomFade': {
		outClass: ['pt-page-rotateFoldTop'],
		inClass: ['pt-page-moveFromBottomFade']
	},
	'rotateFoldBottomVsMoveFromTopFade': {
		outClass: ['pt-page-rotateFoldBottom'],
		inClass: ['pt-page-moveFromTopFade']
	},
	'moveToRightFadeVsRotateUnfoldLeft': {
		outClass: ['pt-page-moveToRightFade'],
		inClass: ['pt-page-rotateUnfoldLeft']
	},
	'moveToLeftFadeVsRotateUnfoldRight': {
		outClass: ['pt-page-moveToLeftFade'],
		inClass: ['pt-page-rotateUnfoldRight']
	},
	'moveToBottomFadeVsRotateUnfoldTop': {
		outClass: ['pt-page-moveToBottomFade'],
		inClass: ['pt-page-rotateUnfoldTop']
	},
	'moveToTopFadeVsRotateUnfoldBottom': {
		outClass: ['pt-page-moveToTopFade'],
		inClass: ['pt-page-rotateUnfoldBottom']
	},
	'rotateRoomLeftOutVsRotateRoomLeftIn': {
		outClass: ['pt-page-rotateRoomLeftOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateRoomLeftIn']
	},
	'rotateRoomRightOutVsRotateRoomRightIn': {
		outClass: ['pt-page-rotateRoomRightOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateRoomRightIn']
	},
	'rotateRoomTopOutVsRotateRoomTopIn': {
		outClass: ['pt-page-rotateRoomTopOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateRoomTopIn']
	},
	'rotateRoomBottomOutVsRotateRoomBottomIn': {
		outClass: ['pt-page-rotateRoomBottomOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateRoomBottomIn']
	},
	'rotateCubeLeftOutVsRotateCubeLeftIn': {
		outClass: ['pt-page-rotateCubeLeftOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCubeLeftIn']
	},
	'rotateCubeRightOutVsRotateCubeRightIn': {
		outClass: ['pt-page-rotateCubeRightOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCubeRightIn']
	},
	'rotateCubeTopOutVsRotateCubeTopIn': {
		outClass: ['pt-page-rotateCubeTopOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCubeTopIn']
	},
	'rotateCubeBottomOutVsRotateCubeBottomIn': {
		outClass: ['pt-page-rotateCubeBottomOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCubeBottomIn']
	},
	'rotateCarouselLeftOutVsRotateCarouselLeftIn': {
		outClass: ['pt-page-rotateCarouselLeftOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCarouselLeftIn']
	},
	'rotateCarouselRightOutVsRotateCarouselRightIn': {
		outClass: ['pt-page-rotateCarouselRightOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCarouselRightIn']
	},
	'rotateCarouselTopOutVsRotateCarouselTopIn': {
		outClass: ['pt-page-rotateCarouselTopOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCarouselTopIn']
	},
	'rotateCarouselBottomOutVsRotateCarouselBottomIn': {
		outClass: ['pt-page-rotateCarouselBottomOut', 'pt-page-ontop'],
		inClass: ['pt-page-rotateCarouselBottomIn']
	},
	'rotateSidesOutVsRotateSidesIn': {
		outClass: ['pt-page-rotateSidesOut'],
		inClass: ['pt-page-rotateSidesIn']
	},
	'rotateSlideOutVsRotateSlideIn': {
		outClass: ['pt-page-rotateSlideOut'],
		inClass: ['pt-page-rotateSlideIn']
	},
};

export function animatePageTransition(outEl: HTMLElement, inEl: HTMLElement, animationName: keyof typeof pageTransitionAnimationPairs, animationDuration: number = 300, callback?: () => any) {
	let animationCallbackCount = 0;

	function invokeCallbackIfBothReturned() {
		animationCallbackCount++;
		if (animationCallbackCount == 2) {
			callback();
		}
	}

	if (outEl != null) {
		animate(outEl, pageTransitionAnimationPairs[animationName].outClass, animationDuration, invokeCallbackIfBothReturned);
	} else {
		animationCallbackCount++;
	}
	if (inEl != null) {
		animate(inEl, pageTransitionAnimationPairs[animationName].inClass, animationDuration, invokeCallbackIfBothReturned);
	} else {
		animationCallbackCount++;
	}
}

export function pageTransition(outEl: HTMLElement, inEl: HTMLElement, pageTransition: DtoPageTransition, animationDuration: number = 300, callback?: () => any) {
	let s = DtoPageTransition[pageTransition].toLowerCase().replace(/_{1,1}([a-z])/g, (g0, g1) => g1.toUpperCase()) as keyof typeof pageTransitionAnimationPairs;
	animatePageTransition(outEl, inEl, s, animationDuration, callback);
}

export function toggleElementCollapsed($element: HTMLElement, collapsed: boolean, animationDuration: number = 0, hiddenClass: string = "hidden", completeHandler?: () => any) {
	if (collapsed) {
		if (animationDuration > 0) {
			animateCollapse($element, true, animationDuration, () => {
				$element.classList.add(hiddenClass);
				completeHandler?.();
			});
		} else {
			$element.classList.add(hiddenClass);
			completeHandler?.();
		}
	} else {
		if (animationDuration > 0) {
			$element.classList.remove(hiddenClass)
			animateCollapse($element, false, animationDuration, completeHandler);
		} else {
			$element.classList.remove(hiddenClass);
			completeHandler?.();
		}
	}
}

export function animateCollapse(element: HTMLElement, collapsed: boolean, duration: number, onTransitionEnd?: () => void) {
	const isCollapsed = element.getAttribute("ta-collapsed") != null;
	if (isCollapsed == collapsed) {
		onTransitionEnd?.();
		return;
	}
	const initialMaxHeight = collapsed ? element.scrollHeight + "px" : "0px";
	const targetMaxHeight = collapsed ? "0px" : element.scrollHeight + "px";
	if (element.style.maxHeight == null || element.style.maxHeight == "") {
		element.style.maxHeight = initialMaxHeight;
	}
	const oldTransitionStyle = element.style.transition;
	element.style.transition = `max-height ${duration}ms`;

	let transitionEndListener = (ev: Event) => {
		["transitionend", "transitioncancel"].forEach(eventName => element.removeEventListener(eventName, transitionEndListener));
		window.clearTimeout(timeout);
		element.style.transition = oldTransitionStyle;
		if (!collapsed) {
			element.style.removeProperty("max-height");
		}
		onTransitionEnd?.();
	};
	["transitionend", "transitioncancel"].forEach(eventName => element.addEventListener(eventName, transitionEndListener));
	let timeout = window.setTimeout(transitionEndListener, duration + 100); // make sure the listener is removed no matter what!

	element.offsetHeight; // force reflow to make sure there is a transition animation
	element.style.maxHeight = targetMaxHeight;
	element.toggleAttribute("ta-collapsed", collapsed);
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