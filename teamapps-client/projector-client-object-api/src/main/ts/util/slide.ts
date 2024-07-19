/* SLIDE UP */
import {applyCss, extractCssValues} from ".";

type Options = { hiddenClass?: string, duration?: number };
const DEFAULT_OPTIONS = {
	hiddenClass: 'hidden',
	duration: 200
};

export function slideUp(target: HTMLElement, options: Options = {}): Promise<void> {
	options = {...DEFAULT_OPTIONS, ...options};

	if (target.classList.contains(options.hiddenClass)) { // already hidden!
		return Promise.resolve();
	}

	const originalCssValues = extractCssValues(target, [
		"transitionProperty",
		"transitionDuration",
		"overflow",
		"height",
		"paddingTop",
		"paddingBottom",
		"marginTop",
		"marginBottom",
	]);

	applyCss(target, {
		transitionProperty: 'height, margin, padding',
		transitionDuration: options.duration + 'ms',
		height: target.offsetHeight + 'px'
	});
	target.offsetHeight;

	applyCss(target, {
		overflow: 'hidden',
		height: '0',
		paddingTop: '0',
		paddingBottom: '0',
		marginTop: '0',
		marginBottom: '0'
	});

	return new Promise((resolve) => setTimeout(resolve, options.duration))
		.then(unused => {
			applyCss(target, originalCssValues);
			target.classList.add('hidden');
		});
}

export function slideDown(target: HTMLElement, options: Options = {}): Promise<void> {
	options = {...DEFAULT_OPTIONS, ...options};

	if (!target.classList.contains(options.hiddenClass)) { // already expanded!
		return Promise.resolve();
	}

	const originalCssValues = extractCssValues(target, [
		"transitionProperty",
		"transitionDuration",
		"overflow",
		"height",
		"paddingTop",
		"paddingBottom",
		"marginTop",
		"marginBottom",
	]);

	target.classList.remove(options.hiddenClass);
	let height = target.offsetHeight;
	applyCss(target, {
		overflow: 'hidden',
		height: '0',
		paddingTop: '0',
		paddingBottom: '0',
		marginTop: '0',
		marginBottom: '0'
	});
	target.offsetHeight;

	applyCss(target, {
		height: height + 'px',
		paddingTop: originalCssValues['paddingTop'],
		paddingBottom: originalCssValues['paddingBottom'],
		marginTop: originalCssValues['marginTop'],
		marginBottom: originalCssValues['marginBottom']
	});
	return new Promise((resolve) => setTimeout(resolve, options.duration))
		.then(unused => {
			applyCss(target, originalCssValues);
		});
}