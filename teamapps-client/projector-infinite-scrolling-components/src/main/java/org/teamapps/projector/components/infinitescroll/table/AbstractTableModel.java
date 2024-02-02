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
package org.teamapps.projector.components.infinitescroll.table;

import org.teamapps.event.ProjectorEvent;
import org.teamapps.projector.components.infinitescroll.infiniteitemview.RecordsAddedEvent;
import org.teamapps.projector.components.infinitescroll.infiniteitemview.RecordsChangedEvent;
import org.teamapps.projector.components.infinitescroll.infiniteitemview.RecordsRemovedEvent;

public abstract class AbstractTableModel<RECORD> implements TableModel<RECORD> {

	public final ProjectorEvent<Void> onAllDataChanged = new ProjectorEvent<>();
	public final ProjectorEvent<RecordsAddedEvent<RECORD>> onRecordAdded = new ProjectorEvent<>();
	public final ProjectorEvent<RecordsChangedEvent<RECORD>> onRecordUpdated = new ProjectorEvent<>();
	public final ProjectorEvent<RecordsRemovedEvent<RECORD>> onRecordDeleted = new ProjectorEvent<>();

	protected Sorting sorting;

	@Override
	public ProjectorEvent<Void> onAllDataChanged() {
		return onAllDataChanged;
	}

	@Override
	public ProjectorEvent<RecordsAddedEvent<RECORD>> onRecordsAdded() {
		return onRecordAdded;
	}

	@Override
	public ProjectorEvent<RecordsChangedEvent<RECORD>> onRecordsChanged() {
		return onRecordUpdated;
	}

	@Override
	public ProjectorEvent<RecordsRemovedEvent<RECORD>> onRecordsRemoved() {
		return onRecordDeleted;
	}

	@Override
	public void setSorting(Sorting sorting) {
		this.sorting = sorting;
		onAllDataChanged.fire();
	}
}
