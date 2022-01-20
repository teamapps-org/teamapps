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

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.dto.UiInfiniteItemView;
import org.teamapps.dto.UiInfiniteItemViewDataRequest;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.record.legacy.CacheManipulationHandle;
import org.teamapps.ux.cache.record.legacy.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.itemview.ItemViewRowJustification;
import org.teamapps.ux.component.itemview.ItemViewVerticalItemAlignment;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @deprecated Use {@link InfiniteItemView2} instead!
 */
@Deprecated
public class InfiniteItemView<RECORD> extends AbstractComponent {

	public final Event<ItemClickedEventData<RECORD>> onItemClicked = new Event<>();

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
	protected final ClientRecordCache<RECORD, UiIdentifiableClientRecord> itemCache;

	private final Consumer<Void> modelOnAllDataChangedListener = aVoid -> this.refresh();
	private final Consumer<RecordsAddedEvent<RECORD>> modelOnRecordAddedListener = x -> this.refresh();
	private final Consumer<RecordsChangedEvent<RECORD>> modelOnRecordChangedListener = x -> this.refresh();
	private final Consumer<RecordsRemovedEvent<RECORD>> modelOnRecordDeletedListener = x -> this.refresh();

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
				getSessionContext().queueCommand(new UiInfiniteItemView.RemoveDataCommand(getId(), removedItemIds), aVoid -> operationHandle.commit());
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
	public UiComponent createUiComponent() {
		UiInfiniteItemView ui = new UiInfiniteItemView(itemTemplate.createUiTemplate(), rowHeight);
		mapAbstractUiComponentProperties(ui);
		int recordCount = model.getCount();
		CacheManipulationHandle<List<UiIdentifiableClientRecord>> cacheResponse = itemCache.replaceRecords(model.getRecords(0, Math.min(recordCount, numberOfInitialRecords)));
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
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_INFINITE_ITEM_VIEW_DISPLAYED_RANGE_CHANGED:
				UiInfiniteItemView.DisplayedRangeChangedEvent rangeChangedEvent = (UiInfiniteItemView.DisplayedRangeChangedEvent) event;
				viewportDisplayedRecordClientIds = rangeChangedEvent.getDisplayedRecordIds();
				displayedRangeStart = rangeChangedEvent.getStartIndex();
				displayedRangeLength = rangeChangedEvent.getLength();
				if (rangeChangedEvent.getDataRequest() != null) {
					UiInfiniteItemViewDataRequest dataRequest = rangeChangedEvent.getDataRequest();
					int startIndex = dataRequest.getStartIndex();
					int length = dataRequest.getLength();
					this.sendRecords(startIndex, length, false);
				}
				break;
			case UI_INFINITE_ITEM_VIEW_ITEM_CLICKED: {
				UiInfiniteItemView.ItemClickedEvent itemClickedEvent = (UiInfiniteItemView.ItemClickedEvent) event;
				RECORD record = itemCache.getRecordByClientId(itemClickedEvent.getRecordId());
				if (record != null) {
					onItemClicked.fire(new ItemClickedEventData<>(record, itemClickedEvent.getIsDoubleClick(), itemClickedEvent.getIsRightMouseButton()));
				}
				break;
			}
			case UI_INFINITE_ITEM_VIEW_CONTEXT_MENU_REQUESTED: {
				UiInfiniteItemView.ContextMenuRequestedEvent e = (UiInfiniteItemView.ContextMenuRequestedEvent) event;
				lastSeenContextMenuRequestId = e.getRequestId();
				RECORD record = itemCache.getRecordByClientId(e.getRecordId());
				if (record != null && contextMenuProvider != null) {
					Component contextMenuContent = contextMenuProvider.apply(record);
					if (contextMenuContent != null) {
						queueCommandIfRendered(() -> new UiInfiniteItemView.SetContextMenuContentCommand(getId(), e.getRequestId(), contextMenuContent.createUiReference()));
					} else {
						queueCommandIfRendered(() -> new UiInfiniteItemView.CloseContextMenuCommand(getId(), e.getRequestId()));
					}
				} else {
					closeContextMenu();
				}
				break;
			}
		}
	}

	public boolean isAutoHeight() {
		return autoHeight;
	}

	public void setAutoHeight(boolean autoHeight) {
		this.autoHeight = autoHeight;
	}

	private UiIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		UiIdentifiableClientRecord clientRecord = new UiIdentifiableClientRecord();
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
		queueCommandIfRendered(() -> new UiInfiniteItemView.SetItemTemplateCommand(getId(), itemTemplate.createUiTemplate()));
		return this;
	}

	public float getItemWidth() {
		return itemWidth;
	}

	public InfiniteItemView<RECORD> setItemWidth(float itemWidth) {
		this.itemWidth = itemWidth;
		queueCommandIfRendered(() -> new UiInfiniteItemView.SetItemWidthCommand(getId(), itemWidth));
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
		queueCommandIfRendered(() -> new UiInfiniteItemView.SetVerticalItemAlignmentCommand(getId(), verticalItemAlignment.toUiItemJustification()));
	}

	public int getHorizontalItemMargin() {
		return horizontalItemMargin;
	}

	public InfiniteItemView<RECORD> setHorizontalItemMargin(int horizontalItemMargin) {
		this.horizontalItemMargin = horizontalItemMargin;
		queueCommandIfRendered(() -> new UiInfiniteItemView.SetHorizontalItemMarginCommand(getId(), horizontalItemMargin));
		return this;
	}

	public ItemViewRowJustification getItemJustification() {
		return itemJustification;
	}

	public InfiniteItemView<RECORD> setItemJustification(ItemViewRowJustification itemJustification) {
		this.itemJustification = itemJustification;
		queueCommandIfRendered(() -> new UiInfiniteItemView.SetItemJustificationCommand(getId(), itemJustification.toUiItemJustification()));
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
		model.onAllDataChanged().addListener(this.modelOnAllDataChangedListener);
		model.onRecordsAdded().addListener(this.modelOnRecordAddedListener);
		model.onRecordsChanged().addListener(this.modelOnRecordChangedListener);
		model.onRecordsRemoved().addListener(this.modelOnRecordDeletedListener);
		return this;
	}

	private void unregisterModelListeners() {
		this.model.onAllDataChanged().removeListener(this.modelOnAllDataChangedListener);
		this.model.onRecordsAdded().removeListener(this.modelOnRecordAddedListener);
		this.model.onRecordsChanged().removeListener(this.modelOnRecordChangedListener);
		this.model.onRecordsRemoved().removeListener(this.modelOnRecordDeletedListener);
	}

	public void refresh() {
		sendRecords(displayedRangeStart, displayedRangeLength, true);
	}

	protected void sendRecords(int startIndex, int length, boolean clear) {
		if (isRendered()) {
			int totalCount = model.getCount();
			List<RECORD> records = model.getRecords(startIndex, Math.max(0, Math.min(totalCount - startIndex, length)));
			CacheManipulationHandle<List<UiIdentifiableClientRecord>> cacheResponse;
			if (clear) {
				cacheResponse = itemCache.replaceRecords(records);
			} else {
				cacheResponse = itemCache.addRecords(records);
			}
			getSessionContext().queueCommand(new UiInfiniteItemView.AddDataCommand(getId(), startIndex, cacheResponse.getAndClearResult(), totalCount, clear),
					aVoid -> cacheResponse.commit());
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
		queueCommandIfRendered(() -> new UiInfiniteItemView.CloseContextMenuCommand(getId(), this.lastSeenContextMenuRequestId));
	}

}
