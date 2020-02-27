package org.teamapps.ux.component.table;

public class CellClickedEvent<RECORD> {

	private final RECORD record;
	private final TableColumn<RECORD> column;

	public CellClickedEvent(RECORD record, TableColumn<RECORD> column) {
		this.record = record;
		this.column = column;
	}

	public RECORD getRecord() {
		return record;
	}

	public TableColumn<RECORD> getColumn() {
		return column;
	}
}
