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
import "bootstrap-notify";
import * as log from "loglevel";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {TeamAppsUiContext} from "./TeamAppsUiContext";
import {UiEntranceAnimation} from "../generated/UiEntranceAnimation";
import {UiExitAnimation} from "../generated/UiExitAnimation";
import {UiPageDisplayMode} from "../generated/UiPageDisplayMode";
import {UiNotification_Position} from "../generated/UiNotificationConfig";
import {createUiColorCssString} from "./util/CssFormatUtil";
import {UiColorConfig} from "../generated/UiColorConfig";
import {UiTemplateConfig} from "../generated/UiTemplateConfig";
import {UiTextMatchingMode} from "../generated/UiTextMatchingMode";
import * as moment from "moment-timezone";
import {UiComponent} from "./UiComponent";
import {UiPageTransition} from "../generated/UiPageTransition";

export type RenderingFunction = (data: any) => string;

export type Renderer = {
	render: RenderingFunction,
	template: UiTemplateConfig
};

const logger = log.getLogger("Commmon");

export class Constants {
	private static _SCROLLBAR_WIDTH: number;

	public static ENTRANCE_ANIMATION_CSS_CLASSES = {
		[UiEntranceAnimation.LIGHTSPEED_IN]: "lightSpeedIn",
		[UiEntranceAnimation.ROLL_IN]: "rollIn",
		[UiEntranceAnimation.ZOOM_IN]: "zoomIn",
		[UiEntranceAnimation.ZOOM_IN_DOWN]: "zoomInDown",
		[UiEntranceAnimation.ZOOM_IN_LEFT]: "zoomInLeft",
		[UiEntranceAnimation.ZOOM_IN_RIGHT]: "zoomInRight",
		[UiEntranceAnimation.ZOOM_IN_UP]: "zoomInUp",
		[UiEntranceAnimation.SLIDE_IN_UP]: "slideInUp",
		[UiEntranceAnimation.SLIDE_IN_DOWN]: "slideInDown",
		[UiEntranceAnimation.SLIDE_IN_LEFT]: "slideInLeft",
		[UiEntranceAnimation.SLIDE_IN_RIGHT]: "slideInRight",
		[UiEntranceAnimation.ROTATE_IN]: "rotateIn",
		[UiEntranceAnimation.ROTATE_IN_DOWNLEFT]: "rotateInDownLeft",
		[UiEntranceAnimation.ROTATE_IN_DOWNRIGHT]: "rotateInDownRight",
		[UiEntranceAnimation.ROTATE_IN_UPLEFT]: "rotateInUpLeft",
		[UiEntranceAnimation.ROTATE_IN_UPRIGHT]: "rotateInUpRight",
		[UiEntranceAnimation.FLIP_IN_X]: "flipInX",
		[UiEntranceAnimation.FLIP_IN_Y]: "flipInY",
		[UiEntranceAnimation.FADE_IN]: "fadeIn",
		[UiEntranceAnimation.FADE_IN_DOWN]: "fadeInDown",
		[UiEntranceAnimation.FADE_IN_DOWNBIG]: "fadeInDownBig",
		[UiEntranceAnimation.FADE_IN_LEFT]: "fadeInLeft",
		[UiEntranceAnimation.FADE_IN_LEFTBIG]: "fadeInLeftBig",
		[UiEntranceAnimation.FADE_IN_RIGHT]: "fadeInRight",
		[UiEntranceAnimation.FADE_IN_RIGHTBIG]: "fadeInRightBig",
		[UiEntranceAnimation.FADE_IN_UP]: "fadeInUp",
		[UiEntranceAnimation.FADE_IN_UPBIG]: "fadeInUpBig",
		[UiEntranceAnimation.BOUNCE_IN]: "bounceIn",
		[UiEntranceAnimation.BOUNCE_IN_DOWN]: "bounceInDown",
		[UiEntranceAnimation.BOUNCE_IN_LEFT]: "bounceInLeft",
		[UiEntranceAnimation.BOUNCE_IN_RIGHT]: "bounceInRight",
		[UiEntranceAnimation.BOUNCE_IN_UP]: "bounceInUp"
	};

