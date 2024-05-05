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
package org.teamapps.projector.components.common.grid.bootstrap;

import org.teamapps.dto.protocol.DtoEventWrapper;
import org.teamapps.ux.component.AbstractComponent;
import org.teamapps.ux.component.Component;
import org.teamapps.projector.format.HorizontalElementAlignment;
import org.teamapps.projector.format.SizingPolicy;
import org.teamapps.projector.format.VerticalElementAlignment;
import org.teamapps.projector.components.common.grid.layout.GridColumn;

import java.util.*;
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
	public DtoComponent createDto() {
		DtoResponsiveGridLayout uiResponsiveGridLayout = new DtoResponsiveGridLayout(createUiLayoutPolicies());
		mapAbstractUiComponentProperties(uiResponsiveGridLayout);
		uiResponsiveGridLayout.setFillHeight(this.fillHeight);
		return uiResponsiveGridLayout;
	}

	private List<DtoResponsiveGridLayoutPolicy> createUiLayoutPolicies() {
		// find out which responsive breakpoints are actually being used
		Set<BootstrapishBreakpoint> usedResponsiveBreakpoints = rows.stream()
				.flatMap(row -> row.getPlacements().stream())
				.flatMap(placement -> placement.getSizings().keySet().stream())
				.collect(Collectors.toSet());
		// for each of these breakpoints, create the layout policy
		return usedResponsiveBreakpoints.stream()
				.map(breakpoint -> {
					List<DtoGridColumn> uiGridColumns = columns.stream()
							.map(c -> c.createUiGridColumn()).collect(Collectors.toList());
					List<DtoGridPlacement> uiGridPlacements = createUiGridPlacements(breakpoint);
					Integer maxUiRowIndex = uiGridPlacements.stream()
							.map(uiGridPlacement -> uiGridPlacement.getRow())
							.max(Integer::compareTo).orElse(0);
					List<DtoGridRow> uiGridRows = IntStream.rangeClosed(0, maxUiRowIndex)
							.mapToObj(i -> new DtoGridRow().setHeightPolicy(new DtoSizingPolicy(DtoSizeType.AUTO)))
							.collect(Collectors.toList());
					DtoGridLayout uiGridLayout = new DtoGridLayout(uiGridColumns, uiGridRows, uiGridPlacements)
							.setPadding(new DtoSpacing().setTop(gridGap / 2).setRight(gridGap / 2).setBottom(gridGap / 2).setLeft(gridGap / 2))
							.setGridGap(this.gridGap)
							.setVerticalAlignment(this.verticalItemAlignment != null ? this.verticalItemAlignment.toUiVerticalElementAlignment() : null)
							.setHorizontalAlignment(this.horizontalItemAlignment != null ? this.horizontalItemAlignment.toUiHorizontalElementAlignment() : null);
					return new DtoResponsiveGridLayoutPolicy(responsiveBreakpointMinWidths.get(breakpoint), uiGridLayout);
				})
				.collect(Collectors.toList());
	}

	private List<DtoGridPlacement> createUiGridPlacements(BootstrapishBreakpoint breakpoint) {
		List<DtoGridPlacement> uiPlacements = new ArrayList<>();
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
				uiPlacements.add(new DtoComponentGridPlacement(placement.getComponent().createDtoReference())
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
		sendCommandIfRendered(() -> new DtoResponsiveGridLayout.UpdateLayoutPoliciesCommand(createUiLayoutPolicies()));
	}

	@Override
	public void handleUiEvent(DtoEventWrapper event) {
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
		sendCommandIfRendered(() -> new DtoResponsiveGridLayout.SetFillHeightCommand(fillHeight));
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

