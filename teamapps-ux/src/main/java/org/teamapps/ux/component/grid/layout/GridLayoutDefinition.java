/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2019 TeamApps.org
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

import org.teamapps.dto.UiGridLayout;
import org.teamapps.ux.component.format.Border;
import org.teamapps.common.format.Color;
import org.teamapps.ux.component.format.Shadow;
import org.teamapps.ux.component.format.Spacing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.teamapps.util.UiUtil.createUiColor;

public class GridLayoutDefinition {

	private List<GridColumn> columns = new ArrayList<>();
	private List<GridRow> rows = new ArrayList<>();
	private List<GridPlacement> placements = new ArrayList<>();
	
	private int gridGap = 7;
	private Spacing margin;
	private Spacing padding;
	private Border border;
	private Shadow shadow;
	private Color backgroundColor;

	public GridLayoutDefinition() {
	}

	public GridLayoutDefinition(List<GridColumn> columns, List<GridRow> rows, List<GridPlacement> placements) {
		this.columns.addAll(columns);
		this.rows.addAll(rows);
		this.placements.addAll(placements);
	}

	public GridLayoutDefinition addColumn(GridColumn column) {
		this.columns.add(column);
		return this;
	}

	public GridLayoutDefinition addRow(GridRow row) {
		this.rows.add(row);
		return this;
	}

	public GridLayoutDefinition addPlacement(GridPlacement placement) {
		this.placements.add(placement);
		return this;
	}

	public UiGridLayout createUiGridLayout() {
		UiGridLayout uiGridLayout = new UiGridLayout(
				columns.stream().map(c -> c.createUiGridColumn()).collect(Collectors.toList()),
				rows.stream().map(r -> r.createUiGridRow()).collect(Collectors.toList()),
				placements.stream().map(p -> p.createUiGridPlacement()).collect(Collectors.toList())
		);
		uiGridLayout.setGridGap(gridGap);
		uiGridLayout.setMargin(margin != null ? margin.createUiSpacing() : null);
		uiGridLayout.setPadding(padding != null ? padding.createUiSpacing() : null);
		uiGridLayout.setBorder(border != null ? border.createUiBorder() : null);
		uiGridLayout.setShadow(shadow != null ? shadow.createUiShadow() : null);
		uiGridLayout.setBackgroundColor(backgroundColor != null ? createUiColor(backgroundColor) : null);
		return uiGridLayout;
	}

	public List<GridColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<GridColumn> columns) {
		this.columns = columns;
	}

	public List<GridRow> getRows() {
		return rows;
	}

	public void setRows(List<GridRow> rows) {
		this.rows = rows;
	}

	public List<GridPlacement> getPlacements() {
		return placements;
	}

	public void setPlacements(List<GridPlacement> placements) {
		this.placements = placements;
	}

	public int getGridGap() {
		return gridGap;
	}

	public void setGridGap(int gridGap) {
		this.gridGap = gridGap;
	}

	public Spacing getMargin() {
		return margin;
	}

	public void setMargin(Spacing margin) {
		this.margin = margin;
	}

	public Spacing getPadding() {
		return padding;
	}

	public void setPadding(Spacing padding) {
		this.padding = padding;
	}

	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		this.border = border;
	}

	public Shadow getShadow() {
		return shadow;
	}

	public void setShadow(Shadow shadow) {
		this.shadow = shadow;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
