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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyProvider;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiIdentifiableClientRecord;
import org.teamapps.dto.UiInfiniteItemView2;
import org.teamapps.event.ProjectorEvent;
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

	public final ProjectorEvent<ItemClickedEventData<RECORD>> onItemClicked = createProjectorEventBoundToUiEvent(UiInfiniteItemView2.ItemClickedEvent.NAME);

	private Template itemTemplate;
	private float itemWidth;
	private float itemHeight;
	// private float horizontalSpacing; // TODO
	// private float verticalSpacing; // TODO
	private HorizontalElementAlignment itemContentHorizontalAlignment = HorizontalElementAlignment.STRETCH;
	private VerticalElementAlignment itemContentVerticalAlignment = VerticalElementAlignment.STRETCH;
	// private ItemViewRowJustification rowHorizontalAlignment = ItemViewRowJustification.LEFT; // TODO

	private int itemPositionAnimationTime = 200;

	private PropertyProvider<RECORD> itemPropertyProvider = new BeanPropertyExtractor<>();

	private int clientRecordIdCounter = 0;

	private Function<RECORD, Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

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
	public UiComponent createUiClientObject() {
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
		return ui;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		if (event instanceof UiInfiniteItemView2.DisplayedRangeChangedEvent) {
			UiInfiniteItemView2.DisplayedRangeChangedEvent d = (UiInfiniteItemView2.DisplayedRangeChangedEvent) event;
			try {
				handleScrollOrResize(ItemRange.startLength(d.getStartIndex(), d.getLength()));
			} catch (DuplicateEntriesException e) {
				// if the model returned a duplicate entry while scrolling, the underlying data apparently changed.
				// So try to refresh the whole data instead.
				LOGGER.warn("DuplicateEntriesException while retrieving data from model. This means the underlying data of the model has changed without the model notifying this component, so will refresh the whole data of this component.");
				refresh();
			}
		} else if (event instanceof UiInfiniteItemView2.ItemClickedEvent) {
			UiInfiniteItemView2.ItemClickedEvent e = (UiInfiniteItemView2.ItemClickedEvent) event;
			RECORD record = renderedRecords.getRecord(e.getRecordId());
			if (record != null) {
				onItemClicked.fire(new ItemClickedEventData<>(record, e.getIsDoubleClick()));
			}
		} else if (event instanceof UiInfiniteItemView2.ContextMenuRequestedEvent) {
			UiInfiniteItemView2.ContextMenuRequestedEvent e = (UiInfiniteItemView2.ContextMenuRequestedEvent) event;
			lastSeenContextMenuRequestId = e.getRequestId();
			if (contextMenuProvider == null) {
				closeContextMenu();
			} else {
				RECORD record = renderedRecords.getRecord(e.getRecordId());
				if (record != null) {
					Component contextMenuContent = contextMenuProvider.apply(record);
					if (contextMenuContent != null) {
						sendCommandIfRendered(() -> new UiInfiniteItemView2.SetContextMenuContentCommand(e.getRequestId(), contextMenuContent.createUiReference()));
					} else {
						sendCommandIfRendered(() -> new UiInfiniteItemView2.CloseContextMenuCommand(e.getRequestId()));
					}
				}
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
		sendCommandIfRendered(() -> {
			LOGGER.debug("SENDING: renderedRange.start: {}; uiRecordIds.size: {}; renderedRecords.size: {}; totalCount: {}",
					start, uiRecordIds.size(), renderedRecords.size(), totalNumberOfRecords);
			return new UiInfiniteItemView2.SetDataCommand(start,
					uiRecordIds,
					newUiRecords,
					totalNumberOfRecords
			);
		});
	}

	@Override
	protected UiIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		UiIdentifiableClientRecord clientRecord = new UiIdentifiableClientRecord();
		clientRecord.setId(++clientRecordIdCounter);
		clientRecord.setValues(itemPropertyProvider.getValues(record, itemTemplate.getPropertyNames()));
		return clientRecord;
	}

	public Function<RECORD, Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Function<RECORD, Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		sendCommandIfRendered(() -> new UiInfiniteItemView2.CloseContextMenuCommand(this.lastSeenContextMenuRequestId));
	}

	public Template getItemTemplate() {
		return itemTemplate;
	}

	public InfiniteItemView2<RECORD> setItemTemplate(Template itemTemplate) {
		this.itemTemplate = itemTemplate;
		sendCommandIfRendered(() -> new UiInfiniteItemView2.SetItemTemplateCommand(itemTemplate.createUiTemplate()));
		return this;
	}

	public float getItemWidth() {
		return itemWidth;
	}

	public InfiniteItemView2<RECORD> setItemWidth(float itemWidth) {
		this.itemWidth = itemWidth;
		sendCommandIfRendered(() -> new UiInfiniteItemView2.SetItemWidthCommand(itemWidth));
		return this;
	}

	public float getItemHeight() {
		return itemHeight;
	}

	public InfiniteItemView2<RECORD> setItemHeight(float itemHeight) {
		this.itemHeight = itemHeight;
		sendCommandIfRendered(() -> new UiInfiniteItemView2.SetItemHeightCommand(itemHeight));
		return this;
	}

	// public float getHorizontalSpacing() {
	// 	return horizontalSpacing;
	// }
	//
	// public InfiniteItemView2<RECORD> setHorizontalSpacing(float horizontalSpacing) {
	// 	this.horizontalSpacing = horizontalSpacing;
	// 	queueCommandIfRendered(() -> new UiInfiniteItemView2.SetHorizontalSpacingCommand(horizontalSpacing));
	// 	return this;
	// }
	//
	// public float getVerticalSpacing() {
	// 	return verticalSpacing;
	// }
	//
	// public InfiniteItemView2<RECORD> setVerticalSpacing(float verticalSpacing) {
	// 	this.verticalSpacing = verticalSpacing;
	// 	queueCommandIfRendered(() -> new UiInfiniteItemView2.SetVerticalSpacingCommand(verticalSpacing));
	// 	return this;
	// }

	public HorizontalElementAlignment getItemContentHorizontalAlignment() {
		return itemContentHorizontalAlignment;
	}

	public InfiniteItemView2<RECORD> setItemContentHorizontalAlignment(HorizontalElementAlignment itemContentHorizontalAlignment) {
		this.itemContentHorizontalAlignment = itemContentHorizontalAlignment;
		sendCommandIfRendered(() -> new UiInfiniteItemView2.SetItemContentHorizontalAlignmentCommand(itemContentHorizontalAlignment.toUiHorizontalElementAlignment()));
		return this;
	}

	public VerticalElementAlignment getItemContentVerticalAlignment() {
		return itemContentVerticalAlignment;
	}

	public InfiniteItemView2<RECORD> setItemContentVerticalAlignment(VerticalElementAlignment itemContentVerticalAlignment) {
		this.itemContentVerticalAlignment = itemContentVerticalAlignment;
		sendCommandIfRendered(() -> new UiInfiniteItemView2.SetItemContentVerticalAlignmentCommand(itemContentVerticalAlignment.toUiVerticalElementAlignment()));
		return this;
	}

	// public ItemViewRowJustification getRowHorizontalAlignment() {
	// 	return rowHorizontalAlignment;
	// }

	// public InfiniteItemView2<RECORD> setRowHorizontalAlignment(ItemViewRowJustification rowHorizontalAlignment) {
	// 	this.rowHorizontalAlignment = rowHorizontalAlignment;
	// 	queueCommandIfRendered(() -> new UiInfiniteItemView2.SetRowHorizontalAlignmentCommand(rowHorizontalAlignment.toUiItemJustification()));
	// 	return this;
	// }

	public int getItemPositionAnimationTime() {
		return itemPositionAnimationTime;
	}

	public void setItemPositionAnimationTime(int itemPositionAnimationTime) {
		this.itemPositionAnimationTime = itemPositionAnimationTime;
		sendCommandIfRendered(() -> new UiInfiniteItemView2.SetItemPositionAnimationTimeCommand(itemPositionAnimationTime));
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

}
