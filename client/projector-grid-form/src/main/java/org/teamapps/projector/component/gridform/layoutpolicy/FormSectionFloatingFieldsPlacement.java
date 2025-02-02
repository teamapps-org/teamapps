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

import org.teamapps.projector.component.gridform.DtoFormSectionFloatingField;
import org.teamapps.projector.component.gridform.DtoFormSectionFloatingFieldsPlacement;
import org.teamapps.projector.component.gridform.DtoFormSectionPlacement;
import org.teamapps.projector.format.AlignItems;
import org.teamapps.projector.format.JustifyContent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormSectionFloatingFieldsPlacement implements FormSectionPlacement {

	private List<FormSectionFloatingField> floatingFields = new ArrayList<>();
	private boolean wrap;
	private int horizontalSpacing = 5;
	private int verticalSpacing = 3;

	private int row;
	private int column;
	private int rowSpan;
	private int colSpan;
	private int minWidth;
	private int maxWidth;
	private AlignItems verticalAlignment = AlignItems.CENTER;
	private JustifyContent horizontalAlignment = JustifyContent.START;

	public FormSectionFloatingFieldsPlacement(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public FormSectionFloatingFieldsPlacement(int row, int column, List<FormSectionFloatingField> floatingFields) {
		this.row = row;
		this.column = column;
		this.floatingFields = floatingFields;
	}

	public FormSectionFloatingFieldsPlacement(List<FormSectionFloatingField> floatingFields) {
		this.floatingFields = floatingFields;
	}

	public List<FormSectionFloatingField> getFloatingFields() {
		return floatingFields;
	}

	public void addFloatingField(FormSectionFloatingField floatingField) {
		floatingFields.add(floatingField);
	}

	public void setFloatingFields(List<FormSectionFloatingField> floatingFields) {
		this.floatingFields = floatingFields;
	}

	public boolean isWrap() {
		return wrap;
	}

	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}

	public int getHorizontalSpacing() {
		return horizontalSpacing;
	}

	public void setHorizontalSpacing(int horizontalSpacing) {
		this.horizontalSpacing = horizontalSpacing;
	}

	public int getVerticalSpacing() {
		return verticalSpacing;
	}

	public void setVerticalSpacing(int verticalSpacing) {
		this.verticalSpacing = verticalSpacing;
	}

	@Override
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public int getRowSpan() {
		return rowSpan;
	}

	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	@Override
	public int getColSpan() {
		return colSpan;
	}

	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}

	@Override
	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public AlignItems getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(AlignItems verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	@Override
	public JustifyContent getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(JustifyContent horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	@Override
	public DtoFormSectionPlacement createDtoFormSectionPlacement() {
		List<DtoFormSectionFloatingField> uiFloatingFields = floatingFields.stream()
				.map(floatingField -> floatingField.createDtoFormSectionFloatingField())
				.collect(Collectors.toList());
		DtoFormSectionFloatingFieldsPlacement placement = new DtoFormSectionFloatingFieldsPlacement(uiFloatingFields)
				.setRow(row)
				.setColumn(column)
				.setWrap(wrap)
				.setVerticalSpacing(verticalSpacing)
				.setHorizontalSpacing(horizontalSpacing)
				.setRowSpan(rowSpan)
				.setColSpan(colSpan)
				.setMinWidth(minWidth)
				.setMaxWidth(maxWidth)
				.setVerticalAlignment(verticalAlignment)
				.setHorizontalAlignment(horizontalAlignment);

		return placement;
	}
}
