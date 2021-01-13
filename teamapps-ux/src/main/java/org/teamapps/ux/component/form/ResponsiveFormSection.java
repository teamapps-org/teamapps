/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2021 TeamApps.org
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
package org.teamapps.ux.component.form;

import org.teamapps.common.format.Color;
import org.teamapps.ux.component.form.layoutpolicy.FormSection;
import org.teamapps.ux.component.grid.layout.GridColumn;
import org.teamapps.ux.component.form.layoutpolicy.FormSectionFieldPlacement;
import org.teamapps.ux.component.grid.layout.GridRow;
import org.teamapps.ux.component.format.Border;
import org.teamapps.ux.component.format.Shadow;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponsiveFormSection {
	private final ResponsiveFormLayout formLayout;
	private final String id;
	private Spacing margin;
	private Spacing padding;
	private Border border;
	private Shadow shadow;
	private boolean drawHeaderLine;
	private Color backgroundColor;
	private boolean collapsible;
	private boolean collapsed;
	private boolean visible = true;
	private Template headerTemplate;
	private Object headerData;
	private int gridGap;
	private boolean fillRemainingHeight;
	private boolean hideWhenNoVisibleFields;

	private int minimalNumberOfColumns;
	private int minimalNumberOfRow;

	private final Map<Integer, GridRow> rowMap = new HashMap<>();
	private final Map<Integer, GridColumn> columnMap = new HashMap<>();
	private final List<ResponsiveFormField> responsiveFormFields = new ArrayList<>();

	private final ResponsiveFormConfigurationTemplate configurationTemplate;


	protected ResponsiveFormSection(ResponsiveFormLayout formLayout, String id, ResponsiveFormConfigurationTemplate configurationTemplate) {
		this.formLayout = formLayout;
		this.id = id;
		this.configurationTemplate = configurationTemplate;
		setTemplate();
	}

	private ResponsiveFormSection setTemplate() {
		FormSection sectionTemplate = configurationTemplate.getSectionTemplate();
		if (sectionTemplate == null) {
			return this;
		}
		setMargin(sectionTemplate.getMargin());
		setPadding(sectionTemplate.getPadding());
		setBorder(sectionTemplate.getBorder());
		setShadow(sectionTemplate.getShadow());
		setDrawHeaderLine(sectionTemplate.isDrawHeaderLine());
		setBackgroundColor(sectionTemplate.getBackgroundColor());
		setCollapsible(sectionTemplate.isCollapsible());
		setCollapsed(sectionTemplate.isCollapsed());
		setVisible(sectionTemplate.isVisible());
		setHeaderTemplate(sectionTemplate.getHeaderTemplate());
		setHeaderData(sectionTemplate.getHeaderData());
		setGridGap(sectionTemplate.getGridGap());
		setFillRemainingHeight(sectionTemplate.isFillRemainingHeight());
		setHideWhenNoVisibleFields(sectionTemplate.isHideWhenNoVisibleFields());
		return this;
	}


	protected ResponsiveFormSection addField(ResponsiveFormField field) {
		this.responsiveFormFields.add(field);
		int row = field.getRow();
		int column = field.getColumn();
		GridRow rowTemplate = configurationTemplate.createRowTemplate(false, false);
		GridColumn columnTemplate = configurationTemplate.createColumnTemplate(column, false, false);
		if (!rowMap.containsKey(row)) {
			rowMap.put(row, rowTemplate);
		}
		if (!columnMap.containsKey(column)) {
			columnMap.put(column, columnTemplate);
		}
		return this;
	}

	protected int getLastNonEmptyRow() {
		return rowMap.keySet().stream().max(Integer::compareTo).orElse(-1);
	}

	protected int getLastNonEmptyColumnInRow(int row) {
		return responsiveFormFields.stream()
				.filter(field -> field.getRow() == row)
				.map(field -> field.getColumn() + field.getColSpan() - 1)
				.max(Integer::compareTo)
				.orElse(-1);
	}

	protected int getLastNonEmptyColumn() {
		return responsiveFormFields.stream()
				.map(field -> field.getColumn() + field.getColSpan() - 1)
				.max(Integer::compareTo)
				.orElse(-1);
	}

	public GridRow getRow(int row) {
		GridRow sectionRow = rowMap.get(row);
		if (sectionRow == null) {
			sectionRow = new GridRow();
			rowMap.put(row, sectionRow);
		}
		return sectionRow;
	}

	public GridColumn getColumn(int column) {
		GridColumn sectionColumn = columnMap.get(column);
		if (sectionColumn == null) {
			sectionColumn = new GridColumn();
			columnMap.put(column, sectionColumn);
		}
		return sectionColumn;
	}

	public void setRowConfig(int row, GridRow rowConfig) {
		rowMap.put(row, rowConfig);
	}

	public void setColumnConfig(int colum, GridColumn columnConfig) {
		columnMap.put(colum, columnConfig);
	}

	protected Map<Integer, GridRow> getRowMap() {
		return rowMap;
	}

	protected Map<Integer, GridColumn> getColumnMap() {
		return columnMap;
	}

	public List<ResponsiveFormField> getResponsiveFormFields() {
		return responsiveFormFields;
	}

	public String getId() {
		return id;
	}

	public Spacing getMargin() {
		return margin;
	}

	public ResponsiveFormSection setMargin(Spacing margin) {
		this.margin = margin;
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public ResponsiveFormSection setPadding(Spacing padding) {
		this.padding = padding;
		return this;
	}

	public Border getBorder() {
		return border;
	}

	public ResponsiveFormSection setBorder(Border border) {
		this.border = border;
		return this;
	}

	public Shadow getShadow() {
		return shadow;
	}

	public ResponsiveFormSection setShadow(Shadow shadow) {
		this.shadow = shadow;
		return this;
	}

	public boolean isDrawHeaderLine() {
		return drawHeaderLine;
	}

	public ResponsiveFormSection setDrawHeaderLine(boolean drawHeaderLine) {
		this.drawHeaderLine = drawHeaderLine;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public ResponsiveFormSection setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public ResponsiveFormSection setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
		return this;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public ResponsiveFormSection setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public ResponsiveFormSection setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public Template getHeaderTemplate() {
		return headerTemplate;
	}

	public ResponsiveFormSection setHeaderTemplate(Template headerTemplate) {
		this.headerTemplate = headerTemplate;
		return this;
	}

	public Object getHeaderData() {
		return headerData;
	}

	public ResponsiveFormSection setHeaderData(Object headerData) {
		this.headerData = headerData;
		return this;
	}

	public int getGridGap() {
		return gridGap;
	}

	public ResponsiveFormSection setGridGap(int gridGap) {
		this.gridGap = gridGap;
		return this;
	}

	public boolean isFillRemainingHeight() {
		return fillRemainingHeight;
	}

	public ResponsiveFormSection setFillRemainingHeight(boolean fillRemainingHeight) {
		this.fillRemainingHeight = fillRemainingHeight;
		return this;
	}

	public boolean isHideWhenNoVisibleFields() {
		return hideWhenNoVisibleFields;
	}

	public ResponsiveFormSection setHideWhenNoVisibleFields(boolean hideWhenNoVisibleFields) {
		this.hideWhenNoVisibleFields = hideWhenNoVisibleFields;
		return this;
	}

	public int getMinimalNumberOfColumns() {
		return minimalNumberOfColumns;
	}

	public void setMinimalNumberOfColumns(int minimalNumberOfColumns) {
		this.minimalNumberOfColumns = minimalNumberOfColumns;
	}

	public int getMinimalNumberOfRow() {
		return minimalNumberOfRow;
	}

	public void setMinimalNumberOfRow(int minimalNumberOfRow) {
		this.minimalNumberOfRow = minimalNumberOfRow;
	}

	protected FormSection createFormSection() {
		FormSection formSection = createBaseFormSection();

		int maxRow = rowMap.keySet().stream().mapToInt(value -> value).max().orElse(0);
		maxRow = Math.max(minimalNumberOfRow, maxRow);
		for (int i = 0; i <= maxRow; i++) {
			GridRow sectionRow = rowMap.get(i);
			if (sectionRow == null) {
				sectionRow = configurationTemplate.createRowTemplate(true, false);
			}
			formSection.getRows().add(sectionRow);
		}

		int maxColumn = columnMap.keySet().stream().mapToInt(value -> value).max().orElse(0);
		maxColumn = Math.max(minimalNumberOfColumns, maxColumn);
		for (int i = 0; i <= maxColumn; i++) {
			GridColumn sectionColumn = columnMap.get(i);
			if (sectionColumn == null) {
				sectionColumn = configurationTemplate.createColumnTemplate(i,true, false);
			}
			formSection.getColumns().add(sectionColumn);
		}

		responsiveFormFields.stream().forEach(field -> {
			formSection.addPlacement(field.createFormSectionPlacement());
		});
		return formSection;
	}

	protected FormSection createSmallScreenFormSection() {
		FormSection formSection = createBaseFormSection();
		responsiveFormFields.stream().sorted((o1, o2) -> {
			int rowCompare = Integer.compare(o1.getRow(), o2.getRow());
			if (rowCompare != 0) {
				return rowCompare;
			}
			return Integer.compare(o1.getColumn(), o2.getColumn());
		}).forEach(field -> {
			FormSectionFieldPlacement placement = field.createFormSectionPlacement();
			formSection.addRow(getRow(placement.getRow()));
			placement.setColumn(0);
			placement.setRow(formSection.getPlacements().size());
			placement.setRowSpan(1);
			placement.setColSpan(1);
			formSection.add(placement);
		});
		formSection.addColumn(configurationTemplate.createColumnTemplate(0, false, true));
		return formSection;
	}

	private FormSection createBaseFormSection() {
		FormSection section = new FormSection(this.id);
		section.setMargin(this.margin);
		section.setPadding(this.padding);
		section.setBorder(this.border);
		section.setShadow(this.shadow);
		section.setDrawHeaderLine(this.drawHeaderLine);
		section.setBackgroundColor(this.backgroundColor);
		section.setCollapsible(this.collapsible);
		section.setCollapsed(this.collapsed);
		section.setVisible(this.visible);
		section.setHeaderTemplate(this.headerTemplate);
		section.setHeaderData(this.headerData);
		section.setGridGap(this.gridGap);
		section.setFillRemainingHeight(this.fillRemainingHeight);
		section.setHideWhenNoVisibleFields(this.hideWhenNoVisibleFields);
		return section;
	}

}
