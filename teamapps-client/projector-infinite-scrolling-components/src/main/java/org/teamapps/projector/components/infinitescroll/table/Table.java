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
package org.teamapps.projector.components.infinitescroll.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teamapps.common.format.Color;
import org.teamapps.dto.DtoComponent;
import org.teamapps.dto.DtoFieldMessage;
import org.teamapps.dto.DtoIdentifiableClientRecord;
import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.event.ProjectorEvent;
import org.teamapps.icons.Icon;
import org.teamapps.projector.components.infinitescroll.dto.DtoInfiniteItemView;
import org.teamapps.projector.components.infinitescroll.dto.DtoTable;
import org.teamapps.projector.components.infinitescroll.dto.DtoTableClientRecord;
import org.teamapps.projector.components.infinitescroll.dto.DtoTableColumn;
import org.teamapps.projector.components.infinitescroll.infiniteitemview.AbstractInfiniteListComponent;
import org.teamapps.projector.components.infinitescroll.infiniteitemview.RecordsChangedEvent;
import org.teamapps.projector.components.infinitescroll.infiniteitemview.RecordsRemovedEvent;
import org.teamapps.ux.cache.record.DuplicateEntriesException;
import org.teamapps.ux.cache.record.ItemRange;
import org.teamapps.ux.component.annotations.ProjectorComponent;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.component.field.FieldMessage;
import org.teamapps.ux.component.field.validator.FieldValidator;
import org.teamapps.ux.data.extraction.*;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ProjectorComponent(library = InfiniteScrollingComponentLibrary.class)
public class Table<RECORD> extends AbstractInfiniteListComponent<RECORD, TableModel<RECORD>> implements org.teamapps.ux.component.Component {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public final ProjectorEvent<CellEditingStartedEvent<RECORD, ?>> onCellEditingStarted = createProjectorEventBoundToUiEvent(DtoTable.CellEditingStartedEvent.TYPE_ID);
	public final ProjectorEvent<CellEditingStoppedEvent<RECORD, ?>> onCellEditingStopped = createProjectorEventBoundToUiEvent(DtoTable.CellEditingStoppedEvent.TYPE_ID);
	public final ProjectorEvent<FieldValueChangedEventData<RECORD, ?>> onCellValueChanged = createProjectorEventBoundToUiEvent(DtoTable.CellValueChangedEvent.TYPE_ID);
	/**
	 * Fired when any number of rows is selected by the user.
	 */
	public final ProjectorEvent<List<RECORD>> onRowsSelected = createProjectorEventBoundToUiEvent(DtoTable.RowsSelectedEvent.TYPE_ID);
	/**
	 * Fired only when a single row is selected by the user.
	 */
	public final ProjectorEvent<RECORD> onSingleRowSelected = createProjectorEventBoundToUiEvent(DtoTable.RowsSelectedEvent.TYPE_ID);
	/**
	 * Fired only when multiple rows are selected by the user.
	 */
	public final ProjectorEvent<List<RECORD>> onMultipleRowsSelected = createProjectorEventBoundToUiEvent(DtoTable.RowsSelectedEvent.TYPE_ID);

	public final ProjectorEvent<CellClickedEvent<RECORD, ?>> onCellClicked = createProjectorEventBoundToUiEvent(DtoTable.CellClickedEvent.TYPE_ID);
	public final ProjectorEvent<SortingChangedEventData> onSortingChanged = createProjectorEventBoundToUiEvent(DtoTable.SortingChangedEvent.TYPE_ID);
	public final ProjectorEvent<ColumnOrderChangeEventData<RECORD, ?>> onColumnOrderChange = createProjectorEventBoundToUiEvent(DtoTable.FieldOrderChangeEvent.TYPE_ID);
	public final ProjectorEvent<ColumnSizeChangeEventData<RECORD, ?>> onColumnSizeChange = createProjectorEventBoundToUiEvent(DtoTable.ColumnSizeChangeEvent.TYPE_ID);

	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();
	private PropertyInjector<RECORD> propertyInjector = new BeanPropertyInjector<>();

	private int clientRecordIdCounter = 0;

	private List<RECORD> selectedRecords = List.of();
	private TableCellCoordinates<RECORD> activeEditorCell;

	private final Map<RECORD, Map<String, Object>> transientChangesByRecordAndPropertyName = new HashMap<>();
	private final Map<RECORD, Map<String, List<FieldMessage>>> cellMessages = new HashMap<>();
	private final Map<RECORD, Set<String>> markedCells = new HashMap<>();

