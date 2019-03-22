/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import {debounce} from "./debounce";

export function throttledMethod(delay: number) {
	return function (target: any, propertyKey: string, descriptor: PropertyDescriptor) {
		let oldMethod = descriptor.value;
		descriptor.value = throttle(oldMethod, delay);
	};
}

/**
 *  Returns a function, whom's execution is limited with a certain delay.
 *  When the returned function gets called multiple times within a delay cycle, only the last invocation will be executed.
 *
 *  @see debounce
 */
export function throttle(func: (...args: any[]) => any, delay: number): ((...args: any[]) => void) {
	if (delay <= 0) {
		return func; // no throttling
	}
	let timeout: any;
	let lastExecutionTimestamp: number = new Date(0).valueOf();
	let lastArguments: { thisArg: any, args: IArguments };

	return function () {
		if (timeout == null) {
			if (Date.now() - lastExecutionTimestamp >= delay) {
				func.apply(this, arguments);
				lastExecutionTimestamp = Date.now().valueOf();
			} else {
				lastArguments = {thisArg: this, args: arguments};
				timeout = setTimeout(() => {
					func.apply(lastArguments.thisArg, lastArguments.args);
					timeout = null;
					lastExecutionTimestamp = Date.now().valueOf();
				}, delay - (Date.now().valueOf() - lastExecutionTimestamp))
			}
		} else {
			lastArguments = {thisArg: this, args: arguments};
		}
	};
}


