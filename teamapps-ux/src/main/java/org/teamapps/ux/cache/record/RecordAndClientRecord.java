package org.teamapps.ux.cache.record;

import org.teamapps.dto.UiIdentifiableClientRecord;

public class RecordAndClientRecord<RECORD> {
	private final RECORD record;
	private final UiIdentifiableClientRecord uiRecord;

	public RecordAndClientRecord(RECORD record, UiIdentifiableClientRecord uiRecord) {
		this.record = record;
		this.uiRecord = uiRecord;
	}

	public RECORD getRecord() {
		return record;
	}

	public UiIdentifiableClientRecord getUiRecord() {
		return uiRecord;
	}
}