	private final List<TableColumn<RECORD, ?>> columns = new ArrayList<>();

	private boolean displayAsList; // list has no cell borders, table has. selection policy: list = row selection, table = cell selection
	private boolean forceFitWidth; //if true, force the widths of all columns to fit into the available space of the list
	private int rowHeight = 28;
	private boolean stripedRows = true;
	private boolean hideHeaders; //if true, do not show any headers
	private boolean allowMultiRowSelection = false;
	private boolean showRowCheckBoxes; //if true, show check boxes on the left
	private boolean showNumbering; //if true, show numbering on the left
	private boolean textSelectionEnabled = true;

	private Sorting sorting; // nullable

	private boolean editable; //only valid for tables
	private boolean ensureEmptyLastRow; //if true, there is always an empty last row, as soon as any data is inserted into the empty row a new empty row is inserted

	private boolean treeMode; //if true, use the parent id property of UiDataRecord to display the table as tree
	private String indentedColumnName; // if set, indent this column depending on the depth in the data hierarchy
	private int indentation = 15; // in pixels

	private SelectionFrame selectionFrame;

	// ----- header -----

	private boolean showHeaderRow = false;
	private int headerRowHeight = 28;
	private final Map<String, AbstractField> headerRowFields = new HashMap<>(0);

	// ----- footer -----

	private boolean showFooterRow = false;
	private int footerRowHeight = 28;
	private final Map<String, AbstractField> footerRowFields = new HashMap<>(0);

	private final List<RECORD> topNonModelRecords = new ArrayList<>();
	private final List<RECORD> bottomNonModelRecords = new ArrayList<>();

	private Function<RECORD, org.teamapps.ux.component.Component> contextMenuProvider = null;
	private int lastSeenContextMenuRequestId;

	public Table() {
		this(new ArrayList<>());
	}

	public Table(List<TableColumn<RECORD, ?>> columns) {
		super(new ListTableModel<>());
		columns.forEach(this::addColumn);
	}

	public static <RECORD> Table<RECORD> create() {
		return new Table<>();
	}

	public void addColumn(TableColumn<RECORD, ?> column) {
		addColumn(column, columns.size());
	}

	public void addColumn(TableColumn<RECORD, ?> column, int index) {
		addColumns(List.of(column), index);
	}

	public <VALUE> TableColumn<RECORD, VALUE> addColumn(String propertyName, String title, AbstractField<VALUE> field) {
		return addColumn(propertyName, null, title, field, TableColumn.DEFAULT_WIDTH);
	}

	public <VALUE> TableColumn<RECORD, VALUE> addColumn(String propertyName, Icon<?, ?> icon, String title, AbstractField<VALUE> field) {
		return addColumn(propertyName, icon, title, field, TableColumn.DEFAULT_WIDTH);
	}

	public <VALUE> TableColumn<RECORD, VALUE> addColumn(String propertyName, Icon<?, ?> icon, String title, AbstractField<VALUE> field, int defaultWidth) {
		TableColumn<RECORD, VALUE> column = new TableColumn<>(propertyName, icon, title, field, defaultWidth);
		addColumn(column);
		return column;
	}

	public void addColumns(List<TableColumn<RECORD, ?>> newColumns, int index) {
		this.columns.addAll(index, newColumns);
		newColumns.forEach(column -> {
			column.setTable(this);
			AbstractField<?> field = column.getField();
			field.setParent(this);
		});
		if (isRendered()) {
			getSessionContext().sendCommandIfRendered(this, new DtoTable.AddColumnsCommand(newColumns.stream()
					.map(TableColumn::createUiTableColumn)
					.collect(Collectors.toList()), index));
			// TODO #table resend data
		}
	}

	public void removeColumn(String propertyName) {
		columns.stream()
				.filter(c -> Objects.equals(c.getPropertyName(), propertyName))
				.findFirst()
				.ifPresent(this::removeColumn);
	}

	public void removeColumn(TableColumn<RECORD, ?> column) {
		this.removeColumns(Collections.singletonList(column));
	}

	public void removeColumns(List<TableColumn<RECORD, ?>> obsoleteColumns) {
		this.columns.removeAll(obsoleteColumns);
		if (isRendered()) {
			getSessionContext().sendCommandIfRendered(this, new DtoTable.RemoveColumnsCommand(obsoleteColumns.stream()
					.map(TableColumn::getPropertyName)
					.collect(Collectors.toList())));
		}
	}

