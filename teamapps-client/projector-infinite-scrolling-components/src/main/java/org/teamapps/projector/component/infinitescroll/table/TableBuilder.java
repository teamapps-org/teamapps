package org.teamapps.projector.component.infinitescroll.table;

import org.teamapps.common.format.Color;
import org.teamapps.projector.component.field.Field;
import org.teamapps.projector.component.infinitescroll.recordcache.EqualsAndHashCode;
import org.teamapps.projector.dataextraction.BeanPropertyExtractor;
import org.teamapps.projector.dataextraction.BeanPropertyInjector;
import org.teamapps.projector.dataextraction.PropertyInjector;
import org.teamapps.projector.dataextraction.PropertyProvider;

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
	private Map<String, Field<?>> headerFields = new HashMap<>(0);
	private Color headerFieldsRowBackgroundColor = null;
	private Color headerFieldsRowBorderColor = null;
	private Integer headerFieldsRowBorderWidth = null;

	private Map<String, Field<?>> footerFields = new HashMap<>(0);
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

	public TableBuilder<RECORD> withModel(TableModel<RECORD> model) {
		this.model = model;
		return this;
	}

	public TableBuilder<RECORD> withColumns(List<TableColumn<RECORD, ?>> columns) {
		this.columns = columns;
		return this;
	}

	public void addColumn(TableColumn<RECORD, ?> column) {
		this.columns.add(column);
	}

	public TableBuilder<RECORD> withCustomEqualsAndHashCode(EqualsAndHashCode<RECORD> customEqualsAndHashCode) {
		this.customEqualsAndHashCode = customEqualsAndHashCode;
		return this;
	}

	public TableBuilder<RECORD> withDisplayStyle(TableDisplayStyle displayStyle) {
		this.displayStyle = displayStyle;
		return this;
	}

	public TableBuilder<RECORD> withForceFitWidth(boolean forceFitWidth) {
		this.forceFitWidth = forceFitWidth;
		return this;
	}

	public TableBuilder<RECORD> withRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
		return this;
	}

	public TableBuilder<RECORD> withStripedRowsEnabled(boolean stripedRowsEnabled) {
		this.stripedRowsEnabled = stripedRowsEnabled;
		return this;
	}

	public TableBuilder<RECORD> withColumnHeadersVisible(boolean columnHeadersVisible) {
		this.columnHeadersVisible = columnHeadersVisible;
		return this;
	}

	public TableBuilder<RECORD> withMultiRowSelectionEnabled(boolean multiRowSelectionEnabled) {
		this.multiRowSelectionEnabled = multiRowSelectionEnabled;
		return this;
	}

	public TableBuilder<RECORD> withRowCheckBoxesEnabled(boolean rowCheckBoxesEnabled) {
		this.rowCheckBoxesEnabled = rowCheckBoxesEnabled;
		return this;
	}

	public TableBuilder<RECORD> withNumberingColumnEnabled(boolean numberingColumnEnabled) {
		this.numberingColumnEnabled = numberingColumnEnabled;
		return this;
	}

	public TableBuilder<RECORD> withTextSelectionEnabled(boolean textSelectionEnabled) {
		this.textSelectionEnabled = textSelectionEnabled;
		return this;
	}

	public TableBuilder<RECORD> withEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	public TableBuilder<RECORD> withSelectionFrame(SelectionFrame selectionFrame) {
		this.selectionFrame = selectionFrame;
		return this;
	}

	public TableBuilder<RECORD> withHeaderFields(Map<String, Field<?>> headerFields) {
		this.headerFields = headerFields;
		return this;
	}

	public TableBuilder<RECORD> withHeaderFieldsRowEnabled(boolean headerFieldsRowEnabled) {
		this.headerFieldsRowEnabled = headerFieldsRowEnabled;
		return this;
	}

	public TableBuilder<RECORD> withHeaderFieldsRowHeight(int headerFieldsRowHeight) {
		this.headerFieldsRowHeight = headerFieldsRowHeight;
		return this;
	}

	public TableBuilder<RECORD> withHeaderFieldsRowBackgroundColor(Color headerFieldsRowBackgroundColor) {
		this.headerFieldsRowBackgroundColor = headerFieldsRowBackgroundColor;
		return this;
	}

	public TableBuilder<RECORD> withHeaderFieldsRowBorderColor(Color headerFieldsRowBorderColor) {
		this.headerFieldsRowBorderColor = headerFieldsRowBorderColor;
		return this;
	}

	public TableBuilder<RECORD> withHeaderFieldsRowBorderWidth(Integer headerFieldsRowBorderWidth) {
		this.headerFieldsRowBorderWidth = headerFieldsRowBorderWidth;
		return this;
	}

	public TableBuilder<RECORD> withFooterFields(Map<String, Field<?>> footerFields) {
		this.footerFields = footerFields;
		return this;
	}

	public TableBuilder<RECORD> withFooterFieldsRowEnabled(boolean footerFieldsRowEnabled) {
		this.footerFieldsRowEnabled = footerFieldsRowEnabled;
		return this;
	}

	public TableBuilder<RECORD> withFooterFieldsRowHeight(int footerFieldsRowHeight) {
		this.footerFieldsRowHeight = footerFieldsRowHeight;
		return this;
	}

	public TableBuilder<RECORD> withFooterFieldsRowBackgroundColor(Color footerFieldsRowBackgroundColor) {
		this.footerFieldsRowBackgroundColor = footerFieldsRowBackgroundColor;
		return this;
	}

	public TableBuilder<RECORD> withFooterFieldsRowBorderColor(Color footerFieldsRowBorderColor) {
		this.footerFieldsRowBorderColor = footerFieldsRowBorderColor;
		return this;
	}

	public TableBuilder<RECORD> withFooterFieldsRowBorderWidth(Integer footerFieldsRowBorderWidth) {
		this.footerFieldsRowBorderWidth = footerFieldsRowBorderWidth;
		return this;
	}

	public TableBuilder<RECORD> withPropertyProvider(PropertyProvider<RECORD> propertyProvider) {
		this.propertyProvider = propertyProvider;
		return this;
	}

	public TableBuilder<RECORD> withPropertyInjector(PropertyInjector<RECORD> propertyInjector) {
		this.propertyInjector = propertyInjector;
		return this;
	}

	public TableBuilder<RECORD> withRowBorderColor(Color rowBorderColor) {
		this.rowBorderColor = rowBorderColor;
		return this;
	}

	public TableBuilder<RECORD> withRowBorderWidth(Integer rowBorderWidth) {
		this.rowBorderWidth = rowBorderWidth;
		return this;
	}

	public TableBuilder<RECORD> withRowColor(Color rowColor) {
		this.rowColor = rowColor;
		return this;
	}

	public TableBuilder<RECORD> withStripedRowColorOdd(Color stripedRowColorOdd) {
		this.stripedRowColorOdd = stripedRowColorOdd;
		return this;
	}

	public TableBuilder<RECORD> withSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
		return this;
	}

	public TableBuilder<RECORD> withSorting(Sorting sorting) {
		this.sorting = sorting;
		return this;
	}
}

