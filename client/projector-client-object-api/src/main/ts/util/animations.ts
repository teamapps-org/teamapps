import {EntranceAnimation, ExitAnimation, PageTransition} from "../generated";

function animate(el: HTMLElement, animationClassNames: string[], animationDuration: number = 300, callback?: () => any) {
	if (animationClassNames == null || animationClassNames.length == 0) {
		console.log("directly calling callback");
		callback();
		return;
	}
	if (!document.body.contains(el)) {
		console.warn("Cannot animate detached element! Will fire callback directly.");
		callback && callback();
		return;
	}
	console.log("setting animation duration");
	let oldAnimationDurationValue = el.style.animationDuration;
	el.style.animationDuration = animationDuration + "ms";
	el.classList.add(...animationClassNames);
	console.log("added animation classnames " + animationClassNames + " to ", el);

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
	animateCSS(el, ExitAnimation.FADE_OUT, 300, () => el.classList.add("hidden"));
}

export function fadeIn(el: HTMLElement) {
	el.classList.remove("hidden");
	animateCSS(el, EntranceAnimation.FADE_IN);
}

export function pageTransition(outEl: HTMLElement, inEl: HTMLElement, pageTransition: PageTransition, animationDuration: number = 300, callback?: () => any) {
	let cssClasses = pageTransition.split("-vs-").map(s => "pt-" + s);
	let animationCallbackCount = 0;

	function invokeCallbackIfBothReturned() {
		animationCallbackCount++;
		if (animationCallbackCount == 2) {
			callback();
		}
	}

	if (outEl != null) {
		animate(outEl, [cssClasses[0]], animationDuration, invokeCallbackIfBothReturned);
	} else {
		animationCallbackCount++;
	}
	if (inEl != null) {
		animate(inEl, [cssClasses[1]], animationDuration, invokeCallbackIfBothReturned);
	} else {
		animationCallbackCount++;
	}
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

export function manipulateWithoutTransitions($element: HTMLElement, action: (el: HTMLElement) => void, transitionEnabled = false) {
	if (!transitionEnabled) {
		$element.classList.add('notransition');
	}
	action($element);
	if (!transitionEnabled) {
		$element.offsetHeight; // Trigger a reflow, flushing the CSS changes
		$element.classList.remove('notransition');
	}
}