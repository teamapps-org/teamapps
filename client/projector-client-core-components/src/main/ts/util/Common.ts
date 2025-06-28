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
	type Component,
	type EntranceAnimation,
	type ExitAnimation,
	extractCssValues,
	type PageTransition,
	parseHtml,
	type RepeatableAnimation
} from "projector-client-object-api";

let _SCROLLBAR_WIDTH: number;

export function getScrollbarWidth() {
	if (_SCROLLBAR_WIDTH == null) {
		_SCROLLBAR_WIDTH = calculateScrollbarWidth();
	}
	return _SCROLLBAR_WIDTH;
}

function calculateScrollbarWidth() {
	const $div = parseHtml(`<div id="ASDF" style="width: 100px; height: 100px; position: absolute; top: -10000px">`);
	document.body.appendChild($div);
	const widthNoScroll = $div.clientWidth;
	$div.style.overflowY = "scroll";
	const widthWithScroll = $div.clientWidth;
	$div.remove();
	return widthNoScroll - widthWithScroll;
}

export function escapeHtml(string: string): string {
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
	return String(string).replace(/[&<>"'`=\/]/g, function fromEntityMap(s: string) {
		return entityMap[s] as string;
	});
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

function transition(el: HTMLElement, targetValues: { [style: string]: string }, animationDuration: number = 300, callback?: () => any) {
	const changingCssProperties = Object.keys(targetValues);
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