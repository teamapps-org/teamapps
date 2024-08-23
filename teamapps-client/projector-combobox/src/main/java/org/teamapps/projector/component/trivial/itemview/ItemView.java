package org.teamapps.projector.component.trivial.itemview;/*-
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

import org.teamapps.projector.component.AbstractComponent;
import org.teamapps.projector.component.DtoComponent;
import org.teamapps.projector.component.trivial.DtoItemView;
import org.teamapps.projector.component.trivial.DtoItemViewClientObjectChannel;
import org.teamapps.projector.component.trivial.DtoItemViewEventHandler;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.record.DtoIdentifiableClientRecord;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemView<HEADERRECORD, RECORD> extends AbstractComponent implements DtoItemViewEventHandler {

	private final DtoItemViewClientObjectChannel clientObjectChannel = new DtoItemViewClientObjectChannel(getClientObjectChannel());

	public ProjectorEvent<ItemClickedEventData<RECORD>> onItemClicked = new ProjectorEvent<>(clientObjectChannel::toggleItemClickedEvent);

	private final List<ItemGroup<HEADERRECORD, RECORD>> itemGroups = new ArrayList<>();

	private int horizontalPadding = 10;
	private int verticalPadding = 0;
	private int groupSpacing;
	private ItemViewItemBackgroundMode itemBackgroundMode = ItemViewItemBackgroundMode.LIGHT;

	private String filter;

	private Template groupHeaderTemplate = BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE;
	private PropertyProvider<HEADERRECORD> headerPropertyProvider = new BeanPropertyExtractor<>();

	public ItemView() {
		this(null);
	}

	public ItemView(List<ItemGroup<HEADERRECORD, RECORD>> itemGroups) {
		if (itemGroups != null) {
			itemGroups.forEach(this::addGroup);
		}
	}

	public Template getGroupHeaderTemplate() {
		return groupHeaderTemplate;
	}

	public void setGroupHeaderTemplate(Template groupHeaderTemplate) {
		this.groupHeaderTemplate = groupHeaderTemplate;
		clientObjectChannel.setGroupHeaderTemplate(groupHeaderTemplate);
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
			public DtoIdentifiableClientRecord createHeaderClientRecord(HEADERRECORD headerRecord) {
				DtoIdentifiableClientRecord clientRecord = new DtoIdentifiableClientRecord();
				clientRecord.setValues(headerPropertyProvider.getValues(headerRecord, groupHeaderTemplate.getPropertyNames()));
				return clientRecord;
			}

			@Override
			public void handleAddItem(DtoIdentifiableClientRecord itemClientRecord, Consumer<Void> uiCommandCallback) {
				boolean sent = clientObjectChannel.addItem(group.getClientId(), itemClientRecord, wrapper -> uiCommandCallback.accept(null));
				if (!sent) {
					uiCommandCallback.accept(null);
				}
			}

			@Override
			public void handleRemoveItem(int itemClientRecordId, Consumer<Void> uiCommandCallback) {
				boolean sent = clientObjectChannel.removeItem(group.getClientId(), itemClientRecordId, wrapper -> uiCommandCallback.accept(null));
				if (!sent) {
					uiCommandCallback.accept(null);
				}
			}

			@Override
			public void handleRefreshRequired() {
				clientObjectChannel.refreshItemGroup(group.createUiItemViewItemGroup());
			}
		});
		clientObjectChannel.addItemGroup(group.createUiItemViewItemGroup());
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
		clientObjectChannel.setFilter(filter);
	}

	public void removeAllGroups() {
		new ArrayList<>(itemGroups).forEach(this::removeItemGroup);
	}

	public void removeItemGroup(ItemGroup itemGroup) {
		itemGroups.remove(itemGroup);
		clientObjectChannel.removeItemGroup(itemGroup.getClientId());
	}

	public int getHorizontalPadding() {
		return horizontalPadding;
	}

	public void setHorizontalPadding(int horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
		clientObjectChannel.setHorizontalPadding(horizontalPadding);
	}

	public int getVerticalPadding() {
		return verticalPadding;
	}

	public void setVerticalPadding(int verticalPadding) {
		this.verticalPadding = verticalPadding;
		clientObjectChannel.setVerticalPadding(verticalPadding);
	}

	public int getGroupSpacing() {
		return groupSpacing;
	}

	public void setGroupSpacing(int groupSpacing) {
		this.groupSpacing = groupSpacing;
		clientObjectChannel.setGroupSpacing(groupSpacing);
	}

	public ItemViewItemBackgroundMode getItemBackgroundMode() {
		return itemBackgroundMode;
	}

	public void setItemBackgroundMode(ItemViewItemBackgroundMode itemBackgroundMode) {
		this.itemBackgroundMode = itemBackgroundMode;
		clientObjectChannel.setItemBackgroundMode(itemBackgroundMode.toDtoValue());
	}

	public PropertyProvider<HEADERRECORD> getHeaderPropertyProvider() {
		return headerPropertyProvider;
	}

	public void setHeaderPropertyProvider(PropertyProvider<HEADERRECORD> propertyProvider) {
		this.headerPropertyProvider = propertyProvider;
	}

	public void setHeaderPropertyExtractor(PropertyExtractor<HEADERRECORD> propertyExtractor) {
		this.setHeaderPropertyProvider(propertyExtractor);
	}

	@Override
	public DtoComponent createConfig() {
		DtoItemView uiItemView = new DtoItemView();
		mapAbstractConfigProperties(uiItemView);
		uiItemView.setGroupHeaderTemplate(groupHeaderTemplate != null ? groupHeaderTemplate : null);
		uiItemView.setItemGroups(this.itemGroups.stream()
				.map(group -> group.createUiItemViewItemGroup())
				.collect(Collectors.toList()));
		uiItemView.setHorizontalPadding(horizontalPadding);
		uiItemView.setVerticalPadding(verticalPadding);
		uiItemView.setGroupSpacing(groupSpacing);
		uiItemView.setItemBackgroundMode(itemBackgroundMode.toDtoValue());
		uiItemView.setFilter(filter);
		return uiItemView;
	}

	@Override
	public void handleItemClicked(DtoItemView.ItemClickedEventWrapper itemClickedEvent) {
		ItemGroup<HEADERRECORD, RECORD> itemGroup = getItemGroupByClientId(itemClickedEvent.getGroupId());
		if (itemGroup != null) {
			RECORD item = itemGroup.getItemByClientId(itemClickedEvent.getItemId());
			this.onItemClicked.fire(new ItemClickedEventData<>(itemGroup, item));
		}
	}

	private ItemGroup<HEADERRECORD, RECORD> getItemGroupByClientId(String clientId) {
		return itemGroups.stream()
				.filter(group -> Objects.equals(group.getClientId(), clientId))
				.findFirst().orElse(null);
	}

}
