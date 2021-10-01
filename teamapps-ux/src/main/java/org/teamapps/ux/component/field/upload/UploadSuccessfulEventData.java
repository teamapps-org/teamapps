package org.teamapps.ux.component.field.upload;

public class UploadSuccessfulEventData<RECORD> {

	private final UploadedFile uploadedFile;
	private final RECORD record;

	public UploadSuccessfulEventData(UploadedFile uploadedFile, RECORD record) {
		this.uploadedFile = uploadedFile;
		this.record = record;
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public RECORD getRecord() {
		return record;
	}
}
