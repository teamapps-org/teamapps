/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.databinding.ObservableValue;
import org.teamapps.databinding.TwoWayBindableValueImpl;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.dto.UiTableClientRecord;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.record.EqualsAndHashCode;
import org.teamapps.ux.cache.record.ItemRange;
import org.teamapps.ux.cache.record.RecordAndClientRecord;
import org.teamapps.ux.cache.record.RenderedRecordsCache;
import org.teamapps.ux.component.AbstractComponent;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractInfiniteListComponent<RECORD, MODEL extends InfiniteListModel<RECORD>> extends AbstractComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final Event<ItemRange> onDisplayedRangeChanged = new Event<>();

	private final Consumer<Void> modelOnAllDataChangedListener = aVoid -> this.refresh();
	private final Consumer<RecordsAddedEvent<RECORD>> modelOnRecordsAddedListener = this::handleModelRecordsAdded;
	private final Consumer<RecordsChangedEvent<RECORD>> modelOnRecordsChangedListener = this::handleModelRecordsChanged;
	private final Consumer<RecordsRemovedEvent<RECORD>> modelOnRecordsDeletedListener = this::handleModelRecordsRemoved;

	private final TwoWayBindableValueImpl<Integer> count = new TwoWayBindableValueImpl<>(0);

	private MODEL model;
	protected EqualsAndHashCode<RECORD> customEqualsAndHashCode = EqualsAndHashCode.bypass();
	protected RenderedRecordsCache<RECORD> renderedRecords = new RenderedRecordsCache<>();
	private ItemRange displayedRange = ItemRange.startEnd(0, 0);

	public AbstractInfiniteListComponent(MODEL model) {
		this.model = model;
	}

	public MODEL getModel() {
		return model;
	}

	public void setModel(MODEL model) {
		unregisterModelListeners();
		this.model = model;
		if (model != null) {
			preRegisteringModel(model);
			model.onAllDataChanged().addListener(this.modelOnAllDataChangedListener);
			model.onRecordsAdded().addListener(this.modelOnRecordsAddedListener);
			model.onRecordsChanged().addListener(this.modelOnRecordsChangedListener);
			model.onRecordsRemoved().addListener(this.modelOnRecordsDeletedListener);
		}
		refresh();
	}

	protected void preRegisteringModel(MODEL model) {
		// override if needed
	}

	private void unregisterModelListeners() {
		this.model.onAllDataChanged().removeListener(this.modelOnAllDataChangedListener);
		this.model.onRecordsAdded().removeListener(this.modelOnRecordsAddedListener);
		this.model.onRecordsChanged().removeListener(this.modelOnRecordsChangedListener);
		this.model.onRecordsRemoved().removeListener(this.modelOnRecordsDeletedListener);
	}

	public void refresh() {
		count.set(-1);
		sendFullDisplayedRange();
	}

	private void sendFullDisplayedRange() {
		if (!isRendered()) {
			return;
		}
		List<RECORD> records = retrieveRecords(displayedRange.getStart(), displayedRange.getLength());
		UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(records);
		renderedRecords.clear();
		renderedRecords.addNoShift(displayedRange.getStart(), uiRecordMappingResult.recordAndClientRecords);
		updateClientRenderData(uiRecordMappingResult.newUiRecords);
	}

	protected void handleScrollOrResize(ItemRange newRange) {
		onDisplayedRangeChanged.fire(newRange);
		var oldRange = this.displayedRange;
		this.displayedRange = newRange;
		LOGGER.debug("new displayedRange: {}", newRange);
		if (newRange.overlaps(oldRange)) {
			List<UiIdentifiableClientRecord> newUiRecords = new ArrayList<>();
			boolean recordsRemoved = false;
			if (newRange.getStart() < oldRange.getStart()) {
				List<RECORD> records = retrieveRecords(newRange.getStart(), oldRange.getStart() - newRange.getStart());
				UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(records);
				renderedRecords.addNoShift(displayedRange.getStart(), uiRecordMappingResult.recordAndClientRecords);
				LOGGER.debug("newRange.start < oldRange.start: {} < {} so adding {} uiRecords",
						newRange, oldRange, uiRecordMappingResult.newUiRecords.size());
				newUiRecords.addAll(uiRecordMappingResult.newUiRecords);
			} else if (newRange.getStart() > oldRange.getStart()) {
				renderedRecords.removeBeforeNoShift(newRange.getStart());
				recordsRemoved = true;
			}
			if (newRange.getEnd() > oldRange.getEnd()) {
				List<RECORD> records = retrieveRecords(oldRange.getEnd(), newRange.getEnd() - oldRange.getEnd());
				UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(records);
				renderedRecords.addNoShift(oldRange.getEnd(), uiRecordMappingResult.recordAndClientRecords);
				newUiRecords.addAll(uiRecordMappingResult.newUiRecords);
			} else if (newRange.getEnd() < oldRange.getEnd() && newRange.getEnd() < getModelCount()) {
				renderedRecords.removeAfterNoShift(newRange.getEnd());
				recordsRemoved = true;
			}
			boolean recordsAdded = newUiRecords.size() > 0;
			if (recordsAdded || recordsRemoved) {
				updateClientRenderData(newUiRecords);
			}
		} else {
			LOGGER.debug("no overlap!");
			sendFullDisplayedRange();
		}
		LOGGER.debug("displayedRange after scroll update: {}; renderedRecords.size: {}", displayedRange, renderedRecords.size());
	}

	protected void handleModelRecordsAdded(RecordsAddedEvent<RECORD> changeEvent) {
		count.set(count.get() + changeEvent.getLength());
		if (!isRendered()) {
			return;
		}
		if (changeEvent.getStart() < displayedRange.getEnd()) {
			int newRecordsStartIndex = Math.max(changeEvent.getStart(), displayedRange.getStart());
			int newRecordsLength = Math.min(changeEvent.getLength(), Math.min(displayedRange.getEnd() - changeEvent.getStart(), displayedRange.getLength()));
			List<RECORD> newRecords = retrieveRecords(newRecordsStartIndex, newRecordsLength);
			UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(newRecords);
			renderedRecords.insertShifting(changeEvent.getStart(), uiRecordMappingResult.recordAndClientRecords);

			if (displayedRange.getLength() < renderedRecords.size()) {
				renderedRecords.removeNoShift(renderedRecords.getStartIndex() + displayedRange.getLength(), renderedRecords.getEndIndex());
			}

			updateClientRenderData(uiRecordMappingResult.newUiRecords);
		}
	}

	protected void handleModelRecordsChanged(RecordsChangedEvent<RECORD> changeEvent) {
		if (!isRendered()) {
			return;
		}
		if (changeEvent.getItemRange().overlaps(displayedRange)) {
			int queryStartIndex = Math.max(changeEvent.getStart(), displayedRange.getStart());
			int queryEndIndex = Math.min(changeEvent.getEnd(), displayedRange.getEnd());
			List<RECORD> changedRecords = changeEvent.getRecords()
					.map(records -> records.subList(queryStartIndex - changeEvent.getStart(), queryEndIndex - changeEvent.getStart()))
					.orElseGet(() -> retrieveRecords(queryStartIndex, queryEndIndex - queryStartIndex));
			UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(changedRecords);
			renderedRecords.removeNoShift(queryStartIndex, queryStartIndex + changedRecords.size());
			renderedRecords.addNoShift(queryStartIndex, uiRecordMappingResult.recordAndClientRecords);
			updateClientRenderData(uiRecordMappingResult.newUiRecords);
		}
	}

	protected void handleModelRecordsRemoved(RecordsRemovedEvent<RECORD> deleteEvent) {
		count.set(count.get() - deleteEvent.getLength());
		if (!isRendered()) {
			return;
		}
		if (deleteEvent.getStart() < displayedRange.getEnd()) {
			int removedRecordsStartIndex = Math.max(deleteEvent.getStart(), displayedRange.getStart());
			int removedRecordsLength = Math.min(deleteEvent.getLength(), Math.min(displayedRange.getEnd() - deleteEvent.getStart(), displayedRange.getLength()));
			int removeIndexInsideList = removedRecordsStartIndex - displayedRange.getStart();
			renderedRecords.removeNoShift(removedRecordsStartIndex, removedRecordsStartIndex + removedRecordsLength);
			List<RECORD> newRecords = retrieveRecords(displayedRange.getEnd() - removedRecordsLength, removedRecordsLength);
			UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(newRecords);
			renderedRecords.addNoShift(renderedRecords.getEndIndex(), uiRecordMappingResult.recordAndClientRecords);
			updateClientRenderData(uiRecordMappingResult.newUiRecords);
		}
	}

	protected void updateSingleRecordOnClient(RECORD record) {
		if (isRendered()) {
			UiTableClientRecord uiRecord = (UiTableClientRecord) renderedRecords.getUiRecord(record);
			int index = renderedRecords.getIndex(record);
			sendUpdateDataCommandToClient(index, renderedRecords.getUiRecordIds(), List.of(uiRecord), getModelCount());
		}
	}

	protected int getModelCount() {
		if (count.get() < 0) {
			count.set(model.getCount());
		}
		return count.get();
	}

	protected abstract List<RECORD> retrieveRecords(int startIndex, int length);

	private void updateClientRenderData(List<UiIdentifiableClientRecord> newUiRecords) {
		LOGGER.debug("newUiRecords: {}", newUiRecords.size());
		sendUpdateDataCommandToClient(displayedRange.getStart(), renderedRecords.getUiRecordIds(), newUiRecords, getModelCount());
	}

	protected abstract void sendUpdateDataCommandToClient(int start, List<Integer> uiRecordIds, List<UiIdentifiableClientRecord> newUiRecords, int totalNumberOfRecords);

	private UiRecordMappingResult<RECORD> mapToClientRecords(List<RECORD> newRecords) {
		List<UiIdentifiableClientRecord> newUiRecords = new ArrayList<>();
		List<RecordAndClientRecord<RECORD>> recordAndClientRecords = new ArrayList<>();
		for (RECORD r : newRecords) {
			UiIdentifiableClientRecord existingUiRecord = renderedRecords.getUiRecord(r);
			UiIdentifiableClientRecord newUiRecord = createUiIdentifiableClientRecord(r);
			boolean isNew = existingUiRecord == null || !existingUiRecord.getValues().equals(newUiRecord.getValues());
			if (isNew) {
				newUiRecords.add(newUiRecord);
			}
			recordAndClientRecords.add(new RecordAndClientRecord<>(r, isNew ? newUiRecord : existingUiRecord));
		}
		return new UiRecordMappingResult<>(recordAndClientRecords, newUiRecords);
	}

	protected abstract UiIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record);

	private static class UiRecordMappingResult<RECORD> {
		List<RecordAndClientRecord<RECORD>> recordAndClientRecords;
		List<UiIdentifiableClientRecord> newUiRecords;

		public UiRecordMappingResult(List<RecordAndClientRecord<RECORD>> recordAndClientRecords, List<UiIdentifiableClientRecord> newUiRecords) {
			this.recordAndClientRecords = recordAndClientRecords;
			this.newUiRecords = newUiRecords;
		}
	}

	public ObservableValue<Integer> getCount() {
		return count;
	}

	public EqualsAndHashCode<RECORD> getCustomEqualsAndHashCode() {
		return customEqualsAndHashCode;
	}

	public void setCustomEqualsAndHashCode(EqualsAndHashCode<RECORD> customEqualsAndHashCode) {
		this.customEqualsAndHashCode = customEqualsAndHashCode;
		this.renderedRecords = new RenderedRecordsCache<>(customEqualsAndHashCode);
		refresh();
	}
}
