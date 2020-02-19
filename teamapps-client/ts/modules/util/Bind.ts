/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
export function bind(target: any, property: string, descriptor: PropertyDescriptor) {
	if (!descriptor || (typeof descriptor.value !== 'function')) {
		throw new TypeError("Cannot bind " + property + " since it is not a method!");
	}
	return {
		configurable: true,
		get: function () {
			const bound = descriptor.value.bind(this);
			Object.defineProperty(this, property, {
				value: bound,
				configurable: true,
				writable: true
			});
			return bound;
		}
	};
}
