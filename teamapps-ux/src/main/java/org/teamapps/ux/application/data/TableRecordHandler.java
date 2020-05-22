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
package org.teamapps.ux.application.data;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.event.Event;
import org.teamapps.icon.material.MaterialIcon;
import org.teamapps.ux.application.filter.FilterProvider;
import org.teamapps.ux.application.validation.RecordChangeSet;
import org.teamapps.ux.application.view.View;
import org.teamapps.ux.application.validation.ValidationResult;
import org.teamapps.ux.component.field.TextField;
import org.teamapps.ux.component.itemview.SimpleItemGroup;
import org.teamapps.ux.component.itemview.SimpleItemView;
import org.teamapps.ux.component.table.Table;
import org.teamapps.ux.component.table.TableColumn;
import org.teamapps.ux.component.template.BaseTemplate;
import org.teamapps.ux.component.template.BaseTemplateRecord;
import org.teamapps.ux.component.toolbar.ToolbarButton;
import org.teamapps.ux.component.toolbar.ToolbarButtonGroup;
import org.teamapps.ux.session.CurrentSessionContext;
import org.teamapps.ux.session.SessionContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TableRecordHandler<RECORD> {

	public Event<RECORD> onRecordSaved = new Event<>();
	public Event<RECORD> onRecordSavingFailed = new Event<>();

	private final Table<RECORD> table;
	private UpdateMode updateMode = UpdateMode.NOT_EDITABLE;
	private boolean emptyRecordsOnTop;
	private boolean ensureEmptyRecordRow;
	private RecordEditableDecider<RECORD> recordEditableDecider;
	private RecordDeletableDecider<RECORD> recordDeletableDecider;
	private RecordValidator<RECORD> recordValidator;
	private RecordUpdateHandler<RECORD> recordUpdateHandler;
	private RecordDeletionHandler<RECORD> recordDeletionHandler;

	private Supplier<RECORD> emptyRecordSupplier;
	private FilterProvider<RECORD> filterProvider;

	private Set<RECORD> uncommittedEmptyRecords = new HashSet<>();
	private RECORD previouslySelectedRecord;
	private String previouslyUpdatedProperty;

	private ToolbarButton buttonAdd;
	private ToolbarButton buttonSave;
	private ToolbarButton buttonDiscardChanges;
	private ToolbarButton buttonDelete;
	private ToolbarButton buttonMultiDeleteRecord;
	private ToolbarButton buttonDisplayedColumns;
	private ToolbarButton buttonColumnWidth;
	private ToolbarButton buttonFilter;
	private ToolbarButton buttonOptions;
	private SessionContext context;



	protected TableRecordHandler(Table<RECORD> table, UpdateMode updateMode, boolean emptyRecordsOnTop, boolean ensureEmptyRecordRow, RecordEditableDecider<RECORD> recordEditableDecider, RecordDeletableDecider<RECORD> recordDeletableDecider, RecordValidator<RECORD> recordValidator, RecordUpdateHandler<RECORD> recordUpdateHandler, RecordDeletionHandler<RECORD> recordDeletionHandler, RecordHandler<RECORD> recordHandler, Supplier<RECORD> emptyRecordSupplier, FilterProvider<RECORD> filterProvider, View view) {
		this.table = table;
		this.updateMode = updateMode;
		this.filterProvider = filterProvider;
		this.emptyRecordsOnTop = emptyRecordsOnTop;
		this.emptyRecordSupplier = emptyRecordSupplier;
		setEnsureEmptyRecordRow(ensureEmptyRecordRow);

		this.recordEditableDecider = recordEditableDecider;
		this.recordDeletableDecider = recordDeletableDecider;
		this.recordValidator = recordValidator;
		this.recordUpdateHandler = recordUpdateHandler;
		this.recordDeletionHandler = recordDeletionHandler;
		if (recordHandler != null) {
			setRecordHandler(recordHandler);
		}
		init();
		if (view != null) {
			updateView(view);
		}
	}

	public void setEnsureEmptyRecordRow(boolean ensureEmptyRecordRow) {
		if (emptyRecordSupplier == null) {
			return;
		}
		this.ensureEmptyRecordRow = ensureEmptyRecordRow;
		if (ensureEmptyRecordRow) {
			RECORD emptyRecord = emptyRecordSupplier.get();
			table.addNonModelRecord(emptyRecord, emptyRecordsOnTop);
		} else {
			table.removeAllNonModelRecords();
		}
	}

	private void setRecordHandler(RecordHandler<RECORD> recordHandler) {
		this.recordEditableDecider = recordHandler;
		this.recordDeletableDecider = recordHandler;
		this.recordValidator = recordHandler;
		this.recordUpdateHandler = recordHandler;
		this.recordDeletionHandler = recordHandler;
	}


	private void init() {
		context = CurrentSessionContext.get();
		createCRUDButtons();
		createTableOptions();
	}

	private void createCRUDButtons() {
		buttonAdd = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.ADD_CIRCLE, context.getLocalized("dict.add"), context.getLocalized("dict.addNewRecord"))).setVisible(false);
		buttonSave = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.SAVE, context.getLocalized("dict.save"))).setVisible(false);
		buttonDiscardChanges = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.UNDO, context.getLocalized("dict.discard"))).setVisible(false);
		buttonDelete = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.DELETE, context.getLocalized("dict.delete"))).setVisible(false);
		buttonMultiDeleteRecord = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.DELETE_FOREVER, context.getLocalized("dict.deleteAll"))).setVisible(false);

		if (emptyRecordSupplier != null) {
			buttonAdd.setVisible(true);
			buttonAdd.onClick.addListener(toolbarButtonClickEvent -> {
				checkEmptyRecordRow();
			});
			checkEmptyRecordRow();
		}


		table.onCellValueChanged.addListener(fieldChangeEventData -> {
			if (updateMode == UpdateMode.NOT_EDITABLE) {
				return;
			}
			RECORD record = fieldChangeEventData.getRecord();
			String propertyName = fieldChangeEventData.getPropertyName();
			table.setCellMarked(record, propertyName, true);
			handleRecordChange(record, propertyName, updateMode == UpdateMode.AUTO_SAVE_ANY_CELL);

			if (updateMode == UpdateMode.SAVE_ON_USER_REQUEST) {
				buttonSave.setVisible(true);
				buttonDiscardChanges.setVisible(true);
			}
			previouslyUpdatedProperty = propertyName;
		});

		table.onRowSelected.addListener(record -> {
			buttonMultiDeleteRecord.setVisible(false);
			buttonDelete.setVisible(isRecordDeletable(record));
			if (updateMode == UpdateMode.AUTO_SAVE_ON_ROW_CHANGE) {
				List<RECORD> changedRecords = table.getRecordsWithChangedCellValues();
				for (RECORD changedRecord : changedRecords) {
					handleRecordChange(changedRecord, null, true);
				}
			}
			previouslySelectedRecord = record;
		});

		table.onMultipleRowsSelected.addListener(records -> {
			buttonDelete.setVisible(false);
			if (records.stream().allMatch(record -> isRecordDeletable(record))) {
				buttonMultiDeleteRecord.setVisible(true);
			} else {
				buttonMultiDeleteRecord.setVisible(false);
			}
		});

		buttonDiscardChanges.onClick.addListener(toolbarButtonClickEvent -> {
			table.clearChangeBuffer();
			table.clearAllCellMarkings();
			table.refreshData();
			buttonSave.setVisible(false);
			buttonDiscardChanges.setVisible(false);
		});

		buttonSave.onClick.addListener(toolbarButtonClickEvent -> {
			List<RECORD> changedRecords = table.getRecordsWithChangedCellValues();
			for (RECORD record : changedRecords) {
				handleRecordChange(record, null, true);
			}
		});


		buttonDelete.onClick.addListener(toolbarButtonClickEvent -> {
			recordDeletionHandler.deleteRecord(table.getSelectedRecord());
			//todo: check for unsaved data!
			table.getModel().onAllDataChanged().fire(null);
		});

		buttonMultiDeleteRecord.onClick.addListener(toolbarButtonClickEvent -> {
			if (recordDeletableDecider != null) {
				List<RECORD> records = table.getSelectedRecords();
				records.forEach(record -> recordDeletionHandler.deleteRecord(table.getSelectedRecord()));
				//todo: check for unsaved data!
				table.getModel().onAllDataChanged().fire(null);
			}
		});


	}

	private boolean isRecordDeletable(RECORD record) {
		return recordDeletableDecider != null && recordDeletableDecider.isRecordDeletable(record);
	}

	private void handleRecordChange(RECORD record, String lastUpdatedProperty, boolean saveRecord) {
		RecordChangeSet<RECORD> changeSet = new RecordChangeSet<>(record, table.getChangedCellValues(record));
		if (!changeSet.isChanged()) {
			return;
		}
		ValidationResult validationResult = recordValidator.validateRecordUpdate(changeSet);

		if (saveRecord && validationResult.isSuccess()) {
			RECORD updateRecord = recordUpdateHandler.updateRecord(changeSet);
			table.getModel().onRecordUpdated().fire(updateRecord);

			if (uncommittedEmptyRecords.contains(record)) {
				uncommittedEmptyRecords.remove(record);
				checkEmptyRecordRow();
			}
			onRecordSaved.fire(record);
		} else if (saveRecord && !validationResult.isSuccess()) {
			onRecordSavingFailed.fire(record);
		}
		table.updateRecordMessages(record, validationResult.getFieldMessagesMap());

		if (saveRecord && !validationResult.isSuccess() && lastUpdatedProperty != null) {
			table.focusCell(record, lastUpdatedProperty);
		}
	}

	private void checkEmptyRecordRow() {
		if (ensureEmptyRecordRow) {
			RECORD emptyRecord = emptyRecordSupplier.get();
			uncommittedEmptyRecords.add(emptyRecord);
			table.addNonModelRecord(emptyRecord, emptyRecordsOnTop);
		}
	}

	private void createTableOptions() {
		buttonDisplayedColumns = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.ADD, context.getLocalized("dict.columns"))).setVisible(true);
		buttonColumnWidth = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.ADD, context.getLocalized("dict.columnWidth"))).setVisible(true);
		buttonFilter = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.ADD, context.getLocalized("dict.filter"))).setVisible(true);
		buttonOptions = new ToolbarButton(BaseTemplate.TOOLBAR_BUTTON_TINY, new BaseTemplateRecord(MaterialIcon.ADD, context.getLocalized("dict.options"))).setVisible(true);

		SimpleItemView<?> itemView = new SimpleItemView<>();
		buttonDisplayedColumns.setDropDownComponent(itemView);
		SimpleItemGroup<?> itemGroup = itemView.addSingleColumnGroup(MaterialIcon.ADD, context.getLocalized("dict.baseSettings"));
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.defaultColumns"), context.getLocalized("dict.displayTheDefaultColumns")).onClick.addListener(o -> {
			//todo: implement
		});
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.allColumns"), context.getLocalized("dict.displayAllColumns")).onClick.addListener(o -> {
			table.getColumns().stream().map(TableColumn::getField).forEach(field -> {
				if (!field.isVisible()) {
					field.setVisible(true);
				}
			});
		});
		itemGroup = itemView.addSingleColumnGroup(MaterialIcon.ADD, context.getLocalized("dict.selectColumnToDisplayOrHide"));
		for (TableColumn column : table.getColumns()) {
			itemGroup.addItem(MaterialIcon.ADD, column.getTitle(), context.getLocalized("dict.setVisibilityOfColumn", column.getTitle())).onClick.addListener(o -> {
				column.getField().setVisible(!column.getField().isVisible());
			});
		}

		itemView = new SimpleItemView();
		buttonColumnWidth.setDropDownComponent(itemView);
		itemGroup = itemView.addSingleColumnGroup(MaterialIcon.ADD, context.getLocalized("dict.columnWidth"));
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.autoFit"), context.getLocalized("dict.setAllColumnWidthsToTheWidthOfTheTable")).onClick.addListener(o -> {
			table.setForceFitWidth(true);
		});
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.standardWidth"), context.getLocalized("dict.setColumnWidthsToTheDefaultWidths")).onClick.addListener(o -> {
			table.setForceFitWidth(false);
		});

		itemGroup = itemView.addSingleColumnGroup(MaterialIcon.ADD, context.getLocalized("dict.automaticColumnDisplay"));
		//todo: implement
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.hideEmptyColumns"), context.getLocalized("dict.hideAllColumnsThatDoNotContainAnyData"));
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.displayEmptyColumns"), context.getLocalized("dict.displayAllColumnsEventEmptyOnes"));

		itemGroup = itemView.addSingleColumnGroup(MaterialIcon.ADD, context.getLocalized("dict.changeWidth"));
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.decreaseColumnWidths"), context.getLocalized("dict.decreaseTheWidthsOfAllColumns")).onClick.addListener(o -> {
			for (TableColumn column : table.getColumns()) {
				column.setDefaultWidth((int) (column.getDefaultWidth() / 1.5));
			}
			if (table.isForceFitWidth()) {
				table.setForceFitWidth(false);
			} else {
				table.reRenderIfRendered();
			}
		});
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.increaseColumnWidths"), context.getLocalized("dict.increaseTheWidthsOfAllColumns")).onClick.addListener(o -> {
			for (TableColumn column : table.getColumns()) {
				column.setDefaultWidth((int) (column.getDefaultWidth() * 1.5));
			}
			if (table.isForceFitWidth()) {
				table.setForceFitWidth(false);
			} else {
				table.reRenderIfRendered();
			}
		});

		itemView = new SimpleItemView();
		buttonOptions.setDropDownComponent(itemView);
		itemGroup = itemView.addSingleColumnGroup(MaterialIcon.ADD, context.getLocalized("dict.tableOptions"));
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.shadedRows"), context.getLocalized("dict.displayTableWithShadedRows")).onClick.addListener(o -> {
			table.setStripedRows(!table.isStripedRows());
		});
		itemGroup.addItem(MaterialIcon.ADD, context.getLocalized("dict.showNumbering"), context.getLocalized("dict.displayRowNumbers")).onClick.addListener(o -> {
			table.setShowNumbering(!table.isShowNumbering());
		});

		//itemGroup = itemView.addSingleColumnGroup(MaterialIcon.ADD, context.getLocalized("dict.editOptions"));

		buttonFilter.onClick.addListener(toolbarButtonClickEvent -> {
			if (table.isShowHeaderRow()) {
				table.setShowHeaderRow(false);
				if (filterProvider != null) {
					filterProvider.clearTablePropertyFilters();
				}
			} else {
				if (table.getHeaderRowFields().isEmpty()) {
					table.setHeaderRowFields(table.getColumns().stream()
							.collect(Collectors.toMap(col -> col.getPropertyName(), col -> {
								String propertyName = col.getPropertyName();
								TextField field = new TextField();
								field.setEmptyText(context.getLocalized("dict.filter") + "...");
								field.onTextInput.addListener(s -> {
									if (filterProvider == null) {
										return;
									}
									if (StringUtils.isBlank(s)) {
										filterProvider.setTablePropertyFilter(propertyName, null);
									} else {
										filterProvider.setTablePropertyFilter(propertyName, s);
									}
								});
								return field;
							})));
				}
				table.setShowHeaderRow(true);
			}
		});
	}


	public List<ToolbarButtonGroup> getToolbarButtonGroups() {
		List<ToolbarButtonGroup> groups = new ArrayList<>();
		ToolbarButtonGroup group = new ToolbarButtonGroup();
		groups.add(group);
		group.addButton(buttonAdd);
		group.addButton(buttonSave);
		group.addButton(buttonDiscardChanges);
		group.addButton(buttonDelete);
		group.addButton(buttonMultiDeleteRecord);
		group = new ToolbarButtonGroup();
		groups.add(group);
		group.addButton(buttonDisplayedColumns);
		group.addButton(buttonFilter);
		group.addButton(buttonOptions);
		return groups;
	}

	private void updateView(View view) {
		if (!table.equals(view.getComponent())) {
			return;
		}
		getToolbarButtonGroups().forEach(group -> view.addLocalButtonGroup(group));
	}
}
