package org.teamapps.projector.components.infinitescroll.table;

import org.teamapps.common.format.Color;
import org.teamapps.ux.cache.record.EqualsAndHashCode;
import org.teamapps.ux.component.field.AbstractField;
import org.teamapps.ux.data.extraction.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableBuilder<RECORD> {

	private TableModel<RECORD> model;
	private List<TableColumn<RECORD, ?>> columns = new ArrayList<>();
	protected EqualsAndHashCode<RECORD> customEqualsAndHashCode = EqualsAndHashCode.bypass();

	// immutable (currently)
	private TableDisplayStyle displayStyle = TableDisplayStyle.TABLE;
	private boolean forceFitWidth = false;
	private int rowHeight = 28;
	private boolean stripedRowsEnabled = true;
	private boolean columnHeadersVisible = true;
	private boolean multiRowSelectionEnabled = false;
	private boolean rowCheckBoxesEnabled = false;
	private boolean numberingColumnEnabled = false;
	private boolean textSelectionEnabled = true;
	private boolean editable = false;
	private SelectionFrame selectionFrame = null;

	private boolean headerFieldsRowEnabled = false;
	private int headerFieldsRowHeight = 28;

	private boolean footerFieldsRowEnabled = false;
	private int footerFieldsRowHeight = 28;

	// other setters:
	private Map<String, AbstractField<?>> headerFields = new HashMap<>(0);
	private Color headerFieldsRowBackgroundColor = null;
	private Color headerFieldsRowBorderColor = null;
	private Integer headerFieldsRowBorderWidth = null;

	private Map<String, AbstractField<?>> footerFields = new HashMap<>(0);
	private Color footerFieldsRowBackgroundColor = null;
	private Color footerFieldsRowBorderColor = null;
	private Integer footerFieldsRowBorderWidth = null;

	private PropertyProvider<RECORD> propertyProvider = new BeanPropertyExtractor<>();
	private PropertyInjector<RECORD> propertyInjector = new BeanPropertyInjector<>();

	private Color rowBorderColor = null;
	private Integer rowBorderWidth = null;
	private Color rowColor = null;
	private Color stripedRowColorOdd = null;
	private Color selectionColor = null;
	private Sorting sorting = null;

	TableBuilder() {
	}

	public Table<RECORD> build() {
		Table<RECORD> table = new Table<>(
				model,
				displayStyle,
				forceFitWidth,
				rowHeight,
				stripedRowsEnabled,
				columnHeadersVisible,
				multiRowSelectionEnabled,
				rowCheckBoxesEnabled,
				numberingColumnEnabled,
				textSelectionEnabled,
				editable,
				selectionFrame,
				headerFieldsRowEnabled,
				headerFieldsRowHeight,
				footerFieldsRowEnabled,
				footerFieldsRowHeight
		);
		table.setColumns(columns);
		table.setCustomEqualsAndHashCode(customEqualsAndHashCode);
		table.setHeaderFields(headerFields);
		table.setHeaderFieldsRowBackgroundColor(headerFieldsRowBackgroundColor);
		table.setHeaderFieldsRowBorderColor(headerFieldsRowBorderColor);
		table.setHeaderFieldsRowBorderWidth(headerFieldsRowBorderWidth);
		table.setFooterFields(footerFields);
		table.setFooterFieldsRowBackgroundColor(footerFieldsRowBackgroundColor);
		table.setFooterFieldsRowBorderColor(footerFieldsRowBorderColor);
		table.setFooterFieldsRowBorderWidth(footerFieldsRowBorderWidth);
		table.setPropertyProvider(propertyProvider);
		table.setPropertyInjector(propertyInjector);
		table.setRowBorderColor(rowBorderColor);
		table.setRowBorderWidth(rowBorderWidth);
		table.setRowColor(rowColor);
		table.setStripedRowColorOdd(stripedRowColorOdd);
		table.setSelectionColor(selectionColor);
		table.setSorting(sorting);
		return table;
	}

	public void withModel(TableModel<RECORD> model) {
		this.model = model;
	}

	public void withColumns(List<TableColumn<RECORD, ?>> columns) {
		this.columns = columns;
	}

	public void addColumn(TableColumn<RECORD, ?> column) {
		this.columns.add(column);
	}

	public void withCustomEqualsAndHashCode(EqualsAndHashCode<RECORD> customEqualsAndHashCode) {
		this.customEqualsAndHashCode = customEqualsAndHashCode;
	}

	public void withDisplayStyle(TableDisplayStyle displayStyle) {
		this.displayStyle = displayStyle;
	}

	public void withForceFitWidth(boolean forceFitWidth) {
		this.forceFitWidth = forceFitWidth;
	}

	public void withRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public void withStripedRowsEnabled(boolean stripedRowsEnabled) {
		this.stripedRowsEnabled = stripedRowsEnabled;
	}

	public void withColumnHeadersVisible(boolean columnHeadersVisible) {
		this.columnHeadersVisible = columnHeadersVisible;
	}

	public void withMultiRowSelectionEnabled(boolean multiRowSelectionEnabled) {
		this.multiRowSelectionEnabled = multiRowSelectionEnabled;
	}

	public void withRowCheckBoxesEnabled(boolean rowCheckBoxesEnabled) {
		this.rowCheckBoxesEnabled = rowCheckBoxesEnabled;
	}

	public void withNumberingColumnEnabled(boolean numberingColumnEnabled) {
		this.numberingColumnEnabled = numberingColumnEnabled;
	}

	public void withTextSelectionEnabled(boolean textSelectionEnabled) {
		this.textSelectionEnabled = textSelectionEnabled;
	}

	public void withEditable(boolean editable) {
		this.editable = editable;
	}

	public void withSelectionFrame(SelectionFrame selectionFrame) {
		this.selectionFrame = selectionFrame;
	}

	public void withHeaderFields(Map<String, AbstractField<?>> headerFields) {
		this.headerFields = headerFields;
	}

	public void withHeaderFieldsRowEnabled(boolean headerFieldsRowEnabled) {
		this.headerFieldsRowEnabled = headerFieldsRowEnabled;
	}

	public void withHeaderFieldsRowHeight(int headerFieldsRowHeight) {
		this.headerFieldsRowHeight = headerFieldsRowHeight;
	}

	public void withHeaderFieldsRowBackgroundColor(Color headerFieldsRowBackgroundColor) {
		this.headerFieldsRowBackgroundColor = headerFieldsRowBackgroundColor;
	}

	public void withHeaderFieldsRowBorderColor(Color headerFieldsRowBorderColor) {
		this.headerFieldsRowBorderColor = headerFieldsRowBorderColor;
	}

	public void withHeaderFieldsRowBorderWidth(Integer headerFieldsRowBorderWidth) {
		this.headerFieldsRowBorderWidth = headerFieldsRowBorderWidth;
	}

	public void withFooterFields(Map<String, AbstractField<?>> footerFields) {
		this.footerFields = footerFields;
	}

	public void withFooterFieldsRowEnabled(boolean footerFieldsRowEnabled) {
		this.footerFieldsRowEnabled = footerFieldsRowEnabled;
	}

	public void withFooterFieldsRowHeight(int footerFieldsRowHeight) {
		this.footerFieldsRowHeight = footerFieldsRowHeight;
	}

	public void withFooterFieldsRowBackgroundColor(Color footerFieldsRowBackgroundColor) {
		this.footerFieldsRowBackgroundColor = footerFieldsRowBackgroundColor;
	}

	public void withFooterFieldsRowBorderColor(Color footerFieldsRowBorderColor) {
		this.footerFieldsRowBorderColor = footerFieldsRowBorderColor;
	}

	public void withFooterFieldsRowBorderWidth(Integer footerFieldsRowBorderWidth) {
		this.footerFieldsRowBorderWidth = footerFieldsRowBorderWidth;
	}

	public void withPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
	}

	public void withPropertyInjector(PropertyInjector<RECORD> propertyInjector) {
		this.propertyInjector = propertyInjector;
	}

	public void withRowBorderColor(Color rowBorderColor) {
		this.rowBorderColor = rowBorderColor;
	}

	public void withRowBorderWidth(Integer rowBorderWidth) {
		this.rowBorderWidth = rowBorderWidth;
	}

	public void withRowColor(Color rowColor) {
		this.rowColor = rowColor;
	}

	public void withStripedRowColorOdd(Color stripedRowColorOdd) {
		this.stripedRowColorOdd = stripedRowColorOdd;
	}

	public void withSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}

	public void withSorting(Sorting sorting) {
		this.sorting = sorting;
	}
}
