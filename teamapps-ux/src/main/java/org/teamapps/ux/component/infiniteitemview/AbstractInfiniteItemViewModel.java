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
package org.teamapps.ux.component.infiniteitemview;

import org.teamapps.event.Event;

public abstract class AbstractInfiniteItemViewModel<RECORD> implements InfiniteItemViewModel<RECORD> {

	public final Event<Void> onAllDataChanged = new Event<>();
	public final Event<RecordsAddedEvent<RECORD>> onRecordsAdded = new Event<>();
	public final Event<RecordsChangedEvent<RECORD>> onRecordsChanged = new Event<>();
	public final Event<RecordsRemovedEvent<RECORD>> onRecordsDeleted = new Event<>();

	@Override
	public Event<Void> onAllDataChanged() {
		return onAllDataChanged;
	}

	@Override
	public Event<RecordsAddedEvent<RECORD>> onRecordsAdded() {
		return onRecordsAdded;
	}

	@Override
	public Event<RecordsChangedEvent<RECORD>> onRecordsChanged() {
		return onRecordsChanged;
	}

	@Override
	public Event<RecordsRemovedEvent<RECORD>> onRecordsRemoved() {
		return onRecordsDeleted;
	}
}
