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
package org.teamapps.ux.component.table;

import java.util.Collections;
import java.util.List;

public class RecordAddedEventData<RECORD> {

	private final List<RECORD> records;

	/**
	 * <= 0: insert at top
	 * existing index: insert at index
	 * >= totalRowCount: insert at bottom
	 */
	private final int index;

	public RecordAddedEventData(RECORD record, int index) {
		this.records = Collections.singletonList(record);
		this.index = index;
	}

	public RecordAddedEventData(List<RECORD> records, int index) {
		this.records = records;
		this.index = index;
	}

	public List<RECORD> getRecords() {
		return records;
	}

	public int getIndex() {
		return index;
	}

}
