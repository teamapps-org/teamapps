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
export class SimpleObjectProxy<T extends object> {

	constructor(target: T, handler: {
		get(target: T, p: PropertyKey, receiver: any): any;
		set(target: T, p: PropertyKey, value: any, receiver: any): boolean;
	}) {

		// set the prototype of the proxy, so instanceof returns the same result as for the original object!
		if (Object.setPrototypeOf) {
			Object.setPrototypeOf(this, Object.getPrototypeOf(target));
		} else if ((this as any).__proto__) {
			(this as any).__proto__ = (target as any).__proto__;
		} else {
			throw "Prototype could not be set!";
		}

		for (let key in target) {
			// yes, not only own properties!
			const desc = {
				get: () => handler.get(target, key, this),
				set: (value: any) => handler.set(target, key, value, this)
			};
			Object.defineProperty(this, key, desc);
		}
	};

}