	public static EXIT_ANIMATION_CSS_CLASSES = {
		[UiExitAnimation.LIGHTSPEED_OUT]: "lightSpeedOut",
		[UiExitAnimation.ROLL_OUT]: "rollOut",
		[UiExitAnimation.HINGE]: "hinge",
		[UiExitAnimation.ZOOM_OUT]: "zoomOut",
		[UiExitAnimation.ZOOM_OUT_DOWN]: "zoomOutDown",
		[UiExitAnimation.ZOOM_OUT_LEFT]: "zoomOutLeft",
		[UiExitAnimation.ZOOM_OUT_RIGHT]: "zoomOutRight",
		[UiExitAnimation.ZOOM_OUT_UP]: "zoomOutUp",
		[UiExitAnimation.SLIDE_OUT_UP]: "slideOutUp",
		[UiExitAnimation.SLIDE_OUT_DOWN]: "slideOutDown",
		[UiExitAnimation.SLIDE_OUT_LEFT]: "slideOutLeft",
		[UiExitAnimation.SLIDE_OUT_RIGHT]: "slideOutRight",
		[UiExitAnimation.ROTATE_OUT]: "rotateOut",
		[UiExitAnimation.ROTATE_OUT_DOWNLEFT]: "rotateOutDownLeft",
		[UiExitAnimation.ROTATE_OUT_DOWNRIGHT]: "rotateOutDownRight",
		[UiExitAnimation.ROTATE_OUT_UPLEFT]: "rotateOutUpLeft",
		[UiExitAnimation.ROTATE_OUT_UPRIGHT]: "rotateOutUpRight",
		[UiExitAnimation.FLIP_OUT_X]: "flipOutX",
		[UiExitAnimation.FLIP_OUT_Y]: "flipOutY",
		[UiExitAnimation.FADE_OUT]: "fadeOut",
		[UiExitAnimation.FADE_OUT_DOWN]: "fadeOutDown",
		[UiExitAnimation.FADE_OUT_DOWNBIG]: "fadeOutDownBig",
		[UiExitAnimation.FADE_OUT_LEFT]: "fadeOutLeft",
		[UiExitAnimation.FADE_OUT_LEFTBIG]: "fadeOutLeftBig",
		[UiExitAnimation.FADE_OUT_RIGHT]: "fadeOutRight",
		[UiExitAnimation.FADE_OUT_RIGHTBIG]: "fadeOutRightBig",
		[UiExitAnimation.FADE_OUT_UP]: "fadeOutUp",
		[UiExitAnimation.FADE_OUT_UPBIG]: "fadeOutUpBig",
		[UiExitAnimation.BOUNCE_OUT]: "bounceOut",
		[UiExitAnimation.BOUNCE_OUT_DOWN]: "bounceOutDown",
		[UiExitAnimation.BOUNCE_OUT_LEFT]: "bounceOutLeft",
		[UiExitAnimation.BOUNCE_OUT_RIGHT]: "bounceOutRight",
		[UiExitAnimation.BOUNCE_OUT_UP]: "bounceOutUp"
	};

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
		const $div = parseHtml(`<div id="ASDF" style="width: 100px; height: 100px; position: absolute; top: -10000px">`)
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

export const matchingModesMapping: { [x in UiTextMatchingMode]: 'contains' | 'prefix' | 'prefix-word' | 'prefix-levenshtein' | 'levenshtein' } = {
	[UiTextMatchingMode.PREFIX]: "prefix",
	[UiTextMatchingMode.PREFIX_WORD]: "prefix-word",
	[UiTextMatchingMode.CONTAINS]: "contains",
	[UiTextMatchingMode.SIMILARITY]: "levenshtein"
};

export function loadScriptAsynchronously(url: string, callback?: EventListener) {
	const scriptElement = document.createElement('script');
	scriptElement.src = url;
	if (callback) {
		scriptElement.addEventListener('load', callback, false);
	}
	const someExistingScriptElement = document.getElementsByTagName('script')[0];
	someExistingScriptElement.parentNode.insertBefore(scriptElement, someExistingScriptElement);
}

export interface ClickOutsideHandle {
	cancel: () => void
}

export function doOnceOnClickOutsideElement(elements: Element | NodeList | Element[], handler: (e?: JQueryMouseEventObject) => any, useCapture = false): ClickOutsideHandle {
	const eventType = "mousedown";
	const elementsAsArray = elements instanceof Element ? [elements] : Array.from(elements);
	let handlerWrapper = (e: JQueryMouseEventObject) => {
		if (closestAncestorMatching(e.target, ancestor => (elementsAsArray.indexOf(ancestor) !== -1), true) == null) {
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

export function generateUUID(startingWithCharacter?: boolean) {
	return (startingWithCharacter ? 'u-' : '') + 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		const randomHex = Math.random() * 16 | 0;
		const v = c == 'x' ? randomHex : (randomHex & 0x3 | 0x8);
		return v.toString(16);
	});
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

export function applyDisplayMode($outer: HTMLElement, $inner: HTMLElement, displayMode: UiPageDisplayMode | any, options?: {
	innerPreferedDimensions?: { // inferred for img
		width: number,
		height: number,
	}
	zoomFactor?: number,
	padding?: number,
	considerScrollbars?: Boolean
}) {
	options = $.extend({}, options); // copy the options as we are potentially making changes to the object...
	if (options.innerPreferedDimensions == null || !options.innerPreferedDimensions.width || !options.innerPreferedDimensions.height) {
		if ($inner instanceof HTMLImageElement) {
			let imgElement = <HTMLImageElement>$($inner)[0];
			options.innerPreferedDimensions = {
				width: imgElement.naturalWidth,
				height: imgElement.naturalHeight
			}
		} else {
			$inner.style.width = "100%";
			$inner.style.height = "100%";
			return;
		}
	}
	options.zoomFactor = options.zoomFactor || 1;
	if (options.padding == null) {
		options.padding = parseInt($outer.style.paddingLeft) || 0;
	}

	let availableWidth = $outer.offsetWidth - 2 * options.padding;
	let availableHeight = $outer.offsetHeight - 2 * options.padding;
	let viewPortAspectRatio = availableWidth / availableHeight;
	let imageAspectRatio = options.innerPreferedDimensions.width / options.innerPreferedDimensions.height;

	logger.trace(`outer dimensions: ${availableWidth}x${availableHeight}`);
	logger.trace(`inner dimensions: ${options.innerPreferedDimensions.width}x${options.innerPreferedDimensions.height}`);
	logger.trace(`displayMode: ${UiPageDisplayMode[displayMode]}`);

	if (displayMode === UiPageDisplayMode.FIT_WIDTH) {
		let width = Math.floor(availableWidth * options.zoomFactor);
		if (options.considerScrollbars && options.zoomFactor <= 1 && Math.ceil(width / imageAspectRatio) > availableHeight) {
			// There will be a vertical scroll bar, so make sure the width will not result in a horizontal scrollbar, too
			// NOTE: Chrome still shows scrollbars sometimes. https://bugs.chromium.org/p/chromium/issues/detail?id=240772&can=2&start=0&num=100&q=&colspec=ID%20Pri%20M%20Stars%20ReleaseBlock%20Component%20Status%20Owner%20Summary%20OS%20Modified&groupby=&sort=
			width = Math.min(width, availableWidth - Constants.SCROLLBAR_WIDTH);
		}
		$inner.style.width = width + "px";
		$inner.style.height = "auto";
	} else if (displayMode === UiPageDisplayMode.FIT_HEIGHT) {
		let height = Math.floor(availableHeight * options.zoomFactor);
		if (options.considerScrollbars && options.zoomFactor <= 1 && height * imageAspectRatio > availableWidth) {
			// There will be a horizontal scroll bar, so make sure the width will not result in a vertical scrollbar, too
			// NOTE: Chrome still shows scrollbars sometimes. https://bugs.chromium.org/p/chromium/issues/detail?id=240772&can=2&start=0&num=100&q=&colspec=ID%20Pri%20M%20Stars%20ReleaseBlock%20Component%20Status%20Owner%20Summary%20OS%20Modified&groupby=&sort=
			height = Math.min(height, availableHeight - Constants.SCROLLBAR_WIDTH);
		}
		$inner.style.width = "auto";
		$inner.style.height = height + "px";
	} else if (displayMode === UiPageDisplayMode.FIT_SIZE) {
		if (imageAspectRatio > viewPortAspectRatio) {
			let width = Math.floor(availableWidth * options.zoomFactor);
			$inner.style.width = width + "px";
			$inner.style.height = imageAspectRatio ? (width / imageAspectRatio) + "px" : "auto";
		} else {
			let height = Math.floor(availableHeight * options.zoomFactor);
			$inner.style.width = imageAspectRatio ? (height * imageAspectRatio) + "px" : "auto";
			$inner.style.height = height + "px";
		}
	} else if (displayMode === UiPageDisplayMode.COVER) {
		if (imageAspectRatio < viewPortAspectRatio) {
			$inner.style.width = Math.floor(availableWidth * options.zoomFactor) + "px";
			$inner.style.height = "auto";
		} else {
			$inner.style.width = "auto";
			$inner.style.height = Math.floor(availableHeight * options.zoomFactor) + "px";
		}
	} else { // ORIGINAL_SIZE
		$inner.style.width = (options.innerPreferedDimensions.width * options.zoomFactor) + "px";
		$inner.style.height = "auto";
	}
}

export function boundSelection(selection: { left: number, top: number, width: number, height: number }, bounds: { width: number, height: number }, aspectRatio?: number) {
	let newSelection = {
		left: selection.left,
		top: selection.top,
		width: selection.width,
		height: selection.height
	};
	if (newSelection.width > bounds.width) {
		newSelection.width = bounds.width;
	}
	if (newSelection.height > bounds.height) {
		newSelection.height = bounds.height;
	}
	if (aspectRatio > 0) {
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
	return newSelection;
}


// ===== FULLSCREEN HANDLING ===== (maybe extract this to own file...)

document.addEventListener("fullscreenchange", fullScreenChangeHandler);
document.addEventListener("webkitfullscreenchange", fullScreenChangeHandler);
document.addEventListener("mozfullscreenchange", fullScreenChangeHandler);
document.addEventListener("MSFullscreenChange", fullScreenChangeHandler);

export function enterFullScreen(component: UiComponent<UiComponentConfig>) {
	let element: Element = component.getMainDomElement();
	element.classList.add("fullscreen");
	if (element.requestFullscreen) {
		element.requestFullscreen();
	} else if ((element as any).msRequestFullscreen) {
		(element as any).msRequestFullscreen();
	} else if ((element as any).mozRequestFullScreen) {
		(element as any).mozRequestFullScreen();
	} else if ((element as any).webkitRequestFullscreen) {
		(element as any).webkitRequestFullscreen();
	}
}

function fullScreenChangeHandler(e: Event) {
	if (!getFullScreenElement()) {
		(e.target as Element).classList.remove("fullscreen");
	}
}

export function isFullScreen(): boolean {
	return !!getFullScreenElement();
}

function getFullScreenElement(): Element {
	return document.fullscreenElement ||
		(document as any).webkitFullscreenElement ||
		(document as any).mozFullScreenElement ||
		(document as any).msFullscreenElement;
}

export function exitFullScreen() {
	let fullScreenElement = getFullScreenElement();
	if (fullScreenElement) {
		if (document.exitFullscreen) {
			document.exitFullscreen();
		} else if ((document as any).webkitExitFullscreen) {
			(document as any).webkitExitFullscreen();
		} else if ((document as any).mozCancelFullScreen) {
			(document as any).mozCancelFullScreen();
		} else if ((document as any).msExitFullscreen) {
			(document as any).msExitFullscreen();
		}
	}
}

export function positionDropDown($button: Element, $dropDown: HTMLElement, {
	viewPortPadding = 10,
	minHeightBeforeFlipping = 200
}) {
	$dropDown.classList.remove("pseudo-hidden");

	let boundingClientRect = $button.getBoundingClientRect();
	let maxHeight = window.innerHeight - (boundingClientRect.top + boundingClientRect.height) - viewPortPadding;
	let maxFlippedHeight = boundingClientRect.top - viewPortPadding;
	let flip = maxHeight < minHeightBeforeFlipping && maxFlippedHeight > maxHeight;

	$($dropDown).position({
		my: "left " + (flip ? "bottom" : "top"),
		at: "left " + (flip ? "top" : "bottom"),
		of: $button,
		collision: "fit none"
	});
	$dropDown.querySelector<HTMLElement>(':scope > .background-color-div').style.maxHeight = (flip ? maxFlippedHeight : maxHeight) + "px";
	$dropDown.querySelector<HTMLElement>(':scope > .background-color-div > *').style.maxHeight = (flip ? maxFlippedHeight : maxHeight) + "px";
	if ($dropDown.offsetWidth > window.innerWidth - 2 * viewPortPadding) {
		$dropDown.style.width = "auto";
		$dropDown.style.left = viewPortPadding + "px";
		$dropDown.style.right = viewPortPadding + "px";
	} else if ($dropDown.offsetLeft + $dropDown.offsetWidth > window.innerWidth - viewPortPadding) {
		$dropDown.style.left = "auto";
		$dropDown.style.right = viewPortPadding + "px";
	}
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
	log.getLogger("Common").trace("selectables: " + navigatableElements.length + "; current: " + currentIndex);
	let newIndex = currentIndex + navDirection;
	if (newIndex > 0 && newIndex < navigatableElements.length) {
		logger.trace(navigatableElements[newIndex]);
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

export function capitalizeFirstLetter(string: string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
}

export function logException(e: any, additionalString?: string) {
	console.error(e, e.stack, additionalString);
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

export function showNotification(html: string, config?: {
	backgroundColor?: UiColorConfig,
	position?: UiNotification_Position,
	displayTimeInMillis?: number,
	dismissable?: boolean,
	showProgressBar?: boolean,
	entranceAnimation?: UiEntranceAnimation,
	exitAnimation?: UiExitAnimation
}) {
	config = {
		backgroundColor: {_type: "UiColor", red: 255, green: 255, blue: 255, alpha: 1},
		position: UiNotification_Position.TOP_RIGHT,
		displayTimeInMillis: 3000,
		dismissable: true,
		showProgressBar: true,
		entranceAnimation: UiEntranceAnimation.SLIDE_IN_RIGHT,
		exitAnimation: UiExitAnimation.SLIDE_OUT_RIGHT,
		...config
	};

	let position2placement = {
		[UiNotification_Position.TOP_LEFT]: {from: 'top', align: 'left'},
		[UiNotification_Position.TOP_CENTER]: {from: 'top', align: 'center'},
		[UiNotification_Position.TOP_RIGHT]: {from: 'top', align: 'right'},
		[UiNotification_Position.BOTTOM_LEFT]: {from: 'bottom', align: 'left'},
		[UiNotification_Position.BOTTOM_CENTER]: {from: 'bottom', align: 'center'},
		[UiNotification_Position.BOTTOM_RIGHT]: {from: 'bottom', align: 'right'},
	};

	$.notify({message: null}, {
		// settings
		element: 'body',
		position: null,
		allow_dismiss: config.dismissable,
		newest_on_top: false,
		showProgressbar: config.showProgressBar,
		placement: position2placement[config.position],
		offset: 20,
		spacing: 10,
		z_index: 1031,
		delay: config.displayTimeInMillis,
		timer: 1000,
		mouse_over: null,
		animate: {
			enter: 'animated ' + Constants.ENTRANCE_ANIMATION_CSS_CLASSES[config.entranceAnimation],
			exit: 'animated ' + Constants.EXIT_ANIMATION_CSS_CLASSES[config.exitAnimation]
		},
		onShow: null,
		onShown: null,
		onClose: null,
		onClosed: null,
		template: `<div data-notify="container" class="col-xs-11 col-sm-4 col-lg-3" style="background-color: ${createUiColorCssString(config.backgroundColor)}" role="alert">
    <div data-notify="dismiss" style="background-image: url(/resources/window-close-grey.png)"></div>
    <div class="content">${html}</div>
    <div class="progress" data-notify="progressbar">
		<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>
	</div>
</div>`
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

export function getIconPath(context: TeamAppsUiContext, iconName: string, iconSize: number, ignoreRetina?: boolean): string {
	if (!iconName) {
		return null;
	}
	if (!ignoreRetina) {
		iconSize = context.isHighDensityScreen ? iconSize * 2 : iconSize;
	}
	if (iconSize > 128) { // there are currently no icons larger than 128px
		iconSize = 128;
	}
	if (iconName.indexOf("/") === 0) {
		return iconName; // hack for static resources instead of icons...
	}
	return context.config.iconPath + '/' + iconSize + '/' + iconName;
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

export function convertJavaDateTimeFormatToMomentDateTimeFormat(javaFormat: string): string {
	if (javaFormat == null) {
		return null;
	} else {
		return (moment() as any).toMomentFormatString(javaFormat);
	}
}

export function insertAtIndex($parent: Element, $child: Element, index: number) {
	let effectiveIndex = Math.min($parent.childElementCount, index);
	if (effectiveIndex === 0) {
		$parent.prepend($child);
	} else if (effectiveIndex === $parent.childElementCount) {
		$parent.insertAdjacentElement('beforeend', $child);
	} else {
		$parent.children[effectiveIndex].insertAdjacentElement('beforebegin', $child);
	}
}

export function maximizeComponent(component: UiComponent, maximizeAnimationCallback?: () => void) {
	const $parentDomElement = component.getMainDomElement().parentElement;
	const scrollTop = window.scrollY;
	const scrollLeft = window.scrollX;
	const offset = component.getMainDomElement().getBoundingClientRect();

	const changingCssProperties: ["position", "top", "left", "width", "height", "zIndex"] = ["position", "top", "left", "width", "height", "zIndex"];
	const style = component.getMainDomElement().style as CSSStyleDeclaration;
	const originalCssValues = changingCssProperties.reduce((properties, cssPropertyName) => {
		properties[cssPropertyName] = style[cssPropertyName];
		return properties;
	}, {} as { [x: string]: string });

	const animationStartCssValues = {
		top: (offset.top - scrollTop) + "px",
		left: (offset.left - scrollLeft) + "px",
		width: offset.width,
		height: offset.height,
	};
	Object.assign(component.getMainDomElement().style, {
		...animationStartCssValues
	});
	document.body.appendChild(component.getMainDomElement());
	component.getMainDomElement().classList.add("teamapps-component-maximized");
	$(component.getMainDomElement()).animate({
		top: "5px",
		left: "5px",
		width: (window.innerWidth - 10),
		height: (window.innerHeight - 10)
	}, 100, 'swing', () => {
		css(component.getMainDomElement(), {
			width: "calc(100% - 10px)",
			height: "calc(100% - 10px)"
		});
		maximizeAnimationCallback && maximizeAnimationCallback();
	});

	let restore = (restoreAnimationCallback?: () => void) => {
		$(component.getMainDomElement()).animate(animationStartCssValues, 100, 'swing', () => {
			Object.assign(component.getMainDomElement().style, originalCssValues);
			component.getMainDomElement().classList.remove("teamapps-component-maximized");
			$parentDomElement.appendChild(component.getMainDomElement());
			restoreAnimationCallback && restoreAnimationCallback();
		});
	};

	return restore;
}

export function flattenArray<T>(array: (T | T[])[]): T[] {
	return array.reduce(function (flat: T[], toFlatten: T | T[]) {
		return flat.concat(Array.isArray(toFlatten) ? flattenArray(toFlatten) : toFlatten);
	}, [] as any);
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

export function parseHtml<E extends HTMLElement>(htmlString: string): E {
	// let tagStartCount = (htmlString.match(/<\w+/g) || []).length;
	// let tagEndCount = (htmlString.match(/<\//g) || []).length;
	// if (tagStartCount > 1 && tagStartCount !== tagEndCount) {
	// 	throw new Error("HTML strings need to have explicit closing tags! " + htmlString);
	// }
	// const node: E = new DOMParser().parseFromString(htmlString, 'text/html').querySelector('html > * > *');
	// node.remove(); // detach from DOMParser <body>!
	// return node;

	// let tmpl = document.createElement('template');
	// tmpl.innerHTML = htmlString;
	// return tmpl.content.cloneNode(true).firstChild as E;

	return $(htmlString)[0] as E;
}

export function parseSvg<E extends Element>(htmlString: string): E {
	// let tagStartCount = (htmlString.match(/<\w+/g) || []).length;
	// let tagEndCount = (htmlString.match(/<\//g) || []).length;
	// if (tagStartCount !== tagEndCount) {
	// 	throw "SVG strings need to have explicit closing tags! " + htmlString;
	// }
	// const node: E = new DOMParser().parseFromString(htmlString, 'application/xml').getRootNode() as E;
	// node.remove(); // detach from DOMParser <body>!
	// return node;

	return $(htmlString)[0] as unknown as E;
}

export function prependChild(parent: Element, child: Element) {
	if (parent.childNodes.length > 0) {
		parent.insertBefore(child, parent.firstChild);
	} else {
		parent.appendChild(child);
	}
}

export function insertBefore(newNode: Element, referenceNode: Element) {
	referenceNode.parentNode.insertBefore(newNode, referenceNode);
}

export function insertAfter(newNode: Element, referenceNode: Element) {
	referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling);
}

export function outerWidthIncludingMargins(el: HTMLElement) {
	var width = el.offsetWidth;
	var style = getComputedStyle(el);
	width += parseInt(style.marginLeft) + parseInt(style.marginRight);
	return width;
}

export function outerHeightIncludingMargins(el: HTMLElement) {
	var height = el.offsetHeight;
	var style = getComputedStyle(el);
	height += parseInt(style.marginTop) + parseInt(style.marginBottom);
	return height;
}

export function closestAncestor(el: HTMLElement, selector: string, includeSelf = false) {
	let currentNode: HTMLElement = (includeSelf ? el : el.parentNode) as HTMLElement;
	while (currentNode) {
		if (currentNode.matches(selector)) {
			return currentNode;
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
			var reader = new FileReader();
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
	let matches: string[] = [];
	classList.forEach(function (className) {
		if (deleteDecider(className)) {
			matches.push(className);
		}
	});
	matches.forEach(function (value) {
		classList.remove(value);
	});
}

function animate(el: HTMLElement, animationClassNames: string[], animationDuration: number = 300, callback?: () => any) {
	let oldAnimationDurationValue = el.style.animationDuration;
	el.style.animationDuration = animationDuration + "ms";
	el.classList.add('animated', ...animationClassNames);

	function handleAnimationEnd() {
		el.classList.remove('animated', ...animationClassNames);
		el.removeEventListener('animationend', handleAnimationEnd);
		el.style.animationDuration = oldAnimationDurationValue;

		if (typeof callback === 'function') {
			callback();
		}
	}

	el.addEventListener('animationend', handleAnimationEnd);
}

export function animateCSS(el: HTMLElement, animationName: "bounce" | "flash" | "pulse" | "rubberBand" | "shake" | "headShake" | "swing" | "tada"
	| "wobble" | "jello" | "bounceIn" | "bounceInDown" | "bounceInLeft" | "bounceInRight" | "bounceInUp" | "bounceOut" | "bounceOutDown"
	| "bounceOutLeft" | "bounceOutRight" | "bounceOutUp" | "fadeIn" | "fadeInDown" | "fadeInDownBig" | "fadeInLeft" | "fadeInLeftBig" | "fadeInRight"
	| "fadeInRightBig" | "fadeInUp" | "fadeInUpBig" | "fadeOut" | "fadeOutDown" | "fadeOutDownBig" | "fadeOutLeft" | "fadeOutLeftBig" | "fadeOutRight"
	| "fadeOutRightBig" | "fadeOutUp" | "fadeOutUpBig" | "flipInX" | "flipInY" | "flipOutX" | "flipOutY" | "lightSpeedIn" | "lightSpeedOut" | "rotateIn"
	| "rotateInDownLeft" | "rotateInDownRight" | "rotateInUpLeft" | "rotateInUpRight" | "rotateOut" | "rotateOutDownLeft" | "rotateOutDownRight"
	| "rotateOutUpLeft" | "rotateOutUpRight" | "hinge" | "jackInTheBox" | "rollIn" | "rollOut" | "zoomIn" | "zoomInDown" | "zoomInLeft" | "zoomInRight"
	| "zoomInUp" | "zoomOut" | "zoomOutDown" | "zoomOutLeft" | "zoomOutRight" | "zoomOutUp" | "slideInDown" | "slideInLeft" | "slideInRight" | "slideInUp"
	| "slideOutDown" | "slideOutLeft" | "slideOutRight" | "slideOutUp" | "heartBeat", animationDuration: number = 300, callback?: () => any) {

	animate(el, [animationName], animationDuration, callback)
}


export function fadeOut(el: HTMLElement) {
	animateCSS(el, "fadeOut", 300, () => el.classList.add("hidden"));
}

export function fadeIn(el: HTMLElement) {
	el.classList.remove("hidden");
	animateCSS(el, "fadeIn");
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

export function pageTransition(outEl: HTMLElement, inEl: HTMLElement, pageTransition: UiPageTransition, animationDuration: number = 300, callback?: () => any) {
	let s = UiPageTransition[pageTransition].toLowerCase().replace(/_{1,1}([a-z])/g, (g0, g1) => g1.toUpperCase()) as keyof typeof pageTransitionAnimationPairs;
	animatePageTransition(outEl, inEl, s, animationDuration, callback);
}

export function css(el: HTMLElement, values: object) {
	Object.assign(el.style, values);
}

let lastPointerCoordinates: [number, number] = [0, 0];
document.body.addEventListener("pointermove", ev => lastPointerCoordinates = [ev.clientX, ev.clientY], {capture: true});
export function getLastPointerCoordinates() {
	return lastPointerCoordinates;
}