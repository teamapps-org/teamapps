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
package org.teamapps.projector.component.trivial.itemview;

import org.teamapps.projector.clientrecordcache.CacheManipulationHandle;
import org.teamapps.projector.clientrecordcache.ClientRecordCache;
import org.teamapps.projector.component.trivial.DtoItemViewItemGroup;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.record.DtoIdentifiableClientRecord;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ItemGroup<HEADERRECORD, RECORD> {

	private final String clientId = UUID.randomUUID().toString();

	private List<RECORD> items = new ArrayList<>();
	private Template itemTemplate;
	private HEADERRECORD headerRecord;

	private boolean headerVisible = true;
	private ItemViewFloatStyle floatStyle = ItemViewFloatStyle.HORIZONTAL_FLOAT;
	private ItemViewRowJustification itemJustification = ItemViewRowJustification.LEFT;
	private float buttonWidth = -1;
	private int horizontalPadding = 0;
	private int verticalPadding = 0;
	private int horizontalItemMargin = 0;
	private int verticalItemMargin = 0;

	private ItemGroupContainer<HEADERRECORD, RECORD> container;
	private PropertyProvider<RECORD> itemPropertyProvider = new BeanPropertyExtractor<>();
	private final ClientRecordCache<RECORD, DtoIdentifiableClientRecord> itemCache = new ClientRecordCache<>(this::createUiIdentifiableClientRecord);

	public ItemGroup() {
		this(null);
	}

	public ItemGroup(HEADERRECORD headerRecord) {
		this(headerRecord, null);
	}

	public ItemGroup(HEADERRECORD headerRecord, Template itemTemplate) {
		this(headerRecord, itemTemplate, null);
	}

	public ItemGroup(HEADERRECORD headerRecord, Template itemTemplate, List<RECORD> items) {
		this.itemTemplate = itemTemplate == null ? BaseTemplates.ITEM_VIEW_ITEM : itemTemplate;
		this.headerRecord = headerRecord;
		this.items.addAll(items != null ? items : Collections.emptyList());
	}

	public DtoItemViewItemGroup createUiItemViewItemGroup() {
		DtoItemViewItemGroup itemGroup = new DtoItemViewItemGroup(itemTemplate != null ? itemTemplate : null);
		itemGroup.setId(clientId);
		if (headerRecord != null) {
			itemGroup.setHeaderData(container.createHeaderClientRecord(headerRecord));
		}

		CacheManipulationHandle<List<DtoIdentifiableClientRecord>> cacheResponse = itemCache.replaceRecords(this.items);
		cacheResponse.commit();
		itemGroup.setItems(cacheResponse.getAndClearResult());
		itemGroup.setHeaderVisible(headerVisible);
		itemGroup.setFloatStyle(floatStyle.toUiItemViewFloatStyle());
		itemGroup.setButtonWidth(buttonWidth);
		itemGroup.setHorizontalPadding(horizontalPadding);
		itemGroup.setVerticalPadding(verticalPadding);
		itemGroup.setHorizontalItemMargin(horizontalItemMargin);
		itemGroup.setVerticalItemMargin(verticalItemMargin);
		itemGroup.setItemJustification(itemJustification.toUiItemJustification());
		return itemGroup;
	}

	private DtoIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		DtoIdentifiableClientRecord clientRecord = new DtoIdentifiableClientRecord();
		clientRecord.setValues(itemPropertyProvider.getValues(record, itemTemplate.getPropertyNames()));
		return clientRecord;
	}

	public void setContainer(ItemGroupContainer<HEADERRECORD, RECORD> container) {
		this.container = container;
	}

	public void addItem(RECORD item) {
		if (items.contains(item)) {
			return;
		}
		items.add(item);
		if (container != null) {
			CacheManipulationHandle<DtoIdentifiableClientRecord> cacheResponse = itemCache.addRecord(item);
			container.handleAddItem(cacheResponse.getAndClearResult(), aVoid -> cacheResponse.commit());
		}
	}

	public void removeItem(RECORD item) {
		boolean removed = items.remove(item);
		if (removed) {
			CacheManipulationHandle<Integer> cacheResponse = itemCache.removeRecord(item);
			container.handleRemoveItem(cacheResponse.getAndClearResult(), aVoid -> cacheResponse.commit());
		}
	}

	public List<RECORD> getItems() {
		return items;
	}

	private void requireRefresh() {
		if (container != null) {
			container.handleRefreshRequired();
		}
	}

	public ItemGroup setItems(List<RECORD> items) {
		this.items = items;
		requireRefresh();
		return this;
	}

	public Template getItemTemplate() {
		return itemTemplate;
	}

	public ItemGroup setItemTemplate(Template itemTemplate) {
		this.itemTemplate = itemTemplate;
		requireRefresh();
		return this;
	}

	public HEADERRECORD getHeaderRecord() {
		return headerRecord;
	}

	public ItemGroup setHeaderRecord(HEADERRECORD headerRecord) {
		this.headerRecord = headerRecord;
		requireRefresh();
		return this;
	}

	public boolean isHeaderVisible() {
		return headerVisible;
	}

	public ItemGroup setHeaderVisible(boolean headerVisible) {
		this.headerVisible = headerVisible;
		requireRefresh();
		return this;
	}

	public ItemViewFloatStyle getFloatStyle() {
		return floatStyle;
	}

	public ItemGroup setFloatStyle(ItemViewFloatStyle floatStyle) {
		this.floatStyle = floatStyle;
		requireRefresh();
		return this;
	}

	public float getButtonWidth() {
		return buttonWidth;
	}

	public ItemGroup setButtonWidth(float buttonWidth) {
		this.buttonWidth = buttonWidth;
		requireRefresh();
		return this;
	}

	public int getHorizontalPadding() {
		return horizontalPadding;
	}

	public ItemGroup setHorizontalPadding(int horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
		requireRefresh();
		return this;
	}

	public int getVerticalPadding() {
		return verticalPadding;
	}

	public ItemGroup setVerticalPadding(int verticalPadding) {
		this.verticalPadding = verticalPadding;
		requireRefresh();
		return this;
	}

	public int getHorizontalItemMargin() {
		return horizontalItemMargin;
	}

	public ItemGroup setHorizontalItemMargin(int horizontalItemMargin) {
		this.horizontalItemMargin = horizontalItemMargin;
		requireRefresh();
		return this;
	}

	public int getVerticalItemMargin() {
		return verticalItemMargin;
	}

	public ItemGroup setVerticalItemMargin(int verticalItemMargin) {
		this.verticalItemMargin = verticalItemMargin;
		requireRefresh();
		return this;
	}

	public ItemViewRowJustification getItemJustification() {
		return itemJustification;
	}

	public ItemGroup setItemJustification(ItemViewRowJustification itemJustification) {
		this.itemJustification = itemJustification;
		requireRefresh();
		return this;
	}

	public ItemGroupContainer<HEADERRECORD, RECORD> getContainer() {
		return container;
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

	/*package-private*/ RECORD getItemByClientId(int clientId) {
		return itemCache.getRecordByClientId(clientId);
	}

	/*package-private*/ String getClientId() {
		return clientId;
	}

}
