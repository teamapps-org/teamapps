import {closestAncestor, closestAncestorMatching} from "./dom-util";

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