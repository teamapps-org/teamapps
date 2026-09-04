/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2026 TeamApps.org
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
package org.teamapps.ux.component.form.dynamic;

import org.teamapps.ux.component.field.Label;
import org.teamapps.ux.component.form.layoutpolicy.FormLayoutPolicy;
import org.teamapps.ux.component.form.layoutpolicy.FormSection;
import org.teamapps.ux.component.form.layoutpolicy.FormSectionFieldPlacement;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.format.VerticalElementAlignment;
import org.teamapps.ux.component.grid.layout.GridColumn;
import org.teamapps.ux.component.grid.layout.GridRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Repeatable rows rendered as cards and optionally as a common wide table. */
public final class DynamicFormRepeater implements DynamicFormPart {

	private final DynamicForm<?> form;
	private final String id;
	private final List<Column> columns = new ArrayList<>();
	private final List<DynamicFormRow> rows = new ArrayList<>();
	private final DynamicFormSectionOptions tableOptions = new DynamicFormSectionOptions();
	private String title;
	private int tableMinWidth = Integer.MAX_VALUE;
	private Column actionColumn;
	private long nextRowId = 1;
	private boolean active = true;

	DynamicFormRepeater(DynamicForm<?> form, String id, String title) {
		this.form = Objects.requireNonNull(form, "form");
		this.id = requireId(id);
		this.title = title;
	}

