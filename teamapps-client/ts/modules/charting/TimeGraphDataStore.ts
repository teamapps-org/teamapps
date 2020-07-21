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
import {UiLongIntervalConfig} from "../../generated/UiLongIntervalConfig";
import {UiTimeGraphDataPointConfig} from "../../generated/UiTimeGraphDataPointConfig";
import {DataPoint} from "./Charting";

export class TimeGraphDataStore {

	private stores: { [dataSeriesId: string]: DataSeriesStore } = {};
	private dataObsolete = false;

	public reset(): void {
		this.dataObsolete = true; // do not reset store's data directly but wait until the new data has been set!
	}

	public addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: { [dataSeriesId: string]: UiTimeGraphDataPointConfig[] }) {
		if (this.dataObsolete) {
			this.stores = {};
			this.dataObsolete = false;
		}
		Object.keys(data).forEach(dataSeriesId => {
			let store = this.getOrCreateStore(dataSeriesId);
			store.addData(zoomLevel, intervalX, data[dataSeriesId]);
		})
	}

	private getOrCreateStore(dataSeriesId: string) {
		let store = this.stores[dataSeriesId];
		if (store == null) {
			store = this.stores[dataSeriesId] = new DataSeriesStore();
		}
		return store;
	}

	public getData(dataSeriesIds: string[], zoomLevelIndex: number, xStart: number, xEnd: number): { [dataSeriesId: string]: DataPoint[] } {
		return dataSeriesIds.reduce(
			(previousValue, id) => (previousValue[id] = this.getOrCreateStore(id).getData(zoomLevelIndex, xStart, xEnd)) && previousValue,
			{} as { [dataSeriesId: string]: DataPoint[] }
		);
	}
}

class DataSeriesStore {

	private zoomLevelData: UiTimeGraphDataPointConfig[][] = [];

	constructor() {
		this.zoomLevelData = [];
	}

	public addData(zoomLevel: number, intervalX: UiLongIntervalConfig, data: UiTimeGraphDataPointConfig[]) {
		this.assureZoomLevelArrayExists(zoomLevel);

		let zoomLevelData = this.zoomLevelData[zoomLevel];
		let minOverlappingIndex: number = null;
		let maxOverlappingIndex: number = null;
		for (let i = 0; i < zoomLevelData.length; i++) {
			if (zoomLevelData[i].x >= intervalX.min && zoomLevelData[i].x <= intervalX.max) {
				if (minOverlappingIndex == null) {
					minOverlappingIndex = i;
				}
				maxOverlappingIndex = i;
			}
		}
		if (minOverlappingIndex != null && maxOverlappingIndex != null) {
			zoomLevelData.splice(minOverlappingIndex, maxOverlappingIndex - minOverlappingIndex + 1, ...data);
		} else {
			this.zoomLevelData[zoomLevel] = zoomLevelData.concat(data);
		}
		this.zoomLevelData[zoomLevel].sort((a, b) => a.x - b.x);
	}

	public getData(zoomLevelIndex: number, xStart: number, xEnd: number): DataPoint[] {
		this.assureZoomLevelArrayExists(zoomLevelIndex);

		let i = 0;
		for (; i < this.zoomLevelData[zoomLevelIndex].length; i++) {
			if (this.zoomLevelData[zoomLevelIndex][i].x > xStart) {
				break;
			}
		}
		let startIndex = i === 0 ? 0 : i - 1;
		for (; i < this.zoomLevelData[zoomLevelIndex].length; i++) {
			if (this.zoomLevelData[zoomLevelIndex][i].x >= xEnd) {
				break;
			}
		}
		let endIndex = i === this.zoomLevelData[zoomLevelIndex].length ? i : i + 1;
		return this.zoomLevelData[zoomLevelIndex].slice(startIndex, endIndex);
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
