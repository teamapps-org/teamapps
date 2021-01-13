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
export class OrderedDictionary<V> {
	private _values: V[] = [];
	private valuesByKey: { [key: string]: V } = {};

	constructor() {
	}

	public push(key: string, value: V) {
		if (this.valuesByKey[key]) {
			this.removeValue(this.valuesByKey[key]);
		}

		this._values.push(value);
		this.valuesByKey[key] = value;
	}

	public insertAfterValue(key: string, value: V, otherValue: V) {
		if (this.valuesByKey[key]) {
			this.removeValue(this.valuesByKey[key]);
		}

		this._values.splice(this._values.indexOf(otherValue) + 1, 0, value);
		this.valuesByKey[key] = value;
	}

	public insertBeforeValue(key: string, value: V, otherValue: V) {
		if (this.valuesByKey[key]) {
			this.removeValue(this.valuesByKey[key]);
		}

		this._values.splice(this._values.indexOf(otherValue), 0, value);
		this.valuesByKey[key] = value;
	}

	public remove(key: string) {
		let value = this.valuesByKey[key];
		if (value) {
			delete this.valuesByKey[key];
			this._values = this._values.filter((e) => e !== value);
		}
	}

	public removeValue(value: V) {
		this._values = this._values.filter((e) => e !== value);
		Object.keys(this.valuesByKey).forEach((key) => {
			if (this.valuesByKey[key] === value) {
				delete this.valuesByKey[key];
			}
		});
	}

	public getValue(key: string) {
		return this.valuesByKey[key];
	}

	get values(): V[] {
		return this._values.slice(); // return a copy of the array!
	}

	get length(): number {
		return this._values.length;
	}
}
