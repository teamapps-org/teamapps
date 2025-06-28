import {autoUpdate, computePosition, flip, hide, shift, size, type Strategy} from "@floating-ui/dom";

type Options = { padding?: number, strategy?: Strategy };
let DEFAULT_OPTIONS: Options = {
	padding: 5,
	strategy: 'fixed',
};

export function positionDropdownWithAutoUpdate(reference: Element, dropdown: HTMLElement, options: Options = {}): () => any {
	options = {...DEFAULT_OPTIONS, ...options};
	return autoUpdate(reference, dropdown, () => positionDropdown(reference, dropdown, options));
}

export function positionDropdown(reference: Element, dropdown: HTMLElement, options: Options = {}) {
	options = {...DEFAULT_OPTIONS, ...options};
	computePosition(reference, dropdown, {
		placement: 'bottom-start',
		strategy: options.strategy, // TODO ################################ check !!!
		middleware: [
			hide(),
			flip(),
			size({
				apply({rects, elements, availableHeight, placement}) {
					// const isFlipped = placement.indexOf('bottom') === -1;
					Object.assign(elements.floating.style, {
						width: `${rects.reference.width}px`,
						maxHeight: `${Math.ceil(availableHeight - options.padding)}px`
					});
				}
			}),
			shift({padding: 5})
		],
	}).then((values) => {
		console.log(values.x, values.y);
		Object.assign(dropdown.style, {
			left: `${(values.x)}px`,
			top: `${(values.y)}px`,
			visibility: values.middlewareData.hide.referenceHidden ? 'hidden' : null,
			pointerEvents: values.middlewareData.hide.referenceHidden ? 'none' : null
		});
	});
}