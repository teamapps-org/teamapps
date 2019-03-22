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
import "bootstrap-notify";
import * as log from "loglevel";
import {UiComponentConfig} from "../generated/UiComponentConfig";
import {UiComponent} from "./UiComponent";
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

export type RenderingFunction = (data: any) => string;

export type Renderer = {
	render: RenderingFunction,
	template: UiTemplateConfig
};

export type ImageRenderer = (imageIdentifier: string) => string;

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

	public static POINTER_EVENTS = window.navigator.pointerEnabled ? {
		start: 'pointerdown',
		move: 'pointermove',
		end: 'pointerup'
	} : window.navigator.msPointerEnabled ? {
		start: 'MSPointerDown',
		move: 'MSPointerMove',
		end: 'MSPointerUp'
	} : {
		start: 'mousedown touchstart',
		move: 'mousemove touchmove',
		end: 'mouseup touchend'
	};

	static get SCROLLBAR_WIDTH() {
		if (Constants._SCROLLBAR_WIDTH == null) {
			Constants._SCROLLBAR_WIDTH = Constants.calculateScrollbarWidth();
		}
		return Constants._SCROLLBAR_WIDTH;
	}

	private static calculateScrollbarWidth() {
		const $div = $(`<div id="ASDF" style="width: 100px; height: 100px; position: absolute; top: -10000px">`)
			.appendTo(document.body);
		const widthNoScroll = $div[0].clientWidth;
		$div.css("overflow-y", "scroll");
		const widthWithScroll = $div[0].clientWidth;
		$div.detach();
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

export function doOnceOnClickOutsideElement(elements: JQuery, handler: (e?: JQueryMouseEventObject) => any, useCapture = false): ClickOutsideHandle {
	const eventType = "mousedown";
	let handlerWrapper = (e: JQueryMouseEventObject) => {
		if (!elements.toArray().some((element) => e.target === element || $(e.target).parents().toArray().indexOf(element) !== -1)) {
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

export function applyDisplayMode($outer: JQuery, $inner: JQuery, displayMode: UiPageDisplayMode | any, options?: {
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
		if ($inner[0] instanceof HTMLImageElement) {
			let imgElement = <HTMLImageElement> $($inner)[0];
			options.innerPreferedDimensions = {
				width: imgElement.naturalWidth,
				height: imgElement.naturalHeight
			}
		} else {
			$inner.css({
				width: "100%",
				height: "100%"
			});
			return;
		}
	}
	options.zoomFactor = options.zoomFactor || 1;
	if (options.padding == null) {
		options.padding = parseInt($outer.css("padding-left")) || 0;
	}

	let availableWidth = $outer.width() - 2 * options.padding;
	let availableHeight = $outer.height() - 2 * options.padding;
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
		$inner.css({
			width: width + "px",
			height: "auto"
		});
	} else if (displayMode === UiPageDisplayMode.FIT_HEIGHT) {
		let height = Math.floor(availableHeight * options.zoomFactor);
		if (options.considerScrollbars && options.zoomFactor <= 1 && height * imageAspectRatio > availableWidth) {
			// There will be a horizontal scroll bar, so make sure the width will not result in a vertical scrollbar, too
			// NOTE: Chrome still shows scrollbars sometimes. https://bugs.chromium.org/p/chromium/issues/detail?id=240772&can=2&start=0&num=100&q=&colspec=ID%20Pri%20M%20Stars%20ReleaseBlock%20Component%20Status%20Owner%20Summary%20OS%20Modified&groupby=&sort=
			height = Math.min(height, availableHeight - Constants.SCROLLBAR_WIDTH);
		}
		$inner.css({
			width: "auto",
			height: height + "px"
		});
	} else if (displayMode === UiPageDisplayMode.FIT_SIZE) {
		if (imageAspectRatio > viewPortAspectRatio) {
			let width = Math.floor(availableWidth * options.zoomFactor);
			$inner.css({
				width: width + "px",
				height: imageAspectRatio ? width / imageAspectRatio : "auto"
			});
		} else {
			let height = Math.floor(availableHeight * options.zoomFactor);
			$inner.css({
				width: imageAspectRatio ? height * imageAspectRatio : "auto",
				height: height + "px"
			});
		}
	} else if (displayMode === UiPageDisplayMode.COVER) {
		if (imageAspectRatio < viewPortAspectRatio) {
			$inner.css({
				width: Math.floor(availableWidth * options.zoomFactor) + "px",
				height: "auto"
			});
		} else {
			$inner.css({
				width: "auto",
				height: Math.floor(availableHeight * options.zoomFactor) + "px"
			});
		}
	} else { // ORIGINAL_SIZE
		$inner.css({
			width: (options.innerPreferedDimensions.width * options.zoomFactor) + "px",
			height: "auto"
		});
	}
}

// ===== FULLSCREEN HANDLING ===== (maybe extract this to own file...)

document.addEventListener("fullscreenchange", fullScreenChangeHandler);
document.addEventListener("webkitfullscreenchange", fullScreenChangeHandler);
document.addEventListener("mozfullscreenchange", fullScreenChangeHandler);
document.addEventListener("MSFullscreenChange", fullScreenChangeHandler);

export function enterFullScreen(component: UiComponent<UiComponentConfig> | JQuery | Element) {
	let element: Element;
	if (component instanceof UiComponent) {
		element = component.getMainDomElement()[0];
	} else {
		element = $(component)[0];
	}
	$(element).addClass("fullscreen");
	if (element.requestFullscreen) {
		element.requestFullscreen();
	} else if ((element as any).msRequestFullscreen) {
		(element as any).msRequestFullscreen();
	} else if ((element as any).mozRequestFullScreen) {
		(element as any).mozRequestFullScreen();
	} else if (element.webkitRequestFullscreen) {
		element.webkitRequestFullscreen();
	}
}

function fullScreenChangeHandler(e: Event) {
	if (!getFullScreenElement()) {
		$(e.target).removeClass("fullscreen");
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

export function hexColorStringToRgb(hexColorString: string) {
	const rgb = parseInt(hexColorString.substring(1), 16);   // convert rrggbb to decimal
	let r;
	let g;
	let b;
	if (hexColorString.length === 7) {
		r = (rgb >> 16) & 0xff;  // extract red
		g = (rgb >> 8) & 0xff;  // extract green
		b = (rgb >> 0) & 0xff;  // extract blue
	} else {
		r = (((rgb >> 8) & 0xf) << 4) + ((rgb >> 8) & 0xf);  // extract red
		g = (((rgb >> 4) & 0xf) << 4) + ((rgb >> 4) & 0xf);  // extract green
		b = ((rgb & 0xf) << 4) + (rgb & 0xf);  // extract blue
		logger.trace(`rgb: ${r} ${g} ${b}`);
	}
	return {r, g, b};
}

export function adjustIfColorTooBright(c: string, maxLuma256: number = 210) {
	if (c.indexOf('#') !== 0 || (c.length !== 4 && c.length !== 7)) {
		return c; // do not normalize. performance is more important than supporting any kind of color coding.
	}
	const rgb = hexColorStringToRgb(c);
	const luma = 0.2126 * rgb.r + 0.7152 * rgb.g + 0.0722 * rgb.b; // per ITU-R BT.709
	logger.trace("luma: " + luma);
	if (luma > maxLuma256) {
		let adjustionFactor = (maxLuma256 / luma);
		return `rgb(${Math.floor(rgb.r * adjustionFactor)}, ${Math.floor(rgb.g * adjustionFactor)}, ${Math.floor(rgb.b * adjustionFactor)})`;
	} else {
		return c;
	}
}

export function positionDropDown($button: JQuery, $dropDown: JQuery, {
	viewPortPadding = 10,
	minHeightBeforeFlipping = 200
}) {
	$dropDown.removeClass("pseudo-hidden");

	let maxHeight = window.innerHeight - ($button.offset().top + $button.outerHeight()) - viewPortPadding;
	let maxFlippedHeight = $button.offset().top - viewPortPadding;
	let flip = maxHeight < minHeightBeforeFlipping && maxFlippedHeight > maxHeight;

	(<any>$dropDown).position({
		my: "left " + (flip ? "bottom" : "top"),
		at: "left " + (flip ? "top" : "bottom"),
		of: $button,
		collision: "fit none"
	});
	$dropDown.find('> .background-color-div, > .background-color-div > *').css({
		"max-height": (flip ? maxFlippedHeight : maxHeight) + "px"
	});
	if ($dropDown[0].offsetWidth > window.innerWidth - 2 * viewPortPadding) {
		$dropDown.css("width", "auto");
		$dropDown.css("left", viewPortPadding + "px");
		$dropDown.css("right", viewPortPadding + "px");
	} else if ($dropDown[0].offsetLeft + $dropDown[0].offsetWidth > window.innerWidth - viewPortPadding) {
		$dropDown.css("left", "auto");
		$dropDown.css("right", viewPortPadding + "px");
	}
}

export function manipulateWithoutTransitions($element: JQuery, action: Function, transitionEnabled = false) {
	if (!transitionEnabled) {
		$element.addClass('notransition');
	}
	action();
	if (!transitionEnabled) {
		$element[0].offsetHeight; // Trigger a reflow, flushing the CSS changes
		$element.removeClass('notransition');
	}
}

export function focusNextByTabIndex(navigatableElements: string | HTMLElement[] | JQuery, navDirection: -1 | 1): boolean {
	let selectables: HTMLElement[] = $(navigatableElements).toArray().sort((e1: HTMLElement, e2: HTMLElement) => e2.tabIndex - e1.tabIndex);
	let current = document.activeElement;
	let currentIndex = selectables.indexOf(current as HTMLElement);
	log.getLogger("Common").trace("selectables: " + selectables.length + "; current: " + currentIndex);
	let newIndex = currentIndex + navDirection;
	if (newIndex > 0 && newIndex < selectables.length) {
		logger.trace(selectables[newIndex]);
		selectables[newIndex].focus();
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

export function enableScrollViaDragAndDrop($scrollContainer: JQuery) {
	function mousedownHandler(startEvent: MouseEvent) {
		$scrollContainer.css("cursor", "move");
		startEvent.preventDefault();
		let initialScrollLeft = $scrollContainer.scrollLeft();
		let initialScrollTop = $scrollContainer.scrollTop();
		const moveEvent = 'mousemove';
		const endEvent = 'mouseup';
		$(document).on(moveEvent, (e) => {
			let diffX = e.pageX - startEvent.pageX;
			let diffY = e.pageY - startEvent.pageY;
			$scrollContainer.scrollLeft(initialScrollLeft - diffX);
			$scrollContainer.scrollTop(initialScrollTop - diffY);
		});
		$(document).on(endEvent, (event) => {
			$(document).unbind(moveEvent);
			$(document).unbind(endEvent);
			$scrollContainer.css("cursor", "");
		});
	}

	$scrollContainer[0].addEventListener("mousedown", (e) => mousedownHandler(e));
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

export function insertAtIndex($parent: JQuery | Element, $child: JQuery | Element | string, index: number) {
	let effectiveIndex = Math.min($($parent).children().length, index);
	if (effectiveIndex === 0) {
		$($parent).prepend($child);
	} else {
		$($parent).find(`>:nth-child(${effectiveIndex})`).after($child);
	}
}

export function maximizeComponent(component: UiComponent, maximizeAnimationCallback?: () => void) {
	const $parentDomElement = component.getMainDomElement().parent();
	const scrollTop = $(document).scrollTop();
	const scrollLeft = $(document).scrollLeft();
	const offset = component.getMainDomElement().offset();

	const changingCssProperties: (keyof CSSStyleDeclaration)[] = ["position", "top", "left", "width", "height", "zIndex"];
	const style = component.getMainDomElement()[0].style as CSSStyleDeclaration;
	const originalCssValues = changingCssProperties.reduce((properties, cssPropertyName) => {
		properties[cssPropertyName] = style[cssPropertyName];
		return properties;
	}, {} as { [x: string]: string });

	const animationStartCssValues = {
		top: (offset.top - scrollTop) + "px",
		left: (offset.left - scrollLeft) + "px",
		width: component.getMainDomElement().width(),
		height: component.getMainDomElement().height(),
	};
	component.getMainDomElement().css({
		position: 'fixed',
		"z-index": 1000000,
		...animationStartCssValues
	}).appendTo(
		$('body')
	).addClass(
		"teamapps-component-maximized"
	).animate({
		top: "5px",
		left: "5px",
		width: ($(window).width() - 10),
		height: ($(window).height() - 10)
	}, 100, 'swing', () => {
		component.getMainDomElement().css({
			width: "calc(100% - 10px)",
			height: "calc(100% - 10px)"
		});
		maximizeAnimationCallback && maximizeAnimationCallback();
	});

	let restore = (restoreAnimationCallback?: () => void) => {
		component.getMainDomElement().animate(animationStartCssValues, 100, 'swing', () => {
			component.getMainDomElement().css(originalCssValues);
			component.getMainDomElement().removeClass("teamapps-component-maximized");
			component.getMainDomElement().appendTo($parentDomElement);
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
	const node: E = new DOMParser().parseFromString(htmlString, 'text/html').querySelector('body > *');
	node.remove(); // detach from DOMParser <body>!
	return node;
}

export function prependChild(parent: Element, child: Element) {
	if (parent.childNodes.length > 0) {
		parent.insertBefore(child, parent.firstChild);
	} else {
		parent.appendChild(child);
	}
}

export function outerWidthIncludingMargins(el: HTMLElement) {
	var width = el.offsetWidth;
	var style = getComputedStyle(el);
	width += parseInt(style.marginLeft) + parseInt(style.marginRight);
	return width;
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
		return new Promise<string>((resolve, reject) => {
			var reader = new FileReader();
			reader.onloadend = function () {
				resolve(reader.result);
			};
			reader.onerror = function () {
				reject("Error while reading file.");
			};
			reader.readAsDataURL(file);
		});
	} else {
		return Promise.reject("Not a known image file type.");
	}
}
