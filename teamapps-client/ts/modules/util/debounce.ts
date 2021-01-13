/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
import {throttle} from "./throttle";

export enum DebounceMode {
	// trigger the function on the leading edge
	IMMEDIATE,
	// trigger the function on the trailing edge
	LATER,
	// trigger the function on the leading edge and the trailing edge, but only if the debounced function has been called again
	BOTH
}

export function debouncedMethod(delay: number, mode = DebounceMode.LATER) {
	return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
		let oldMethod = descriptor.value;
		descriptor.value = function () {
			if (this['__debounced_' + propertyKey] == null) {
				this['__debounced_' + propertyKey] = debounce(oldMethod, delay, mode);
			}
			this['__debounced_' + propertyKey].apply(this, arguments);
		}
	};
}

/**
 *  Returns a function, that, as long as it continues to be invoked, will not be triggered.
 *  The function will be called after it stops being called for N milliseconds.
 *
 *  @see throttle
 */
export function debounce(func: (...args: any[]) => any, delay: number, mode = DebounceMode.LATER): ((...args: any[]) => void) {
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


