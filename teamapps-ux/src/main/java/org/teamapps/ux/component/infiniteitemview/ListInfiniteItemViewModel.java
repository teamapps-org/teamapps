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
package org.teamapps.ux.component.infiniteitemview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListInfiniteItemViewModel<RECORD> extends AbstractInfiniteItemViewModel<RECORD> {
	private List<RECORD> records;

	public ListInfiniteItemViewModel() {
		this.records = new ArrayList<>();
	}

	public ListInfiniteItemViewModel(List<RECORD> records) {
		this.records = records;
	}

	public void addRecord(RECORD record) {
		this.records.add(record);
		onRecordAdded.fire(record);
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
		if (startIndex >= records.size()) {
			return Collections.emptyList();
		}
		if (startIndex + length > records.size()) {
			length = records.size() - startIndex;
		}
		return records.subList(startIndex, startIndex + length);
	}
}
