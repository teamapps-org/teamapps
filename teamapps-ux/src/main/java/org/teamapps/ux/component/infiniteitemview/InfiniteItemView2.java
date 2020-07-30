/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.dto.UiInfiniteItemView2;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InfiniteItemView2<RECORD> extends AbstractComponent {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final Event<ItemClickedEventData<RECORD>> onItemClicked = new Event<>();

	private Template itemTemplate;
	private float itemWidth;
	private float itemHeight;
	// private float horizontalSpacing; // TODO
	// private float verticalSpacing; // TODO
	private HorizontalElementAlignment itemContentHorizontalAlignment = HorizontalElementAlignment.STRETCH;
	private VerticalElementAlignment itemContentVerticalAlignment = VerticalElementAlignment.STRETCH;
	// private ItemViewRowJustification rowHorizontalAlignment = ItemViewRowJustification.LEFT; // TODO

	private InfiniteItemViewModel<RECORD> model = new ListInfiniteItemViewModel<>(Collections.emptyList());
	private PropertyProvider<RECORD> itemPropertyProvider = new BeanPropertyExtractor<>();

	private ItemRange renderedRange = ItemRange.startEnd(0, 0);
	private RenderedRecordsCache<RECORD> renderedRecords = new RenderedRecordsCache<>();
	private int clientRecordIdCounter = 0;

	private Function<RECORD, Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	private int cachedModelCount = -1;

	private final Consumer<Void> modelOnAllDataChangedListener = aVoid -> this.refresh();
	private final Consumer<ItemRangeChangeEvent<RECORD>> modelOnRecordsAddedListener = this::handleModelRecordsAdded;
	private final Consumer<ItemRangeChangeEvent<RECORD>> modelOnRecordsChangedListener = this::handleModelRecordsChanged;
	private final Consumer<ItemRangeChangeEvent<RECORD>> modelOnRecordsDeletedListener = this::handleModelRecordsDeleted;

	public InfiniteItemView2(Template itemTemplate, float itemWidth, int itemHeight) {
		this.itemTemplate = itemTemplate;
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
	}

	public InfiniteItemView2(float itemWidth, int itemHeight) {
		this(BaseTemplate.ITEM_VIEW_ITEM, itemWidth, itemHeight);
	}

	public InfiniteItemView2() {
		this(BaseTemplate.ITEM_VIEW_ITEM, 300, 300);
	}

	@Override
	public UiComponent createUiComponent() {
		UiInfiniteItemView2 ui = new UiInfiniteItemView2(itemTemplate.createUiTemplate());
		mapAbstractUiComponentProperties(ui);
		ui.setItemWidth(itemWidth);
		ui.setItemHeight(itemHeight);
		// ui.setHorizontalSpacing(horizontalSpacing);
		// ui.setVerticalSpacing(verticalSpacing);
		ui.setItemContentHorizontalAlignment(itemContentHorizontalAlignment.toUiHorizontalElementAlignment());
		ui.setItemContentVerticalAlignment(itemContentVerticalAlignment.toUiVerticalElementAlignment());
		// ui.setRowHorizontalAlignment(rowHorizontalAlignment.toUiItemJustification());
		ui.setContextMenuEnabled(contextMenuProvider != null);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_INFINITE_ITEM_VIEW2_RENDERED_ITEM_RANGE_CHANGED:
				UiInfiniteItemView2.RenderedItemRangeChangedEvent d = (UiInfiniteItemView2.RenderedItemRangeChangedEvent) event;
				handleScrollOrResize(ItemRange.startEnd(d.getStartIndex(), d.getEndIndex()));
				break;
			case UI_INFINITE_ITEM_VIEW2_ITEM_CLICKED: {
				UiInfiniteItemView2.ItemClickedEvent e = (UiInfiniteItemView2.ItemClickedEvent) event;
				renderedRecords.getRecord(e.getRecordId()).ifPresent(record -> onItemClicked.fire(new ItemClickedEventData<>(record, e.getIsDoubleClick(), e.getIsRightMouseButton())));
				break;
			}
			case UI_INFINITE_ITEM_VIEW2_CONTEXT_MENU_REQUESTED: {
				UiInfiniteItemView2.ContextMenuRequestedEvent e = (UiInfiniteItemView2.ContextMenuRequestedEvent) event;
				lastSeenContextMenuRequestId = e.getRequestId();
				if (contextMenuProvider == null) {
					closeContextMenu();
				} else {
					renderedRecords.getRecord(e.getRecordId()).ifPresent(record -> {
						Component contextMenuContent = contextMenuProvider.apply(record);
						if (contextMenuContent != null) {
							queueCommandIfRendered(() -> new UiInfiniteItemView2.SetContextMenuContentCommand(getId(), e.getRequestId(), contextMenuContent.createUiReference()));
						} else {
							queueCommandIfRendered(() -> new UiInfiniteItemView2.CloseContextMenuCommand(getId(), e.getRequestId()));
						}
					});
				}
				break;
			}
		}
	}

	public InfiniteItemViewModel<RECORD> getModel() {
		return model;
	}

	public InfiniteItemView2<RECORD> setModel(InfiniteItemViewModel<RECORD> model) {
		unregisterModelListeners();
		this.model = model;
		refresh();
		model.onAllDataChanged().addListener(this.modelOnAllDataChangedListener);
		model.onRecordsAdded().addListener(this.modelOnRecordsAddedListener);
		model.onRecordsChanged().addListener(this.modelOnRecordsChangedListener);
		model.onRecordsDeleted().addListener(this.modelOnRecordsDeletedListener);
		return this;
	}

	private void unregisterModelListeners() {
		this.model.onAllDataChanged().removeListener(this.modelOnAllDataChangedListener);
		this.model.onRecordsAdded().removeListener(this.modelOnRecordsAddedListener);
		this.model.onRecordsChanged().removeListener(this.modelOnRecordsChangedListener);
		this.model.onRecordsDeleted().removeListener(this.modelOnRecordsDeletedListener);
	}

	public void refresh() {
		cachedModelCount = -1;
		sendFullRenderedRange();
	}

	private void sendFullRenderedRange() {
		if (!isRendered()) {
			return;
		}
		List<RECORD> records = retrieveRecords(renderedRange.getStart(), renderedRange.getLength());
		UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(records);
		renderedRecords = new RenderedRecordsCache<>(uiRecordMappingResult.recordAndClientRecords);
		updateClientRenderData(uiRecordMappingResult.newUiRecords);
	}

	private void handleScrollOrResize(ItemRange newRange) {
		var oldRange = this.renderedRange;
		this.renderedRange = newRange;
		LOGGER.debug("new renderedRange: {}", newRange);
		if (newRange.overlaps(oldRange)) {
			List<UiIdentifiableClientRecord> newUiRecords = new ArrayList<>();
			boolean recordsRemoved = false;
			if (newRange.getStart() < oldRange.getStart()) {
				List<RECORD> records = retrieveRecords(newRange.getStart(), oldRange.getStart() - newRange.getStart());
				UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(records);
				renderedRecords.insert(0, uiRecordMappingResult.recordAndClientRecords);
				LOGGER.debug("newRange.start < oldRange.start: {} < {} so adding {} uiRecords",
						newRange, oldRange, uiRecordMappingResult.newUiRecords.size());
				newUiRecords.addAll(uiRecordMappingResult.newUiRecords);
			} else if (newRange.getStart() > oldRange.getStart()) {
				renderedRecords.remove(0, newRange.getStart() - oldRange.getStart());
				recordsRemoved = true;
			}
			if (newRange.getEnd() > oldRange.getEnd()) {
				List<RECORD> records = retrieveRecords(oldRange.getEnd(), newRange.getEnd() - oldRange.getEnd());
				UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(records);
				renderedRecords.insert(renderedRecords.size(), uiRecordMappingResult.recordAndClientRecords);
				newUiRecords.addAll(uiRecordMappingResult.newUiRecords);
			} else if (newRange.getEnd() < oldRange.getEnd() && newRange.getEnd() < getModelCount()) {
				int absoluteDeleteStartIndex = newRange.getEnd();
				int absoluteDeleteEndIndex = Math.min(oldRange.getEnd(), getModelCount());
				renderedRecords.remove(renderedRecords.size() - (absoluteDeleteEndIndex - absoluteDeleteStartIndex), renderedRecords.size());
				recordsRemoved = true;
			}
			boolean recordsAdded = newUiRecords.size() > 0;
			if (recordsAdded || recordsRemoved) {
				updateClientRenderData(newUiRecords);
			}
		} else {
			LOGGER.debug("no overlap!");
			sendFullRenderedRange();
		}
		LOGGER.debug("renderedRange after scroll update: {}; renderedRecords.size: {}", renderedRange, renderedRecords.size());
	}

	private void handleModelRecordsAdded(ItemRangeChangeEvent<RECORD> changeEvent) {
		cachedModelCount += changeEvent.getLength();
		if (!isRendered()) {
			return;
		}
		if (changeEvent.getStart() < renderedRange.getEnd()) {
			int newRecordsStartIndex = Math.max(changeEvent.getStart(), renderedRange.getStart());
			int newRecordsLength = Math.min(changeEvent.getLength(), Math.min(renderedRange.getEnd() - changeEvent.getStart(), renderedRange.getLength()));
			int listInsertIndex = newRecordsStartIndex - renderedRange.getStart();
			List<RECORD> newRecords = retrieveRecords(newRecordsStartIndex, newRecordsLength);
			UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(newRecords);
			renderedRecords.insert(listInsertIndex, uiRecordMappingResult.recordAndClientRecords);

			if (renderedRange.getLength() < renderedRecords.size()) {
				renderedRecords.remove(renderedRange.getLength(), renderedRecords.size());
			}

			updateClientRenderData(uiRecordMappingResult.newUiRecords);
		}
	}

	private void handleModelRecordsChanged(ItemRangeChangeEvent<RECORD> changeEvent) {
		if (!isRendered()) {
			return;
		}
		if (changeEvent.getItemRange().overlaps(renderedRange)) {
			int queryStartIndex = Math.max(changeEvent.getStart(), renderedRange.getStart());
			int queryEndIndex = Math.min(changeEvent.getEnd(), renderedRange.getEnd());
			List<RECORD> changedRecords = changeEvent.getRecords()
					.map(records -> records.subList(queryStartIndex - changeEvent.getStart(), queryEndIndex - changeEvent.getStart()))
					.orElseGet(() -> retrieveRecords(queryStartIndex, queryEndIndex - queryStartIndex));
			UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(changedRecords);
			renderedRecords.remove(queryStartIndex - renderedRange.getStart(), queryEndIndex - renderedRange.getStart());
			renderedRecords.insert(queryStartIndex - renderedRange.getStart(), uiRecordMappingResult.recordAndClientRecords);
			updateClientRenderData(uiRecordMappingResult.newUiRecords);
		}
	}

	private void handleModelRecordsDeleted(ItemRangeChangeEvent<RECORD> changeEvent) {
		cachedModelCount -= changeEvent.getLength();
		if (!isRendered()) {
			return;
		}
		if (changeEvent.getStart() < renderedRange.getEnd()) {
			int removedRecordsStartIndex = Math.max(changeEvent.getStart(), renderedRange.getStart());
			int removedRecordsLength = Math.min(changeEvent.getLength(), Math.min(renderedRange.getEnd() - changeEvent.getStart(), renderedRange.getLength()));
			int removeIndexInsideList = removedRecordsStartIndex - renderedRange.getStart();
			List<RECORD> newRecords = retrieveRecords(renderedRange.getEnd(), removedRecordsLength);
			UiRecordMappingResult<RECORD> uiRecordMappingResult = mapToClientRecords(newRecords);
			renderedRecords.remove(removeIndexInsideList, removeIndexInsideList + removedRecordsLength);
			renderedRecords.insert(renderedRecords.size(), uiRecordMappingResult.recordAndClientRecords);
			updateClientRenderData(uiRecordMappingResult.newUiRecords);
		}
	}

	private int getModelCount() {
		if (cachedModelCount < 0) {
			cachedModelCount = model.getCount();
		}
		return cachedModelCount;
	}

	private List<RECORD> retrieveRecords(int startIndex, int length) {
		if (startIndex > getModelCount() || length <= 0) {
			return Collections.emptyList();
		}
		int actualStartIndex = Math.max(startIndex, 0);
		int actualLength = Math.min(getModelCount() - startIndex, length);
		return model.getRecords(actualStartIndex, actualLength);
	}

	private void updateClientRenderData(List<UiIdentifiableClientRecord> newUiRecords) {
		LOGGER.debug("newUiRecords: {}", newUiRecords.size());
		queueCommandIfRendered(() -> {
			LOGGER.debug("SENDING: renderedRange.start: {}; renderedRecords.size: {}; Count: {}", renderedRange.getStart(), renderedRecords.size(), getModelCount());
			return new UiInfiniteItemView2.SetDataCommand(
					getId(),
					renderedRange.getStart(),
					renderedRecords.getUiRecordIds(),
					newUiRecords,
					getModelCount()
			);
		});
	}


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

	private UiIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		UiIdentifiableClientRecord clientRecord = new UiIdentifiableClientRecord();
		clientRecord.setId(++clientRecordIdCounter);
		clientRecord.setValues(itemPropertyProvider.getValues(record, itemTemplate.getDataKeys()));
		return clientRecord;
	}

	public Function<RECORD, Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Function<RECORD, Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		queueCommandIfRendered(() -> new UiInfiniteItemView2.CloseContextMenuCommand(getId(), this.lastSeenContextMenuRequestId));
	}

	public Template getItemTemplate() {
		return itemTemplate;
	}

	public InfiniteItemView2<RECORD> setItemTemplate(Template itemTemplate) {
		this.itemTemplate = itemTemplate;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetItemTemplateCommand(getId(), itemTemplate.createUiTemplate()));
		return this;
	}

	public float getItemWidth() {
		return itemWidth;
	}

	public InfiniteItemView2<RECORD> setItemWidth(float itemWidth) {
		this.itemWidth = itemWidth;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetItemWidthCommand(getId(), itemWidth));
		return this;
	}

	public float getItemHeight() {
		return itemHeight;
	}

	public InfiniteItemView2<RECORD> setItemHeight(float itemHeight) {
		this.itemHeight = itemHeight;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetItemHeightCommand(getId(), itemHeight));
		return this;
	}

	// public float getHorizontalSpacing() {
	// 	return horizontalSpacing;
	// }
	//
	// public InfiniteItemView2<RECORD> setHorizontalSpacing(float horizontalSpacing) {
	// 	this.horizontalSpacing = horizontalSpacing;
	// 	queueCommandIfRendered(() -> new UiInfiniteItemView2.SetHorizontalSpacingCommand(getId(), horizontalSpacing));
	// 	return this;
	// }
	//
	// public float getVerticalSpacing() {
	// 	return verticalSpacing;
	// }
	//
	// public InfiniteItemView2<RECORD> setVerticalSpacing(float verticalSpacing) {
	// 	this.verticalSpacing = verticalSpacing;
	// 	queueCommandIfRendered(() -> new UiInfiniteItemView2.SetVerticalSpacingCommand(getId(), verticalSpacing));
	// 	return this;
	// }

	public HorizontalElementAlignment getItemContentHorizontalAlignment() {
		return itemContentHorizontalAlignment;
	}

	public InfiniteItemView2<RECORD> setItemContentHorizontalAlignment(HorizontalElementAlignment itemContentHorizontalAlignment) {
		this.itemContentHorizontalAlignment = itemContentHorizontalAlignment;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetItemContentHorizontalAlignmentCommand(getId(), itemContentHorizontalAlignment.toUiHorizontalElementAlignment()));
		return this;
	}

	public VerticalElementAlignment getItemContentVerticalAlignment() {
		return itemContentVerticalAlignment;
	}

	public InfiniteItemView2<RECORD> setItemContentVerticalAlignment(VerticalElementAlignment itemContentVerticalAlignment) {
		this.itemContentVerticalAlignment = itemContentVerticalAlignment;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetItemContentVerticalAlignmentCommand(getId(), itemContentVerticalAlignment.toUiVerticalElementAlignment()));
		return this;
	}

	// public ItemViewRowJustification getRowHorizontalAlignment() {
	// 	return rowHorizontalAlignment;
	// }

	// public InfiniteItemView2<RECORD> setRowHorizontalAlignment(ItemViewRowJustification rowHorizontalAlignment) {
	// 	this.rowHorizontalAlignment = rowHorizontalAlignment;
	// 	queueCommandIfRendered(() -> new UiInfiniteItemView2.SetRowHorizontalAlignmentCommand(getId(), rowHorizontalAlignment.toUiItemJustification()));
	// 	return this;
	// }

	public PropertyProvider<RECORD> getItemPropertyProvider() {
		return itemPropertyProvider;
	}

	public void setItemPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.itemPropertyProvider = propertyProvider;
	}

	public void setItemPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.setItemPropertyProvider(propertyExtractor);
	}

	private static class RecordAndClientRecord<RECORD> {
		final RECORD record;
		final UiIdentifiableClientRecord uiRecord;

		public RecordAndClientRecord(RECORD record, UiIdentifiableClientRecord uiRecord) {
			this.record = record;
			this.uiRecord = uiRecord;
		}
	}

	private static class RenderedRecordsCache<RECORD> {
		private final Map<RECORD, UiIdentifiableClientRecord> uiRecordsByRecord = new HashMap<>();
		private final List<RecordAndClientRecord<RECORD>> recordPairs = new ArrayList<>();

		public RenderedRecordsCache() {
		}

		public RenderedRecordsCache(List<RecordAndClientRecord<RECORD>> recordPairs) {
			insert(0, recordPairs);
		}

		UiIdentifiableClientRecord getUiRecord(RECORD record) {
			return uiRecordsByRecord.get(record);
		}

		Optional<RECORD> getRecord(int uiRecordId) {
			// no fast implementation needed! only called on user click
			return recordPairs.stream()
					.filter(rr -> rr.uiRecord.getId() == uiRecordId)
					.map(rr -> rr.record)
					.findFirst();
		}

		List<Integer> getUiRecordIds() {
			return recordPairs.stream()
					.map(rr -> rr.uiRecord.getId())
					.collect(Collectors.toList());
		}

		void insert(int listInsertIndex, List<RecordAndClientRecord<RECORD>> newClientRecordPairs) {
			recordPairs.addAll(listInsertIndex, newClientRecordPairs);
			for (RecordAndClientRecord<RECORD> rr : newClientRecordPairs) {
				uiRecordsByRecord.put(rr.record, rr.uiRecord);
			}
		}

		void remove(int startIndex, int endIndex) {
			List<RecordAndClientRecord<RECORD>> recordsToBeRemoved = recordPairs.subList(startIndex, endIndex);
			for (RecordAndClientRecord<RECORD> rr : recordsToBeRemoved) {
				uiRecordsByRecord.remove(rr.record);
			}
			recordsToBeRemoved.clear();
		}

		int size() {
			return recordPairs.size();
		}
	}

	private static class UiRecordMappingResult<RECORD> {
		List<RecordAndClientRecord<RECORD>> recordAndClientRecords;
		List<UiIdentifiableClientRecord> newUiRecords;

		public UiRecordMappingResult(List<RecordAndClientRecord<RECORD>> recordAndClientRecords, List<UiIdentifiableClientRecord> newUiRecords) {
			this.recordAndClientRecords = recordAndClientRecords;
			this.newUiRecords = newUiRecords;
		}
	}
}
