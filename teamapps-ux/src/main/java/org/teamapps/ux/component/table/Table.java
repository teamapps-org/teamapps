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
package org.teamapps.ux.component.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.data.extract.BeanPropertyExtractor;
import org.teamapps.data.extract.BeanPropertyInjector;
import org.teamapps.data.extract.PropertyExtractor;
import org.teamapps.data.extract.PropertyInjector;
import org.teamapps.data.value.SortDirection;
import org.teamapps.data.value.Sorting;
import org.teamapps.dto.UiComponent;
import org.teamapps.dto.UiEvent;
import org.teamapps.dto.UiFieldMessage;
import org.teamapps.dto.UiInfiniteItemView;
import org.teamapps.dto.UiTable;
import org.teamapps.dto.UiTableClientRecord;
import org.teamapps.dto.UiTableColumn;
import org.teamapps.dto.UiTableDataRequest;
import org.teamapps.event.Event;
import org.teamapps.ux.cache.CacheManipulationHandle;
import org.teamapps.ux.cache.ClientRecordCache;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.Container;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Table<RECORD> extends AbstractComponent implements Container {

	private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

	public final Event<CellEditingStartedEvent<RECORD, Object>> onCellEditingStarted = new Event<>();
	public final Event<CellEditingStoppedEvent<RECORD>> onCellEditingStopped = new Event<>();
	public final Event<FieldValueChangedEventData<RECORD, Object>> onCellValueChanged = new Event<>();
	public final Event<RECORD> onRowSelected = new Event<>();
	public final Event<List<RECORD>> onMultipleRowsSelected = new Event<>();
	public final Event<SortingChangedEventData> onSortingChanged = new Event<>();
	public final Event<TableDataRequestEventData> onTableDataRequest = new Event<>();
	public final Event<FieldOrderChangeEventData<RECORD>> onFieldOrderChange = new Event<>();
	public final Event<ColumnSizeChangeEventData<RECORD>> onColumnSizeChange = new Event<>();

	private TableModel<RECORD> model = new ListTableModel<>(Collections.emptyList());
	private PropertyExtractor<RECORD> propertyExtractor = new BeanPropertyExtractor<>();
	private PropertyInjector<RECORD> propertyInjector = new BeanPropertyInjector<>();
	private final ClientRecordCache<RECORD, UiTableClientRecord> clientRecordCache;

	private int pageSize = 50;

	private RECORD selectedRecord;
	private final List<RECORD> selectedRecords = new ArrayList<>();
	private TableCellCoordinates<RECORD> activeEditorCell;

	private Map<RECORD, Map<String, Object>> transientChangesByRecordAndPropertyName = new HashMap<>();
	private Map<RECORD, Map<String, List<FieldMessage>>> cellMessages = new HashMap<>();
	private Map<RECORD, Set<String>> markedCells = new HashMap<>();

	private List<TableColumn<RECORD>> columns = new ArrayList<>();

	private boolean displayAsList; // list has no cell borders, table has. selection policy: list = row selection, table = cell selection
	private boolean forceFitWidth; //if true, force the widths of all columns to fit into the available space of the list
	private int rowHeight = 28;
	private boolean stripedRows = true;
	private boolean hideHeaders; //if true, do not show any headers
	private boolean allowMultiRowSelection = false;
	private boolean showRowCheckBoxes; //if true, show check boxes on the left
	private boolean showNumbering; //if true, show numbering on the left

	private String sortField; //if available the table is initially sorted by this field
	private SortDirection sortDirection = SortDirection.ASC;

	private boolean editable; //only valid for tables
	private boolean ensureEmptyLastRow; //if true, there is always an empty last row, as soon as any data is inserted into the empty row a new empty row is inserted

	private boolean treeMode; //if true, use the parent id property of UiDataRecord to display the table as tree
	private String indentedColumnName; // if set, indent this column depending on the depth in the data hierarchy
	private int indentation = 15; // in pixels

	private SelectionFrame selectionFrame;

	// ----- header -----

	private boolean showHeaderRow = false;
	private int headerRowHeight = 28;
	private Map<String, AbstractField> headerRowFields = new HashMap<>(0);

	// ----- footer -----

	private boolean showFooterRow = false;
	private int footerRowHeight = 28;
	private Map<String, AbstractField> footerRowFields = new HashMap<>(0);

	// ----- listeners -----

	// needs to be a field for reference equality (sad but true, java method references are only syntactic sugar for lambdas)
	private Consumer<Void> onAllDataChangedListener = this::onAllDataChanged;
	private Consumer<RECORD> onRecordAddedListener = this::onRecordAdded;
	private Consumer<RECORD> onRecordDeletedListener = this::onRecordDeleted;
	private Consumer<RECORD> onRecordUpdatedListener = this::onRecordUpdated;

	private List<Integer> viewportDisplayedRecordClientIds = Collections.emptyList();

	private List<RECORD> topNonModelRecords = new ArrayList<>();
	private List<RECORD> bottomNonModelRecords = new ArrayList<>();

	private Function<RECORD, Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	public Table() {
		this(new ArrayList<>());
	}

	public Table(List<TableColumn<RECORD>> columns) {
		super();
		columns.forEach(this::addColumn);

		clientRecordCache = new ClientRecordCache<>(this::createUiTableClientRecord);
		clientRecordCache.setMaxCapacity(200);
		clientRecordCache.setPurgeDecider((record, clientId) -> !viewportDisplayedRecordClientIds.contains(clientId));
		clientRecordCache.setPurgeListener(purgedRecordIds -> {
			if (isRendered()) {
				getSessionContext().queueCommand(new UiTable.RemoveDataCommand(getId(), purgedRecordIds.getResult()), aVoid -> purgedRecordIds.commit());
			} else {
				purgedRecordIds.commit();
			}
		});
	}

	public static <RECORD> Table<RECORD> create() {
		return new Table<>();
	}

	public void addColumn(TableColumn<RECORD> column) {
		addColumn(column, columns.size());
	}

	public void addColumn(TableColumn<RECORD> column, int index) {
		addColumns(Collections.singletonList(column), index);
	}

	public void addColumns(List<TableColumn<RECORD>> newColumns, int index) {
		this.columns.addAll(index, newColumns);
		newColumns.forEach(column -> {
			column.setTable(this);
			AbstractField<?> field = column.getField();
			field.setParent(this);
		});
		if (isRendered()) {
			getSessionContext().queueCommand(
					new UiTable.AddColumnsCommand(getId(), newColumns.stream()
							.map(c -> c.createUiTableColumn())
							.collect(Collectors.toList()), index),
					aVoid -> this.clientRecordCache.clear().commit()
			);
		}
	}

	public void removeColumn(String propertyName) {
		columns.stream()
				.filter(c -> Objects.equals(c.getPropertyName(), propertyName))
				.findFirst()
				.ifPresent(this::removeColumn);
	}

	public void removeColumn(TableColumn<RECORD> column) {
		this.removeColumns(Collections.singletonList(column));
	}

	public void removeColumns(List<TableColumn<RECORD>> obsoleteColumns) {
		this.columns.removeAll(obsoleteColumns);
		if (isRendered()) {
			getSessionContext().queueCommand(
					new UiTable.RemoveColumnsCommand(getId(), obsoleteColumns.stream()
							.map(c -> c.getPropertyName())
							.collect(Collectors.toList())),
					aVoid -> this.clientRecordCache.clear().commit()
			);
		}
	}

	@Override
	public UiComponent createUiComponent() {
		List<UiTableColumn> columns = this.columns.stream()
				.map(tableColumn -> tableColumn.createUiTableColumn())
				.collect(Collectors.toList());
		UiTable uiTable = new UiTable(columns);
		mapAbstractUiComponentProperties(uiTable);
		uiTable.setSelectionFrame(selectionFrame != null ? selectionFrame.createUiSelectionFrame() : null);
		uiTable.setDisplayAsList(displayAsList);
		uiTable.setForceFitWidth(forceFitWidth);
		uiTable.setRowHeight(rowHeight);
		uiTable.setStripedRows(stripedRows);
		uiTable.setHideHeaders(hideHeaders);
		uiTable.setAllowMultiRowSelection(allowMultiRowSelection);
		uiTable.setShowRowCheckBoxes(showRowCheckBoxes);
		uiTable.setShowNumbering(showNumbering);

		List<RECORD> records = retrieveRecords(0, pageSize);
		CacheManipulationHandle<List<UiTableClientRecord>> cacheResponse = clientRecordCache.replaceRecords(records);
		cacheResponse.commit();
		uiTable.setTableData(cacheResponse.getResult());

		uiTable.setTotalNumberOfRecords(getTotalRecordsCount());
		uiTable.setSortField(sortField);
		uiTable.setSortDirection(sortDirection.toUiSortDirection());
		uiTable.setEditable(editable);
		uiTable.setTreeMode(treeMode);
		uiTable.setIndentedColumnName(indentedColumnName);
		uiTable.setIndentation(indentation);
		uiTable.setShowHeaderRow(showHeaderRow);
		uiTable.setHeaderRowHeight(headerRowHeight);
		uiTable.setHeaderRowFields(this.headerRowFields.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().createUiReference())));
		uiTable.setShowFooterRow(showFooterRow);
		uiTable.setFooterRowHeight(footerRowHeight);
		uiTable.setFooterRowFields(this.footerRowFields.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().createUiReference())));
		uiTable.setContextMenuEnabled(contextMenuProvider != null);
		return uiTable;
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		switch (event.getUiEventType()) {
			case UI_TABLE_ROW_SELECTED:
				UiTable.RowSelectedEvent rowSelectedEvent = (UiTable.RowSelectedEvent) event;
				selectedRecord = clientRecordCache.getRecordByClientId(rowSelectedEvent.getRecordId());
				selectedRecords.clear();
				this.onRowSelected.fire(selectedRecord);
				break;
			case UI_TABLE_CELL_EDITING_STARTED: {
				UiTable.CellEditingStartedEvent editingStartedEvent = (UiTable.CellEditingStartedEvent) event;
				RECORD record = clientRecordCache.getRecordByClientId(editingStartedEvent.getRecordId());
				this.activeEditorCell = new TableCellCoordinates<>(record, editingStartedEvent.getColumnPropertyName());
				this.selectedRecord = activeEditorCell.getRecord();
				TableColumn<RECORD> column = getColumnByPropertyName(editingStartedEvent.getColumnPropertyName());
				Object cellValue = getCellValue(record, column);
				AbstractField activeEditorField = getActiveEditorField();
				activeEditorField.setValue(cellValue);
				List<FieldMessage> cellMessages = getCellMessages(record, editingStartedEvent.getColumnPropertyName());
				List<FieldMessage> columnMessages = getColumnByPropertyName(editingStartedEvent.getColumnPropertyName()).getMessages();
				if (columnMessages == null) {
					columnMessages = Collections.emptyList();
				}
				List<FieldMessage> messages = new ArrayList<>(cellMessages);
				messages.addAll(columnMessages);
				activeEditorField.setCustomFieldMessages(messages);
				this.onCellEditingStarted.fire(new CellEditingStartedEvent<>(clientRecordCache.getRecordByClientId(editingStartedEvent.getRecordId()), column, cellValue));
				break;
			}
			case UI_TABLE_CELL_EDITING_STOPPED: {
				this.activeEditorCell = null;
				UiTable.CellEditingStoppedEvent editingStoppedEvent = (UiTable.CellEditingStoppedEvent) event;
				TableColumn<RECORD> column = getColumnByPropertyName(editingStoppedEvent.getColumnPropertyName());
				this.onCellEditingStopped.fire(new CellEditingStoppedEvent<>(clientRecordCache.getRecordByClientId(editingStoppedEvent.getRecordId()), column));
				break;
			}
			case UI_TABLE_CELL_VALUE_CHANGED: {
				UiTable.CellValueChangedEvent changeEvent = (UiTable.CellValueChangedEvent) event;
				TableColumn<RECORD> column = this.getColumnByPropertyName(changeEvent.getColumnPropertyName());
				RECORD record = clientRecordCache.getRecordByClientId(changeEvent.getRecordId());
				Object value = column.getField().convertUiValueToUxValue(changeEvent.getValue());
				transientChangesByRecordAndPropertyName
						.computeIfAbsent(record, idValue -> new HashMap<>())
						.put(column.getPropertyName(), value);
				onCellValueChanged.fire(new FieldValueChangedEventData<>(record, column, value));
				break;
			}
			case UI_TABLE_MULTIPLE_ROWS_SELECTED:
				selectedRecord = null;
				UiTable.MultipleRowsSelectedEvent multipleRowsSelectedEvent = (UiTable.MultipleRowsSelectedEvent) event;
				selectedRecords.clear();
				selectedRecords.addAll(multipleRowsSelectedEvent.getRecordIds().stream().map(id -> clientRecordCache.getRecordByClientId(id)).collect(Collectors.toList()));
				this.onMultipleRowsSelected.fire(selectedRecords);
				break;
			case UI_TABLE_SORTING_CHANGED:
				UiTable.SortingChangedEvent sortingChangedEvent = (UiTable.SortingChangedEvent) event;
				sortField = sortingChangedEvent.getSortField();
				sortDirection = SortDirection.fromUiSortDirection(sortingChangedEvent.getSortDirection());
				onSortingChanged.fire(new SortingChangedEventData(sortingChangedEvent.getSortField(), SortDirection.fromUiSortDirection(sortingChangedEvent.getSortDirection())));
				refreshData(false, false, false);
				break;
			case UI_TABLE_DISPLAYED_RANGE_CHANGED:
				UiTable.DisplayedRangeChangedEvent rangeChangedEvent = (UiTable.DisplayedRangeChangedEvent) event;
				viewportDisplayedRecordClientIds = rangeChangedEvent.getDisplayedRecordIds();
				if (rangeChangedEvent.getDataRequest() != null) {
					UiTableDataRequest dataRequest = rangeChangedEvent.getDataRequest();
					SortDirection sortDirection = SortDirection.fromUiSortDirection(dataRequest.getSortDirection());
					int startIndex = dataRequest.getStartIndex();
					int requestedLength = dataRequest.getLength();
					onTableDataRequest.fire(new TableDataRequestEventData(startIndex, requestedLength, dataRequest.getSortField(), sortDirection));
					int pagedStartIndex = startIndex - (startIndex % pageSize);
					int endIndex = startIndex + requestedLength;
					int pagedEndIndex = endIndex % pageSize == 0 ? endIndex : endIndex + pageSize - (endIndex % pageSize);
					this.sendDataToClient(pagedStartIndex, pagedEndIndex, false);
				}
				break;
			case UI_TABLE_REQUEST_NESTED_DATA:
				UiTable.RequestNestedDataEvent nestedDataEvent = (UiTable.RequestNestedDataEvent) event;
				List<RECORD> childRecords = model.getChildRecords(clientRecordCache.getRecordByClientId(nestedDataEvent.getRecordId()), getSorting());
				CacheManipulationHandle<List<UiTableClientRecord>> cacheResponse = clientRecordCache.addRecords(childRecords);
				if (isRendered()) {
					getSessionContext().queueCommand(new UiTable.SetChildrenDataCommand(getId(), nestedDataEvent.getRecordId(), cacheResponse.getResult()), aVoid -> cacheResponse.commit());
				} else {
					cacheResponse.commit();
				}
				break;
			case UI_TABLE_FIELD_ORDER_CHANGE: {
				UiTable.FieldOrderChangeEvent fieldOrderChangeEvent = (UiTable.FieldOrderChangeEvent) event;
				TableColumn<RECORD> column = getColumnByPropertyName(fieldOrderChangeEvent.getColumnPropertyName());
				onFieldOrderChange.fire(new FieldOrderChangeEventData(column, fieldOrderChangeEvent.getPosition()));
				break;
			}
			case UI_TABLE_COLUMN_SIZE_CHANGE: {
				UiTable.ColumnSizeChangeEvent columnSizeChangeEvent = (UiTable.ColumnSizeChangeEvent) event;
				TableColumn<RECORD> column = getColumnByPropertyName(columnSizeChangeEvent.getColumnPropertyName());
				onColumnSizeChange.fire(new ColumnSizeChangeEventData(column, columnSizeChangeEvent.getSize()));
				break;
			}
			case UI_TABLE_CONTEXT_MENU_REQUESTED: {
				UiTable.ContextMenuRequestedEvent e = (UiTable.ContextMenuRequestedEvent) event;
				lastSeenContextMenuRequestId = e.getRequestId();
				RECORD record = clientRecordCache.getRecordByClientId(e.getRecordId());
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

	private Object getCellValue(RECORD record, TableColumn<RECORD> column) {
		Map<String, Object> changesForRecord = transientChangesByRecordAndPropertyName.getOrDefault(record, Collections.emptyMap());
		boolean changed = changesForRecord.containsKey(column.getPropertyName());
		Object cellValue;
		if (changed) { // associated value might be null!!
			cellValue = changesForRecord.get(column.getPropertyName());
		} else {
			cellValue = extractRecordProperty(record, column);
		}
		return cellValue;
	}

	private Object extractRecordProperty(RECORD record, TableColumn<RECORD> column) {
		if (column.getValueExtractor() != null) {
			return column.getValueExtractor().extract(record);
		} else {
			return propertyExtractor.getValue(record, column.getPropertyName());
		}
	}

	public List<String> getColumnPropertyNames() {
		return columns.stream()
				.map(TableColumn::getPropertyName)
				.collect(Collectors.toList());
	}

	public TableCellCoordinates<RECORD> getActiveEditorCell() {
		return activeEditorCell;
	}

	public AbstractField getActiveEditorField() {
		if (activeEditorCell != null) {
			return getColumnByPropertyName(activeEditorCell.getPropertyName()).getField();
		} else {
			return null;
		}
	}

	public void setCellValue(RECORD record, String propertyName, Object value) {
		transientChangesByRecordAndPropertyName.computeIfAbsent(record, record1 -> new HashMap<>()).put(propertyName, value);
		Integer uiRecordIdOrNull = clientRecordCache.getUiRecordIdOrNull(record);
		if (uiRecordIdOrNull != null) {
			queueCommandIfRendered(() -> new UiTable.SetCellValueCommand(getId(), uiRecordIdOrNull, propertyName, value));
		}
	}

	public void focusCell(RECORD record, String propertyName) {
		// TODO #field=component
	}

	public void setCellMarked(RECORD record, String propertyName, boolean mark) {
		if (mark) {
			markedCells.computeIfAbsent(record, record1 -> new HashSet<>()).add(propertyName);
		} else {
			Set<String> markedCellPropertyNames = markedCells.getOrDefault(record, Collections.emptySet());
			if (markedCellPropertyNames.isEmpty()) {
				markedCells.remove(record);
			}
		}
		Integer uiRecordIdOrNull = clientRecordCache.getUiRecordIdOrNull(record);
		if (uiRecordIdOrNull != null) {
			queueCommandIfRendered(() -> new UiTable.MarkTableFieldCommand(getId(), uiRecordIdOrNull, propertyName, mark));
		}
	}

	public void clearRecordMarkings(RECORD record) {
		markedCells.remove(record);
		updateRecordOnClientSide(record);
	}

	public void clearAllCellMarkings() {
		markedCells.clear();
		queueCommandIfRendered(() -> new UiTable.ClearAllFieldMarkingsCommand(getId()));
	}

	// TODO implement using decider? more general formatting options?
	public void setRecordBold(RECORD record, boolean bold) {
		Integer uiRecordIdOrNull = clientRecordCache.getUiRecordIdOrNull(record);
		if (uiRecordIdOrNull != null) {
			queueCommandIfRendered(() -> new UiTable.SetRecordBoldCommand(getId(), uiRecordIdOrNull, bold));
		}
	}

	public void selectSingleRow(RECORD record, boolean scrollToRecord) {
		this.selectedRecord = record;
		this.selectedRecords.clear();
		queueCommandIfRendered(() -> new UiTable.SelectRowsCommand(getId(), clientRecordCache.getUiRecordIds(getSelectedRecords()), scrollToRecord));
	}

	protected void updateColumnMessages(TableColumn<RECORD> tableColumn) {
		queueCommandIfRendered(() -> new UiTable.SetColumnMessagesCommand(getId(), tableColumn.getPropertyName(), tableColumn.getMessages().stream()
				.map(message -> message.createUiFieldMessage(FieldMessage.Position.POPOVER, FieldMessage.Visibility.ON_HOVER_OR_FOCUS))
				.collect(Collectors.toList())));
	}

	public List<FieldMessage> getCellMessages(RECORD record, String propertyName) {
		return this.cellMessages.getOrDefault(record, Collections.emptyMap()).getOrDefault(propertyName, Collections.emptyList());
	}

	public void addCellMessage(RECORD record, String propertyName, FieldMessage message) {
		List<FieldMessage> cellMessages = this.cellMessages.computeIfAbsent(record, x -> new HashMap<>()).computeIfAbsent(propertyName, x -> new ArrayList<>());
		cellMessages.add(message);
		updateSingleCellMessages(record, propertyName, cellMessages);
	}

	public void removeCellMessage(RECORD record, String propertyName, FieldMessage message) {
		List<FieldMessage> cellMessages = this.cellMessages.computeIfAbsent(record, x -> new HashMap<>()).computeIfAbsent(propertyName, x -> new ArrayList<>());
		cellMessages.remove(message);
		updateSingleCellMessages(record, propertyName, cellMessages);
	}

	private void updateSingleCellMessages(RECORD record, String propertyName, List<FieldMessage> cellMessages) {
		Integer uiRecordId = clientRecordCache.getUiRecordIdOrNull(record);
		if (uiRecordId != null) {
			queueCommandIfRendered(() -> new UiTable.SetSingleCellMessagesCommand(
					getId(),
					uiRecordId,
					propertyName,
					cellMessages.stream()
							.map(m -> m.createUiFieldMessage(FieldMessage.Position.POPOVER, FieldMessage.Visibility.ON_HOVER_OR_FOCUS))
							.collect(Collectors.toList())
			));
		}
	}


	protected void updateColumnVisibility(TableColumn<RECORD> tableColumn) {
		queueCommandIfRendered(() -> new UiTable.SetColumnVisibilityCommand(getId(), tableColumn.getPropertyName(), tableColumn.isVisible()));
	}

	// TODO #focus propagation
//	@Override
//	public void handleFieldFocused(AbstractField field) {
//		if (selectedRecord != null) {
//			queueCommandIfRendered(() -> new UiTable.FocusCellCommand(getId(), clientRecordCache.getUiRecord(selectedRecord), field.getPropertyName()));
//		}
//	}

	public void addNonModelRecord(RECORD record, boolean addToTop) {
		if (addToTop) {
			this.topNonModelRecords.add(0, record);
		} else {
			this.bottomNonModelRecords.add(record);
		}
		refreshData();
	}

	public void removeNonModelRecord(RECORD record) {
		this.topNonModelRecords.remove(record);
		this.bottomNonModelRecords.remove(record);
		refreshData();
	}

	public void removeAllNonModelRecords() {
		this.topNonModelRecords.clear();
		this.bottomNonModelRecords.clear();
		refreshData();
	}

	public TableModel getModel() {
		return model;
	}

	public void setModel(TableModel<RECORD> model) {
		unregisterModelEventListeners();
		this.model = model;
		clearChangeBuffer();
		model.onAllDataChanged().addListener(this.onAllDataChangedListener);
		model.onRecordAdded().addListener(this.onRecordAddedListener);
		model.onRecordDeleted().addListener(this.onRecordDeletedListener);
		model.onRecordUpdated().addListener(this.onRecordUpdatedListener);
		refreshData(true, true, true);
	}

	private void unregisterModelEventListeners() {
		if (this.model != null) {
			this.model.onAllDataChanged().removeListener(this.onAllDataChangedListener);
			this.model.onRecordAdded().removeListener(this.onRecordAddedListener);
			this.model.onRecordDeleted().removeListener(this.onRecordDeletedListener);
			this.model.onRecordUpdated().removeListener(this.onRecordUpdatedListener);
		}
	}

	private void onAllDataChanged(Void aVoid) {
		refreshData(true, false, true);
	}

	private void onRecordAdded(RECORD record) {
		if (isRendered()) {
			this.refreshData(false, false, false);
		}
	}

	private void onRecordDeleted(RECORD record) {
		clearMetaDataForRecord(record);
		if (Objects.equals(selectedRecord, record)) {
			selectedRecord = null;
		}
		if (selectedRecords != null) {
			selectedRecords.remove(record);
		}
		if (isRendered()) {
			CacheManipulationHandle<Integer> cacheResponse = clientRecordCache.removeRecord(record);
			getSessionContext().queueCommand(
					new UiTable.DeleteRowsCommand(getId(), Collections.singletonList(cacheResponse.getResult())),
					aVoid -> cacheResponse.commit()
			);
		}
	}

	public void clearRecordMessages(RECORD record) {
		cellMessages.remove(record);
		updateRecordOnClientSide(record);
	}

	public void updateRecordMessages(RECORD record, Map<String, List<FieldMessage>> messages) {
		cellMessages.put(record, new HashMap<>(messages));
		updateRecordOnClientSide(record);
	}

	private void clearMetaDataForRecord(RECORD record) {
		transientChangesByRecordAndPropertyName.remove(record);
		cellMessages.remove(record);
		markedCells.remove(record);
	}

	private void onRecordUpdated(RECORD record) {
		clearMetaDataForRecord(record);
		updateRecordOnClientSide(record);
	}

	private void updateRecordOnClientSide(RECORD record) {
		if (isRendered()) {
			CacheManipulationHandle<UiTableClientRecord> cacheResponse = clientRecordCache.addOrUpdateRecord(record);
			UiTableClientRecord clientRecord = cacheResponse.getResult();
			applyTransientChangesToClientRecord(clientRecord);
			getSessionContext().queueCommand(
					new UiTable.UpdateRecordCommand(getId(), clientRecord),
					aVoid -> cacheResponse.commit()
			);
		}
	}

	public void refreshData() {
		refreshData(false, false, false);
	}

	private void refreshData(boolean resetMarkingsAndMessages, boolean resetSelection, boolean resetEditingState) {
		if (resetEditingState) {
			transientChangesByRecordAndPropertyName.clear();
		}
		if (resetMarkingsAndMessages) {
			cellMessages.clear();
			markedCells.clear();
		}
		if (resetSelection) {
			selectedRecord = null;
			selectedRecords.clear();
		}

		sendDataToClient(0, pageSize, true);

		if (!resetEditingState && activeEditorCell != null) {
			Integer editingClientRecordId = clientRecordCache.getUiRecordIdOrNull(activeEditorCell.getRecord());
			if (editingClientRecordId != null) {
				// TODO DO NOT DO THIS!! This must be done on the client side!!!! (client sends events for active cell vs server sends command to edit cell --> server will not know the currently edited cell. Solution: reuse ids to enable client to do this!
				// queueCommandIfRendered(() -> new UiTable.EditCellIfAvailableCommand(getId(), editingClientRecordId, activeEditorCell.getPropertyName()));
			}
		}
	}

	private void sendDataToClient(int startIndex, int endIndex, boolean clear) {
		if (startIndex == endIndex && !clear && topNonModelRecords.isEmpty() && bottomNonModelRecords.isEmpty()) {
			// nothing to do on the client side!
			return;
		}
		if (isRendered()) {
			int totalCount = getTotalRecordsCount();
			List<RECORD> records = retrieveRecords(startIndex, endIndex - startIndex);

			CacheManipulationHandle<List<UiTableClientRecord>> cacheResponse;
			if (clear) {
				cacheResponse = clientRecordCache.replaceRecords(records);
			} else {
				cacheResponse = clientRecordCache.addRecords(records);
			}

			if (!transientChangesByRecordAndPropertyName.isEmpty()) {
				cacheResponse.getResult().forEach(uiRecord -> applyTransientChangesToClientRecord(uiRecord));
			}

			UiTable.AddDataCommand addDataCommand = new UiTable.AddDataCommand(getId(), startIndex, cacheResponse.getResult(), totalCount, sortField, sortDirection.toUiSortDirection(), clear);
			LOGGER.debug("Sending table data to client: start: " + addDataCommand.getStartRowIndex() + "; length: " + addDataCommand.getData().size());
			getSessionContext().queueCommand(
					addDataCommand,
					aVoid -> cacheResponse.commit()
			);
		}
	}

	private int getTotalRecordsCount() {
		return model.getCount() + topNonModelRecords.size() + bottomNonModelRecords.size();
	}

	private List<RECORD> retrieveRecords(int startIndex, int length) {
		if (startIndex < 0 || length < 0) {
			LOGGER.warn("Data coordinates do not make sense: startIndex {}, length {}", startIndex, length);
			return Collections.emptyList();
		}
		
		int endIndex = startIndex + length;
		int totalTopRecords = topNonModelRecords.size();
		int totalModelRecords = model.getCount();
		int totalBottomRecords = bottomNonModelRecords.size();

		if (endIndex > totalTopRecords + totalModelRecords + totalBottomRecords) {
			endIndex = totalTopRecords + totalModelRecords + totalBottomRecords;
			length = endIndex - startIndex;
		}

		if (startIndex < totalTopRecords && endIndex <= totalTopRecords) {
			return topNonModelRecords.stream().skip(startIndex).limit(length).collect(Collectors.toList());
		} else if (startIndex < totalTopRecords && endIndex <= totalTopRecords + totalModelRecords) {
			List<RECORD> records = new ArrayList<>();
			records.addAll(topNonModelRecords.stream().skip(startIndex).limit(totalTopRecords - startIndex).collect(Collectors.toList()));
			records.addAll(retrieveRecordsFromModel(0, length - records.size()));
			return records;
		} else if (startIndex < totalTopRecords && endIndex > totalTopRecords + totalModelRecords) {
			List<RECORD> records = new ArrayList<>();
			records.addAll(topNonModelRecords.stream().skip(startIndex).limit(totalTopRecords - startIndex).collect(Collectors.toList()));
			records.addAll(retrieveRecordsFromModel(0, totalModelRecords));
			records.addAll(bottomNonModelRecords.stream().skip(0).limit(length - records.size()).collect(Collectors.toList()));
			return records;
		} else if (startIndex >= totalTopRecords && startIndex < totalTopRecords + totalModelRecords && endIndex <= totalTopRecords + totalModelRecords) {
			return retrieveRecordsFromModel(startIndex - topNonModelRecords.size(), length);
		} else if (startIndex >= totalTopRecords && startIndex < totalTopRecords + totalModelRecords && endIndex > totalTopRecords + totalModelRecords) {
			List<RECORD> records = new ArrayList<>();
			records.addAll(retrieveRecordsFromModel(startIndex - topNonModelRecords.size(), endIndex - startIndex - totalTopRecords));
			records.addAll(bottomNonModelRecords.stream().skip(0).limit(length - records.size()).collect(Collectors.toList()));
			return records;
		} else if (startIndex >= totalTopRecords + totalModelRecords) {
			return bottomNonModelRecords.stream().skip(startIndex - totalTopRecords - totalModelRecords).limit(length).collect(Collectors.toList());
		} else {
			LOGGER.error("This path should never be reached!");
			return Collections.emptyList();
		}
	}

	private List<RECORD> retrieveRecordsFromModel(int startIndex, int length) {
		List<RECORD> records = model.getRecords(startIndex, length, getSorting());
		if (records.size() == length) {
			return records;
		} else if (records.size() < length) {
			LOGGER.warn("TableModel did not return the requested amount of data!");
			return records;
		} else {
			LOGGER.warn("TableModel returned too much data. Truncating!");
			return new ArrayList<>(records.subList(0, length));
		}
	}


	private void applyTransientChangesToClientRecord(UiTableClientRecord uiRecord) {
		Map<String, Object> changes = transientChangesByRecordAndPropertyName.get(clientRecordCache.getRecordByClientId(uiRecord.getId()));
		if (changes != null) {
			changes.forEach((key, value) -> uiRecord.getValues().put(key, getColumnByPropertyName(key).getField().convertUxValueToUiValue(value)));
		}
	}

	public void cancelEditing() {
		TableCellCoordinates<RECORD> activeEditorCell = getActiveEditorCell();
		Integer uiRecordIdOrNull = clientRecordCache.getUiRecordIdOrNull(activeEditorCell.getRecord());
		if (uiRecordIdOrNull != null) {
			queueCommandIfRendered(() -> new UiTable.CancelEditingCellCommand(getId(), uiRecordIdOrNull, activeEditorCell.getPropertyName()));
		}
	}

	private Map<String, List<UiFieldMessage>> createUiFieldMessagesForRecord(Map<String, List<FieldMessage>> recordFieldMessages) {
		return recordFieldMessages.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> entry.getKey(),
						entry -> entry.getValue().stream()
								.map(fieldMessage -> fieldMessage.createUiFieldMessage(FieldMessage.Position.POPOVER, FieldMessage.Visibility.ON_HOVER_OR_FOCUS))
								.collect(Collectors.toList())
				));
	}

	private UiTableClientRecord createUiTableClientRecord(RECORD record) {
		UiTableClientRecord clientRecord = new UiTableClientRecord();
		Map<String, Object> uiValues = new HashMap<>();
		for (TableColumn<RECORD> column : columns) {
			Object uxValue = extractRecordProperty(record, column);
			uiValues.put(column.getPropertyName(), column.getField().convertUxValueToUiValue(uxValue));
		}
		clientRecord.setValues(uiValues);
		clientRecord.setSelected(selectedRecord != null && selectedRecord.equals(record) || selectedRecords.contains(record));
		clientRecord.setMessages(createUiFieldMessagesForRecord(cellMessages.getOrDefault(record, Collections.emptyMap())));
		clientRecord.setMarkings(new ArrayList<>(markedCells.getOrDefault(record, Collections.emptySet())));
		return clientRecord;
	}

	public List<TableColumn<RECORD>> getColumns() {
		return columns;
	}

	public boolean isDisplayAsList() {
		return displayAsList;
	}

	public void setDisplayAsList(boolean displayAsList) {
		this.displayAsList = displayAsList;
		reRenderIfRendered();
	}

	public boolean isForceFitWidth() {
		return forceFitWidth;
	}

	public void setForceFitWidth(boolean forceFitWidth) {
		this.forceFitWidth = forceFitWidth;
		reRenderIfRendered();
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
		reRenderIfRendered();
	}

	public boolean isStripedRows() {
		return stripedRows;
	}

	public void setStripedRows(boolean stripedRows) {
		this.stripedRows = stripedRows;
		reRenderIfRendered();
	}

	public void setStripedRowColorEven(Color stripedRowColorEven) {
		this.setCssStyle(".striped-rows .slick-row.even", "background-color", stripedRowColorEven != null ? stripedRowColorEven.toHtmlColorString() : null);
	}

	public void setStripedRowColorOdd(Color stripedRowColorOdd) {
		this.setCssStyle(".striped-rows .slick-row.odd", "background-color", stripedRowColorOdd != null ? stripedRowColorOdd.toHtmlColorString() : null);
	}

	public boolean isHideHeaders() {
		return hideHeaders;
	}

	public void setHideHeaders(boolean hideHeaders) {
		this.hideHeaders = hideHeaders;
		reRenderIfRendered();
	}

	public boolean isAllowMultiRowSelection() {
		return allowMultiRowSelection;
	}

	public void setAllowMultiRowSelection(boolean allowMultiRowSelection) {
		this.allowMultiRowSelection = allowMultiRowSelection;
		reRenderIfRendered();
	}

	public void setSelectionColor(Color selectionColor) {
		this.setCssStyle(".slick-cell.selected", "background-color", selectionColor != null ? selectionColor.toHtmlColorString() : null);
	}

	public void setRowBorderWidth(int rowBorderWidth) {
		this.setCssStyle(".slick-cell", "border-bottom-width", rowBorderWidth + "px");
	}

	public void setRowBorderColor(Color rowBorderColor) {
		this.setCssStyle(".slick-cell", "border-color", rowBorderColor.toHtmlColorString());
	}

	public boolean isShowRowCheckBoxes() {
		return showRowCheckBoxes;
	}

	public void setShowRowCheckBoxes(boolean showRowCheckBoxes) {
		this.showRowCheckBoxes = showRowCheckBoxes;
		reRenderIfRendered();
	}

	public boolean isShowNumbering() {
		return showNumbering;
	}

	public void setShowNumbering(boolean showNumbering) {
		this.showNumbering = showNumbering;
		reRenderIfRendered();
	}

	public Sorting getSorting() {
		return new Sorting(sortField, sortDirection);
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
		refreshData(false, false, false);
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
		refreshData(false, false, false);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		reRenderIfRendered();
	}

	public boolean isEnsureEmptyLastRow() {
		return ensureEmptyLastRow;
	}

	public void setEnsureEmptyLastRow(boolean ensureEmptyLastRow) {
		this.ensureEmptyLastRow = ensureEmptyLastRow;
		reRenderIfRendered();
	}

	public boolean isTreeMode() {
		return treeMode;
	}

	public void setTreeMode(boolean treeMode) {
		this.treeMode = treeMode;
		reRenderIfRendered();
	}

	public String getIndentedColumnName() {
		return indentedColumnName;
	}

	public void setIndentedColumnName(String indentedColumnName) {
		this.indentedColumnName = indentedColumnName;
		reRenderIfRendered();
	}

	public int getIndentation() {
		return indentation;
	}

	public void setIndentation(int indentation) {
		this.indentation = indentation;
		reRenderIfRendered();
	}

	public SelectionFrame getSelectionFrame() {
		return selectionFrame;
	}

	public void setSelectionFrame(SelectionFrame selectionFrame) {
		this.selectionFrame = selectionFrame;
		reRenderIfRendered();
	}

	public boolean isShowHeaderRow() {
		return showHeaderRow;
	}

	public void setShowHeaderRow(boolean showHeaderRow) {
		this.showHeaderRow = showHeaderRow;
		reRenderIfRendered();
	}

	public void setHeaderRowBorderWidth(int headerRowBorderWidth) {
		this.setCssStyle(".slick-headerrow", "border-bottom-width", headerRowBorderWidth + "px");
	}

	public void setHeaderRowBorderColor(Color headerRowBorderColor) {
		this.setCssStyle(".slick-headerrow", "border-bottom-color", headerRowBorderColor != null ? headerRowBorderColor.toHtmlColorString() : null);
	}

	public int getHeaderRowHeight() {
		return headerRowHeight;
	}

	public void setHeaderRowHeight(int headerRowHeight) {
		this.headerRowHeight = headerRowHeight;
		reRenderIfRendered();
	}

	public void setHeaderRowBackgroundColor(Color headerRowBackgroundColor) {
		this.setCssStyle(".slick-headerrow", "background-color", headerRowBackgroundColor != null ? headerRowBackgroundColor.toHtmlColorString() : null);
	}

	public Map<String, AbstractField> getHeaderRowFields() {
		return Collections.unmodifiableMap(headerRowFields);
	}

	public void setHeaderRowFields(Map<String, AbstractField> headerRowFields) {
		this.headerRowFields.clear();
		this.headerRowFields.putAll(headerRowFields);
		this.headerRowFields.values().forEach(field -> field.setParent(this));
		reRenderIfRendered();
	}

	public void setHeaderRowField(String columnName, AbstractField field) {
		this.headerRowFields.put(columnName, field);
	}

	public boolean isShowFooterRow() {
		return showFooterRow;
	}

	public void setShowFooterRow(boolean showFooterRow) {
		this.showFooterRow = showFooterRow;
		reRenderIfRendered();
	}

	public void setFooterRowBorderWidth(int footerRowBorderWidth) {
		this.setCssStyle(".slick-footerrow", "border-top-width", footerRowBorderWidth + "px");
	}

	public void setFooterRowBorderColor(Color footerRowBorderColor) {
		this.setCssStyle(".slick-footerrow", "border-top-color", footerRowBorderColor != null ? footerRowBorderColor.toHtmlColorString() : null);
	}

	public int getFooterRowHeight() {
		return footerRowHeight;
	}

	public void setFooterRowHeight(int footerRowHeight) {
		this.footerRowHeight = footerRowHeight;
		reRenderIfRendered();
	}

	public void setFooterRowBackgroundColor(Color footerRowBackgroundColor) {
		this.setCssStyle(".slick-footerrow", "background-color", footerRowBackgroundColor != null ? footerRowBackgroundColor.toHtmlColorString() : null);
	}

	public Map<String, AbstractField> getFooterRowFields() {
		return footerRowFields;
	}

	public void setFooterRowFields(Map<String, AbstractField> footerRowFields) {
		this.footerRowFields.clear();
		this.footerRowFields.putAll(footerRowFields);
		this.headerRowFields.values().forEach(field -> field.setParent(this));
		reRenderIfRendered();
	}

	public void setFooterRowField(String columnName, AbstractField field) {
		this.footerRowFields.put(columnName, field);
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public TableColumn<RECORD> getColumnByPropertyName(String propertyName) {
		return columns.stream()
				.filter(column -> column.getPropertyName().equals(propertyName))
				.findFirst().orElse(null);
	}

	public AbstractField getHeaderRowFieldByName(String propertyName) {
		return headerRowFields.get(propertyName);
	}

	public AbstractField getFooterRowFieldByName(String propertyName) {
		return footerRowFields.get(propertyName);
	}

	public List<RECORD> getRecordsWithChangedCellValues() {
		return new ArrayList<>(transientChangesByRecordAndPropertyName.keySet());
	}

	public Map<String, Object> getChangedCellValues(RECORD record) {
		return transientChangesByRecordAndPropertyName.getOrDefault(record, Collections.emptyMap());
	}

	public Map<String, Object> getAllCellValuesForRecord(RECORD record) {
		Map<String, Object> values = columns.stream()
				.collect(Collectors.toMap(TableColumn::getPropertyName, c -> extractRecordProperty(record, c)));
		values.putAll(transientChangesByRecordAndPropertyName.getOrDefault(record, Collections.emptyMap()));
		return values;
	}

	public void clearChangeBuffer() {
		transientChangesByRecordAndPropertyName.clear();
	}

	public void applyCellValuesToRecord(RECORD record) {
		Map<String, Object> changedCellValues = getChangedCellValues(record);
		propertyInjector.setValues(record, changedCellValues);
	}

	public void revertChanges() {
		refreshData(true, false, true);
	}

	public RECORD getSelectedRecord() {
		return selectedRecord;
	}

	public PropertyExtractor<RECORD> getPropertyExtractor() {
		return propertyExtractor;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.propertyExtractor = propertyExtractor;
	}

	public PropertyInjector<RECORD> getPropertyInjector() {
		return propertyInjector;
	}

	public void setPropertyInjector(PropertyInjector<RECORD> propertyInjector) {
		this.propertyInjector = propertyInjector;
	}

	public void setMaxCacheCapacity(int maxCapacity) {
		clientRecordCache.setMaxCapacity(maxCapacity);
	}

	public int getMaxCacheCapacity() {
		return clientRecordCache.getMaxCapacity();
	}

	public List<RECORD> getSelectedRecords() {
		return selectedRecords != null ? selectedRecords : selectedRecord != null ? Collections.singletonList(selectedRecord) : Collections.emptyList(); // TODO completely rewrite this!!! (multiple
		// selected records should be standard!)
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
