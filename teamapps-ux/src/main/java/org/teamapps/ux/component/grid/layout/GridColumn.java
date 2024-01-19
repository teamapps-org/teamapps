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
package org.teamapps.ux.component.grid.layout;

import org.teamapps.dto.UiGridColumn;
import org.teamapps.ux.component.format.SizingPolicy;

public class GridColumn {

	private SizingPolicy widthPolicy;
	private int leftPadding;
	private int rightPadding;

	public GridColumn() {
		this.widthPolicy = SizingPolicy.AUTO;
	}

	public GridColumn(SizingPolicy widthPolicy) {
		this.widthPolicy = widthPolicy;
	}

	public GridColumn(SizingPolicy widthPolicy, int leftPadding, int rightPadding) {
		this.widthPolicy = widthPolicy;
		this.leftPadding = leftPadding;
		this.rightPadding = rightPadding;
	}

	public GridColumn(GridColumn gridColumn) {
		this(gridColumn.widthPolicy, gridColumn.leftPadding, gridColumn.rightPadding);
	}

	public SizingPolicy getWidthPolicy() {
		return widthPolicy;
	}

	public GridColumn setWidthPolicy(SizingPolicy widthPolicy) {
		this.widthPolicy = widthPolicy;
		return this;
	}

	public int getLeftPadding() {
		return leftPadding;
	}

	public GridColumn setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
		return this;
	}

	public int getRightPadding() {
		return rightPadding;
	}

	public GridColumn setRightPadding(int rightPadding) {
		this.rightPadding = rightPadding;
		return this;
	}

	public UiGridColumn createUiGridColumn() {
		return new UiGridColumn()
				.setWidthPolicy(widthPolicy != null ? widthPolicy.createUiSizingPolicy() : null)
				.setLeftPadding(leftPadding)
				.setRightPadding(rightPadding);
	}
}
