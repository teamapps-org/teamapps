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
package org.teamapps.ux.component.grid.layout;

import org.teamapps.dto.UiGridPlacement;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.VerticalElementAlignment;

public abstract class AbstractGridPlacement implements GridPlacement {

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

	public AbstractGridPlacement() {
	}

	public AbstractGridPlacement(int row, int column) {
		this.row = row;
		this.column = column;
	}

	protected void mapAbstractGridPlacementUiProperties(UiGridPlacement uiPlacement) {
		uiPlacement.setColumn(column)
				.setRow(row)
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
	public int getMinHeight() {
		return minHeight;
	}

	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	@Override
	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
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
}
