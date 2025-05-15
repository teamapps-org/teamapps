/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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


export function postponeUntil(postponed: Function, predicate: () => boolean, delayInterval = 500, maxDelay = 10000) {
	let startTime = +new Date();
	return new Promise((resolve, reject) => {
		let timeoutId: number;

		function executeOrWait() {
			if (!predicate()) {
				if (+new Date() - startTime > maxDelay) {
					reject("Postponing canceled due to timeout!");
				} else {
					timeoutId = window.setTimeout(executeOrWait, delayInterval);
				}
				return;
			} else {
				resolve(postponed());
			}
		}

		executeOrWait();
	});
}

export class Postponer {
	private timeoutId: number;

	postponeUntil(postponed: Function, predicate: () => boolean, delayInterval = 500, maxDelay = 10000) {
		window.clearTimeout(this.timeoutId);

		let startTime = +new Date();
		return new Promise((resolve, reject) => {
			let executeOrWait = () => {
				if (!predicate()) {
					if (+new Date() - startTime > maxDelay) {
						reject("Postponing canceled due to timeout!");
					} else {
						this.timeoutId = window.setTimeout(executeOrWait, delayInterval);
					}
					return;
				} else {
					resolve(postponed());
				}
			}
			executeOrWait();
		});
	}
}

