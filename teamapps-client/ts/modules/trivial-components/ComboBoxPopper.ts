import {createPopper, detectOverflow} from "@popperjs/core";
import ResizeObserver from "resize-observer-polyfill";

export function createComboBoxPopper($reference: Element, $dropdown: HTMLElement, referenceOutOfViewPortHandler: () => void) {
	return createPopper($reference, $dropdown, {
		placement: 'bottom',
		strategy: "fixed",
		modifiers: [
			{
				name: "flip",
				options: {
					fallbackPlacements: ['top']
				}
			}, {
				name: "preventOverflow"
			}, {
				name: 'hideDropdownIfFieldOutOfViewport',
				enabled: true,
				phase: 'main',
				requiresIfExists: ['offset'],
				fn: ({state}) => {
					// console.log("main: " + state.rects.reference.width);
					let sideObject = detectOverflow(state, {elementContext: "reference"});
					if (sideObject.left > state.rects.reference.width || sideObject.right > state.rects.reference.width
						|| sideObject.top > state.rects.reference.height || sideObject.bottom > state.rects.reference.height) {
						referenceOutOfViewPortHandler?.();
					}
				}
			}, {
				name: 'dropDownCornerSmoother',
				enabled: true,
				phase: 'write',
				fn: ({state}) => {
					// console.log("write: " + state.rects.reference.width);
					$reference.classList.toggle("dropdown-flipped", state.placement === 'top');
					$dropdown.classList.toggle("flipped", state.placement === 'top');

				}
			}, {
				name: 'forceCorrectDropDownWidth_EvenIf_TemporarilyOverflowedViewPortAndThereforeScrollbarsCausedResizeBeforeFlip',
				enabled: true,
				phase: 'afterWrite',
				fn: ({state}) => {
					$dropdown.style.width = state.elements.reference.getBoundingClientRect().width + "px";
				}
			}, {
				name: "observeReferenceModifier",
				enabled: true,
				phase: "main",
				effect: ({state, instance}) => {
					const RO_PROP = "__popperjsResizeObserver__";
					const {reference} = state.elements;
					(reference as any)[RO_PROP] = new ResizeObserver(() => instance.update());
					(reference as any)[RO_PROP].observe(reference);
					return () => {
						(reference as any)[RO_PROP].disconnect();
						delete (reference as any)[RO_PROP];
					};
				}
			}
		]
	});
}