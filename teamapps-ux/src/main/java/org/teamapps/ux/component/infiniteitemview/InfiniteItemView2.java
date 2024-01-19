/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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
import org.teamapps.dto.*;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.record.DuplicateEntriesException;
import org.teamapps.ux.cache.record.ItemRange;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.Template;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class InfiniteItemView2<RECORD> extends AbstractInfiniteListComponent<RECORD, InfiniteItemViewModel<RECORD>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final Event<ItemClickedEventData<RECORD>> onItemClicked = new Event<>();
	public final Event<RECORD> onItemSelected = new Event<>();

	private Template itemTemplate;
	private float itemWidth;
	private float itemHeight;
	private HorizontalElementAlignment itemContentHorizontalAlignment = HorizontalElementAlignment.STRETCH;
	private VerticalElementAlignment itemContentVerticalAlignment = VerticalElementAlignment.STRETCH;

	private int itemPositionAnimationTime = 200;

	private PropertyProvider<RECORD> itemPropertyProvider = new BeanPropertyExtractor<>();

	private int clientRecordIdCounter = 0;

	private Function<RECORD, Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	private boolean selectionEnabled;
	private RECORD selectedRecord;

	public InfiniteItemView2(Template itemTemplate, float itemWidth, int itemHeight) {
		super(new ListInfiniteItemViewModel<>());
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
		ui.setItemPositionAnimationTime(itemPositionAnimationTime);
		ui.setContextMenuEnabled(contextMenuProvider != null);
		ui.setSelectionEnabled(selectionEnabled);
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_INFINITE_ITEM_VIEW2_DISPLAYED_RANGE_CHANGED:
				UiInfiniteItemView2.DisplayedRangeChangedEvent d = (UiInfiniteItemView2.DisplayedRangeChangedEvent) event;
				try {
					handleScrollOrResize(ItemRange.startLength(d.getStartIndex(), d.getLength()));
				} catch (DuplicateEntriesException e) {
					// if the model returned a duplicate entry while scrolling, the underlying data apparently changed.
					// So try to refresh the whole data instead.
					LOGGER.warn("DuplicateEntriesException while retrieving data from model. This means the underlying data of the model has changed without the model notifying this component, so will refresh the whole data of this component.");
					refresh();
				}
				break;
			case UI_INFINITE_ITEM_VIEW2_ITEM_CLICKED: {
				UiInfiniteItemView2.ItemClickedEvent e = (UiInfiniteItemView2.ItemClickedEvent) event;
				RECORD record = renderedRecords.getRecord(e.getRecordId());
				if (record != null) {
					onItemClicked.fire(new ItemClickedEventData<>(record, e.getIsDoubleClick()));
				}
				if (this.selectionEnabled) {
					this.selectedRecord = record;
					this.onItemSelected.fire(record);
				}
				break;
			}
			case UI_INFINITE_ITEM_VIEW2_CONTEXT_MENU_REQUESTED: {
				UiInfiniteItemView2.ContextMenuRequestedEvent e = (UiInfiniteItemView2.ContextMenuRequestedEvent) event;
				lastSeenContextMenuRequestId = e.getRequestId();
				if (contextMenuProvider == null) {
					closeContextMenu();
				} else {
					RECORD record = renderedRecords.getRecord(e.getRecordId());
					if (record != null) {
						Component contextMenuContent = contextMenuProvider.apply(record);
						if (contextMenuContent != null) {
							queueCommandIfRendered(() -> new UiInfiniteItemView2.SetContextMenuContentCommand(getId(), e.getRequestId(), contextMenuContent.createUiReference()));
						} else {
							queueCommandIfRendered(() -> new UiInfiniteItemView2.CloseContextMenuCommand(getId(), e.getRequestId()));
						}
					}
				}
				break;
			}
		}
	}

	@Override
	protected List<RECORD> retrieveRecords(int startIndex, int length) {
		if (startIndex > getModelCount() || length <= 0) {
			return Collections.emptyList();
		}
		int actualStartIndex = Math.max(startIndex, 0);
		int actualLength = Math.min(getModelCount() - startIndex, length);
		return getModel().getRecords(actualStartIndex, actualLength);
	}

	@Override
	protected void sendUpdateDataCommandToClient(int start, List<Integer> uiRecordIds, List<UiIdentifiableClientRecord> newUiRecords, int totalNumberOfRecords) {
		queueCommandIfRendered(() -> {
			LOGGER.debug("SENDING: renderedRange.start: {}; uiRecordIds.size: {}; renderedRecords.size: {}; totalCount: {}",
					start, uiRecordIds.size(), renderedRecords.size(), totalNumberOfRecords);
			return new UiInfiniteItemView2.SetDataCommand(
					getId(),
					start,
					uiRecordIds,
					(List) newUiRecords,
					totalNumberOfRecords
			);
		});
	}

	@Override
	protected UiInfiniteItemViewClientRecord createClientRecord(RECORD record) {
		UiInfiniteItemViewClientRecord clientRecord = new UiInfiniteItemViewClientRecord();
		clientRecord.setId(++clientRecordIdCounter);
		clientRecord.setValues(itemPropertyProvider.getValues(record, itemTemplate.getPropertyNames()));
		clientRecord.setSelected(record.equals(selectedRecord));
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

	public int getItemPositionAnimationTime() {
		return itemPositionAnimationTime;
	}

	public void setItemPositionAnimationTime(int itemPositionAnimationTime) {
		this.itemPositionAnimationTime = itemPositionAnimationTime;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetItemPositionAnimationTimeCommand(getId(), itemPositionAnimationTime));
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

	public boolean isSelectionEnabled() {
		return selectionEnabled;
	}

	public void setSelectionEnabled(boolean selectionEnabled) {
		this.selectionEnabled = selectionEnabled;
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetSelectionEnabledCommand(getId(), selectionEnabled));
	}

	public RECORD getSelectedRecord() {
		return selectedRecord;
	}

	public void setSelectedRecord(RECORD record) {
		this.selectedRecord = record;
		UiIdentifiableClientRecord uiRecord = renderedRecords.getUiRecord(record);
		queueCommandIfRendered(() -> new UiInfiniteItemView2.SetSelectedRecordCommand(getId(), uiRecord != null ? uiRecord.getId(): null));
	}
}
