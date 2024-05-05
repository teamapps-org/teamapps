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
package org.teamapps.projector.components.common.grid.layout;

import org.teamapps.projector.components.common.dto.DtoGridRow;
import org.teamapps.projector.format.SizingPolicy;

public class GridRow {

	private SizingPolicy heightPolicy;
	private int topPadding;
	private int bottomPadding;

	public GridRow() {
		this.heightPolicy = SizingPolicy.AUTO;
	}

	public GridRow(SizingPolicy heightPolicy) {
		this.heightPolicy = heightPolicy;
	}

	public GridRow(SizingPolicy heightPolicy, int topPadding, int bottomPadding) {
		this.heightPolicy = heightPolicy;
		this.topPadding = topPadding;
		this.bottomPadding = bottomPadding;
	}

	public GridRow(GridRow gridRow) {
		this(gridRow.heightPolicy, gridRow.topPadding, gridRow.bottomPadding);
	}

	public SizingPolicy getHeightPolicy() {
		return heightPolicy;
	}

	public GridRow setHeightPolicy(SizingPolicy heightPolicy) {
		this.heightPolicy = heightPolicy;
		return this;
	}

	public int getTopPadding() {
		return topPadding;
	}

	public GridRow setTopPadding(int topPadding) {
		this.topPadding = topPadding;
		return this;
	}

	public int getBottomPadding() {
		return bottomPadding;
	}

	public GridRow setBottomPadding(int bottomPadding) {
		this.bottomPadding = bottomPadding;
		return this;
	}

	public DtoGridRow createUiGridRow() {
		return new DtoGridRow()
				.setHeightPolicy(heightPolicy != null ? heightPolicy.createUiSizingPolicy() : null)
				.setTopPadding(topPadding)
				.setBottomPadding(bottomPadding);
	}
}
