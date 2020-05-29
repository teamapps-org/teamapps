/*-
 * ========================LICENSE_START=================================
 * TeamApps
 * ---
 * Copyright (C) 2014 - 2020 TeamApps.org
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
///<reference types="slickgrid"/>
///<reference types="slickgrid/slick.rowselectionmodel"/>

import {keyCodes} from "../trivial-components/TrivialCore";
import {bind} from "../util/Bind";

export class UiTableRowSelectionModel {

	private static readonly DEFAULTS = {
		selectActiveRow: true
	};

	public onSelectedRangesChanged: Slick.Event<any> = new Slick.Event();

	private grid: Slick.Grid<any>;
	private ranges: Slick.Range[] = [];

	constructor(private _options?: { selectActiveRow: boolean; }) {

	}

	/**
	 * An initializer function that will be called with an instance of the grid whenever a selection model is registered with setSelectionModel. The selection model can use this to initialize its state and subscribe to grid events.
	 **/
	init(grid: Slick.Grid<any>): void {
		this._options = $.extend(true, {}, UiTableRowSelectionModel.DEFAULTS, this._options);
		this.grid = grid;
		this.grid.onActiveCellChanged.subscribe(this.handleActiveCellChange);
		this.grid.onKeyDown.subscribe(this.handleKeyDown);
		this.grid.onClick.subscribe(this.handleClick);
	}

	/**
	 * A destructor function that will be called whenever a selection model is unregistered from the grid by a call to setSelectionModel with another selection model
	 * or whenever a grid with this selection model is destroyed.
	 * The selection model can use this destructor to unsubscribe from grid events and release all resources (remove DOM nodes, event listeners, etc.).
	 **/
	destroy(): void {
		this.grid.onActiveCellChanged.unsubscribe(this.handleActiveCellChange);
		this.grid.onKeyDown.unsubscribe(this.handleKeyDown);
		this.grid.onClick.unsubscribe(this.handleClick);
	}


	public rangesToRows(ranges: Slick.Range[]) {
		var rows = [];
		for (var i = 0; i < ranges.length; i++) {
			for (var j = ranges[i].fromRow; j <= ranges[i].toRow; j++) {
				rows.push(j);
			}
		}
		return rows;
	}

	private rowsToRanges(rows: number[]) {
		var ranges = [];
		var lastCell = this.grid.getColumns().length - 1;
		for (var i = 0; i < rows.length; i++) {
			ranges.push(new Slick.Range(rows[i], 0, rows[i], lastCell));
		}
		return ranges;
	}

	private getRowsRange(from: number, to: number) {
		var i, rows = [];
		for (i = from; i <= to; i++) {
			rows.push(i);
		}
		for (i = to; i < from; i++) {
			rows.push(i);
		}
		return rows;
	}

	public getSelectedRows() {
		return this.rangesToRows(this.ranges);
	}

	public setSelectedRows(rows: number[]) {
		this.setSelectedRanges(this.rowsToRanges(rows));
	}

	public setSelectedRanges(ranges: Slick.Range[]) {
		// simple check for: empty selection didn't change, prevent firing onSelectedRangesChanged
		if ((!this.ranges || this.ranges.length === 0) && (!ranges || ranges.length === 0)) {
			return;
		}
		this.ranges = ranges;
		this.onSelectedRangesChanged.notify(this.ranges);
	}

	public getSelectedRanges() {
		return this.ranges;
	}

	@bind
	private handleActiveCellChange(e: any, data: any) {
		if (this._options.selectActiveRow && data.row != null) {
			this.setSelectedRanges([new Slick.Range(data.row, 0, data.row, this.grid.getColumns().length - 1)]);
		}
	}

	@bind
	private handleKeyDown(e: any) {
		var activeRow = this.grid.getActiveCell();
		if (this.grid.getOptions().multiSelect && activeRow
			&& e.shiftKey && !e.ctrlKey && !e.altKey && !e.metaKey
			&& (e.which == keyCodes.up_arrow || e.which == keyCodes.down_arrow)) {
			var selectedRows = this.getSelectedRows();
			selectedRows.sort((x, y) => {
				return x - y
			});

			if (!selectedRows.length) {
				selectedRows = [activeRow.row];
			}

			var top = selectedRows[0];
			var bottom = selectedRows[selectedRows.length - 1];
			var active;

			if (e.which == keyCodes.down_arrow) {
				active = activeRow.row < bottom || top == bottom ? ++bottom : ++top;
			} else {
				active = activeRow.row < bottom ? --bottom : --top;
			}

			if (active >= 0 && active < this.grid.getDataLength()) {
				this.grid.scrollRowIntoView(active, false);
				var tempRanges = this.rowsToRanges(this.getRowsRange(top, bottom));
				this.setSelectedRanges(tempRanges);
			}

			e.preventDefault();
			e.stopPropagation();
		}
	}

	@bind
	private handleClick(e: any) {
		var cell = this.grid.getCellFromEvent(e);
		if (!cell) {
			return false;
		}

		var selection = this.rangesToRows(this.ranges);
		var idx = $.inArray(cell.row, selection);

		if (!this.grid.getOptions().multiSelect) {
			selection = [];
		}

		if (!(e.ctrlKey || e.metaKey || e.shiftKey)) {
			selection = [cell.row];
		} else if (idx === -1 && (e.ctrlKey || e.metaKey)) {
			selection.push(cell.row);
		} else if (idx !== -1 && (e.ctrlKey || e.metaKey)) {
			selection = selection.filter(s => s !== cell.row);
		} else if (selection.length > 0 && e.shiftKey) {
			const last = selection.pop();
			const from = Math.min(cell.row, last);
			const to = Math.max(cell.row, last);
			selection = [];
			for (let i = from; i <= to; i++) {
				if (i !== last) {
					selection.push(i);
				}
			}
			selection.push(last);
		}

		if (this.grid.canCellBeActive(cell.row, cell.cell)) {
			if (this.grid.getEditorLock().isActive(null)) {
				this.grid.getEditorLock().commitCurrentEdit()
			}
			this.grid.setActiveCell(cell.row, cell.cell)
		}
		var tempRanges = this.rowsToRanges(selection);
		this.setSelectedRanges(tempRanges);
		// e.stopImmediatePropagation();

		return true;
	}

}
