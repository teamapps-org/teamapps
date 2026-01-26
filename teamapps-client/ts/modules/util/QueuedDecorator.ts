/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
/**
 * Wraps an async function so that every call is queued (FIFO) until fn finishes
 *
 * @param fn your async function
 * @returns a scheduler with the same parameters, returning the original promise
 */
function makeQueue<T extends any[], R>(
	fn: (...args: T) => Promise<R>,
): (...args: T) => Promise<R> {
	interface IJob {
		args: T;
		resolvers: Array<{ resolve: (v: R) => void; reject: (e: any) => void; }>;
	}

	let running = false;
	const queue: IJob[] = [];
	const run = (job: IJob) => {
		running = true;
		fn(...job.args)
			.then(res => job.resolvers.forEach(r => r.resolve(res)), err => job.resolvers.forEach(r => r.reject(err)))
			.finally(() => {
				const next = queue.shift();
				running = false;
				if (next) {
					run(next);
				}
			});
	};

	return (...args: T): Promise<R> => {
		return new Promise<R>((resolve, reject) => {
			if (running) {
				queue.push({args, resolvers: [{resolve, reject}]});
			} else {
				// idle → run immediately
				run({args, resolvers: [{resolve, reject}]});
			}
		});
	};
}

/**
 * Decorator to queue a method call if it is called while the method is already running.
 */
function Queued() {
	return (
		target: any,
		propertyKey: string,
		descriptor: PropertyDescriptor
	) => {
		const original = descriptor.value;
		const wrapperKey = Symbol(`__queue_${propertyKey}`);

		descriptor.value = function(...args: any[]) {
			// on first call, create & store the queued wrapper bound to this instance
			if (!this[wrapperKey]) {
				this[wrapperKey] = makeQueue(original.bind(this));
			}
			// delegate to the queued wrapper
			return this[wrapperKey](...args);
		};
	};
}
