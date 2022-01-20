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
package org.teamapps.ux.component.infiniteitemview;

import org.teamapps.ux.cache.record.ItemRange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListInfiniteItemViewModel<RECORD> extends AbstractInfiniteItemViewModel<RECORD> {
	private List<RECORD> records;

	public ListInfiniteItemViewModel() {
		this.records = new ArrayList<>();
	}

	public ListInfiniteItemViewModel(List<RECORD> records) {
		this.records = records;
	}

	public void addRecord(RECORD record) {
		addRecord(records.size(), record);
	}

	public void addRecord(int index, RECORD record) {
		this.records.add(index, record);
		onRecordsAdded.fire(new RecordsAddedEvent<>(index, Collections.singletonList(record)));
	}

	public void addRecords(List<RECORD> records) {
		addRecords(records.size(), records);
	}

	public void addRecords(int index, List<RECORD> records) {
		this.records.addAll(index, records);
		onRecordsAdded.fire(new RecordsAddedEvent<>(index, records));
	}

	public void removeRecord(RECORD record) {
		removeRecord(records.indexOf(record));
	}

	public void removeRecord(int index) {
		onRecordsDeleted.fire(new RecordsRemovedEvent<>(ItemRange.startLength(index, 1)));
	}

	public void removeRecord(int startIndex, int length) {
		List<RECORD> subList = records.subList(startIndex, startIndex + length);
		subList.clear();
		onRecordsDeleted.fire(new RecordsRemovedEvent<>(ItemRange.startLength(startIndex, length)));
	}

	public void updateRecord(int index) {
		onRecordsChanged.fire(new RecordsChangedEvent<>(index, List.of(records.get(index))));
	}

	public void updateRecords(int startIndex, int length) {
		onRecordsChanged.fire(new RecordsChangedEvent<>(ItemRange.startLength(startIndex, length)));
	}

	public void replaceRecord(int index, RECORD record) {
		records.set(index, record);
		onRecordsChanged.fire(new RecordsChangedEvent<>(index, Collections.singletonList(record)));
	}

	public void replaceRecords(int startIndex, List<RECORD> records) {
		for (int i = 0; i < records.size(); i++) {
			records.set(startIndex + i, records.get(i));
		}
		onRecordsChanged.fire(new RecordsChangedEvent<>(ItemRange.startLength(startIndex, records.size())));
	}

	public void setRecords(List<RECORD> records) {
		this.records = records;
		onAllDataChanged.fire(null);
	}

	@Override
	public int getCount() {
		return records.size();
	}

	@Override
	public List<RECORD> getRecords(int startIndex, int length) {
		return records.stream().skip(startIndex).limit(length).collect(Collectors.toList());
	}
}
