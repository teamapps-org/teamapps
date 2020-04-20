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

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.dto.UiInfiniteItemView2;
import org.teamapps.dto.UiInfiniteItemViewDataRequest;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.itemview.ItemViewItemJustification;
import org.teamapps.ux.component.itemview.ItemViewVerticalItemAlignment;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class InfiniteItemView2<RECORD> extends AbstractComponent {

	public final Event<ItemClickedEventData<RECORD>> onItemClicked = new Event<>();

	private int numberOfInitialRecords = 100;
	private Template itemTemplate;
	private float itemWidth;
	private int rowHeight;
	boolean autoHeight = false; // make this component set its own height using totalNumberOfRecords! Use with max-height to preserve infinite scrolling!
	private ItemViewItemJustification itemJustification = ItemViewItemJustification.LEFT;
	private ItemViewVerticalItemAlignment verticalItemAlignment = ItemViewVerticalItemAlignment.STRETCH;

	private InfiniteItemViewModel<RECORD> model = new ListInfiniteItemViewModel<>(Collections.emptyList());
	private PropertyExtractor<RECORD> itemPropertyExtractor = new BeanPropertyExtractor<>();
	private final ClientRecordCache<RECORD, UiIdentifiableClientRecord> itemCache;

	private Consumer<Void> modelOnAllDataChangedListener = aVoid -> this.refresh();
	private Consumer<RECORD> modelOnRecordAddedListener = record -> this.refresh();
	private Consumer<RECORD> modelOnRecordChangedListener = record -> this.refresh();
	private Consumer<RECORD> modelOnRecordDeletedListener = record -> this.refresh();

	private Function<RECORD, Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	private List<Integer> viewportDisplayedRecordClientIds = Collections.emptyList();

	public InfiniteItemView2(Template itemTemplate, float itemWidth, int rowHeight) {
		this.itemTemplate = itemTemplate;
		this.itemWidth = itemWidth;
		this.rowHeight = rowHeight;

		itemCache = new ClientRecordCache<>(this::createUiIdentifiableClientRecord);
		itemCache.setMaxCapacity(1000);
		itemCache.setPurgeDecider((record, clientId) -> !viewportDisplayedRecordClientIds.contains(clientId));
		itemCache.setPurgeListener(operationHandle -> {
			if (isRendered()) {
				List<Integer> removedItemIds = operationHandle.getAndClearResult();
				getSessionContext().queueCommand(new UiInfiniteItemView2.RemoveDataCommand(getId(), removedItemIds), aVoid -> operationHandle.commit());
			} else {
				operationHandle.commit();
			}
		});
	}

	public InfiniteItemView2(float itemWidth, int itemHeight) {
		this(BaseTemplate.ITEM_VIEW_ITEM, itemWidth, itemHeight);
	}

	public InfiniteItemView2() {
		this(BaseTemplate.ITEM_VIEW_ITEM, 300, 300);
	}

	@Override
	public UiComponent createUiComponent() {
		UiInfiniteItemView2 ui = new UiInfiniteItemView2(itemTemplate.createUiTemplate(), rowHeight);
		mapAbstractUiComponentProperties(ui);
		int recordCount = model.getCount();
		ui.setTotalNumberOfRecords(recordCount);
		ui.setItemWidth(itemWidth);
		ui.setItemJustification(itemJustification.toUiItemJustification());
		ui.setVerticalItemAlignment(verticalItemAlignment.toUiItemJustification());
		ui.setAutoHeight(autoHeight);
		ui.setContextMenuEnabled(contextMenuProvider != null);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_INFINITE_ITEM_VIEW2_DISPLAYED_RANGE_CHANGED:
				UiInfiniteItemView2.DisplayedRangeChangedEvent rangeChangedEvent = (UiInfiniteItemView2.DisplayedRangeChangedEvent) event;
				viewportDisplayedRecordClientIds = rangeChangedEvent.getDisplayedRecordIds();
				if (rangeChangedEvent.getDataRequest() != null) {
					UiInfiniteItemViewDataRequest dataRequest = rangeChangedEvent.getDataRequest();
					int startIndex = dataRequest.getStartIndex();
					int length = dataRequest.getLength();
					this.sendRecords(startIndex, length, false);
				}
				break;
			case UI_INFINITE_ITEM_VIEW2_ITEM_CLICKED: {
				UiInfiniteItemView2.ItemClickedEvent itemClickedEvent = (UiInfiniteItemView2.ItemClickedEvent) event;
				RECORD record = itemCache.getRecordByClientId(itemClickedEvent.getRecordId());
				if (record != null) {
					onItemClicked.fire(new ItemClickedEventData<>(record, itemClickedEvent.getIsDoubleClick(), itemClickedEvent.getIsRightMouseButton()));
				}
				break;
			}
			case UI_INFINITE_ITEM_VIEW2_CONTEXT_MENU_REQUESTED: {
				UiInfiniteItemView2.ContextMenuRequestedEvent e = (UiInfiniteItemView2.ContextMenuRequestedEvent) event;
				lastSeenContextMenuRequestId = e.getRequestId();
				RECORD record = itemCache.getRecordByClientId(e.getRecordId());
				if (record != null && contextMenuProvider != null) {
					Component contextMenuContent = contextMenuProvider.apply(record);
					if (contextMenuContent != null) {
						queueCommandIfRendered(() -> new UiInfiniteItemView2.SetContextMenuContentCommand(getId(), e.getRequestId(), contextMenuContent.createUiReference()));
					} else {
						queueCommandIfRendered(() -> new UiInfiniteItemView2.CloseContextMenuCommand(getId(), e.getRequestId()));
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
		clientRecord.setValues(itemPropertyExtractor.getValues(record, itemTemplate.getDataKeys()));
		return clientRecord;
	}

	public int getNumberOfInitialRecords() {
		return numberOfInitialRecords;
	}

	public InfiniteItemView2<RECORD> setNumberOfInitialRecords(int numberOfInitialRecords) {
		this.numberOfInitialRecords = numberOfInitialRecords;
		return this;
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
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetVerticalItemAlignmentCommand(getId(), verticalItemAlignment.toUiItemJustification()));
	}

	public ItemViewItemJustification getItemJustification() {
		return itemJustification;
	}

	public InfiniteItemView2<RECORD> setItemJustification(ItemViewItemJustification itemJustification) {
		this.itemJustification = itemJustification;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetItemJustificationCommand(getId(), itemJustification.toUiItemJustification()));
		return this;
	}

	public InfiniteItemViewModel<RECORD> getModel() {
		return model;
	}

	public PropertyExtractor<RECORD> getItemPropertyExtractor() {
		return itemPropertyExtractor;
	}

	public void setItemPropertyExtractor(PropertyExtractor<RECORD> itemPropertyExtractor) {
		this.itemPropertyExtractor = itemPropertyExtractor;
	}

	public InfiniteItemView2<RECORD> setModel(InfiniteItemViewModel<RECORD> model) {
		unregisterModelListeners();
		this.model = model;
		refresh();
		model.onAllDataChanged().addListener(this.modelOnAllDataChangedListener);
		model.onRecordAdded().addListener(this.modelOnRecordAddedListener);
		model.onRecordChanged().addListener(this.modelOnRecordChangedListener);
		model.onRecordDeleted().addListener(this.modelOnRecordDeletedListener);
		return this;
	}

	private void unregisterModelListeners() {
		this.model.onAllDataChanged().removeListener(this.modelOnAllDataChangedListener);
		this.model.onRecordAdded().removeListener(this.modelOnRecordAddedListener);
		this.model.onRecordChanged().removeListener(this.modelOnRecordChangedListener);
		this.model.onRecordDeleted().removeListener(this.modelOnRecordDeletedListener);
	}

	public void refresh() {
		sendRecords(0, numberOfInitialRecords, true);
	}

	private void sendRecords(int startIndex, int length, boolean clear) {
		if (isRendered()) {
			int totalCount = model.getCount();
			List<RECORD> records = model.getRecords(startIndex, Math.max(0, Math.min(totalCount - startIndex, length)));
			CacheManipulationHandle<List<UiIdentifiableClientRecord>> cacheResponse;
			if (clear) {
				cacheResponse = itemCache.replaceRecords(records);
			} else {
				cacheResponse = itemCache.addRecords(records);
			}
			getSessionContext().queueCommand(new UiInfiniteItemView2.AddDataCommand(getId(), startIndex, cacheResponse.getAndClearResult(), totalCount, clear),
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
		queueCommandIfRendered(() -> new UiInfiniteItemView2.CloseContextMenuCommand(getId(), this.lastSeenContextMenuRequestId));
	}

}
