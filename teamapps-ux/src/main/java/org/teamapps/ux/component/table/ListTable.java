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
import java.util.function.Predicate;

public class ListTable<RECORD> extends Table<RECORD> {

	private ListTableModel<RECORD> model;

	public ListTable() {
		this(Collections.emptyList());
	}

	public ListTable(List<RECORD> records) {
		model = new ListTableModel<>(records);
		setModel(model);
	}

	public void setRecords(List<RECORD> records) {
		model.setList(records);
	}

	public List<RECORD> getRecords() {
		return model.getAllRecords();
	}

	public void addRecord(RECORD record) {
		model.addRecord(record);
	}

	public void addRecords(List<RECORD> records) {
		model.addRecords(records);
	}

	public void setFilter(Predicate<RECORD> filter) {
		model.setFilter(filter);
	}
}
