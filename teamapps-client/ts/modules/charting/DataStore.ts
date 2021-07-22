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
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {DataPoint} from "./Charting";
import {UiLineGraphDataPointConfig} from "../../generated/UiLineGraphDataPointConfig";
import {UiLineGraphDataConfig} from "../../generated/UiLineGraphDataConfig";
import {UiGraphDataConfig} from "../../generated/UiGraphDataConfig";
import {UiIncidentGraphDataConfig} from "../../generated/UiIncidentGraphDataConfig";
import {UiIncidentGraphDataPointConfig} from "../../generated/UiIncidentGraphDataPointConfig";


abstract class AbstractDataStore<D extends UiGraphDataConfig> {

	private dataObsolete = false;

	public reset(): void {
		this.dataObsolete = true; // do not reset store's data directly but wait until the new data has been set!
	}

	public addData(zoomLevel: number, intervalX: [number, number], data: D) {
		if (this.dataObsolete) {
			this.doResetData();
			this.dataObsolete = false;
		}
		this.doAddData(zoomLevel, intervalX, data);
	}

	protected abstract doResetData(): void;

	protected abstract doAddData(zoomLevel: number, intervalX: [number, number], data: D): void;

	public abstract getData(zoomLevelIndex: number, intervalX: [number, number]): D;
}

export class LineGraphDataStore extends AbstractDataStore<UiLineGraphDataConfig> {

	private zoomLevelData: UiLineGraphDataPointConfig[][] = [];

	protected doResetData() {
		this.zoomLevelData = [];
	}

	protected doAddData(zoomLevel: number, intervalX: [number, number], data: UiLineGraphDataConfig) {
		this.assureZoomLevelArrayExists(zoomLevel);

		let zoomLevelData = this.zoomLevelData[zoomLevel];
		let minOverlappingIndex: number = null;
		let maxOverlappingIndex: number = null;
		for (let i = 0; i < zoomLevelData.length; i++) {
			if (zoomLevelData[i].x >= intervalX[0] && zoomLevelData[i].x <= intervalX[1]) {
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

	public getData(zoomLevelIndex: number, intervalX: [number, number]): UiLineGraphDataConfig {
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

	protected doAddData(zoomLevel: number, intervalX: [number, number], data: UiIncidentGraphDataConfig) {
		this.assureZoomLevelArrayExists(zoomLevel);
		console.log(this.zoomLevelData[zoomLevel].length)
		this.zoomLevelData[zoomLevel] = this.zoomLevelData[zoomLevel].filter(dp => {
			console.log(intervalX, dp, (dp.x2 >= intervalX[0] && dp.x1 < intervalX[1]))
			return !(dp.x2 >= intervalX[0] && dp.x1 < intervalX[1])
		});
		this.zoomLevelData[zoomLevel].push(...data.dataPoints);
		console.log("-->" +this.zoomLevelData[zoomLevel].length)
	}

	public getData(zoomLevelIndex: number, intervalX: [number, number]): UiIncidentGraphDataConfig {
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