	DynamicForm<?> getForm() {
		return form;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public DynamicFormRepeater title(String title) {
		this.title = title;
		form.layoutChanged();
		return this;
	}

	public Column addColumn(String caption) {
		ensureActive();
		Column column = new Column(this, caption, null);
		columns.add(column);
		form.registerAuxiliaryComponent(column.heading);
		form.layoutChanged();
		return column;
	}

	public DynamicFormRepeater actionColumn(String caption, int width) {
		if (width < 1) throw new IllegalArgumentException("Action column width must be >= 1");
		if (actionColumn != null) form.retireAuxiliaryComponent(actionColumn.heading);
		actionColumn = new Column(this, caption, SizingPolicy.fixed(width));
		form.registerAuxiliaryComponent(actionColumn.heading);
		form.layoutChanged();
		return this;
	}

	/** At and above this width all active rows share one header and grid section. */
	public DynamicFormRepeater tableAt(int minWidth) {
		if (minWidth < 0) throw new IllegalArgumentException("minWidth must be >= 0");
		tableMinWidth = minWidth;
		form.layoutChanged();
		return this;
	}

	public DynamicFormRepeater tableAt(DynamicFormLayouts.Breakpoint breakpoint) {
		return tableAt(Objects.requireNonNull(breakpoint, "breakpoint").getMinWidth());
	}

	public int getTableMinWidth() {
		return tableMinWidth;
	}

	public DynamicFormRow addRow(String title, Consumer<DynamicFormRow> rowBuilder) {
		return addRow(nextRowId++, title, rowBuilder);
	}

	public DynamicFormRow addRow(long rowId, String title, Consumer<DynamicFormRow> rowBuilder) {
		ensureActive();
		Objects.requireNonNull(rowBuilder, "rowBuilder");
		if (rows.stream().anyMatch(row -> row.getId() == rowId)) {
			throw new IllegalArgumentException("Duplicate row id: " + rowId);
		}
		nextRowId = Math.max(nextRowId, rowId + 1);
		DynamicFormRow row = new DynamicFormRow(this, rowId, title);
		form.update(() -> {
			rows.add(row);
			rowBuilder.accept(row);
			form.structureChanged(DynamicFormStructureChange.Type.ROW_ADDED, id + "." + rowId);
		});
		return row;
	}

	public boolean removeRow(long rowId) {
		DynamicFormRow row = rows.stream().filter(candidate -> candidate.getId() == rowId).findFirst().orElse(null);
		return row != null && removeRow(row);
	}

	boolean removeRow(DynamicFormRow row) {
		if (!rows.contains(row)) return false;
		form.update(() -> {
			rows.remove(row);
			row.retire();
			form.structureChanged(DynamicFormStructureChange.Type.ROW_REMOVED, id + "." + row.getId());
		});
		return true;
	}

	public boolean removeLastRow() {
		return !rows.isEmpty() && removeRow(rows.get(rows.size() - 1));
	}

	public List<DynamicFormRow> getRows() {
		return Collections.unmodifiableList(rows);
	}

	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	public DynamicFormRepeater collapsible(boolean collapsible) {
		tableOptions.setCollapsible(collapsible);
		form.layoutChanged();
		return this;
	}

	public DynamicFormRepeater collapsed(boolean collapsed) {
		tableOptions.setCollapsed(collapsed);
		form.layoutChanged();
		return this;
	}

	public DynamicFormRepeater configureTable(BiConsumer<DynamicFormLayout, FormSection> customizer) {
		tableOptions.addCustomizer(customizer);
		form.layoutChanged();
		return this;
	}

	public boolean remove() {
		return active && form.removePart(this, DynamicFormStructureChange.Type.REPEATER_REMOVED);
	}

	@Override
	public void appendTo(FormLayoutPolicy policy, DynamicFormLayout layout) {
		if (layout.getMinWidth() >= tableMinWidth) {
			appendTable(policy, layout);
		} else if (rows.isEmpty()) {
			policy.addSection(form.createElementSection(id + "-empty", title, List.of(), layout, tableOptions));
		} else {
			for (DynamicFormRow row : rows) {
				policy.addSection(form.createElementSection(id + "-row-" + row.getId(), row.getTitle(),
						row.getCardElements(), layout, row.getCardOptions()));
			}
		}
	}

	private void appendTable(FormLayoutPolicy policy, DynamicFormLayout layout) {
		FormSection section = form.createBaseSection(id, title, layout, tableOptions);
		columns.forEach(column -> section.addColumn(column.createGridColumn()));
		if (actionColumn != null) section.addColumn(actionColumn.createGridColumn());
		section.addRow(new GridRow(SizingPolicy.AUTO, 0, form.getLabelFieldGap()));
		for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
			section.addPlacement(form.placement(columns.get(columnIndex).heading, 0, columnIndex)
					.setVerticalAlignment(VerticalElementAlignment.BOTTOM));
		}
		if (actionColumn != null) {
			section.addPlacement(form.placement(actionColumn.heading, 0, columns.size())
					.setVerticalAlignment(VerticalElementAlignment.BOTTOM));
		}

		for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
			DynamicFormRow row = rows.get(rowIndex);
			int gridRow = rowIndex + 1;
			section.addRow(new GridRow(SizingPolicy.AUTO,
					rowIndex == 0 ? 0 : form.getFieldGroupGap(), 0));
			int columnIndex = 0;
			for (DynamicFormField field : row.getFields()) {
				if (columnIndex >= columns.size()) break;
				int span = Math.min(field.resolveColumnSpan(layout), columns.size() - columnIndex);
				FormSectionFieldPlacement placement = form.placement(field.getField(), gridRow, columnIndex)
						.setColSpan(span);
				field.configurePlacement(layout, placement);
				section.addPlacement(placement);
				columnIndex += span;
			}
			if (actionColumn != null && row.getAction() != null) {
				FormSectionFieldPlacement placement = form.placement(row.getAction().getComponent(), gridRow, columns.size())
						.setHorizontalAlignment(HorizontalElementAlignment.LEFT);
				row.getAction().configurePlacement(layout, placement);
				section.addPlacement(placement);
			}
		}
		form.finishSection(layout, section, tableOptions);
		policy.addSection(section);
	}

	@Override
	public void retire() {
		active = false;
		new ArrayList<>(rows).forEach(this::removeRow);
		columns.forEach(column -> form.retireAuxiliaryComponent(column.heading));
		if (actionColumn != null) form.retireAuxiliaryComponent(actionColumn.heading);
	}

	String getCaption(int fieldIndex) {
		if (fieldIndex >= columns.size()) {
			throw new IllegalStateException("Add a repeater column before field " + (fieldIndex + 1));
		}
		return columns.get(fieldIndex).caption;
	}

	private void ensureActive() {
		if (!active) throw new IllegalStateException("Repeater has been removed: " + id);
	}

	private static String requireId(String id) {
		if (id == null || id.isBlank()) throw new IllegalArgumentException("Repeater id must not be blank");
		return id;
	}

	public static final class Column {
		private final DynamicFormRepeater repeater;
		private final String caption;
		private final Label heading;
		private SizingPolicy width;
		private Consumer<GridColumn> customizer = column -> {};

		private Column(DynamicFormRepeater repeater, String caption, SizingPolicy width) {
			this.repeater = repeater;
			this.caption = Objects.requireNonNull(caption, "caption");
			this.heading = new Label(caption);
			this.width = width;
		}

		public String getCaption() {
			return caption;
		}

		public Column width(SizingPolicy width) {
			this.width = Objects.requireNonNull(width, "width");
			repeater.form.layoutChanged();
			return this;
		}

		public Column configure(Consumer<GridColumn> customizer) {
			this.customizer = this.customizer.andThen(Objects.requireNonNull(customizer, "customizer"));
			repeater.form.layoutChanged();
			return this;
		}

		private GridColumn createGridColumn() {
			GridColumn result = repeater.form.column(width);
			customizer.accept(result);
			return result;
		}
	}
}
