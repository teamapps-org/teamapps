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
package org.teamapps.ux.cache.record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.dto.UiIdentifiableClientRecord;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RenderedRecordsCache<RECORD> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Map<RECORD, UiIdentifiableClientRecord> uiRecordsByRecord = new HashMap<>();
	private final List<RecordAndClientRecord<RECORD>> recordPairs = new ArrayList<>();
	private int startIndex = 0;

	public RenderedRecordsCache() {
	}

	public UiIdentifiableClientRecord getUiRecord(RECORD record) {
		return uiRecordsByRecord.get(record);
	}

	public int getIndex(RECORD record) {
		int index = IntStream.range(0, recordPairs.size())
				.filter(i -> record.equals(recordPairs.get(i).getRecord()))
				.findFirst().orElse(-1);
		if (index >= 0) {
			return startIndex + index;
		} else {
			return -1;
		}
	}

	public List<RECORD> getRecords() {
		return recordPairs.stream()
				.map(RecordAndClientRecord::getRecord)
				.collect(Collectors.toList());
	}

	public RECORD getRecord(int uiRecordId) {
		// no fast implementation needed! only called on user click
		return recordPairs.stream()
				.filter(rr -> rr.getUiRecord().getId() == uiRecordId)
				.map(RecordAndClientRecord::getRecord)
				.findFirst().orElse(null);
	}

	public RECORD getRecordByIndex(int index) {
		return recordPairs.get(index - startIndex).getRecord();
	}

	public List<RECORD> getRecords(List<Integer> uiRecordIds) {
		HashSet<Integer> recordIdsSet = new HashSet<>(uiRecordIds);
		return recordPairs.stream()
				.filter(rr -> recordIdsSet.contains(rr.getUiRecord().getId()))
				.map(RecordAndClientRecord::getRecord)
				.collect(Collectors.toList());
	}

	public List<Integer> getUiRecordIds() {
		return recordPairs.stream()
				.map(rr -> rr.getUiRecord().getId())
				.collect(Collectors.toList());
	}

	public List<Integer> getUiRecordIds(List<RECORD> records) {
		HashSet<RECORD> recordsAsSet = new HashSet<>(records);
		return recordPairs.stream()
				.filter(rp -> recordsAsSet.contains(rp.getRecord()))
				.map(rr -> rr.getUiRecord().getId())
				.collect(Collectors.toList());
	}

	public void addNoShift(int startIndex, List<RecordAndClientRecord<RECORD>> newClientRecordPairs) {
		LOGGER.debug("inserting at {}: {}", startIndex, newClientRecordPairs.size());
		if (newClientRecordPairs.size() == 0) {
			return; // this is important! if the startIndex is completely of, this might otherwise throw an "unattached records" exception
		}
		if (this.recordPairs.size() == 0) { // fresh record cache!
			this.startIndex = startIndex;
		}
		if (startIndex + newClientRecordPairs.size() < this.startIndex
				|| startIndex > this.startIndex + recordPairs.size()) {
			String errorMessage = String.format("Cannot addNoShift unattached records! %d, %d, %d, %d", startIndex, newClientRecordPairs.size(), this.startIndex, recordPairs.size());
			LOGGER.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		int listInsertIndex = Math.max(0, startIndex - this.startIndex);
		if (newClientRecordPairs.stream().anyMatch(rp -> uiRecordsByRecord.containsKey(rp.getRecord()))) {
			throw new DuplicateEntriesException("List components MUST NOT contains the same item several times!");
		}
		recordPairs.addAll(listInsertIndex, newClientRecordPairs);
		for (RecordAndClientRecord<RECORD> rr : newClientRecordPairs) {
			uiRecordsByRecord.put(rr.getRecord(), rr.getUiRecord());
		}
		this.startIndex = Math.min(this.startIndex, startIndex);
	}

	public void insertShifting(int startIndex, List<RecordAndClientRecord<RECORD>> newClientRecordPairs) {
		if (startIndex < this.startIndex || startIndex > this.startIndex + this.recordPairs.size()) {
			String errorMessage = String.format("Cannot insertShifting unattached records! %d, %d, %d, %d", startIndex, newClientRecordPairs.size(), this.startIndex, recordPairs.size());
			LOGGER.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		int listInsertIndex = startIndex - this.startIndex;
		if (newClientRecordPairs.stream().anyMatch(rp -> uiRecordsByRecord.containsKey(rp.getRecord()))) {
			throw new DuplicateEntriesException("List components MUST NOT contains the same item several times! ");
		}
		recordPairs.addAll(listInsertIndex, newClientRecordPairs);
		for (RecordAndClientRecord<RECORD> rr : newClientRecordPairs) {
			uiRecordsByRecord.put(rr.getRecord(), rr.getUiRecord());
		}
	}

	public void removeNoShift(int startIndex, int endIndex) {
		int length = endIndex - startIndex;
		int listStartIndex = startIndex - this.startIndex;
		int listEndIndex = listStartIndex + Math.min(length, recordPairs.size());
		removeNoShiftInternal(listStartIndex, listEndIndex);

	}

	public void removeBeforeNoShift(int index) {
		if (index > startIndex) {
			removeNoShiftInternal(0, Math.min(index - this.startIndex, recordPairs.size()));
			this.startIndex = index;
		}
	}

	public void removeAfterNoShift(int index) {
		if (index < startIndex + recordPairs.size()) {
			removeNoShiftInternal(Math.max(0, index - this.startIndex), recordPairs.size());
		}
	}

	private void removeNoShiftInternal(int startIndex, int endIndex) {
		List<RecordAndClientRecord<RECORD>> recordsToBeRemoved = recordPairs.subList(startIndex, endIndex);
		for (RecordAndClientRecord<RECORD> rr : recordsToBeRemoved) {
			uiRecordsByRecord.remove(rr.getRecord());
		}
		recordsToBeRemoved.clear();
	}

	public void clear() {
		this.startIndex = 0;
		uiRecordsByRecord.clear();
		recordPairs.clear();
	}

	public int size() {
		return recordPairs.size();
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return startIndex + recordPairs.size();
	}

	public ItemRange getRange() {
		return ItemRange.startLength(startIndex, recordPairs.size());
	}
}
