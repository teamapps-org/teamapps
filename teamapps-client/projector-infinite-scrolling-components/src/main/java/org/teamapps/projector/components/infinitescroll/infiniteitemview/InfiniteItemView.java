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
package org.teamapps.projector.components.infinitescroll.infiniteitemview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.projector.dto.DtoComponent;
import org.teamapps.projector.dto.DtoIdentifiableClientRecord;
import org.teamapps.projector.dto.JsonWrapper;
import org.teamapps.projector.clientobject.component.Component;
import org.teamapps.projector.event.ProjectorEvent;
import org.teamapps.projector.components.infinitescroll.dto.DtoInfiniteItemView;
import org.teamapps.projector.components.infinitescroll.table.InfiniteScrollingComponentLibrary;
import org.teamapps.ux.cache.record.DuplicateEntriesException;
import org.teamapps.ux.cache.record.ItemRange;
import org.teamapps.projector.annotation.ClientObjectLibrary;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.projector.template.Template;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyExtractor;
import org.teamapps.projector.dataextraction.PropertyProvider;
import org.teamapps.projector.format.HorizontalElementAlignment;
import org.teamapps.projector.format.VerticalElementAlignment;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ClientObjectLibrary(value = InfiniteScrollingComponentLibrary.class)
public class InfiniteItemView<RECORD> extends AbstractInfiniteListComponent<RECORD, InfiniteItemViewModel<RECORD>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final ProjectorEvent<ItemClickedEventData<RECORD>> onItemClicked = new ProjectorEvent<>(clientObjectChannel::toggleItemClickedEvent);

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

	public InfiniteItemView(Template itemTemplate, float itemWidth, int itemHeight) {
		super(new ListInfiniteItemViewModel<>());
		this.itemTemplate = itemTemplate;
		this.itemWidth = itemWidth;
		this.itemHeight = itemHeight;
	}

	public InfiniteItemView(float itemWidth, int itemHeight) {
		this(BaseTemplates.ITEM_VIEW_ITEM, itemWidth, itemHeight);
	}

	public InfiniteItemView() {
		this(BaseTemplates.ITEM_VIEW_ITEM, 300, 300);
	}

	@Override
	public DtoComponent createConfig() {
		DtoInfiniteItemView ui = new DtoInfiniteItemView(itemTemplate != null ? itemTemplate : null);
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
	public void handleUiEvent(String name, JsonWrapper params) {
		switch (event.getTypeId()) {
			case DtoInfiniteItemView.DisplayedRangeChangedEvent.TYPE_ID -> {
				var d = event.as(DtoInfiniteItemView.DisplayedRangeChangedEventWrapper.class);
				try {
					handleScrollOrResize(ItemRange.startLength(d.getStartIndex(), d.getLength()));
				} catch (DuplicateEntriesException e) {
					// if the model returned a duplicate entry while scrolling, the underlying data apparently changed.
					// So try to refresh the whole data instead.
					LOGGER.warn("DuplicateEntriesException while retrieving data from model. This means the underlying data of the model has changed without the model notifying this component, so will refresh the whole data of this component.");
					refresh();
				}
			}
			case DtoInfiniteItemView.ItemClickedEvent.TYPE_ID -> {
				var e = event.as(DtoInfiniteItemView.ItemClickedEventWrapper.class);
				RECORD record = renderedRecords.getRecord(e.getRecordId());
				if (record != null) {
					onItemClicked.fire(new ItemClickedEventData<>(record, e.isDoubleClick()));
				}
			}
			case DtoInfiniteItemView.ContextMenuRequestedEvent.TYPE_ID -> {
				var e = event.as(DtoInfiniteItemView.ContextMenuRequestedEventWrapper.class);
				lastSeenContextMenuRequestId = e.getRequestId();
				if (contextMenuProvider == null) {
					closeContextMenu();
				} else {
					RECORD record = renderedRecords.getRecord(e.getRecordId());
					if (record != null) {
						Component contextMenuContent = contextMenuProvider.apply(record);
						if (contextMenuContent != null) {
							clientObjectChannel.setContextMenuContent(e.getRequestId(), contextMenuContent);
						} else {
							clientObjectChannel.closeContextMenu(e.getRequestId());
						}
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
	protected void sendUpdateDataCommandToClient(int start, List<Integer> uiRecordIds, List<DtoIdentifiableClientRecord> newUiRecords, int totalNumberOfRecords) {
		getClientObjectChannel().sendCommandIfRendered(((Supplier<DtoCommand<?>>) () -> {
			LOGGER.debug("SENDING: renderedRange.start: {}; uiRecordIds.size: {}; renderedRecords.size: {}; totalCount: {}",
					start, uiRecordIds.size(), renderedRecords.size(), totalNumberOfRecords);
			return new DtoInfiniteItemView.SetDataCommand(start,
					uiRecordIds,
					newUiRecords,
					totalNumberOfRecords
			);
		}).get(), null);
	}

	@Override
	protected DtoIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		DtoIdentifiableClientRecord clientRecord = new DtoIdentifiableClientRecord();
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
		clientObjectChannel.closeContextMenu(this.lastSeenContextMenuRequestId);
	}

	public Template getItemTemplate() {
		return itemTemplate;
	}

	public InfiniteItemView<RECORD> setItemTemplate(Template itemTemplate) {
		this.itemTemplate = itemTemplate;
		clientObjectChannel.setItemTemplate(itemTemplate != null ? itemTemplate : null);
		return this;
	}

	public float getItemWidth() {
		return itemWidth;
	}

	public InfiniteItemView<RECORD> setItemWidth(float itemWidth) {
		this.itemWidth = itemWidth;
		clientObjectChannel.setItemWidth(itemWidth);
		return this;
	}

	public float getItemHeight() {
		return itemHeight;
	}

	public InfiniteItemView<RECORD> setItemHeight(float itemHeight) {
		this.itemHeight = itemHeight;
		clientObjectChannel.setItemHeight(itemHeight);
		return this;
	}

	// public float getHorizontalSpacing() {
	// 	return horizontalSpacing;
	// }
	//
	// public InfiniteItemView2<RECORD> setHorizontalSpacing(float horizontalSpacing) {
	// 	this.horizontalSpacing = horizontalSpacing;
	// 	queueCommandIfRendered(() -> new DtoInfiniteItemView.SetHorizontalSpacingCommand(horizontalSpacing));
	// 	return this;
	// }
	//
	// public float getVerticalSpacing() {
	// 	return verticalSpacing;
	// }
	//
	// public InfiniteItemView2<RECORD> setVerticalSpacing(float verticalSpacing) {
	// 	this.verticalSpacing = verticalSpacing;
	// 	queueCommandIfRendered(() -> new DtoInfiniteItemView.SetVerticalSpacingCommand(verticalSpacing));
	// 	return this;
	// }

	public HorizontalElementAlignment getItemContentHorizontalAlignment() {
		return itemContentHorizontalAlignment;
	}

	public InfiniteItemView<RECORD> setItemContentHorizontalAlignment(HorizontalElementAlignment itemContentHorizontalAlignment) {
		this.itemContentHorizontalAlignment = itemContentHorizontalAlignment;
		clientObjectChannel.setItemContentHorizontalAlignment(itemContentHorizontalAlignment.toUiHorizontalElementAlignment());
		return this;
	}

	public VerticalElementAlignment getItemContentVerticalAlignment() {
		return itemContentVerticalAlignment;
	}

	public InfiniteItemView<RECORD> setItemContentVerticalAlignment(VerticalElementAlignment itemContentVerticalAlignment) {
		this.itemContentVerticalAlignment = itemContentVerticalAlignment;
		clientObjectChannel.setItemContentVerticalAlignment(itemContentVerticalAlignment.toUiVerticalElementAlignment());
		return this;
	}

	// public ItemViewRowJustification getRowHorizontalAlignment() {
	// 	return rowHorizontalAlignment;
	// }

	// public InfiniteItemView2<RECORD> setRowHorizontalAlignment(ItemViewRowJustification rowHorizontalAlignment) {
	// 	this.rowHorizontalAlignment = rowHorizontalAlignment;
	// 	queueCommandIfRendered(() -> new DtoInfiniteItemView.SetRowHorizontalAlignmentCommand(rowHorizontalAlignment.toUiItemJustification()));
	// 	return this;
	// }

	public int getItemPositionAnimationTime() {
		return itemPositionAnimationTime;
	}

	public void setItemPositionAnimationTime(int itemPositionAnimationTime) {
		this.itemPositionAnimationTime = itemPositionAnimationTime;
		clientObjectChannel.setItemPositionAnimationTime(itemPositionAnimationTime);
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
