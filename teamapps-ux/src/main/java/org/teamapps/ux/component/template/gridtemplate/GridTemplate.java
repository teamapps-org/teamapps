/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2023 TeamApps.org
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
package org.teamapps.ux.component.template.gridtemplate;

import org.teamapps.common.format.Color;
import org.teamapps.dto.AbstractUiTemplateElement;
import org.teamapps.dto.UiGridColumn;
import org.teamapps.dto.UiGridRow;
import org.teamapps.dto.UiGridTemplate;
import org.teamapps.dto.UiTemplate;
import org.teamapps.ux.component.format.Border;
import org.teamapps.ux.component.format.SizeType;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.format.Spacing;
import org.teamapps.ux.component.grid.layout.GridColumn;
import org.teamapps.ux.component.grid.layout.GridRow;
import org.teamapps.ux.component.template.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GridTemplate implements Template {

	private int minWidth = 0;
	private int maxWidth = -1;
	private int minHeight = 0;
	private int maxHeight = -1;
	private Spacing padding;
	private int gridGap = 0;
	private Color backgroundColor;
	private Border border;
	private String ariaLabelProperty = "ariaLabel";
	private String titleProperty = "title";

	List<GridColumn> columns = new ArrayList<>();
	List<GridRow> rows = new ArrayList<>();
	List<AbstractTemplateElement<?>> elements = new ArrayList<>();

	public GridTemplate() {

	}

	public GridTemplate(int minWidth, int maxWidth, int minHeight, int maxHeight, Spacing padding, int gridGap) {
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.padding = padding;
		this.gridGap = gridGap;
	}

	public List<String> getPropertyNames() {
		ArrayList<String> propertyNames = elements.stream()
				.flatMap(element -> element.getPropertyNames().stream())
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
		if (ariaLabelProperty != null) {
			propertyNames.add(ariaLabelProperty);
		}
		if (titleProperty != null) {
			propertyNames.add(titleProperty);
		}
		return propertyNames;
	}

	public GridTemplate addColumn(GridColumn column) {
		columns.add(column);
		return this;
	}

	public GridTemplate addColumn(SizingPolicy widthPolicy) {
		columns.add(new GridColumn(widthPolicy, 0, 0));
		return this;
	}

	public GridTemplate addColumn(SizingPolicy widthPolicy, int leftPadding, int rightPadding) {
		columns.add(new GridColumn(widthPolicy, leftPadding, rightPadding));
		return this;
	}

	public GridTemplate addColumn(SizeType type, float widthValue, int minAbsoluteWidth, int leftPadding, int rightPadding) {
		columns.add(new GridColumn(new SizingPolicy(type, widthValue, minAbsoluteWidth), leftPadding, rightPadding));
		return this;
	}

	public GridTemplate addRow(SizingPolicy height) {
		rows.add(new GridRow(height, 0, 0));
		return this;
	}

	public GridTemplate addRow(GridRow row) {
		rows.add(row);
		return this;
	}

	public GridTemplate addRow(SizingPolicy heightPolicy, int topPadding, int bottomPadding) {
		rows.add(new GridRow(heightPolicy, topPadding, bottomPadding));
		return this;
	}

	public GridTemplate addRow(SizeType type, float heightValue, int minAbsoluteHeight, int topPadding, int bottomPadding) {
		rows.add(new GridRow(new SizingPolicy(type, heightValue, minAbsoluteHeight), topPadding, bottomPadding));
		return this;
	}

	public GridTemplate setMinHeight(final int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public GridTemplate setMaxHeight(final int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public GridTemplate setColumns(final List<GridColumn> columns) {
		this.columns = columns;
		return this;
	}

	public GridTemplate setRows(final List<GridRow> rows) {
		this.rows = rows;
		return this;
	}

	public GridTemplate setElements(final List<AbstractTemplateElement<?>> elements) {
		this.elements = elements;
		return this;
	}

	public GridTemplate addElement(final AbstractTemplateElement element) {
		this.elements.add(element);
		return this;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public List<GridColumn> getColumns() {
		return columns;
	}

	public List<GridRow> getRows() {
		return rows;
	}

	public List<AbstractTemplateElement<?>> getElements() {
		return elements;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public GridTemplate setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public GridTemplate setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public Spacing getPadding() {
		return padding;
	}

	public GridTemplate setPadding(Spacing padding) {
		this.padding = padding;
		return this;
	}

	public int getGridGap() {
		return gridGap;
	}

	public GridTemplate setGridGap(int gridGap) {
		this.gridGap = gridGap;
		return this;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public GridTemplate setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Border getBorder() {
		return border;
	}

	public GridTemplate setBorder(Border border) {
		this.border = border;
		return this;
	}

	public String getAriaLabelProperty() {
		return ariaLabelProperty;
	}

	public GridTemplate setAriaLabelProperty(String ariaLabelProperty) {
		this.ariaLabelProperty = ariaLabelProperty;
		return this;
	}

	public String getTitleProperty() {
		return titleProperty;
	}

	public GridTemplate setTitleProperty(String titleProperty) {
		this.titleProperty = titleProperty;
		return this;
	}

	@Override
	public UiTemplate createUiTemplate() {
		List<UiGridColumn> uiColumns = columns.stream()
				.map(column -> column != null ? column.createUiGridColumn() : null)
				.collect(Collectors.toList());
		List<UiGridRow> uiRows = rows.stream()
				.map(row -> row != null ? row.createUiGridRow() : null)
				.collect(Collectors.toList());
		List<AbstractUiTemplateElement> uiTemplateElements = elements.stream()
				.map(element -> element != null ? element.createUiTemplateElement() : null)
				.collect(Collectors.toList());
		UiGridTemplate uiGridTemplate = new UiGridTemplate(uiColumns, uiRows, uiTemplateElements);
		uiGridTemplate.setMinWidth(minWidth);
		uiGridTemplate.setMaxWidth(maxWidth);
		uiGridTemplate.setMinHeight(minHeight);
		uiGridTemplate.setMaxHeight(maxHeight);
		if (padding != null) {
			uiGridTemplate.setPadding(padding.createUiSpacing());
		}
		uiGridTemplate.setGridGap(gridGap);
		uiGridTemplate.setBorder(border != null ? border.createUiBorder() : null);
		uiGridTemplate.setBackgroundColor(backgroundColor != null ? backgroundColor.toHtmlColorString() : null);
		uiGridTemplate.setAriaLabelProperty(ariaLabelProperty);
		uiGridTemplate.setTitleProperty(titleProperty);
		return uiGridTemplate;
	}

}
