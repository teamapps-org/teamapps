/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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

export type MobileSwipeDirection = -1 | 1;

// Interactive horizontal surfaces and controls retain their complete native behavior.
const EXCLUDED = '.UiMap, .UiMap2, .UiNetworkGraph, .UiTimeGraph, iframe, input, textarea, select, button, a, '
	+ '[contenteditable]:not([contenteditable="false"]), [role="slider"], [draggable="true"], '
	+ '.UiButton, .UiCheckBox, .UiToolButton, .UiNumberField .slider, [role="button"], .resizer, .noUi-target, .ui-resizable-handle, .slick-resizable-handle, video, audio, .mejs__container, '
	+ '[data-teamapps-disable-edge-swipe]';

interface Gesture {
	id: number; x: number; y: number; time: number; width: number; height: number;
	edge: MobileSwipeDirection; context: unknown;
}

/** Passive recognition only. No capture of pointers, preventDefault or touch-action overrides. */
export class MobileEdgeSwipe {
	private gesture: Gesture;
	private readonly options = {passive: true, capture: true};

	constructor(private readonly element: HTMLElement, private readonly context: () => unknown,
				private readonly navigate: (direction: MobileSwipeDirection) => void) {
		element.addEventListener('touchstart', this.start, this.options);
		element.addEventListener('touchmove', this.move, this.options);
		element.addEventListener('touchend', this.end, this.options);
		element.addEventListener('touchcancel', this.cancel, this.options);
	}

	public cancel = (): void => { this.gesture = null; };

	public destroy(): void {
		this.cancel();
		this.element.removeEventListener('touchstart', this.start, true);
		this.element.removeEventListener('touchmove', this.move, true);
		this.element.removeEventListener('touchend', this.end, true);
		this.element.removeEventListener('touchcancel', this.cancel, true);
	}

	private start = (event: TouchEvent): void => {
		this.cancel();
		const context = this.context();
		if (context == null || event.touches.length !== 1 || !(event.target instanceof Element)) return;
		if (event.target.closest(EXCLUDED)) return;
		for (let node = event.target; node && this.element.contains(node); node = node.parentElement) {
			const overflow = getComputedStyle(node).overflowX;
			if (node.scrollWidth > node.clientWidth + 2 && /auto|scroll/.test(overflow)) return;
		}
		const rect = this.element.getBoundingClientRect();
		const touch = event.touches[0];
		const x = touch.clientX - rect.left;
		if (!rect.width || x < 0 || x > rect.width) return;
		const edge = x <= rect.width * .07 ? -1 : x >= rect.width * .93 ? 1 : null;
		if (edge == null) return;
		this.gesture = {id: touch.identifier, x: touch.clientX, y: touch.clientY, time: event.timeStamp,
			width: rect.width, height: rect.height, edge, context};
	};

	private move = (event: TouchEvent): void => {
		const gesture = this.gesture;
		if (!gesture) return;
		const touch = Array.from(event.touches).find(t => t.identifier === gesture.id);
		if (event.touches.length !== 1 || !touch || this.context() !== gesture.context
			|| Math.abs(touch.clientY - gesture.y) > 40) this.cancel();
	};

	private end = (event: TouchEvent): void => {
		const gesture = this.gesture;
		this.cancel();
		if (!gesture || event.touches.length || this.context() !== gesture.context) return;
		const touch = Array.from(event.changedTouches).find(t => t.identifier === gesture.id);
		const rect = this.element.getBoundingClientRect();
		if (!touch || rect.width !== gesture.width || rect.height !== gesture.height) return;
		const dx = touch.clientX - gesture.x;
		const dy = touch.clientY - gesture.y;
		if (event.timeStamp - gesture.time <= 1000 && -gesture.edge * dx >= Math.max(60, gesture.width * .35)
			&& Math.abs(dx) >= Math.abs(dy) * 3 && Math.abs(dy) <= 40) this.navigate(gesture.edge);
	};
}
