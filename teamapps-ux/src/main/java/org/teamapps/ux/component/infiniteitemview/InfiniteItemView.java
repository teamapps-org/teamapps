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
package org.teamapps.ux.component.infiniteitemview;

import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoIdentifiableClientRecord;
import org.teamapps.dto.DtoInfiniteItemView;
import org.teamapps.dto.DtoInfiniteItemViewDataRequestWrapper;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.Disposable;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.ux.cache.record.legacy.CacheManipulationHandle;
import org.teamapps.ux.cache.record.legacy.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.itemview.ItemViewRowJustification;
import org.teamapps.ux.component.itemview.ItemViewVerticalItemAlignment;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;
import org.teamapps.ux.data.extraction.BeanPropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyExtractor;
import org.teamapps.ux.data.extraction.PropertyProvider;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @deprecated Use {@link InfiniteItemView2} instead!
 */
@Deprecated
public class InfiniteItemView<RECORD> extends AbstractComponent {

	public final ProjectorEvent<ItemClickedEventData<RECORD>> onItemClicked = createProjectorEventBoundToUiEvent(DtoInfiniteItemView.ItemClickedEvent.TYPE_ID);

	private int numberOfInitialRecords = 100;
	private Template itemTemplate;
	private float itemWidth;
	private int rowHeight;
	private int horizontalItemMargin = 0;
	boolean autoHeight = false; // make this component set its own height using totalNumberOfRecords! Use with max-height to preserve infinite scrolling!
	private ItemViewRowJustification itemJustification = ItemViewRowJustification.LEFT;
	private ItemViewVerticalItemAlignment verticalItemAlignment = ItemViewVerticalItemAlignment.STRETCH;

	private InfiniteItemViewModel<RECORD> model = new ListInfiniteItemViewModel<>(Collections.emptyList());
	private PropertyProvider<RECORD> itemPropertyProvider = new BeanPropertyExtractor<>();
	protected final ClientRecordCache<RECORD, DtoIdentifiableClientRecord> itemCache;

	private Disposable modelOnAllDataChangedListener;
	private Disposable modelOnRecordAddedListener;
	private Disposable modelOnRecordChangedListener;
	private Disposable modelOnRecordDeletedListener;

	private Function<RECORD, Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	private int displayedRangeStart = 0;
	private int displayedRangeLength = numberOfInitialRecords;
	private List<Integer> viewportDisplayedRecordClientIds = Collections.emptyList();

	public InfiniteItemView(Template itemTemplate, float itemWidth, int rowHeight) {
		this.itemTemplate = itemTemplate;
		this.itemWidth = itemWidth;
		this.rowHeight = rowHeight;

		itemCache = new ClientRecordCache<>(this::createUiIdentifiableClientRecord);
		itemCache.setMaxCapacity(1000);
		itemCache.setPurgeDecider((record, clientId) -> !viewportDisplayedRecordClientIds.contains(clientId));
		itemCache.setPurgeListener(operationHandle -> {
			if (isRendered()) {
				List<Integer> removedItemIds = operationHandle.getAndClearResult();
				final DtoInfiniteItemView.RemoveDataCommand removeDataCommand = new DtoInfiniteItemView.RemoveDataCommand(removedItemIds);
				getSessionContext().sendCommandIfRendered(this, aVoid -> operationHandle.commit(), () -> removeDataCommand);
			} else {
				operationHandle.commit();
			}
		});
	}

	public InfiniteItemView(float itemWidth, int itemHeight) {
		this(BaseTemplate.ITEM_VIEW_ITEM, itemWidth, itemHeight);
	}

	public InfiniteItemView() {
		this(BaseTemplate.ITEM_VIEW_ITEM, 300, 300);
	}

