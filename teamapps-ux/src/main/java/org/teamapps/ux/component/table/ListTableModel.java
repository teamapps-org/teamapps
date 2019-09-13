/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
package org.teamapps.ux.component.table;

import org.teamapps.data.value.Sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListTableModel<RECORD> extends AbstractTableModel<RECORD> {

	private List<RECORD> list = new ArrayList<>();
	private Predicate<RECORD> filter = record -> true;

	public ListTableModel() {
	}

	public ListTableModel(List<RECORD> list) {
		this.list.addAll(list);
	}

	public void setList(List<RECORD> list) {
		if (list == null) {
			list = Collections.emptyList();
		}
		this.list = new ArrayList<>(list);
		onAllDataChanged.fire(null);
	}

	public List<RECORD> getList() {
		return Collections.unmodifiableList(list);
	}

	public void addRecord(RECORD record) {
		list.add(record);
		onRecordAdded.fire(record);
	}

	public void addRecords(List<RECORD> records) {
		list.addAll(records);
		onAllDataChanged.fire(null);
	}

	@Override
	public int getCount() {
		if (filter == null) {
			return list.size();
		} else {
			return (int) list.stream()
					.filter(filter)
					.count();
		}
	}

	@Override
	public List<RECORD> getRecords(int startIndex, int length, Sorting sorting) {
		if (filter == null) {
			return list.subList(startIndex, Math.min(list.size(), startIndex + length));
		} else {
			return list.stream()
					.filter(filter)
					.skip(startIndex)
					.limit(length)
					.collect(Collectors.toList());
		}
	}

	public List<RECORD> getAllRecords() {
		return new ArrayList<>(list);
	}

	@Override
	public List<RECORD> getChildRecords(RECORD parentRecord, Sorting sorting) {
		return null;
	}

	public Predicate<RECORD> getFilter() {
		return filter;
	}

	public void setFilter(Predicate<RECORD> filter) {
		this.filter = filter;
		onAllDataChanged.fire(null);
	}
}
