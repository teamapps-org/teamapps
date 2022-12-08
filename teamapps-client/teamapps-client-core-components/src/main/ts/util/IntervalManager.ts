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

export class IntervalManager {
	private _intervals: Interval[] = []; // sorted!

	public addInterval(newInterval: Interval) {
		console.info("Adding interval: " + newInterval);
		let {
			firstOverlappingExistingIntervalIndex,
			lastOverlappingExistingIntervalIndex,
			firstLaterIntervalIndex
		} = this.getFirstAndLastOverlappingExistingIntervalIndex(newInterval);

		if (firstOverlappingExistingIntervalIndex === null && lastOverlappingExistingIntervalIndex == null) {
			this._intervals.splice(firstLaterIntervalIndex, 0, newInterval);
			console.info("NO OVERLAP. New intervals: " + this._intervals.map(interval => interval.toString()).join("; "));
		} else {
			const firstOverlappingInterval = this._intervals[firstOverlappingExistingIntervalIndex];
			const lastOverlappingInterval = this._intervals[lastOverlappingExistingIntervalIndex];
			const resultingInterval: Interval = [
				Math.min(firstOverlappingInterval[0], newInterval[0]),
				Math.max(lastOverlappingInterval[1], newInterval[1])
			];
			this._intervals.splice(firstOverlappingExistingIntervalIndex, lastOverlappingExistingIntervalIndex - firstOverlappingExistingIntervalIndex + 1, resultingInterval);
			console.info("Overlap detected, first: " + firstOverlappingInterval + ", last: " + lastOverlappingInterval + ". New intervals: " + this._intervals.map(interval => interval.toString()).join("; "));
		}
	}

	public addIntervals(intervals: Interval[]) {
		intervals.forEach(interval => this.addInterval(interval))
	}

	public getUncoveredIntervals(interval: Interval): Interval[] {
		let {
			firstOverlappingExistingIntervalIndex,
			lastOverlappingExistingIntervalIndex
		} = this.getFirstAndLastOverlappingExistingIntervalIndex(interval);
		if (firstOverlappingExistingIntervalIndex === null && lastOverlappingExistingIntervalIndex == null) {
			return [interval];
		} else {
			return IntervalManager.subtractIntervals(interval, this._intervals.slice(firstOverlappingExistingIntervalIndex, lastOverlappingExistingIntervalIndex + 1));
		}
	}

	public getCoveredIntervals(): Interval[] {
		return this._intervals;
	}

	private getFirstAndLastOverlappingExistingIntervalIndex(interval: Interval) {
		let firstOverlappingExistingIntervalIndex = null;
		let lastOverlappingExistingIntervalIndex = null;
		let index = 0;
		for (; index < this._intervals.length; index++) {
			const existingInterval = this._intervals[index];
			if (IntervalManager.intervalsOverlap(existingInterval, interval)) {
				if (firstOverlappingExistingIntervalIndex === null) {
					firstOverlappingExistingIntervalIndex = index;
				}
				lastOverlappingExistingIntervalIndex = index;
			}
			if (existingInterval[1] > interval[1]) {
				break;
			}
		}
		return {
			firstOverlappingExistingIntervalIndex: firstOverlappingExistingIntervalIndex,
			lastOverlappingExistingIntervalIndex: lastOverlappingExistingIntervalIndex,
			firstLaterIntervalIndex: index
		};
	}

	public static intervalsOverlap(existingInterval: Interval, interval: Interval) {
		return existingInterval[0] <= interval[1] && existingInterval[1] >= interval[0];
	}

	private static subtractIntervals(minuend: Interval, subtrahends: Interval[]): Interval[] {
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

	get intervals(): Interval[] {
		return this._intervals;
	}
}
