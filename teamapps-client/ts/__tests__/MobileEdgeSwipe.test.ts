/** @jest-environment jsdom */
import {MobileEdgeSwipe} from '../modules/util/MobileEdgeSwipe';

let element: HTMLElement, target: HTMLElement, swipe: MobileEdgeSwipe, navigate: jest.Mock, context: object;
beforeEach(() => {
	document.body.innerHTML = '<div id="surface"><div id="target"></div></div>';
	element = document.getElementById('surface'); target = document.getElementById('target');
	element.getBoundingClientRect = () => ({left: 0, width: 400, height: 800} as DOMRect);
	navigate = jest.fn(); context = {};
	swipe = new MobileEdgeSwipe(element, () => context, navigate);
});
afterEach(() => swipe.destroy());
function touch(type: string, x: number, y = 100, count = 1) {
	const event = new Event(type, {bubbles: true, cancelable: true});
	const point = {identifier: 42, clientX: x, clientY: y};
	Object.defineProperties(event, {
		touches: {value: type === 'touchend' ? [] : Array.from({length: count}, () => point)},
		changedTouches: {value: [point]}
	});
	target.dispatchEvent(event);
	expect(event.defaultPrevented).toBe(false);
}
test.each([[5, 200, -1], [395, 180, 1]])('edge %s only navigates on release', (start, end, direction) => {
	touch('touchstart', start); touch('touchmove', end);
	expect(navigate).not.toHaveBeenCalled(); touch('touchend', end);
	expect(navigate).toHaveBeenCalledTimes(1); expect(navigate).toHaveBeenCalledWith(direction);
});
test.each([[100, 350, 100], [5, 55, 100], [5, 200, 250], [395, 399, 100]])('rejects non-gesture %s/%s/%s', (start, end, y) => {
	touch('touchstart', start); touch('touchmove', end, y); touch('touchend', end, y);
	expect(navigate).not.toHaveBeenCalled();
});
test('system cancellation and multitouch never navigate', () => {
	touch('touchstart', 5); touch('touchcancel', 200); touch('touchend', 200);
	touch('touchstart', 5); touch('touchmove', 100, 100, 2); touch('touchend', 200);
	expect(navigate).not.toHaveBeenCalled();
});
test.each(['UiMap2', 'UiNetworkGraph', 'UiTimeGraph', 'noUi-target', 'UiButton', 'UiCheckBox', 'UiToolButton', 'resizer'])('excludes %s', name => {
	target.className = name; touch('touchstart', 5); touch('touchend', 200);
	expect(navigate).not.toHaveBeenCalled();
});
test('excludes controls and horizontal scroll containers', () => {
	target.innerHTML = '<input>'; target = target.firstElementChild as HTMLElement;
	touch('touchstart', 5); touch('touchend', 200);
	target = document.getElementById('target'); target.style.overflowX = 'auto';
	Object.defineProperties(target, {scrollWidth: {value: 800}, clientWidth: {value: 400}});
	touch('touchstart', 5); touch('touchend', 200);
	expect(navigate).not.toHaveBeenCalled();
});
test('context changes, vertical excursions and destroy cancel pending gestures', () => {
	touch('touchstart', 5); context = {}; touch('touchend', 200);
	touch('touchstart', 5); touch('touchmove', 100, 180); touch('touchend', 200);
	touch('touchstart', 5); swipe.destroy(); touch('touchend', 200);
	expect(navigate).not.toHaveBeenCalled();
});

test('resize during a gesture invalidates its edge and distance', () => {
	touch('touchstart', 5);
	element.getBoundingClientRect = () => ({left: 0, width: 800, height: 400} as DOMRect);
	touch('touchend', 250);
	expect(navigate).not.toHaveBeenCalled();
});
