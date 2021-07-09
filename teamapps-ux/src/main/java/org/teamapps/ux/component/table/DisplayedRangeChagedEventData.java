package org.teamapps.ux.component.table;

public class DisplayedRangeChagedEventData {

	private final long startRowNumber;
	private final long numberOfDisplayedRows;

	public DisplayedRangeChagedEventData(long startRowNumber, long numberOfDisplayedRows) {
		this.startRowNumber = startRowNumber;
		this.numberOfDisplayedRows = numberOfDisplayedRows;
	}

	public long getStartRowNumber() {
		return startRowNumber;
	}

	public long getNumberOfDisplayedRows() {
		return numberOfDisplayedRows;
	}
}
