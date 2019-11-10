/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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
import org.teamapps.dto.UiInfiniteItemView;
import org.teamapps.event.Event;
import org.teamapps.event.EventListener;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.itemview.ItemViewItemJustification;
import org.teamapps.ux.component.itemview.ItemViewVerticalItemAlignment;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.Collections;
import java.util.List;

public class InfiniteItemView<RECORD> extends AbstractComponent {

	public final Event<ItemClickedEventData<RECORD>> onItemClicked = new Event<>();

	private int numberOfInitialRecords = 100;
	private Template itemTemplate;
	private float itemWidth;
	private int rowHeight;
	private int horizontalItemMargin = 0;
	boolean autoHeight = false; // make this component set its own height using totalNumberOfRecords! Use with max-height to preserve infinite scrolling!
	private ItemViewItemJustification itemJustification = ItemViewItemJustification.LEFT;
	private ItemViewVerticalItemAlignment verticalItemAlignment = ItemViewVerticalItemAlignment.STRETCH;

	private InfiniteItemViewModel<RECORD> model = new ListInfiniteItemViewModel<>(Collections.emptyList());
	private PropertyExtractor<RECORD> itemPropertyExtractor = new BeanPropertyExtractor<>();
	private final ClientRecordCache<RECORD, UiIdentifiableClientRecord> itemCache;

	private EventListener<Void> modelOnAllDataChangedListener = aVoid -> this.resetClientSideData();
	private EventListener<RECORD> modelOnRecordAddedListener = record -> this.resetClientSideData();
	private EventListener<RECORD> modelOnRecordChangedListener = record -> this.resetClientSideData();
	private EventListener<RECORD> modelOnRecordDeletedListener = record -> this.resetClientSideData();

	public InfiniteItemView(Template itemTemplate, float itemWidth, int rowHeight) {
		this.itemTemplate = itemTemplate;
		this.itemWidth = itemWidth;
		this.rowHeight = rowHeight;

		itemCache = new ClientRecordCache<>(this::createUiIdentifiableClientRecord);
		itemCache.setMaxCapacity(1000);
		itemCache.setPurgeListener(operationHandle -> {
			if (isRendered()) {
				List<Integer> removedItemIds = operationHandle.getResult();
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
		UiInfiniteItemView uiComponent = new UiInfiniteItemView(itemTemplate.createUiTemplate(), rowHeight);
		mapAbstractUiComponentProperties(uiComponent);
		int recordCount = model.getCount();
		CacheManipulationHandle<List<UiIdentifiableClientRecord>> cacheResponse = itemCache.replaceRecords(model.getRecords(0, Math.min(recordCount, numberOfInitialRecords)));
		cacheResponse.commit();
		uiComponent.setData(cacheResponse.getResult());
		uiComponent.setTotalNumberOfRecords(recordCount);
		uiComponent.setItemWidth(itemWidth);
		uiComponent.setHorizontalItemMargin(horizontalItemMargin);
		uiComponent.setItemJustification(itemJustification.toUiItemJustification());
		uiComponent.setVerticalItemAlignment(verticalItemAlignment.toUiItemJustification());
		uiComponent.setAutoHeight(autoHeight);
		return uiComponent;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_INFINITE_ITEM_VIEW_DATA_REQUEST:
				UiInfiniteItemView.DataRequestEvent dataRequestEvent = (UiInfiniteItemView.DataRequestEvent) event;
				int startIndex = dataRequestEvent.getStartIndex();
				int length = dataRequestEvent.getLength();
				sendRecords(startIndex, length, false);
				break;
			case UI_INFINITE_ITEM_VIEW_ITEM_CLICKED:
				UiInfiniteItemView.ItemClickedEvent itemClickedEvent = (UiInfiniteItemView.ItemClickedEvent) event;
				RECORD record = itemCache.getRecordByClientId(itemClickedEvent.getRecordId());
				if (record != null) {
					onItemClicked.fire(new ItemClickedEventData<>(record, itemClickedEvent.getIsDoubleClick()));
				}
				break;
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

	public ItemViewItemJustification getItemJustification() {
		return itemJustification;
	}

	public InfiniteItemView<RECORD> setItemJustification(ItemViewItemJustification itemJustification) {
		this.itemJustification = itemJustification;
		queueCommandIfRendered(() -> new UiInfiniteItemView.SetItemJustificationCommand(getId(), itemJustification.toUiItemJustification()));
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

	public InfiniteItemView<RECORD> setModel(InfiniteItemViewModel<RECORD> model) {
		unregisterModelListeners();
		this.model = model;
		resetClientSideData();
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

	private void resetClientSideData() {
		sendRecords(0, numberOfInitialRecords, true);
	}

	private void sendRecords(int startIndex, int length, boolean clear) {
		if (isRendered()) {
			int totalCount = model.getCount();
			List<RECORD> records = model.getRecords(startIndex, Math.min(totalCount - startIndex, length));
			CacheManipulationHandle<List<UiIdentifiableClientRecord>> cacheResponse;
			if (clear) {
				cacheResponse = itemCache.replaceRecords(records);
			} else {
				cacheResponse = itemCache.addRecords(records);
			}
			getSessionContext().queueCommand(new UiInfiniteItemView.AddDataCommand(getId(), startIndex, cacheResponse.getResult(), totalCount, clear), aVoid -> cacheResponse.commit());
		}
	}

	public void setMaxCacheCapacity(int maxCapacity) {
		itemCache.setMaxCapacity(maxCapacity);
	}

	@Override
	protected void doDestroy() {
		unregisterModelListeners();
	}
}
