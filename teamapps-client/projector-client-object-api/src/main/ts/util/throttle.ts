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
import {debounce} from "./debounce";

export function throttledMethod(delay: number) {
	return function <This, Args extends any[], Return>(
		originalMethod: (this: This, ...args: Args) => Return,
		context: ClassMethodDecoratorContext<This, (this: This, ...args: Args) => Return>
	) {
		return throttle(originalMethod, delay);
	}
}

export function loadSensitiveThrottling(minDelay: number, executionTimeDelayFactor: number, maxDelay = Number.MAX_SAFE_INTEGER) {
	return function<This, Args extends any[], Return>(
		originalMethod: (this: This, ...args: Args) => Return,
		context: ClassMethodDecoratorContext<This, (this: This, ...args: Args) => Return>
	) {
		return loadSensitiveThrottle(originalMethod, minDelay, executionTimeDelayFactor, maxDelay);
	};
}

/**
 *  Returns a function, whose execution is limited with a certain delay.
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

/**
 *  Returns a function, whose execution is dynamically throttled, depending on the actual load. The load is determined by the time it takes from the beginning of the execution
 *  to a queued task.
 *
 * @param func                      function to be throttled
 * @param minDelay                  minimum throttling delay
 * @param executionTimeDelayFactor  the factor to multiply with the actual execution time + queue task execution delay
 * @param maxDelay                  maximum throttling delay
 */
export function loadSensitiveThrottle(func: (...args: any[]) => any, minDelay: number, executionTimeDelayFactor: number, maxDelay = Number.MAX_SAFE_INTEGER): ((...args: any[]) => void) {
	if (minDelay <= 0) {
		minDelay = 0;
	}
	let lastDelay = minDelay;
	let nextDelay = minDelay;
	let currentTimeoutId: number;
	let lastExecutionTimestamp: number = new Date(0).valueOf();
	let lastArguments: { thisArg: any, args: IArguments };

	function execute() {
		let startTime = Date.now().valueOf();
		func.apply(lastArguments.thisArg, lastArguments.args);
		lastArguments = null;
		nextDelay = -1;
		setTimeout(() => {
			let queueAvailabilityTime = Date.now().valueOf() - startTime;
			nextDelay = Math.min(maxDelay, Math.max(minDelay, (lastDelay + queueAvailabilityTime * executionTimeDelayFactor) / 2));
			lastDelay = nextDelay;
			if (lastArguments != null) {
				runWrapped.apply(lastArguments.thisArg, lastArguments.args);
			}
		});
		lastExecutionTimestamp = Date.now().valueOf();
	}

	function runWrapped() {
		lastArguments = {thisArg: this, args: arguments};
		if (currentTimeoutId == null) {
			let now = Date.now().valueOf();
			if (nextDelay == -1) {
				return;
			} else if (now - lastExecutionTimestamp >= nextDelay) {
				execute();
			} else {
				currentTimeoutId = window.setTimeout(() => {
					execute();
					currentTimeoutId = null;
				}, nextDelay - (now - lastExecutionTimestamp))
			}
		}
	}

	return runWrapped;
}
