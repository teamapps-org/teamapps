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
import {Logger} from "loglevel";
import * as log from "loglevel";

export class Interval {
	constructor(public start: number, public end: number) {
	}

	public toString() {
		return "Interval[" + new Date(this.start) + " - " + new Date(this.end) + "]";
	}
}

export class IntervalManager {
	private log: Logger = log.getLogger((<any>IntervalManager.prototype).name || this.constructor.toString().match(/\w+/g)[1]);
	private _intervals: Interval[] = []; // sorted!

	public addInterval(newInterval: Interval): Interval[] {
		this.log.info("Adding interval: " + newInterval);
		var {firstOverlappingExistingIntervalIndex, lastOverlappingExistingIntervalIndex, firstLaterIntervalIndex} = this.getFirstAndLastOverlappingExistingIntervalIndex(newInterval);

		if (firstOverlappingExistingIntervalIndex === null && lastOverlappingExistingIntervalIndex == null) {
			this._intervals.splice(firstLaterIntervalIndex, 0, newInterval);
			this.log.info("NO OVERLAP. New intervals: " + this._intervals.map(interval => interval.toString()).join("; "));
			return [newInterval];
		} else {
			var firstOverlappingInterval = this._intervals[firstOverlappingExistingIntervalIndex];
			var lastOverlappingInterval = this._intervals[lastOverlappingExistingIntervalIndex];
			var resultingInterval = new Interval(
				Math.min(firstOverlappingInterval.start, newInterval.start),
				Math.max(lastOverlappingInterval.end, newInterval.end)
			);
			var result = IntervalManager.subtractIntervals(newInterval, this._intervals.slice(firstOverlappingExistingIntervalIndex, lastOverlappingExistingIntervalIndex + 1));
			this._intervals.splice(firstOverlappingExistingIntervalIndex, lastOverlappingExistingIntervalIndex - firstOverlappingExistingIntervalIndex + 1, resultingInterval);
			this.log.info("Overlap detected, first: " + firstOverlappingInterval + ", last: " + lastOverlappingInterval + ". New intervals: " + this._intervals.map(interval => interval.toString()).join("; "));
			return result;
		}
	}

	public getUncoveredIntervals(interval: Interval): Interval[] {
		var {firstOverlappingExistingIntervalIndex, lastOverlappingExistingIntervalIndex} = this.getFirstAndLastOverlappingExistingIntervalIndex(interval);
		if (firstOverlappingExistingIntervalIndex === null && lastOverlappingExistingIntervalIndex == null) {
			return [interval];
		} else {
			return IntervalManager.subtractIntervals(interval, this._intervals.slice(firstOverlappingExistingIntervalIndex, lastOverlappingExistingIntervalIndex + 1));
		}
	}

	private getFirstAndLastOverlappingExistingIntervalIndex(interval: Interval) {
		let firstOverlappingExistingIntervalIndex = null;
		let lastOverlappingExistingIntervalIndex = null;
		let index = 0;
		for (; index < this._intervals.length; index++) {
			var existingInterval = this._intervals[index];
			if (IntervalManager.intervalsOverlap(existingInterval, interval)) {
				if (firstOverlappingExistingIntervalIndex === null) {
					firstOverlappingExistingIntervalIndex = index;
				}
				lastOverlappingExistingIntervalIndex = index;
			}
			if (existingInterval.end > interval.end) {
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
		return existingInterval.start <= interval.end && existingInterval.end >= interval.start;
	}

	private static subtractIntervals(minuend: Interval, subtrahends: Interval[]): Interval[] {
		var currentPosition = minuend.start;
		var difference: Interval[] = [];
		for (var i = 0; i < subtrahends.length; i++) {
			var subtrahend = subtrahends[i];
			if (currentPosition < subtrahend.start) {
				difference.push(new Interval(currentPosition, subtrahend.start));
			}
			currentPosition = subtrahend.end;
		}
		if (currentPosition < minuend.end) {
			difference.push(new Interval(currentPosition, minuend.end));
		}
		return difference;
	}

	get intervals(): Interval[] {
		return this._intervals;
	}
}