	@Override
	public DtoComponent createDto() {
		DtoInfiniteItemView ui = new DtoInfiniteItemView(itemTemplate.createDtoReference(), rowHeight);
		mapAbstractUiComponentProperties(ui);
		int recordCount = model.getCount();
		CacheManipulationHandle<List<DtoIdentifiableClientRecord>> cacheResponse = itemCache.replaceRecords(model.getRecords(0, Math.min(recordCount, numberOfInitialRecords)));
		cacheResponse.commit();
		ui.setData(cacheResponse.getAndClearResult());
		ui.setTotalNumberOfRecords(recordCount);
		ui.setItemWidth(itemWidth);
		ui.setHorizontalItemMargin(horizontalItemMargin);
		ui.setItemJustification(itemJustification.toUiItemJustification());
		ui.setVerticalItemAlignment(verticalItemAlignment.toUiItemJustification());
		ui.setAutoHeight(autoHeight);
		ui.setContextMenuEnabled(contextMenuProvider != null);
		return ui;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoInfiniteItemView.DisplayedRangeChangedEvent.TYPE_ID -> {
				var rangeChangedEvent = event.as(DtoInfiniteItemView.DisplayedRangeChangedEventWrapper.class);
				viewportDisplayedRecordClientIds = rangeChangedEvent.getDisplayedRecordIds();
				displayedRangeStart = rangeChangedEvent.getStartIndex();
				displayedRangeLength = rangeChangedEvent.getLength();
				if (rangeChangedEvent.getDataRequest() != null) {
					DtoInfiniteItemViewDataRequestWrapper dataRequest = rangeChangedEvent.getDataRequest();
					int startIndex = dataRequest.getStartIndex();
					int length = dataRequest.getLength();
					this.sendRecords(startIndex, length, false);
				}
			}
			case DtoInfiniteItemView.ItemClickedEvent.TYPE_ID -> {
				var itemClickedEvent = event.as(DtoInfiniteItemView.ItemClickedEventWrapper.class);
				RECORD record = itemCache.getRecordByClientId(itemClickedEvent.getRecordId());
				if (record != null) {
					onItemClicked.fire(new ItemClickedEventData<>(record, itemClickedEvent.getIsDoubleClick()));
				}
			}
			case DtoInfiniteItemView.ContextMenuRequestedEvent.TYPE_ID -> {
				var e = event.as(DtoInfiniteItemView.ContextMenuRequestedEventWrapper.class);
				lastSeenContextMenuRequestId = e.getRequestId();
				RECORD record = itemCache.getRecordByClientId(e.getRecordId());
				if (record != null && contextMenuProvider != null) {
					Component contextMenuContent = contextMenuProvider.apply(record);
					if (contextMenuContent != null) {
						sendCommandIfRendered(() -> new DtoInfiniteItemView.SetContextMenuContentCommand(e.getRequestId(), contextMenuContent.createDtoReference()));
					} else {
						sendCommandIfRendered(() -> new DtoInfiniteItemView.CloseContextMenuCommand(e.getRequestId()));
					}
				} else {
					closeContextMenu();
				}
			}
		}
	}

	public boolean isAutoHeight() {
		return autoHeight;
	}

	public void setAutoHeight(boolean autoHeight) {
		this.autoHeight = autoHeight;
	}

	private DtoIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		DtoIdentifiableClientRecord clientRecord = new DtoIdentifiableClientRecord();
		clientRecord.setValues(itemPropertyProvider.getValues(record, itemTemplate.getPropertyNames()));
		return clientRecord;
	}

	public int getNumberOfInitialRecords() {
		return numberOfInitialRecords;
	}

	public InfiniteItemView<RECORD> setNumberOfInitialRecords(int numberOfInitialRecords) {
		this.numberOfInitialRecords = numberOfInitialRecords;
		return this;
	}

	public Template getItemTemplate() {
		return itemTemplate;
	}

	public InfiniteItemView<RECORD> setItemTemplate(Template itemTemplate) {
		this.itemTemplate = itemTemplate;
		sendCommandIfRendered(() -> new DtoInfiniteItemView.SetItemTemplateCommand(itemTemplate.createDtoReference()));
		return this;
	}

	public float getItemWidth() {
		return itemWidth;
	}

	public InfiniteItemView<RECORD> setItemWidth(float itemWidth) {
		this.itemWidth = itemWidth;
		sendCommandIfRendered(() -> new DtoInfiniteItemView.SetItemWidthCommand(itemWidth));
		return this;
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
		reRenderIfRendered();
	}

	public ItemViewVerticalItemAlignment getVerticalItemAlignment() {
		return verticalItemAlignment;
	}

	public void setVerticalItemAlignment(ItemViewVerticalItemAlignment verticalItemAlignment) {
		this.verticalItemAlignment = verticalItemAlignment;
		sendCommandIfRendered(() -> new DtoInfiniteItemView.SetVerticalItemAlignmentCommand(verticalItemAlignment.toUiItemJustification()));
	}

	public int getHorizontalItemMargin() {
		return horizontalItemMargin;
	}

	public InfiniteItemView<RECORD> setHorizontalItemMargin(int horizontalItemMargin) {
		this.horizontalItemMargin = horizontalItemMargin;
		sendCommandIfRendered(() -> new DtoInfiniteItemView.SetHorizontalItemMarginCommand(horizontalItemMargin));
		return this;
	}

	public ItemViewRowJustification getItemJustification() {
		return itemJustification;
	}

	public InfiniteItemView<RECORD> setItemJustification(ItemViewRowJustification itemJustification) {
		this.itemJustification = itemJustification;
		sendCommandIfRendered(() -> new DtoInfiniteItemView.SetItemJustificationCommand(itemJustification.toUiItemJustification()));
		return this;
	}

	public InfiniteItemViewModel<RECORD> getModel() {
		return model;
	}

	public PropertyProvider<RECORD> getItemPropertyProvider() {
		return itemPropertyProvider;
	}

	public void setItemPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.itemPropertyProvider = propertyProvider;
	}

	public void setItemPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.setItemPropertyProvider(propertyExtractor);
	}

	public InfiniteItemView<RECORD> setModel(InfiniteItemViewModel<RECORD> model) {
		unregisterModelListeners();
		this.model = model;
		refresh();
		modelOnAllDataChangedListener = model.onAllDataChanged().addListener(aVoid -> this.refresh());
		modelOnRecordAddedListener = model.onRecordsAdded().addListener(x -> this.refresh());
		modelOnRecordChangedListener = model.onRecordsChanged().addListener(x -> this.refresh());
		modelOnRecordDeletedListener = model.onRecordsRemoved().addListener(x -> this.refresh());
		return this;
	}

	private void unregisterModelListeners() {
		if (modelOnAllDataChangedListener != null) {
			modelOnAllDataChangedListener.dispose();
		}
		if (modelOnRecordAddedListener != null) {
			modelOnRecordAddedListener.dispose();
		}
		if (modelOnRecordChangedListener != null) {
			modelOnRecordChangedListener.dispose();
		}
		if (modelOnRecordDeletedListener != null) {
			modelOnRecordDeletedListener.dispose();
		}
	}

	public void refresh() {
		sendRecords(displayedRangeStart, displayedRangeLength, true);
	}

	protected void sendRecords(int startIndex, int length, boolean clear) {
		if (isRendered()) {
			int totalCount = model.getCount();
			List<RECORD> records = model.getRecords(startIndex, Math.max(0, Math.min(totalCount - startIndex, length)));
			CacheManipulationHandle<List<DtoIdentifiableClientRecord>> cacheResponse;
			if (clear) {
				cacheResponse = itemCache.replaceRecords(records);
			} else {
				cacheResponse = itemCache.addRecords(records);
			}
			final DtoInfiniteItemView.AddDataCommand addDataCommand = new DtoInfiniteItemView.AddDataCommand(startIndex, cacheResponse.getAndClearResult(), totalCount, clear);
			getSessionContext().sendCommandIfRendered(this, aVoid -> cacheResponse.commit(), () -> addDataCommand);
		}
	}

	public void setMaxCacheCapacity(int maxCapacity) {
		itemCache.setMaxCapacity(maxCapacity);
	}

	public Function<RECORD, Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Function<RECORD, Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		sendCommandIfRendered(() -> new DtoInfiniteItemView.CloseContextMenuCommand(this.lastSeenContextMenuRequestId));
	}

}
