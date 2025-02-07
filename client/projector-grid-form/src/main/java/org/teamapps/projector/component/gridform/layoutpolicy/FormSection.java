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
package org.teamapps.projector.component.gridform.layoutpolicy;

import org.teamapps.projector.common.format.Color;
import org.teamapps.projector.component.gridform.*;
import org.teamapps.projector.format.Border;
import org.teamapps.projector.format.BoxShadow;
import org.teamapps.projector.format.Spacing;
import org.teamapps.projector.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormSection {

	private final String id;
	private Spacing margin;
	private Spacing padding;
	private Border border;
	private BoxShadow shadow;
	private boolean drawHeaderLine = true;
	private Color backgroundColor;
	private List<GridColumn> columns = new ArrayList<>();
	private List<GridRow> rows = new ArrayList<>();
	private List<FormSectionPlacement> fieldPlacements = new ArrayList<>();
	private boolean collapsible;
	private boolean collapsed;
	private boolean visible = true;
	private Template headerTemplate;
	private Object headerData;
	private int gridGap = 5;
	private boolean fillRemainingHeight;
	private boolean hideWhenNoVisibleFields;


	public FormSection(String id) {
		this.id = id;
	}

	public FormSection createCopy(String id) {
		FormSection section = new FormSection(id);
		section.setMargin(margin);
		section.setPadding(padding);
		section.setBorder(border);
		section.setShadow(shadow);
		section.setDrawHeaderLine(drawHeaderLine);
		section.setBackgroundColor(backgroundColor);
		section.setCollapsible(collapsible);
		section.setCollapsed(collapsed);
		section.setVisible(visible);
		section.setHeaderTemplate(headerTemplate);
		section.setHeaderData(headerData);
		section.setGridGap(gridGap);
		section.setFillRemainingHeight(fillRemainingHeight);
		section.setHideWhenNoVisibleFields(hideWhenNoVisibleFields);
		return section;
	}

	public void add(GridColumn column) {
		columns.add(column);
	}

	public void add(GridRow row) {
		rows.add(row);
	}

	public void add(FormSectionFieldPlacement fieldPlacement) {
		fieldPlacements.add(fieldPlacement);
	}

	public String getId() {
		return id;
	}

	public Spacing getMargin() {
		return margin;
	}

	public FormSection setMargin(Spacing margin) {
		this.margin = margin;
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public FormSection setPadding(Spacing padding) {
		this.padding = padding;
		return this;
	}

	public Border getBorder() {
		return border;
	}

	public FormSection setBorder(Border border) {
		this.border = border;
		return this;
	}

	public BoxShadow getShadow() {
		return shadow;
	}

	public FormSection setShadow(BoxShadow shadow) {
		this.shadow = shadow;
		return this;
	}

	public boolean isDrawHeaderLine() {
		return drawHeaderLine;
	}

	public FormSection setDrawHeaderLine(boolean drawHeaderLine) {
		this.drawHeaderLine = drawHeaderLine;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public FormSection setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public List<GridColumn> getColumns() {
		return columns;
	}

	public FormSection setColumns(List<GridColumn> columns) {
		this.columns = columns;
		return this;
	}

	public FormSection addColumn(GridColumn column) {
		this.columns.add(column);
		return this;
	}

	public List<GridRow> getRows() {
		return rows;
	}

	public FormSection addRow(GridRow row) {
		this.rows.add(row);
		return this;
	}

	public FormSection setRows(List<GridRow> rows) {
		this.rows = rows;
		return this;
	}

	public List<FormSectionPlacement> getPlacements() {
		return fieldPlacements;
	}

	public FormSection addPlacement(FormSectionPlacement fieldPlacement) {
		this.fieldPlacements.add(fieldPlacement);
		return this;
	}

	public FormSection setPlacements(List<FormSectionPlacement> fieldPlacements) {
		this.fieldPlacements = fieldPlacements;
		return this;
	}

	public boolean isCollapsible() {
		return collapsible;
	}

	public FormSection setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
		return this;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public FormSection setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public FormSection setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public Template getHeaderTemplate() {
		return headerTemplate;
	}

	public FormSection setHeaderTemplate(Template headerTemplate) {
		this.headerTemplate = headerTemplate;
		return this;
	}

	public Object getHeaderData() {
		return headerData;
	}

	public FormSection setHeaderData(Object headerData) {
		this.headerData = headerData;
		return this;
	}

	public int getGridGap() {
		return gridGap;
	}

	public FormSection setGridGap(int gridGap) {
		this.gridGap = gridGap;
		return this;
	}

	public boolean isFillRemainingHeight() {
		return fillRemainingHeight;
	}

	public FormSection setFillRemainingHeight(boolean fillRemainingHeight) {
		this.fillRemainingHeight = fillRemainingHeight;
		return this;
	}

	public boolean isHideWhenNoVisibleFields() {
		return hideWhenNoVisibleFields;
	}

	public FormSection setHideWhenNoVisibleFields(boolean hideWhenNoVisibleFields) {
		this.hideWhenNoVisibleFields = hideWhenNoVisibleFields;
		return this;
	}

	public DtoFormSection createDtoFormSection() {
		List<DtoGridColumn> uiColumns = this.columns.stream()
				.map(column -> column != null ? column.createDtoGridColumn() : null)
				.collect(Collectors.toList());
		List<DtoGridRow> rows = this.rows.stream()
				.map(row -> row != null ? row.createDtoGridRow() : null)
				.collect(Collectors.toList());
		List<DtoFormSectionPlacement> uiFieldPlacements = fieldPlacements.stream()
				.map(fieldPlacement -> fieldPlacement != null ? fieldPlacement.createDtoFormSectionPlacement() : null)
				.collect(Collectors.toList());
		DtoFormSection uiSection = new DtoFormSection(id, uiColumns, rows, uiFieldPlacements);
		uiSection.setMargin(this.margin != null ? this.margin.createDtoSpacing() : null);
		uiSection.setPadding(this.padding != null ? this.padding.createDtoSpacing() : null);
		uiSection.setBorder(this.border != null ? this.border.createDtoBorder() : null);
		uiSection.setShadow(this.shadow != null ? this.shadow.createDtoShadow() : null);
		uiSection.setDrawHeaderLine(this.drawHeaderLine);
		uiSection.setBackgroundColor(this.backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiSection.setCollapsible(this.collapsible);
		uiSection.setCollapsed(this.collapsed);
		uiSection.setVisible(this.visible);
		uiSection.setHeaderTemplate(this.headerTemplate != null ? this.headerTemplate : null);
		uiSection.setHeaderData(this.headerData);
		uiSection.setGridGap(this.gridGap);
		uiSection.setFillRemainingHeight(fillRemainingHeight);
		uiSection.setHideWhenNoVisibleFields(hideWhenNoVisibleFields);
		return uiSection;
	}

}
