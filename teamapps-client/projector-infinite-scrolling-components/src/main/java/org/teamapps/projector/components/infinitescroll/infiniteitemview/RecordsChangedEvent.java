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
package org.teamapps.projector.components.infinitescroll.infiniteitemview;

import org.teamapps.projector.components.infinitescroll.recordcache.ItemRange;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RecordsChangedEvent<RECORD> {

	private final ItemRange itemRange;
	private final List<RECORD> records;

	/**
	 * Use this constructor if the model does not have the changed items at hand.
	 * This might happen when the model was not in charge of the actual change but got notified of it.
	 * Views might not actually need to react on the change, anyway.
	 * So forcing the model to retrieve the changed records does not make sense.
	 */
	public RecordsChangedEvent(ItemRange itemRange) {
		this.itemRange = itemRange;
		this.records = null;
	}

	/**
	 * Use this constructor if the model has the changed items at hand.
	 */
	public RecordsChangedEvent(int startIndex, List<RECORD> records) {
		Objects.requireNonNull(records);
		this.itemRange = ItemRange.startLength(startIndex, records.size());
		this.records = records;
	}

	public ItemRange getItemRange() {
		return itemRange;
	}

	public Optional<List<RECORD>> getRecords() {
		return Optional.ofNullable(records);
	}

	public int getStart() {
		return getItemRange().getStart();
	}

	public int getLength() {
		return getItemRange().getLength();
	}

	public int getEnd() {
		return getItemRange().getEnd();
	}
}