	@Override
	protected void preRegisteringModel(TableModel<RECORD> model) {
		// this should be done before registering the model, so we don't have to handle the corresponding model events
		model.setSorting(sorting);
	}

	@Override
	public DtoComponent createDto() {
		List<DtoTableColumn> columns = this.columns.stream()
				.map(TableColumn::createUiTableColumn)
				.collect(Collectors.toList());
		DtoTable uiTable = new DtoTable(columns);
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
		uiTable.setTextSelectionEnabled(textSelectionEnabled);
		uiTable.setSortField(sorting != null ? sorting.getFieldName() : null);
		uiTable.setSortDirection(sorting != null ? sorting.getSortDirection().toUiSortDirection() : null);
		uiTable.setEditable(editable);
		uiTable.setTreeMode(treeMode);
		uiTable.setIndentedColumnName(indentedColumnName);
		uiTable.setIndentation(indentation);
		uiTable.setShowHeaderRow(showHeaderRow);
		uiTable.setHeaderRowHeight(headerRowHeight);
		uiTable.setHeaderRowFields(this.headerRowFields.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().createDtoReference())));
		uiTable.setShowFooterRow(showFooterRow);
		uiTable.setFooterRowHeight(footerRowHeight);
		uiTable.setFooterRowFields(this.footerRowFields.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().createDtoReference())));
		uiTable.setContextMenuEnabled(contextMenuProvider != null);
		return uiTable;
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
		switch (event.getTypeId()) {
			case DtoTable.RowsSelectedEvent.TYPE_ID -> {
				var rowsSelectedEvent = event.as(DtoTable.RowsSelectedEventWrapper.class);
				selectedRecords = renderedRecords.getRecords(rowsSelectedEvent.getRecordIds());
				this.onRowsSelected.fire(selectedRecords);
				if (selectedRecords.size() == 1) {
					this.onSingleRowSelected.fire(selectedRecords.get(0));
				} else if (selectedRecords.size() > 1) {
					this.onMultipleRowsSelected.fire(selectedRecords);
				}
			}
			case DtoTable.CellClickedEvent.TYPE_ID -> {
				var cellClickedEvent = event.as(DtoTable.CellClickedEventWrapper.class);
				RECORD record = renderedRecords.getRecord(cellClickedEvent.getRecordId());
				TableColumn<RECORD, ?> column = getColumnByPropertyName(cellClickedEvent.getColumnPropertyName());
				if (record != null && column != null) {
					this.onCellClicked.fire(new CellClickedEvent<>(record, column));
				}
			}
			case DtoTable.CellEditingStartedEvent.TYPE_ID -> {
				var editingStartedEvent = event.as(DtoTable.CellEditingStartedEventWrapper.class);
				RECORD record = renderedRecords.getRecord(editingStartedEvent.getRecordId());
				TableColumn<RECORD, ?> column = getColumnByPropertyName(editingStartedEvent.getColumnPropertyName());
				if (record == null || column == null) {
					return;
				}
				this.activeEditorCell = new TableCellCoordinates<>(record, editingStartedEvent.getColumnPropertyName());
				this.selectedRecords = List.of(activeEditorCell.getRecord());
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
				this.onCellEditingStarted.fire(new CellEditingStartedEvent(record, column, cellValue));
			}
			case DtoTable.CellEditingStoppedEvent.TYPE_ID -> {
				var editingStoppedEvent = event.as(DtoTable.CellEditingStoppedEventWrapper.class);
				this.activeEditorCell = null;
				RECORD record = renderedRecords.getRecord(editingStoppedEvent.getRecordId());
				TableColumn<RECORD, ?> column = getColumnByPropertyName(editingStoppedEvent.getColumnPropertyName());
				if (record == null || column == null) {
					return;
				}
				this.onCellEditingStopped.fire(new CellEditingStoppedEvent<>(record, column));
			}
			case DtoTable.CellValueChangedEvent.TYPE_ID -> {
				var changeEvent = event.as(DtoTable.CellValueChangedEventWrapper.class);
				RECORD record = renderedRecords.getRecord(changeEvent.getRecordId());
				TableColumn<RECORD, ?> column = this.getColumnByPropertyName(changeEvent.getColumnPropertyName());
				if (record == null || column == null) {
					return;
				}
				Object value = column.getField().convertUiValueToUxValue(changeEvent.getValue());
				transientChangesByRecordAndPropertyName
						.computeIfAbsent(record, idValue -> new HashMap<>())
						.put(column.getPropertyName(), value);
				onCellValueChanged.fire(new FieldValueChangedEventData(record, column, value));
			}
			case DtoTable.SortingChangedEvent.TYPE_ID -> {
				var sortingChangedEvent = event.as(DtoTable.SortingChangedEventWrapper.class);
				var sortField = sortingChangedEvent.getSortField();
				var sortDirection = SortDirection.fromUiSortDirection(sortingChangedEvent.getSortDirection());
				this.sorting = sortField != null && sortDirection != null ? new Sorting(sortField, sortDirection) : null;
				getModel().setSorting(sorting);
				onSortingChanged.fire(new SortingChangedEventData(sortingChangedEvent.getSortField(), SortDirection.fromUiSortDirection(sortingChangedEvent.getSortDirection())));
			}
			case DtoTable.DisplayedRangeChangedEvent.TYPE_ID -> {
				var d = event.as(DtoTable.DisplayedRangeChangedEventWrapper.class);
				try {
					handleScrollOrResize(ItemRange.startLength(d.getStartIndex(), d.getLength()));
				} catch (DuplicateEntriesException e) {
					// if the model returned a duplicate entry while scrolling, the underlying data apparently changed.
					// So try to refresh the whole data instead.
					LOGGER.warn("DuplicateEntriesException while retrieving data from model. This means the underlying data of the model has changed without the model notifying this component, so will refresh the whole data of this component.");
					refreshData();
				}
			}
			case DtoTable.FieldOrderChangeEvent.TYPE_ID -> {
				var fieldOrderChangeEvent = event.as(DtoTable.FieldOrderChangeEventWrapper.class);
				TableColumn<RECORD, ?> column = getColumnByPropertyName(fieldOrderChangeEvent.getColumnPropertyName());
				onColumnOrderChange.fire(new ColumnOrderChangeEventData<>(column, fieldOrderChangeEvent.getPosition()));
			}
			case DtoTable.ColumnSizeChangeEvent.TYPE_ID -> {
				var columnSizeChangeEvent = event.as(DtoTable.ColumnSizeChangeEventWrapper.class);
				TableColumn<RECORD, ?> column = getColumnByPropertyName(columnSizeChangeEvent.getColumnPropertyName());
				onColumnSizeChange.fire(new ColumnSizeChangeEventData<>(column, columnSizeChangeEvent.getSize()));
			}
			case DtoTable.ContextMenuRequestedEvent.TYPE_ID -> {
				var e = event.as(DtoTable.ContextMenuRequestedEventWrapper.class);
				lastSeenContextMenuRequestId = e.getRequestId();
				RECORD record = renderedRecords.getRecord(e.getRecordId());
				if (record != null && contextMenuProvider != null) {
					org.teamapps.ux.component.Component contextMenuContent = contextMenuProvider.apply(record);
					if (contextMenuContent != null) {
						sendCommandIfRendered(() -> new DtoInfiniteItemView.SetContextMenuContentCommand(e.getRequestId(), contextMenuContent.createDtoReference()));
					} else {
						sendCommandIfRendered(() -> new DtoInfiniteItemView.CloseContextMenuCommand(e.getRequestId()));
					}
				} else {
					closeContextMenu();
				}
			}

		}
	}

	private <VALUE> VALUE getCellValue(RECORD record, TableColumn<RECORD, VALUE> column) {
		Map<String, Object> changesForRecord = transientChangesByRecordAndPropertyName.getOrDefault(record, Collections.emptyMap());
		boolean changed = changesForRecord.containsKey(column.getPropertyName());
		Object cellValue;
		if (changed) { // associated value might be null!!
			cellValue = changesForRecord.get(column.getPropertyName());
		} else {
			cellValue = extractRecordProperty(record, column);
		}
		return (VALUE) cellValue;
	}

	private <VALUE> VALUE extractRecordProperty(RECORD record, TableColumn<RECORD, VALUE> column) {
		if (column.getValueExtractor() != null) {
			return column.getValueExtractor().extract(record);
		} else {
			return (VALUE) propertyProvider.getValues(record, Collections.singletonList(column.getPropertyName())).get(column.getPropertyName());
		}
	}

	private Map<String, Object> extractRecordProperties(RECORD record) {
		Map<Boolean, List<TableColumn<RECORD, ?>>> columnsWithAndWithoutValueExtractor = columns.stream().collect(Collectors.partitioningBy(c -> c.getValueExtractor() != null));
		Map<String, Object> valuesByPropertyName = new HashMap<>(propertyProvider.getValues(record, columnsWithAndWithoutValueExtractor.get(false).stream()
				.map(TableColumn::getPropertyName)
				.collect(Collectors.toList())));
		columnsWithAndWithoutValueExtractor.get(true).forEach(recordTableColumn -> valuesByPropertyName.put(recordTableColumn.getPropertyName(),
				recordTableColumn.getValueExtractor().extract(record)));
		return valuesByPropertyName;
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
		DtoIdentifiableClientRecord uiRecordIdOrNull = renderedRecords.getUiRecord(record);
		if (uiRecordIdOrNull != null) {
			final Object uiValue = getColumnByPropertyName(propertyName).getField().convertUxValueToUiValue(value);
			sendCommandIfRendered(() -> new DtoTable.SetCellValueCommand(uiRecordIdOrNull.getId(), propertyName, uiValue));
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
		DtoIdentifiableClientRecord uiRecordIdOrNull = renderedRecords.getUiRecord(record);
		if (uiRecordIdOrNull != null) {
			sendCommandIfRendered(() -> new DtoTable.MarkTableFieldCommand(uiRecordIdOrNull.getId(), propertyName, mark));
		}
	}

	public void clearRecordMarkings(RECORD record) {
		markedCells.remove(record);
		updateSingleRecordOnClient(record);
	}

	public void clearAllCellMarkings() {
		markedCells.clear();
		sendCommandIfRendered(() -> new DtoTable.ClearAllFieldMarkingsCommand());
	}

	// TODO implement using decider? more general formatting options?
	public void setRecordBold(RECORD record, boolean bold) {
		DtoIdentifiableClientRecord uiRecordIdOrNull = renderedRecords.getUiRecord(record);
		if (uiRecordIdOrNull != null) {
			sendCommandIfRendered(() -> new DtoTable.SetRecordBoldCommand(uiRecordIdOrNull.getId(), bold));
		}
	}

	public void setSelectedRecord(RECORD record) {
		setSelectedRecord(record, false);
	}

	public void setSelectedRecord(RECORD record, boolean scrollToRecordIfAvailable) {
		setSelectedRecords(record != null ? List.of(record) : List.of(), scrollToRecordIfAvailable);
	}

	public void setSelectedRecords(List<RECORD> records) {
		setSelectedRecords(records, false);
	}

	public void setSelectedRecords(List<RECORD> records, boolean scrollToFirstIfAvailable) {
		this.selectedRecords = records == null ? List.of() : List.copyOf(records);
		sendCommandIfRendered(() -> new DtoTable.SelectRecordsCommand(renderedRecords.getUiRecordIds(selectedRecords), scrollToFirstIfAvailable));
	}

	public void setSelectedRow(int rowIndex) {
		setSelectedRow(rowIndex, false);
	}

	public void setSelectedRow(int rowIndex, boolean scrollTo) {
		getRecordByRowIndex(rowIndex).ifPresentOrElse(record -> {
			this.selectedRecords = List.of(record);
			sendCommandIfRendered(() -> new DtoTable.SelectRowsCommand(List.of(rowIndex), scrollTo));
		}, () -> {
			this.selectedRecords = List.of();
			sendCommandIfRendered(() -> new DtoTable.SelectRowsCommand(List.of(), scrollTo));
		});
	}

	public void setSelectedRows(List<Integer> rowIndexes) {
		setSelectedRows(rowIndexes, false);
	}

	public void setSelectedRows(List<Integer> rowIndexes, boolean scrollToFirst) {
		this.selectedRecords = getRecordsByRowIndexes(rowIndexes);
		sendCommandIfRendered(() -> new DtoTable.SelectRowsCommand(rowIndexes, scrollToFirst));
	}

	private List<RECORD> getRecordsByRowIndexes(List<Integer> rowIndexes) {
		return rowIndexes.stream()
				.flatMap((Integer rowIndex) -> getRecordByRowIndex(rowIndex).stream())
				.collect(Collectors.toList());
	}

	private Optional<RECORD> getRecordByRowIndex(int rowIndex) {
		List<RECORD> records = getModel().getRecords(rowIndex, 1);
		if (records.size() >= 1) {
			if (records.size() > 1) {
				LOGGER.warn("Got multiple records from model when only asking for one! Taking the first one.");
			}
			return Optional.of(records.get(0));
		} else {
			LOGGER.error("Could not find record at row index {}", rowIndex);
			return Optional.empty();
		}
	}

	protected void updateColumnMessages(TableColumn<RECORD, ?> tableColumn) {
		sendCommandIfRendered(() -> new DtoTable.SetColumnMessagesCommand(tableColumn.getPropertyName(), tableColumn.getMessages().stream()
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

	public List<FieldMessage> validateRecord(RECORD record) {
		return validateRowInternal(record, false);
	}

	public List<FieldMessage> validateRow(RECORD record) {
		return validateRowInternal(record, true);
	}

	private List<FieldMessage> validateRowInternal(RECORD record, boolean considerChangedCellValues) {
		Map<String, Object> stringObjectMap;
		stringObjectMap = extractRecordProperties(record);
		if (considerChangedCellValues) {
			stringObjectMap.putAll(getChangedCellValues(record));
		}
		//noinspection unchecked
		return getColumns().stream()
				.flatMap(column -> column.getField().getValidators().stream()
						.flatMap(validator -> {
							List<FieldMessage> messages = (((FieldValidator) validator).validate(stringObjectMap.get(column.getPropertyName())));
							if (messages != null) {
								return messages.stream();
							} else {
								return Stream.empty();
							}
						}))
				.collect(Collectors.toList());
	}

	private void updateSingleCellMessages(RECORD record, String propertyName, List<FieldMessage> cellMessages) {
		DtoIdentifiableClientRecord uiRecordId = renderedRecords.getUiRecord(record);
		if (uiRecordId != null) {
			sendCommandIfRendered(() -> new DtoTable.SetSingleCellMessagesCommand(
					uiRecordId.getId(),
					propertyName,
					cellMessages.stream()
							.map(m -> m.createUiFieldMessage(FieldMessage.Position.POPOVER, FieldMessage.Visibility.ON_HOVER_OR_FOCUS))
							.collect(Collectors.toList())
			));
		}
	}


	protected void updateColumnVisibility(TableColumn<RECORD, ?> tableColumn) {
		sendCommandIfRendered(() -> new DtoTable.SetColumnVisibilityCommand(tableColumn.getPropertyName(), tableColumn.isVisible()));
	}

	// TODO #focus propagation
//	@Override
//	public void handleFieldFocused(AbstractField field) {
//		if (selectedRecord != null) {
//			queueCommandIfRendered(() -> new DtoTable.FocusCellCommand(clientRecordCache.getUiRecord(selectedRecord), field.getPropertyName()));
//		}
//	}

	public List<RECORD> getTopNonModelRecords() {
		return List.copyOf(topNonModelRecords);
	}

	public List<RECORD> getBottomNonModelRecords() {
		return List.copyOf(bottomNonModelRecords);
	}

	public List<RECORD> getNonModelRecords(boolean top) {
		return top ? getTopNonModelRecords() : getBottomNonModelRecords();
	}

	public void addTopNonModelRecord(RECORD record) {
		this.topNonModelRecords.add(0, record);
		refreshData();
	}

	public void addBottomNonModelRecord(RECORD record) {
		this.bottomNonModelRecords.add(record);
		refreshData();
	}

	public void addNonModelRecord(RECORD record, boolean addToTop) {
		if (addToTop) {
			addTopNonModelRecord(record);
		} else {
			addBottomNonModelRecord(record);
		}

	}

	public void removeTopNonModelRecord(RECORD record) {
		this.topNonModelRecords.remove(record);
		refreshData();
	}

	public void removeBottomNonModelRecord(RECORD record) {
		this.bottomNonModelRecords.remove(record);
		refreshData();
	}

	public void removeNonModelRecord(RECORD record) {
		this.topNonModelRecords.remove(record);
		this.bottomNonModelRecords.remove(record);
		refreshData();
	}

	public void removeNonModelRecord(RECORD record, boolean top) {
		if (top) {
			removeTopNonModelRecord(record);
		} else {
			removeBottomNonModelRecord(record);
		}
	}

	public void removeAllTopNonModelRecords() {
		this.topNonModelRecords.clear();
		refreshData();
	}

	public void removeAllBottomNonModelRecords() {
		this.topNonModelRecords.clear();
		refreshData();
	}

	public void removeAllNonModelRecords() {
		this.topNonModelRecords.clear();
		this.bottomNonModelRecords.clear();
		refreshData();
	}

	public void clearRecordMessages(RECORD record) {
		cellMessages.remove(record);
		updateSingleRecordOnClient(record);
	}

	public void updateRecordMessages(RECORD record, Map<String, List<FieldMessage>> messages) {
		cellMessages.put(record, new HashMap<>(messages));
		updateSingleRecordOnClient(record);
	}

	@Override
	protected void handleModelRecordsRemoved(RecordsRemovedEvent<RECORD> deleteEvent) {
		for (int i = Math.max(deleteEvent.getStart(), renderedRecords.getStartIndex()); i < Math.min(deleteEvent.getEnd(), renderedRecords.getEndIndex()); i++) {
			clearMetaDataForRecord(renderedRecords.getRecordByIndex(i));
		}
		super.handleModelRecordsRemoved(deleteEvent);
	}

	@Override
	protected void handleModelRecordsChanged(RecordsChangedEvent<RECORD> changeEvent) {
		for (int i = Math.max(changeEvent.getStart(), renderedRecords.getStartIndex()); i < Math.min(changeEvent.getEnd(), renderedRecords.getEndIndex()); i++) {
			clearMetaDataForRecord(renderedRecords.getRecordByIndex(i));
		}
		super.handleModelRecordsChanged(changeEvent);
	}

	private void clearMetaDataForRecord(RECORD record) {
		transientChangesByRecordAndPropertyName.remove(record);
		cellMessages.remove(record);
		markedCells.remove(record);
	}

	public void refreshData() {
		refresh();
	}

	@Override
	protected void sendUpdateDataCommandToClient(int start, List<Integer> uiRecordIds, List<DtoIdentifiableClientRecord> newUiRecords, int totalNumberOfRecords) {
		sendCommandIfRendered(() -> {
			LOGGER.debug("SENDING: renderedRange.start: {}; uiRecordIds.size: {}; renderedRecords.size: {}; newUiRecords.size: {}; totalCount: {}",
					start, uiRecordIds.size(), renderedRecords.size(), newUiRecords.size(), totalNumberOfRecords);
			return new DtoTable.UpdateDataCommand(
					start,
					uiRecordIds,
					(List) newUiRecords,
					totalNumberOfRecords
			);
		});
	}

	private int getTotalRecordsCount() {
		return getModelCount() + topNonModelRecords.size() + bottomNonModelRecords.size();
	}

	protected List<RECORD> retrieveRecords(int startIndex, int length) {
		if (startIndex < 0 || length < 0) {
			LOGGER.warn("Data coordinates do not make sense: startIndex {}, length {}", startIndex, length);
			return Collections.emptyList();
		}

		int endIndex = startIndex + length;
		int totalTopRecords = topNonModelRecords.size();
		int totalModelRecords = getModelCount();
		int totalBottomRecords = bottomNonModelRecords.size();

		if (endIndex > totalTopRecords + totalModelRecords + totalBottomRecords) {
			endIndex = Math.max(totalTopRecords + totalModelRecords + totalBottomRecords, startIndex);
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
		List<RECORD> records = getModel().getRecords(startIndex, length);
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


	private void applyTransientChangesToClientRecord(DtoTableClientRecord uiRecord) {
		Map<String, Object> changes = transientChangesByRecordAndPropertyName.get(renderedRecords.getRecord(uiRecord.getId()));
		if (changes != null) {
			changes.forEach((key, value) -> uiRecord.getValues().put(key, getColumnByPropertyName(key).getField().convertUxValueToUiValue(value)));
		}
	}

	public void cancelEditing() {
		TableCellCoordinates<RECORD> activeEditorCell = getActiveEditorCell();
		DtoIdentifiableClientRecord uiRecord = renderedRecords.getUiRecord(activeEditorCell.getRecord());
		if (uiRecord != null) {
			sendCommandIfRendered(() -> new DtoTable.CancelEditingCellCommand(uiRecord.getId(), activeEditorCell.getPropertyName()));
		}
	}

	private Map<String, List<DtoFieldMessage>> createUiFieldMessagesForRecord(Map<String, List<FieldMessage>> recordFieldMessages) {
		return recordFieldMessages.entrySet().stream()
				.collect(Collectors.toMap(
						entry -> entry.getKey(),
						entry -> entry.getValue().stream()
								.map(fieldMessage -> fieldMessage.createUiFieldMessage(FieldMessage.Position.POPOVER, FieldMessage.Visibility.ON_HOVER_OR_FOCUS))
								.collect(Collectors.toList())
				));
	}

	@Override
	protected DtoIdentifiableClientRecord createUiIdentifiableClientRecord(RECORD record) {
		DtoTableClientRecord clientRecord = new DtoTableClientRecord();
		clientRecord.setId(++clientRecordIdCounter);
		Map<String, Object> uxValues = extractRecordProperties(record);
		Map<String, Object> uiValues = columns.stream()
				.collect(HashMap::new, (map, column) -> map.put(column.getPropertyName(), ((AbstractField) column.getField()).convertUxValueToUiValue(uxValues.get(column.getPropertyName()))), HashMap::putAll);
		clientRecord.setValues(uiValues);
		clientRecord.setSelected(selectedRecords.stream().anyMatch(r -> customEqualsAndHashCode.getEquals().test(r, record)));
		clientRecord.setMessages(createUiFieldMessagesForRecord(cellMessages.getOrDefault(record, Collections.emptyMap())));
		clientRecord.setMarkings(new ArrayList<>(markedCells.getOrDefault(record, Collections.emptySet())));
		applyTransientChangesToClientRecord(clientRecord);
		return clientRecord;
	}

	public List<TableColumn<RECORD, ?>> getColumns() {
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
		this.setCssStyle(".slick-cell", "border-color", rowBorderColor != null ? rowBorderColor.toHtmlColorString() : null);
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

	public boolean isTextSelectionEnabled() {
		return textSelectionEnabled;
	}

	public void setTextSelectionEnabled(boolean textSelectionEnabled) {
		this.textSelectionEnabled = textSelectionEnabled;
		reRenderIfRendered();
	}

	public Sorting getSorting() {
		return sorting;
	}

	public void setSorting(String sortField, SortDirection sortDirection) {
		setSorting(sortField != null && sortDirection != null ? new Sorting(sortField, sortDirection) : null);
	}

	public void setSorting(Sorting sorting) {
		this.sorting = sorting;
		TableModel<RECORD> model = getModel();
		if (model != null) {
			model.setSorting(sorting);
		}
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

	public <VALUE> TableColumn<RECORD, VALUE> getColumnByPropertyName(String propertyName) {
		return columns.stream()
				.filter(column -> column.getPropertyName().equals(propertyName))
				.map(c -> (TableColumn<RECORD, VALUE>) c)
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
		Map<String, Object> values = extractRecordProperties(record);
		values.putAll(transientChangesByRecordAndPropertyName.getOrDefault(record, Collections.emptyMap()));
		return values;
	}

	public void clearChangeBuffer() {
		transientChangesByRecordAndPropertyName.clear();
	}

	public void applyCellValuesToRecord(RECORD record) {
		Map<String, Object> changedCellValues = getChangedCellValues(record);
		changedCellValues.forEach((propertyName, value) -> {
			ValueInjector<RECORD, Object> columnValueInjector = getColumnByPropertyName(propertyName).getValueInjector();
			if (columnValueInjector != null) {
				columnValueInjector.inject(record, value);
			} else {
				propertyInjector.setValue(record, propertyName, value);
			}
		});
	}

	public void revertChanges() {
		transientChangesByRecordAndPropertyName.clear();
		refreshData();
	}

	public RECORD getSelectedRecord() {
		return selectedRecords.isEmpty() ? null : selectedRecords.get(selectedRecords.size() - 1);
	}

	public List<RECORD> getSelectedRecords() {
		return selectedRecords;
	}

	public PropertyProvider<RECORD> getPropertyProvider() {
		return propertyProvider;
	}

	public void setPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void setPropertyExtractor(PropertyExtractor<RECORD> propertyExtractor) {
		this.setPropertyProvider(propertyExtractor);
	}

	public PropertyInjector<RECORD> getPropertyInjector() {
		return propertyInjector;
	}

	public void setPropertyInjector(PropertyInjector<RECORD> propertyInjector) {
		this.propertyInjector = propertyInjector;
	}

	public Function<RECORD, org.teamapps.ux.component.Component> getContextMenuProvider() {
		return contextMenuProvider;
	}

	public void setContextMenuProvider(Function<RECORD, org.teamapps.ux.component.Component> contextMenuProvider) {
		this.contextMenuProvider = contextMenuProvider;
	}

	public void closeContextMenu() {
		sendCommandIfRendered(() -> new DtoInfiniteItemView.CloseContextMenuCommand(this.lastSeenContextMenuRequestId));
	}

}
