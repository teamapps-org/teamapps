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

export type Interval = [number, number]; 

export function intervalToString(interval: Interval) {
	return "Interval[" + new Date(interval[0]) + " - " + new Date(interval[1]) + "]";
}

export function intervalsOverlap(existingInterval: Interval, interval: Interval) {
	return existingInterval[0] <= interval[1] && existingInterval[1] >= interval[0];
}

export function subtractIntervals(minuend: Interval, subtrahends: Interval[]): Interval[] {
	let currentPosition = minuend[0];
	const difference: Interval[] = [];
	for (let i = 0; i < subtrahends.length; i++) {
		const subtrahend = subtrahends[i];
		if (currentPosition < subtrahend[0]) {
			difference.push([currentPosition, subtrahend[0]]);
		}
		currentPosition = subtrahend[1];
	}
	if (currentPosition < minuend[1]) {
		difference.push([currentPosition, minuend[1]]);
	}
	return difference;
}