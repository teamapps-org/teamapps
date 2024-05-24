package org.teamapps.projector.components.trivial.itemview;/*-
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

import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.DtoIdentifiableClientRecord;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.components.trivial.dto.DtoItemView;
import org.teamapps.projector.clientobject.component.AbstractComponent;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemView<HEADERRECORD, RECORD> extends AbstractComponent {

	public ProjectorEvent<ItemClickedEventData<RECORD>> onItemClicked = createProjectorEventBoundToUiEvent(DtoItemView.ItemClickedEvent.TYPE_ID);

	private final List<ItemGroup<HEADERRECORD, RECORD>> itemGroups = new ArrayList<>();

	private int horizontalPadding = 10;
	private int verticalPadding = 0;
	private int groupSpacing;
	private ItemViewItemBackgroundMode itemBackgroundMode = ItemViewItemBackgroundMode.LIGHT;

	private String filter;

	private Template groupHeaderTemplate = BaseTemplate.LIST_ITEM_SMALL_ICON_SINGLE_LINE;
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
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.SetGroupHeaderTemplateCommand(groupHeaderTemplate.createClientReference()), null);
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
				if (isRendered()) {
					final DtoItemView.AddItemCommand addItemCommand = new DtoItemView.AddItemCommand(group.getClientId(), itemClientRecord);
					getSessionContext().sendCommandIfRendered(ItemView.this, () -> addItemCommand.get(), uiCommandCallback);
				} else {
					uiCommandCallback.accept(null);
				}
			}

			@Override
			public void handleRemoveItem(int itemClientRecordId, Consumer<Void> uiCommandCallback) {
				if (isRendered()) {
					final DtoItemView.RemoveItemCommand removeItemCommand = new DtoItemView.RemoveItemCommand(group.getClientId(), itemClientRecordId);
					getSessionContext().sendCommandIfRendered(ItemView.this, () -> removeItemCommand.get(), uiCommandCallback);
				}
			}

			@Override
			public void handleRefreshRequired() {
				getClientObjectChannel().sendCommandIfRendered(new DtoItemView.RefreshItemGroupCommand(group.createUiItemViewItemGroup()), null);
			}
		});
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.AddItemGroupCommand(group.createUiItemViewItemGroup()), null);
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.SetFilterCommand(filter), null);
	}

	public void removeAllGroups() {
		new ArrayList<>(itemGroups).forEach(this::removeItemGroup);
	}

	public void removeItemGroup(ItemGroup itemGroup) {
		itemGroups.remove(itemGroup);
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.RemoveItemGroupCommand(itemGroup.getClientId()), null);
	}

	public int getHorizontalPadding() {
		return horizontalPadding;
	}

	public void setHorizontalPadding(int horizontalPadding) {
		this.horizontalPadding = horizontalPadding;
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.SetHorizontalPaddingCommand(horizontalPadding), null);
	}

	public int getVerticalPadding() {
		return verticalPadding;
	}

	public void setVerticalPadding(int verticalPadding) {
		this.verticalPadding = verticalPadding;
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.SetVerticalPaddingCommand(verticalPadding), null);
	}

	public int getGroupSpacing() {
		return groupSpacing;
	}

	public void setGroupSpacing(int groupSpacing) {
		this.groupSpacing = groupSpacing;
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.SetGroupSpacingCommand(groupSpacing), null);
	}

	public ItemViewItemBackgroundMode getItemBackgroundMode() {
		return itemBackgroundMode;
	}

	public void setItemBackgroundMode(ItemViewItemBackgroundMode itemBackgroundMode) {
		this.itemBackgroundMode = itemBackgroundMode;
		getClientObjectChannel().sendCommandIfRendered(new DtoItemView.SetItemBackgroundModeCommand(itemBackgroundMode.toDtoValue()), null);
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
		mapAbstractUiComponentProperties(uiItemView);
		uiItemView.setGroupHeaderTemplate(groupHeaderTemplate != null ? groupHeaderTemplate.createClientReference() : null);
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
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoItemView.ItemClickedEvent.TYPE_ID -> {
				var itemClickedEvent = event.as(DtoItemView.ItemClickedEventWrapper.class);
				ItemGroup<HEADERRECORD, RECORD> itemGroup = getItemGroupByClientId(itemClickedEvent.getGroupId());
				if (itemGroup != null) {
					RECORD item = itemGroup.getItemByClientId(itemClickedEvent.getItemId());
					this.onItemClicked.fire(new ItemClickedEventData<>(itemGroup, item));
				}
			}
		}
	}

	private ItemGroup<HEADERRECORD, RECORD> getItemGroupByClientId(String clientId) {
		return itemGroups.stream()
				.filter(group -> Objects.equals(group.getClientId(), clientId))
				.findFirst().orElse(null);
	}

}
