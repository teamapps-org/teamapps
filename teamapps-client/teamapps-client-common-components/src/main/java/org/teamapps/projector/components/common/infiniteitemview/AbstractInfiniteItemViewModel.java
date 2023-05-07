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
package org.teamapps.projector.components.common.infiniteitemview;

import org.teamapps.event.ProjectorEvent;

public abstract class AbstractInfiniteItemViewModel<RECORD> implements InfiniteItemViewModel<RECORD> {

	public final ProjectorEvent<Void> onAllDataChanged = new ProjectorEvent<>();
	public final ProjectorEvent<RecordsAddedEvent<RECORD>> onRecordsAdded = new ProjectorEvent<>();
	public final ProjectorEvent<RecordsChangedEvent<RECORD>> onRecordsChanged = new ProjectorEvent<>();
	public final ProjectorEvent<RecordsRemovedEvent<RECORD>> onRecordsDeleted = new ProjectorEvent<>();

	@Override
	public ProjectorEvent<Void> onAllDataChanged() {
		return onAllDataChanged;
	}

	@Override
	public ProjectorEvent<RecordsAddedEvent<RECORD>> onRecordsAdded() {
		return onRecordsAdded;
	}

	@Override
	public ProjectorEvent<RecordsChangedEvent<RECORD>> onRecordsChanged() {
		return onRecordsChanged;
	}

	@Override
	public ProjectorEvent<RecordsRemovedEvent<RECORD>> onRecordsRemoved() {
		return onRecordsDeleted;
	}
}
