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
package org.teamapps.ux.component.form.layoutpolicy;

import org.teamapps.dto.UiFormSectionFloatingField;
import org.teamapps.dto.UiFormSectionFloatingFieldsPlacement;
import org.teamapps.dto.UiFormSectionPlacement;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;

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
	private VerticalElementAlignment verticalAlignment = VerticalElementAlignment.CENTER;
	private HorizontalElementAlignment horizontalAlignment = HorizontalElementAlignment.LEFT;

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
	public VerticalElementAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(VerticalElementAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
	}

	@Override
	public HorizontalElementAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(HorizontalElementAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}

	@Override
	public UiFormSectionPlacement createUiFormSectionPlacement() {
		List<UiFormSectionFloatingField> uiFloatingFields = floatingFields.stream()
				.map(floatingField -> floatingField.createUiFormSectionFloatingField())
				.collect(Collectors.toList());
		UiFormSectionFloatingFieldsPlacement placement = new UiFormSectionFloatingFieldsPlacement(uiFloatingFields)
				.setRow(row)
				.setColumn(column)
				.setWrap(wrap)
				.setVerticalSpacing(verticalSpacing)
				.setHorizontalSpacing(horizontalSpacing)
				.setRowSpan(rowSpan)
				.setColSpan(colSpan)
				.setMinWidth(minWidth)
				.setMaxWidth(maxWidth)
				.setVerticalAlignment(verticalAlignment.toUiVerticalElementAlignment())
				.setHorizontalAlignment(horizontalAlignment.toUiHorizontalElementAlignment());

		return placement;
	}
}
