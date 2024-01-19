/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2024 TeamApps.org
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

import org.teamapps.dto.UiFormSectionFieldPlacement;
import org.teamapps.dto.UiFormSectionPlacement;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;

public class FormSectionFieldPlacement implements FormSectionPlacement {

	private Component field;
	private int row;
	private int column;
	private int rowSpan = 1;
	private int colSpan = 1;
	private int minWidth;
	private int maxWidth;
	private int minHeight;
	private int maxHeight;
	private VerticalElementAlignment verticalAlignment = VerticalElementAlignment.CENTER;
	private HorizontalElementAlignment horizontalAlignment = HorizontalElementAlignment.LEFT;

	public FormSectionFieldPlacement() {

	}

	public FormSectionFieldPlacement(Component field, int row, int column) {
		this.field = field;
		this.row = row;
		this.column = column;
	}

	public FormSectionFieldPlacement createCopy() {
		FormSectionFieldPlacement placement = new FormSectionFieldPlacement(field, row, column);
		placement.setColumn(column);
		placement.setRow(row);
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

	public int getRow() {
		return row;
	}

	public FormSectionFieldPlacement setRow(int row) {
		this.row = row;
		return this;
	}

	public int getColumn() {
		return column;
	}

	public FormSectionFieldPlacement setColumn(int column) {
		this.column = column;
		return this;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public FormSectionFieldPlacement setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
		return this;
	}

	public int getColSpan() {
		return colSpan;
	}

	public FormSectionFieldPlacement setColSpan(int colSpan) {
		this.colSpan = colSpan;
		return this;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public FormSectionFieldPlacement setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
		return this;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public FormSectionFieldPlacement setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	public VerticalElementAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	public FormSectionFieldPlacement setVerticalAlignment(VerticalElementAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	public HorizontalElementAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public FormSectionFieldPlacement setHorizontalAlignment(HorizontalElementAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	@Override
	public int getMinWidth() {
		return minWidth;
	}

	public FormSectionFieldPlacement setMinWidth(int minWidth) {
		this.minWidth = minWidth;
		return this;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public FormSectionFieldPlacement setMinHeight(int minHeight) {
		this.minHeight = minHeight;
		return this;
	}

	@Override
	public UiFormSectionPlacement createUiFormSectionPlacement() {
		return new UiFormSectionFieldPlacement(field.createUiReference())
				.setRow(row)
				.setColumn(column)
				.setRowSpan(rowSpan)
				.setColSpan(colSpan)
				.setMinWidth(minWidth)
				.setMaxWidth(maxWidth)
				.setMinHeight(minHeight)
				.setMaxHeight(maxHeight)
				.setVerticalAlignment(verticalAlignment.toUiVerticalElementAlignment())
				.setHorizontalAlignment(horizontalAlignment.toUiHorizontalElementAlignment());
	}

	@Override
	public String toString() {
		return "FormSectionFieldPlacement{" +
				"field='" + field + '\'' +
				", row=" + row +
				", column=" + column +
				", rowSpan=" + rowSpan +
				", colSpan=" + colSpan +
				'}';
	}
}
