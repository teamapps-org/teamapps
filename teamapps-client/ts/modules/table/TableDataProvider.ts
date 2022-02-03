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
import {UiTableClientRecordConfig} from "../../generated/UiTableClientRecordConfig";
import {UiFieldMessageConfig} from "../../generated/UiFieldMessageConfig";
import {UiIdentifiableClientRecordConfig} from "../../generated/UiIdentifiableClientRecordConfig";
import DataProvider = Slick.DataProvider;

export class TableDataProvider implements DataProvider<UiTableClientRecordConfig> {
	public onDataLoading = new Slick.Event();

	private dataStartIndex: number;
	private data: UiTableClientRecordConfig[] = [];
	private recordById: Map<number, UiTableClientRecordConfig> = new Map();
	private totalNumberOfRecords: number;

	public getLength(): number {
		return this.totalNumberOfRecords;
	}

	public getItem(index: number): UiTableClientRecordConfig {
		return this.data[index - this.dataStartIndex];  // yep, that's ok!
	}

	public getItemMetadata(index: number): Slick.RowMetadata<any> {
		let record = this.data[index - this.dataStartIndex];
		if (record == null) {
			return null;
		} else {
			return {
				cssClasses: record.bold ? "text-bold" : null
			};
		}
	}

	updateData(startIndex: number, recordIds: number[], newRecords: UiIdentifiableClientRecordConfig[], totalNumberOfRecords: number): number[] | true {
		const changedRowNumbers = this.calculateChangingRowNumbers(startIndex, recordIds);

		this.dataStartIndex = startIndex;
		newRecords.forEach(r => this.recordById.set(r.id, r));
		this.data = recordIds.map(recordId => this.recordById.get(recordId));

		// cleanup
		if (this.recordById.size > recordIds.length * 2) {
			let recordIdsAsSet = new Set(recordIds);
			this.recordById.forEach((value, key) => {
				if (!recordIdsAsSet.has(key)) {
					this.recordById.delete(key);
				}
			})
		}

		this.totalNumberOfRecords = totalNumberOfRecords;

		return changedRowNumbers;
	}

	private calculateChangingRowNumbers(startIndex: number, recordIds: number[]) {
		const everythingChanged = startIndex > (this.dataStartIndex + this.data.length) || (startIndex + recordIds.length) < this.dataStartIndex;
		let changedRowNumbers: number[] = [];
		if (!everythingChanged) {
			for (let i = Math.min(startIndex, this.dataStartIndex); i < Math.max(startIndex + recordIds.length, this.dataStartIndex + this.data.length); i++) {
				const oldId = this.data[i - this.dataStartIndex]?.id;
				const newId = recordIds[i - startIndex];
				if (oldId !== newId) {
					changedRowNumbers.push(i);
				}
			}
		}
		return everythingChanged || changedRowNumbers;
	}

	public clear(): void {
		this.data = [];
	}

	getRecordById(recordId: number) {
		return this.recordById.get(recordId);
	}

	getRowIndexByRecordId(recordId: number) {
		return this.dataStartIndex + this.data.findIndex(r => r.id == recordId);
	}

	setCellMessages(recordId: number, columnName: string, messages: UiFieldMessageConfig[]) {
		let record = this.recordById.get(recordId);
		if (record == null) {
			return;
		}
		if (record.messages == null) {
			record.messages = {};
		}
		record.messages[columnName] = messages;
	}

	clearAllCellMessages() {
		this.data.forEach(r => r.messages = null)
	}


	setCellMarked(recordId: any, columnName: string, marked: boolean) {
		let record = this.recordById.get(recordId);
		if (record == null) {
			return;
		}
		if (record.markings == null) {
			record.markings = [];
		}
		record.markings = record.markings.filter(existing => existing !== columnName);
		if (marked) {
			record.markings.push(columnName);
		}
	}

	clearAllCellMarkings() {
		this.data.forEach(r => r.markings = null);
	}

	getSelectedRowsIndexes() {
		const selectedRowIndexes = [];
		for (let i = 0; i < this.data.length; i++) {
			if (this.data[i].selected) {
				selectedRowIndexes.push(i + this.dataStartIndex);
			}
		}
		return selectedRowIndexes;
	}

	setSelectedRows(rows: number[]) {
		this.data.forEach(d => d.selected = false)
		rows.forEach(r => {
			let record = this.data[r - this.dataStartIndex];
			if (record != null) {
				record.selected = true;
			}
		})
	}

	/**
	 *  Selection management might seem a bit tricky, but with the following explanation, things might clear up:
	 *  - When the user selects a row, it gets selected on the client side (dataprovider) and server side.
	 *  - Even after getting thrown out of the data provider, the server still knows about the selection and will set the selected flag
	 *    when re-sending a selected record to the client
	 *  - When a record is selected via server-side API, it also gets selected here (if present).
	 *  TODO multi selection handling does currently not work with rows outside the buffer!
	 */
	agreesWithSelectedRows(rowIndexes: number[]) {
		for (let i = 0; i < rowIndexes.length; i++) {
			const item = this.getItem(rowIndexes[i]);
			if (item != null && !item.selected) {
				return false; // record selected in given rowIndexes but not selected here!
			}
		}
		for (let i = 0; i < this.data.length; i++) {
			const record = this.data[i];
			const rowIndex = i + this.dataStartIndex;
			if (record.selected && rowIndexes.indexOf(rowIndex) < 0) {
				return false; // record not selected in given rowIndexes but here!
			}
		}
		return true;
	}

}
