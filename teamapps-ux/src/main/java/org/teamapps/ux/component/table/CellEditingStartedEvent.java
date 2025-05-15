/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2025 TeamApps.org
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

import org.teamapps.ux.component.field.AbstractField;

public class CellEditingStartedEvent<RECORD, VALUE> {

	private final RECORD recordId;
	private final TableColumn<RECORD, VALUE> column;
	private final VALUE currentValue;

	public CellEditingStartedEvent(RECORD record, TableColumn<RECORD, VALUE> column, VALUE currentValue) {
		this.recordId = record;
		this.column = column;
		this.currentValue = currentValue;
	}

	public RECORD getRecord() {
		return recordId;
	}

	public TableColumn<RECORD, VALUE> getColumn() {
		return column;
	}

	public String getPropertyName() {
		return column.getPropertyName();
	}

	public AbstractField getField() {
		return column.getField();
	}

	public VALUE getCurrentValue() {
		return currentValue;
	}
}
