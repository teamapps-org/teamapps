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
package org.teamapps.projector.component.common.form;

import org.teamapps.projector.component.common.form.layoutpolicy.FormSectionFieldPlacement;
import org.teamapps.ux.component.Component;
import org.teamapps.projector.format.HorizontalElementAlignment;
import org.teamapps.projector.format.VerticalElementAlignment;
import org.teamapps.projector.component.common.grid.layout.GridColumn;
import org.teamapps.projector.component.common.grid.layout.GridRow;

public class ResponsiveFormField {
	private final ResponsiveFormSection responsiveFormSection;
	private final Component field;
	//FormSectionFloatingFieldsPlacement floatingFieldsPlacement;
	private final int row;
	private final int column;
	private int rowSpan;
	private int colSpan;
	private int minWidth;
	private int maxWidth;
	private int minHeight;
	private int maxHeight;
	private VerticalElementAlignment verticalAlignment = VerticalElementAlignment.CENTER;
	private HorizontalElementAlignment horizontalAlignment = HorizontalElementAlignment.LEFT;

	protected ResponsiveFormField(ResponsiveFormSection responsiveFormSection, Component field, int row, int column, FormSectionFieldPlacement fieldPlacementTemplate) {
		this.responsiveFormSection = responsiveFormSection;
		this.field = field;
		this.row = row;
		this.column = column;
		if (fieldPlacementTemplate != null) {
			rowSpan = fieldPlacementTemplate.getRowSpan();
			colSpan = fieldPlacementTemplate.getColSpan();
			minWidth = fieldPlacementTemplate.getMinWidth();
			maxWidth = fieldPlacementTemplate.getMaxWidth();
			minHeight = fieldPlacementTemplate.getMinHeight();
			maxHeight = fieldPlacementTemplate.getMaxHeight();
			verticalAlignment = fieldPlacementTemplate.getVerticalAlignment();
			horizontalAlignment = fieldPlacementTemplate.getHorizontalAlignment();
		}
	}


	protected FormSectionFieldPlacement createFormSectionPlacement() {
		FormSectionFieldPlacement placement = new FormSectionFieldPlacement(field, row, column);
		placement.setRowSpan(rowSpan);
		placement.setColSpan(colSpan);
		placement.setMinWidth(minWidth);
		placement.setMaxWidth(maxWidth);
		placement.setMinHeight(minHeight);
		placement.setMaxHeight(maxHeight);
		placement.setVerticalAlignment(verticalAlignment);
		placement.setHorizontalAlignment(horizontalAlignment);
		return placement;
	}

	public GridRow getRowDefinition() {
		return responsiveFormSection.getRow(row);
	}

	public GridColumn getColumnDefinition() {
		return responsiveFormSection.getColumn(column);
	}

	public ResponsiveFormSection getResponsiveFormSection() {
		return responsiveFormSection;
	}

	public Component getField() {
		return field;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public ResponsiveFormField setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
		return this;
	}

	public int getColSpan() {
		return colSpan;
	}

	public ResponsiveFormField setColSpan(int colSpan) {
		this.colSpan = colSpan;
		return this;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public ResponsiveFormField setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public ResponsiveFormField setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public ResponsiveFormField setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public ResponsiveFormField setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public VerticalElementAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public ResponsiveFormField setVerticalAlignment(VerticalElementAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	public HorizontalElementAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public ResponsiveFormField setHorizontalAlignment(HorizontalElementAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}
}
