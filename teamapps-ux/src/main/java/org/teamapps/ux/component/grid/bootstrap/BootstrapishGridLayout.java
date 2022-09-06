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
package org.teamapps.ux.component.grid.bootstrap;

import org.teamapps.dto.*;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.ux.component.format.HorizontalElementAlignment;
import org.teamapps.ux.component.format.SizingPolicy;
import org.teamapps.ux.component.format.VerticalElementAlignment;
import org.teamapps.ux.component.grid.layout.GridColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BootstrapishGridLayout extends AbstractComponent implements Component {

	private static final int DEFAULT_NUMBER_OF_COLUMNS = 12;

	private boolean fillHeight = false;
	private final List<GridColumn> columns = new ArrayList<>();
	private final List<BootstrapishRow> rows = new ArrayList<>();
	private Map<BootstrapishBreakpoint, Integer> responsiveBreakpointMinWidths = new EnumMap<>(BootstrapishBreakpoint.class);
	private int gridGap = 5;
	private VerticalElementAlignment verticalItemAlignment = VerticalElementAlignment.STRETCH; // fall back to default defined on layout level!
	private HorizontalElementAlignment horizontalItemAlignment = HorizontalElementAlignment.STRETCH; // fall back to default defined on layout level!

	{
		Arrays.stream(BootstrapishBreakpoint.values())
				.forEach(bootstrapishBreakpoint -> responsiveBreakpointMinWidths.put(bootstrapishBreakpoint, bootstrapishBreakpoint.getDefaultMinWidth()));
	}

	public BootstrapishGridLayout() {
		this(DEFAULT_NUMBER_OF_COLUMNS);
	}

	public BootstrapishGridLayout(int numberOfColumns) {
		this(IntStream.range(0, numberOfColumns)
				.mapToObj(i -> new GridColumn(SizingPolicy.FRACTION))
				.collect(Collectors.toList()));
	}

	public BootstrapishGridLayout(List<GridColumn> columnDefinitions) {
		columns.addAll(columnDefinitions);
	}

	@Override
	public UiComponent createUiClientObject() {
		UiResponsiveGridLayout uiResponsiveGridLayout = new UiResponsiveGridLayout(createUiLayoutPolicies());
		mapAbstractUiComponentProperties(uiResponsiveGridLayout);
		uiResponsiveGridLayout.setFillHeight(this.fillHeight);
		return uiResponsiveGridLayout;
	}

	private List<UiResponsiveGridLayoutPolicy> createUiLayoutPolicies() {
		// find out which responsive breakpoints are actually being used
		Set<BootstrapishBreakpoint> usedResponsiveBreakpoints = rows.stream()
				.flatMap(row -> row.getPlacements().stream())
				.flatMap(placement -> placement.getSizings().keySet().stream())
				.collect(Collectors.toSet());
		// for each of these breakpoints, create the layout policy
		return usedResponsiveBreakpoints.stream()
				.map(breakpoint -> {
					List<UiGridColumn> uiGridColumns = columns.stream()
							.map(c -> c.createUiGridColumn()).collect(Collectors.toList());
					List<UiGridPlacement> uiGridPlacements = createUiGridPlacements(breakpoint);
					Integer maxUiRowIndex = uiGridPlacements.stream()
							.map(uiGridPlacement -> uiGridPlacement.getRow())
							.max(Integer::compareTo).orElse(0);
					List<UiGridRow> uiGridRows = IntStream.rangeClosed(0, maxUiRowIndex)
							.mapToObj(i -> new UiGridRow().setHeightPolicy(new UiSizingPolicy(UiSizeType.AUTO)))
							.collect(Collectors.toList());
					UiGridLayout uiGridLayout = new UiGridLayout(uiGridColumns, uiGridRows, uiGridPlacements)
							.setPadding(new UiSpacing().setTop(gridGap / 2).setRight(gridGap / 2).setBottom(gridGap / 2).setLeft(gridGap / 2))
							.setGridGap(this.gridGap)
							.setVerticalAlignment(this.verticalItemAlignment != null ? this.verticalItemAlignment.toUiVerticalElementAlignment() : null)
							.setHorizontalAlignment(this.horizontalItemAlignment != null ? this.horizontalItemAlignment.toUiHorizontalElementAlignment() : null);
					return new UiResponsiveGridLayoutPolicy(responsiveBreakpointMinWidths.get(breakpoint), uiGridLayout);
				})
				.collect(Collectors.toList());
	}

	private List<UiGridPlacement> createUiGridPlacements(BootstrapishBreakpoint breakpoint) {
		List<UiGridPlacement> uiPlacements = new ArrayList<>();
		int uiRowIndex = 0;
		for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++, uiRowIndex++) {
			int currentColumnIndex = 0;
			for (BootstrapishPlacement placement : rows.get(rowIndex).getPlacements()) {
				BootstrapishSizing sizing = placement.getSizingForBreakPoint(breakpoint);
				int colSpan = sizing.getColSpan() == BootstrapishSizing.COL_SPAN_FULL_WIDTH ? this.columns.size() : sizing.getColSpan();
				if (sizing.getOffset() + colSpan > this.columns.size()) {
					throw new IllegalArgumentException("Offset + colspan > numberOfColumns");
				}
				int rightmostSpannedColIndex = currentColumnIndex + sizing.getOffset() + colSpan - 1;
				if (rightmostSpannedColIndex >= this.columns.size()) {
					uiRowIndex++;
					currentColumnIndex = 0;
				}
				uiPlacements.add(new UiComponentGridPlacement(placement.getComponent().createUiReference())
						.setRow(uiRowIndex)
						.setColumn(currentColumnIndex + sizing.getOffset())
						.setColSpan(colSpan)
						.setHorizontalAlignment(placement.getHorizontalAlignment() != null ? placement.getHorizontalAlignment().toUiHorizontalElementAlignment() : null)
						.setVerticalAlignment(placement.getVerticalAlignment() != null ? placement.getVerticalAlignment().toUiVerticalElementAlignment() : null)
				);
				currentColumnIndex += sizing.getOffset() + colSpan;
			}
		}
		return uiPlacements;
	}

	public void refreshLayout() {
		sendCommandIfRendered(() -> new UiResponsiveGridLayout.UpdateLayoutPoliciesCommand(createUiLayoutPolicies()));
	}

	@Override
	public void handleUiEvent(UiEvent event) {
		// none
	}

	public void setColumns(List<GridColumn> columns) {
		this.columns.clear();
		this.columns.addAll(columns);
	}

	public List<GridColumn> getColumns() {
		return columns;
	}

	public List<BootstrapishRow> getRows() {
		return rows;
	}

	public void setRows(List<BootstrapishRow> rows) {
		this.rows.clear();
		this.rows.addAll(rows);

	}

	public BootstrapishGridLayout addRow(BootstrapishRow row) {
		rows.add(row);
		return this;
	}

	public BootstrapishRow.ChainBuilder addRow() {
		return new BootstrapishRow.ChainBuilder(this);
	}

	public boolean isFillHeight() {
		return fillHeight;
	}

	public void setFillHeight(boolean fillHeight) {
		this.fillHeight = fillHeight;
		sendCommandIfRendered(() -> new UiResponsiveGridLayout.SetFillHeightCommand(fillHeight));
	}

	public Map<BootstrapishBreakpoint, Integer> getResponsiveBreakpointMinWidths() {
		return responsiveBreakpointMinWidths;
	}

	public void setResponsiveBreakpointMinWidths(Map<BootstrapishBreakpoint, Integer> responsiveBreakpointMinWidths) {
		this.responsiveBreakpointMinWidths = responsiveBreakpointMinWidths;
		this.refreshLayout();
	}

	public int getGridGap() {
		return gridGap;
	}

	public void setGridGap(int gridGap) {
		this.gridGap = gridGap;
		this.refreshLayout();
	}

	public VerticalElementAlignment getVerticalItemAlignment() {
		return verticalItemAlignment;
	}

	public void setVerticalItemAlignment(VerticalElementAlignment verticalItemAlignment) {
		this.verticalItemAlignment = verticalItemAlignment;
		this.refreshLayout();
	}

	public HorizontalElementAlignment getHorizontalItemAlignment() {
		return horizontalItemAlignment;
	}

	public void setHorizontalItemAlignment(HorizontalElementAlignment horizontalItemAlignment) {
		this.horizontalItemAlignment = horizontalItemAlignment;
		this.refreshLayout();
	}
}

