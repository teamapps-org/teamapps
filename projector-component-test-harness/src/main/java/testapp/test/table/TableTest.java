

package testapp.test.table;

import org.apache.commons.lang3.StringUtils;
import org.teamapps.commons.util.ReflectionUtil;
import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.core.field.*;
import org.teamapps.projector.component.field.FieldMessage;
import org.teamapps.projector.component.field.FieldMessagePosition;
import org.teamapps.projector.component.field.FieldMessageSeverity;
import org.teamapps.projector.component.field.FieldMessageVisibility;
import org.teamapps.projector.component.filefield.FileField;
import org.teamapps.projector.component.gridform.ResponsiveFormLayout;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.RecordsAddedEvent;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.RecordsChangedEvent;
import org.teamapps.projector.component.infinitescroll.infiniteitemview.RecordsRemovedEvent;
import org.teamapps.projector.component.infinitescroll.recordcache.EqualsAndHashCode;
import org.teamapps.projector.component.infinitescroll.recordcache.ItemRange;
import org.teamapps.projector.component.infinitescroll.table.AbstractTableModel;
import org.teamapps.projector.component.infinitescroll.table.SelectionFrame;
import org.teamapps.projector.component.infinitescroll.table.SortDirection;
import org.teamapps.projector.component.infinitescroll.table.Table;
import org.teamapps.projector.component.treecomponents.combobox.ComboBox;
import org.teamapps.projector.component.treecomponents.combobox.TagComboBox;
import org.teamapps.projector.component.treecomponents.datetime.InstantDateTimeField;
import org.teamapps.projector.component.treecomponents.datetime.LocalDateField;
import org.teamapps.projector.component.treecomponents.datetime.LocalDateTimeField;
import org.teamapps.projector.component.treecomponents.datetime.LocalTimeField;
import org.teamapps.projector.component.treecomponents.itemview.ItemView;
import org.teamapps.projector.component.treecomponents.money.CurrencyField;
import org.teamapps.projector.component.treecomponents.money.value.CurrencyUnit;
import org.teamapps.projector.component.treecomponents.money.value.CurrencyValue;
import org.teamapps.projector.format.TextAlignment;
import org.teamapps.projector.icon.composite.CompositeIcon;
import org.teamapps.projector.icon.material.MaterialIcon;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplateRecord;
import org.teamapps.projector.template.grid.basetemplates.BaseTemplates;
import testapp.AbstractComponentTest;
import testapp.ComponentTestContext;
import testapp.ConfigurationFieldGenerator;
import testapp.util.DemoComponentsGenerator;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TableTest extends AbstractComponentTest<Table<TableTestRecord>> {

	private Table<TableTestRecord> table;

	private boolean showSelectionFrame;
	SelectionFrame selectionFrame = new SelectionFrame();
	private TableTestTableModel model;

	private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	public TableTest(ComponentTestContext testContext) {
		super(testContext);
	}

	@Override
	protected void addFieldsToParametersForm(ResponsiveFormLayout responsiveFormLayout) {
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Configuration").setGridGap(5);
		ConfigurationFieldGenerator fieldGenerator = new ConfigurationFieldGenerator(getComponent(), getTestContext());

		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Hide headers", fieldGenerator.createCheckBox("hideHeaders"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Header row", fieldGenerator.createCheckBox("showHeaderRow"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Header row", fieldGenerator.createCheckBox("showFooterRow"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Display as list", fieldGenerator.createCheckBox("displayAsList"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Force fit width", fieldGenerator.createCheckBox("forceFitWidth"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Row height", fieldGenerator.createNumberField("rowHeight", 0, 1, 1000, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Striped rows", fieldGenerator.createCheckBox("stripedRows"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Even row color", fieldGenerator.createColorPicker("stripedRowColorEven"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Odd row color", fieldGenerator.createColorPicker("stripedRowColorOdd"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Allow multi-row selection", fieldGenerator.createCheckBox("allowMultiRowSelection"));
		responsiveFormLayout.addLabelAndField(CompositeIcon.of(MaterialIcon.HELP, MaterialIcon.HELP), "Selection color", fieldGenerator.createColorPicker("selectionColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Row border width", fieldGenerator.createNumberField("rowBorderWidth", 0, 0, 100, false));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Row border color", fieldGenerator.createColorPicker("rowBorderColor"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show selection check boxes", fieldGenerator.createCheckBox("showRowCheckBoxes"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show numbering column", fieldGenerator.createCheckBox("showNumbering"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Editable", fieldGenerator.createCheckBox("editable"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Ensure empty last row", fieldGenerator.createCheckBox("ensureEmptyLastRow"));
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Text selection", fieldGenerator.createCheckBox("textSelectionEnabled"));

		responsiveFormLayout.addSection(MaterialIcon.HELP, "Commands").setGridGap(5);
		Button refreshButton = Button.create("refreshData()");
		refreshButton.onClick.addListener(() -> getComponent().refreshData());
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Refresh data", refreshButton);

		Button onAllDataChangedButton = Button.create("onAllDataChanged from model");
		onAllDataChangedButton.onClick.addListener(() -> model.fireOnAllDataChanged());
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Fire onAllDataChanged", onAllDataChangedButton);

		Button addRecordButton = Button.create("add record");
		addRecordButton.onClick.addListener(() -> model.addRecord(true));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Add record", addRecordButton);

		Button addRecordWithoutEventButton = Button.create("add record without firing event");
		addRecordWithoutEventButton.onClick.addListener(() -> model.addRecord(false));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Add record without event", addRecordWithoutEventButton);

		Button removeRecordButton = Button.create("remove record");
		removeRecordButton.onClick.addListener(() -> model.removeRecord());
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Remove record", removeRecordButton);

		Button selectRecordButton = Button.create("select record with id 3");
		selectRecordButton.onClick.addListener(() -> getComponent().setSelectedRecord(model.getRecordById(3)));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "select record with id 3", selectRecordButton);

		Button selectRecordButton500 = Button.create("select record with id 500");
		selectRecordButton500.onClick.addListener(() -> getComponent().setSelectedRecord(model.getRecordById(500), true));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "select record with id 500", selectRecordButton500);

		Button selectRecordButton499500 = Button.create("select record with ids 499+500");
		selectRecordButton499500.onClick.addListener(() -> getComponent().setSelectedRecords(List.of(model.getRecordById(499), model.getRecordById(500)), true));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "select record with id 499+500", selectRecordButton499500);

		Button selectRowButton = Button.create("select row at index 4 (no scrolling");
		selectRowButton.onClick.addListener(() -> getComponent().setSelectedRow(4));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "select row at index 4", selectRowButton);

		Button selectRowButton501 = Button.create("select row at index 501");
		selectRowButton501.onClick.addListener(() -> getComponent().setSelectedRow(501, true));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "select row at index 501", selectRowButton501);

		Button selectRowButton501502 = Button.create("select row at index 501 + 502");
		selectRowButton501502.onClick.addListener(() -> getComponent().setSelectedRows(List.of(501, 502), true));
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "select row at index 501 + 502", selectRowButton501502);

		AtomicInteger columnNameCounter = new AtomicInteger(0);
		Button addColumnButton = Button.create("addColumn");
		addColumnButton.onClick.addListener(() -> {
			String columnName = "foo" + columnNameCounter.incrementAndGet();
			getComponent().addColumn(columnName, columnName, new TextField());
			getComponent().setHeaderRowField(columnName, new TextField());
			getComponent().setFooterRowField(columnName, new TextField());
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "add column", addColumnButton);

		Button addCellMessagesButton = Button.create("add cell messages");
		addCellMessagesButton.onClick.addListener(() -> {
			List<TableTestRecord> someRecords = model.getRecords(0, 10);
			table.addCellMessage(someRecords.get(0), "textField", new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.ERROR, "This is"
					+ " an error message"));
			table.addCellMessage(someRecords.get(1), "textField", new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.WARNING, "This "
					+ "is a warning message"));
			table.addCellMessage(someRecords.get(1), "numberField", new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.ERROR, "This "
					+ "is an error message"));
			table.addCellMessage(someRecords.get(2), "numberField", new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.WARNING,
					"This is a warning message"));
			table.addCellMessage(someRecords.get(3), "numberField", new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.SUCCESS,
					"This is a success message"));
			table.addCellMessage(someRecords.get(4), "numberField", new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.INFO, "This "
					+ "is an info message"));
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Add a cell message", addCellMessagesButton);

		Button udpateSingleCellMessagesButton = Button.create("update record messages");
		udpateSingleCellMessagesButton.onClick.addListener(() -> {
			table.updateRecordMessages(model.getRecords(11, 1).get(0), Collections.singletonMap("textField", Collections.singletonList(new FieldMessage(FieldMessageSeverity.SUCCESS, "record "
					+ "success..."))));
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "update record messages", udpateSingleCellMessagesButton);

		Button markCellsButton = Button.create("Mark cells");
		markCellsButton.onClick.addListener(() -> {
			List<TableTestRecord> someRecords = model.getRecords(0, 10);
			table.setCellMarked(someRecords.get(0), "textField", true);
			table.setCellMarked(someRecords.get(1), "numberField", true);
			table.setCellMarked(someRecords.get(2), "multiLineTextField", true);
			table.setCellMarked(someRecords.get(3), "passwordField", true);
			table.setCellMarked(someRecords.get(4), "richTextEditor", true);
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Mark cells", markCellsButton);

		Button unmarkCellsButton = Button.create("Unmark cells");
		unmarkCellsButton.onClick.addListener(() -> {
			List<TableTestRecord> someRecords = model.getRecords(0, 10);
			table.setCellMarked(someRecords.get(0), "textField", false);
			table.setCellMarked(someRecords.get(1), "numberField", false);
			table.setCellMarked(someRecords.get(2), "multiLineTextField", false);
			table.setCellMarked(someRecords.get(3), "passwordField", false);
			table.setCellMarked(someRecords.get(4), "richTextEditor", false);
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Unmark cells", unmarkCellsButton);

		Button addColumnMessagesButton = Button.create("add column message");
		addColumnMessagesButton.onClick.addListener(() -> {
			table.getColumnByPropertyName("multiLineTextField").setMessages(Arrays.asList(
					new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.INFO, "This is a column info message"),
					new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.ERROR, "This is a column error message")
			));
			table.getColumnByPropertyName("passwordField").setMessages(Arrays.asList(
					new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_FOCUS, FieldMessageSeverity.INFO, "This is a column info message")
			));
			table.getColumnByPropertyName("numberField").setMessages(Arrays.asList(
					new FieldMessage(FieldMessagePosition.POPOVER, FieldMessageVisibility.ON_HOVER_OR_FOCUS, FieldMessageSeverity.WARNING, "This is a column warning message")
			));
		});
		responsiveFormLayout.addLabelAndComponent(MaterialIcon.HELP, "Add a column message", addColumnMessagesButton);

		

		// ========= Selection Frame =========
		responsiveFormLayout.addSection(MaterialIcon.HELP, "Selection frame");

		CheckBox showSelectionFrameCheckBox = new CheckBox("Show selection frame");
		showSelectionFrameCheckBox.setValue(showSelectionFrame);
		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Show selection frame", showSelectionFrameCheckBox);

//		CheckBox selectionFrameFullRowCheckBox = new CheckBox("Full row");
//		selectionFrameFullRowCheckBox.onValueChanged.addListener(selectionFrameFullRow -> {
//			this.selectionFrame.setFullRow(selectionFrameFullRow);
//			printInvocationToConsole("setSelectionFrame", selectionFrame);
//			table.setSelectionFrame(this.selectionFrame);
//		});
//		selectionFrameFullRowCheckBox.setValue(this.selectionFrame.isFullRow());
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Full row", selectionFrameFullRowCheckBox);

//		ColorPicker selectionFrameColorPicker = new ColorPicker();
//		selectionFrameColorPicker.onValueChanged.addListener(color -> {
//			this.selectionFrame.setColor(color);
//			printInvocationToConsole("setSelectionFrame", this.selectionFrame);
//			table.setSelectionFrame(this.selectionFrame);
//		});
//		selectionFrameColorPicker.setValue(this.selectionFrame.getColor());
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Color", selectionFrameColorPicker);
//
//		NumberField selectionFrameBorderWidthSlider = new NumberField(0);
//		selectionFrameBorderWidthSlider.setMinValue(0);
//		selectionFrameBorderWidthSlider.setMaxValue(50);
//		selectionFrameBorderWidthSlider.onValueChanged.addListener(selectionFrameBorderWidth -> {
//			this.selectionFrame.setBorderWidth(selectionFrameBorderWidth.intValue());
//			printInvocationToConsole("setSelectionFrame", this.selectionFrame);
//			table.setSelectionFrame(this.selectionFrame);
//		});
//		selectionFrameBorderWidthSlider.setValue(this.selectionFrame.getBorderWidth());
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Row border width", selectionFrameBorderWidthSlider);
//
//		NumberField selectionFrameGlowingWidthSlider = new NumberField(0);
//		selectionFrameGlowingWidthSlider.setMinValue(0);
//		selectionFrameGlowingWidthSlider.setMaxValue(100);
//		selectionFrameGlowingWidthSlider.onValueChanged.addListener(selectionFrameGlowingWidth -> {
//			this.selectionFrame.setGlowingWidth(selectionFrameGlowingWidth.intValue());
//			printInvocationToConsole("setSelectionFrame", this.selectionFrame);
//			table.setSelectionFrame(this.selectionFrame);
//		});
//		selectionFrameGlowingWidthSlider.setValue(this.selectionFrame.getGlowingWidth());
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Glowing width", selectionFrameGlowingWidthSlider);
//
//		NumberField selectionFrameShadowWidthSlider = new NumberField(0);
//		selectionFrameShadowWidthSlider.setMinValue(0);
//		selectionFrameShadowWidthSlider.setMaxValue(100);
//		selectionFrameShadowWidthSlider.onValueChanged.addListener(selectionFrameShadowWidth -> {
//			this.selectionFrame.setShadowWidth(selectionFrameShadowWidth.intValue());
//			printInvocationToConsole("setSelectionFrame", this.selectionFrame);
//			table.setSelectionFrame(this.selectionFrame);
//		});
//		selectionFrameShadowWidthSlider.setValue(this.selectionFrame.getShadowWidth());
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Shadow width", selectionFrameShadowWidthSlider);
//
//		NumberField selectionFrameAnimationDurationSlider = new NumberField(0);
//		selectionFrameAnimationDurationSlider.setMinValue(0);
//		selectionFrameAnimationDurationSlider.setMaxValue(1000);
//		selectionFrameAnimationDurationSlider.onValueChanged.addListener(selectionFrameAnimationDuration -> {
//			this.selectionFrame.setAnimationDuration(selectionFrameAnimationDuration.intValue());
//			printInvocationToConsole("setSelectionFrame", this.selectionFrame);
//			table.setSelectionFrame(this.selectionFrame);
//		});
//		selectionFrameAnimationDurationSlider.setValue(this.selectionFrame.getAnimationDuration());
//		responsiveFormLayout.addLabelAndField(MaterialIcon.HELP, "Animation duration", selectionFrameAnimationDurationSlider);
//
//		showSelectionFrameCheckBox.onValueChanged.addListener(showSelectionFrame -> {
//			this.showSelectionFrame = showSelectionFrame;
//
//			selectionFrameFullRowCheckBox.setVisible(this.showSelectionFrame);
//			selectionFrameColorPicker.setVisible(this.showSelectionFrame);
//			selectionFrameBorderWidthSlider.setVisible(this.showSelectionFrame);
//			selectionFrameGlowingWidthSlider.setVisible(this.showSelectionFrame);
//			selectionFrameShadowWidthSlider.setVisible(this.showSelectionFrame);
//			selectionFrameAnimationDurationSlider.setVisible(this.showSelectionFrame);
//
//			printInvocationToConsole("setSelectionFrame", this.selectionFrame);
//			table.setSelectionFrame(showSelectionFrame ? this.selectionFrame : null);
//		});

	}

	@Override
	public Table createComponent() {
		ComboBox<BaseTemplateRecord> comboBox = new ComboBox<>();
		comboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		TemplateField<BaseTemplateRecord> templateField = new TemplateField<>(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		TagComboBox<BaseTemplateRecord> tagComboBox = new TagComboBox<>();
		tagComboBox.setTemplate(BaseTemplates.LIST_ITEM_SMALL_ICON_SINGLE_LINE);
		TextField field = new TextField();
//		field.setEditingMode(FieldEditingMode.DISABLED);
		this.table = Table.<TableTestRecord>builder().build();

		table.addColumn("textField", MaterialIcon.HELP, "TextField", field).setHeaderAlignment(TextAlignment.LEFT);
		table.addColumn("numberField", MaterialIcon.HELP, "NumberField", new NumberField(2)).setHeaderAlignment(TextAlignment.RIGHT);
		table.addColumn("multiLineTextField", MaterialIcon.HELP, "MultiLineTextField", new MultiLineTextField()).setHeaderAlignment(TextAlignment.CENTER);
		table.addColumn("passwordField", MaterialIcon.HELP, "PasswordField", new PasswordField()).setHeaderAlignment(TextAlignment.JUSTIFY);
//		table.addColumn("richTextEditor", MaterialIcon.HELP, "RichTextEditor", new RichTextEditor());
		table.addColumn("currencyField", MaterialIcon.HELP, "CurrencyField", new CurrencyField()).setSortable(false);
		table.addColumn("fileField", MaterialIcon.HELP, "FileField", FileField.create()).setSortable(false);
		table.addColumn("checkBox", MaterialIcon.HELP, "CheckBox", new CheckBox());
		table.addColumn("comboBox", MaterialIcon.HELP, "ComboBox", comboBox).setSortable(false);
		table.addColumn("templateField", MaterialIcon.HELP, "TemplateField", templateField).setSortable(false);
		table.addColumn("tagComboBox", MaterialIcon.HELP, "TagComboBox", tagComboBox).setSortable(false);
		table.addColumn("displayField", MaterialIcon.HELP, "DisplayField", new DisplayField());
		table.addColumn("localDateField", MaterialIcon.HELP, "LocalDateField", new LocalDateField());
		table.addColumn("localTimeField", MaterialIcon.HELP, "LocalTimeField", new LocalTimeField());
		table.addColumn("localDateTimeField", MaterialIcon.HELP, "LocalDateTimeField", new LocalDateTimeField(), 200);
		table.addColumn("instantDateTimeField", MaterialIcon.HELP, "InstantDateTimeField", new InstantDateTimeField(), 200);
		table.addColumn("toggledColumn", MaterialIcon.HELP, "Toggled Column", new TextField()).setHiddenIfOnlyEmptyCellsVisible(true)
				.setValueExtractor(object -> "value from column's ValueExtractor " + object.getId());

//		table.setShowHeaderRow(true);
//		table.setShowFooterRow(true);
//		table.setSelectionFrame(showSelectionFrame ? selectionFrame : null);

		model = new TableTestTableModel();
		table.setModel(model);

		ItemView<?, ?> contextMenuItemView = DemoComponentsGenerator.createDummyItemView();
		table.setContextMenuProvider(record -> contextMenuItemView);

		table.setCustomEqualsAndHashCode(new EqualsAndHashCode<>((tableTestRecord, o) -> tableTestRecord == o, System::identityHashCode));

		return table;
	}

	@Override
	protected Component wrapComponent(Table<TableTestRecord> component) {
		return component ; //return new ComponentField(new Panel(null, null, component), 198);
	}

	@Override
	public String getDocsHtmlResourceName() {
		return "org/teamapps/ux/testapp/docs/Table.html";
	}

	private class TableTestTableModel extends AbstractTableModel<TableTestRecord> {

		private final List<TableTestRecord> records = IntStream.range(0, 60)
				.mapToObj(i -> {
					LocalDate localDate = LocalDate.of(2018, 7, i % 30 + 1);
					LocalTime localTime = LocalTime.of(22, i % 60);
					LocalDateTime localDateTime = LocalDateTime.of(2018, 7, 1, 8, i % 60);
					Instant instantDate = localDate.atStartOfDay(getSessionContext().getTimeZone()).toInstant();
					Instant instantTime = localTime.atDate(LocalDate.now()).atZone(getSessionContext().getTimeZone()).toInstant();
					Instant instandDateTime = localDateTime.atZone(getSessionContext().getTimeZone()).toInstant();
					return new TableTestRecord(
							i,
							"textField " + i,
							i,
							"multiLineTextField " + i,
							"passwordField " + i,
							"<b>Rich</b><i>Text</i>Editor<p>Some text " + i + "</p>",
							"displayField - " + localDate + "; " + localTime + "; " + localDateTime + ";; ZoneId: " + getSessionContext().getTimeZone().getId(),
							(i % 7 == 0) ? null : new CurrencyValue((i % 3 == 0) ? null : CurrencyUnit.forCode("EUR"), (i % 5 == 0) ? null : BigDecimal.valueOf(i)),
							Collections.singletonList(new BaseTemplateRecord(MaterialIcon.HELP, "File" + i, "application/binary")),
							i % 2 == 0,
							new BaseTemplateRecord(MaterialIcon.HELP, "comboBox " + i),
							new BaseTemplateRecord(MaterialIcon.HELP, "templateField " + i, "description"),
							Arrays.asList(new BaseTemplateRecord(MaterialIcon.HELP, "tagComboBox " + i + "-1"), new BaseTemplateRecord(MaterialIcon.HELP, "tagComboBox " + i + "-2")),
							localDate,
							localTime,
							localDateTime,
							instantDate,
							instantTime,
							instandDateTime,
							i % 100 < 50 ? "toggled if empty" : null
					);
				}).collect(Collectors.toList());
		private int addedRecordIdCounter = -1;

		public void fireOnAllDataChanged() {
			this.onAllDataChanged.fire(null);
		}

		@Override
		public int getCount() {
			return records.size();
		}

		public TableTestRecord getRecord(int index) {
			return index < records.size() ? records.get(index) : null;
		}

		@Override
		public List<TableTestRecord> getRecords(int startIndex, int length) {
			if (sorting == null) {
				return records.subList(startIndex, startIndex + length);
			} else {
				Comparator<TableTestRecord> comparing = Comparator.comparing(tableTestRecord -> {
					try {
						Method getter = ReflectionUtil.findMethodByName(TableTestRecord.class, "get" + StringUtils.capitalize(sorting.getFieldName()));
						if (getter == null) {
							getter = ReflectionUtil.findMethodByName(TableTestRecord.class, "is" + StringUtils.capitalize(sorting.getFieldName()));
						}
						return ((Comparable<Object>) getter.invoke(tableTestRecord));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
				if (sorting.getSortDirection() == SortDirection.DESC) {
					comparing = comparing.reversed();
				}
				return records.stream()
						.sorted(comparing)
						.skip(startIndex)
						.limit(length)
						.collect(Collectors.toList());
			}
		}

		public void addRecord(boolean fireEvent) {
			int addRecordId = addedRecordIdCounter--;
			String s = "added " + addRecordId;
			TableTestRecord record = new TableTestRecord(1, s, addRecordId, s, "", s, s, null, null, true, null, null, null, null, null, null, null, null, null, s);
			records.add(10, record);
			if (fireEvent) {
				onRecordAdded.fire(new RecordsAddedEvent<>(10, List.of(record)));
			}
		}

		public void removeRecord() {
			TableTestRecord record = records.remove(10);
			onRecordDeleted.fire(new RecordsRemovedEvent<>(ItemRange.startLength(10, 1)));
		}

		public void updateRecord() {
			TableTestRecord record = records.get(1);
			record.setNumberField(System.currentTimeMillis());
			onRecordUpdated.fire(new RecordsChangedEvent<>(1, List.of(record)));
		}

		public TableTestRecord getRecordById(int id) {
			return records.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
		}
	}

}