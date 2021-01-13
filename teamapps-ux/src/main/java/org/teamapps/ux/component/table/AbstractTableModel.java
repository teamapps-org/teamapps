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

import org.teamapps.event.Event;

public abstract class AbstractTableModel<RECORD> implements TableModel<RECORD> {

	public final Event<Void> onAllDataChanged = new Event<>();
	public final Event<RECORD> onRecordAdded = new Event<>();
	public final Event<RECORD> onRecordUpdated = new Event<>();
	public final Event<RECORD> onRecordDeleted = new Event<>();

	@Override
	public Event<Void> onAllDataChanged() {
		return onAllDataChanged;
	}

	@Override
	public Event<RECORD> onRecordAdded() {
		return onRecordAdded;
	}

	@Override
	public Event<RECORD> onRecordUpdated() {
		return onRecordUpdated;
	}

	@Override
	public Event<RECORD> onRecordDeleted() {
		return onRecordDeleted;
	}
}
