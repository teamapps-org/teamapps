/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {DataPoint} from "./Charting";
import {UiLineGraphDataPointConfig} from "../../generated/UiLineGraphDataPointConfig";
import {UiLineGraphDataConfig} from "../../generated/UiLineGraphDataConfig";
import {UiGraphDataConfig} from "../../generated/UiGraphDataConfig";
import {UiIncidentGraphDataConfig} from "../../generated/UiIncidentGraphDataConfig";
import {UiIncidentGraphDataPointConfig} from "../../generated/UiIncidentGraphDataPointConfig";
import {Interval, IntervalManager} from "../util/IntervalManager";


abstract class AbstractDataStore<D extends UiGraphDataConfig> {

	private intervalManagerByZoomLevel: Map<number, IntervalManager> = new Map();
	private dataObsolete = false;

	public reset(): void {
		this.dataObsolete = true; // do not reset store's data directly but wait until the new data has been set!
		this.intervalManagerByZoomLevel.clear();
	}

	public getUncoveredIntervals(zoomLevel: number, interval: [number, number]): [number, number][] {
		return this.getIntervalManager(zoomLevel).getUncoveredIntervals(interval);
	}

	public markIntervalAsCovered(zoomLevel: number, interval: [number, number]): void {
		this.getIntervalManager(zoomLevel).addInterval(interval);
	}

	public addData(zoomLevel: number, data: D) {
		if (this.dataObsolete) {
			this.doResetData();
			this.dataObsolete = false;
		}
		this.getIntervalManager(zoomLevel).addInterval([data.interval.min, data.interval.max]);
		this.doAddData(zoomLevel, data);
	}

	protected abstract doResetData(): void;

	protected abstract doAddData(zoomLevel: number, data: D): void;

	public abstract getData(zoomLevelIndex: number, intervalX: [number, number]): Omit<D, "interval">;

	private getIntervalManager(zoomLevel: number) {
		if (!this.intervalManagerByZoomLevel.has(zoomLevel)) {
			this.intervalManagerByZoomLevel.set(zoomLevel, new IntervalManager())
		}
		return this.intervalManagerByZoomLevel.get(zoomLevel);
	}
}

export class LineGraphDataStore extends AbstractDataStore<UiLineGraphDataConfig> {

	private zoomLevelData: UiLineGraphDataPointConfig[][] = [];

	protected doResetData() {
		this.zoomLevelData = [];
	}

	protected doAddData(zoomLevel: number, data: UiLineGraphDataConfig) {
		this.assureZoomLevelArrayExists(zoomLevel);

		let interval = [data.interval.min, data.interval.max];
		let zoomLevelData = this.zoomLevelData[zoomLevel];
		let minOverlappingIndex: number = null;
		let maxOverlappingIndex: number = null;
		for (let i = 0; i < zoomLevelData.length; i++) {
			if (zoomLevelData[i].x >= interval[0] && zoomLevelData[i].x <= interval[1]) {
				if (minOverlappingIndex == null) {
					minOverlappingIndex = i;
				}
				maxOverlappingIndex = i;
			}
		}
		if (minOverlappingIndex != null && maxOverlappingIndex != null) {
			zoomLevelData.splice(minOverlappingIndex, maxOverlappingIndex - minOverlappingIndex + 1, ...data.dataPoints);
		} else {
			this.zoomLevelData[zoomLevel] = zoomLevelData.concat(data.dataPoints);
		}
		this.zoomLevelData[zoomLevel].sort((a, b) => a.x - b.x);
	}

	public getData(zoomLevelIndex: number, intervalX: [number, number]) {
		this.assureZoomLevelArrayExists(zoomLevelIndex);

		let i = 0;
		for (; i < this.zoomLevelData[zoomLevelIndex].length; i++) {
			if (this.zoomLevelData[zoomLevelIndex][i].x >= intervalX[0]) {
				break;
			}
		}
		let startIndex = i === 0 ? 0 : i - 1;
		for (; i < this.zoomLevelData[zoomLevelIndex].length; i++) {
			if (this.zoomLevelData[zoomLevelIndex][i].x >= intervalX[1]) {
				break;
			}
		}
		let endIndex = i === this.zoomLevelData[zoomLevelIndex].length ? i : i + 1;
		return {dataPoints: this.zoomLevelData[zoomLevelIndex].slice(startIndex, endIndex)};
	}

	private assureZoomLevelArrayExists(zoomLevel: number) {
		if (this.zoomLevelData.length <= zoomLevel) {
			let numberOfZoomLevelsToAdd = zoomLevel - this.zoomLevelData.length + 1;
			for (let i = 0; i < numberOfZoomLevelsToAdd; i++) {
				this.zoomLevelData.push([])
			}
		}
	}
}

export class IncidentGraphDataStore extends AbstractDataStore<UiIncidentGraphDataConfig> {

	private zoomLevelData: UiIncidentGraphDataPointConfig[][] = [];

	protected doResetData() {
		this.zoomLevelData = [];
	}

	protected doAddData(zoomLevel: number, data: UiIncidentGraphDataConfig) {
		let interval = [data.interval.min, data.interval.max];
		this.assureZoomLevelArrayExists(zoomLevel);
		this.zoomLevelData[zoomLevel] = this.zoomLevelData[zoomLevel].filter(dp => {
			return !(dp.x2 >= interval[0] && dp.x1 < interval[1])
		});
		this.zoomLevelData[zoomLevel].push(...data.dataPoints);
	}

	public getData(zoomLevelIndex: number, intervalX: [number, number]) {
		this.assureZoomLevelArrayExists(zoomLevelIndex);
		return {dataPoints: this.zoomLevelData[zoomLevelIndex].filter(dp => dp.x2 >= intervalX[0] && dp.x1 < intervalX[1])};
	}

	private assureZoomLevelArrayExists(zoomLevel: number) {
		if (this.zoomLevelData.length <= zoomLevel) {
			let numberOfZoomLevelsToAdd = zoomLevel - this.zoomLevelData.length + 1;
			for (let i = 0; i < numberOfZoomLevelsToAdd; i++) {
				this.zoomLevelData.push([])
			}
		}
	}
}
