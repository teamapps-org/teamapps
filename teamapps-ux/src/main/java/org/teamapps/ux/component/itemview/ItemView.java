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
package org.teamapps.ux.component.itemview;

import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.dto.UiItemView;
import org.teamapps.event.Event;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemView<HEADERRECORD, RECORD> extends AbstractComponent {

	public Event<ItemClickedEventData<RECORD>> onItemClicked = new Event<>();

	private final List<ItemGroup<HEADERRECORD, RECORD>> itemGroups = new ArrayList<>();

	private int horizontalPadding = 10;
	private int verticalPadding = 0;
	private int groupSpacing;
	private ItemViewItemBackgroundMode itemBackgroundMode = ItemViewItemBackgroundMode.LIGHT;

	private String filter;

	private Template groupHeaderTemplate = BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE;
	private PropertyExtractor<HEADERRECORD> headerPropertyExtractor = new BeanPropertyExtractor<>();

	public ItemView() {
		this(null);
	}

	public ItemView(List<ItemGroup<HEADERRECORD, RECORD>> itemGroups) {
		if (itemGroups != null) {
			itemGroups.forEach(this::addGroup);
		}
	}

	/*
		ItemView(Template headerTemplate,  Template template9

		addGroup(Icon icon, String title) -> ItemGroup
		addGroup(Icon icon, String title, Template buttonTemplate) -> ItemGroup

		ItemGroup:
			addButton(Icon icon, String title, [String description]) -> onClickEvent || ItemGroupButton -> onClickEvent

	 */

	public Template getGroupHeaderTemplate() {
		return groupHeaderTemplate;
	}

	public void setGroupHeaderTemplate(Template groupHeaderTemplate) {
		this.groupHeaderTemplate = groupHeaderTemplate;
		reRenderIfRendered();
	}

	public List<ItemGroup> getItemGroups() {
		return new ArrayList<>(itemGroups);
	}

	public ItemGroup<HEADERRECORD, RECORD> addGroup() {
		return addGroup(null, null);
	}

	public ItemGroup<HEADERRECORD, RECORD> addGroup(HEADERRECORD headerRecord) {
		return addGroup(headerRecord, null);
	}

	public ItemGroup<HEADERRECORD, RECORD> addGroup(HEADERRECORD headerRecord, Template itemTemplate) {
		ItemGroup<HEADERRECORD, RECORD> group = new ItemGroup<>(headerRecord, itemTemplate);
		addGroup(group);
		return group;
	}

	public void addGroup(ItemGroup<HEADERRECORD, RECORD> group) {
		itemGroups.add(group);
		group.setContainer(new ItemGroupContainer<>() {
			@Override
			public UiIdentifiableClientRecord createHeaderClientRecord(HEADERRECORD headerRecord) {
				UiIdentifiableClientRecord clientRecord = new UiIdentifiableClientRecord();
				clientRecord.setValues(headerPropertyExtractor.getValues(headerRecord, groupHeaderTemplate.getDataKeys()));
				return clientRecord;
			}

			@Override
			public void handleAddItem(UiIdentifiableClientRecord itemClientRecord, Consumer<Void> uiCommandCallback) {
				if (isRendered()) {
					getSessionContext().queueCommand(new UiItemView.AddItemCommand(getId(), group.getClientId(), itemClientRecord), uiCommandCallback);
				} else {
					uiCommandCallback.accept(null);
				}
			}

			@Override
			public void handleRemoveItem(int itemClientRecordId, Consumer<Void> uiCommandCallback) {
				if (isRendered()) {
					getSessionContext().queueCommand(new UiItemView.RemoveItemCommand(getId(), group.getClientId(), itemClientRecordId), uiCommandCallback);
				}
			}

			@Override
			public void handleRefreshRequired() {
				queueCommandIfRendered(() -> new UiItemView.RefreshItemGroupCommand(getId(), group.createUiItemViewItemGroup()));
			}
		});
		queueCommandIfRendered(() -> new UiItemView.AddItemGroupCommand(getId(), group.createUiItemViewItemGroup()));
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
		queueCommandIfRendered(() -> new UiItemView.SetFilterCommand(getId(), filter));
	}

	public void removeAllGroups() {
		new ArrayList<>(itemGroups).forEach(this::removeItemGroup);
	}

	public void removeItemGroup(ItemGroup itemGroup) {
		itemGroups.remove(itemGroup);
		queueCommandIfRendered(() -> new UiItemView.RemoveItemGroupCommand(getId(), itemGroup.getClientId()));
	}

	public int getHorizontalPadding() {
		return horizontalPadding;
	}

	public void setHorizontalPadding(int horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
		reRenderIfRendered();
	}

	public int getVerticalPadding() {
		return verticalPadding;
	}

	public void setVerticalPadding(int verticalPadding) {
		this.verticalPadding = verticalPadding;
		reRenderIfRendered();
	}

	public int getGroupSpacing() {
		return groupSpacing;
	}

	public void setGroupSpacing(int groupSpacing) {
		this.groupSpacing = groupSpacing;
		reRenderIfRendered();
	}

	public ItemViewItemBackgroundMode getItemBackgroundMode() {
		return itemBackgroundMode;
	}

	public void setItemBackgroundMode(ItemViewItemBackgroundMode itemBackgroundMode) {
		this.itemBackgroundMode = itemBackgroundMode;
		reRenderIfRendered();
	}

	public PropertyExtractor<HEADERRECORD> getHeaderPropertyExtractor() {
		return headerPropertyExtractor;
	}

	public void setHeaderPropertyExtractor(PropertyExtractor<HEADERRECORD> headerPropertyExtractor) {
		this.headerPropertyExtractor = headerPropertyExtractor;
		reRenderIfRendered();
	}

	@Override
	public UiComponent createUiComponent() {
		UiItemView uiItemView = new UiItemView(getId());
		mapAbstractUiComponentProperties(uiItemView);
		uiItemView.setGroupHeaderTemplate(groupHeaderTemplate != null ? groupHeaderTemplate.createUiTemplate() : null);
		uiItemView.setItemGroups(this.itemGroups.stream()
				.map(group -> group.createUiItemViewItemGroup())
				.collect(Collectors.toList()));
		uiItemView.setHorizontalPadding(horizontalPadding);
		uiItemView.setVerticalPadding(verticalPadding);
		uiItemView.setGroupSpacing(groupSpacing);
		uiItemView.setItemBackgroundMode(itemBackgroundMode.toUiItemBackgroundMode());
		uiItemView.setFilter(filter);
		return uiItemView;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_ITEM_VIEW_ITEM_CLICKED:
				UiItemView.ItemClickedEvent itemClickedEvent = (UiItemView.ItemClickedEvent) event;
				ItemGroup<HEADERRECORD, RECORD> itemGroup = getItemGroupByClientId(itemClickedEvent.getGroupId());
				if (itemGroup != null) {
					RECORD item = itemGroup.getItemByClientId(itemClickedEvent.getItemId());
					this.onItemClicked.fire(new ItemClickedEventData<>(itemGroup, item));
				}
				break;
		}
	}

	private ItemGroup<HEADERRECORD, RECORD> getItemGroupByClientId(String clientId) {
		return itemGroups.stream()
				.filter(group -> Objects.equals(group.getClientId(), clientId))
				.findFirst().orElse(null);
	}

	@Override
	protected void doDestroy() {
		// nothing to do
	}
}
