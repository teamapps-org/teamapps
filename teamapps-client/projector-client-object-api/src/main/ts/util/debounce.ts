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
export enum DebounceMode {
	// trigger the function on the leading edge
	IMMEDIATE,
	// trigger the function on the trailing edge
	LATER,
	// trigger the function on the leading edge and the trailing edge, but only if the debounced function has been called again
	BOTH
}

export function debouncedMethod(delay: number, mode = DebounceMode.LATER) {

	return function<This, Args extends any[], Return>(
		originalMethod: (this: This, ...args: Args) => Return,
		context: ClassMethodDecoratorContext<This, (this: This, ...args: Args) => Return>
	): (...args: Args) => void {
		 return debounce(originalMethod, delay, mode);
	}
}

/**
 *  Returns a function, that, as long as it continues to be invoked, will not be triggered.
 *  The function will be called after it stops being called for N milliseconds.
 *
 *  @see throttle
 */
export function debounce<Args extends any[]>(func: (...args: Args) => any, delay: number, mode = DebounceMode.LATER): (...args: Args) => void {
	let timeout: any;
	let needsToBeCalledLater: boolean;
	if (delay <= 0) {
		return func; // no debouncing
	} else {
		return function () {
			const context = this, args = arguments;
			const later = function () {
				timeout = null;
				if (needsToBeCalledLater) func.apply(context, args);
				needsToBeCalledLater = false;
			};
			const callNow = (mode === DebounceMode.IMMEDIATE || mode === DebounceMode.BOTH) && !timeout;
			needsToBeCalledLater = mode === DebounceMode.LATER || (mode === DebounceMode.BOTH && !!timeout);
			clearTimeout(timeout);
			timeout = setTimeout(later, delay);
			if (callNow) func.apply(context, args);
		};
	}
}


