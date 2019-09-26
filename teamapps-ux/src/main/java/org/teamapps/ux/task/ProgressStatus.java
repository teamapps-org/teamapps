package org.teamapps.ux.task;

import org.teamapps.dto.UiProgressStatus;

public enum ProgressStatus {
	NOT_YET_STARTED,
	RUNNING,
	CANCELLATION_REQUESTED,
	CANCELED,
	COMPLETE,
	FAILED;

	public UiProgressStatus toUiProgressStatus() {
		return UiProgressStatus.valueOf(name());
	}
}