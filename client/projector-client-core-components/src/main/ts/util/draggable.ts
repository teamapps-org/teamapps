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

type Options = {
	validDragStartDecider?: (e: PointerEvent & TouchEvent) => boolean
	dragStart: (e: PointerEvent & TouchEvent) => void,
	drag: (e: PointerEvent & TouchEvent, eventData: EventData) => void,
	dragEnd: (e: PointerEvent & TouchEvent, eventData: EventData) => void,
};

type EventData = {
	x: number;
	y: number
	deltaX: number;
	deltaY: number;
};

const POINTER_EVENTS = {
	start: 'pointerdown',
	move: 'pointermove',
	end: 'pointerup'
};

export function draggable($elements: HTMLElement | HTMLElement[] | NodeListOf<HTMLElement>, callbacks: Options) {
	let $els: HTMLElement[];
	$els = $elements instanceof NodeList ? [].slice.call($elements) : [$elements].flat();
	$els.forEach($element => {
		$element.addEventListener(`${POINTER_EVENTS.start}`, (e: PointerEvent & TouchEvent) => {
			const isSpecialMouseButton = e.button != null && e.button !== 0;
			const isValidDragStart = callbacks.validDragStartDecider != null ? callbacks.validDragStartDecider(e) : !isSpecialMouseButton
			if (!isValidDragStart) {
				return;
			}

			callbacks.dragStart(e);
			const startPosition = getEventCoordinates(e);
			const oldTouchAction = $element.style.touchAction;
			$element.style.touchAction = 'none'; // important so the browser does not cancel the move (for native scrolling purposes. See https://stackoverflow.com/a/48254578/524913

			let moveHandler = (e: any) => {
				callbacks.drag(e, createEventData(e, startPosition));
			};
			document.addEventListener(`${POINTER_EVENTS.move}`, moveHandler);

			let endHandler = () => {
				document.removeEventListener(`${POINTER_EVENTS.move}`, moveHandler);
				document.removeEventListener(`${POINTER_EVENTS.end}`, endHandler);
				callbacks.dragEnd(e, createEventData(e, startPosition));
				$element.style.touchAction = oldTouchAction;
			};
			document.addEventListener(`${POINTER_EVENTS.end}`, endHandler);
		});
	})
}

function getEventCoordinates(e: PointerEvent & TouchEvent) {
	return {
		x: (e.clientX || e.touches[0].clientX),
		y: (e.clientY || e.touches[0].clientY)
	};
}

function createEventData(e: PointerEvent & TouchEvent, startPosition: { x: number; y: number }): EventData {
	let coordinates = getEventCoordinates(e);
	return {
		x: coordinates.x,
		y: coordinates.y,
		deltaX: coordinates.x - startPosition.x,
		deltaY: coordinates.y - startPosition.y
	};
}
