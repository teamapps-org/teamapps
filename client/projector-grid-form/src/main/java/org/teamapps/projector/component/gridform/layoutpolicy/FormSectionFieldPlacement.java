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

import org.teamapps.projector.component.Component;
import org.teamapps.projector.component.gridform.DtoFormSectionFieldPlacement;
import org.teamapps.projector.component.gridform.DtoFormSectionPlacement;
import org.teamapps.projector.format.AlignItems;
import org.teamapps.projector.format.JustifyContent;

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
	private AlignItems verticalAlignment = AlignItems.CENTER;
	private JustifyContent horizontalAlignment = JustifyContent.START;

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

	public AlignItems getVerticalAlignment() {
		return verticalAlignment;
	}

	public FormSectionFieldPlacement setVerticalAlignment(AlignItems verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	public JustifyContent getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public FormSectionFieldPlacement setHorizontalAlignment(JustifyContent horizontalAlignment) {
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
	public DtoFormSectionPlacement createDtoFormSectionPlacement() {
		return new DtoFormSectionFieldPlacement(field)
				.setRow(row)
				.setColumn(column)
				.setRowSpan(rowSpan)
				.setColSpan(colSpan)
				.setMinWidth(minWidth)
				.setMaxWidth(maxWidth)
				.setMinHeight(minHeight)
				.setMaxHeight(maxHeight)
				.setVerticalAlignment(verticalAlignment)
				.setHorizontalAlignment(horizontalAlignment);
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
